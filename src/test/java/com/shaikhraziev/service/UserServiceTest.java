package com.shaikhraziev.service;

import com.shaikhraziev.dto.IndicationReadDto;
import com.shaikhraziev.dto.UserCreateEditDto;
import com.shaikhraziev.dto.UserReadDto;
import com.shaikhraziev.entity.Role;
import com.shaikhraziev.entity.User;
import com.shaikhraziev.map.UserCreateEditMapper;
import com.shaikhraziev.map.UserReadMapper;
import com.shaikhraziev.repository.AuditRepository;
import com.shaikhraziev.repository.UserRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static java.time.Month.JANUARY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserCreateEditMapper userCreateEditMapper;
    @Mock
    private UserReadMapper userReadMapper;
    @Mock
    private AuditRepository auditRepository;

    private final UserCreateEditDto USER_DTO = UserCreateEditDto.builder()
            .username("misha")
            .password("123q")
            .build();

    private final UserReadDto USER_READ_DTO = UserReadDto.builder()
            .id(1L)
            .username("misha")
            .password("123q")
            .build();

    private final User MISHA = User.builder()
            .id(1L)
            .username("misha")
            .password("123q")
            .build();

    private final IndicationReadDto INDICATIONS = IndicationReadDto.builder()
            .date(LocalDate.now())
            .heating(100L)
            .hotWater(200L)
            .coldWater(300L)
            .build();

    @Test
    @SneakyThrows
    void shouldRegisterUser() {
        when(userCreateEditMapper.map(USER_DTO)).thenReturn(MISHA);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        var actualResult = (userService.registration(USER_DTO));

        assertThat(actualResult).isTrue();

        verify(userRepository).save(MISHA);
        verify(auditRepository).registration(MISHA.getUsername());
    }

    @Test
    @SneakyThrows
    void shouldAuthorizeUser() {
        when(userRepository.findByUsernameAndPassword(USER_DTO)).thenReturn(Optional.of(MISHA));
        when(userReadMapper.map(MISHA)).thenReturn(USER_READ_DTO);

        var actualResult = userService.authorization(USER_DTO);

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(USER_READ_DTO);
    }

    @Test
    @SneakyThrows
    void shouldGetActualIndications() {
        when(userRepository.getActualIndications(MISHA.getId())).thenReturn(Optional.of(INDICATIONS));

        var actualResult = userService.getActualIndications(MISHA.getId());

        assertThat(actualResult).isPresent();

        actualResult.ifPresent(actual -> assertEquals(actual, INDICATIONS));
        assertThat(actualResult.get()).isEqualTo(INDICATIONS);
    }

    @Test
    @SneakyThrows
    void shouldGetMonthlyIndications() {
        when(userRepository.getMonthlyIndications(MISHA.getId(), JANUARY)).thenReturn(List.of(INDICATIONS));

        var actualResult = userService.getMonthlyIndications(MISHA.getId(), JANUARY);

        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).isEqualTo(List.of(INDICATIONS));
    }

    @Test
    @SneakyThrows
    void shouldGetHistory() {
        List<IndicationReadDto> history = List.of(INDICATIONS);

        when(userRepository.getHistory(MISHA.getId())).thenReturn(history);

        var actualResult = userService.getHistory(MISHA.getId());

        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).isEqualTo(history);
    }
}