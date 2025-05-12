package com.example.ballrsrv;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentStatusActivityGcaash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_gcash); // Make sure your XML is named activity_payment_mode.xml

        Button btnHome = findViewById(R.id.btnHome);

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentStatusActivityGcaash.this, HomeActivity.class);
            // Optional: clear the back stack so user can't return to payment screen
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
