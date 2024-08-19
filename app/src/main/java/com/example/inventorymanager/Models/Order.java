package com.example.inventorymanager.Models;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderNumber;
    private ArrayList<Product> productList;
    private ArrayList<Integer> productQuantityList;
    private Boolean isFulfilled;
    private double orderAmount;
    private String orderID;

    // Static variable to keep track of the last order number
    private static int lastOrderNumber = 0;

    public Order() {
    }

    // Constructor
    public Order(ArrayList<Product> productList, ArrayList<Integer> productQuantityList, boolean isFulfilled, double orderAmount, String orderID) {
        this.orderNumber = ++lastOrderNumber;  // Increment and assign order number
        this.productQuantityList = productQuantityList;
        this.productList = productList;
        this.isFulfilled = isFulfilled;
        this.orderAmount = orderAmount;
        this.orderID = orderID;
    }

    public int checkIfProductInOrder(Product product) {
        for (int i = 0; i < productList.size(); i++) {
            if (product.getBarcode() == productList.get(i).getBarcode()) {
                return i;
            }
        }
        return -1;
    }

    public void addProductToOrder(Product product) {
        int productIndex = checkIfProductInOrder(product);
        if (productIndex >= 0) {
            productQuantityList.set(productIndex, productQuantityList.get(productIndex) + 1);
        } else {
            productList.add(product);
            productQuantityList.add(1);
        }
    }

    public void removeProductToOrder(Product product) {
        productList.remove(product);
    }

    // Getters and Setters


    public String getOrderID() {
        return orderID;
    }

    public Order setOrderID(String orderID) {
        this.orderID = orderID;
        return this;
    }

    public ArrayList<Integer> getProductQuantityList() {
        return productQuantityList;
    }

    public Order setProductQuantityList(ArrayList<Integer> productQuantityList) {
        this.productQuantityList = productQuantityList;
        return this;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(ArrayList<Product> productList) {
        this.productList = productList;
    }

    public Boolean isFulfilled() {
        return isFulfilled;
    }

    public void setFulfilled(boolean fulfilled) {
        isFulfilled = fulfilled;
    }

    public double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(double orderAmount) {
        this.orderAmount = orderAmount;
    }
}

