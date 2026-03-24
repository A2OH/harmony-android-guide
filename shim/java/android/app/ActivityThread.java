package android.app;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * ActivityThread — the main entry point for running Android apps transparently.
 *
 * Supports three launch modes:
 *   1. APK mode:     dalvikvm -cp shim.dex android.app.ActivityThread /path/to/app.apk
 *   2. Package mode:  dalvikvm -cp app.dex android.app.ActivityThread com.example.app
 *   3. Manifest mode: dalvikvm -cp app.dex android.app.ActivityThread [AndroidManifest.xml]
 *
 * APK mode uses MiniServer.loadApk() to parse the APK, extract DEX files,
 * register activities/services, and launch the main activity.
 *
 * Package mode initializes MiniServer with the given package name and launches
 * the registered launcher activity.
 *
 * Manifest mode (legacy) parses AndroidManifest.xml directly and drives the
 * Activity lifecycle via Instrumentation.
 *
 * Also provides the singleton currentActivityThread() used by framework internals.
 */
public class ActivityThread {

    private static final String TAG = "ActivityThread";

    /** Singleton instance — set during main() or ensured via currentActivityThread(). */
    private static ActivityThread sCurrentActivityThread;

    private Application mApplication;
    private Instrumentation mInstrumentation;
    private String mPackageName;

    public ActivityThread() {
    }

    /**
     * Returns the singleton ActivityThread for this process.
     * Creates one if none exists yet (for framework code that calls this before main()).
     */
    public static ActivityThread currentActivityThread() {
        if (sCurrentActivityThread == null) {
            sCurrentActivityThread = new ActivityThread();
        }
        return sCurrentActivityThread;
    }

    /**
     * Returns the Application for this process (static convenience).
     */
    public static Application currentApplication() {
        ActivityThread thread = currentActivityThread();
        return thread != null ? thread.mApplication : null;
    }

    /**
     * Returns the Application object for this process.
     * May be null if called before Application.onCreate().
     */
    public Application getApplication() {
        return mApplication;
    }

    /**
     * Returns the Instrumentation for this process.
     */
    public Instrumentation getInstrumentation() {
        return mInstrumentation;
    }

    /**
     * Returns the package name for this process.
     */
    public String getPackageName() {
        return mPackageName;
    }

    /**
     * Main entry point. Determines launch mode from args:
     *   - If first non-flag arg ends with ".apk" -> APK mode via MiniServer
     *   - If first non-flag arg looks like a package name (contains '.') -> package mode
     *   - Otherwise -> manifest/legacy mode
     */
    public static void main(String[] args) {
        log("I", "--- ActivityThread starting ---");

        // Install singleton
        ActivityThread thread = new ActivityThread();
        sCurrentActivityThread = thread;

        String firstArg = null;
        String overrideActivity = null;

        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("--activity") && i + 1 < args.length) {
                    overrideActivity = args[++i];
                } else if (!args[i].startsWith("-")) {
                    if (firstArg == null) {
                        firstArg = args[i];
                    }
                }
            }
        }

        if (firstArg != null && firstArg.endsWith(".apk")) {
            // APK mode: load and launch via MiniServer
            thread.launchFromApk(firstArg, overrideActivity);
        } else if (firstArg != null && firstArg.indexOf('.') > 0
                && !firstArg.endsWith(".xml")) {
            // Package name mode: components already registered via MiniServer
            thread.launchFromPackage(firstArg, overrideActivity);
        } else {
            // Legacy manifest mode
            thread.run(firstArg, overrideActivity);
        }
    }

    /**
     * APK mode: use MiniServer to load the APK, parse manifest, register
     * components, create Application, and launch the main activity.
     */
    private void launchFromApk(String apkPath, String overrideActivity) {
        log("I", "APK mode: " + apkPath);

        try {
            // Initialize MiniServer with a temporary package name
            MiniServer.init("app");
            MiniServer server = MiniServer.get();

            // Load APK — this parses manifest, extracts DEX, registers activities
            ApkInfo info = server.loadApk(apkPath);
            mPackageName = info.packageName;
            mApplication = server.getApplication();

            log("I", "Package: " + mPackageName);

            // Override activity if specified on command line
            String launcherName = overrideActivity;
            if (launcherName == null) {
                launcherName = info.launcherActivity;
            }

            if (launcherName == null && !info.activities.isEmpty()) {
                launcherName = info.activities.get(0);
            }

            if (launcherName == null) {
                log("E", "No launcher activity found in APK");
                return;
            }

            // Resolve relative class name
            if (launcherName.startsWith(".")) {
                launcherName = mPackageName + launcherName;
            }

            log("I", "Launching: " + launcherName);
            server.startActivity(launcherName);

        } catch (Exception e) {
            log("E", "Failed to launch APK: " + e);
        }
    }

    /**
     * Package mode: initialize MiniServer with the given package name and
     * start the launcher activity (if one is registered).
     */
    private void launchFromPackage(String packageName, String overrideActivity) {
        log("I", "Package mode: " + packageName);

        MiniServer.init(packageName);
        MiniServer server = MiniServer.get();
        mPackageName = packageName;
        mApplication = server.getApplication();

        if (overrideActivity != null) {
            // Resolve relative class name
            String actName = overrideActivity;
            if (actName.startsWith(".")) {
                actName = packageName + actName;
            }
            log("I", "Launching override: " + actName);
            server.startActivity(actName);
        } else {
            // Try to find a launcher activity via implicit intent resolution
            Intent launchIntent = new Intent(Intent.ACTION_MAIN);
            launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            android.content.pm.ResolveInfo ri =
                    server.getPackageManager().resolveActivity(launchIntent);
            if (ri != null && ri.resolvedComponentName != null) {
                String actName = ri.resolvedComponentName.getClassName();
                log("I", "Launching resolved: " + actName);
                server.startActivity(launchIntent);
            } else {
                log("W", "No launcher activity registered for " + packageName);
            }
        }
    }

    /**
     * Legacy manifest mode: parse manifest XML, create Application, instantiate
     * Activity directly, and drive the full lifecycle via Instrumentation.
     */
    private void run(String manifestPath, String overrideActivity) {
        mInstrumentation = new Instrumentation();

        // Parse manifest
        ManifestInfo manifest = null;
        if (manifestPath != null) {
            manifest = parseManifestFile(manifestPath);
        }
        if (manifest == null) {
            manifest = parseManifestFromClasspath();
        }
        if (manifest == null) {
            manifest = new ManifestInfo();
        }

        // Override activity from command line
        if (overrideActivity != null) {
            manifest.launcherActivity = overrideActivity;
        }

        if (manifest.launcherActivity == null) {
            log("E", "No launcher activity found. Specify --activity <class> or provide AndroidManifest.xml");
            return;
        }

        mPackageName = manifest.packageName != null ? manifest.packageName : "app";

        // Resolve relative class names
        if (manifest.launcherActivity.startsWith(".")) {
            manifest.launcherActivity = mPackageName + manifest.launcherActivity;
        }
        if (manifest.applicationClass != null && manifest.applicationClass.startsWith(".")) {
            manifest.applicationClass = mPackageName + manifest.applicationClass;
        }

        log("I", "Package: " + mPackageName);
        log("I", "Launcher: " + manifest.launcherActivity);
        if (manifest.applicationClass != null) {
            log("I", "Application: " + manifest.applicationClass);
        }

        // Create Application
        mApplication = createApplication(manifest.applicationClass);
        ShimCompat.setPackageName(mApplication, mPackageName);
        log("I", "Application.onCreate()");
        mInstrumentation.callApplicationOnCreate(mApplication);

        // Create launch Intent
        ComponentName component = new ComponentName(mPackageName, manifest.launcherActivity);
        Intent launchIntent = Intent.makeMainActivity(component);

        // Instantiate Activity
        Activity activity;
        try {
            activity = mInstrumentation.newActivity(
                    getClass().getClassLoader(),
                    manifest.launcherActivity,
                    launchIntent);
        } catch (Exception e) {
            log("E", "Failed to create activity: " + e);
            return;
        }

        // Attach framework state (via reflection — on real Android, Activity fields differ)
        ShimCompat.setActivityField(activity, "mApplication", mApplication);
        ShimCompat.setActivityField(activity, "mComponent", component);
        ShimCompat.setActivityField(activity, "mIntent", launchIntent);

        log("I", "Activity created: " + activity.getClass().getName());

        // Drive lifecycle: onCreate -> onStart -> onResume
        log("I", "--- onCreate ---");
        mInstrumentation.callActivityOnCreate(activity, null);

        log("I", "--- onStart ---");
        mInstrumentation.callActivityOnStart(activity);

        log("I", "--- onResume ---");
        mInstrumentation.callActivityOnResume(activity);

        log("I", "--- running ---");

        // Simulate app running, then shut down
        log("I", "--- onPause ---");
        mInstrumentation.callActivityOnPause(activity);

        log("I", "--- onStop ---");
        mInstrumentation.callActivityOnStop(activity);

        log("I", "--- onDestroy ---");
        mInstrumentation.callActivityOnDestroy(activity);

        if (activity.isFinishing()) {
            int resultCode = ShimCompat.getActivityIntField(activity, "mResultCode", 0);
            log("I", "Activity finished with result " + resultCode);
        }

        log("I", "--- ActivityThread complete ---");
    }

    private Application createApplication(String className) {
        if (className != null) {
            try {
                Class<?> clazz = getClass().getClassLoader().loadClass(className);
                return (Application) clazz.newInstance();
            } catch (Exception e) {
                log("W", "Could not create Application " + className + ": " + e);
            }
        }
        return new Application();
    }

    /* -- Simple manifest parser -- */

    static class ManifestInfo {
        String packageName;
        String applicationClass;
        String launcherActivity;
    }

    private ManifestInfo parseManifestFile(String path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            ManifestInfo info = parseManifestStream(reader);
            reader.close();
            return info;
        } catch (Exception e) {
            log("W", "Could not read manifest: " + path + ": " + e);
            return null;
        }
    }

    private ManifestInfo parseManifestFromClasspath() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("AndroidManifest.xml");
            if (is == null) return null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            ManifestInfo info = parseManifestStream(reader);
            reader.close();
            return info;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Minimal XML-like parser for AndroidManifest.xml.
     */
    private ManifestInfo parseManifestStream(BufferedReader reader) throws Exception {
        ManifestInfo info = new ManifestInfo();
        String line;
        String currentActivity = null;
        boolean inIntentFilter = false;
        boolean hasActionMain = false;
        boolean hasCategoryLauncher = false;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.startsWith("<manifest")) {
                info.packageName = extractAttr(line, "package");
            }

            if (line.startsWith("<application")) {
                String name = extractAttr(line, "android:name");
                if (name == null) name = extractAttr(line, "name");
                if (name != null) {
                    info.applicationClass = name;
                }
            }

            if (line.startsWith("<activity")) {
                String name = extractAttr(line, "android:name");
                if (name == null) name = extractAttr(line, "name");
                currentActivity = name;
                inIntentFilter = false;
                hasActionMain = false;
                hasCategoryLauncher = false;
            }

            if (line.startsWith("<intent-filter")) {
                inIntentFilter = true;
                hasActionMain = false;
                hasCategoryLauncher = false;
            }

            if (inIntentFilter) {
                if (line.contains("android.intent.action.MAIN")) {
                    hasActionMain = true;
                }
                if (line.contains("android.intent.category.LAUNCHER")) {
                    hasCategoryLauncher = true;
                }
            }

            if (line.startsWith("</intent-filter")) {
                if (hasActionMain && hasCategoryLauncher && currentActivity != null) {
                    info.launcherActivity = currentActivity;
                }
                inIntentFilter = false;
            }

            if (line.startsWith("</activity")) {
                currentActivity = null;
            }
        }

        return info;
    }

    /** Extract attribute value: android:name="value" -> value */
    private static String extractAttr(String line, String attr) {
        String search = attr + "=\"";
        int start = line.indexOf(search);
        if (start < 0) return null;
        start += search.length();
        int end = line.indexOf('"', start);
        if (end < 0) return null;
        return line.substring(start, end);
    }

    private static void log(String level, String msg) {
        System.out.println(level + "/" + TAG + ": " + msg);
    }
}
