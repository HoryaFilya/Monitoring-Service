package com.shaikhraziev.dto;

import lombok.*;

/**
 * Хранит username и password, введенные пользователем
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
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
}