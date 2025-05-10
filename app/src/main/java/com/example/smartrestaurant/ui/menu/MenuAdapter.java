package com.example.smartrestaurant.ui.menu;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartrestaurant.R;
import com.example.smartrestaurant.models.User;
import com.example.smartrestaurant.ui.menu.MenuItem;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Adapter for displaying menu items in a RecyclerView
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private final List<com.example.smartrestaurant.ui.menu.MenuItem> menuItemList;
    private final User currentUser;

    public MenuAdapter(List<com.example.smartrestaurant.ui.menu.MenuItem> menuItemList, User currentUser) {
        this.menuItemList = menuItemList;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        com.example.smartrestaurant.ui.menu.MenuItem menuItem = menuItemList.get(position);

        holder.nameTextView.setText(menuItem.getName());
        holder.descriptionTextView.setText(menuItem.getDescription());
        holder.priceTextView.setText(String.format("R%.2f", menuItem.getPrice()));
        holder.categoryTextView.setText(menuItem.getCategory());

        // Load image if available
        String imageUrl = menuItem.getImageUrl();
        if (!TextUtils.isEmpty(imageUrl)) {
            // Load image in background
            new DownloadImageTask(holder.imageView).execute(imageUrl);
        } else {
            // Use a placeholder
            holder.imageView.setImageResource(R.drawable.img_1);
        }
    }

    @Override
    public int getItemCount() {
        return menuItemList.size();
    }

    // AsyncTask to download images
    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String imageUrl = urls[0];
            Bitmap bitmap = null;
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null && imageView != null) {
                imageView.setImageBitmap(result);
            }
        }
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView descriptionTextView;
        TextView priceTextView;
        TextView categoryTextView;
        ImageView imageView;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.menu_item_name);
            descriptionTextView = itemView.findViewById(R.id.menu_item_description);
            priceTextView = itemView.findViewById(R.id.menu_item_price);
            categoryTextView = itemView.findViewById(R.id.menu_item_category);
            imageView = itemView.findViewById(R.id.menu_item_image);
        }
    }
}