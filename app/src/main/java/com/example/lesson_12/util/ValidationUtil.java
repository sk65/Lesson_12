package com.example.lesson_12.util;

import android.content.Context;
import android.util.Patterns;

import com.example.lesson_12.App;
import com.example.lesson_12.R;
import com.google.android.material.textfield.TextInputLayout;

import java.lang.ref.WeakReference;
import java.util.regex.Pattern;

public class ValidationUtil {
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +
                    "(?=.*[a-z])" +
                    "(?=.*[A-Z])" +
                    "(?=\\S+$)" +
                    ".{6,20}" +
                    "$");

    private final static WeakReference<Context> context;

    static {
        context = App.contextWeakReference;
    }

    public static String validatePassword(String password) {
        if (password.isEmpty()) {
            return context.get().getString(R.string.empty_field_explanation);
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return context.get().getString(R.string.weak_password_explanation);
        } else {
            return null;
        }
    }

    public static String validateName(String name) {
        if (name.isEmpty()) {
            return context.get().getString(R.string.empty_field_explanation);
        } else {
            return null;
        }
    }

    public static String validateConfirmPassword(String confirmPassword, String password) {
        if (confirmPassword.isEmpty()) {
            return context.get().getString(R.string.empty_field_explanation);
        } else if (!confirmPassword.equals(password)) {
            return context.get().getString(R.string.password_mismatch);
        } else {
            return null;
        }
    }

    public static String validateLastName(String lastName) {
        if (lastName.isEmpty()) {
            return context.get().getString(R.string.empty_field_explanation);
        } else {
            return null;
        }
    }

    public static String validateEmail(String email) {
        if (email.isEmpty()) {
            return context.get().getString(R.string.empty_field_explanation);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return context.get().getString(R.string.invalid_email_explanation);
        } else {
            return null;
        }
    }

    public static String getStringFromInputLayout(TextInputLayout inputLayout) {
        return inputLayout.getEditText().getText().toString().trim();
    }

}
