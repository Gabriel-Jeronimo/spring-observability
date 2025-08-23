package com.example.production_ready_homework.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;

    @ElementCollection
    private List<Long> products = new ArrayList<>();

    protected Order() {
    }

    public Order(Long userId, List<Long> products) {
        this.userId = userId;
        this.products = products != null ? new ArrayList<>(products) : new ArrayList<>();
    }

}