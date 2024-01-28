package com.shaikhraziev.map;

import com.shaikhraziev.dto.UserReadDto;
import com.shaikhraziev.entity.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserReadMapperTest {

    private final UserReadMapper userReadMapper = new UserReadMapper();
    private final User USER = User.builder()
            .username("katya")
            .password("123w")
            .build();
    private final UserReadDto USER_DTO = UserReadDto.builder()
            .username("katya")
            .password("123w")
            .build();

    @Test
    void shouldMapUserToUserDto() {
        var actualResult = userReadMapper.map(USER);
        var expectedResult = USER_DTO;

        assertThat(actualResult).isEqualTo(expectedResult);
    }
}