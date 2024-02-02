package com.shaikhraziev.map;

import org.junit.jupiter.api.Test;

import static com.shaikhraziev.entity.Action.ERROR;
import static com.shaikhraziev.entity.Action.USER_INDICATIONS;
import static org.assertj.core.api.Assertions.assertThat;

class ActionAdminMapperTest {

    private final ActionAdminMapper actionAdminMapper = new ActionAdminMapper();

    @Test
    void shouldMapToUserIndications() {
        var actualResult = actionAdminMapper.map(1);
        var expectedResult = USER_INDICATIONS;

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void shouldMapToError() {
        var actualResult = actionAdminMapper.map(100);
        var expectedResult = ERROR;

        assertThat(actualResult).isEqualTo(expectedResult);
    }
}