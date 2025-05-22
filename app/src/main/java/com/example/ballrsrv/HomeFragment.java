package com.example.ballrsrv;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;

public class HomeFragment extends Fragment {
    private String userEmail;
    private boolean isGuest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Get user email and guest status from activity
        if (getActivity() != null) {
            userEmail = getActivity().getIntent().getStringExtra("email");
            isGuest = getActivity().getIntent().getBooleanExtra("isGuest", false);
        }

        // Initialize buttons from each card's included layout
        View card1 = view.findViewById(R.id.card1);
        View card2 = view.findViewById(R.id.card2);
        View card3 = view.findViewById(R.id.card3);

        MaterialButton btn1Book = card1.findViewById(R.id.btnBook);
        MaterialButton btn2Book = card2.findViewById(R.id.btnBook);
        MaterialButton btn3Book = card3.findViewById(R.id.btnBook);

        // Set up click listeners
        btn1Book.setOnClickListener(v -> startBooking("YMCA Basketball Court"));
        btn2Book.setOnClickListener(v -> startBooking("Irisan Basketball Court"));
        btn3Book.setOnClickListener(v -> startBooking("St. Vincent Basketball Court"));

        return view;
    }

    private void startBooking(String courtName) {
        if (isGuest) {
            Toast.makeText(getContext(), "Please log in to make a booking", Toast.LENGTH_SHORT).show();
            LoginActivity.logout(getContext());
            return;
        }

        Intent intent = new Intent(getContext(), BookingActivity.class);
        intent.putExtra("email", userEmail);
        intent.putExtra("courtName", courtName);
        startActivity(intent);
    }
} 