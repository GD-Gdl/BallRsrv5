package com.example.ballrsrv;

import android.app.Application;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class BallRsrvApplication extends Application {
    private static final String TAG = "BallRsrvApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        
        try {
            // Initialize Firebase
            FirebaseApp.initializeApp(this);
            
            // Enable offline persistence
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            
            // Set database URL explicitly
            FirebaseDatabase.getInstance().getReference().keepSynced(true);
            
            Log.d(TAG, "Firebase initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize Firebase: " + e.getMessage());
        }
    }
} 