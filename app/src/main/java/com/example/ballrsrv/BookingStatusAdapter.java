package com.example.ballrsrv;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.ArrayList;

public class BookingStatusAdapter extends RecyclerView.Adapter<BookingStatusAdapter.ViewHolder> {
    private static final String TAG = "BookingStatusAdapter";
    private final List<BookingRequest> requests;

    public BookingStatusAdapter(List<BookingRequest> requests) {
        this.requests = requests != null ? requests : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_booking_status, parent, false);
            return new ViewHolder(view);
        } catch (Exception e) {
            Log.e(TAG, "Error creating ViewHolder: " + e.getMessage(), e);
            throw new RuntimeException("Failed to create ViewHolder", e);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            if (position < 0 || position >= requests.size()) {
                Log.e(TAG, "Invalid position: " + position);
                return;
            }

            BookingRequest request = requests.get(position);
            if (request == null) {
                Log.e(TAG, "Request is null at position: " + position);
                return;
            }

            // Set text safely for all fields with null checks
            setTextSafely(holder.date, "Date: ", request.getDate());
            setTextSafely(holder.timeSlot, "Time: ", request.getTimeSlot());
            setDurationText(holder.duration, String.valueOf(request.getDuration()));
            setPriceText(holder.price, String.valueOf(request.getTotalPrice()));
            setTextSafely(holder.bookingDetails, "Details: ", request.getBookingDetails());
            setTextSafely(holder.paymentStatus, "Payment: ", request.getPaymentStatus());
            setTextSafely(holder.status, "Status: ", request.getStatus());

            // Set status text color based on status
            updateStatusColor(holder.status, request.getStatus());
            updatePaymentStatusColor(holder.paymentStatus, request.getPaymentStatus());

        } catch (Exception e) {
            Log.e(TAG, "Error binding view holder: " + e.getMessage(), e);
        }
    }

    private void setTextSafely(TextView textView, String prefix, String value) {
        if (textView != null) {
            textView.setText(prefix + (value != null ? value : "N/A"));
        }
    }

    private void setDurationText(TextView textView, String duration) {
        if (textView != null) {
            String formattedDuration = "N/A";
            if (duration != null && !duration.isEmpty()) {
                try {
                    // If duration is a number, append " hours"
                    int hours = Integer.parseInt(duration);
                    formattedDuration = hours + (hours == 1 ? " hour" : " hours");
                } catch (NumberFormatException e) {
                    // If not a number, use as is
                    formattedDuration = duration;
                }
            }
            textView.setText("Duration: " + formattedDuration);
        }
    }

    private void setPriceText(TextView textView, String price) {
        if (textView != null) {
            String formattedPrice = "0";
            if (price != null && !price.isEmpty()) {
                try {
                    // Try to parse as double and format with 2 decimal places
                    double amount = Double.parseDouble(price);
                    formattedPrice = String.format("%.2f", amount);
                } catch (NumberFormatException e) {
                    // If not a valid number, use as is
                    formattedPrice = price;
                }
            }
            textView.setText("Price: ₱" + formattedPrice);
        }
    }

    private void updateStatusColor(TextView statusView, String status) {
        if (statusView == null) return;
        
        int colorResId;
        switch (status != null ? status.toLowerCase() : "") {
            case "accepted":
                colorResId = android.R.color.holo_green_dark;
                break;
            case "denied":
                colorResId = android.R.color.holo_red_dark;
                break;
            default:
                colorResId = android.R.color.black;
                break;
        }
        statusView.setTextColor(ContextCompat.getColor(statusView.getContext(), colorResId));
    }

    private void updatePaymentStatusColor(TextView paymentView, String paymentStatus) {
        if (paymentView == null) return;
        
        int colorResId;
        switch (paymentStatus != null ? paymentStatus.toLowerCase() : "") {
            case "paid":
                colorResId = android.R.color.holo_green_dark;
                break;
            case "pending":
                colorResId = android.R.color.holo_orange_dark;
                break;
            default:
                colorResId = android.R.color.black;
                break;
        }
        paymentView.setTextColor(ContextCompat.getColor(paymentView.getContext(), colorResId));
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView date;
        public final TextView timeSlot;
        public final TextView duration;
        public final TextView price;
        public final TextView bookingDetails;
        public final TextView paymentStatus;
        public final TextView status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                date = itemView.findViewById(R.id.textDate);
                timeSlot = itemView.findViewById(R.id.textTimeSlot);
                duration = itemView.findViewById(R.id.textDuration);
                price = itemView.findViewById(R.id.textPrice);
                bookingDetails = itemView.findViewById(R.id.textBookingDetails);
                paymentStatus = itemView.findViewById(R.id.textPaymentStatus);
                status = itemView.findViewById(R.id.textStatus);

                // Verify all views are found
                if (date == null || timeSlot == null || duration == null || 
                    price == null || bookingDetails == null || paymentStatus == null || 
                    status == null) {
                    throw new IllegalStateException("One or more views not found in layout");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error initializing ViewHolder: " + e.getMessage(), e);
                throw new RuntimeException("Failed to initialize ViewHolder", e);
            }
        }
    }
} 