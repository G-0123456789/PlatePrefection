package com.example.smartrestaurant.models;

import java.io.Serializable;
import java.util.Date;

public class InventoryItem implements Serializable {
    private String itemId;
    private String name;
    private String category;
    private double quantity;
    private String unit;
    private double unitPrice;
    private double minimumLevel;
    private Date lastUpdated;
    private String supplierInfo;

    // Default constructor required for Firebase
    public InventoryItem() {
        this.lastUpdated = new Date();
    }

    public InventoryItem(String itemId, String name, String category, double quantity,
                         String unit, double unitPrice, double minimumLevel, String supplierInfo) {
        this.itemId = itemId;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.unit = unit;
        this.unitPrice = unitPrice;
        this.minimumLevel = minimumLevel;
        this.lastUpdated = new Date();
        this.supplierInfo = supplierInfo;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
        this.lastUpdated = new Date();
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getMinimumLevel() {
        return minimumLevel;
    }

    public void setMinimumLevel(double minimumLevel) {
        this.minimumLevel = minimumLevel;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getSupplierInfo() {
        return supplierInfo;
    }

    public void setSupplierInfo(String supplierInfo) {
        this.supplierInfo = supplierInfo;
    }

    public boolean isLowStock() {
        return quantity <= minimumLevel;
    }

    public double getTotalValue() {
        return quantity * unitPrice;
    }
}