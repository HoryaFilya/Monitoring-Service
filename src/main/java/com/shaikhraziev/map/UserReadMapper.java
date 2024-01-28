package com.shaikhraziev.map;

import com.shaikhraziev.dto.UserReadDto;
import com.shaikhraziev.entity.User;

/**
 * Класс для преобразования User в UserReadDto
 */
public class UserReadMapper {

    /**
     * Преобразует User в UserReadDto
     * @param user          Пользователь
     * @return              Возвращает пользователя для чтения
     */
    public UserReadDto map(User user) {
        return UserReadDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }
}