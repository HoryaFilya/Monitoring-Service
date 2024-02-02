package com.shaikhraziev.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Класс для показаний пользователя
 */
@Data
@AllArgsConstructor
public class Indication {
    /**
     * Значение с счетчика отопления
     */
    private Long heating;

    /**
     * Значение с счетчика горячей воды
     */
    private Long hotWater;

    /**
     * Значение с счетчика холодной воды
     */
    private Long coldWater;
}