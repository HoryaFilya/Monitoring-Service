package com.shaikhraziev;

import com.shaikhraziev.in.Application;
import com.shaikhraziev.map.*;
import com.shaikhraziev.repository.UserRepository;
import com.shaikhraziev.service.AdminService;
import com.shaikhraziev.service.AuditService;
import com.shaikhraziev.service.UserService;
import com.shaikhraziev.validation.UserValidation;

/**
 * Класс, запускающий приложение
 */
public class ApplicationRunner {

    /**
     * Метод, запускающий приложение
     */
    public static void main( String[] args ) {
        UserRepository userRepository = new UserRepository();

        ActionMapper actionMapper = new ActionMapper();
        ActionAdminMapper actionAdminMapper = new ActionAdminMapper();
        UserCreateEditMapper userCreateEditMapper = new UserCreateEditMapper();
        UserReadMapper userReadMapper = new UserReadMapper();
        MonthMapper monthMapper = new MonthMapper();

        UserValidation userValidation = new UserValidation();

        AuditService auditService = new AuditService();
        UserService userService = new UserService(userRepository, userCreateEditMapper, userReadMapper, userValidation, auditService);
        AdminService adminService = new AdminService(userService, actionAdminMapper, auditService);

        Application application = new Application(actionMapper, monthMapper, userService, adminService, userValidation, auditService);

        application.run();
    }
}