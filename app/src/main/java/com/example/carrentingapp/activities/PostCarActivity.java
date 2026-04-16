package com.example.carrentingapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.carrentingapp.R;
import com.example.carrentingapp.adapters.SelectedImageAdapter;
import com.example.carrentingapp.databinding.ActivityPostCarBinding;
import com.example.carrentingapp.models.Car;
import com.example.carrentingapp.repositories.CarRepository;
import com.example.carrentingapp.repositories.StorageRepository;
import com.example.carrentingapp.utils.Constants;
import com.example.carrentingapp.utils.SessionManager;
import com.example.carrentingapp.utils.ValidationUtils;

import java.util.ArrayList;
import java.util.List;

public class PostCarActivity extends AppCompatActivity {
    private ActivityPostCarBinding binding;
    private final CarRepository carRepo = new CarRepository();
    private final StorageRepository storageRepo = new StorageRepository();
    private SessionManager session;
    private List<Uri> selectedImages = new ArrayList<>();
    private SelectedImageAdapter imageAdapter;

    private final ActivityResultLauncher<Intent> imagePicker = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                if (result.getData().getClipData() != null) {
                    int count = Math.min(result.getData().getClipData().getItemCount(), Constants.MAX_IMAGES);
                    for (int i = 0; i < count; i++)
                        selectedImages.add(result.getData().getClipData().getItemAt(i).getUri());
                } else if (result.getData().getData() != null) {
                    selectedImages.add(result.getData().getData());
                }
                imageAdapter.notifyDataSetChanged();
            }
        }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostCarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        session = new SessionManager(this);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupSpinners();
        setupImagePicker();

        binding.btnPost.setOnClickListener(v -> validateAndPost());
    }

    private void setupSpinners() {
        String[] types = Constants.getAllCarTypes();
        binding.spinnerType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types));

        String[] conditions = {Constants.CONDITION_NEW, Constants.CONDITION_GOOD, Constants.CONDITION_FAIR};
        binding.spinnerCondition.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, conditions));
    }

    private void setupImagePicker() {
        imageAdapter = new SelectedImageAdapter(this, selectedImages);
        binding.rvImages.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
        binding.rvImages.setAdapter(imageAdapter);
        binding.btnAddImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            imagePicker.launch(intent);
        });
    }

    private void validateAndPost() {
        String name = binding.etName.getText().toString().trim();
        String brand = binding.etBrand.getText().toString().trim();
        String licensePlate = binding.etLicensePlate.getText().toString().trim();
        String location = binding.etLocation.getText().toString().trim();
        String priceStr = binding.etPrice.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();
        String latStr = binding.etLatitude.getText().toString().trim();
        String lngStr = binding.etLongitude.getText().toString().trim();

        boolean valid = true;
        // FR2.1 - Required field validation
        if (!ValidationUtils.isNotEmpty(name)) { binding.tilName.setError("Vui lòng nhập tên xe"); valid = false; } else binding.tilName.setError(null);
        if (!ValidationUtils.isNotEmpty(brand)) { binding.tilBrand.setError("Vui lòng nhập hãng xe"); valid = false; } else binding.tilBrand.setError(null);
        if (!ValidationUtils.isNotEmpty(licensePlate)) { binding.tilLicensePlate.setError("Vui lòng nhập biển số"); valid = false; } else binding.tilLicensePlate.setError(null);
        if (!ValidationUtils.isNotEmpty(location)) { binding.tilLocation.setError("Vui lòng nhập địa điểm"); valid = false; } else binding.tilLocation.setError(null);
        // FR2.2 - Price must be positive integer
        if (!ValidationUtils.isValidPrice(priceStr)) { binding.tilPrice.setError("Giá thuê phải là số dương"); valid = false; } else binding.tilPrice.setError(null);
        if (!valid) return;

        double price = Double.parseDouble(priceStr);
        // FR2.7 - Price warning
        if (price > Constants.PRICE_WARNING_THRESHOLD) {
            Toast.makeText(this, "Giá thuê có vẻ cao", Toast.LENGTH_SHORT).show();
        }

        setLoading(true);
        // Upload images first
        storageRepo.uploadImages(selectedImages, Constants.STORAGE_CAR_IMAGES + session.getUid() + "/",
            new StorageRepository.MultiUploadCallback() {
                @Override public void onSuccess(List<String> urls) {
                    Car car = new Car();
                    car.setName(name); car.setBrand(brand); car.setLicensePlate(licensePlate);
                    car.setLocation(location); car.setDescription(description);
                    car.setPricePerDay(price);
                    car.setType(binding.spinnerType.getSelectedItem().toString());
                    car.setCondition(binding.spinnerCondition.getSelectedItem().toString());
                    car.setImageUrls(urls);
                    car.setOwnerId(session.getUid());
                    car.setOwnerName(session.getName());
                    car.setOwnerPhone(session.getPhone());
                    if (!latStr.isEmpty() && !lngStr.isEmpty()) {
                        try {
                            car.setLatitude(Double.parseDouble(latStr));
                            car.setLongitude(Double.parseDouble(lngStr));
                        } catch (NumberFormatException ignored) {}
                    }

                    carRepo.addCar(car, new CarRepository.ActionCallback() {
                        @Override public void onSuccess() {
                            setLoading(false);
                            // FR2.4
                            Toast.makeText(PostCarActivity.this, "Đăng xe thành công", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        @Override public void onFailure(String error) {
                            setLoading(false);
                            Toast.makeText(PostCarActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                @Override public void onFailure(String error) {
                    setLoading(false);
                    // FR2.6
                    Toast.makeText(PostCarActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnPost.setEnabled(!loading);
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
