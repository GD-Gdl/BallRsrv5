package com.example.ballrsrv;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class BookingStatusAdapter extends RecyclerView.Adapter<BookingStatusAdapter.ViewHolder> {
    private List<Booking> bookings;

    public BookingStatusAdapter(List<Booking> bookings) {
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
        Booking booking = bookings.get(position);
        
        // Set booking details
        holder.courtNameText.setText(booking.getCourtName());
        holder.dateTimeText.setText(String.format("Date: %s at %s", booking.getDate(), booking.getTime()));
        holder.durationText.setText(String.format("Duration: %d hour(s)", booking.getDuration()));
        holder.priceText.setText(String.format(Locale.getDefault(), "Total Price: ₱%.2f", booking.getTotalPrice()));
        holder.paymentMethodText.setText(String.format("Payment Method: %s", booking.getPaymentMethod()));
        
        // Make status more prominent
        String status = booking.getStatus();
        holder.statusText.setText(String.format("Status: %s", status.toUpperCase()));
        holder.statusText.setTypeface(null, Typeface.BOLD);
        
        // Set status color and card background
        int statusColor = getStatusColor(status);
        holder.statusText.setTextColor(statusColor);
        holder.cardView.setCardBackgroundColor(getCardBackgroundColor(status));
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    private int getStatusColor(String status) {
        switch (status.toLowerCase()) {
            case "pending":
                return Color.parseColor("#FF9800"); // Orange
            case "accepted":
                return Color.parseColor("#4CAF50"); // Green
            case "denied":
                return Color.parseColor("#F44336"); // Red
            default:
                return Color.parseColor("#757575"); // Gray
        }
    }

    private int getCardBackgroundColor(String status) {
        switch (status.toLowerCase()) {
            case "pending":
                return Color.parseColor("#FFF3E0"); // Light Orange
            case "accepted":
                return Color.parseColor("#E8F5E9"); // Light Green
            case "denied":
                return Color.parseColor("#FFEBEE"); // Light Red
            default:
                return Color.parseColor("#FFFFFF"); // White
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView courtNameText;
        public TextView dateTimeText;
        public TextView durationText;
        public TextView priceText;
        public TextView paymentMethodText;
        public TextView statusText;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            courtNameText = itemView.findViewById(R.id.courtNameText);
            dateTimeText = itemView.findViewById(R.id.dateTimeText);
            durationText = itemView.findViewById(R.id.durationText);
            priceText = itemView.findViewById(R.id.priceText);
            paymentMethodText = itemView.findViewById(R.id.paymentMethodText);
            statusText = itemView.findViewById(R.id.statusText);
        }
    }

    public void updateBookings(List<Booking> newBookings) {
        this.bookings = newBookings;
        notifyDataSetChanged();
    }
} 