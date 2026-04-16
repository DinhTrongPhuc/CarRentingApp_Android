package com.example.carrentingapp.fragments;

import android.os.Bundle;
import android.view.*;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.carrentingapp.adapters.CarAdapter;
import com.example.carrentingapp.databinding.FragmentSavedBinding;
import com.example.carrentingapp.models.Car;
import com.example.carrentingapp.repositories.CarRepository;
import com.example.carrentingapp.repositories.SavedCarRepository;
import com.example.carrentingapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class SavedFragment extends Fragment {
    private FragmentSavedBinding binding;
    private final SavedCarRepository savedRepo = new SavedCarRepository();
    private final CarRepository carRepo = new CarRepository();
    private CarAdapter adapter;
    private final List<Car> carList = new ArrayList<>();

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle state) {
        binding = FragmentSavedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new CarAdapter(requireContext(), carList);
        binding.rvSavedCars.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSavedCars.setAdapter(adapter);
        loadSavedCars();
    }

    private void loadSavedCars() {
        String userId = new SessionManager(requireContext()).getUid();
        binding.progressBar.setVisibility(View.VISIBLE);
        // FR7.3
        savedRepo.getSavedCarIds(userId, new SavedCarRepository.ListCallback() {
            @Override public void onSuccess(List<String> ids) {
                carList.clear();
                if (ids.isEmpty()) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.tvEmpty.setVisibility(View.VISIBLE);
                    return;
                }
                final int[] loaded = {0};
                for (String id : ids) {
                    carRepo.getCarById(id, new CarRepository.CarCallback() {
                        @Override public void onSuccess(Car car) {
                            carList.add(car); loaded[0]++;
                            if (loaded[0] == ids.size()) {
                                binding.progressBar.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            }
                        }
                        @Override public void onFailure(String error) {
                            loaded[0]++;
                            if (loaded[0] == ids.size()) {
                                binding.progressBar.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
            @Override public void onFailure(String error) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override public void onDestroyView() { super.onDestroyView(); binding = null; }
}
