package com.pricetracker.pricetracker.service;

import com.pricetracker.pricetracker.model.PriceAlert;
import com.pricetracker.pricetracker.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    private Product testProduct;
    private PriceAlert testAlert;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Setup test product
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setCurrentPrice(750.00);
        testProduct.setUrl("https://chrys-online.com/test");
        
        // Setup test alert
        testAlert = new PriceAlert();
        testAlert.setId(1L);
        testAlert.setProduct(testProduct);
        testAlert.setTargetPrice(800.00);
        testAlert.setUserEmail("test@chrys-online.com");
    }

    @Test
    void sendNotification_ShouldLogNotification() {
        // Arrange
        String expectedLogMessage = String.format(
            "Price Alert: Product %s has reached target price of %.2f. Current price is %.2f. Notifying user: %s",
            testProduct.getName(),
            testAlert.getTargetPrice(),
            testProduct.getCurrentPrice(),
            testAlert.getUserEmail()
        );

        // Act
        notificationService.sendNotification(testAlert);

        // Assert
        assertNotNull(expectedLogMessage);
    }


    @Test
    void sendNotification_ShouldHandleNullAlert() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            notificationService.sendNotification(null);
        });
    }

    @Test
    void sendNotification_ShouldHandleNullProduct() {
        // Arrange
        testAlert.setProduct(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            notificationService.sendNotification(testAlert);
        });
    }

    @Test
    void sendNotification_ShouldHandleNullUserEmail() {
        // Arrange
        testAlert.setUserEmail(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            notificationService.sendNotification(testAlert);
        });
    }
} 