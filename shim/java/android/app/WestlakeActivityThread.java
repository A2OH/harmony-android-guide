package android.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import com.westlake.engine.WestlakeLauncher;
import java.util.HashMap;
import java.util.Map;

/**
 * WestlakeActivityThread -- stripped-down ActivityThread for the Westlake Engine.
 *
 * Replaces MiniActivityManager as the activity lifecycle manager, using the REAL
 * AOSP activity creation flow:
 *
 *   Instrumentation.newActivity() -> AppComponentFactory.instantiateActivity()
 *   -> activity.attach() -> Instrumentation.callActivityOnCreate()
 *
 * This mirrors AOSP's ActivityThread with all Binder IPC, system service calls,
 * and WindowManager integration removed. Activities are managed in-process via
 * a simple token-based map.
 *
 * Key differences from AOSP:
 *   - No IActivityManager / ActivityTaskManager Binder IPC
 *   - No system server communication (attach doesn't call mgr.attachApplication)
 *   - No Configuration / CompatInfo propagation
 *   - No ResourcesManager or LoadedApk complexity -- uses simple PackageInfo holder
 *   - Activity.attach() is called via reflection since shim Activity may not have it
 *   - Single-threaded: Looper.loop() is a no-op, messages pumped externally
 *
 * Usage:
 *   WestlakeActivityThread.main(new String[]{ "/path/to/app.apk" });
 *   -- or --
 *   WestlakeActivityThread thread = WestlakeActivityThread.currentActivityThread();
 *   Activity a = thread.performLaunchActivity("com.example.MainActivity",
 *                                              "com.example", launchIntent, null);
 */
public class WestlakeActivityThread {

    private static final String TAG = "WestlakeActivityThread";

    private static String throwableSummary(Throwable t) {
        if (t == null) {
            return "null";
        }
        String message = null;
        try {
            message = t.getMessage();
        } catch (Throwable ignored) {
        }
        if (message == null || message.isEmpty()) {
            return t.getClass().getName();
        }
        return t.getClass().getName() + ": " + message;
    }

    // ── Singleton ──────────────────────────────────────────────────────────

    private static volatile WestlakeActivityThread sCurrentActivityThread;
    /** Queued dashboard activity to launch after render loop starts */
    public static volatile String pendingDashboardClass;

    /** Return the process-wide singleton. Creates one if none exists. */
    public static WestlakeActivityThread currentActivityThread() {
        if (sCurrentActivityThread == null) {
            synchronized (WestlakeActivityThread.class) {
                if (sCurrentActivityThread == null) {
                    sCurrentActivityThread = new WestlakeActivityThread();
                }
            }
        }
        return sCurrentActivityThread;
    }

    /** Return the current Application for this process. */
    public static Application currentApplication() {
        WestlakeActivityThread t = sCurrentActivityThread;
        return t != null ? t.mInitialApplication : null;
    }

    // ── Core state ─────────────────────────────────────────────────────────

    WestlakeInstrumentation mInstrumentation;
    Application mInitialApplication;
    Looper mLooper;
    private String mPackageName;
    private ClassLoader mClassLoader;
    private AppComponentFactory mAppComponentFactory;

    /** Token -> ActivityClientRecord map. Mirrors AOSP's mActivities. */
    final Map<IBinder, ActivityClientRecord> mActivities = new HashMap<>();

    /** The currently resumed activity (at most one). */
    private ActivityClientRecord mResumedRecord;

    // ── Inner class: ActivityClientRecord ───────────────────────────────────

    /**
     * Per-activity state record. Mirrors AOSP's ActivityThread.ActivityClientRecord
     * but without the heavyweight fields (activityInfo, compatInfo, etc.).
     */
    public static final class ActivityClientRecord {
        /** Unique token for this activity instance. */
        public IBinder token;

        /** The live Activity object. */
        public Activity activity;

        /** The intent that launched this activity. */
        public Intent intent;

        /** Component name (package + class). */
        public ComponentName component;

        /** Class name of the activity. */
        public String className;

        /** Package name. */
        public String packageName;

        /** Saved instance state (for restoring). */
        public Bundle savedState;

        /** Lifecycle state constants. */
        public static final int INITIALIZING = 0;
        public static final int CREATED = 1;
        public static final int STARTED = 2;
        public static final int RESUMED = 3;
        public static final int PAUSED = 4;
        public static final int STOPPED = 5;
        public static final int DESTROYED = 6;

        /** Current lifecycle state. */
        public int lifecycleState = INITIALIZING;

        /** Whether the activity called finish(). */
        public boolean finished;

        /** Caller record (for startActivityForResult). */
        public ActivityClientRecord caller;
        public int requestCode = -1;

        ActivityClientRecord() {
            // Generate a unique token for this activity
            token = new Binder("activity-token");
        }

        @Override
        public String toString() {
            return "ActivityClientRecord{" + className
                    + " state=" + lifecycleState
                    + " token=" + token + "}";
        }
    }

    // ── Inner class: PackageInfo (simplified LoadedApk) ────────────────────

    /**
     * Minimal stand-in for AOSP's LoadedApk. Holds the ClassLoader and can
     * create the Application singleton.
     */
    static final class PackageInfo {
        final String packageName;
        final ClassLoader classLoader;
        private Application mApplication;
        private AppComponentFactory mFactory;

        PackageInfo(String packageName, ClassLoader classLoader, AppComponentFactory factory) {
            this.packageName = packageName;
            this.classLoader = classLoader;
            this.mFactory = factory != null ? factory : new AppComponentFactory();
        }

        /**
         * Get or create the Application for this package.
         * Mirrors LoadedApk.makeApplication().
         *
         * @param forceNew If true, always create a new Application.
         * @param appClassName Application subclass name, or null for default.
         * @return The Application instance.
         */
        Application makeApplication(boolean forceNew, String appClassName,
                                     WestlakeInstrumentation instrumentation) {
            if (mApplication != null && !forceNew) {
                return mApplication;
            }

            Application app = null;
            String cls = appClassName;
            if (cls == null || cls.isEmpty()) {
                cls = "android.app.Application";
            }

            try {
                app = mFactory.instantiateApplication(classLoader, cls);
            } catch (Exception e) {
                log("W", "AppComponentFactory.instantiateApplication failed for "
                        + cls + ": " + throwableSummary(e));
            }

            if (app == null) {
                try {
                    Class<?> clazz = classLoader.loadClass(cls);
                    Object nativeInstance = com.westlake.engine.WestlakeLauncher.tryAllocInstance(clazz);
                    if (nativeInstance != null) {
                        app = (Application) nativeInstance;
                    } else {
                        Object unsafeInstance = AppComponentFactory.tryAllocateInstance(clazz);
                        if (unsafeInstance != null) {
                            app = (Application) unsafeInstance;
                        } else {
                            app = (Application) clazz.getDeclaredConstructor().newInstance();
                        }
                    }
                } catch (Exception e) {
                    log("W", "Fallback Application creation failed for " + cls + ": "
                            + throwableSummary(e));
                    app = new Application();
                }
            }

            // Set package name via ShimCompat (handles both shim and real Android)
            ShimCompat.setPackageName(app, packageName);

            mApplication = app;

            // Invoke Application.onCreate via Instrumentation
            if (instrumentation != null) {
                try {
                    instrumentation.callApplicationOnCreate(app);
                } catch (Exception e) {
                    log("W", "Application.onCreate() threw: " + throwableSummary(e));
                }
            }

            return app;
        }
    }

    // ── Constructor ────────────────────────────────────────────────────────

    public WestlakeActivityThread() {
        mLooper = Looper.getMainLooper();
    }

    // ── Getters ────────────────────────────────────────────────────────────

    public Application getApplication() {
        return mInitialApplication;
    }

    public WestlakeInstrumentation getInstrumentation() {
        return mInstrumentation;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public ClassLoader getClassLoader() {
        if (mClassLoader != null) return mClassLoader;
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return cl != null ? cl : ClassLoader.getSystemClassLoader();
    }

    public AppComponentFactory getAppComponentFactory() {
        return mAppComponentFactory;
    }

    /** Set a custom AppComponentFactory (e.g., Hilt's). */
    public void setAppComponentFactory(AppComponentFactory factory) {
        mAppComponentFactory = factory;
    }

    /** Find an ActivityClientRecord by its token. */
    public ActivityClientRecord getActivityRecord(IBinder token) {
        synchronized (mActivities) {
            return mActivities.get(token);
        }
    }

    /** Find an ActivityClientRecord by its Activity instance. */
    public ActivityClientRecord findRecord(Activity activity) {
        synchronized (mActivities) {
            for (ActivityClientRecord r : mActivities.values()) {
                if (r.activity == activity) return r;
            }
        }
        return null;
    }

    /** Return the currently resumed activity, or null. */
    public Activity getResumedActivity() {
        ActivityClientRecord r = mResumedRecord;
        return r != null ? r.activity : null;
    }

    // ── Activity lifecycle: launch ─────────────────────────────────────────

    /**
     * Launch an Activity by class name. This is the core AOSP-style launch flow:
     *
     *   1. Resolve ClassLoader and PackageInfo
     *   2. Create base Context for the activity
     *   3. Instrumentation.newActivity() -> AppComponentFactory.instantiateActivity()
     *   4. PackageInfo.makeApplication() (get or create the Application)
     *   5. activity.attach(context, this, instrumentation, ...) via reflection
     *   6. Instrumentation.callActivityOnCreate(activity, savedState)
     *
     * @param className   Fully-qualified Activity class name.
     * @param packageName Package name for the app.
     * @param intent      The launching Intent.
     * @param savedState  Saved instance state bundle, or null for fresh launch.
     * @return The launched Activity, or null on failure.
     */
    public static Activity launchActivity(WestlakeActivityThread thread,
                                          String className,
                                          String packageName,
                                          Intent intent) {
        if (thread == null) {
            throw new NullPointerException("thread");
        }
        return thread.performLaunchActivityImpl(className, packageName, intent, null);
    }

    public Activity performLaunchActivity(String className, String packageName,
                                           Intent intent, Bundle savedState) {
        return performLaunchActivityImpl(className, packageName, intent, savedState);
    }

    private Activity performLaunchActivityImpl(String className, String packageName,
                                               Intent intent, Bundle savedState) {
        WestlakeLauncher.trace("[WestlakeActivityThread] performLaunchActivity begin: " + className);
        log("I", "performLaunchActivity: " + className);

        // ── Step 1: Resolve component and classloader ──
        if (intent == null) {
            intent = new Intent();
        }
        String resolvedPackageName = resolveLaunchPackageName(packageName, className, intent);
        if (resolvedPackageName != null && !resolvedPackageName.isEmpty()) {
            packageName = resolvedPackageName;
            if (intent.getPackage() == null || intent.getPackage().isEmpty()
                    || isPlaceholderPackage(intent.getPackage())) {
                intent.setPackage(resolvedPackageName);
            }
        }
        ComponentName component = intent.getComponent();
        if (component == null || isPlaceholderPackage(component.getPackageName())) {
            String componentPackage = component != null ? component.getPackageName() : null;
            String stablePackage = resolveLaunchPackageName(componentPackage, className, intent);
            if (isPlaceholderPackage(stablePackage)) {
                stablePackage = choosePackageCandidate(packageName);
            }
            if (isPlaceholderPackage(stablePackage)) {
                stablePackage = knownPackageForClass(className);
            }
            if (!isPlaceholderPackage(stablePackage)) {
                component = new ComponentName(stablePackage, className);
                intent.setComponent(component);
                intent.setPackage(stablePackage);
            }
        }
        if (isPlaceholderPackage(packageName) && component != null) {
            packageName = component.getPackageName();
        }
        if (isPlaceholderPackage(packageName)) {
            packageName = resolveLaunchPackageName(packageName, className, intent);
        }
        if (component == null && !isPlaceholderPackage(packageName)) {
            component = new ComponentName(packageName, className);
            intent.setComponent(component);
            intent.setPackage(packageName);
        }
        WestlakeLauncher.trace("[WestlakeActivityThread] component resolved: pkg="
                + packageName + " cls=" + (component != null ? component.getClassName() : className));

        ClassLoader cl = getClassLoader();
        AppComponentFactory factory = mAppComponentFactory != null
                ? mAppComponentFactory : new AppComponentFactory();

        // ── Step 2: Create PackageInfo (simplified LoadedApk) ──
        PackageInfo packageInfo = new PackageInfo(packageName, cl, factory);

        // ── Step 3: Create base context for the activity ──
        // In AOSP this is ContextImpl.createBaseContextForActivity().
        // We use the Application as the base context, since our shim Context
        // is simple (no ContextImpl distinction).
        Context baseContext = mInitialApplication;
        if (baseContext == null) {
            baseContext = new Context();
        }

        // ── Step 3.5: Ensure DataSourceHelper is initialized before Activity constructor ──
        // Create stub proxies for ALL null interface fields on DataSourceHelper
        try {
            Class<?> helperClass = cl.loadClass("com.mcdonalds.mcdcoreapp.common.model.DataSourceHelper");
            final java.lang.reflect.InvocationHandler[] stubRef = new java.lang.reflect.InvocationHandler[1];
            stubRef[0] = new java.lang.reflect.InvocationHandler() {
                public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) {
                    String mn = method.getName();
                    if (mn.equals("toString")) return "StubProxy";
                    if (mn.equals("hashCode")) return Integer.valueOf(0);
                    if (mn.equals("equals")) return Boolean.FALSE;
                    Class<?> rt = method.getReturnType();
                    if (rt == boolean.class) return Boolean.FALSE;
                    if (rt == int.class) return Integer.valueOf(0);
                    if (rt == long.class) return Long.valueOf(0);
                    if (rt == float.class) return Float.valueOf(0);
                    if (rt == double.class) return Double.valueOf(0);
                    if (rt == String.class) return "";
                    if (rt.isInterface()) {
                        try {
                            ClassLoader rtCl = rt.getClassLoader();
                            if (rtCl == null) rtCl = method.getDeclaringClass().getClassLoader();
                            if (rtCl == null) rtCl = proxy.getClass().getClassLoader();
                            if (rtCl == null) rtCl = Thread.currentThread().getContextClassLoader();
                            if (rtCl == null) rtCl = ClassLoader.getSystemClassLoader();
                            Object nested = java.lang.reflect.Proxy.newProxyInstance(rtCl, new Class<?>[]{rt}, stubRef[0]);
                            log("D", "[StubProxy] " + mn + "() -> proxy(" + rt.getSimpleName()
                                    + ") cl=" + rtCl.getClass().getSimpleName());
                            return nested;
                        } catch (Throwable t) {
                            log("W", "[StubProxy] " + mn + "() -> fail: " + t.getMessage());
                            return null;
                        }
                    }
                    // For non-interface classes, try no-arg constructor first
                    try { return rt.getDeclaredConstructor().newInstance(); } catch (Throwable t) {}
                    // For abstract classes: use Unsafe.allocateInstance to create a bare instance
                    // (abstract method calls will throw AbstractMethodError, but at least it's non-null)
                    if (java.lang.reflect.Modifier.isAbstract(rt.getModifiers())) {
                        try {
                            java.lang.reflect.Field uf = Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");
                            uf.setAccessible(true);
                            Object unsafe = uf.get(null);
                            Object inst = unsafe.getClass().getMethod("allocateInstance", Class.class).invoke(unsafe, rt);
                            log("D", "[StubProxy] " + mn + "() -> Unsafe.allocateInstance(" + rt.getSimpleName() + ")");
                            return inst;
                        } catch (Throwable u) {
                            log("W", "[StubProxy] " + mn + "() -> Unsafe failed: " + u.getMessage());
                        }
                    }
                    return null;
                }
            };
            for (java.lang.reflect.Field f : safeGetDeclaredFields(helperClass)) {
                try {
                    if (!java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
                    f.setAccessible(true);
                    Class<?> fType = f.getType();
                    Object curVal = f.get(null);
                    log("D", "DataSourceHelper." + f.getName() + " type=" + fType.getSimpleName() + " isInterface=" + fType.isInterface() + " value=" + (curVal != null ? "set" : "null"));
                    if (curVal != null) continue;
                    if (fType == boolean.class) { f.set(null, true); continue; }
                    if (fType.isInterface()) {
                        ClassLoader fcl = fType.getClassLoader();
                        if (fcl == null) fcl = Thread.currentThread().getContextClassLoader();
                        if (fcl == null) fcl = ClassLoader.getSystemClassLoader();
                        Object stubProxy = java.lang.reflect.Proxy.newProxyInstance(fcl, new Class<?>[]{fType}, stubRef[0]);
                        f.set(null, stubProxy);
                        log("I", "DataSourceHelper." + f.getName() + " = stub proxy (" + fType.getSimpleName() + ")");
                    } else if (!fType.isPrimitive() && fType != String.class) {
                        try {
                            Object inst = fType.getDeclaredConstructor().newInstance();
                            f.set(null, inst);
                            log("I", "DataSourceHelper." + f.getName() + " = new " + fType.getSimpleName() + "()");
                        } catch (Throwable t) {}
                    }
                } catch (Throwable perFieldEx) {
                    log("W", "DataSourceHelper field '" + f.getName() + "' scan error: " + perFieldEx.getMessage());
                }
            }
            // Verify key methods return non-null after field scan
            // getOrderModuleInteractor() chains through dataSourceModuleProvider.I()
            // The field scan above should proxy dataSourceModuleProvider, making I() return a nested proxy.
            // But verify and fix if needed.
            for (java.lang.reflect.Method m : safeGetDeclaredMethods(helperClass)) {
                if (!java.lang.reflect.Modifier.isStatic(m.getModifiers())) continue;
                if (m.getParameterTypes().length > 0) continue;
                Class<?> mrt = m.getReturnType();
                if (!mrt.isInterface()) continue;
                m.setAccessible(true);
                try {
                    Object val = m.invoke(null);
                    if (val == null) {
                        log("W", "DataSourceHelper." + m.getName() + "() still returns null — scanning fields for indirect backing");
                        // The method may chain through a DIFFERENT-typed field (e.g., DataSourceModuleProvider.I() → OrderModuleInteractor)
                        // First try: find field with SAME type as return
                        boolean fixed = false;
                        for (java.lang.reflect.Field bf : safeGetDeclaredFields(helperClass)) {
                            try {
                                if (bf.getType() == mrt && java.lang.reflect.Modifier.isStatic(bf.getModifiers())) {
                                    bf.setAccessible(true);
                                    if (bf.get(null) == null) {
                                        ClassLoader bcl = mrt.getClassLoader();
                                        if (bcl == null) bcl = cl;
                                        Object proxy = java.lang.reflect.Proxy.newProxyInstance(bcl, new Class<?>[]{mrt}, stubRef[0]);
                                        bf.set(null, proxy);
                                        log("I", "DataSourceHelper." + bf.getName() + " = proxy(" + mrt.getSimpleName() + ") [via " + m.getName() + "()]");
                                        fixed = true;
                                        break;
                                    }
                                }
                            } catch (Throwable t) {}
                        }
                        // Still null? The backing field might be a different interface type (e.g., DataSourceModuleProvider)
                        // Re-check all interface fields — ensure they're proxied
                        if (!fixed) {
                            for (java.lang.reflect.Field bf : safeGetDeclaredFields(helperClass)) {
                                try {
                                    if (!java.lang.reflect.Modifier.isStatic(bf.getModifiers())) continue;
                                    bf.setAccessible(true);
                                    if (bf.get(null) != null) continue;
                                    Class<?> bfType = bf.getType();
                                    if (!bfType.isInterface()) continue;
                                    ClassLoader bcl = bfType.getClassLoader();
                                    if (bcl == null) bcl = cl;
                                    Object proxy = java.lang.reflect.Proxy.newProxyInstance(bcl, new Class<?>[]{bfType}, stubRef[0]);
                                    bf.set(null, proxy);
                                    log("I", "DataSourceHelper." + bf.getName() + " = proxy(" + bfType.getSimpleName() + ") [re-scan for " + m.getName() + "()]");
                                } catch (Throwable t) {}
                            }
                            // Re-test
                            try {
                                val = m.invoke(null);
                                if (val != null) log("I", "DataSourceHelper." + m.getName() + "() → fixed (now " + val.getClass().getSimpleName() + ")");
                                else log("W", "DataSourceHelper." + m.getName() + "() → still null after re-scan");
                            } catch (Throwable t) {
                                log("W", "DataSourceHelper." + m.getName() + "() re-test threw: " + t.getMessage());
                            }
                        }
                    } else {
                        log("D", "DataSourceHelper." + m.getName() + "() = " + val.getClass().getSimpleName() + " (ok)");
                    }
                } catch (Throwable ignored) {
                    log("W", "DataSourceHelper." + m.getName() + "() threw: " + ignored.getMessage());
                }
            }
        } catch (Throwable t) {
            log("W", "Step 3.5 DataSourceHelper error: " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }

        // ── Step 4: Instantiate the Activity via Instrumentation ──
        Activity activity = null;
        try {
            WestlakeLauncher.trace("[WestlakeActivityThread] step4 newActivity");
            activity = mInstrumentation.newActivity(cl, className, intent);
            if (activity == null) {
                throw new InstantiationException(
                        "Instrumentation returned null for " + className);
            }
            WestlakeLauncher.trace("[WestlakeActivityThread] step4 newActivity OK: "
                    + activity.getClass().getName());
            log("I", "  Created activity: " + activity.getClass().getName());
        } catch (Exception e) {
            WestlakeLauncher.dumpThrowable("[WestlakeActivityThread] step4 newActivity failed", e);
            if (!mInstrumentation.onException(null, e)) {
                log("E", "  Unable to instantiate activity " + className + ": " + e);
                throw new RuntimeException(
                        "Unable to instantiate activity " + component + ": " + e, e);
            }
            return null;
        }

        // ── Step 5: Get or create the Application ──
        // IMPORTANT: reuse existing Application — do NOT call makeApplication() again
        // as it calls Application.onCreate() which blocks on initializer chains
        Application app = mInitialApplication;
        if (app == null) {
            // Try MiniServer's application first
            try { app = MiniServer.get().getApplication(); } catch (Exception ignored) {}
        }
        if (app == null) {
            String appClassName = null;
            MiniServer server = null;
            try { server = MiniServer.get(); } catch (Exception ignored) {}
            if (server != null) {
                ApkInfo info = server.getApkInfo();
                if (info != null) appClassName = info.applicationClassName;
            }
            app = packageInfo.makeApplication(false, appClassName, mInstrumentation);
            mInitialApplication = app;
        } else {
            log("D", "  Reusing existing Application (skip makeApplication)");
        }

        // ── Step 6: Attach the activity ──
        log("D", "  Step 6: attach " + className);
        WestlakeLauncher.trace("[WestlakeActivityThread] step6 attach begin");
        attachActivity(activity, baseContext, app, intent, component);
        WestlakeLauncher.trace("[WestlakeActivityThread] step6 attach done");

        // ── Step 7: Create the ActivityClientRecord ──
        WestlakeLauncher.trace("[WestlakeActivityThread] step7 record begin");
        ActivityClientRecord r = new ActivityClientRecord();
        WestlakeLauncher.trace("[WestlakeActivityThread] step7 record created");
        r.activity = activity;
        r.intent = intent;
        r.component = component;
        r.className = className;
        r.packageName = packageName;
        r.savedState = savedState;
        r.lifecycleState = ActivityClientRecord.CREATED;

        synchronized (mActivities) {
            mActivities.put(r.token, r);
        }
        WestlakeLauncher.trace("[WestlakeActivityThread] step7 record stored");

        // ── Step 8: Call onCreate via Instrumentation ──
        log("D", "  Step 8: callActivityOnCreate " + className);
        try {
            ensureNamedComponentActivitySavedStateReady(activity);
            WestlakeLauncher.trace("[WestlakeActivityThread] step8 onCreate begin");
            mInstrumentation.callActivityOnCreate(activity, savedState);
            WestlakeLauncher.trace("[WestlakeActivityThread] step8 onCreate done");
            log("I", "  onCreate complete for " + className);
        } catch (Throwable e) {
            WestlakeLauncher.dumpThrowable("[WestlakeActivityThread] step8 onCreate failed", e);
            if (!mInstrumentation.onException(activity, e)) {
                log("E", "  onCreate failed for " + className + ": " + e);
                throw new RuntimeException(
                        "Unable to start activity " + component + ": " + e, e);
            }
        }

        return activity;
    }

    private void ensureNamedComponentActivitySavedStateReady(Activity activity) {
        if (activity == null) {
            return;
        }
        try {
            Class<?> componentActivityClass =
                    findNamedClassOnHierarchy(activity.getClass(), "androidx.activity.ComponentActivity");
            if (componentActivityClass == null) {
                return;
            }
            Object controller = getNamedField(activity, componentActivityClass,
                    "savedStateRegistryController");
            if (controller == null) {
                ClassLoader loader = chooseClassLoader(componentActivityClass);
                controller = newSavedStateRegistryController(loader, activity);
                if (controller != null) {
                    setNamedField(activity, componentActivityClass, "savedStateRegistryController",
                            controller);
                    controller = getNamedField(activity, componentActivityClass,
                            "savedStateRegistryController");
                }
            }
            if (controller != null) {
                invokeNoArgIfPresent(controller, "c", "performAttach");
                enableNamedSavedStateHandles(chooseClassLoader(controller.getClass()), activity);
            }
        } catch (Throwable t) {
            WestlakeLauncher.dumpThrowable(
                    "[WestlakeActivityThread] ensureNamedComponentActivitySavedStateReady", t);
        }
    }

    private static boolean isPlaceholderPackage(String packageName) {
        return packageName == null
                || packageName.isEmpty()
                || "app".equals(packageName)
                || "com.example.app".equals(packageName);
    }

    private static String knownPackageForClass(String className) {
        if (className == null || className.isEmpty()) {
            return null;
        }
        if (className.startsWith("com.mcdonalds.")) {
            return "com.mcdonalds.app";
        }
        return null;
    }

    private static String choosePackageCandidate(String candidate) {
        if (isPlaceholderPackage(candidate)) {
            return null;
        }
        return candidate;
    }

    private static String resolveLaunchPackageName(String packageName, String className, Intent intent) {
        String candidate = choosePackageCandidate(packageName);
        if (candidate != null) {
            return candidate;
        }
        if (intent != null) {
            candidate = choosePackageCandidate(intent.getPackage());
            if (candidate != null) {
                return candidate;
            }
            ComponentName component = intent.getComponent();
            if (component != null) {
                candidate = choosePackageCandidate(component.getPackageName());
                if (candidate != null) {
                    return candidate;
                }
            }
        }
        candidate = choosePackageCandidate(MiniServer.currentPackageName());
        if (candidate != null) {
            return candidate;
        }
        candidate = choosePackageCandidate(System.getProperty("westlake.apk.package"));
        if (candidate != null) {
            return candidate;
        }
        candidate = knownPackageForClass(className);
        if (candidate != null) {
            return candidate;
        }
        if (packageName != null && !packageName.isEmpty()) {
            return packageName;
        }
        return null;
    }

    /**
     * Attach framework state to the Activity. Tries the AOSP-style attach() method
     * via reflection first; falls back to setting fields directly.
     *
     * The AOSP Activity.attach() signature is:
     *   attach(Context, ActivityThread, Instrumentation, IBinder token, int ident,
     *          Application, Intent, ActivityInfo, CharSequence title, Activity parent,
     *          String embeddedID, NonConfigurationInstances, Configuration,
     *          String referrer, IVoiceInteractor, Window, ActivityConfigCallback,
     *          IBinder assistToken)
     *
     * Our shim Activity does NOT define attach(). We directly set the fields that
     * the Activity class expects (mApplication, mIntent, mComponent, etc.).
     *
     * NOTE: If attach() is added to the shim's Activity in the future, this method
     * will use it automatically via the reflection path.
     */
    private void attachActivity(Activity activity, Context baseContext,
                                 Application app, Intent intent,
                                 ComponentName component) {
        WestlakeLauncher.trace("[WestlakeActivityThread] attachActivity begin: "
                + activity.getClass().getName());
        boolean attached = false;
        try {
            setInstanceField(activity, android.content.Context.class, "mBase", baseContext);
            attached = true;
            WestlakeLauncher.trace("[WestlakeActivityThread] attachActivity base context set");
        } catch (Throwable e) {
            WestlakeLauncher.dumpThrowable("[WestlakeActivityThread] attachActivity base context failed", e);
        }

        ensureActivityWindow(activity);

        // Try 2: Direct field setting (always works with the shim's Activity)
        // Even if attach() succeeded, ensure critical fields are set.
        try { setInstanceField(activity, android.app.Activity.class, "mApplication", app); } catch (Throwable ignored) {}
        try { setInstanceField(activity, android.app.Activity.class, "mIntent", intent); } catch (Throwable ignored) {}
        try { setInstanceField(activity, android.app.Activity.class, "mComponent", component); } catch (Throwable ignored) {}
        try { setInstanceField(activity, android.app.Activity.class, "mFinished", Boolean.FALSE); } catch (Throwable ignored) {}
        try { setInstanceField(activity, android.app.Activity.class, "mDestroyed", Boolean.FALSE); } catch (Throwable ignored) {}
        WestlakeLauncher.trace("[WestlakeActivityThread] attachActivity core fields set");
        initializeAndroidxActivityState(activity);
        WestlakeLauncher.trace("[WestlakeActivityThread] attachActivity AndroidX init done");

        // Skip direct framework AssetManager surgery here. It is still unstable on the
        // standalone ART path and can recurse badly during reflected field access.
        WestlakeLauncher.trace("[WestlakeActivityThread] attachActivity asset inject skipped");

        // Wire ResourceTable to the activity's resources
        // Try MiniServer's ApkInfo first (it has the parsed resources.arsc)
        try {
            android.content.res.Resources actRes = activity.getResources();
            String resDir = System.getProperty("westlake.apk.resdir");
            WestlakeLauncher.trace("[WestlakeActivityThread] attachActivity resource wiring begin");

            // Try to get ResourceTable from MiniServer's ApkInfo
            android.app.MiniServer server = android.app.MiniServer.get();
            if (server != null && actRes != null) {
                try {
                    java.lang.reflect.Field apkField = android.app.MiniServer.class.getDeclaredField("mApkInfo");
                    apkField.setAccessible(true);
                    Object apkInfo = apkField.get(server);
                    if (apkInfo != null) {
                        java.lang.reflect.Field rtField = apkInfo.getClass().getField("resourceTable");
                        Object table = rtField.get(apkInfo);
                        if (table instanceof android.content.res.ResourceTable) {
                            ShimCompat.loadResourceTable(actRes, (android.content.res.ResourceTable) table);
                            WestlakeLauncher.trace("[WestlakeActivityThread] attachActivity resource table wired");
                            log("I", "Wired ResourceTable to " + activity.getClass().getSimpleName());
                        }
                    }
                } catch (Throwable ex) { /* MiniServer may not have ApkInfo */ }
            }
            // Also try from Application's resources
            android.content.res.Resources appRes = app != null ? app.getResources() : null;
            if (appRes != null && actRes != null) {
                try {
                    java.lang.reflect.Field tableField = android.content.res.Resources.class.getDeclaredField("mTable");
                    tableField.setAccessible(true);
                    Object table = tableField.get(appRes);
                    if (table != null) tableField.set(actRes, table);
                } catch (NoSuchFieldException e) { /* field may not exist */ }
            }
            ShimCompat.setApkPath(actRes, System.getProperty("westlake.apk.path"));
            if (resDir != null) ShimCompat.setAssetDir(activity.getAssets(), resDir);
            WestlakeLauncher.trace("[WestlakeActivityThread] attachActivity resource wiring done");
        } catch (Throwable t) {
            WestlakeLauncher.dumpThrowable("[WestlakeActivityThread] attachActivity resource wiring failed", t);
            log("W", "Resource wiring: " + t.getMessage());
        }

        // Skip attachBaseContext — mBase was already set directly above.
        // attachBaseContext is protected and inaccessible cross-classloader on app_process64.

        if (!attached) {
            log("D", "  Attached via direct field setting");
        }
        WestlakeLauncher.trace("[WestlakeActivityThread] attachActivity end");
    }

    private void ensureActivityWindow(Activity activity) {
        if (activity == null) {
            return;
        }
        android.view.Window window = null;
        try {
            window = activity.getWindow();
        } catch (Throwable ignored) {
        }
        if (window == null) {
            try {
                android.view.Window fallback = new android.view.Window(activity);
                fallback.setCallback(activity);
                setInstanceField(activity, android.app.Activity.class, "mWindow", fallback);
                window = fallback;
                WestlakeLauncher.trace("[WestlakeActivityThread] attachActivity fallback window created");
            } catch (Throwable t) {
                WestlakeLauncher.dumpThrowable(
                        "[WestlakeActivityThread] attachActivity fallback window failed", t);
                return;
            }
        }
        try {
            window.setCallback(activity);
        } catch (Throwable t) {
            WestlakeLauncher.dumpThrowable(
                    "[WestlakeActivityThread] attachActivity window callback failed", t);
        }
    }

    private void initializeAndroidxActivityState(Activity activity) {
        boolean anySeeded = false;
        try {
            anySeeded |= initializeNamedCoreComponentActivityState(activity);
        } catch (Throwable t) {
            WestlakeLauncher.dumpThrowable("[WestlakeActivityThread] AndroidX core init failed", t);
            log("W", "AndroidX core init failed: " + throwableTag(t));
        }
        try {
            anySeeded |= initializeNamedComponentActivityState(activity);
        } catch (Throwable t) {
            WestlakeLauncher.dumpThrowable("[WestlakeActivityThread] AndroidX component init failed", t);
            log("W", "AndroidX component init failed: " + throwableTag(t));
        }
        try {
            anySeeded |= initializeNamedAppCompatActivityState(activity);
        } catch (Throwable t) {
            WestlakeLauncher.dumpThrowable("[WestlakeActivityThread] AndroidX appcompat init failed", t);
            log("W", "AndroidX appcompat init failed: " + throwableTag(t));
        }
        if (anySeeded) {
            WestlakeLauncher.trace("[WestlakeActivityThread] AndroidX init complete");
        }
    }

    private boolean initializeNamedCoreComponentActivityState(Activity activity) throws Exception {
        Class<?> coreActivityClass =
                findNamedClassOnHierarchy(activity.getClass(), "androidx.core.app.ComponentActivity");
        if (coreActivityClass == null) {
            return false;
        }
        ClassLoader loader = chooseClassLoader(coreActivityClass);
        WestlakeLauncher.trace("[WestlakeActivityThread] AndroidX core init lifecycle");
        Object lifecycleRegistry = null;
        if (activity instanceof androidx.lifecycle.LifecycleOwner) {
            lifecycleRegistry = new androidx.lifecycle.LifecycleRegistry(
                    (androidx.lifecycle.LifecycleOwner) activity);
        } else {
            Class<?> lifecycleOwnerClass = loadNamedClass(loader, "androidx.lifecycle.LifecycleOwner");
            lifecycleRegistry = newNamedInstance(loader, "androidx.lifecycle.LifecycleRegistry",
                    new Class<?>[]{lifecycleOwnerClass}, new Object[]{activity});
        }
        setNamedFieldIfNull(activity, coreActivityClass, "lifecycleRegistry",
                lifecycleRegistry);
        WestlakeLauncher.trace("[WestlakeActivityThread] AndroidX core init extra data");
        setNamedFieldIfNull(activity, coreActivityClass, "extraDataMap",
                newNamedInstance(loader, "androidx.collection.SimpleArrayMap"));
        return true;
    }

    private boolean initializeNamedComponentActivityState(Activity activity) throws Exception {
        Class<?> componentActivityClass =
                findNamedClassOnHierarchy(activity.getClass(), "androidx.activity.ComponentActivity");
        if (componentActivityClass == null) {
            return false;
        }
        ClassLoader loader = chooseClassLoader(componentActivityClass);
        WestlakeLauncher.trace("[WestlakeActivityThread] AndroidX init context helper");
        setNamedFieldIfNull(activity, componentActivityClass, "contextAwareHelper",
                newNamedInstance(loader, "androidx.activity.contextaware.ContextAwareHelper"));
        WestlakeLauncher.trace("[WestlakeActivityThread] AndroidX init menu host");
        setNamedFieldIfNull(activity, componentActivityClass, "menuHostHelper",
                newNamedInstance(loader, "androidx.core.view.MenuHostHelper",
                        new Class<?>[]{Runnable.class},
                        new Object[]{new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    activity.invalidateOptionsMenu();
                                } catch (Throwable ignored) {
                                }
                            }
                        }}));
        WestlakeLauncher.trace("[WestlakeActivityThread] AndroidX init saved state");
        Object savedStateController = newSavedStateRegistryController(loader, activity);
        if (savedStateController != null) {
            // Constructor-bypassed APK-owned ComponentActivity expects this final field to
            // exist before onCreate() runs; write it directly and verify the readback.
            setNamedField(activity, componentActivityClass, "savedStateRegistryController",
                    savedStateController);
            Object savedStateReadback = getNamedField(activity, componentActivityClass,
                    "savedStateRegistryController");
            Log.i(TAG, "savedState controller write="
                    + describeObject(savedStateController)
                    + " readback=" + describeObject(savedStateReadback));
            log("I", "AndroidX saved-state controller="
                    + describeObject(savedStateController)
                    + " readback=" + describeObject(savedStateReadback));
            invokeNoArgIfPresent(savedStateReadback, "c", "performAttach");
            enableNamedSavedStateHandles(chooseClassLoader(savedStateReadback.getClass()), activity);
            log("I", "AndroidX saved-state attach invoked for "
                    + componentActivityClass.getName());
            return true;
        }
        return false;
    }

    private boolean initializeNamedAppCompatActivityState(Activity activity) throws Exception {
        Class<?> appCompatActivityClass =
                findNamedClassOnHierarchy(activity.getClass(), "androidx.appcompat.app.AppCompatActivity");
        if (appCompatActivityClass == null) {
            return false;
        }
        WestlakeLauncher.trace("[WestlakeActivityThread] AndroidX appcompat init");
        invokeNoArgIfPresent(activity, appCompatActivityClass, "Z");
        return true;
    }

    private Object newSavedStateRegistryController(ClassLoader loader, Activity activity) {
        ClassLoader activityLoader = activity != null ? chooseClassLoader(activity.getClass()) : loader;
        Object controller = tryBuildSavedStateRegistryController(
                activityLoader,
                activity,
                true,
                "app");
        if (controller != null) {
            return controller;
        }
        if (loader != activityLoader) {
            controller = tryBuildSavedStateRegistryController(
                    loader,
                    activity,
                    true,
                    "owner-child-first");
            if (controller != null) {
                return controller;
            }
        }
        controller = tryBuildSavedStateRegistryController(
                loader,
                activity,
                false,
                "owner");
        if (controller != null) {
            return controller;
        }
        if (loader != activityLoader) {
            controller = tryBuildSavedStateRegistryController(
                    activityLoader,
                    activity,
                    false,
                    "app-parent-first");
            if (controller != null) {
                return controller;
            }
        }
        if (activity instanceof androidx.savedstate.SavedStateRegistryOwner) {
            Log.i(TAG, "savedState shim-direct activityClass="
                    + describeClass(activity.getClass())
                    + " ownerMatch=true");
            return androidx.savedstate.SavedStateRegistryController.create(
                    (androidx.savedstate.SavedStateRegistryOwner) activity);
        }
        return null;
    }

    private Object tryBuildSavedStateRegistryController(ClassLoader loader,
                                                        Activity activity,
                                                        boolean childFirst,
                                                        String label) {
        if (loader == null || activity == null) {
            return null;
        }
        try {
            Class<?> controllerClass = childFirst
                    ? loadNamedClassChildFirst(loader,
                            "androidx.savedstate.SavedStateRegistryController")
                    : loadNamedClass(loader, "androidx.savedstate.SavedStateRegistryController");
            Class<?> ownerClass = childFirst
                    ? loadNamedClassChildFirst(loader,
                            "androidx.savedstate.SavedStateRegistryOwner")
                    : loadNamedClass(loader, "androidx.savedstate.SavedStateRegistryOwner");
            boolean ownerMatch = ownerClass != null && ownerClass.isInstance(activity);
            Log.i(TAG, "savedState " + label
                    + " controller=" + describeClass(controllerClass)
                    + " owner=" + describeClass(ownerClass)
                    + " activityClass=" + describeClass(activity.getClass())
                    + " ownerMatch=" + ownerMatch);
            if (!ownerMatch) {
                return null;
            }
            return buildSavedStateRegistryController(
                    controllerClass,
                    ownerClass,
                    activity,
                    "[WestlakeActivityThread] " + label + " SavedStateRegistryController");
        } catch (Throwable t) {
            WestlakeLauncher.dumpThrowable(
                    "[WestlakeActivityThread] " + label + " SavedStateRegistryController init", t);
            return null;
        }
    }

    private Object buildSavedStateRegistryController(Class<?> controllerClass,
                                                     Class<?> ownerClass,
                                                     Activity activity,
                                                     String label) {
        if (controllerClass == null
                || ownerClass == null
                || activity == null
                || !ownerClass.isInstance(activity)) {
            return null;
        }
        Object controller = invokeStaticSingleArgIfPresent(controllerClass, "a", ownerClass, activity);
        if (controller != null) {
            return controller;
        }
        controller = invokeStaticSingleArgIfPresent(controllerClass, "create", ownerClass, activity);
        if (controller != null) {
            return controller;
        }
        try {
            java.lang.reflect.Field companionField = controllerClass.getDeclaredField("c");
            companionField.setAccessible(true);
            Object companion = companionField.get(null);
            controller = invokeSingleArgIfPresent(companion, "b", ownerClass, activity);
            if (controller != null) {
                return controller;
            }
        } catch (NoSuchFieldException ignored) {
        } catch (Throwable companionError) {
            WestlakeLauncher.dumpThrowable(label + " companion", companionError);
        }
        return null;
    }

    private void enableNamedSavedStateHandles(ClassLoader loader, Activity activity) {
        if (activity == null) {
            return;
        }
        try {
            Class<?> appSupportClass =
                    loadNamedClassChildFirst(loader,
                            "androidx.lifecycle.SavedStateHandleSupport");
            Class<?> appOwnerClass =
                    loadNamedClassChildFirst(loader,
                            "androidx.savedstate.SavedStateRegistryOwner");
            if (appSupportClass != null
                    && appOwnerClass != null
                    && appOwnerClass.isInstance(activity)) {
                invokeStaticSingleArgIfPresent(appSupportClass, "c", appOwnerClass, activity);
                return;
            }

            Class<?> supportClass =
                    loadNamedClass(loader, "androidx.lifecycle.SavedStateHandleSupport");
            Class<?> ownerClass =
                    loadNamedClass(loader, "androidx.savedstate.SavedStateRegistryOwner");
            invokeStaticSingleArgIfPresent(supportClass, "c", ownerClass, activity);
        } catch (Throwable t) {
            WestlakeLauncher.dumpThrowable(
                    "[WestlakeActivityThread] SavedStateHandleSupport enable", t);
        }
    }

    private void setInstanceFieldIfNull(Object target, Class<?> owner, String fieldName, Object value)
            throws Exception {
        java.lang.reflect.Field field = owner.getDeclaredField(fieldName);
        field.setAccessible(true);
        Object current = field.get(target);
        if (current == null) {
            field.set(target, value);
        }
    }

    private void setInstanceField(Object target, Class<?> owner, String fieldName, Object value)
            throws Exception {
        java.lang.reflect.Field field = owner.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private void setNamedFieldIfNull(Object target, Class<?> owner, String fieldName, Object value)
            throws Exception {
        if (target == null || owner == null || value == null) {
            return;
        }
        java.lang.reflect.Field field = findFieldOnHierarchy(owner, fieldName);
        if (field == null) {
            return;
        }
        field.setAccessible(true);
        Object current = field.get(target);
        if (current == null) {
            setFieldValue(target, field, value);
        }
    }

    private void setNamedField(Object target, Class<?> owner, String fieldName, Object value)
            throws Exception {
        if (target == null || owner == null || value == null) {
            return;
        }
        java.lang.reflect.Field field = findFieldOnHierarchy(owner, fieldName);
        if (field == null) {
            return;
        }
        setFieldValue(target, field, value);
    }

    private Object getNamedField(Object target, Class<?> owner, String fieldName) throws Exception {
        if (target == null || owner == null) {
            return null;
        }
        java.lang.reflect.Field field = findFieldOnHierarchy(owner, fieldName);
        if (field == null) {
            return null;
        }
        field.setAccessible(true);
        return field.get(target);
    }

    private void setFieldValue(Object target, java.lang.reflect.Field field, Object value)
            throws Exception {
        if (target == null || field == null) {
            return;
        }
        field.setAccessible(true);
        try {
            field.set(target, value);
            Object current = field.get(target);
            if (current == value || (current != null && current.equals(value))) {
                return;
            }
        } catch (Throwable ignored) {
        }
        forceSetFieldViaUnsafe(target, field, value);
    }

    private void forceSetFieldViaUnsafe(Object target, java.lang.reflect.Field field, Object value)
            throws Exception {
        Throwable lastError = null;
        String[] unsafeClasses = {"jdk.internal.misc.Unsafe", "sun.misc.Unsafe"};
        for (String unsafeName : unsafeClasses) {
            try {
                Class<?> unsafeClass = Class.forName(unsafeName);
                java.lang.reflect.Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
                unsafeField.setAccessible(true);
                Object unsafe = unsafeField.get(null);
                long offset = ((Number) unsafeClass
                        .getMethod("objectFieldOffset", java.lang.reflect.Field.class)
                        .invoke(unsafe, field)).longValue();
                unsafeClass.getMethod("putObject", Object.class, long.class, Object.class)
                        .invoke(unsafe, target, offset, value);
                return;
            } catch (Throwable t) {
                lastError = t;
            }
        }
        if (lastError instanceof Exception) {
            throw (Exception) lastError;
        }
        if (lastError instanceof Error) {
            throw (Error) lastError;
        }
        throw new IllegalStateException("Unable to force-set field " + field.getName());
    }

    private Object newNamedOwnedInstance(ClassLoader loader, String className,
                                         Class<?> ownerClass, Object ownerInstance,
                                         String errorLabel) {
        try {
            return newNamedInstance(loader, className, new Class<?>[]{ownerClass},
                    new Object[]{ownerInstance});
        } catch (Throwable t) {
            WestlakeLauncher.dumpThrowable(errorLabel, t);
            return null;
        }
    }

    private Object newNamedLazy(ClassLoader loader, String producerClassName,
                                Class<?> ownerClass, Object ownerInstance,
                                String errorLabel) {
        try {
            Object producer = newNamedOwnedInstance(loader, producerClassName, ownerClass,
                    ownerInstance, errorLabel);
            if (producer == null) {
                return null;
            }
            Class<?> function0Class = loadNamedClass(loader, "kotlin.jvm.functions.Function0");
            Class<?> lazyKtClass = loadNamedClass(loader, "kotlin.LazyKt");
            java.lang.reflect.Method method =
                    findMethodOnHierarchy(lazyKtClass, "b", function0Class);
            if (method == null || !java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
                return null;
            }
            method.setAccessible(true);
            return method.invoke(null, producer);
        } catch (Throwable t) {
            WestlakeLauncher.dumpThrowable(errorLabel, t);
            return null;
        }
    }

    private String describeObject(Object value) {
        if (value == null) {
            return "null";
        }
        Class<?> cls = value.getClass();
        ClassLoader loader = cls.getClassLoader();
        return cls.getName() + " loader="
                + (loader == null ? "bootstrap" : loader.getClass().getName());
    }

    private String describeClass(Class<?> cls) {
        if (cls == null) {
            return "null";
        }
        ClassLoader loader = cls.getClassLoader();
        return cls.getName() + " loader="
                + (loader == null ? "bootstrap" : loader.getClass().getName());
    }

    private java.lang.reflect.Field findFieldOnHierarchy(Class<?> start, String fieldName) {
        Class<?> current = start;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
            }
            current = current.getSuperclass();
        }
        return null;
    }

    private Class<?> findNamedClassOnHierarchy(Class<?> start, String className) {
        Class<?> current = start;
        while (current != null && current != Object.class) {
            if (className.equals(current.getName())) {
                return current;
            }
            current = current.getSuperclass();
        }
        return null;
    }

    private ClassLoader chooseClassLoader(Class<?> cls) {
        if (cls != null && cls.getClassLoader() != null) {
            return cls.getClassLoader();
        }
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        if (contextLoader != null) {
            return contextLoader;
        }
        return ClassLoader.getSystemClassLoader();
    }

    private Class<?> loadNamedClass(ClassLoader loader, String className) throws ClassNotFoundException {
        if (loader != null) {
            return Class.forName(className, false, loader);
        }
        return Class.forName(className);
    }

    private Class<?> loadNamedClassChildFirst(ClassLoader loader, String className)
            throws ClassNotFoundException {
        if (loader != null) {
            try {
                java.lang.reflect.Method findLoaded =
                        ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
                findLoaded.setAccessible(true);
                Object loaded = findLoaded.invoke(loader, className);
                if (loaded instanceof Class<?>) {
                    return (Class<?>) loaded;
                }
            } catch (Throwable ignored) {
            }
            try {
                java.lang.reflect.Method findClass =
                        findMethodOnHierarchy(loader.getClass(), "findClass", String.class);
                if (findClass != null) {
                    findClass.setAccessible(true);
                    Object direct = findClass.invoke(loader, className);
                    if (direct instanceof Class<?>) {
                        return (Class<?>) direct;
                    }
                }
            } catch (java.lang.reflect.InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (!(cause instanceof ClassNotFoundException)) {
                    throw new ClassNotFoundException(className, cause);
                }
            } catch (Throwable ignored) {
            }
        }
        return loadNamedClass(loader, className);
    }

    private Object newNamedInstance(ClassLoader loader, String className) throws Exception {
        Class<?> cls = loadNamedClass(loader, className);
        java.lang.reflect.Constructor<?> ctor = cls.getDeclaredConstructor();
        ctor.setAccessible(true);
        return ctor.newInstance();
    }

    private Object newNamedInstance(ClassLoader loader, String className,
                                    Class<?>[] parameterTypes, Object[] args) throws Exception {
        Class<?> cls = loadNamedClass(loader, className);
        java.lang.reflect.Constructor<?> ctor = cls.getDeclaredConstructor(parameterTypes);
        ctor.setAccessible(true);
        return ctor.newInstance(args);
    }

    private Object invokeNoArgIfPresent(Object target, String... methodNames) {
        if (target == null || methodNames == null) {
            return null;
        }
        for (String methodName : methodNames) {
            if (methodName == null || methodName.isEmpty()) {
                continue;
            }
            try {
                java.lang.reflect.Method method = findMethodOnHierarchy(target.getClass(), methodName);
                if (method == null) {
                    continue;
                }
                method.setAccessible(true);
                return method.invoke(target);
            } catch (Throwable t) {
                WestlakeLauncher.dumpThrowable(
                        "[WestlakeActivityThread] invoke " + methodName + " failed", t);
            }
        }
        return null;
    }

    private Object invokeNoArgIfPresent(Object target, Class<?> owner, String... methodNames) {
        if (owner == null) {
            return null;
        }
        for (String methodName : methodNames) {
            if (methodName == null || methodName.isEmpty()) {
                continue;
            }
            try {
                java.lang.reflect.Method method = findMethodOnHierarchy(owner, methodName);
                if (method == null) {
                    continue;
                }
                method.setAccessible(true);
                return method.invoke(target);
            } catch (Throwable t) {
                WestlakeLauncher.dumpThrowable(
                        "[WestlakeActivityThread] invoke " + methodName + " failed", t);
            }
        }
        return null;
    }

    private Object invokeSingleArgIfPresent(Object target, String methodName,
                                            Class<?> parameterType, Object arg) {
        if (target == null || methodName == null) {
            return null;
        }
        try {
            java.lang.reflect.Method method =
                    findMethodOnHierarchy(target.getClass(), methodName, parameterType);
            if (method == null) {
                return null;
            }
            method.setAccessible(true);
            return method.invoke(target, arg);
        } catch (Throwable t) {
            WestlakeLauncher.dumpThrowable(
                    "[WestlakeActivityThread] invoke " + methodName + " failed", t);
            return null;
        }
    }

    private Object invokeStaticSingleArgIfPresent(Class<?> owner, String methodName,
                                                  Class<?> parameterType, Object arg) {
        if (owner == null || methodName == null) {
            return null;
        }
        try {
            java.lang.reflect.Method method = findMethodOnHierarchy(owner, methodName, parameterType);
            if (method == null || !java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
                return null;
            }
            method.setAccessible(true);
            return method.invoke(null, arg);
        } catch (Throwable t) {
            WestlakeLauncher.dumpThrowable(
                    "[WestlakeActivityThread] invoke static " + methodName + " failed", t);
            return null;
        }
    }

    private java.lang.reflect.Method findMethodOnHierarchy(Class<?> start, String methodName,
                                                           Class<?>... parameterTypes) {
        Class<?> current = start;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException ignored) {
            }
            current = current.getSuperclass();
        }
        return null;
    }

    /**
     * Search for an attach() method on the Activity class hierarchy.
     * Returns null if not found.
     */
    private java.lang.reflect.Method findAttachMethod(Class<?> clazz) {
        while (clazz != null && clazz != Object.class) {
            for (java.lang.reflect.Method m : safeGetDeclaredMethods(clazz)) {
                if ("attach".equals(m.getName())
                        && m.getParameterCount() >= 6) {
                    return m;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    /**
     * Build the argument array for the attach() method, matching parameter types
     * as closely as possible. Unknown parameters get null/0 defaults.
     */
    private Object[] buildAttachArgs(java.lang.reflect.Method method,
                                      Context baseContext, Application app,
                                      Intent intent, ComponentName component) {
        Class<?>[] paramTypes = method.getParameterTypes();
        Object[] args = new Object[paramTypes.length];

        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> type = paramTypes[i];
            if (type == Context.class || type.isAssignableFrom(Context.class)) {
                args[i] = baseContext;
            } else if (type == Application.class) {
                args[i] = app;
            } else if (type == Intent.class) {
                args[i] = intent;
            } else if (type == Instrumentation.class || type.getSimpleName().contains("Instrumentation")) {
                args[i] = mInstrumentation;
            } else if (type == IBinder.class) {
                args[i] = new Binder("activity-token");
            } else if (type == int.class) {
                args[i] = 0;
            } else if (type == String.class) {
                // Could be title, referrer, embeddedID -- provide component class name
                if (component != null) {
                    args[i] = component.getClassName();
                }
            } else if (type == Activity.class) {
                args[i] = null; // parent activity
            } else if (type == CharSequence.class) {
                args[i] = component != null ? component.getShortClassName() : "";
            } else {
                args[i] = null; // ActivityInfo, Configuration, Window, etc.
            }
        }

        return args;
    }

    // ── Activity lifecycle: resume ─────────────────────────────────────────

    /**
     * Resume an activity. Calls onStart() then onResume() if needed.
     *
     * @param activity The activity to resume.
     */
    public void performResumeActivity(Activity activity) {
        ActivityClientRecord r = findRecord(activity);
        if (r == null) {
            log("W", "performResumeActivity: no record for " + activity);
            return;
        }

        log("I", "performResumeActivity: " + r.className);

        // Pause the current resumed activity first
        if (mResumedRecord != null && mResumedRecord != r) {
            performPauseActivity(mResumedRecord.activity);
        }

        try {
            // Call onStart if not yet started
            if (r.lifecycleState < ActivityClientRecord.STARTED) {
                mInstrumentation.callActivityOnStart(activity);
                r.lifecycleState = ActivityClientRecord.STARTED;
            }

            // Call onResume
            mInstrumentation.callActivityOnResume(activity);
            r.lifecycleState = ActivityClientRecord.RESUMED;
            mResumedRecord = r;

            log("I", "  Resumed: " + r.className);
        } catch (Exception e) {
            if (!mInstrumentation.onException(activity, e)) {
                log("E", "  performResumeActivity failed: " + e);
                throw new RuntimeException("Resume failed for " + r.className, e);
            }
        }
    }

    // ── Activity lifecycle: pause ──────────────────────────────────────────

    /**
     * Pause an activity. Calls onPause().
     *
     * @param activity The activity to pause.
     */
    public void performPauseActivity(Activity activity) {
        ActivityClientRecord r = findRecord(activity);
        if (r == null) {
            log("W", "performPauseActivity: no record for " + activity);
            return;
        }
        if (r.lifecycleState != ActivityClientRecord.RESUMED) {
            return; // not currently resumed
        }

        log("I", "performPauseActivity: " + r.className);

        try {
            // Save instance state before pausing
            Bundle outState = new Bundle();
            mInstrumentation.callActivityOnSaveInstanceState(activity, outState);
            r.savedState = outState;

            mInstrumentation.callActivityOnPause(activity);
            r.lifecycleState = ActivityClientRecord.PAUSED;

            if (mResumedRecord == r) {
                mResumedRecord = null;
            }
        } catch (Exception e) {
            if (!mInstrumentation.onException(activity, e)) {
                log("E", "  performPauseActivity failed: " + e);
            }
        }
    }

    // ── Activity lifecycle: stop ───────────────────────────────────────────

    /**
     * Stop an activity. Calls onStop().
     *
     * @param activity The activity to stop.
     */
    public void performStopActivity(Activity activity) {
        ActivityClientRecord r = findRecord(activity);
        if (r == null) return;
        if (r.lifecycleState == ActivityClientRecord.STOPPED
                || r.lifecycleState == ActivityClientRecord.DESTROYED) {
            return;
        }

        log("I", "performStopActivity: " + r.className);

        try {
            // Pause first if still resumed
            if (r.lifecycleState == ActivityClientRecord.RESUMED) {
                performPauseActivity(activity);
            }

            mInstrumentation.callActivityOnStop(activity);
            r.lifecycleState = ActivityClientRecord.STOPPED;
        } catch (Exception e) {
            if (!mInstrumentation.onException(activity, e)) {
                log("E", "  performStopActivity failed: " + e);
            }
        }
    }

    // ── Activity lifecycle: destroy ────────────────────────────────────────

    /**
     * Destroy an activity. Calls onStop() (if needed) then onDestroy().
     *
     * @param activity The activity to destroy.
     */
    public void performDestroyActivity(Activity activity) {
        ActivityClientRecord r = findRecord(activity);
        if (r == null) return;
        if (r.lifecycleState == ActivityClientRecord.DESTROYED) return;

        log("I", "performDestroyActivity: " + r.className);

        try {
            // Stop first if needed
            if (r.lifecycleState == ActivityClientRecord.RESUMED
                    || r.lifecycleState == ActivityClientRecord.STARTED
                    || r.lifecycleState == ActivityClientRecord.PAUSED) {
                performStopActivity(activity);
            }

            mInstrumentation.callActivityOnDestroy(activity);
            r.lifecycleState = ActivityClientRecord.DESTROYED;
            r.finished = true;

            synchronized (mActivities) {
                mActivities.remove(r.token);
            }

            log("I", "  Destroyed: " + r.className);
        } catch (Exception e) {
            if (!mInstrumentation.onException(activity, e)) {
                log("E", "  performDestroyActivity failed: " + e);
            }
        }
    }

    // ── Full launch + resume convenience ───────────────────────────────────

    /**
     * Launch an activity and immediately drive it to the RESUMED state.
     * This is what most callers want: create -> start -> resume in one call.
     */
    public Activity launchAndResumeActivity(String className, String packageName,
                                             Intent intent, Bundle savedState) {
        Activity activity = performLaunchActivity(className, packageName, intent, savedState);
        if (activity != null) {
            performResumeActivity(activity);
        }
        return activity;
    }

    // ── Start activity from another activity ───────────────────────────────

    /**
     * Handle startActivity/startActivityForResult from within an activity.
     * This replaces the MiniActivityManager.startActivity() path.
     */
    public void startActivityFromActivity(Activity caller, Intent intent,
                                           int requestCode) {
        if (intent == null) {
            log("W", "startActivityFromActivity: null intent");
            return;
        }

        ComponentName component = intent.getComponent();
        if (component == null) {
            // Try implicit resolution via MiniServer's PackageManager
            try {
                MiniServer server = MiniServer.get();
                if (server != null) {
                    android.content.pm.ResolveInfo ri =
                            server.getPackageManager().resolveActivity(intent);
                    if (ri != null && ri.resolvedComponentName != null) {
                        component = ri.resolvedComponentName;
                        intent.setComponent(component);
                    }
                }
            } catch (Exception e) {
                // ignore
            }
            if (component == null) {
                log("W", "startActivityFromActivity: cannot resolve " + intent);
                return;
            }
        }

        String className = component.getClassName();
        String pkgName = component.getPackageName();
        if (pkgName == null || pkgName.isEmpty()) {
            pkgName = mPackageName;
        }

        // Pause the calling activity
        if (caller != null) {
            performPauseActivity(caller);
        }

        // Track the caller for onActivityResult
        ActivityClientRecord callerRecord = caller != null ? findRecord(caller) : null;

        // Launch the new activity
        Activity newActivity = performLaunchActivity(className, pkgName, intent, null);
        if (newActivity != null) {
            // Link caller for result delivery
            ActivityClientRecord newRecord = findRecord(newActivity);
            if (newRecord != null && callerRecord != null && requestCode >= 0) {
                newRecord.caller = callerRecord;
                newRecord.requestCode = requestCode;
            }

            performResumeActivity(newActivity);
        }
    }

    // ── Finish activity ────────────────────────────────────────────────────

    /**
     * Finish an activity and deliver result to caller if applicable.
     */
    public void finishActivity(Activity activity) {
        ActivityClientRecord r = findRecord(activity);
        if (r == null) return;

        log("I", "finishActivity: " + r.className);

        // Save result for delivery
        int resultCode = Activity.RESULT_CANCELED;
        Intent resultData = null;
        try {
            resultCode = ShimCompat.getActivityIntField(activity, "mResultCode",
                    Activity.RESULT_CANCELED);
            resultData = ShimCompat.getActivityField(activity, "mResultData",
                    (Intent) null);
        } catch (Exception ignored) {}

        ActivityClientRecord callerRecord = r.caller;
        int requestCode = r.requestCode;

        // Destroy the finishing activity
        performDestroyActivity(activity);

        // Deliver result to caller
        if (callerRecord != null && requestCode >= 0
                && callerRecord.activity != null
                && callerRecord.lifecycleState != ActivityClientRecord.DESTROYED) {
            try {
                callerRecord.activity.onActivityResult(requestCode, resultCode, resultData);
            } catch (Exception e) {
                log("W", "onActivityResult delivery failed: " + e);
            }

            // Resume the caller
            performResumeActivity(callerRecord.activity);
        }
    }

    // ── Initialization (attach) ────────────────────────────────────────────

    /**
     * Initialize the ActivityThread. Creates the Instrumentation and Application.
     * Mirrors AOSP's ActivityThread.attach().
     *
     * @param packageName The app's package name.
     * @param appClassName The Application subclass name, or null.
     * @param classLoader  The ClassLoader for the app's classes.
     */
    public void attach(String packageName, String appClassName, ClassLoader classLoader) {
        sCurrentActivityThread = this;
        mPackageName = packageName;
        mClassLoader = classLoader;
        mLooper = Looper.getMainLooper();
        try {
            if (classLoader != null) {
                Thread.currentThread().setContextClassLoader(classLoader);
                WestlakeLauncher.patchProblematicAppClasses(classLoader);
            }
        } catch (Throwable t) {
            log("W", "attach patchProblematicAppClasses failed: " + throwableSummary(t));
        }

        // Create Instrumentation
        mInstrumentation = new WestlakeInstrumentation(this);

        // Try to discover AppComponentFactory from the app's manifest/metadata
        mAppComponentFactory = discoverAppComponentFactory(classLoader);

        // Reuse existing Application if available (avoid re-running blocking onCreate)
        try {
            Application existing = MiniServer.currentApplication();
            if (existing != null) {
                String existingPkg = null;
                try {
                    existingPkg = existing.getPackageName();
                } catch (Throwable ignored) {
                }
                if (isPlaceholderPackage(existingPkg)) {
                    existingPkg = MiniServer.currentPackageName();
                }
                if (isPlaceholderPackage(existingPkg)) {
                    try {
                        existingPkg = System.getProperty("westlake.apk.package");
                    } catch (Throwable ignored) {
                    }
                }
                if (isPlaceholderPackage(existingPkg) && !isPlaceholderPackage(packageName)) {
                    existingPkg = packageName;
                    ShimCompat.setPackageName(existing, packageName);
                    MiniServer.currentSetPackageName(packageName);
                }
                if (packageName != null
                        && !packageName.isEmpty()
                        && existingPkg != null
                        && !existingPkg.isEmpty()
                        && !packageName.equals(existingPkg)) {
                    log("W", "Ignoring MiniServer Application package mismatch: existing="
                            + existingPkg + " expected=" + packageName);
                } else {
                    mInitialApplication = existing;
                    log("I", "Reusing existing Application from MiniServer (skip makeApplication)");
                }
            }
        } catch (Exception ignored) {}
        if (mInitialApplication == null) {
            PackageInfo pkgInfo = new PackageInfo(packageName, getClassLoader(),
                    mAppComponentFactory);
            mInitialApplication = pkgInfo.makeApplication(false, appClassName, mInstrumentation);
        }

        log("I", "Attached: pkg=" + packageName
                + " app=" + (mInitialApplication != null
                        ? mInitialApplication.getClass().getName() : "null")
                + " factory=" + (mAppComponentFactory != null
                        ? mAppComponentFactory.getClass().getName() : "default"));

        // Initialize DataSourceHelper static fields from the singleton component
        // (Application.onCreate may have failed, leaving DataSourceHelper uninitialized)
        Object singleton = dagger.hilt.android.internal.managers.ApplicationComponentManager.singletonComponent;
        if (singleton != null) {
            try {
                ClassLoader appCl = mInitialApplication != null ? mInitialApplication.getClass().getClassLoader() : classLoader;
                // Find DataSourceHelper and set its static fields from the singleton
                Class<?> helperClass = appCl.loadClass("com.mcdonalds.mcdcoreapp.common.model.DataSourceHelper");
                for (java.lang.reflect.Field f : safeGetDeclaredFields(helperClass)) {
                    if (!java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
                    f.setAccessible(true);
                    if (f.get(null) != null) continue; // already set
                    Class<?> fType = f.getType();
                    if (fType == boolean.class) { f.set(null, true); continue; } // set 'initialized' to true
                    if (fType.isInterface() && fType.isInstance(singleton)) {
                        f.set(null, singleton);
                        log("I", "DataSourceHelper." + f.getName() + " = singleton (implements " + fType.getSimpleName() + ")");
                    } else if (!fType.isPrimitive() && !fType.isInterface()) {
                        // Non-interface, non-primitive: try to create via no-arg constructor
                        try {
                            Object inst = fType.getDeclaredConstructor().newInstance();
                            f.set(null, inst);
                            log("I", "DataSourceHelper." + f.getName() + " = new " + fType.getSimpleName() + "()");
                        } catch (Throwable t3) { /* no default constructor */ }
                    } else if (fType.isInterface() || !fType.isPrimitive()) {
                        // Try ALL methods on the singleton that return the right type
                        boolean found = false;
                        for (java.lang.reflect.Method m : safeGetMethods(singleton.getClass())) {
                            try {
                                if (m.getParameterTypes().length == 0 && fType.isAssignableFrom(m.getReturnType())) {
                                    Object val = m.invoke(singleton);
                                    if (val != null) {
                                        f.set(null, val);
                                        log("I", "DataSourceHelper." + f.getName() + " = singleton." + m.getName() + "()");
                                        found = true;
                                        break;
                                    }
                                }
                            } catch (Throwable t2) { /* skip this method */ }
                        }
                    }
                }
                log("I", "DataSourceHelper initialized from singleton");
                // Initialize ApplicationContext static field
                try {
                    Class<?> appCtx = appCl.loadClass("com.mcdonalds.mcdcoreapp.common.ApplicationContext");
                    java.lang.reflect.Field ctxField = appCtx.getDeclaredField("a");
                    ctxField.setAccessible(true);
                    if (ctxField.get(null) == null) {
                        ctxField.set(null, mInitialApplication);
                        log("I", "Set ApplicationContext.a = " + mInitialApplication.getClass().getSimpleName());
                    }
                } catch (Throwable t4) { log("W", "ApplicationContext init: " + t4.getMessage()); }
                // Also initialize ClickstreamDataHelper
                try {
                    initStaticHelperFromSingleton(appCl, singleton,
                        "com.mcdonalds.mcdcoreapp.analytics.ClickstreamDataHelper");
                    log("I", "ClickstreamDataHelper initialized from singleton");
                } catch (Throwable t3) { log("W", "ClickstreamDataHelper init: " + t3.getMessage()); }
                // Fix Crypto on objects returned by the singleton
                // Crypto needs Context — create one with the Application context
                try {
                    Class<?> cryptoClass = appCl.loadClass("com.mcdonalds.mcdcoreapp.common.Crypto");
                    Object crypto = cryptoClass.getConstructor(android.content.Context.class)
                            .newInstance(mInitialApplication);
                    log("I", "Created Crypto instance: " + crypto);
                    // Find and populate Crypto fields on ALL DataSourceHelper-cached objects
                    Class<?> dsh = appCl.loadClass("com.mcdonalds.mcdcoreapp.common.model.DataSourceHelper");
                    for (java.lang.reflect.Field df : safeGetDeclaredFields(dsh)) {
                        if (!java.lang.reflect.Modifier.isStatic(df.getModifiers())) continue;
                        df.setAccessible(true);
                        Object obj = df.get(null);
                        if (obj == null) continue;
                        // Search this object's fields for Crypto-typed fields
                        for (java.lang.reflect.Field of : safeGetDeclaredFields(obj.getClass())) {
                            if (of.getType() == cryptoClass || of.getType().getName().contains("Crypto")) {
                                of.setAccessible(true);
                                if (of.get(obj) == null) {
                                    of.set(obj, crypto);
                                    log("I", "Set Crypto on " + obj.getClass().getSimpleName() + "." + of.getName());
                                }
                            }
                        }
                    }
                } catch (Throwable ct) {
                    log("W", "Crypto init: " + ct.getMessage());
                }
            } catch (Throwable t) {
                log("E", "DataSourceHelper init: " + t);
            }
        }
    }

    /**
     * Try to discover a custom AppComponentFactory from the app's classes.
     * Hilt apps declare one via the manifest's <application> tag's
     * android:appComponentFactory attribute. We check for common Hilt factory classes.
     */
    private AppComponentFactory discoverAppComponentFactory(ClassLoader cl) {
        if (cl == null) return new AppComponentFactory();

        // Common Hilt AppComponentFactory class names
        String[] candidates = {
            // Hilt's generated factory (most common)
            "dagger.hilt.android.internal.lifecycle.HiltViewModelFactory",
            // Some apps override AppComponentFactory directly
        };

        // Try the Hilt-standard pattern: check for @CustomTestApplication or
        // standard Hilt component factory
        try {
            // Hilt uses its own AppComponentFactory subclass if present
            Class<?> hiltFactory = cl.loadClass(
                    "dagger.hilt.android.internal.managers.HiltAppComponentFactory");
            // Attempt to see if it exists but is not necessarily an AppComponentFactory
            // The real Hilt factory needs the SingletonComponent -- skip for now
        } catch (ClassNotFoundException ignored) {
            // Not a Hilt app, or Hilt not using custom factory
        }

        return new AppComponentFactory();
    }

    // ── Static main() ──────────────────────────────────────────────────────

    /**
     * Main entry point. Mirrors AOSP's ActivityThread.main():
     *   1. Prepare the main Looper
     *   2. Create the ActivityThread singleton
     *   3. Attach (create Instrumentation + Application)
     *   4. Launch the main activity
     *
     * Args:
     *   [0] = APK path or package name
     *   --activity <class> = override the launcher activity
     *   --app-class <class> = override the Application class
     *   --factory <class> = override the AppComponentFactory class
     */
    public static void main(String[] args) {
        log("I", "--- WestlakeActivityThread.main() ---");

        // Step 1: Prepare main looper
        Looper.prepareMainLooper();

        // Step 2: Create singleton
        WestlakeActivityThread thread = new WestlakeActivityThread();
        sCurrentActivityThread = thread;

        // Parse arguments
        String firstArg = null;
        String overrideActivity = null;
        String overrideAppClass = null;
        String overrideFactory = null;

        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                if ("--activity".equals(args[i]) && i + 1 < args.length) {
                    overrideActivity = args[++i];
                } else if ("--app-class".equals(args[i]) && i + 1 < args.length) {
                    overrideAppClass = args[++i];
                } else if ("--factory".equals(args[i]) && i + 1 < args.length) {
                    overrideFactory = args[++i];
                } else if (!args[i].startsWith("-") && firstArg == null) {
                    firstArg = args[i];
                }
            }
        }

        // Step 3: Determine package name and classloader
        String packageName = "app";
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) cl = ClassLoader.getSystemClassLoader();

        // Handle custom AppComponentFactory
        if (overrideFactory != null) {
            try {
                Class<?> factoryClass = cl.loadClass(overrideFactory);
                thread.mAppComponentFactory = (AppComponentFactory)
                        factoryClass.getDeclaredConstructor().newInstance();
                log("I", "Using custom AppComponentFactory: " + overrideFactory);
            } catch (Exception e) {
                log("W", "Failed to load AppComponentFactory " + overrideFactory + ": " + e);
            }
        }

        // APK mode: delegate to MiniServer for manifest parsing, then use our launch flow
        if (firstArg != null && firstArg.endsWith(".apk")) {
            log("I", "APK mode: " + firstArg);
            try {
                MiniServer.init("app");
                MiniServer server = MiniServer.get();
                ApkInfo info = server.loadApk(firstArg);
                packageName = info.packageName;

                // Attach with the APK's info
                thread.attach(packageName, info.applicationClassName != null
                        ? info.applicationClassName : overrideAppClass, cl);

                // Determine launcher activity
                String launcher = overrideActivity;
                if (launcher == null) launcher = info.launcherActivity;
                if (launcher == null && !info.activities.isEmpty()) {
                    launcher = info.activities.get(0);
                }
                if (launcher == null) {
                    log("E", "No launcher activity found in APK");
                    return;
                }
                if (launcher.startsWith(".")) {
                    launcher = packageName + launcher;
                }

                // Launch!
                ComponentName comp = new ComponentName(packageName, launcher);
                Intent launchIntent = Intent.makeMainActivity(comp);
                thread.launchAndResumeActivity(launcher, packageName, launchIntent, null);

            } catch (Exception e) {
                log("E", "APK launch failed: " + e);
            }
            return;
        }

        // Package name mode
        if (firstArg != null && firstArg.contains(".")) {
            packageName = firstArg;
        }

        // Step 4: Attach
        thread.attach(packageName, overrideAppClass, cl);

        // Step 5: Launch activity if specified
        if (overrideActivity != null) {
            String actName = overrideActivity;
            if (actName.startsWith(".")) {
                actName = packageName + actName;
            }
            ComponentName comp = new ComponentName(packageName, actName);
            Intent launchIntent = Intent.makeMainActivity(comp);
            thread.launchAndResumeActivity(actName, packageName, launchIntent, null);
        } else {
            log("I", "No activity specified. Use --activity <class> to launch one.");
        }

        // In AOSP, Looper.loop() blocks here. In Westlake, messages are pumped externally.
        // Looper.loop() is a no-op in our shim.
        log("I", "--- WestlakeActivityThread ready ---");
    }

    /** Initialize any static helper class by populating its null fields from the singleton */
    private void initStaticHelperFromSingleton(ClassLoader cl, Object singleton, String className) throws Exception {
        Class<?> helperClass = cl.loadClass(className);
        for (java.lang.reflect.Field f : safeGetDeclaredFields(helperClass)) {
            if (!java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
            f.setAccessible(true);
            if (f.get(null) != null) continue;
            Class<?> fType = f.getType();
            if (fType == boolean.class) { f.set(null, true); continue; }
            if (fType == String.class) { f.set(null, ""); continue; }
            if (fType.isPrimitive()) continue;
            // Try singleton methods that return this type
            if (fType.isInstance(singleton)) { f.set(null, singleton); continue; }
            for (java.lang.reflect.Method m : safeGetMethods(singleton.getClass())) {
                try {
                    if (m.getParameterTypes().length == 0 && fType.isAssignableFrom(m.getReturnType())) {
                        Object val = m.invoke(singleton);
                        if (val != null) { f.set(null, val); break; }
                    }
                } catch (Throwable t) { /* skip */ }
            }
            // Try no-arg constructor for concrete types
            if (f.get(null) == null && !fType.isInterface()) {
                try {
                    f.set(null, fType.getDeclaredConstructor().newInstance());
                } catch (Throwable t) { /* skip */ }
            }
        }
    }

    // ── Logging ────────────────────────────────────────────────────────────

    private static void log(String level, String msg) {
        try {
            WestlakeLauncher.trace("[" + TAG + "/" + level + "] " + msg);
        } catch (Throwable ignored) {
        }
    }

    private static java.lang.reflect.Field[] safeGetDeclaredFields(Class<?> cls) {
        if (cls == null) {
            return new java.lang.reflect.Field[0];
        }
        try {
            java.lang.reflect.Field[] fields = cls.getDeclaredFields();
            if (fields != null) {
                return fields;
            }
            log("W", "getDeclaredFields returned null for " + cls.getName());
        } catch (Throwable t) {
            log("W", "getDeclaredFields failed for " + cls.getName() + ": " + throwableTag(t));
        }
        return new java.lang.reflect.Field[0];
    }

    private static java.lang.reflect.Method[] safeGetDeclaredMethods(Class<?> cls) {
        if (cls == null) {
            return new java.lang.reflect.Method[0];
        }
        try {
            java.lang.reflect.Method[] methods = cls.getDeclaredMethods();
            if (methods != null) {
                return methods;
            }
            log("W", "getDeclaredMethods returned null for " + cls.getName());
        } catch (Throwable t) {
            log("W", "getDeclaredMethods failed for " + cls.getName() + ": " + throwableTag(t));
        }
        return new java.lang.reflect.Method[0];
    }

    private static java.lang.reflect.Method[] safeGetMethods(Class<?> cls) {
        if (cls == null) {
            return new java.lang.reflect.Method[0];
        }
        try {
            java.lang.reflect.Method[] methods = cls.getMethods();
            if (methods != null) {
                return methods;
            }
            log("W", "getMethods returned null for " + cls.getName());
        } catch (Throwable t) {
            log("W", "getMethods failed for " + cls.getName() + ": " + throwableTag(t));
        }
        return new java.lang.reflect.Method[0];
    }

    private static String throwableTag(Throwable t) {
        if (t == null) {
            return "null";
        }
        String message = t.getMessage();
        if (message == null || message.isEmpty()) {
            return t.getClass().getName();
        }
        return t.getClass().getName() + ": " + message;
    }
}
