package com.shaikhraziev.service;

import com.shaikhraziev.dto.UserCreateEditDto;
import com.shaikhraziev.dto.UserReadDto;
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

    private final User TEST_USER = User.builder()
            .id(5L)
            .username("misha")
            .password("123q")
            .role(USER)
            .build();

    private final UserCreateEditDto TEST_USER_CREATE_EDIT_DTO = UserCreateEditDto.builder()
            .username("misha")
            .password("123q")
            .build();

    private final UserReadDto TEST_USER_READ_DTO = UserReadDto.builder()
            .id(TEST_USER.getId())
            .username(TEST_USER.getUsername())
            .role(TEST_USER.getRole())
            .build();

    @Test
    @SneakyThrows
    @DisplayName("should be successful registration user")
    void registration() {
        when(userCreateEditMapper.map(TEST_USER_CREATE_EDIT_DTO)).thenReturn(TEST_USER);
        when(userRepository.save(TEST_USER)).thenReturn(TEST_USER);
        when(userReadMapper.map(TEST_USER)).thenReturn(TEST_USER_READ_DTO);

        UserReadDto actualResult = (userService.registration(TEST_USER_CREATE_EDIT_DTO));

        assertThat(actualResult).isEqualTo(TEST_USER_READ_DTO);

        verify(userRepository).save(TEST_USER);
    }

    @Test
    @SneakyThrows
    @DisplayName("should be authorize user")
    void authorization() {
        when(userRepository.findByUsernameAndPassword(TEST_USER_CREATE_EDIT_DTO)).thenReturn(Optional.of(TEST_USER));
        when(userReadMapper.map(TEST_USER)).thenReturn(TEST_USER_READ_DTO);

        Optional<UserReadDto> actualResult = userService.authorization(TEST_USER_CREATE_EDIT_DTO);

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(TEST_USER_READ_DTO);
    }

    @Test
    @SneakyThrows
    @DisplayName("should be successful search by name")
    void findByUsername() {
        when(userRepository.findByUsername(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));
        when(userReadMapper.map(TEST_USER)).thenReturn(TEST_USER_READ_DTO);

        Optional<UserReadDto> actualResult = userService.findByUsername(TEST_USER.getUsername());

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(TEST_USER_READ_DTO);

        verify(userRepository, times(1)).findByUsername(TEST_USER.getUsername());
        verify(userReadMapper, times(1)).map(TEST_USER);
    }

    @Test
    @SneakyThrows
    @DisplayName("should be successful search by id")
    void findById() {
        when(userRepository.findById(TEST_USER.getId())).thenReturn(Optional.of(TEST_USER));

        Optional<User> actualResult = userService.findById(TEST_USER.getId());

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(TEST_USER);

        verify(userRepository, times(1)).findById(TEST_USER.getId());
    }
}