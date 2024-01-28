package com.shaikhraziev.map;

import com.shaikhraziev.entity.Action;

import static com.shaikhraziev.entity.Action.*;
import static com.shaikhraziev.entity.Action.ERROR;

/**
 * Класс для преобразования числа, введенного администратором с консоли, в действие
 */
public class ActionAdminMapper {

    /**
     * Преобразует число, введенное администратором с консоли, в действие
     * @param action        Число, введенное администратором
     * @return              Возвращает действие
     */
    public Action map(Integer action) {
        return switch (action) {
            case 1 -> USER_INDICATIONS;
            case 2 -> USER_AUDIT;
            case 3 -> LOGOUT;
            case 4 -> EXIT;
            default -> ERROR;
        };
    }
}