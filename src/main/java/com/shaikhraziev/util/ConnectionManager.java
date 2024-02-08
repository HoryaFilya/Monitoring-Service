package com.shaikhraziev.util;

import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Позволяет получить соединение с БД
 */
@RequiredArgsConstructor
public class ConnectionManager {

    private final String URL;
    private final String USERNAME;
    private final String PASSWORD;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Позволяет получить соединение с БД
     * @return      Соединение с БД
     */
    public Connection open() {
        try {
            return DriverManager.getConnection(
                    URL,
                    USERNAME,
                    PASSWORD
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}