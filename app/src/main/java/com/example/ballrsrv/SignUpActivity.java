package com.example.ballrsrv;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_IS_ADMIN = "isAdmin";
    private static final String DATABASE_URL = "https://ballrsrv-a94eb-default-rtdb.asia-southeast1.firebasedatabase.app/";

    private EditText editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button btnSignUp;
    private DatabaseReference databaseRef;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        try {
            // Initialize Firebase Database
            databaseRef = FirebaseDatabase.getInstance(DATABASE_URL).getReference("users");
            
            // Initialize SharedPreferences
            sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            
            // Initialize views
            editTextEmail = findViewById(R.id.editTextEmail);
            editTextPassword = findViewById(R.id.editTextPassword);
            editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
            btnSignUp = findViewById(R.id.buttonSignUp);

            btnSignUp.setOnClickListener(v -> {
                try {
                    String email = editTextEmail.getText().toString().trim();
                    String password = editTextPassword.getText().toString().trim();
                    String confirmPassword = editTextConfirmPassword.getText().toString().trim();

                    if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                        Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (password.length() < 6) {
                        Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!password.equals(confirmPassword)) {
                        Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    createAccount(email, password);
                } catch (Exception e) {
                    Log.e(TAG, "Error in signup click: " + e.getMessage());
                    Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            Toast.makeText(this, "An error occurred while initializing the app.", Toast.LENGTH_SHORT).show();
        }
    }

    private void createAccount(String email, String password) {
        try {
            btnSignUp.setEnabled(false);
            btnSignUp.setText("Creating Account...");

            String userId = email.replace(".", "_");
            Log.d(TAG, "Creating account for user ID: " + userId);

            // Check if user already exists
            databaseRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        if (snapshot.exists()) {
                            Log.d(TAG, "User already exists");
                            Toast.makeText(SignUpActivity.this, "Account already exists. Please login.", Toast.LENGTH_SHORT).show();
                            btnSignUp.setEnabled(true);
                            btnSignUp.setText("Sign Up");
                            return;
                        }

                        // Create user profile
                        Map<String, Object> userProfile = new HashMap<>();
                        userProfile.put("email", email);
                        userProfile.put("password", password);
                        userProfile.put("isAdmin", false);

                        // Save to database
                        databaseRef.child(userId).setValue(userProfile)
                            .addOnSuccessListener(aVoid -> {
                                try {
                                    Log.d(TAG, "Account created successfully");
                                    
                                    // Save login state
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean(KEY_IS_LOGGED_IN, true);
                                    editor.putString(KEY_EMAIL, email);
                                    editor.putBoolean(KEY_IS_ADMIN, false);
                                    editor.apply();

                                    Toast.makeText(SignUpActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                                    
                                    // Navigate to home screen
                                    Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                                    intent.putExtra("email", email);
                                    startActivity(intent);
                                    finish();
                                } catch (Exception e) {
                                    Log.e(TAG, "Error in account creation success: " + e.getMessage());
                                    Toast.makeText(SignUpActivity.this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                                    btnSignUp.setEnabled(true);
                                    btnSignUp.setText("Sign Up");
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error creating account: " + e.getMessage());
                                Toast.makeText(SignUpActivity.this, "Error creating account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                btnSignUp.setEnabled(true);
                                btnSignUp.setText("Sign Up");
                            });
                    } catch (Exception e) {
                        Log.e(TAG, "Error in account creation data change: " + e.getMessage());
                        Toast.makeText(SignUpActivity.this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                        btnSignUp.setEnabled(true);
                        btnSignUp.setText("Sign Up");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Error checking existing account: " + error.getMessage());
                    Toast.makeText(SignUpActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    btnSignUp.setEnabled(true);
                    btnSignUp.setText("Sign Up");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in createAccount: " + e.getMessage());
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
            btnSignUp.setEnabled(true);
            btnSignUp.setText("Sign Up");
        }
    }
}
