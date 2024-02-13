package com.shaikhraziev.in;

import com.shaikhraziev.dto.IndicationCreateEditDto;
import com.shaikhraziev.dto.IndicationReadDto;
import com.shaikhraziev.dto.UserCreateEditDto;
import com.shaikhraziev.dto.UserReadDto;
import com.shaikhraziev.entity.Action;
import com.shaikhraziev.service.AuditService;
import com.shaikhraziev.service.IndicationService;
import com.shaikhraziev.service.UserService;
import com.shaikhraziev.validation.UserValidation;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.time.Month;
import java.util.List;

import static com.shaikhraziev.entity.Action.*;
import static com.shaikhraziev.entity.Role.ADMIN;

/**
 * Главный класс приложения. Выполняется основной цикл программы.
 */
@RequiredArgsConstructor
public class Application {

    private final UserService userService;
    private final IndicationService indicationService;
    private final AuditService auditService;
    private final Menu menu;
    private final Input input;
    private final Output output;
    private final UserValidation userValidation;

    /**
     * Основной метод программы. Запускает бесконечный цикл для обработки действий пользователя.
     */
    public void run() {

        while (true) {
            Action action = menu.applicationMenuForNoAuthenticationUser();

            switch (action) {
                case REGISTRATION:
                    registrationProcedure();
                    continue;

                case AUTHORIZATION:
                    UserCreateEditDto maybeUser = input.enteringUsernameAndPassword();
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
     * Процедура регистрации пользователя
     */
    private void registrationProcedure() {
        UserCreateEditDto user = input.enteringUsernameAndPassword();

        if (!userValidation.isValidLoginAndPassword(user)) return;

        userService.registration(user);
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
            adminControl();
            return false;
        } else {
            System.out.println("%s has successfully logged in!".formatted(authorizationUser.getUsername()));
            auditService.authorization(authorizationUser.getUsername());
            return true;
        }
    }

    /**
     * Функционал авторизованного пользователя
     * @param authorizationUser     Авторизованный пользователь
     */
    @SneakyThrows
    private void authorizedUserManagement(UserReadDto authorizationUser) {
        while (true) {
            Action actionAuthorizationUser = menu.applicationMenuForAuthorizationUser();

            switch (actionAuthorizationUser) {
                case ACTUAL_INDICATIONS:
                    IndicationReadDto actualIndications = indicationService.getActualIndications(authorizationUser.getId()).orElse(null);

                    if (actualIndications == null) {
                        System.out.println("Indications was not transmitted!");
                        continue;
                    }

                    output.printIndicationsAndAudit(actualIndications, authorizationUser);
                    continue;

                case UPLOAD_INDICATIONS:
                    IndicationCreateEditDto indications = input.enteringIndications();

                    if (!userValidation.isValidUploadIndications(indications)) continue;

                    indicationService.uploadIndications(authorizationUser.getId(), indications);
                    continue;

                case MONTHLY_INDICATIONS:
                    Month month = input.enteringMonth();

                    if (month == null) continue;

                    List<IndicationReadDto> monthlyIndications = indicationService.getMonthlyIndications(authorizationUser.getId(), month);

                    if (monthlyIndications.isEmpty()) {
                        System.out.println("Indications was not uploaded this month!");
                        continue;
                    }

                    output.printMonthlyIndications(monthlyIndications, month);
                    auditService.getMonthlyIndications(authorizationUser.getUsername(), month);
                    continue;

                case HISTORY:
                    List<IndicationReadDto> maybeHistory = indicationService.getHistory(authorizationUser.getId());

                    if (maybeHistory.isEmpty()) {
                        System.out.println("Indications was never transmitted!");
                        continue;
                    }

                    output.printHistory(maybeHistory);
                    auditService.getHistory(authorizationUser.getUsername());
                    continue;

                case LOGOUT:
                    auditService.logout(authorizationUser.getUsername());
                    return;

                case EXIT:
                    auditService.logout(authorizationUser.getUsername());
                    userService.exit();

                case ERROR:
                    System.out.println("An incorrect number was entered!");
            }
        }
    }

    /**
     * Логика работы для администратора
     */
    public void adminControl() {
        while (true) {
            Action action = menu.applicationMenuForAdmin();

            if (action.equals(LOGOUT)) break;

            switch (action) {
                case USER_INDICATIONS:
                    indicationService.printHistoryAllUsers();
                    continue;

                case USER_AUDIT:
                    auditService.getUserAudit();
                    continue;

                case EXIT:
                    System.exit(0);

                default:
                    continue;
            }
        }
    }
}