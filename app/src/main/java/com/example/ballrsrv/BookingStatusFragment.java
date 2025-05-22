package com.example.ballrsrv;

import android.content.Intent;
import android.os.Bundle;
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

public class BookingStatusFragment extends Fragment {
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private BookingStatusAdapter adapter;
    private List<Booking> bookingList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_booking_status, container, false);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Get user email from activity
        String userEmail = getActivity() != null ? 
            getActivity().getIntent().getStringExtra("email") : null;

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewBookings);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        bookingList = new ArrayList<>();
        adapter = new BookingStatusAdapter(bookingList);
        recyclerView.setAdapter(adapter);

        if (userEmail != null && !userEmail.equals("Guest User")) {
            // Convert email to valid Firebase key
            String userKey = userEmail.replace(".", "_");
            
            // Fetch user's bookings directly from their node
            databaseReference.child("bookings").child(userKey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        bookingList.clear();
                        
                        if (!snapshot.exists() || !snapshot.hasChildren()) {
                            showNoBookingsMessage();
                            return;
                        }

                        for (DataSnapshot bookingSnapshot : snapshot.getChildren()) {
                            BookingRequest bookingRequest = bookingSnapshot.getValue(BookingRequest.class);
                            if (bookingRequest != null) {
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
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error loading bookings: " + error.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    }
                });
        } else {
            // Redirect guest users to login using centralized method
            LoginActivity.logout(getContext());
        }

        return view;
    }

    private void showNoBookingsMessage() {
        Toast.makeText(getContext(), "No bookings found", Toast.LENGTH_SHORT).show();
    }
} 