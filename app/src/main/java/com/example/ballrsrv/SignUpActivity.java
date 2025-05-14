package com.example.ballrsrv;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    Button btnCreate;
    private EditText etEmailOrContact;
    private EditText etPassword;
    private EditText etConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize views
        etEmailOrContact = findViewById(R.id.etEmailOrContact);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnCreate = findViewById(R.id.btnCreate);

        btnCreate.setOnClickListener(v -> {
            // TODO: Add sign up logic
            finish(); // Go back to login
        });
        btnCreate.setOnClickListener(v -> handleSignUp());
    }

    private void handleSignUp() {
        // Get input values
        String emailOrContact = etEmailOrContact.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(emailOrContact)) {
            etEmailOrContact.setError("Email or Contact Number is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Please confirm your password");
            return;
        }

        // Validate email/contact format
        if (!isValidEmailOrContact(emailOrContact)) {
            etEmailOrContact.setError("Invalid email or contact number format");
            return;
        }

        // Validate password length
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            return;
        }

        // TODO: Add your user registration logic here
        // For example, save to database or make API call

        // Show success message
        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();

        // Create intent to navigate to HomeActivity
        Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
        // Add any extra data you want to pass to HomeActivity
        intent.putExtra("email", emailOrContact);
        // Clear the activity stack so user can't go back to signup/login
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Close the current activity
    }

    private boolean isValidEmailOrContact(String input) {
        // Email regex pattern
        String emailPattern = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        // Phone number pattern (adjust based on your requirements)
        String phonePattern = "^[0-9]{10,11}$";

        return Pattern.compile(emailPattern, Pattern.CASE_INSENSITIVE).matcher(input).matches() ||
                Pattern.compile(phonePattern).matcher(input).matches();
    }
}