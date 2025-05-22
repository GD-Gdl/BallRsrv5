package com.example.ballrsrv;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

public class BookingStatusFragment extends Fragment {
    private String userEmail;
    private boolean isGuest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_booking_status, container, false);

        // Get user email and guest status from activity
        if (getActivity() != null) {
            userEmail = getActivity().getIntent().getStringExtra("email");
            isGuest = getActivity().getIntent().getBooleanExtra("isGuest", false);
        }

        // If user is a guest, redirect to login
        if (isGuest) {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
            return view;
        }

        // The booking status activity layout already contains the necessary views
        // We just need to ensure the email is passed correctly
        return view;
    }
} 