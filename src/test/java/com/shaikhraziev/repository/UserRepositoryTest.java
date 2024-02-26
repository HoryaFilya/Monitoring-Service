package com.shaikhraziev.repository;

import com.shaikhraziev.UnitTestBase;
import com.shaikhraziev.dto.UserCreateEditDto;
import com.shaikhraziev.entity.User;
import com.shaikhraziev.util.ConnectionManager;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;

import java.util.Optional;

import static com.shaikhraziev.entity.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;

public class UserRepositoryTest extends UnitTestBase {

    private final ConnectionManager connectionManager = new ConnectionManager(
            container.getJdbcUrl(),
            container.getUsername(),
            container.getPassword()
    );
    private final UserRepository userRepository = new UserRepository(connectionManager);
    private final User TEST_USER = new User(5L, "misha", "123q", USER);
    private final Long TEST_ADMIN_ID = 1L;
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
    @DisplayName("should be get user which was saved")
    void save() {
        User actualResult = userRepository.save(TEST_USER);

        assertThat(actualResult.getUsername()).isEqualTo(TEST_USER.getUsername());
    }

    @Test
    @SneakyThrows
    @DisplayName("should find the registered user")
    void findByUsernameAndPassword() {
        userRepository.save(TEST_USER);

        Optional<User> actualResult = userRepository.findByUsernameAndPassword(UserCreateEditDto.builder()
                .username(TEST_USER.getUsername())
                .password(TEST_USER.getPassword())
                .build());

        assertThat(actualResult).isPresent();

        assertThat(actualResult.get().getPassword()).isEqualTo(TEST_USER.getPassword());
    }

    @Test
    @SneakyThrows
    @DisplayName("should find the registered user")
    void findByUsername() {
        userRepository.save(TEST_USER);

        Optional<User> actualResult = userRepository.findByUsername(TEST_USER.getUsername());

        assertThat(actualResult).isPresent();

        assertThat(actualResult.get().getUsername()).isEqualTo(TEST_USER.getUsername());
    }

    @Test
    @SneakyThrows
    @DisplayName("should get admin by id = 1")
    void findById() {
        Optional<User> actualResult = userRepository.findById(TEST_ADMIN_ID);

        assertThat(actualResult).isPresent();

        assertThat(actualResult.get().getUsername()).isEqualTo("admin");
    }
}