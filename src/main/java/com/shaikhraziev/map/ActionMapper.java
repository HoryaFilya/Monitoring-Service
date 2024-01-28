package com.shaikhraziev.map;

import com.shaikhraziev.entity.Action;
import com.shaikhraziev.entity.Phase;

import static com.shaikhraziev.entity.Action.*;
import static com.shaikhraziev.entity.Action.AUTHORIZATION;
import static com.shaikhraziev.entity.Action.REGISTRATION;
import static com.shaikhraziev.entity.Phase.NO_AUTHENTICATION_USER;

/**
 * Класс для преобразования числа, введенного пользователем с консоли, в действие
 */
public class ActionMapper {

    /**
     * Преобразует число, введенное пользователем с консоли, в действие (в зависимости от стадии, на которой находится пользователь)
     * @param action        Число, введенное пользователем
     * @return              Возвращает действие
     */
    public Action map(Integer action, Phase phase) {
        if (phase == NO_AUTHENTICATION_USER) {
            return switch (action) {
                case 1 -> REGISTRATION;
                case 2 -> AUTHORIZATION;
                case 3 -> EXIT;
                default -> ERROR;
            };
        }

        return switch (action) {
            case 1 -> ACTUAL_INDICATIONS;
            case 2 -> UPLOAD_INDICATIONS;
            case 3 -> MONTHLY_INDICATIONS;
            case 4 -> HISTORY;
            case 5 -> LOGOUT;
            case 6 -> EXIT;
            default -> ERROR;
        };
    }
}