package com.example.ballrsrv;

import android.content.Intent;
import android.os.Bundle;
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
    EditText etEmail, etPassword;
    Button btnLogin, btnSignUp;
    DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase reference
        databaseRef = FirebaseDatabase.getInstance("https://ballrsrv-a94eb-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.equals("123") && password.equals("123")) {
                Toast.makeText(this, "Welcome Admin!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, AdminActivity.class));
                finish();
            } else {
                loginUser(email, password);
            }
        });

        btnSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });
    }

    private void loginUser(String emailOrContact, String password) {
        databaseRef.orderByChild("identifier").equalTo(emailOrContact)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean matchFound = false;
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String dbPassword = userSnapshot.child("password").getValue(String.class);
                            if (dbPassword != null && dbPassword.equals(password)) {
                                matchFound = true;
                                break;
                            }
                        }

                        if (matchFound) {
                            Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.putExtra("email", emailOrContact);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
