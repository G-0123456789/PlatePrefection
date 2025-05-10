package com.example.smartrestaurant;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Base activity class that applies fullscreen mode to all activities that extend it.
 * This provides a consistent fullscreen experience throughout the app.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity fullscreen
        makeFullscreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reapply fullscreen when resuming to handle cases where it might be interrupted
        makeFullscreen();
    }

    /**
     * Makes the activity fullscreen by hiding system UI elements
     */
    protected void makeFullscreen() {
        // Hide the status bar
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // For Android R+ (API 30+), use reflection to avoid direct API calls
        if (Build.VERSION.SDK_INT >= 30) {
            try {
                getWindow().getClass().getMethod("setDecorFitsSystemWindows", boolean.class)
                        .invoke(getWindow(), false);

                Object insetsController = getWindow().getClass().getMethod("getInsetsController").invoke(getWindow());
                Class<?> typeClass = Class.forName("android.view.WindowInsets$Type");
                int statusBars = (int) typeClass.getMethod("statusBars").invoke(null);
                int navBars = (int) typeClass.getMethod("navigationBars").invoke(null);

                insetsController.getClass().getMethod("hide", int.class)
                        .invoke(insetsController, statusBars | navBars);

                int behaviorConstant = 1; // BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                insetsController.getClass().getMethod("setSystemBarsBehavior", int.class)
                        .invoke(insetsController, behaviorConstant);
            } catch (Exception e) {
                Log.e("BaseActivity", "Error setting immersive mode: " + e.getMessage());
                oldStyleFullscreen();
            }
        } else {
            oldStyleFullscreen();
        }
    }

    /**
     * Fallback fullscreen implementation for older Android versions
     */
    private void oldStyleFullscreen() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }
}