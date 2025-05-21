package com.example.ballrsrv;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    private String userEmail;
    private boolean isGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get user email and guest status from intent
        userEmail = getIntent().getStringExtra("email");
        isGuest = getIntent().getBooleanExtra("isGuest", false);

        // Initialize buttons
        Button btn1Book = findViewById(R.id.btn1Book);
        Button btn2Book = findViewById(R.id.btn2Book);
        Button btn3Book = findViewById(R.id.btn3Book);
        Button btnViewStatus = findViewById(R.id.btnViewStatus);
        Button btnLogout = findViewById(R.id.btnLogout);

        // Set up click listeners
        btn1Book.setOnClickListener(v -> startBooking("YMCA Basketball Court"));
        btn2Book.setOnClickListener(v -> startBooking("Irisan Basketball Court"));
        btn3Book.setOnClickListener(v -> startBooking("St. Vincent Basketball Court"));

        // Only show view status button if not a guest
        if (!isGuest) {
            btnViewStatus.setOnClickListener(v -> {
                Intent intent = new Intent(this, BookingStatusActivity.class);
                intent.putExtra("email", userEmail);
                startActivity(intent);
            });
        } else {
            btnViewStatus.setVisibility(Button.GONE);
        }

        // Set up logout button
        btnLogout.setOnClickListener(v -> {
            if (!isGuest) {
                LoginActivity.logout(this);
            } else {
                // For guest users, just go back to login
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void startBooking(String courtName) {
        if (isGuest) {
            Toast.makeText(this, "Please log in to make a booking", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        Intent intent = new Intent(this, BookingActivity.class);
        intent.putExtra("email", userEmail);
        intent.putExtra("courtName", courtName);
        startActivity(intent);
    }
}
