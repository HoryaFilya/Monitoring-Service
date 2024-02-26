package com.shaikhraziev.validation;

import com.shaikhraziev.dto.IndicationCreateEditDto;
import com.shaikhraziev.dto.IndicationReadDto;
import com.shaikhraziev.dto.UserCreateEditDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     *
     * @param user username и password, введенные пользователем
     * @return Возращает true, если данные валидны, иначе false
     */
    public boolean isValidLoginAndPassword(UserCreateEditDto user) {
        Matcher usernameMatcher = usernamePattern.matcher(user.getUsername());
        Matcher passwordMatcher = passwordPattern.matcher(user.getPassword());

        return usernameMatcher.matches() && passwordMatcher.matches();
    }

    /**
     * Валидация подаваемых показаний
     *
     * @param transmittedIndications Подаваемые показания
     * @return Возращает true, если показания валидны, иначе false
     */
    @SneakyThrows
    public boolean isValidUploadIndications(IndicationCreateEditDto transmittedIndications, IndicationReadDto actualIndications) {
        boolean isTransmittedMoreActual = isTransmittedMoreActual(actualIndications, transmittedIndications);

        return isTransmittedMoreActual &&
               (transmittedIndications != null && transmittedIndications.getHeating() >= 0
                && transmittedIndications.getHotWater() >= 0 && transmittedIndications.getColdWater() >= 0);
    }

    /**
     * Проверка, что передаваемые показания не меньше последних
     *
     * @param actualIndications      Актуальные показания
     * @param transmittedIndications Передаваемые показания
     * @return Возращает true, если показания валидны, иначе false
     */
    public boolean isTransmittedMoreActual(IndicationReadDto actualIndications, IndicationCreateEditDto transmittedIndications) {
        return actualIndications == null || (transmittedIndications.getHeating() >= actualIndications.getHeating()
                                             && transmittedIndications.getHotWater() >= actualIndications.getHotWater()
                                             && transmittedIndications.getColdWater() >= actualIndications.getColdWater());
    }
}