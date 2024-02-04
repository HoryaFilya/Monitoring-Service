package com.shaikhraziev.validation;

import com.shaikhraziev.dto.IndicationCreateEditDto;
import com.shaikhraziev.dto.IndicationReadDto;
import com.shaikhraziev.dto.UserCreateEditDto;
import com.shaikhraziev.dto.UserReadDto;
import com.shaikhraziev.entity.Action;
import com.shaikhraziev.entity.Indication;
import com.shaikhraziev.entity.User;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.shaikhraziev.entity.Action.ERROR;

/**
 * Класс для валидации введенной информации
 */
@RequiredArgsConstructor
public class UserValidation {

    /**
     * Регулярное выражение для валидации username
     */
    private final Pattern usernamePattern = Pattern.compile("^[a-zA-Z0-9_-]{3,10}$");

    /**
     * Регулярное выражение для валидации password
     */
    private final Pattern passwordPattern = Pattern.compile("^(?=.*[a-zA-Z]).{3,10}$");

    /**
     * Валидация username и password
     * @param username      username, введенный пользователем
     * @param password      password, введенный пользователем
     * @return              Возвращает пользователя, если данные валидны
     */
    public Optional<UserCreateEditDto> loginAndPassword(String username, String password) {
        Matcher usernameMatcher = usernamePattern.matcher(username);
        Matcher passwordMatcher = passwordPattern.matcher(password);

        if (!usernameMatcher.matches() || !passwordMatcher.matches()) {
            return Optional.empty();
        }
        return Optional.of(UserCreateEditDto.builder()
                .username(username)
                .password(password)
                .build());
    }

    /**
     * Валидация username и password
     * @param maybeUser     username и password, введенные пользователем
     * @return              Возращает true, если данные валидны, иначе false
     */
    public boolean isValidInput(UserCreateEditDto maybeUser) {
        Optional<UserCreateEditDto> validUser = loginAndPassword(maybeUser.getUsername(), maybeUser.getPassword());
        if (validUser.isEmpty()) {
            System.out.println("Invalid username or password!");
            return false;
        }
        return true;
    }

    /**
     * Валидация действия
     * @param action        Действие
     * @return              Возращает false, если возникла ошибка, иначе true
     */
    public boolean isError(Action action) {
        if (action.equals(ERROR)) {
            System.out.println("An incorrect number was entered!");
            return true;
        }
        return false;
    }

    /**
     * Валидация актуальных показаний
     * @param actualIndications     Актуальные показания
     * @return                      Возращает true, если показания передавались, иначе false
     */
    public boolean isValidIndications(Optional<Indication> actualIndications) {
        if (actualIndications.isEmpty()) {
            System.out.println("Показания никогда не передавались!");
            return false;
        }
        return true;
    }

    /**
     * Валидация подаваемых показаний
     * @param indications   Подаваемые показания
     * @return              Возращает true, если показания валидны, иначе false
     */
    public boolean isValidUploadIndications(IndicationCreateEditDto indications) {
        if (indications == null || indications.getHeating() < 0
            || indications.getHotWater() < 0 || indications.getColdWater() < 0) {
            System.out.println("Incorrect values!");
            return false;
        }
        return true;
    }

    /**
     * Проверка, что передаваемые показания не меньше последних
     * @param actualIndications         Актуальные показания
     * @param transmittedIndications    Передаваемые показания
     * @return                          Возращает true, если показания валидны, иначе false
     */
    public boolean isTransmittedMoreActual(IndicationReadDto actualIndications, IndicationCreateEditDto transmittedIndications) {
        if (actualIndications == null || (transmittedIndications.getHeating() >= actualIndications.getHeating()
                                          && transmittedIndications.getHotWater() >= actualIndications.getHotWater()
                                          && transmittedIndications.getColdWater() >= actualIndications.getColdWater())) {
            return true;
        }
        System.out.println("Indications should be no less than last!");
        return false;
    }
}