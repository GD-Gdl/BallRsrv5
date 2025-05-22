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
    private static final String DATABASE_URL = "https://ballrsrv-a94eb-default-rtdb.asia-southeast1.firebasedatabase.app/";
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

        try {
            Log.d(TAG, "Starting BookingActivity initialization");
            
            // Get user email and court name from intent
            userEmail = getIntent().getStringExtra("email");
            courtName = getIntent().getStringExtra("courtName");
            
            Log.d(TAG, "User email: " + (userEmail != null ? userEmail : "null"));
            Log.d(TAG, "Court name: " + (courtName != null ? courtName : "null"));
            
            if (userEmail == null) {
                Log.e(TAG, "User email not found in intent");
                Toast.makeText(this, "Error: User email not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            if (courtName == null) {
                Log.e(TAG, "Court name not found in intent");
                Toast.makeText(this, "Please select a court", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Initialize Firebase with URL
            Log.d(TAG, "Initializing Firebase");
            databaseReference = FirebaseDatabase.getInstance(DATABASE_URL).getReference();
            
            // Initialize time format
            timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Log.d(TAG, "Time format initialized");

            // Initialize views first
            Log.d(TAG, "Initializing views");
            initializeViews();
            
            // Initialize back button
            Log.d(TAG, "Setting up back button");
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

            // Setup remaining components
            Log.d(TAG, "Setting up click listeners");
            setupClickListeners();
            
            Log.d(TAG, "Initializing calendar");
            startTime = Calendar.getInstance();
            
            Log.d(TAG, "Updating UI");
            updateUI();
            
            Log.d(TAG, "BookingActivity initialization completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error initializing booking: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeViews() {
        try {
            Log.d(TAG, "Finding views in layout");
            btnSelectTime = findViewById(R.id.btnSelectTime);
            btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
            btnOneHour = findViewById(R.id.btnOneHour);
            btnTwoHours = findViewById(R.id.btnTwoHours);
            tvSelectedTime = findViewById(R.id.tvSelectedTime);
            tvDuration = findViewById(R.id.tvDuration);
            tvTotalPrice = findViewById(R.id.tvTotalPrice);
            tvCourtName = findViewById(R.id.tvCourtName);

            // Log each view's status
            Log.d(TAG, "btnSelectTime: " + (btnSelectTime != null ? "found" : "null"));
            Log.d(TAG, "btnConfirmBooking: " + (btnConfirmBooking != null ? "found" : "null"));
            Log.d(TAG, "btnOneHour: " + (btnOneHour != null ? "found" : "null"));
            Log.d(TAG, "btnTwoHours: " + (btnTwoHours != null ? "found" : "null"));
            Log.d(TAG, "tvSelectedTime: " + (tvSelectedTime != null ? "found" : "null"));
            Log.d(TAG, "tvDuration: " + (tvDuration != null ? "found" : "null"));
            Log.d(TAG, "tvTotalPrice: " + (tvTotalPrice != null ? "found" : "null"));
            Log.d(TAG, "tvCourtName: " + (tvCourtName != null ? "found" : "null"));

            if (btnSelectTime == null || btnConfirmBooking == null || btnOneHour == null || 
                btnTwoHours == null || tvSelectedTime == null || tvDuration == null || 
                tvTotalPrice == null || tvCourtName == null) {
                String missingViews = "";
                if (btnSelectTime == null) missingViews += "btnSelectTime ";
                if (btnConfirmBooking == null) missingViews += "btnConfirmBooking ";
                if (btnOneHour == null) missingViews += "btnOneHour ";
                if (btnTwoHours == null) missingViews += "btnTwoHours ";
                if (tvSelectedTime == null) missingViews += "tvSelectedTime ";
                if (tvDuration == null) missingViews += "tvDuration ";
                if (tvTotalPrice == null) missingViews += "tvTotalPrice ";
                if (tvCourtName == null) missingViews += "tvCourtName ";
                
                Log.e(TAG, "Missing views: " + missingViews);
                throw new IllegalStateException("Required views not found in layout: " + missingViews);
            }

            // Set court name
            tvCourtName.setText("Booking for: " + courtName);
            Log.d(TAG, "Views initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage(), e);
            throw e;
        }
    }

    private void setupClickListeners() {
        btnSelectTime.setOnClickListener(v -> showTimePickerDialog());
        btnConfirmBooking.setOnClickListener(v -> checkAndConfirmBooking());
        btnOneHour.setOnClickListener(v -> setDuration(1));
        btnTwoHours.setOnClickListener(v -> setDuration(2));
    }

    private void showTimePickerDialog() {
        try {
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
        } catch (Exception e) {
            Log.e(TAG, "Error showing time picker: " + e.getMessage());
            Toast.makeText(this, "Error selecting time", Toast.LENGTH_SHORT).show();
        }
    }

    private void setDuration(int hours) {
        duration = hours;
        updateUI();
    }

    private void updateUI() {
        try {
            tvSelectedTime.setText("Selected Time: " + timeFormat.format(startTime.getTime()));
            tvDuration.setText("Duration: " + duration + " hour(s)");
            tvTotalPrice.setText("Total Price: ₱" + (duration * PRICE_PER_HOUR));
            
            // Enable/disable confirm button based on duration
            btnConfirmBooking.setEnabled(duration > 0);
        } catch (Exception e) {
            Log.e(TAG, "Error updating UI: " + e.getMessage());
        }
    }

    private void checkAndConfirmBooking() {
        if (duration == 0) {
            Toast.makeText(this, "Please select duration", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Get the selected time slot
            String selectedTime = timeFormat.format(startTime.getTime());
            String selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            // Show loading message
            Toast.makeText(this, "Checking availability...", Toast.LENGTH_SHORT).show();

            // Check for existing bookings across all users
            databaseReference.child("bookings")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            boolean isTimeSlotAvailable = true;
                            String selectedTimeSlot = selectedTime;

                            // Check all bookings for the selected court and time
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                for (DataSnapshot bookingSnapshot : userSnapshot.getChildren()) {
                                    BookingRequest booking = bookingSnapshot.getValue(BookingRequest.class);
                                    if (booking != null && 
                                        booking.getBookingDetails() != null &&
                                        booking.getBookingDetails().contains(courtName) &&
                                        booking.getDate() != null &&
                                        booking.getDate().equals(selectedDate) &&
                                        booking.getTimeSlot() != null &&
                                        booking.getTimeSlot().equals(selectedTimeSlot) &&
                                        booking.getStatus() != null &&
                                        !booking.getStatus().equals("denied")) {
                                        
                                        isTimeSlotAvailable = false;
                                        break;
                                    }
                                }
                                if (!isTimeSlotAvailable) break;
                            }

                            if (isTimeSlotAvailable) {
                                confirmBooking();
                            } else {
                                Toast.makeText(BookingActivity.this, 
                                    "This time slot is already booked. Please select another time.", 
                                    Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error checking availability: " + e.getMessage());
                            Toast.makeText(BookingActivity.this, 
                                "Error checking availability: " + e.getMessage(), 
                                Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Database error: " + error.getMessage());
                        Toast.makeText(BookingActivity.this, 
                            "Error checking availability: " + error.getMessage(), 
                            Toast.LENGTH_LONG).show();
                    }
                });
        } catch (Exception e) {
            Log.e(TAG, "Error in checkAndConfirmBooking: " + e.getMessage());
            Toast.makeText(this, "Error checking availability: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void confirmBooking() {
        try {
            // Create booking request
            String userKey = userEmail.replace(".", "_");
            DatabaseReference userBookingsRef = databaseReference.child("bookings").child(userKey);
            String requestId = userBookingsRef.push().getKey();
            
            if (requestId == null) {
                throw new IllegalStateException("Failed to generate booking ID");
            }

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
                        Log.e(TAG, "Error saving booking: " + task.getException().getMessage());
                        Toast.makeText(this, "Failed to submit booking request: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        } catch (Exception e) {
            Log.e(TAG, "Error in confirmBooking: " + e.getMessage());
            Toast.makeText(this, "Error confirming booking: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
