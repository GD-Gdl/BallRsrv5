package com.example.ballrsrv;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity {
    private String userEmail;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get user email from intent or saved preferences
        userEmail = getIntent().getStringExtra("email");
        if (userEmail == null) {
            Toast.makeText(this, "Please log in to continue", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference();

        // Initialize buttons
        Button btn1Book = findViewById(R.id.btn1Book);
        Button btn2Book = findViewById(R.id.btn2Book);
        Button btn3Book = findViewById(R.id.btn3Book);
        Button btnViewStatus = findViewById(R.id.btnViewStatus);
        Button btnLogout = findViewById(R.id.btnLogout);

        // Set up click listeners
        btn1Book.setOnClickListener(v -> startBooking("Court 1"));
        btn2Book.setOnClickListener(v -> startBooking("Court 2"));
        btn3Book.setOnClickListener(v -> startBooking("Court 3"));

        btnViewStatus.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingStatusActivity.class);
            intent.putExtra("email", userEmail);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            // Clear saved login state
            LoginActivity.logout(this);
            
            // Show logout message
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            
            // Navigate back to login screen
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void startBooking(String courtName) {
        Intent intent = new Intent(this, BookingActivity.class);
        intent.putExtra("email", userEmail);
        intent.putExtra("courtName", courtName);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if user is still logged in
        if (userEmail == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}
