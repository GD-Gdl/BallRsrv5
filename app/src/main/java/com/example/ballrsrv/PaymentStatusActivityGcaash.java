package com.example.ballrsrv;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PaymentStatusActivityGcaash extends AppCompatActivity {
    private DatabaseReference bookingsRef;
    private String bookingId;
    private int totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_gcash);

        // Initialize Firebase
        bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");
        
        // Get booking details from intent
        bookingId = getIntent().getStringExtra("booking_id");
        totalPrice = getIntent().getIntExtra("total_price", 0);

        Button btnBack = findViewById(R.id.btnBack);
        Button btnConfirmBooking = findViewById(R.id.btnConfirmBooking);

        btnBack.setOnClickListener(v -> finish());

        btnConfirmBooking.setOnClickListener(v -> {
            if (bookingId != null) {
                // Update booking status in Firebase
                DatabaseReference bookingRef = bookingsRef.child(bookingId);
                bookingRef.child("status").setValue("confirmed")
                    .addOnSuccessListener(aVoid -> {
                        bookingRef.child("paymentStatus").setValue("PENDING_GCASH")
                            .addOnSuccessListener(aVoid2 -> {
                                Toast.makeText(this, 
                                    "Booking confirmed! Please complete your GCash payment.", 
                                    Toast.LENGTH_LONG).show();
                                btnConfirmBooking.setEnabled(false);
                                
                                // Navigate to home after successful confirmation
                                Intent intent = new Intent(this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, 
                                    "Failed to update payment status: " + e.getMessage(), 
                                    Toast.LENGTH_LONG).show();
                            });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, 
                            "Failed to confirm booking: " + e.getMessage(), 
                            Toast.LENGTH_LONG).show();
                    });
            } else {
                Toast.makeText(this, 
                    "Error: Booking information not found", 
                    Toast.LENGTH_LONG).show();
            }
        });
    }
}
