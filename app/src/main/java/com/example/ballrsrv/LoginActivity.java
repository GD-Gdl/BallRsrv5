package com.example.ballrsrv;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_IS_ADMIN = "isAdmin";

    private EditText etEmail, etPassword;
    private MaterialButton btnLogin, btnSignUp, btnGuest;
    private DatabaseReference databaseRef;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check if user is already logged in
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
        String email = sharedPreferences.getString(KEY_EMAIL, "");
        boolean isAdmin = sharedPreferences.getBoolean(KEY_IS_ADMIN, false);
        
        if (isLoggedIn && !email.isEmpty()) {
            // User is already logged in, go to appropriate activity
            startAppropriateActivity(email, isAdmin);
            return;
        }
        
        setContentView(R.layout.activity_login);

        // Initialize Firebase Database
        databaseRef = FirebaseDatabase.getInstance("https://ballrsrv-a94eb-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users");
        
        // Initialize views
        initializeViews();
        
        // Set click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnGuest = findViewById(R.id.btnGuest);
    }

    private void setupClickListeners() {
        // Login button click listener
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            validateUser(email, password);
        });

        // Sign Up button click listener
        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });

        // Guest button click listener
        btnGuest.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("isGuest", true);
            startActivity(intent);
            finish();
        });
    }

    private void validateUser(String email, String password) {
        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in...");

        // Special handling for admin login
        if (email.equals("Admin@gmail.com") && password.equals("123456789")) {
            handleAdminLogin();
            return;
        }

        databaseRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String storedPassword = userSnapshot.child("password").getValue(String.class);
                        Boolean isAdmin = userSnapshot.child("isAdmin").getValue(Boolean.class);
                        
                        if (storedPassword != null && storedPassword.equals(password)) {
                            // Save login state
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(KEY_IS_LOGGED_IN, true);
                            editor.putString(KEY_EMAIL, email);
                            editor.putBoolean(KEY_IS_ADMIN, isAdmin != null && isAdmin);
                            editor.apply();

                            // Navigate to appropriate activity
                            startAppropriateActivity(email, isAdmin != null && isAdmin);
                            return;
                        }
                    }
                    Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
                btnLogin.setEnabled(true);
                btnLogin.setText("Login");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                btnLogin.setEnabled(true);
                btnLogin.setText("Login");
            }
        });
    }

    private void handleAdminLogin() {
        // Save admin login state
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_EMAIL, "Admin@gmail.com");
        editor.putBoolean(KEY_IS_ADMIN, true);
        editor.apply();

        // Navigate to admin activity
        Intent intent = new Intent(this, AdminActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void startAppropriateActivity(String email, boolean isAdmin) {
        Intent intent;
        if (isAdmin) {
            intent = new Intent(this, AdminActivity.class);
        } else {
            intent = new Intent(this, HomeActivity.class);
        }
        intent.putExtra("email", email);
        intent.putExtra("isAdmin", isAdmin);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public static void logout(android.content.Context context) {
        try {
            // Clear SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            
            // Start LoginActivity and clear the back stack
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error during logout: " + e.getMessage());
            Toast.makeText(context, "An error occurred during logout.", Toast.LENGTH_SHORT).show();
        }
    }
}
