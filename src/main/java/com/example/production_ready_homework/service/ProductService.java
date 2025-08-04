package com.example.production_ready_homework.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.production_ready_homework.model.Product;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Service
public class ProductService {
    @Autowired
    private MeterRegistry registry;

    // TODO: Add a random chance to throw an error
    public Product createProduct(Product product) {
        Timer.Sample sample = Timer.start(registry);
        try {
            registry.counter("create_product_success").increment();
        } finally {
            sample.stop(registry.timer("create_product_finish_time"));
        }
        return product;
    }
}