package com.example.carrentingapp.models;

import java.util.List;

public class Car {
    private String id, name, brand, type, licensePlate, location, description;
    private String ownerId, ownerName, ownerPhone, condition;
    private double pricePerDay, latitude, longitude;
    private List<String> imageUrls;
    private boolean available;
    private long createdAt;

    public Car() {}

    public String getId() { return id; } public void setId(String v) { id = v; }
    public String getName() { return name; } public void setName(String v) { name = v; }
    public String getBrand() { return brand; } public void setBrand(String v) { brand = v; }
    public String getType() { return type; } public void setType(String v) { type = v; }
    public String getLicensePlate() { return licensePlate; } public void setLicensePlate(String v) { licensePlate = v; }
    public String getLocation() { return location; } public void setLocation(String v) { location = v; }
    public String getDescription() { return description; } public void setDescription(String v) { description = v; }
    public String getOwnerId() { return ownerId; } public void setOwnerId(String v) { ownerId = v; }
    public String getOwnerName() { return ownerName; } public void setOwnerName(String v) { ownerName = v; }
    public String getOwnerPhone() { return ownerPhone; } public void setOwnerPhone(String v) { ownerPhone = v; }
    public String getCondition() { return condition; } public void setCondition(String v) { condition = v; }
    public double getPricePerDay() { return pricePerDay; } public void setPricePerDay(double v) { pricePerDay = v; }
    public double getLatitude() { return latitude; } public void setLatitude(double v) { latitude = v; }
    public double getLongitude() { return longitude; } public void setLongitude(double v) { longitude = v; }
    public List<String> getImageUrls() { return imageUrls; } public void setImageUrls(List<String> v) { imageUrls = v; }
    public boolean isAvailable() { return available; } public void setAvailable(boolean v) { available = v; }
    public long getCreatedAt() { return createdAt; } public void setCreatedAt(long v) { createdAt = v; }
}
