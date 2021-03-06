package com.example.socrm.data.TrackOrder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attributes {

    @SerializedName("addressFrom")
    @Expose
    private String addressFrom;
    @SerializedName("estimatedDelivery")
    @Expose
    private String estimatedDelivery;
    @SerializedName("storageDate")
    @Expose
    private String storageDate;
    @SerializedName("recipient")
    @Expose
    private String recipient;
    @SerializedName("destinationAddress")
    @Expose
    private String destinationAddress;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("sender")
    @Expose
    private String sender;

    public String getAddressFrom() {
        return addressFrom;
    }

    public void setAddressFrom(String addressFrom) {
        this.addressFrom = addressFrom;
    }

    public String getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public void setEstimatedDelivery(String estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
    }

    public String getStorageDate() {
        return storageDate;
    }

    public void setStorageDate(String storageDate) {
        this.storageDate = storageDate;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getType() {
        return type;
    }

    public String getSender() {
        return sender;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
