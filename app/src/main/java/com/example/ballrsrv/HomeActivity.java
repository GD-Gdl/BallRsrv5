package com.example.ballrsrv;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // Make sure your XML is named activity_home.xml

        // Find the buttons
        Button btn1Book = findViewById(R.id.btn1Book);
        Button btn2Book = findViewById(R.id.btn2Book);
        Button btn3Book = findViewById(R.id.btn3Book);

        // Set click listeners for each button
        btn1Book.setOnClickListener(v -> goToBooking());
        btn2Book.setOnClickListener(v -> goToBooking());
        btn3Book.setOnClickListener(v -> goToBooking());
    }

    private void goToBooking() {
        Intent intent = new Intent(HomeActivity.this, BookingActivity.class);
        startActivity(intent);
    }
}