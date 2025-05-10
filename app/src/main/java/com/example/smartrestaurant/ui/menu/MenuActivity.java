package com.example.smartrestaurant.ui.menu;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartrestaurant.BaseActivity;
import com.example.smartrestaurant.R;
import com.example.smartrestaurant.models.User;
import com.example.smartrestaurant.ui.menu.MenuItem;
import com.example.smartrestaurant.helpers.DialogHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends BaseActivity {

    private RecyclerView menuRecyclerView;
    private MenuAdapter menuAdapter;
    private FloatingActionButton addMenuItemFab;
    private User currentUser;
    private List<MenuItem> menuItems;
    private DatabaseReference menuRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Get current user
        if (getIntent().hasExtra("USER")) {
            currentUser = (User) getIntent().getSerializableExtra("USER");
        } else {
            finish();
            return;
        }

        // Initialize Firebase
        menuRef = FirebaseDatabase.getInstance().getReference("menu_items");

        // Initialize UI components
        menuRecyclerView = findViewById(R.id.menu_recycler_view);
        addMenuItemFab = findViewById(R.id.add_menu_item_fab);

        // Set up RecyclerView
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize menu items list
        menuItems = new ArrayList<>();

        // Set up adapter
        menuAdapter = new MenuAdapter(menuItems, currentUser);
        menuRecyclerView.setAdapter(menuAdapter);

        // Show FAB only for managers
        if (currentUser.getRole() == User.Role.MANAGER) {
            addMenuItemFab.setVisibility(View.VISIBLE);
            addMenuItemFab.setOnClickListener(v -> showAddMenuItemDialog());
        } else {
            addMenuItemFab.setVisibility(View.GONE);
        }

        // Set title
        setTitle("Menu");

        // Load menu items from Firebase
        loadMenuItemsFromFirebase();
    }

    private void loadMenuItemsFromFirebase() {
        menuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                menuItems.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MenuItem menuItem = snapshot.getValue(MenuItem.class);
                    if (menuItem != null) {
                        menuItems.add(menuItem);
                    }
                }

                if (menuItems.isEmpty()) {
                    // If no items in database, add demo items
                    addDemoMenuItems();
                }

                // Notify adapter of data change
                menuAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MenuActivity.this, "Failed to load menu items", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addDemoMenuItems() {
        // Add some demo items to Firebase with image URLs
        List<MenuItem> demoItems = new ArrayList<>();
        demoItems.add(new MenuItem("1", "Caesar Salad", "Fresh romaine lettuce with Caesar dressing, croutons, and parmesan", 139.99, "appetizer",
                "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?q=80&w=500"));
        demoItems.add(new MenuItem("2", "Margherita Pizza", "Classic pizza with tomato sauce, mozzarella, and fresh basil", 194.99, "main",
                "https://images.unsplash.com/photo-1513104890138-7c749659a591?q=80&w=500"));
        demoItems.add(new MenuItem("3", "Spaghetti Bolognese", "Spaghetti pasta with rich meat sauce", 189.99, "main",
                "https://images.unsplash.com/photo-1555949258-eb67b1ef0ceb?q=80&w=500"));
        demoItems.add(new MenuItem("4", "Grilled Salmon", "Fresh salmon fillet served with seasonal vegetables", 249.99, "main",
                "https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?q=80&w=500"));
        demoItems.add(new MenuItem("5", "Tiramisu", "Classic Italian coffee-flavored dessert", 60.45, "dessert",
                "https://images.unsplash.com/photo-1571877899756-56bd867cf8fe?q=80&w=500"));
        demoItems.add(new MenuItem("6", "Cheesecake", "New York style cheesecake with berry compote", 59.99, "dessert",
                "https://images.unsplash.com/photo-1533134242443-d4fd215305ad?q=80&w=500"));
        demoItems.add(new MenuItem("7", "Iced Coffee", "Freshly brewed coffee served over ice with cream", 54.99, "drink",
                "https://images.unsplash.com/photo-1461023058943-07fcbe16d735?q=80&w=500"));
        demoItems.add(new MenuItem("8", "Strawberry Smoothie", "Refreshing blend of fresh strawberries, yogurt, and honey", 84.99, "drink",
                "https://images.unsplash.com/photo-1553177595-4de2bb0842b9?q=80&w=500"));

        for (MenuItem item : demoItems) {
            menuRef.child(item.getId()).setValue(item);
        }
    }

    private void showAddMenuItemDialog() {
        // Create dialog view
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_menu_item, null);

        final TextInputEditText nameInput = dialogView.findViewById(R.id.menu_item_name_input);
        final TextInputEditText descriptionInput = dialogView.findViewById(R.id.menu_item_description_input);
        final TextInputEditText priceInput = dialogView.findViewById(R.id.menu_item_price_input);
        final TextInputEditText categoryInput = dialogView.findViewById(R.id.menu_item_category_input);
        final TextInputEditText imageUrlInput = dialogView.findViewById(R.id.menu_item_image_url_input);

        DialogHelper.getStyledDialog(this)
                .setTitle("Add New Menu Item")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    String description = descriptionInput.getText().toString().trim();
                    String priceStr = priceInput.getText().toString().trim();
                    String category = categoryInput.getText().toString().trim();
                    String imageUrl = imageUrlInput.getText().toString().trim();

                    if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty() || category.isEmpty()) {
                        Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double price = Double.parseDouble(priceStr);

                    // Generate a new key from Firebase
                    String newItemId = menuRef.push().getKey();

                    // Create new menu item
                    MenuItem newItem = new MenuItem(newItemId, name, description, price, category, imageUrl);

                    // Save to Firebase
                    menuRef.child(newItemId).setValue(newItem)
                            .addOnSuccessListener(aVoid -> Toast.makeText(this, "Menu item added", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}