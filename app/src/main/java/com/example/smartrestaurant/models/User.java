package com.example.smartrestaurant.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User implements Serializable {
    public enum Role {
        CUSTOMER,
        WAITER,
        CHEF,
        MANAGER
    }

    private String userId;
    private String username;
    private String name;
    private String email;
    private Role role;

    // Default constructor required for Firebase
    public User() {
    }

    public User(String userId, String username, String name, String email, Role role) {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Exclude
    public boolean hasPermission(String permission) {
        switch (role) {
            case MANAGER:
                // Manager has all permissions
                return true;
            case CHEF:
                // Chef permissions - expanded
                return permission.equals("VIEW_ORDERS") ||
                        permission.equals("UPDATE_ORDER_STATUS") ||
                        permission.equals("VIEW_INVENTORY");
            case WAITER:
                // Waiter permissions - expanded
                return permission.equals("CREATE_ORDER") ||
                        permission.equals("VIEW_ORDERS") ||
                        permission.equals("UPDATE_ORDER_STATUS") ||
                        permission.equals("CREATE_BILL") ||
                        permission.equals("VIEW_MENU") ||
                        permission.equals("VIEW_BILLING") ||
                        permission.equals("CREATE_RESERVATION") ||
                        permission.equals("VIEW_RESERVATION");
            case CUSTOMER:
                // Customer permissions - expanded
                return permission.equals("CREATE_RESERVATION") ||
                        permission.equals("VIEW_RESERVATION") ||
                        permission.equals("VIEW_MENU") ||
                        permission.equals("SUBMIT_FEEDBACK");
            default:
                return false;
        }
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("username", username);
        result.put("name", name);
        result.put("email", email);
        result.put("role", role);

        return result;
    }
}