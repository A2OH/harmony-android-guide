package com.example.mockdonalds;

import android.app.Activity;
import android.app.MiniServer;
import android.app.MiniActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import com.ohos.shim.bridge.OHBridge;

/**
 * OHOS entry point for MockDonalds app.
 * Initializes MiniServer, sets up OHBridge rendering surface, and launches MenuActivity.
 *
 * Run on OHOS ART:
 *   dalvikvm -classpath app.dex com.example.mockdonalds.MockDonaldsApp
 */
public class MockDonaldsApp {
    private static final String TAG = "MockDonaldsApp";
    private static final int SURFACE_WIDTH = 480;
    private static final int SURFACE_HEIGHT = 800;

    public static void main(String[] args) {
        System.out.println("[MockDonaldsApp] Starting on OHOS + ART ...");

        // Check native bridge availability
        boolean nativeOk = OHBridge.isNativeAvailable();
        System.out.println("[MockDonaldsApp] OHBridge native: " + (nativeOk ? "LOADED" : "UNAVAILABLE"));

        // Initialize ArkUI if available
        if (nativeOk) {
            int rc = OHBridge.arkuiInit();
            System.out.println("[MockDonaldsApp] arkuiInit() = " + rc);
        }

        // Initialize MiniServer (replaces Android SystemServer)
        MiniServer.init("com.example.mockdonalds");

        // Get context from host activity
        android.content.Context appCtx = null;
        try {
            Class<?> host = Class.forName("com.westlake.host.WestlakeActivity");
            appCtx = (android.content.Context) host.getField("instance").get(null);
        } catch (Exception e) {}

        // If running on phone with real Android, launch app gallery home
        if (appCtx != null) {
            System.out.println("[MockDonaldsApp] Running with native Android Views");
            MockApp.init(appCtx);
            com.example.apklauncher.ApkLauncher.init(appCtx);
            com.example.apklauncher.ApkLauncher.showHome();
            System.out.println("[MockDonaldsApp] App gallery displayed");
            // Keep thread alive
            while (true) { try { Thread.sleep(1000); } catch (Exception e) { break; } }
            return;
        }
        MiniServer server = MiniServer.get();
        MiniActivityManager am = server.getActivityManager();
        System.out.println("[MockDonaldsApp] MiniServer initialized");

        // Launch MenuActivity
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(
                "com.example.mockdonalds", "com.example.mockdonalds.MenuActivity"));
        am.startActivity(null, intent, -1);

        Activity menuActivity = am.getResumedActivity();
        if (menuActivity == null) {
            System.out.println("[MockDonaldsApp] ERROR: MenuActivity failed to launch");
            return;
        }
        System.out.println("[MockDonaldsApp] MenuActivity launched: " + menuActivity.getClass().getName());

        // Create rendering surface and do initial render
        if (nativeOk) {
            System.out.println("[MockDonaldsApp] Creating surface " + SURFACE_WIDTH + "x" + SURFACE_HEIGHT);
            // Try shim rendering first (works on our custom dalvikvm)
            boolean shimRendering = false;
            try {
                java.lang.reflect.Method m = menuActivity.getClass().getMethod("onSurfaceCreated", long.class, int.class, int.class);
                m.invoke(menuActivity, 0L, SURFACE_WIDTH, SURFACE_HEIGHT);
                m = menuActivity.getClass().getMethod("renderFrame");
                m.invoke(menuActivity);
                shimRendering = true;
            } catch (Exception e) {
                // On real Android: get the root view and display it natively
                System.out.println("[MockDonaldsApp] Shim render failed: " + e);
                System.out.println("[MockDonaldsApp] Using native Android rendering");
                try {
                    // Use reflection to avoid hard dependency on WestlakeActivity class
                    Class<?> hostCls = Class.forName("com.westlake.host.WestlakeActivity");
                    final android.view.View root = (android.view.View) hostCls.getField("shimRootView").get(null);
                    final Object hostInstance = hostCls.getField("instance").get(null);
                    if (root != null && hostInstance != null) {
                        java.lang.reflect.Method runOnUi = hostCls.getMethod("runOnUiThread", Runnable.class);
                        runOnUi.invoke(hostInstance, new Runnable() {
                            public void run() {
                                try {
                                    if (root.getParent() != null) {
                                        ((android.view.ViewGroup)root.getParent()).removeView(root);
                                    }
                                    java.lang.reflect.Method scv = hostInstance.getClass().getMethod("setContentView", android.view.View.class);
                                    scv.invoke(hostInstance, root);
                                    System.out.println("[MockDonaldsApp] Native content view set");
                                } catch (Exception re) {
                                    System.out.println("[MockDonaldsApp] setContentView failed: " + re);
                                }
                            }
                        });
                    } else {
                        System.out.println("[MockDonaldsApp] No shimRootView or host instance");
                    }
                } catch (Exception ex) {
                    System.out.println("[MockDonaldsApp] Native rendering not available: " + ex);
                }
            }
            System.out.println("[MockDonaldsApp] Initial frame rendered (shim=" + shimRendering + ")");

            // Enter render loop - wait for touch events from native side
            System.out.println("[MockDonaldsApp] Entering event loop...");
            renderLoop(menuActivity, am);
        } else {
            // Headless mode - just verify the app works
            System.out.println("[MockDonaldsApp] Running in headless mode (no native bridge)");
            System.out.println("[MockDonaldsApp] Menu items: " + ((MenuActivity) menuActivity).getMenuItems().size());
            System.out.println("[MockDonaldsApp] App initialized successfully in headless mode");
        }
    }

    /**
     * Simple render loop: re-render the current Activity's view tree.
     * Touch events arrive via touch.dat file from the Compose host.
     * Format: 16 bytes LE [action:i32, x:i32, y:i32, seq:i32]
     * Actions: 0=DOWN, 1=UP, 2=MOVE
     */
    private static final String[] TOUCH_PATHS_FALLBACK = {
        "/data/local/tmp/a2oh/touch.dat",
        "/sdcard/westlake_touch.dat"
    };

    private static void renderLoop(Activity initialActivity, MiniActivityManager am) {
        long frameCount = 0;
        int lastTouchSeq = -1;

        // Prefer WESTLAKE_TOUCH env var (set by Compose host)
        String envTouch = System.getenv("WESTLAKE_TOUCH");
        java.io.File touchFile = null;
        if (envTouch != null && !envTouch.isEmpty()) {
            touchFile = new java.io.File(envTouch);
            // Ensure parent directory exists
            java.io.File parent = touchFile.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
            System.out.println("[MockDonaldsApp] Touch file (env): " + envTouch);
        } else {
            // Fallback: find first usable path
            for (String p : TOUCH_PATHS_FALLBACK) {
                java.io.File f = new java.io.File(p);
                if (f.getParentFile() != null && f.getParentFile().exists()) {
                    touchFile = f;
                    System.out.println("[MockDonaldsApp] Touch file (fallback): " + p);
                    break;
                }
            }
            if (touchFile == null) {
                touchFile = new java.io.File(TOUCH_PATHS_FALLBACK[0]);
                System.out.println("[MockDonaldsApp] Touch file (default): " + TOUCH_PATHS_FALLBACK[0]);
            }
        }

        // Track touch state for drag/scroll gestures
        boolean needsRender = false;
        long downTime = 0;
        int lastTouchY = 0;       // Y of previous touch event (for drag delta)
        int scrollOffset = 0;     // accumulated scroll offset in pixels

        while (true) {
            try {
                Thread.sleep(16); // ~60fps
            } catch (InterruptedException e) {
                break;
            }

            Activity current = am.getResumedActivity();
            if (current == null) {
                System.out.println("[MockDonaldsApp] No resumed activity, exiting");
                break;
            }

            // Check for touch events
            if (touchFile.exists() && touchFile.length() == 16) {
                try {
                    java.io.FileInputStream fis = new java.io.FileInputStream(touchFile);
                    byte[] buf = new byte[16];
                    int n = fis.read(buf);
                    fis.close();
                    if (n == 16) {
                        java.nio.ByteBuffer bb = java.nio.ByteBuffer.wrap(buf);
                        bb.order(java.nio.ByteOrder.LITTLE_ENDIAN);
                        int action = bb.getInt();  // 0=DOWN, 1=UP, 2=MOVE
                        int x = bb.getInt();
                        int y = bb.getInt();
                        int seq = bb.getInt();

                        if (seq != lastTouchSeq) {
                            lastTouchSeq = seq;

                            long now = System.currentTimeMillis();
                            if (action == 0) {
                                // DOWN: start of gesture
                                downTime = now;
                                lastTouchY = y;
                                System.out.println("[MockDonaldsApp] Touch DOWN at (" + x + "," + y + ")");
                                current.dispatchTouchEvent(
                                    android.view.MotionEvent.obtain(downTime, now, 0, (float)x, (float)y, 0));
                                needsRender = true;
                            } else if (action == 2) {
                                // MOVE: drag/scroll
                                if (downTime == 0) downTime = now;
                                int deltaY = lastTouchY - y;  // positive = scroll down (finger moves up)
                                lastTouchY = y;

                                // Apply scroll to the scrollable view in the tree
                                android.view.View decor = null;
                                try { decor = current.getWindow().getDecorView(); } catch (Exception e3) {}
                                if (decor != null) {
                                    // Scroll the decor view directly. Content is taller than
                                    // the 800px surface. Use a generous max scroll since
                                    // layout clips children to surface height.
                                    // 8 menu items × ~170px + header ~200px ≈ 1560px total
                                    int maxScroll = SURFACE_HEIGHT * 2; // generous limit
                                    scrollOffset += deltaY;
                                    if (scrollOffset < 0) scrollOffset = 0;
                                    if (scrollOffset > maxScroll) scrollOffset = maxScroll;
                                    decor.scrollTo(0, scrollOffset);
                                }

                                current.dispatchTouchEvent(
                                    android.view.MotionEvent.obtain(downTime, now, 2, (float)x, (float)y, 0));
                                needsRender = true;
                            } else if (action == 1) {
                                // UP: end of gesture
                                if (downTime == 0) downTime = now;
                                System.out.println("[MockDonaldsApp] Touch UP at (" + x + "," + y + ")");
                                current.dispatchTouchEvent(
                                    android.view.MotionEvent.obtain(downTime, now, 1, (float)x, (float)y, 0));
                                needsRender = true;

                                // Also handle click for short taps (fallback for views without touch handling)
                                android.view.View decor = null;
                                try { decor = current.getWindow().getDecorView(); } catch (Exception e3) {}
                                if (decor != null) {
                                    android.view.View target = findViewAt(decor, x, y);
                                    if (target != null) {
                                        // If it's a ListView item, use performItemClick
                                        android.view.ViewParent parent = target.getParent();
                                        while (parent != null) {
                                            if (parent instanceof android.widget.ListView) {
                                                android.widget.ListView lv = (android.widget.ListView) parent;
                                                int pos = lv.getPositionForView(target);
                                                if (pos >= 0) {
                                                    System.out.println("[MockDonaldsApp] ListView item " + pos + " clicked");
                                                    lv.performItemClick(target, pos, pos);
                                                }
                                                break;
                                            }
                                            if (parent instanceof android.view.View) {
                                                parent = ((android.view.View) parent).getParent();
                                            } else {
                                                break;
                                            }
                                        }
                                        target.performClick();
                                    }
                                }
                                downTime = 0; // reset for next gesture
                            }

                            // Re-render after any touch event
                            if (needsRender) {
                                Activity next = am.getResumedActivity();
                                if (next != null) {
                                    if (next != current) {
                                        try {
                                            java.lang.reflect.Method sc = next.getClass().getMethod("onSurfaceCreated", long.class, int.class, int.class);
                                            sc.invoke(next, 0L, SURFACE_WIDTH, SURFACE_HEIGHT);
                                        } catch (Exception e2) {}
                                        System.out.println("[MockDonaldsApp] Navigated to " + next.getClass().getSimpleName());
                                    }
                                    try {
                                        java.lang.reflect.Method rf = next.getClass().getMethod("renderFrame");
                                        rf.invoke(next);
                                    } catch (Exception e2) {
                                        if (frameCount < 5) System.out.println("[MockDonaldsApp] renderFrame error: " + e2);
                                    }
                                }
                                needsRender = false;
                            }
                        }
                    }
                } catch (Exception e) {
                    if (frameCount < 10) System.out.println("[MockDonaldsApp] Touch error: " + e);
                }
            }

            frameCount++;
        }
        System.out.println("[MockDonaldsApp] Render loop ended after " + frameCount + " frames");
    }

    /** Find the first scrollable view (ListView or ScrollView) in the tree via BFS */
    /** Recursively measure the total height of all views in the tree */
    private static int measureTotalHeight(android.view.View v) {
        if (!(v instanceof android.view.ViewGroup)) {
            return v.getBottom();
        }
        android.view.ViewGroup vg = (android.view.ViewGroup) v;
        int maxBottom = v.getBottom();
        for (int i = 0; i < vg.getChildCount(); i++) {
            android.view.View child = vg.getChildAt(i);
            // For vertical LinearLayout, children stack — sum their heights
            int childTotal = child.getTop() + measureTotalHeight(child);
            if (childTotal > maxBottom) maxBottom = childTotal;
        }
        return maxBottom;
    }

    private static android.view.View findScrollableView(android.view.View root) {
        java.util.LinkedList<android.view.View> queue = new java.util.LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            android.view.View v = queue.poll();
            if (v instanceof android.widget.ListView || v instanceof android.widget.ScrollView) {
                return v;
            }
            if (v instanceof android.view.ViewGroup) {
                android.view.ViewGroup vg = (android.view.ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    queue.add(vg.getChildAt(i));
                }
            }
        }
        return null;
    }

    /** Find the deepest view containing the given point (absolute coords) */
    private static android.view.View findViewAt(android.view.View v, int x, int y) {
        if (!(v instanceof android.view.ViewGroup)) {
            return v; // leaf view
        }
        android.view.ViewGroup vg = (android.view.ViewGroup) v;
        for (int i = vg.getChildCount() - 1; i >= 0; i--) {
            android.view.View child = vg.getChildAt(i);
            // Convert to child-local coordinates
            int cx = x - child.getLeft() + child.getScrollX();
            int cy = y - child.getTop() + child.getScrollY();
            if (cx >= 0 && cx < child.getWidth() && cy >= 0 && cy < child.getHeight()) {
                return findViewAt(child, cx, cy);
            }
        }
        return v; // no child hit, return this ViewGroup itself
    }

    private static void dumpViewBounds(android.view.View v, int depth) {
        String indent = "";
        for (int i = 0; i < depth; i++) indent += "  ";
        String name = v.getClass().getSimpleName();
        System.out.println("[LAYOUT] " + indent + name
            + " bounds=(" + v.getLeft() + "," + v.getTop() + ")-(" + v.getRight() + "," + v.getBottom() + ")"
            + " size=" + v.getWidth() + "x" + v.getHeight()
            + (v instanceof android.view.ViewGroup ? " children=" + ((android.view.ViewGroup)v).getChildCount() : ""));
        if (v instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                dumpViewBounds(vg.getChildAt(i), depth + 1);
            }
        }
    }
}
