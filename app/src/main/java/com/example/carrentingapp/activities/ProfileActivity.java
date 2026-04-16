package com.example.carrentingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.carrentingapp.databinding.ActivityProfileBinding;
import com.example.carrentingapp.repositories.AuthRepository;
import com.example.carrentingapp.utils.SessionManager;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private SessionManager session;
    private final AuthRepository authRepo = new AuthRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        session = new SessionManager(this);

        binding.tvName.setText(session.getName());
        binding.tvEmail.setText(session.getEmail());
        binding.tvPhone.setText(session.getPhone());
        binding.tvRole.setText(session.isOwner() ? "Chủ xe" : "Người thuê");

        binding.btnLogout.setOnClickListener(v -> {
            authRepo.logout();
            session.clearSession();
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity();
        });
    }
}
