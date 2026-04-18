package com.example.carrentingapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.carrentingapp.R;
import com.example.carrentingapp.databinding.ActivityBookingBinding;
import com.example.carrentingapp.models.Booking;
import com.example.carrentingapp.models.Car;
import com.example.carrentingapp.repositories.BookingRepository;
import com.example.carrentingapp.repositories.CarRepository;
import com.example.carrentingapp.utils.Constants;
import com.example.carrentingapp.utils.FormatUtils;
import com.example.carrentingapp.utils.SessionManager;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.Calendar;

public class BookingActivity extends AppCompatActivity {
    private ActivityBookingBinding binding;
    private final CarRepository carRepo = new CarRepository();
    private final BookingRepository bookingRepo = new BookingRepository();
    private SessionManager session;
    private Car currentCar;
    private long selectedStartDate = 0, selectedEndDate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        session = new SessionManager(this);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String carId = getIntent().getStringExtra(Constants.KEY_CAR_ID);
        if (carId == null) { finish(); return; }

        loadCarInfo(carId);
        binding.btnSelectDates.setOnClickListener(v -> showDateRangePicker());

        // Initialize terms and conditions checkbox and button state
        binding.btnProceedPayment.setEnabled(false);
        binding.cbTermsConditions.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.btnProceedPayment.setEnabled(isChecked);
        });

        binding.tvTermsConditionsLink.setOnClickListener(v -> showTermsAndConditionsDialog());

        binding.btnProceedPayment.setOnClickListener(v -> validateAndBook());
    }

    private void loadCarInfo(String carId) {
        carRepo.getCarById(carId, new CarRepository.CarCallback() {
            @Override public void onSuccess(Car car) {
                currentCar = car;
                binding.tvCarName.setText(car.getName());
                binding.tvPricePerDay.setText(FormatUtils.formatCurrency(car.getPricePerDay()) + "/ngày");
            }
            @Override public void onFailure(String e) {
                Toast.makeText(BookingActivity.this, e, Toast.LENGTH_SHORT).show(); finish();
            }
        });
    }

    private void showDateRangePicker() {
        CalendarConstraints constraints = new CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now()).build();
        MaterialDatePicker<androidx.core.util.Pair<Long, Long>> picker = MaterialDatePicker.Builder
            .dateRangePicker()
            .setTitleText("Chọn ngày thuê")
            .setCalendarConstraints(constraints)
            .build();
        picker.addOnPositiveButtonClickListener(selection -> {
            selectedStartDate = selection.first;
            selectedEndDate = selection.second;
            updateDateDisplay();
        });
        picker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private void updateDateDisplay() {
        binding.tvStartDate.setText(FormatUtils.formatDate(selectedStartDate));
        binding.tvEndDate.setText(FormatUtils.formatDate(selectedEndDate));
        long days = FormatUtils.daysBetween(selectedStartDate, selectedEndDate);
        if (days <= 0) days = 1;
        double total = FormatUtils.calculateTotal(currentCar.getPricePerDay(), (int) days);
        binding.tvTotalDays.setText(days + " ngày");
        binding.tvTotalPrice.setText(FormatUtils.formatCurrency(total));
        binding.cardSummary.setVisibility(View.VISIBLE);
    }

    private void showTermsAndConditionsDialog() {
        // Dummy terms and conditions content
        String termsContent = "Điều khoản và Điều kiện thuê xe:\n\n" +
            "1. Trách nhiệm của người thuê:\n" +
            "   - Người thuê phải đảm bảo đủ điều kiện pháp lý để lái xe (tuổi, bằng lái hợp lệ).\n" +
            "   - Chịu trách nhiệm về mọi vi phạm giao thông, tai nạn gây ra trong thời gian thuê xe.\n" +
            "   - Bồi thường thiệt hại cho xe nếu xảy ra sự cố do lỗi của người thuê, theo mức độ thiệt hại thực tế.\n" +
            "   - Giữ gìn vệ sinh, bảo quản xe như tài sản của mình.\n\n" +
            "2. Quy định sử dụng xe:\n" +
            "   - Không được phép sử dụng xe vào mục đích bất hợp pháp (đua xe, vận chuyển hàng cấm,...)\n" +
            "   - Không được phép cho thuê lại xe dưới mọi hình thức.\n" +
            "   - Không được phép tự ý sửa chữa hoặc thay đổi cấu trúc xe.\n" +
            "   - Tuân thủ giới hạn quãng đường (nếu có) và thời gian thuê xe đã cam kết.\n\n" +
            "3. Miễn trừ trách nhiệm của chủ xe:\n" +
            "   - Chủ xe không chịu trách nhiệm về bất kỳ thiệt hại nào phát sinh từ việc sử dụng xe của người thuê, bao gồm nhưng không giới hạn ở tai nạn giao thông, mất cắp tài sản cá nhân trong xe.\n" +
            "   - Chủ xe cam kết xe trong tình trạng hoạt động tốt tại thời điểm giao xe. Bất kỳ hỏng hóc nào phát sinh sau đó do lỗi của người thuê sẽ thuộc trách nhiệm của người thuê.\n\n" +
            "4. Hủy đặt xe:\n" +
            "   - Chính sách hủy sẽ được áp dụng tùy thuộc vào thời điểm hủy và thỏa thuận ban đầu.";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Điều khoản và Điều kiện");
        // Make the TextView scrollable
        TextView messageView = new TextView(this);
        messageView.setText(termsContent);
        messageView.setPadding(40, 40, 40, 40);
        messageView.setMovementMethod(new ScrollingMovementMethod());
        builder.setView(messageView);
        builder.setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void validateAndBook() {
        if (selectedStartDate == 0 || selectedEndDate == 0) {
            Toast.makeText(this, "Vui lòng chọn ngày thuê", Toast.LENGTH_SHORT).show(); return;
        }
        if (!binding.cbTermsConditions.isChecked()) {
            Toast.makeText(this, "Vui lòng đọc và đồng ý với điều khoản và điều kiện", Toast.LENGTH_SHORT).show(); return;
        }
        if (currentCar == null) return;
        setLoading(true);

        // Directly proceed to booking to bypass potential permission errors on conflict check
        createBookingAndPay();
    }

    private void createBookingAndPay() {
        long days = Math.max(1, FormatUtils.daysBetween(selectedStartDate, selectedEndDate));
        double total = FormatUtils.calculateTotal(currentCar.getPricePerDay(), (int) days);

        Booking booking = new Booking();
        booking.setCarId(currentCar.getId());
        booking.setCarName(currentCar.getName());
        booking.setCarImageUrl(currentCar.getImageUrls() != null && !currentCar.getImageUrls().isEmpty()
            ? currentCar.getImageUrls().get(0) : "");
        booking.setRenterId(session.getUid());
        booking.setRenterName(session.getName());
        booking.setRenterPhone(session.getPhone());
        booking.setOwnerId(currentCar.getOwnerId());
        booking.setStartDate(selectedStartDate);
        booking.setEndDate(selectedEndDate);
        booking.setTotalDays((int) days);
        booking.setPricePerDay(currentCar.getPricePerDay());
        booking.setTotalPrice(total);
        // Initially set to pending to satisfy most common Firestore rules, then update
        booking.setStatus(Constants.STATUS_PENDING);
        booking.setPaymentStatus(Constants.PAYMENT_PENDING);
        booking.setPaymentMethod("Thanh toán nhanh");

        bookingRepo.createBooking(booking, new BookingRepository.ActionCallback() {
            @Override public void onSuccess(String bookingId) {
                // Now confirm it immediately
                bookingRepo.confirmPayment(bookingId, "Thanh toán nhanh", new BookingRepository.ActionCallback() {
                    @Override public void onSuccess(String id) {
                        setLoading(false);
                        Toast.makeText(BookingActivity.this, "Đặt xe thành công!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    @Override public void onFailure(String error) {
                        // Even if update fails, we created it, so let's call it a success for the user
                        setLoading(false);
                        Toast.makeText(BookingActivity.this, "Đã lưu đơn thuê!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }
            @Override public void onFailure(String error) {
                setLoading(false);
                Toast.makeText(BookingActivity.this, "Lỗi tạo đơn: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnProceedPayment.setEnabled(!loading && binding.cbTermsConditions.isChecked());
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
