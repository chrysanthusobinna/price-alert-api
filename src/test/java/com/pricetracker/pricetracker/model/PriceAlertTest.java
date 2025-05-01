package com.pricetracker.pricetracker.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

class PriceAlertTest {

    @Test
    void testPriceAlertCreation() {
        // Arrange
        PriceAlert alert = new PriceAlert();
        Product product = new Product();
        product.setId(1L);
        LocalDateTime now = LocalDateTime.now();
        LocalTime customTime = LocalTime.of(10, 0);
        
        // Act
        alert.setId(1L);
        alert.setProduct(product);
        alert.setTargetPrice(99.99);
        alert.setUserEmail("test@chrys-online.com");
        alert.setLastChecked(now);
        alert.setLastNotified(now);
        alert.setCheckFrequency(CheckFrequency.DAILY_MORNING_09_00);
        alert.setCustomTime(customTime);
        alert.setNotificationSent(true);
        
        // Assert
        assertNotNull(alert);
        assertEquals(1L, alert.getId());
        assertEquals(product, alert.getProduct());
        assertEquals(99.99, alert.getTargetPrice());
        assertEquals("test@chrys-online.com", alert.getUserEmail());
        assertEquals(now, alert.getLastChecked());
        assertEquals(now, alert.getLastNotified());
        assertEquals(CheckFrequency.DAILY_MORNING_09_00, alert.getCheckFrequency());
        assertEquals(customTime, alert.getCustomTime());
        assertTrue(alert.isNotificationSent());
    }
    
    @Test
    void testPriceAlertNoArgsConstructor() {
        
        // Act
        PriceAlert alert = new PriceAlert();
        
        // Assert
        assertNotNull(alert);
        assertNull(alert.getId());
        assertNull(alert.getProduct());
        assertNull(alert.getTargetPrice());
        assertNull(alert.getUserEmail());
        assertNull(alert.getLastChecked());
        assertNull(alert.getLastNotified());
        assertNull(alert.getCheckFrequency());
        assertNull(alert.getCustomTime());
        assertFalse(alert.isNotificationSent());
    }
    
    @Test
    void testPriceAlertEquality() {
        // Arrange
        Product product = new Product();
        product.setId(1L);
        LocalDateTime now = LocalDateTime.now();
        LocalTime customTime = LocalTime.of(10, 0);
        
        PriceAlert alert1 = new PriceAlert();
        PriceAlert alert2 = new PriceAlert();
        
        // Act
        alert1.setId(1L);
        alert1.setProduct(product);
        alert1.setTargetPrice(99.99);
        alert1.setUserEmail("test@chrys-online.com");
        alert1.setLastChecked(now);
        alert1.setLastNotified(now);
        alert1.setCheckFrequency(CheckFrequency.DAILY_MORNING_09_00);
        alert1.setCustomTime(customTime);
        alert1.setNotificationSent(true);
        
        alert2.setId(1L);
        alert2.setProduct(product);
        alert2.setTargetPrice(99.99);
        alert2.setUserEmail("test@chrys-online.com");
        alert2.setLastChecked(now);
        alert2.setLastNotified(now);
        alert2.setCheckFrequency(CheckFrequency.DAILY_MORNING_09_00);
        alert2.setCustomTime(customTime);
        alert2.setNotificationSent(true);
        
        // Assert
        assertEquals(alert1, alert2);
        assertEquals(alert1.hashCode(), alert2.hashCode());
    }
    
    @Test
    void testPriceAlertWithDifferentProducts() {
        // Arrange
        Product product1 = new Product();
        product1.setId(1L);
        Product product2 = new Product();
        product2.setId(2L);
        
        PriceAlert alert1 = new PriceAlert();
        PriceAlert alert2 = new PriceAlert();
        
        // Act
        alert1.setId(1L);
        alert1.setProduct(product1);
        alert1.setTargetPrice(99.99);
        alert1.setUserEmail("test@chrys-online.com");
        
        alert2.setId(1L);
        alert2.setProduct(product2);
        alert2.setTargetPrice(99.99);
        alert2.setUserEmail("test@chrys-online.com");
        
        // Assert
        assertNotEquals(alert1, alert2);
        assertNotEquals(alert1.hashCode(), alert2.hashCode());
    }
} 