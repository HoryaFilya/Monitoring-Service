package com.shaikhraziev.map;

import com.shaikhraziev.dto.UserCreateEditDto;
import com.shaikhraziev.entity.User;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static com.shaikhraziev.entity.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;

class UserCreateEditMapperTest {

    private final UserCreateEditMapper userCreateEditMapper = new UserCreateEditMapper();
    private final UserCreateEditDto USER_DTO = UserCreateEditDto.builder()
            .username("katya")
            .password("123w")
            .build();

    @Test
    void shouldMapUserDtoToUser() {
        var actualResult = userCreateEditMapper.map(USER_DTO);
        var expectedResult = new User(null, "katya", "123w", USER);

        assertThat(actualResult).isEqualTo(expectedResult);
    }
}