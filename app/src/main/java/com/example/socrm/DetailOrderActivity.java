package com.example.socrm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.socrm.data.Order;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.io.Console;
import java.util.HashMap;
import java.util.Map;

public class DetailOrderActivity extends AppCompatActivity {

    private AutoCompleteTextView deliveryItem, statusItem;
    private TextInputLayout fioTextInputLayout, phoneTextInputLayout, emailTextInputLayout,
    cityTextInputLayout, addressTextInputLayout, deliveryTextInputLayout,
    productTextInputLayout, countProductTextInputLayout, statusTextInputLayout;
    private TextView dateTextView;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private Order order;

    // установка drop_menu input
    @Override
    protected void onResume() {
        super.onResume();
        String[] delivery_array = getResources().getStringArray(R.array.delivery_input);
        String[] status_array = getResources().getStringArray(R.array.status_input);
        ArrayAdapter<String> arrayAdapterDelivery = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item, delivery_array);
        ArrayAdapter<String> arrayAdapterStatus = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item, status_array);
        deliveryItem = findViewById(R.id.deliveryItem);
        statusItem = findViewById(R.id.statusItem);
        deliveryItem.setAdapter(arrayAdapterDelivery);
        statusItem.setAdapter(arrayAdapterStatus);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order);
        // установка toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // создаем ссылку на БД
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // получаем залогиненного пользователя
        user = FirebaseAuth.getInstance().getCurrentUser();
        // инициализируем компоненты activity
        fioTextInputLayout = findViewById(R.id.fioTextInputLayout);
        phoneTextInputLayout = findViewById(R.id.phoneTextInputLayout);
        emailTextInputLayout = findViewById(R.id.emailTextInputLayout);
        cityTextInputLayout = findViewById(R.id.cityTextInputLayout);
        addressTextInputLayout = findViewById(R.id.addressTextInputLayout);
        deliveryTextInputLayout = findViewById(R.id.deliveryInput);
        productTextInputLayout = findViewById(R.id.productTextInputLayout);
        countProductTextInputLayout = findViewById(R.id.countProductTextInputLayout);
        dateTextView = findViewById(R.id.orderDateTime);
        statusTextInputLayout = findViewById(R.id.statusInput);
        // кнопка назад в toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // получаем переданный intent из родительской activity
        Bundle arguments = getIntent().getExtras();
        // получаем объект order из intent
        order = arguments.getParcelable(Order.class.getSimpleName());

        fioTextInputLayout.getEditText().setText(order.getFio());
        phoneTextInputLayout.getEditText().setText(order.getPhone());
        emailTextInputLayout.getEditText().setText(order.getEmail());
        cityTextInputLayout.getEditText().setText(order.getCity());
        addressTextInputLayout.getEditText().setText(order.getAddress());
        deliveryTextInputLayout.getEditText().setText(order.getDelivery());
        productTextInputLayout.getEditText().setText(order.getProduct());
        countProductTextInputLayout.getEditText().setText(order.getCount_product());
        dateTextView.setText(order.getDate());
        statusTextInputLayout.getEditText().setText(order.getStatus());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_detail_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.save){
            Order orderUpdate = new Order(addressTextInputLayout.getEditText().getText().toString(), cityTextInputLayout.getEditText().getText().toString(),
                    countProductTextInputLayout.getEditText().getText().toString(), dateTextView.getText().toString(),
                    deliveryTextInputLayout.getEditText().getText().toString(), emailTextInputLayout.getEditText().getText().toString(),
                    fioTextInputLayout.getEditText().getText().toString(), phoneTextInputLayout.getEditText().getText().toString(),
                    productTextInputLayout.getEditText().getText().toString(), statusTextInputLayout.getEditText().getText().toString());
            Map<String, Object> orderValues = orderUpdate.toMap();

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("orders/" + user.getUid() + "/" + order.getId(), orderValues);

            mDatabase.updateChildren(childUpdates);

            onBackPressed();

        }
        return true;
    }
}