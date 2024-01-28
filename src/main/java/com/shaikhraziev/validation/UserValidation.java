package com.shaikhraziev.validation;

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
    private final String usernameRegex = "^[a-zA-Z0-9_-]{3,10}$";

    /**
     * Регулярное выражение для валидации password
     */
    private final String passwordRegex = "^(?=.*[a-zA-Z]).{3,10}$";

    /**
     * Валидация username и password
     * @param username      username, введенный пользователем
     * @param password      password, введенный пользователем
     * @return              Возвращает пользователя, если данные валидны
     */
    public Optional<UserCreateEditDto> loginAndPassword(String username, String password) {
        Pattern usernamePattern = Pattern.compile(usernameRegex);
        Pattern passwordPattern = Pattern.compile(passwordRegex);

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
     * Валидация username и password
     * @param maybeAuthorizationUser    username и password, введенные пользователем
     * @return                          Возращает true, если данные валидны, иначе false
     */
    public boolean isValidUser(Optional<UserReadDto> maybeAuthorizationUser) {
        if (maybeAuthorizationUser.isEmpty()) {
            System.out.println("There is no such user!");
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
    public boolean isValidIndications(Optional<Map<LocalDate, Indication>> actualIndications) {
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
    public boolean isValidUploadIndications(Indication indications) {
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
    public boolean isTransmittedMoreActual(Indication actualIndications, Indication transmittedIndications) {
        if (actualIndications == null || (transmittedIndications.getHeating() >= actualIndications.getHeating()
                                          && transmittedIndications.getHotWater() >= actualIndications.getHotWater()
                                          && transmittedIndications.getColdWater() >= actualIndications.getColdWater())) {
            return true;
        }
        System.out.println("Indications should be no less than last!");
        return false;
    }

    /**
     * Проверка, что показания не передавались в этом месяце
     * @param user      Пользователь
     * @return          Возращает true - если показания уже передавались в этом месяце, иначе - false
     */
    public boolean indicationsAlreadyUploaded(User user) {
        boolean indicationsAlreadyUploaded = Optional.ofNullable(user.getDateActualIndications())
                .map(LocalDate::getMonth)
                .filter(month -> month.equals(LocalDate.now().getMonth()))
                .isPresent();

        if (indicationsAlreadyUploaded) {
            System.out.println("Indications can be uploaded once a month!");
            return true;
        }
        return false;
    }

    /**
     * Проверка, что показания в этом месяце подавались
     * @param maybeIndications      Показания
     * @return                      Возращает true - если показания подавались в этом месяуе, иначе false
     */
    public boolean haveMonthlyIndications(Optional<List<Indication>> maybeIndications) {
        if (maybeIndications.isEmpty() || maybeIndications.get().isEmpty()) {
            System.out.println("Indications was not uploaded this month!");
            return false;
        }
        return true;
    }

    /**
     * Проверка, что имеется история подачи показаний
     * @param maybeHistory      История подачи показаний
     * @return                  Возвращает true, если история имеется
     */
    public boolean haveHistory(Optional<Map<LocalDate, Indication>> maybeHistory) {
        if (maybeHistory.isEmpty() || maybeHistory.get().isEmpty()) {
            System.out.println("Показания никогда не передавались!");
            return false;
        }
        return true;
    }
}