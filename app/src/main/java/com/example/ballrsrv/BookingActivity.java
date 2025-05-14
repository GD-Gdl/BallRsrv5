package com.example.ballrsrv;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {
    private Button btnSelectTime, btnConfirmBooking, btnBack;
    private Button btnOneHour, btnTwoHours;
    private TextView tvSelectedTime, tvDuration, tvTotalPrice;
    private Calendar startTime;
    private int duration = 0; // in hours
    private static final int MAX_DURATION = 2; // maximum 2 hours
    private static final int PRICE_PER_HOUR = 750;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Initialize views
        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        btnBack = findViewById(R.id.btnBack);
        btnOneHour = findViewById(R.id.btnOneHour);
        btnTwoHours = findViewById(R.id.btnTwoHours);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        tvDuration = findViewById(R.id.tvDuration);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);

        // Set click listeners
        btnSelectTime.setOnClickListener(v -> showTimePickerDialog());
        btnConfirmBooking.setOnClickListener(v -> confirmBooking());
        btnBack.setOnClickListener(v -> finish());

        // Set duration button click listeners
        btnOneHour.setOnClickListener(v -> {
            duration = 1;
            updateUI();
        });

        btnTwoHours.setOnClickListener(v -> {
            duration = 2;
            updateUI();
        });

        // Initialize start time
        startTime = Calendar.getInstance();
        updateUI();
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    startTime.set(Calendar.MINUTE, minute);
                    if (duration == 0) {
                        duration = 1; // Default to 1 hour if no duration selected
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
        // Update selected time
        String timeStr = String.format(Locale.getDefault(), "%02d:%02d",
                startTime.get(Calendar.HOUR_OF_DAY),
                startTime.get(Calendar.MINUTE));
        tvSelectedTime.setText("Selected Time: " + timeStr);

        // Update duration
        tvDuration.setText("Duration: " + duration + " hour" + (duration > 1 ? "s" : ""));

        // Update total price
        int totalPrice = duration * PRICE_PER_HOUR;
        tvTotalPrice.setText("Total Price: ₱" + totalPrice);

        // Update duration buttons state
        btnOneHour.setEnabled(duration != 1);
        btnTwoHours.setEnabled(duration != 2);

        // Enable/disable confirm button based on duration and time selection
        btnConfirmBooking.setEnabled(duration > 0 && duration <= MAX_DURATION);
    }

    private void confirmBooking() {
        if (duration > 0 && duration <= MAX_DURATION) {
            int totalPrice = duration * PRICE_PER_HOUR;
            String message = String.format("Booking confirmed for %02d:%02d, %d hour(s), Total: ₱%d",
                    startTime.get(Calendar.HOUR_OF_DAY),
                    startTime.get(Calendar.MINUTE),
                    duration,
                    totalPrice);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            // Start PaymentModeActivity and pass the total price
            Intent intent = new Intent(this, PaymentMenu.class);
            intent.putExtra("total_price", totalPrice);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Please select a valid duration (1-2 hours)", Toast.LENGTH_SHORT).show();
        }
    }
}
