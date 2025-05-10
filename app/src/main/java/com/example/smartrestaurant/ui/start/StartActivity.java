package com.example.smartrestaurant.ui.start;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smartrestaurant.BaseActivity;
import com.example.smartrestaurant.MainActivity;
import com.example.smartrestaurant.R;
import com.example.smartrestaurant.ui.start.CircularImageAdapter;
import com.example.smartrestaurant.models.User;
import com.example.smartrestaurant.ui.auth.LoginActivity;
import com.example.smartrestaurant.ui.auth.SignupActivity;
import com.example.smartrestaurant.SmartRestaurantApplication;

public class StartActivity extends BaseActivity {

    private ViewPager2 mImageSlider;
    private LinearLayout mIndicatorLayout;
    private int[] mSlideImages;
    private ImageView[] mDots;
    private Handler sliderHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Reset application state when reaching start screen
        SmartRestaurantApplication.setCurrentUser(null);
        SmartRestaurantApplication.setSessionActive(false);

        // Initialize image slider
        initializeImageSlider();

        // Set up buttons
        Button guestButton = findViewById(R.id.guest_button);
        Button loginButton = findViewById(R.id.login_button);
        TextView signupText = findViewById(R.id.signup_link);

        guestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAsGuest();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, LoginActivity.class));
            }
        });

        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, SignupActivity.class));
            }
        });
    }

    private void loginAsGuest() {
        // Create guest user with customer role
        User guestUser = new User(
                "guest-" + System.currentTimeMillis(),
                "guest",
                "Guest User",
                "guest@example.com",
                User.Role.CUSTOMER
        );

        // Set up session for the guest user
        SmartRestaurantApplication.setCurrentUser(guestUser);
        SmartRestaurantApplication.setAppClosed(false);

        // Navigate to MainActivity with guest user
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        intent.putExtra("USER", guestUser);
        startActivity(intent);
        finish();
    }

    private void initializeImageSlider() {
        // Find slider views
        mImageSlider = findViewById(R.id.image_slider);
        mIndicatorLayout = findViewById(R.id.indicator_layout);

        // Set up the slider images
        mSlideImages = new int[]{
                R.drawable.img_2,
                R.drawable.img_3,
                R.drawable.img_5
        };

        // Create and set the slider adapter
        CircularImageAdapter sliderAdapter = new CircularImageAdapter(mSlideImages);
        mImageSlider.setAdapter(sliderAdapter);

        // Apply circular page transformer
        mImageSlider.setPageTransformer(new CirclePageTransformer());

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
            sliderHandler.postDelayed(this, 10000); // Change slide every 5 seconds
        }
    };

    private void startAutoSlide() {
        sliderHandler.postDelayed(sliderRunnable, 1000);
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

    @Override
    protected void onStop() {
        super.onStop();
        // Mark app as stopped/closed when leaving this activity too
        SmartRestaurantApplication.setAppClosed(true);
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
}