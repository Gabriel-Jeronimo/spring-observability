package com.example.production_ready_homework.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.production_ready_homework.dto.BuyProductsResponse;
import com.example.production_ready_homework.dto.DummyJsonProductResponse;
import com.example.production_ready_homework.model.Product;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Service
public class ProductService {
    @Autowired
    private MeterRegistry registry;
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String APP_B_URL = "http://localhost:8089/api/validate-product";

    public List<BuyProductsResponse> buyProduct(List<Integer> ids, long clientId) {
        logger.info("Buy request from clientId = {}", clientId);

        List<BuyProductsResponse> products = new ArrayList<>();
        for (Integer id : ids) {
            try {
                logger.info("Searching in API by product = {}", id);
                Map<String, Object> request = Map.of("id", id);
                DummyJsonProductResponse product = restTemplate.postForObject(APP_B_URL, request, DummyJsonProductResponse.class);
                products.add(new BuyProductsResponse(product.id(), product.title()));
            } catch(HttpClientErrorException.NotFound ex) {
                logger.info("Product with ID = {} doesnt exist", id);
            }
             catch (Exception ex) {
                logger.error("Something went wrong when fetching product", ex);
            }
        }

        return products;
    }

    public Product createProduct(Product product) {

        Timer.Sample sample = Timer.start(registry);
        try {
            logger.info("Product created with ID = {}", product.id());
            registry.counter("create_product_success").increment();
        } finally {
            sample.stop(registry.timer("create_product_finish_time"));
        }
        return product;
    }
}