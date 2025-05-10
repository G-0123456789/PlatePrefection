package com.example.smartrestaurant.models;

import java.io.Serializable;

public class OrderItem implements Serializable {
    private String itemId;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private String specialInstructions;

    // Default constructor required for Firebase
    public OrderItem() {
    }

    public OrderItem(String itemId, String name, double price, int quantity) {
        this.itemId = itemId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public OrderItem(String itemId, String name, String description, double price, int quantity, String specialInstructions) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.specialInstructions = specialInstructions;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public double getSubtotal() {
        return price * quantity;
    }
}