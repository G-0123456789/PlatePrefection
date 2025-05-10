package com.example.smartrestaurant.ui.reservations;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartrestaurant.BaseActivity;
import com.example.smartrestaurant.R;
import com.example.smartrestaurant.models.Reservation;
import com.example.smartrestaurant.models.User;
import com.example.smartrestaurant.helpers.DialogHelper;
import com.example.smartrestaurant.ui.auth.LoginActivity;
import com.example.smartrestaurant.ui.auth.SignupActivity;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ReservationActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private ReservationAdapter reservationAdapter;
    private List<Reservation> reservationList;
    private User currentUser;
    private FloatingActionButton fabAddReservation;
    private boolean isViewOnly = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        // Get current user from intent
        if (getIntent().hasExtra("USER")) {
            currentUser = (User) getIntent().getSerializableExtra("USER");
        } else {
            // Handle error - user should be provided
            Toast.makeText(this, "Error: User information not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Check if view-only mode
        if (getIntent().hasExtra("VIEW_ONLY")) {
            isViewOnly = getIntent().getBooleanExtra("VIEW_ONLY", false);
        }

        // Set up toolbar
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.reservation_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create dummy data for demo
        createDummyReservations();

        // Filter reservations for customers and guests - they should only see their own
        if (currentUser.getRole() == User.Role.CUSTOMER || currentUser.getUserId().startsWith("guest")) {
            filterReservationsForCustomer();
        }

        // Set up adapter
        reservationAdapter = new ReservationAdapter(reservationList, currentUser, this);
        recyclerView.setAdapter(reservationAdapter);

        // Set up add reservation button
        fabAddReservation = findViewById(R.id.fab_add_reservation);

        // Show FAB only if not in view-only mode
        if (isViewOnly || currentUser.getRole() == User.Role.WAITER) {
            fabAddReservation.setVisibility(View.GONE);
            getSupportActionBar().setTitle("View Reservations");
        } else {
            fabAddReservation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Check if user is a guest - if so, prompt to login
                    if (currentUser.getUserId().startsWith("guest")) {
                        showLoginRequiredDialog();
                    } else {
                        showDatePicker();
                    }
                }
            });
            getSupportActionBar().setTitle("Manage Reservations");
        }
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Reservation Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);
            calendar.set(Calendar.HOUR_OF_DAY, 19); // Default time at 7 PM
            showReservationDialog(calendar.getTime());
        });

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private void showReservationDialog(Date selectedDate) {
        // In a real app, this would show a more detailed dialog to select time, party size, etc.
        String formattedDate = new SimpleDateFormat("MMMM dd, yyyy", Locale.US).format(selectedDate);

        DialogHelper.getStyledDialog(this)
                .setTitle("Reserve a Table")
                .setMessage("Would you like to reserve a table for " + formattedDate + "?")
                .setPositiveButton("Reserve", (dialog, which) -> {
                    // Create a new reservation and add to list
                    Reservation newReservation = new Reservation(
                            currentUser.getUserId(),
                            currentUser.getName(),
                            "555-1234", // Would get from form in real app
                            "12", // Table number would be assigned by system
                            4, // Party size would come from form
                            selectedDate,
                            "No special requests" // Would come from form
                    );

                    reservationList.add(0, newReservation);
                    reservationAdapter.notifyItemInserted(0);
                    recyclerView.smoothScrollToPosition(0);

                    Toast.makeText(ReservationActivity.this, "Reservation created", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void createDummyReservations() {
        reservationList = new ArrayList<>();

        // Create dates for sample reservations
        Calendar calendar = Calendar.getInstance();

        // Today at 7 PM
        calendar.set(Calendar.HOUR_OF_DAY, 19);
        calendar.set(Calendar.MINUTE, 0);
        Date today = calendar.getTime();

        // Tomorrow at 8 PM
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        Date tomorrow = calendar.getTime();

        // Day after tomorrow at 6:30 PM
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 30);
        Date dayAfterTomorrow = calendar.getTime();

        // Create sample reservations
        Reservation reservation1 = new Reservation(
                "customer1", "John Smith", "555-1111", "8", 2, today, "Window seat preferred");
        reservation1.setStatus(Reservation.Status.CONFIRMED);

        Reservation reservation2 = new Reservation(
                "customer2", "Emma Johnson", "555-2222", "15", 4, tomorrow, "");
        reservation2.setStatus(Reservation.Status.PENDING);

        Reservation reservation3 = new Reservation(
                "customer3", "Michael Brown", "555-3333", "6", 6, dayAfterTomorrow, "Birthday celebration");
        reservation3.setStatus(Reservation.Status.CONFIRMED);

        // Add reservations to list
        reservationList.add(reservation1);
        reservationList.add(reservation2);
        reservationList.add(reservation3);
    }

    private void showLoginRequiredDialog() {
        DialogHelper.getConfirmationDialog(
                this,
                "Login Required",
                "You need to log in or create an account to make a reservation.",
                "Login",
                (dialog, which) -> {
                    // Navigate to login
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                },
                "Sign Up",
                (dialog, which) -> {
                    // Navigate to signup
                    Intent intent = new Intent(this, SignupActivity.class);
                    startActivity(intent);
                }
        ).show();
    }

    private void filterReservationsForCustomer() {
        if (currentUser != null) {
            List<Reservation> filteredList = new ArrayList<>();

            for (Reservation reservation : reservationList) {
                if (reservation.getCustomerId().equals(currentUser.getUserId())) {
                    filteredList.add(reservation);
                }
            }

            // Replace the list with filtered results
            reservationList.clear();
            reservationList.addAll(filteredList);
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