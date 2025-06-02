package com.example.ballrsrv;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import android.content.ContentResolver;

public class CourtsFragment extends Fragment {
    private RecyclerView recyclerView;
    private CourtsAdapter adapter;
    private List<Court> courtsList;
    private DatabaseReference courtsRef;
    private Uri selectedImageUri;
    private Bitmap selectedImageBitmap;
    private ImageView courtImagePreview;
    private AlertDialog currentDialog;
    private AlertDialog progressDialog;
    private static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/jpg", "image/png"};
    private static final int MAX_IMAGE_SIZE_MB = 5;
    private static final int MAX_IMAGE_DIMENSION = 1024; // Maximum width or height for the image

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                selectedImageUri = result.getData().getData();
                if (selectedImageUri == null) {
                    Log.e("CourtsFragment", "Selected image URI is null");
                    Toast.makeText(getContext(), "Failed to get image URI", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (currentDialog != null) {
                    ImageView imagePreview = currentDialog.findViewById(R.id.imagePreview);
                    if (imagePreview != null) {
                        try {
                            ContentResolver resolver = requireContext().getContentResolver();
                            InputStream inputStream = resolver.openInputStream(selectedImageUri);
                            selectedImageBitmap = BitmapFactory.decodeStream(inputStream);
                            if (inputStream != null) {
                                inputStream.close();
                            }

                            if (selectedImageBitmap == null) {
                                Log.e("CourtsFragment", "Selected image bitmap is null after decoding");
                                Toast.makeText(getContext(), "Failed to decode image", Toast.LENGTH_SHORT).show();
                                resetUploadState();
                                return;
                            }

                            // Resize the image if it's too large
                            selectedImageBitmap = resizeImageIfNeeded(selectedImageBitmap);
                            imagePreview.setImageBitmap(selectedImageBitmap);
                            showAddCourtDialog();
                        } catch (Exception e) {
                            Log.e("CourtsFragment", "Error processing image: " + e.getMessage());
                            Toast.makeText(getContext(), "Error processing image", Toast.LENGTH_SHORT).show();
                            resetUploadState();
                        }
                    }
                }
            } else {
                Toast.makeText(getContext(), "Image selection cancelled or failed", Toast.LENGTH_SHORT).show();
            }
        }
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_courts, container, false);

        recyclerView = view.findViewById(R.id.courtsRecyclerView);
        FloatingActionButton fabAddCourt = view.findViewById(R.id.fabAddCourt);

        courtsList = new ArrayList<>();
        adapter = new CourtsAdapter(courtsList, court -> removeCourt(court));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        courtsRef = FirebaseDatabase.getInstance().getReference("courts");
        
        fabAddCourt.setOnClickListener(v -> showImageUploadDialog());
        
        loadCourts();

        return view;
    }

    private void showImageUploadDialog() {
        resetUploadState();

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_upload_image, null);
        builder.setView(dialogView);

        Button btnSelectImage = dialogView.findViewById(R.id.btnSelectImage);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        currentDialog = builder.create();

        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        btnCancel.setOnClickListener(v -> {
            currentDialog.dismiss();
            currentDialog = null;
            resetUploadState();
        });

        currentDialog.show();
    }

    private void showAddCourtDialog() {
        if (currentDialog != null) {
            currentDialog.dismiss();
            currentDialog = null;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_court, null);
        builder.setView(dialogView);

        EditText etCourtName = dialogView.findViewById(R.id.etCourtName);
        EditText etCourtLocation = dialogView.findViewById(R.id.etCourtLocation);
        EditText etCourtPrice = dialogView.findViewById(R.id.etCourtPrice);
        ImageView courtImagePreview = dialogView.findViewById(R.id.courtImagePreview);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnAdd = dialogView.findViewById(R.id.btnAdd);

        if (selectedImageBitmap != null) {
            courtImagePreview.setImageBitmap(selectedImageBitmap);
        }

        currentDialog = builder.create();

        btnCancel.setOnClickListener(v -> {
            currentDialog.dismiss();
            resetUploadState();
        });

        btnAdd.setOnClickListener(v -> {
            String name = etCourtName.getText().toString().trim();
            String location = etCourtLocation.getText().toString().trim();
            String priceStr = etCourtPrice.getText().toString().trim();

            if (name.isEmpty() || location.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedImageBitmap == null) {
                Toast.makeText(getContext(), "Please select an image first", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!validateImage(selectedImageUri)) {
                return;
            }

            try {
                double price = Double.parseDouble(priceStr);
                String courtId = courtsRef.push().getKey();
                if (courtId == null) {
                    Toast.makeText(getContext(), "Failed to generate court ID", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Convert bitmap to base64
                String base64Image = convertBitmapToBase64(selectedImageBitmap);

                Court court = new Court(courtId, name, location, price, base64Image, true);

                progressDialog = new AlertDialog.Builder(requireContext())
                    .setMessage("Adding court...")
                    .setCancelable(false)
                    .create();
                progressDialog.show();

                courtsRef.child(courtId).setValue(court)
                    .addOnSuccessListener(aVoid -> {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Court added successfully", Toast.LENGTH_SHORT).show();
                        currentDialog.dismiss();
                        resetUploadState();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Failed to add court: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        resetUploadState();
                    });
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid price format", Toast.LENGTH_SHORT).show();
            }
        });

        currentDialog.show();
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private Bitmap resizeImageIfNeeded(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width <= MAX_IMAGE_DIMENSION && height <= MAX_IMAGE_DIMENSION) {
            return bitmap;
        }

        float scale;
        if (width > height) {
            scale = (float) MAX_IMAGE_DIMENSION / width;
        } else {
            scale = (float) MAX_IMAGE_DIMENSION / height;
        }

        int newWidth = Math.round(width * scale);
        int newHeight = Math.round(height * scale);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    private void removeCourt(Court court) {
        if (court.getId() != null) {
            courtsRef.child(court.getId()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Court removed successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("CourtsFragment", "Failed to remove court: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to remove court", Toast.LENGTH_SHORT).show();
                });
        }
    }

    private void resetUploadState() {
        selectedImageUri = null;
        selectedImageBitmap = null;
        if (currentDialog != null) {
            currentDialog.dismiss();
            currentDialog = null;
        }
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private boolean validateImage(Uri imageUri) {
        try {
            long fileSize = getFileSize(imageUri);
            if (fileSize > MAX_IMAGE_SIZE_MB * 1024 * 1024) {
                Toast.makeText(getContext(), 
                    "Image size should be less than " + MAX_IMAGE_SIZE_MB + "MB", 
                    Toast.LENGTH_LONG).show();
                return false;
            }

            String mimeType = requireContext().getContentResolver().getType(imageUri);
            if (mimeType == null || !isAllowedImageType(mimeType)) {
                Toast.makeText(getContext(), 
                    "Please select a JPG or PNG image", 
                    Toast.LENGTH_LONG).show();
                return false;
            }

            return true;
        } catch (Exception e) {
            Log.e("CourtsFragment", "Error validating image: " + e.getMessage());
            Toast.makeText(getContext(), "Error validating image", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private long getFileSize(Uri uri) {
        try {
            return requireContext().getContentResolver().openFileDescriptor(uri, "r").getStatSize();
        } catch (Exception e) {
            Log.e("CourtsFragment", "Error getting file size: " + e.getMessage());
            return 0;
        }
    }

    private boolean isAllowedImageType(String mimeType) {
        for (String type : ALLOWED_IMAGE_TYPES) {
            if (type.equals(mimeType)) {
                return true;
            }
        }
        return false;
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
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CourtsFragment", "Failed to load courts: " + error.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        resetUploadState();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        resetUploadState();
    }
} 