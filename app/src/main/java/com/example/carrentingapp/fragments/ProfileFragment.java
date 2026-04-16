package com.example.carrentingapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.carrentingapp.R;
import com.example.carrentingapp.adapters.BookingAdapter;
import com.example.carrentingapp.databinding.FragmentProfileBinding;
import com.example.carrentingapp.activities.LoginActivity;
import com.example.carrentingapp.models.Booking;
import com.example.carrentingapp.repositories.AuthRepository;
import com.example.carrentingapp.repositories.BookingRepository;
import com.example.carrentingapp.utils.SessionManager;
import com.example.carrentingapp.utils.ValidationUtils;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private SessionManager session;
    private final AuthRepository authRepo = new AuthRepository();
    private final BookingRepository bookingRepo = new BookingRepository();
    private BookingAdapter bookingAdapter;
    private final List<Booking> bookingList = new ArrayList<>();

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle state) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        session = new SessionManager(requireContext());

        binding.tvName.setText(session.getName());
        binding.tvEmail.setText(session.getEmail());
        binding.tvPhone.setText(session.getPhone());
        binding.tvRole.setText(session.isOwner() ? "Chủ xe" : "Người thuê");

        bookingAdapter = new BookingAdapter(requireContext(), bookingList);
        binding.rvBookings.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvBookings.setAdapter(bookingAdapter);

        // FR8.7 - Load booking history
        loadBookings();

        binding.btnLogout.setOnClickListener(v -> {
            authRepo.logout();
            session.clearSession();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finishAffinity();
        });

        binding.btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
    }

    private void showChangePasswordDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_change_password, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create();

        TextInputEditText etNew = dialogView.findViewById(R.id.et_new_password);
        TextInputEditText etConfirm = dialogView.findViewById(R.id.et_confirm_password);
        TextInputLayout tilNew = dialogView.findViewById(R.id.til_new_password);
        TextInputLayout tilConfirm = dialogView.findViewById(R.id.til_confirm_password);

        dialogView.findViewById(R.id.btn_cancel).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btn_save).setOnClickListener(v -> {
            String newPwd = etNew.getText().toString().trim();
            String confirmPwd = etConfirm.getText().toString().trim();

            if (!ValidationUtils.isValidPassword(newPwd)) {
                tilNew.setError("Mật khẩu tối thiểu 6 ký tự"); return;
            } else tilNew.setError(null);

            if (!newPwd.equals(confirmPwd)) {
                tilConfirm.setError("Mật khẩu không khớp"); return;
            } else tilConfirm.setError(null);

            authRepo.updatePassword(newPwd, new AuthRepository.SimpleCallback() {
                @Override public void onSuccess() {
                    dialog.dismiss();
                    Toast.makeText(requireContext(), "Đã cập nhật mật khẩu", Toast.LENGTH_SHORT).show();
                }
                @Override public void onFailure(String errorMsg) {
                    Toast.makeText(requireContext(), "Lỗi: " + errorMsg, Toast.LENGTH_LONG).show();
                }
            });
        });

        dialog.show();
    }

    private void loadBookings() {
        bookingRepo.getBookingsByRenter(session.getUid(), new BookingRepository.BookingListCallback() {
            @Override public void onSuccess(List<Booking> bookings) {
                bookingList.clear(); bookingList.addAll(bookings);
                bookingAdapter.notifyDataSetChanged();
                binding.tvNoBookings.setVisibility(bookings.isEmpty() ? View.VISIBLE : View.GONE);
            }
            @Override public void onFailure(String error) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override public void onDestroyView() { super.onDestroyView(); binding = null; }
}
