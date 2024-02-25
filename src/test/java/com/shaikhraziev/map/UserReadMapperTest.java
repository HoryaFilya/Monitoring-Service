package com.shaikhraziev.map;

import com.shaikhraziev.dto.UserReadDto;
import com.shaikhraziev.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.shaikhraziev.entity.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;

class UserReadMapperTest {

    private final UserReadMapper userReadMapper = new UserReadMapper();
    private final User TEST_USER = User.builder()
            .id(5L)
            .username("katya")
            .password("123w")
            .role(USER)
            .build();
    private final UserReadDto TEST_USER_DTO = UserReadDto.builder()
            .id(5L)
            .username("katya")
            .role(USER)
            .build();

    @Test
    @DisplayName("should map User to UserReadDto")
    void map() {
        UserReadDto actualResult = userReadMapper.map(TEST_USER);
        UserReadDto expectedResult = TEST_USER_DTO;

        assertThat(actualResult).isEqualTo(expectedResult);
    }
}