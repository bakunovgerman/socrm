package com.example.socrm.data;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Shop {
    public String shop_name;
    public String email;

    public Shop() {
    }

    public Shop(String shop_name, String email) {
        this.shop_name = shop_name;
        this.email = email;
    }
}

