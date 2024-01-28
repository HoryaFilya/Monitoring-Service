package com.shaikhraziev.service;

import com.shaikhraziev.entity.Admin;
import com.shaikhraziev.entity.Audit;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для аудита действий пользователя
 */
@RequiredArgsConstructor
public class AuditService {

    /**
     * Аудит действий пользователей
     */
    @Getter
    private List<Audit> audits = new ArrayList<>();

    /**
     * Аудирование при регистрации
     * @param username      username пользователя
     */
    public void registration(String username) {
        audits.add(Audit.builder()
                .date(LocalDate.now())
                .event("Пользователь %s зарегистрировался.".formatted(username))
                .build());
    }

    /**
     * Аудирование при авторизации
     * @param username      username пользователя
     */
    public void authorization(String username) {
        audits.add(Audit.builder()
                .date(LocalDate.now())
                .event("Пользователь %s авторизовался.".formatted(username))
                .build());
    }

    /**
     * Аудирование при выходе из приложения
     * @param username      username пользователя
     */
    public void logout(String username) {
        audits.add(Audit.builder()
                .date(LocalDate.now())
                .event("Пользователь %s вышел из системы.".formatted(username))
                .build());
    }

    /**
     * Аудирование при получении актуальных показаний
     * @param username      username пользователя
     */
    public void getActualIndications(String username) {
        audits.add(Audit.builder()
                .date(LocalDate.now())
                .event("Пользователь %s получил актуальные показания.".formatted(username))
                .build());
    }

    /**
     * Аудирование при подачи показаний
     * @param username      username пользователя
     */
    public void uploadIndications(String username) {
        audits.add(Audit.builder()
                .date(LocalDate.now())
                .event("Пользователь %s подал показания.".formatted(username))
                .build());
    }

    /**
     * Аудирование при получении показаний за конкретный месяц
     * @param username      username пользователя
     * @param month         Месяц
     */
    public void getMonthlyIndications(String username, Month month) {
        audits.add(Audit.builder()
                .date(LocalDate.now())
                .event("Пользователь %s получил показания за %s.".formatted(username, month))
                .build());
    }


    /**
     * Аудирование при получении истории подачи показаний
     * @param username      username пользователя
     */
    public void getHistory(String username) {
        audits.add(Audit.builder()
                .date(LocalDate.now())
                .event("Пользователь %s получил историю показаний.".formatted(username))
                .build());
    }
}