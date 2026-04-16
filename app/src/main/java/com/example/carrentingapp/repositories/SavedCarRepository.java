package com.example.carrentingapp.repositories;

import com.example.carrentingapp.models.SavedCar;
import com.example.carrentingapp.utils.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SavedCarRepository {
    public interface Callback { void onResult(); }
    public interface ListCallback {
        void onSuccess(List<String> carIds);
        void onFailure(String error);
    }

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // FR7.1 - Save car
    public void saveCar(String carId, String userId, Callback cb) {
        SavedCar saved = new SavedCar(carId, userId);
        db.collection(Constants.COLLECTION_SAVED_CARS)
            .whereEqualTo("carId", carId)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener(snap -> {
                if (snap.isEmpty()) {
                    db.collection(Constants.COLLECTION_SAVED_CARS).add(saved)
                        .addOnSuccessListener(v -> cb.onResult());
                }
            });
    }

    // FR7.2 - Unsave car
    public void unsaveCar(String carId, String userId, Callback cb) {
        db.collection(Constants.COLLECTION_SAVED_CARS)
            .whereEqualTo("carId", carId)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener(snap -> {
                for (QueryDocumentSnapshot d : snap) d.getReference().delete();
                cb.onResult();
            });
    }

    // FR7.3 - Get saved car IDs
    public void getSavedCarIds(String userId, ListCallback cb) {
        db.collection(Constants.COLLECTION_SAVED_CARS)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener(snap -> {
                List<String> ids = new ArrayList<>();
                for (QueryDocumentSnapshot d : snap) ids.add(d.getString("carId"));
                cb.onSuccess(ids);
            })
            .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    // Check if saved
    public void isCarSaved(String carId, String userId, java.util.function.Consumer<Boolean> cb) {
        db.collection(Constants.COLLECTION_SAVED_CARS)
            .whereEqualTo("carId", carId)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener(snap -> cb.accept(!snap.isEmpty()));
    }
}
