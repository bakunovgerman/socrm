package com.example.socrm;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socrm.data.Order;
import com.example.socrm.data.OrderComposition;
import com.example.socrm.data.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class OrdersFragment extends Fragment {

    private AutoCompleteTextView deliveryItem, statusItem;
    private ArrayList<Order> orders;
    private ArrayList<Order> ordersFind;
    private ArrayList<Order> ordersFilters;
    private RecyclerView recyclerViewOrders;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference mDatabase;
    private String uid;
    private FrameLayout progressBar;
    private EditText searchOrderEditText;
    private RadioButton newOrderRadioButton, oldOrderRadioButton;
    private Boolean newOrderRadioButtonIsChecked, oldOrderRadioButtonIsChecked;
    private int ordersSize = 0;
    private FirebaseUser user;

    public OrdersFragment() {
        // Required empty public constructor
    }

    public OrdersFragment(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static OrdersFragment newInstance(DatabaseReference mDatabase) {
        return new OrdersFragment(mDatabase);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_orders, container, false);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
        View bottomSheetView  = LayoutInflater.from(getContext()).inflate(R.layout.layout_bottom_sheet,
                v.findViewById(R.id.bottomSheetContainer));
        BottomSheetDialog bottomSheetFiltersDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
        View bottomSheetFiltersView  = LayoutInflater.from(getContext()).inflate(R.layout.layout_bottom_sheet_filter,
                v.findViewById(R.id.bottomSheetFiltersContainer));

        String[] delivery_array = getResources().getStringArray(R.array.delivery_input);
        String[] status_array = getResources().getStringArray(R.array.status_input);
        ArrayAdapter<String> arrayAdapterDelivery = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, delivery_array);
        ArrayAdapter<String> arrayAdapterStatus = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, status_array);
        deliveryItem = bottomSheetFiltersView.findViewById(R.id.deliveryItem);
        statusItem = bottomSheetFiltersView.findViewById(R.id.statusItem);
        deliveryItem.setAdapter(arrayAdapterDelivery);
        statusItem.setAdapter(arrayAdapterStatus);
        MaterialButton filtersApplyMaterialButton = bottomSheetFiltersView.findViewById(R.id.filtersApplyMaterialButton);
        TextInputLayout startSumTextInputLayout = bottomSheetFiltersView.findViewById(R.id.startSumTextInputLayout);
        TextInputLayout endSumTextInputLayout = bottomSheetFiltersView.findViewById(R.id.endSumTextInputLayout);
        TextInputLayout statusTextInputLayout = bottomSheetFiltersView.findViewById(R.id.statusInput);
        TextInputLayout deliveryTextInputLayout = bottomSheetFiltersView.findViewById(R.id.deliveryInput);
        ordersFilters = new ArrayList<>();
        // Получаем instance авторизированного пользователя
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // получаем инфу о пользователе чтобы потом вывести в фрагменте для профиля
            uid = user.getUid();
        }else{
            Log.d("photoUser", "user is null");
        }
        progressBar = v.findViewById(R.id.progressbar_layout);
        recyclerViewOrders = v.findViewById(R.id.recyclerViewOrders);
        searchOrderEditText = v.findViewById(R.id.searchOrdersEditText);
        MaterialButton filterTimeBtn = v.findViewById(R.id.filterTimeBtn);
        MaterialButton filters = v.findViewById(R.id.filtersBtn);
        orders = new ArrayList<Order>();
        ordersFind = new ArrayList<Order>();
        mDatabase.child("orders").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("onDataChange_111", "onDataChange start");
                orders.clear();
                for (DataSnapshot ordersSnapshot : snapshot.getChildren()){
                    Order order = ordersSnapshot.getValue(Order.class);
                    order.id = ordersSnapshot.getKey();
                    orders.add(order);
                }
                Collections.reverse(orders);
                if (ordersSize != 0 && orders.size() > ordersSize){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemInserted(0);
                        }
                    }, 1000);
                }
                else if ((ordersSize = orders.size()) != 0){
                    adapter = new RecyclerViewAdapter(getContext(), orders);
                    layoutManager = new LinearLayoutManager(getContext());
                    recyclerViewOrders.setAdapter(adapter);
                    recyclerViewOrders.setLayoutManager(layoutManager);
                    progressBar.setVisibility(View.GONE);
                    recyclerViewOrders.startLayoutAnimation();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        newOrderRadioButton = bottomSheetView.findViewById(R.id.newOrderRadioButton);
        newOrderRadioButton.setChecked(true);
        newOrderRadioButtonIsChecked = true;
        oldOrderRadioButton = bottomSheetView.findViewById(R.id.oldOrderRadioButton);
        oldOrderRadioButton.setChecked(false);
        oldOrderRadioButtonIsChecked = false;
        filterTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newOrderRadioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!newOrderRadioButtonIsChecked){
                            newOrderRadioButton.setChecked(true);
                            newOrderRadioButtonIsChecked = true;
                            oldOrderRadioButtonIsChecked = false;
                            Collections.reverse(orders);
                            adapter.notifyDataSetChanged();
                        }
                        bottomSheetDialog.dismiss();
                    }
                });
                oldOrderRadioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!oldOrderRadioButtonIsChecked){
                            oldOrderRadioButton.setChecked(true);
                            newOrderRadioButtonIsChecked = false;
                            oldOrderRadioButtonIsChecked = true;
                            Collections.reverse(orders);
                            adapter.notifyDataSetChanged();
                        }
                        bottomSheetDialog.dismiss();
                        recyclerViewOrders.startLayoutAnimation();
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });
        filters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtersApplyMaterialButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ordersFilters.clear();
                        ArrayList<Order> duplicateOrders = new ArrayList<>();
                        duplicateOrders.addAll(orders);
                        if (!startSumTextInputLayout.getEditText().getText().toString().equals("")
                                && !endSumTextInputLayout.getEditText().getText().toString().equals("")){
                            int startSum = Integer.parseInt(startSumTextInputLayout.getEditText().getText().toString());
                            int endSum = Integer.parseInt(endSumTextInputLayout.getEditText().getText().toString());
                            for (Order order: duplicateOrders){
                                int sumOrder = 0;
                                for (OrderComposition orderComposition : order.getProducts())
                                {
                                    sumOrder += Integer.parseInt(orderComposition.getPrice_product()) * Integer.parseInt(orderComposition.count);
                                }

                                if (!statusTextInputLayout.getEditText().getText().toString().equals("") && !deliveryTextInputLayout.getEditText().getText().toString().equals("")){
                                    String status = statusTextInputLayout.getEditText().getText().toString();
                                    String delivery = deliveryTextInputLayout.getEditText().getText().toString();
                                    if (sumOrder>= startSum && sumOrder<=endSum && order.getStatus().equals(status)
                                            && order.getDelivery().equals(delivery)){
                                        ordersFilters.add(order);
                                    }
                                } else if (!statusTextInputLayout.getEditText().getText().toString().equals("")){
                                    String status = statusTextInputLayout.getEditText().getText().toString();
                                    if (sumOrder>= startSum && sumOrder<=endSum && order.getStatus().equals(status)){
                                        ordersFilters.add(order);
                                    }
                                } else if (!deliveryTextInputLayout.getEditText().getText().toString().equals("")){
                                    String delivery = deliveryTextInputLayout.getEditText().getText().toString();
                                    if (sumOrder>= startSum && sumOrder<=endSum && order.getDelivery().equals(delivery)){
                                        ordersFilters.add(order);
                                    }
                                } else if (sumOrder>= startSum && sumOrder<=endSum){
                                    ordersFilters.add(order);
                                }
                            }
                            for (Order order: ordersFilters){
                                if (duplicateOrders.contains(order))
                                    duplicateOrders.remove(order);
                            }
                        }
                        else if (!startSumTextInputLayout.getEditText().getText().toString().equals("")){
                            int startSum = Integer.parseInt(startSumTextInputLayout.getEditText().getText().toString());
                            for (Order order: duplicateOrders){
                                int sumOrder = 0;
                                for (OrderComposition orderComposition : order.getProducts())
                                {
                                    sumOrder += Integer.parseInt(orderComposition.getPrice_product()) * Integer.parseInt(orderComposition.count);
                                }
                                if (!statusTextInputLayout.getEditText().getText().toString().equals("") && !deliveryTextInputLayout.getEditText().getText().toString().equals("")){
                                    String status = statusTextInputLayout.getEditText().getText().toString();
                                    String delivery = deliveryTextInputLayout.getEditText().getText().toString();
                                    if (sumOrder>= startSum && order.getStatus().equals(status)
                                            && order.getDelivery().equals(delivery)){
                                        ordersFilters.add(order);
                                    }
                                } else if (!statusTextInputLayout.getEditText().getText().toString().equals("")){
                                    String status = statusTextInputLayout.getEditText().getText().toString();
                                    if (sumOrder>= startSum && order.getStatus().equals(status)){
                                        ordersFilters.add(order);
                                    }
                                } else if (!deliveryTextInputLayout.getEditText().getText().toString().equals("")){
                                    String delivery = deliveryTextInputLayout.getEditText().getText().toString();
                                    if (sumOrder>= startSum && order.getDelivery().equals(delivery)){
                                        ordersFilters.add(order);
                                    }
                                } else if (sumOrder>= startSum){
                                    ordersFilters.add(order);
                                }
                            }
                            for (Order order: ordersFilters){
                                if (duplicateOrders.contains(order))
                                    duplicateOrders.remove(order);
                            }
                        } else if (!endSumTextInputLayout.getEditText().getText().toString().equals("")){
                            int endSum = Integer.parseInt(endSumTextInputLayout.getEditText().getText().toString());
                            for (Order order: duplicateOrders){
                                int sumOrder = 0;
                                for (OrderComposition orderComposition : order.getProducts())
                                {
                                    sumOrder += Integer.parseInt(orderComposition.getPrice_product()) * Integer.parseInt(orderComposition.count);
                                }
                                if (!statusTextInputLayout.getEditText().getText().toString().equals("") && !deliveryTextInputLayout.getEditText().getText().toString().equals("")){
                                    String status = statusTextInputLayout.getEditText().getText().toString();
                                    String delivery = deliveryTextInputLayout.getEditText().getText().toString();
                                    if (sumOrder<=endSum && order.getStatus().equals(status)
                                            && order.getDelivery().equals(delivery)){
                                        ordersFilters.add(order);
                                    }
                                } else if (!statusTextInputLayout.getEditText().getText().toString().equals("")){
                                    String status = statusTextInputLayout.getEditText().getText().toString();
                                    if (sumOrder<=endSum && order.getStatus().equals(status)){
                                        ordersFilters.add(order);
                                    }
                                } else if (!deliveryTextInputLayout.getEditText().getText().toString().equals("")){
                                    String delivery = deliveryTextInputLayout.getEditText().getText().toString();
                                    if (sumOrder<=endSum && order.getDelivery().equals(delivery)){
                                        ordersFilters.add(order);
                                    }
                                } else if (sumOrder<=endSum){
                                    ordersFilters.add(order);
                                }
                            }
                            for (Order order: ordersFilters){
                                if (duplicateOrders.contains(order))
                                    duplicateOrders.remove(order);
                            }
                        }
                        else if (!statusTextInputLayout.getEditText().getText().toString().equals("")){
                            String status = statusTextInputLayout.getEditText().getText().toString();
                            if (!deliveryTextInputLayout.getEditText().getText().toString().equals("")){
                                String delivery = deliveryTextInputLayout.getEditText().getText().toString();
                                for (Order order: duplicateOrders){
                                    if (order.getStatus().equals(status) && order.getDelivery().equals(delivery)){
                                        ordersFilters.add(order);
                                    }
                                }
                            } else {
                                for (Order order: duplicateOrders){
                                    if (order.getStatus().equals(status)){
                                        ordersFilters.add(order);
                                    }
                                }
                            }
                            for (Order order: ordersFilters){
                                if (duplicateOrders.contains(order))
                                    duplicateOrders.remove(order);
                            }
                        }
                        else if (!deliveryTextInputLayout.getEditText().getText().toString().equals("")){
                            String delivery = deliveryTextInputLayout.getEditText().getText().toString();
                            if (!statusTextInputLayout.getEditText().getText().toString().equals("")){
                                String status = statusTextInputLayout.getEditText().getText().toString();
                                for (Order order: duplicateOrders){
                                    if (order.getStatus().equals(status) && order.getDelivery().equals(delivery)){
                                        ordersFilters.add(order);
                                    }
                                }
                            } else{
                                for (Order order: duplicateOrders){
                                    if (order.getDelivery().equals(delivery)){
                                        ordersFilters.add(order);
                                    }
                                }
                            }
                            for (Order order: ordersFilters){
                                if (duplicateOrders.contains(order))
                                    duplicateOrders.remove(order);
                            }
                        }
                        if (ordersFilters.size() != 0)
                            recyclerViewOrders.setAdapter(new RecyclerViewAdapter(getContext(), ordersFilters));
                        bottomSheetFiltersDialog.dismiss();
                        recyclerViewOrders.startLayoutAnimation();
                    }
                });
                bottomSheetFiltersDialog.setContentView(bottomSheetFiltersView);
                bottomSheetFiltersDialog.show();
            }
        });
        searchOrderEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!searchOrderEditText.getText().toString().equals("")){
                    searchOrders(searchOrderEditText.getText().toString());
                } else {
                    adapter = new RecyclerViewAdapter(getContext(), orders);
                    recyclerViewOrders.setAdapter(adapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return v;
    }
    public void getOrders(){
        mDatabase.child("orders").child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    orders.clear();
                    for (DataSnapshot orderSnapshot : task.getResult().getChildren()){
                        Order order = orderSnapshot.getValue(Order.class);
                        order.id = orderSnapshot.getKey();
                        orders.add(order);
                    }

                }
                else {
                    Log.e("firebase", "Error getting data", task.getException());
                }
            }
        });
    }
    private void searchOrders(String searchText){
        ordersFind.clear();
        for (Order order : orders)
        {
            if (order.getFio().toLowerCase().contains(searchText.toLowerCase())){
                ordersFind.add(order);
            }
        }
        adapter = new RecyclerViewAdapter(getContext(), ordersFind);
        recyclerViewOrders.setAdapter(adapter);
    }
}