package com.example.appb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.LoggerFactory;

import java.util.Map;
import org.slf4j.Logger;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.example.appb.dto.DummyJsonProductResponse;

@RestController
@RequestMapping("/api")
public class ProductValidationController {
    @Autowired
    private RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ProductValidationController.class);

    @PostMapping("/validate-product")
    public ResponseEntity<Object> validateProduct(@RequestBody Map<String, Object> request) {
        Object idObj = request.get("id");
        logger.info("Searching product with ID = {}", idObj);

        if (idObj == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing product id"));
        }

        int delay = (int) (Math.random() * 2000) + 500;
        String url = "https://dummyjson.com/products/" + idObj + "?delay=" + delay;

        try {
            DummyJsonProductResponse product = restTemplate.getForObject(url, DummyJsonProductResponse.class);
            return ResponseEntity.ok(product);
            } catch (HttpClientErrorException.NotFound ex) {
                logger.warn("Product not found in API: {}", ex.getMessage());
                return ResponseEntity.status(404).body(Map.of(
                    "error", "Product not found",
                    "details", ex.getMessage()
                ));
            } catch (Exception ex) {
                logger.error("Error fetching product from API: {}", ex.getMessage());
                return ResponseEntity.status(502).body(Map.of(
                    "error", "Failed to fetch product from external API",
                    "details", ex.getMessage()
                ));
        }
    }
}
