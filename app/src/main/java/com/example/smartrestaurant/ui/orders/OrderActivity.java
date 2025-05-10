package com.example.smartrestaurant.ui.orders;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartrestaurant.BaseActivity;
import com.example.smartrestaurant.R;
import com.example.smartrestaurant.models.Order;
import com.example.smartrestaurant.models.OrderItem;
import com.example.smartrestaurant.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class OrderActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private User currentUser;
    private FloatingActionButton fabAddOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

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
        getSupportActionBar().setTitle("Order Management");

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.order_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create dummy data for demo
        createDummyOrders();

        // Set up adapter
        orderAdapter = new OrderAdapter(orderList, currentUser, this);
        recyclerView.setAdapter(orderAdapter);

        // Set up add order button
        fabAddOrder = findViewById(R.id.fab_add_order);

        // Only show add button to waiters
        if (currentUser.getRole() == User.Role.WAITER) {
            fabAddOrder.setVisibility(View.VISIBLE);
            fabAddOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle creating a new order - would open a new activity/dialog
                    Toast.makeText(OrderActivity.this, "Create new order", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            fabAddOrder.setVisibility(View.GONE);
        }
    }

    private void createDummyOrders() {
        orderList = new ArrayList<>();

        // Create some menu items
        OrderItem item1 = new OrderItem("1", "Margherita Pizza", "Classic cheese and tomato pizza", 12.99, 2, "Extra cheese");
        OrderItem item2 = new OrderItem("2", "Grilled Chicken Salad", "Fresh salad with grilled chicken breast", 9.99, 1, "No onions");
        OrderItem item3 = new OrderItem("3", "Spaghetti Carbonara", "Creamy pasta with bacon", 14.99, 1, "");
        OrderItem item4 = new OrderItem("4", "Tiramisu", "Classic Italian dessert", 6.99, 2, "");

        // Create sample orders
        List<OrderItem> items1 = new ArrayList<>(Arrays.asList(item1, item2));
        Order order1 = new Order("1", items1, "waiter1", "Allergic to nuts");
        order1.setStatus(Order.Status.PREPARING);

        List<OrderItem> items2 = new ArrayList<>(Arrays.asList(item3, item4));
        Order order2 = new Order("2", items2, "waiter1", "");
        order2.setStatus(Order.Status.RECEIVED);

        List<OrderItem> items3 = new ArrayList<>(Arrays.asList(item1, item4));
        Order order3 = new Order("3", items3, "waiter2", "Birthday celebration");
        order3.setStatus(Order.Status.READY);

        // Add orders to list
        orderList.add(order1);
        orderList.add(order2);
        orderList.add(order3);
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