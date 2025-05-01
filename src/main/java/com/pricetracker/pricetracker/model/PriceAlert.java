package com.pricetracker.pricetracker.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
public class PriceAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    private Double targetPrice;
    private String userEmail;

    private LocalDateTime lastChecked;
    
    private LocalDateTime lastNotified;
    
    @Enumerated(EnumType.STRING)
    private CheckFrequency checkFrequency;
    
    private LocalTime customTime;
    
    private boolean notificationSent;
} 
