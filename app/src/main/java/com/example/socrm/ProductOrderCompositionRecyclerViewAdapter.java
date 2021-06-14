package com.example.socrm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socrm.data.Order;
import com.example.socrm.data.OrderComposition;
import com.example.socrm.data.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductOrderCompositionRecyclerViewAdapter extends RecyclerView.Adapter<ProductOrderCompositionRecyclerViewAdapter.ProductRecyclerViewViewHolder>{

    private ArrayList<OrderComposition> arrayList;
    private Context context;
    private DatabaseReference mDatabase;
    private String uid;
    private String productId;
    private String nameProduct;
    private String urlImageProduct;

    public static class ProductRecyclerViewViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView, priceTextView, countProductTextView;
        public CardView productCardView;
        public ImageView productImageView;

        public ProductRecyclerViewViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            productImageView = itemView.findViewById(R.id.productImageView);
            productCardView = itemView.findViewById(R.id.productCardView);
            countProductTextView = itemView.findViewById(R.id.countProductTextView);
        }
    }

    public ProductOrderCompositionRecyclerViewAdapter(ArrayList<OrderComposition> arrayList, Context context, DatabaseReference mDatabase, String uid) {
        this.arrayList = arrayList;
        this.context = context;
        this.mDatabase = mDatabase;
        this.uid = uid;
    }
    @NonNull
    @Override
    public ProductRecyclerViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item,
                parent, false);

        ProductOrderCompositionRecyclerViewAdapter.ProductRecyclerViewViewHolder recyclerViewViewHolder = new ProductOrderCompositionRecyclerViewAdapter.ProductRecyclerViewViewHolder(view);
        return recyclerViewViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductRecyclerViewViewHolder holder, int position) {
        holder.productCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, DetailProductActivity.class);
//                intent.putExtra(Product.class.getSimpleName(), arrayList.get(position));
//                context.startActivity(intent);
            }
        });

        OrderComposition orderComposition = arrayList.get(position);
        productId = orderComposition.getId_product();

        Picasso.get()
                .load(urlImageProduct)
                .placeholder(R.drawable.default_image_product)
                .error(R.drawable.default_image_product)
                .fit()
                .centerCrop()
                .into(holder.productImageView);

        holder.nameTextView.setText(nameProduct);
        holder.priceTextView.setText(String.valueOf(orderComposition.getPrice_product()) + "₽");

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void getInfoProduct(){
        mDatabase.child("products").child(uid).child(productId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot productSnapshot : task.getResult().getChildren()){
                        Product product = productSnapshot.getValue(Product.class);
                        nameProduct = product.getName();
                        urlImageProduct = product.getUrl();
                    }

                }
                else {
                    Log.e("firebase", "Error getting data", task.getException());
                }
            }
        });
    }




}
