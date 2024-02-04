package com.shaikhraziev;

import com.shaikhraziev.config.LiquibaseConfig;
import com.shaikhraziev.in.Application;
import com.shaikhraziev.map.*;
import com.shaikhraziev.repository.UserRepository;
import com.shaikhraziev.repository.AuditRepository;
import com.shaikhraziev.service.UserService;
import com.shaikhraziev.util.ConnectionManager;
import com.shaikhraziev.util.PropertiesUtil;
import com.shaikhraziev.validation.UserValidation;

/**
 * Класс, запускающий приложение
 */
public class ApplicationRunner {

    /**
     * Метод, запускающий приложение
     */
    public static void main( String[] args ) {
        ActionUserMapper actionUserMapper = new ActionUserMapper();
        ActionAdminMapper actionAdminMapper = new ActionAdminMapper();
        UserCreateEditMapper userCreateEditMapper = new UserCreateEditMapper();
        UserReadMapper userReadMapper = new UserReadMapper();

        UserValidation userValidation = new UserValidation();

        ConnectionManager connectionManager = new ConnectionManager(
                PropertiesUtil.get("db.url"),
                PropertiesUtil.get("db.username"),
                PropertiesUtil.get("db.password")
        );

        UserRepository userRepository = new UserRepository(connectionManager);
        AuditRepository auditRepository = new AuditRepository(connectionManager);

        UserService userService = new UserService(userRepository, userCreateEditMapper, userReadMapper, actionAdminMapper, userValidation, auditRepository);

        Application application = new Application(actionUserMapper, userService, userValidation, auditRepository);

        LiquibaseConfig liquibaseConfig = new LiquibaseConfig(connectionManager);

        liquibaseConfig.startMigrations();

        application.run();
    }
}