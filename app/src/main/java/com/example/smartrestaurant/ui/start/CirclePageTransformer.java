package com.example.smartrestaurant.ui.start;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

/**
 * A custom page transformer for circle-clipped ViewPager2
 * to handle the transitions smoothly within a circular container.
 */
public class CirclePageTransformer implements ViewPager2.PageTransformer {

    @Override
    public void transformPage(@NonNull View page, float position) {
        page.setAlpha(0);
        page.setVisibility(View.VISIBLE);

        // When position is in [-1,1]
        if (position <= 1 && position >= -1) {
            page.setAlpha(1);

            // Counteract the default slide transition
            page.setTranslationX(page.getWidth() * -position);

            // Center the image in the circular container
            float yPosition = position * page.getHeight() / 4;
            page.setTranslationY(yPosition);
        }
    }
}