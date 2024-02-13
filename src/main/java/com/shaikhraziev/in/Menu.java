package com.shaikhraziev.in;

import com.shaikhraziev.entity.Action;
import lombok.RequiredArgsConstructor;

import static com.shaikhraziev.entity.Phase.AUTHORIZATION_USER;
import static com.shaikhraziev.entity.Phase.NO_AUTHENTICATION_USER;

/**
 * Меню приложения
 */
@RequiredArgsConstructor
public class Menu {

    private final ActionUser actionUser;

    /**
     * Меню приложения
     *
     * @return Возвращает действие, выбранное пользователем
     */
    public Action applicationMenuForNoAuthenticationUser() {
        System.out.println("Сервис для подачи показаний счетчиков.");
        System.out.println("Выберите действие:");
        System.out.println("1. Регистрация");
        System.out.println("2. Авторизация");
        System.out.println("3. Выйти из приложения");

        return actionUser.getAction(NO_AUTHENTICATION_USER);
    }

    /**
     * Меню приложения для авторизованного пользователя
     *
     * @return Возвращает действие, выбранное пользователем
     */
    public Action applicationMenuForAuthorizationUser() {
        System.out.println("Выберите действие:");
        System.out.println("1. Получение актуальных показаний счетчиков");
        System.out.println("2. Подача показаний");
        System.out.println("3. Просмотр показаний за конкретный месяц");
        System.out.println("4. Просмотр истории подачи показаний");
        System.out.println("5. Выйти из аккаунта");
        System.out.println("6. Выйти из приложения");

        return actionUser.getAction(AUTHORIZATION_USER);
    }

    /**
     * Меня для яадинистратора
     * @return      Возвращает действие, выбранное администратором
     */
    public Action applicationMenuForAdmin() {
        System.out.println("Выберите действие:");
        System.out.println("1. Показания пользователей");
        System.out.println("2. Аудит действий пользователей");
        System.out.println("3. Выйти из аккаунта");
        System.out.println("4. Выйти из приложения");

        return actionUser.getAction();
    }
}