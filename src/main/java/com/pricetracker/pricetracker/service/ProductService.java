package com.pricetracker.pricetracker.service;

import com.pricetracker.pricetracker.model.Product;
import com.pricetracker.pricetracker.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    
    private final RestTemplate restTemplate;
    
    private final ObjectMapper objectMapper;
    
    public void loadAllProducts() {
        try {
            // Read the static JSON file
            ClassPathResource resource = new ClassPathResource("static/products.json");
            JsonNode root = objectMapper.readTree(resource.getInputStream());
            JsonNode products = root.get("products");
            
            // Load all products from JSON
            for (JsonNode productNode : products) {
                String url = productNode.get("url").asText();

                if (productRepository.findByUrl(url) == null) {
                    Product product = new Product();
                    product.setUrl(url);
                    product.setName(productNode.get("name").asText());
                    product.setCurrentPrice(productNode.get("currentPrice").asDouble());  
                    productRepository.save(product);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading products from JSON", e);
        }
    }
    
    public Product getProductByUrl(String url) {
        Product product = productRepository.findByUrl(url);
        if (product == null) {
            throw new RuntimeException("Product not found with URL: " + url);
        }
        return product;
    }
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public Optional<Product> findProductByUrl(String url) {
        return Optional.ofNullable(productRepository.findByUrl(url));
    }
    
    public void updateProductPrice(String productUrl, Double newPrice) {
        Product product = getProductByUrl(productUrl);
        product.setCurrentPrice(newPrice);
        productRepository.save(product);
    }
    
    public Map<String, Object> updateProductPriceAndReturnResponse(String url, Double price) {
        double roundedPrice = Math.round(price * 100.0) / 100.0;
        updateProductPrice(url, roundedPrice);
        Product updatedProduct = getProductByUrl(url);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Price updated successfully");
        response.put("product", updatedProduct);
        return response;
    }
	
	public List<Product> getAllProductsWhereLastUpdatedIsAfter(LocalDateTime lastNotified) {
        //Inclusive of never updated
        return productRepository.findAllByLastUpdatedAfter(lastNotified);
	}
}
