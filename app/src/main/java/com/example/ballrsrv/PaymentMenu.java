package com.example.ballrsrv;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentMenu extends AppCompatActivity {
    Button btnGcash;
    Button btnCash;
    Button btnBack;
    private String userEmail;
    private String courtName;
    private String bookingId;
    private int totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_menu);

        // Get booking details from intent
        userEmail = getIntent().getStringExtra("email");
        courtName = getIntent().getStringExtra("court_name");
        bookingId = getIntent().getStringExtra("booking_id");
        totalPrice = getIntent().getIntExtra("total_price", 0);

        btnGcash = findViewById(R.id.btnGcash);
        btnCash = findViewById(R.id.btnCash);
        btnBack = findViewById(R.id.btnBack);

        btnGcash.setOnClickListener(v -> {
            Intent intent = new Intent(this, PaymentStatusActivityGcaash.class);
            intent.putExtra("mode", "GCASH");
            intent.putExtra("booking_id", bookingId);
            intent.putExtra("total_price", totalPrice);
            startActivity(intent);
        });

        btnCash.setOnClickListener(v -> {
            Intent intent = new Intent(this, PaymentModeActivityCash.class);
            intent.putExtra("mode", "CASH");
            intent.putExtra("booking_id", bookingId);
            intent.putExtra("total_price", totalPrice);
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