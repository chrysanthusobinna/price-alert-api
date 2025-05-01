package com.pricetracker.pricetracker.controller;

import com.pricetracker.pricetracker.model.Product;
import com.pricetracker.pricetracker.service.ProductService;
import com.pricetracker.pricetracker.config.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(TestConfig.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setCurrentPrice(1000.00);
        testProduct.setUrl("https://chrys-online.com/test");
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() throws Exception {
        // Arrange
        List<Product> products = Arrays.asList(testProduct);
        when(productService.getAllProducts()).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testProduct.getId()))
                .andExpect(jsonPath("$[0].name").value(testProduct.getName()))
                .andExpect(jsonPath("$[0].currentPrice").value(testProduct.getCurrentPrice()));
    }

    @Test
    void updateProductPrice_ShouldUpdatePriceSuccessfully() throws Exception {
        // Arrange
        String productUrl = "https://chrys-online.com/test";
        Double newPrice = 750.00;
        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName("Test Product");
        updatedProduct.setCurrentPrice(750.00);
        updatedProduct.setUrl(productUrl);
        
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("message", "Price updated successfully");
        response.put("product", updatedProduct);
        
        when(productService.updateProductPriceAndReturnResponse(productUrl, newPrice))
            .thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/products/price")
                .param("url", productUrl)
                .param("price", newPrice.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Price updated successfully"))
                .andExpect(jsonPath("$.product.currentPrice").value(750.00));
    }

    @Test
    void updateProductPrice_ShouldReturnBadRequest_WhenProductNotFound() throws Exception {
        // Arrange
        String productUrl = "https://chrys-online.com/nonexistent";
        Double newPrice = 750.00;
        
        when(productService.updateProductPriceAndReturnResponse(productUrl, newPrice))
            .thenThrow(new RuntimeException("Product not found with URL: " + productUrl));

        // Act & Assert
        mockMvc.perform(put("/api/products/price")
                .param("url", productUrl)
                .param("price", newPrice.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Product not found with URL: " + productUrl));
    }

    @Test
    void updateProductPrice_ShouldReturnBadRequest_WhenPriceIsNegative() throws Exception {
        // Arrange
        String productUrl = "https://chrys-online.com/test";
        Double negativePrice = -100.00;

        // Act & Assert
        mockMvc.perform(put("/api/products/price")
                .param("url", productUrl)
                .param("price", negativePrice.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Price must be greater than or equal to 1"));
    }

    @Test
    void updateProductPrice_ShouldReturnBadRequest_WhenPriceIsZero() throws Exception {
        // Arrange
        String productUrl = "https://chrys-online.com/test";
        Double zeroPrice = 0.00;

        // Act & Assert
        mockMvc.perform(put("/api/products/price")
                .param("url", productUrl)
                .param("price", zeroPrice.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Price must be greater than or equal to 1"));
    }

    @Test
    void updateProductPrice_ShouldReturnBadRequest_WhenPriceIsLessThanOne() throws Exception {
        // Arrange
        String productUrl = "https://chrys-online.com/test";
        Double smallPrice = 0.99;

        // Act & Assert
        mockMvc.perform(put("/api/products/price")
                .param("url", productUrl)
                .param("price", smallPrice.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Price must be greater than or equal to 1"));
    }


} 