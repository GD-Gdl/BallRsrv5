package com.example.ballrsrv;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.text.SimpleDateFormat;

public class PaymentStatusActivityGcaash extends AppCompatActivity {
    private static final String DATABASE_URL = "https://ballrsrv-a94eb-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private DatabaseReference bookingsRef;
    private String bookingId;
    private int totalPrice;
    private Button btnConfirmReference;
    private EditText etReferenceCode;
    private String savedReferenceCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_gcash);

        // Initialize Firebase
        bookingsRef = FirebaseDatabase.getInstance(DATABASE_URL).getReference("bookings");

        // Get booking details from intent
        bookingId = getIntent().getStringExtra("booking_id");
        totalPrice = getIntent().getIntExtra("total_price", 0);

        Button btnBack = findViewById(R.id.btnBack);
        Button btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        btnConfirmReference = findViewById(R.id.btnConfirmReference);
        etReferenceCode = findViewById(R.id.etReferenceCode);

        btnBack.setOnClickListener(v -> finish());

        btnConfirmReference.setOnClickListener(v -> {
            String referenceCode = etReferenceCode.getText().toString().trim();
            if (referenceCode.isEmpty()) {
                Toast.makeText(this, "Please enter a reference code", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get user email from intent
            String userEmail = getIntent().getStringExtra("email");
            if (userEmail == null) {
                Toast.makeText(this, "Error: User email not found", Toast.LENGTH_LONG).show();
                return;
            }

            // Save reference code to user's booking in Firebase
            String userKey = userEmail.replace(".", "_");
            DatabaseReference bookingRef = bookingsRef.child(userKey).child(bookingId);

            bookingRef.child("referenceCode").setValue(referenceCode)
                    .addOnSuccessListener(aVoid -> {
                        savedReferenceCode = referenceCode; // Store the saved reference code
                        Toast.makeText(this, "Reference code saved successfully", Toast.LENGTH_SHORT).show();
                        etReferenceCode.setText("");
                        btnConfirmBooking.setEnabled(true);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to save reference code: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        });

        btnConfirmBooking.setOnClickListener(v -> {
            if (bookingId != null) {
                // Get user email from intent
                String userEmail = getIntent().getStringExtra("email");
                if (userEmail == null) {
                    Toast.makeText(this, "Error: User email not found", Toast.LENGTH_LONG).show();
                    return;
                }

                // Check if reference code is provided
                String referenceCode = etReferenceCode.getText().toString().trim();
                if (referenceCode.isEmpty()) {
                    Toast.makeText(this, "Please enter and confirm your reference code first", Toast.LENGTH_LONG).show();
                    return;
                }

                // Check if the reference code matches the saved one
                if (!referenceCode.equals(savedReferenceCode)) {
                    Toast.makeText(this, "Reference code does not match. Please enter the same reference code.", Toast.LENGTH_LONG).show();
                    return;
                }

                // Update booking in Firebase
                String userKey = userEmail.replace(".", "_");
                DatabaseReference bookingRef = bookingsRef.child(userKey).child(bookingId);

                // Update booking data
                Map<String, Object> updates = new HashMap<>();
                updates.put("status", "pending");
                updates.put("paymentMethod", "gcash");
                updates.put("totalPrice", totalPrice);
                updates.put("referenceCode", referenceCode);

                bookingRef.updateChildren(updates)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this,
                                    "Booking confirmed! Please complete your GCash payment.",
                                    Toast.LENGTH_LONG).show();
                            btnConfirmBooking.setEnabled(false);

                            // Navigate to home after successful confirmation
                            Intent intent = new Intent(this, HomeActivity.class);
                            intent.putExtra("email", userEmail);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this,
                                    "Failed to update booking: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        });
            } else {
                Toast.makeText(this,
                        "Error: Booking information not found",
                        Toast.LENGTH_LONG).show();
            }
        });

        // Initially disable the confirm booking button until reference code is confirmed
        btnConfirmBooking.setEnabled(false);
    }
}