package com.example.socrm.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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


    protected Order(Parcel in) {
        address = in.readString();
        city = in.readString();
        date = in.readString();
        delivery = in.readString();
        email = in.readString();
        fio = in.readString();
        phone = in.readString();
        id = in.readString();
        status = in.readString();
        if (in.readByte() == 0x01) {
            products = new ArrayList<OrderComposition>();
            in.readList(products, OrderComposition.class.getClassLoader());
        } else {
            products = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(city);
        dest.writeString(date);
        dest.writeString(delivery);
        dest.writeString(email);
        dest.writeString(fio);
        dest.writeString(phone);
        dest.writeString(id);
        dest.writeString(status);
        if (products == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(products);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Order> CREATOR = new Parcelable.Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
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
}