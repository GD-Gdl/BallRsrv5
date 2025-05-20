package com.example.ballrsrv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;

public class AdminActivity extends AppCompatActivity {
    private MaterialCardView bookingsCard, usersCard, paymentsCard, requestsCard;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize views
        bookingsCard = findViewById(R.id.bookingsCard);
        usersCard = findViewById(R.id.usersCard);
        paymentsCard = findViewById(R.id.paymentsCard);
        requestsCard = findViewById(R.id.requestsCard);
        logoutButton = findViewById(R.id.logoutButton);

        // Set click listeners
        bookingsCard.setOnClickListener(v -> {
            // TODO: Navigate to bookings management
            Toast.makeText(this, "Bookings Management", Toast.LENGTH_SHORT).show();
        });

        usersCard.setOnClickListener(v -> {
            // TODO: Navigate to users management
            Toast.makeText(this, "Users Management", Toast.LENGTH_SHORT).show();
        });

        paymentsCard.setOnClickListener(v -> {
            // TODO: Navigate to payments management
            Toast.makeText(this, "Payments Management", Toast.LENGTH_SHORT).show();
        });

        requestsCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, RequestsActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            // Clear login state using LoginActivity's logout method
            LoginActivity.logout(this);
            
            // Show logout message
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            
            // Navigate to login screen and clear all previous activities
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
