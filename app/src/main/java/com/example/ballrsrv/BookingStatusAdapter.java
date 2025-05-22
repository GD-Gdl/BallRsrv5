package com.example.ballrsrv;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BookingStatusAdapter extends RecyclerView.Adapter<BookingStatusAdapter.ViewHolder> {
    private List<BookingRequest> bookings;

    public BookingStatusAdapter(List<BookingRequest> bookings) {
        this.bookings = bookings;
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
        BookingRequest booking = bookings.get(position);
        
        // Set booking details
        holder.bookingDetails.setText(booking.getBookingDetails());
        holder.date.setText("Date: " + booking.getDate());
        holder.timeSlot.setText("Time: " + booking.getTimeSlot());
        holder.duration.setText("Duration: " + booking.getDuration() + " hour(s)");
        holder.totalPrice.setText("Total Price: ₱" + booking.getTotalPrice());
        
        // Set status with color
        String status = "Status: " + booking.getStatus();
        holder.status.setText(status);
        holder.status.setTextColor(getStatusColor(booking.getStatus()));
        
        // Set payment method
        String paymentInfo = "Payment Method: " + booking.getPaymentMethod();
        holder.paymentStatus.setText(paymentInfo);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    private int getStatusColor(String status) {
        switch (status.toLowerCase()) {
            case "pending":
                return Color.parseColor("#FFA500"); // Orange
            case "accepted":
                return Color.parseColor("#4CAF50"); // Green
            case "denied":
                return Color.parseColor("#F44336"); // Red
            default:
                return Color.parseColor("#666666"); // Gray
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView bookingDetails, date, timeSlot, duration, totalPrice, status, paymentStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookingDetails = itemView.findViewById(R.id.tvBookingDetails);
            date = itemView.findViewById(R.id.tvDate);
            timeSlot = itemView.findViewById(R.id.tvTimeSlot);
            duration = itemView.findViewById(R.id.tvDuration);
            totalPrice = itemView.findViewById(R.id.tvTotalPrice);
            status = itemView.findViewById(R.id.tvStatus);
            paymentStatus = itemView.findViewById(R.id.tvPaymentStatus);
        }
    }
} 