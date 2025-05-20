package com.example.ballrsrv;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private String userIdentifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        userIdentifier = getIntent().getStringExtra("email");

        Button btn1Book = findViewById(R.id.btn1Book);
        Button btn2Book = findViewById(R.id.btn2Book);
        Button btn3Book = findViewById(R.id.btn3Book);
        Button btnViewStatus = findViewById(R.id.btnViewStatus);

        btn1Book.setOnClickListener(v -> goToBooking());
        btn2Book.setOnClickListener(v -> goToBooking());
        btn3Book.setOnClickListener(v -> goToBooking());
        btnViewStatus.setOnClickListener(v -> viewBookingStatus());
    }

    private void goToBooking() {
        Intent intent = new Intent(HomeActivity.this, BookingActivity.class);
        intent.putExtra("email", userIdentifier); // ✅ Forward the identifier
        startActivity(intent);
    }

    private void viewBookingStatus() {
        Intent intent = new Intent(HomeActivity.this, BookingStatusActivity.class);
        startActivity(intent);
    }
}
