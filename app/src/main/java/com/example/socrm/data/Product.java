package com.example.socrm.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties

public class Product implements Parcelable {
    public String name;
    public int price;
    public float sale;
    public String url;
    public String id;

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            String name = source.readString();
            int price = source.readInt();
            float sale = source.readFloat();
            String url = source.readString();
            String id = source.readString();

            return new Product(name, price,sale, url, id);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public Product() {
    }

    public Product(String name, int price, float sale, String url) {
        this.name = name;
        this.price = price;
        this.sale = sale;
        this.url = url;
    }

    public Product(String name, int price, float sale, String url, String id) {
        this.name = name;
        this.price = price;
        this.sale = sale;
        this.url = url;
        this.id = id;
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

    public String getId() { return id; }

    @Override
    public int describeContents() { return 0; }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("price", price);
        result.put("sale", sale);
        result.put("url", url);

        return result;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(price);
        dest.writeFloat(sale);
        dest.writeString(url);
        dest.writeString(id);
    }
}
