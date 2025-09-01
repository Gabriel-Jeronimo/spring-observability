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
import com.example.production_ready_homework.model.Order;
import com.example.production_ready_homework.model.Product;
import com.example.production_ready_homework.repository.OrderRepository;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;

@Service
public class ProductService {
    @Autowired
    private MeterRegistry registry;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private Tracer tracer;

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private static final String APP_B_URL = "http://app-b:8089/api/validate-product";

    public List<BuyProductsResponse> buyProduct(List<Integer> ids, long clientId) {
        Span buyProductSpan = tracer.nextSpan().name("buy-products").start();
        try (Tracer.SpanInScope ws = tracer.withSpan(buyProductSpan)) {
            logger.info("Buy request from clientId = {}", clientId);
            buyProductSpan.tag("client.id", String.valueOf(clientId));
            buyProductSpan.tag("products.count", String.valueOf(ids.size()));

            List<BuyProductsResponse> response = new ArrayList<>();
            List<Long> products = new ArrayList<>();
            for (Integer id : ids) {
                Span validateProductSpan = tracer.nextSpan().name("validate-product-call").start();
                try (Tracer.SpanInScope ws2 = tracer.withSpan(validateProductSpan)) {
                    validateProductSpan.tag("product.id", String.valueOf(id));
                    logger.info("Searching in API by product = {}", id);
                    Map<String, Object> request = Map.of("id", id);
                    DummyJsonProductResponse product = restTemplate.postForObject(APP_B_URL, request,
                            DummyJsonProductResponse.class);
                    response.add(new BuyProductsResponse(product.id(), product.title()));
                    products.add(product.id());
                    validateProductSpan.tag("product.found", "true");
                } catch (HttpClientErrorException.NotFound ex) {
                    logger.info("Product with ID = {} doesnt exist", id);
                    validateProductSpan.tag("product.found", "false");
                } catch (Exception ex) {
                    logger.error("Something went wrong when fetching product", ex);
                    validateProductSpan.tag("error", ex.getMessage());
                } finally {
                    validateProductSpan.end();
                }
            }

            this.orderRepository.save(new Order(clientId, products));
            buyProductSpan.tag("order.saved", "true");
            return response;
        } finally {
            buyProductSpan.end();
        }
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