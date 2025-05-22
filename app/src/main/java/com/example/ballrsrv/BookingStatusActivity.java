package com.example.ballrsrv;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.*;
import java.util.Comparator;

public class BookingStatusActivity extends AppCompatActivity {
    private static final String TAG = "BookingStatusActivity";
    private RecyclerView recyclerView;
    private BookingStatusAdapter adapter;
    private List<BookingRequest> bookings;
    private DatabaseReference bookingsRef;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_status);

        // Get user email from intent
        userEmail = getIntent().getStringExtra("email");
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Error: User email not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase
        bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");
        
        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Initialize bookings list
        bookings = new ArrayList<>();
        adapter = new BookingStatusAdapter(bookings);
        recyclerView.setAdapter(adapter);
        
        // Load bookings
        loadBookings();
    }

    private void loadBookings() {
        // Convert email to valid Firebase key
        String userKey = userEmail.replace(".", "_");
        DatabaseReference userBookingsRef = bookingsRef.child(userKey);

        userBookingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                bookings.clear();
                
                if (!snapshot.exists()) {
                    Toast.makeText(BookingStatusActivity.this, 
                        "No bookings found", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot bookingSnapshot : snapshot.getChildren()) {
                    BookingRequest booking = bookingSnapshot.getValue(BookingRequest.class);
                    if (booking != null) {
                        booking.setId(bookingSnapshot.getKey());
                        bookings.add(booking);
                    }
                }

                // Sort bookings by date and time
                Collections.sort(bookings, new Comparator<BookingRequest>() {
                    @Override
                    public int compare(BookingRequest b1, BookingRequest b2) {
                        int dateCompare = b1.getDate().compareTo(b2.getDate());
                        if (dateCompare == 0) {
                            return b1.getTimeSlot().compareTo(b2.getTimeSlot());
                        }
                        return dateCompare;
                    }
                });

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Error loading bookings: " + error.getMessage());
                Toast.makeText(BookingStatusActivity.this, 
                    "Error loading bookings", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bookingsRef != null && userEmail != null) {
            String userKey = userEmail.replace(".", "_");
            DatabaseReference userBookingsRef = bookingsRef.child(userKey);
            userBookingsRef.removeEventListener((ValueEventListener) adapter);
        }
    }
} 