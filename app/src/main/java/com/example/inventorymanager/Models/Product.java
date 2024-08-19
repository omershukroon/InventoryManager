package com.example.inventorymanager.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Product implements Serializable {
    private String name = "";
    private String barcode = "";
    private List<String> images = new ArrayList<>();
    private int amount = 0;
    private double price = 0;
    private String productID;

    public Product() {
    }

    public Product(String name, String barcode, List<String> images, int amount, double price, String productID) {
        this.name = name;
        this.barcode = barcode;
        this.images = images;
        this.amount = amount;
        this.price = price;
        this.productID = productID;
    }

    // Getters and setters...

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }
}
