package com.example.socrm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.socrm.API.TrackOrderAPIService;
import com.example.socrm.API.TrackOrderService;
import com.example.socrm.data.TrackOrder.Carrier;
import com.example.socrm.data.TrackOrder.TrackOrder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TrackOrderActivity extends AppCompatActivity {
    private List<TrackOrder> trackOrders;
    private TrackOrderAPIService service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);

        // установка toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // кнопка назад в toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        String deliveryCode = intent.getStringExtra("deliveryCode");
        String trackCode = intent.getStringExtra("trackCode");

        service = TrackOrderService.getTrackOrderService();
        service.getTrackOrder(deliveryCode, trackCode).enqueue(new Callback<TrackOrder>() {
            @Override
            public void onResponse(Call<TrackOrder> call, Response<TrackOrder> response) {
                TrackOrder trackOrder = response.body();
            }

            @Override
            public void onFailure(Call<TrackOrder> call, Throwable t) {
                Log.d("onResponse_getTrack", "onFailure");
            }
        });

    }
}