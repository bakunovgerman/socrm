package com.example.socrm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.socrm.API.TrackOrderAPIService;
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
    private List<Carrier> carriers;
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
        carriers = new ArrayList<>();
        Intent intent = getIntent();
        String trackCode = intent.getStringExtra("trackCode");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://moyaposylka.ru/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TrackOrderAPIService service = retrofit.create(TrackOrderAPIService.class);
        service.getTrackOrder("cdek", trackCode).enqueue(new Callback<TrackOrder>() {
            @Override
            public void onResponse(Call<TrackOrder> call, Response<TrackOrder> response) {
                TrackOrder trackOrder = response.body();
            }

            @Override
            public void onFailure(Call<TrackOrder> call, Throwable t) {
                Log.d("onResponse_getTrack", "onFailure");
            }
        });
//        service.getCarrier(trackCode).enqueue(new Callback<List<Carrier>>() {
//            @Override
//            public void onResponse(Call<List<Carrier>> call, Response<List<Carrier>> response) {
//                carriers.addAll(response.body());
//                getTrackOrder(carriers, trackCode, service);
//            }
//            @Override
//            public void onFailure(Call<List<Carrier>> call, Throwable t) {
//                Log.d("onResponse_log", "onFailure");
//            }
//        });

    }
    public void getTrackOrder(List<Carrier> carriers, String trackCode, TrackOrderAPIService service){
//        if (carriers.size()>0){
//            Toast.makeText(getApplicationContext(), carriers.get(0).getCode(), Toast.LENGTH_LONG).show();
//            service.getTrackOrder(carriers.get(0).getCode(), trackCode).enqueue(new Callback<List<TrackOrder>>() {
//                @Override
//                public void onResponse(Call<List<TrackOrder>> call, Response<List<TrackOrder>> response) {
//                    trackOrders.addAll(response.body());
//                }
//
//                @Override
//                public void onFailure(Call<List<TrackOrder>> call, Throwable t) {
//                    Log.d("onResponse_getTrack", "onFailure");
//                }
//            });
//        } else {
//            Toast.makeText(getApplicationContext(), "пиздец", Toast.LENGTH_LONG).show();
//        }

    }
}