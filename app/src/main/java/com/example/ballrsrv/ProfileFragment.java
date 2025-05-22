package com.example.ballrsrv;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.*;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Get user email from activity
        String userEmail = getActivity() != null ? 
            getActivity().getIntent().getStringExtra("email") : "Guest User";

        // Set email text
        TextView emailText = view.findViewById(R.id.emailText);
        emailText.setText(userEmail);

        // Set up password text
        TextView passwordText = view.findViewById(R.id.passwordText);
        if (userEmail != null && !userEmail.equals("Guest User")) {
            // Get user's password from Firebase
            String userKey = userEmail.replace(".", "_");
            databaseReference.child("users").child(userKey).child("password")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String password = snapshot.getValue(String.class);
                            if (password != null) {
                                // Show masked password
                                passwordText.setText("********");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), "Error loading password", Toast.LENGTH_SHORT).show();
                    }
                });
        } else {
            passwordText.setText("Not available for guest users");
        }

        // Set up change password button
        Button btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(v -> {
            if (userEmail != null && !userEmail.equals("Guest User")) {
                // TODO: Implement change password dialog or activity
                Toast.makeText(getActivity(), "Change password feature coming soon", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Not available for guest users", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up logout button with simplified code
        Button logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            // Sign out from Firebase Auth
            mAuth.signOut();
            
            // Go directly to login activity
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        });

        return view;
    }
} 