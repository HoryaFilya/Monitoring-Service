package com.shaikhraziev.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * Хранит показания, введенные пользователем
 */
@Value
@Builder
public class IndicationCreateEditDto {

    /**
     * Дата подачи показаний
     */
    LocalDate date;

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