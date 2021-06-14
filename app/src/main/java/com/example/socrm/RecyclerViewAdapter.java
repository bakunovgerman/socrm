package com.example.socrm;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socrm.data.Order;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewViewHolder> {

    private ArrayList<Order> arrayList;
    private Context context;

    public static class RecyclerViewViewHolder extends RecyclerView.ViewHolder {

        public TextView fioTextView, cityTextView, phoneNumberTextView, statusProductTextView, orderDateTime;
        public CardView orderCardView;
        public MaterialButton moreMaterialButton;

        public RecyclerViewViewHolder(@NonNull View itemView) {
            super(itemView);
            orderDateTime = itemView.findViewById(R.id.orderDateTime);
            fioTextView = itemView.findViewById(R.id.fioTextView);
            cityTextView = itemView.findViewById(R.id.cityTextView);
            phoneNumberTextView = itemView.findViewById(R.id.phoneNumberTextView);
            statusProductTextView = itemView.findViewById(R.id.statusProductTextView);
            orderCardView = itemView.findViewById(R.id.orderCardView);
            moreMaterialButton = itemView.findViewById(R.id.moreMaterialButton);
        }
    }

    public RecyclerViewAdapter(Context context, ArrayList<Order> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
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

        recyclerViewViewHolder.orderCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailOrderActivity.class);
                intent.putExtra(Order.class.getSimpleName(), arrayList.get(i));
                context.startActivity(intent);
            }
        });

        Order order = arrayList.get(i);
        String[] fioTrim = order.getFio().split(" ");
        if (fioTrim.length == 3){
            recyclerViewViewHolder.fioTextView.setText(String.format("%s %s. %s.", fioTrim[0],
                    fioTrim[1].substring(0, 1), fioTrim[2].substring(0, 1)));
        } else if (fioTrim.length == 2){
            recyclerViewViewHolder.fioTextView.setText(String.format("%s %s.", fioTrim[0],
                    fioTrim[1].substring(0, 1)));
        } else if (fioTrim.length == 1){
            recyclerViewViewHolder.fioTextView.setText(order.getFio());
        }
        recyclerViewViewHolder.cityTextView.setText(order.getCity());
        recyclerViewViewHolder.phoneNumberTextView.setText(order.getPhone());
        if (order.getStatus().equals("Открыт")){
            recyclerViewViewHolder.statusProductTextView.setTextColor(context.getResources().getColor(R.color.purple_500));
        }
        else if(order.getStatus().equals("Отменен")) {
            recyclerViewViewHolder.statusProductTextView.setTextColor(context.getResources().getColor(R.color.red_my));
        }
        else if(order.getStatus().equals("Доставлен")) {
            recyclerViewViewHolder.statusProductTextView.setTextColor(context.getResources().getColor(R.color.yellow_my));
        }
        recyclerViewViewHolder.statusProductTextView.setText(order.getStatus());
        recyclerViewViewHolder.orderDateTime.setText(order.getDate());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
