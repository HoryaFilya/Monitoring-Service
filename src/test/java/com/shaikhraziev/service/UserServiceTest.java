package com.shaikhraziev.service;

import com.shaikhraziev.dto.UserCreateEditDto;
import com.shaikhraziev.dto.UserReadDto;
import com.shaikhraziev.dto.UserReadDtoWithoutPassword;
import com.shaikhraziev.entity.User;
import com.shaikhraziev.map.UserCreateEditMapper;
import com.shaikhraziev.map.UserReadMapper;
import com.shaikhraziev.repository.UserRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static com.shaikhraziev.entity.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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

    private final UserCreateEditDto USER_CREATE_EDIT_DTO = UserCreateEditDto.builder()
            .username("misha")
            .password("123q")
            .build();

    private final UserReadDto USER_READ_DTO = UserReadDto.builder()
            .username("misha")
            .password("123q")
            .build();

    private final UserReadDtoWithoutPassword USER_READ_DTO_WITHOUT_PASSWORD = UserReadDtoWithoutPassword.builder()
            .username("misha")
            .build();

    private final User TEST_USER = User.builder()
            .username("misha")
            .password("123q")
            .role(USER)
            .build();

    private final String TEST_USERNAME = "misha";

    @Test
    @SneakyThrows
    @DisplayName("should be successful registration user")
    void registration() {
        when(userCreateEditMapper.map(USER_CREATE_EDIT_DTO)).thenReturn(TEST_USER);
        when(userRepository.findByUsername(TEST_USER.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(TEST_USER)).thenReturn(true);

        var actualResult = (userService.registration(USER_CREATE_EDIT_DTO));

        assertThat(actualResult).isTrue();

        verify(userRepository).save(TEST_USER);
        verify(auditService).registration(TEST_USER.getUsername());
    }

    @Test
    @SneakyThrows
    @DisplayName("should be authorize user")
    void authorization() {
        when(userRepository.findByUsernameAndPassword(USER_CREATE_EDIT_DTO)).thenReturn(Optional.of(TEST_USER));
        when(userReadMapper.map(TEST_USER)).thenReturn(USER_READ_DTO);

        var actualResult = userService.authorization(USER_CREATE_EDIT_DTO);

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(USER_READ_DTO);
    }

    @Test
    @SneakyThrows
    @DisplayName("should be successful search by name")
    void findByUsername() {
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(TEST_USER));
        when(userReadMapper.map(TEST_USER)).thenReturn(USER_READ_DTO);

        var actualResult = userService.findByUsername(TEST_USERNAME);

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(USER_READ_DTO);

        verify(userRepository, times(1)).findByUsername(TEST_USERNAME);
        verify(userReadMapper, times(1)).map(TEST_USER);
    }

    @Test
    @SneakyThrows
    @DisplayName("should be successful search by id")
    void findById() {
        when(userRepository.findById(TEST_USER.getId())).thenReturn(Optional.of(USER_READ_DTO_WITHOUT_PASSWORD));

        var actualResult = userService.findById(TEST_USER.getId());

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(USER_READ_DTO_WITHOUT_PASSWORD);

        verify(userRepository, times(1)).findById(TEST_USER.getId());
    }
}