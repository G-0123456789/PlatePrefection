package com.example.smartrestaurant.ui.inventory;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartrestaurant.BaseActivity;
import com.example.smartrestaurant.R;
import com.example.smartrestaurant.models.InventoryItem;
import com.example.smartrestaurant.models.User;
import com.example.smartrestaurant.helpers.DialogHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InventoryActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private InventoryAdapter inventoryAdapter;
    private List<InventoryItem> inventoryItems;
    private User currentUser;
    private FloatingActionButton fabAddInventoryItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

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
        getSupportActionBar().setTitle("Inventory Management");

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.inventory_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create dummy data for demo
        createDummyInventoryItems();

        // Set up adapter
        inventoryAdapter = new InventoryAdapter(inventoryItems, currentUser, this);
        recyclerView.setAdapter(inventoryAdapter);

        // Set up add inventory item button
        fabAddInventoryItem = findViewById(R.id.fab_add_inventory);

        // Only managers can add inventory items
        if (currentUser.getRole() == User.Role.MANAGER || currentUser.getRole() == User.Role.CHEF) {
            fabAddInventoryItem.setVisibility(View.VISIBLE);
            fabAddInventoryItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddItemDialog();
                }
            });
        } else {
            fabAddInventoryItem.setVisibility(View.GONE);
        }
    }

    private void showAddItemDialog() {
        // Create dialog view
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_inventory, null);
        final TextInputEditText nameInput = dialogView.findViewById(R.id.input_item_name);
        final TextInputEditText quantityInput = dialogView.findViewById(R.id.input_item_quantity);

        DialogHelper.getStyledDialog(this)
                .setTitle("Add Inventory Item")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    String quantityText = quantityInput.getText().toString().trim();

                    if (!name.isEmpty() && !quantityText.isEmpty()) {
                        try {
                            double quantity = Double.parseDouble(quantityText);

                            // Create a new inventory item
                            InventoryItem newItem = new InventoryItem(
                                    UUID.randomUUID().toString(),
                                    name,
                                    "General", // Would be selected in a real app
                                    quantity,
                                    "kg", // Would be selected in a real app
                                    10.0, // Default price
                                    5.0,  // Default minimum level
                                    "Default Supplier" // Would be selected in a real app
                            );

                            inventoryItems.add(0, newItem);
                            inventoryAdapter.notifyItemInserted(0);
                            recyclerView.smoothScrollToPosition(0);

                            Toast.makeText(InventoryActivity.this, "Item added", Toast.LENGTH_SHORT).show();
                        } catch (NumberFormatException e) {
                            Toast.makeText(InventoryActivity.this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(InventoryActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void createDummyInventoryItems() {
        inventoryItems = new ArrayList<>();

        // Add sample inventory items
        inventoryItems.add(new InventoryItem("1", "Tomatoes", "Vegetables", 50.5, "kg", 2.99, 15.0, "Fresh Farms Inc."));
        inventoryItems.add(new InventoryItem("2", "Chicken Breast", "Meat", 25.0, "kg", 8.75, 10.0, "Quality Meats LLC"));
        inventoryItems.add(new InventoryItem("3", "Olive Oil", "Oils", 12.5, "L", 15.50, 5.0, "Mediterranean Imports"));
        inventoryItems.add(new InventoryItem("4", "Flour", "Baking", 45.0, "kg", 1.25, 20.0, "Baker's Supply Co."));
        inventoryItems.add(new InventoryItem("5", "Eggs", "Dairy", 120.0, "units", 0.25, 60.0, "Local Farm Fresh"));

        // Add a low stock item to demonstrate alert
        InventoryItem lowStockItem = new InventoryItem("6", "Basil", "Herbs", 1.5, "kg", 7.99, 2.0, "Herb Gardens Ltd.");
        inventoryItems.add(lowStockItem);
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