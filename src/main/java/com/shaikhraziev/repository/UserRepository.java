package com.shaikhraziev.repository;

import com.shaikhraziev.dto.*;
import com.shaikhraziev.entity.Role;
import com.shaikhraziev.entity.User;
import com.shaikhraziev.util.ConnectionManager;
import lombok.RequiredArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Репозиторий для работы с информацией пользователя
 */
@RequiredArgsConstructor
public class UserRepository {

    private final ConnectionManager connectionManager;
    private static final String CREATE_USER = "INSERT INTO monitoring.users(username, password, role) VALUES(?, ?, ?)";
    private static final String FIND_USER_BY_USERNAME_AND_PASSWORD = "SELECT id, username, password, role FROM monitoring.users WHERE username = ? AND password = ?";
    private static final String FIND_USER_BY_USERNAME = "SELECT id, username, password, role FROM monitoring.users WHERE username = ?";
    private static final String FIND_USER_BY_ID = "SELECT id, username, password FROM monitoring.users WHERE id = ?";

    /**
     * Сохраняет пользователя в БД
     *
     * @param user  Пользователь
     * @return      Возращает true при успешной регистрации
     */
    public boolean save(User user) throws SQLException {
        try (var connection = connectionManager.open();
             var preparedStatement = connection.prepareStatement(CREATE_USER)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, String.valueOf(user.getRole()));
            preparedStatement.execute();
            return true;
        }
    }

    /**
     * Ищет пользователя по username и password
     *
     * @param userDto   username и password, введенные пользователем
     * @return          Возвращает пользователя, если он существует
     */
    public Optional<User> findByUsernameAndPassword(UserCreateEditDto userDto) throws SQLException {
        try (var connection = connectionManager.open();
             var preparedStatement = connection.prepareStatement(FIND_USER_BY_USERNAME_AND_PASSWORD)) {
            preparedStatement.setString(1, userDto.getUsername());
            preparedStatement.setString(2, userDto.getPassword());
            var resultSet = preparedStatement.executeQuery();
            return buildUser(resultSet);
        }
    }

    /**
     * Ищет пользователя по username
     *
     * @param username      username пользователя
     * @return              Возвращает пользователя, если он существует
     */
    public Optional<User> findByUsername(String username) throws SQLException {
        try (var connection = connectionManager.open();
             var preparedStatement = connection.prepareStatement(FIND_USER_BY_USERNAME)) {
            preparedStatement.setString(1, username);
            var resultSet = preparedStatement.executeQuery();
            return buildUser(resultSet);
        }
    }

    /**
     * Ищет пользователя по id
     * @param id                id пользователя
     * @return                  Возвращает пользователя по id
     * @throws SQLException     SQLException
     */
    public Optional<UserReadDtoWithoutPassword> findById(Long id) throws SQLException {
        try (var connection = connectionManager.open();
             var preparedStatement = connection.prepareStatement(FIND_USER_BY_ID)) {
            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();

            return buildUseWithoutPassword(resultSet);
        }
    }

    /**
     * Создает пользователя по полученному ResultSet из БД
     *
     * @param resultSet     Результат SELECT запроса в БД, содержит пользователя
     * @return              Возвращает пользователя, если он существует
     * @throws SQLException SQLException
     */
    private Optional<User> buildUser(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            var user = User.builder()
                    .id(resultSet.getObject("id", Long.class))
                    .username(resultSet.getObject("username", String.class))
                    .password(resultSet.getObject("password", String.class))
                    .role(Role.valueOf(resultSet.getObject("role", String.class)))
                    .build();
            return Optional.ofNullable(user);
        }
        return Optional.empty();
    }

    /**
     * Создает пользователя по полученному ResultSet из БД
     * @param resultSet         Результат SELECT запроса в БД, содержит пользователя
     * @return                  Возвращает пользователя, если он существует
     * @throws SQLException     SQLException
     */
    private Optional<UserReadDtoWithoutPassword> buildUseWithoutPassword(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return Optional.ofNullable(UserReadDtoWithoutPassword.builder()
                    .username(resultSet.getObject("username", String.class))
                    .build());
        }
        return Optional.empty();
    }
}