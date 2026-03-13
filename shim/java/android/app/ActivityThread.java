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
 * Usage:
 *   dalvikvm -cp app.dex android.app.ActivityThread [AndroidManifest.xml]
 *
 * If no manifest is given, it looks for AndroidManifest.xml in the classpath
 * or current directory.
 *
 * The manifest is parsed (simple XML) to find:
 *   - package name
 *   - application class (optional)
 *   - launcher activity (activity with ACTION_MAIN + CATEGORY_LAUNCHER)
 *
 * Then it creates the Application, calls onCreate(), instantiates the
 * launcher Activity via reflection, and drives the full lifecycle.
 */
public class ActivityThread {

    private static final String TAG = "ActivityThread";

    private Application mApplication;
    private Instrumentation mInstrumentation;
    private String mPackageName;

    public static void main(String[] args) {
        log("I", "--- ActivityThread starting ---");

        String manifestPath = null;
        String overrideActivity = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--activity") && i + 1 < args.length) {
                overrideActivity = args[++i];
            } else if (!args[i].startsWith("-")) {
                manifestPath = args[i];
            }
        }

        ActivityThread thread = new ActivityThread();
        thread.run(manifestPath, overrideActivity);
    }

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
        mApplication.setPackageName(mPackageName);
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

        // Attach framework state
        activity.mApplication = mApplication;
        activity.mComponent = component;
        activity.mIntent = launchIntent;

        log("I", "Activity created: " + activity.getClass().getName());

        // Drive lifecycle: onCreate → onStart → onResume
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
            log("I", "Activity finished with result " + activity.mResultCode);
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

    /* ── Simple manifest parser ── */

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
     * Handles the standard format:
     *   <manifest package="com.example.app">
     *     <application android:name=".MyApp">
     *       <activity android:name=".MainActivity">
     *         <intent-filter>
     *           <action android:name="android.intent.action.MAIN" />
     *           <category android:name="android.intent.category.LAUNCHER" />
     *         </intent-filter>
     *       </activity>
     *     </application>
     *   </manifest>
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

            // <manifest package="...">
            if (line.startsWith("<manifest")) {
                info.packageName = extractAttr(line, "package");
            }

            // <application android:name="...">
            if (line.startsWith("<application")) {
                String name = extractAttr(line, "android:name");
                if (name == null) name = extractAttr(line, "name");
                if (name != null) {
                    info.applicationClass = name;
                }
            }

            // <activity android:name="...">
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

    /** Extract attribute value: android:name="value" → value */
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
