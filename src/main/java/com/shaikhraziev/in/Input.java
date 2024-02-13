package com.shaikhraziev.in;

import com.shaikhraziev.dto.IndicationCreateEditDto;
import com.shaikhraziev.dto.UserCreateEditDto;

import java.time.LocalDate;
import java.time.Month;
import java.util.Scanner;

public class Input {

    Scanner scanner = new Scanner(System.in);

    /**
     * Метод для ввода пользователем username и password
     *
     * @return Возвращает username и password, введенные пользователем
     */
    public UserCreateEditDto enteringUsernameAndPassword() {
        System.out.println("Введите username (от 3 до 10 символов, английские буквы, цифры, знаки _ и -):");
        String username = scanner.nextLine();

        System.out.println("Введите password (от 3 до 10 символов, хотя бы одна буква): ");
        String password = scanner.nextLine();

        return UserCreateEditDto.builder()
                .username(username)
                .password(password)
                .build();
    }

    /**
     * Метод для ввода пользователем показаний со счетчиков
     *
     * @return Возвращает показания, введенные пользователем
     */
    public IndicationCreateEditDto enteringIndications() {
        try {
            System.out.println("Отопление:");
            Long heating = scanner.nextLong();

            System.out.println("Горячая вода:");
            Long hotWater = scanner.nextLong();

            System.out.println("Холодная вода:");
            Long coldWater = scanner.nextLong();

            return IndicationCreateEditDto.builder()
                    .date(LocalDate.now())
                    .heating(heating)
                    .hotWater(hotWater)
                    .coldWater(coldWater)
                    .build();
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Метод для ввода пользователем месяца
     *
     * @return Возвращает месяц
     */
    public Month enteringMonth() {
        System.out.println("Введите порядковый номер месяца:");

        try {
            int monthNumber = scanner.nextInt();
            return Month.of(monthNumber);
        } catch (Exception e) {
            System.out.println("An incorrect number was entered!");
            return null;
        }
    }
}