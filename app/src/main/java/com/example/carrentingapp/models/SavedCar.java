package com.example.carrentingapp.models;

public class SavedCar {
    private String id, carId, userId;
    private long savedAt;

    public SavedCar() {}
    public SavedCar(String carId, String userId) {
        this.carId = carId; this.userId = userId;
        this.savedAt = System.currentTimeMillis();
    }
    public String getId() { return id; } public void setId(String v) { id = v; }
    public String getCarId() { return carId; } public void setCarId(String v) { carId = v; }
    public String getUserId() { return userId; } public void setUserId(String v) { userId = v; }
    public long getSavedAt() { return savedAt; } public void setSavedAt(long v) { savedAt = v; }
}
