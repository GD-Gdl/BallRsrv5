package com.example.ballrsrv;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BookingStatusAdapter extends RecyclerView.Adapter<BookingStatusAdapter.ViewHolder> {
    private List<BookingRequest> requests;

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
        holder.details.setText("Details: " + request.getBookingDetails());
        holder.duration.setText("Duration: " + request.getDuration() + " hour(s)");
        holder.price.setText("Price: ₱" + request.getTotalPrice());
        holder.paymentStatus.setText("Payment: " + request.getPaymentStatus());
        
        // Set status with appropriate color
        String status = request.getStatus();
        holder.status.setText("Status: " + status);
        
        // Set color based on status
        int colorRes;
        switch (status.toLowerCase()) {
            case "accepted":
                colorRes = android.R.color.holo_green_dark;
                break;
            case "denied":
                colorRes = android.R.color.holo_red_dark;
                break;
            default:
                colorRes = android.R.color.darker_gray;
                break;
        }
        holder.status.setTextColor(holder.itemView.getContext().getResources().getColor(colorRes));
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView date;
        public TextView timeSlot;
        public TextView details;
        public TextView status;
        public TextView duration;
        public TextView price;
        public TextView paymentStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.textDate);
            timeSlot = itemView.findViewById(R.id.textTimeSlot);
            details = itemView.findViewById(R.id.textBookingDetails);
            status = itemView.findViewById(R.id.textStatus);
            duration = itemView.findViewById(R.id.textDuration);
            price = itemView.findViewById(R.id.textPrice);
            paymentStatus = itemView.findViewById(R.id.textPaymentStatus);
        }
    }
} 