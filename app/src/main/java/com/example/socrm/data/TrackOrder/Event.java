package com.example.socrm.data.TrackOrder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Event {

    @SerializedName("position")
    @Expose
    private Integer position;
    @SerializedName("eventDate")
    @Expose
    private Long eventDate;
    @SerializedName("operation")
    @Expose
    private String operation;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("delivered")
    @Expose
    private Boolean delivered;
    @SerializedName("arrived")
    @Expose
    private Boolean arrived;

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Long getEventDate() {
        return eventDate;
    }

    public void setEventDate(Long eventDate) {
        this.eventDate = eventDate;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getDelivered() {
        return delivered;
    }

    public void setDelivered(Boolean delivered) {
        this.delivered = delivered;
    }

    public Boolean getArrived() {
        return arrived;
    }

    public void setArrived(Boolean arrived) {
        this.arrived = arrived;
    }

}