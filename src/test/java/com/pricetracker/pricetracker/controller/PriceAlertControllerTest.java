package com.pricetracker.pricetracker.controller;

import com.pricetracker.pricetracker.model.PriceAlert;
import com.pricetracker.pricetracker.model.Product;
import com.pricetracker.pricetracker.model.CheckFrequency;
import com.pricetracker.pricetracker.service.PriceAlertService;
import com.pricetracker.pricetracker.service.ProductService;
import com.pricetracker.pricetracker.config.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PriceAlertController.class)
@Import(TestConfig.class)
class PriceAlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PriceAlertService priceAlertService;

    @MockBean
    private ProductService productService;

    private PriceAlert testAlert;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setCurrentPrice(1000.00);
        testProduct.setUrl("https://chrys-online.com/test");
        
        testAlert = new PriceAlert();
        testAlert.setId(1L);
        testAlert.setProduct(testProduct);
        testAlert.setTargetPrice(800.00);
        testAlert.setUserEmail("test@chrys-online.com");
        testAlert.setCheckFrequency(CheckFrequency.DAILY_MORNING_09_00);
    }

    @Test
    void createPriceAlert_ShouldCreateAlertSuccessfully() throws Exception {
        // Arrange
        String productUrl = "https://chrys-online.com/test";
        Double targetPrice = 800.00;
        String userEmail = "test@chrys-online.com";
        String checkFrequency = "DAILY_MORNING_09_00";
        String customTime = null;
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Price alert created successfully");
        response.put("alert", testAlert);
        
        when(priceAlertService.createOrUpdatePriceAlertWithResponse(productUrl, targetPrice, userEmail, checkFrequency, customTime))
            .thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/alerts")
                .param("productUrl", productUrl)
                .param("targetPrice", targetPrice.toString())
                .param("userEmail", userEmail)
                .param("checkFrequency", checkFrequency))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Price alert created successfully"))
                .andExpect(jsonPath("$.alert").exists());
    }

    @Test
    void createPriceAlert_WithCustomTime_ShouldCreateAlertSuccessfully() throws Exception {
        // Arrange
        String productUrl = "https://chrys-online.com/test";
        Double targetPrice = 800.00;
        String userEmail = "test@chrys-online.com";
        String checkFrequency = "CUSTOM";
        String customTime = "10:30";
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Price alert created successfully");
        response.put("alert", testAlert);
        
        when(priceAlertService.createOrUpdatePriceAlertWithResponse(productUrl, targetPrice, userEmail, checkFrequency, customTime))
            .thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/alerts")
                .param("productUrl", productUrl)
                .param("targetPrice", targetPrice.toString())
                .param("userEmail", userEmail)
                .param("checkFrequency", checkFrequency)
                .param("customTime", customTime))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Price alert created successfully"))
                .andExpect(jsonPath("$.alert").exists());
    }

    @Test
    void createPriceAlert_WithInvalidCustomTime_ShouldReturnBadRequest() throws Exception {
        // Arrange
        String productUrl = "https://chrys-online.com/test";
        Double targetPrice = 800.00;
        String userEmail = "test@chrys-online.com";
        String checkFrequency = "CUSTOM";
        String customTime = "25:00"; // Invalid time
        
        // Act & Assert
        mockMvc.perform(post("/api/alerts")
                .param("productUrl", productUrl)
                .param("targetPrice", targetPrice.toString())
                .param("userEmail", userEmail)
                .param("checkFrequency", checkFrequency)
                .param("customTime", customTime))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid custom time format. Please use HH:mm format (e.g., 10:30)"));
    }

    @Test
    void createPriceAlert_WithCustomFrequencyNoTime_ShouldReturnBadRequest() throws Exception {
        // Arrange
        String productUrl = "https://chrys-online.com/test";
        Double targetPrice = 800.00;
        String userEmail = "test@chrys-online.com";
        String checkFrequency = "CUSTOM";
        
        // Act & Assert
        mockMvc.perform(post("/api/alerts")
                .param("productUrl", productUrl)
                .param("targetPrice", targetPrice.toString())
                .param("userEmail", userEmail)
                .param("checkFrequency", checkFrequency))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Custom time is required when CUSTOM frequency is selected"));
    }

    @Test
    void createPriceAlert_ShouldUpdateExistingAlert() throws Exception {
        // Arrange
        String productUrl = "https://chrys-online.com/test";
        Double targetPrice = 800.00;
        String userEmail = "test@chrys-online.com";
        String checkFrequency = "DAILY_MORNING_09_00";
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Price alert updated successfully");
        response.put("alert", testAlert);
        
        when(priceAlertService.createOrUpdatePriceAlertWithResponse(productUrl, targetPrice, userEmail, checkFrequency, null))
            .thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/alerts")
                .param("productUrl", productUrl)
                .param("targetPrice", targetPrice.toString())
                .param("userEmail", userEmail)
                .param("checkFrequency", checkFrequency))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Price alert updated successfully"))
                .andExpect(jsonPath("$.alert").exists());
    }

    @Test
    void createPriceAlert_ShouldReturnBadRequest_WhenTargetPriceNotLess() throws Exception {
        // Arrange
        String productUrl = "https://chrys-online.com/test";
        Double targetPrice = 1200.00;
        String userEmail = "test@chrys-online.com";
        String checkFrequency = "DAILY_MORNING_09_00";
        
        when(priceAlertService.createOrUpdatePriceAlertWithResponse(productUrl, targetPrice, userEmail, checkFrequency, null))
            .thenThrow(new IllegalArgumentException("Target price must be less than current price"));

        // Act & Assert
        mockMvc.perform(post("/api/alerts")
                .param("productUrl", productUrl)
                .param("targetPrice", targetPrice.toString())
                .param("userEmail", userEmail)
                .param("checkFrequency", checkFrequency))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Target price must be less than current price"));
    }

    @Test
    void createPriceAlert_ShouldReturnBadRequest_WhenInvalidFrequency() throws Exception {
        // Arrange
        String productUrl = "https://chrys-online.com/test";
        Double targetPrice = 800.00;
        String userEmail = "test@chrys-online.com";
        String invalidFrequency = "INVALID";
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Invalid check frequency. Valid options are: " + 
            String.join(", ", List.of(CheckFrequency.values()).stream()
                .map(CheckFrequency::name)
                .collect(java.util.stream.Collectors.toList())));
        
        when(priceAlertService.createOrUpdatePriceAlertWithResponse(productUrl, targetPrice, userEmail, invalidFrequency, null))
            .thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/alerts")
                .param("productUrl", productUrl)
                .param("targetPrice", targetPrice.toString())
                .param("userEmail", userEmail)
                .param("checkFrequency", invalidFrequency))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void createPriceAlert_ShouldAcceptValidFrequencies() throws Exception {
        // Arrange
        String productUrl = "https://chrys-online.com/test";
        Double targetPrice = 800.00;
        String userEmail = "test@chrys-online.com";
        
        for (CheckFrequency frequency : CheckFrequency.values()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Price alert created successfully");
            response.put("alert", testAlert);
            
            // Create a mock request
            MockHttpServletRequestBuilder requestBuilder = post("/api/alerts")
                    .param("productUrl", productUrl)
                    .param("targetPrice", targetPrice.toString())
                    .param("userEmail", userEmail)
                    .param("checkFrequency", frequency.name());
            
            // Add custom time parameter for CUSTOM frequency
            if (frequency == CheckFrequency.CUSTOM) {
                requestBuilder.param("customTime", "10:30");
            }
            
            when(priceAlertService.createOrUpdatePriceAlertWithResponse(
                productUrl, 
                targetPrice, 
                userEmail, 
                frequency.name(), 
                frequency == CheckFrequency.CUSTOM ? "10:30" : null))
                .thenReturn(response);

            // Act & Assert
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Price alert created successfully"))
                    .andExpect(jsonPath("$.alert").exists());
        }
    }

    @Test
    void createPriceAlert_ShouldReturnBadRequest_WhenInvalidEmail() throws Exception {
        // Arrange
        String productUrl = "https://chrys-online.com/test";
        Double targetPrice = 800.00;
        String invalidEmail = "invalid-email";
        String checkFrequency = "DAILY_MORNING_09_00";

        // Act & Assert
        mockMvc.perform(post("/api/alerts")
                .param("productUrl", productUrl)
                .param("targetPrice", targetPrice.toString())
                .param("userEmail", invalidEmail)
                .param("checkFrequency", checkFrequency))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid email format"));
    }

    @Test
    void createPriceAlert_ShouldReturnBadRequest_WhenTargetPriceIsZero() throws Exception {
        // Arrange
        String productUrl = "https://chrys-online.com/test";
        Double zeroPrice = 0.00;
        String userEmail = "test@chrys-online.com";
        String checkFrequency = "DAILY_MORNING_09_00";

        // Act & Assert
        mockMvc.perform(post("/api/alerts")
                .param("productUrl", productUrl)
                .param("targetPrice", zeroPrice.toString())
                .param("userEmail", userEmail)
                .param("checkFrequency", checkFrequency))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Target price must be greater than or equal to 1"));
    }

    @Test
    void createPriceAlert_ShouldReturnBadRequest_WhenTargetPriceIsLessThanOne() throws Exception {
        // Arrange
        String productUrl = "https://chrys-online.com/test";
        Double priceLessThanOne = 0.50;
        String userEmail = "test@chrys-online.com";
        String checkFrequency = "DAILY_MORNING_09_00";

        // Act & Assert
        mockMvc.perform(post("/api/alerts")
                .param("productUrl", productUrl)
                .param("targetPrice", priceLessThanOne.toString())
                .param("userEmail", userEmail)
                .param("checkFrequency", checkFrequency))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Target price must be greater than or equal to 1"));
    }

    @Test
    void getUserAlerts_ShouldReturnUserAlerts() throws Exception {
        // Arrange
        String userEmail = "test@chrys-online.com";
        List<PriceAlert> alerts = Arrays.asList(testAlert);
        
        when(priceAlertService.getUserAlertsWithResponse(userEmail))
            .thenReturn(alerts);

        // Act & Assert
        mockMvc.perform(get("/api/alerts/user/{userEmail}", userEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testAlert.getId()))
                .andExpect(jsonPath("$[0].userEmail").value(userEmail));
    }

    @Test
    void getUserAlerts_ShouldReturnEmptyListMessage_WhenNoAlertsFound() throws Exception {
        // Arrange
        String userEmail = "test@chrys-online.com";
        Map<String, String> response = new HashMap<>();
        response.put("message", "No tracking records found for user: " + userEmail);
        
        when(priceAlertService.getUserAlertsWithResponse(userEmail))
            .thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/alerts/user/{userEmail}", userEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("No tracking records found for user: " + userEmail));
    }

    @Test
    void getUserAlerts_ShouldReturnBadRequest_WhenInvalidEmail() throws Exception {
        // Arrange
        String invalidEmail = "invalid-email";

        // Act & Assert
        mockMvc.perform(get("/api/alerts/user/{userEmail}", invalidEmail))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid email format"));
    }

    @Test
    void deletePriceAlert_ShouldDeleteAlertSuccessfully() throws Exception {
        // Arrange
        String productUrl = "https://chrys-online.com/test";
        String userEmail = "test@chrys-online.com";
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Price alert deleted successfully");
        
        when(priceAlertService.deletePriceAlert(productUrl, userEmail))
            .thenReturn(response);

        // Act & Assert
        mockMvc.perform(delete("/api/alerts")
                .param("productUrl", productUrl)
                .param("userEmail", userEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Price alert deleted successfully"));
    }

    @Test
    void deletePriceAlert_ShouldReturnBadRequest_WhenAlertNotFound() throws Exception {
        // Arrange
        String productUrl = "https://chrys-online.com/test";
        String userEmail = "test@chrys-online.com";
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "No price alert found for the given product and user");
        
        when(priceAlertService.deletePriceAlert(productUrl, userEmail))
            .thenReturn(response);

        // Act & Assert
        mockMvc.perform(delete("/api/alerts")
                .param("productUrl", productUrl)
                .param("userEmail", userEmail))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("No price alert found for the given product and user"));
    }

    @Test
    void deletePriceAlert_ShouldReturnBadRequest_WhenInvalidEmail() throws Exception {
        // Arrange
        String productUrl = "https://chrys-online.com/test";
        String invalidEmail = "invalid-email";

        // Act & Assert
        mockMvc.perform(delete("/api/alerts")
                .param("productUrl", productUrl)
                .param("userEmail", invalidEmail))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid email format"));
    }
} 