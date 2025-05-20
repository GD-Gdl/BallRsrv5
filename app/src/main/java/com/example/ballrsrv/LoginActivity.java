package com.example.ballrsrv;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
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

public class LoginActivity extends AppCompatActivity {
    private static final String PREF_NAME = "BallRsrvPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private EditText etEmail, etPassword;
    private Button btnLogin, btnSignUp;
    private DatabaseReference databaseRef;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check if user is already logged in
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            String savedEmail = sharedPreferences.getString(KEY_EMAIL, "");
            if (!TextUtils.isEmpty(savedEmail)) {
                startHomeActivity(savedEmail);
                return;
            }
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);

        databaseRef = FirebaseDatabase.getInstance().getReference("Users");

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (email.equals("123") && password.equals("123")) {
                Toast.makeText(this, "Welcome Admin!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, AdminActivity.class));
                finish();
                return;
            }

            loginUser(email, password);
        });

        btnSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });
    }

    private void loginUser(String email, String password) {
        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in...");

        databaseRef.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                String storedPassword = userSnapshot.child("password").getValue(String.class);
                                if (storedPassword != null && storedPassword.equals(password)) {
                                    // Save login state
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean(KEY_IS_LOGGED_IN, true);
                                    editor.putString(KEY_EMAIL, email);
                                    editor.apply();

                                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                    startHomeActivity(email);
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
                        Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        btnLogin.setEnabled(true);
                        btnLogin.setText("Login");
                    }
                });
    }

    private void startHomeActivity(String email) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }

    public static void logout(android.content.Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
