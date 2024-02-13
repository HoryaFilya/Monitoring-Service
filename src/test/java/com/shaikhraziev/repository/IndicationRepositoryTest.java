package com.shaikhraziev.repository;

import com.shaikhraziev.UnitTestBase;
import com.shaikhraziev.dto.IndicationCreateEditDto;
import com.shaikhraziev.entity.Role;
import com.shaikhraziev.entity.User;
import com.shaikhraziev.util.ConnectionManager;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static java.time.Month.FEBRUARY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IndicationRepositoryTest extends UnitTestBase {

    private final ConnectionManager connectionManager = new ConnectionManager(
            container.getJdbcUrl(),
            container.getUsername(),
            container.getPassword()
    );
    private final IndicationRepository indicationRepository = new IndicationRepository(connectionManager);
    private final UserRepository userRepository = new UserRepository(connectionManager);
    private final User TEST_USER = new User(null, "misha", "123q", Role.USER);
    private final Long TEST_USER_ID = 2L;
    private final Long TEST_ADMIN_ID = 1L;
    private final IndicationCreateEditDto TEST_TRANSMITTED_INDICATIONS = IndicationCreateEditDto.builder()
            .date(LocalDate.now())
            .heating(100L)
            .hotWater(200L)
            .coldWater(300L)
            .build();

    private static final String CLEAR_TABLE_AUDIT = "DELETE FROM monitoring.audit WHERE id >= 1";
    private static final String CLEAR_TABLE_INDICATION = "DELETE FROM monitoring.indication WHERE id >= 1";
    private static final String CLEAR_TABLE_USERS = "DELETE FROM monitoring.users WHERE id >= 2";
    private static final String RESTART_SEQUENCE = "ALTER SEQUENCE monitoring.users_seq_id START WITH 2 RESTART";

    @AfterEach
    @SneakyThrows
    void destroy() {
        try (var connection = connectionManager.open();
             var statement = connection.createStatement()) {
            statement.execute(CLEAR_TABLE_AUDIT);
            statement.execute(CLEAR_TABLE_INDICATION);
            statement.execute(CLEAR_TABLE_USERS);
            statement.execute(RESTART_SEQUENCE);
        }
    }

    @Test
    @SneakyThrows
    @DisplayName("should obtain actual indications which were uploaded")
    void getActualIndications() {
        userRepository.save(TEST_USER);

        indicationRepository.uploadIndications(TEST_USER_ID, TEST_TRANSMITTED_INDICATIONS);

        var actualResult = indicationRepository.getActualIndications(TEST_USER_ID);

        assertThat(actualResult).isPresent();

        var expectedResult = TEST_TRANSMITTED_INDICATIONS;

        actualResult.ifPresent(actual -> {
            assertEquals(actual.getDate(), expectedResult.getDate());
            assertEquals(actual.getHeating(), expectedResult.getHeating());
            assertEquals(actual.getHotWater(), expectedResult.getHotWater());
            assertEquals(actual.getColdWater(), expectedResult.getColdWater());
        });
    }

    @Test
    @SneakyThrows
    @DisplayName("should be get monthly indications")
    void getMonthlyIndications() {
        userRepository.save(TEST_USER);

        indicationRepository.uploadIndications(TEST_USER_ID, TEST_TRANSMITTED_INDICATIONS);

        var actualResult = indicationRepository.getMonthlyIndications(TEST_USER_ID, LocalDate.now().getMonth());

        assertThat(actualResult).isNotEmpty();

        assertThat(actualResult).hasSize(1);
    }

    @Test
    @SneakyThrows
    @DisplayName("should get empty history")
    void getHistory() {
        userRepository.save(TEST_USER);

        var actualResult = indicationRepository.getHistory(TEST_USER_ID);

        assertThat(actualResult).isEmpty();
    }

    @Test
    @SneakyThrows
    @DisplayName("should get false because the admin cannot give indications")
    void indicationsAlreadyUploaded() {
        var actualResult = indicationRepository.indicationsAlreadyUploaded(TEST_ADMIN_ID, FEBRUARY);

        assertThat(actualResult).isFalse();
    }
}