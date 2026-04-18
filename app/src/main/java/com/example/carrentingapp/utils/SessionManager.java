package com.example.carrentingapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "CarRentingSession";
    private static final String KEY_UID = "uid";
    private static final String KEY_ROLE = "role";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_AGE = "age";
    private static final String KEY_DRIVER_LICENSE = "driver_license";
    private static final String KEY_LICENSE_EXPIRATION = "license_expiration";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveSession(String uid, String role, String name, String email, String phone, int age, String driverLicense, String licenseExpiration) {
        editor.putString(KEY_UID, uid);
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONE, phone);
        editor.putInt(KEY_AGE, age);
        editor.putString(KEY_DRIVER_LICENSE, driverLicense);
        editor.putString(KEY_LICENSE_EXPIRATION, licenseExpiration);
        editor.apply();
    }

    public void clearSession() { editor.clear().apply(); }

    public boolean isLoggedIn() { return prefs.contains(KEY_UID); }
    public String getUid() { return prefs.getString(KEY_UID, null); }
    public String getRole() { return prefs.getString(KEY_ROLE, null); }
    public String getName() { return prefs.getString(KEY_NAME, null); }
    public String getEmail() { return prefs.getString(KEY_EMAIL, null); }
    public String getPhone() { return prefs.getString(KEY_PHONE, null); }
    public int getAge() { return prefs.getInt(KEY_AGE, 0); }
    public String getDriverLicense() { return prefs.getString(KEY_DRIVER_LICENSE, null); }
    public String getLicenseExpiration() { return prefs.getString(KEY_LICENSE_EXPIRATION, null); }
    public boolean isOwner() { return Constants.ROLE_OWNER.equals(getRole()); }
}
