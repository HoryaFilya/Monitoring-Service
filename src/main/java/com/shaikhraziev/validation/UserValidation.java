package com.shaikhraziev.validation;

import com.shaikhraziev.dto.IndicationCreateEditDto;
import com.shaikhraziev.dto.IndicationReadDto;
import com.shaikhraziev.dto.UserCreateEditDto;
import lombok.RequiredArgsConstructor;

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
     * @param user          username и password, введенные пользователем
     * @return              Возращает true, если данные валидны, иначе false
     */
    public boolean isValidLoginAndPassword(UserCreateEditDto user) {
        Matcher usernameMatcher = usernamePattern.matcher(user.getUsername());
        Matcher passwordMatcher = passwordPattern.matcher(user.getPassword());

        if (!usernameMatcher.matches() || !passwordMatcher.matches()) {
            System.out.println("Invalid username or password!");
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