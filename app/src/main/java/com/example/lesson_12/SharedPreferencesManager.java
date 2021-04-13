package com.example.lesson_12;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferencesManager {
    private static final String PACKAGE_NAME = "com.example.lesson8";
    private static final String PREF_KEY = PACKAGE_NAME + ".appSetting";

    public static final String IS_AUTH_KEY = PACKAGE_NAME + ".isAuth";
    public static final String CURRENT_USER_ID_KEY = PACKAGE_NAME + "currentUserId";
    public static final String IMG_URI_KEY = PACKAGE_NAME + ".imgUri";
    public static final String CURRENT_USER_EMAIL_KEY = PACKAGE_NAME + ".userEmailKey";

    private final SharedPreferences sharedPreferences;
    private static SharedPreferencesManager instance;
    private final SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    private SharedPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesManager(context.getApplicationContext());
        }
    }

    public static SharedPreferencesManager getInstance() {
        return instance;
    }

    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void putIsAuth(boolean isAuth) {
        editor.putBoolean(IS_AUTH_KEY, isAuth).apply();
    }

    public boolean getIsAuth() {
        return sharedPreferences.getBoolean(IS_AUTH_KEY, false);
    }

    public void putCurrentUserId(long userId) {
        editor.putLong(CURRENT_USER_ID_KEY, userId).apply();
    }

    public long getCurrentUserId() {
        return sharedPreferences.getLong(CURRENT_USER_ID_KEY, -1);
    }

    public void putCurrentUserEmail(String email) {
        editor.putString(CURRENT_USER_EMAIL_KEY, email).apply();
    }

    public String getCurrentUserEmail() {
        return sharedPreferences.getString(CURRENT_USER_EMAIL_KEY, "");
    }

    public void putCurrentImgUri(String imgUri) {
        editor.putString(IMG_URI_KEY, imgUri).apply();
    }

    public String getCurrentUserImageUri() {
        return sharedPreferences.getString(IMG_URI_KEY, "");
    }
}
