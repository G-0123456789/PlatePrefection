package com.example.smartrestaurant.ui.inventory;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartrestaurant.R;
import com.example.smartrestaurant.models.InventoryItem;
import com.example.smartrestaurant.models.User;
import com.example.smartrestaurant.helpers.DialogHelper;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private List<InventoryItem> inventoryItems;
    private User currentUser;
    private Context context;
    private SimpleDateFormat dateFormat;

    public InventoryAdapter(List<InventoryItem> inventoryItems, User currentUser, Context context) {
        this.inventoryItems = inventoryItems;
        this.currentUser = currentUser;
        this.context = context;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        InventoryItem item = inventoryItems.get(position);

        holder.itemName.setText(item.getName());
        holder.itemCategory.setText(item.getCategory());
        holder.itemQuantity.setText(String.format("%.1f %s", item.getQuantity(), item.getUnit()));
        holder.itemPrice.setText(String.format("R%.2f / %s", item.getUnitPrice(), item.getUnit()));
        holder.itemValue.setText(String.format("Total: R%.2f", item.getTotalValue()));
        holder.lastUpdated.setText("Last updated: " + dateFormat.format(item.getLastUpdated()));

        // Show low stock warning
        if (item.isLowStock()) {
            holder.lowStockWarning.setVisibility(View.VISIBLE);
            holder.itemQuantity.setTextColor(Color.RED);
        } else {
            holder.lowStockWarning.setVisibility(View.GONE);
            holder.itemQuantity.setTextColor(Color.BLACK);
        }

        // Configure buttons based on user role
        configureButtons(holder, item, position);
    }

    private void configureButtons(InventoryViewHolder holder, InventoryItem item, int position) {
        boolean canEdit = currentUser.getRole() == User.Role.MANAGER || currentUser.getRole() == User.Role.CHEF;

        if (canEdit) {
            holder.updateButton.setVisibility(View.VISIBLE);
            holder.updateButton.setOnClickListener(v -> showUpdateQuantityDialog(item, position));
        } else {
            holder.updateButton.setVisibility(View.GONE);
        }

        // Only managers can delete items
        if (currentUser.getRole() == User.Role.MANAGER) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog(item, position));
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }
    }

    private void showUpdateQuantityDialog(InventoryItem item, int position) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_update_quantity, null);
        TextInputEditText quantityInput = dialogView.findViewById(R.id.input_update_quantity);
        quantityInput.setText(String.valueOf(item.getQuantity()));

        DialogHelper.getStyledDialog(context)
                .setTitle("Update Quantity")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String quantityText = quantityInput.getText().toString().trim();

                    if (!quantityText.isEmpty()) {
                        try {
                            double newQuantity = Double.parseDouble(quantityText);
                            item.setQuantity(newQuantity);
                            notifyItemChanged(position);
                        } catch (NumberFormatException e) {
                            // Show error message
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteConfirmationDialog(InventoryItem item, int position) {
        DialogHelper.getConfirmationDialog(
                context,
                "Delete Item",
                "Are you sure you want to delete " + item.getName() + " from inventory?",
                "Delete",
                (dialog, which) -> {
                    inventoryItems.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, inventoryItems.size());
                },
                "Cancel",
                null
        ).show();
    }

    @Override
    public int getItemCount() {
        return inventoryItems.size();
    }

    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemCategory, itemQuantity, itemPrice, itemValue, lastUpdated, lowStockWarning;
        Button updateButton, deleteButton;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.text_item_name);
            itemCategory = itemView.findViewById(R.id.text_item_category);
            itemQuantity = itemView.findViewById(R.id.text_item_quantity);
            itemPrice = itemView.findViewById(R.id.text_item_price);
            itemValue = itemView.findViewById(R.id.text_item_value);
            lastUpdated = itemView.findViewById(R.id.text_last_updated);
            lowStockWarning = itemView.findViewById(R.id.text_low_stock_warning);
            updateButton = itemView.findViewById(R.id.button_update);
            deleteButton = itemView.findViewById(R.id.button_delete);
        }
    }
}