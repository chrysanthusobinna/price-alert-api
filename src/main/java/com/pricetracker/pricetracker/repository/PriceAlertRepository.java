package com.pricetracker.pricetracker.repository;

import com.pricetracker.pricetracker.model.PriceAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PriceAlertRepository extends JpaRepository<PriceAlert, Long> {
    List<PriceAlert> findByUserEmail(String userEmail);
    Optional<PriceAlert> findByProductUrlAndUserEmail(String productUrl, String userEmail);
    
    Optional<PriceAlert> findFirstByOrderByLastNotifiedAsc();
    
    List<PriceAlert> findAllByProductIdAndTargetPriceIsLessThanEqual(Long id, Double currentPrice);
    
    List<PriceAlert> findAllByProductIdAndTargetPriceIsGreaterThanEqual(Long id, Double currentPrice);
}
