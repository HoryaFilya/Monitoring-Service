package com.shaikhraziev.service;

import com.shaikhraziev.repository.AuditRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuditServiceTest {

    @InjectMocks
    private AuditService auditService;

    @Mock
    private AuditRepository auditRepository;

    private static final String USERNAME = "misha";

    @DisplayName("should not throw an exception")
    @SneakyThrows
    @Test
    void registration() {
        assertDoesNotThrow(() -> auditService.registration(USERNAME));
        verify(auditRepository, times(1)).registration(any());
    }

    @DisplayName("should not throw an exception")
    @SneakyThrows
    @Test
    void authorization() {
        assertDoesNotThrow(() -> auditService.authorization(USERNAME));
        verify(auditRepository, times(1)).authentication(any());
    }

    @DisplayName("should not throw an exception")
    @SneakyThrows
    @Test
    void logout() {
        assertDoesNotThrow(() -> auditService.logout(USERNAME));
        verify(auditRepository, times(1)).logout(any());
    }

    @DisplayName("should not throw an exception")
    @SneakyThrows
    @Test
    void getActualIndications() {
        assertDoesNotThrow(() -> auditService.getActualIndications(USERNAME));
        verify(auditRepository, times(1)).getActualIndications(any());
    }

    @DisplayName("should not throw an exception")
    @SneakyThrows
    @Test
    void uploadIndications() {
        assertDoesNotThrow(() -> auditService.uploadIndications("testUser", Month.JANUARY));
        verify(auditRepository, times(1)).uploadIndications(any(), any());
    }

    @DisplayName("should not throw an exception")
    @SneakyThrows
    @Test
    void getMonthlyIndications() {
        assertDoesNotThrow(() -> auditService.getMonthlyIndications("testUser", Month.JANUARY));
        verify(auditRepository, times(1)).getMonthlyIndications(any(), any());
    }

    @DisplayName("should not throw an exception")
    @SneakyThrows
    @Test
    void getHistory() {
        assertDoesNotThrow(() -> auditService.getHistory("testUser"));
        verify(auditRepository, times(1)).getHistory(any());
    }

    @DisplayName("should return empty audit")
    @SneakyThrows
    @Test
    void getUserAudit() {
        when(auditRepository.findAuditsAllUser()).thenReturn(new ArrayList<>());

        List<String> result = auditService.getUserAudit();

        assertEquals(List.of("Пользователи не совершали действий!"), result);
        verify(auditRepository, times(1)).findAuditsAllUser();
    }
}