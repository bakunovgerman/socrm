package com.example.socrm;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.socrm.data.Order;
import com.example.socrm.data.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class ProductsFragment extends Fragment {

    FloatingActionButton floatingActionButton;
    private ArrayList<Product> products;
    private RecyclerView recyclerViewProducts;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FrameLayout frameLayout;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private String uid;
    private int productsSize = 0;

    public ProductsFragment() {
        // Required empty public constructor
    }
    public ProductsFragment(DatabaseReference mDatabase, String uid) {
        this.mDatabase = mDatabase;
        this.uid = uid;
    }
    public static ProductsFragment newInstance(DatabaseReference mDatabase, String uid) {
        return new ProductsFragment(mDatabase, uid);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_products, container, false);
        frameLayout = v.findViewById(R.id.progressbar_layout);
        recyclerViewProducts = v.findViewById(R.id.recyclerViewProducts);
        products = new ArrayList<>();

        floatingActionButton = v.findViewById(R.id.FloatingActionButton);
        floatingActionButton.setColorFilter(Color.argb(255, 255, 255, 255));
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddProductActivity.class));
            }
        });
        ValueEventListener productsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                products.clear();
                for (DataSnapshot productsSnapshot : dataSnapshot.getChildren()){
                    Product product = productsSnapshot.getValue(Product.class);
                    product.id = productsSnapshot.getKey();
                    products.add(product);
                }
                Collections.reverse(products);
                if (productsSize != 0 && products.size() > productsSize){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemInserted(0);
                        }
                    }, 1000);
                }
                else if ((productsSize = products.size()) != 0){
                    adapter = new ProductRecyclerViewAdapter( products, getContext());
                    layoutManager = new GridLayoutManager(getContext(), 2);
                    recyclerViewProducts.setLayoutManager(layoutManager);
                    recyclerViewProducts.setAdapter(adapter);
                    frameLayout.setVisibility(View.GONE);
                    recyclerViewProducts.startLayoutAnimation();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("products").child(uid).addValueEventListener(productsListener);
        //getProducts();

        return v;
    }
    public void getProducts(){
        mDatabase.child("products").child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    products.clear();
                    for (DataSnapshot productsSnapshot : task.getResult().getChildren()){
                        Product product = productsSnapshot.getValue(Product.class);
                        //product.id = productsSnapshot.getKey();
                        products.add(product);
                    }
                    Collections.reverse(products);
                    adapter = new ProductRecyclerViewAdapter( products, getContext());
                    layoutManager = new GridLayoutManager(getContext(), 2);
                    recyclerViewProducts.setLayoutManager(layoutManager);
                    recyclerViewProducts.setAdapter(adapter);
                    frameLayout.setVisibility(View.GONE);
                }
            }
        });
    }


}