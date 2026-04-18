package com.example.carrentingapp.utils;

public class Constants {
    // Firestore Collections
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_CARS = "cars";
    public static final String COLLECTION_BOOKINGS = "bookings";
    public static final String COLLECTION_SAVED_CARS = "saved_cars";

    // Storage Paths
    public static final String STORAGE_CAR_IMAGES = "car_images/";
    public static final String STORAGE_AVATARS = "avatars/";

    // Car Types
    public static final String TYPE_MOTORBIKE = "Xe máy";
    public static final String TYPE_CAR = "Ô tô (4-5 chỗ)";
    public static final String TYPE_SUV = "Xe SUV (7 chỗ)";
    public static final String TYPE_VAN = "Xe 16 chỗ";
    public static final String TYPE_ELECTRIC = "Xe điện";
    public static final String TYPE_TRUCK = "Xe tải";
    public static final String TYPE_PICKUP = "Xe bán tải";

    public static String[] getAllCarTypes() {
        return new String[]{TYPE_MOTORBIKE, TYPE_CAR, TYPE_SUV, TYPE_VAN, TYPE_ELECTRIC, TYPE_TRUCK, TYPE_PICKUP};
    }

    // Car Conditions
    public static final String CONDITION_NEW = "Mới";
    public static final String CONDITION_GOOD = "Tốt";
    public static final String CONDITION_FAIR = "Trung bình";

    // Booking Status
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_CONFIRMED = "confirmed";
    public static final String STATUS_CANCELLED = "cancelled";
    public static final String STATUS_COMPLETED = "completed";

    // Payment Methods
    public static final String PAYMENT_MOMO = "MoMo";
    public static final String PAYMENT_ZALOPAY = "ZaloPay";
    public static final String PAYMENT_BANK = "Thẻ ngân hàng";

    // Payment Status
    public static final String PAYMENT_PENDING = "pending";
    public static final String PAYMENT_SUCCESS = "success";
    public static final String PAYMENT_FAILED = "failed";

    // Intent Keys
    public static final String KEY_CAR_ID = "car_id";
    public static final String KEY_BOOKING_ID = "booking_id";
    public static final String KEY_USER_ROLE = "user_role";

    // Pagination
    public static final int PAGE_SIZE = 20;

    // Image constraints
    public static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
    public static final int MAX_IMAGES = 5;

    // Price warning threshold (VND)
    public static final double PRICE_WARNING_THRESHOLD = 5_000_000;

    // Cloudinary Config (Thay thế các giá trị này bằng account của bạn)
    public static final String CLOUD_NAME = "dxff8nkx6";
    public static final String API_KEY = "749374789184881";
    public static final String API_SECRET = "fxb1M5IjoTFRmU2ke-DlFQD8yRc";

    // Roles
    public static final String ROLE_OWNER = "owner";
    public static final String ROLE_RENTER = "renter";
}
