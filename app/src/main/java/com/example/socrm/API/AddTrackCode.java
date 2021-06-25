package com.example.socrm.API;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddTrackCode {
    @SerializedName("result")
    @Expose
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
