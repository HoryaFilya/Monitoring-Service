package com.shaikhraziev.service;

import com.shaikhraziev.aop.annotations.Audit;
import com.shaikhraziev.dto.*;
import com.shaikhraziev.entity.*;
import com.shaikhraziev.map.UserCreateEditMapper;
import com.shaikhraziev.map.UserReadMapper;
import com.shaikhraziev.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.*;

/**
 * Сервис для работы с пользователем
 */
@RequiredArgsConstructor
@Audit
public class UserService {

    private final UserRepository userRepository;
    private final UserCreateEditMapper userCreateEditMapper;
    private final UserReadMapper userReadMapper;

    /**
     * Регистрирует пользователя
     *
     * @param userDto username и password, введенные пользователем
     * @return Возвращает true при успешной регистрации
     */
    @SneakyThrows
    public UserReadDto registration(UserCreateEditDto userDto) {
            return Optional.of(userDto)
                    .map(userCreateEditMapper::map)
                    .map(userRepository::save)
                    .map(userReadMapper::map)
                    .orElseThrow();
    }

    /**
     * Авторизирует пользователя
     *
     * @param userDto username и password, введенные пользователем
     * @return Возвращает пользователя, если он существует
     */
    @SneakyThrows
    public Optional<UserReadDto> authorization(UserCreateEditDto userDto) {
        return userRepository.findByUsernameAndPassword(userDto)
                .map(userReadMapper::map);
    }

    /**
     * Ищет пользователя по username
     *
     * @param username username пользователя
     * @return Возвращает пользователя, если он существует
     */
    @SneakyThrows
    public Optional<UserReadDto> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userReadMapper::map);
    }

    /**
     * Ищет пользователя по id
     *
     * @param id id пользователя
     * @return Возвращает пользователя по id, если он существует
     */
    @SneakyThrows
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Cookie logout(HttpServletRequest req, HttpServletResponse resp) {
        Cookie jwt = Arrays.stream(req.getCookies())
                .filter(cookie -> cookie.getName().equals("JWT"))
                .findFirst().orElse(null);
        jwt.setMaxAge(0);
        resp.addCookie(jwt);

        return jwt;
    }
}