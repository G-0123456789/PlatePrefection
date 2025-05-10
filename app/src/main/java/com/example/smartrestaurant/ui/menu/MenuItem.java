package com.example.smartrestaurant.ui.menu;

import java.io.Serializable;

public class MenuItem implements Serializable {
    private String id;
    private String name;
    private String description;
    private double price;
    private String category;
    private String imageUrl;

    // No-argument constructor required for Firebase
    public MenuItem() {
    }

    public MenuItem(String id, String name, String description, double price, String category) {
        this(id, name, description, price, category, "");
    }

    public MenuItem(String id, String name, String description, double price, String category, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}