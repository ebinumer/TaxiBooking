package com.example.taxibooking.utils;

import android.text.TextUtils;
import android.util.Patterns;

public class Validation {

    public static boolean isValidEmail(String email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public static boolean isValidPassword(String email) {
        return email.length() >= 6;
    }

    public static boolean isPasswordConfirm(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }
}
