package com.example.carrentingapp.models;

public class Booking {
    private String id, carId, carName, carImageUrl;
    private String renterId, renterName, renterPhone;
    private String ownerId, status, paymentMethod, paymentStatus;
    private long startDate, endDate, createdAt;
    private double totalPrice, pricePerDay;
    private int totalDays;

    public Booking() {}

    public String getId() { return id; } public void setId(String v) { id = v; }
    public String getCarId() { return carId; } public void setCarId(String v) { carId = v; }
    public String getCarName() { return carName; } public void setCarName(String v) { carName = v; }
    public String getCarImageUrl() { return carImageUrl; } public void setCarImageUrl(String v) { carImageUrl = v; }
    public String getRenterId() { return renterId; } public void setRenterId(String v) { renterId = v; }
    public String getRenterName() { return renterName; } public void setRenterName(String v) { renterName = v; }
    public String getRenterPhone() { return renterPhone; } public void setRenterPhone(String v) { renterPhone = v; }
    public String getOwnerId() { return ownerId; } public void setOwnerId(String v) { ownerId = v; }
    public String getStatus() { return status; } public void setStatus(String v) { status = v; }
    public String getPaymentMethod() { return paymentMethod; } public void setPaymentMethod(String v) { paymentMethod = v; }
    public String getPaymentStatus() { return paymentStatus; } public void setPaymentStatus(String v) { paymentStatus = v; }
    public long getStartDate() { return startDate; } public void setStartDate(long v) { startDate = v; }
    public long getEndDate() { return endDate; } public void setEndDate(long v) { endDate = v; }
    public long getCreatedAt() { return createdAt; } public void setCreatedAt(long v) { createdAt = v; }
    public double getTotalPrice() { return totalPrice; } public void setTotalPrice(double v) { totalPrice = v; }
    public double getPricePerDay() { return pricePerDay; } public void setPricePerDay(double v) { pricePerDay = v; }
    public int getTotalDays() { return totalDays; } public void setTotalDays(int v) { totalDays = v; }
}
