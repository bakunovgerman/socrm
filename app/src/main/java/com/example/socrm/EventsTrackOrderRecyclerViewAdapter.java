package com.example.socrm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socrm.data.Product;
import com.example.socrm.data.TrackOrder.Event;
import com.squareup.picasso.Picasso;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;

public class EventsTrackOrderRecyclerViewAdapter extends RecyclerView.Adapter<EventsTrackOrderRecyclerViewAdapter.EventsTrackOrderRecyclerViewViewHolder>{

    private ArrayList<Event> arrayList;
    private Context context;
    public static class EventsTrackOrderRecyclerViewViewHolder extends RecyclerView.ViewHolder {

        public TextView eventDateTextView, eventOperationTextView, eventLocationTextView;
        public View way1_event, way2_event, circle_event;
        public LinearLayout eventItemLinearLayout;

        public EventsTrackOrderRecyclerViewViewHolder(@NonNull View itemView) {
            super(itemView);
            eventDateTextView = itemView.findViewById(R.id.eventDateTextView);
            eventOperationTextView = itemView.findViewById(R.id.eventOperationTextView);
            eventLocationTextView = itemView.findViewById(R.id.eventLocationTextView);
            way1_event = itemView.findViewById(R.id.way1_event);
            way2_event = itemView.findViewById(R.id.way2_event);
            circle_event = itemView.findViewById(R.id.circle_event);
            eventItemLinearLayout = itemView.findViewById(R.id.eventItemLinearLayout);
        }
    }

    public EventsTrackOrderRecyclerViewAdapter(ArrayList<Event> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }
    @NonNull
    @Override
    public EventsTrackOrderRecyclerViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_track_order_item,
                parent, false);

        EventsTrackOrderRecyclerViewAdapter.EventsTrackOrderRecyclerViewViewHolder recyclerViewViewHolder = new EventsTrackOrderRecyclerViewAdapter.EventsTrackOrderRecyclerViewViewHolder(view);
        return recyclerViewViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventsTrackOrderRecyclerViewViewHolder holder, int position) {
//        holder.productCardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, DetailProductActivity.class);
//                intent.putExtra(Product.class.getSimpleName(), arrayList.get(position));
//                context.startActivity(intent);
//            }
//        });
        if (position % 2 != 0){
            holder.eventItemLinearLayout.setBackgroundColor(Color.WHITE);
        }
        Event event = arrayList.get(position);
//        Boolean b = event.getDelivered();
        if (event.getDelivered() != null){
            holder.circle_event.setBackgroundResource(R.drawable.shape_circle_event_finish_item);
            holder.way1_event.setVisibility(View.INVISIBLE);
        }
        if (event.getArrived() != null){
            holder.circle_event.setBackgroundResource(R.drawable.shape_circle_event_arrived_item);
        }
        if (position == arrayList.size()-1){
            holder.way2_event.setVisibility(View.INVISIBLE);
        }
        holder.eventDateTextView.setText(new Date(event.getEventDate()).toString());
        holder.eventOperationTextView.setText(event.getOperation());
        holder.eventLocationTextView.setText(event.getLocation());
//                Timestamp stamp = new Timestamp(Long.parseLong("1622667600000"));
//                Date date = new Date(stamp.getTime());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }




}
