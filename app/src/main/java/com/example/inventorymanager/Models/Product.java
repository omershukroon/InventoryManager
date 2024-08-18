package com.example.inventorymanager.Models;

import java.util.ArrayList;
import java.util.List;

public class Product {

    private String name = "";
    private String barcode = "";
    private List<String> images = new ArrayList<>();
    private int amount = 0;
    private double price = 0;
    private String productID;

    public Product(String name, String barcode, List<String> images, int amount, double price, String productID) {
        this.name = name;
        this.barcode = barcode;
        this.images = images;
        this.amount = amount;
        this.price = price;
        this.productID = productID;
    }

    public Product() {
    }

    public String getName() {
        return name;
    }

    public Product setName(String name) {
        this.name = name;
        return this;
    }

    public String getBarcode() {
        return barcode;
    }

    public Product setBarcode(String barcode) {
        this.barcode = barcode;
        return this;
    }

    public List<String> getImages() {
        return images;
    }

    public Product addImage(String imageSrc) {
        this.images.add(imageSrc);
        return this;
    }

    public Product removeImage(String imageSrc) {
        this.images.remove(imageSrc);
        return this;
    }

    public int getAmount() {
        return amount;
    }

    public Product setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public Product setPrice(double price) {
        this.price = price;
        return this;
    }

    public String getProductID() {
        return productID;
    }

    public Product setProductID(String productID) {
        this.productID = productID;
        return this;
    }
}
