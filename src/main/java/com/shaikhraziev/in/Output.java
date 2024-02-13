package com.shaikhraziev.in;

import com.shaikhraziev.dto.IndicationReadDto;
import com.shaikhraziev.dto.UserReadDto;
import com.shaikhraziev.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.time.Month;
import java.util.List;

@RequiredArgsConstructor
public class Output {

    private final AuditService auditService;

    /**
     * Выводит актуальные показания пользователя
     * @param indication            Актуальные показания пользователя
     * @param authorizationUser     Авторизованный пользователь
     */
    @SneakyThrows
    public void printIndicationsAndAudit(IndicationReadDto indication, UserReadDto authorizationUser) {
        printActualIndications(indication);
        auditService.getActualIndications(authorizationUser.getUsername());
    }

    /**
     * Выводит последние показания, отправленные пользователем
     *
     * @param indication Последние показания, отправленные пользователем
     */
    private void printActualIndications(IndicationReadDto indication) {
        System.out.printf("""
                Актуальные показания на %s:
                    Отопление:      %d
                    Горячая вода:   %d
                    Холодная вода:  %d
                """, indication.getDate(), indication.getHeating(), indication.getHotWater(), indication.getColdWater());
    }

    /**
     * Выводит на консоль историю подачи показаний пользователя
     *
     * @param history Содержит историю подачи показаний пользователя
     */
    public void printHistory(List<IndicationReadDto> history) {
        history.forEach((indications -> System.out.printf("""
                Дата: %s. Показания:
                    Отопление:      %d
                    Горячая вода:   %d
                    Холодная вода:  %d
                """, indications.getDate(), indications.getHeating(), indications.getHotWater(), indications.getColdWater())));
    }

    /**
     * Выводит на консоль показания пользователя за выбранный месяц
     *
     * @param monthlyIndications Показания пользователя за выбранный месяц
     * @param month              Месяц, выбранный пользователем
     */
    public void printMonthlyIndications(List<IndicationReadDto> monthlyIndications, Month month) {
        System.out.println("Показания за %s:".formatted(month.name()));
        monthlyIndications.forEach(indication -> {
                    System.out.printf("""
                                Отопление:      %d
                                Горячая вода:   %d
                                Холодная вода:  %d
                            """, indication.getHeating(), indication.getHotWater(), indication.getColdWater());
                }
        );
    }
}