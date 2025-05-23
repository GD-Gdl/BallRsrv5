package com.example.ballrsrv;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements CustomerCourtsAdapter.OnBookCourtListener {
    private String userEmail;
    private boolean isGuest;
    private RecyclerView courtsRecyclerView;
    private CustomerCourtsAdapter courtsAdapter;
    private List<Court> courtsList;
    private DatabaseReference courtsRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Get user email and guest status from activity
        if (getActivity() != null) {
            userEmail = getActivity().getIntent().getStringExtra("email");
            isGuest = getActivity().getIntent().getBooleanExtra("isGuest", false);
        }

        // Initialize RecyclerView and Adapter
        courtsRecyclerView = view.findViewById(R.id.courtsRecyclerView);
        courtsList = new ArrayList<>();
        courtsAdapter = new CustomerCourtsAdapter(courtsList, this);
        courtsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        courtsRecyclerView.setAdapter(courtsAdapter);

        // Initialize Firebase
        courtsRef = FirebaseDatabase.getInstance().getReference("courts");

        // Load courts from Firebase
        loadCourts();

        return view;
    }

    private void loadCourts() {
        courtsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                courtsList.clear();
                for (DataSnapshot courtSnapshot : snapshot.getChildren()) {
                    Court court = courtSnapshot.getValue(Court.class);
                    if (court != null) {
                        courtsList.add(court);
                    } else {
                        Log.w("HomeFragment", "Court data is null for snapshot: " + courtSnapshot.getKey());
                    }
                }
                courtsAdapter.notifyDataSetChanged();
                Log.d("HomeFragment", "Loaded " + courtsList.size() + " courts");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load courts: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("HomeFragment", "Failed to load courts", error.toException());
            }
        });
    }

    @Override
    public void onBookCourt(Court court) {
        startBooking(court.getName()); // Assuming startBooking uses court name
        // If startBooking needs more court details, you will need to modify it
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