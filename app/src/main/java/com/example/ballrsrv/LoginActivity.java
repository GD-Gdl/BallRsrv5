package com.example.ballrsrv;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_IS_ADMIN = "isAdmin";
    private static final String DATABASE_URL = "https://ballrsrv-a94eb-default-rtdb.asia-southeast1.firebasedatabase.app/";

    private EditText etEmail, etPassword;
    private Button btnLogin, btnSignUp;
    private DatabaseReference databaseRef;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            // Initialize Firebase Database
            databaseRef = FirebaseDatabase.getInstance(DATABASE_URL).getReference("users");
            
            // Initialize SharedPreferences
            sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            
            // Initialize views
            etEmail = findViewById(R.id.etEmail);
            etPassword = findViewById(R.id.etPassword);
            btnLogin = findViewById(R.id.btnLogin);
            btnSignUp = findViewById(R.id.btnSignUp);

            // Check if user is already logged in
            if (sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
                String savedEmail = sharedPreferences.getString(KEY_EMAIL, "");
                boolean isAdmin = sharedPreferences.getBoolean(KEY_IS_ADMIN, false);
                if (!TextUtils.isEmpty(savedEmail)) {
                    startAppropriateActivity(savedEmail, isAdmin);
                    return;
                }
            }

            // Set click listeners
            btnLogin.setOnClickListener(v -> {
                try {
                    String email = etEmail.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();

                    if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                        Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Special handling for admin login
                    if (email.equals("Admin@gmail.com") && password.equals("123456789")) {
                        handleAdminLogin();
                        return;
                    }

                    loginUser(email, password);
                } catch (Exception e) {
                    Log.e(TAG, "Error in login click: " + e.getMessage());
                    Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });

            btnSignUp.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(this, SignUpActivity.class));
                } catch (Exception e) {
                    Log.e(TAG, "Error starting SignUpActivity: " + e.getMessage());
                    Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            Toast.makeText(this, "An error occurred while initializing the app.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleAdminLogin() {
        try {
            btnLogin.setEnabled(false);
            btnLogin.setText("Logging in...");

            String userId = "Admin_gmail_com";
            databaseRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        if (snapshot.exists()) {
                            // Admin account exists, proceed with login
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(KEY_IS_LOGGED_IN, true);
                            editor.putString(KEY_EMAIL, "Admin@gmail.com");
                            editor.putBoolean(KEY_IS_ADMIN, true);
                            editor.apply();

                            Toast.makeText(LoginActivity.this, "Welcome Admin!", Toast.LENGTH_SHORT).show();
                            startAppropriateActivity("Admin@gmail.com", true);
                        } else {
                            // Admin account doesn't exist, create it
                            createAdminAccount();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error in admin login: " + e.getMessage());
                        Toast.makeText(LoginActivity.this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                        btnLogin.setEnabled(true);
                        btnLogin.setText("Login");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Error checking admin account: " + error.getMessage());
                    Toast.makeText(LoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Login");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in handleAdminLogin: " + e.getMessage());
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
            btnLogin.setEnabled(true);
            btnLogin.setText("Login");
        }
    }

    private void createAdminAccount() {
        try {
            String userId = "Admin_gmail_com";
            Map<String, Object> adminProfile = new HashMap<>();
            adminProfile.put("email", "Admin@gmail.com");
            adminProfile.put("password", "123456789");
            adminProfile.put("isAdmin", true);

            databaseRef.child(userId).setValue(adminProfile)
                .addOnSuccessListener(aVoid -> {
                    try {
                        // Admin account created, proceed with login
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(KEY_IS_LOGGED_IN, true);
                        editor.putString(KEY_EMAIL, "Admin@gmail.com");
                        editor.putBoolean(KEY_IS_ADMIN, true);
                        editor.apply();

                        Toast.makeText(LoginActivity.this, "Welcome Admin!", Toast.LENGTH_SHORT).show();
                        startAppropriateActivity("Admin@gmail.com", true);
                    } catch (Exception e) {
                        Log.e(TAG, "Error in admin account creation success: " + e.getMessage());
                        Toast.makeText(LoginActivity.this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                        btnLogin.setEnabled(true);
                        btnLogin.setText("Login");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error creating admin account: " + e.getMessage());
                    Toast.makeText(LoginActivity.this, "Error creating admin account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Login");
                });
        } catch (Exception e) {
            Log.e(TAG, "Error in createAdminAccount: " + e.getMessage());
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
            btnLogin.setEnabled(true);
            btnLogin.setText("Login");
        }
    }

    private void loginUser(String email, String password) {
        try {
            btnLogin.setEnabled(false);
            btnLogin.setText("Logging in...");

            String userId = email.replace(".", "_");
            Log.d(TAG, "Attempting to login user with ID: " + userId);

            databaseRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        Log.d(TAG, "Database response for user: " + snapshot.exists());
                        
                        if (snapshot.exists()) {
                            String storedPassword = snapshot.child("password").getValue(String.class);
                            Boolean isAdmin = snapshot.child("isAdmin").getValue(Boolean.class);
                            
                            Log.d(TAG, "Stored password exists: " + (storedPassword != null));
                            Log.d(TAG, "Password matches: " + (storedPassword != null && storedPassword.equals(password)));
                            
                            if (storedPassword != null && storedPassword.equals(password)) {
                                // Save login state
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean(KEY_IS_LOGGED_IN, true);
                                editor.putString(KEY_EMAIL, email);
                                editor.putBoolean(KEY_IS_ADMIN, isAdmin != null && isAdmin);
                                editor.apply();

                                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                startAppropriateActivity(email, isAdmin != null && isAdmin);
                                return;
                            }
                            Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "User not found in database");
                            Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error in login data change: " + e.getMessage());
                        Toast.makeText(LoginActivity.this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                    } finally {
                        btnLogin.setEnabled(true);
                        btnLogin.setText("Login");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Error during login: " + error.getMessage());
                    Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Login");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in loginUser: " + e.getMessage());
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
            btnLogin.setEnabled(true);
            btnLogin.setText("Login");
        }
    }

    private void startAppropriateActivity(String email, boolean isAdmin) {
        try {
            Intent intent = new Intent(this, isAdmin ? AdminActivity.class : HomeActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error starting activity: " + e.getMessage());
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
            btnLogin.setEnabled(true);
            btnLogin.setText("Login");
        }
    }

    public static void logout(android.content.Context context) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        } catch (Exception e) {
            Log.e(TAG, "Error in logout: " + e.getMessage());
        }
    }
}
