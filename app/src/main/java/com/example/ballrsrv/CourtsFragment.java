package com.example.ballrsrv;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
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
    private static final int PERMISSION_REQUEST_CODE = 123;
    private static final String TAG = "CourtsFragment";
    private RecyclerView recyclerView;
    private CourtsAdapter adapter;
    private List<Court> courtsList;
    private DatabaseReference courtsRef;
    private Uri selectedImageUri;
    private ImageView courtImagePreview;
    private AlertDialog currentDialog;
    private AlertDialog progressDialog;
    private static final int MAX_IMAGE_SIZE_MB = 1; // Reduced to 1MB for base64
    private static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/jpg", "image/png"};
    private Bitmap selectedImageBitmap = null;

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
                            // Read and compress the image
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

                            // Compress the bitmap to reduce size
                            int originalWidth = selectedImageBitmap.getWidth();
                            int originalHeight = selectedImageBitmap.getHeight();
                            int maxDimension = 800; // Max dimension for the compressed image

                            if (originalWidth > maxDimension || originalHeight > maxDimension) {
                                float ratio = Math.min((float) maxDimension / originalWidth, (float) maxDimension / originalHeight);
                                int newWidth = Math.round(originalWidth * ratio);
                                int newHeight = Math.round(originalHeight * ratio);
                                selectedImageBitmap = Bitmap.createScaledBitmap(selectedImageBitmap, newWidth, newHeight, true);
                            }

                            imagePreview.setImageBitmap(selectedImageBitmap);
                            showAddCourtDialog();
                        } catch (Exception e) {
                            Log.e("CourtsFragment", "Error reading image: " + e.getMessage());
                            Toast.makeText(getContext(), "Error reading image", Toast.LENGTH_SHORT).show();
                            resetUploadState();
                        }
                    }
                }
            } else {
                Log.w("CourtsFragment", "Image selection cancelled or failed");
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
        FirebaseApp.initializeApp(requireContext());
        courtsRef = FirebaseDatabase.getInstance().getReference("courts");

        fabAddCourt.setOnClickListener(v -> {
            if (checkAndRequestPermissions()) {
                showImageUploadDialog();
            }
        });

        loadCourts();

        return view;
    }

    private boolean checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_CODE);
                return false;
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        PERMISSION_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                showImageUploadDialog();
            } else {
                Toast.makeText(getContext(), "Storage permissions are required to add courts", Toast.LENGTH_LONG).show();
            }
        }
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

        AlertDialog addCourtDialog = builder.create();

        btnCancel.setOnClickListener(v -> {
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

            if (!validateImage(selectedImageUri)) {
                return;
            }

            progressDialog = new AlertDialog.Builder(requireContext())
                .setMessage("Processing image...")
                .setCancelable(false)
                .create();
            progressDialog.show();

            try {
                // Convert bitmap to base64
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] imageData = baos.toByteArray();
                String base64Image = Base64.encodeToString(imageData, Base64.DEFAULT);

                // Add court to database with base64 image
                double price = Double.parseDouble(priceStr);
                addCourtToDatabase(name, location, price, base64Image);
                addCourtDialog.dismiss();

            } catch (Exception e) {
                progressDialog.dismiss();
                Log.e("CourtsFragment", "Error converting image: " + e.getMessage());
                Toast.makeText(getContext(), "Error processing image: " + e.getMessage(), Toast.LENGTH_LONG).show();
                resetUploadState();
            }
        });

        addCourtDialog.show();
    }

    private void addCourtToDatabase(String name, String location, double price, String base64Image) {
        String courtId = courtsRef.push().getKey();
        if (courtId == null) {
            progressDialog.dismiss();
            Toast.makeText(getContext(), "Failed to generate court ID", Toast.LENGTH_SHORT).show();
            resetUploadState();
            return;
        }

        Court court = new Court(courtId, name, location, price, base64Image);
        Log.d("CourtsFragment", "Adding court to database with base64 image");

        courtsRef.child(courtId).setValue(court)
            .addOnSuccessListener(aVoid -> {
                progressDialog.dismiss();
                Log.d("CourtsFragment", "Court added successfully to database");
                Toast.makeText(getContext(), "Court added successfully", Toast.LENGTH_SHORT).show();
                resetUploadState();
            })
            .addOnFailureListener(e -> {
                progressDialog.dismiss();
                Log.e("CourtsFragment", "Failed to add court to database: " + e.getMessage());
                Toast.makeText(getContext(), "Failed to add court: " + e.getMessage(), Toast.LENGTH_LONG).show();
                resetUploadState();
            });
    }

    private void removeCourt(Court court) {
        if (court.getId() != null) {
            AlertDialog progressDialog = new AlertDialog.Builder(requireContext())
                .setMessage("Removing court...")
                .setCancelable(false)
                .create();
            progressDialog.show();

            courtsRef.child(court.getId()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Log.d("CourtsFragment", "Court removed successfully");
                    Toast.makeText(getContext(), "Court removed successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Log.e("CourtsFragment", "Failed to remove court: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to remove court: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
            long fileSize = getFileSize(imageUri);
            if (fileSize > MAX_IMAGE_SIZE_MB * 1024 * 1024) {
                Toast.makeText(getContext(), 
                    "Image size should be less than " + MAX_IMAGE_SIZE_MB + "MB", 
                    Toast.LENGTH_LONG).show();
                return false;
            }

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