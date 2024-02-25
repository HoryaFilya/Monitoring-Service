package com.shaikhraziev.service;

import com.shaikhraziev.dto.IndicationCreateEditDto;
import com.shaikhraziev.dto.IndicationReadDto;
import com.shaikhraziev.map.IndicationReadMapper;
import com.shaikhraziev.repository.IndicationRepository;
import com.shaikhraziev.validation.UserValidation;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

/**
 * Сервис для работы с показаниями
 */
@RequiredArgsConstructor
public class IndicationService {

    private final IndicationRepository indicationRepository;
    private final UserValidation userValidation;
    private final IndicationReadMapper indicationReadMapper;

    /**
     * Ищет актуальные показания
     *
     * @param id id пользователя
     * @return Возвращает актуальные показания пользователя, если они существуют
     */
    @SneakyThrows
    public Optional<IndicationReadDto> getActualIndications(Long id) {
        return indicationRepository.getActualIndications(id)
                .map(indicationReadMapper::map);
    }

    /**
     * Подает показания пользователя
     *
     * @param id                     id пользователя
     * @param transmittedIndications Переданные показания
     */
    @SneakyThrows
    public void uploadIndications(Long id, IndicationCreateEditDto transmittedIndications, IndicationReadDto actualIndications) {
        Month currentMonth = LocalDate.now().getMonth();

        if (!userValidation.isTransmittedMoreActual(actualIndications, transmittedIndications) ||
            indicationsAlreadyUploaded(id, currentMonth)) return;

        indicationRepository.uploadIndications(id, transmittedIndications);
    }

    /**
     * Проверяет, подавали ли показания в этом месяце
     *
     * @param id           id пользователя
     * @param currentMonth Текущий месяц
     * @return Возвращает true, если показания передавали в этом месяце
     */
    @SneakyThrows
    public boolean indicationsAlreadyUploaded(Long id, Month currentMonth) {
        return indicationRepository.indicationsAlreadyUploaded(id, currentMonth);
    }

    /**
     * Ищет показания пользователя за конкретный месяц
     *
     * @param id    id пользователя
     * @param month Месяц
     * @return Возвращает показания пользователя за конкретный месяц, если они передавались
     */
    @SneakyThrows
    public List<IndicationReadDto> getMonthlyIndications(Long id, Month month) {
        return indicationRepository.getMonthlyIndications(id, month).stream()
                .map(indicationReadMapper::map)
                .toList();
    }

    /**
     * Ищет историю подачи показаний пользователя
     *
     * @param id id пользователя
     * @return Возвращает историю подачи показаний пользователя, если показания передавались
     */
    @SneakyThrows
    public List<IndicationReadDto> getHistory(Long id) {
        return indicationRepository.getHistory(id).stream()
                .map(indicationReadMapper::map)
                .toList();
    }

    /**
     * Выводит показания всех пользователей
     */
    @SneakyThrows
    public List<IndicationReadDto> getHistoryAllUsers() {
        List<IndicationReadDto> history = indicationRepository.getHistory().stream()
                .map(indicationReadMapper::map)
                .toList();

        if (history.isEmpty())
            return List.of();

        return history;
    }
}