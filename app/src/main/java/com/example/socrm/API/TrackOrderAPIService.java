package com.example.socrm.API;

import com.example.socrm.data.TrackOrder.Carrier;
import com.example.socrm.data.TrackOrder.TrackOrder;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TrackOrderAPIService {
    @GET("carriers/{barcode}")
    Call<List<Carrier>> getCarrier(@Path("barcode") String barcode);

    @GET("trackers/{carrier}/{barcode}")
    Call<TrackOrder> getTrackOrder(@Path("carrier") String carrier, @Path("barcode") String barcode);

    @POST("trackers/{carrier}/{barcode}")
    Call<AddTrackCode> postTrack(@Path("carrier") String carrier, @Path("barcode")
            String barcode, @Query("api_key") String API_KEY);
}
