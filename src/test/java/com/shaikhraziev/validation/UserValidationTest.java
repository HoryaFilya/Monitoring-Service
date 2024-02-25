package com.shaikhraziev.validation;

import com.shaikhraziev.dto.IndicationCreateEditDto;
import com.shaikhraziev.dto.IndicationReadDto;
import com.shaikhraziev.dto.UserCreateEditDto;
import com.shaikhraziev.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.shaikhraziev.entity.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;

class UserValidationTest {

    private final UserValidation userValidation = new UserValidation();
    private final User TEST_USER = new User(5L, "misha", "123q", USER);
    private final UserCreateEditDto TEST_USER_CREATE_EDIT_DTO = UserCreateEditDto.builder()
            .username(TEST_USER.getUsername())
            .password(TEST_USER.getPassword())
            .build();

    private final IndicationCreateEditDto TRANSMITTED_INDICATIONS = IndicationCreateEditDto.builder()
            .heating(105L)
            .hotWater(205L)
            .coldWater(305L)
            .build();

    private final IndicationReadDto ACTUAL_INDICATIONS = IndicationReadDto.builder()
            .id(TEST_USER.getId())
            .username(TEST_USER.getUsername())
            .date(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
            .heating(100L)
            .hotWater(200L)
            .coldWater(300L)
            .build();

    @DisplayName("should be valid login and password")
    @Test
    void isValidLoginAndPassword() {
        boolean actualResult = userValidation.isValidLoginAndPassword(TEST_USER_CREATE_EDIT_DTO);

        assertThat(actualResult).isTrue();
    }

    @DisplayName("should be valid indications")
    @Test
    void isValidUploadIndications() {
        boolean actualResult = userValidation.isValidUploadIndications(TRANSMITTED_INDICATIONS, ACTUAL_INDICATIONS);

        assertThat(actualResult).isTrue();
    }

    @DisplayName("should be transmitted indications more than actual")
    @Test
    void isTransmittedMoreActual() {
        boolean actualResult = userValidation.isTransmittedMoreActual(ACTUAL_INDICATIONS, TRANSMITTED_INDICATIONS);

        assertThat(actualResult).isTrue();
    }
}