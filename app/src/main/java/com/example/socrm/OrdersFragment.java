package com.example.socrm;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.socrm.data.Order;
import com.example.socrm.data.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class OrdersFragment extends Fragment {

    private ArrayList<Order> orders;
    private RecyclerView recyclerViewOrders;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference mDatabase;
    private String uid;
    private FrameLayout progressBar;
    private RadioButton newOrderRadioButton, oldOrderRadioButton;
    private Boolean newOrderRadioButtonIsChecked, oldOrderRadioButtonIsChecked;
    private int ordersSize = 0;

    public OrdersFragment() {
        // Required empty public constructor
    }

    public OrdersFragment(DatabaseReference mDatabase, String uid) {
        this.mDatabase = mDatabase;
        this.uid = uid;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static OrdersFragment newInstance(DatabaseReference mDatabase, String uid) {
        return new OrdersFragment(mDatabase, uid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_orders, container, false);

        progressBar = v.findViewById(R.id.progressbar_layout);
        recyclerViewOrders = v.findViewById(R.id.recyclerViewOrders);
        MaterialButton filterTimeBtn = v.findViewById(R.id.filterTimeBtn);
        orders = new ArrayList<Order>();

        ValueEventListener ordersListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orders.clear();
                for (DataSnapshot ordersSnapshot : dataSnapshot.getChildren()){
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
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("orders").child(uid).addValueEventListener(ordersListener);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
        View bottomSheetView  = LayoutInflater.from(getContext()).inflate(R.layout.layout_bottom_sheet,
                v.findViewById(R.id.bottomSheetContainer));
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
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
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
}