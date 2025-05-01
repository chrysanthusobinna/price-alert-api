package com.pricetracker.pricetracker.controller;

import com.pricetracker.pricetracker.model.Product;
import com.pricetracker.pricetracker.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@Validated
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    @PutMapping("/price")
    public ResponseEntity<?> updateProductPrice(
            @RequestParam String url, 
            @RequestParam @Min(value = 1, message = "Price must be greater than or equal to 1") Double price) {
        try {
            Map<String, Object> response = productService.updateProductPriceAndReturnResponse(url, price);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleValidationExceptions(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getConstraintViolations()
            .stream()
            .map(violation -> violation.getMessage())
            .collect(Collectors.joining(", ")));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, String> errors = new HashMap<>();
        if (ex.getName().equals("price")) {
            errors.put("error", "Price must be a valid number");
        } else {
            errors.put("error", "Invalid input format for " + ex.getName());
        }
        return ResponseEntity.badRequest().body(errors);
    }
} 
