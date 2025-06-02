package com.example.ballrsrv;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class CustomerCourtsAdapter extends RecyclerView.Adapter<CustomerCourtsAdapter.CourtViewHolder> {

    private List<Court> courtsList;
    private OnBookCourtListener listener;

    public interface OnBookCourtListener {
        void onBookCourt(Court court);
    }

    public CustomerCourtsAdapter(List<Court> courtsList, OnBookCourtListener listener) {
        this.courtsList = courtsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_court, parent, false);
        return new CourtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourtViewHolder holder, int position) {
        Court court = courtsList.get(position);
        holder.courtName.setText(court.getName());
        holder.courtLocation.setText(court.getLocation());
        holder.courtPrice.setText(String.format(Locale.getDefault(), "₱%.2f per hour", court.getPrice()));

        if (court.getImageUrl() != null && !court.getImageUrl().isEmpty()) {
        } else {
        }

        holder.btnBook.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookCourt(court);
            }
        });

        // Hide the remove button for the customer view
        holder.btnRemove.setVisibility(View.GONE);
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