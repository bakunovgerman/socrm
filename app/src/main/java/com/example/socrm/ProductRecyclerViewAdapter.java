package com.example.socrm;

import android.content.Context;
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
import com.example.socrm.data.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.ProductRecyclerViewViewHolder>{

    private ArrayList<Product> arrayList;
    private Context context;
    public static class ProductRecyclerViewViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView, priceTextView, saleTextView;
        public CardView productCardView;
        public ImageView productImageView;
        public LinearLayout saleRectLinearLayout;

        public ProductRecyclerViewViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            saleTextView = itemView.findViewById(R.id.saleTextView);
            saleRectLinearLayout = itemView.findViewById(R.id.saleRect);
            productImageView = itemView.findViewById(R.id.productImageView);
            productCardView = itemView.findViewById(R.id.productCardView);
        }
    }

    public ProductRecyclerViewAdapter(ArrayList<Product> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }
    @NonNull
    @Override
    public ProductRecyclerViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item,
                parent, false);

        ProductRecyclerViewAdapter.ProductRecyclerViewViewHolder recyclerViewViewHolder = new ProductRecyclerViewAdapter.ProductRecyclerViewViewHolder(view);
        return recyclerViewViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductRecyclerViewViewHolder holder, int position) {
        Product product = arrayList.get(position);
        Picasso.get()
                .load(product.getUrl())
                .placeholder(R.drawable.default_image_product)
                .error(R.drawable.default_image_product)
                .fit()
                .centerCrop()
                .into(holder.productImageView);

        holder.nameTextView.setText(product.getName());
        holder.priceTextView.setText(String.valueOf(product.getPrice()));



    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }




}
