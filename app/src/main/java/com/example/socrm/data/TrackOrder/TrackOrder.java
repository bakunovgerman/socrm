package com.example.socrm.data.TrackOrder;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrackOrder {

    @SerializedName("barcode")
    @Expose
    private String barcode;
    @SerializedName("carrier")
    @Expose
    private Carrier carrier;
    @SerializedName("originCountry")
    @Expose
    private OriginCountry originCountry;
    @SerializedName("attributes")
    @Expose
    private Attributes attributes;
    @SerializedName("createdAt")
    @Expose
    private Long createdAt;
    @SerializedName("updatedAt")
    @Expose
    private Long updatedAt;
    @SerializedName("deliveringTime")
    @Expose
    private Integer deliveringTime;
    @SerializedName("weight")
    @Expose
    private Double weight;
    @SerializedName("delivered")
    @Expose
    private Boolean delivered;
    @SerializedName("events")
    @Expose
    private ArrayList<Event> events = null;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Carrier getCarrier() {
        return carrier;
    }

    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    public OriginCountry getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(OriginCountry originCountry) {
        this.originCountry = originCountry;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getDeliveringTime() {
        return deliveringTime;
    }

    public void setDeliveringTime(Integer deliveringTime) {
        this.deliveringTime = deliveringTime;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Boolean getDelivered() {
        return delivered;
    }

    public void setDelivered(Boolean delivered) {
        this.delivered = delivered;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

}