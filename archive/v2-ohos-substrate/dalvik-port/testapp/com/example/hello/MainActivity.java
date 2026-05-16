package com.example.hello;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Standard Android Activity — completely unmodified, no OHOS awareness.
 * Uses standard Android APIs: Activity, Bundle, Intent, Log.
 * This should run transparently on both Android and OHOS+Dalvik.
 */
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate called");

        if (savedInstanceState != null) {
            Log.i(TAG, "Restoring from saved state");
        } else {
            Log.i(TAG, "Fresh launch (no saved state)");
        }

        // Use Bundle
        Bundle data = new Bundle();
        data.putString("message", "Hello from a real Android app!");
        data.putInt("version", 1);
        data.putBoolean("transparent", true);
        Log.i(TAG, "Bundle: message=" + data.getString("message")
                + " version=" + data.getInt("version")
                + " transparent=" + data.getBoolean("transparent"));

        // Use Intent
        Intent intent = getIntent();
        if (intent != null) {
            Log.i(TAG, "Launch intent: " + intent);
            String action = intent.getAction();
            Log.i(TAG, "Action: " + action);
        }

        // Create a new Intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Shared from Android on OHOS!");
        Log.i(TAG, "Share intent: " + shareIntent);

        // System info
        Log.i(TAG, "OS: " + System.getProperty("os.name") + "/" + System.getProperty("os.arch"));
        Log.i(TAG, "VM: " + System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version"));
        Log.i(TAG, "Java: " + System.getProperty("java.version"));

        // Component info
        if (getComponentName() != null) {
            Log.i(TAG, "Component: " + getComponentName().flattenToShortString());
        }
        if (getApplication() != null) {
            Log.i(TAG, "Application: " + getApplication().getClass().getName());
        }

        Log.i(TAG, "Computation: 6 * 7 = " + (6 * 7));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("key", "saved_value");
        Log.i(TAG, "onSaveInstanceState");
    }
}
