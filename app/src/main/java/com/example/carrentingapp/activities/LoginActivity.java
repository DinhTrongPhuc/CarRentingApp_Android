package com.example.carrentingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.carrentingapp.databinding.ActivityLoginBinding;
import com.example.carrentingapp.models.User;
import com.example.carrentingapp.repositories.AuthRepository;
import com.example.carrentingapp.utils.SessionManager;
import com.example.carrentingapp.utils.ValidationUtils;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private final AuthRepository authRepo = new AuthRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLogin.setOnClickListener(v -> doLogin());
        binding.tvGoRegister.setOnClickListener(v ->
            startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void doLogin() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        // FR1.1
        if (!ValidationUtils.isValidEmail(email)) {
            binding.tilEmail.setError("Email không hợp lệ"); return;
        } else binding.tilEmail.setError(null);

        // FR1.2
        if (!ValidationUtils.isValidPassword(password)) {
            binding.tilPassword.setError("Mật khẩu tối thiểu 6 ký tự"); return;
        } else binding.tilPassword.setError(null);

        setLoading(true);
        // FR1.3
        authRepo.login(email, password, new AuthRepository.AuthCallback() {
            @Override public void onSuccess(User user) {
                new SessionManager(LoginActivity.this)
                    .saveSession(user.getUid(), user.getRole(), user.getFullName(), user.getEmail(), user.getPhone());
                // FR1.4
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
            @Override public void onFailure(String errorMsg) {
                setLoading(false);
                Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnLogin.setEnabled(!loading);
    }
}
