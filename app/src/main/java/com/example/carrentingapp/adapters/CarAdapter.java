package com.example.carrentingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.carrentingapp.R;
import com.example.carrentingapp.activities.CarDetailActivity;
import com.example.carrentingapp.models.Car;
import com.example.carrentingapp.utils.Constants;
import com.example.carrentingapp.utils.FormatUtils;

import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {
    private final Context context;
    private final List<Car> cars;

    public CarAdapter(Context context, List<Car> cars) {
        this.context = context; this.cars = cars;
    }

    @NonNull @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_car, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Car car = cars.get(position);
        holder.tvName.setText(car.getName());
        holder.tvPrice.setText(FormatUtils.formatCurrency(car.getPricePerDay()) + "/ngày");
        holder.tvLocation.setText(car.getLocation());
        holder.tvType.setText(car.getType());

        // FR4.2 / FR4.4 - Load thumbnail
        if (car.getImageUrls() != null && !car.getImageUrls().isEmpty()) {
            Glide.with(context).load(car.getImageUrls().get(0))
                .placeholder(R.drawable.ic_car_placeholder)
                .error(R.drawable.ic_image_error)
                .centerCrop().into(holder.ivCar);
        } else {
            holder.ivCar.setImageResource(R.drawable.ic_car_placeholder);
        }

        // FR5.1 - Navigate to detail
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CarDetailActivity.class);
            intent.putExtra(Constants.KEY_CAR_ID, car.getId());
            context.startActivity(intent);
        });
    }

    @Override public int getItemCount() { return cars.size(); }

    static class CarViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCar;
        TextView tvName, tvPrice, tvLocation, tvType;

        CarViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCar = itemView.findViewById(R.id.iv_car);
            tvName = itemView.findViewById(R.id.tv_car_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvType = itemView.findViewById(R.id.tv_type);
        }
    }
}
