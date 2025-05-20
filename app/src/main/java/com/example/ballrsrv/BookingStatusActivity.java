package com.example.ballrsrv;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class BookingStatusActivity extends AppCompatActivity {
    private static final String TAG = "BookingStatusActivity";
    private RecyclerView recyclerViewBooked;
    private BookingStatusAdapter adapter;
    private List<BookingRequest> bookedRequests;
    private DatabaseReference databaseReference;
    private ValueEventListener acceptedListener;
    private ValueEventListener pendingListener;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_status);

        try {
            initializeViews();
            initializeFirebase();
            setupRecyclerView();
            setupRequestsListeners();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error initializing booking status", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeViews() {
        recyclerViewBooked = findViewById(R.id.recyclerViewBooked);
        if (recyclerViewBooked == null) {
            throw new IllegalStateException("RecyclerView not found in layout");
        }

        userEmail = getIntent().getStringExtra("userEmail");
        if (userEmail == null || userEmail.isEmpty()) {
            throw new IllegalStateException("User email not found in intent");
        }
    }

    private void initializeFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        if (databaseReference == null) {
            throw new IllegalStateException("Failed to initialize Firebase database reference");
        }
    }

    private void setupRecyclerView() {
        bookedRequests = new ArrayList<>();
        adapter = new BookingStatusAdapter(bookedRequests);
        recyclerViewBooked.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBooked.setAdapter(adapter);
    }

    private void setupRequestsListeners() {
        try {
            String userKey = userEmail.replace(".", "_");
            setupAcceptedRequestsListener(userKey);
            setupPendingRequestsListener(userKey);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up listeners: " + e.getMessage(), e);
            Toast.makeText(this, "Error setting up booking status", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupAcceptedRequestsListener(String userKey) {
        acceptedListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    processAcceptedRequests(snapshot);
                } catch (Exception e) {
                    Log.e(TAG, "Error processing accepted requests: " + e.getMessage(), e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading accepted requests: " + error.getMessage());
                Toast.makeText(BookingStatusActivity.this, 
                    "Error loading accepted requests", Toast.LENGTH_SHORT).show();
            }
        };

        databaseReference.child("accepted_requests").child(userKey)
            .addValueEventListener(acceptedListener);
    }

    private void setupPendingRequestsListener(String userKey) {
        pendingListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    processPendingRequests(snapshot);
                } catch (Exception e) {
                    Log.e(TAG, "Error processing pending requests: " + e.getMessage(), e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading pending requests: " + error.getMessage());
                Toast.makeText(BookingStatusActivity.this, 
                    "Error loading pending requests", Toast.LENGTH_SHORT).show();
            }
        };

        databaseReference.child("pending_requests").child(userKey)
            .addValueEventListener(pendingListener);
    }

    private void processAcceptedRequests(DataSnapshot snapshot) {
        if (!snapshot.exists()) {
            Log.d(TAG, "No accepted requests found for user: " + userEmail);
            return;
        }

        // Clear only accepted requests
        bookedRequests.removeIf(request -> "accepted".equalsIgnoreCase(request.getStatus()));
        
        for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
            BookingRequest request = requestSnapshot.getValue(BookingRequest.class);
            if (request != null) {
                request.setStatus("accepted");
                bookedRequests.add(request);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void processPendingRequests(DataSnapshot snapshot) {
        if (!snapshot.exists()) {
            Log.d(TAG, "No pending requests found for user: " + userEmail);
            return;
        }

        // Clear only pending requests
        bookedRequests.removeIf(request -> "pending".equalsIgnoreCase(request.getStatus()));
        
        for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
            BookingRequest request = requestSnapshot.getValue(BookingRequest.class);
            if (request != null) {
                request.setStatus("pending");
                bookedRequests.add(request);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            removeListeners();
        } catch (Exception e) {
            Log.e(TAG, "Error in onDestroy: " + e.getMessage(), e);
        }
    }

    private void removeListeners() {
        if (databaseReference != null && userEmail != null) {
            String userKey = userEmail.replace(".", "_");
            if (acceptedListener != null) {
                databaseReference.child("accepted_requests").child(userKey)
                    .removeEventListener(acceptedListener);
            }
            if (pendingListener != null) {
                databaseReference.child("pending_requests").child(userKey)
                    .removeEventListener(pendingListener);
            }
        }
    }
} 