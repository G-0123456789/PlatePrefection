package com.example.smartrestaurant.ui.usermanagement;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartrestaurant.BaseActivity;
import com.example.smartrestaurant.R;
import com.example.smartrestaurant.helpers.DatabaseHelper;
import com.example.smartrestaurant.helpers.DialogHelper;
import com.example.smartrestaurant.models.User;
import com.example.smartrestaurant.ui.usermanagement.UserAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserManagementActivity extends BaseActivity {
    private static final String TAG = "UserManagementActivity";

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private User currentUser;
    private FloatingActionButton fabAddUser;
    private ProgressBar progressBar;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        // Get current user from intent
        if (getIntent().hasExtra("USER")) {
            currentUser = (User) getIntent().getSerializableExtra("USER");
        } else {
            // Handle error - user should be provided
            Toast.makeText(this, "Error: User information not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Only managers should access this screen
        if (currentUser.getRole() != User.Role.MANAGER) {
            Toast.makeText(this, "Access denied: Only managers can access user management", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up toolbar
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("User Management");

        // Initialize DatabaseHelper
        databaseHelper = DatabaseHelper.getInstance();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.user_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Find progress bar
        progressBar = findViewById(R.id.progress_bar);

        // Initialize the list and adapter
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, this);
        recyclerView.setAdapter(userAdapter);

        // Load users from Firebase
        loadUsersFromDatabase();

        // Set up add user button
        fabAddUser = findViewById(R.id.fab_add_user);
        fabAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddUserDialog();
            }
        });
    }

    private void loadUsersFromDatabase() {
        progressBar.setVisibility(View.VISIBLE);

        databaseHelper.getAllUsers(new DatabaseHelper.DatabaseCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> result) {
                progressBar.setVisibility(View.GONE);

                if (result != null && !result.isEmpty()) {
                    userList.clear();
                    userList.addAll(result);
                    userAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Loaded " + result.size() + " users from database");
                } else {
                    Log.d(TAG, "No users found in database, loading default data");
                    // No users found, create dummy data
                    createDummyUsers();
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(UserManagementActivity.this,
                        "Failed to load users: " + error, Toast.LENGTH_SHORT).show();

                // Create dummy data as fallback
                createDummyUsers();
                userAdapter.notifyDataSetChanged();

                Log.e(TAG, "Error loading users: " + error);
            }
        });
    }

    private void showAddUserDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_user, null);

        final TextInputEditText nameInput = dialogView.findViewById(R.id.input_user_name);
        final TextInputEditText usernameInput = dialogView.findViewById(R.id.input_username);
        final TextInputEditText emailInput = dialogView.findViewById(R.id.input_email);
        final Spinner roleSpinner = dialogView.findViewById(R.id.spinner_role);

        // Set up spinner with user roles
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                java.util.Arrays.stream(User.Role.values()).map(Enum::name).toArray(String[]::new));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        DialogHelper.getStyledDialog(this)
                .setTitle("Add New User")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    String username = usernameInput.getText().toString().trim();
                    String email = emailInput.getText().toString().trim();
                    String selectedRole = roleSpinner.getSelectedItem().toString();

                    if (name.isEmpty() || username.isEmpty() || email.isEmpty()) {
                        Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Create new user
                    final User newUser = new User(
                            UUID.randomUUID().toString(),
                            username,
                            name,
                            email,
                            User.Role.valueOf(selectedRole)
                    );

                    // Add to local list first for immediate feedback
                    userList.add(0, newUser);
                    userAdapter.notifyItemInserted(0);
                    recyclerView.smoothScrollToPosition(0);

                    // Save to database
                    databaseHelper.saveUser(newUser, new DatabaseHelper.DatabaseCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            Log.d(TAG, "User " + newUser.getUsername() + " saved to database");
                            Toast.makeText(UserManagementActivity.this,
                                    "User added successfully", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "Error saving user: " + error);
                            Toast.makeText(UserManagementActivity.this,
                                    "User added locally but not synced", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void createDummyUsers() {
        userList.clear();

        // Add sample users
        User manager = new User("1", "john_manager", "John Manager", "john@example.com", User.Role.MANAGER);
        User chef1 = new User("2", "sarah_chef", "Sarah Chef", "sarah@example.com", User.Role.CHEF);
        User waiter1 = new User("3", "mike_waiter", "Mike Waiter", "mike@example.com", User.Role.WAITER);
        User waiter2 = new User("4", "lisa_waiter", "Lisa Waiter", "lisa@example.com", User.Role.WAITER);
        User chef2 = new User("5", "alex_chef", "Alex Chef", "alex@example.com", User.Role.CHEF);
        User customer = new User("6", "customer1", "Jane Customer", "jane@example.com", User.Role.CUSTOMER);

        userList.add(manager);
        userList.add(chef1);
        userList.add(waiter1);
        userList.add(waiter2);
        userList.add(chef2);
        userList.add(customer);

        // Save these users to database
        for (User user : userList) {
            databaseHelper.saveUser(user, new DatabaseHelper.DatabaseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    Log.d(TAG, "Dummy user " + user.getUsername() + " saved to database");
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "Error saving dummy user " + user.getUsername() + ": " + error);
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}