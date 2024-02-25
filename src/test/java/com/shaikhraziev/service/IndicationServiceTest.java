package com.shaikhraziev.service;

import com.shaikhraziev.dto.IndicationCreateEditDto;
import com.shaikhraziev.dto.IndicationReadDto;
import com.shaikhraziev.entity.Indication;
import com.shaikhraziev.entity.User;
import com.shaikhraziev.map.IndicationReadMapper;
import com.shaikhraziev.repository.IndicationRepository;
import com.shaikhraziev.validation.UserValidation;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.shaikhraziev.entity.Role.USER;
import static java.time.Month.FEBRUARY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IndicationServiceTest {

    @InjectMocks
    private IndicationService indicationService;

    @Mock
    private IndicationRepository indicationRepository;

    @Mock
    private UserValidation userValidation;

    @Mock
    private IndicationReadMapper indicationReadMapper;

    private final User TEST_USER = User.builder()
            .id(5L)
            .username("misha")
            .password("123q")
            .role(USER)
            .build();

    private final Month CURRENT_MONTH = LocalDate.now().getMonth();

    private final Indication TEST_INDICATIONS = Indication.builder()
            .id(TEST_USER.getId())
            .date(LocalDate.now())
            .heating(100L)
            .hotWater(200L)
            .coldWater(300L)
            .usersId(TEST_USER.getId())
            .build();

    private final IndicationReadDto TEST_INDICATIONS_READ_DTO = IndicationReadDto.builder()
            .id(TEST_USER.getId())
            .username(TEST_USER.getUsername())
            .date(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
            .heating(100L)
            .hotWater(200L)
            .coldWater(300L)
            .build();

    private final IndicationCreateEditDto TEST_INDICATIONS_CREATE_EDIT_DTO = IndicationCreateEditDto.builder()
            .heating(200L)
            .hotWater(300L)
            .coldWater(400L)
            .build();

    @Test
    @SneakyThrows
    @DisplayName("should be get actual indications")
    void getActualIndications() {
        when(indicationRepository.getActualIndications(TEST_USER.getId())).thenReturn(Optional.of(TEST_INDICATIONS));
        when(indicationReadMapper.map(TEST_INDICATIONS)).thenReturn(TEST_INDICATIONS_READ_DTO);

        Optional<IndicationReadDto> actualResult = indicationService.getActualIndications(TEST_USER.getId());

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(TEST_INDICATIONS_READ_DTO);
    }

    @Test
    @SneakyThrows
    @DisplayName("should be get monthly indications")
    void getMonthlyIndications() {
        when(indicationRepository.getMonthlyIndications(TEST_USER.getId(), CURRENT_MONTH)).thenReturn(List.of(TEST_INDICATIONS));
        when(indicationReadMapper.map(TEST_INDICATIONS)).thenReturn(TEST_INDICATIONS_READ_DTO);

        List<IndicationReadDto> actualResult = indicationService.getMonthlyIndications(TEST_USER.getId(), FEBRUARY);

        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).isEqualTo(List.of(TEST_INDICATIONS_READ_DTO));
    }

    @Test
    @SneakyThrows
    @DisplayName("should be get history")
    void getHistory() {
        when(indicationRepository.getHistory(TEST_USER.getId())).thenReturn(List.of(TEST_INDICATIONS));
        when(indicationReadMapper.map(TEST_INDICATIONS)).thenReturn(TEST_INDICATIONS_READ_DTO);

        List<IndicationReadDto> actualResult = indicationService.getHistory(TEST_USER.getId());

        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).isEqualTo(List.of(TEST_INDICATIONS_READ_DTO));
    }

    @Test
    @SneakyThrows
    @DisplayName("method must be called 1 time")
    void uploadIndications() {
        when(userValidation.isTransmittedMoreActual(TEST_INDICATIONS_READ_DTO, TEST_INDICATIONS_CREATE_EDIT_DTO)).thenReturn(true);
        when(indicationRepository.indicationsAlreadyUploaded(TEST_USER.getId(), CURRENT_MONTH)).thenReturn(false);

        indicationService.uploadIndications(TEST_USER.getId(), TEST_INDICATIONS_CREATE_EDIT_DTO, TEST_INDICATIONS_READ_DTO);

        verify(indicationRepository, times(1)).uploadIndications(TEST_USER.getId(), TEST_INDICATIONS_CREATE_EDIT_DTO);
    }

    @Test
    @SneakyThrows
    @DisplayName("should return true, because the indications have already been transmitted")
    void indicationsAlreadyUploaded() {
        when(indicationRepository.indicationsAlreadyUploaded(TEST_USER.getId(), CURRENT_MONTH)).thenReturn(true);

        boolean actualResult = indicationService.indicationsAlreadyUploaded(TEST_USER.getId(), CURRENT_MONTH);

        assertThat(actualResult).isTrue();
    }

    @Test
    @SneakyThrows
    @DisplayName("should return an empty list, because the history is empty")
    void getHistoryAllUsers() {
        List<Indication> history = List.of(TEST_INDICATIONS);
        when(indicationRepository.getHistory()).thenReturn(history);
        when(indicationReadMapper.map(TEST_INDICATIONS)).thenReturn(TEST_INDICATIONS_READ_DTO);

        List<IndicationReadDto> actualResult = indicationService.getHistoryAllUsers();

        assertThat(actualResult).hasSize(history.size());
    }
}