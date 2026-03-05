package com.orderservice.dto;

public class OrderResponseDTO {
    private Long id;
    private String productName;
    private int quantity;
    private double price;
    private String createdBy;

    public OrderResponseDTO() {}

    public OrderResponseDTO(Long id, String productName, int quantity, double price, String createdBy) {
        this.id = id;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.createdBy = createdBy;
    }

    public Long getId() { return id; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getCreatedBy() { return createdBy; }

    public void setId(Long id) { this.id = id; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
