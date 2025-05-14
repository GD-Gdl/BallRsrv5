package com.example.ballrsrv;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class RequestsActivity extends AppCompatActivity implements BookingRequestAdapter.OnRequestActionListener {
    private RecyclerView recyclerView;
    private BookingRequestAdapter adapter;
    private List<BookingRequest> requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        recyclerView = findViewById(R.id.recyclerView);
        requests = fetchRequests();
        adapter = new BookingRequestAdapter(requests, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private List<BookingRequest> fetchRequests() {
        // Mock data for demonstration
        List<BookingRequest> list = new ArrayList<>();
        list.add(new BookingRequest("1", "KYLE SIBAYAN", "11:00 AM - 12:00 PM", "pending"));
        list.add(new BookingRequest("2", "JANE DOE", "12:00 PM - 1:00 PM", "pending"));
        return list;
    }

    @Override
    public void onAccept(BookingRequest request) {
        request.setStatus("accepted");
        // Update backend/database here
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDeny(BookingRequest request) {
        request.setStatus("denied");
        // Update backend/database here
        adapter.notifyDataSetChanged();
    }
}

