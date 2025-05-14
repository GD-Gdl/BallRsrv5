package com.example.ballrsrv;

import android.content.Intent;
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

public class SignUpActivity extends AppCompatActivity {

    private Button btnCreate;
    private EditText etEmailOrContact, etPassword, etConfirmPassword;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        databaseRef = FirebaseDatabase.getInstance("https://ballrsrv-a94eb-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");

        etEmailOrContact = findViewById(R.id.etEmailOrContact);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnCreate = findViewById(R.id.btnCreate);

        btnCreate.setOnClickListener(v -> handleSignUp());
    }

    private void handleSignUp() {
        String emailOrContact = etEmailOrContact.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

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

        // ❌ Removed email/contact format validation
        // ❌ Removed password length check

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            return;
        }

        checkIfUserExists(emailOrContact, password);
    }

    private void checkIfUserExists(String emailOrContact, String password) {
        databaseRef.orderByChild("identifier").equalTo(emailOrContact)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            etEmailOrContact.setError("User already exists");
                        } else {
                            createUser(emailOrContact, password);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SignUpActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createUser(String emailOrContact, String password) {
        String userId = databaseRef.push().getKey();
        if (userId == null) return;

        User user = new User(emailOrContact, password);

        databaseRef.child(userId).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SignUpActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                intent.putExtra("email", emailOrContact);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(SignUpActivity.this, "Failed to create account", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
