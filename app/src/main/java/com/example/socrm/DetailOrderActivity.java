package com.example.socrm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.example.socrm.data.Order;
import com.google.android.material.textfield.TextInputLayout;

public class DetailOrderActivity extends AppCompatActivity {

    private AutoCompleteTextView deliveryItem;

    @Override
    protected void onResume() {
        super.onResume();
        String[] delivery_array = getResources().getStringArray(R.array.delivery_input);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item, delivery_array);
        deliveryItem = findViewById(R.id.deliveryItem);
        deliveryItem.setAdapter(arrayAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order);

        Toolbar toolbar = findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);




        Bundle arguments = getIntent().getExtras();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Order order;
        if(arguments!=null){
            order = arguments.getParcelable(Order.class.getSimpleName());
        }
    }


}