package com.shaikhraziev.dto;

import lombok.Builder;
import lombok.Value;

/**
 * Хранит имя пользователя для чтения
 */
@Value
@Builder
public class UserReadDtoWithoutPassword {
    /**
     * username пользователя
     */
    String username;
}