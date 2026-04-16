package com.example.carrentingapp;

import android.app.Application;
import com.cloudinary.android.MediaManager;
import com.example.carrentingapp.utils.Constants;
import com.google.firebase.FirebaseApp;

import java.util.HashMap;
import java.util.Map;

public class CarRentingApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);

        // Khởi tạo Cloudinary
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", Constants.CLOUD_NAME);
        config.put("api_key", Constants.API_KEY);
        config.put("api_secret", Constants.API_SECRET);
        MediaManager.init(this, config);
    }
}
