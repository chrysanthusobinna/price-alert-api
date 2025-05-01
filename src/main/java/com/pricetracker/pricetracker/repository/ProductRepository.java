package com.pricetracker.pricetracker.repository;

import java.time.LocalDateTime;
import java.util.List;
import com.pricetracker.pricetracker.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByUrl(String url);
	
	List<Product> findAllByLastUpdatedAfter(LocalDateTime lastNotified);
}
