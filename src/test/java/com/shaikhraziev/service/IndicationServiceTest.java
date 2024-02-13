package com.shaikhraziev.service;

import com.shaikhraziev.dto.IndicationReadDto;
import com.shaikhraziev.entity.User;
import com.shaikhraziev.repository.IndicationRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.shaikhraziev.entity.Role.USER;
import static java.time.Month.FEBRUARY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IndicationServiceTest {

    @InjectMocks
    private IndicationService indicationService;
    @Mock
    private IndicationRepository indicationRepository;
    private final User TEST_USER = User.builder()
            .username("misha")
            .password("123q")
            .role(USER)
            .build();

    private final IndicationReadDto INDICATIONS = IndicationReadDto.builder()
            .date(LocalDate.now())
            .heating(100L)
            .hotWater(200L)
            .coldWater(300L)
            .build();

    @Test
    @SneakyThrows
    @DisplayName("should be get actual indications")
    void getActualIndications() {
        when(indicationRepository.getActualIndications(TEST_USER.getId())).thenReturn(Optional.of(INDICATIONS));

        var actualResult = indicationService.getActualIndications(TEST_USER.getId());

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(INDICATIONS);
    }

    @Test
    @SneakyThrows
    @DisplayName("should be get monthly indications")
    void getMonthlyIndications() {
        when(indicationRepository.getMonthlyIndications(TEST_USER.getId(), FEBRUARY)).thenReturn(List.of(INDICATIONS));

        var actualResult = indicationService.getMonthlyIndications(TEST_USER.getId(), FEBRUARY);

        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).isEqualTo(List.of(INDICATIONS));
    }

    @Test
    @SneakyThrows
    @DisplayName("should be get history")
    void getHistory() {
        when(indicationRepository.getHistory(TEST_USER.getId())).thenReturn(List.of(INDICATIONS));

        var actualResult = indicationService.getHistory(TEST_USER.getId());

        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).isEqualTo(List.of(INDICATIONS));
    }
}