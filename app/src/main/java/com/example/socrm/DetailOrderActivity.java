package com.example.socrm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DetailOrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order);

        TextView textView = findViewById(R.id.textView2);
        Intent intent = getIntent();
        String fio = intent.getStringExtra("fio");
        textView.setText(fio);
    }
}