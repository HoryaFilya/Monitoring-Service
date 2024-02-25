package com.shaikhraziev.map;

import com.shaikhraziev.dto.UserCreateEditDto;
import com.shaikhraziev.entity.User;

import static com.shaikhraziev.entity.Role.USER;

/**
 * Класс для преобразования UserCreateEditDto в User
 */
public class UserCreateEditMapper implements Mapper<UserCreateEditDto, User> {

    /**
     * Преобразует UserCreateEditDto в User
     * @param userDto       Содержит username и password, введенные пользователем
     * @return              Возвращает пользователя
     */
    @Override
    public User map(UserCreateEditDto userDto) {
        return User.builder()
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .role(USER)
                .build();
    }
}