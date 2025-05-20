package com.example.ballrsrv;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";

    private Button btnCreate;
    private EditText etEmailOrContact, etPassword, etConfirmPassword, etFullName, etPhoneNumber;
    private DatabaseReference databaseRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        
        if (mAuth != null) {
            databaseRef = FirebaseDatabase.getInstance("https://ballrsrv-a94eb-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("Users");
            initializeViews();
            setupClickListeners();
        } else {
            Log.e(TAG, "Firebase Auth initialization failed");
            Toast.makeText(this, "Failed to initialize authentication. Please try again.", Toast.LENGTH_LONG).show();
            finish();
        }
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
        btnCreate.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                handleSignUp();
            } else {
                Toast.makeText(this, "No internet connection. Please check your network.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

        // Show loading state
        btnCreate.setEnabled(false);
        btnCreate.setText("Creating Account...");

        // Create user account
        createUserAccount(email, password, fullName, phoneNumber);
    }

    private void createUserAccount(String email, String password, String fullName, String phoneNumber) {
        if (mAuth == null) {
            handleError("Authentication service not available. Please try again.");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User created successfully");
                    // Update user profile with full name
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(fullName)
                        .build();

                    if (mAuth.getCurrentUser() != null) {
                        mAuth.getCurrentUser().updateProfile(profileUpdates)
                            .addOnCompleteListener(profileTask -> {
                                if (profileTask.isSuccessful()) {
                                    Log.d(TAG, "Profile updated successfully");
                                    // Save additional user data to Realtime Database
                                    saveUserData(email, fullName, phoneNumber);
                                } else {
                                    Log.e(TAG, "Profile update failed: " + profileTask.getException().getMessage());
                                    handleError("Failed to update profile: " + profileTask.getException().getMessage());
                                }
                            });
                    } else {
                        handleError("Failed to get current user. Please try again.");
                    }
                } else {
                    String errorMessage = "Authentication failed";
                    if (task.getException() instanceof FirebaseAuthException) {
                        FirebaseAuthException e = (FirebaseAuthException) task.getException();
                        switch (e.getErrorCode()) {
                            case "ERROR_WEAK_PASSWORD":
                                errorMessage = "Password is too weak. Please use a stronger password.";
                                break;
                            case "ERROR_INVALID_EMAIL":
                                errorMessage = "Invalid email format.";
                                break;
                            case "ERROR_EMAIL_ALREADY_IN_USE":
                                errorMessage = "This email is already registered.";
                                break;
                            case "ERROR_INTERNAL_ERROR":
                                errorMessage = "An internal error occurred. Please try again later.";
                                break;
                            default:
                                errorMessage = "Authentication failed: " + e.getMessage();
                        }
                    }
                    handleError(errorMessage);
                }
            });
    }

    private void handleError(String errorMessage) {
        btnCreate.setEnabled(true);
        btnCreate.setText("Create Account");
        Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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
        if (mAuth.getCurrentUser() == null) {
            handleError("User session not found. Please try again.");
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        
        User user = new User(
            userId,
            email,
            "", // Don't store password in Realtime Database
            fullName,
            phoneNumber,
            "user" // Default user type
        );

        if (databaseRef != null) {
            databaseRef.child(userId).setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User data saved successfully");
                        Toast.makeText(SignUpActivity.this, 
                            "Account created successfully!", Toast.LENGTH_SHORT).show();
                        
                        // Navigate to HomeActivity
                        Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                        intent.putExtra("email", email);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e(TAG, "Failed to save user data: " + task.getException().getMessage());
                        handleError("Failed to save user data: " + task.getException().getMessage());
                    }
                });
        } else {
            handleError("Database reference not available. Please try again.");
        }
    }
}
