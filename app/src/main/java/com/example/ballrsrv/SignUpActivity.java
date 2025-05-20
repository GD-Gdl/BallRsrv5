package com.example.ballrsrv;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    private Button btnCreate;
    private EditText etEmail, etPassword, etConfirmPassword;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Database
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            databaseRef = database.getReference("Users");
            Log.d(TAG, "Firebase Database initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Firebase initialization error: " + e.getMessage());
            Toast.makeText(this, "Failed to initialize database. Please try again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Initialize views
        etEmail = findViewById(R.id.etEmailOrContact);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnCreate = findViewById(R.id.btnCreate);

        // Set up click listener
        btnCreate.setOnClickListener(v -> signUp());
    }

    private void signUp() {
        // Get input values
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Basic validation
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable button to prevent multiple clicks
        btnCreate.setEnabled(false);
        btnCreate.setText("Creating Account...");

        // Check if email already exists
        databaseRef.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(SignUpActivity.this, 
                            "Email already registered", Toast.LENGTH_SHORT).show();
                        btnCreate.setEnabled(true);
                        btnCreate.setText("Create Account");
                    } else {
                        createUser(email, password);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e(TAG, "Database error: " + error.getMessage());
                    Toast.makeText(SignUpActivity.this, 
                        "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    btnCreate.setEnabled(true);
                    btnCreate.setText("Create Account");
                }
            });
    }

    private void createUser(String email, String password) {
        try {
            // Create new user
            String userId = UUID.randomUUID().toString();
            User user = new User(userId, email, password);

            // Save to database
            databaseRef.child(userId).setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User created successfully");
                        Toast.makeText(SignUpActivity.this, 
                            "Account created successfully!", Toast.LENGTH_SHORT).show();
                        
                        // Go to home screen
                        Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e(TAG, "Failed to create user: " + task.getException());
                        Toast.makeText(SignUpActivity.this, 
                            "Failed to create account. Please try again.", Toast.LENGTH_SHORT).show();
                        btnCreate.setEnabled(true);
                        btnCreate.setText("Create Account");
                    }
                });
        } catch (Exception e) {
            Log.e(TAG, "Error creating user: " + e.getMessage());
            Toast.makeText(this, "Error creating account. Please try again.", Toast.LENGTH_SHORT).show();
            btnCreate.setEnabled(true);
            btnCreate.setText("Create Account");
        }
    }
}
