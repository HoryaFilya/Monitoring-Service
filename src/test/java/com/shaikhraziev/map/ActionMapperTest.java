package com.shaikhraziev.map;

import org.junit.jupiter.api.Test;

import static com.shaikhraziev.entity.Action.*;
import static com.shaikhraziev.entity.Phase.NO_AUTHENTICATION_USER;
import static org.assertj.core.api.Assertions.assertThat;

class ActionMapperTest {

    private final ActionMapper actionMapper = new ActionMapper();

    @Test
    void shouldMapToAuthorization() {
        var actualResult = actionMapper.map(2, NO_AUTHENTICATION_USER);
        var expectedResult = AUTHORIZATION;

        assertThat(actualResult).isEqualTo(expectedResult);
    }
}