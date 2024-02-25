package com.shaikhraziev.map;

import com.shaikhraziev.dto.IndicationReadDto;
import com.shaikhraziev.dto.UserReadDto;
import com.shaikhraziev.entity.Indication;
import com.shaikhraziev.service.UserService;
import lombok.RequiredArgsConstructor;

import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class IndicationReadMapper implements Mapper<Indication, IndicationReadDto> {

    private final UserService userService;
    private final UserReadMapper userReadMapper;

    @Override
    public IndicationReadDto map(Indication object) {

        UserReadDto userReadDto = userService.findById(object.getUsersId())
                .map(userReadMapper::map).orElse(null);

        String name = null;

        if (userReadDto != null)
            name = userReadDto.getUsername();

        return IndicationReadDto.builder()
                .id(object.getId())
                .username(name)
                .date(object.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .heating(object.getHeating())
                .hotWater(object.getHotWater())
                .coldWater(object.getColdWater())
                .build();
    }
}