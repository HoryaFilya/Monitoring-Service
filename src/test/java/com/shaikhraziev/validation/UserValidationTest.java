package com.shaikhraziev.validation;

import com.shaikhraziev.dto.IndicationCreateEditDto;
import com.shaikhraziev.dto.IndicationReadDto;
import com.shaikhraziev.dto.UserCreateEditDto;
import com.shaikhraziev.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.shaikhraziev.entity.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;

class UserValidationTest {

    private final UserValidation userValidation = new UserValidation();
    private final User TEST_USER = new User(null, "misha", "123q", USER);
    private final UserCreateEditDto USER_CREATE_EDIT_DTO = UserCreateEditDto.builder()
            .username(TEST_USER.getUsername())
            .password(TEST_USER.getPassword())
            .build();
    private final IndicationReadDto ACTUAL_INDICATIONS = IndicationReadDto.builder()
            .date(LocalDate.now())
            .heating(100L)
            .hotWater(200L)
            .coldWater(300L)
            .build();

    private final IndicationCreateEditDto TRANSMITTED_INDICATIONS = IndicationCreateEditDto.builder()
            .date(LocalDate.now())
            .heating(105L)
            .hotWater(205L)
            .coldWater(305L)
            .build();

    @DisplayName("should be valid login and password")
    @Test
    void isValidLoginAndPassword() {
        var actualResult = userValidation.isValidLoginAndPassword(USER_CREATE_EDIT_DTO);

        assertThat(actualResult).isTrue();
    }

    @DisplayName("should be valid indications")
    @Test
    void isValidUploadIndications() {
        var actualResult = userValidation.isValidUploadIndications(TRANSMITTED_INDICATIONS);

        assertThat(actualResult).isTrue();
    }

    @DisplayName("should be transmitted indications more than actual")
    @Test
    void isTransmittedMoreActual() {
        var actualResult = userValidation.isTransmittedMoreActual(ACTUAL_INDICATIONS, TRANSMITTED_INDICATIONS);

        assertThat(actualResult).isTrue();
    }
}