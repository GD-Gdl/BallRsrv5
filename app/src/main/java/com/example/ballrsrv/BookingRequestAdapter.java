package com.example.ballrsrv;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.*;

import java.io.File;
import java.util.List;

import android.util.Base64;

public class BookingRequestAdapter extends RecyclerView.Adapter<BookingRequestAdapter.ViewHolder> {
    private List<BookingRequest> requests;
    private OnRequestActionListener listener;
    private DatabaseReference courtsRef;
    private DatabaseReference bookingsRef;

    public BookingRequestAdapter(List<BookingRequest> requests, OnRequestActionListener listener) {
        this.requests = requests;
        this.listener = listener;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.courtsRef = database.getReference().child("courts");
        this.bookingsRef = database.getReference().child("bookings");
    }

    public interface OnRequestActionListener {
        void onAccept(BookingRequest request);
        void onDeny(BookingRequest request);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingRequest request = requests.get(position);
        holder.userName.setText("Customer: " + request.getUserName());
        holder.date.setText("Date: " + request.getDate());
        holder.timeSlot.setText("Time: " + request.getTimeSlot());
        holder.details.setText("Details: " + request.getBookingDetails());
        
        // Extract court name from booking details
        String courtName = request.getBookingDetails().split(" - ")[0];
        if (courtName.startsWith("Booking for ")) {
            courtName = courtName.substring("Booking for ".length());
        }

        // Load court image
        loadCourtImage(courtName, holder.courtImage);
        
        // Set payment method
        String paymentMethod = request.getPaymentMethod();
        if (paymentMethod != null && !paymentMethod.equals("none")) {
            holder.paymentMethod.setVisibility(View.VISIBLE);
            holder.paymentMethod.setText("Payment Method: " + paymentMethod.toUpperCase());
        } else {
            holder.paymentMethod.setVisibility(View.GONE);
        }

        // Show reference code for all requests (if available)
        String referenceCode = request.getReferenceCode();
        if (referenceCode != null && !referenceCode.isEmpty()) {
            holder.referenceCode.setVisibility(View.VISIBLE);
            if ("gcash".equals(paymentMethod)) {
                holder.referenceCode.setText("GCash Reference: " + referenceCode);
            } else {
                holder.referenceCode.setText("Reference Code: " + referenceCode);
            }
        } else {
            holder.referenceCode.setVisibility(View.GONE);
        }

        // Show/hide buttons based on status
        String status = request.getStatus();
        if ("pending".equals(status)) {
            holder.acceptButton.setVisibility(View.VISIBLE);
            holder.denyButton.setVisibility(View.VISIBLE);
            holder.statusText.setVisibility(View.GONE);
        } else {
            holder.acceptButton.setVisibility(View.GONE);
            holder.denyButton.setVisibility(View.GONE);
            holder.statusText.setVisibility(View.VISIBLE);
            holder.statusText.setText("Status: " + status.toUpperCase());
        }
        
        holder.acceptButton.setOnClickListener(v -> {
            // Update the request status in Firebase
            String userKey = request.getEmail().replace(".", "_");
            DatabaseReference requestRef = bookingsRef.child(userKey).child(request.getId());
            
            requestRef.child("status").setValue("accepted")
                .addOnSuccessListener(aVoid -> {
                    request.setStatus("accepted");
                    notifyItemChanged(holder.getAdapterPosition());
                    if (listener != null) {
                        listener.onAccept(request);
                    }
                });
        });

        holder.denyButton.setOnClickListener(v -> {
            // Update the request status in Firebase
            String userKey = request.getEmail().replace(".", "_");
            DatabaseReference requestRef = bookingsRef.child(userKey).child(request.getId());
            
            requestRef.child("status").setValue("denied")
                .addOnSuccessListener(aVoid -> {
                    request.setStatus("denied");
                    notifyItemChanged(holder.getAdapterPosition());
                    if (listener != null) {
                        listener.onDeny(request);
                    }
                });
        });
    }

    private void loadCourtImage(String courtName, ImageView imageView) {
        courtsRef.orderByChild("name").equalTo(courtName)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot courtSnapshot : snapshot.getChildren()) {
                            Court court = courtSnapshot.getValue(Court.class);
                            if (court != null && court.getImageBase64() != null) {
                                try {
                                    byte[] decodedString = Base64.decode(court.getImageBase64(), Base64.DEFAULT);
                                    Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                    imageView.setImageBitmap(decodedBitmap);
                                } catch (Exception e) {
                                    imageView.setImageResource(getDefaultImageResource(courtName));
                                }
                                break;
                            }
                        }
                    } else {
                        imageView.setImageResource(getDefaultImageResource(courtName));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    imageView.setImageResource(getDefaultImageResource(courtName));
                }
            });
    }

    private int getDefaultImageResource(String courtName) {
        switch (courtName) {
            case "YMCA Basketball Court":
                return R.drawable.ymca_hostel_baguio06;
            case "Irisan Basketball Court":
                return R.drawable.irisan;
            case "St. Vincent Basketball Court":
                return R.drawable.vincent;
            default:
                return android.R.drawable.ic_menu_gallery;
        }
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userName;
        public TextView date;
        public TextView timeSlot;
        public TextView details;
        public TextView paymentMethod;
        public TextView referenceCode;
        public TextView statusText;
        public Button acceptButton;
        public Button denyButton;
        public ImageView courtImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.textUserName);
            date = itemView.findViewById(R.id.textDate);
            timeSlot = itemView.findViewById(R.id.textTimeSlot);
            details = itemView.findViewById(R.id.textBookingDetails);
            paymentMethod = itemView.findViewById(R.id.textPaymentMethod);
            referenceCode = itemView.findViewById(R.id.textReferenceCode);
            statusText = itemView.findViewById(R.id.textStatus);
            acceptButton = itemView.findViewById(R.id.btnAccept);
            denyButton = itemView.findViewById(R.id.btnDeny);
            courtImage = itemView.findViewById(R.id.courtImage);
        }
    }
}
