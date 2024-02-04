package com.shaikhraziev.service;

import com.shaikhraziev.dto.*;
import com.shaikhraziev.entity.*;
import com.shaikhraziev.map.ActionAdminMapper;
import com.shaikhraziev.map.UserCreateEditMapper;
import com.shaikhraziev.map.UserReadMapper;
import com.shaikhraziev.repository.AuditRepository;
import com.shaikhraziev.repository.UserRepository;
import com.shaikhraziev.validation.UserValidation;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static com.shaikhraziev.entity.Action.ERROR;
import static com.shaikhraziev.entity.Action.LOGOUT;

/**
 * Сервис для работы с пользователем
 */
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserCreateEditMapper userCreateEditMapper;
    private final UserReadMapper userReadMapper;
    private final ActionAdminMapper actionAdminMapper;
    private final UserValidation userValidation;
    private final AuditRepository auditRepository;
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Регистрирует пользователя
     *
     * @param userDto   username и password, введенные пользователем
     * @return          Возвращает true при успешной регистрации
     */

    @SneakyThrows
    public boolean registration(UserCreateEditDto userDto) {
        User user = userCreateEditMapper.map(userDto);
        Optional<UserReadDto> maybeUser = findByUsername(user.getUsername());

        if (maybeUser.isPresent()) {
            System.out.println("User already exists!");
            return false;
        }

        System.out.println("%s registered successfully!".formatted(user.getUsername()));
        auditRepository.registration(user.getUsername());
        return userRepository.save(user);
    }

    /**
     * Авторизирует пользователя
     *
     * @param userDto   username и password, введенные пользователем
     * @return          Возвращает пользователя, если он существует
     */
    @SneakyThrows
    public Optional<UserReadDto> authorization(UserCreateEditDto userDto) {
        return userRepository.findByUsernameAndPassword(userDto)
                .map(userReadMapper::map);
    }

    /**
     * Ищет пользователя по username
     *
     * @param username  username пользователя
     * @return          Возвращает пользователя, если он существует
     */
    @SneakyThrows
    public Optional<UserReadDto> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userReadMapper::map);
    }

    /**
     * Останавливает приложение
     */
    public void exit() {
        System.exit(0);
    }


    /**
     * Ищет актуальные показания
     *
     * @param id    id пользователя
     * @return      Возвращает актуальные показания пользователя, если они существуют
     */
    @SneakyThrows
    public Optional<IndicationReadDto> getActualIndications(Long id) {
        return userRepository.getActualIndications(id);
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

        userRepository.uploadIndications(id, transmittedIndications);
        System.out.println("Показания успешно поданы!");

        var userReadDtoWithoutPassword = userRepository.findById(id);

        userReadDtoWithoutPassword.ifPresent(user -> {
            try {
                auditRepository.uploadIndications(user.getUsername());
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
        if (userRepository.indicationsAlreadyUploaded(id, currentMonth)) {
            System.out.println("Indications can be submitted once a month!");
        }
        return userRepository.indicationsAlreadyUploaded(id, currentMonth);
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
        return userRepository.getMonthlyIndications(id, month);
    }

    /**
     * Ищет историю подачи показаний пользователя
     *
     * @param id    id пользователя
     * @return      Возвращает историю подачи показаний пользователя, если показания передавались
     */
    @SneakyThrows
    public List<IndicationReadDto> getHistory(Long id) {
        return userRepository.getHistory(id);
    }

    /**
     * Ищет пользователя по id
     * @param id    id пользователя
     * @return      Возвращает пользователя по id, если он существует
     */
    @SneakyThrows
    public Optional<UserReadDtoWithoutPassword> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Логика работы для администратора
     */
    public void adminControl() {
        while (true) {
            Action action = applicationMenuForAdmin();

            if (action.equals(LOGOUT)) break;

            switch (action) {
                case USER_INDICATIONS:
                    printHistoryAllUsers();
                    continue;

                case USER_AUDIT:
                    getUserAudit();
                    continue;

                case EXIT:
                    System.exit(0);

                default:
                    continue;
            }
        }
    }

    /**
     * Меня для яадинистратора
     * @return      Возвращает действие, выбранное администратором
     */
    private Action applicationMenuForAdmin() {
        System.out.println("Выберите действие:");
        System.out.println("1. Показания пользователей");
        System.out.println("2. Аудит действий пользователей");
        System.out.println("3. Выйти из аккаунта");
        System.out.println("4. Выйти из приложения");

        return getAction();
    }

    /**
     * Возвращает действие, выбранное администратором на основании введенного номера
     * @return      Возвращает действие, выбранное администратором
     */
    private Action getAction() {
        try {
            Integer actionNumber = scanner.nextInt();
            return actionAdminMapper.map(actionNumber);
        } catch (Exception ex) {
            scanner.nextLine();
            System.out.println("Invalid value!");
            return ERROR;
        }
    }

    /**
     * Выводит на консоль показания всех пользователей
     */
    @SneakyThrows
    public void printHistoryAllUsers() {
        List<Indication> history = userRepository.getHistory();

        if (history.isEmpty()) {
            System.out.println("Показания никогда не передавались!");
            return;
        }

        history.forEach(indication -> {
            String username = Objects.requireNonNull(findById(indication.getUsersId()).orElse(null)).getUsername();
            System.out.printf("""
                                Показания пользователя %s, дата %s:
                                    Отопление:      %d
                                    Горячая вода:   %d
                                    Холодная вода:  %d
                """, username, indication.getDate(), indication.getHeating(), indication.getHotWater(), indication.getColdWater());
        });
    }

    /**
     * Выводит на консоль аудит действий пользователей
     */
    @SneakyThrows
    private void getUserAudit() {
        List<Audit> audits = auditRepository.findAuditsAllUser();

        if (audits.isEmpty()) {
            System.out.println("Пользователи не совершали действий!");
            return;
        }

        audits.forEach(audit -> System.out.println("Дата: %s. Событие: %s".formatted(audit.getDate(), audit.getEvent())));
    }
}