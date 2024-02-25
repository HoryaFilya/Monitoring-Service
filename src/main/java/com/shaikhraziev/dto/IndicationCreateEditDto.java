package com.shaikhraziev.dto;

import lombok.*;

/**
 * Хранит показания, введенные пользователем
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IndicationCreateEditDto {

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