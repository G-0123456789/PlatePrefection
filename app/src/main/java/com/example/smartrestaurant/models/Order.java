package com.example.smartrestaurant.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Order implements Serializable {
    public enum Status {
        RECEIVED,
        PREPARING,
        READY,
        SERVED,
        COMPLETED,
        CANCELLED
    }

    private String orderId;
    private String tableNumber;
    private List<OrderItem> items;
    private String waiterId;
    private String notes;
    private Status status;
    private Date createdAt;
    private Date updatedAt;
    private double totalAmount;

    // Default constructor required for Firebase
    public Order() {
        this.orderId = generateOrderId();
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.status = Status.RECEIVED;
    }

    public Order(String tableNumber, List<OrderItem> items, String waiterId, String notes) {
        this.orderId = generateOrderId();
        this.tableNumber = tableNumber;
        this.items = items;
        this.waiterId = waiterId;
        this.notes = notes;
        this.status = Status.RECEIVED;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        calculateTotalAmount();
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
        calculateTotalAmount();
    }

    public String getWaiterId() {
        return waiterId;
    }

    public void setWaiterId(String waiterId) {
        this.waiterId = waiterId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        this.updatedAt = new Date();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    private void calculateTotalAmount() {
        this.totalAmount = 0;
        if (items != null) {
            for (OrderItem item : items) {
                this.totalAmount += item.getPrice() * item.getQuantity();
            }
        }
    }

    // Generate a short numeric order ID based on timestamp and random digits
    private String generateOrderId() {
        // Last 5 digits of current time in milliseconds
        long timestamp = System.currentTimeMillis() % 100000;
        // 3 random digits for uniqueness
        int random = new Random().nextInt(1000);
        // Combine for an 8-digit order ID
        return String.format("%05d%03d", timestamp, random);
    }
}