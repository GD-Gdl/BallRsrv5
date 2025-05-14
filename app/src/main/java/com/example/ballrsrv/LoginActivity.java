package com.example.ballrsrv;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText etEmail, etPassword;
    Button btnLogin, btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Check for admin credentials
            if (email.equals("123") && password.equals("123")) {
                // Show success message
                Toast.makeText(this, "Welcome Admin!", Toast.LENGTH_SHORT).show();
                // Navigate to AdminActivity
                startActivity(new Intent(this, AdminActivity.class));
                finish(); // Close the login activity
            } else {
                // Regular user login
                // TODO: Add regular user authentication logic
                startActivity(new Intent(this, HomeActivity.class));
            }
        });

        btnSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });
    }
}
