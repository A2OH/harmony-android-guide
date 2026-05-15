// SPDX-License-Identifier: Apache-2.0
//
// M7-Step2 (CR38 §5 acceptance fixture, production launch path)
// NoiceProductionLauncher.java
//
// CR55 established the architectural ceiling of the discovery harness'
// reflection-driven onCreate path: calling MainActivity.onCreate(null)
// via reflection (DiscoverWrapperBase.phaseG_mainActivityLaunch's G4)
// bypasses Activity.performCreate, which is the framework hook that
// drives Application's dispatchActivity{Pre,Post}Created hooks AND --
// critically -- transitions the AndroidX LifecycleRegistry through
// ON_CREATE.  CR55's lazy-map prime in Activity.attach() got us past
// the original "null array" NPE inside ComponentActivity.<init>, but
// the reflection path is still missing the production performCreate
// wrapping that real AOSP launches do.
//
// M7-Step2 wires the production path end-to-end:
//
//     WestlakeActivityThread.performLaunchActivity
//       -> WAT.performLaunchActivityImpl
//         -> Instrumentation.newActivity            (G2 equivalent)
//         -> Activity.attach (6-arg V2 shape)       (G3 equivalent — CR55 prime)
//         -> Instrumentation.callActivityOnCreate   (G4 equivalent, PRODUCTION)
//             -> Activity.performCreate
//             -> Activity.onCreate (user body)
//             -> Application.dispatchActivityCreated
//
// Same code, generic across all apps.  No reflection-onto-onCreate, no
// per-app branches.  The launcher:
//
//   1. Loads libandroid_runtime_stub.so so NoiceDiscoverWrapper.println
//      JNI symbols are bound (we reuse them for stdout — same reason
//      NoiceLauncher does in M7-Step1).
//   2. ColdBootstrap.ensure() — plants the synthetic ActivityThread +
//      mSystemContext + PermissionEnforcer that all M4 service ctors
//      expect on first invocation.
//   3. Loads the noice APK + sets the contextClassLoader so
//      WestlakeActivityThread.getClassLoader() picks it up.
//   4. WestlakeActivityThread.attach(packageName, applicationClass, cl)
//      — drives the canonical Application creation flow.
//   5. WestlakeActivityThread.setForceLifecycleEnabled(true) — opt-in to
//      the strict-standalone-with-lifecycle code path (additive,
//      generic, no per-app).
//   6. Builds an Intent with ComponentName(packageName, mainActivityClass).
//   7. performLaunchActivity(...) which runs the FULL Step 1..8 sequence
//      culminating in Instrumentation.callActivityOnCreate.
//   8. Optional STARTED/RESUMED via launchAndResumeActivity (controlled
//      by --resume flag — default false to isolate onCreate signal).
//
// Manifest-driven via noice.discover.properties (the SAME file
// NoiceLauncher / NoiceDiscoverWrapper use).  Zero per-app code here.
// Renaming this class to McdProductionLauncher and changing
// MANIFEST_PATH would produce the M8-Step2 launcher with no other
// edits.
//
// Anti-drift compliance per memory/feedback_macro_shim_contract.md:
//   - ZERO Unsafe / Field.setAccessible additions in this class
//   - ZERO per-app branches
//   - All shim edits are additive (sForceLifecycleEnabled flag)

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.Properties;

import dalvik.system.PathClassLoader;

public final class NoiceProductionLauncher {

    /** Standard noice manifest path on the phone (M7-Step1 sibling). */
    static final String DEFAULT_MANIFEST_PATH =
            "/data/local/tmp/westlake/noice.discover.properties";

    /** Flag: drive create -> start -> resume after onCreate succeeds. */
    static boolean sResumeAfterCreate = false;

    public static void main(String[] args) throws Exception {
        // Resolve manifest path from args (mirrors NoiceLauncher /
        // McdLauncher contract — the only piece of "app-identity" the
        // launcher knows is "which manifest file describes the app").
        String manifestPath = DEFAULT_MANIFEST_PATH;
        for (int i = 0; i < (args != null ? args.length : 0); i++) {
            String a = args[i];
            if (a == null) continue;
            if ("--resume".equals(a)) {
                sResumeAfterCreate = true;
            } else if (a.startsWith("--manifest=")) {
                manifestPath = a.substring("--manifest=".length());
            } else if (!a.startsWith("-")) {
                manifestPath = a;
            }
        }

        // ------------ Step 1: bind native println for stdout ------------
        try {
            System.loadLibrary("android_runtime_stub");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("M7_PROD_LAUNCHER: loadLibrary(android_runtime_stub) "
                    + "FAILED: " + e);
        }
        println("M7_PROD_LAUNCHER: NoiceProductionLauncher.main() entered");
        println("M7_PROD_LAUNCHER: manifest=" + manifestPath
                + " resume=" + sResumeAfterCreate);

        // ------------ Step 2: load manifest ------------
        Properties props = loadManifest(manifestPath);
        String pkg = req(props, "app.packageName");
        String apkPath = req(props, "app.apkPath");
        String appCls = req(props, "app.applicationClass");
        String mainActCls = req(props, "app.mainActivityClass");
        int targetSdk = parseIntOr(props.getProperty("app.targetSdkVersion", "33"), 33);
        String dataDir = props.getProperty("app.dataDir",
                "/data/local/tmp/westlake/" + pkg);
        println("M7_PROD_LAUNCHER: pkg=" + pkg);
        println("M7_PROD_LAUNCHER:   apk=" + apkPath);
        println("M7_PROD_LAUNCHER:   appClass=" + appCls);
        println("M7_PROD_LAUNCHER:   mainActivity=" + mainActCls);
        println("M7_PROD_LAUNCHER:   targetSdk=" + targetSdk
                + " dataDir=" + dataDir);

        // ------------ Step 3: charset + ColdBootstrap priming ------------
        // The production launch path eventually hits the same charset
        // and ActivityThread.sCurrentActivityThread reads that the M4
        // services + AOSP framework code transitively touch.  Mirror
        // DiscoverWrapperBase.runFromManifest's prelude — same priming
        // calls, generic across all apps.
        try {
            CharsetPrimer.primeCharsetState();
            CharsetPrimer.primeActivityThread();
            println("M7_PROD_LAUNCHER: charset + ColdBootstrap primed");
        } catch (Throwable t) {
            println("M7_PROD_LAUNCHER: priming WARN: " + t);
        }

        // ------------ Step 3.5: prepare main Looper ------------
        // androidx.lifecycle.LifecycleRegistry.<init> calls
        // Looper.getMainLooper().getThread() to verify single-thread
        // invariants. Without a prepared main looper, ComponentActivity's
        // constructor NPEs on Looper.getMainLooper(). The discovery harness
        // does this in phaseG_mainActivityLaunch's G1 prereq; the
        // production path needs the same. Generic, no per-app code.
        try {
            Class<?> looperCls = Class.forName("android.os.Looper");
            Method getMainLooper = looperCls.getMethod("getMainLooper");
            Object existingMain = getMainLooper.invoke(null);
            if (existingMain == null) {
                Method prepare = looperCls.getMethod("prepareMainLooper");
                prepare.invoke(null);
                Object now = getMainLooper.invoke(null);
                println("M7_PROD_LAUNCHER: Looper.prepareMainLooper() -> "
                        + (now != null ? "ok" : "null"));
            } else {
                println("M7_PROD_LAUNCHER: main Looper already prepared");
            }
        } catch (Throwable t) {
            println("M7_PROD_LAUNCHER: prepareMainLooper WARN: " + t);
        }

        // ------------ Step 4: register M4 services ------------
        try {
            int registered =
                com.westlake.services.ServiceRegistrar.registerAllServices();
            println("M7_PROD_LAUNCHER: ServiceRegistrar registered "
                    + registered + " M4 services");
        } catch (Throwable t) {
            println("M7_PROD_LAUNCHER: ServiceRegistrar WARN: " + t);
        }

        // ------------ Step 5: load APK + set context classloader ------------
        File apk = new File(apkPath);
        if (!apk.exists()) {
            throw new RuntimeException("APK missing: " + apkPath);
        }
        PathClassLoader appCl = new PathClassLoader(apkPath,
                NoiceProductionLauncher.class.getClassLoader());
        Thread.currentThread().setContextClassLoader(appCl);
        println("M7_PROD_LAUNCHER: PathClassLoader installed (identity hash=0x"
                + Integer.toHexString(System.identityHashCode(appCl))
                + ")");

        // Pre-warm: confirm the app's main classes resolve.
        for (String c : new String[] { appCls, mainActCls }) {
            try {
                Class<?> cls = Class.forName(c, false, appCl);
                println("M7_PROD_LAUNCHER: preload " + c + " -> " + cls.getName());
            } catch (Throwable t) {
                println("M7_PROD_LAUNCHER: preload " + c + " FAIL: " + t);
            }
        }

        // ------------ Step 6: WestlakeActivityThread.attach ------------
        // Drives mInstrumentation = new WestlakeInstrumentation(thread),
        // mAppComponentFactory = new AppComponentFactory(), and
        // mInitialApplication = packageInfo.makeApplication(...).
        // Generic flow — no per-app code.
        Class<?> watCls = Class.forName("android.app.WestlakeActivityThread");
        Method attachStandalone = watCls.getMethod("attachStandalone",
                watCls, String.class, String.class, ClassLoader.class);
        Object wat = watCls.getMethod("currentActivityThread").invoke(null);
        attachStandalone.invoke(null, wat, pkg, appCls, appCl);
        println("M7_PROD_LAUNCHER: WAT.attachStandalone returned");

        Object appInstance = watCls.getMethod("currentApplication").invoke(null);
        println("M7_PROD_LAUNCHER: WAT.currentApplication = "
                + (appInstance == null
                        ? "null"
                        : appInstance.getClass().getName()
                                + "@" + System.identityHashCode(appInstance)));

        // ------------ Step 7: enable production lifecycle ------------
        // Flip the additive sForceLifecycleEnabled gate (M7-Step2). Once
        // set, performLaunchActivity will run Step 7 (record) + Step 8
        // (Instrumentation.callActivityOnCreate) inside the strict path
        // — i.e. the same lifecycle the cutoff-canary / McD per-app
        // gates already get.  Generic across all apps.
        Method setForceLifecycle = watCls.getMethod(
                "setForceLifecycleEnabled", boolean.class);
        setForceLifecycle.invoke(null, Boolean.TRUE);
        println("M7_PROD_LAUNCHER: setForceLifecycleEnabled(true)");

        // ------------ Step 8: build launch Intent ------------
        Class<?> componentCls = Class.forName("android.content.ComponentName");
        Object component = componentCls
                .getConstructor(String.class, String.class)
                .newInstance(pkg, mainActCls);
        Class<?> intentCls = Class.forName("android.content.Intent");
        Object launchIntent = intentCls.getConstructor().newInstance();
        intentCls.getMethod("setComponent", componentCls)
                .invoke(launchIntent, component);
        intentCls.getMethod("setPackage", String.class).invoke(launchIntent, pkg);
        // Set ACTION_MAIN + CATEGORY_LAUNCHER so framework probes that
        // sniff the launch intent for "is this the launcher?" succeed.
        intentCls.getMethod("setAction", String.class)
                .invoke(launchIntent, "android.intent.action.MAIN");
        intentCls.getMethod("addCategory", String.class)
                .invoke(launchIntent, "android.intent.category.LAUNCHER");
        println("M7_PROD_LAUNCHER: launch intent built component="
                + component);

        // ------------ Step 9: PRODUCTION LAUNCH ------------
        // The crucial line.  Either performLaunchActivity (CREATED only)
        // or launchAndResumeActivity (CREATED + STARTED + RESUMED).
        // Both honor sForceLifecycleEnabled per M7-Step2.
        String launchMethod = sResumeAfterCreate
                ? "launchAndResumeActivity"
                : "performLaunchActivity";
        Method launch = watCls.getMethod(launchMethod,
                String.class, String.class, intentCls,
                Class.forName("android.os.Bundle"));
        println("M7_PROD_LAUNCHER: PRODUCTION_LAUNCH_BEGIN method="
                + launchMethod + " class=" + mainActCls);
        Object activity = null;
        try {
            activity = launch.invoke(wat, mainActCls, pkg, launchIntent,
                    /*savedState*/ null);
            println("M7_PROD_LAUNCHER: PRODUCTION_LAUNCH_RETURNED activity="
                    + (activity == null
                            ? "null"
                            : activity.getClass().getName()
                                    + "@" + System.identityHashCode(activity)));
        } catch (Throwable t) {
            Throwable c = t;
            while (c != null) {
                eprintln("M7_PROD_LAUNCHER: PRODUCTION_LAUNCH_THREW "
                        + c.getClass().getName() + ": " + c.getMessage());
                Throwable next = null;
                try { next = c.getCause(); } catch (Throwable ignored) {}
                if (next == c || next == null) break;
                c = next;
            }
        }

        // ------------ Step 10: report + exit ------------
        if (activity != null) {
            // Snapshot a couple of activity-level facts that confirm we
            // reached past onCreate (vs the discovery harness which only
            // gets the InvocationTargetException stack trace).
            try {
                Method getIntent = activity.getClass().getMethod("getIntent");
                Object got = getIntent.invoke(activity);
                println("M7_PROD_LAUNCHER: activity.getIntent() = "
                        + (got == null ? "null" : got.toString()));
            } catch (Throwable t) {
                println("M7_PROD_LAUNCHER: getIntent probe WARN: " + t);
            }
            try {
                Method getWindow = activity.getClass().getMethod("getWindow");
                Object w = getWindow.invoke(activity);
                println("M7_PROD_LAUNCHER: activity.getWindow() = "
                        + (w == null ? "null" : w.getClass().getName()));
            } catch (Throwable t) {
                println("M7_PROD_LAUNCHER: getWindow probe WARN: " + t);
            }
            println("M7_PROD_LAUNCHER: PRODUCTION_LAUNCH_OK class=" + mainActCls);
        } else {
            println("M7_PROD_LAUNCHER: PRODUCTION_LAUNCH_NULL activity (see PRODUCTION_LAUNCH_THREW)");
        }
        println("M7_PROD_LAUNCHER: DONE");
        System.exit(0);
    }

    // ------------ helpers ------------

    static void println(String s) {
        // Route through NoiceDiscoverWrapper.println so the JNI-bound
        // native println in art-latest/stubs/binder_jni_stub.cc gets used
        // (System.out throws NPE in this dalvikvm).  Same approach as
        // M7-Step1's NoiceLauncher.
        try {
            NoiceDiscoverWrapper.println(s);
        } catch (Throwable t) {
            System.err.println(s);
        }
    }

    static void eprintln(String s) {
        try {
            NoiceDiscoverWrapper.eprintln(s);
        } catch (Throwable t) {
            System.err.println(s);
        }
    }

    static Properties loadManifest(String path) {
        File f = new File(path);
        if (!f.exists()) {
            throw new RuntimeException("manifest missing: " + path);
        }
        Properties p = new Properties();
        try (FileInputStream fis = new FileInputStream(f)) {
            p.load(fis);
        } catch (Exception e) {
            throw new RuntimeException("manifest read failed: " + e, e);
        }
        return p;
    }

    static String req(Properties p, String key) {
        String v = p.getProperty(key);
        if (v == null || v.trim().isEmpty()) {
            throw new RuntimeException("manifest key missing: " + key);
        }
        return v.trim();
    }

    static int parseIntOr(String s, int fallback) {
        if (s == null) return fallback;
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException nfe) {
            return fallback;
        }
    }
}
