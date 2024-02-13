package com.shaikhraziev.service;

import com.shaikhraziev.dto.*;
import com.shaikhraziev.entity.*;
import com.shaikhraziev.map.UserCreateEditMapper;
import com.shaikhraziev.map.UserReadMapper;
import com.shaikhraziev.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.*;

/**
 * Сервис для работы с пользователем
 */
@RequiredArgsConstructor
public class UserService {

    private final AuditService auditService;
    private final UserRepository userRepository;
    private final UserCreateEditMapper userCreateEditMapper;
    private final UserReadMapper userReadMapper;

    /**
     * Регистрирует пользователя
     *
     * @param userDto   username и password, введенные пользователем
     * @return          Возвращает true при успешной регистрации
     */

    @SneakyThrows
    public boolean registration(UserCreateEditDto userDto) {
        User user = userCreateEditMapper.map(userDto);
        Optional<UserReadDto> maybeUser = findByUsername(user.getUsername());

        if (maybeUser.isPresent()) {
            System.out.println("User already exists!");
            return false;
        }

        System.out.println("%s registered successfully!".formatted(user.getUsername()));
        auditService.registration(user.getUsername());
        return userRepository.save(user);
    }

    /**
     * Авторизирует пользователя
     *
     * @param userDto   username и password, введенные пользователем
     * @return          Возвращает пользователя, если он существует
     */
    @SneakyThrows
    public Optional<UserReadDto> authorization(UserCreateEditDto userDto) {
        return userRepository.findByUsernameAndPassword(userDto)
                .map(userReadMapper::map);
    }

    /**
     * Ищет пользователя по username
     *
     * @param username  username пользователя
     * @return          Возвращает пользователя, если он существует
     */
    @SneakyThrows
    public Optional<UserReadDto> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userReadMapper::map);
    }

    /**
     * Останавливает приложение
     */
    public void exit() {
        System.exit(0);
    }

    /**
     * Ищет пользователя по id
     * @param id    id пользователя
     * @return      Возвращает пользователя по id, если он существует
     */
    @SneakyThrows
    public Optional<UserReadDtoWithoutPassword> findById(Long id) {
        return userRepository.findById(id);
    }
}