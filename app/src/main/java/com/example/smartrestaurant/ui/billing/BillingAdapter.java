package com.example.smartrestaurant.ui.billing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartrestaurant.R;
import com.example.smartrestaurant.models.Bill;
import com.example.smartrestaurant.models.User;
import com.example.smartrestaurant.helpers.DialogHelper;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BillingAdapter extends RecyclerView.Adapter<BillingAdapter.BillingViewHolder> {

    private List<Bill> billList;
    private Context context;
    private User currentUser;
    private SimpleDateFormat dateFormat;

    public BillingAdapter(List<Bill> billList, Context context, User currentUser) {
        this.billList = billList;
        this.context = context;
        this.currentUser = currentUser;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public BillingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bill, parent, false);
        return new BillingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillingViewHolder holder, int position) {
        Bill bill = billList.get(position);

        holder.customerName.setText(bill.getCustomerName());
        holder.tableNumber.setText("Table: " + bill.getTableNumber());
        holder.billTotal.setText(String.format("Total: R%.2f", bill.getTotalAmount()));
        holder.billSubtotal.setText(String.format("Subtotal: R%.2f", bill.getSubTotal()));
        holder.billTax.setText(String.format("Tax: R%.2f", bill.getTaxAmount()));
        holder.billTip.setText(String.format("Tip: R%.2f", bill.getTipAmount()));

        // Format and display dates
        holder.createdAt.setText("Created: " + dateFormat.format(bill.getCreatedAt()));

        // Status display
        holder.status.setText("Status: " + bill.getStatus().toString());

        // Display payment method if paid
        if (bill.getStatus() == Bill.Status.PAID) {
            holder.paidAt.setVisibility(View.VISIBLE);
            holder.paidAt.setText("Paid: " + dateFormat.format(bill.getPaidAt()));

            holder.paymentMethod.setVisibility(View.VISIBLE);
            holder.paymentMethod.setText("Method: " + bill.getPaymentMethod().toString());

            holder.processPaymentButton.setVisibility(View.GONE);
        } else {
            holder.paidAt.setVisibility(View.GONE);
            holder.paymentMethod.setVisibility(View.GONE);

            // Show process payment button if bill is pending and user is waiter or manager
            if (bill.getStatus() == Bill.Status.PENDING &&
                    (currentUser.getRole() == User.Role.WAITER || currentUser.getRole() == User.Role.MANAGER)) {
                holder.processPaymentButton.setVisibility(View.VISIBLE);
                holder.processPaymentButton.setOnClickListener(v -> showPaymentDialog(bill, position));
            } else {
                holder.processPaymentButton.setVisibility(View.GONE);
            }
        }

        // Show notes if available
        if (bill.getNotes() != null && !bill.getNotes().isEmpty()) {
            holder.notes.setVisibility(View.VISIBLE);
            holder.notes.setText("Notes: " + bill.getNotes());
        } else {
            holder.notes.setVisibility(View.GONE);
        }
    }

    private void showPaymentDialog(Bill bill, int position) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_process_payment, null);
        Spinner paymentMethodSpinner = dialogView.findViewById(R.id.spinner_payment_method);

        // Set up spinner with payment methods
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context, R.array.payment_methods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentMethodSpinner.setAdapter(adapter);

        DialogHelper.getStyledDialog(context)
                .setTitle("Process Payment")
                .setView(dialogView)
                .setPositiveButton("Process", (dialog, which) -> {
                    String selectedMethod = paymentMethodSpinner.getSelectedItem().toString();
                    Bill.PaymentMethod paymentMethod = Bill.PaymentMethod.valueOf(selectedMethod);

                    // Process payment
                    bill.processPayment(paymentMethod);
                    notifyItemChanged(position);

                    Toast.makeText(context, "Payment processed successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return billList.size();
    }

    static class BillingViewHolder extends RecyclerView.ViewHolder {
        TextView customerName, tableNumber, billTotal, billSubtotal, billTax, billTip,
                status, createdAt, paidAt, paymentMethod, notes;
        Button processPaymentButton;

        public BillingViewHolder(@NonNull View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.text_customer_name);
            tableNumber = itemView.findViewById(R.id.text_table_number);
            billTotal = itemView.findViewById(R.id.text_bill_total);
            billSubtotal = itemView.findViewById(R.id.text_bill_subtotal);
            billTax = itemView.findViewById(R.id.text_bill_tax);
            billTip = itemView.findViewById(R.id.text_bill_tip);
            status = itemView.findViewById(R.id.text_bill_status);
            createdAt = itemView.findViewById(R.id.text_created_at);
            paidAt = itemView.findViewById(R.id.text_paid_at);
            paymentMethod = itemView.findViewById(R.id.text_payment_method);
            notes = itemView.findViewById(R.id.text_bill_notes);
            processPaymentButton = itemView.findViewById(R.id.button_process_payment);
        }
    }
}