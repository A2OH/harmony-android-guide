package com.westlake.engine;

import android.app.Activity;
import android.app.MiniServer;
import android.app.MiniActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import com.ohos.shim.bridge.OHBridge;

/**
 * Generic APK launcher for the Westlake VM subprocess.
 *
 * Replaces the app-specific MockDonaldsApp.main() with a generic entry point
 * that can launch any APK. Reads config from system properties set by the
 * Compose host (WestlakeVM.kt):
 *
 *   -Dwestlake.apk.path=/data/local/tmp/westlake/counter.apk
 *   -Dwestlake.apk.activity=me.tsukanov.counter.ui.MainActivity   (optional)
 *
 * If no APK path is set, falls back to the old MockDonalds behavior.
 *
 * Run on OHOS ART:
 *   dalvikvm -classpath aosp-shim.dex:counter.dex \
 *     -Dwestlake.apk.path=/path/to/counter.apk \
 *     com.westlake.engine.WestlakeLauncher
 */
public class WestlakeLauncher {
    private static final String TAG = "WestlakeLauncher";
    private static final int SURFACE_WIDTH = 480;
    private static final int SURFACE_HEIGHT = 800;

    public static void main(String[] args) {
        String apkPath = System.getProperty("westlake.apk.path");
        String activityName = System.getProperty("westlake.apk.activity");
        String packageName = System.getProperty("westlake.apk.package", "com.example.app");

        System.out.println("[WestlakeLauncher] Starting on OHOS + ART ...");
        System.out.println("[WestlakeLauncher] APK: " + apkPath);
        System.out.println("[WestlakeLauncher] Activity: " + activityName);
        System.out.println("[WestlakeLauncher] Package: " + packageName);

        // Check native bridge
        boolean nativeOk = OHBridge.isNativeAvailable();
        System.out.println("[WestlakeLauncher] OHBridge native: " + (nativeOk ? "LOADED" : "UNAVAILABLE"));

        if (nativeOk) {
            int rc = OHBridge.arkuiInit();
            System.out.println("[WestlakeLauncher] arkuiInit() = " + rc);
        }

        // Initialize MiniServer
        MiniServer.init(packageName);
        MiniServer server = MiniServer.get();
        MiniActivityManager am = server.getActivityManager();
        System.out.println("[WestlakeLauncher] MiniServer initialized");

        // Load APK if provided — this parses manifest, extracts resources, registers activities
        Activity launchedActivity = null;
        if (apkPath != null && !apkPath.isEmpty()) {
            try {
                System.out.println("[WestlakeLauncher] Loading APK: " + apkPath);
                android.app.ApkInfo info = server.loadApk(apkPath);
                System.out.println("[WestlakeLauncher] APK loaded: " + info);
                System.out.println("[WestlakeLauncher]   package: " + info.packageName);
                System.out.println("[WestlakeLauncher]   activities: " + info.activities);
                System.out.println("[WestlakeLauncher]   launcher: " + info.launcherActivity);
                System.out.println("[WestlakeLauncher]   dex paths: " + info.dexPaths);

                // Determine which activity to launch
                String targetActivity = activityName;
                if (targetActivity == null || targetActivity.isEmpty()) {
                    targetActivity = info.launcherActivity;
                }
                if (targetActivity == null || targetActivity.isEmpty()) {
                    if (!info.activities.isEmpty()) {
                        targetActivity = info.activities.get(0);
                    }
                }
                if (targetActivity == null) {
                    System.out.println("[WestlakeLauncher] ERROR: No activity to launch");
                    return;
                }

                System.out.println("[WestlakeLauncher] Launching: " + targetActivity);
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(info.packageName, targetActivity));
                am.startActivity(null, intent, -1);

                launchedActivity = am.getResumedActivity();
            } catch (Exception e) {
                System.out.println("[WestlakeLauncher] APK load error: " + e);
                e.printStackTrace();
                return;
            }
        } else {
            System.out.println("[WestlakeLauncher] No APK path, nothing to launch");
            return;
        }

        if (launchedActivity == null) {
            System.out.println("[WestlakeLauncher] ERROR: Activity failed to launch");
            return;
        }
        System.out.println("[WestlakeLauncher] Activity launched: " + launchedActivity.getClass().getName());

        // Render loop
        if (nativeOk) {
            System.out.println("[WestlakeLauncher] Creating surface " + SURFACE_WIDTH + "x" + SURFACE_HEIGHT);
            try {
                java.lang.reflect.Method m = launchedActivity.getClass().getMethod(
                        "onSurfaceCreated", long.class, int.class, int.class);
                m.invoke(launchedActivity, 0L, SURFACE_WIDTH, SURFACE_HEIGHT);
                m = launchedActivity.getClass().getMethod("renderFrame");
                m.invoke(launchedActivity);
            } catch (Exception e) {
                System.out.println("[WestlakeLauncher] Initial render error: " + e);
            }
            System.out.println("[WestlakeLauncher] Initial frame rendered");
            System.out.println("[WestlakeLauncher] Entering event loop...");
            renderLoop(launchedActivity, am);
        } else {
            System.out.println("[WestlakeLauncher] Running in headless mode (no native bridge)");
        }
    }

    /**
     * Render loop: re-render on touch events from the Compose host.
     * Touch events arrive via touch.dat file.
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
            java.io.File parent = touchFile.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
            System.out.println("[WestlakeLauncher] Touch file (env): " + envTouch);
        } else {
            for (String p : TOUCH_PATHS_FALLBACK) {
                java.io.File f = new java.io.File(p);
                if (f.getParentFile() != null && f.getParentFile().exists()) {
                    touchFile = f;
                    System.out.println("[WestlakeLauncher] Touch file (fallback): " + p);
                    break;
                }
            }
            if (touchFile == null) {
                touchFile = new java.io.File(TOUCH_PATHS_FALLBACK[0]);
                System.out.println("[WestlakeLauncher] Touch file (default): " + TOUCH_PATHS_FALLBACK[0]);
            }
        }

        boolean needsRender = false;
        long downTime = 0;
        int lastTouchY = 0;
        int scrollOffset = 0;
        int totalDragDistance = 0;
        android.view.View lastDecorView = null;

        while (true) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                break;
            }

            Activity current = am.getResumedActivity();
            if (current == null) {
                System.out.println("[WestlakeLauncher] No resumed activity, exiting");
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

                        if (seq != lastTouchSeq) {
                            lastTouchSeq = seq;

                            long now = System.currentTimeMillis();
                            if (action == 0) {
                                downTime = now;
                                lastTouchY = y;
                                totalDragDistance = 0;
                                System.out.println("[WestlakeLauncher] Touch DOWN at (" + x + "," + y + ")");
                                current.dispatchTouchEvent(
                                    android.view.MotionEvent.obtain(downTime, now, 0, (float)x, (float)y, 0));
                                needsRender = true;
                            } else if (action == 2) {
                                if (downTime == 0) downTime = now;
                                int deltaY = lastTouchY - y;
                                lastTouchY = y;
                                totalDragDistance += Math.abs(deltaY);

                                android.view.View decor = null;
                                try { decor = current.getWindow().getDecorView(); } catch (Exception e3) {}
                                if (decor != null) {
                                    int maxScroll = SURFACE_HEIGHT * 2;
                                    scrollOffset += deltaY;
                                    if (scrollOffset < 0) scrollOffset = 0;
                                    if (scrollOffset > maxScroll) scrollOffset = maxScroll;
                                    decor.scrollTo(0, scrollOffset);
                                }

                                current.dispatchTouchEvent(
                                    android.view.MotionEvent.obtain(downTime, now, 2, (float)x, (float)y, 0));
                                needsRender = true;
                            } else if (action == 1) {
                                if (downTime == 0) downTime = now;
                                System.out.println("[WestlakeLauncher] Touch UP at (" + x + "," + y + ")");
                                current.dispatchTouchEvent(
                                    android.view.MotionEvent.obtain(downTime, now, 1, (float)x, (float)y, 0));
                                needsRender = true;

                                if (totalDragDistance < 20) {
                                    android.view.View decor = null;
                                    try { decor = current.getWindow().getDecorView(); } catch (Exception e3) {}
                                    if (decor != null) {
                                        android.view.View target = findViewAt(decor, x, y + scrollOffset);
                                        if (target != null) {
                                            android.view.ViewParent parent = target.getParent();
                                            while (parent != null) {
                                                if (parent instanceof android.widget.ListView) {
                                                    android.widget.ListView lv = (android.widget.ListView) parent;
                                                    int pos = lv.getPositionForView(target);
                                                    if (pos >= 0) {
                                                        System.out.println("[WestlakeLauncher] ListView item " + pos + " clicked");
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
                                }
                                android.view.View newDecor = null;
                                try { newDecor = current.getWindow().getDecorView(); } catch (Exception e4) {}
                                if (newDecor != null && newDecor != lastDecorView) {
                                    newDecor.scrollTo(0, 0);
                                    scrollOffset = 0;
                                    lastDecorView = newDecor;
                                }
                                downTime = 0;
                            }

                            if (needsRender) {
                                Activity next = am.getResumedActivity();
                                if (next != null) {
                                    if (next != current) {
                                        try {
                                            java.lang.reflect.Method sc = next.getClass().getMethod(
                                                    "onSurfaceCreated", long.class, int.class, int.class);
                                            sc.invoke(next, 0L, SURFACE_WIDTH, SURFACE_HEIGHT);
                                        } catch (Exception e2) {}
                                        System.out.println("[WestlakeLauncher] Navigated to " + next.getClass().getSimpleName());
                                    }
                                    try {
                                        java.lang.reflect.Method rf = next.getClass().getMethod("renderFrame");
                                        rf.invoke(next);
                                    } catch (Exception e2) {
                                        if (frameCount < 5) System.out.println("[WestlakeLauncher] renderFrame error: " + e2);
                                    }
                                }
                                needsRender = false;
                            }
                        }
                    }
                } catch (Exception e) {
                    if (frameCount < 10) System.out.println("[WestlakeLauncher] Touch error: " + e);
                }
            }

            frameCount++;
        }
        System.out.println("[WestlakeLauncher] Render loop ended after " + frameCount + " frames");
    }

    /** Find the deepest view containing the given point (absolute coords) */
    private static android.view.View findViewAt(android.view.View v, int x, int y) {
        if (!(v instanceof android.view.ViewGroup)) {
            return v;
        }
        android.view.ViewGroup vg = (android.view.ViewGroup) v;
        for (int i = vg.getChildCount() - 1; i >= 0; i--) {
            android.view.View child = vg.getChildAt(i);
            int cx = x - child.getLeft() + child.getScrollX();
            int cy = y - child.getTop() + child.getScrollY();
            if (cx >= 0 && cx < child.getWidth() && cy >= 0 && cy < child.getHeight()) {
                return findViewAt(child, cx, cy);
            }
        }
        return v;
    }
}
