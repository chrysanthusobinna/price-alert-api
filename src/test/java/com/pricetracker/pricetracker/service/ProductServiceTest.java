package com.pricetracker.pricetracker.service;

import com.pricetracker.pricetracker.model.Product;
import com.pricetracker.pricetracker.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import java.util.List;
import java.util.Collections;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setCurrentPrice(1000.00);
        testProduct.setUrl("https://chrys-online.com/test");
    }

    @Test
    void getProductByUrl_ShouldReturnProduct() {
        // Arrange
        when(productRepository.findByUrl("https://chrys-online.com/test")).thenReturn(testProduct);

        // Act
        Product result = productService.getProductByUrl("https://chrys-online.com/test");

        // Assert
        assertNotNull(result);
        assertEquals(testProduct, result);
    }

    @Test
    void getProductByUrl_ShouldThrowException_WhenProductNotFound() {
        // Arrange
        when(productRepository.findByUrl("https://chrys-online.com/test")).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            productService.getProductByUrl("https://chrys-online.com/test");
        });
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        // Arrange
        List<Product> products = Collections.singletonList(testProduct);
        when(productRepository.findAll()).thenReturn(products);

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testProduct, result.get(0));
    }

    @Test
    void findProductByUrl_ShouldReturnOptionalProduct() {
        // Arrange
        when(productRepository.findByUrl("https://chrys-online.com/test")).thenReturn(testProduct);

        // Act
        Optional<Product> result = productService.findProductByUrl("https://chrys-online.com/test");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testProduct, result.get());
    }

    @Test
    void findProductByUrl_ShouldReturnEmptyOptional_WhenProductNotFound() {
        // Arrange
        when(productRepository.findByUrl("https://chrys-online.com/test")).thenReturn(null);

        // Act
        Optional<Product> result = productService.findProductByUrl("https://chrys-online.com/test");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void updateProductPrice_ShouldUpdatePrice() {
        // Arrange
        when(productRepository.findByUrl("https://chrys-online.com/test")).thenReturn(testProduct);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        productService.updateProductPrice("https://chrys-online.com/test", 750.00);

        // Assert
        verify(productRepository).save(any(Product.class));
        assertEquals(750.00, testProduct.getCurrentPrice());
    }

    @Test
    void updateProductPrice_ShouldThrowException_WhenProductNotFound() {
        // Arrange
        when(productRepository.findByUrl("https://chrys-online.com/test")).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            productService.updateProductPrice("https://chrys-online.com/test", 750.00);
        });
    }

    @Test
    void getAllProductsWhereLastUpdatedIsAfter_ShouldReturnProductsUpdatedAfterDate() {
        // Arrange
        LocalDateTime testDate = LocalDateTime.now().minusDays(1);
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findAllByLastUpdatedAfter(testDate)).thenReturn(products);

        // Act
        List<Product> result = productService.getAllProductsWhereLastUpdatedIsAfter(testDate);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testProduct, result.get(0));
    }

    @Test
    void getAllProductsWhereLastUpdatedIsAfter_ShouldReturnEmptyList_WhenNoProductsFound() {
        // Arrange
        LocalDateTime testDate = LocalDateTime.now().minusDays(1);
        when(productRepository.findAllByLastUpdatedAfter(testDate)).thenReturn(Collections.emptyList());

        // Act
        List<Product> result = productService.getAllProductsWhereLastUpdatedIsAfter(testDate);

        // Assert
        assertTrue(result.isEmpty());
    }
} 