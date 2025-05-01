package com.pricetracker.pricetracker.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

class CheckFrequencyTest {

    @Test
    void testEnumValues() {
        // Arrange & Act & Assert
        assertEquals(LocalTime.of(9, 0), CheckFrequency.DAILY_MORNING_09_00.getCheckTime());
        assertEquals(LocalTime.of(15, 0), CheckFrequency.DAILY_AFTERNOON_15_00.getCheckTime());
        assertEquals(LocalTime.of(18, 0), CheckFrequency.DAILY_EVENING_18_00.getCheckTime());
        assertEquals(LocalTime.of(0, 0), CheckFrequency.DAILY_MIDNIGHT_00_00.getCheckTime());
        assertNull(CheckFrequency.CUSTOM.getCheckTime());
    }

    @Test
    void testValidFrequencies() {
        // Arrange & Act & Assert
        assertTrue(CheckFrequency.isValid("DAILY_MORNING_09_00"));
        assertTrue(CheckFrequency.isValid("DAILY_AFTERNOON_15_00"));
        assertTrue(CheckFrequency.isValid("DAILY_EVENING_18_00"));
        assertTrue(CheckFrequency.isValid("DAILY_MIDNIGHT_00_00"));
        assertTrue(CheckFrequency.isValid("CUSTOM"));
    }

    @Test
    void testInvalidFrequencies() {
        // Arrange & Act & Assert
        assertFalse(CheckFrequency.isValid("INVALID_FREQUENCY"));
        assertFalse(CheckFrequency.isValid("DAILY"));
        assertFalse(CheckFrequency.isValid(""));
    }

    @Test
    void testNullFrequency() {
        // Arrange & Act & Assert
        assertFalse(CheckFrequency.isValid(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "00:00", "09:00", "15:00", "18:30", "23:59"
    })
    void testValidCustomTimes(String time) {
        // Arrange & Act & Assert
        assertTrue(CheckFrequency.isValidCustomTime(time));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "24:00", "25:00", "09:60", "-1:00", 
        "invalid", "", "9am", "3:30pm", "9:0", "09:0"
    })
    void testInvalidCustomTimes(String time) {
        // Arrange & Act & Assert
        assertFalse(CheckFrequency.isValidCustomTime(time));
    }

    @Test
    void testParseCustomTimeValid() {
        // Arrange
        String validTime = "09:30";
        
        // Act
        LocalTime parsedTime = CheckFrequency.parseCustomTime(validTime);
        
        // Assert
        assertEquals(LocalTime.of(9, 30), parsedTime);
    }

    @Test
    void testParseCustomTimeInvalid() {
        // Arrange
        String invalidTime = "25:00";
        
        // Act & Assert
        assertThrows(DateTimeParseException.class, () -> {
            CheckFrequency.parseCustomTime(invalidTime);
        });
    }

    @Test
    void testEnumCount() {
        // Arrange & Act & Assert
        assertEquals(5, CheckFrequency.values().length);
    }
} 