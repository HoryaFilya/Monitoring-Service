package com.shaikhraziev.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Класс администратора
 */
@Data
@AllArgsConstructor
@Builder
public class Admin {
    /**
     * username администратора
     */
    private String username;

    /**
     * password администратора
     */
    private String password;
}