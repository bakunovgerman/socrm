package com.example.socrm;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.socrm.data.Order;
import com.example.socrm.data.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ProductsFragment extends Fragment {

    FloatingActionButton floatingActionButton;
    private ArrayList<Product> products;
    private RecyclerView recyclerViewProducts;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    public ProductsFragment() {
        // Required empty public constructor
    }
    public ProductsFragment(ArrayList<Product> products) {
        this.products = products;
    }
    public static ProductsFragment newInstance(ArrayList<Product> products) {
        return new ProductsFragment(products);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_products, container, false);
        floatingActionButton = v.findViewById(R.id.FloatingActionButton);
        floatingActionButton.setColorFilter(Color.argb(255, 255, 255, 255));
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddProductActivity.class));
            }
        });

        recyclerViewProducts = v.findViewById(R.id.recyclerViewProducts);
        adapter = new ProductRecyclerViewAdapter( products, getContext());
        layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerViewProducts.setAdapter(adapter);
        recyclerViewProducts.setLayoutManager(layoutManager);

        return v;
    }
}