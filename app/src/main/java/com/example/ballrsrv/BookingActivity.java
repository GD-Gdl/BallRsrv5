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

import java.util.Calendar;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {
    private Button btnSelectTime, btnConfirmBooking, btnBack;
    private Button btnOneHour, btnTwoHours;
    private TextView tvSelectedTime, tvDuration, tvTotalPrice;
    private Calendar startTime;
    private int duration = 0;
    private static final int MAX_DURATION = 2;
    private static final int PRICE_PER_HOUR = 750;

    private DatabaseReference databaseRef;
    private String userIdentifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        databaseRef = FirebaseDatabase.getInstance("https://ballrsrv-a94eb-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Bookings");

        userIdentifier = getIntent().getStringExtra("email");

        if (userIdentifier == null || userIdentifier.isEmpty()) {
            Toast.makeText(this, "User info missing. Please log in again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        btnBack = findViewById(R.id.btnBack);
        btnOneHour = findViewById(R.id.btnOneHour);
        btnTwoHours = findViewById(R.id.btnTwoHours);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        tvDuration = findViewById(R.id.tvDuration);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);

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

            String bookingId = databaseRef.push().getKey();
            if (bookingId != null) {
                String userKey = userIdentifier.replace(".", "_");
                DatabaseReference userBookingRef = databaseRef.child(userKey).child(bookingId);

                Booking booking = new Booking(timeStr, duration, totalPrice);
                userBookingRef.setValue(booking);
            }

            Toast.makeText(this, "Booking confirmed for " + timeStr +
                    ", " + duration + " hour(s), Total: ₱" + totalPrice, Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, PaymentMenu.class);
            intent.putExtra("total_price", totalPrice);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Please select a valid duration (1-2 hours)", Toast.LENGTH_SHORT).show();
        }
    }
}
