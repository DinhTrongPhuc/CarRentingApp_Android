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

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveSession(String uid, String role, String name, String email, String phone) {
        editor.putString(KEY_UID, uid);
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONE, phone);
        editor.apply();
    }

    public void clearSession() { editor.clear().apply(); }

    public boolean isLoggedIn() { return prefs.contains(KEY_UID); }
    public String getUid() { return prefs.getString(KEY_UID, null); }
    public String getRole() { return prefs.getString(KEY_ROLE, null); }
    public String getName() { return prefs.getString(KEY_NAME, null); }
    public String getEmail() { return prefs.getString(KEY_EMAIL, null); }
    public String getPhone() { return prefs.getString(KEY_PHONE, null); }
    public boolean isOwner() { return Constants.ROLE_OWNER.equals(getRole()); }
}
