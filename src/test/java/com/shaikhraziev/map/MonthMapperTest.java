package com.shaikhraziev.map;

import org.junit.jupiter.api.Test;

import static java.time.Month.MAY;
import static org.assertj.core.api.Assertions.assertThat;

class MonthMapperTest {

    private MonthMapper monthMapper = new MonthMapper();

    @Test
    void shouldMapToMay() {
        var actualResult = monthMapper.map("5");
        var expectedResult = MAY;

        assertThat(actualResult).isEqualTo(expectedResult);
    }
}