package com.example.socrm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socrm.data.Order;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewViewHolder> {

    private ArrayList<Order> arrayList;

    public static class RecyclerViewViewHolder extends RecyclerView.ViewHolder {

        public TextView fioTextView, productTextView, countProductTextView, cityTextView, phoneNumberTextView, statusProductTextView;

        public RecyclerViewViewHolder(@NonNull View itemView) {
            super(itemView);
            fioTextView = itemView.findViewById(R.id.fioTextView);
            productTextView = itemView.findViewById(R.id.productTextView);
            countProductTextView = itemView.findViewById(R.id.countProductTextView);
            cityTextView = itemView.findViewById(R.id.cityTextView);
            phoneNumberTextView = itemView.findViewById(R.id.phoneNumberTextView);
            statusProductTextView = itemView.findViewById(R.id.statusProductTextView);
        }
    }

    public RecyclerViewAdapter(ArrayList<Order> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public RecyclerViewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_item,
                viewGroup, false);
        RecyclerViewViewHolder recyclerViewViewHolder = new RecyclerViewViewHolder(view);
        return recyclerViewViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewViewHolder recyclerViewViewHolder, int i) {
        Order order = arrayList.get(i);

        String[] fioTrim = order.getFio().split(" ");
        recyclerViewViewHolder.fioTextView.setText(String.format("%s %s. %s.", fioTrim[0], fioTrim[1].substring(0, 1), fioTrim[2].substring(0, 1)));
        recyclerViewViewHolder.productTextView.setText(order.getProduct());
        recyclerViewViewHolder.countProductTextView.setText(order.getCount_product());
        recyclerViewViewHolder.cityTextView.setText(order.getCity());
        recyclerViewViewHolder.phoneNumberTextView.setText(order.getPhone());
        recyclerViewViewHolder.statusProductTextView.setText(order.getStatus());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
