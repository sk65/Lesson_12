package com.example.lesson_12;

import android.app.Application;
import android.content.Context;

import java.lang.ref.WeakReference;

public class App extends Application {
    public static WeakReference<Context> contextWeakReference;

    @Override
    public void onCreate() {
        super.onCreate();
        contextWeakReference = new WeakReference<>(getApplicationContext());
        SharedPreferencesManager.init(getApplicationContext());
    }
}
