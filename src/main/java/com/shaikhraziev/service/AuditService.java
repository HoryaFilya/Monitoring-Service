package com.shaikhraziev.service;

import com.shaikhraziev.entity.Audit;
import com.shaikhraziev.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.sql.SQLException;
import java.time.Month;
import java.util.List;

/**
 * Сервис для работы с аудитом
 */
@RequiredArgsConstructor
public class AuditService {

    private final AuditRepository auditRepository;

    /**
     * Аудирование при регистрации
     *
     * @param username username пользователя
     */
    public void registration(String username) throws SQLException {
        auditRepository.registration(username);
    }

    /**
     * Аудирование при авторизации
     *
     * @param username username пользователя
     */
    public void authorization(String username) throws SQLException {
        auditRepository.authentication(username);
    }

    /**
     * Аудирование при выходе из приложения
     *
     * @param username username пользователя
     */
    public void logout(String username) throws SQLException {
        auditRepository.logout(username);
    }

    /**
     * Аудирование при получении актуальных показаний
     *
     * @param username username пользователя
     */
    public void getActualIndications(String username) throws SQLException {
        auditRepository.getActualIndications(username);
    }

    /**
     * Аудирование при подачи показаний
     *
     * @param username username пользователя
     */
    public void uploadIndications(String username, Month month) throws SQLException {
        auditRepository.uploadIndications(username, month);
    }

    /**
     * Аудирование при получении показаний за конкретный месяц
     *
     * @param username username пользователя
     * @param month    Месяц
     */
    public void getMonthlyIndications(String username, Month month) throws SQLException {
        auditRepository.getMonthlyIndications(username, month);
    }


    /**
     * Аудирование при получении истории подачи показаний
     *
     * @param username username пользователя
     */
    public void getHistory(String username) throws SQLException {
        auditRepository.getHistory(username);
    }

    /**
     * Выводит на консоль аудит действий пользователей
     */
    @SneakyThrows
    public List<String> getUserAudit() {
        List<Audit> audits = auditRepository.findAuditsAllUser();

        if (audits.isEmpty())
            return List.of("Пользователи не совершали действий!");

        return audits.stream()
                .map(audit -> "Дата: %s. Событие: %s".formatted(audit.getDate(), audit.getEvent()))
                .toList();
    }
}