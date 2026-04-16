package com.example.carrentingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.carrentingapp.databinding.ActivityRegisterBinding;
import com.example.carrentingapp.models.User;
import com.example.carrentingapp.repositories.AuthRepository;
import com.example.carrentingapp.utils.Constants;
import com.example.carrentingapp.utils.SessionManager;
import com.example.carrentingapp.utils.ValidationUtils;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private final AuthRepository authRepo = new AuthRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnRegister.setOnClickListener(v -> doRegister());
        binding.tvGoLogin.setOnClickListener(v -> finish());
    }

    private void doRegister() {
        String fullName = binding.etFullName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPwd = binding.etConfirmPassword.getText().toString().trim();
        String role = binding.rbOwner.isChecked() ? Constants.ROLE_OWNER : Constants.ROLE_RENTER;

        boolean valid = true;
        if (!ValidationUtils.isNotEmpty(fullName)) { binding.tilFullName.setError("Vui lòng nhập họ tên"); valid = false; }
        else binding.tilFullName.setError(null);
        if (!ValidationUtils.isValidEmail(email)) { binding.tilEmail.setError("Email không hợp lệ"); valid = false; }
        else binding.tilEmail.setError(null);
        if (!ValidationUtils.isValidPhone(phone)) { binding.tilPhone.setError("Số điện thoại không hợp lệ"); valid = false; }
        else binding.tilPhone.setError(null);
        if (!ValidationUtils.isValidPassword(password)) { binding.tilPassword.setError("Mật khẩu tối thiểu 6 ký tự"); valid = false; }
        else binding.tilPassword.setError(null);
        if (!password.equals(confirmPwd)) { binding.tilConfirmPassword.setError("Mật khẩu không khớp"); valid = false; }
        else binding.tilConfirmPassword.setError(null);
        if (!valid) return;

        setLoading(true);
        authRepo.register(email, password, fullName, phone, role, new AuthRepository.AuthCallback() {
            @Override public void onSuccess(User user) {
                new SessionManager(RegisterActivity.this)
                    .saveSession(user.getUid(), user.getRole(), user.getFullName(), user.getEmail(), user.getPhone());
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finishAffinity();
            }
            @Override public void onFailure(String errorMsg) {
                setLoading(false);
                // FR1.5
                Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnRegister.setEnabled(!loading);
    }
}
