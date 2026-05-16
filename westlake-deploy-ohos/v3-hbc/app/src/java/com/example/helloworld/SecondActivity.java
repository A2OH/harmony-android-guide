/*
 * SecondActivity.java
 *
 * Second Activity, launched from MainActivity via standard startActivity(Intent).
 * Pure vanilla Android — no awareness of any adaptation layer.
 */
package com.example.helloworld;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SecondActivity extends Activity {

    private static final String TAG = "HelloWorld_Second";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "=== SecondActivity.onCreate() ===");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 48, 48, 48);

        TextView title = new TextView(this);
        title.setText("SecondActivity");
        title.setTextSize(24);
        layout.addView(title);

        // Display parameters passed from Intent
        String greeting = getIntent().getStringExtra("greeting");
        long timestamp = getIntent().getLongExtra("timestamp", 0);

        TextView info = new TextView(this);
        info.setText("\nReceived from Intent extras:\n"
                + "  greeting: " + greeting + "\n"
                + "  timestamp: " + timestamp);
        info.setTextSize(14);
        info.setPadding(0, 24, 0, 0);
        layout.addView(info);

        setContentView(layout);
    }
}
