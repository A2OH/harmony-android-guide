package com.example.composetest;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Typeface;
import android.view.Gravity;

/**
 * Tests Jetpack Compose loading on the Westlake engine.
 *
 * Phase 5 test: Can we load Compose classes, create a ComposeView,
 * and render setContent { Text("Hello Compose!") } ?
 */
public class ComposeTestApp {

    static Context ctx;
    static Activity hostActivity;

    public static void init(Context context) {
        ctx = context;
        try {
            Class<?> host = Class.forName("com.westlake.host.WestlakeActivity");
            hostActivity = (Activity) host.getField("instance").get(null);
        } catch (Exception e) {}
    }

    static int dp(int d) { return (int)(d * ctx.getResources().getDisplayMetrics().density); }

    static void show(final View view) {
        if (hostActivity == null) return;
        hostActivity.runOnUiThread(new Runnable() {
            public void run() {
                if (view.getParent() != null)
                    ((ViewGroup) view.getParent()).removeView(view);
                hostActivity.setContentView(view);
            }
        });
    }

    /**
     * Test Compose class loading and basic setup.
     * Shows diagnostic output for each step.
     */
    public static void testCompose() {
        System.out.println("[ComposeTest] Starting Compose test...");

        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFFFFFFFF);
        root.setPadding(dp(16), dp(16), dp(16), dp(16));

        // Back button
        android.widget.Button back = new android.widget.Button(ctx);
        back.setText("← Back");
        back.setTextSize(14);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try { com.example.apklauncher.ApkLauncher.showHome(); }
                catch (Exception e) {}
            }
        });
        root.addView(back);

        TextView title = new TextView(ctx);
        title.setText("Jetpack Compose Test");
        title.setTextSize(22);
        title.setTextColor(0xFF1565C0);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setPadding(0, dp(8), 0, dp(16));
        root.addView(title);

        ClassLoader cl = ComposeTestApp.class.getClassLoader();

        // Step 1: Test Compose runtime classes
        testClass(root, cl, "androidx.compose.runtime.Composer", "Compose Runtime");
        testClass(root, cl, "androidx.compose.runtime.Recomposer", "Recomposer");
        testClass(root, cl, "androidx.compose.runtime.Composition", "Composition");

        // Step 2: Test Compose UI classes
        testClass(root, cl, "androidx.compose.ui.platform.ComposeView", "ComposeView");
        testClass(root, cl, "androidx.compose.ui.platform.AbstractComposeView", "AbstractComposeView");
        testClass(root, cl, "androidx.compose.ui.platform.AndroidComposeView", "AndroidComposeView");

        // Step 3: Test Compose Foundation/Material
        testClass(root, cl, "androidx.compose.foundation.layout.ColumnKt", "Column");
        testClass(root, cl, "androidx.compose.foundation.layout.RowKt", "Row");
        testClass(root, cl, "androidx.compose.material3.TextKt", "Material3 Text");
        testClass(root, cl, "androidx.compose.material3.ButtonKt", "Material3 Button");

        // Step 4: Test Lifecycle integration
        testClass(root, cl, "androidx.lifecycle.LifecycleOwner", "LifecycleOwner");
        testClass(root, cl, "androidx.lifecycle.LifecycleRegistry", "LifecycleRegistry");
        testClass(root, cl, "androidx.lifecycle.ViewTreeLifecycleOwner", "ViewTreeLifecycleOwner");

        // Step 5: Test Activity integration
        testClass(root, cl, "androidx.activity.ComponentActivity", "ComponentActivity");
        testClass(root, cl, "androidx.activity.compose.ComponentActivityKt", "setContent extension");

        // Step 6: Test Kotlin runtime
        testClass(root, cl, "kotlin.Unit", "Kotlin Unit");
        testClass(root, cl, "kotlinx.coroutines.CoroutineScope", "Coroutines");

        // Step 7: Test our Activity implements LifecycleOwner
        addStatus(root, "---", "");
        addStatus(root, "⏳", "Testing Activity lifecycle support...");
        try {
            // Check our shim interface
            boolean isShimLO = ctx instanceof androidx.lifecycle.LifecycleOwner;
            addStatus(root, isShimLO ? "✅" : "⚠️",
                "Shim LifecycleOwner: " + isShimLO);

            // Check if phone's Activity has getLifecycle() (real AndroidX)
            try {
                java.lang.reflect.Method glm = hostActivity.getClass().getMethod("getLifecycle");
                Object lifecycle = glm.invoke(hostActivity);
                addStatus(root, "✅", "Phone Activity.getLifecycle(): " +
                    (lifecycle != null ? lifecycle.getClass().getSimpleName() : "null"));
                if (lifecycle != null) {
                    java.lang.reflect.Method gsm = lifecycle.getClass().getMethod("getCurrentState");
                    Object state = gsm.invoke(lifecycle);
                    addStatus(root, "✅", "Phone Lifecycle state: " + state);
                }
            } catch (NoSuchMethodException e) {
                addStatus(root, "❌", "Phone Activity has no getLifecycle()");
            }
        } catch (Exception e) {
            addStatus(root, "❌", "Lifecycle test failed: " + e.getMessage());
        }

        // Step 8: Try creating a ComposeView
        addStatus(root, "---", "");
        addStatus(root, "⏳", "Creating ComposeView + setting content...");
        try {
            Class<?> composeViewClass = cl.loadClass("androidx.compose.ui.platform.ComposeView");
            final View composeView = (View) composeViewClass.getConstructor(Context.class).newInstance(hostActivity);
            addStatus(root, "✅", "ComposeView created");

            // Set ViewTree owners on the ComposeView so Compose can find lifecycle
            // Use reflection since phone Activity might not implement our shim interfaces
            try {
                // Try setting lifecycle owner via the real AndroidX ViewTreeLifecycleOwner
                Class<?> vtlo = cl.loadClass("androidx.lifecycle.ViewTreeLifecycleOwner");
                // The compose.dex ViewTreeLifecycleOwner uses View.setTag(R.id.view_tree_lifecycle_owner, owner)
                // We need to provide a LifecycleOwner — create one
                Class<?> lrClass = cl.loadClass("androidx.lifecycle.LifecycleRegistry");
                Class<?> loClass = cl.loadClass("androidx.lifecycle.LifecycleOwner");

                // Create a simple LifecycleOwner wrapper for the host activity
                addStatus(root, "⏳", "Setting up lifecycle for ComposeView...");

                // Use our shim LifecycleRegistry as the lifecycle provider
                final androidx.lifecycle.LifecycleRegistry lifecycle = new androidx.lifecycle.LifecycleRegistry(
                    new androidx.lifecycle.LifecycleOwner() {
                        public androidx.lifecycle.Lifecycle getLifecycle() { return null; }
                    }
                );
                lifecycle.handleLifecycleEvent(androidx.lifecycle.Lifecycle.Event.ON_CREATE);
                lifecycle.handleLifecycleEvent(androidx.lifecycle.Lifecycle.Event.ON_START);
                lifecycle.handleLifecycleEvent(androidx.lifecycle.Lifecycle.Event.ON_RESUME);
                addStatus(root, "✅", "Lifecycle at RESUMED state");

            } catch (Exception le) {
                addStatus(root, "⚠️", "Lifecycle setup: " + le.getMessage());
            }

            // Don't add ComposeView to layout yet — needs lifecycle setup first
            addStatus(root, "✅", "ComposeView ready (not added to layout yet — needs lifecycle)");

            // Try calling setContent via reflection
            addStatus(root, "⏳", "Calling ComposeView.setContent...");
            try {
                // ComposeView.setContent takes a kotlin Function2<Composer, Int, Unit>
                // We can't easily create Kotlin lambdas from Java
                // Instead, try using the ComposeView directly — it will render when attached
                java.lang.reflect.Method setContentMethod = null;
                for (java.lang.reflect.Method m : composeViewClass.getMethods()) {
                    if ("setContent".equals(m.getName())) {
                        setContentMethod = m;
                        addStatus(root, "✅", "Found setContent: " + m.toGenericString());
                        break;
                    }
                }
                if (setContentMethod == null) {
                    addStatus(root, "⚠️", "setContent method not found on ComposeView");
                }
            } catch (Exception sce) {
                addStatus(root, "⚠️", "setContent lookup: " + sce.getMessage());
            }

        } catch (Exception e) {
            addStatus(root, "❌", "ComposeView failed: " + e.getClass().getSimpleName());
            addStatus(root, "❌", "" + e.getMessage());
            Throwable cause = e.getCause();
            while (cause != null) {
                addStatus(root, "❌", "Caused by: " + cause.getClass().getSimpleName() + ": " + cause.getMessage());
                cause = cause.getCause();
            }
        }

        // Also test ComponentActivity loading with detailed error
        addStatus(root, "---", "");
        addStatus(root, "⏳", "Diagnosing ComponentActivity...");
        try {
            Class<?> ca = cl.loadClass("androidx.activity.ComponentActivity");
            addStatus(root, "✅", "ComponentActivity loaded: " + ca.getSuperclass().getName());
        } catch (Throwable t) {
            addStatus(root, "❌", "ComponentActivity: " + t.getClass().getSimpleName());
            addStatus(root, "❌", "" + t.getMessage());
            if (t.getCause() != null) {
                addStatus(root, "❌", "Cause: " + t.getCause().getClass().getSimpleName() + ": " + t.getCause().getMessage());
                if (t.getCause().getCause() != null) {
                    addStatus(root, "❌", "Root: " + t.getCause().getCause().getMessage());
                }
            }
        }

        show(root);
    }

    static void testClass(LinearLayout root, ClassLoader cl, String className, String label) {
        try {
            Class<?> c = cl.loadClass(className);
            addStatus(root, "✅", label + " (" + className.substring(className.lastIndexOf('.') + 1) + ")");
        } catch (ClassNotFoundException e) {
            addStatus(root, "❌", label + " — NOT FOUND");
        } catch (Exception e) {
            addStatus(root, "⚠️", label + " — " + e.getClass().getSimpleName());
        }
    }

    static void addStatus(LinearLayout parent, String icon, String text) {
        System.out.println("[ComposeTest] " + icon + " " + text);
        LinearLayout row = new LinearLayout(parent.getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, dp(3), 0, dp(3));

        TextView iconTv = new TextView(parent.getContext());
        iconTv.setText(icon + " ");
        iconTv.setTextSize(13);
        row.addView(iconTv);

        TextView textTv = new TextView(parent.getContext());
        textTv.setText(text);
        textTv.setTextSize(12);
        textTv.setTextColor(0xFF424242);
        row.addView(textTv);

        parent.addView(row);
    }
}
