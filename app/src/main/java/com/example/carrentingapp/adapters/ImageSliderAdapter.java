package com.example.carrentingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.carrentingapp.R;

import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder> {
    private final Context context;
    private final List<String> imageUrls;

    public ImageSliderAdapter(Context context, List<String> imageUrls) {
        this.context = context; this.imageUrls = imageUrls;
    }

    @NonNull @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_slider, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        // FR5.2 / FR5.5 - High quality images with error fallback
        Glide.with(context).load(imageUrls.get(position))
            .placeholder(R.drawable.ic_car_placeholder)
            .error(R.drawable.ic_image_error)
            .centerCrop().into(holder.ivSlide);
    }

    @Override public int getItemCount() { return imageUrls.size(); }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSlide;
        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSlide = itemView.findViewById(R.id.iv_slide);
        }
    }
}
