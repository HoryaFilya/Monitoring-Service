package com.shaikhraziev.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * Класс для показаний пользователя
 */
@Data
@AllArgsConstructor
@Builder
public class Indication {

    /**
     * Идентификатор показаний
     */
    private Long id;

    /**
     * Дата подачи показаний
     */
    private LocalDate date;

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

    /**
     * id пользователя
     */
    private Long usersId;
}