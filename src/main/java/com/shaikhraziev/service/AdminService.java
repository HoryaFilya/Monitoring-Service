package com.shaikhraziev.service;

import com.shaikhraziev.dto.UserCreateEditDto;
import com.shaikhraziev.entity.Action;
import com.shaikhraziev.entity.Admin;
import com.shaikhraziev.entity.Audit;
import com.shaikhraziev.entity.User;
import com.shaikhraziev.map.ActionAdminMapper;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import static com.shaikhraziev.entity.Action.ERROR;
import static com.shaikhraziev.entity.Action.LOGOUT;

/**
 * Сервис для работы с администратором
 */
@RequiredArgsConstructor
public class AdminService {

    private static final Admin ADMIN = new Admin("admin", "admin");
    private final Scanner scanner = new Scanner(System.in);
    private final UserService userService;
    private final ActionAdminMapper actionAdminMapper;
    private final AuditService auditService;

    /**
     * Проверяет, является ли пользователь администратором
     * @param user      Пользователь
     * @return          Является ли пользователь администратором
     */
    public boolean isAdmin(UserCreateEditDto user) {
        return user.getUsername().equals(ADMIN.getUsername()) && user.getPassword().equals(ADMIN.getPassword());
    }

    /**
     * Выводит на консоль показания всех пользователей
     */
    public void getAllIndications() {
        Optional<Map<String, User>> maybeDatabase = userService.getDatabase();

        if (maybeDatabase.isEmpty() || maybeDatabase.get().isEmpty()) {
            System.out.println("Показания никогда не передавались!");
            return;
        }

        Map<String, User> database = maybeDatabase.get();

        database.forEach((key, value) -> System.out.printf("""
                                Показания пользователя %s:
                                    %s
                """, key, value.getDatabaseIndications()));
    }

    /**
     * Логика работы для администратора
     */
    public void management() {
        while (true) {
            Action action = applicationMenuForAdmin();

            if (action.equals(LOGOUT)) break;

            switch (action) {
                case USER_INDICATIONS:
                    getAllIndications();
                    continue;

                case USER_AUDIT:
                    getUserAudit();
                    continue;

                case EXIT:
                    System.exit(0);

                default:
                    continue;
            }
        }
    }

    /**
     * Выводит на консоль аудит действий пользователей
     */
    private void getUserAudit() {
        List<Audit> audits = auditService.getAudits();

        if (audits.isEmpty()) {
            System.out.println("Пользователи не совершали действий!");
            return;
        }

        audits.forEach(audit -> System.out.println("Дата: %s. Событие: %s".formatted(audit.getDate(), audit.getEvent())));
    }

    /**
     * Меня для яадинистратора
     * @return      Возвращает действие, выбранное администратором
     */
    private Action applicationMenuForAdmin() {
        System.out.println("Выберите действие:");
        System.out.println("1. Показания пользователей");
        System.out.println("2. Аудит действий пользователей");
        System.out.println("3. Выйти из аккаунта");
        System.out.println("4. Выйти из приложения");

        return getAction();
    }

    /**
     * Возвращает действие, выбранное администратором на основании введенного номера
     * @return      Возвращает действие, выбранное администратором
     */
    private Action getAction() {
        try {
            Integer actionNumber = scanner.nextInt();
            return actionAdminMapper.map(actionNumber);
        } catch (Exception ex) {
            scanner.nextLine();
            System.out.println("Invalid value!");
            return ERROR;
        }
    }
}