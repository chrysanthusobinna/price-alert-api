package com.pricetracker.pricetracker.service;

import com.pricetracker.pricetracker.model.PriceAlert;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    public void sendNotification(PriceAlert alert) {
        if (alert == null) {
            throw new NullPointerException("Alert cannot be null");
        }
        if (alert.getProduct() == null) {
            throw new NullPointerException("Product cannot be null");
        }
        if (alert.getUserEmail() == null) {
            throw new NullPointerException("User email cannot be null");
        }
        
        logger.info("Price Alert: Product {} has reached target price of {}. Current price is {}. Notifying user: {}",
            alert.getProduct().getName(),
            alert.getTargetPrice(),
            alert.getProduct().getCurrentPrice(),
            alert.getUserEmail());
    }
} 