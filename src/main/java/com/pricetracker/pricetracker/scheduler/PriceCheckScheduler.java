package com.pricetracker.pricetracker.scheduler;

import com.pricetracker.pricetracker.service.PriceAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceCheckScheduler {
    
    private final PriceAlertService priceAlertService;
    
    // Run every minute
    @Scheduled(cron = "0 * * * * ?")
    public void checkPrices() {
        priceAlertService.notifyPriceChanges();
    }
}
