package com.example.smartrestaurant.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartrestaurant.BaseActivity;
import com.example.smartrestaurant.MainActivity;
import com.example.smartrestaurant.R;
import com.example.smartrestaurant.helpers.DatabaseHelper;
import com.example.smartrestaurant.models.User;
import com.example.smartrestaurant.SmartRestaurantApplication;

public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView signupLink;
    private SharedPreferences sharedPreferences;
    private boolean isLoading = false;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Clear any previous session
        SmartRestaurantApplication.setCurrentUser(null);
        SmartRestaurantApplication.setSessionActive(false);

        // Initialize UI elements
        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_button);
        signupLink = findViewById(R.id.signup_link);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("SmartRestaurantPrefs", MODE_PRIVATE);

        // Initialize database helper
        databaseHelper = DatabaseHelper.getInstance();

        // Check if we should redirect to signup
        if (getIntent().getBooleanExtra("SHOW_SIGNUP", false)) {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLoading) {
                    localLogin();
                }
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to SignupActivity
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user was previously logged in via local storage
        String lastUsername = sharedPreferences.getString("last_username", null);
        if (lastUsername != null) {
            String roleName = sharedPreferences.getString(lastUsername + "_role", "CUSTOMER");
            try {
                User.Role role = User.Role.valueOf(roleName);
                createAndLoginLocalUser(lastUsername, role);
            } catch (Exception e) {
                // Clear preferences if corrupted
                sharedPreferences.edit().clear().apply();
            }
        }
    }

    private void localLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Basic validation
        if (username.isEmpty()) {
            usernameEditText.setError("Username is required");
            usernameEditText.requestFocus();
            return;
        }

        // For demo purposes, accept any password or specifically require "demo" password
        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }

        isLoading = true;
        loginButton.setText("Logging in...");

        // Determine user role based on username or stored preferences
        User.Role role;
        String storedRoleStr = sharedPreferences.getString(username + "_role", null);

        if (storedRoleStr != null) {
            try {
                role = User.Role.valueOf(storedRoleStr);
            } catch (Exception e) {
                role = determineRoleFromUsername(username);
            }
        } else {
            role = determineRoleFromUsername(username);
        }

        // Store username and role for future logins
        sharedPreferences.edit()
                .putString("last_username", username)
                .putString(username + "_role", role.name())
                .apply();

        // Create user and navigate
        createAndLoginLocalUser(username, role);
    }

    private User.Role determineRoleFromUsername(String username) {
        username = username.toLowerCase();
        if (username.contains("manager")) {
            return User.Role.MANAGER;
        } else if (username.contains("waiter")) {
            return User.Role.WAITER;
        } else if (username.contains("chef")) {
            return User.Role.CHEF;
        } else {
            return User.Role.CUSTOMER;
        }
    }

    private void createAndLoginLocalUser(String username, User.Role role) {
        // Create a local user object without relying on Firebase Auth
        String userId = "local-" + username + "-" + System.currentTimeMillis();
        String email = username + "@example.com";
        String name = username;

        final User newUser = new User(userId, username, name, email, role);

        // Reset login button status
        isLoading = false;
        loginButton.setText("Login");

        // Try to save to Firebase Realtime Database (but don't wait for completion)
        databaseHelper.saveUser(newUser, new DatabaseHelper.DatabaseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean synced) {
                Log.d(TAG, "User saved to database: " + (synced ? "synced" : "local only"));
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error saving user to database: " + error);
                // Continue anyway with local user
            }
        });

        // Navigate to main activity with the local user
        navigateToMainActivity(newUser);
    }

    private void navigateToMainActivity(User user) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("USER", user);

        // Mark app as active and store user
        SmartRestaurantApplication.setAppClosed(false);
        SmartRestaurantApplication.setCurrentUser(user);

        startActivity(intent);
        finish(); // Close the login activity
    }

    // Fallback method when Firebase is not working
    private void useOfflineDemoMode() {
        String username = usernameEditText.getText().toString().trim();
        if (username.isEmpty()) {
            username = "guest";
        }

        User.Role role = User.Role.CUSTOMER;

        // Assign role based on username
        if (username.contains("manager")) {
            role = User.Role.MANAGER;
        } else if (username.contains("waiter")) {
            role = User.Role.WAITER;
        } else if (username.contains("chef")) {
            role = User.Role.CHEF;
        }

        // Create demo user directly
        User demoUser = new User("demo-" + System.currentTimeMillis(),
                username,
                username,
                username + "@demo.com",
                role);

        Toast.makeText(this, "Demo mode activated for " + role.name(), Toast.LENGTH_SHORT).show();
        navigateToMainActivity(demoUser);
    }
}