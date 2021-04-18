package com.example.socrm.data;

public class Order {
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

    public Order() {
    }

    public Order(String address, String city, String count_product, String date, String delivery,
                 String email, String fio, String phone, String product) {
        this.address = address;
        this.city = city;
        this.count_product = count_product;
        this.date = date;
        this.delivery = delivery;
        this.email = email;
        this.fio = fio;
        this.phone = phone;
        this.product = product;
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
}
