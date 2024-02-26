package com.shaikhraziev.error;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder
@Value
public class Error {

    String error;
    String date;

    public static final Error INVALID_LOGIN_OR_PASSWORD = Error.builder()
            .error("Invalid username or password!")
            .date(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:s")))
            .build();

    public static final Error INVALID_REQUEST = Error.builder()
            .error("Invalid request!")
            .date(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:s")))
            .build();

    public static final Error USER_ALREADY_EXISTS = Error.builder()
            .error("User already exists!")
            .date(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:s")))
            .build();

    public static final Error USER_WAS_NOT_FOUND = Error.builder()
            .error("There is no such user!")
            .date(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:s")))
            .build();

    public static final Error INDICATIONS_WAS_NOT_TRANSMITTED = Error.builder()
            .error("The indications was not transmitted!")
            .date(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:s")))
            .build();

    public static final Error NOT_ENOUGH_RIGHTS = Error.builder()
            .error("Not enough rights!")
            .date(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:s")))
            .build();

    public static final Error INVALID_VALUES_TRANSMITTED_INDICATIONS = Error.builder()
            .error("Transmitted indications should be no less than last!")
            .date(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:s")))
            .build();

    public static final Error INDICATIONS_HAS_ALREADY_BEEN_TRANSMITTED = Error.builder()
            .error("Indications can be submitted once a month!")
            .date(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:s")))
            .build();
}