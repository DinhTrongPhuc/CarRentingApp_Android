package com.example.carrentingapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.carrentingapp.R;
import com.example.carrentingapp.databinding.ActivityEditCarBinding;
import com.example.carrentingapp.models.Car;
import com.example.carrentingapp.repositories.CarRepository;
import com.example.carrentingapp.utils.Constants;
import com.example.carrentingapp.utils.ValidationUtils;

public class EditCarActivity extends AppCompatActivity {
    private ActivityEditCarBinding binding;
    private final CarRepository carRepo = new CarRepository();
    private Car currentCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditCarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String carId = getIntent().getStringExtra(Constants.KEY_CAR_ID);
        if (carId == null) { finish(); return; }

        setupSpinners();
        loadCar(carId);
        binding.btnUpdate.setOnClickListener(v -> validateAndUpdate());
    }

    private void setupSpinners() {
        String[] types = {Constants.TYPE_MOTORBIKE, Constants.TYPE_CAR, Constants.TYPE_ELECTRIC, Constants.TYPE_TRUCK};
        binding.spinnerType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types));
        String[] conditions = {Constants.CONDITION_NEW, Constants.CONDITION_GOOD, Constants.CONDITION_FAIR};
        binding.spinnerCondition.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, conditions));
    }

    private void loadCar(String carId) {
        binding.progressBar.setVisibility(View.VISIBLE);
        carRepo.getCarById(carId, new CarRepository.CarCallback() {
            @Override public void onSuccess(Car car) {
                currentCar = car;
                binding.progressBar.setVisibility(View.GONE);
                binding.etName.setText(car.getName());
                binding.etBrand.setText(car.getBrand());
                binding.etLicensePlate.setText(car.getLicensePlate());
                binding.etLocation.setText(car.getLocation());
                binding.etPrice.setText(String.valueOf((long) car.getPricePerDay()));
                binding.etDescription.setText(car.getDescription());
            }
            @Override public void onFailure(String error) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(EditCarActivity.this, error, Toast.LENGTH_SHORT).show(); finish();
            }
        });
    }

    private void validateAndUpdate() {
        if (currentCar == null) return;
        String name = binding.etName.getText().toString().trim();
        String brand = binding.etBrand.getText().toString().trim();
        String licensePlate = binding.etLicensePlate.getText().toString().trim();
        String location = binding.etLocation.getText().toString().trim();
        String priceStr = binding.etPrice.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();

        boolean valid = true;
        if (!ValidationUtils.isNotEmpty(name)) { binding.tilName.setError("Bắt buộc"); valid = false; } else binding.tilName.setError(null);
        if (!ValidationUtils.isNotEmpty(brand)) { binding.tilBrand.setError("Bắt buộc"); valid = false; } else binding.tilBrand.setError(null);
        if (!ValidationUtils.isNotEmpty(licensePlate)) { binding.tilLicensePlate.setError("Bắt buộc"); valid = false; } else binding.tilLicensePlate.setError(null);
        if (!ValidationUtils.isNotEmpty(location)) { binding.tilLocation.setError("Bắt buộc"); valid = false; } else binding.tilLocation.setError(null);
        if (!ValidationUtils.isValidPrice(priceStr)) { binding.tilPrice.setError("Giá không hợp lệ"); valid = false; } else binding.tilPrice.setError(null);
        if (!valid) return;

        currentCar.setName(name); currentCar.setBrand(brand);
        currentCar.setLicensePlate(licensePlate); currentCar.setLocation(location);
        currentCar.setPricePerDay(Double.parseDouble(priceStr));
        currentCar.setDescription(description);
        currentCar.setType(binding.spinnerType.getSelectedItem().toString());
        currentCar.setCondition(binding.spinnerCondition.getSelectedItem().toString());

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnUpdate.setEnabled(false);
        carRepo.updateCar(currentCar, new CarRepository.ActionCallback() {
            @Override public void onSuccess() {
                binding.progressBar.setVisibility(View.GONE);
                // FR6.3
                Toast.makeText(EditCarActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                finish();
            }
            @Override public void onFailure(String error) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnUpdate.setEnabled(true);
                // FR6.4
                Toast.makeText(EditCarActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
