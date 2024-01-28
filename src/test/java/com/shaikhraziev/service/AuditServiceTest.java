package com.shaikhraziev.service;

import com.shaikhraziev.entity.User;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static java.time.Month.JANUARY;
import static org.assertj.core.api.Assertions.assertThat;

class AuditServiceTest {

    private final AuditService auditService = new AuditService();
    private final User MISHA = new User(1L, "misha", "123q", new HashMap<>(), null);

    @Test
    void shouldRecordRegistrationAudit() {
        auditService.registration(MISHA.getUsername());

        var actualResult = auditService.getAudits();

        assertThat(actualResult).hasSize(1);
    }

    @Test
    void shouldNotFindAuditsOnAuthorization() {
        var actualResult = auditService.getAudits();

        assertThat(actualResult).isEmpty();
    }

    @Test
    void shouldRecordLogoutAudit() {
        auditService.logout(MISHA.getUsername());

        var actualResult = auditService.getAudits();

        assertThat(actualResult).hasSize(1);
    }

    @Test
    void shouldRecordActualIndicationsAudit() {
        auditService.getActualIndications(MISHA.getUsername());

        var actualResult = auditService.getAudits();

        assertThat(actualResult).hasSize(1);
    }

    @Test
    void shouldRecordUploadIndicationsAudit() {
        auditService.uploadIndications(MISHA.getUsername());

        var actualResult = auditService.getAudits();

        assertThat(actualResult).hasSize(1);
    }

    @Test
    void shouldRecordMonthlyIndicationsAudit() {
        auditService.getMonthlyIndications(MISHA.getUsername(), JANUARY);

        var actualResult = auditService.getAudits();

        assertThat(actualResult).hasSize(1);
    }

    @Test
    void shouldRecordHistoryAudit() {
        auditService.getHistory(MISHA.getUsername());

        var actualResult = auditService.getAudits();

        assertThat(actualResult).hasSize(1);
    }
}