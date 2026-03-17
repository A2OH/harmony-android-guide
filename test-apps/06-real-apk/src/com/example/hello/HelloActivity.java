package com.example.hello;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Button;
import android.view.View;

/**
 * A real Hello World Activity compiled against android.jar.
 * Uses setContentView(R.layout.activity_main) for XML layout inflation.
 * Falls back to programmatic layout if inflation fails.
 */
public class HelloActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Try XML layout first (from compiled resources)
        try {
            setContentView(R.layout.activity_main);
            System.out.println("=== HelloActivity: XML layout inflated ===");
        } catch (Exception e) {
            // Fallback to programmatic layout
            System.out.println("=== HelloActivity: XML inflation failed, using programmatic ===");
            buildProgrammaticLayout();
        }

        // Print resource values to prove resources.arsc was parsed
        try {
            String appName = getString(R.string.app_name);
            System.out.println("R.string.app_name = " + appName);
            String greeting = getString(R.string.greeting);
            System.out.println("R.string.greeting = " + greeting);
        } catch (Exception e) {
            System.out.println("Resource lookup failed: " + e);
        }

        System.out.println("=== HelloActivity.onCreate() complete ===");
    }

    private void buildProgrammaticLayout() {
        LinearLayout layout = new LinearLayout(this);

        TextView title = new TextView(this);
        title.setText("Hello from Real APK!");
        layout.addView(title);

        TextView info = new TextView(this);
        info.setText("Package: " + getPackageName());
        layout.addView(info);

        Button btn = new Button(this);
        btn.setText("Click Me");
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Button clicked!");
            }
        });
        layout.addView(btn);

        setContentView(layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("=== HelloActivity.onResume() ===");
    }
}
