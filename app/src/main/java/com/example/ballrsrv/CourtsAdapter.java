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
    }

    @Override
    public int getItemCount() {
        return courtsList.size();
    }


            super(itemView);
            courtImage = itemView.findViewById(R.id.courtImage);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
} 