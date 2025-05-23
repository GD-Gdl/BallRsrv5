package com.example.ballrsrv;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
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
        holder.bind(court);
    }

    @Override
    public int getItemCount() {
        return courtsList.size();
    }

    class CourtViewHolder extends RecyclerView.ViewHolder {
        private ImageView courtImage;
        private TextView courtNameText;
        private TextView courtLocationText;
        private TextView courtPriceText;
        private Button btnRemove;

        CourtViewHolder(@NonNull View itemView) {
            super(itemView);
            courtImage = itemView.findViewById(R.id.courtImage);
            courtNameText = itemView.findViewById(R.id.courtNameText);
            courtLocationText = itemView.findViewById(R.id.courtLocationText);
            courtPriceText = itemView.findViewById(R.id.courtPriceText);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }

        void bind(Court court) {
            courtNameText.setText(court.getName());
            courtLocationText.setText(court.getLocation());
            courtPriceText.setText(String.format(Locale.getDefault(), "₱%.2f per hour", court.getPrice()));
            
            // Load court image using Glide
            if (court.getImageUrl() != null && !court.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                    .load(court.getImageUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(courtImage);
            } else {
                // Set a placeholder image if no image URL is available
                courtImage.setImageResource(R.drawable.placeholder_court);
            }

            btnRemove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveCourt(court);
                }
            });
        }
    }
} 