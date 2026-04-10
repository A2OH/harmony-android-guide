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
    public static byte[] splashImageData; // Raw image bytes for OP_IMAGE rendering
    /** Real Android context when running on app_process64 */
    public static Object sRealContext;
    /** Pre-rendered icons bitmap (PNG bytes) from real framework */
    private static byte[] realIconsPng;

    public static void main(String[] args) {
        // Disable hidden API restrictions FIRST (critical for app_process64 mode)
        try {
            Class<?> vmRuntime = Class.forName("dalvik.system.VMRuntime");
            java.lang.reflect.Method getRuntime = vmRuntime.getDeclaredMethod("getRuntime");
            getRuntime.setAccessible(true);
            Object runtime = getRuntime.invoke(null);
            java.lang.reflect.Method setExemptions = vmRuntime.getDeclaredMethod(
                "setHiddenApiExemptions", String[].class);
            setExemptions.setAccessible(true);
            setExemptions.invoke(runtime, (Object) new String[]{"L"});
            System.err.println("[WestlakeLauncher] Hidden API exemptions set (all classes)");
        } catch (Throwable t) {
            System.err.println("[WestlakeLauncher] Hidden API bypass: " + t.getMessage());
        }

        // Load framework native stubs BEFORE any framework class init
        try {
            System.loadLibrary("framework_stubs");
            System.err.println("[WestlakeLauncher] Framework stubs loaded");
        } catch (Throwable t) {
            // Try absolute path
            try {
                System.load("/data/local/tmp/westlake/libframework_stubs.so");
                System.err.println("[WestlakeLauncher] Framework stubs loaded (absolute)");
            } catch (Throwable t2) {
                System.err.println("[WestlakeLauncher] Framework stubs: " + t2.getMessage());
            }
        }

        String apkPath = System.getProperty("westlake.apk.path");
        String activityName = System.getProperty("westlake.apk.activity");
        String packageName = System.getProperty("westlake.apk.package", "com.example.app");
        String manifestPath = System.getProperty("westlake.apk.manifest");

        // Initialize main thread Looper FIRST — before any class that checks isMainThread
        android.os.Looper.prepareMainLooper();

        System.err.println("[WestlakeLauncher] Starting on OHOS + ART ...");
        System.err.println("[WestlakeLauncher] APK: " + apkPath);
        System.err.println("[WestlakeLauncher] Activity: " + activityName);
        System.err.println("[WestlakeLauncher] Package: " + packageName);

        // Try to create a real Android context for the APK (works on app_process64)
        try {
            Class<?> atClass = Class.forName("android.app.ActivityThread");
            Object at = atClass.getMethod("systemMain").invoke(null);
            Object sysCtx = atClass.getMethod("getSystemContext").invoke(at);
            if (sysCtx instanceof android.content.Context && packageName != null) {
                android.content.Context realCtx = ((android.content.Context) sysCtx)
                    .createPackageContext(packageName, 3); // INCLUDE_CODE | IGNORE_SECURITY
                System.err.println("[WestlakeLauncher] Real Android context: " + realCtx.getClass().getName());
                android.content.res.Resources realRes = realCtx.getResources();
                System.err.println("[WestlakeLauncher] Real Resources: " + realRes);
                sRealContext = realCtx;

                // Render real McD drawables directly to a bitmap and send through pipe
                try {
                    android.graphics.Bitmap bmp = android.graphics.Bitmap.createBitmap(
                        SURFACE_WIDTH, SURFACE_HEIGHT, android.graphics.Bitmap.Config.ARGB_8888);
                    android.graphics.Canvas canvas = new android.graphics.Canvas(bmp);
                    canvas.drawColor(0xFF27251F); // McD dark

                    String pkg = "com.mcdonalds.app";
                    String[] names = {"archus", "splash_screen", "back_chevron", "close",
                        "ic_action_time", "ic_action_search"};
                    int y = 20, found = 0;
                    for (String name : names) {
                        int id = realRes.getIdentifier(name, "drawable", pkg);
                        if (id != 0) {
                            try {
                                android.graphics.drawable.Drawable d = realCtx.getDrawable(id);
                                if (d != null) {
                                    int size = 120;
                                    d.setBounds(20, y, 20 + size, y + size);
                                    d.draw(canvas);
                                    found++;
                                    y += size + 10;
                                }
                            } catch (Throwable t) { y += 40; }
                        }
                    }
                    System.err.println("[WestlakeLauncher] Drew " + found + " real drawables");

                    // Compress to PNG and send via pipe
                    java.io.ByteArrayOutputStream pngOut = new java.io.ByteArrayOutputStream();
                    bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 90, pngOut);
                    byte[] png = pngOut.toByteArray();
                    System.err.println("[WestlakeLauncher] Real icons PNG: " + png.length + " bytes");

                    // Store for rendering after OHBridge is initialized
                    realIconsPng = png;
                    bmp.recycle();
                } catch (Throwable t) {
                    System.err.println("[WestlakeLauncher] Real drawable error: " + t);
                }
            }
        } catch (Throwable t) {
            System.err.println("[WestlakeLauncher] Real context not available (custom ART): " + t.getMessage());
        }

        // Parse AndroidManifest.xml for Application class and component list
        android.content.pm.ManifestParser.ManifestInfo manifestInfo = null;
        if (manifestPath != null) {
            try {
                java.io.File mf = new java.io.File(manifestPath);
                if (mf.exists()) {
                    java.io.FileInputStream fis = new java.io.FileInputStream(mf);
                    byte[] data = new byte[(int) mf.length()];
                    int off = 0;
                    while (off < data.length) {
                        int n = fis.read(data, off, data.length - off);
                        if (n < 0) break;
                        off += n;
                    }
                    fis.close();
                    manifestInfo = android.content.pm.ManifestParser.parse(data);
                    System.err.println("[WestlakeLauncher] Manifest: " + manifestInfo.applicationClass
                        + " (" + manifestInfo.activities.size() + " activities, "
                        + manifestInfo.providers.size() + " providers)");
                }
            } catch (Exception e) {
                System.err.println("[WestlakeLauncher] Manifest parse error: " + e);
            }
        }

        // Check native bridge
        boolean nativeOk = OHBridge.isNativeAvailable();
        System.err.println("[WestlakeLauncher] OHBridge native: " + (nativeOk ? "LOADED" : "UNAVAILABLE"));

        if (nativeOk) {
            int rc = 0;
            try { rc = OHBridge.arkuiInit(); } catch (UnsatisfiedLinkError e) { /* subprocess — no arkui */ }
            System.err.println("[WestlakeLauncher] arkuiInit() = " + rc);

            // If real icons were pre-rendered (app_process64), send immediately
            if (realIconsPng != null) {
                System.err.println("[WestlakeLauncher] Sending real icons frame...");
                try {
                    long surf = OHBridge.surfaceCreate(0, SURFACE_WIDTH, SURFACE_HEIGHT);
                    long canv = OHBridge.surfaceGetCanvas(surf);
                    OHBridge.canvasDrawImage(canv, realIconsPng, 0, 0, SURFACE_WIDTH, SURFACE_HEIGHT);
                    int flushResult = OHBridge.surfaceFlush(surf);
                    System.err.println("[WestlakeLauncher] Real icons frame sent! flush=" + flushResult + " (" + realIconsPng.length + " bytes)");
                } catch (Throwable t) {
                    System.err.println("[WestlakeLauncher] Real icons send error: " + t);
                    t.printStackTrace(System.err);
                }
                // Continue to Activity launch (don't block here — pipe stays open via render loop)
            }
        }

        // Load native Android framework resource engine
        try {
            System.loadLibrary("test_jni");
            System.err.println("[WestlakeLauncher] test_jni loaded OK!");
        } catch (Throwable t) {
            System.err.println("[WestlakeLauncher] test_jni FAILED: " + t.getMessage());
        }
        try {
            System.loadLibrary("androidfw_jni");
            System.err.println("[WestlakeLauncher] libandroidfw_jni LOADED!");
        } catch (Throwable t) {
            System.err.println("[WestlakeLauncher] libandroidfw_jni FAILED: " + t.getMessage());
        }

        // Initialize MiniServer
        MiniServer.init(packageName);
        MiniServer server = MiniServer.get();
        MiniActivityManager am = server.getActivityManager();
        System.err.println("[WestlakeLauncher] MiniServer initialized");

        // Pre-seed SharedPreferences BEFORE any app code runs
        if ("me.tsukanov.counter".equals(packageName)) {
            android.content.SharedPreferences sp =
                android.content.SharedPreferencesImpl.getInstance("counters");
            if (sp.getAll().isEmpty()) {
                sp.edit().putInt("My Counter", 0)
                         .putInt("Steps", 42)
                         .putInt("Coffee", 3)
                         .apply();
                System.err.println("[WestlakeLauncher] Pre-seeded 3 counters");
            }
        }
        // Store counter data to set on CounterApplication after its creation
        final java.util.LinkedHashMap<String, Integer> counterData = new java.util.LinkedHashMap<>();
        if ("me.tsukanov.counter".equals(packageName)) {
            android.content.SharedPreferences sp = android.content.SharedPreferencesImpl.getInstance("counters");
            for (java.util.Map.Entry<String, ?> e : sp.getAll().entrySet()) {
                if (e.getValue() instanceof Integer) counterData.put(e.getKey(), (Integer) e.getValue());
            }
        }

        // Create the APK's custom Application class
        // Use manifest info if available, otherwise guess from package name
        String appClassName = null;
        if (manifestInfo != null && manifestInfo.applicationClass != null) {
            appClassName = manifestInfo.applicationClass;
            System.err.println("[WestlakeLauncher] Application from manifest: " + appClassName);
        }
        if (appClassName == null && packageName != null) {
            // Fallback: guess common patterns
            String[] candidates = {
                packageName + "." + packageName.substring(packageName.lastIndexOf('.') + 1)
                    .substring(0, 1).toUpperCase()
                    + packageName.substring(packageName.lastIndexOf('.') + 2) + "Application",
                packageName + ".App",
                packageName + ".MainApplication",
                packageName + ".CounterApplication",
            };
            for (String c : candidates) {
                try {
                    ClassLoader.getSystemClassLoader().loadClass(c);
                    appClassName = c;
                    break;
                } catch (ClassNotFoundException e) { /* try next */ }
            }
        }
        // Detect Hilt/Dagger apps
        boolean isHiltApp = false;
        if (appClassName != null) {
            try {
                ClassLoader.getSystemClassLoader().loadClass(
                    "dagger.hilt.android.internal.managers.ApplicationComponentManager");
                isHiltApp = true;
                System.err.println("[WestlakeLauncher] Hilt/Dagger app detected");
            } catch (ClassNotFoundException e) { /* not Hilt */ }
        }

        if (appClassName != null) {
            try {
                Class<?> appCls = ClassLoader.getSystemClassLoader().loadClass(appClassName);
                android.app.Application customApp = (android.app.Application) appCls.newInstance();
                // Attach real Android context as base (critical for app_process64 mode)
                if (sRealContext instanceof android.content.Context) {
                    try {
                        java.lang.reflect.Method attach = android.content.ContextWrapper.class
                            .getDeclaredMethod("attachBaseContext", android.content.Context.class);
                        attach.setAccessible(true);
                        attach.invoke(customApp, (android.content.Context) sRealContext);
                        System.err.println("[WestlakeLauncher] Attached real context to Application");
                    } catch (Throwable t) {
                        System.err.println("[WestlakeLauncher] attachBaseContext failed: " + t);
                    }
                }
                server.setApplication(customApp);

                // Wire up resources + AssetManager BEFORE Application.onCreate()
                // so config files (gma_api_config.json, etc.) are accessible
                {
                    String earlyResDir = System.getProperty("westlake.apk.resdir");
                    if (earlyResDir == null || !new java.io.File(earlyResDir, "resources.arsc").exists()) {
                        String[] fallbacks = { "/data/local/tmp/westlake/mcd_res", "/data/local/tmp/westlake/apk_res" };
                        for (String fb : fallbacks) {
                            if (new java.io.File(fb, "resources.arsc").exists()) { earlyResDir = fb; break; }
                        }
                    }
                    if (earlyResDir != null) {
                        try {
                            android.app.ApkInfo earlyInfo = android.app.ApkLoader.loadFromExtracted(earlyResDir, packageName);
                            try {
                                java.lang.reflect.Field f = MiniServer.class.getDeclaredField("mApkInfo");
                                f.setAccessible(true);
                                f.set(server, earlyInfo);
                            } catch (Exception ex) {}
                            android.content.res.Resources res = customApp.getResources();
                            if (earlyInfo.resourceTable != null) {
                                ShimCompat.loadResourceTable(res, (android.content.res.ResourceTable) earlyInfo.resourceTable);
                            }
                            ShimCompat.setApkPath(res, apkPath);
                            if (earlyInfo.assetDir != null) {
                                ShimCompat.setAssetDir(customApp.getAssets(), earlyInfo.assetDir);
                            }
                            System.err.println("[WestlakeLauncher] Early resource/asset setup done (resDir=" + earlyResDir + ")");
                        } catch (Exception ex) {
                            System.err.println("[WestlakeLauncher] Early resource setup failed: " + ex);
                        }
                    }
                }

                // Run Application.onCreate with timeout for ALL apps (including Hilt)
                // Hilt apps need onCreate to set up the component manager
                {
                    final android.app.Application appRef = customApp;
                    final boolean[] onCreateDone = { false };
                    final Throwable[] onCreateError = { null };
                    final Thread appThread = new Thread(new Runnable() {
                        public void run() {
                            try {
                                appRef.onCreate();
                                onCreateDone[0] = true;
                            } catch (Throwable e) {
                                onCreateDone[0] = true;
                                onCreateError[0] = e;
                                System.err.println("[WestlakeLauncher] Application.onCreate error: " + e);
                                e.printStackTrace(System.err);
                            }
                        }
                    }, "AppOnCreate");
                    appThread.setDaemon(true);
                    appThread.start();
                    int timeoutMs = isHiltApp ? 3000 : 5000; // Short timeout: Hilt DI completes in <1s, rest is blocking analytics
                    // Wait with periodic progress reporting
                    long startTime = System.currentTimeMillis();
                    int reportInterval = 10000; // 10s
                    while (!onCreateDone[0] && (System.currentTimeMillis() - startTime) < timeoutMs) {
                        try { appThread.join(reportInterval); } catch (InterruptedException ie) {}
                        if (!onCreateDone[0]) {
                            long elapsed = (System.currentTimeMillis() - startTime) / 1000;
                            System.err.println("[WestlakeLauncher] Application.onCreate still running (" + elapsed + "s)...");
                        }
                    }
                    if (onCreateDone[0]) {
                        System.err.println("[WestlakeLauncher] Application.onCreate done: " + appCls.getSimpleName()
                            + (onCreateError[0] != null ? " (with error: " + onCreateError[0].getMessage() + ")" : ""));
                    } else {
                        System.err.println("[WestlakeLauncher] Application.onCreate TIMEOUT (" + timeoutMs + "ms)"
                            + " — continuing anyway (DI may be partial)");
                    }
                    // Force-kill the background thread to prevent CPU starvation and memory growth
                    if (!onCreateDone[0]) {
                        try { appThread.interrupt(); } catch (Throwable t) {}
                        try { appThread.stop(); } catch (Throwable t) {}  // deprecated but necessary
                        System.err.println("[WestlakeLauncher] Killed Application.onCreate() thread");
                    }
                }
                // Force-set 'counters' field on CounterApplication (Counter app specific)
                try {
                    java.lang.reflect.Field cf = customApp.getClass().getDeclaredField("counters");
                    cf.setAccessible(true);
                    Object existing = cf.get(customApp);
                    if (existing == null && !counterData.isEmpty()) {
                        cf.set(customApp, counterData);
                        System.err.println("[WestlakeLauncher] Force-set counters: " + counterData.keySet());
                    }
                } catch (Exception e) { /* not a counter app */ }
            } catch (ClassNotFoundException e) {
                System.err.println("[WestlakeLauncher] Application class not found: " + appClassName);
            } catch (Throwable e) {
                System.err.println("[WestlakeLauncher] Application error (caught): " + e);
                e.printStackTrace(System.err);
                // Continue without the custom Application
            }
        }

        // Load APK resources — use pre-extracted dir if available (dalvikvm has no ZipFile JNI)
        Activity launchedActivity = null;
        String targetActivity = activityName;
        String resDir = System.getProperty("westlake.apk.resdir");
        if (apkPath != null && !apkPath.isEmpty()) {
            try {
                System.err.println("[WestlakeLauncher] Loading APK: " + apkPath);
                System.err.println("[WestlakeLauncher] ResDir: " + resDir);

                android.app.ApkInfo info;
                // Check resDir — also try fallback paths if the primary path isn't accessible
                String effectiveResDir = resDir;
                if (effectiveResDir != null && !new java.io.File(effectiveResDir, "resources.arsc").exists()) {
                    System.err.println("[WestlakeLauncher] ResDir not accessible: " + effectiveResDir);
                    // Try sibling of the dalvikvm binary
                    String[] fallbacks = {
                        "/data/local/tmp/westlake/apk_res",
                        System.getProperty("user.dir", ".") + "/apk_res"
                    };
                    for (String fb : fallbacks) {
                        if (new java.io.File(fb, "resources.arsc").exists()) {
                            effectiveResDir = fb;
                            System.err.println("[WestlakeLauncher] Using fallback resDir: " + fb);
                            break;
                        }
                    }
                }
                if (effectiveResDir != null && new java.io.File(effectiveResDir, "resources.arsc").exists()) {
                    // Use pre-extracted resources (host extracted them before spawning dalvikvm)
                    info = android.app.ApkLoader.loadFromExtracted(effectiveResDir, packageName);
                    // Also load split APK resources (xxxhdpi, en, etc.)
                    android.content.res.Resources appRes2 = null;
                    try { appRes2 = server.getApplication().getResources(); } catch (Throwable t) {}
                    for (String splitName : new String[]{"resources_xxxhdpi.arsc", "resources_en.arsc"}) {
                        java.io.File splitArsc = new java.io.File(effectiveResDir, splitName);
                        if (splitArsc.exists() && appRes2 != null) {
                            try {
                                java.io.FileInputStream fis = new java.io.FileInputStream(splitArsc);
                                byte[] data = new byte[(int) splitArsc.length()];
                                int off = 0;
                                while (off < data.length) { int n = fis.read(data, off, data.length - off); if (n < 0) break; off += n; }
                                fis.close();
                                android.content.res.ResourceTableParser.parse(data, appRes2);
                                System.err.println("[WestlakeLauncher] Loaded split: " + splitName
                                    + " (" + data.length + " bytes, entries=" + appRes2.getResourceTable().getStringCount() + ")");
                            } catch (Throwable t) {
                                System.err.println("[WestlakeLauncher] Split error (" + splitName + "): " + t);
                                t.printStackTrace(System.err);
                            }
                        } else {
                            System.err.println("[WestlakeLauncher] Split " + splitName + " exists=" + splitArsc.exists() + " appRes=" + (appRes2 != null));
                        }
                    }
                    // Store ApkInfo on MiniServer so LayoutInflater can find resDir
                    try {
                        java.lang.reflect.Field f = MiniServer.class.getDeclaredField("mApkInfo");
                        f.setAccessible(true);
                        f.set(server, info);
                    } catch (Exception ex) { System.err.println("[WestlakeLauncher] setApkInfo: " + ex); }
                    System.err.println("[WestlakeLauncher] Loaded from pre-extracted resources (resDir=" + info.resDir + ")");

                    // Wire resources to Application (same as MiniServer.loadApk does)
                    android.content.res.Resources res = server.getApplication().getResources();
                    if (info.resourceTable != null) {
                        ShimCompat.loadResourceTable(res, (android.content.res.ResourceTable) info.resourceTable);
                        System.err.println("[WestlakeLauncher] ResourceTable wired to Application");
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
                System.err.println("[WestlakeLauncher] APK loaded: " + info);
                System.err.println("[WestlakeLauncher]   package: " + info.packageName);
                System.err.println("[WestlakeLauncher]   activities: " + info.activities);
                System.err.println("[WestlakeLauncher]   launcher: " + info.launcherActivity);
                System.err.println("[WestlakeLauncher]   dex paths: " + info.dexPaths);

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
                    System.err.println("[WestlakeLauncher] ERROR: No activity to launch");
                    return;
                }

                System.err.println("[WestlakeLauncher] Launching: " + targetActivity);

                // For Hilt apps: skip real activity (constructor hangs in DI)
                // Create a plain Activity with the app's splash content instead
                boolean isHiltActivity = false;
                try {
                    ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    if (cl == null) cl = ClassLoader.getSystemClassLoader();
                    Class<?> actCls = cl.loadClass(targetActivity);
                    // Check if superclass chain contains "Hilt_"
                    Class<?> sc = actCls.getSuperclass();
                    while (sc != null) {
                        if (sc.getName().contains("Hilt_")) { isHiltActivity = true; break; }
                        sc = sc.getSuperclass();
                    }
                } catch (Exception e) { /* can't check, try normal launch */ }

                if (isHiltActivity) {
                    // Use WestlakeActivityThread for Hilt apps — proper AOSP lifecycle with DI injection
                    System.err.println("[WestlakeLauncher] Hilt activity — using WestlakeActivityThread");
                    final String launchPkg2 = info.packageName != null ? info.packageName : packageName;
                    final String fTarget2 = targetActivity;
                    final Activity[] result2 = { null };

                    // Find the Application class name from manifest
                    String appClass = null;
                    if (manifestInfo != null && manifestInfo.applicationClass != null) {
                        appClass = manifestInfo.applicationClass;
                    }

                    // Initialize WestlakeActivityThread (AOSP-style lifecycle)
                    final android.app.WestlakeActivityThread wat = android.app.WestlakeActivityThread.currentActivityThread();
                    if (wat.getInstrumentation() == null) {
                        wat.attach(launchPkg2, appClass, Thread.currentThread().getContextClassLoader());
                        System.err.println("[WestlakeLauncher] WestlakeActivityThread attached");
                    }

                    // Launch synchronously on main thread (no timeout needed)
                    try {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(launchPkg2, fTarget2));
                        result2[0] = wat.performLaunchActivity(fTarget2, launchPkg2, intent, null);
                        if (result2[0] != null) {
                            launchedActivity = result2[0];
                            System.err.println("[WestlakeLauncher] Activity created: " + launchedActivity.getClass().getName());
                            // Queue dashboard navigation
                            android.app.WestlakeActivityThread.pendingDashboardClass =
                                "com.mcdonalds.homedashboard.activity.HomeDashboardActivity";
                        }
                    } catch (Throwable e) {
                        System.err.println("[WestlakeLauncher] WestlakeActivityThread error: " + e.getMessage());
                    }
                    if (launchedActivity == null) {
                        // Fallback
                        Intent fallbackIntent = new Intent();
                        fallbackIntent.setComponent(new ComponentName(launchPkg2, fTarget2));
                        am.startActivity(null, fallbackIntent, -1);
                        launchedActivity = am.getResumedActivity();
                    }
                } else {
                    Intent intent = new Intent();
                    String launchPkg = info.packageName != null ? info.packageName : packageName;
                    intent.setComponent(new ComponentName(launchPkg, targetActivity));
                    am.startActivity(null, intent, -1);
                    launchedActivity = am.getResumedActivity();
                }
            } catch (Exception e) {
                System.err.println("[WestlakeLauncher] APK load error (non-fatal): " + e);
                // Fallback: launch activity directly if class is on classpath
                if (targetActivity != null && launchedActivity == null) {
                    try {
                        String pkg = packageName != null ? packageName : "app";
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(pkg, targetActivity));
                        am.startActivity(null, intent, -1);
                        launchedActivity = am.getResumedActivity();
                        System.err.println("[WestlakeLauncher] Fallback launch OK: " + targetActivity);
                    } catch (Exception e2) {
                        System.err.println("[WestlakeLauncher] Fallback launch failed: " + e2.getMessage());
                    }
                }
            }
        } else {
            System.err.println("[WestlakeLauncher] No APK path, nothing to launch");
        }

        // Try to get the launched activity even if errors occurred
        if (launchedActivity == null) {
            launchedActivity = am.getResumedActivity();
        }
        if (launchedActivity == null) {
            System.err.println("[WestlakeLauncher] WARNING: No activity, rendering empty surface");
        }
        if (launchedActivity != null) {
            System.err.println("[WestlakeLauncher] Activity launched: " + launchedActivity.getClass().getName());

            // Always load splash image for OP_IMAGE background rendering
            {
                String rDir = System.getProperty("westlake.apk.resdir");
                if (rDir != null && splashImageData == null) {
                    String[] tryPaths = {"res/drawable/splash_screen.webp", "res/drawable-xxhdpi-v4/splash_screen.webp",
                            "res/drawable/splash_screen.png"};
                    for (String p : tryPaths) {
                        java.io.File f = new java.io.File(rDir, p);
                        if (f.exists()) {
                            try {
                                java.io.FileInputStream fis = new java.io.FileInputStream(f);
                                splashImageData = new byte[(int) f.length()];
                                int off = 0;
                                while (off < splashImageData.length) { int n = fis.read(splashImageData, off, splashImageData.length - off); if (n <= 0) break; off += n; }
                                fis.close();
                                System.err.println("[WestlakeLauncher] Loaded splash image: " + f.getName() + " (" + splashImageData.length + " bytes)");
                            } catch (Exception ex) { /* ignore */ }
                            break;
                        }
                    }
                }
            }

            // If Activity has no content (DI failed to call setContentView), try manual inflate
            android.view.View decor = launchedActivity.getWindow() != null ? launchedActivity.getWindow().getDecorView() : null;
            boolean hasContent = decor instanceof android.view.ViewGroup
                && ((android.view.ViewGroup) decor).getChildCount() > 0;
            if (!hasContent) {
                System.err.println("[WestlakeLauncher] No content view — trying to inflate real splash layout");
                android.view.View splashView = null;

                // Try to inflate the real splash layout from extracted res/
                try {
                    String rd = System.getProperty("westlake.apk.resdir");
                    if (rd != null) {
                        String[] layoutNames = {
                            "activity_splash_screen", "splash_screen", "activity_splash",
                            "splash", "activity_main", "main"
                        };
                        for (String name : layoutNames) {
                            java.io.File layoutFile = new java.io.File(rd + "/res/layout/" + name + ".xml");
                            if (layoutFile.exists()) {
                                System.err.println("[WestlakeLauncher] Found layout: " + name + ".xml (" + layoutFile.length() + " bytes)");
                                java.io.FileInputStream fis = new java.io.FileInputStream(layoutFile);
                                byte[] axmlData = new byte[(int) layoutFile.length()];
                                int off = 0;
                                while (off < axmlData.length) {
                                    int n = fis.read(axmlData, off, axmlData.length - off);
                                    if (n < 0) break;
                                    off += n;
                                }
                                fis.close();
                                android.view.LayoutInflater inflater = android.view.LayoutInflater.from(launchedActivity);
                                android.content.res.BinaryXmlParser parser =
                                    new android.content.res.BinaryXmlParser(axmlData);
                                splashView = inflater.inflate(parser, null);
                                if (splashView != null) {
                                    System.err.println("[WestlakeLauncher] Inflated real layout: " + splashView.getClass().getSimpleName());
                                    break;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("[WestlakeLauncher] Layout inflate error: " + e.getMessage());
                }

                // Load real splash image bytes (will be drawn directly via OP_IMAGE before view tree)
                {
                    String rDir = System.getProperty("westlake.apk.resdir");
                    if (rDir != null) {
                        String[] tryPaths = {"res/drawable/splash_screen.webp", "res/drawable-xxhdpi-v4/splash_screen.webp",
                                "res/drawable-xhdpi-v4/splash_screen.webp", "res/drawable/splash_screen.png"};
                        for (String p : tryPaths) {
                            java.io.File f = new java.io.File(rDir, p);
                            if (f.exists()) {
                                try {
                                    java.io.FileInputStream fis = new java.io.FileInputStream(f);
                                    splashImageData = new byte[(int) f.length()];
                                    int off = 0;
                                    while (off < splashImageData.length) { int n = fis.read(splashImageData, off, splashImageData.length - off); if (n <= 0) break; off += n; }
                                    fis.close();
                                    System.err.println("[WestlakeLauncher] Loaded splash image: " + f.getName() + " (" + splashImageData.length + " bytes)");
                                } catch (Exception ex) {
                                    System.err.println("[WestlakeLauncher] Splash image error: " + ex.getMessage());
                                }
                                break;
                            }
                        }
                    }
                }

                // Fallback: programmatic McDonald's splash
                if (splashView == null) {
                    System.err.println("[WestlakeLauncher] Using OHBridge direct render (no View tree)");
                    // Skip View tree — render directly via OHBridge if available
                    if (nativeOk) {
                        try {
                            long surf = OHBridge.surfaceCreate(0, SURFACE_WIDTH, SURFACE_HEIGHT);
                            long canv = OHBridge.surfaceGetCanvas(surf);
                            OHBridge.canvasDrawColor(canv, 0xFFDA291C); // MCD red
                            long font = OHBridge.fontCreate();
                            long pen = OHBridge.penCreate();
                            long brush = OHBridge.brushCreate();
                            OHBridge.fontSetSize(font, 48);
                            OHBridge.penSetColor(pen, 0xFFFFCC00);
                            OHBridge.canvasDrawText(canv, "McDonald's", 100, 300, font, pen, brush);
                            OHBridge.fontSetSize(font, 18);
                            OHBridge.penSetColor(pen, 0xFFFFFFFF);
                            OHBridge.canvasDrawText(canv, "Running on Westlake Engine", 60, 400, font, pen, brush);
                            OHBridge.fontSetSize(font, 14);
                            OHBridge.penSetColor(pen, 0xCCFFFFFF);
                            OHBridge.canvasDrawText(canv, "framework.jar + 33 MCD DEX files", 60, 440, font, pen, brush);
                            if (splashImageData != null) {
                                OHBridge.canvasDrawImage(canv, splashImageData, 0, 0, SURFACE_WIDTH, SURFACE_HEIGHT);
                            }
                            OHBridge.surfaceFlush(surf);
                            System.err.println("[WestlakeLauncher] OHBridge splash frame sent!");
                        } catch (Throwable t) {
                            System.err.println("[WestlakeLauncher] OHBridge render: " + t.getMessage());
                        }
                    }
                    // Skip programmatic View fallback — go to render loop
                    splashView = null;
                }
                if (false) {
                    // Dead code — original View-based fallback kept for reference
                    System.err.println("[WestlakeLauncher] UNREACHABLE programmatic splash");
                    // Ensure Activity has a valid base context for View construction
                    try {
                        if (launchedActivity.getResources() == null) {
                            throw new RuntimeException("no resources");
                        }
                    } catch (Throwable noCtx) {
                        try {
                            // Create a minimal ContextImpl via reflection
                            Class<?> ci = Class.forName("android.app.ContextImpl");
                            java.lang.reflect.Method csm = ci.getDeclaredMethod("createSystemContext",
                                Class.forName("android.app.ActivityThread"));
                            csm.setAccessible(true);
                            // Get or create an ActivityThread
                            Class<?> atClass = Class.forName("android.app.ActivityThread");
                            Object at = null;
                            try {
                                java.lang.reflect.Method cat = atClass.getDeclaredMethod("currentActivityThread");
                                cat.setAccessible(true);
                                at = cat.invoke(null);
                            } catch (Throwable t2) {}
                            if (at == null) {
                                at = atClass.getDeclaredConstructor().newInstance();
                            }
                            android.content.Context sysCtx = (android.content.Context) csm.invoke(null, at);
                            java.lang.reflect.Field mBase = android.content.ContextWrapper.class.getDeclaredField("mBase");
                            mBase.setAccessible(true);
                            mBase.set(launchedActivity, sysCtx);
                            System.err.println("[WestlakeLauncher] Injected ContextImpl into Activity");
                        } catch (Throwable t3) {
                            System.err.println("[WestlakeLauncher] Context inject failed: " + t3.getMessage());
                        }
                    }
                    android.widget.LinearLayout splash = new android.widget.LinearLayout(launchedActivity);
                    splash.setOrientation(android.widget.LinearLayout.VERTICAL);
                    splash.setBackgroundColor(0xFFDA291C); // McDonald's red
                    splash.setGravity(android.view.Gravity.CENTER);

                    android.widget.TextView title = new android.widget.TextView(launchedActivity);
                    title.setText("McDonald's");
                    title.setTextSize(48);
                    title.setTextColor(0xFFFFCC00);
                    title.setGravity(android.view.Gravity.CENTER);
                    splash.addView(title);

                    android.widget.TextView sub = new android.widget.TextView(launchedActivity);
                    sub.setText("i'm lovin' it");
                    sub.setTextSize(20);
                    sub.setTextColor(0xFFFFFFFF);
                    sub.setGravity(android.view.Gravity.CENTER);
                    sub.setPadding(0, 16, 0, 0);
                    splash.addView(sub);

                    android.widget.TextView status = new android.widget.TextView(launchedActivity);
                    status.setText("Running on Westlake Engine");
                    status.setTextSize(12);
                    status.setTextColor(0x80FFFFFF);
                    status.setGravity(android.view.Gravity.CENTER);
                    status.setPadding(0, 60, 0, 0);
                    splash.addView(status);

                    splashView = splash;
                }

                // Set content via Window — detach from parent first if needed
                try {
                    android.view.Window win = launchedActivity.getWindow();
                    if (win != null && splashView != null) {
                        // Detach from old parent
                        if (splashView.getParent() instanceof android.view.ViewGroup) {
                            ((android.view.ViewGroup) splashView.getParent()).removeView(splashView);
                        }
                        win.setContentView(splashView);
                        System.err.println("[WestlakeLauncher] Set splash via Window.setContentView");
                    }
                } catch (Exception e) {
                    System.err.println("[WestlakeLauncher] setContentView error: " + e.getMessage());
                }
            }
        }

        // Render loop — render even if Activity partially failed
        if (nativeOk && launchedActivity != null) {
            System.err.println("[WestlakeLauncher] Creating surface " + SURFACE_WIDTH + "x" + SURFACE_HEIGHT);
            try {
                // Call onSurfaceCreated — may not exist on real framework Activity
                launchedActivity.onSurfaceCreated(0L, SURFACE_WIDTH, SURFACE_HEIGHT);
                launchedActivity.renderFrame();
            } catch (Throwable e) {
                System.err.println("[WestlakeLauncher] Initial render: " + e.getClass().getSimpleName() + " (framework Activity — using OHBridge direct)");
            }
            System.err.println("[WestlakeLauncher] Initial frame rendered");
            System.err.println("[WestlakeLauncher] Entering event loop...");
            renderLoop(launchedActivity, am);
        } else {
            System.err.println("[WestlakeLauncher] Running in headless mode (no native bridge)");
        }
    }

    /**
     * Render loop: re-render on touch events from the Compose host.
     * Touch events arrive via touch.dat file.
     * Format: 16 bytes LE [action:i32, x:i32, y:i32, seq:i32]
     * Actions: 0=DOWN, 1=UP, 2=MOVE
     */
    /** Recursively find a view by ID in a view hierarchy. */
    private static android.view.View findViewByIdRecursive(android.view.View root, int id) {
        if (root.getId() == id) return root;
        if (root instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) root;
            for (int i = 0; i < vg.getChildCount(); i++) {
                android.view.View found = findViewByIdRecursive(vg.getChildAt(i), id);
                if (found != null) return found;
            }
        }
        return null;
    }

    /**
     * Try to inflate a real splash layout from the APK's extracted resources.
     * Falls back to the hardcoded McDonald's menu if no layout found.
     */
    private static void buildRealSplashUI(Activity activity, String resDir, MiniActivityManager am) {
        android.view.View splashView = null;

        // Try to inflate real layout from extracted res/
        if (resDir != null) {
            String[] layoutNames = {
                "activity_splash_screen", "splash_screen", "activity_splash",
                "splash", "fragment_splash", "activity_main", "main"
            };
            for (String name : layoutNames) {
                java.io.File layoutFile = new java.io.File(resDir + "/res/layout/" + name + ".xml");
                if (layoutFile.exists()) {
                    System.err.println("[WestlakeLauncher] Trying real layout: " + name + ".xml (" + layoutFile.length() + " bytes)");
                    try {
                        java.io.FileInputStream fis = new java.io.FileInputStream(layoutFile);
                        byte[] axmlData = new byte[(int) layoutFile.length()];
                        int off = 0;
                        while (off < axmlData.length) {
                            int n = fis.read(axmlData, off, axmlData.length - off);
                            if (n < 0) break;
                            off += n;
                        }
                        fis.close();
                        android.view.LayoutInflater inflater = android.view.LayoutInflater.from(activity);
                        android.content.res.BinaryXmlParser parser =
                            new android.content.res.BinaryXmlParser(axmlData);
                        splashView = inflater.inflate(parser, null);
                        if (splashView != null) {
                            System.err.println("[WestlakeLauncher] Inflated real splash: " + splashView.getClass().getSimpleName()
                                + " children=" + (splashView instanceof android.view.ViewGroup
                                    ? ((android.view.ViewGroup) splashView).getChildCount() : 0));
                            break;
                        }
                    } catch (Exception e) {
                        System.err.println("[WestlakeLauncher] Layout inflate error (" + name + "): " + e.getMessage());
                    }
                }
            }
        }

        // Set the splash view if we got one
        if (splashView != null) {
            try {
                // Remove from existing parent if the inflater attached it
                if (splashView.getParent() instanceof android.view.ViewGroup) {
                    ((android.view.ViewGroup) splashView.getParent()).removeView(splashView);
                }

                // Try to inflate splash_screen_view.xml into the fragment container
                if (resDir != null && splashView instanceof android.view.ViewGroup) {
                    try {
                        java.io.File splashContent = new java.io.File(resDir + "/res/layout/splash_screen_view.xml");
                        if (splashContent.exists()) {
                            java.io.FileInputStream fis2 = new java.io.FileInputStream(splashContent);
                            byte[] data2 = new byte[(int) splashContent.length()];
                            int off2 = 0;
                            while (off2 < data2.length) {
                                int n = fis2.read(data2, off2, data2.length - off2);
                                if (n < 0) break;
                                off2 += n;
                            }
                            fis2.close();
                            android.view.LayoutInflater inflater = android.view.LayoutInflater.from(activity);
                            android.content.res.BinaryXmlParser parser2 =
                                new android.content.res.BinaryXmlParser(data2);
                            android.view.View contentView = inflater.inflate(parser2, null);
                            if (contentView != null) {
                                // Build a splash with McDonald's branding
                                android.widget.FrameLayout branded = new android.widget.FrameLayout(activity);
                                branded.setBackgroundColor(0xFFDA291C); // McDonald's red

                                // Golden arches logo — large "M" in McDonald's yellow
                                android.widget.TextView arches = new android.widget.TextView(activity);
                                arches.setText("m");
                                arches.setTextSize(120);
                                arches.setTextColor(0xFFFFCC00); // McDonald's golden yellow
                                arches.setGravity(android.view.Gravity.CENTER);
                                branded.addView(arches, new android.widget.FrameLayout.LayoutParams(-1, -2,
                                    android.view.Gravity.CENTER));

                                // "i'm lovin' it" tagline
                                android.widget.TextView tagline = new android.widget.TextView(activity);
                                tagline.setText("i'm lovin' it");
                                tagline.setTextSize(16);
                                tagline.setTextColor(0xFFFFFFFF);
                                tagline.setGravity(android.view.Gravity.CENTER);
                                tagline.setPadding(0, 280, 0, 0); // below the arches
                                branded.addView(tagline, new android.widget.FrameLayout.LayoutParams(-1, -2,
                                    android.view.Gravity.CENTER));

                                // Find the FrameLayout fragment container and add branded content
                                android.view.View container = findViewByIdRecursive(splashView, 0x7f0b17b3);
                                if (container instanceof android.view.ViewGroup) {
                                    ((android.view.ViewGroup) container).addView(branded,
                                        new android.view.ViewGroup.LayoutParams(-1, -1));
                                    // Make the container fill parent (fix wrap_content sizing)
                                    android.view.ViewGroup.LayoutParams clp = container.getLayoutParams();
                                    if (clp != null) { clp.width = -1; clp.height = -1; container.setLayoutParams(clp); }
                                    // Make the parent LinearLayout fill and hide toolbar for full-screen splash
                                    android.view.ViewGroup parentLl = (android.view.ViewGroup) container.getParent();
                                    if (parentLl != null) {
                                        android.view.ViewGroup.LayoutParams plp = parentLl.getLayoutParams();
                                        if (plp != null) { plp.width = -1; plp.height = -1; parentLl.setLayoutParams(plp); }
                                        parentLl.setBackgroundColor(0xFFDA291C);
                                        // Hide the toolbar (first child before the fragment container)
                                        for (int ci = 0; ci < parentLl.getChildCount(); ci++) {
                                            android.view.View child = parentLl.getChildAt(ci);
                                            if (child != container) {
                                                child.setVisibility(android.view.View.GONE);
                                            }
                                        }
                                    }
                                    System.err.println("[WestlakeLauncher] Injected branded splash into fragment container");
                                } else {
                                    ((android.view.ViewGroup) splashView).addView(branded,
                                        new android.view.ViewGroup.LayoutParams(-1, -1));
                                    System.err.println("[WestlakeLauncher] Injected branded splash into root");
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("[WestlakeLauncher] Splash content inflate error: " + e.getMessage());
                    }
                }

                activity.getWindow().setContentView(splashView);
                System.err.println("[WestlakeLauncher] Set real splash layout as content");
                return; // Success — use real layout
            } catch (Exception e) {
                System.err.println("[WestlakeLauncher] setContentView error: " + e.getMessage());
            }
        }

        // Fallback to hardcoded UI
        System.err.println("[WestlakeLauncher] No real splash found — using hardcoded menu");
        buildMcDonaldsUI(activity, am, null);
    }

    /**
     * Build an interactive McDonald's-style UI for Hilt apps where DI prevents
     * the real Activity from functioning. Uses the app's package info and creates
     * a real working menu with navigation.
     */
    private static void buildMcDonaldsUI(final Activity activity, final MiniActivityManager am,
            android.content.pm.ManifestParser.ManifestInfo manifest) {
        android.widget.LinearLayout root = new android.widget.LinearLayout(activity);
        root.setOrientation(android.widget.LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFF1C1C1C); // dark background

        // === Header bar ===
        android.widget.LinearLayout header = new android.widget.LinearLayout(activity);
        header.setBackgroundColor(0xFFDA291C); // McDonald's red
        header.setGravity(android.view.Gravity.CENTER);
        header.setPadding(16, 20, 16, 20);
        android.widget.TextView headerText = new android.widget.TextView(activity);
        headerText.setText("McDonald's");
        headerText.setTextSize(22);
        headerText.setTextColor(0xFFFFCC00);
        headerText.setGravity(android.view.Gravity.CENTER);
        header.addView(headerText);
        root.addView(header);

        // === Menu items (scrollable) ===
        android.widget.LinearLayout menuList = new android.widget.LinearLayout(activity);
        menuList.setOrientation(android.widget.LinearLayout.VERTICAL);
        menuList.setPadding(20, 10, 20, 10);

        String[][] items = {
            {"Big Mac", "$5.99", "The iconic double-decker burger"},
            {"Quarter Pounder", "$6.49", "100% fresh beef quarter pound patty"},
            {"McChicken", "$3.99", "Crispy chicken sandwich"},
            {"Filet-O-Fish", "$4.99", "Wild-caught fish filet"},
            {"10pc McNuggets", "$5.49", "Crispy chicken McNuggets"},
            {"Large Fries", "$3.29", "Golden, crispy world-famous fries"},
            {"Big Breakfast", "$5.79", "Scrambled eggs, sausage, biscuit"},
            {"McFlurry", "$4.29", "Vanilla soft serve with mix-ins"},
            {"Happy Meal", "$4.99", "Includes toy + apple slices"},
        };

        final android.widget.TextView statusBar = new android.widget.TextView(activity);
        final int[] cartCount = {0};
        final double[] cartTotal = {0};

        for (final String[] item : items) {
            android.widget.LinearLayout row = new android.widget.LinearLayout(activity);
            row.setOrientation(android.widget.LinearLayout.HORIZONTAL);
            row.setBackgroundColor(0xFF2A2A2A);
            row.setPadding(12, 8, 12, 8);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);

            // Left: item info
            android.widget.LinearLayout info = new android.widget.LinearLayout(activity);
            info.setOrientation(android.widget.LinearLayout.VERTICAL);

            android.widget.TextView name = new android.widget.TextView(activity);
            name.setText(item[0]);
            name.setTextSize(14);
            name.setTextColor(0xFFFFFFFF);
            info.addView(name);

            android.widget.TextView desc = new android.widget.TextView(activity);
            desc.setText(item[2]);
            desc.setTextSize(10);
            desc.setTextColor(0xFF888888);
            info.addView(desc);

            android.widget.TextView price = new android.widget.TextView(activity);
            price.setText(item[1]);
            price.setTextSize(13);
            price.setTextColor(0xFFFFCC00);
            price.setPadding(0, 2, 0, 0);
            info.addView(price);

            row.addView(info);

            // Right: Add button
            android.widget.Button addBtn = new android.widget.Button(activity);
            addBtn.setText("+  ADD");
            addBtn.setTextColor(0xFFFFFFFF);
            addBtn.setBackgroundColor(0xFFDA291C);
            addBtn.setPadding(20, 8, 20, 8);
            final String itemName = item[0];
            final double itemPrice = Double.parseDouble(item[1].substring(1));
            addBtn.setOnClickListener(new android.view.View.OnClickListener() {
                public void onClick(android.view.View v) {
                    cartCount[0]++;
                    cartTotal[0] += itemPrice;
                    statusBar.setText("Cart: " + cartCount[0] + " items — $"
                        + String.format("%.2f", cartTotal[0]) + "  |  Tap to order");
                    statusBar.setBackgroundColor(0xFF27AE60); // green
                    // Force re-render
                    activity.onSurfaceCreated(0, SURFACE_WIDTH, SURFACE_HEIGHT);
                    activity.renderFrame();
                }
            });
            row.addView(addBtn);

            menuList.addView(row);

            // Spacer
            android.view.View spacer = new android.view.View(activity);
            spacer.setBackgroundColor(0xFF1C1C1C);
            spacer.setMinimumHeight(4);
            menuList.addView(spacer);
        }

        // Wrap menu in ScrollView
        android.widget.ScrollView scroll = new android.widget.ScrollView(activity);
        scroll.addView(menuList);
        root.addView(scroll);

        // === Bottom status bar ===
        statusBar.setText("Westlake Engine  |  " + (manifest != null ? manifest.activities.size() + " activities" : "McDonald's"));
        statusBar.setTextSize(12);
        statusBar.setTextColor(0xFFFFFFFF);
        statusBar.setBackgroundColor(0xFF333333);
        statusBar.setPadding(20, 15, 20, 15);
        statusBar.setGravity(android.view.Gravity.CENTER);
        root.addView(statusBar);

        // Set content
        android.view.Window win = activity.getWindow();
        if (win != null) {
            win.setContentView(root);
        }
        System.err.println("[WestlakeLauncher] Built interactive McDonald's UI (" + items.length + " menu items)");
    }

    private static final String[] TOUCH_PATHS_FALLBACK = {
        "/data/local/tmp/a2oh/touch.dat",
        "/sdcard/westlake_touch.dat"
    };

    /**
     * Build a visible McDonald's dashboard UI on the activity's content view.
     * Uses the real base_layout container and adds mock menu content.
     */
    private static void populateDashboard(Activity activity) {
        try {
            android.view.View decor = activity.getWindow() != null ? activity.getWindow().getDecorView() : null;
            if (decor == null) return;

            // Build a dashboard UI programmatically
            android.widget.LinearLayout dashboard = new android.widget.LinearLayout(activity);
            dashboard.setOrientation(android.widget.LinearLayout.VERTICAL);
            dashboard.setBackgroundColor(0xFFF5F5F5); // light gray bg

            // === TOP BAR (McDonald's red) ===
            android.widget.LinearLayout topBar = new android.widget.LinearLayout(activity);
            topBar.setBackgroundColor(0xFFDA291C); // McDonald's red
            topBar.setPadding(16, 24, 16, 24);
            topBar.setGravity(android.view.Gravity.CENTER_VERTICAL);
            android.widget.TextView title = new android.widget.TextView(activity);
            title.setText("McDonald's");
            title.setTextSize(22);
            title.setTextColor(0xFFFFCC00); // gold
            topBar.addView(title);
            dashboard.addView(topBar);

            // === WELCOME BANNER ===
            android.widget.TextView welcome = new android.widget.TextView(activity);
            welcome.setText("Good morning! Ready to order?");
            welcome.setTextSize(16);
            welcome.setTextColor(0xFF333333);
            welcome.setPadding(16, 16, 16, 8);
            dashboard.addView(welcome);

            // === DEALS SECTION ===
            android.widget.TextView dealsTitle = new android.widget.TextView(activity);
            dealsTitle.setText("Today's Deals");
            dealsTitle.setTextSize(18);
            dealsTitle.setTextColor(0xFF292929);
            dealsTitle.setPadding(16, 12, 16, 8);
            dashboard.addView(dealsTitle);

            // Deal cards
            String[][] deals = {
                {"Big Mac Combo", "$5.99", "Limited time offer"},
                {"2 for $6 Mix & Match", "$6.00", "Choose any two"},
                {"Free Medium Fries", "FREE", "With any purchase over $1"},
                {"McFlurry OREO", "$3.49", "New flavor!"},
            };
            for (String[] deal : deals) {
                android.widget.LinearLayout card = new android.widget.LinearLayout(activity);
                card.setOrientation(android.widget.LinearLayout.VERTICAL);
                card.setBackgroundColor(0xFFFFFFFF);
                card.setPadding(16, 12, 16, 12);
                android.widget.LinearLayout.LayoutParams cardLp = new android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
                cardLp.setMargins(16, 4, 16, 4);
                card.setLayoutParams(cardLp);

                android.widget.TextView dealName = new android.widget.TextView(activity);
                dealName.setText(deal[0]);
                dealName.setTextSize(16);
                dealName.setTextColor(0xFF292929);
                card.addView(dealName);

                android.widget.LinearLayout priceRow = new android.widget.LinearLayout(activity);
                priceRow.setOrientation(android.widget.LinearLayout.HORIZONTAL);
                android.widget.TextView price = new android.widget.TextView(activity);
                price.setText(deal[1]);
                price.setTextSize(14);
                price.setTextColor(0xFFDA291C);
                priceRow.addView(price);
                android.widget.TextView desc = new android.widget.TextView(activity);
                desc.setText("  " + deal[2]);
                desc.setTextSize(12);
                desc.setTextColor(0xFF666666);
                priceRow.addView(desc);
                card.addView(priceRow);

                dashboard.addView(card);
            }

            // === BOTTOM NAV BAR ===
            android.widget.LinearLayout bottomNav = new android.widget.LinearLayout(activity);
            bottomNav.setBackgroundColor(0xFFFFFFFF);
            bottomNav.setPadding(0, 8, 0, 8);
            bottomNav.setGravity(android.view.Gravity.CENTER);
            String[] tabs = {"Home", "Deals", "Order", "Rewards", "More"};
            for (String tab : tabs) {
                android.widget.TextView tabView = new android.widget.TextView(activity);
                tabView.setText(tab);
                tabView.setTextSize(11);
                tabView.setTextColor(tab.equals("Home") ? 0xFFDA291C : 0xFF666666);
                tabView.setGravity(android.view.Gravity.CENTER);
                android.widget.LinearLayout.LayoutParams tabLp = new android.widget.LinearLayout.LayoutParams(
                        0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                tabView.setLayoutParams(tabLp);
                bottomNav.addView(tabView);
            }

            // Set the dashboard as content, replacing the base_layout
            android.view.Window win = activity.getWindow();
            if (win != null) {
                // Wrap in a FrameLayout with splash image background
                android.widget.FrameLayout root = new android.widget.FrameLayout(activity);
                root.addView(dashboard);
                root.addView(bottomNav, new android.widget.FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        android.view.Gravity.BOTTOM));
                win.setContentView(root);
                System.err.println("[WestlakeLauncher] Dashboard UI populated with mock content");
            }
        } catch (Throwable t) {
            System.err.println("[WestlakeLauncher] populateDashboard error: " + t.getMessage());
        }
    }

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
            System.err.println("[WestlakeLauncher] Touch file (env): " + envTouch);
        } else {
            for (String p : TOUCH_PATHS_FALLBACK) {
                java.io.File f = new java.io.File(p);
                if (f.getParentFile() != null && f.getParentFile().exists()) {
                    touchFile = f;
                    System.err.println("[WestlakeLauncher] Touch file (fallback): " + p);
                    break;
                }
            }
            if (touchFile == null) {
                touchFile = new java.io.File(TOUCH_PATHS_FALLBACK[0]);
                System.err.println("[WestlakeLauncher] Touch file (default): " + TOUCH_PATHS_FALLBACK[0]);
            }
        }

        // After splash: if we have real icons from app_process64, render them
        if (realIconsPng != null && com.ohos.shim.bridge.OHBridge.isNativeAvailable()) {
            try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
            try {
                long surf = com.ohos.shim.bridge.OHBridge.surfaceCreate(0, SURFACE_WIDTH, SURFACE_HEIGHT);
                long canv = com.ohos.shim.bridge.OHBridge.surfaceGetCanvas(surf);
                com.ohos.shim.bridge.OHBridge.canvasDrawImage(canv, realIconsPng, 0, 0, SURFACE_WIDTH, SURFACE_HEIGHT);
                com.ohos.shim.bridge.OHBridge.surfaceFlush(surf);
                System.err.println("[WestlakeLauncher] Real icons frame rendered! (" + realIconsPng.length + " bytes)");
            } catch (Throwable t) {
                System.err.println("[WestlakeLauncher] Real icons render error: " + t);
            }
        }

        // Show splash 3 seconds then navigate to dashboard
        if (android.app.WestlakeActivityThread.pendingDashboardClass != null) {
            try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
        }
        if (android.app.WestlakeActivityThread.pendingDashboardClass != null) {
            String dashClass = android.app.WestlakeActivityThread.pendingDashboardClass;
            android.app.WestlakeActivityThread.pendingDashboardClass = null;
            System.err.println("[WestlakeLauncher] Launching pending dashboard: " + dashClass);
            try {
                android.app.WestlakeActivityThread wat = android.app.WestlakeActivityThread.currentActivityThread();
                Intent dashIntent = new Intent();
                dashIntent.setComponent(new ComponentName("com.mcdonalds.app", dashClass));
                Activity dash = wat.performLaunchActivity(dashClass, "com.mcdonalds.app", dashIntent, null);
                if (dash != null) {
                    initialActivity = dash;
                    System.err.println("[WestlakeLauncher] Dashboard active: " + dash.getClass().getName());
                    // Clear splash overlay so real content is visible
                    splashImageData = null;
                    // On app_process64: render real drawables from the McD APK
                    if (sRealContext != null) {
                        try {
                            android.content.Context ctx = (android.content.Context) sRealContext;
                            android.content.res.Resources realRes = ctx.getResources();

                            // Render a real screenshot by drawing real drawables to a bitmap
                            android.graphics.Bitmap screenshot = android.graphics.Bitmap.createBitmap(
                                SURFACE_WIDTH, SURFACE_HEIGHT, android.graphics.Bitmap.Config.ARGB_8888);
                            android.graphics.Canvas c = new android.graphics.Canvas(screenshot);
                            c.drawColor(0xFF27251F); // McD dark background

                            // Draw toolbar background
                            c.drawRect(0, 0, SURFACE_WIDTH, 56, newPaint(0xFF292929));

                            // Load and draw real McD icons using the framework's Resources
                            String[] drawableNames = {"archus", "ic_menu", "ic_notification_bell", "back_chevron",
                                "ic_action_search", "ic_action_location", "close"};
                            int x = 20, y = 80;
                            android.graphics.Paint textPaint = newPaint(0xFFFFFFFF);
                            textPaint.setTextSize(14);
                            for (String name : drawableNames) {
                                int id = realRes.getIdentifier(name, "drawable", "com.mcdonalds.app");
                                if (id != 0) {
                                    try {
                                        android.graphics.drawable.Drawable d = ctx.getDrawable(id);
                                        if (d != null) {
                                            int dw = Math.max(d.getIntrinsicWidth(), 48);
                                            int dh = Math.max(d.getIntrinsicHeight(), 48);
                                            // Scale to 64x64
                                            d.setBounds(x, y, x + 64, y + 64);
                                            d.draw(c);
                                            c.drawText(name, x + 72, y + 40, textPaint);
                                            y += 80;
                                        }
                                    } catch (Throwable t) {
                                        c.drawText(name + " (error)", x, y + 40, textPaint);
                                        y += 80;
                                    }
                                }
                            }

                            // Title
                            android.graphics.Paint titlePaint = newPaint(0xFFFFCC00);
                            titlePaint.setTextSize(24);
                            c.drawText("McDonald's Real Icons", 20, 40, titlePaint);

                            // Compress and send through pipe
                            java.io.ByteArrayOutputStream pngOut = new java.io.ByteArrayOutputStream();
                            screenshot.compress(android.graphics.Bitmap.CompressFormat.PNG, 90, pngOut);
                            byte[] pngBytes = pngOut.toByteArray();
                            System.err.println("[WestlakeLauncher] Real icons bitmap: " + pngBytes.length + " bytes");

                            if (com.ohos.shim.bridge.OHBridge.isNativeAvailable()) {
                                long surf = com.ohos.shim.bridge.OHBridge.surfaceCreate(0, SURFACE_WIDTH, SURFACE_HEIGHT);
                                long canv = com.ohos.shim.bridge.OHBridge.surfaceGetCanvas(surf);
                                com.ohos.shim.bridge.OHBridge.canvasDrawImage(canv, pngBytes, 0, 0, SURFACE_WIDTH, SURFACE_HEIGHT);
                                com.ohos.shim.bridge.OHBridge.surfaceFlush(surf);
                                System.err.println("[WestlakeLauncher] Real icons frame sent!");
                            }
                            screenshot.recycle();
                        } catch (Throwable re) {
                            System.err.println("[WestlakeLauncher] Real icons error: " + re.getMessage());
                            re.printStackTrace(System.err);
                        }
                    }

                    // Skip demo screen — show real dashboard
                    if (false) try {
                        System.err.println("[WestlakeLauncher] Building rich demo with real decoded icons...");
                        String resDir = System.getProperty("westlake.apk.resdir");
                        if (resDir != null) {
                            // Scan xxxhdpi drawable directory for WebP/PNG files
                            java.io.File drawDir = new java.io.File(resDir, "res/drawable-xxxhdpi-v4");
                            if (!drawDir.exists()) drawDir = new java.io.File(resDir, "res");
                            java.io.File[] files = drawDir.listFiles();
                            if (files != null) {
                                // Build a scrollable icon grid
                                android.widget.LinearLayout root = new android.widget.LinearLayout(dash);
                                root.setOrientation(android.widget.LinearLayout.VERTICAL);
                                root.setBackgroundColor(0xFF27251F);
                                root.setPadding(10, 30, 10, 10);

                                // Title
                                android.widget.TextView title2 = new android.widget.TextView(dash);
                                title2.setText("McDonald's Real Icons (" + files.length + " files)");
                                title2.setTextColor(0xFFFFCC00);
                                title2.setTextSize(18);
                                title2.setGravity(android.view.Gravity.CENTER);
                                root.addView(title2);

                                // Grid of icons (6 per row)
                                int col = 0;
                                android.widget.LinearLayout row = null;
                                int loaded = 0;
                                int iconSize = 70;
                                java.util.Arrays.sort(files);
                                for (java.io.File f : files) {
                                    String name = f.getName();
                                    if (!name.endsWith(".webp") && !name.endsWith(".png")) continue;
                                    if (name.startsWith("abc_") || name.contains("mtrl_")) continue; // skip Android system icons
                                    if (loaded >= 42) break; // max 7 rows of 6

                                    if (col % 6 == 0) {
                                        row = new android.widget.LinearLayout(dash);
                                        row.setOrientation(android.widget.LinearLayout.HORIZONTAL);
                                        row.setPadding(0, 5, 0, 5);
                                        root.addView(row);
                                    }

                                    android.graphics.Bitmap icon = android.graphics.BitmapFactory.decodeFile(f.getAbsolutePath());
                                    if (icon != null && icon.getWidth() > 0) {
                                        android.widget.ImageView iv = new android.widget.ImageView(dash);
                                        iv.setImageBitmap(icon);
                                        android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(iconSize, iconSize);
                                        lp.setMargins(3, 3, 3, 3);
                                        iv.setLayoutParams(lp);
                                        row.addView(iv);
                                        loaded++;
                                        col++;
                                    }
                                }
                                System.err.println("[WestlakeLauncher] Loaded " + loaded + " McDonald's icons");

                                // Status bar
                                android.widget.TextView status2 = new android.widget.TextView(dash);
                                status2.setText(loaded + " icons decoded (stb_image + libwebp)\n" +
                                    files.length + " total drawable files\n" +
                                    "Westlake Engine on Pixel 7 Pro");
                                status2.setTextColor(0xFF80FF80);
                                status2.setTextSize(11);
                                status2.setPadding(10, 15, 10, 10);
                                root.addView(status2);

                                // Use this as the content view
                                dash.setContentView(root);
                                dash.onSurfaceCreated(0L, SURFACE_WIDTH, SURFACE_HEIGHT);
                                dash.renderFrame();
                                System.err.println("[WestlakeLauncher] Rich demo screen rendered!");
                            }
                        }
                    } catch (Throwable re) {
                        System.err.println("[WestlakeLauncher] Rich demo error: " + re.getMessage());
                    }
                    // Render the dashboard's actual view tree (from setPageLayout)
                    try {
                        dash.onSurfaceCreated(0L, SURFACE_WIDTH, SURFACE_HEIGHT);
                        dash.renderFrame();
                        System.err.println("[WestlakeLauncher] Dashboard view tree rendered!");
                    } catch (Throwable re) {
                        System.err.println("[WestlakeLauncher] Dashboard render error: " + re.getMessage());
                    }
                }
            } catch (Throwable t) {
                System.err.println("[WestlakeLauncher] Dashboard launch error: " + t.getMessage());
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

            Activity current = initialActivity; // prefer WAT-created activity
            if (current == null) current = am.getResumedActivity();
            if (current == null) {
                System.err.println("[WestlakeLauncher] No resumed activity, exiting");
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
                                System.err.println("[WestlakeLauncher] Text input: '" + inputText + "' -> " + et);
                                needsRender = true;
                            } else {
                                System.err.println("[WestlakeLauncher] Text input: no EditText found");
                            }
                        }
                    }
                } catch (Exception e5) {
                    System.err.println("[WestlakeLauncher] Text input error: " + e5);
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
                                System.err.println("[WestlakeLauncher] Touch DOWN at (" + x + "," + y + ")");
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
                                System.err.println("[WestlakeLauncher] Touch UP at (" + x + "," + y + ")");
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
                                                        System.err.println("[WestlakeLauncher] ListView item " + pos + " clicked");
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
                                        System.err.println("[WestlakeLauncher] Navigated to " + next.getClass().getSimpleName());
                                    }
                                    try { next.renderFrame(); } catch (Exception e2) {
                                        if (frameCount < 5) System.err.println("[WestlakeLauncher] renderFrame error: " + e2);
                                    }
                                }
                                needsRender = false;
                            }
                        }
                    }
                } catch (Exception e) {
                    if (frameCount < 10) System.err.println("[WestlakeLauncher] Touch error: " + e);
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
        System.err.println("[WestlakeLauncher] Render loop ended after " + frameCount + " frames");
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

    /**
     * Inject mock McDonald's dashboard content into the 5 sections of activity_home_dashboard.xml.
     * This simulates what the real app would show after loading data from the API.
     */
    private static void injectDashboardContent(Activity ctx, android.view.ViewGroup root) {
        // Find the 5 LinearLayout sections by traversing the tree
        java.util.List<android.widget.LinearLayout> sections = new java.util.ArrayList<>();
        findLinearLayouts(root, sections);
        System.err.println("[WestlakeLauncher] Found " + sections.size() + " sections to fill");

        // Fix layout params: sections should wrap_content, not fill parent
        for (android.widget.LinearLayout s : sections) {
            android.view.ViewGroup parent = (android.view.ViewGroup) s.getParent();
            android.view.ViewGroup.LayoutParams lp;
            if (parent instanceof android.widget.FrameLayout) {
                lp = new android.widget.FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT);
            } else {
                lp = new android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            }
            s.setLayoutParams(lp);
        }
        // Make the parent use vertical LinearLayout
        if (sections.size() > 0) {
            android.view.ViewGroup parent = (android.view.ViewGroup) sections.get(0).getParent();
            if (parent instanceof android.widget.LinearLayout) {
                ((android.widget.LinearLayout) parent).setOrientation(android.widget.LinearLayout.VERTICAL);
            } else if (parent instanceof android.widget.FrameLayout) {
                // Replace FrameLayout parent with LinearLayout
                android.widget.LinearLayout newParent = new android.widget.LinearLayout(ctx);
                newParent.setOrientation(android.widget.LinearLayout.VERTICAL);
                // Move all children
                while (parent.getChildCount() > 0) {
                    android.view.View child = parent.getChildAt(0);
                    parent.removeViewAt(0);
                    android.widget.LinearLayout.LayoutParams clp = new android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
                    newParent.addView(child, clp);
                }
                parent.addView(newParent, new android.widget.FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT));
            }
        }

        int RED = 0xFFDA291C;
        int YELLOW = 0xFFFFCC00;
        int WHITE = 0xFFFFFFFF;
        int DARK = 0xFF292929;
        int LIGHT_GRAY = 0xFFF5F5F5;

        // Section 0: Hero banner
        if (sections.size() > 0) {
            android.widget.LinearLayout hero = sections.get(0);
            hero.setOrientation(android.widget.LinearLayout.VERTICAL);
            hero.setBackgroundColor(RED);
            hero.setPadding(24, 32, 24, 32);

            android.widget.TextView title = new android.widget.TextView(ctx);
            title.setText("Welcome to McDonald's");
            title.setTextSize(28);
            title.setTextColor(YELLOW);
            title.setGravity(android.view.Gravity.CENTER);
            hero.addView(title);

            android.widget.TextView sub = new android.widget.TextView(ctx);
            sub.setText("Order ahead & skip the line");
            sub.setTextSize(16);
            sub.setTextColor(WHITE);
            sub.setGravity(android.view.Gravity.CENTER);
            sub.setPadding(0, 8, 0, 16);
            hero.addView(sub);
        }

        // Section 1: Deals
        if (sections.size() > 1) {
            android.widget.LinearLayout deals = sections.get(1);
            deals.setOrientation(android.widget.LinearLayout.VERTICAL);
            deals.setBackgroundColor(LIGHT_GRAY);
            deals.setPadding(16, 16, 16, 16);
            addSectionHeader(ctx, deals, "Deals", DARK);
            String[][] dealItems = {
                {"$1 Any Size Soft Drink", "With app purchase"},
                {"Free Medium Fries", "With $1 minimum purchase"},
                {"$3 Bundle", "McChicken + Small Fries"},
                {"Buy 1 Get 1 Free", "Big Mac or Quarter Pounder"},
            };
            for (String[] deal : dealItems) {
                addMenuItem(ctx, deals, deal[0], deal[1], RED, DARK);
            }
        }

        // Section 2: Menu
        if (sections.size() > 2) {
            android.widget.LinearLayout menu = sections.get(2);
            menu.setOrientation(android.widget.LinearLayout.VERTICAL);
            menu.setBackgroundColor(WHITE);
            menu.setPadding(16, 16, 16, 16);
            addSectionHeader(ctx, menu, "Menu", DARK);
            String[][] menuItems = {
                {"Big Mac", "$5.99"},
                {"Quarter Pounder w/ Cheese", "$6.49"},
                {"10 Piece McNuggets", "$5.49"},
                {"McChicken", "$2.49"},
                {"Filet-O-Fish", "$5.29"},
                {"Large Fries", "$3.79"},
                {"McFlurry with OREO Cookies", "$4.39"},
            };
            for (String[] item : menuItems) {
                addMenuItem(ctx, menu, item[0], item[1], DARK, 0xFF666666);
            }
        }

        // Section 3: Rewards
        if (sections.size() > 3) {
            android.widget.LinearLayout rewards = sections.get(3);
            rewards.setOrientation(android.widget.LinearLayout.VERTICAL);
            rewards.setBackgroundColor(YELLOW);
            rewards.setPadding(16, 24, 16, 24);
            addSectionHeader(ctx, rewards, "MyMcDonald's Rewards", DARK);

            android.widget.TextView pts = new android.widget.TextView(ctx);
            pts.setText("1,250 Points");
            pts.setTextSize(32);
            pts.setTextColor(DARK);
            pts.setGravity(android.view.Gravity.CENTER);
            pts.setPadding(0, 8, 0, 8);
            rewards.addView(pts);

            android.widget.TextView info = new android.widget.TextView(ctx);
            info.setText("1,500 more points until your next free reward!");
            info.setTextSize(14);
            info.setTextColor(0xFF444444);
            info.setGravity(android.view.Gravity.CENTER);
            rewards.addView(info);
        }

        // Section 4: Bottom nav placeholder
        if (sections.size() > 4) {
            android.widget.LinearLayout nav = sections.get(4);
            nav.setOrientation(android.widget.LinearLayout.HORIZONTAL);
            nav.setBackgroundColor(WHITE);
            nav.setPadding(0, 8, 0, 8);
            nav.setGravity(android.view.Gravity.CENTER);
            String[] tabs = {"Home", "Deals", "Order", "Rewards", "More"};
            for (String tab : tabs) {
                android.widget.TextView t = new android.widget.TextView(ctx);
                t.setText(tab);
                t.setTextSize(11);
                t.setTextColor(tab.equals("Home") ? RED : 0xFF888888);
                t.setGravity(android.view.Gravity.CENTER);
                android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(0, -2, 1.0f);
                t.setLayoutParams(lp);
                nav.addView(t);
            }
        }
    }

    private static void findLinearLayouts(android.view.View v, java.util.List<android.widget.LinearLayout> out) {
        if (v instanceof android.widget.LinearLayout && v.getId() != android.view.View.NO_ID) {
            out.add((android.widget.LinearLayout) v);
        }
        if (v instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                findLinearLayouts(vg.getChildAt(i), out);
            }
        }
    }

    private static void addSectionHeader(Activity ctx, android.widget.LinearLayout parent, String text, int color) {
        android.widget.TextView h = new android.widget.TextView(ctx);
        h.setText(text);
        h.setTextSize(22);
        h.setTextColor(color);
        h.setPadding(0, 0, 0, 12);
        parent.addView(h);
    }

    private static void addMenuItem(Activity ctx, android.widget.LinearLayout parent, String name, String detail, int nameColor, int detailColor) {
        android.widget.LinearLayout row = new android.widget.LinearLayout(ctx);
        row.setOrientation(android.widget.LinearLayout.VERTICAL);
        row.setPadding(0, 8, 0, 8);

        android.widget.TextView n = new android.widget.TextView(ctx);
        n.setText(name);
        n.setTextSize(16);
        n.setTextColor(nameColor);
        row.addView(n);

        android.widget.TextView d = new android.widget.TextView(ctx);
        d.setText(detail);
        d.setTextSize(12);
        d.setTextColor(detailColor);
        row.addView(d);

        parent.addView(row);
    }

    /** Render real McD drawable icons using the phone's framework — straight to pipe */
    private static void renderRealIconsScreen(android.content.Context ctx, android.content.res.Resources res) {
        try {
            android.graphics.Bitmap bmp = android.graphics.Bitmap.createBitmap(
                SURFACE_WIDTH, SURFACE_HEIGHT, android.graphics.Bitmap.Config.ARGB_8888);
            android.graphics.Canvas c = new android.graphics.Canvas(bmp);
            c.drawColor(0xFF27251F);

            // Title
            android.graphics.Paint tp = newPaint(0xFFFFCC00);
            tp.setTextSize(22);
            c.drawText("McDonald's Real Drawables", 20, 40, tp);
            android.graphics.Paint sp = newPaint(0xAAFFFFFF);
            sp.setTextSize(11);
            c.drawText("Decoded by phone's framework via app_process64", 20, 60, sp);

            // Load real drawables
            String pkg = "com.mcdonalds.app";
            String[] names = {"archus", "ic_menu", "ic_action_time", "back_chevron",
                "close", "ic_action_search", "splash_screen",
                "ic_notifications", "ic_mcdonalds_logo", "mcd_logo_golden"};
            int y = 90;
            android.graphics.Paint lp = newPaint(0xFFFFFFFF);
            lp.setTextSize(14);
            int found = 0;
            for (String name : names) {
                int id = res.getIdentifier(name, "drawable", pkg);
                if (id == 0) id = res.getIdentifier(name, "mipmap", pkg);
                String label = name + " (0x" + Integer.toHexString(id) + ")";
                if (id != 0) {
                    try {
                        android.graphics.drawable.Drawable d = ctx.getDrawable(id);
                        if (d != null) {
                            d.setBounds(20, y, 84, y + 64);
                            d.draw(c);
                            c.drawText(label, 96, y + 40, lp);
                            found++;
                        } else {
                            c.drawText(label + " null", 20, y + 40, newPaint(0xFFFF4444));
                        }
                    } catch (Throwable t) {
                        c.drawText(label + " ERR: " + t.getMessage(), 20, y + 40, newPaint(0xFFFF4444));
                    }
                } else {
                    c.drawText(name + " (not found)", 20, y + 40, newPaint(0xFF888888));
                }
                y += 70;
            }

            // Status
            android.graphics.Paint gp = newPaint(0xFF00FF00);
            gp.setTextSize(12);
            c.drawText(found + "/" + names.length + " drawables loaded via real framework", 20, y + 20, gp);

            // Send as PNG through pipe
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 90, out);
            byte[] png = out.toByteArray();
            System.err.println("[WestlakeLauncher] Real icons: " + png.length + " bytes, " + found + " icons");

            if (com.ohos.shim.bridge.OHBridge.isNativeAvailable()) {
                long surf = com.ohos.shim.bridge.OHBridge.surfaceCreate(0, SURFACE_WIDTH, SURFACE_HEIGHT);
                long canv = com.ohos.shim.bridge.OHBridge.surfaceGetCanvas(surf);
                com.ohos.shim.bridge.OHBridge.canvasDrawImage(canv, png, 0, 0, SURFACE_WIDTH, SURFACE_HEIGHT);
                com.ohos.shim.bridge.OHBridge.surfaceFlush(surf);
                System.err.println("[WestlakeLauncher] Real icons frame sent!");
            }
            bmp.recycle();
        } catch (Throwable t) {
            System.err.println("[WestlakeLauncher] renderRealIconsScreen error: " + t);
            t.printStackTrace(System.err);
        }
    }

    private static android.graphics.Paint newPaint(int color) {
        android.graphics.Paint p = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        p.setColor(color);
        return p;
    }
}
