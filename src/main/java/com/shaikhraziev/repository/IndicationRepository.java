package com.shaikhraziev.repository;

import com.shaikhraziev.aop.annotations.Audit;
import com.shaikhraziev.aop.annotations.Loggable;
import com.shaikhraziev.dto.IndicationCreateEditDto;
import com.shaikhraziev.entity.Indication;
import com.shaikhraziev.util.ConnectionManager;
import lombok.RequiredArgsConstructor;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с показаниями пользователя
 */
@RequiredArgsConstructor
@Loggable
@Audit
public class IndicationRepository {

    private final ConnectionManager connectionManager;
    private static final String FIND_ACTUAL_INDICATIONS_BY_ID = "SELECT id, date, heating, hot_water, cold_water, users_id FROM monitoring.indication WHERE users_id = ? ORDER BY date LIMIT 1";
    private static final String FIND_INDICATIONS_BY_MONTH = "SELECT id, date, heating, hot_water, cold_water, users_id FROM monitoring.indication WHERE users_id = ? AND DATE_PART('month', date) = ?";
    private static final String FIND_HISTORY_INDICATIONS_BY_ID = "SELECT id, date, heating, hot_water, cold_water, users_id FROM monitoring.indication WHERE users_id = ?";
    private static final String UPLOAD_INDICATIONS = "INSERT INTO monitoring.indication(date, heating, hot_water, cold_water, users_id) VALUES(?, ?, ?, ?, ?)";
    private static final String FIND_HISTORY_ALL_USERS = "SELECT id, date, heating, hot_water, cold_water, users_id FROM monitoring.indication ORDER BY users_id";


    /**
     * Ищет актуальные показания по id пользователя
     *
     * @param id id пользователя
     * @return Возвращает актуальные показания, если их передавали
     * @throws SQLException SQLException
     */
    public Optional<Indication> getActualIndications(Long id) throws SQLException {
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
            preparedStatement.setDate(1, Date.valueOf(LocalDate.now()));
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
    public List<Indication> getMonthlyIndications(Long id, Month month) throws SQLException {
        try (var connection = connectionManager.open();
             var preparedStatement = connection.prepareStatement(FIND_INDICATIONS_BY_MONTH)) {
            preparedStatement.setLong(1, id);
            preparedStatement.setInt(2, month.getValue());

            var resultSet = preparedStatement.executeQuery();

            return buildListIndications(resultSet);
        }
    }

    /**
     * Возвращает историю подачи показаний пользователя
     *
     * @param id id пользователя
     * @return Возвращает историю подачи показаний пользователя
     */
    public List<Indication> getHistory(Long id) throws SQLException {
        try (var connection = connectionManager.open();
             var preparedStatement = connection.prepareStatement(FIND_HISTORY_INDICATIONS_BY_ID)) {
            preparedStatement.setLong(1, id);

            var resultSet = preparedStatement.executeQuery();

            return buildListIndications(resultSet);
        }
    }

    /**
     * Получает показания всех пользователей
     *
     * @return Возвращает показания всех пользователей
     * @throws SQLException SQLException
     */
    public List<Indication> getHistory() throws SQLException {
        try (var connection = connectionManager.open();
             var statement = connection.createStatement()) {
            var resultSet = statement.executeQuery(FIND_HISTORY_ALL_USERS);

            return buildListIndications(resultSet);
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
     * Создает показания по полученному ResultSet из БД
     *
     * @param resultSet Результат SELECT запроса в БД, содержит показания
     * @return Возвращает показания
     * @throws SQLException SQLException
     */
    private Optional<Indication> buildIndications(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return Optional.ofNullable(Indication.builder()
                    .id(resultSet.getObject("id", Long.class))
                    .date(resultSet.getObject("date", LocalDate.class))
                    .heating(resultSet.getObject("heating", Long.class))
                    .hotWater(resultSet.getObject("hot_water", Long.class))
                    .coldWater(resultSet.getObject("cold_water", Long.class))
                    .usersId(resultSet.getObject("users_id", Long.class))
                    .build());
        }
        return Optional.empty();
    }

    /**
     * Создает показания по полученному ResultSet из БД
     *
     * @param resultSet Результат SELECT запроса в БД, содержит показания
     * @return Возвращает показания
     * @throws SQLException SQLException
     */
    private List<Indication> buildListIndications(ResultSet resultSet) throws SQLException {
        List<Indication> listIndications = new ArrayList<>();

        while (resultSet.next()) {
            listIndications.add(Indication.builder()
                    .id(resultSet.getObject("id", Long.class))
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