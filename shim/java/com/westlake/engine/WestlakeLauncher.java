package com.westlake.engine;

import android.app.Activity;
import android.app.MiniServer;
import android.app.MiniActivityManager;
import android.app.ShimCompat;
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

        // Pre-seed SharedPreferences BEFORE any app code runs
        if ("me.tsukanov.counter".equals(packageName)) {
            android.content.SharedPreferences sp =
                android.content.SharedPreferences.getInstance("counters");
            if (sp.getAll().isEmpty()) {
                sp.edit().putInt("My Counter", 0)
                         .putInt("Steps", 42)
                         .putInt("Coffee", 3)
                         .apply();
                System.out.println("[WestlakeLauncher] Pre-seeded 3 counters");
            }
        }
        // Store counter data to set on CounterApplication after its creation
        final java.util.LinkedHashMap<String, Integer> counterData = new java.util.LinkedHashMap<>();
        if ("me.tsukanov.counter".equals(packageName)) {
            android.content.SharedPreferences sp = android.content.SharedPreferences.getInstance("counters");
            for (java.util.Map.Entry<String, ?> e : sp.getAll().entrySet()) {
                if (e.getValue() instanceof Integer) counterData.put(e.getKey(), (Integer) e.getValue());
            }
        }

        // Try to create the APK's custom Application class
        // (e.g., CounterApplication instead of generic Application)
        if (packageName != null) {
            // Common patterns: <pkg>.App, <pkg>.<Name>Application
            String[] candidates = {
                packageName + "." + packageName.substring(packageName.lastIndexOf('.') + 1)
                    .substring(0, 1).toUpperCase()
                    + packageName.substring(packageName.lastIndexOf('.') + 2) + "Application",
                packageName + ".App",
                packageName + ".MainApplication",
                // For me.tsukanov.counter → CounterApplication
                packageName + ".CounterApplication",
            };
            for (String appName : candidates) {
                try {
                    Class<?> appCls = ClassLoader.getSystemClassLoader().loadClass(appName);
                    android.app.Application customApp = (android.app.Application) appCls.newInstance();
                    server.setApplication(customApp);
                    // Call Application.onCreate() — many apps initialize storage/singletons here
                    try {
                        customApp.onCreate();
                        System.out.println("[WestlakeLauncher] Custom Application created + onCreate: " + appCls.getSimpleName());
                    } catch (Exception appEx) {
                        System.out.println("[WestlakeLauncher] Application.onCreate error (non-fatal): " + appEx);
                    }
                    // Force-set 'counters' field on CounterApplication
                    try {
                        java.lang.reflect.Field cf = customApp.getClass().getDeclaredField("counters");
                        cf.setAccessible(true);
                        Object existing = cf.get(customApp);
                        System.out.println("[WestlakeLauncher] counters field: " + existing + " (type=" + (existing != null ? existing.getClass().getName() : "null") + ")");
                        if (existing == null && !counterData.isEmpty()) {
                            cf.set(customApp, counterData);
                            System.out.println("[WestlakeLauncher] Force-set counters: " + counterData.keySet());
                        }
                    } catch (Exception e) {
                        System.out.println("[WestlakeLauncher] counters field error: " + e);
                    }
                    break;
                } catch (ClassNotFoundException e) {
                    // try next
                } catch (Exception e) {
                    System.out.println("[WestlakeLauncher] Application error: " + e);
                    break;
                }
            }
        }

        // Load APK resources — use pre-extracted dir if available (dalvikvm has no ZipFile JNI)
        Activity launchedActivity = null;
        String targetActivity = activityName;
        String resDir = System.getProperty("westlake.apk.resdir");
        if (apkPath != null && !apkPath.isEmpty()) {
            try {
                System.out.println("[WestlakeLauncher] Loading APK: " + apkPath);
                System.out.println("[WestlakeLauncher] ResDir: " + resDir);

                android.app.ApkInfo info;
                // Check resDir — also try fallback paths if the primary path isn't accessible
                String effectiveResDir = resDir;
                if (effectiveResDir != null && !new java.io.File(effectiveResDir, "resources.arsc").exists()) {
                    System.out.println("[WestlakeLauncher] ResDir not accessible: " + effectiveResDir);
                    // Try sibling of the dalvikvm binary
                    String[] fallbacks = {
                        "/data/local/tmp/westlake/apk_res",
                        System.getProperty("user.dir", ".") + "/apk_res"
                    };
                    for (String fb : fallbacks) {
                        if (new java.io.File(fb, "resources.arsc").exists()) {
                            effectiveResDir = fb;
                            System.out.println("[WestlakeLauncher] Using fallback resDir: " + fb);
                            break;
                        }
                    }
                }
                if (effectiveResDir != null && new java.io.File(effectiveResDir, "resources.arsc").exists()) {
                    // Use pre-extracted resources (host extracted them before spawning dalvikvm)
                    info = android.app.ApkLoader.loadFromExtracted(effectiveResDir, packageName);
                    // Store ApkInfo on MiniServer so LayoutInflater can find resDir
                    try {
                        java.lang.reflect.Field f = MiniServer.class.getDeclaredField("mApkInfo");
                        f.setAccessible(true);
                        f.set(server, info);
                    } catch (Exception ex) { System.out.println("[WestlakeLauncher] setApkInfo: " + ex); }
                    System.out.println("[WestlakeLauncher] Loaded from pre-extracted resources (resDir=" + info.resDir + ")");

                    // Wire resources to Application (same as MiniServer.loadApk does)
                    android.content.res.Resources res = server.getApplication().getResources();
                    if (info.resourceTable != null) {
                        ShimCompat.loadResourceTable(res, (android.content.res.ResourceTable) info.resourceTable);
                        System.out.println("[WestlakeLauncher] ResourceTable wired to Application");
                    }
                    // Set APK path for layout inflation (LayoutInflater reads AXML from here)
                    ShimCompat.setApkPath(res, apkPath);
                    // Set asset dir for extracted res/ layouts
                    if (info.assetDir != null) {
                        ShimCompat.setAssetDir(server.getApplication().getAssets(), info.assetDir);
                    }
                } else {
                    info = server.loadApk(apkPath);
                }
                System.out.println("[WestlakeLauncher] APK loaded: " + info);
                System.out.println("[WestlakeLauncher]   package: " + info.packageName);
                System.out.println("[WestlakeLauncher]   activities: " + info.activities);
                System.out.println("[WestlakeLauncher]   launcher: " + info.launcherActivity);
                System.out.println("[WestlakeLauncher]   dex paths: " + info.dexPaths);

                // Determine which activity to launch (declared before try for catch visibility)
                targetActivity = activityName;
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
                String launchPkg = info.packageName != null ? info.packageName : packageName;
                intent.setComponent(new ComponentName(launchPkg, targetActivity));
                am.startActivity(null, intent, -1);

                launchedActivity = am.getResumedActivity();
            } catch (Exception e) {
                System.out.println("[WestlakeLauncher] APK load error (non-fatal): " + e);
                // Fallback: launch activity directly if class is on classpath
                if (targetActivity != null && launchedActivity == null) {
                    try {
                        String pkg = packageName != null ? packageName : "app";
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(pkg, targetActivity));
                        am.startActivity(null, intent, -1);
                        launchedActivity = am.getResumedActivity();
                        System.out.println("[WestlakeLauncher] Fallback launch OK: " + targetActivity);
                    } catch (Exception e2) {
                        System.out.println("[WestlakeLauncher] Fallback launch failed: " + e2.getMessage());
                    }
                }
            }
        } else {
            System.out.println("[WestlakeLauncher] No APK path, nothing to launch");
        }

        // Try to get the launched activity even if errors occurred
        if (launchedActivity == null) {
            launchedActivity = am.getResumedActivity();
        }
        if (launchedActivity == null) {
            System.out.println("[WestlakeLauncher] WARNING: No activity, rendering empty surface");
        }
        if (launchedActivity != null) {
            System.out.println("[WestlakeLauncher] Activity launched: " + launchedActivity.getClass().getName());
        }

        // Render loop — render even if Activity partially failed
        if (nativeOk && launchedActivity != null) {
            System.out.println("[WestlakeLauncher] Creating surface " + SURFACE_WIDTH + "x" + SURFACE_HEIGHT);
            try {
                // Call onSurfaceCreated on Activity class directly (AppCompat subclasses may not find it)
                launchedActivity.onSurfaceCreated(0L, SURFACE_WIDTH, SURFACE_HEIGHT);
                launchedActivity.renderFrame();
            } catch (Exception e) {
                System.out.println("[WestlakeLauncher] Initial render error: " + e);
                e.printStackTrace();
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

            // Check for text input from host (long-press dialog)
            java.io.File textFile = new java.io.File(touchFile.getParent(), "westlake_text.dat");
            if (textFile.exists() && textFile.length() > 0) {
                try {
                    byte[] textBuf = new byte[(int) textFile.length()];
                    java.io.FileInputStream tis = new java.io.FileInputStream(textFile);
                    tis.read(textBuf);
                    tis.close();
                    textFile.delete();
                    String inputText = new String(textBuf, "UTF-8").trim();
                    if (inputText.length() > 0) {
                        android.view.View decor = null;
                        try { decor = current.getWindow().getDecorView(); } catch (Exception e5) {}
                        if (decor != null) {
                            android.widget.EditText et = findEditText(decor);
                            if (et != null) {
                                et.setText(inputText);
                                System.out.println("[WestlakeLauncher] Text input: '" + inputText + "' -> " + et);
                                needsRender = true;
                            } else {
                                System.out.println("[WestlakeLauncher] Text input: no EditText found");
                            }
                        }
                    }
                } catch (Exception e5) {
                    System.out.println("[WestlakeLauncher] Text input error: " + e5);
                }
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
                                        try { next.onSurfaceCreated(0L, SURFACE_WIDTH, SURFACE_HEIGHT); } catch (Exception e2) {}
                                        System.out.println("[WestlakeLauncher] Navigated to " + next.getClass().getSimpleName());
                                    }
                                    try { next.renderFrame(); } catch (Exception e2) {
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

            // Detect activity change (finish/navigate) and force re-render
            Activity nowResumed = am.getResumedActivity();
            if (nowResumed != null && nowResumed != current) {
                try { nowResumed.onSurfaceCreated(0L, SURFACE_WIDTH, SURFACE_HEIGHT); } catch (Exception e6) {}
                needsRender = true;
            }

            // Render if needed (text input, activity change, or other non-touch triggers)
            if (needsRender) {
                Activity next = am.getResumedActivity();
                if (next != null) {
                    try { next.renderFrame(); } catch (Exception e2) {}
                }
                needsRender = false;
            }

            frameCount++;
        }
        System.out.println("[WestlakeLauncher] Render loop ended after " + frameCount + " frames");
    }

    /** Find the deepest view containing the given point (absolute coords) */
    private static android.widget.EditText findEditText(android.view.View v) {
        if (v instanceof android.widget.EditText) return (android.widget.EditText) v;
        if (v instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                android.widget.EditText found = findEditText(vg.getChildAt(i));
                if (found != null) return found;
            }
        }
        return null;
    }

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
