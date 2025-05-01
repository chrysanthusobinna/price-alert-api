package com.pricetracker.pricetracker.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

class ProductTest {

    @Test
    void testProductCreation() {
        // Arrange
        Product product = new Product();
        LocalDateTime now = LocalDateTime.now();
        
        // Act
        product.setId(1L);
        product.setName("Test Product");
        product.setUrl("https://chrys-online.com/product");
        product.setCurrentPrice(99.99);
        product.setLastUpdated(now);
        
        // Assert
        assertNotNull(product);
        assertEquals(1L, product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals("https://chrys-online.com/product", product.getUrl());
        assertEquals(99.99, product.getCurrentPrice());
        assertEquals(now, product.getLastUpdated());
    }
    
    @Test
    void testProductNoArgsConstructor() {
        // Arrange - nothing needed
        
        // Act
        Product product = new Product();
        
        // Assert
        assertNotNull(product);
        assertNull(product.getId());
        assertNull(product.getName());
        assertNull(product.getUrl());
        assertNull(product.getCurrentPrice());
        assertNotNull(product.getLastUpdated());
    }
    
    @Test
    void testProductEquality() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Product product1 = new Product();
        Product product2 = new Product();
        
        // Act
        product1.setId(1L);
        product1.setName("Test Product");
        product1.setUrl("https://chrys-online.com/product");
        product1.setCurrentPrice(99.99);
        product1.setLastUpdated(now);
        
        product2.setId(1L);
        product2.setName("Test Product");
        product2.setUrl("https://chrys-online.com/product");
        product2.setCurrentPrice(99.99);
        product2.setLastUpdated(now);
        
        // Assert
        assertEquals(product1, product2);
        assertEquals(product1.hashCode(), product2.hashCode());
    }
} 