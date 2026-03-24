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
            menuActivity.onSurfaceCreated(0, SURFACE_WIDTH, SURFACE_HEIGHT);
            menuActivity.renderFrame();
            // Debug: dump view tree bounds
            android.view.View decor = menuActivity.getWindow().getDecorView();
            dumpViewBounds(decor, 0);
            System.out.println("[MockDonaldsApp] Initial frame rendered");

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
     * Touch events arrive via OHBridge.dispatchTouchEvent() from native.
     */
    private static final String[] TOUCH_PATHS = {
        "/sdcard/Android/data/com.westlake.host/files/touch.dat",
        "/storage/emulated/0/Android/data/com.westlake.host/files/touch.dat",
        "/data/local/tmp/a2oh/touch.dat",
        "/sdcard/westlake_touch.dat"
    };

    private static void renderLoop(Activity initialActivity, MiniActivityManager am) {
        long frameCount = 0;
        int lastTouchSeq = -1;
        java.io.File touchFile = null;
        // Find first readable touch file
        for (String p : TOUCH_PATHS) {
            java.io.File f = new java.io.File(p);
            if (f.getParentFile() != null && f.getParentFile().exists()) {
                touchFile = f;
                System.out.println("[MockDonaldsApp] Touch file: " + p);
                break;
            }
        }
        if (touchFile == null) {
            touchFile = new java.io.File(TOUCH_PATHS[0]);
            System.out.println("[MockDonaldsApp] Touch file fallback: " + TOUCH_PATHS[0]);
        }

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
                        int action = bb.getInt();
                        int x = bb.getInt();
                        int y = bb.getInt();
                        int seq = bb.getInt();
                        if (seq != lastTouchSeq && action == 1) { // Only process UP (click)
                            lastTouchSeq = seq;
                            touchFile.delete(); // prevent re-read
                            System.out.println("[MockDonaldsApp] Click at (" + x + "," + y + ")");
                            // Find the clickable view at (x,y) and click it directly
                            android.view.View decor = current.getWindow().getDecorView();
                            android.view.View target = findViewAt(decor, x, y);
                            if (target != null) {
                                System.out.println("[MockDonaldsApp] Hit: " + target.getClass().getSimpleName()
                                    + " bounds=(" + target.getLeft() + "," + target.getTop() + ")");
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
                                // Also try regular click
                                target.performClick();
                            } else {
                                // Fallback: dispatch DOWN+UP
                                long now = System.currentTimeMillis();
                                current.dispatchTouchEvent(android.view.MotionEvent.obtain(now, now, 0, (float)x, (float)y, 0));
                                current.dispatchTouchEvent(android.view.MotionEvent.obtain(now, now+50, 1, (float)x, (float)y, 0));
                            }
                            // Activity may have changed after click
                            Activity next = am.getResumedActivity();
                            if (next != null) {
                                if (next != current) {
                                    next.onSurfaceCreated(0, SURFACE_WIDTH, SURFACE_HEIGHT);
                                    System.out.println("[MockDonaldsApp] Navigated to " + next.getClass().getSimpleName());
                                }
                                next.renderFrame();
                                // Signal C to write new PNG
                            }
                        } else if (seq != lastTouchSeq) {
                            lastTouchSeq = seq;
                            touchFile.delete(); // prevent re-read // consume DOWN/MOVE without processing
                        }
                    }
                } catch (Exception e) {
                    if (frameCount < 10) System.out.println("[MockDonaldsApp] Touch error: " + e);
                }
            }

            // Only render on touch — no continuous rendering
            frameCount++;
        }
        System.out.println("[MockDonaldsApp] Render loop ended after " + frameCount + " frames");
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
