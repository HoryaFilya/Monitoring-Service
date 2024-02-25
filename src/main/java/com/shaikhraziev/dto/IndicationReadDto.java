package com.shaikhraziev.dto;

import lombok.Builder;
import lombok.Value;

/**
 * Хранит показания, введенные пользователем, для чтения
 */
@Value
@Builder
public class IndicationReadDto {

    /**
     * Идентификатор показаний
     */
    Long id;

    /**
     * Имя пользователя
     */
    String username;

    /**
     * Дата подачи показаний
     */
    String date;

    /**
     * Значение с счетчика отопления
     */
    Long heating;

    /**
     * Значение с счетчика горячей воды
     */
    Long hotWater;

    /**
     * Значение с счетчика холодной воды
     */
    Long coldWater;
}