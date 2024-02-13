package com.shaikhraziev;

import com.shaikhraziev.config.LiquibaseConfig;
import com.shaikhraziev.in.*;
import com.shaikhraziev.map.*;
import com.shaikhraziev.repository.IndicationRepository;
import com.shaikhraziev.repository.UserRepository;
import com.shaikhraziev.repository.AuditRepository;
import com.shaikhraziev.service.AuditService;
import com.shaikhraziev.service.IndicationService;
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

        ActionUser actionUser = new ActionUser(actionUserMapper, actionAdminMapper);
        Menu menu = new Menu(actionUser);
        Input input = new Input();

        UserRepository userRepository = new UserRepository(connectionManager);
        AuditRepository auditRepository = new AuditRepository(connectionManager);
        IndicationRepository indicationRepository = new IndicationRepository(connectionManager);

        AuditService auditService = new AuditService(auditRepository);
        UserService userService = new UserService(auditService, userRepository, userCreateEditMapper, userReadMapper);
        IndicationService indicationService = new IndicationService(userService, auditService, userRepository, indicationRepository, userValidation);

        Output output = new Output(auditService);

        Application application = new Application(userService, indicationService, auditService, menu, input, output, userValidation);

        LiquibaseConfig liquibaseConfig = new LiquibaseConfig(connectionManager);

        liquibaseConfig.startMigrations();

        application.run();
    }
}