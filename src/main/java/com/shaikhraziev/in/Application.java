package com.shaikhraziev.in;

import com.shaikhraziev.dto.UserCreateEditDto;
import com.shaikhraziev.dto.UserReadDto;
import com.shaikhraziev.entity.Action;
import com.shaikhraziev.entity.Indication;
import com.shaikhraziev.entity.Phase;
import com.shaikhraziev.map.ActionMapper;
import com.shaikhraziev.map.MonthMapper;
import com.shaikhraziev.service.AdminService;
import com.shaikhraziev.service.AuditService;
import com.shaikhraziev.service.UserService;
import com.shaikhraziev.validation.UserValidation;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import static com.shaikhraziev.entity.Action.*;
import static com.shaikhraziev.entity.Phase.*;

/**
 * Главный класс приложения. Выполняется основной цикл программы.
 */
@RequiredArgsConstructor
public class Application {

    private final Scanner scanner = new Scanner(System.in);
    private final ActionMapper actionMapper;
    private final MonthMapper monthMapper;
    private final UserService userService;
    private final AdminService adminService;
    private final UserValidation userValidation;
    private final AuditService auditService;

    /**
     * Основной метод программы. Запускает бесконечный цикл для обработки действий пользователя.
     */
    public void run() {

        while (true) {
            Action action = applicationMenuForNoAuthenticationUser();

            if (userValidation.isError(action)) continue;

            switch (action) {
                case REGISTRATION:
                    registrationProcedure();
                    continue;

                case AUTHORIZATION:
                    UserCreateEditDto maybeUser = enteringUsernameAndPassword();

                    if (!userValidation.isValidInput(maybeUser)) continue;

                    if (adminService.isAdmin(maybeUser)) {
                        adminService.management();
                        continue;
                    }

                    Optional<UserReadDto> maybeAuthorizationUser = userService.authorization(maybeUser);

                    if (!userValidation.isValidUser(maybeAuthorizationUser)) continue;

                    UserReadDto authorizationUser = maybeAuthorizationUser.get();
                    System.out.println("%s has successfully logged in!".formatted(authorizationUser.getUsername()));
                    auditService.authorization(authorizationUser.getUsername());

                    while (true) {
                        Action actionAuthorizationUser = applicationMenuForAuthorizationUser();

                        if (actionAuthorizationUser.equals(LOGOUT)) {
                            auditService.logout(authorizationUser.getUsername());
                            break;
                        }

                        switch (actionAuthorizationUser) {
                            case ACTUAL_INDICATIONS:
                                Optional<Map<LocalDate, Indication>> actualIndications = userService.getActualIndications(authorizationUser.getUsername());
                                if (!userValidation.isValidIndications(actualIndications)) continue;
                                printActualIndications(actualIndications.get());
                                auditService.getActualIndications(authorizationUser.getUsername());
                                continue;

                            case UPLOAD_INDICATIONS:
                                Indication indications = enteringIndications();
                                if (!userValidation.isValidUploadIndications(indications)) continue;
                                userService.uploadIndications(authorizationUser.getUsername(), indications);
                                continue;

                            case MONTHLY_INDICATIONS:
                                Month month = enteringMonth();
                                if (month == null) continue;
                                Optional<List<Indication>> maybeIndications = userService.getMonthlyIndications(authorizationUser.getUsername(), month);
                                if (!userValidation.haveMonthlyIndications(maybeIndications)) continue;
                                printMonthlyIndications(maybeIndications, month);
                                auditService.getMonthlyIndications(authorizationUser.getUsername(), month);
                                continue;

                            case HISTORY:
                                Optional<Map<LocalDate, Indication>> maybeHistory = userService.getHistory(authorizationUser.getUsername());
                                if (!userValidation.haveHistory(maybeHistory)) continue;
                                printHistory(maybeHistory);
                                auditService.getHistory(authorizationUser.getUsername());
                                continue;

                            case EXIT:
                                auditService.logout(authorizationUser.getUsername());
                                userService.exit();

                            default:
                                continue;
                        }
                    }
                    continue;

                case EXIT:
                    userService.exit();
            }
        }
    }

    /**
     * Выводит на консоль историю подачи показаний пользователя
     * @param historyIndications            Содержит историю подачи показаний пользователя
     */
    private void printHistory(Optional<Map<LocalDate, Indication>> historyIndications) {
        Map<LocalDate, Indication> history = historyIndications.get();
        history.forEach((key, value) -> System.out.printf("""
                Дата: %s. Показания:
                    Отопление:      %d
                    Горячая вода:   %d
                    Холодная вода:  %d
                """, key, value.getHeating(), value.getHotWater(), value.getColdWater()));
    }

    /**
     * Выводит на консоль показания пользователя за выбранный месяц
     * @param maybeIndications              Показания пользователя за выбранный месяц
     * @param month                         Месяц, выбранный пользователем
     */
    private void printMonthlyIndications(Optional<List<Indication>> maybeIndications, Month month) {
        List<Indication> monthlyIndications = maybeIndications.get();
        System.out.println("Показания за %s:".formatted(month.name()));
        monthlyIndications.forEach(indication -> {
                    System.out.printf("""
                                Отопление:      %d
                                Горячая вода:   %d
                                Холодная вода:  %d
                            """, indication.getHeating(), indication.getHotWater(), indication.getColdWater());
                }
        );
    }

    /**
     * Процедура регистрации пользователя
     */
    private void registrationProcedure() {
        UserCreateEditDto user = enteringUsernameAndPassword();

        if (!userValidation.isValidInput(user)) {
            System.out.println("Invalid username or password!");
            return;
        }
        userService.registration(user);
    }

    /**
     * Меню приложения
     * @return          Возвращает действие, выбранное пользователем
     */
    private Action applicationMenuForNoAuthenticationUser() {
        System.out.println("Сервис для подачи показаний счетчиков.");
        System.out.println("Выберите действие:");
        System.out.println("1. Регистрация");
        System.out.println("2. Авторизация");
        System.out.println("3. Выйти из приложения");

        return getAction(NO_AUTHENTICATION_USER);
    }

    /**
     * Меню приложения для авторизованного пользователя
     * @return          Возвращает действие, выбранное пользователем
     */
    private Action applicationMenuForAuthorizationUser() {
        System.out.println("Выберите действие:");
        System.out.println("1. Получение актуальных показаний счетчиков");
        System.out.println("2. Подача показаний");
        System.out.println("3. Просмотр показаний за конкретный месяц");
        System.out.println("4. Просмотр истории подачи показаний");
        System.out.println("5. Выйти из аккаунта");
        System.out.println("6. Выйти из приложения");

        return getAction(AUTHORIZATION_USER);
    }

    /**
     * Метод для ввода пользователем username и password
     * @return          Возвращает username и password, введенные пользователем
     */
    private UserCreateEditDto enteringUsernameAndPassword() {
        scanner.nextLine();
        System.out.println("Введите username (от 3 до 10 символов, английские буквы, цифры, знаки _ и -):");
        String username = scanner.nextLine();

        System.out.println("Введите password (от 3 до 10 символов, хотя бы одна буква): ");
        String password = scanner.nextLine();

        return UserCreateEditDto.builder()
                .username(username)
                .password(password)
                .build();
    }

    /**
     * Конвертирует число, выбранное пользователем, в действие (в зависимости от стадии, на которой находится пользователь)
     * @param phase         Стадия, на которой находится пользователь
     * @return              Возвращает действие, выбранное пользователем
     */
    private Action getAction(Phase phase) {
        try {
            Integer actionNumber = scanner.nextInt();
            return actionMapper.map(actionNumber, phase);
        } catch (Exception ex) {
            scanner.nextLine();
            return ERROR;
        }
    }

    /**
     * Метод для ввода пользователем показаний со счетчиков
     * @return          Возвращает показания, введенные пользователем
     */
    private Indication enteringIndications() {
        try {
            System.out.println("Отопление:");
            Long heating = scanner.nextLong();

            System.out.println("Горячая вода:");
            Long hotWater = scanner.nextLong();

            System.out.println("Холодная вода:");
            Long coldWater = scanner.nextLong();

            return new Indication(heating, hotWater, coldWater);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Метод для ввода пользователем месяца
     * @return          Возвращает месяц
     */
    private Month enteringMonth() {
        scanner.nextLine();
        System.out.println("Введите порядковый номер месяца:");
        String monthNumber = scanner.nextLine();
        Month month = monthMapper.map(monthNumber);

        if (month == null) {
            System.out.println("An incorrect number was entered!");
            return null;
        }
        return month;
    }

    /**
     * Выводит последние показания, отправленные пользователем
     * @param actualIndications         Последние показания, отправленные пользователем
     */
    private void printActualIndications(Map<LocalDate, Indication> actualIndications) {
        LocalDate date = actualIndications.entrySet().stream().findFirst().get().getKey();
        Indication indications = actualIndications.entrySet().stream().findFirst().get().getValue();
        System.out.printf("""
                Актуальные показания на %s:
                    Отопление:      %d
                    Горячая вода:   %d
                    Холодная вода:  %d
                """, date, indications.getHeating(), indications.getHotWater(), indications.getColdWater());
    }
}