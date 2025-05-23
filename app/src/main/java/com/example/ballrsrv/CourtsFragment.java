package com.example.ballrsrv;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import android.content.ContentResolver;

public class CourtsFragment extends Fragment {
    private RecyclerView recyclerView;
    private CourtsAdapter adapter;
    private List<Court> courtsList;
    private DatabaseReference courtsRef;
    private Uri selectedImageUri;
    private ImageView courtImagePreview;
    private String uploadedImagePath = null;
    private AlertDialog currentDialog;
    private AlertDialog progressDialog;
    private static final int MAX_IMAGE_SIZE_MB = 15; // 15MB limit
    private static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/jpg", "image/png"};
    private Bitmap selectedImageBitmap = null; // Store the bitmap in memory

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
                            // Read the image once and store it in memory
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

                            imagePreview.setImageBitmap(selectedImageBitmap);
                            // Show add court dialog immediately after image selection
                            showAddCourtDialog();
                        } catch (FileNotFoundException e) {
                            Log.e("CourtsFragment", "Error reading image (FileNotFound): " + e.getMessage());
                            Toast.makeText(getContext(), "Error reading image", Toast.LENGTH_SHORT).show();
                            resetUploadState();
                        } catch (IOException e) {
                            Log.e("CourtsFragment", "Error reading image (IOException): " + e.getMessage());
                            Toast.makeText(getContext(), "Error reading image", Toast.LENGTH_SHORT).show();
                            resetUploadState();
                        } catch (SecurityException e) {
                            Log.e("CourtsFragment", "Security error reading image: " + e.getMessage());
                            Toast.makeText(getContext(), "Permission denied to read image", Toast.LENGTH_SHORT).show();
                            resetUploadState();
                        } catch (Exception e) {
                            Log.e("CourtsFragment", "Unexpected error reading image: " + e.getMessage());
                            Toast.makeText(getContext(), "Error processing image", Toast.LENGTH_SHORT).show();
                            resetUploadState();
                        }
                    }
                } else {
                     Log.w("CourtsFragment", "currentDialog is null in imagePickerLauncher result");
                }
            } else {
                 Log.w("CourtsFragment", "Activity result not OK or data is null");
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
        adapter = new CourtsAdapter(courtsList, new CourtsAdapter.OnCourtActionListener() {
            @Override
            public void onRemoveCourt(Court court) {
                removeCourt(court);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Initialize Firebase references
        courtsRef = FirebaseDatabase.getInstance().getReference("courts");

        fabAddCourt.setOnClickListener(v -> showImageUploadDialog());

        loadCourts();

        return view;
    }

    private void showImageUploadDialog() {
        // Reset any previous upload state before showing a new dialog
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
            // Dismiss image upload dialog and reset state
            currentDialog.dismiss();
            currentDialog = null;
            resetUploadState();
        });

        currentDialog.show();
    }

    private void showAddCourtDialog() {
        // Dismiss the image upload dialog if it's still showing
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

        // Display the selected image
        if (selectedImageBitmap != null) {
            courtImagePreview.setImageBitmap(selectedImageBitmap);
        }

        AlertDialog addCourtDialog = builder.create(); // Use a different name for this dialog

        btnCancel.setOnClickListener(v -> {
            // Dismiss add court dialog and reset state
            addCourtDialog.dismiss();
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

            // Validate image before saving
             if (!validateImage(selectedImageUri)) {
                 return;
             }

            // Show progress dialog
            progressDialog = new AlertDialog.Builder(requireContext())
                .setMessage("Adding court...")
                .setCancelable(false)
                .create();
            progressDialog.show();

            // Create directory if it doesn't exist
            File imagesDir = new File(requireContext().getFilesDir(), "court_images");
            if (!imagesDir.exists()) {
                imagesDir.mkdirs();
            }

            // Create unique filename
            String timestamp = String.valueOf(System.currentTimeMillis());
            String imageId = "court_" + timestamp + ".jpg";
            File imageFile = new File(imagesDir, imageId);

            try {
                // Save the bitmap to file
                FileOutputStream fos = new FileOutputStream(imageFile);
                selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();

                uploadedImagePath = imageFile.getAbsolutePath();
                Log.d("CourtsFragment", "Image saved at: " + uploadedImagePath);

                // Add court to database
                double price = Double.parseDouble(priceStr);
                addCourtToDatabase(name, location, price, uploadedImagePath);
                addCourtDialog.dismiss(); // Dismiss the add court dialog

            } catch (IOException e) {
                progressDialog.dismiss();
                Log.e("CourtsFragment", "Error saving image: " + e.getMessage());
                Toast.makeText(getContext(), "Failed to save image: " + e.getMessage(), Toast.LENGTH_LONG).show();
                resetUploadState();
             } catch (SecurityException e) {
                 progressDialog.dismiss();
                 Log.e("CourtsFragment", "Security error saving image: " + e.getMessage());
                 Toast.makeText(getContext(), "Permission denied to save image", Toast.LENGTH_SHORT).show();
                 resetUploadState();
             } catch (Exception e) {
                 progressDialog.dismiss();
                 Log.e("CourtsFragment", "Unexpected error saving image: " + e.getMessage());
                 Toast.makeText(getContext(), "Error saving image", Toast.LENGTH_SHORT).show();
                 resetUploadState();
            }
        });

        addCourtDialog.show(); // Show the add court dialog
    }

    private void addCourtToDatabase(String name, String location, double price, String imagePath) {
        String courtId = courtsRef.push().getKey();
        Court court = new Court(courtId, name, location, price, imagePath);

        courtsRef.child(courtId).setValue(court)
            .addOnSuccessListener(aVoid -> {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Court added successfully", Toast.LENGTH_SHORT).show();
                // Clear the uploaded image data
                resetUploadState();
            })
            .addOnFailureListener(e -> {
                progressDialog.dismiss();
                Log.e("CourtsFragment", "Failed to add court: " + e.getMessage());
                Toast.makeText(getContext(), "Failed to add court: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                // Also clear state on failure
                resetUploadState();
            });
    }

    private void removeCourt(Court court) {
        if (court.getId() != null) {
            // Delete the court image from local storage
            if (court.getImageUrl() != null && !court.getImageUrl().isEmpty()) {
                File imageFile = new File(court.getImageUrl());
                if (imageFile.exists()) {
                    imageFile.delete();
                } else {
                     Log.w("CourtsFragment", "Image file not found at: " + court.getImageUrl());
                }
            }
            // Delete the court from Database
            deleteCourtFromDatabase(court);
        }
    }

    private void deleteCourtFromDatabase(Court court) {
        courtsRef.child(court.getId()).removeValue()
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(getContext(), "Court removed successfully", Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(e -> {
                Log.e("CourtsFragment", "Failed to remove court: " + e.getMessage());
                Toast.makeText(getContext(), "Failed to remove court", Toast.LENGTH_SHORT).show();
            });
    }

    private void resetUploadState() {
        selectedImageUri = null;
        uploadedImagePath = null;
        selectedImageBitmap = null; // Clear the bitmap from memory
        if (currentDialog != null) {
            currentDialog.dismiss();
            currentDialog = null;
        }
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
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

    private boolean validateImage(Uri imageUri) {
        try {
            // Check file size
            long fileSize = getFileSize(imageUri);
            if (fileSize > MAX_IMAGE_SIZE_MB * 1024 * 1024) {
                Toast.makeText(getContext(), 
                    "Image size should be less than " + MAX_IMAGE_SIZE_MB + "MB", 
                    Toast.LENGTH_LONG).show();
                return false;
            }

            // Check file type
            String mimeType = getContext().getContentResolver().getType(imageUri);
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
            return getContext().getContentResolver().openFileDescriptor(uri, "r").getStatSize();
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
                Toast.makeText(getContext(), "Failed to load courts", Toast.LENGTH_SHORT).show();
            }
        });
    }
} 