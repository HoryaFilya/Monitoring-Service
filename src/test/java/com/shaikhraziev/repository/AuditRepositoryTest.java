package com.shaikhraziev.repository;

import com.shaikhraziev.UnitTestBase;
import com.shaikhraziev.util.ConnectionManager;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuditRepositoryTest extends UnitTestBase {

    private final ConnectionManager connectionManager = new ConnectionManager(
            container.getJdbcUrl(),
            container.getUsername(),
            container.getPassword()
    );
    private final AuditRepository auditRepository = new AuditRepository(connectionManager);
    private final String TEST_USERNAME = "misha";

    @Test
    @SneakyThrows
    @Order(1)
    @DisplayName("should be record registration audit")
    void shouldRecordRegistrationAudit() {
        auditRepository.registration(TEST_USERNAME);

        var actualResult = auditRepository.findAuditsAllUser().stream()
                .filter(audit -> audit.getEvent().equals("Пользователь %s зарегистрировался.".formatted(TEST_USERNAME)))
                .findFirst();

        assertThat(actualResult).isPresent();
    }

    @Test
    @SneakyThrows
    @Order(2)
    @DisplayName("should be find audit on authorization")
    void authorization() {
        auditRepository.authorization(TEST_USERNAME);

        var actualResult = auditRepository.findAuditsAllUser().stream()
                .filter(audit -> audit.getEvent().equals("Пользователь %s авторизовался.".formatted(TEST_USERNAME)))
                .findFirst();

        assertThat(actualResult).isPresent();
    }

    @Test
    @SneakyThrows
    @Order(3)
    @DisplayName("should be record logout audit")
    void logout() {
        auditRepository.logout(TEST_USERNAME);

        var actualResult = auditRepository.findAuditsAllUser().stream()
                .filter(audit -> audit.getEvent().equals("Пользователь %s вышел из системы.".formatted(TEST_USERNAME)))
                .findFirst();

        assertThat(actualResult).isPresent();
    }

    @Test
    @SneakyThrows
    @Order(4)
    @DisplayName("should be record actual indications audit")
    void getActualIndications() {
        auditRepository.getActualIndications(TEST_USERNAME);

        var actualResult = auditRepository.findAuditsAllUser().stream()
                .filter(audit -> audit.getEvent().equals("Пользователь %s получил актуальные показания.".formatted(TEST_USERNAME)))
                .findFirst();

        assertThat(actualResult).isPresent();
    }

    @Test
    @SneakyThrows
    @Order(5)
    @DisplayName("should be record upload indications audit")
    void uploadIndications() {
        auditRepository.uploadIndications(TEST_USERNAME);

        var actualResult = auditRepository.findAuditsAllUser().stream()
                .filter(audit -> audit.getEvent().equals("Пользователь %s подал показания.".formatted(TEST_USERNAME)))
                .findFirst();

        assertThat(actualResult).isPresent();
    }

    @Test
    @SneakyThrows
    @Order(6)
    @DisplayName("should be record monthly indications audit")
    void getMonthlyIndications() {
        auditRepository.getMonthlyIndications(TEST_USERNAME, LocalDate.now().getMonth());

        var actualResult = auditRepository.findAuditsAllUser().stream()
                .filter(audit -> audit.getEvent().equals("Пользователь %s получил показания за %s.".formatted(TEST_USERNAME, LocalDate.now().getMonth())))
                .findFirst();

        assertThat(actualResult).isPresent();
    }

    @Test
    @SneakyThrows
    @Order(7)
    @DisplayName("should be record history audit")
    void getHistory() {
        auditRepository.getHistory(TEST_USERNAME);

        var actualResult = auditRepository.findAuditsAllUser().stream()
                .filter(audit -> audit.getEvent().equals("Пользователь %s получил историю показаний.".formatted(TEST_USERNAME)))
                .findFirst();

        assertThat(actualResult).isPresent();
    }

    @Test
    @SneakyThrows
    @Order(8)
    @DisplayName("should be get a history of audits in the amount of 7 pieces")
    void findAuditsAllUser() {
        var actualResult = auditRepository.findAuditsAllUser();

        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).hasSize(7);
    }
}