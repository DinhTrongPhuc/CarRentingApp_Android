package com.example.carrentingapp.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.carrentingapp.R;
import com.example.carrentingapp.databinding.ActivityMainBinding;
import com.example.carrentingapp.fragments.HomeFragment;
import com.example.carrentingapp.fragments.SearchFragment;
import com.example.carrentingapp.fragments.SavedFragment;
import com.example.carrentingapp.fragments.ManageCarsFragment;
import com.example.carrentingapp.fragments.ProfileFragment;
import com.example.carrentingapp.utils.Constants;
import com.example.carrentingapp.utils.SessionManager;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        session = new SessionManager(this);

        loadFragment(new HomeFragment());
        setupBottomNav();
    }

    private void setupBottomNav() {
        binding.bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int id = item.getItemId();
            if (id == R.id.nav_home) fragment = new HomeFragment();
            else if (id == R.id.nav_search) fragment = new SearchFragment();
            else if (id == R.id.nav_saved) fragment = new SavedFragment();
            else if (id == R.id.nav_manage) fragment = session.isOwner() ? new ManageCarsFragment() : new SavedFragment();
            else if (id == R.id.nav_profile) fragment = new ProfileFragment();
            if (fragment != null) { loadFragment(fragment); return true; }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, fragment).commit();
    }
}
