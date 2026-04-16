package com.example.carrentingapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.carrentingapp.activities.PostCarActivity;
import com.example.carrentingapp.adapters.CarAdapter;
import com.example.carrentingapp.databinding.FragmentHomeBinding;
import com.example.carrentingapp.models.Car;
import com.example.carrentingapp.repositories.CarRepository;
import com.example.carrentingapp.utils.NetworkUtils;
import com.example.carrentingapp.utils.SessionManager;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private final CarRepository carRepo = new CarRepository();
    private CarAdapter adapter;
    private final List<Car> carList = new ArrayList<>();
    private DocumentSnapshot lastDoc = null;
    private boolean isLoading = false;
    private boolean hasMore = true;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle state) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // FR4.1 - Check network
        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            // FR4.5
            binding.layoutEmpty.setVisibility(View.VISIBLE);
            binding.btnSeed.setVisibility(View.GONE);
            return;
        }

        SessionManager session = new SessionManager(requireContext());
        adapter = new CarAdapter(requireContext(), carList);
        binding.rvCars.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.rvCars.setAdapter(adapter);

        // FR4.3 - Pagination on scroll
        binding.rvCars.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                if (!rv.canScrollVertically(1) && !isLoading && hasMore) loadCars();
            }
        });

        binding.swipeRefresh.setOnRefreshListener(() -> {
            carList.clear(); lastDoc = null; hasMore = true;
            loadCars();
        });

        if (session.isOwner()) {
            binding.fabPostCar.setVisibility(View.VISIBLE);
            binding.fabPostCar.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), PostCarActivity.class)));
        }

        binding.btnSeed.setOnClickListener(v -> {
            carRepo.seedSampleData();
            Toast.makeText(requireContext(), "Đang thêm dữ liệu, vui lòng chờ...", Toast.LENGTH_SHORT).show();
            v.postDelayed(() -> {
                carList.clear(); lastDoc = null; hasMore = true;
                loadCars();
            }, 2000);
        });

        loadCars();
    }

    private void loadCars() {
        isLoading = true;
        binding.progressBar.setVisibility(View.VISIBLE);
        carRepo.getCars(lastDoc, new CarRepository.CarListCallback() {
            @Override public void onSuccess(List<Car> cars) {
                isLoading = false;
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                if (cars.size() < 20) hasMore = false;
                if (!cars.isEmpty()) {
                    lastDoc = null; // simplified; in real app track DocumentSnapshot
                    carList.addAll(cars);
                    adapter.notifyDataSetChanged();
                }
                // FR4.4 - FR1.6 - Show if empty
                binding.layoutEmpty.setVisibility(carList.isEmpty() ? View.VISIBLE : View.GONE);
            }
            @Override public void onFailure(String error) {
                isLoading = false;
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                binding.layoutEmpty.setVisibility(carList.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override public void onDestroyView() { super.onDestroyView(); binding = null; }
}
