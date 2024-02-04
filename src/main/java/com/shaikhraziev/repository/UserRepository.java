package com.shaikhraziev.repository;

import com.shaikhraziev.dto.*;
import com.shaikhraziev.entity.Indication;
import com.shaikhraziev.entity.Role;
import com.shaikhraziev.entity.User;
import com.shaikhraziev.util.ConnectionManager;
import lombok.RequiredArgsConstructor;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
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
    private static final String FIND_ACTUAL_INDICATIONS_BY_ID = "SELECT date, heating, hot_water, cold_water, users_id FROM monitoring.indication WHERE users_id = ? ORDER BY date LIMIT 1";
    private static final String FIND_INDICATIONS_BY_MONTH = "SELECT date, heating, hot_water, cold_water FROM monitoring.indication WHERE users_id = ? AND DATE_PART('month', date) = ?";
    private static final String FIND_HISTORY_INDICATIONS_BY_ID = "SELECT date, heating, hot_water, cold_water FROM monitoring.indication WHERE users_id = ?";
    private static final String UPLOAD_INDICATIONS = "INSERT INTO monitoring.indication(date, heating, hot_water, cold_water, users_id) VALUES(?, ?, ?, ?, ?)";
    private static final String FIND_HISTORY_ALL_USERS = "SELECT date, heating, hot_water, cold_water, users_id FROM monitoring.indication ORDER BY users_id";

    /**
     * Сохраняет пользователя в хранилище
     *
     * @param user Пользователь
     * @return Возращает сохраненного пользователя
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
     * @param userDto username и password, введенные пользователем
     * @return Возвращает пользователя, если он существует
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
     * @param username username пользователя
     * @return Возвращает пользователя, если он существует
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
     * Ищет актуальные показания по id пользователя
     *
     * @param id id пользователя
     * @return Возвращает актуальные показания, если их передавали
     * @throws SQLException SQLException
     */
    public Optional<IndicationReadDto> getActualIndications(Long id) throws SQLException {
        try (var connection = connectionManager.open();
             var preparedStatement = connection.prepareStatement(FIND_ACTUAL_INDICATIONS_BY_ID)) {
            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();
            return buildIndications(resultSet);
        }
    }

    /**
     * Подает показания пользователя
     *
     * @param id          id пользователя
     * @param indications Переданные показания
     * @throws SQLException SQLException
     */
    public void uploadIndications(Long id, IndicationCreateEditDto indications) throws SQLException {
        try (var connection = connectionManager.open();
             var preparedStatement = connection.prepareStatement(UPLOAD_INDICATIONS)) {
            preparedStatement.setDate(1, Date.valueOf(indications.getDate()));
            preparedStatement.setLong(2, indications.getHeating());
            preparedStatement.setLong(3, indications.getHotWater());
            preparedStatement.setLong(4, indications.getColdWater());
            preparedStatement.setLong(5, id);

            preparedStatement.executeUpdate();
        }
    }

    /**
     * Возращает показания за конкретный месяц
     *
     * @param id    id пользователя
     * @param month Месяц
     * @return Возращает показания за конкретный месяц
     */
    public List<IndicationReadDto> getMonthlyIndications(Long id, Month month) throws SQLException {
        try (var connection = connectionManager.open();
             var preparedStatement = connection.prepareStatement(FIND_INDICATIONS_BY_MONTH)) {
            preparedStatement.setLong(1, id);
            preparedStatement.setInt(2, month.getValue());

            var resultSet = preparedStatement.executeQuery();

            return buildListIndicationsRead(resultSet);
        }
    }

    /**
     * Возвращает историю подачи показаний пользователя
     *
     * @param id id пользователя
     * @return Возвращает историю подачи показаний пользователя
     */
    public List<IndicationReadDto> getHistory(Long id) throws SQLException {
        try (var connection = connectionManager.open();
             var preparedStatement = connection.prepareStatement(FIND_HISTORY_INDICATIONS_BY_ID)) {
            preparedStatement.setLong(1, id);

            var resultSet = preparedStatement.executeQuery();

            return buildListIndicationsRead(resultSet);
        }
    }

    public List<Indication> getHistory() throws SQLException {
        try (var connection = connectionManager.open();
             var statement = connection.createStatement()) {
            var resultSet = statement.executeQuery(FIND_HISTORY_ALL_USERS);

            return buildListIndications(resultSet);
        }
    }

    public Optional<UserReadDtoWithoutPassword> findById(Long id) throws SQLException {
        try (var connection = connectionManager.open();
             var preparedStatement = connection.prepareStatement(FIND_USER_BY_ID)) {
            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();

            return buildUseWithoutPassword(resultSet);
        }
    }

    /**
     * Проверка, что показания не передавались в этом месяце
     *
     * @param id id пользователя
     * @return Возращает true - если показания уже передавались в этом месяце, иначе - false
     */
    public boolean indicationsAlreadyUploaded(Long id, Month currentMonth) throws SQLException {
        try (var connection = connectionManager.open();
             var preparedStatement = connection.prepareStatement(FIND_INDICATIONS_BY_MONTH)) {
            preparedStatement.setLong(1, id);
            preparedStatement.setInt(2, currentMonth.getValue());
            var resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    /**
     * Создает пользователя по полученному ResultSet из БД
     *
     * @param resultSet Результат SELECT запроса в БД, содержит пользователя
     * @return Возвращает пользователя, если он существует
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

    private Optional<UserReadDtoWithoutPassword> buildUseWithoutPassword(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return Optional.ofNullable(UserReadDtoWithoutPassword.builder()
                    .username(resultSet.getObject("username", String.class))
                    .build());
        }
        return Optional.empty();
    }

    private List<IndicationReadDto> buildListIndicationsRead(ResultSet resultSet) throws SQLException {
        List<IndicationReadDto> listIndications = new ArrayList<>();

        while (resultSet.next()) {
            listIndications.add(IndicationReadDto.builder()
                    .date(resultSet.getObject("date", LocalDate.class))
                    .heating(resultSet.getObject("heating", Long.class))
                    .hotWater(resultSet.getObject("hot_water", Long.class))
                    .coldWater(resultSet.getObject("cold_water", Long.class))
                    .build());
        }
        return listIndications;
    }

    private Optional<IndicationReadDto> buildIndications(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return Optional.ofNullable(IndicationReadDto.builder()
                    .date(resultSet.getObject("date", LocalDate.class))
                    .heating(resultSet.getObject("heating", Long.class))
                    .hotWater(resultSet.getObject("hot_water", Long.class))
                    .coldWater(resultSet.getObject("cold_water", Long.class))
                    .build());
        }
        return Optional.empty();
    }

    private List<Indication> buildListIndications(ResultSet resultSet) throws SQLException {
        List<Indication> listIndications = new ArrayList<>();

        while (resultSet.next()) {
            listIndications.add(Indication.builder()
                    .date(resultSet.getObject("date", LocalDate.class))
                    .heating(resultSet.getObject("heating", Long.class))
                    .hotWater(resultSet.getObject("hot_water", Long.class))
                    .coldWater(resultSet.getObject("cold_water", Long.class))
                    .usersId(resultSet.getObject("users_id", Long.class))
                    .build());
        }
        return listIndications;
    }
}