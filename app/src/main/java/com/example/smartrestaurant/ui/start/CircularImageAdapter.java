package com.example.smartrestaurant.ui.start;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartrestaurant.R;

public class CircularImageAdapter extends RecyclerView.Adapter<CircularImageAdapter.CircularImageViewHolder> {

    private int[] images;

    public CircularImageAdapter(int[] images) {
        this.images = images;
    }

    @NonNull
    @Override
    public CircularImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.circular_slide_item, parent, false);
        return new CircularImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CircularImageViewHolder holder, int position) {
        holder.imageView.setImageResource(images[position]);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public static class CircularImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public CircularImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.slide_image);
        }
    }
}