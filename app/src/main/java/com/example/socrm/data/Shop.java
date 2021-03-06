package com.example.socrm.data;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Shop {
    public String shop_name;
    public String email;
    public String extensionAvatar;
    public String urlAvatar;

    public Shop() {
    }

    public Shop(String shop_name, String email, String extensionAvatar) {
        this.shop_name = shop_name;
        this.email = email;
        this.extensionAvatar = extensionAvatar;
    }
    public Shop(String shop_name, String email, String extensionAvatar, String urlAvatar) {
        this.shop_name = shop_name;
        this.email = email;
        this.extensionAvatar = extensionAvatar;
        this.urlAvatar = urlAvatar;
    }

    public String getShop_name() {
        return shop_name;
    }

    public String getEmail() {
        return email;
    }

    public String getExtensionAvatar() {
        return extensionAvatar;
    }

    public String getUrlAvatar() {
        return urlAvatar;
    }
}

