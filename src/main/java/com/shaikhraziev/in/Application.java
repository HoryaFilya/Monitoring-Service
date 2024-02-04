package com.shaikhraziev.in;

import com.shaikhraziev.dto.IndicationCreateEditDto;
import com.shaikhraziev.dto.IndicationReadDto;
import com.shaikhraziev.dto.UserCreateEditDto;
import com.shaikhraziev.dto.UserReadDto;
import com.shaikhraziev.entity.Action;
import com.shaikhraziev.entity.Phase;
import com.shaikhraziev.map.ActionUserMapper;
import com.shaikhraziev.repository.AuditRepository;
import com.shaikhraziev.service.UserService;
import com.shaikhraziev.validation.UserValidation;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Scanner;

import static com.shaikhraziev.entity.Action.*;
import static com.shaikhraziev.entity.Phase.*;
import static com.shaikhraziev.entity.Role.ADMIN;
import static com.shaikhraziev.entity.Role.USER;

/**
 * Главный класс приложения. Выполняется основной цикл программы.
 */
@RequiredArgsConstructor
public class Application {

    private final Scanner scanner = new Scanner(System.in);
    private final ActionUserMapper actionUserMapper;
    private final UserService userService;
    private final UserValidation userValidation;
    private final AuditRepository auditRepository;

    /**
     * Основной метод программы. Запускает бесконечный цикл для обработки действий пользователя.
     */
    public void run() {

        while (true) {
            Action action = applicationMenuForNoAuthenticationUser();

            switch (action) {
                case REGISTRATION:
                    registrationProcedure();
                    continue;

                case AUTHORIZATION:
                    UserCreateEditDto maybeUser = enteringUsernameAndPassword();
                    UserReadDto authorizationUser = userService.authorization(maybeUser).orElse(null);

                    if (!successfulAuthorization(authorizationUser)) continue;

                    authorizedUserManagement(authorizationUser);
                    continue;

                case ERROR:
                    System.out.println("An incorrect number was entered!");
                    continue;

                case EXIT:
                    userService.exit();
            }
        }
    }

    /**
     * Функционал авторизованного пользователя
     * @param authorizationUser     Авторизованный пользователь
     */
    @SneakyThrows
    private void authorizedUserManagement(UserReadDto authorizationUser) {
        while (true) {
            Action actionAuthorizationUser = applicationMenuForAuthorizationUser();

            switch (actionAuthorizationUser) {
                case ACTUAL_INDICATIONS:
                    IndicationReadDto actualIndications = userService.getActualIndications(authorizationUser.getId()).orElse(null);

                    if (actualIndications == null) {
                        System.out.println("Indications was not transmitted!");
                        continue;
                    }

                    printIndicationsAndAudit(actualIndications, authorizationUser);
                    continue;

                case UPLOAD_INDICATIONS:
                    IndicationCreateEditDto indications = enteringIndications();

                    if (!userValidation.isValidUploadIndications(indications)) continue;

                    userService.uploadIndications(authorizationUser.getId(), indications);
                    continue;

                case MONTHLY_INDICATIONS:
                    Month month = enteringMonth();

                    if (month == null) continue;

                    List<IndicationReadDto> monthlyIndications = userService.getMonthlyIndications(authorizationUser.getId(), month);

                    if (monthlyIndications.isEmpty()) {
                        System.out.println("Indications was not uploaded this month!");
                        continue;
                    }

                    printMonthlyIndications(monthlyIndications, month);
                    auditRepository.getMonthlyIndications(authorizationUser.getUsername(), month);
                    continue;

                case HISTORY:
                    List<IndicationReadDto> maybeHistory = userService.getHistory(authorizationUser.getId());

                    if (maybeHistory.isEmpty()) {
                        System.out.println("Indications was never transmitted!");
                        continue;
                    }

                    printHistory(maybeHistory);
                    auditRepository.getHistory(authorizationUser.getUsername());
                    continue;

                case LOGOUT:
                    auditRepository.logout(authorizationUser.getUsername());
                    return;

                case EXIT:
                    auditRepository.logout(authorizationUser.getUsername());
                    userService.exit();

                case ERROR:
                    System.out.println("An incorrect number was entered!");
            }
        }
    }

    /**
     * Авторизация пользователя
     * @param authorizationUser     Возможный авторизованный пользователь
     * @return                      Возвращает true, если user зарегистрирован
     */
    @SneakyThrows
    private boolean successfulAuthorization(UserReadDto authorizationUser) {
        if (authorizationUser == null) {
            System.out.println("There is no such user!");
            return false;
        } else if (authorizationUser.getRole().equals(ADMIN)) {
            userService.adminControl();
            return false;
        } else {
            System.out.println("%s has successfully logged in!".formatted(authorizationUser.getUsername()));
            auditRepository.authorization(authorizationUser.getUsername());
            return true;
        }
    }

    /**
     * Выводит актуальные показания пользователя
     * @param indication            Актуальные показания пользователя
     * @param authorizationUser     Авторизованный пользователь
     */
    @SneakyThrows
    private void printIndicationsAndAudit(IndicationReadDto indication, UserReadDto authorizationUser) {
        printActualIndications(indication);
        auditRepository.getActualIndications(authorizationUser.getUsername());
    }

    /**
     * Выводит на консоль историю подачи показаний пользователя
     *
     * @param history Содержит историю подачи показаний пользователя
     */
    private void printHistory(List<IndicationReadDto> history) {
        history.forEach((indications -> System.out.printf("""
                Дата: %s. Показания:
                    Отопление:      %d
                    Горячая вода:   %d
                    Холодная вода:  %d
                """, indications.getDate(), indications.getHeating(), indications.getHotWater(), indications.getColdWater())));
    }

    /**
     * Выводит на консоль показания пользователя за выбранный месяц
     *
     * @param monthlyIndications Показания пользователя за выбранный месяц
     * @param month              Месяц, выбранный пользователем
     */
    private void printMonthlyIndications(List<IndicationReadDto> monthlyIndications, Month month) {
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

        if (!userValidation.isValidLoginAndPassword(user)) return;

        userService.registration(user);
    }

    /**
     * Меню приложения
     *
     * @return Возвращает действие, выбранное пользователем
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
     *
     * @return Возвращает действие, выбранное пользователем
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
     *
     * @return Возвращает username и password, введенные пользователем
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
     *
     * @param phase Стадия, на которой находится пользователь
     * @return Возвращает действие, выбранное пользователем
     */
    private Action getAction(Phase phase) {
        try {
            Integer actionNumber = scanner.nextInt();
            return actionUserMapper.map(actionNumber, phase);
        } catch (Exception ex) {
            scanner.nextLine();
            return ERROR;
        }
    }

    /**
     * Метод для ввода пользователем показаний со счетчиков
     *
     * @return Возвращает показания, введенные пользователем
     */
    private IndicationCreateEditDto enteringIndications() {
        try {
            System.out.println("Отопление:");
            Long heating = scanner.nextLong();

            System.out.println("Горячая вода:");
            Long hotWater = scanner.nextLong();

            System.out.println("Холодная вода:");
            Long coldWater = scanner.nextLong();

            return IndicationCreateEditDto.builder()
                    .date(LocalDate.now())
                    .heating(heating)
                    .hotWater(hotWater)
                    .coldWater(coldWater)
                    .build();
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Метод для ввода пользователем месяца
     *
     * @return Возвращает месяц
     */
    private Month enteringMonth() {
        scanner.nextLine();
        System.out.println("Введите порядковый номер месяца:");

        try {
            int monthNumber = scanner.nextInt();
            return Month.of(monthNumber);
        } catch (Exception e) {
            System.out.println("An incorrect number was entered!");
            return null;
        }
    }

    /**
     * Выводит последние показания, отправленные пользователем
     *
     * @param indication Последние показания, отправленные пользователем
     */
    private void printActualIndications(IndicationReadDto indication) {
        System.out.printf("""
                Актуальные показания на %s:
                    Отопление:      %d
                    Горячая вода:   %d
                    Холодная вода:  %d
                """, indication.getDate(), indication.getHeating(), indication.getHotWater(), indication.getColdWater());
    }
}