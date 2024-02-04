package com.shaikhraziev.map;

import org.junit.jupiter.api.Test;

import static com.shaikhraziev.entity.Action.*;
import static com.shaikhraziev.entity.Phase.NO_AUTHENTICATION_USER;
import static org.assertj.core.api.Assertions.assertThat;

class ActionUserMapperTest {

    private final ActionUserMapper actionUserMapper = new ActionUserMapper();

    @Test
    void shouldMapToAuthorization() {
        var actualResult = actionUserMapper.map(2, NO_AUTHENTICATION_USER);
        var expectedResult = AUTHORIZATION;

        assertThat(actualResult).isEqualTo(expectedResult);
    }
}