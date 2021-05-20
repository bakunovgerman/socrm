package com.example.socrm.data;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Product {
    public String name;
    public int price;
    public float sale;
    public String url;

    public Product() {
    }

    public Product(String name, int price, float sale, String url) {
        this.name = name;
        this.price = price;
        this.sale = sale;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public float getSale() {
        return sale;
    }

    public String getUrl() {
        return url;
    }
}
