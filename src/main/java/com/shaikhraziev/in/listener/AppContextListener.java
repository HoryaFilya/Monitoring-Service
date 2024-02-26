package com.shaikhraziev.in.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shaikhraziev.config.LiquibaseConfig;
import com.shaikhraziev.map.*;
import com.shaikhraziev.repository.AuditRepository;
import com.shaikhraziev.repository.IndicationRepository;
import com.shaikhraziev.repository.UserRepository;
import com.shaikhraziev.service.AuditService;
import com.shaikhraziev.service.IndicationService;
import com.shaikhraziev.service.JwtService;
import com.shaikhraziev.service.UserService;
import com.shaikhraziev.util.ConnectionManager;
import com.shaikhraziev.util.PropertiesUtil;
import com.shaikhraziev.validation.UserValidation;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    private final UserCreateEditMapper userCreateEditMapper = new UserCreateEditMapper();
    private final UserReadMapper userReadMapper = new UserReadMapper();
    private final ConnectionManager connectionManager = new ConnectionManager(
            PropertiesUtil.get("db.url"),
            PropertiesUtil.get("db.username"),
            PropertiesUtil.get("db.password")
    );
    private final UserRepository userRepository = new UserRepository(connectionManager);
    private final AuditRepository auditRepository = new AuditRepository(connectionManager);
    private final IndicationRepository indicationRepository = new IndicationRepository(connectionManager);
    private final AuditService auditService = new AuditService(auditRepository);
    private final UserService userService = new UserService(userRepository, userCreateEditMapper, userReadMapper);
    private final IndicationReadMapper indicationReadMapper = new IndicationReadMapper(userService, userReadMapper);
    private final UserValidation userValidation = new UserValidation();
    private final IndicationService indicationService = new IndicationService(indicationRepository, userValidation, indicationReadMapper);
    private final JwtService jwtService = new JwtService();
    private final LiquibaseConfig liquibaseConfig = new LiquibaseConfig(connectionManager);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext ctx = servletContextEvent.getServletContext();

        ctx.setAttribute("userCreateEditMapper", userCreateEditMapper);
        ctx.setAttribute("userReadMapper", userReadMapper);
        ctx.setAttribute("userValidation", userValidation);
        ctx.setAttribute("connectionManager", connectionManager);
        ctx.setAttribute("userRepository", userRepository);
        ctx.setAttribute("auditRepository", auditRepository);
        ctx.setAttribute("indicationRepository", indicationRepository);
        ctx.setAttribute("auditService", auditService);
        ctx.setAttribute("userService", userService);
        ctx.setAttribute("indicationService", indicationService);
        ctx.setAttribute("jwtService", jwtService);
        ctx.setAttribute("liquibaseConfig", liquibaseConfig);
        ctx.setAttribute("objectMapper", objectMapper);

        objectMapper.registerModule(new JavaTimeModule());
        liquibaseConfig.startMigrations();
    }
}