package com.shaikhraziev.service;

import com.shaikhraziev.dto.IndicationCreateEditDto;
import com.shaikhraziev.dto.IndicationReadDto;
import com.shaikhraziev.entity.Indication;
import com.shaikhraziev.repository.IndicationRepository;
import com.shaikhraziev.repository.UserRepository;
import com.shaikhraziev.validation.UserValidation;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Сервис для работы с показаниями
 */
@RequiredArgsConstructor
public class IndicationService {

    private final UserService userService;
    private final AuditService auditService;
    private final UserRepository userRepository;
    private final IndicationRepository indicationRepository;
    private final UserValidation userValidation;

    /**
     * Ищет актуальные показания
     *
     * @param id    id пользователя
     * @return      Возвращает актуальные показания пользователя, если они существуют
     */
    @SneakyThrows
    public Optional<IndicationReadDto> getActualIndications(Long id) {
        return indicationRepository.getActualIndications(id);
    }

    /**
     * Подает показания пользователя
     * @param id                        id пользователя
     * @param transmittedIndications    Переданные показания
     */
    @SneakyThrows
    public void uploadIndications(Long id, IndicationCreateEditDto transmittedIndications) {
        IndicationReadDto actualIndications = getActualIndications(id).orElse(null);
        Month currentMonth = LocalDate.now().getMonth();

        if (!userValidation.isTransmittedMoreActual(actualIndications, transmittedIndications) ||
            indicationsAlreadyUploaded(id, currentMonth)) return;

        indicationRepository.uploadIndications(id, transmittedIndications);
        System.out.println("Показания успешно поданы!");

        var userReadDtoWithoutPassword = userRepository.findById(id);

        userReadDtoWithoutPassword.ifPresent(user -> {
            try {
                auditService.uploadIndications(user.getUsername());
            } catch (SQLException e) {
                System.out.println("Audit exception!");
            }
        });
    }

    /**
     * Проверяет, подавали ли показания в этом месяце
     * @param id            id пользователя
     * @param currentMonth  Текущий месяц
     * @return              Возвращает true, если показания передавали в этом месяце
     */
    @SneakyThrows
    private boolean indicationsAlreadyUploaded(Long id, Month currentMonth) {
        if (indicationRepository.indicationsAlreadyUploaded(id, currentMonth)) {
            System.out.println("Indications can be submitted once a month!");
        }
        return indicationRepository.indicationsAlreadyUploaded(id, currentMonth);
    }

    /**
     * Ищет показания пользователя за конкретный месяц
     *
     * @param id        id пользователя
     * @param month     Месяц
     * @return          Возвращает показания пользователя за конкретный месяц, если они передавались
     */
    @SneakyThrows
    public List<IndicationReadDto> getMonthlyIndications(Long id, Month month) {
        return indicationRepository.getMonthlyIndications(id, month);
    }

    /**
     * Ищет историю подачи показаний пользователя
     *
     * @param id    id пользователя
     * @return      Возвращает историю подачи показаний пользователя, если показания передавались
     */
    @SneakyThrows
    public List<IndicationReadDto> getHistory(Long id) {
        return indicationRepository.getHistory(id);
    }

    /**
     * Выводит на консоль показания всех пользователей
     */
    @SneakyThrows
    public void printHistoryAllUsers() {
        List<Indication> history = indicationRepository.getHistory();

        if (history.isEmpty()) {
            System.out.println("Показания никогда не передавались!");
            return;
        }

        history.forEach(indication -> {
            String username = Objects.requireNonNull(userService.findById(indication.getUsersId()).orElse(null)).getUsername();
            System.out.printf("""
                                Показания пользователя %s, дата %s:
                                    Отопление:      %d
                                    Горячая вода:   %d
                                    Холодная вода:  %d
                """, username, indication.getDate(), indication.getHeating(), indication.getHotWater(), indication.getColdWater());
        });
    }
}