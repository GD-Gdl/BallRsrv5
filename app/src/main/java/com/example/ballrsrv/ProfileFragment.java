package com.example.ballrsrv;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    private String userEmail;
    private boolean isGuest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Get user email and guest status from activity
        if (getActivity() != null) {
            userEmail = getActivity().getIntent().getStringExtra("email");
            isGuest = getActivity().getIntent().getBooleanExtra("isGuest", false);
        }

        // Initialize views
        TextView emailText = view.findViewById(R.id.emailText);
        Button logoutButton = view.findViewById(R.id.logoutButton);

        // Set email
        emailText.setText(userEmail != null ? userEmail : "Guest User");

        // Set up logout button
        logoutButton.setOnClickListener(v -> {
            // Create intent to start LoginActivity
            Intent intent = new Intent(getContext(), LoginActivity.class);
            // Clear all activities in the stack and start fresh
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            // Finish the current activity
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        return view;
    }
} 