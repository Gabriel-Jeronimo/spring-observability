package com.example.appb.dto;

import java.util.List;

public record DummyJsonProductResponse(
    Long id,
    String title,
    String description,
    String category,
    Double price,
    Double discountPercentage,
    Double rating,
    Integer stock,
    List<String> tags,
    String brand,
    String sku,
    Integer weight,
    Dimensions dimensions,
    String warrantyInformation,
    String shippingInformation,
    String availabilityStatus,
    List<Review> reviews,
    String returnPolicy,
    Integer minimumOrderQuantity,
    Meta meta,
    List<String> images,
    String thumbnail
) {
    public static record Dimensions(Double width, Double height, Double depth) {}
    public static record Review(Integer rating, String comment, String date, String reviewerName, String reviewerEmail) {}
    public static record Meta(String createdAt, String updatedAt, String barcode, String qrCode) {}
}
