package com.example.ballrsrv;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {

    private Button btnCreate;
    private EditText etEmailOrContact, etPassword, etConfirmPassword, etFullName, etPhoneNumber;
    private DatabaseReference databaseRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        etEmailOrContact = findViewById(R.id.etEmailOrContact);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etFullName = findViewById(R.id.etFullName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnCreate = findViewById(R.id.btnCreate);
    }

    private void setupClickListeners() {
        btnCreate.setOnClickListener(v -> handleSignUp());
    }

    private void handleSignUp() {
        String email = etEmailOrContact.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();

        // Validate all fields
        if (!validateFields(email, password, confirmPassword, fullName, phoneNumber)) {
            return;
        }

        // Create user with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Update user profile with full name
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(fullName)
                        .build();

                    mAuth.getCurrentUser().updateProfile(profileUpdates)
                        .addOnCompleteListener(profileTask -> {
                            if (profileTask.isSuccessful()) {
                                // Save additional user data to Realtime Database
                                saveUserData(email, fullName, phoneNumber);
                            }
                        });
                } else {
                    Toast.makeText(SignUpActivity.this, 
                        "Authentication failed: " + task.getException().getMessage(),
                        Toast.LENGTH_SHORT).show();
                }
            });
    }

    private boolean validateFields(String email, String password, String confirmPassword, 
                                 String fullName, String phoneNumber) {
        // Validate email
        if (TextUtils.isEmpty(email)) {
            etEmailOrContact.setError("Email is required");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmailOrContact.setError("Please enter a valid email address");
            return false;
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            return false;
        }
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return false;
        }

        // Validate confirm password
        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Please confirm your password");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            return false;
        }

        // Validate full name
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Full name is required");
            return false;
        }

        // Validate phone number
        if (TextUtils.isEmpty(phoneNumber)) {
            etPhoneNumber.setError("Phone number is required");
            return false;
        }
        if (phoneNumber.length() < 10) {
            etPhoneNumber.setError("Please enter a valid phone number");
            return false;
        }

        return true;
    }

    private void saveUserData(String email, String fullName, String phoneNumber) {
        String userId = mAuth.getCurrentUser().getUid();
        
        User user = new User(
            userId,
            email,
            "", // Don't store password in Realtime Database
            fullName,
            phoneNumber,
            "user" // Default user type
        );

        databaseRef.child(userId).setValue(user)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, 
                        "Account created successfully!", Toast.LENGTH_SHORT).show();
                    
                    // Navigate to HomeActivity
                    Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SignUpActivity.this, 
                        "Failed to save user data: " + task.getException().getMessage(),
                        Toast.LENGTH_SHORT).show();
                }
            });
    }
}
