package com.example.ballrsrv;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin, btnSignUp, btnGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnGuest = findViewById(R.id.btnGuest);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill all fields",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // For now, just navigate to HomeActivity
            // TODO: Add proper authentication logic later
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish(); // Close login activity
        });

        btnSignUp.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });
        btnGuest.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, VenueCard.class));
        });
    }
}
