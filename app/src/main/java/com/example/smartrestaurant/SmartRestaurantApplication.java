package com.example.smartrestaurant;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.example.smartrestaurant.models.User;

public class SmartRestaurantApplication extends Application {
    private static final String TAG = "SmartRestApp";
    private static FirebaseDatabase firebaseDatabase;
    private static boolean isSessionActive = false;
    private static User currentUser = null;

    // Application-level flag for detecting app restart
    private static boolean wasAppClosed = true;

    // Getter for database access throughout the app
    public static FirebaseDatabase getDatabase() {
        return firebaseDatabase;
    }

    public static boolean isSessionActive() {
        return isSessionActive;
    }

    public static void setSessionActive(boolean active) {
        isSessionActive = active;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
        isSessionActive = (user != null);
    }

    public static boolean wasAppClosed() {
        return wasAppClosed;
    }

    public static void setAppClosed(boolean closed) {
        wasAppClosed = closed;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Firebase for database only
        try {
            FirebaseApp.initializeApp(this);
            firebaseDatabase = FirebaseDatabase.getInstance();

            // Enable offline capabilities - this is causing crashes, so commented out for now
            // This should only be called once and before any Firebase database usage
            // firebaseDatabase.setPersistenceEnabled(true);

            Log.i(TAG, "Firebase Database initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Firebase initialization failed: " + e.getMessage());
            // Using Toast in Application class might cause issues if called before UI is ready
            // Use a handler to delay the toast until UI is more likely ready
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Toast.makeText(this, "Warning: Database synchronization unavailable", Toast.LENGTH_LONG).show();
            }, 3000);
            // Create a dummy database instance for offline operation
            firebaseDatabase = null;
        }
    }
}