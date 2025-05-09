package com.example.ballrsrv;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {
    Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        btnCreate = findViewById(R.id.btnCreate);

        btnCreate.setOnClickListener(v -> {
            // TODO: Add sign up logic
            finish(); // Go back to login
        });
    }
}