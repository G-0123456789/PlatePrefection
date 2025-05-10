package com.example.smartrestaurant.ui.reports;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.smartrestaurant.BaseActivity;
import com.example.smartrestaurant.R;
import com.example.smartrestaurant.models.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class ReportsActivity extends BaseActivity {

    private User currentUser;
    private TextView reportTitle, reportDate, totalSalesText, totalOrdersText, avgOrderValueText;
    private CardView salesCard, ordersCard, inventoryCard, staffCard;
    private Spinner reportTypeSpinner, timePeriodSpinner;

    // Sample data for demonstration purposes
    private double[] monthlySales = {12450.75, 15230.50, 13675.25, 16890.00, 18250.75, 17560.50};
    private int[] monthlyOrders = {425, 512, 476, 543, 598, 567};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

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
            Toast.makeText(this, "Access denied: Only managers can access reports", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up toolbar
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Reports & Analytics");

        // Initialize UI elements
        reportTitle = findViewById(R.id.text_report_title);
        reportDate = findViewById(R.id.text_report_date);
        totalSalesText = findViewById(R.id.text_total_sales);
        totalOrdersText = findViewById(R.id.text_total_orders);
        avgOrderValueText = findViewById(R.id.text_avg_order);

        salesCard = findViewById(R.id.card_sales);
        ordersCard = findViewById(R.id.card_orders);
        inventoryCard = findViewById(R.id.card_inventory);
        staffCard = findViewById(R.id.card_staff);

        reportTypeSpinner = findViewById(R.id.spinner_report_type);
        timePeriodSpinner = findViewById(R.id.spinner_time_period);

        // Set up spinners
        setupSpinners();

        // Set current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        reportDate.setText("Generated: " + dateFormat.format(new Date()));

        // Load default report
        loadReportData("Sales Report", "Monthly");
    }

    private void setupSpinners() {
        // Report type spinner
        ArrayAdapter<CharSequence> reportTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.report_types, android.R.layout.simple_spinner_item);
        reportTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reportTypeSpinner.setAdapter(reportTypeAdapter);

        // Time period spinner
        ArrayAdapter<CharSequence> timePeriodAdapter = ArrayAdapter.createFromResource(this,
                R.array.time_periods, android.R.layout.simple_spinner_item);
        timePeriodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timePeriodSpinner.setAdapter(timePeriodAdapter);

        // Listeners
        reportTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String reportType = parent.getItemAtPosition(position).toString();
                String timePeriod = timePeriodSpinner.getSelectedItem().toString();
                loadReportData(reportType, timePeriod);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        timePeriodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String reportType = reportTypeSpinner.getSelectedItem().toString();
                String timePeriod = parent.getItemAtPosition(position).toString();
                loadReportData(reportType, timePeriod);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadReportData(String reportType, String timePeriod) {
        reportTitle.setText(reportType + " (" + timePeriod + ")");

        // Show/hide relevant cards based on report type
        switch (reportType) {
            case "Sales Report":
                salesCard.setVisibility(View.VISIBLE);
                ordersCard.setVisibility(View.VISIBLE);
                inventoryCard.setVisibility(View.GONE);
                staffCard.setVisibility(View.GONE);
                loadSalesData(timePeriod);
                break;
            case "Inventory Report":
                salesCard.setVisibility(View.GONE);
                ordersCard.setVisibility(View.GONE);
                inventoryCard.setVisibility(View.VISIBLE);
                staffCard.setVisibility(View.GONE);
                loadInventoryData(timePeriod);
                break;
            case "Staff Performance":
                salesCard.setVisibility(View.GONE);
                ordersCard.setVisibility(View.VISIBLE);
                inventoryCard.setVisibility(View.GONE);
                staffCard.setVisibility(View.VISIBLE);
                loadStaffData(timePeriod);
                break;
            default:
                // Show all by default
                salesCard.setVisibility(View.VISIBLE);
                ordersCard.setVisibility(View.VISIBLE);
                inventoryCard.setVisibility(View.VISIBLE);
                staffCard.setVisibility(View.VISIBLE);
                loadSalesData(timePeriod);
                break;
        }
    }

    private void loadSalesData(String timePeriod) {
        // Generate sample data based on time period
        double totalSales;
        int totalOrders;

        switch (timePeriod) {
            case "Daily":
                totalSales = monthlySales[5] / 30.0;
                totalOrders = monthlyOrders[5] / 30;
                break;
            case "Weekly":
                totalSales = monthlySales[5] / 4.0;
                totalOrders = monthlyOrders[5] / 4;
                break;
            case "Yearly":
                totalSales = 0;
                totalOrders = 0;
                for (int i = 0; i < monthlySales.length; i++) {
                    totalSales += monthlySales[i];
                    totalOrders += monthlyOrders[i];
                }
                break;
            default:
                // Monthly
                totalSales = monthlySales[5];
                totalOrders = monthlyOrders[5];
                break;
        }

        double avgOrderValue = totalSales / totalOrders;

        totalSalesText.setText(String.format("R%.2f", totalSales));
        totalOrdersText.setText(String.valueOf(totalOrders));
        avgOrderValueText.setText(String.format("R%.2f", avgOrderValue));
    }

    private void loadInventoryData(String timePeriod) {
        // This would load inventory-specific data in a real app
        Toast.makeText(this, "Loading inventory data for " + timePeriod, Toast.LENGTH_SHORT).show();
    }

    private void loadStaffData(String timePeriod) {
        // This would load staff performance data in a real app
        Toast.makeText(this, "Loading staff performance data for " + timePeriod, Toast.LENGTH_SHORT).show();
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