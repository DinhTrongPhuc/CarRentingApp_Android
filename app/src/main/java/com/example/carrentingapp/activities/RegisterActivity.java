package com.example.carrentingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
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

        // Initial visibility for renter-specific fields
        toggleRenterFields(binding.rbRenter.isChecked());

        binding.rgRole.setOnCheckedChangeListener((group, checkedId) -> {
            toggleRenterFields(checkedId == binding.rbRenter.getId());
        });

        binding.btnRegister.setOnClickListener(v -> doRegister());
        binding.tvGoLogin.setOnClickListener(v -> finish());
    }

    private void toggleRenterFields(boolean isRenter) {
        int visibility = isRenter ? View.VISIBLE : View.GONE;
        binding.tilAge.setVisibility(visibility);
        binding.tilDriverLicense.setVisibility(visibility);
        binding.tilLicenseExpiration.setVisibility(visibility);
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

        int age = 0;
        String driverLicense = "";
        String licenseExpiration = "";

        if (role.equals(Constants.ROLE_RENTER)) {
            String ageStr = binding.etAge.getText().toString().trim();
            driverLicense = binding.etDriverLicense.getText().toString().trim();
            licenseExpiration = binding.etLicenseExpiration.getText().toString().trim();

            if (!ValidationUtils.isNotEmpty(ageStr)) { binding.tilAge.setError("Vui lòng nhập tuổi"); valid = false; }
            else {
                try { age = Integer.parseInt(ageStr); binding.tilAge.setError(null); } catch (NumberFormatException e) { binding.tilAge.setError("Tuổi không hợp lệ"); valid = false; }
            }
            if (!ValidationUtils.isNotEmpty(driverLicense)) { binding.tilDriverLicense.setError("Vui lòng nhập số bằng lái"); valid = false; }
            else binding.tilDriverLicense.setError(null);
            if (!ValidationUtils.isNotEmpty(licenseExpiration)) { binding.tilLicenseExpiration.setError("Vui lòng nhập hạn bằng lái"); valid = false; }
            else binding.tilLicenseExpiration.setError(null);
        }

        if (!valid) return;

        setLoading(true);
        authRepo.register(email, password, fullName, phone, role, age, driverLicense, licenseExpiration, new AuthRepository.AuthCallback() {
            @Override public void onSuccess(User user) {
                new SessionManager(RegisterActivity.this)
                    .saveSession(user.getUid(), user.getRole(), user.getFullName(), user.getEmail(), user.getPhone(), user.getAge(), user.getDriverLicense(), user.getLicenseExpirationDate());
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finishAffinity();
            }
            @Override public void onFailure(String errorMsg) {
                setLoading(false);
                Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnRegister.setEnabled(!loading);
    }
}
