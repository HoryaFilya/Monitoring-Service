package com.shaikhraziev.dto;

import lombok.Builder;
import lombok.Value;

/**
 * Хранит данные пользователя для чтения
 */
@Value
@Builder
public class UserReadDto {
    /**
     * id пользователя
     */
    Long id;

    /**
     * username пользователя
     */
    String username;

    /**
     * password пользователя
     */
    String password;
}