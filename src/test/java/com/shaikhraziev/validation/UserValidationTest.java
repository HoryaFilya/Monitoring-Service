package com.shaikhraziev.validation;

import com.shaikhraziev.dto.UserCreateEditDto;
import com.shaikhraziev.dto.UserReadDto;
import com.shaikhraziev.entity.Indication;
import com.shaikhraziev.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.shaikhraziev.entity.Action.EXIT;
import static org.assertj.core.api.Assertions.assertThat;

class UserValidationTest {

    private UserValidation userValidation = new UserValidation();
    private User MISHA = new User(1L, "misha", "123q", new HashMap<>(), null);
    private final Indication INDICATIONS = new Indication(100L, 200L, 300L);
    private final Indication ACTUAL_INDICATIONS = new Indication(100L, 200L, 300L);
    private final Indication TRANSMITTED_INDICATIONS = new Indication(200L, 300L, 400L);
    private final LocalDate DATE = LocalDate.now();
    private UserCreateEditDto USER_CREATE_EDIT_DTO = UserCreateEditDto.builder()
            .username(MISHA.getUsername())
            .password(MISHA.getPassword())
            .build();

    private UserReadDto USER_READ_DTO = UserReadDto.builder()
            .id(1L)
            .username("katya")
            .password("123w")
            .build();

    @Test
    void shouldValidateLoginAndPassword() {
        var actualResult = userValidation.loginAndPassword(MISHA.getUsername(), MISHA.getPassword());
        var expectedResult = USER_CREATE_EDIT_DTO;

        assertThat(actualResult.get()).isEqualTo(expectedResult);
    }

    @Test
    void shouldValidateInput() {
        var actualResult = userValidation.isValidInput(USER_CREATE_EDIT_DTO);

        assertThat(actualResult).isTrue();
    }

    @Test
    void shouldValidateUser() {
        var actualResult = userValidation.isValidUser(Optional.of(USER_READ_DTO));

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
        var actualResult = userValidation.isValidUploadIndications(INDICATIONS);

        assertThat(actualResult).isTrue();
    }

    @Test
    void shouldBeTransmittedMoreThanActual() {
        var actualResult = userValidation.isTransmittedMoreActual(ACTUAL_INDICATIONS, TRANSMITTED_INDICATIONS);

        assertThat(actualResult).isTrue();
    }

    @Test
    void shouldNotHaveUploadedIndications() {
        var actualResult = userValidation.indicationsAlreadyUploaded(MISHA);

        assertThat(actualResult).isFalse();
    }

    @Test
    void shouldNotHaveMonthlyIndications() {
        var actualResult = userValidation.haveMonthlyIndications(Optional.empty());

        assertThat(actualResult).isFalse();
    }

    @Test
    void shouldHaveHistory() {
        var actualResult = userValidation.haveHistory(Optional.of(Map.of(DATE, INDICATIONS)));

        assertThat(actualResult).isTrue();
    }
}