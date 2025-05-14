package com.example.ballrsrv;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookingRequestAdapter extends RecyclerView.Adapter<BookingRequestAdapter.ViewHolder> {
    private List<BookingRequest> requests;
    private OnRequestActionListener listener;

    public BookingRequestAdapter(List<BookingRequest> requests, OnRequestActionListener listener) {
        this.requests = requests;
        this.listener = listener;
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
        holder.userName.setText(request.getUserName());
        holder.details.setText(request.getBookingDetails());
        holder.acceptButton.setOnClickListener(v -> listener.onAccept(request));
        holder.denyButton.setOnClickListener(v -> listener.onDeny(request));
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userName;
        public TextView details;
        public Button acceptButton;
        public Button denyButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.textUserName);
            details = itemView.findViewById(R.id.textBookingDetails);
            acceptButton = itemView.findViewById(R.id.btnAccept);
            denyButton = itemView.findViewById(R.id.btnDeny);
        }
    }
}
