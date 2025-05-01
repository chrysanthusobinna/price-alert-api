package com.pricetracker.pricetracker.model;

import lombok.Getter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Getter
public enum CheckFrequency {
    DAILY_MORNING_09_00(LocalTime.of(9, 0)),
    DAILY_AFTERNOON_15_00(LocalTime.of(15, 0)),
    DAILY_EVENING_18_00(LocalTime.of(18, 0)),
    DAILY_MIDNIGHT_00_00(LocalTime.of(0, 0)),
    CUSTOM(null);

    private final LocalTime checkTime;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    CheckFrequency(LocalTime checkTime) {
        this.checkTime = checkTime;
    }

    public static boolean isValid(String frequency) {
        if (frequency == null) {
            return false;
        }
        try {
            CheckFrequency.valueOf(frequency);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean isValidCustomTime(String time) {
        if (time == null || time.isEmpty() || !time.matches("^([0-1][0-9]|2[0-3]):[0-5][0-9]$")) {
            return false;
        }
        try {
            LocalTime.parse(time, TIME_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static LocalTime parseCustomTime(String time) {
        return LocalTime.parse(time, TIME_FORMATTER);
    }
} 