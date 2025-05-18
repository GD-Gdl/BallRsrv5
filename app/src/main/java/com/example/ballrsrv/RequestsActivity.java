package com.example.ballrsrv;

import android.os.Bundle;
import android.widget.Toast;
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
    private DatabaseReference acceptedRequestsRef;
    private DatabaseReference pendingRequestsRef;
    private ValueEventListener requestsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        // Initialize Firebase Database references
        pendingRequestsRef = FirebaseDatabase.getInstance().getReference("pending_requests");
        acceptedRequestsRef = FirebaseDatabase.getInstance().getReference("accepted_requests");

        recyclerView = findViewById(R.id.recyclerView);
        requests = new ArrayList<>();
        adapter = new BookingRequestAdapter(requests, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Start listening for requests
        setupRequestsListener();
    }

    private void setupRequestsListener() {
        requestsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                requests.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BookingRequest request = snapshot.getValue(BookingRequest.class);
                    if (request != null) {
                        request.setId(snapshot.getKey());
                        requests.add(request);
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
        pendingRequestsRef.addValueEventListener(requestsListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestsListener != null) {
            pendingRequestsRef.removeEventListener(requestsListener);
        }
    }

    @Override
    public void onAccept(BookingRequest request) {
        // Remove from pending requests
        pendingRequestsRef.child(request.getId()).removeValue()
            .addOnSuccessListener(aVoid -> {
                // Add to accepted requests
                acceptedRequestsRef.child(request.getId()).setValue(request)
                    .addOnSuccessListener(aVoid2 -> {
                        Toast.makeText(this, "Court reserved, booking accepted.", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to save accepted booking: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to process booking: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    @Override
    public void onDeny(BookingRequest request) {
        // Remove from pending requests
        pendingRequestsRef.child(request.getId()).removeValue()
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Booking request denied", Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to deny booking: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }
}

