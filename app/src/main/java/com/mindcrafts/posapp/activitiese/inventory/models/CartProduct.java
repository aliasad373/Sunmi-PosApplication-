package com.mindcrafts.posapp.activitiese.inventory.models;

public class CartProduct {
    private int id;
    private double price;
    private String name;

    private int count;

    // Constructor
    public CartProduct(int id, double price, String name) {
        this.id = id;
        this.price = price;
        this.name = name;
    }

    // Empty constructor (required for libraries like Gson, Firebase, etc.)
    public CartProduct() {
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Optional: toString() for easy logging
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", price=" + price +
                ", name='" + name + '\'' +
                '}';
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

