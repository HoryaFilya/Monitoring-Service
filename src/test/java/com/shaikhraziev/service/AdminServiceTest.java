package com.shaikhraziev.service;

import com.shaikhraziev.dto.UserCreateEditDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private UserService userService;

    private UserCreateEditDto USER_DTO = UserCreateEditDto.builder()
            .username("misha")
            .password("123q")
            .build();

    private UserCreateEditDto ADMIN = UserCreateEditDto.builder()
            .username("admin")
            .password("admin")
            .build();

    @Test
    void testIsAdminAndIsNotAdmin() {
        var actualResult = adminService.isAdmin(USER_DTO);
        var actualResult2 = adminService.isAdmin(ADMIN);

        assertThat(actualResult).isFalse();
        assertThat(actualResult2).isTrue();
    }

    @Test
    void testGetAllIndicationsWithEmptyDatabase() {
        when(userService.getDatabase()).thenReturn(Optional.of(Map.of()));
        assertDoesNotThrow(() -> adminService.getAllIndications());
    }
}