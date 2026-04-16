package com.example.carrentingapp.repositories;

import com.example.carrentingapp.models.Booking;
import com.example.carrentingapp.utils.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BookingRepository {
    public interface BookingListCallback {
        void onSuccess(List<Booking> bookings);
        void onFailure(String error);
    }
    public interface ActionCallback {
        void onSuccess(String bookingId);
        void onFailure(String error);
    }
    public interface ConflictCallback {
        void onResult(boolean hasConflict);
        void onFailure(String error);
    }

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // FR8.2 - Check booking conflict
    public void checkConflict(String carId, long startDate, long endDate, ConflictCallback cb) {
        db.collection(Constants.COLLECTION_BOOKINGS)
            .whereEqualTo("carId", carId)
            .whereIn("status", List.of(Constants.STATUS_PENDING, Constants.STATUS_CONFIRMED))
            .get()
            .addOnSuccessListener(snap -> {
                boolean conflict = false;
                for (QueryDocumentSnapshot d : snap) {
                    Booking b = d.toObject(Booking.class);
                    // Overlap check: new period overlaps existing
                    if (startDate < b.getEndDate() && endDate > b.getStartDate()) {
                        conflict = true; break;
                    }
                }
                cb.onResult(conflict);
            })
            .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    // FR8.5 - Create booking
    public void createBooking(Booking booking, ActionCallback cb) {
        booking.setCreatedAt(System.currentTimeMillis());
        if (booking.getStatus() == null) booking.setStatus(Constants.STATUS_PENDING);
        if (booking.getPaymentStatus() == null) booking.setPaymentStatus(Constants.PAYMENT_PENDING);
        db.collection(Constants.COLLECTION_BOOKINGS).add(booking)
            .addOnSuccessListener(ref -> cb.onSuccess(ref.getId()))
            .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    // Confirm payment
    public void confirmPayment(String bookingId, String method, ActionCallback cb) {
        db.collection(Constants.COLLECTION_BOOKINGS).document(bookingId)
            .update("paymentStatus", Constants.PAYMENT_SUCCESS,
                    "paymentMethod", method,
                    "status", Constants.STATUS_CONFIRMED)
            .addOnSuccessListener(v -> cb.onSuccess(bookingId))
            .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    // FR8.7 - Renter booking history
    public void getBookingsByRenter(String renterId, BookingListCallback cb) {
        db.collection(Constants.COLLECTION_BOOKINGS)
            .whereEqualTo("renterId", renterId)
            .get()
            .addOnSuccessListener(snap -> {
                List<Booking> list = new ArrayList<>();
                for (QueryDocumentSnapshot d : snap) {
                    Booking b = d.toObject(Booking.class); b.setId(d.getId()); list.add(b);
                }
                // Client-side sort
                list.sort((b1, b2) -> Long.compare(b2.getCreatedAt(), b1.getCreatedAt()));
                cb.onSuccess(list);
            })
            .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    // FR8.8 - Owner: view bookings for their cars
    public void getBookingsByOwner(String ownerId, BookingListCallback cb) {
        db.collection(Constants.COLLECTION_BOOKINGS)
            .whereEqualTo("ownerId", ownerId)
            .get()
            .addOnSuccessListener(snap -> {
                List<String> carIds = new ArrayList<>();
                List<Booking> list = new ArrayList<>();
                for (QueryDocumentSnapshot d : snap) {
                    Booking b = d.toObject(Booking.class); b.setId(d.getId()); list.add(b);
                }
                // Client-side sort
                list.sort((b1, b2) -> Long.compare(b2.getCreatedAt(), b1.getCreatedAt()));
                cb.onSuccess(list);
            })
            .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }
}
