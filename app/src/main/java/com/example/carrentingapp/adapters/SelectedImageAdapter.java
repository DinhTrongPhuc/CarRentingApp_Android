package com.example.carrentingapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.carrentingapp.R;

import java.util.List;

public class SelectedImageAdapter extends RecyclerView.Adapter<SelectedImageAdapter.VH> {
    private final Context context;
    private final List<Uri> uris;

    public SelectedImageAdapter(Context context, List<Uri> uris) {
        this.context = context; this.uris = uris;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_image_selected, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Glide.with(context).load(uris.get(position)).centerCrop().into(holder.iv);
        holder.ivRemove.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                uris.remove(pos);
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, uris.size());
            }
        });
    }

    @Override public int getItemCount() { return uris.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView iv, ivRemove;
        VH(@NonNull View v) {
            super(v);
            iv = v.findViewById(R.id.iv_selected);
            ivRemove = v.findViewById(R.id.iv_remove);
        }
    }
}
