package com.pricetracker.pricetracker.service;

import com.pricetracker.pricetracker.model.PriceAlert;
import com.pricetracker.pricetracker.model.Product;
import com.pricetracker.pricetracker.model.CheckFrequency;
import com.pricetracker.pricetracker.repository.PriceAlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceAlertServiceTest {

    @Mock
    private PriceAlertRepository priceAlertRepository;

    @Mock
    private ProductService productService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PriceAlertService priceAlertService;

    private Product testProduct;
    private PriceAlert testAlert;

    @BeforeEach
    void setUp() {
        // Arrange - Common test objects
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setCurrentPrice(100.0);
        testProduct.setUrl("http://test.com/product");

        testAlert = new PriceAlert();
        testAlert.setId(1L);
        testAlert.setProduct(testProduct);
        testAlert.setTargetPrice(80.0);
        testAlert.setUserEmail("test@chrys-online.com");
        testAlert.setCheckFrequency(CheckFrequency.DAILY_MORNING_09_00);
        testAlert.setNotificationSent(false);
    }

    @Test
    void createPriceAlert_NewAlert_Success() {
        // Arrange
        when(productService.getProductByUrl(anyString())).thenReturn(testProduct);
        when(priceAlertRepository.findByProductUrlAndUserEmail(anyString(), anyString()))
                .thenReturn(Optional.empty());
        when(priceAlertRepository.save(any(PriceAlert.class))).thenReturn(testAlert);

        // Act
        PriceAlert result = priceAlertService.createPriceAlert(
                "http://test.com/product",
                80.0,
                "test@chrys-online.com",
                "DAILY_MORNING_09_00",
                null
        );

        // Assert
        assertNotNull(result);
        assertEquals(80.0, result.getTargetPrice());
        assertEquals(CheckFrequency.DAILY_MORNING_09_00, result.getCheckFrequency());
        verify(priceAlertRepository).save(any(PriceAlert.class));
    }

    @Test
    void createPriceAlert_InvalidFrequency_ThrowsException() {

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                priceAlertService.createPriceAlert(
                        "http://test.com/product",
                        80.0,
                        "test@chrys-online.com",
                        "INVALID",
                        null
                )
        );
    }

    @Test
    void createPriceAlert_TargetPriceHigherThanCurrent_ThrowsException() {
        // Arrange
        when(productService.getProductByUrl(anyString())).thenReturn(testProduct);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                priceAlertService.createPriceAlert(
                        "http://test.com/product",
                        120.0,
                        "test@chrys-online.com",
                        "DAILY_MORNING_09_00",
                        null
                )
        );
    }

    @Test
    void createPriceAlert_ExistingAlert_UpdatesAlert() {
        // Arrange
        when(productService.getProductByUrl(anyString())).thenReturn(testProduct);
        when(priceAlertRepository.findByProductUrlAndUserEmail(anyString(), anyString()))
                .thenReturn(Optional.of(testAlert));
        when(priceAlertRepository.save(any(PriceAlert.class))).thenReturn(testAlert);

        // Act
        PriceAlert result = priceAlertService.createPriceAlert(
                "http://test.com/product",
                70.0,
                "test@chrys-online.com",
                "DAILY_MORNING_09_00",
                null
        );

        // Assert
        assertNotNull(result);
        assertEquals(70.0, result.getTargetPrice());
        verify(priceAlertRepository).save(any(PriceAlert.class));
    }

    @Test
    void getUserAlerts_ReturnsUserAlerts() {
        // Arrange
        List<PriceAlert> expectedAlerts = Arrays.asList(testAlert);
        when(priceAlertRepository.findByUserEmail(anyString())).thenReturn(expectedAlerts);

        // Act
        List<PriceAlert> result = priceAlertService.getUserAlerts("test@chrys-online.com");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAlert, result.get(0));
    }

    @Test
    void alertExists_ReturnsTrue() {
        // Arrange
        when(priceAlertRepository.findByProductUrlAndUserEmail(anyString(), anyString()))
                .thenReturn(Optional.of(testAlert));

        // Act
        boolean result = priceAlertService.alertExists("http://test.com/product", "test@chrys-online.com");

        // Assert
        assertTrue(result);
    }

    @Test
    void alertExists_ReturnsFalse() {
        // Arrange
        when(priceAlertRepository.findByProductUrlAndUserEmail(anyString(), anyString()))
                .thenReturn(Optional.empty());

        // Act
        boolean result = priceAlertService.alertExists("http://test.com/product", "test@chrys-online.com");

        // Assert
        assertFalse(result);
    }

    @Test
    void deletePriceAlert_ExistingAlert_DeletesSuccessfully() {
        // Arrange
        when(priceAlertRepository.findByProductUrlAndUserEmail(anyString(), anyString()))
                .thenReturn(Optional.of(testAlert));

        // Act
        Map<String, Object> result = priceAlertService.deletePriceAlert(
                "http://test.com/product",
                "test@chrys-online.com"
        );

        // Assert
        assertNotNull(result);
        assertEquals("Price alert deleted successfully", result.get("message"));
        verify(priceAlertRepository).delete(testAlert);
    }

    @Test
    void deletePriceAlert_NonExistingAlert_ReturnsError() {
        // Arrange
        when(priceAlertRepository.findByProductUrlAndUserEmail(anyString(), anyString()))
                .thenReturn(Optional.empty());

        // Act
        Map<String, Object> result = priceAlertService.deletePriceAlert(
                "http://test.com/product",
                "test@chrys-online.com"
        );

        // Assert
        assertNotNull(result);
        assertEquals("No price alert found for the given product and user", result.get("error"));
        verify(priceAlertRepository, never()).delete(any());
    }

    @Test
    void createOrUpdatePriceAlertWithResponse_NewAlert_ReturnsSuccess() {
        // Arrange
        when(productService.getProductByUrl(anyString())).thenReturn(testProduct);
        when(priceAlertRepository.findByProductUrlAndUserEmail(anyString(), anyString()))
                .thenReturn(Optional.empty());
        when(priceAlertRepository.save(any(PriceAlert.class))).thenReturn(testAlert);

        // Act
        Map<String, Object> result = priceAlertService.createOrUpdatePriceAlertWithResponse(
                "http://test.com/product",
                80.0,
                "test@chrys-online.com",
                "DAILY_MORNING_09_00",
                null
        );

        // Assert
        assertNotNull(result);
        assertEquals("Price alert created successfully", result.get("message"));
        assertEquals(testAlert, result.get("alert"));
    }

    @Test
    void getUserAlertsWithResponse_NoAlerts_ReturnsMessage() {
        // Arrange
        when(priceAlertRepository.findByUserEmail(anyString())).thenReturn(Collections.emptyList());

        // Act
        Object result = priceAlertService.getUserAlertsWithResponse("test@chrys-online.com");

        // Assert
        assertTrue(result instanceof Map);
        Map<String, String> response = (Map<String, String>) result;
        assertEquals("No tracking records found for user: test@chrys-online.com", response.get("message"));
    }
} 