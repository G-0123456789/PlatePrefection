package com.example.smartrestaurant.ui.reservations;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartrestaurant.R;
import com.example.smartrestaurant.models.Reservation;
import com.example.smartrestaurant.models.User;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    private List<Reservation> reservationList;
    private User currentUser;
    private Context context;
    private SimpleDateFormat dateFormat;

    public ReservationAdapter(List<Reservation> reservationList, User currentUser, Context context) {
        this.reservationList = reservationList;
        this.currentUser = currentUser;
        this.context = context;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault());
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reservation, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation reservation = reservationList.get(position);

        holder.customerName.setText(reservation.getCustomerName());
        holder.reservationTime.setText(dateFormat.format(reservation.getReservationDate()));
        holder.partySize.setText("Party size: " + reservation.getPartySize());
        holder.tableNumber.setText("Table: " + reservation.getTableNumber());
        holder.status.setText("Status: " + reservation.getStatus().name());

        // Show notes if available
        if (reservation.getNotes() != null && !reservation.getNotes().isEmpty()) {
            holder.notes.setVisibility(View.VISIBLE);
            holder.notes.setText("Notes: " + reservation.getNotes());
        } else {
            holder.notes.setVisibility(View.GONE);
        }

        // Configure action buttons based on user role and reservation status
        configureActionButtons(holder, reservation);
    }

    private void configureActionButtons(ReservationViewHolder holder, Reservation reservation) {
        // Show/hide buttons based on user role and reservation status
        if (currentUser.getRole() == User.Role.MANAGER) {
            holder.primaryButton.setVisibility(View.VISIBLE);

            if (reservation.getStatus() == Reservation.Status.PENDING) {
                holder.primaryButton.setText("Confirm");
                holder.primaryButton.setOnClickListener(v ->
                        updateReservationStatus(reservation, Reservation.Status.CONFIRMED));
            } else if (reservation.getStatus() == Reservation.Status.CONFIRMED) {
                holder.primaryButton.setText("Check In");
                holder.primaryButton.setOnClickListener(v ->
                        updateReservationStatus(reservation, Reservation.Status.CHECKED_IN));
            } else if (reservation.getStatus() == Reservation.Status.CHECKED_IN) {
                holder.primaryButton.setText("Complete");
                holder.primaryButton.setOnClickListener(v ->
                        updateReservationStatus(reservation, Reservation.Status.COMPLETED));
            } else {
                holder.primaryButton.setVisibility(View.GONE);
            }

            // Manager can cancel any non-completed reservation
            if (reservation.getStatus() != Reservation.Status.COMPLETED &&
                    reservation.getStatus() != Reservation.Status.CANCELLED &&
                    reservation.getStatus() != Reservation.Status.NO_SHOW) {
                holder.secondaryButton.setVisibility(View.VISIBLE);
                holder.secondaryButton.setText("Cancel");
                holder.secondaryButton.setOnClickListener(v ->
                        updateReservationStatus(reservation, Reservation.Status.CANCELLED));
            } else {
                holder.secondaryButton.setVisibility(View.GONE);
            }
        } else if (currentUser.getRole() == User.Role.WAITER) {
            // Waiters can only check in customers
            if (reservation.getStatus() == Reservation.Status.CONFIRMED) {
                holder.primaryButton.setVisibility(View.VISIBLE);
                holder.primaryButton.setText("Check In");
                holder.primaryButton.setOnClickListener(v ->
                        updateReservationStatus(reservation, Reservation.Status.CHECKED_IN));
            } else {
                holder.primaryButton.setVisibility(View.GONE);
            }

            holder.secondaryButton.setVisibility(View.GONE);
        } else if (currentUser.getRole() == User.Role.CUSTOMER) {
            // Customers can cancel their own pending or confirmed reservations
            if (reservation.getCustomerId().equals(currentUser.getUserId()) &&
                    (reservation.getStatus() == Reservation.Status.PENDING ||
                            reservation.getStatus() == Reservation.Status.CONFIRMED)) {
                holder.primaryButton.setVisibility(View.VISIBLE);
                holder.primaryButton.setText("Cancel");
                holder.primaryButton.setOnClickListener(v ->
                        updateReservationStatus(reservation, Reservation.Status.CANCELLED));
            } else {
                holder.primaryButton.setVisibility(View.GONE);
            }

            holder.secondaryButton.setVisibility(View.GONE);
        } else {
            // All other roles just view
            holder.primaryButton.setVisibility(View.GONE);
            holder.secondaryButton.setVisibility(View.GONE);
        }
    }

    private void updateReservationStatus(Reservation reservation, Reservation.Status newStatus) {
        // In a real app, this would update the database
        reservation.setStatus(newStatus);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return reservationList.size();
    }

    static class ReservationViewHolder extends RecyclerView.ViewHolder {
        TextView customerName, reservationTime, partySize, tableNumber, status, notes;
        Button primaryButton, secondaryButton;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.text_customer_name);
            reservationTime = itemView.findViewById(R.id.text_reservation_time);
            partySize = itemView.findViewById(R.id.text_party_size);
            tableNumber = itemView.findViewById(R.id.text_table_number);
            status = itemView.findViewById(R.id.text_reservation_status);
            notes = itemView.findViewById(R.id.text_reservation_notes);
            primaryButton = itemView.findViewById(R.id.button_primary_action);
            secondaryButton = itemView.findViewById(R.id.button_secondary_action);
        }
    }
}