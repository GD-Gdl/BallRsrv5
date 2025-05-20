package com.example.ballrsrv;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class BookingStatusActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BookingStatusAdapter adapter;
    private List<BookingRequest> requests;
    private DatabaseReference acceptedRequestsRef;
    private DatabaseReference pendingRequestsRef;
    private DatabaseReference deniedRequestsRef;
    private ValueEventListener requestsListener;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_status);

        // Initialize Firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
        } else {
            Toast.makeText(this, "Please log in to view booking status", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase Database references
        pendingRequestsRef = FirebaseDatabase.getInstance().getReference("pending_requests");
        acceptedRequestsRef = FirebaseDatabase.getInstance().getReference("accepted_requests");
        deniedRequestsRef = FirebaseDatabase.getInstance().getReference("denied_requests");

        recyclerView = findViewById(R.id.recyclerView);
        requests = new ArrayList<>();
        adapter = new BookingStatusAdapter(requests);
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
                
                // Listen to all three references
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BookingRequest request = snapshot.getValue(BookingRequest.class);
                    if (request != null && request.getUserName().equals(userId)) {
                        request.setId(snapshot.getKey());
                        requests.add(request);
                    }
                }
                
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(BookingStatusActivity.this, 
                    "Failed to load booking status: " + databaseError.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        };

        // Add listeners to all three references
        pendingRequestsRef.addValueEventListener(requestsListener);
        acceptedRequestsRef.addValueEventListener(requestsListener);
        deniedRequestsRef.addValueEventListener(requestsListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestsListener != null) {
            pendingRequestsRef.removeEventListener(requestsListener);
            acceptedRequestsRef.removeEventListener(requestsListener);
            deniedRequestsRef.removeEventListener(requestsListener);
        }
    }
} 