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
    private Button btnSelectTime, btnBack;
    private Button btnOneHour, btnTwoHours, btnConfirmBooking;
    private TextView tvSelectedTime, tvDuration, tvTotalPrice, tvCourtName;
    private Calendar startTime;
    private int duration = 0;
    private static final int MAX_DURATION = 2;
    private static final int PRICE_PER_HOUR = 750;

    private DatabaseReference pendingRequestsRef;
    private String userEmail;
    private String courtName;
    private boolean isReturningFromPayment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Get user email and court name from intent
        userEmail = getIntent().getStringExtra("email");
        courtName = getIntent().getStringExtra("courtName");
        
        if (userEmail == null) {
            Toast.makeText(this, "Please log in to make a booking", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        if (courtName == null) {
            Toast.makeText(this, "Please select a court", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Check if returning from payment menu
        isReturningFromPayment = getIntent().getBooleanExtra("returning_from_payment", false);

        // Initialize Firebase
        pendingRequestsRef = FirebaseDatabase.getInstance().getReference("pending_requests");

        initializeViews();
        setupClickListeners();
        startTime = Calendar.getInstance();
        updateUI();

        // If returning from payment, show message
        if (isReturningFromPayment) {
            Toast.makeText(this, "Please select your booking details again", Toast.LENGTH_LONG).show();
        }
    }

    private void initializeViews() {
        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnBack = findViewById(R.id.btnBack);
        btnOneHour = findViewById(R.id.btnOneHour);
        btnTwoHours = findViewById(R.id.btnTwoHours);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        tvDuration = findViewById(R.id.tvDuration);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvCourtName = findViewById(R.id.tvCourtName);

        // Set court name
        tvCourtName.setText("Booking for: " + courtName);
        
        // Initially disable confirm button
        btnConfirmBooking.setEnabled(false);
    }

    private void setupClickListeners() {
        btnSelectTime.setOnClickListener(v -> showTimePickerDialog());
        btnBack.setOnClickListener(v -> finish());
        btnOneHour.setOnClickListener(v -> setDuration(1));
        btnTwoHours.setOnClickListener(v -> setDuration(2));
        btnConfirmBooking.setOnClickListener(v -> proceedToPayment());
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
            this,
            (view, hourOfDay, minute) -> {
                startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                startTime.set(Calendar.MINUTE, minute);
                updateUI();
                checkConfirmButtonState();
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
        checkConfirmButtonState();
    }

    private void checkConfirmButtonState() {
        // Enable confirm button only if both time and duration are selected
        boolean timeSelected = !tvSelectedTime.getText().toString().contains("Not selected");
        boolean durationSelected = duration > 0;
        btnConfirmBooking.setEnabled(timeSelected && durationSelected);
    }

    private void updateUI() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        tvSelectedTime.setText("Selected Time: " + timeFormat.format(startTime.getTime()));
        tvDuration.setText("Duration: " + duration + " hour(s)");
        tvTotalPrice.setText("Total Price: ₱" + (duration * PRICE_PER_HOUR));
    }

    private void proceedToPayment() {
        if (duration == 0) {
            Toast.makeText(this, "Please select duration", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create booking request
        String requestId = pendingRequestsRef.push().getKey();
        BookingRequest request = new BookingRequest();
        request.setId(requestId);
        request.setEmail(userEmail);
        request.setUserName(userEmail.split("@")[0]); // Use part before @ as username
        request.setDate(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        request.setTimeSlot(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(startTime.getTime()));
        request.setDuration(duration);
        request.setTotalPrice(duration * PRICE_PER_HOUR);
        request.setStatus("pending");
        request.setBookingDetails("Booking for " + courtName + " - " + duration + " hour(s)");

        // Save to database and proceed to payment
        pendingRequestsRef.child(requestId).setValue(request)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(this, PaymentMenu.class);
                    intent.putExtra("booking_id", requestId);
                    intent.putExtra("total_price", duration * PRICE_PER_HOUR);
                    intent.putExtra("court_name", courtName);
                    intent.putExtra("email", userEmail);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Failed to create booking request", Toast.LENGTH_SHORT).show();
                }
            });
    }
}
