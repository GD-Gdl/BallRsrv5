package com.example.ballrsrv;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import java.util.ArrayList;
import java.util.List;

public class RequestsActivity extends AppCompatActivity implements BookingRequestAdapter.OnRequestActionListener {
    private static final String TAG = "RequestsActivity";
    private static final String DATABASE_URL = "https://ballrsrv-a94eb-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private RecyclerView recyclerView;
    private BookingRequestAdapter adapter;
    private List<BookingRequest> requests;
    private DatabaseReference databaseReference;
    private ValueEventListener requestsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        try {
            // Initialize Firebase Database reference
            databaseReference = FirebaseDatabase.getInstance(DATABASE_URL).getReference();

            recyclerView = findViewById(R.id.recyclerView);
            requests = new ArrayList<>();
            adapter = new BookingRequestAdapter(requests, this);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            // Start listening for requests
            setupRequestsListener();

            Button btnBack = findViewById(R.id.btnBack);
            btnBack.setOnClickListener(v -> finish());
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            Toast.makeText(this, "Error initializing requests: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupRequestsListener() {
        try {
            requestsListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        requests.clear();
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            for (DataSnapshot bookingSnapshot : userSnapshot.getChildren()) {
                                try {
                                    BookingRequest request = bookingSnapshot.getValue(BookingRequest.class);
                                    if (request != null && "pending".equals(request.getStatus())) {
                                        request.setId(bookingSnapshot.getKey());
                                        requests.add(request);
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error processing booking: " + e.getMessage());
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e(TAG, "Error in onDataChange: " + e.getMessage());
                        Toast.makeText(RequestsActivity.this, 
                            "Error loading requests: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "Database error: " + databaseError.getMessage());
                    Toast.makeText(RequestsActivity.this, 
                        "Failed to load requests: " + databaseError.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                }
            };
            databaseReference.child("bookings").addValueEventListener(requestsListener);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up listener: " + e.getMessage());
            Toast.makeText(this, "Error setting up requests listener: " + e.getMessage(), 
                Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (requestsListener != null) {
                databaseReference.child("bookings").removeEventListener(requestsListener);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onDestroy: " + e.getMessage());
        }
    }

    @Override
    public void onAccept(BookingRequest request) {
        try {
            String userKey = request.getEmail().replace(".", "_");
            databaseReference.child("bookings").child(userKey).child(request.getId())
                .child("status").setValue("accepted")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Booking request accepted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error accepting booking: " + e.getMessage());
                    Toast.makeText(this, "Failed to accept booking: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                });
        } catch (Exception e) {
            Log.e(TAG, "Error in onAccept: " + e.getMessage());
            Toast.makeText(this, "Error accepting booking: " + e.getMessage(), 
                Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeny(BookingRequest request) {
        try {
            String userKey = request.getEmail().replace(".", "_");
            databaseReference.child("bookings").child(userKey).child(request.getId())
                .child("status").setValue("denied")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Booking request denied", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error denying booking: " + e.getMessage());
                    Toast.makeText(this, "Failed to deny booking: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                });
        } catch (Exception e) {
            Log.e(TAG, "Error in onDeny: " + e.getMessage());
            Toast.makeText(this, "Error denying booking: " + e.getMessage(), 
                Toast.LENGTH_SHORT).show();
        }
    }
}

