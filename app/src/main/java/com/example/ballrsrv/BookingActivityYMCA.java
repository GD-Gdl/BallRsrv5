package com.example.ballrsrv;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class BookingActivityYMCA extends AppCompatActivity {
    Button btnGcash;
    Button btnCash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_ymca);

        btnGcash = findViewById(R.id.btnGcash);
        btnCash = findViewById(R.id.btnCash);

        btnGcash.setOnClickListener(v -> {
            Intent intent = new Intent(this, PaymentStatusActivityGcaash.class);
            intent.putExtra("mode", "GCASH");
            startActivity(intent);
        });

        btnCash.setOnClickListener(v -> {
            Intent intent = new Intent(this, PaymentModeActivityCash.class);
            intent.putExtra("mode", "CASH");
            startActivity(intent);
        });
    }
}