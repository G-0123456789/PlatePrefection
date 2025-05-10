package com.example.smartrestaurant.helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import com.example.smartrestaurant.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Helper class for creating dialogs with consistent styling throughout the app.
 */
public class DialogHelper {

    /**
     * Creates a styled MaterialAlertDialogBuilder with the app's dialog theme.
     * Background: #616569, Text: black, Button background: black, Button text: #F8BC12
     *
     * @param context The context
     * @return A styled MaterialAlertDialogBuilder
     */
    public static MaterialAlertDialogBuilder getStyledDialog(Context context) {
        return new MaterialAlertDialogBuilder(context, R.style.CustomDialogStyle);
    }

    /**
     * Creates a styled MaterialAlertDialogBuilder with the app's dialog theme and a custom view.
     *
     * @param context     The context
     * @param layoutResId The resource ID of the layout to inflate
     * @return A styled MaterialAlertDialogBuilder with the inflated view
     */
    public static MaterialAlertDialogBuilder getStyledDialogWithView(Context context, int layoutResId) {
        View view = LayoutInflater.from(context).inflate(layoutResId, null);
        return getStyledDialog(context).setView(view);
    }

    /**
     * Creates a styled MaterialAlertDialogBuilder for confirmation dialogs.
     *
     * @param context            The context
     * @param title              The dialog title
     * @param message            The dialog message
     * @param positiveButtonText The text for the positive button
     * @param positiveListener   The listener for the positive button click
     * @param negativeButtonText The text for the negative button (can be null)
     * @param negativeListener   The listener for the negative button click (can be null)
     * @return A styled MaterialAlertDialogBuilder configured as a confirmation dialog
     */
    public static MaterialAlertDialogBuilder getConfirmationDialog(
            Context context,
            String title,
            String message,
            String positiveButtonText,
            DialogInterface.OnClickListener positiveListener,
            String negativeButtonText,
            DialogInterface.OnClickListener negativeListener) {

        MaterialAlertDialogBuilder builder = getStyledDialog(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, positiveListener);

        if (negativeButtonText != null) {
            builder.setNegativeButton(negativeButtonText, negativeListener);
        }

        return builder;
    }
}