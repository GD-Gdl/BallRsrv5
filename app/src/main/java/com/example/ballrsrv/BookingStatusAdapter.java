package com.example.ballrsrv;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import android.util.Log;
import com.google.firebase.database.*;
import com.bumptech.glide.Glide;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.content.Context;

public class BookingStatusAdapter extends RecyclerView.Adapter<BookingStatusAdapter.ViewHolder> {
    private static final String TAG = "BookingStatusAdapter";
    private List<Booking> bookings;
    private final SimpleDateFormat dateFormat;
    private final SimpleDateFormat displayFormat;
    private DatabaseReference courtsRef;

    public BookingStatusAdapter(List<Booking> bookings) {
        this.bookings = new ArrayList<>(bookings);
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        this.displayFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        this.courtsRef = FirebaseDatabase.getInstance().getReference().child("courts");
        sortBookings();
    }

    private void loadCourtImage(String courtName, ImageView imageView, Context context) {
        courtsRef.orderByChild("name").equalTo(courtName)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot courtSnapshot : snapshot.getChildren()) {
                            Court court = courtSnapshot.getValue(Court.class);
                            if (court != null && court.getImageBase64() != null) {
                                try {
                                    // Decode and display base64 image
                                    byte[] decodedString = Base64.decode(court.getImageBase64(), Base64.DEFAULT);
                                    Glide.with(context)
                                        .load(decodedString)
                                        .placeholder(getDefaultImageResource(courtName))
                                        .error(getDefaultImageResource(courtName))
                                        .into(imageView);
                                    return;
                                } catch (Exception e) {
                                    Log.e(TAG, "Error loading image: " + e.getMessage());
                                }
                            }
                        }
                    }
                    // If no image found, load default
                    imageView.setImageResource(getDefaultImageResource(courtName));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Error loading court image: " + error.getMessage());
                    imageView.setImageResource(getDefaultImageResource(courtName));
                }
            });
    }

    private int getDefaultImageResource(String courtName) {
        if (courtName == null) {
            return android.R.drawable.ic_menu_gallery;
        }
        
        switch (courtName.toLowerCase()) {
            case "ymca basketball court":
                return R.drawable.ymca_hostel_baguio06;
            case "irisan basketball court":
                return R.drawable.irisan;
            case "st. vincent basketball court":
                return R.drawable.vincent;
            default:
                return android.R.drawable.ic_menu_gallery;
        }
    }

    private void sortBookings() {
        try {
            Collections.sort(bookings, new Comparator<Booking>() {
                @Override
                public int compare(Booking b1, Booking b2) {
                    try {
                        // Parse the dates
                        Date date1 = dateFormat.parse(b1.getDate());
                        Date date2 = dateFormat.parse(b2.getDate());
                        
                        if (date1 == null || date2 == null) {
                            return 0;
                        }

                        // Add time to the comparison
                        long time1 = date1.getTime() + parseTimeToMinutes(b1.getTime()) * 60 * 1000;
                        long time2 = date2.getTime() + parseTimeToMinutes(b2.getTime()) * 60 * 1000;
                        
                        // Sort in descending order (latest first)
                        return Long.compare(time2, time1);
                    } catch (ParseException e) {
                        Log.e(TAG, "Error parsing date: " + e.getMessage());
                        return 0;
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error sorting bookings: " + e.getMessage());
        }
    }

    private long parseTimeToMinutes(String time) {
        try {
            String[] parts = time.split(":");
            if (parts.length == 2) {
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);
                return hours * 60L + minutes;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing time: " + e.getMessage());
        }
        return 0;
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
        try {
            Booking booking = bookings.get(position);
            holder.courtNameText.setText(booking.getCourtName());
            
            // Format the date for display
            Date bookingDate = dateFormat.parse(booking.getDate());
            String displayDate = bookingDate != null ? displayFormat.format(bookingDate) : booking.getDate();
            holder.dateTimeText.setText(String.format("%s at %s", displayDate, booking.getTime()));
            
            // Set status text and color
            String status = booking.getStatus().toUpperCase();
            holder.statusText.setText("Status: " + status);
            holder.statusText.setTextColor(getStatusColor(booking.getStatus()));
            
            // Set the background color of the status text
            holder.statusText.setBackgroundColor(getStatusBackgroundColor(booking.getStatus()));
            holder.statusText.setPadding(16, 8, 16, 8); // Add padding for better appearance

            // Load court image
            loadCourtImage(booking.getCourtName(), holder.courtImage, holder.itemView.getContext());
        } catch (Exception e) {
            Log.e(TAG, "Error binding view holder: " + e.getMessage());
        }
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

    private int getStatusBackgroundColor(String status) {
        switch (status.toLowerCase()) {
            case "pending":
                return Color.parseColor("#FFF3E0"); // Light Orange
            case "accepted":
                return Color.parseColor("#E8F5E9"); // Light Green
            case "denied":
                return Color.parseColor("#FFEBEE"); // Light Red
            default:
                return Color.parseColor("#F5F5F5"); // Light Gray
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView courtNameText;
        public TextView dateTimeText;
        public TextView statusText;
        public ImageView courtImage;

        public ViewHolder(View itemView) {
            super(itemView);
            courtNameText = itemView.findViewById(R.id.courtNameText);
            dateTimeText = itemView.findViewById(R.id.dateTimeText);
            statusText = itemView.findViewById(R.id.statusText);
            courtImage = itemView.findViewById(R.id.courtImage);
        }
    }

    public void updateBookings(List<Booking> newBookings) {
        this.bookings = new ArrayList<>(newBookings);
        sortBookings(); // Sort the new bookings before updating
        notifyDataSetChanged();
    }
} 