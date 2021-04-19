package com.example.socrm.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Order implements Parcelable {
    public String address;
    public String city;
    public String count_product;
    public String date;
    public String delivery;
    public String email;
    public String fio;
    public String phone;
    public String product;
    public String id;
    public String status;

    public Order() {
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel source) {
            String address = source.readString();
            String city = source.readString();
            String count_product = source.readString();
            String date = source.readString();
            String delivery = source.readString();
            String email = source.readString();
            String status = source.readString();
            String fio = source.readString();
            String phone = source.readString();
            String product = source.readString();
            String id = source.readString();
            return new Order(address,  city,  count_product,  date,  delivery,
                     email,  fio,  phone,  product,  status);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public Order(String address, String city, String count_product, String date, String delivery,
                 String email, String fio, String phone, String product, String status) {
        this.address = address;
        this.city = city;
        this.count_product = count_product;
        this.date = date;
        this.delivery = delivery;
        this.email = email;
        this.fio = fio;
        this.phone = phone;
        this.product = product;
        this.status = status;
    }

    public String getId() {
        return id;
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

    public String getCount_product() {
        return count_product;
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

    public String getProduct() {
        return product;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(city);
        dest.writeString(count_product);
        dest.writeString(date);
        dest.writeString(delivery);
        dest.writeString(email);
        dest.writeString(fio);
        dest.writeString(phone);
        dest.writeString(product);
        dest.writeString(status);
        dest.writeString(id);
    }
}
