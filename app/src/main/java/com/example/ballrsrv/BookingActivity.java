package com.example.ballrsrv;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {
    private Button btnSelectTime, btnConfirmBooking, btnBack;
    private Button btnOneHour, btnTwoHours;
    private TextView tvSelectedTime, tvDuration, tvTotalPrice;
    private Calendar startTime;
    private int duration = 0;
    private static final int MAX_DURATION = 2;
    private static final int PRICE_PER_HOUR = 750;

    private DatabaseReference pendingRequestsRef;
    private FirebaseAuth mAuth;
    private String userId;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        pendingRequestsRef = FirebaseDatabase.getInstance().getReference("pending_requests");
        
        // Get current user info
        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
            userName = mAuth.getCurrentUser().getDisplayName();
        } else {
            Toast.makeText(this, "Please log in to make a booking", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupClickListeners();
        startTime = Calendar.getInstance();
        updateUI();
    }

    private void initializeViews() {
        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        btnBack = findViewById(R.id.btnBack);
        btnOneHour = findViewById(R.id.btnOneHour);
        btnTwoHours = findViewById(R.id.btnTwoHours);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        tvDuration = findViewById(R.id.tvDuration);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
    }

    private void setupClickListeners() {
        btnSelectTime.setOnClickListener(v -> showTimePickerDialog());
        btnConfirmBooking.setOnClickListener(v -> confirmBooking());
        btnBack.setOnClickListener(v -> finish());

        btnOneHour.setOnClickListener(v -> {
            duration = 1;
            updateUI();
        });

        btnTwoHours.setOnClickListener(v -> {
            duration = 2;
            updateUI();
        });
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    startTime.set(Calendar.MINUTE, minute);
                    if (duration == 0) {
                        duration = 1;
                    }
                    updateUI();
                },
                startTime.get(Calendar.HOUR_OF_DAY),
                startTime.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void updateUI() {
        String timeStr = String.format(Locale.getDefault(), "%02d:%02d",
                startTime.get(Calendar.HOUR_OF_DAY),
                startTime.get(Calendar.MINUTE));
        tvSelectedTime.setText("Selected Time: " + timeStr);
        tvDuration.setText("Duration: " + duration + " hour" + (duration > 1 ? "s" : ""));
        int totalPrice = duration * PRICE_PER_HOUR;
        tvTotalPrice.setText("Total Price: ₱" + totalPrice);
        btnOneHour.setEnabled(duration != 1);
        btnTwoHours.setEnabled(duration != 2);
        btnConfirmBooking.setEnabled(duration > 0 && duration <= MAX_DURATION);
    }

    private void confirmBooking() {
        if (duration > 0 && duration <= MAX_DURATION) {
            int totalPrice = duration * PRICE_PER_HOUR;
            String timeStr = String.format(Locale.getDefault(), "%02d:%02d",
                    startTime.get(Calendar.HOUR_OF_DAY),
                    startTime.get(Calendar.MINUTE));
            
            // Get current date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String currentDate = dateFormat.format(new Date());

            // Create booking request
            String requestId = pendingRequestsRef.push().getKey();
            if (requestId != null) {
                BookingRequest request = new BookingRequest(
                    requestId,
                    userName,
                    "Court booking request",
                    "pending",
                    currentDate,
                    timeStr
                );
                request.setDuration(duration);
                request.setTotalPrice(totalPrice);
                request.setPaymentStatus("pending");

                // Save to Firebase
                pendingRequestsRef.child(requestId).setValue(request)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Booking request submitted successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, PaymentMenu.class);
                        intent.putExtra("total_price", totalPrice);
                        intent.putExtra("booking_id", requestId);
                        intent.putExtra("duration", duration);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to submit booking request: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    });
            }
        } else {
            Toast.makeText(this, "Please select a valid duration (1-2 hours)", Toast.LENGTH_SHORT).show();
        }
    }
}
