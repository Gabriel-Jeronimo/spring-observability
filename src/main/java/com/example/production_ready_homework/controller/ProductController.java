package com.example.production_ready_homework.controller;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.production_ready_homework.dto.BuyProductsResponse;
import com.example.production_ready_homework.model.Product;
import com.example.production_ready_homework.service.ProductService;
import com.example.production_ready_homework.dto.BuyProductsRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController()
@RequestMapping("/api/products")
public class ProductController {
    ProductService productService;

    ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/")
    public ResponseEntity<Product> postMethodName(@RequestBody Product product) {
        Random gerador = new Random();
        Integer num = gerador.nextInt();

        if (num > 50) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        productService.createProduct(product);

        return new ResponseEntity<Product>(product, HttpStatus.CREATED);
    }

    @PostMapping("/buy-products")
    public ResponseEntity<java.util.List<BuyProductsResponse>> buyProducts(@RequestBody BuyProductsRequest request,
            @RequestHeader long clientId) {

        java.util.List<BuyProductsResponse> products = productService.buyProduct(request.products(), clientId);
        return ResponseEntity.ok(products);
    }
}
