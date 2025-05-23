package com.example.ballrsrv;

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

public class RequestsFragment extends Fragment implements BookingRequestAdapter.OnRequestActionListener {
    private static final String TAG = "RequestsFragment";
    private static final String DATABASE_URL = "https://ballrsrv-a94eb-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private BookingRequestAdapter adapter;
    private List<BookingRequest> bookingRequests;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requests, container, false);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance(DATABASE_URL).getReference();

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        bookingRequests = new ArrayList<>();
        adapter = new BookingRequestAdapter(bookingRequests, this);
        recyclerView.setAdapter(adapter);

        // Load booking requests
        loadBookingRequests();

        return view;
    }

    private void loadBookingRequests() {
        databaseReference.child("bookings")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    bookingRequests.clear();
                    
                    if (!snapshot.exists() || !snapshot.hasChildren()) {
                        showNoRequestsMessage();
                        return;
                    }

                    // Iterate through each user's bookings
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        for (DataSnapshot bookingSnapshot : userSnapshot.getChildren()) {
                            BookingRequest request = bookingSnapshot.getValue(BookingRequest.class);
                            if (request != null) {
                                // Only add requests that are pending
                                if ("pending".equals(request.getStatus())) {
                                    bookingRequests.add(request);
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Error loading requests: " + error.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void showNoRequestsMessage() {
        if (getContext() != null) {
            Toast.makeText(getContext(), "No booking requests found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAccept(BookingRequest request) {
        // Update request status in Firebase
        String requestId = request.getId();
        if (requestId != null) {
            databaseReference.child("booking_requests").child(requestId)
                .child("status").setValue("accepted")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Request accepted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error accepting request: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                });
        }
    }

    @Override
    public void onDeny(BookingRequest request) {
        // Update request status in Firebase
        String requestId = request.getId();
        if (requestId != null) {
            databaseReference.child("booking_requests").child(requestId)
                .child("status").setValue("denied")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Request denied", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error denying request: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                });
        }
    }
} 