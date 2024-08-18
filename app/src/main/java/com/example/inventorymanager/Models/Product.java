package com.example.inventorymanager.Models;

import java.util.ArrayList;

public class Product {

    private String name = "";
    private String barcode = "";
    private String imageSrc = "";
    private int amount = 0;
    private double price = 0;
    private String productID;


    public Product(String name, String barcode, String imageId, int amount, double price,String productID) {
        this.name = name;
        this.barcode = barcode;
        this.imageSrc=imageId;
        this.amount = amount;
        this.price = price;
        this.productID = productID;
    }

    public Product() {
    }

//    public void addImgId(String ImgId) {
//        imageSrc.add(ImgId);
//    }
//
//    public void removeImgId(String ImgId) {
//        imageSrc.remove(ImgId);
//    }


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

    public String getImageSrc() {
        return imageSrc;
    }

    public Product setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
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

    public Product setPrice(int price) {
        this.price = price;
        return this;
    }
}
