package com.example.smartrestaurant;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smartrestaurant.databinding.ActivityMainBinding;
import com.example.smartrestaurant.models.User;
import com.example.smartrestaurant.ui.auth.LoginActivity;
import com.example.smartrestaurant.ui.orders.OrderActivity;
import com.example.smartrestaurant.ui.reservations.ReservationActivity;
import com.example.smartrestaurant.ui.inventory.InventoryActivity;
import com.example.smartrestaurant.ui.usermanagement.UserManagementActivity;
import com.example.smartrestaurant.ui.billing.BillingActivity;
import com.example.smartrestaurant.ui.reports.ReportsActivity;
import com.example.smartrestaurant.ui.menu.MenuActivity;
import com.example.smartrestaurant.ui.map.MapActivity;
import com.example.smartrestaurant.ui.start.StartActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.view.WindowInsets;
import android.view.WindowInsetsController;

public class MainActivity extends BaseActivity implements OnMapReadyCallback {

    private ActivityMainBinding binding;
    private User currentUser;
    private GoogleMap mMap;
    private FloatingActionButton fab;
    private ViewPager2 mImageSlider;
    private LinearLayout mIndicatorLayout;
    private int[] mSlideImages;
    private ImageView[] mDots;
    private Handler sliderHandler = new Handler(Looper.getMainLooper());

    // Module card views
    private MaterialCardView orderCard, reservationCard, inventoryCard,
            userManagementCard, billingCard, reportCard, menuCard;
    private TextView welcomeText;
    private ImageView instagramIcon, twitterIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Check if app was closed and needs to redirect to login
        if (SmartRestaurantApplication.wasAppClosed()) {
            // App was closed, redirect to login
            redirectToLogin();
            return;
        }

        // Mark app as active
        SmartRestaurantApplication.setAppClosed(false);

        setSupportActionBar(binding.toolbar);
        // Remove the title from the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        // Get the FAB from layout
        fab = findViewById(R.id.fab);

        // Get current user
        if (getIntent().hasExtra("USER")) {
            currentUser = (User) getIntent().getSerializableExtra("USER");
            // Store the user in the application state
            SmartRestaurantApplication.setCurrentUser(currentUser);
        } else {
            // If no user, redirect to login
            redirectToLogin();
            return;
        }

        // Set welcome message based on user role
        welcomeText = findViewById(R.id.welcome_text);
        welcomeText.setText("Welcome, " + currentUser.getName() + "! Role: You are current logged in as " + currentUser.getRole().toString().toLowerCase() + ".");

        // Initialize image slider
        initializeImageSlider();

        // Initialize module cards
        initializeModuleCards();

        // Initialize social media icons
        initializeSocialMediaIcons();

        // Initialize the map
        initializeMap();

        // Modify FAB based on user role
        if (currentUser.getRole() == User.Role.WAITER) {
            if (fab != null) {
                fab.setVisibility(View.VISIBLE);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Quick access to order creation for waiters
                        navigateToRoleBasedFeature("ORDER");
                    }
                });
            }
        } else {
            if (fab != null) {
                fab.setVisibility(View.GONE);
            }
        }
    }

    private void initializeSocialMediaIcons() {
        // Find the icon views
        instagramIcon = findViewById(R.id.instagramIcon);
        twitterIcon = findViewById(R.id.twitterIcon);

        // Set click listeners for social media icons
        if (instagramIcon != null) {
            instagramIcon.setOnClickListener(v -> {
                openUrl("https://www.instagram.com");
            });
        }

        if (twitterIcon != null) {
            twitterIcon.setOnClickListener(v -> {
                openUrl("https://www.x.com");
            });
        }
    }

    private void openUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(android.net.Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open link: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("MainActivity", "Error opening URL: " + e.getMessage());
        }
    }

    private void initializeMap() {
        try {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }

            // Set click listener on the map container to open the full map activity
            View mapView = findViewById(R.id.map);
            if (mapView != null) {
                View parentView = (View) mapView.getParent();
                if (parentView != null) {
                    View cardView = (View) parentView.getParent();
                    if (cardView != null) {
                        cardView.setOnClickListener(v -> {
                            Intent intent = new Intent(MainActivity.this, MapActivity.class);
                            startActivity(intent);
                        });
                    }
                }
            }
        } catch (Exception e) {
            // Log the error but don't crash
            Log.e("MainActivity", "Error initializing map: " + e.getMessage());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;

            // Add a marker for the restaurant location (replace with your actual coordinates)
            LatLng restaurantLocation = new LatLng(40.7128, -74.0060); // Example: New York City
            mMap.addMarker(new MarkerOptions()
                    .position(restaurantLocation)
                    .title("Our Restaurant"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(restaurantLocation, 15f));
        } catch (Exception e) {
            // Log the error but don't crash
            Log.e("MainActivity", "Error setting up map: " + e.getMessage());
        }
    }

    private void initializeModuleCards() {
        // Find card views
        orderCard = findViewById(R.id.card_orders);
        reservationCard = findViewById(R.id.card_reservations);
        inventoryCard = findViewById(R.id.card_inventory);
        userManagementCard = findViewById(R.id.card_user_management);
        billingCard = findViewById(R.id.card_billing);
        reportCard = findViewById(R.id.card_reports);
        menuCard = findViewById(R.id.card_menu);

        // Set click listeners
        orderCard.setOnClickListener(v -> navigateToModule("ORDER"));
        reservationCard.setOnClickListener(v -> navigateToModule("RESERVATION"));
        inventoryCard.setOnClickListener(v -> navigateToModule("INVENTORY"));
        userManagementCard.setOnClickListener(v -> navigateToModule("USER_MANAGEMENT"));
        billingCard.setOnClickListener(v -> navigateToModule("BILLING"));
        reportCard.setOnClickListener(v -> navigateToModule("REPORTS"));
        menuCard.setOnClickListener(v -> navigateToModule("MENU"));

        // Set visibility based on user role
        setModuleVisibility();
    }

    private void setModuleVisibility() {
        // Different modules are visible to different roles
        switch (currentUser.getRole()) {
            case MANAGER:
                // Manager sees everything
                orderCard.setVisibility(View.VISIBLE);
                reservationCard.setVisibility(View.VISIBLE);
                inventoryCard.setVisibility(View.VISIBLE);
                userManagementCard.setVisibility(View.VISIBLE);
                billingCard.setVisibility(View.VISIBLE);
                reportCard.setVisibility(View.VISIBLE);
                menuCard.setVisibility(View.VISIBLE);
                break;
            case WAITER:
                // Waiter sees orders, reservations, billing, and menu
                orderCard.setVisibility(View.VISIBLE);
                reservationCard.setVisibility(View.VISIBLE);
                inventoryCard.setVisibility(View.GONE);
                userManagementCard.setVisibility(View.GONE);
                billingCard.setVisibility(View.VISIBLE);
                reportCard.setVisibility(View.GONE);
                menuCard.setVisibility(View.VISIBLE);
                break;
            case CHEF:
                // Chef sees orders and inventory
                orderCard.setVisibility(View.VISIBLE);
                reservationCard.setVisibility(View.GONE);
                inventoryCard.setVisibility(View.VISIBLE);
                userManagementCard.setVisibility(View.GONE);
                billingCard.setVisibility(View.GONE);
                reportCard.setVisibility(View.GONE);
                menuCard.setVisibility(View.GONE);
                break;
            case CUSTOMER:
                // Customer sees reservations and menu
                orderCard.setVisibility(View.GONE);
                reservationCard.setVisibility(View.VISIBLE);
                inventoryCard.setVisibility(View.GONE);
                userManagementCard.setVisibility(View.GONE);
                billingCard.setVisibility(View.GONE);
                reportCard.setVisibility(View.GONE);
                menuCard.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void initializeImageSlider() {
        // Find slider views
        mImageSlider = findViewById(R.id.image_slider);
        mIndicatorLayout = findViewById(R.id.indicator_layout);

        // Set up the slider images (replace these with your actual drawable resources)
        mSlideImages = new int[]{
                R.drawable.img_1,
                R.drawable.img_2,
                R.drawable.img_3,
                R.drawable.img_4,
                R.drawable.img_5
        };

        // Create and set the slider adapter
        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(mSlideImages);
        mImageSlider.setAdapter(sliderAdapter);

        // Set up dots indicator
        setupSliderDots(0);

        // Add page change listener
        mImageSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                setupSliderDots(position);
                super.onPageSelected(position);
            }
        });

        // Start auto-sliding
        startAutoSlide();
    }

    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (mImageSlider != null) {
                int currentItem = mImageSlider.getCurrentItem();
                if (currentItem < mSlideImages.length - 1) {
                    mImageSlider.setCurrentItem(currentItem + 1);
                } else {
                    mImageSlider.setCurrentItem(0);
                }
            }
            sliderHandler.postDelayed(this, 5000); // Change slide every 3 seconds
        }
    };

    private void startAutoSlide() {
        sliderHandler.postDelayed(sliderRunnable, 10000000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 5000);
    }

    private void setupSliderDots(int currentPosition) {
        if (mIndicatorLayout != null) {
            mIndicatorLayout.removeAllViews();
            mDots = new ImageView[mSlideImages.length];

            for (int i = 0; i < mDots.length; i++) {
                mDots[i] = new ImageView(this);

                if (i == currentPosition) {
                    mDots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_active));
                } else {
                    mDots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_inactive));
                }

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(8, 0, 8, 0);
                mIndicatorLayout.addView(mDots[i], params);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Change the color of the logout icon
        MenuItem logoutItem = menu.findItem(R.id.action_logout);
        if (logoutItem != null && logoutItem.getIcon() != null) {
            // Set the icon color to #F8BC12
            logoutItem.getIcon().setColorFilter(android.graphics.Color.parseColor("#F8BC12"), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            // Clear user from application state
            currentUser = null;
            SmartRestaurantApplication.setCurrentUser(null);
            SmartRestaurantApplication.setSessionActive(false);

            // Clear shared preferences to prevent auto-login
            getSharedPreferences("SmartRestaurantPrefs", MODE_PRIVATE)
                    .edit()
                    .remove("last_username")
                    .apply();

            // Redirect to login page
            Intent intent = new Intent(MainActivity.this, StartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(this, "Logged out.", Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Navigate to a specific module
    private void navigateToModule(String module) {
        Intent intent = null;

        switch (module) {
            case "ORDER":
                if (currentUser.hasPermission("VIEW_ORDERS")) {
                    intent = new Intent(MainActivity.this, OrderActivity.class);
                    intent.putExtra("USER", currentUser);
                }
                break;
            case "RESERVATION":
                if (currentUser.hasPermission("CREATE_RESERVATION") || currentUser.hasPermission("VIEW_RESERVATION")) {
                    intent = new Intent(MainActivity.this, ReservationActivity.class);
                    intent.putExtra("USER", currentUser);
                    // For waiters, set to view mode
                    if (currentUser.getRole() == User.Role.WAITER) {
                        intent.putExtra("VIEW_ONLY", true);
                    }
                }
                break;
            case "INVENTORY":
                if (currentUser.hasPermission("VIEW_INVENTORY")) {
                    intent = new Intent(MainActivity.this, InventoryActivity.class);
                    intent.putExtra("USER", currentUser);
                }
                break;
            case "USER_MANAGEMENT":
                if (currentUser.hasPermission("MANAGE_USERS")) {
                    intent = new Intent(MainActivity.this, UserManagementActivity.class);
                    intent.putExtra("USER", currentUser);
                }
                break;
            case "BILLING":
                if (currentUser.hasPermission("VIEW_BILLING")) {
                    intent = new Intent(MainActivity.this, BillingActivity.class);
                    intent.putExtra("USER", currentUser);
                }
                break;
            case "REPORTS":
                if (currentUser.hasPermission("VIEW_REPORTS")) {
                    intent = new Intent(MainActivity.this, ReportsActivity.class);
                    intent.putExtra("USER", currentUser);
                }
                break;
            case "MENU":
                if (currentUser.hasPermission("VIEW_MENU")) {
                    intent = new Intent(MainActivity.this, MenuActivity.class);
                    intent.putExtra("USER", currentUser);
                }
                break;
            default:
                return;
        }

        if (intent != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "You don't have permission to access this feature", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // App is being stopped, may be closed
        // We'll set a flag that will be checked when the app restarts
        SmartRestaurantApplication.setAppClosed(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up resources
        binding = null;
    }

    // Helper method for quick access actions
    private void navigateToRoleBasedFeature(String action) {
        switch (action) {
            case "ORDER":
                if (currentUser.getRole() == User.Role.WAITER) {
                    // In a real implementation, this would navigate directly to creating a new order
                    showNotImplementedMessage("New order creation");
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void showNotImplementedMessage(String feature) {
        Snackbar snackbar = Snackbar.make(binding.getRoot(), feature + " will be implemented soon", Snackbar.LENGTH_LONG);

        // Only set anchor view if FAB is visible
        if (fab != null && fab.getVisibility() == View.VISIBLE) {
            snackbar.setAnchorView(fab);
        }

        snackbar.setAction("OK", null).show();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}