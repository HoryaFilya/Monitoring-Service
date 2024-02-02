package com.shaikhraziev.map;

import java.time.Month;

import static java.time.Month.*;

/**
 * Класс для преобразования числа, введенного пользователем с консоли, в месяц
 */
public class MonthMapper {

    /**
     * Преобразует число, введенное пользователем с консоли, в месяц
     * @param month         Число, введенное пользователем
     * @return              Возвращает месяц
     */
    public Month map (String month) {
        return switch (month) {
            case "1" -> JANUARY;
            case "2" -> FEBRUARY;
            case "3" -> MARCH;
            case "4" -> APRIL;
            case "5" -> MAY;
            case "6" -> JUNE;
            case "7" -> JULY;
            case "8" -> AUGUST;
            case "9" -> SEPTEMBER;
            case "10" -> OCTOBER;
            case "11" -> NOVEMBER;
            case "12" -> DECEMBER;
            default -> null;
        };
    }
}