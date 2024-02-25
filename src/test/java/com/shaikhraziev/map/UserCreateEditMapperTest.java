package com.shaikhraziev.map;

import com.shaikhraziev.dto.UserCreateEditDto;
import com.shaikhraziev.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.shaikhraziev.entity.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;

class UserCreateEditMapperTest {

    private final UserCreateEditMapper userCreateEditMapper = new UserCreateEditMapper();
    private final UserCreateEditDto USER_DTO = UserCreateEditDto.builder()
            .username("katya")
            .password("123w")
            .build();

    @Test
    @DisplayName("should map UserCreateEditDto to User")
    void map() {
        User actualResult = userCreateEditMapper.map(USER_DTO);
        User expectedResult = new User(null, "katya", "123w", USER);

        assertThat(actualResult).isEqualTo(expectedResult);
    }
}