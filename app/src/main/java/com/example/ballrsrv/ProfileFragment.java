package com.example.ballrsrv;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.*;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {
    private static final String DATABASE_URL = "https://ballrsrv-a94eb-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance(DATABASE_URL).getReference();
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
                showChangePasswordDialog(userEmail);
            } else {
                Toast.makeText(getActivity(), "Not available for guest users", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up logout button
        Button logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            // Sign out from Firebase Auth
            mAuth.signOut();
            
            // Use the centralized logout method
            LoginActivity.logout(getActivity());
        });

        return view;
    }

    private void showChangePasswordDialog(String userEmail) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        builder.setView(dialogView);

        EditText etCurrentPassword = dialogView.findViewById(R.id.etCurrentPassword);
        EditText etNewPassword = dialogView.findViewById(R.id.etNewPassword);
        EditText etConfirmNewPassword = dialogView.findViewById(R.id.etConfirmNewPassword);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnChangePassword = dialogView.findViewById(R.id.btnChangePassword);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnChangePassword.setOnClickListener(v -> {
            String currentPassword = etCurrentPassword.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmNewPassword = etConfirmNewPassword.getText().toString().trim();

            // Validate inputs
            if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword) || 
                TextUtils.isEmpty(confirmNewPassword)) {
                Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPassword.length() < 6) {
                Toast.makeText(getActivity(), "New password must be at least 6 characters long", 
                    Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmNewPassword)) {
                Toast.makeText(getActivity(), "New passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verify current password and update to new password
            String userKey = userEmail.replace(".", "_");
            databaseReference.child("users").child(userKey).child("password")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String storedPassword = snapshot.getValue(String.class);
                            if (storedPassword != null && storedPassword.equals(currentPassword)) {
                                // Update password in Firebase
                                databaseReference.child("users").child(userKey).child("password")
                                    .setValue(newPassword)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getActivity(), 
                                            "Password updated successfully", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getActivity(), 
                                            "Failed to update password: " + e.getMessage(), 
                                            Toast.LENGTH_SHORT).show();
                                    });
                            } else {
                                Toast.makeText(getActivity(), 
                                    "Current password is incorrect", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), 
                            "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        });
    }
} 