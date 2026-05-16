/*
 * MainActivity.java
 *
 * Hello World main screen — pure vanilla Android code.
 *
 * No imports of any adapter.* class, no calls to OHEnvironment / *Adapter.
 * The same APK runs unchanged on a stock Android device and on an OH device
 * with the adapter installed. Verification that the adapter is active is done
 * externally via `hdc shell logcat | grep OH_AMAdapter` etc., NOT inside the
 * app code.
 *
 * This file demonstrates 4 standard Android subsystems whose calls will be
 * transparently routed to OH services by the adaptation layer:
 *   1. View / Button rendering         — libhwui + Skia
 *   2. Touch input event delivery       — InputChannel bridging
 *   3. startActivity() / Intent         — ActivityManager → OH AbilityManager
 *   4. bindService() / ServiceConnection — ActivityManager → OH AbilityManager
 *   5. Activity lifecycle (onCreate/onResume/onPause/onStop/onDestroy)
 */
package com.example.helloworld;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final String TAG = "HelloWorld_Main";

    private TextView mStatusText;
    private TextView mHelloText;
    private int mColorIndex = 0;
    private boolean mServiceBound = false;

    private static final int[] COLORS = {
        Color.BLACK,
        Color.RED,
        Color.BLUE,
        Color.GREEN,
        Color.MAGENTA,
        Color.rgb(255, 128, 0),  // Orange
        Color.CYAN,
        Color.rgb(128, 0, 255),  // Purple
    };

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "ServiceConnection.onServiceConnected: " + name);
            mServiceBound = true;
            appendStatus("[BIND] Service connected: " + name.getShortClassName());
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "ServiceConnection.onServiceDisconnected: " + name);
            mServiceBound = false;
            appendStatus("[BIND] Service disconnected: " + name.getShortClassName());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "=== MainActivity.onCreate() ===");

        // Build simple UI
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 48, 48, 48);

        // Title
        TextView title = new TextView(this);
        title.setText("Hello World");
        title.setTextSize(24);
        title.setPadding(0, 0, 0, 24);
        layout.addView(title);

        // Hello World text — large, centered, color-changeable
        mHelloText = new TextView(this);
        mHelloText.setText("Hello World!");
        mHelloText.setTextSize(36);
        mHelloText.setTextColor(Color.BLACK);
        mHelloText.setGravity(Gravity.CENTER);
        mHelloText.setPadding(0, 32, 0, 32);
        layout.addView(mHelloText);

        // Button: Change color (verifies input event delivery + view re-draw)
        Button btnChangeColor = new Button(this);
        btnChangeColor.setText("Change Color");
        btnChangeColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mColorIndex = (mColorIndex + 1) % COLORS.length;
                int newColor = COLORS[mColorIndex];
                mHelloText.setTextColor(newColor);
                String colorName = colorToName(newColor);
                Log.i(TAG, "Color changed to: " + colorName);
                appendStatus("[CLICK] Color -> " + colorName);
            }
        });
        layout.addView(btnChangeColor);

        // Status display
        mStatusText = new TextView(this);
        mStatusText.setText("Lifecycle log:");
        mStatusText.setTextSize(14);
        mStatusText.setPadding(0, 32, 0, 32);
        layout.addView(mStatusText);

        // Button 1: startActivity (verifies Activity lifecycle / Intent routing)
        Button btnStartActivity = new Button(this);
        btnStartActivity.setText("Start SecondActivity");
        btnStartActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Button clicked: startActivity(SecondActivity)");
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("greeting", "Hello from MainActivity");
                intent.putExtra("timestamp", System.currentTimeMillis());
                startActivity(intent);
                appendStatus("[INTENT] startActivity(SecondActivity)");
            }
        });
        layout.addView(btnStartActivity);

        // Button 2: bindService (verifies Service / IBinder routing)
        Button btnBindService = new Button(this);
        btnBindService.setText("Bind HelloService");
        btnBindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Button clicked: bindService(HelloService)");
                Intent serviceIntent = new Intent(MainActivity.this, HelloService.class);
                boolean ok = bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
                appendStatus("[BIND] bindService -> " + (ok ? "ok" : "failed"));
            }
        });
        layout.addView(btnBindService);

        // Button 3: unbindService
        Button btnUnbind = new Button(this);
        btnUnbind.setText("Unbind HelloService");
        btnUnbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mServiceBound) {
                    Log.i(TAG, "Button clicked: unbindService");
                    unbindService(mServiceConnection);
                    mServiceBound = false;
                    appendStatus("[BIND] unbindService");
                } else {
                    appendStatus("[BIND] no active connection");
                }
            }
        });
        layout.addView(btnUnbind);

        setContentView(layout);
        appendStatus("[LIFECYCLE] CREATED");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "=== MainActivity.onResume() ===");
        appendStatus("[LIFECYCLE] RESUMED");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "=== MainActivity.onPause() ===");
        appendStatus("[LIFECYCLE] PAUSED");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "=== MainActivity.onStop() ===");
        appendStatus("[LIFECYCLE] STOPPED");
    }

    @Override
    protected void onDestroy() {
        if (mServiceBound) {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
        super.onDestroy();
        Log.i(TAG, "=== MainActivity.onDestroy() ===");
    }

    private void appendStatus(String line) {
        if (mStatusText != null) {
            mStatusText.append("\n" + line);
        }
    }

    private static String colorToName(int color) {
        if (color == Color.BLACK) return "BLACK";
        if (color == Color.RED) return "RED";
        if (color == Color.BLUE) return "BLUE";
        if (color == Color.GREEN) return "GREEN";
        if (color == Color.MAGENTA) return "MAGENTA";
        if (color == Color.CYAN) return "CYAN";
        if (color == Color.rgb(255, 128, 0)) return "ORANGE";
        if (color == Color.rgb(128, 0, 255)) return "PURPLE";
        return String.format("#%06X", 0xFFFFFF & color);
    }
}
