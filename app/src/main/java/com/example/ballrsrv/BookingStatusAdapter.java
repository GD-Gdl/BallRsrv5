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
        holder.courtNameText.setText(booking.getCourtName());
        holder.dateTimeText.setText(String.format("%s at %s", booking.getDate(), booking.getTime()));
        holder.statusText.setText(booking.getStatus());
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
        public TextView courtNameText;
        public TextView dateTimeText;
        public TextView statusText;

        public ViewHolder(View itemView) {
            super(itemView);
            courtNameText = itemView.findViewById(R.id.courtNameText);
            dateTimeText = itemView.findViewById(R.id.dateTimeText);
            statusText = itemView.findViewById(R.id.statusText);
        }
    }

    public void updateBookings(List<Booking> newBookings) {
        this.bookings = newBookings;
        notifyDataSetChanged();
    }
} 