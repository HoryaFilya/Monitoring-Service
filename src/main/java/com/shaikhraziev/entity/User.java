package com.shaikhraziev.entity;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс пользователя
 */
@Data
@Builder
public class User {

    /**
     * id пользователя
     */
    private Long id;

    /**
     * username пользователя
     */
    private String username;

    /**
     * password пользователя
     */
    private String password;

    /**
     * История всех показаний пользователя
     */
    @Builder.Default
    private Map<LocalDate, Indication> databaseIndications = new HashMap<>();

    /**
     * Дата последних отправленных показаний пользователем
     */
    private LocalDate dateActualIndications;

    /**
     * Переменная для отслеживания кол-ва зарегистрированных пользователей
     */
    @Getter
    private static Long count = 1L;

    /**
     * Конструктор для создания пользователя
     * @param id:                       id пользователя
     * @param username                  username пользователя
     * @param password                  password пользователя
     * @param databaseIndications       История всех показаний пользователя
     * @param dateActualIndications     Дата последних отправленных показаний пользователем
     */
    public User(Long id, String username, String password, Map<LocalDate, Indication> databaseIndications, LocalDate dateActualIndications) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.databaseIndications = databaseIndications;
        this.dateActualIndications = dateActualIndications;
        count++;
    }
}