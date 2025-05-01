package com.pricetracker.pricetracker.service;

import com.pricetracker.pricetracker.model.PriceAlert;
import com.pricetracker.pricetracker.model.Product;
import com.pricetracker.pricetracker.model.CheckFrequency;
import com.pricetracker.pricetracker.repository.PriceAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class PriceAlertService {
    
    private final PriceAlertRepository priceAlertRepository;
    
    private final ProductService productService;
    
    private final NotificationService notificationService;

    public PriceAlert createPriceAlert(String productUrl, Double targetPrice, String userEmail, String checkFrequency, String customTime) {
        if (!CheckFrequency.isValid(checkFrequency)) {
            throw new IllegalArgumentException("Invalid check frequency. Valid options are: " + 
                String.join(", ", List.of(CheckFrequency.values()).stream()
                    .map(CheckFrequency::name)
                    .collect(Collectors.toList())));
        }
        
        Product product = productService.getProductByUrl(productUrl);
        
        if (targetPrice >= product.getCurrentPrice()) {
            throw new IllegalArgumentException("Target price must be less than current price (" + product.getCurrentPrice() + ")");
        }
        
        Optional<PriceAlert> existingAlert = priceAlertRepository.findByProductUrlAndUserEmail(productUrl, userEmail);
        
        if (existingAlert.isPresent()) {
            // Update existing alert
            PriceAlert alert = existingAlert.get();
            alert.setTargetPrice(targetPrice);
            alert.setCheckFrequency(CheckFrequency.valueOf(checkFrequency));
            if (checkFrequency.equals("CUSTOM")) {
                alert.setCustomTime(CheckFrequency.parseCustomTime(customTime));
            } else {
                alert.setCustomTime(null);
            }
            return priceAlertRepository.save(alert);
        } else {
            // Create new alert
            PriceAlert alert = new PriceAlert();
            alert.setProduct(product);
            alert.setTargetPrice(targetPrice);
            alert.setUserEmail(userEmail);
            alert.setCheckFrequency(CheckFrequency.valueOf(checkFrequency));
            if (checkFrequency.equals("CUSTOM")) {
                alert.setCustomTime(CheckFrequency.parseCustomTime(customTime));
            }
            alert.setNotificationSent(false);
            return priceAlertRepository.save(alert);
        }
    }

    @Async
    public void notifyPriceChanges() {
        //Get the earliest last notified date from the database
        Optional<PriceAlert> earliestPriceAlert = priceAlertRepository.findFirstByOrderByLastNotifiedAsc();
        if (earliestPriceAlert.isEmpty()) {
            return;
        }
        
        LocalDateTime earliestLastNotified = earliestPriceAlert.get().getLastNotified() == null ?
                LocalDateTime.now().minusYears(1) : earliestPriceAlert.get().getLastNotified();
        
        List<Product> products = productService.getAllProductsWhereLastUpdatedIsAfter(earliestLastNotified);
        
        for (Product product : products) {
            List<PriceAlert> alerts = priceAlertRepository
                    .findAllByProductIdAndTargetPriceIsGreaterThanEqual(product.getId(), product.getCurrentPrice())
                    .stream()
                    .filter(this::shouldCheckAlertNow)
                    .toList();
            
            //send email notification to multiple users
            alerts.forEach(alert -> {
                LocalDateTime now = LocalDateTime.now();
                alert.setLastNotified(now);
                alert.setLastChecked(now);
                priceAlertRepository.save(alert);
                notificationService.sendNotification(alert);
            });
        }
    }
    
    public List<PriceAlert> getUserAlerts(String userEmail) {
        return priceAlertRepository.findByUserEmail(userEmail);
    }
    
    public boolean alertExists(String productUrl, String userEmail) {
        return priceAlertRepository.findByProductUrlAndUserEmail(productUrl, userEmail).isPresent();
    }

    private boolean shouldCheckAlertNow(PriceAlert alert) {
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();
        
        // For new alerts or if we haven't checked today yet
        if (alert.getLastChecked() == null || alert.getLastChecked().toLocalDate().isBefore(now.toLocalDate())) {
            // Get the appropriate check time based on frequency
            LocalTime checkTime;
            if (alert.getCheckFrequency() == CheckFrequency.CUSTOM) {
                checkTime = alert.getCustomTime();
            } else {
                checkTime = alert.getCheckFrequency().getCheckTime();
            }
            
            // Only check if we have a valid check time and we're at or past the check time
            return checkTime != null && !currentTime.isBefore(checkTime);
        }
        
        // If we've already checked today, don't check again
        return false;
    }

    public Map<String, Object> createOrUpdatePriceAlertWithResponse(String productUrl, Double targetPrice, String userEmail, String checkFrequency, String customTime) {
        if (!CheckFrequency.isValid(checkFrequency)) {
            Map<String, String> response = new java.util.HashMap<>();
            response.put("error", "Invalid check frequency. Valid options are: " + 
                String.join(", ", List.of(CheckFrequency.values()).stream()
                    .map(CheckFrequency::name)
                    .collect(Collectors.toList())));
            return (Map) response;
        }
        boolean alertExists = alertExists(productUrl, userEmail);
        PriceAlert alert = createPriceAlert(productUrl, targetPrice, userEmail, checkFrequency, customTime);
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("message", "Price alert " + (alertExists ? "updated" : "created") + " successfully");
        response.put("alert", alert);
        return response;
    }

    public Object getUserAlertsWithResponse(String userEmail) {
        List<PriceAlert> alerts = getUserAlerts(userEmail);
        if (alerts.isEmpty()) {
            Map<String, String> response = new java.util.HashMap<>();
            response.put("message", "No tracking records found for user: " + userEmail);
            return response;
        }
        return alerts;
    }

    public Map<String, Object> deletePriceAlert(String productUrl, String userEmail) {
        Optional<PriceAlert> alert = priceAlertRepository.findByProductUrlAndUserEmail(productUrl, userEmail);
        Map<String, Object> response = new HashMap<>();
        
        if (alert.isPresent()) {
            priceAlertRepository.delete(alert.get());
            response.put("message", "Price alert deleted successfully");
        } else {
            response.put("error", "No price alert found for the given product and user");
        }
        
        return response;
    }
} 
