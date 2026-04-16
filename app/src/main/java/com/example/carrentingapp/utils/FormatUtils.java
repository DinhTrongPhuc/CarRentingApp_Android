package com.example.carrentingapp.utils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatUtils {
    private static final NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public static String formatCurrency(double amount) {
        return currencyFormat.format((long) amount) + " VNĐ";
    }

    public static String formatDate(long timestamp) {
        return dateFormat.format(new Date(timestamp));
    }

    public static long daysBetween(long startMs, long endMs) {
        return (endMs - startMs) / (1000 * 60 * 60 * 24);
    }

    public static double calculateTotal(double pricePerDay, int days) {
        return pricePerDay * days;
    }
}
