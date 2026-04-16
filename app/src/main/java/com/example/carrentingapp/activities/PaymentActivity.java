package com.example.carrentingapp.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.carrentingapp.R;
import com.example.carrentingapp.databinding.ActivityPaymentBinding;
import com.example.carrentingapp.repositories.BookingRepository;
import com.example.carrentingapp.utils.Constants;

public class PaymentActivity extends AppCompatActivity {
    private ActivityPaymentBinding binding;
    private final BookingRepository bookingRepo = new BookingRepository();
    private String bookingId;
    private String selectedMethod = Constants.PAYMENT_MOMO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bookingId = getIntent().getStringExtra(Constants.KEY_BOOKING_ID);

        // FR8.4 - Payment method selection
        binding.rbMomo.setOnCheckedChangeListener((b, checked) -> { if (checked) selectedMethod = Constants.PAYMENT_MOMO; });
        binding.rbZaloPay.setOnCheckedChangeListener((b, checked) -> { if (checked) selectedMethod = Constants.PAYMENT_ZALOPAY; });
        binding.rbBank.setOnCheckedChangeListener((b, checked) -> { if (checked) selectedMethod = Constants.PAYMENT_BANK; });

        binding.btnConfirmPayment.setOnClickListener(v -> simulatePayment());
    }

    private void simulatePayment() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnConfirmPayment.setEnabled(false);
        binding.tvProcessing.setVisibility(View.VISIBLE);

        // Mock payment processing - simulate 2 second delay
        new Handler().postDelayed(() -> {
            // Simulate 90% success rate
            boolean success = Math.random() > 0.1;
            if (success) processPaymentSuccess();
            else processPaymentFailure();
        }, 2000);
    }

    private void processPaymentSuccess() {
        bookingRepo.confirmPayment(bookingId, selectedMethod, new BookingRepository.ActionCallback() {
            @Override public void onSuccess(String id) {
                binding.progressBar.setVisibility(View.GONE);
                binding.tvProcessing.setVisibility(View.GONE);
                binding.layoutSuccess.setVisibility(View.VISIBLE);
                binding.btnConfirmPayment.setVisibility(View.GONE);
                // FR8.5
                binding.btnDone.setOnClickListener(v -> {
                    finishAffinity();
                });
            }
            @Override public void onFailure(String error) {
                processPaymentFailure();
            }
        });
    }

    private void processPaymentFailure() {
        binding.progressBar.setVisibility(View.GONE);
        binding.tvProcessing.setVisibility(View.GONE);
        binding.btnConfirmPayment.setEnabled(true);
        // FR8.6
        Toast.makeText(this, "Thanh toán thất bại, vui lòng thử lại", Toast.LENGTH_LONG).show();
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
