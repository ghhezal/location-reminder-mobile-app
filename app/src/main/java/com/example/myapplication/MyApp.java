package com.example.myapplication;

import android.app.Application;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Restore the saved dark/light mode preference on every app launch
        ThemeHelper.applySavedTheme(this);
    }
}
