package com.example.ballrsrv;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BookingStatusAdapter extends RecyclerView.Adapter<BookingStatusAdapter.ViewHolder> {
    private final List<BookingRequest> requests;

    public BookingStatusAdapter(List<BookingRequest> requests) {
        this.requests = requests;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_status, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingRequest request = requests.get(position);
        
        holder.date.setText("Date: " + request.getDate());
        holder.timeSlot.setText("Time: " + request.getTimeSlot());
        holder.bookingDetails.setText("Details: " + request.getBookingDetails());
        holder.status.setText("Status: " + request.getStatus());
        
        // Set status text color based on status
        switch (request.getStatus().toLowerCase()) {
            case "accepted":
                holder.status.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
                break;
            case "denied":
                holder.status.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
                break;
            default:
                holder.status.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.black));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView date;
        public final TextView timeSlot;
        public final TextView bookingDetails;
        public final TextView status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.textDate);
            timeSlot = itemView.findViewById(R.id.textTimeSlot);
            bookingDetails = itemView.findViewById(R.id.textBookingDetails);
            status = itemView.findViewById(R.id.textStatus);
        }
    }
} 