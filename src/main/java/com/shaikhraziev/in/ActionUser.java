package com.shaikhraziev.in;

import com.shaikhraziev.entity.Action;
import com.shaikhraziev.entity.Phase;
import com.shaikhraziev.map.ActionAdminMapper;
import com.shaikhraziev.map.ActionUserMapper;
import lombok.RequiredArgsConstructor;

import java.util.Scanner;

import static com.shaikhraziev.entity.Action.ERROR;

/**
 * Действия пользователей
 */
@RequiredArgsConstructor
public class ActionUser {

    private final Scanner scanner = new Scanner(System.in);
    private final ActionUserMapper actionUserMapper;
    private final ActionAdminMapper actionAdminMapper;

    /**
     * Конвертирует число, выбранное пользователем, в действие (в зависимости от стадии, на которой находится пользователь)
     *
     * @param phase Стадия, на которой находится пользователь
     * @return Возвращает действие, выбранное пользователем
     */
    public Action getAction(Phase phase) {
        try {
            Integer actionNumber = scanner.nextInt();
            return actionUserMapper.map(actionNumber, phase);
        } catch (Exception ex) {
            scanner.nextLine();
            return ERROR;
        }
    }

    /**
     * Возвращает действие, выбранное администратором на основании введенного номера
     * @return      Возвращает действие, выбранное администратором
     */
    public Action getAction() {
        try {
            Integer actionNumber = scanner.nextInt();
            return actionAdminMapper.map(actionNumber);
        } catch (Exception ex) {
            scanner.nextLine();
            System.out.println("Invalid value!");
            return ERROR;
        }
    }
}