package com.example.production_ready_homework.service;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.example.production_ready_homework.model.Product;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Service
public class ProductService {
    @Autowired
    private MeterRegistry registry;
    private static final Logger logger 
      = LoggerFactory.getLogger(ProductService.class);

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