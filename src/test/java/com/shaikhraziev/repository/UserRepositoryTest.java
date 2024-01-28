package com.shaikhraziev.repository;

import com.shaikhraziev.dto.UserCreateEditDto;
import com.shaikhraziev.entity.Indication;
import com.shaikhraziev.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    private final UserRepository userRepository = new UserRepository();

    private final User MISHA = new User(1L, "misha", "123q", new HashMap<>(), null);
    private final Indication INDICATIONS = new Indication(100L, 200L, 300L);
    private final LocalDate DATE = LocalDate.now();
    private final UserCreateEditDto USER_DTO = UserCreateEditDto.builder()
            .username("katya")
            .password("123q")
            .build();

    @Test
    void shouldSaveUser() {
        var actualResult = userRepository.save(MISHA);
        var actualDatabase = userRepository.getDatabase().get();

        var expectedResult = MISHA;

        assertThat(actualResult).isEqualTo(expectedResult);
        assertThat(actualDatabase).hasSize(1);
    }

    @Test
    void shouldNotFindUserByUsernameAndPassword() {
        var actualResult = userRepository.findByUsernameAndPassword(USER_DTO);

        assertThat(actualResult).isEmpty();
    }

    @Test
    void shouldFindByUsername() {
        userRepository.save(MISHA);
        var actualResult = userRepository.findByUsername(MISHA.getUsername());

        assertThat(actualResult).isPresent();
    }

    @Test
    void shouldGetActualIndications() {
        userRepository.save(MISHA);
        userRepository.uploadIndications(MISHA, INDICATIONS, DATE);

        var actualResult = userRepository.getActualIndications(MISHA.getUsername());

        assertThat(actualResult).isPresent();

        var expectedResult = INDICATIONS;
        actualResult.ifPresent(actual -> assertEquals(actual.get(DATE), expectedResult));
    }

    @Test
    void shouldUploadIndications() {
        userRepository.save(MISHA);
        userRepository.uploadIndications(MISHA, INDICATIONS, DATE);

        var actualResult = userRepository.getActualIndications(MISHA.getUsername()).get();
        var expectedResult = INDICATIONS;

        assertThat(actualResult).hasSize(1);
        assertThat(actualResult.get(DATE)).isEqualTo(expectedResult);
    }

    @Test
    void shouldGetMonthlyIndications() {
        userRepository.save(MISHA);
        userRepository.uploadIndications(MISHA, INDICATIONS, DATE);

        var actualResult = userRepository.getMonthlyIndications(MISHA.getUsername(), LocalDate.now().getMonth());

        assertThat(actualResult.get()).hasSize(1);
    }

    @Test
    void shouldGetHistory() {
        userRepository.save(MISHA);
        var actualResult = userRepository.getHistory(MISHA.getUsername());

        assertThat(actualResult.get()).isEmpty();
    }

    @Test
    void shouldGetDatabase() {
        var actualResult = userRepository.getDatabase().get();

        assertThat(actualResult).isEmpty();

        userRepository.save(MISHA);

        assertThat(actualResult).isNotEmpty();
    }
}