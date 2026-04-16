package com.example.carrentingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.carrentingapp.R;
import com.example.carrentingapp.models.Car;
import com.example.carrentingapp.utils.FormatUtils;

import java.util.List;

public class ManageCarAdapter extends RecyclerView.Adapter<ManageCarAdapter.VH> {
    public interface OnCarActionListener {
        void onEdit(Car car);
        void onDelete(Car car);
    }

    private final Context context;
    private final List<Car> cars;
    private final OnCarActionListener listener;

    public ManageCarAdapter(Context context, List<Car> cars, OnCarActionListener listener) {
        this.context = context; this.cars = cars; this.listener = listener;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_manage_car, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Car car = cars.get(position);
        holder.tvName.setText(car.getName());
        holder.tvPrice.setText(FormatUtils.formatCurrency(car.getPricePerDay()) + "/ngày");
        holder.tvStatus.setText(car.isAvailable() ? "Đang cho thuê" : "Không khả dụng");
        if (car.getImageUrls() != null && !car.getImageUrls().isEmpty()) {
            Glide.with(context).load(car.getImageUrls().get(0)).centerCrop().into(holder.iv);
        }
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(car));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(car));
    }

    @Override public int getItemCount() { return cars.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView iv; TextView tvName, tvPrice, tvStatus; Button btnEdit, btnDelete;
        VH(@NonNull View v) {
            super(v);
            iv = v.findViewById(R.id.iv_car);
            tvName = v.findViewById(R.id.tv_name);
            tvPrice = v.findViewById(R.id.tv_price);
            tvStatus = v.findViewById(R.id.tv_status);
            btnEdit = v.findViewById(R.id.btn_edit);
            btnDelete = v.findViewById(R.id.btn_delete);
        }
    }
}
