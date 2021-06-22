package com.example.socrm.API;

public class TrackOrderService {
    private static final String BASE_URL = "https://moyaposylka.ru/api/v1/";
    public static TrackOrderAPIService getTrackOrderService() {
        return RetrofitClient.getClient(BASE_URL).create(TrackOrderAPIService.class);
    }
}
