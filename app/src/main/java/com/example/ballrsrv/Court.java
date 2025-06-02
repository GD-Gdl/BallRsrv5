package com.example.ballrsrv;

public class Court {
    private String id;
    private String name;
    private String location;
    private double price;
    private String imageBase64;
    private boolean available;

    public Court() {
        // Default constructor required for Firebase
    }

    public Court(String id, String name, String location, double price, String imageBase64, boolean available) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.price = price;
        this.imageBase64 = imageBase64;
        this.available = available;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
} 