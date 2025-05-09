package com.example.ballrsrv;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    // For a real app, use RecyclerView for the venue list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Example: Find the first Reserve button and set a click listener
        Button btnReserve = findViewById(R.id.btnReserve); // Set unique IDs for each button in your layout or use RecyclerView
        if (btnReserve != null) {
            btnReserve.setOnClickListener(v -> {
                startActivity(new Intent(this, BookingActivity.class));
            });
        }

        // TODO: Set up bottom navigation listeners
    }
}