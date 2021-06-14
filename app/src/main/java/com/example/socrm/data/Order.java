package com.example.socrm.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class Order implements Parcelable {
    public String address;
    public String city;
    public String date;
    public String delivery;
    public String email;
    public String fio;
    public String phone;
    public String id;
    public String status;
    public ArrayList<OrderComposition> products;

    public Order() {
    }

    public Order(String address, String city, String date, String delivery,
                 String email, String fio, String phone, String status, ArrayList<OrderComposition> products) {
        this.address = address;
        this.city = city;
        this.date = date;
        this.delivery = delivery;
        this.email = email;
        this.fio = fio;
        this.phone = phone;
        this.status = status;
        this.products = products;
    }
    public Order(String address, String city, String date, String delivery,
                 String email, String fio, String phone, String status, ArrayList<OrderComposition> products, String id) {
        this.address = address;
        this.city = city;
        this.date = date;
        this.delivery = delivery;
        this.email = email;
        this.fio = fio;
        this.phone = phone;
        this.status = status;
        this.products = products;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public ArrayList<OrderComposition> getProducts() {
        return products;
    }

    public String getStatus() {
        return status;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getDate() {
        return date;
    }

    public String getDelivery() {
        return delivery;
    }

    public String getEmail() {
        return email;
    }

    public String getFio() {
        return fio;
    }

    public String getPhone() {
        return phone;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("address", address);
        result.put("city", city);
        result.put("date", date);
        result.put("delivery", delivery);
        result.put("email", email);
        result.put("fio", fio);
        result.put("phone", phone);
        result.put("status", status);
        result.put("products", products);

        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.address);
        dest.writeString(this.city);
        dest.writeString(this.date);
        dest.writeString(this.delivery);
        dest.writeString(this.email);
        dest.writeString(this.fio);
        dest.writeString(this.phone);
        dest.writeString(this.id);
        dest.writeString(this.status);
        dest.writeList(this.products);
    }

    protected Order(Parcel in) {
        this.address = in.readString();
        this.city = in.readString();
        this.date = in.readString();
        this.delivery = in.readString();
        this.email = in.readString();
        this.fio = in.readString();
        this.phone = in.readString();
        this.id = in.readString();
        this.status = in.readString();
        this.products = new ArrayList<OrderComposition>();
        in.readList(this.products, OrderComposition.class.getClassLoader());
    }

    public static final Parcelable.Creator<Order> CREATOR = new Parcelable.Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel source) {
            return new Order(source);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
}