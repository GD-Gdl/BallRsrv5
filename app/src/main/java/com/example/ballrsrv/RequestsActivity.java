package com.example.ballrsrv;

import android.content.Intent;
import android.os.Bundle;
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
    private RecyclerView recyclerView;
    private BookingRequestAdapter adapter;
    private List<BookingRequest> requests;
    private DatabaseReference databaseReference;
    private ValueEventListener requestsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        recyclerView = findViewById(R.id.recyclerView);
        requests = new ArrayList<>();
        adapter = new BookingRequestAdapter(requests, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Start listening for requests
        setupRequestsListener();

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupRequestsListener() {
        requestsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                requests.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot bookingSnapshot : userSnapshot.getChildren()) {
                        BookingRequest request = bookingSnapshot.getValue(BookingRequest.class);
                        if (request != null && "pending".equals(request.getStatus())) {
                            request.setId(bookingSnapshot.getKey());
                            requests.add(request);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RequestsActivity.this, 
                    "Failed to load requests: " + databaseError.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        };
        databaseReference.child("bookings").addValueEventListener(requestsListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestsListener != null) {
            databaseReference.child("bookings").removeEventListener(requestsListener);
        }
    }

    @Override
    public void onAccept(BookingRequest request) {
        String userKey = request.getEmail().replace(".", "_");
        databaseReference.child("bookings").child(userKey).child(request.getId())
            .child("status").setValue("accepted")
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Booking request accepted", Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to accept booking: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    @Override
    public void onDeny(BookingRequest request) {
        String userKey = request.getEmail().replace(".", "_");
        databaseReference.child("bookings").child(userKey).child(request.getId())
            .child("status").setValue("denied")
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Booking request denied", Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to deny booking: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }
}

