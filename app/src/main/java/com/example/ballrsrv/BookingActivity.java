package com.example.ballrsrv;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class BookingActivity extends AppCompatActivity {
    Button btnGcash, btnCash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        btnGcash = findViewById(R.id.btnGcash); // Add these IDs to your XML
        btnCash = findViewById(R.id.btnCash);

        btnGcash.setOnClickListener(v -> {
            Intent intent = new Intent(this, PaymentModeActivity.class);
            intent.putExtra("mode", "GCASH");
            startActivity(intent);
        });

        btnCash.setOnClickListener(v -> {
            Intent intent = new Intent(this, PaymentModeActivity.class);
            intent.putExtra("mode", "CASH");
            startActivity(intent);
        });
    }
}