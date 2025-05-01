package com.pricetracker.pricetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import com.pricetracker.pricetracker.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
@EnableScheduling
@EnableWebMvc
public class PricetrackerApplication implements CommandLineRunner {

	@Autowired
	private ProductService productService;

	public static void main(String[] args) {
		SpringApplication.run(PricetrackerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Load all products from JSON at startup
		productService.loadAllProducts();
	}
}
