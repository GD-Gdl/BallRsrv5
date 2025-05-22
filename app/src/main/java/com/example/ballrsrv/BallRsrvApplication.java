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
            Log.d(TAG, "Starting application initialization");
            
            // Initialize Firebase
            if (FirebaseApp.getApps(this).isEmpty()) {
                Log.d(TAG, "Initializing Firebase app");
                FirebaseApp.initializeApp(this);
            } else {
                Log.d(TAG, "Firebase app already initialized");
            }
            
            // Configure Firebase Database
            Log.d(TAG, "Configuring Firebase Database");
            FirebaseDatabase database = FirebaseDatabase.getInstance(DATABASE_URL);
            
            // Enable offline persistence
            Log.d(TAG, "Enabling offline persistence");
            database.setPersistenceEnabled(true);
            
            // Enable offline capabilities
            Log.d(TAG, "Enabling offline capabilities");
            database.getReference().keepSynced(true);
            
            Log.d(TAG, "Firebase initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize Firebase: " + e.getMessage(), e);
            e.printStackTrace();
        }

        try {
            // Enable Firebase offline persistence before any Firebase instances are created
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            Log.d(TAG, "Firebase persistence enabled successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error enabling Firebase persistence: " + e.getMessage(), e);
        }
    }
} 