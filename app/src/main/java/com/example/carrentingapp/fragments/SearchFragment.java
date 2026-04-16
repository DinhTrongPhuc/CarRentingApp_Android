package com.example.carrentingapp.fragments;

import android.os.Bundle;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.carrentingapp.adapters.CarAdapter;
import com.example.carrentingapp.databinding.FragmentSearchBinding;
import com.example.carrentingapp.models.Car;
import com.example.carrentingapp.repositories.CarRepository;
import com.example.carrentingapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private FragmentSearchBinding binding;
    private final CarRepository carRepo = new CarRepository();
    private CarAdapter adapter;
    private final List<Car> carList = new ArrayList<>();

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle state) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new CarAdapter(requireContext(), carList);
        binding.rvCars.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCars.setAdapter(adapter);

        // FR3.5 - Filter by type
        String[] typesFromConstants = Constants.getAllCarTypes();
        String[] types = new String[typesFromConstants.length + 1];
        types[0] = "Tất cả";
        System.arraycopy(typesFromConstants, 0, types, 1, typesFromConstants.length);
        binding.spinnerType.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, types));

        binding.btnSearch.setOnClickListener(v -> doSearch());
        binding.etSearch.setOnEditorActionListener((tv, action, event) -> { doSearch(); return true; });
    }

    private void doSearch() {
        String query = binding.etSearch.getText().toString().trim();
        String type = binding.spinnerType.getSelectedItem().toString();
        if ("Tất cả".equals(type)) type = "";
        String minStr = binding.etMinPrice.getText().toString().trim();
        String maxStr = binding.etMaxPrice.getText().toString().trim();
        double min = minStr.isEmpty() ? 0 : Double.parseDouble(minStr);
        double max = maxStr.isEmpty() ? 0 : Double.parseDouble(maxStr);

        binding.progressBar.setVisibility(View.VISIBLE);
        final String finalType = type;
        // FR3.1
        carRepo.searchCars(query, finalType, "", min, max, new CarRepository.CarListCallback() {
            @Override public void onSuccess(List<Car> cars) {
                binding.progressBar.setVisibility(View.GONE);
                carList.clear();
                carList.addAll(cars);
                adapter.notifyDataSetChanged();
                // FR3.4
                binding.tvNoResult.setVisibility(cars.isEmpty() ? View.VISIBLE : View.GONE);
            }
            @Override public void onFailure(String error) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override public void onDestroyView() { super.onDestroyView(); binding = null; }
}
