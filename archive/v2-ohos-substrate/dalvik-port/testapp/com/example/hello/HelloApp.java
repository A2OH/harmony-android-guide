package com.example.hello;

import android.app.Application;
import android.util.Log;

/**
 * Standard Android Application subclass.
 * This is an UNMODIFIED Android app — no knowledge of OHOS or shims.
 */
public class HelloApp extends Application {
    private static final String TAG = "HelloApp";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Application initialized for package: " + getPackageName());
    }
}
