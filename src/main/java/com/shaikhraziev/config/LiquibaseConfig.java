package com.shaikhraziev.config;

import com.shaikhraziev.util.ConnectionManager;
import com.shaikhraziev.util.PropertiesUtil;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;

/**
 * Конфигурация Liquibase
 */
@RequiredArgsConstructor
public class LiquibaseConfig {

    private final ConnectionManager connectionManager;
    private static final String SCHEMA_FOR_LIQUIBASE = PropertiesUtil.get("db.schema.liquibase");
    private static final String CHANGELOG_FILE = PropertiesUtil.get("changelog.file");
    private static final String CREATE_SCHEMA_FOR_LIQUIBASE = "CREATE SCHEMA IF NOT EXISTS %s".formatted(SCHEMA_FOR_LIQUIBASE);

    /**
     * Запускает миграции
     */
    public void startMigrations() {
        try (var connection = connectionManager.open();
             var statement = connection.createStatement()) {
            statement.execute(CREATE_SCHEMA_FOR_LIQUIBASE);

            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            database.setDefaultSchemaName(SCHEMA_FOR_LIQUIBASE);

            Liquibase liquibase = new Liquibase(CHANGELOG_FILE, new ClassLoaderResourceAccessor(), database);
            liquibase.update();
            System.out.println("Migration is completed successfully!");
        } catch (SQLException | LiquibaseException e) {
            System.out.println("SQL Exception in migration " + e.getMessage());
        }
    }
}
