package com.example.smartrestaurant.ui.usermanagement;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartrestaurant.R;
import com.example.smartrestaurant.helpers.DatabaseHelper;
import com.example.smartrestaurant.helpers.DialogHelper;
import com.example.smartrestaurant.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private static final String TAG = "UserAdapter";

    private List<User> userList;
    private Context context;
    private DatabaseHelper databaseHelper;

    public UserAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
        this.databaseHelper = DatabaseHelper.getInstance();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.userName.setText(user.getName());
        holder.userUsername.setText("Username: " + user.getUsername());
        holder.userEmail.setText("Email: " + user.getEmail());
        holder.userRole.setText("Role: " + user.getRole().toString());

        holder.editButton.setOnClickListener(v -> showEditUserDialog(user, position));
        holder.deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog(user, position));
    }

    private void showEditUserDialog(User user, int position) {
        // In a real app, this would show a dialog with fields to edit the user
        Toast.makeText(context, "Edit user functionality would be implemented here", Toast.LENGTH_SHORT).show();
    }

    private void showDeleteConfirmationDialog(User user, int position) {
        DialogHelper.getConfirmationDialog(
                context,
                "Delete User",
                "Are you sure you want to delete " + user.getName() + "?",
                "Delete",
                (dialog, which) -> {
                    // Remove locally first for better UX
                    userList.remove(position);
                    notifyItemRemoved(position);

                    // Delete from database
                    databaseHelper.deleteUser(user.getUserId(), new DatabaseHelper.DatabaseCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            Toast.makeText(context, "User deleted successfully", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "User " + user.getUsername() + " deleted from database");
                        }

                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "Error deleting user: " + error);
                            Toast.makeText(context, "Deleted locally but sync failed", Toast.LENGTH_SHORT).show();

                            // Could add code to restore the user in the list if needed
                        }
                    });
                },
                "Cancel",
                null
        ).show();
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userUsername, userEmail, userRole;
        Button editButton, deleteButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.text_user_name);
            userUsername = itemView.findViewById(R.id.text_username);
            userEmail = itemView.findViewById(R.id.text_email);
            userRole = itemView.findViewById(R.id.text_role);
            editButton = itemView.findViewById(R.id.button_edit);
            deleteButton = itemView.findViewById(R.id.button_delete);
        }
    }
}