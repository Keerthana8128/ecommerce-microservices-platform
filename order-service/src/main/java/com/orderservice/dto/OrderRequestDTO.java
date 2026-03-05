package com.orderservice.dto;

public class OrderRequestDTO {
    private String productName;
    private int quantity;
    private double price;

    public OrderRequestDTO() {}

    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }

    public void setProductName(String productName) { this.productName = productName; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }
}
