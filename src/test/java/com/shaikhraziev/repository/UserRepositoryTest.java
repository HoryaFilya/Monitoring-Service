package com.shaikhraziev.repository;

import com.shaikhraziev.UnitTestBase;
import com.shaikhraziev.dto.IndicationCreateEditDto;
import com.shaikhraziev.dto.IndicationReadDto;
import com.shaikhraziev.dto.UserCreateEditDto;
import com.shaikhraziev.entity.User;
import com.shaikhraziev.util.ConnectionManager;
import com.shaikhraziev.util.PropertiesUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.shaikhraziev.entity.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryTest extends UnitTestBase {

    private final ConnectionManager connectionManager = new ConnectionManager(
            PropertiesUtil.get(container.getJdbcUrl()),
            PropertiesUtil.get("db.username.test"),
            PropertiesUtil.get("db.password.test")
    );

    private final UserRepository userRepository = new UserRepository(connectionManager);

    private final User MISHA = new User(null, "misha", "123q", USER);

    private final UserCreateEditDto USER_DTO = UserCreateEditDto.builder()
            .username("katya")
            .password("123q")
            .build();
    private final IndicationReadDto ACTUAL_INDICATIONS = IndicationReadDto.builder()
            .date(LocalDate.now())
            .heating(100L)
            .hotWater(200L)
            .coldWater(300L)
            .build();

    private final IndicationCreateEditDto TRANSMITTED_INDICATIONS = IndicationCreateEditDto.builder()
            .heating(100L)
            .hotWater(200L)
            .coldWater(300L)
            .build();

    @Test
    @SneakyThrows
    void shouldSaveUser() {
        var actualResult = userRepository.save(MISHA);

        assertThat(actualResult).isTrue();
    }

    @Test
    @SneakyThrows
    void shouldGetUser() {
        var actualResult = userRepository.save(MISHA);

        assertThat(actualResult).isTrue();

        var actualResult2 = userRepository.findByUsername(MISHA.getUsername());

        assertThat(actualResult2).isPresent();
    }

    @Test
    @SneakyThrows
    void shouldNotFindUserByUsernameAndPassword() {
        var actualResult = userRepository.findByUsernameAndPassword(USER_DTO);

        assertThat(actualResult).isEmpty();
    }

    @Test
    @SneakyThrows
    void shouldFindByUsername() {
        userRepository.save(MISHA);
        var actualResult = userRepository.findByUsername(MISHA.getUsername());

        assertThat(actualResult).isPresent();
    }

    @Test
    @SneakyThrows
    void shouldGetActualIndications() {
        userRepository.save(MISHA);
        userRepository.uploadIndications(MISHA.getId(), TRANSMITTED_INDICATIONS);

        var actualResult = userRepository.getActualIndications(MISHA.getId());

        assertThat(actualResult).isPresent();

        var expectedResult = ACTUAL_INDICATIONS;
        actualResult.ifPresent(actual -> assertEquals(actual, expectedResult));
    }

    @Test
    @SneakyThrows
    void shouldUploadIndications() {
        userRepository.save(MISHA);
        userRepository.uploadIndications(MISHA.getId(), TRANSMITTED_INDICATIONS);

        var actualResult = userRepository.getActualIndications(MISHA.getId());
        var expectedResult = ACTUAL_INDICATIONS;

        assertThat(actualResult).isPresent();

        actualResult.ifPresent(actual -> assertEquals(actual, expectedResult));
    }

    @Test
    @SneakyThrows
    void shouldGetMonthlyIndications() {
        userRepository.save(MISHA);
        userRepository.uploadIndications(MISHA.getId(), TRANSMITTED_INDICATIONS);

        var actualResult = userRepository.getMonthlyIndications(MISHA.getId(), LocalDate.now().getMonth());

        assertThat(actualResult).hasSize(1);
    }

    @Test
    @SneakyThrows
    void shouldGetHistory() {
        userRepository.save(MISHA);
        var actualResult = userRepository.getHistory(MISHA.getId());

        assertThat(actualResult).isNotEmpty();

        assertThat(actualResult).isEmpty();
    }

    @Test
    @SneakyThrows
    void shouldNotHaveUploadedIndications() {
        var actualResult = userRepository.indicationsAlreadyUploaded(MISHA.getId(), LocalDate.now().getMonth());

        assertThat(actualResult).isFalse();
    }
}