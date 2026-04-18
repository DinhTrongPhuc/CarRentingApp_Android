package com.example.carrentingapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
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
import com.google.android.material.button.MaterialButton;

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

        holder.btnViewHandover.setOnClickListener(v -> showHandoverDocumentDialog(b));
    }

    private void showHandoverDocumentDialog(Booking booking) {
        String handoverContent = "\nBIÊN BẢN GIAO NHẬN XE\n\n"
            + "Ngày lập: " + FormatUtils.formatDate(System.currentTimeMillis()) + "\n\n"
            + "1. Thông tin đơn thuê:\n"
            + "   - Mã đơn thuê: " + (booking.getId() != null ? booking.getId() : "N/A") + "\n"
            + "   - Tên xe: " + booking.getCarName() + "\n"
            + "   - Người thuê: " + booking.getRenterName() + "\n"
            + "   - SĐT Người thuê: " + booking.getRenterPhone() + "\n"
            + "   - Ngày nhận xe: " + FormatUtils.formatDate(booking.getStartDate()) + "\n"
            + "   - Ngày trả xe dự kiến: " + FormatUtils.formatDate(booking.getEndDate()) + "\n"
            + "   - Tổng số ngày thuê: " + booking.getTotalDays() + " ngày\n"
            + "   - Tổng tiền: " + FormatUtils.formatCurrency(booking.getTotalPrice()) + "\n\n"
            + "2. Tình trạng xe khi giao:\n"
            + "   - Xe hoạt động tốt, sạch sẽ, đầy đủ giấy tờ cần thiết.\n"
            + "   - Các hạng mục kiểm tra (xăng, lốp, đèn, còi, gương, nội thất, vết xước) đã được ghi nhận và xác nhận giữa hai bên.\n\n"
            + "3. Trách nhiệm của người thuê:\n"
            + "   - Bảo quản xe cẩn thận, tuân thủ luật giao thông đường bộ.\n"
            + "   - Chịu trách nhiệm bồi thường thiệt hại nếu xe bị hỏng hóc, mất cắp do lỗi của người thuê.\n\n"
            + "4. Trách nhiệm của chủ xe:\n"
            + "   - Đảm bảo xe đủ điều kiện lưu hành và an toàn kỹ thuật.\n"
            + "   - Hỗ trợ người thuê trong các trường hợp khẩn cấp (hỏng hóc trên đường không do lỗi người thuê).\n\n"
            + "5. Thủ tục nhận/trả xe:\n"
            + "   - Hai bên tiến hành kiểm tra xe, ký xác nhận biên bản giao nhận khi nhận và trả xe.\n"
            + "   - Việc bàn giao xe hoàn tất khi hai bên đã ký vào biên bản này.\n\n"
            + "__________________________\n"
            + "Chủ xe                           Người thuê\n";

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Biên bản Giao nhận Xe");
        TextView messageView = new TextView(context);
        messageView.setText(handoverContent);
        messageView.setPadding(40, 40, 40, 40);
        messageView.setMovementMethod(new ScrollingMovementMethod());
        builder.setView(messageView);
        builder.setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss());
        builder.show();
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
        MaterialButton btnViewHandover;
        VH(@NonNull View v) {
            super(v);
            ivCar = v.findViewById(R.id.iv_car);
            tvCarName = v.findViewById(R.id.tv_car_name);
            tvDates = v.findViewById(R.id.tv_dates);
            tvTotal = v.findViewById(R.id.tv_total);
            tvDays = v.findViewById(R.id.tv_days);
            tvStatus = v.findViewById(R.id.tv_status);
            btnViewHandover = v.findViewById(R.id.btn_view_handover);
        }
    }
}
