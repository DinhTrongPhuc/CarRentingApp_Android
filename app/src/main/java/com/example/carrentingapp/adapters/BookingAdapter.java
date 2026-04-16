package com.example.carrentingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.carrentingapp.R;
import com.example.carrentingapp.models.Booking;
import com.example.carrentingapp.utils.Constants;
import com.example.carrentingapp.utils.FormatUtils;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.VH> {
    private final Context context;
    private final List<Booking> bookings;

    public BookingAdapter(Context context, List<Booking> bookings) {
        this.context = context; this.bookings = bookings;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_booking, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Booking b = bookings.get(position);
        holder.tvCarName.setText(b.getCarName());
        holder.tvDates.setText(FormatUtils.formatDate(b.getStartDate()) + " - " + FormatUtils.formatDate(b.getEndDate()));
        holder.tvTotal.setText(FormatUtils.formatCurrency(b.getTotalPrice()));
        holder.tvDays.setText(b.getTotalDays() + " ngày");

        String status = b.getStatus();
        holder.tvStatus.setText(getStatusLabel(status));
        int color = getStatusColor(status);
        holder.tvStatus.setTextColor(ContextCompat.getColor(context, color));

        if (b.getCarImageUrl() != null && !b.getCarImageUrl().isEmpty()) {
            Glide.with(context).load(b.getCarImageUrl()).centerCrop().into(holder.ivCar);
        }
    }

    private String getStatusLabel(String status) {
        switch (status) {
            case Constants.STATUS_CONFIRMED: return "Đã xác nhận";
            case Constants.STATUS_CANCELLED: return "Đã hủy";
            case Constants.STATUS_COMPLETED: return "Hoàn thành";
            default: return "Chờ xác nhận";
        }
    }

    private int getStatusColor(String status) {
        switch (status) {
            case Constants.STATUS_CONFIRMED: return R.color.status_confirmed;
            case Constants.STATUS_CANCELLED: return R.color.status_cancelled;
            case Constants.STATUS_COMPLETED: return R.color.status_completed;
            default: return R.color.status_pending;
        }
    }

    @Override public int getItemCount() { return bookings.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivCar; TextView tvCarName, tvDates, tvTotal, tvDays, tvStatus;
        VH(@NonNull View v) {
            super(v);
            ivCar = v.findViewById(R.id.iv_car);
            tvCarName = v.findViewById(R.id.tv_car_name);
            tvDates = v.findViewById(R.id.tv_dates);
            tvTotal = v.findViewById(R.id.tv_total);
            tvDays = v.findViewById(R.id.tv_days);
            tvStatus = v.findViewById(R.id.tv_status);
        }
    }
}
