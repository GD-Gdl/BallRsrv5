package com.example.ballrsrv;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import java.util.Locale;

public class CourtsAdapter extends RecyclerView.Adapter<CourtsAdapter.CourtViewHolder> {

    private List<Court> courtsList;
    private OnCourtActionListener listener;

    public interface OnCourtActionListener {
        void onRemoveCourt(Court court);
    }

    public CourtsAdapter(List<Court> courtsList, OnCourtActionListener listener) {
        this.courtsList = courtsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_court, parent, false);
        return new CourtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourtViewHolder holder, int position) {
        Court court = courtsList.get(position);
        holder.courtName.setText(court.getName());
        holder.courtLocation.setText(court.getLocation());
        holder.courtPrice.setText(String.format(Locale.getDefault(), "₱%.2f per hour", court.getPrice()));

        // Load court image from base64
        if (court.getImageUrl() != null && !court.getImageUrl().isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(court.getImageUrl(), Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.courtImage.setImageBitmap(decodedBitmap);
            } catch (Exception e) {
                holder.courtImage.setImageResource(getDefaultImageResource(court.getName()));
            }
        } else {
            holder.courtImage.setImageResource(getDefaultImageResource(court.getName()));
        }

        // Show remove button for admin view
        holder.btnRemove.setVisibility(View.VISIBLE);
        holder.btnRemove.setOnClickListener(v -> {
            // Show confirmation dialog
            new AlertDialog.Builder(holder.itemView.getContext())
                .setTitle("Remove Court")
                .setMessage("Are you sure you want to remove " + court.getName() + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (listener != null) {
                        listener.onRemoveCourt(court);
                    }
                })
                .setNegativeButton("No", null)
                .show();
        });

        // Hide book button in admin view
        holder.btnBook.setVisibility(View.GONE);
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
        return courtsList.size();
    }

    public static class CourtViewHolder extends RecyclerView.ViewHolder {
        ImageView courtImage;
        TextView courtName;
        TextView courtLocation;
        TextView courtPrice;
        Button btnBook;
        Button btnRemove;

        public CourtViewHolder(@NonNull View itemView) {
            super(itemView);
            courtImage = itemView.findViewById(R.id.courtImage);
            courtName = itemView.findViewById(R.id.courtNameText);
            courtLocation = itemView.findViewById(R.id.courtLocationText);
            courtPrice = itemView.findViewById(R.id.courtPriceText);
            btnBook = itemView.findViewById(R.id.btnBook);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
} 