package com.shaikhraziev.map;

import com.shaikhraziev.dto.UserCreateEditDto;
import com.shaikhraziev.entity.Role;
import com.shaikhraziev.entity.User;

/**
 * Класс для преобразования UserCreateEditDto в User
 */
public class UserCreateEditMapper {

    /**
     * Преобразует UserCreateEditDto в User
     * @param userDto       Содержит username и password, введенные пользователем
     * @return              Возвращает пользователя
     */
    public User map(UserCreateEditDto userDto) {
        return User.builder()
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .role(userDto.getRole())
                .build();
    }
}