package com.example.carrentingapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.carrentingapp.R;
import com.example.carrentingapp.adapters.ImageSliderAdapter;
import com.example.carrentingapp.databinding.ActivityCarDetailBinding;
import com.example.carrentingapp.models.Car;
import com.example.carrentingapp.repositories.CarRepository;
import com.example.carrentingapp.repositories.SavedCarRepository;
import com.example.carrentingapp.utils.Constants;
import com.example.carrentingapp.utils.FormatUtils;
import com.example.carrentingapp.utils.SessionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class CarDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ActivityCarDetailBinding binding;
    private final CarRepository carRepo = new CarRepository();
    private final SavedCarRepository savedRepo = new SavedCarRepository();
    private Car currentCar;
    private boolean isSaved = false;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCarDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        session = new SessionManager(this);

        String carId = getIntent().getStringExtra(Constants.KEY_CAR_ID);
        if (carId == null) { finish(); return; }

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadCarDetail(carId);
        checkSavedStatus(carId);

        // Init Google Maps
        SupportMapFragment mapFragment = (SupportMapFragment)
            getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);
    }

    private void loadCarDetail(String carId) {
        binding.progressBar.setVisibility(View.VISIBLE);
        // FR5.1
        carRepo.getCarById(carId, new CarRepository.CarCallback() {
            @Override public void onSuccess(Car car) {
                currentCar = car;
                binding.progressBar.setVisibility(View.GONE);
                populateUI(car);
            }
            @Override public void onFailure(String error) {
                binding.progressBar.setVisibility(View.GONE);
                // FR5.4
                Toast.makeText(CarDetailActivity.this, "Xe không còn tồn tại", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void populateUI(Car car) {
        binding.tvCarName.setText(car.getName());
        binding.tvPrice.setText(FormatUtils.formatCurrency(car.getPricePerDay()) + "/ngày");
        binding.tvLocation.setText(car.getLocation());
        binding.tvDescription.setText(car.getDescription());
        binding.tvBrand.setText(car.getBrand());
        binding.tvType.setText(car.getType());
        binding.tvCondition.setText(car.getCondition());
        binding.tvLicensePlate.setText(car.getLicensePlate());
        binding.tvOwnerName.setText(car.getOwnerName());

        // FR5.3 - Image slider
        if (car.getImageUrls() != null && !car.getImageUrls().isEmpty()) {
            ImageSliderAdapter adapter = new ImageSliderAdapter(this, car.getImageUrls());
            binding.viewPager.setAdapter(adapter);
            binding.dotsIndicator.attachTo(binding.viewPager);
        }

        // FR5.6 - Contact owner
        binding.btnContact.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + car.getOwnerPhone()));
            startActivity(intent);
        });

        // FR7.1 - Save/unsave
        binding.btnSave.setOnClickListener(v -> toggleSave(car.getId()));

        // FR8.1 - Booking
        if (session.isOwner() && car.getOwnerId().equals(session.getUid())) {
            binding.btnBook.setVisibility(View.GONE);
        } else {
            binding.btnBook.setOnClickListener(v -> {
                Intent intent = new Intent(this, BookingActivity.class);
                intent.putExtra(Constants.KEY_CAR_ID, car.getId());
                startActivity(intent);
            });
        }
    }

    private void checkSavedStatus(String carId) {
        savedRepo.isCarSaved(carId, session.getUid(), saved -> {
            isSaved = saved;
            binding.btnSave.setIconResource(saved ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark);
        });
    }

    private void toggleSave(String carId) {
        if (isSaved) {
            savedRepo.unsaveCar(carId, session.getUid(), () -> {
                isSaved = false;
                binding.btnSave.setIconResource(R.drawable.ic_bookmark);
                Toast.makeText(this, "Đã bỏ lưu", Toast.LENGTH_SHORT).show();
            });
        } else {
            savedRepo.saveCar(carId, session.getUid(), () -> {
                isSaved = true;
                binding.btnSave.setIconResource(R.drawable.ic_bookmark_filled);
                Toast.makeText(this, "Đã lưu xe", Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // FR5.7 - Show pickup location on map
        if (currentCar != null && currentCar.getLatitude() != 0) {
            LatLng location = new LatLng(currentCar.getLatitude(), currentCar.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(location).title("Địa điểm nhận xe"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        }
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
