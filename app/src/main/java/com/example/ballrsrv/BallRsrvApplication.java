package com.example.ballrsrv;

import android.app.Application;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class BallRsrvApplication extends Application {
    private static final String TAG = "BallRsrvApplication";
    private static final String DATABASE_URL = "https://ballrsrv-a94eb-default-rtdb.asia-southeast1.firebasedatabase.app/";

    @Override
    public void onCreate() {
        super.onCreate();
        
        try {
            // Initialize Firebase
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this);
            }
            
            // Enable offline persistence
            FirebaseDatabase.getInstance(DATABASE_URL).setPersistenceEnabled(true);
            
            // Set database URL explicitly
            FirebaseDatabase.getInstance(DATABASE_URL).getReference().keepSynced(true);
            
            Log.d(TAG, "Firebase initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize Firebase: " + e.getMessage(), e);
        }
    }
} 