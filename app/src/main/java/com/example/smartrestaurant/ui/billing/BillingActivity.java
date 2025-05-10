package com.example.smartrestaurant.ui.billing;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartrestaurant.R;
import com.example.smartrestaurant.BaseActivity;
import com.example.smartrestaurant.models.Bill;
import com.example.smartrestaurant.models.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class BillingActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private BillingAdapter billingAdapter;
    private List<Bill> billList;
    private User currentUser;
    private FloatingActionButton fabAddBill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        // Get current user from intent
        if (getIntent().hasExtra("USER")) {
            currentUser = (User) getIntent().getSerializableExtra("USER");
        } else {
            // Handle error - user should be provided
            Toast.makeText(this, "Error: User information not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up toolbar
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Billing Management");

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.bill_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create dummy data for demo
        createDummyBills();

        // Set up adapter
        billingAdapter = new BillingAdapter(billList, this, currentUser);
        recyclerView.setAdapter(billingAdapter);

        // Set up add bill button
        fabAddBill = findViewById(R.id.fab_add_bill);

        // Only managers and waiters can add bills
        if (currentUser.getRole() == User.Role.MANAGER || currentUser.getRole() == User.Role.WAITER) {
            fabAddBill.setVisibility(View.VISIBLE);
            fabAddBill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // In a real app, this would open a screen to select an order to bill
                    Toast.makeText(BillingActivity.this, "In a real app, this would allow you to select an order to generate a bill", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            fabAddBill.setVisibility(View.GONE);
        }
    }

    private void createDummyBills() {
        billList = new ArrayList<>();

        // Add sample bills
        Bill bill1 = new Bill("order_1", "Table 5", 45.75, 3.66, 9.15,
                "waiter_1", "John Smith", "");
        bill1.setStatus(Bill.Status.PAID);
        bill1.setPaymentMethod(Bill.PaymentMethod.CREDIT_CARD);

        Bill bill2 = new Bill("order_2", "Table 12", 78.50, 6.28, 15.70,
                "waiter_2", "Emily Johnson", "Birthday celebration");
        bill2.setStatus(Bill.Status.PENDING);

        Bill bill3 = new Bill("order_3", "Table 8", 32.45, 2.60, 0.00,
                "waiter_1", "Michael Brown", "");
        bill3.setStatus(Bill.Status.PENDING);

        Bill bill4 = new Bill("order_4", "Table 3", 66.80, 5.34, 13.36,
                "waiter_3", "Sarah Wilson", "");
        bill4.setStatus(Bill.Status.PAID);
        bill4.setPaymentMethod(Bill.PaymentMethod.CASH);

        billList.add(bill1);
        billList.add(bill2);
        billList.add(bill3);
        billList.add(bill4);
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