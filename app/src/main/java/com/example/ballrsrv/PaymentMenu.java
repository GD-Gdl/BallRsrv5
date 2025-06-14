package com.example.ballrsrv;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;

public class PaymentMenu extends AppCompatActivity {
    Button btnGcash;
    Button btnCash;
    Button btnBack;
    private String userEmail;
    private String courtName;
    private String bookingId;
    private int totalPrice;
    private TextView tvPricePerHour;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_menu);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Get booking details from intent
        userEmail = getIntent().getStringExtra("email");
        courtName = getIntent().getStringExtra("court_name");
        bookingId = getIntent().getStringExtra("booking_id");
        totalPrice = getIntent().getIntExtra("total_price", 0);

        btnGcash = findViewById(R.id.btnGcash);
        btnCash = findViewById(R.id.btnCash);
        btnBack = findViewById(R.id.btnBack);
        tvPricePerHour = findViewById(R.id.tvPricePerHour);

        // Fetch and display the court's price per hour
        databaseReference.child("courts").orderByChild("name").equalTo(courtName)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot courtSnapshot : snapshot.getChildren()) {
                            Court court = courtSnapshot.getValue(Court.class);
                            if (court != null) {
                                double pricePerHour = court.getPrice();
                                tvPricePerHour.setText(String.format("Fee per hour: ₱%.2f", pricePerHour));
                                break;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    tvPricePerHour.setText("Fee per hour: ₱0.00");
                }
            });

        btnGcash.setOnClickListener(v -> {
            Intent intent = new Intent(this, PaymentStatusActivityGcaash.class);
            intent.putExtra("mode", "GCASH");
            intent.putExtra("booking_id", bookingId);
            intent.putExtra("total_price", totalPrice);
            intent.putExtra("email", userEmail);
            startActivity(intent);
        });

        btnCash.setOnClickListener(v -> {
            Intent intent = new Intent(this, PaymentModeActivityCash.class);
            intent.putExtra("mode", "CASH");
            intent.putExtra("booking_id", bookingId);
            intent.putExtra("total_price", totalPrice);
            intent.putExtra("email", userEmail);
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingActivity.class);
            intent.putExtra("email", userEmail);
            intent.putExtra("courtName", courtName);
            startActivity(intent);
            finish();
        });
    }
}