package com.shaikhraziev.dto;

import com.shaikhraziev.entity.Role;
import lombok.Builder;
import lombok.Value;

/**
 * Хранит username и password, введенные пользователем
 */
@Value
@Builder
public class UserCreateEditDto {
    /**
     * username возможного пользователя
     */
    String username;

    /**
     * password возможного пользователя
     */
    String password;

    /**
     * Роль возможного пользователя
     */
    Role role;
}