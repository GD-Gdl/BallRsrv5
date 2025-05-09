package com.example.ballrsrv;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentModeActivity extends AppCompatActivity {
    TextView tvMode, tvInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_mode);

        tvMode = findViewById(R.id.tvMode); // Add these IDs to your XML
        tvInstructions = findViewById(R.id.tvInstructions);

        String mode = getIntent().getStringExtra("mode");
        if ("GCASH".equals(mode)) {
            tvMode.setText("GCASH");
            tvInstructions.setText("Please send your payment:\nupload screenshot of receipt:");
        } else {
            tvMode.setText("CASH");
            tvInstructions.setText("Make sure to show up on time");
        }
    }
}