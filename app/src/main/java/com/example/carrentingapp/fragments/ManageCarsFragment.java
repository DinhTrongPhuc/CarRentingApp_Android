package com.example.carrentingapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.carrentingapp.R;
import com.example.carrentingapp.activities.EditCarActivity;
import com.example.carrentingapp.adapters.ManageCarAdapter;
import com.example.carrentingapp.databinding.FragmentManageCarsBinding;
import com.example.carrentingapp.models.Booking;
import com.example.carrentingapp.models.Car;
import com.example.carrentingapp.repositories.BookingRepository;
import com.example.carrentingapp.repositories.CarRepository;
import com.example.carrentingapp.utils.Constants;
import com.example.carrentingapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ManageCarsFragment extends Fragment implements ManageCarAdapter.OnCarActionListener {
    private FragmentManageCarsBinding binding;
    private final CarRepository carRepo = new CarRepository();
    private final BookingRepository bookingRepo = new BookingRepository();
    private ManageCarAdapter adapter;
    private final List<Car> carList = new ArrayList<>();
    private SessionManager session;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle state) {
        binding = FragmentManageCarsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        session = new SessionManager(requireContext());
        adapter = new ManageCarAdapter(requireContext(), carList, this);
        binding.rvCars.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCars.setAdapter(adapter);
        loadMyCars();

        // FR8.8 - Show bookings
        binding.btnViewBookings.setOnClickListener(v -> loadOwnerBookings());
    }

    private void loadMyCars() {
        binding.progressBar.setVisibility(View.VISIBLE);
        // FR6.1
        carRepo.getCarsByOwner(session.getUid(), new CarRepository.CarListCallback() {
            @Override public void onSuccess(List<Car> cars) {
                binding.progressBar.setVisibility(View.GONE);
                carList.clear(); carList.addAll(cars);
                adapter.notifyDataSetChanged();
                binding.tvEmpty.setVisibility(cars.isEmpty() ? View.VISIBLE : View.GONE);
            }
            @Override public void onFailure(String error) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadOwnerBookings() {
        bookingRepo.getBookingsByOwner(session.getUid(), new BookingRepository.BookingListCallback() {
            @Override public void onSuccess(List<Booking> bookings) {
                Toast.makeText(requireContext(), "Có " + bookings.size() + " đơn thuê", Toast.LENGTH_SHORT).show();
            }
            @Override public void onFailure(String error) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // FR6.2 - Edit
    @Override public void onEdit(Car car) {
        Intent intent = new Intent(requireContext(), EditCarActivity.class);
        intent.putExtra(Constants.KEY_CAR_ID, car.getId());
        startActivity(intent);
    }

    // FR6.5 - Delete with confirm
    @Override public void onDelete(Car car) {
        new AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc muốn xóa xe " + car.getName() + "?")
            .setPositiveButton("Xóa", (d, w) -> {
                // FR6.6
                carRepo.deleteCar(car.getId(), new CarRepository.ActionCallback() {
                    @Override public void onSuccess() {
                        carList.remove(car);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(requireContext(), "Đã xóa thành công", Toast.LENGTH_SHORT).show();
                    }
                    @Override public void onFailure(String error) {
                        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                    }
                });
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    @Override public void onDestroyView() { super.onDestroyView(); binding = null; }
}
