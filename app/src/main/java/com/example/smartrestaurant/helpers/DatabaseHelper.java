package com.example.smartrestaurant.helpers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.smartrestaurant.SmartRestaurantApplication;
import com.example.smartrestaurant.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for Firebase Realtime Database operations
 */
public class DatabaseHelper {
    private static final String TAG = "DatabaseHelper";

    // Database references
    private final FirebaseDatabase database;
    private final DatabaseReference usersRef;

    // Singleton instance
    private static DatabaseHelper instance;

    // Public interface for callbacks
    public interface DatabaseCallback<T> {
        void onSuccess(T result);

        void onError(String error);
    }

    private DatabaseHelper() {
        database = SmartRestaurantApplication.getDatabase();

        // If database is null (initialization failed), create a dummy instance
        // This will allow the app to function without crashing, but data won't sync
        if (database == null) {
            Log.w(TAG, "Database is null, using dummy database reference");
            usersRef = null;
        } else {
            usersRef = database.getReference("users");
        }
    }

    public static synchronized DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper();
        }
        return instance;
    }

    /**
     * Save user to Firebase
     */
    public void saveUser(User user, final DatabaseCallback<Boolean> callback) {
        if (usersRef == null) {
            // Database unavailable, assume success with local only
            Log.d(TAG, "Database unavailable, skipping remote user save");
            callback.onSuccess(false); // false = not synced with remote
            return;
        }

        usersRef.child(user.getUserId()).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            callback.onSuccess(true); // true = synced with remote
                        } else {
                            callback.onError(task.getException() != null ?
                                    task.getException().getMessage() : "Failed to save user");
                        }
                    }
                });
    }

    /**
     * Get user from Firebase
     */
    public void getUser(String userId, final DatabaseCallback<User> callback) {
        if (usersRef == null) {
            // Database unavailable
            Log.d(TAG, "Database unavailable, cannot retrieve user from remote");
            callback.onError("Database unavailable");
            return;
        }

        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        callback.onSuccess(user);
                    } else {
                        callback.onError("Failed to parse user data");
                    }
                } else {
                    callback.onError("User not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    /**
     * Get all users from Firebase
     */
    public void getAllUsers(final DatabaseCallback<List<User>> callback) {
        if (usersRef == null) {
            // Database unavailable
            Log.d(TAG, "Database unavailable, cannot retrieve users from remote");
            callback.onError("Database unavailable");
            return;
        }

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> users = new ArrayList<>();

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null) {
                        users.add(user);
                    }
                }

                callback.onSuccess(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    /**
     * Delete user from Firebase
     */
    public void deleteUser(String userId, final DatabaseCallback<Boolean> callback) {
        if (usersRef == null) {
            // Database unavailable
            Log.d(TAG, "Database unavailable, cannot delete user from remote");
            callback.onError("Database unavailable");
            return;
        }

        usersRef.child(userId).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            callback.onSuccess(true);
                        } else {
                            callback.onError(task.getException() != null ?
                                    task.getException().getMessage() : "Failed to delete user");
                        }
                    }
                });
    }
}