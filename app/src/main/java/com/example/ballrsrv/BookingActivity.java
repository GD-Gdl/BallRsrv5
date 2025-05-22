package com.example.ballrsrv;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {
    private static final String TAG = "BookingActivity";
    private Button btnSelectTime, btnConfirmBooking, btnBack;
    private Button btnOneHour, btnTwoHours;
    private TextView tvSelectedTime, tvDuration, tvTotalPrice, tvCourtName;
    private Calendar startTime;
    private int duration = 0;
    private static final int MAX_DURATION = 2;
    private static final int PRICE_PER_HOUR = 750;

    private DatabaseReference databaseReference;
    private String userEmail;
    private String courtName;
    private SimpleDateFormat timeFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Get user email and court name from intent
        userEmail = getIntent().getStringExtra("email");
        courtName = getIntent().getStringExtra("courtName");
        
        if (userEmail == null) {
            Log.e(TAG, "User email not found in intent");
            Toast.makeText(this, "Error: User email not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (courtName == null) {
            Toast.makeText(this, "Please select a court", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();
        timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        // Initialize back button
        btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                Log.d(TAG, "Back button clicked");
                Intent intent = new Intent(BookingActivity.this, HomeActivity.class);
                intent.putExtra("email", userEmail);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        } else {
            Log.e(TAG, "Back button not found in layout");
        }

        initializeViews();
        setupClickListeners();
        startTime = Calendar.getInstance();
        updateUI();
    }

    private void initializeViews() {
        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        btnOneHour = findViewById(R.id.btnOneHour);
        btnTwoHours = findViewById(R.id.btnTwoHours);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        tvDuration = findViewById(R.id.tvDuration);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvCourtName = findViewById(R.id.tvCourtName);

        // Set court name
        tvCourtName.setText("Booking for: " + courtName);
    }

    private void setupClickListeners() {
        btnSelectTime.setOnClickListener(v -> showTimePickerDialog());
        btnConfirmBooking.setOnClickListener(v -> checkAndConfirmBooking());
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
        tvSelectedTime.setText("Selected Time: " + timeFormat.format(startTime.getTime()));
        tvDuration.setText("Duration: " + duration + " hour(s)");
        tvTotalPrice.setText("Total Price: ₱" + (duration * PRICE_PER_HOUR));
        
        // Enable/disable confirm button based on duration
        btnConfirmBooking.setEnabled(duration > 0);
    }

    private void checkAndConfirmBooking() {
        if (duration == 0) {
            Toast.makeText(this, "Please select duration", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the selected time slot
        String selectedTime = timeFormat.format(startTime.getTime());
        String selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Check for existing bookings across all users
        databaseReference.child("bookings")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean isTimeSlotAvailable = true;
                    String selectedTimeSlot = selectedTime;

                    // Check all bookings for the selected court and time
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        for (DataSnapshot bookingSnapshot : userSnapshot.getChildren()) {
                            BookingRequest booking = bookingSnapshot.getValue(BookingRequest.class);
                            if (booking != null && 
                                booking.getBookingDetails().contains(courtName) &&
                                booking.getDate().equals(selectedDate) &&
                                booking.getTimeSlot().equals(selectedTimeSlot) &&
                                !booking.getStatus().equals("denied")) {
                                
                                isTimeSlotAvailable = false;
                                break;
                            }
                        }
                        if (!isTimeSlotAvailable) break;
                    }

                    if (isTimeSlotAvailable) {
                        // Double check if the time slot is still available
                        databaseReference.child("bookings")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    boolean stillAvailable = true;
                                    
                                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                        for (DataSnapshot bookingSnapshot : userSnapshot.getChildren()) {
                                            BookingRequest booking = bookingSnapshot.getValue(BookingRequest.class);
                                            if (booking != null && 
                                                booking.getBookingDetails().contains(courtName) &&
                                                booking.getDate().equals(selectedDate) &&
                                                booking.getTimeSlot().equals(selectedTimeSlot) &&
                                                !booking.getStatus().equals("denied")) {
                                                
                                                stillAvailable = false;
                                                break;
                                            }
                                        }
                                        if (!stillAvailable) break;
                                    }

                                    if (stillAvailable) {
                                        confirmBooking();
                                    } else {
                                        Toast.makeText(BookingActivity.this, 
                                            "This time slot was just booked by another user. Please select another time.", 
                                            Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(BookingActivity.this, 
                                        "Error checking availability: " + error.getMessage(), 
                                        Toast.LENGTH_SHORT).show();
                                }
                            });
                    } else {
                        Toast.makeText(BookingActivity.this, 
                            "This time slot is already booked. Please select another time.", 
                            Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(BookingActivity.this, 
                        "Error checking availability: " + error.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void confirmBooking() {
        // Create booking request
        String userKey = userEmail.replace(".", "_");
        DatabaseReference userBookingsRef = databaseReference.child("bookings").child(userKey);
        String requestId = userBookingsRef.push().getKey();
        
        BookingRequest request = new BookingRequest();
        request.setId(requestId);
        request.setEmail(userEmail);
        request.setUserName(userEmail.split("@")[0]); // Use part before @ as username
        request.setDate(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        request.setTimeSlot(timeFormat.format(startTime.getTime()));
        request.setDuration(duration);
        request.setTotalPrice(duration * PRICE_PER_HOUR);
        request.setStatus("pending");
        request.setBookingDetails("Booking for " + courtName + " - " + duration + " hour(s)");

        // Save to database under user's bookings
        userBookingsRef.child(requestId).setValue(request)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Booking request submitted successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, PaymentMenu.class);
                    intent.putExtra("booking_id", requestId);
                    intent.putExtra("total_price", duration * PRICE_PER_HOUR);
                    intent.putExtra("court_name", courtName);
                    intent.putExtra("email", userEmail);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Failed to submit booking request", Toast.LENGTH_SHORT).show();
                }
            });
    }
}
