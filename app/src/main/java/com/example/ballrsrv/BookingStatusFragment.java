package com.example.ballrsrv;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class BookingStatusFragment extends Fragment {
    private static final String TAG = "BookingStatusFragment";
    private static final String DATABASE_URL = "https://ballrsrv-a94eb-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private BookingStatusAdapter adapter;
    private List<Booking> bookingList;
    private SimpleDateFormat dateFormat;
    private boolean isGuest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_booking_status, container, false);

        try {
            // Get guest status from activity
            if (getActivity() != null) {
                isGuest = getActivity().getIntent().getBooleanExtra("isGuest", false);
            }

            // Initialize Firebase
            databaseReference = FirebaseDatabase.getInstance(DATABASE_URL).getReference();
            dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());

            // Get user email from activity
            String userEmail = getActivity() != null ? 
                getActivity().getIntent().getStringExtra("email") : null;

            // Initialize RecyclerView
            recyclerView = view.findViewById(R.id.recyclerViewBookings);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            bookingList = new ArrayList<>();
            adapter = new BookingStatusAdapter(bookingList);
            recyclerView.setAdapter(adapter);

            if (userEmail != null && !userEmail.equals("Guest User") && !isGuest) {
                // Convert email to valid Firebase key
                String userKey = userEmail.replace(".", "_");
                
                // Fetch user's bookings directly from their node
                databaseReference.child("bookings").child(userKey)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            try {
                                bookingList.clear();
                                
                                if (!snapshot.exists() || !snapshot.hasChildren()) {
                                    showNoBookingsMessage();
                                    return;
                                }

                                for (DataSnapshot bookingSnapshot : snapshot.getChildren()) {
                                    try {
                                        BookingRequest bookingRequest = bookingSnapshot.getValue(BookingRequest.class);
                                        if (bookingRequest != null) {
                                            // Check if booking is older than 24 hours
                                            if (isBookingOlderThan24Hours(bookingRequest)) {
                                                // Delete the old booking
                                                deleteOldBooking(userKey, bookingSnapshot.getKey());
                                                continue;
                                            }

                                            // Convert BookingRequest to Booking
                                            String courtName = bookingRequest.getBookingDetails().split(" - ")[0];
                                            Booking booking = new Booking(
                                                courtName.replace("Booking for ", ""), // Remove "Booking for " prefix
                                                bookingRequest.getDate(),
                                                bookingRequest.getTimeSlot(),
                                                bookingRequest.getStatus(),
                                                bookingRequest.getEmail()
                                            );
                                            bookingList.add(booking);
                                        }
                                    } catch (Exception e) {
                                        Log.e(TAG, "Error processing booking: " + e.getMessage());
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                Log.e(TAG, "Error in onDataChange: " + e.getMessage());
                                Toast.makeText(getContext(), "Error loading bookings: " + e.getMessage(), 
                                    Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "Database error: " + error.getMessage());
                            Toast.makeText(getContext(), "Error loading bookings: " + error.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                        }
                    });
            } else {
                showNoBookingsMessage();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreateView: " + e.getMessage());
            Toast.makeText(getContext(), "Error initializing bookings: " + e.getMessage(), 
                Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private boolean isBookingOlderThan24Hours(BookingRequest booking) {
        try {
            // Parse the booking date and time
            String dateTimeStr = booking.getDate() + " " + booking.getTimeSlot();
            Date bookingDateTime = dateFormat.parse(dateTimeStr);
            
            if (bookingDateTime == null) {
                Log.e(TAG, "Failed to parse booking date/time: " + dateTimeStr);
                return false;
            }

            // Get current time
            Date currentTime = new Date();
            
            // Calculate difference in hours
            long diffInMillis = currentTime.getTime() - bookingDateTime.getTime();
            long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis);
            
            return diffInHours >= 24;
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date: " + e.getMessage());
            return false;
        }
    }

    private void deleteOldBooking(String userKey, String bookingId) {
        try {
            databaseReference.child("bookings").child(userKey).child(bookingId)
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Old booking deleted successfully: " + bookingId);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting old booking: " + e.getMessage());
                });
        } catch (Exception e) {
            Log.e(TAG, "Error in deleteOldBooking: " + e.getMessage());
        }
    }

    private void showNoBookingsMessage() {
        if (getContext() != null) {
            Toast.makeText(getContext(), "No bookings found", Toast.LENGTH_SHORT).show();
        }
    }
} 