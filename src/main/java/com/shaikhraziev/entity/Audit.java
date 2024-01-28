package com.shaikhraziev.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * Класс для аудита пользователей
 */
@Data
@AllArgsConstructor
@Builder
public class Audit {
    /**
     * Дата
     */
    private LocalDate date;

    /**
     * Событие
     */
    private String event;
}