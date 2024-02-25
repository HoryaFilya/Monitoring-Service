package com.shaikhraziev.map;

import com.shaikhraziev.dto.IndicationReadDto;
import com.shaikhraziev.dto.UserReadDto;
import com.shaikhraziev.entity.Indication;
import com.shaikhraziev.entity.User;
import com.shaikhraziev.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static com.shaikhraziev.entity.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndicationReadMapperTest {

    @InjectMocks
    private IndicationReadMapper indicationReadMapper;

    @Mock
    private UserService userService;

    @Mock
    private UserReadMapper userReadMapper;

    private static final Indication TEST_INDICATION = Indication.builder()
            .id(1L)
            .date(LocalDate.now())
            .heating(10L)
            .hotWater(15L)
            .coldWater(12L)
            .usersId(3L)
            .build();

    private static final User TEST_USER = User.builder()
            .id(3L)
            .username("misha")
            .password("123q")
            .role(USER)
            .build();

    private static final UserReadDto TEST_USER_READ_DTO = UserReadDto.builder()
            .id(3L)
            .username("misha")
            .role(USER)
            .build();

    @Test
    @DisplayName("should map Indication to IndicationReadDto")
    void map() {
        when(userService.findById(TEST_INDICATION.getUsersId())).thenReturn(Optional.of(TEST_USER));
        when(userReadMapper.map(TEST_USER)).thenReturn(TEST_USER_READ_DTO);

        IndicationReadDto actualResult = indicationReadMapper.map(TEST_INDICATION);
        IndicationReadDto expectedResult = IndicationReadDto.builder()
                .id(1L)
                .username("misha")
                .date(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .heating(10L)
                .hotWater(15L)
                .coldWater(12L)
                .build();

        assertThat(actualResult).isEqualTo(expectedResult);
    }
}