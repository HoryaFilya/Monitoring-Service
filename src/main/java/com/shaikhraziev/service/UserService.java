package com.shaikhraziev.service;

import com.shaikhraziev.dto.UserCreateEditDto;
import com.shaikhraziev.dto.UserReadDto;
import com.shaikhraziev.entity.Indication;
import com.shaikhraziev.entity.User;
import com.shaikhraziev.map.UserCreateEditMapper;
import com.shaikhraziev.map.UserReadMapper;
import com.shaikhraziev.repository.UserRepository;
import com.shaikhraziev.validation.UserValidation;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Сервис для работы с пользователем
 */
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserCreateEditMapper userCreateEditMapper;
    private final UserReadMapper userReadMapper;
    private final UserValidation userValidation;
    private final AuditService auditService;

    /**
     * Регистрирует пользователя
     * @param userDto   username и password, введенные пользователем
     * @return          Возвращает результат регистрации
     */
    public boolean registration(UserCreateEditDto userDto) {
        User user = userCreateEditMapper.map(userDto);
        Optional<UserReadDto> maybeUser = findByUsername(user.getUsername());

        if (maybeUser.isEmpty()) {
            userRepository.save(user);
            System.out.println("%s has been successfully registered!".formatted(user.getUsername()));
            auditService.registration(user.getUsername());
            return true;
        }

        System.out.println("User already exists!");
        return false;
    }

    /**
     * Авторизирует пользователя
     * @param userDto       username и password, введенные пользователем
     * @return              Возвращает результат авторизации
     */
    public Optional<UserReadDto> authorization(UserCreateEditDto userDto) {
        return userRepository.findByUsernameAndPassword(userDto)
                .map(userReadMapper::map);
    }

    /**
     * Ищет пользователя по username
     * @param username      username пользователя
     * @return              Возвращает пользователя, если он существует
     */
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
     * Ищет актуальные показания
     * @param username      username пользователя
     * @return              Возвращает актуальные показания пользователя, если они существуют
     */
    public Optional<Map<LocalDate, Indication>> getActualIndications(String username) {
        return userRepository.getActualIndications(username);
    }

    /**
     * Подает показания пользователя
     * @param username                  username пользователя
     * @param transmittedIndications    Переданные показания
     */
    public void uploadIndications(String username, Indication transmittedIndications) {
        User user = userRepository.findByUsername(username).get();
        Indication actualIndications = user.getDatabaseIndications().get(user.getDateActualIndications());

        if (!userValidation.isTransmittedMoreActual(actualIndications, transmittedIndications) ||
            userValidation.indicationsAlreadyUploaded(user)) return;

        userRepository.uploadIndications(user, transmittedIndications, LocalDate.now());
        System.out.println("Показания успешно поданы!");
        auditService.uploadIndications(username);
    }

    /**
     * Ищет показания пользователя за конкретный месяц
     * @param username      username пользователя
     * @param month         Месяц
     * @return              Возвращает показания пользователя за конкретный месяц, если они передавались
     */
    public Optional<List<Indication>> getMonthlyIndications(String username, Month month) {
        return userRepository.getMonthlyIndications(username, month);
    }

    /**
     * Ищет историю подачи показаний пользователя
     * @param username      username пользователя
     * @return              Возвращает историю подачи показаний пользователя, если показания передавались
     */
    public Optional<Map<LocalDate, Indication>> getHistory(String username) {
        return userRepository.getHistory(username);
    }

    /**
     * Ищет хранилище пользователей
     * @return      Возвращает хранилище пользователей, если есть зарегистрированные пользователи
     */
    public Optional<Map<String, User>> getDatabase() {
        return userRepository.getDatabase();
    }
}