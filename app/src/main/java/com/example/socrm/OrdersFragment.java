package com.example.socrm;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.socrm.data.Order;

import java.util.ArrayList;

public class OrdersFragment extends Fragment {

    private ArrayList<Order> orders;
    private RecyclerView recyclerViewOrders;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    public OrdersFragment() {
        // Required empty public constructor
    }

    public OrdersFragment(ArrayList<Order> orders) {
        this.orders = orders;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static OrdersFragment newInstance(ArrayList<Order> orders) {
        return new OrdersFragment(orders);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_orders, container, false);

        recyclerViewOrders = v.findViewById(R.id.recyclerViewOrders);
        adapter = new RecyclerViewAdapter(getContext(), orders);
        layoutManager = new LinearLayoutManager(getContext());

        recyclerViewOrders.setAdapter(adapter);
        recyclerViewOrders.setLayoutManager(layoutManager);


        return v;
    }
}