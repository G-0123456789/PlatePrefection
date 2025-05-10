package com.example.smartrestaurant.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartrestaurant.BaseActivity;
import com.example.smartrestaurant.MainActivity;
import com.example.smartrestaurant.R;
import com.example.smartrestaurant.helpers.DatabaseHelper;
import com.example.smartrestaurant.models.User;
import com.example.smartrestaurant.SmartRestaurantApplication;

public class SignupActivity extends BaseActivity {
    private static final String TAG = "SignupActivity";

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private RadioGroup roleRadioGroup;
    private RadioButton customerRadio;
    private RadioButton employeeRadio;
    private Button signupButton;
    private TextView loginLink;
    private SharedPreferences sharedPreferences;
    private DatabaseHelper databaseHelper;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize UI components
        usernameEditText = findViewById(R.id.username_edittext);
        emailEditText = findViewById(R.id.email_edittext);
        passwordEditText = findViewById(R.id.password_edittext);
        confirmPasswordEditText = findViewById(R.id.confirmpass_edittext);
        roleRadioGroup = findViewById(R.id.roleRadioGroup);
        customerRadio = findViewById(R.id.customerRadio);
        employeeRadio = findViewById(R.id.employeeRadio);
        signupButton = findViewById(R.id.signup_button);
        loginLink = findViewById(R.id.login_link);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("SmartRestaurantPrefs", MODE_PRIVATE);

        // Initialize DatabaseHelper
        databaseHelper = DatabaseHelper.getInstance();

        // Setup button click listeners
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLoading) {
                    registerUser();
                }
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to login screen
                finish();
            }
        });
    }

    private void registerUser() {
        // Get user input
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Validate input
        if (!validateInput(username, email, password, confirmPassword)) {
            return;
        }

        // Show loading state
        isLoading = true;
        signupButton.setText("Creating account...");

        // Determine user role
        User.Role role = customerRadio.isChecked() ? User.Role.CUSTOMER : determineEmployeeRole(username);

        // Create user ID
        String userId = "local-" + username + "-" + System.currentTimeMillis();

        // Create user object
        final User newUser = new User(userId, username, username, email, role);

        // Store username and role for future logins
        sharedPreferences.edit()
                .putString("last_username", username)
                .putString(username + "_role", role.name())
                .apply();

        // Try to save to Firebase Realtime Database
        databaseHelper.saveUser(newUser, new DatabaseHelper.DatabaseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean synced) {
                Log.d(TAG, "User saved to database: " + (synced ? "synced" : "local only"));

                // Reset loading state
                isLoading = false;
                signupButton.setText("Sign Up");

                // Navigate to main activity
                navigateToMainActivity(newUser);
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error saving user to database: " + error);

                // Reset loading state
                isLoading = false;
                signupButton.setText("Sign Up");

                // Continue with local user regardless of database error
                Toast.makeText(SignupActivity.this, "Account created in offline mode", Toast.LENGTH_SHORT).show();
                navigateToMainActivity(newUser);
            }
        });
    }

    private boolean validateInput(String username, String email, String password, String confirmPassword) {
        // Check username
        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("Username is required");
            usernameEditText.requestFocus();
            return false;
        }

        // Check email
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email");
            emailEditText.requestFocus();
            return false;
        }

        // Check password
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            passwordEditText.requestFocus();
            return false;
        }

        // Check password confirmation
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords don't match");
            confirmPasswordEditText.requestFocus();
            return false;
        }

        return true;
    }

    private User.Role determineEmployeeRole(String username) {
        username = username.toLowerCase();
        if (username.contains("manager")) {
            return User.Role.MANAGER;
        } else if (username.contains("chef")) {
            return User.Role.CHEF;
        } else {
            return User.Role.WAITER; // Default employee role
        }
    }

    private void navigateToMainActivity(User user) {
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        intent.putExtra("USER", user);

        // Mark app as active and store user
        SmartRestaurantApplication.setAppClosed(false);
        SmartRestaurantApplication.setCurrentUser(user);

        startActivity(intent);
        finish(); // Close the signup activity
    }
}