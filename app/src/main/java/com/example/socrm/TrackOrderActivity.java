package com.example.socrm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socrm.API.TrackOrderAPIService;
import com.example.socrm.API.TrackOrderService;
import com.example.socrm.data.TrackOrder.Carrier;
import com.example.socrm.data.TrackOrder.TrackOrder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
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
    private TextView deliveringTimeTextView, weightTextView, estimatedDeliveryTextView, storageDateTextView, recipientTextView,
            addressFromTextView, destinationAddressTextView, senderTextView, typeTextView;
    private ImageView deliveryLogoImageView;
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

        deliveringTimeTextView = findViewById(R.id.deliveringTimeTextView);
        weightTextView = findViewById(R.id.weightTextView);
        estimatedDeliveryTextView = findViewById(R.id.estimatedDeliveryTextView);
        storageDateTextView = findViewById(R.id.storageDateTextView);
        recipientTextView = findViewById(R.id.recipientTextView);
        addressFromTextView = findViewById(R.id.addressFromTextView);
        destinationAddressTextView = findViewById(R.id.destinationAddressTextView);
        deliveryLogoImageView = findViewById(R.id.deliveryLogoImageView);
        senderTextView = findViewById(R.id.senderTextView);
        typeTextView = findViewById(R.id.typeTextView);

        Intent intent = getIntent();
        String deliveryCode = intent.getStringExtra("deliveryCode");
        String trackCode = intent.getStringExtra("trackCode");

        service = TrackOrderService.getTrackOrderService();
        service.getTrackOrder(deliveryCode, trackCode).enqueue(new Callback<TrackOrder>() {
            @Override
            public void onResponse(Call<TrackOrder> call, Response<TrackOrder> response) {
                TrackOrder trackOrder = response.body();
                deliveringTimeTextView.setText(trackOrder.getDeliveringTime().toString());
                weightTextView.setText(String.format("%.1f",trackOrder.getWeight()));
                estimatedDeliveryTextView.setText(trackOrder.getAttributes().getEstimatedDelivery());
                storageDateTextView.setText(trackOrder.getAttributes().getStorageDate());
                recipientTextView.setText(trackOrder.getAttributes().getRecipient());
                if (trackOrder.getAttributes().getAddressFrom() != null){
                    addressFromTextView.setText(trackOrder.getAttributes().getAddressFrom());
                } else {
                    addressFromTextView.setText(trackOrder.getEvents().get(trackOrder.getEvents().size()-1).getLocation());
                }
                if (trackOrder.getAttributes().getDestinationAddress() != null){
                    destinationAddressTextView.setText(trackOrder.getAttributes().getDestinationAddress());
                } else {
                    destinationAddressTextView.setText(trackOrder.getEvents().get(0).getLocation());
                }

                senderTextView.setText(trackOrder.getAttributes().getSender());
                typeTextView.setText(trackOrder.getAttributes().getType());
                if (trackOrder.getCarrier().getCode().equals("cdek"))
                    deliveryLogoImageView.setImageResource(R.drawable.ic_cdek_logo);
                else
                    deliveryLogoImageView.setImageResource(R.drawable.ic_pochta_russia_logo);
                RecyclerView recyclerView = findViewById(R.id.recyclerViewEventsTrackOrder);
                RecyclerView.Adapter adapter = new EventsTrackOrderRecyclerViewAdapter(trackOrder.getEvents(), getApplicationContext());
                RecyclerView.LayoutManager layoutManager =
                        new LinearLayoutManager(getApplicationContext());
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(layoutManager);
            }

            @Override
            public void onFailure(Call<TrackOrder> call, Throwable t) {
                Log.d("onResponse_getTrack", "onFailure");
            }
        });

    }
}