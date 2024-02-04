package com.shaikhraziev.validation;

import com.shaikhraziev.dto.IndicationCreateEditDto;
import com.shaikhraziev.dto.IndicationReadDto;
import com.shaikhraziev.dto.UserCreateEditDto;
import com.shaikhraziev.dto.UserReadDto;
import com.shaikhraziev.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static com.shaikhraziev.entity.Action.EXIT;
import static com.shaikhraziev.entity.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;

class UserValidationTest {

    private final UserValidation userValidation = new UserValidation();
    private final User MISHA = new User(null, "misha", "123q", USER);
    private final UserCreateEditDto USER_CREATE_EDIT_DTO = UserCreateEditDto.builder()
            .username(MISHA.getUsername())
            .password(MISHA.getPassword())
            .build();
    private final IndicationReadDto INDICATIONS_READ = IndicationReadDto.builder()
            .date(LocalDate.now())
            .heating(100L)
            .hotWater(200L)
            .coldWater(300L)
            .build();

    private final IndicationCreateEditDto INDICATIONS_CREATE_EDIT = IndicationCreateEditDto.builder()
            .heating(100L)
            .hotWater(200L)
            .coldWater(300L)
            .build();

    @Test
    void shouldValidateLoginAndPassword() {
        var actualResult = userValidation.loginAndPassword(MISHA.getUsername(), MISHA.getPassword());
        var expectedResult = USER_CREATE_EDIT_DTO;

        assertThat(actualResult).isPresent();

        assertThat(actualResult.get()).isEqualTo(expectedResult);
    }

    @Test
    void shouldValidateInput() {
        var actualResult = userValidation.isValidInput(USER_CREATE_EDIT_DTO);

        assertThat(actualResult).isTrue();
    }

    @Test
    void shouldNotBeError() {
        var actualResult = userValidation.isError(EXIT);

        assertThat(actualResult).isFalse();
    }

    @Test
    void shouldNotBeValidIndications() {
        var actualResult = userValidation.isValidIndications(Optional.empty());

        assertThat(actualResult).isFalse();
    }

    @Test
    void shouldValidateUploadIndications() {
        var actualResult = userValidation.isValidUploadIndications(INDICATIONS_CREATE_EDIT);

        assertThat(actualResult).isTrue();
    }

    @Test
    void shouldBeTransmittedMoreThanActual() {
        var actualResult = userValidation.isTransmittedMoreActual(INDICATIONS_READ, INDICATIONS_CREATE_EDIT);

        assertThat(actualResult).isTrue();
    }
}