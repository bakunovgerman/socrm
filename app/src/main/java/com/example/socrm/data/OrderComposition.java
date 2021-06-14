package com.example.socrm.data;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties

public class OrderComposition implements Serializable {
    public String count;
    public String id_product;
    public String price_product;

    public OrderComposition() {
    }

    public OrderComposition(String count, String id_product, String price_product) {
        this.count = count;
        this.id_product = id_product;
        this.price_product = price_product;
    }

    public String getCount() {
        return count;
    }

    public String getId_product() {
        return id_product;
    }

    public String getPrice_product() {
        return price_product;
    }
}
