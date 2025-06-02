package com.example.ballrsrv;

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
import java.util.List;

public class CustomerCourtsAdapter extends RecyclerView.Adapter<CustomerCourtsAdapter.CourtViewHolder> {
    private List<Court> courts;
    private OnCourtClickListener listener;

    public interface OnCourtClickListener {
        void onCourtClick(Court court);
    }

    public CustomerCourtsAdapter(List<Court> courts, OnCourtClickListener listener) {
        this.courts = courts;
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
        Court court = courts.get(position);
        holder.bind(court);
    }

    @Override
    public int getItemCount() {
        return courts.size();
    }

    class CourtViewHolder extends RecyclerView.ViewHolder {
        private ImageView courtImage;
        private TextView courtName;
        private TextView courtDescription;
        private TextView courtPrice;
        private Button btnBook;
        private Button btnRemove;

        CourtViewHolder(@NonNull View itemView) {
            super(itemView);
            courtImage = itemView.findViewById(R.id.courtImage);
            courtName = itemView.findViewById(R.id.courtName);
            courtDescription = itemView.findViewById(R.id.courtDescription);
            courtPrice = itemView.findViewById(R.id.courtPrice);
            btnBook = itemView.findViewById(R.id.btnBook);
            btnRemove = itemView.findViewById(R.id.btnRemove);

            btnRemove.setVisibility(View.GONE);
            btnBook.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onCourtClick(courts.get(position));
                }
            });
        }

        void bind(Court court) {
            courtName.setText(court.getName());
            courtDescription.setText(court.getLocation());
            courtPrice.setText(String.format("$%.2f per hour", court.getPrice()));

            // Load base64 image
            if (court.getImageBase64() != null && !court.getImageBase64().isEmpty()) {
                try {
                    byte[] decodedString = Base64.decode(court.getImageBase64(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    courtImage.setImageBitmap(bitmap);
                } catch (Exception e) {
                    // If there's an error loading the image, set a default image or placeholder
                    courtImage.setImageResource(R.drawable.ic_court_placeholder);
                }
            } else {
                // Set default image if no image is available
                courtImage.setImageResource(R.drawable.ic_court_placeholder);
            }

            btnBook.setVisibility(court.isAvailable() ? View.VISIBLE : View.GONE);
        }
    }
} 