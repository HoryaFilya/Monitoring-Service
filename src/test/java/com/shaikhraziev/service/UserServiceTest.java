package com.shaikhraziev.service;

import com.shaikhraziev.dto.UserCreateEditDto;
import com.shaikhraziev.dto.UserReadDto;
import com.shaikhraziev.entity.Indication;
import com.shaikhraziev.entity.User;
import com.shaikhraziev.map.UserCreateEditMapper;
import com.shaikhraziev.map.UserReadMapper;
import com.shaikhraziev.repository.UserRepository;
import com.shaikhraziev.validation.UserValidation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static java.time.Month.JANUARY;
import static org.assertj.core.api.Assertions.assertThat;
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
    private AuditService auditService;

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
            .databaseIndications(new HashMap<>())
            .build();

    private final Indication INDICATIONS = new Indication(100L, 200L, 300L);
    private final LocalDate DATE = LocalDate.now();

    @Test
    void shouldRegisterUser() {
        when(userCreateEditMapper.map(USER_DTO)).thenReturn(MISHA);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        var actualResult = (userService.registration(USER_DTO));

        assertThat(actualResult).isTrue();

        verify(userRepository).save(MISHA);
        verify(auditService).registration(MISHA.getUsername());
    }

    @Test
    void shouldAuthorizeUser() {
        when(userRepository.findByUsernameAndPassword(USER_DTO)).thenReturn(Optional.of(MISHA));
        when(userReadMapper.map(MISHA)).thenReturn(USER_READ_DTO);

        var actualResult = userService.authorization(USER_DTO);

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(USER_READ_DTO);
    }

    @Test
    void shouldGetActualIndications() {
        Map<LocalDate, Indication> indications = new HashMap<>();
        indications.put(DATE, INDICATIONS);

        when(userRepository.getActualIndications("misha")).thenReturn(Optional.of(indications));

        var actualResult = userService.getActualIndications("misha");

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(indications);
    }

    @Test
    void shouldGetMonthlyIndications() {
        when(userRepository.getMonthlyIndications("misha", JANUARY)).thenReturn(Optional.of(List.of(INDICATIONS)));

        var actualResult = userService.getMonthlyIndications("misha", JANUARY);

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(List.of(INDICATIONS));
    }

    @Test
    void shouldGetHistory() {
        Map<LocalDate, Indication> history = Map.of(DATE, INDICATIONS);

        when(userRepository.getHistory("misha")).thenReturn(Optional.of(history));

        var actualResult = userService.getHistory("misha");

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(history);
    }

    @Test
    void shouldGetDatabase() {
        Map<String, User> database = Map.of("misha", MISHA);

        when(userRepository.getDatabase()).thenReturn(Optional.of(database));

        var actualResult = userService.getDatabase();

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(database);
    }
}