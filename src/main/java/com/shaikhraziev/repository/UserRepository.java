package com.shaikhraziev.repository;

import com.shaikhraziev.dto.UserCreateEditDto;
import com.shaikhraziev.entity.Admin;
import com.shaikhraziev.entity.Indication;
import com.shaikhraziev.entity.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

/**
 * Репозиторий для работы с информацией пользователя
 */
public class UserRepository {

    /**
     * Хранилище пользователей: String -> username пользователя, User -> пользователь
     */
    private Map<String, User> database = new HashMap<>();

    /**
     * Сохраняет пользователя в хранилище
     * @param user      Пользователь
     * @return          Возращает сохраненного пользователя
     */
    public User save(User user) {
        database.put(user.getUsername(), user);
        return user;
    }

    /**
     * Ищет пользователя по username и password
     * @param userDto   username и password, введенные пользователем
     * @return          Возвращает пользователя, если он существует
     */
    public Optional<User> findByUsernameAndPassword(UserCreateEditDto userDto) {
        return database.values().stream()
                .filter(user -> user.getUsername().equals(userDto.getUsername()) && user.getPassword().equals(userDto.getPassword()))
                .findFirst();
    }

    /**
     * Ищет пользователя по username
     * @param username  username пользователя
     * @return          Возвращает пользователя, если он существует
     */
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(database.get(username));
    }

    /**
     * Ищет актуальные показания по username
     * @param username  username пользователя
     * @return          Возвращает актуальные показания, если их передавали
     */
    public Optional<Map<LocalDate, Indication>> getActualIndications(String username) {
        User user = database.get(username);
        Optional<LocalDate> date = Optional.ofNullable(user.getDateActualIndications());
        if (date.isEmpty())
            return Optional.empty();
        return Optional.of(
                Map.of(user.getDateActualIndications(), user.getDatabaseIndications().get(user.getDateActualIndications()))
        );
    }

    /**
     * Подает показания пользователя
     * @param user                      Пользователь
     * @param transmittedIndications    Переданные показания
     * @param date                      Дата подачи показаний
     */
    public void uploadIndications(User user, Indication transmittedIndications, LocalDate date) {
        user.setDateActualIndications(date);
        user.getDatabaseIndications().put(date, transmittedIndications);
    }

    /**
     * Возращает показания за конкретный месяц
     * @param username      username пользователя
     * @param month         Месяц
     * @return              Возращает показания за конкретный месяц
     */
    public Optional<List<Indication>> getMonthlyIndications(String username, Month month) {
        User user = database.get(username);
        Map<LocalDate, Indication> dateAndIndications = user.getDatabaseIndications();
        return Optional.of(dateAndIndications.entrySet().stream()
                .filter(dateAndIndication -> month.equals(dateAndIndication.getKey().getMonth()))
                .map(Map.Entry::getValue)
                .toList());
    }

    /**
     * Возвращает историю подачи показаний пользователя
     * @param username      username пользователя
     * @return              Возвращает историю подачи показаний пользователя
     */
    public Optional<Map<LocalDate, Indication>> getHistory(String username) {
        User user = database.get(username);
        return Optional.ofNullable(user.getDatabaseIndications());
    }

    /**
     * Возвращает хранилище пользователей
     * @return      Возвращает хранилище пользователей
     */
    public Optional<Map<String, User>> getDatabase() {
        return Optional.ofNullable(database);
    }
}