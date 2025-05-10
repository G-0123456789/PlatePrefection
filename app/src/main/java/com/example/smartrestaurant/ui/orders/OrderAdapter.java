package com.example.smartrestaurant.ui.orders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartrestaurant.R;
import com.example.smartrestaurant.models.Order;
import com.example.smartrestaurant.models.User;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private User currentUser;
    private Context context;
    private SimpleDateFormat dateFormat;

    public OrderAdapter(List<Order> orderList, User currentUser, Context context) {
        this.orderList = orderList;
        this.currentUser = currentUser;
        this.context = context;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.orderNumber.setText("Order #" + order.getOrderId());
        holder.tableNumber.setText("Table: " + order.getTableNumber());
        holder.orderStatus.setText("Status: " + order.getStatus().name());
        holder.orderTime.setText("Created: " + dateFormat.format(order.getCreatedAt()));
        holder.totalAmount.setText("Total: R" + String.format("%.2f", order.getTotalAmount()));

        // Show notes if available
        if (order.getNotes() != null && !order.getNotes().isEmpty()) {
            holder.orderNotes.setVisibility(View.VISIBLE);
            holder.orderNotes.setText("Notes: " + order.getNotes());
        } else {
            holder.orderNotes.setVisibility(View.GONE);
        }

        // Configure action buttons based on user role and order status
        configureActionButtons(holder, order);
    }

    private void configureActionButtons(OrderViewHolder holder, Order order) {
        // Show/hide buttons based on user role and order status
        if (currentUser.getRole() == User.Role.WAITER) {
            if (order.getStatus() == Order.Status.READY) {
                holder.primaryButton.setVisibility(View.VISIBLE);
                holder.primaryButton.setText("Mark Served");
                holder.primaryButton.setOnClickListener(v -> updateOrderStatus(order, Order.Status.SERVED));
            } else if (order.getStatus() == Order.Status.RECEIVED) {
                holder.primaryButton.setVisibility(View.VISIBLE);
                holder.primaryButton.setText("Edit Order");
                holder.primaryButton.setOnClickListener(v -> editOrder(order));
            } else {
                holder.primaryButton.setVisibility(View.GONE);
            }
        } else if (currentUser.getRole() == User.Role.CHEF) {
            if (order.getStatus() == Order.Status.RECEIVED) {
                holder.primaryButton.setVisibility(View.VISIBLE);
                holder.primaryButton.setText("Start Preparing");
                holder.primaryButton.setOnClickListener(v -> updateOrderStatus(order, Order.Status.PREPARING));
            } else if (order.getStatus() == Order.Status.PREPARING) {
                holder.primaryButton.setVisibility(View.VISIBLE);
                holder.primaryButton.setText("Mark Ready");
                holder.primaryButton.setOnClickListener(v -> updateOrderStatus(order, Order.Status.READY));
            } else {
                holder.primaryButton.setVisibility(View.GONE);
            }
        } else {
            holder.primaryButton.setVisibility(View.GONE);
        }

        // Secondary button is for viewing details - available for all roles
        holder.secondaryButton.setText("View Details");
        holder.secondaryButton.setOnClickListener(v -> viewOrderDetails(order));
    }

    private void updateOrderStatus(Order order, Order.Status newStatus) {
        // In a real app, this would update the database
        order.setStatus(newStatus);
        notifyDataSetChanged();
    }

    private void editOrder(Order order) {
        // This would launch an edit screen in a real app
    }

    private void viewOrderDetails(Order order) {
        // This would show full order details in a real app
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderNumber, tableNumber, orderStatus, orderTime, orderNotes, totalAmount;
        Button primaryButton, secondaryButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderNumber = itemView.findViewById(R.id.text_order_number);
            tableNumber = itemView.findViewById(R.id.text_table_number);
            orderStatus = itemView.findViewById(R.id.text_order_status);
            orderTime = itemView.findViewById(R.id.text_order_time);
            orderNotes = itemView.findViewById(R.id.text_order_notes);
            totalAmount = itemView.findViewById(R.id.text_order_total);
            primaryButton = itemView.findViewById(R.id.button_primary_action);
            secondaryButton = itemView.findViewById(R.id.button_secondary_action);
        }
    }
}