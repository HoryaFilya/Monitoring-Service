package com.shaikhraziev.entity;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
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
     * Роль пользователя
     */
    private Role role;
}