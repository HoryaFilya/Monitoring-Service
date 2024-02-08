package com.shaikhraziev;

import com.shaikhraziev.config.LiquibaseConfig;
import com.shaikhraziev.util.ConnectionManager;
import com.shaikhraziev.util.PropertiesUtil;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class UnitTestBase {
    public static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:14.1")
            .withDatabaseName(PropertiesUtil.get("db.name.test"))
            .withUsername(PropertiesUtil.get("db.username.test"))
            .withPassword(PropertiesUtil.get("db.password.test"));

    @BeforeAll
    static void runContainer() {
        container.start();

        ConnectionManager connectionManager = new ConnectionManager(
                container.getJdbcUrl(),
                container.getUsername(),
                container.getPassword()
        );

        LiquibaseConfig liquibaseConfigForTest = new LiquibaseConfig(connectionManager);

        liquibaseConfigForTest.startMigrations();
    }
}