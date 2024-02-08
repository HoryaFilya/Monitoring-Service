package com.shaikhraziev.repository;

import com.shaikhraziev.entity.Audit;
import com.shaikhraziev.util.ConnectionManager;
import lombok.Getter;
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
 * Репозиторий для аудита действий пользователя
 */
@RequiredArgsConstructor
public class AuditRepository {

    private final ConnectionManager connectionManager;

    private static final String AUDIT = "INSERT INTO monitoring.audit(date, event) VALUES(?, ?)";
    private static final String FIND_AUDITS_ALL_USER = "SELECT date, event FROM monitoring.audit";

    /**
     * Аудирование при регистрации
     *
     * @param username username пользователя
     */
    public void registration(String username) throws SQLException {
        try (var connection = connectionManager.open();
             var preparedStatement = connection.prepareStatement(AUDIT)) {
            preparedStatement.setDate(1, Date.valueOf(LocalDate.now()));
            preparedStatement.setString(2, "Пользователь %s зарегистрировался.".formatted(username));
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Аудирование при авторизации
     *
     * @param username username пользователя
     */
    public void authorization(String username) throws SQLException {
        try (var connection = connectionManager.open();
             var preparedStatement = connection.prepareStatement(AUDIT)) {
            preparedStatement.setDate(1, Date.valueOf(LocalDate.now()));
            preparedStatement.setString(2, "Пользователь %s авторизовался.".formatted(username));
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Аудирование при выходе из приложения
     *
     * @param username username пользователя
     */
    public void logout(String username) throws SQLException {
        try (var connection = connectionManager.open();
             var preparedStatement = connection.prepareStatement(AUDIT)) {
            preparedStatement.setDate(1, Date.valueOf(LocalDate.now()));
            preparedStatement.setString(2, "Пользователь %s вышел из системы.".formatted(username));
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Аудирование при получении актуальных показаний
     *
     * @param username username пользователя
     */
    public void getActualIndications(String username) throws SQLException {
        try (var connection = connectionManager.open();
             var preparedStatement = connection.prepareStatement(AUDIT)) {
            preparedStatement.setDate(1, Date.valueOf(LocalDate.now()));
            preparedStatement.setString(2, "Пользователь %s получил актуальные показания.".formatted(username));
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Аудирование при подачи показаний
     *
     * @param username username пользователя
     */
    public void uploadIndications(String username) throws SQLException {
        try (var connection = connectionManager.open();
             var preparedStatement = connection.prepareStatement(AUDIT)) {
            preparedStatement.setDate(1, Date.valueOf(LocalDate.now()));
            preparedStatement.setString(2, "Пользователь %s подал показания.".formatted(username));
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Аудирование при получении показаний за конкретный месяц
     *
     * @param username username пользователя
     * @param month    Месяц
     */
    public void getMonthlyIndications(String username, Month month) throws SQLException {
        try (var connection = connectionManager.open();
             var preparedStatement = connection.prepareStatement(AUDIT)) {
            preparedStatement.setDate(1, Date.valueOf(LocalDate.now()));
            preparedStatement.setString(2, "Пользователь %s получил показания за %s.".formatted(username, month));
            preparedStatement.executeUpdate();
        }
    }


    /**
     * Аудирование при получении истории подачи показаний
     *
     * @param username username пользователя
     */
    public void getHistory(String username) throws SQLException {
        try (var connection = connectionManager.open();
             var preparedStatement = connection.prepareStatement(AUDIT)) {
            preparedStatement.setDate(1, Date.valueOf(LocalDate.now()));
            preparedStatement.setString(2, "Пользователь %s получил историю показаний.".formatted(username));
            preparedStatement.executeUpdate();
        }
    }


    /**
     * Возвращает аудит действий всех пользователей
     * @return                  Возвращает аудит действий всех пользователей
     * @throws SQLException     SQLException
     */
    public List<Audit> findAuditsAllUser() throws SQLException {
        try (var connection = connectionManager.open();
             var statement = connection.createStatement()) {
            var resultSet = statement.executeQuery(FIND_AUDITS_ALL_USER);
            return buildAudit(resultSet);
        }
    }

    /**
     * Обрабатывает SELECT запрос с БД
     * @param resultSet         Ответ от БД
     * @return                  Возвразает аудит действий всех пользователей
     * @throws SQLException     SQLException
     */
    private List<Audit> buildAudit(ResultSet resultSet) throws SQLException {
        List<Audit> audits = new ArrayList<>();

        while (resultSet.next()) {
            audits.add(Audit.builder()
                    .date(resultSet.getObject("date", Date.class).toLocalDate())
                    .event(resultSet.getObject("event", String.class))
                    .build());
        }

        return audits;
    }
}