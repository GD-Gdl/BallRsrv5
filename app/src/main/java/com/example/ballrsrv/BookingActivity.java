package com.example.ballrsrv;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Get user email from intent
        userEmail = getIntent().getStringExtra("email");
        if (userEmail == null) {
            Toast.makeText(this, "Please log in to make a booking", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize Firebase
        pendingRequestsRef = FirebaseDatabase.getInstance().getReference("pending_requests");

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
        btnOneHour.setOnClickListener(v -> setDuration(1));
        btnTwoHours.setOnClickListener(v -> setDuration(2));
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
            this,
            (view, hourOfDay, minute) -> {
                startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                startTime.set(Calendar.MINUTE, minute);
                updateUI();
            },
            startTime.get(Calendar.HOUR_OF_DAY),
            startTime.get(Calendar.MINUTE),
            true
        );
        timePickerDialog.show();
    }

    private void setDuration(int hours) {
        duration = hours;
        updateUI();
    }

    private void updateUI() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        tvSelectedTime.setText("Selected Time: " + timeFormat.format(startTime.getTime()));
        tvDuration.setText("Duration: " + duration + " hour(s)");
        tvTotalPrice.setText("Total Price: ₱" + (duration * PRICE_PER_HOUR));
    }

    private void confirmBooking() {
        if (duration == 0) {
            Toast.makeText(this, "Please select duration", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create booking request
        String requestId = pendingRequestsRef.push().getKey();
        BookingRequest request = new BookingRequest();
        request.setId(requestId);
        request.setEmail(userEmail);
        request.setDate(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        request.setTimeSlot(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(startTime.getTime()));
        request.setDuration(duration);
        request.setTotalPrice(duration * PRICE_PER_HOUR);
        request.setStatus("pending");
        request.setBookingDetails("Court booking for " + duration + " hour(s)");

        // Save to database
        pendingRequestsRef.child(requestId).setValue(request)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Booking request submitted successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, PaymentMenu.class));
                    finish();
                } else {
                    Toast.makeText(this, "Failed to submit booking request", Toast.LENGTH_SHORT).show();
                }
            });
    }
}
