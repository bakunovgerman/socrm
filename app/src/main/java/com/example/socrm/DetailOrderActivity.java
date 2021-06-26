package com.example.socrm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socrm.API.AddTrackCode;
import com.example.socrm.API.TrackOrderAPIService;
import com.example.socrm.API.TrackOrderService;
import com.example.socrm.data.Order;
import com.example.socrm.data.OrderComposition;
import com.example.socrm.data.Product;
import com.example.socrm.data.TrackOrder.Carrier;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.w3c.dom.Text;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailOrderActivity extends AppCompatActivity {

    private AutoCompleteTextView deliveryItem, statusItem;
    private TextInputLayout fioTextInputLayout, phoneTextInputLayout, emailTextInputLayout,
    cityTextInputLayout, addressTextInputLayout, deliveryTextInputLayout,statusTextInputLayout,
            sumTextInputLayout, trackCodeTextInputLayout;
    private MaterialButton trackOrderMaterialButton;
    private TextView dateTextView;
    private ExpandableLayout trackCodeExpandableLayout, trackOrderExpandableLayout;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private Order order;
    private ArrayList<OrderComposition> products;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private String uid;
    private int sum = 0;
    private TrackOrderAPIService trackOrderAPIService;
    private List<Carrier> carriers;
    private Response<AddTrackCode> response;
    public static final String API_KEY = "88f5707236c5aa4bc83e6d4070301146";
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
        if (user != null) {
            // получаем инфу о пользователе чтобы потом вывести в фрагменте для профиля
            uid = user.getUid();
        }else{
            Log.d("photoUser", "user is null");
        }
        // инициализируем компоненты activity
        fioTextInputLayout = findViewById(R.id.fioTextInputLayout);
        phoneTextInputLayout = findViewById(R.id.phoneTextInputLayout);
        emailTextInputLayout = findViewById(R.id.emailTextInputLayout);
        cityTextInputLayout = findViewById(R.id.cityTextInputLayout);
        addressTextInputLayout = findViewById(R.id.addressTextInputLayout);
        deliveryTextInputLayout = findViewById(R.id.deliveryInput);
        dateTextView = findViewById(R.id.orderDateTime);
        statusTextInputLayout = findViewById(R.id.statusInput);
        sumTextInputLayout = findViewById(R.id.sumTextInputLayout);
        recyclerView = findViewById(R.id.recyclerViewProductsComposition);
        trackCodeExpandableLayout = findViewById(R.id.trackCode_expandable_layout);
        trackOrderExpandableLayout = findViewById(R.id.trackOrder_expandable_layout);
        trackCodeTextInputLayout = findViewById(R.id.trackCodeTextInputLayout);
        trackOrderMaterialButton = findViewById(R.id.trackOrderMaterialButton);
        carriers = new ArrayList<>();
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
        products = order.getProducts();

        fioTextInputLayout.getEditText().setText(order.getFio());
        phoneTextInputLayout.getEditText().setText(order.getPhone());
        emailTextInputLayout.getEditText().setText(order.getEmail());
        cityTextInputLayout.getEditText().setText(order.getCity());
        addressTextInputLayout.getEditText().setText(order.getAddress());
        deliveryTextInputLayout.getEditText().setText(order.getDelivery());
        dateTextView.setText(order.getDate());
        statusTextInputLayout.getEditText().setText(order.getStatus());

        if (!order.getTrackCode().equals("null")){
            trackCodeTextInputLayout.getEditText().setText(order.getTrackCode());
            trackCodeExpandableLayout.expand();
            if (order.getTrackCode().length() >= 7)
                trackOrderExpandableLayout.expand();
        }

        trackCodeTextInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()>=7)
                    trackOrderExpandableLayout.expand();
                else{
                    trackOrderExpandableLayout.collapse();
                    trackCodeTextInputLayout.getEditText().setError("Трек-код должен содержать хотя-бы 7 символов!");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        adapter = new ProductOrderCompositionRecyclerViewAdapter(products, getApplicationContext(), mDatabase, uid);
        layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        for (OrderComposition orderComposition: products){
            sum += Integer.parseInt(orderComposition.getPrice_product()) * Integer.parseInt(orderComposition.count);
        }
        sumTextInputLayout.getEditText().setText(String.valueOf(sum) + "₽");
        statusItem = findViewById(R.id.statusItem);
        statusItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 3)
                    trackCodeExpandableLayout.expand();
                else{
                    trackCodeExpandableLayout.collapse();
                    trackOrderExpandableLayout.collapse();
                    trackCodeTextInputLayout.getEditText().setText("");
                    trackCodeTextInputLayout.getEditText().setError(null);
                }

            }
        });
        trackOrderMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackOrderAPIService = TrackOrderService.getTrackOrderService();
                Runnable runnable = new Runnable(){
                    public void run() {
                        try {
                            carriers = new ArrayList<>();
                            carriers.addAll(trackOrderAPIService.getCarrier(trackCodeTextInputLayout.getEditText().getText().toString()).execute().body());
                            response = trackOrderAPIService.postTrack(carriers.get(0).getCode(),
                                    trackCodeTextInputLayout.getEditText().getText().toString(), API_KEY)
                                    .execute();
                            if (!response.isSuccessful()){
                                Log.d("response.message", response.message());
                            }
                            Intent intent = new Intent(getApplicationContext(), TrackOrderActivity.class);
                            intent.putExtra("deliveryCode", carriers.get(0).getCode());
                            intent.putExtra("trackCode", trackCodeTextInputLayout.getEditText().getText().toString());
                            startActivity(intent);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
            }
        });

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
            if (!trackCodeTextInputLayout.getEditText().getText().toString().isEmpty()){
                Order orderUpdate = new Order(addressTextInputLayout.getEditText().getText().toString(), cityTextInputLayout.getEditText().getText().toString(),
                        dateTextView.getText().toString(), deliveryTextInputLayout.getEditText().getText().toString(), emailTextInputLayout.getEditText().getText().toString(),
                        fioTextInputLayout.getEditText().getText().toString(), phoneTextInputLayout.getEditText().getText().toString(),
                        statusTextInputLayout.getEditText().getText().toString(), products, trackCodeTextInputLayout.getEditText().getText().toString());
                Map<String, Object> orderValues = orderUpdate.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("orders/" + user.getUid() + "/" + order.getId(), orderValues);
                mDatabase.updateChildren(childUpdates);
            } else {
                Order orderUpdate = new Order(addressTextInputLayout.getEditText().getText().toString(), cityTextInputLayout.getEditText().getText().toString(),
                        dateTextView.getText().toString(), deliveryTextInputLayout.getEditText().getText().toString(), emailTextInputLayout.getEditText().getText().toString(),
                        fioTextInputLayout.getEditText().getText().toString(), phoneTextInputLayout.getEditText().getText().toString(),
                        statusTextInputLayout.getEditText().getText().toString(), products, "null");
                Map<String, Object> orderValues = orderUpdate.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("orders/" + user.getUid() + "/" + order.getId(), orderValues);
                mDatabase.updateChildren(childUpdates);
            }

            onBackPressed();

        }
        return true;
    }
}