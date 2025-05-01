package com.pricetracker.pricetracker.controller;

import com.pricetracker.pricetracker.service.PriceAlertService;
import com.pricetracker.pricetracker.model.CheckFrequency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import jakarta.validation.constraints.Min;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Email;
import org.springframework.validation.annotation.Validated;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alerts")
@Validated
public class PriceAlertController {
    
    @Autowired
    private PriceAlertService priceAlertService;
    
    private static final List<String> VALID_FREQUENCIES = Arrays.asList(
        "DAILY_MORNING_09_00", "DAILY_AFTERNOON_15_00", "DAILY_EVENING_18_00", "DAILY_MIDNIGHT_00_00", "CUSTOM"
    );
    
    @PostMapping
    public ResponseEntity<?> createPriceAlert(
            @RequestParam String productUrl,
            @RequestParam @Min(value = 1, message = "Target price must be greater than or equal to 1") Double targetPrice,
            @RequestParam @Email(message = "Invalid email format") String userEmail,
            @RequestParam(defaultValue = "DAILY") String checkFrequency,
            @RequestParam(required = false) String customTime) {
        try {
            if (checkFrequency.equals("CUSTOM") && (customTime == null || customTime.isEmpty())) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Custom time is required when CUSTOM frequency is selected");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (checkFrequency.equals("CUSTOM") && !CheckFrequency.isValidCustomTime(customTime)) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Invalid custom time format. Please use HH:mm format (e.g., 10:30)");
                return ResponseEntity.badRequest().body(response);
            }
            
            Map<String, Object> response = priceAlertService.createOrUpdatePriceAlertWithResponse(
                productUrl, targetPrice, userEmail, checkFrequency, customTime);
            if (response.containsKey("error")) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/user/{userEmail}")
    public ResponseEntity<?> getUserAlerts(
            @PathVariable @jakarta.validation.constraints.Email(message = "Invalid email format") String userEmail) {
        Object response = priceAlertService.getUserAlertsWithResponse(userEmail);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<?> deletePriceAlert(
            @RequestParam String productUrl,
            @RequestParam @Email(message = "Invalid email format") String userEmail) {
        Map<String, Object> response = priceAlertService.deletePriceAlert(productUrl, userEmail);
        if (response.containsKey("error")) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
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
        if (ex.getName().equals("targetPrice")) {
            errors.put("error", "Price must be a valid number");
        } else {
            errors.put("error", "Invalid input format for " + ex.getName());
        }
        return ResponseEntity.badRequest().body(errors);
    }
} 
