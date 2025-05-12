package com.example.ballrsrv;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class VenueCard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.venue_card);

        // Location 1
        ImageView image1 = findViewById(R.id.image1);
        TextView locationName1 = findViewById(R.id.locationName1);
        TextView locationDistance1 = findViewById(R.id.locationDistance1);

        // Location 2
        ImageView image2 = findViewById(R.id.image2);
        TextView locationName2 = findViewById(R.id.locationName2);
        TextView locationDistance2 = findViewById(R.id.locationDistance2);

        // Location 3
        ImageView image3 = findViewById(R.id.image3);
        TextView locationName3 = findViewById(R.id.locationName3);
        TextView locationDistance3 = findViewById(R.id.locationDistance3);

        // Optionally set or update the text/images programmatically
        locationName1.setText("Irisan Basketball Court");
        locationDistance1.setText("2.5 km away");
        // image1.setImageResource(R.drawable.irisan); // Already set in XML

        locationName2.setText("St. Vincent Basketball Gym");
        locationDistance2.setText("5.0 km away");
        // image2.setImageResource(R.drawable.vincent);

        locationName3.setText("YMCA Basketball Court");
        locationDistance3.setText("7.8 km away");
        // image3.setImageResource(R.drawable.ymca_hostel_baguio06);

        // Example: Set click listeners if you want to handle taps
        /*
        image1.setOnClickListener(v -> {
            // Handle click for location 1
        });
        */
    }
}
