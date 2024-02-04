package com.shaikhraziev.repository;

import com.shaikhraziev.entity.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static com.shaikhraziev.entity.Role.USER;
import static java.time.Month.JANUARY;
import static org.assertj.core.api.Assertions.assertThat;

//class AuditRepositoryTest {
//
//    private final AuditRepository auditRepository = new AuditRepository();
//    private final User MISHA = new User(1L, "misha", "123q", USER);
//
//    @Test
//    @SneakyThrows
//    void shouldRecordRegistrationAudit() {
//        auditRepository.registration(MISHA.getUsername());
//
//        var actualResult = auditRepository.getAudits();
//
//        assertThat(actualResult).hasSize(1);
//    }
//
//    @Test
//    void shouldNotFindAuditsOnAuthorization() {
//        var actualResult = auditRepository.getAudits();
//
//        assertThat(actualResult).isEmpty();
//    }
//
//    @Test
//    @SneakyThrows
//    void shouldRecordLogoutAudit() {
//        auditRepository.logout(MISHA.getUsername());
//
//        var actualResult = auditRepository.getAudits();
//
//        assertThat(actualResult).hasSize(1);
//    }
//
//    @Test
//    @SneakyThrows
//    void shouldRecordActualIndicationsAudit() {
//        auditRepository.getActualIndications(MISHA.getUsername());
//
//        var actualResult = auditRepository.getAudits();
//
//        assertThat(actualResult).hasSize(1);
//    }
//
//    @Test
//    @SneakyThrows
//    void shouldRecordUploadIndicationsAudit() {
//        auditRepository.uploadIndications(MISHA.getUsername());
//
//        var actualResult = auditRepository.getAudits();
//
//        assertThat(actualResult).hasSize(1);
//    }
//
//    @Test
//    @SneakyThrows
//    void shouldRecordMonthlyIndicationsAudit() {
//        auditRepository.getMonthlyIndications(MISHA.getUsername(), JANUARY);
//
//        var actualResult = auditRepository.getAudits();
//
//        assertThat(actualResult).hasSize(1);
//    }
//
//    @Test
//    @SneakyThrows
//    void shouldRecordHistoryAudit() {
//        auditRepository.getHistory(MISHA.getUsername());
//
//        var actualResult = auditRepository.getAudits();
//
//        assertThat(actualResult).hasSize(1);
//    }
//}