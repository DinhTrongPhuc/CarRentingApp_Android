package com.example.carrentingapp.utils;

import android.util.Patterns;

public class ValidationUtils {
    public static boolean isValidEmail(String email) {
        return email != null && !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isValidPrice(String price) {
        try {
            double val = Double.parseDouble(price);
            return val > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^(0[3|5|7|8|9])+([0-9]{8})$");
    }
}
