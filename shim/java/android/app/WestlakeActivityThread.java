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

    // M7-Step2 (2026-05-13): production-launcher opt-in to full lifecycle in
    // strict mode. When `true`, performLaunchActivityImpl's strict-standalone
    // branch will execute the same Step 7/8 path (record + callActivityOnCreate)
    // that `forceLifecycleInStrict` (the cutoff-canary / McD per-app gate)
    // executes today.
    //
    // This is NOT a per-app branch: any caller can opt in by invoking
    // `setForceLifecycleEnabled(true)` before driving a launch. It is the
    // production launcher's equivalent of "we are wiring the proper Android
    // launch path, not running the discovery probe" — the same flag is read
    // for every package and every activity once flipped. M7-Step1's
    // NoiceLauncher does not call this method (delegates to the discovery
    // harness reflection path); M7-Step2's NoiceProductionLauncher does.
    private static volatile boolean sForceLifecycleEnabled = false;

    /**
     * M7-Step2: enable the full strict-standalone lifecycle path
     * (record + callActivityOnCreate, then optional Resume) for subsequent
     * performLaunchActivity calls. Production launchers call this with
     * {@code true} before driving an Activity launch through
     * {@link #performLaunchActivity(String, String, Intent, Bundle)}.
     *
     * Discovery harnesses leave this {@code false}: they bypass
     * performLaunchActivity and call onCreate via reflection directly, so
     * the strict-standalone short-circuit is the desired behavior for them.
     *
     * Generic across all apps — no per-app branches.
     */
    public static void setForceLifecycleEnabled(boolean enabled) {
        sForceLifecycleEnabled = enabled;
    }

    /** Read the current force-lifecycle flag. Generic across all apps. */
    public static boolean isForceLifecycleEnabled() {
        return sForceLifecycleEnabled;
    }

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

    // ── CR23-fix: WestlakeContextImpl-based base context construction ──
    //
    // When framework.jar is on -Xbootclasspath (post-PF-arch-053 BCP shim
    // deployment), android.content.Context is the framework's abstract class.
    // `new Context()` throws InstantiationError at runtime — this used to be
    // the McDonald's launch failure mode where the VM spawned but mainImpl
    // crashed immediately after "Using WestlakeActivityThread" with
    // `java.lang.InstantiationError: android.content.Context`.
    //
    // The architectural Context type for the dalvikvm sandbox is
    // WestlakeContextImpl (frozen-surface class, overrides every Context
    // abstract method).  This helper builds one — generic across all APKs,
    // packageName is constructor arg, no per-app branches.
    //
    // Fallback to `new Context()` is kept only for the legacy shim-only-BCP
    // deployment path where Context is concrete.

    /**
     * Build a base Context for the given package.  Prefers
     * {@link com.westlake.services.WestlakeContextImpl}; falls back to the
     * shim's concrete Context when WestlakeContextImpl construction fails
     * (shim-only-BCP path).  May return {@code null} if both paths fail —
     * callers must null-check.
     */
    static Context buildBaseContext(String packageName, ClassLoader cl) {
        String apkPath = null;
        try {
            apkPath = WestlakeLauncher.currentApkPathForShim();
        } catch (Throwable ignored) {
        }
        String dataDir = null;
        if (packageName != null && packageName.length() > 0) {
            dataDir = "/data/local/tmp/westlake/" + packageName;
        }
        String pkgArg = (packageName != null && packageName.length() > 0)
                ? packageName : "com.westlake.unknown";
        try {
            return new com.westlake.services.WestlakeContextImpl(
                    pkgArg, apkPath, dataDir, cl, 33);
        } catch (Throwable t) {
            WestlakeLauncher.marker("CV WAT WestlakeContextImpl ctor failed "
                    + throwableSummary(t));
            Log.w(TAG, "WestlakeContextImpl construction failed: "
                    + throwableSummary(t));
        }
        try {
            return new Context();
        } catch (Throwable t) {
            WestlakeLauncher.marker("CV WAT base context fallback failed "
                    + throwableSummary(t));
            Log.w(TAG, "buildBaseContext: WestlakeContextImpl + new Context() both failed: "
                    + throwableSummary(t));
            return null;
        }
    }

    /**
     * Publish the Application back into the base Context via
     * {@code setAttachedApplication(...)} when the base is a
     * WestlakeContextImpl.  No-op when the base context is not a
     * WestlakeContextImpl — keeps the helper safe for the shim-only-BCP
     * fallback path.
     */
    static void publishApplicationToBaseContext(Context baseContext, Application app) {
        if (baseContext == null || app == null) {
            return;
        }
        try {
            java.lang.reflect.Method method = baseContext.getClass().getMethod(
                    "setAttachedApplication", Application.class);
            method.setAccessible(true);
            method.invoke(baseContext, app);
            WestlakeLauncher.marker("CV WAT base context setAttachedApplication ok");
        } catch (NoSuchMethodException nsme) {
            // Not a WestlakeContextImpl — nothing to do.
        } catch (Throwable t) {
            WestlakeLauncher.marker("CV WAT base context setAttachedApplication failed "
                    + throwableSummary(t));
        }
    }

    // ── Core state ─────────────────────────────────────────────────────────

    WestlakeInstrumentation mInstrumentation;
    Application mInitialApplication;
    Looper mLooper;
    private String mPackageName;
    private ClassLoader mClassLoader;
    private AppComponentFactory mAppComponentFactory;
    private boolean mForceMakeApplicationForNextLaunch;
    private String mForcedApplicationClassName;

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
                WestlakeLauncher.marker("CV WAT makeApplication begin " + cls);
                WestlakeLauncher.marker("CV WAT instantiateApplication begin " + cls);
                app = instantiateApplicationWithFactory(mFactory, classLoader, cls);
                if (app != null) {
                    WestlakeLauncher.marker("CV WAT instantiateApplication returned "
                            + app.getClass().getName());
                } else {
                    WestlakeLauncher.marker("CV WAT instantiateApplication null");
                }
            } catch (Exception e) {
                WestlakeLauncher.marker("CV WAT instantiateApplication failed "
                        + throwableSummary(e));
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
            // CR23-fix (2026-05-13): use WestlakeContextImpl, NOT `new Context()`.
            // When framework.jar is on -Xbootclasspath, `android.content.Context`
            // resolves to the framework's abstract class — instantiating it via
            // `new Context()` throws java.lang.InstantiationError and breaks every
            // launch (McD dashboard never attached pre-CR23-fix).  The architectural
            // Context type for the Westlake dalvikvm sandbox is WestlakeContextImpl
            // (extends Context, overrides every abstract method).  See
            // shim/java/com/westlake/services/WestlakeContextImpl.java FROZEN-SURFACE
            // class header for the contract.  Generic across all APKs — packageName
            // is constructor arg, no per-app branches.
            Context baseContext = createApplicationBaseContext(packageName, classLoader);
            attachApplicationBaseContext(app, baseContext);
            attachApplicationToBaseContext(baseContext, app);

            mApplication = app;
            publishApplicationBeforeOnCreate(app, packageName);
            wireApplicationResourcesBeforeOnCreate(app, packageName);

            if (isMcdonaldsPackageOrClass(packageName)
                    || isMcdonaldsPackageOrClass(cls)
                    || isMcdonaldsPackageOrClass(app.getClass().getName())) {
                seedMcdonaldsApplicationContext(classLoader, app);
                seedMcdonaldsSdkPersistenceState(classLoader);
            }

            // Invoke Application.onCreate via Instrumentation
            if (instrumentation != null) {
                try {
                    WestlakeLauncher.bootstrapIcuDataPath();
                    WestlakeLauncher.marker("CV WAT application onCreate begin");
                    instrumentation.callApplicationOnCreate(app);
                    WestlakeLauncher.marker("CV WAT application onCreate returned");
                } catch (Exception e) {
                    WestlakeLauncher.marker("CV WAT application onCreate failed "
                            + throwableSummary(e));
                    WestlakeLauncher.dumpThrowable(
                            "[WestlakeActivityThread] Application.onCreate failed", e);
                    WestlakeLauncher.dumpThrowableFrames(
                            "CV WAT application onCreate throwable", e, 24);
                    log("W", "Application.onCreate() threw: " + throwableSummary(e));
                }
            }

            return app;
        }

        private void wireApplicationResourcesBeforeOnCreate(Application app, String packageName) {
            if (app == null) {
                return;
            }
            String resDir = null;
            String apkPath = null;
            try {
                resDir = WestlakeLauncher.currentResDirForShim();
                apkPath = WestlakeLauncher.currentApkPathForShim();
            } catch (Throwable ignored) {
            }
            if (resDir == null || resDir.length() == 0) {
                try {
                    resDir = System.getProperty("westlake.apk.resdir", "");
                } catch (Throwable ignored) {
                }
            }
            if (resDir == null || resDir.length() == 0) {
                return;
            }
            try {
                ApkInfo earlyInfo = ApkLoader.loadFromExtracted(resDir, packageName, null);
                try {
                    MiniServer server = MiniServer.get();
                    if (server != null) {
                        java.lang.reflect.Field f = MiniServer.class.getDeclaredField("mApkInfo");
                        f.setAccessible(true);
                        f.set(server, earlyInfo);
                    }
                } catch (Throwable ignored) {
                }

                android.content.res.Resources res = app.getResources();
                if (res != null && earlyInfo.resourceTable instanceof android.content.res.ResourceTable) {
                    ShimCompat.loadResourceTable(
                            res, (android.content.res.ResourceTable) earlyInfo.resourceTable);
                }
                if (res != null && apkPath != null && apkPath.length() > 0) {
                    ShimCompat.setApkPath(res, apkPath);
                }
                android.content.res.AssetManager assets = app.getAssets();
                if (assets != null) {
                    if (apkPath != null && apkPath.length() > 0) {
                        ShimCompat.setAssetApkPath(assets, apkPath);
                    }
                    if (earlyInfo.assetDir != null) {
                        ShimCompat.setAssetDir(assets, earlyInfo.assetDir);
                    }
                }
                int strings = earlyInfo.resourceTable instanceof android.content.res.ResourceTable
                        ? ((android.content.res.ResourceTable) earlyInfo.resourceTable).getStringCount()
                        : 0;
                WestlakeLauncher.marker("CV WAT application early resources wired strings="
                        + strings + " resDir=" + resDir);
            } catch (Throwable t) {
                WestlakeLauncher.marker("CV WAT application early resources failed "
                        + throwableSummary(t));
            }
        }

        private void attachApplicationBaseContext(Application app, Context baseContext) {
            if (app == null || baseContext == null) {
                return;
            }
            // CR59 (2026-05-14): use the shim Application's package-private
            // attach(Context) helper instead of invoking the framework's
            // protected ContextWrapper.attachBaseContext directly across
            // package boundaries.  ContextWrapper.attachBaseContext is
            // protected (Java access checking can reject cross-package
            // invoke-virtual on a protected non-subclass method), and the
            // pre-CR59 reflective fallback used getDeclaredMethod() which
            // does NOT find inherited methods — so when the direct call
            // silently failed, the fallback also silently failed and
            // app.mBase stayed null.  The result was that Hilt's first
            // lazy delegate hit a NullPointerException inside
            // ContextWrapper.getApplicationContext(): the very NPE M7-Step2
            // documented as the new in-body blocker.
            //
            // Application.attach(Context) is package-private on OUR shim
            // Application (lives in android.app; matches AOSP-default body
            // verbatim — `attachBaseContext(base)` from a subclass of
            // ContextWrapper, which is the legal Java route).
            // No new methods, no Unsafe, no setAccessible, no per-app
            // branches.
            try {
                app.attach(baseContext);
                WestlakeLauncher.marker("CV WAT application attach returned");
            } catch (Throwable t) {
                WestlakeLauncher.marker("CV WAT application attach failed "
                        + throwableSummary(t));
                log("W", "Application.attach failed: " + throwableSummary(t));
            }
        }

        private Context createApplicationBaseContext(String packageName, ClassLoader cl) {
            return WestlakeActivityThread.buildBaseContext(packageName, cl);
        }

        private void attachApplicationToBaseContext(Context baseContext, Application app) {
            WestlakeActivityThread.publishApplicationToBaseContext(baseContext, app);
        }

        private void publishApplicationBeforeOnCreate(Application app, String packageName) {
            if (app == null) {
                return;
            }
            try {
                MiniServer.currentSetPackageName(packageName);
            } catch (Throwable ignored) {
            }
            try {
                MiniServer.currentSetApplication(app);
                WestlakeLauncher.marker("CV WAT application pre-onCreate published");
            } catch (Throwable t) {
                WestlakeLauncher.marker("CV WAT application pre-onCreate publish failed "
                        + throwableSummary(t));
                log("W", "Application pre-onCreate publish failed: " + throwableSummary(t));
            }
        }

        private Application instantiateApplicationWithFactory(AppComponentFactory factory,
                ClassLoader cl, String className) throws Exception {
            if (factory != null && factory.getClass() != AppComponentFactory.class) {
                try {
                    java.lang.reflect.Method method = factory.getClass().getMethod(
                            "instantiateApplication", ClassLoader.class, String.class);
                    method.setAccessible(true);
                    WestlakeLauncher.marker("CV WAT instantiateApplication reflect begin "
                            + factory.getClass().getName());
                    Object result = method.invoke(factory, cl, className);
                    WestlakeLauncher.marker("CV WAT instantiateApplication reflect returned");
                    return Application.class.cast(result);
                } catch (java.lang.reflect.InvocationTargetException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof Exception) {
                        throw (Exception) cause;
                    }
                    if (cause instanceof Error) {
                        throw (Error) cause;
                    }
                    throw e;
                }
            }
            return factory.instantiateApplication(cl, className);
        }
    }

    // ── Constructor ────────────────────────────────────────────────────────

    public WestlakeActivityThread() {
        mLooper = Looper.getMainLooper();
        mInstrumentation = new WestlakeInstrumentation(this);
    }

    // ── Getters ────────────────────────────────────────────────────────────

    public Application getApplication() {
        return mInitialApplication;
    }

    public WestlakeInstrumentation getInstrumentation() {
        ensureLaunchHelpersReady();
        return mInstrumentation;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public ClassLoader getClassLoader() {
        final boolean strictStandalone = !WestlakeLauncher.isRealFrameworkFallbackAllowed();
        if (strictStandalone) {
            WestlakeLauncher.marker("PF301 strict WAT getClassLoader entry");
        }
        if (mClassLoader != null) {
            if (strictStandalone) {
                WestlakeLauncher.marker("PF301 strict WAT getClassLoader mClassLoader returned");
            }
            return mClassLoader;
        }
        if (strictStandalone) {
            WestlakeLauncher.marker("PF301 strict WAT getClassLoader context call");
        }
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (strictStandalone) {
            WestlakeLauncher.marker("PF301 strict WAT getClassLoader context returned");
        }
        if (cl != null) {
            if (strictStandalone) {
                WestlakeLauncher.marker("PF301 strict WAT getClassLoader context nonnull");
            }
            return cl;
        }
        if (strictStandalone) {
            WestlakeLauncher.marker("PF301 strict WAT getClassLoader fallback call");
        }
        ClassLoader fallback = WestlakeLauncher.safeGuestFallbackClassLoader();
        if (strictStandalone) {
            WestlakeLauncher.marker("PF301 strict WAT getClassLoader fallback returned");
        }
        return fallback;
    }

    public AppComponentFactory getAppComponentFactory() {
        return mAppComponentFactory;
    }

    private void ensureLaunchHelpersReady() {
        final boolean strictStandalone = !WestlakeLauncher.isRealFrameworkFallbackAllowed();
        if (mInstrumentation == null) {
            mInstrumentation = new WestlakeInstrumentation(this);
            if (strictStandalone) {
                WestlakeLauncher.marker("PF301 strict WAT lazy instrumentation ready");
            }
        }
        if (mAppComponentFactory == null) {
            mAppComponentFactory = new AppComponentFactory();
            if (strictStandalone) {
                WestlakeLauncher.marker("PF301 strict WAT lazy default factory ready");
            }
        } else if (mInstrumentation != null
                && mAppComponentFactory.getClass() != AppComponentFactory.class) {
            mInstrumentation.setAppComponentFactory(mAppComponentFactory);
        }
    }

    /** Set a custom AppComponentFactory (e.g., Hilt's). */
    public void setAppComponentFactory(AppComponentFactory factory) {
        mAppComponentFactory = factory;
        if (mInstrumentation != null) {
            mInstrumentation.setAppComponentFactory(factory);
        }
    }

    public boolean setAppComponentFactoryClassName(String factoryClassName,
            ClassLoader classLoader) {
        AppComponentFactory factory = instantiateAppComponentFactory(
                factoryClassName, classLoader);
        if (factory == null) {
            return false;
        }
        setAppComponentFactory(factory);
        return true;
    }

    public void forceMakeApplicationForNextLaunch(String appClassName) {
        mForceMakeApplicationForNextLaunch = true;
        mForcedApplicationClassName = appClassName;
        mInitialApplication = null;
    }

    private Application makeApplicationForLaunch(PackageInfo packageInfo,
            String forcedAppClassName, boolean strictStandalone, String markerPrefix) {
        String appClassName = forcedAppClassName;
        MiniServer server = null;
        try {
            server = MiniServer.get();
        } catch (Exception ignored) {
        }
        if ((appClassName == null || appClassName.length() == 0) && server != null) {
            ApkInfo info = server.getApkInfo();
            if (info != null) {
                appClassName = info.applicationClassName;
            }
        }
        if (strictStandalone) {
            WestlakeLauncher.marker(markerPrefix + " makeApplication call");
        }
        Application app = packageInfo.makeApplication(false, appClassName, mInstrumentation);
        mForceMakeApplicationForNextLaunch = false;
        mForcedApplicationClassName = null;
        if (strictStandalone) {
            WestlakeLauncher.marker(app != null
                    ? markerPrefix + " makeApplication nonnull"
                    : markerPrefix + " makeApplication null");
        }
        mInitialApplication = app;
        if (app != null) {
            try {
                MiniServer.currentSetApplication(app);
            } catch (Throwable ignored) {
            }
        }
        return app;
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
     *   3. Create Application first when the launch was bound that way
     *   4. Instrumentation.newActivity() -> AppComponentFactory.instantiateActivity()
     *   5. PackageInfo.makeApplication() fallback or reuse
     *   6. activity.attach(context, this, instrumentation, ...) via reflection
     *   7. Instrumentation.callActivityOnCreate(activity, savedState)
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
        if (!WestlakeLauncher.isRealFrameworkFallbackAllowed()) {
            WestlakeLauncher.marker("PF301 strict WAT static launchActivity entry");
        }
        if (thread == null) {
            throw new NullPointerException("thread");
        }
        if (!WestlakeLauncher.isRealFrameworkFallbackAllowed()) {
            WestlakeLauncher.marker("PF301 strict WAT static performLaunchActivityImpl call");
        }
        Activity activity = thread.performLaunchActivityImpl(className, packageName, intent, null);
        if (!WestlakeLauncher.isRealFrameworkFallbackAllowed()) {
            WestlakeLauncher.marker("PF301 strict WAT static performLaunchActivityImpl returned");
        }
        return activity;
    }

    public Activity probeLaunchBoundary() {
        return null;
    }

    public String probeResolveLaunchPackageName(String packageName, String className) {
        return resolveLaunchPackageName(packageName, className, null);
    }

    public String probeResolveLaunchPackageName(String packageName, String className, Intent intent) {
        return resolveLaunchPackageName(packageName, className, intent);
    }

    public String probeResolveLaunchPackageNameNulls() {
        return resolveLaunchPackageName(null, null, null);
    }

    public Intent probeIntentArg(Intent intent) {
        return intent;
    }

    public Object probeObjectArg(Object value) {
        return value;
    }

    public ComponentName probeComponentArg(ComponentName value) {
        if (value == null) {
            return null;
        }
        String className = value.getClassName();
        String stablePackage = choosePackageCandidate(
                resolveLaunchPackageName(value.getPackageName(), className, null));
        if (stablePackage == null) {
            stablePackage = choosePackageCandidate(value.getPackageName());
        }
        if (stablePackage == null) {
            stablePackage = knownPackageForClass(className);
        }
        if (isPlaceholderPackage(stablePackage) || className == null || className.isEmpty()) {
            return value;
        }
        return new ComponentName(stablePackage, className);
    }

    public Object probeLaunchClassLoaderField() {
        return mClassLoader;
    }

    public Object probeLaunchContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public Object probeLaunchEngineClassLoader() {
        return com.westlake.engine.WestlakeLauncher.class.getClassLoader();
    }

    public Object probeLaunchClassLoader() {
        return getClassLoader();
    }

    public ComponentName probeResolveLaunchComponent(String packageName, String className, Intent intent) {
        if (intent == null) {
            return null;
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
        return intent.getComponent();
    }

    public Activity performLaunchActivity(String className, String packageName,
                                           Intent intent, Bundle savedState) {
        return performLaunchActivityImpl(className, packageName, intent, savedState);
    }

    // TODO: V2-Step8 deletion candidate
    private static boolean isCutoffCanaryLifecycleProbe(String packageName,
            String className, Intent intent) {
        if ("com.westlake.cutoffcanary".equals(packageName)) {
            return true;
        }
        if ("com.westlake.showcase".equals(packageName)) {
            return true;
        }
        if ("com.westlake.yelplive".equals(packageName)) {
            return true;
        }
        if ("com.westlake.materialxmlprobe".equals(packageName)) {
            return true;
        }
        if ("com.westlake.mcdprofile".equals(packageName)) {
            return true;
        }
        if (className != null && className.startsWith("com.westlake.cutoffcanary.")) {
            return true;
        }
        if (className != null && className.startsWith("com.westlake.showcase.")) {
            return true;
        }
        if (className != null && className.startsWith("com.westlake.yelplive.")) {
            return true;
        }
        if (className != null && className.startsWith("com.westlake.materialxmlprobe.")) {
            return true;
        }
        if (className != null && className.startsWith("com.westlake.mcdprofile.")) {
            return true;
        }
        try {
            ComponentName component = intent != null ? intent.getComponent() : null;
            if (component != null) {
                if ("com.westlake.cutoffcanary".equals(component.getPackageName())) {
                    return true;
                }
                if ("com.westlake.showcase".equals(component.getPackageName())) {
                    return true;
                }
                if ("com.westlake.yelplive".equals(component.getPackageName())) {
                    return true;
                }
                if ("com.westlake.materialxmlprobe".equals(component.getPackageName())) {
                    return true;
                }
                if ("com.westlake.mcdprofile".equals(component.getPackageName())) {
                    return true;
                }
                String componentClass = component.getClassName();
                return componentClass != null
                        && (componentClass.startsWith("com.westlake.cutoffcanary.")
                                || componentClass.startsWith("com.westlake.showcase.")
                                || componentClass.startsWith("com.westlake.yelplive.")
                                || componentClass.startsWith("com.westlake.materialxmlprobe.")
                                || componentClass.startsWith("com.westlake.mcdprofile."));
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    private static boolean isMcdonaldsPackageOrClass(String value) {
        return "com.mcdonalds.app".equals(value)
                || (value != null && value.startsWith("com.mcdonalds."));
    }

    private static String markerToken(String value) {
        if (value == null || value.length() == 0) {
            return "null";
        }
        StringBuilder out = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            out.append((ch > ' ' && ch < 127) ? ch : '_');
        }
        return out.toString();
    }

    private static boolean isMcdonaldsDataSourceLaunch(String packageName,
            String className, Intent intent) {
        if (isMcdonaldsPackageOrClass(packageName)
                || isMcdonaldsPackageOrClass(className)) {
            return true;
        }
        try {
            ComponentName component = intent != null ? intent.getComponent() : null;
            return component != null
                    && (isMcdonaldsPackageOrClass(component.getPackageName())
                    || isMcdonaldsPackageOrClass(component.getClassName()));
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean isMcdonaldsDataSourceBootstrapEnabled() {
        try {
            if (Boolean.parseBoolean(System.getProperty(
                    "westlake.mcd.datasource.bootstrap", "true"))) {
                return true;
            }
        } catch (Throwable ignored) {
        }
        try {
            return "1".equals(System.getenv("WESTLAKE_MCD_DATASOURCE_BOOTSTRAP"))
                    || "true".equalsIgnoreCase(
                            System.getenv("WESTLAKE_MCD_DATASOURCE_BOOTSTRAP"));
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean shouldBootstrapMcdonaldsDataSource(String packageName,
            String className, Intent intent) {
        return isMcdonaldsDataSourceBootstrapEnabled()
                && isMcdonaldsDataSourceLaunch(packageName, className, intent);
    }

    private static Object createKnownMcdDataSourceReturn(Class<?> returnType) {
        if (returnType == null) return null;
        String implName = null;
        String returnName = returnType.getName();
        if ("com.mcdonalds.mcdcoreapp.helper.interfaces.OrderModuleInteractor".equals(returnName)) {
            implName = "com.mcdonalds.order.util.OrderModuleImplementation";
        } else if ("com.mcdonalds.mcdcoreapp.helper.interfaces.PaymentModuleInteractor"
                .equals(returnName)) {
            implName = "com.mcdonalds.payments.PaymentModuleImplementation";
        } else if ("com.mcdonalds.mcdcoreapp.helper.interfaces.HomeModuleInteractor"
                .equals(returnName)) {
            implName = "com.mcdonalds.homedashboard.util.HomeHelperImplementation";
        } else if ("com.mcdonalds.mcdcoreapp.helper.interfaces.HomeDashboardModuleInteractor"
                .equals(returnName)) {
            implName = "com.mcdonalds.homedashboard.util.HomeDashboardModuleImpl";
        } else if ("com.mcdonalds.mcdcoreapp.helper.interfaces.HomeDashboardHeroInteractor"
                .equals(returnName)) {
            implName = "com.mcdonalds.homedashboard.util.HomeDashboardHeroInteractorImpl";
        } else if ("com.mcdonalds.mcdcoreapp.helper.interfaces.LoyaltyModuleInteractor"
                .equals(returnName)) {
            implName = "com.mcdonalds.loyalty.dashboard.util.DealsLoyaltyImplementation";
        }
        if (implName == null) return null;
        try {
            ClassLoader cl = returnType.getClassLoader();
            if (cl == null) cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) cl = ClassLoader.getSystemClassLoader();
            Class<?> implClass = cl.loadClass(implName);
            if (!returnType.isAssignableFrom(implClass)) {
                log("W", "McD DataSource known return " + implName
                        + " is not assignable to " + returnName);
                return null;
            }
            Object constructed = instantiateMcdWithDefaultArgs(implClass);
            if (constructed != null) {
                log("I", "McD DataSource " + returnName + " = " + implName
                        + " [default args]");
                return constructed;
            }
            if ("com.mcdonalds.loyalty.dashboard.util.DealsLoyaltyImplementation"
                    .equals(implName)) {
                log("W", "McD DataSource known return needs constructor " + implName);
                return null;
            }
            Object instance = allocateWithoutConstructor(implClass);
            if (instance != null) {
                log("I", "McD DataSource " + returnName + " = " + implName);
            }
            return instance;
        } catch (Throwable t) {
            log("W", "McD DataSource known return failed " + returnName + ": "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
            return null;
        }
    }

    private static Object instantiateMcdWithDefaultArgs(Class<?> clazz) {
        if (clazz == null || clazz.isInterface()
                || java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
            return null;
        }
        try {
            java.lang.reflect.Constructor<?> noArg = clazz.getDeclaredConstructor();
            noArg.setAccessible(true);
            return noArg.newInstance();
        } catch (Throwable ignored) {
        }
        for (java.lang.reflect.Constructor<?> ctor : clazz.getDeclaredConstructors()) {
            try {
                Class<?>[] types = ctor.getParameterTypes();
                Object[] args = new Object[types.length];
                for (int i = 0; i < types.length; i++) {
                    args[i] = defaultMcdConstructorArg(types[i]);
                }
                ctor.setAccessible(true);
                return ctor.newInstance(args);
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    private static Object defaultMcdConstructorArg(Class<?> type) {
        if (type == null) return null;
        if (type == boolean.class) return Boolean.FALSE;
        if (type == byte.class) return Byte.valueOf((byte) 0);
        if (type == short.class) return Short.valueOf((short) 0);
        if (type == int.class) return Integer.valueOf(0);
        if (type == long.class) return Long.valueOf(0L);
        if (type == float.class) return Float.valueOf(0f);
        if (type == double.class) return Double.valueOf(0d);
        if (type == char.class) return Character.valueOf('\0');
        if (type == String.class || type == CharSequence.class) return "";
        if (type.isArray()) {
            return java.lang.reflect.Array.newInstance(type.getComponentType(), 0);
        }
        if (java.util.List.class.isAssignableFrom(type)
                || java.util.Collection.class.isAssignableFrom(type)) {
            return new java.util.ArrayList<Object>();
        }
        if (java.util.Map.class.isAssignableFrom(type)) {
            return new java.util.HashMap<Object, Object>();
        }
        if (java.util.Set.class.isAssignableFrom(type)) {
            return new java.util.HashSet<Object>();
        }
        if (type.isInterface()) {
            return createMcdInterfaceProxy(type, type.getClassLoader(), "ctor");
        }
        return null;
    }

    private static Object createMcdInterfaceProxy(final Class<?> type, ClassLoader fallbackCl,
            final String label) {
        if (type == null || !type.isInterface()) {
            return null;
        }
        try {
            ClassLoader cl = type.getClassLoader();
            if (cl == null) cl = fallbackCl;
            if (cl == null) cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) cl = ClassLoader.getSystemClassLoader();
            final ClassLoader proxyCl = cl;
            final java.lang.reflect.InvocationHandler[] handlerRef =
                    new java.lang.reflect.InvocationHandler[1];
            handlerRef[0] = new java.lang.reflect.InvocationHandler() {
                public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) {
                    String name = method.getName();
                    if ("toString".equals(name)) {
                        return "WestlakeMcDProxy(" + label + ":" + type.getSimpleName() + ")";
                    }
                    if ("hashCode".equals(name)) return Integer.valueOf(0);
                    if ("equals".equals(name)) return Boolean.valueOf(proxy == (args != null
                            && args.length > 0 ? args[0] : null));
                    Class<?> rt = method.getReturnType();
                    return defaultMcdProxyReturn(rt, proxyCl, label + "." + name);
                }
            };
            return java.lang.reflect.Proxy.newProxyInstance(proxyCl, new Class<?>[] { type },
                    handlerRef[0]);
        } catch (Throwable t) {
            log("W", "McD proxy failed for " + type.getName() + ": "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
            return null;
        }
    }

    private static Object defaultMcdProxyReturn(Class<?> rt, ClassLoader fallbackCl, String label) {
        if (rt == null || rt == Void.TYPE) return null;
        if (rt == boolean.class) return Boolean.FALSE;
        if (rt == byte.class) return Byte.valueOf((byte) 0);
        if (rt == short.class) return Short.valueOf((short) 0);
        if (rt == int.class) return Integer.valueOf(0);
        if (rt == long.class) return Long.valueOf(0L);
        if (rt == float.class) return Float.valueOf(0f);
        if (rt == double.class) return Double.valueOf(0d);
        if (rt == char.class) return Character.valueOf('\0');
        if (rt == Boolean.class) return Boolean.FALSE;
        if (rt == Byte.class) return Byte.valueOf((byte) 0);
        if (rt == Short.class) return Short.valueOf((short) 0);
        if (rt == Integer.class) return Integer.valueOf(0);
        if (rt == Long.class) return Long.valueOf(0L);
        if (rt == Float.class) return Float.valueOf(0f);
        if (rt == Double.class) return Double.valueOf(0d);
        if (rt == Character.class) return Character.valueOf('\0');
        if (rt == String.class) return "";
        if (rt.isInterface()) {
            return createMcdInterfaceProxy(rt, fallbackCl, label);
        }
        return createKnownMcdDataSourceReturn(rt);
    }

    private static Object allocateWithoutConstructor(Class<?> clazz) {
        try {
            java.lang.reflect.Field unsafeField =
                    Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Object unsafe = unsafeField.get(null);
            return unsafe.getClass().getMethod("allocateInstance", Class.class)
                    .invoke(unsafe, clazz);
        } catch (Throwable t) {
            log("W", "Unsafe.allocateInstance failed for " + clazz.getName() + ": "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
            return null;
        }
    }

    // TODO: V2-Step8 deletion candidate
    private static boolean shouldRunMcdonaldsLifecycleInStrict(String packageName,
            String className, Intent intent) {
        if (!isMcdonaldsDataSourceLaunch(packageName, className, intent)) {
            return false;
        }
        try {
            if ("false".equalsIgnoreCase(
                    System.getProperty("westlake.mcd.lifecycle", "true"))) {
                return false;
            }
        } catch (Throwable ignored) {
        }
        try {
            String env = System.getenv("WESTLAKE_MCD_LIFECYCLE");
            return env == null
                    || (!"0".equals(env) && !"false".equalsIgnoreCase(env));
        } catch (Throwable ignored) {
            return true;
        }
    }

    private static int watPrecreateProbeLevel(Intent intent) {
        String stage = null;
        try {
            stage = intent != null ? intent.getStringExtra("stage") : null;
        } catch (Throwable ignored) {
        }
        if (stage == null || !stage.startsWith("L4WATPRECREATE")) {
            return 0;
        }
        int value = 0;
        for (int i = "L4WATPRECREATE".length(); i < stage.length(); i++) {
            char ch = stage.charAt(i);
            if (ch < '0' || ch > '9') {
                continue;
            }
            value = (value * 10) + (ch - '0');
        }
        return value > 0 ? value : 1;
    }

    private static boolean isExactWatPrecreateStage(Intent intent) {
        try {
            return "L4WATPRECREATE".equals(intent != null
                    ? intent.getStringExtra("stage")
                    : null);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private Activity performLaunchActivityImpl(String className, String packageName,
                                               Intent intent, Bundle savedState) {
        final boolean strictStandalone = !WestlakeLauncher.isRealFrameworkFallbackAllowed();
        // M7-Step2 (2026-05-13): generic production-launch opt-in. Either:
        //  - a westlake/McD probe gate (existing per-app gates, unchanged), or
        //  - any process-wide caller that invoked
        //    `WestlakeActivityThread.setForceLifecycleEnabled(true)` to
        //    request the proper Android launch sequence (record +
        //    callActivityOnCreate). Generic across all apps.
        final boolean forceLifecycleInStrict =
                sForceLifecycleEnabled
                        || isCutoffCanaryLifecycleProbe(packageName, className, intent)
                        || shouldRunMcdonaldsLifecycleInStrict(packageName, className, intent);
        if (strictStandalone) {
            WestlakeLauncher.marker("PF301 strict WAT impl entry");
        }
        if (!strictStandalone) {
            WestlakeLauncher.trace("[WestlakeActivityThread] performLaunchActivity begin: " + className);
            log("I", "performLaunchActivity: " + className);
            android.util.Log.i("WestlakeStep", "performLaunchActivity begin " + className);
        }

        // ── Step 1: Resolve component and classloader ──
        if (intent == null) {
            intent = new Intent();
        }
        if (strictStandalone) {
            WestlakeLauncher.marker("PF301 strict WAT impl resolveLaunchPackageName call");
        }
        String resolvedPackageName = resolveLaunchPackageName(packageName, className, intent);
        if (strictStandalone) {
            WestlakeLauncher.marker("PF301 strict WAT impl resolveLaunchPackageName returned");
        }
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
        if (!strictStandalone) {
            WestlakeLauncher.trace("[WestlakeActivityThread] component resolved: pkg="
                    + packageName + " cls=" + (component != null ? component.getClassName() : className));
            android.util.Log.i("WestlakeStep", "performLaunchActivity component pkg="
                    + packageName + " cls=" + (component != null ? component.getClassName() : className));
        }

        if (strictStandalone) {
            WestlakeLauncher.marker("PF301 strict WAT impl getClassLoader call");
        }
        ClassLoader cl = getClassLoader();
        if (strictStandalone) {
            WestlakeLauncher.marker("PF301 strict WAT impl getClassLoader returned");
        }
        ensureLaunchHelpersReady();
        if (strictStandalone) {
            WestlakeLauncher.marker("PF301 strict WAT impl launch helpers ready");
        }
        try {
            if (isMcdonaldsDataSourceLaunch(packageName, className, intent)) {
                seedCoroutineRuntimeState(cl);
            }
        } catch (Throwable t) {
            log("W", "Coroutine runtime seed failed: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
        AppComponentFactory factory = mAppComponentFactory != null
                ? mAppComponentFactory : new AppComponentFactory();

        // ── Step 2: Create PackageInfo (simplified LoadedApk) ──
        PackageInfo packageInfo = new PackageInfo(packageName, cl, factory);
        if (strictStandalone) {
            WestlakeLauncher.marker("PF301 strict WAT impl packageInfo ready");
        }
        if (!strictStandalone) {
            android.util.Log.i("WestlakeStep", "performLaunchActivity packageInfo ready " + packageName);
        }

        boolean forceMakeApplication = mForceMakeApplicationForNextLaunch;
        String forcedAppClassName = mForcedApplicationClassName;
        Application app = forceMakeApplication ? null : mInitialApplication;
        if (forceMakeApplication) {
            if (strictStandalone) {
                WestlakeLauncher.marker(
                        "PF301 strict WAT impl preactivity force makeApplication enabled");
                WestlakeLauncher.marker("CV WAT app factory preactivity makeApplication begin");
            }
            app = makeApplicationForLaunch(packageInfo, forcedAppClassName, strictStandalone,
                    "PF301 strict WAT impl preactivity");
            if (strictStandalone) {
                WestlakeLauncher.marker(app != null
                        ? "CV WAT app factory preactivity makeApplication returned"
                        : "CV WAT app factory preactivity makeApplication null");
            }
        }

        // ── Step 3: Create base context for the activity ──
        // In AOSP this is ContextImpl.createBaseContextForActivity().
        // We use the Application as the base context when one is available;
        // otherwise build a WestlakeContextImpl.  CR23-fix (2026-05-13):
        // `new Context()` would throw InstantiationError when framework.jar
        // is on -Xbootclasspath (Context is abstract there) — route through
        // the shared helper so both fall-throughs land on WestlakeContextImpl.
        Context baseContext = mInitialApplication;
        if (baseContext == null) {
            baseContext = buildBaseContext(packageName, cl);
        }
        if (strictStandalone) {
            WestlakeLauncher.marker("PF301 strict WAT impl baseContext ready");
        }
        if (!strictStandalone) {
            android.util.Log.i("WestlakeStep", "performLaunchActivity baseContext "
                    + baseContext.getClass().getName());
        }

        // ── Step 3.5: Ensure DataSourceHelper is initialized before Activity constructor ──
        // Create stub proxies for ALL null interface fields on DataSourceHelper
        try {
            boolean mcdDatasourceLaunch =
                    isMcdonaldsDataSourceLaunch(packageName, className, intent);
            if (!mcdDatasourceLaunch) {
                if (strictStandalone) {
                    WestlakeLauncher.marker("PF301 strict WAT impl datasource skipped non-mcd");
                } else {
                    log("D", "Step 3.5 DataSourceHelper skipped: non-McDonald's launch");
                }
            } else if (!shouldBootstrapMcdonaldsDataSource(packageName, className, intent)) {
                if (strictStandalone) {
                    WestlakeLauncher.marker("PF301 strict WAT impl datasource skipped disabled");
                } else {
                    log("D", "Step 3.5 DataSourceHelper skipped: disabled for stock McDonald's launch");
                }
            } else if (strictStandalone && cl == null) {
                WestlakeLauncher.marker("PF301 strict WAT impl datasource skipped null classloader");
            } else {
            if (!strictStandalone) {
                android.util.Log.i("WestlakeStep", "performLaunchActivity datasource begin");
            }
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
                    Object knownReturn = createKnownMcdDataSourceReturn(rt);
                    if (knownReturn != null) {
                        return knownReturn;
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
                    }
                } catch (Throwable perFieldEx) {
                    log("W", "DataSourceHelper field '" + f.getName() + "' scan error: " + perFieldEx.getMessage());
                }
            }
            log("D", "DataSourceHelper getter verification skipped in standalone bootstrap");
            }
        } catch (Throwable t) {
            log("W", "Step 3.5 DataSourceHelper error: " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
        if (strictStandalone) {
            WestlakeLauncher.marker("PF301 strict WAT impl datasource done");
        } else {
            android.util.Log.i("WestlakeStep", "performLaunchActivity datasource end");
        }
        try {
            if (shouldBootstrapMcdonaldsDataSource(packageName, className, intent)) {
                seedMcdonaldsAppConfigurationState(cl);
                seedMcdonaldsJustFlipState(cl);
                seedMcdonaldsSdkPersistenceState(cl);
            }
        } catch (Throwable t) {
            log("W", "McD AppConfiguration seed failed: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }

        // ── Step 4: Instantiate the Activity via Instrumentation ──
        // CR62 Step 2: publish the freshly-built baseContext to a
        // thread-local BEFORE the Activity ctor runs. ContextWrapper(null)
        // — which runs during the Activity no-arg super-chain — consults
        // this thread-local. Net effect: the AppCompatActivity override of
        // attachBaseContext receives a real Context, not null, so
        // AppCompatDelegate.attachBaseContext2 doesn't NPE.
        //
        // Cleared in finally so a failing ctor doesn't leak the
        // thread-local across subsequent activities on this thread.
        Activity activity = null;
        if (baseContext != null) {
            WestlakeInstrumentation.publishContextForNewActivity(baseContext);
            if (strictStandalone) {
                WestlakeLauncher.marker("PF301 strict WAT impl newActivity prebase published");
            }
        }
        try {
            if (strictStandalone) {
                WestlakeLauncher.marker("PF301 strict WAT impl newActivity call");
            } else {
                WestlakeLauncher.trace("[WestlakeActivityThread] step4 newActivity");
                android.util.Log.i("WestlakeStep", "performLaunchActivity newActivity begin " + className);
            }
            activity = mInstrumentation.newActivity(cl, className, intent);
            if (activity == null) {
                throw new InstantiationException(
                        "Instrumentation returned null for " + className);
            }
            if (strictStandalone) {
                WestlakeLauncher.marker("PF301 strict WAT impl newActivity returned");
            } else {
                WestlakeLauncher.trace("[WestlakeActivityThread] step4 newActivity OK: "
                        + activity.getClass().getName());
                log("I", "  Created activity: " + activity.getClass().getName());
                android.util.Log.i("WestlakeStep", "performLaunchActivity newActivity done "
                        + activity.getClass().getName());
            }
        } catch (Exception e) {
            WestlakeLauncher.dumpThrowable("[WestlakeActivityThread] step4 newActivity failed", e);
            // CR62 Step 2: release the published baseContext from the
            // thread-local on the failure path so a subsequent retry
            // doesn't see stale state. (Java doesn't have a try/catch/
            // finally on this block because the rest of
            // performLaunchActivityImpl runs straight-line below and we'd
            // need to wrap everything; the success path clears at the end
            // of step 4, and re-publishing is per-launch so it's fine if
            // intermediate ctors don't see the thread-local.)
            WestlakeInstrumentation.clearPendingBaseContext();
            if (mInstrumentation == null || !mInstrumentation.onException(null, e)) {
                log("E", "  Unable to instantiate activity " + className + ": " + e);
                throw new RuntimeException(
                        "Unable to instantiate activity " + component + ": " + e, e);
            }
            return null;
        }
        // CR62 Step 2: success path — release the thread-local now that
        // the Activity ctor completed.
        WestlakeInstrumentation.clearPendingBaseContext();

        // ── Step 5: Get or create the Application ──
        // IMPORTANT: reuse existing Application — do NOT call makeApplication() again
        // as it calls Application.onCreate() which blocks on initializer chains
        if (strictStandalone) {
            WestlakeLauncher.marker("PF301 strict WAT impl step5 app begin");
        }
        if (strictStandalone) {
            if (forceMakeApplication) {
                WestlakeLauncher.marker("PF301 strict WAT impl step5 force makeApplication handled");
            }
            WestlakeLauncher.marker(app != null
                    ? "PF301 strict WAT impl step5 initial app nonnull"
                    : "PF301 strict WAT impl step5 initial app null");
        }
        if (app == null && !forceMakeApplication) {
            // Try MiniServer's application first
            try {
                if (strictStandalone) {
                    WestlakeLauncher.marker("PF301 strict WAT impl step5 MiniServer app call");
                }
                app = MiniServer.get().getApplication();
                if (strictStandalone) {
                    WestlakeLauncher.marker(app != null
                            ? "PF301 strict WAT impl step5 MiniServer app nonnull"
                            : "PF301 strict WAT impl step5 MiniServer app null");
                }
                // CR59 (2026-05-14): MiniServer.get() auto-inits with a
                // bare-bones placeholder Application (class is exactly
                // `android.app.Application`, NOT a user subclass like
                // NoiceApplication / Hilt_NoiceApplication / McdApplication).
                // That placeholder has the wrong package + the wrong
                // class, and -- pre-CR59 ctor fixup -- a null mBase.
                // Returning it here defeats `makeApplicationForLaunch`
                // and downstream Hilt resolution fails with
                // `ContextWrapper.getApplicationContext()` NPE inside
                // MainActivity's first Kotlin Lazy delegate (the M7-Step2
                // documented blocker).  Detect the placeholder and
                // discard it -- the next branch will call
                // `makeApplicationForLaunch`, which instantiates the
                // real user Application class via the
                // AppComponentFactory + APK classloader path.
                // Generic: no per-app branches.  The placeholder is
                // identified solely by its exact runtime class.
                if (app != null
                        && "android.app.Application".equals(app.getClass().getName())) {
                    if (strictStandalone) {
                        WestlakeLauncher.marker(
                                "PF301 strict WAT impl step5 MiniServer placeholder discarded");
                    }
                    app = null;
                }
            } catch (Exception ignored) {}
        } else if (app == null && strictStandalone) {
            WestlakeLauncher.marker("PF301 strict WAT impl step5 MiniServer app skipped forced");
        }
        if (app == null) {
            app = makeApplicationForLaunch(packageInfo, forcedAppClassName, strictStandalone,
                    "PF301 strict WAT impl step5");
        } else {
            if (strictStandalone) {
                WestlakeLauncher.marker("PF301 strict WAT impl step5 reuse app skip log");
            } else {
                log("D", "  Reusing existing Application (skip makeApplication)");
            }
        }
        if (strictStandalone) {
            WestlakeLauncher.marker("PF301 strict WAT impl step5 app ready");
            WestlakeLauncher.marker("PF301 strict WAT impl step5 ready skip log");
        } else {
            android.util.Log.i("WestlakeStep", "performLaunchActivity application ready "
                    + (app != null ? app.getClass().getName() : "null"));
        }
        if (app != null) {
            mInitialApplication = app;
            ShimCompat.setPackageName(app, packageName);
            // CR56: wire WestlakeContextImpl.setAttachedApplication on the
            // Activity's base context so getApplicationContext() returns the
            // Application instead of `this` (a non-Application Context).
            // Hilt's dagger.hilt.android.internal.Contexts.getApplication(Context)
            // walks getApplicationContext() expecting to find an Application;
            // without this call, MainActivity.onCreate's first lazy Hilt
            // delegate (e.g. settingsRepository$2) NPEs.  The M4-PRE11
            // plumbing in makeApplication() already wires the SEPARATE
            // WestlakeContextImpl that's the Application's own mBase, but the
            // Activity's mBase is a DIFFERENT WestlakeContextImpl built at
            // line ~1340 (buildBaseContext) — wire it here.  Generic across
            // all apps, no per-app branches.
            if (baseContext != null && baseContext != app) {
                publishApplicationToBaseContext(baseContext, app);
            }
        }

        try {
            if (shouldBootstrapMcdonaldsDataSource(packageName, className, intent)) {
                seedMcdonaldsApplicationContext(cl, app != null ? app : baseContext);
                seedMcdonaldsAppConfigurationState(cl);
                seedMcdonaldsJustFlipState(cl);
                seedMcdonaldsSdkCoreReady(cl, app != null ? app : baseContext);
                seedMcdonaldsClickstreamState(cl, app);
                seedMcdonaldsDeepLinkRouter(cl);
                if (strictStandalone) {
                    WestlakeLauncher.marker("PF301 strict WAT impl clickstream seeded");
                }
            }
        } catch (Throwable t) {
            log("W", "McD ClickstreamDataHelper seed failed: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }

        // ── Step 6: Attach the activity ──
        if (strictStandalone) {
            WestlakeLauncher.marker("PF301 strict WAT impl step6 attach skip log");
        } else {
            log("D", "  Step 6: attach " + className);
        }
        if (strictStandalone) {
            WestlakeLauncher.marker("PF301 strict WAT impl step6 attach begin");
            WestlakeLauncher.marker("PF301 strict WAT impl step6 attach skip trace");
            WestlakeLauncher.marker("PF301 strict WAT impl step6 attach skip log2");
            WestlakeLauncher.marker("PF301 strict WAT impl step6 attach call");
        } else {
            WestlakeLauncher.trace("[WestlakeActivityThread] step6 attach begin");
            android.util.Log.i("WestlakeStep", "performLaunchActivity attach begin " + className);
        }
        attachActivity(activity, baseContext, app, intent, component);
        if (strictStandalone) {
            WestlakeLauncher.marker("PF301 strict WAT impl step6 attach done");
        } else {
            WestlakeLauncher.trace("[WestlakeActivityThread] step6 attach done");
            android.util.Log.i("WestlakeStep", "performLaunchActivity attach done " + className);
        }

        // ── Step 7: Create the ActivityClientRecord ──
        if (strictStandalone && !forceLifecycleInStrict) {
            WestlakeLauncher.marker("PF301 strict WAT impl step7 record begin");
            WestlakeLauncher.marker("PF301 strict WAT impl step7 record skipped");
            WestlakeLauncher.marker("PF301 strict WAT impl step7 record created");
            WestlakeLauncher.marker("PF301 strict WAT impl step7 record stored");
        } else {
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
            android.util.Log.i("WestlakeStep", "performLaunchActivity record stored " + className);
        }

        // ── Step 8: Call onCreate via Instrumentation ──
        if (strictStandalone && !forceLifecycleInStrict) {
            WestlakeLauncher.marker("PF301 strict WAT impl step8 onCreate skip log");
            WestlakeLauncher.marker("PF301 strict WAT impl step8 savedstate call");
        } else {
            log("D", "  Step 8: callActivityOnCreate " + className);
        }
        try {
            if (strictStandalone && !forceLifecycleInStrict) {
                WestlakeLauncher.marker("PF301 strict WAT impl step8 savedstate skipped");
                WestlakeLauncher.marker("PF301 strict WAT impl step8 savedstate returned");
                WestlakeLauncher.marker("PF301 strict WAT impl step8 onCreate begin");
                WestlakeLauncher.marker("PF301 strict WAT impl step8 onCreate skipped");
                WestlakeLauncher.marker("PF301 strict WAT impl step8 onCreate done");
            } else {
                boolean mcdonaldsStrictLifecycle =
                        strictStandalone
                                && isMcdonaldsDataSourceLaunch(packageName, className, intent);
                if (mcdonaldsStrictLifecycle) {
                    // Stock McDonald's activities are constructed normally on
                    // this path, so AndroidX constructor-owned saved-state fields
                    // already exist. The synthetic repair path is only needed for
                    // constructor-bypassed probes and currently trips the strict
                    // runtime before SplashActivity.onCreate().
                    WestlakeLauncher.marker("PFMCD precreate savedstate skipped constructor path");
                } else if (forceLifecycleInStrict) {
                    int precreateProbeLevel = watPrecreateProbeLevel(intent);
                    if (precreateProbeLevel > 0 && !isExactWatPrecreateStage(intent)) {
                        probeNamedComponentActivitySavedStateReady(
                                activity, precreateProbeLevel);
                    } else {
                        WestlakeLauncher.noteMarker("CV WAT precreate savedstate begin");
                        ensureNamedComponentActivitySavedStateReady(activity);
                        WestlakeLauncher.noteMarker("CV WAT precreate savedstate returned");
                    }
                } else {
                    ensureNamedComponentActivitySavedStateReady(activity);
                }
                WestlakeLauncher.trace("[WestlakeActivityThread] step8 onCreate begin");
                if (mcdonaldsStrictLifecycle) {
                    WestlakeLauncher.marker("PFMCD onCreate begin");
                }
                android.util.Log.i("WestlakeStep", "performLaunchActivity onCreate begin " + className);
                mInstrumentation.callActivityOnCreate(activity, savedState);
                WestlakeLauncher.trace("[WestlakeActivityThread] step8 onCreate done");
                if (mcdonaldsStrictLifecycle) {
                    WestlakeLauncher.marker("PFMCD onCreate done");
                }
                log("I", "  onCreate complete for " + className);
                android.util.Log.i("WestlakeStep", "performLaunchActivity onCreate done " + className);
                if ("com.westlake.mcdprofile".equals(packageName)
                        || (className != null
                                && className.startsWith("com.westlake.mcdprofile."))) {
                    WestlakeLauncher.appendCutoffCanaryMarker(
                            "MCD_PROFILE_WAT_ACTIVITY_ONCREATE_OK class="
                                    + markerToken(className));
                }
            }
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

    private static void seedCoroutineRuntimeState(final ClassLoader cl) {
        seedCoroutineSchedulerProperties();
        seedCoroutineMainDispatcher(cl);
        seedCoroutineExceptionHandlers(cl);
    }

    public static void prepareCoroutineRuntimeForWestlake(final ClassLoader cl) {
        seedCoroutineRuntimeState(cl);
    }

    private static void seedCoroutineSchedulerProperties() {
        setDefaultSystemProperty("kotlinx.coroutines.scheduler.core.pool.size", "2");
        setDefaultSystemProperty("kotlinx.coroutines.scheduler.max.pool.size", "4");
        setDefaultSystemProperty("kotlinx.coroutines.io.parallelism", "4");
        log("D", "Coroutine scheduler props core="
                + System.getProperty("kotlinx.coroutines.scheduler.core.pool.size")
                + " max=" + System.getProperty("kotlinx.coroutines.scheduler.max.pool.size")
                + " io=" + System.getProperty("kotlinx.coroutines.io.parallelism"));
    }

    private static void setDefaultSystemProperty(String key, String value) {
        try {
            String existing = System.getProperty(key);
            if (existing == null || existing.length() == 0) {
                System.setProperty(key, value);
            }
        } catch (Throwable ignored) {
        }
    }

    static void seedCoroutineMainDispatcher(final ClassLoader cl) {
        if (cl == null) {
            return;
        }
        try {
            System.setProperty("kotlinx.coroutines.fast.service.loader", "false");
        } catch (Throwable ignored) {
        }

        Object dispatcher = createAndroidCoroutineDispatcher(cl);
        if (dispatcher == null) {
            dispatcher = createHandlerContextCoroutineDispatcher(cl);
        }
        if (dispatcher == null) {
            return;
        }

        Class<?> loaderClass;
        Class<?> dispatcherClass;
        try {
            loaderClass = Class.forName("kotlinx.coroutines.internal.MainDispatcherLoader", false, cl);
            dispatcherClass = Class.forName("kotlinx.coroutines.MainCoroutineDispatcher", false, cl);
        } catch (ClassNotFoundException ignored) {
            return;
        } catch (Throwable t) {
            log("W", "Coroutine main dispatcher seed class load failed: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
            return;
        }

        int seeded = seedCoroutineMainDispatcherField(loaderClass, dispatcherClass, dispatcher);
        try {
            Class.forName("kotlinx.coroutines.internal.MainDispatcherLoader", true, cl);
        } catch (Throwable t) {
            log("D", "Coroutine MainDispatcherLoader init tolerated during seed: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
        seeded += seedCoroutineMainDispatcherField(loaderClass, dispatcherClass, dispatcher);
        if (seeded > 0) {
            log("I", "Coroutine main dispatcher seeded "
                    + dispatcher.getClass().getName() + " fields=" + seeded);
        }
    }

    private static Object createAndroidCoroutineDispatcher(final ClassLoader cl) {
        try {
            Class<?> factoryClass = Class.forName(
                    "kotlinx.coroutines.android.AndroidDispatcherFactory", true, cl);
            Object factory = factoryClass.getDeclaredConstructor().newInstance();
            java.util.List<Object> factories = java.util.Collections.singletonList(factory);
            Class<?> dispatcherClass = Class.forName(
                    "kotlinx.coroutines.MainCoroutineDispatcher", false, cl);
            java.lang.reflect.Method[] methods = factoryClass.getDeclaredMethods();
            for (java.lang.reflect.Method method : methods) {
                Class<?>[] params = method.getParameterTypes();
                if (params.length != 1 || !java.util.List.class.isAssignableFrom(params[0])) {
                    continue;
                }
                if (!dispatcherClass.isAssignableFrom(method.getReturnType())) {
                    continue;
                }
                method.setAccessible(true);
                return method.invoke(factory, factories);
            }
            log("D", "Coroutine AndroidDispatcherFactory had no matching create method");
        } catch (ClassNotFoundException ignored) {
        } catch (Throwable t) {
            log("W", "Coroutine AndroidDispatcherFactory seed failed: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
        return null;
    }

    private static Object createHandlerContextCoroutineDispatcher(final ClassLoader cl) {
        try {
            Class<?> handlerContextClass = Class.forName(
                    "kotlinx.coroutines.android.HandlerContext", true, cl);
            java.lang.reflect.Constructor<?> ctor = handlerContextClass.getDeclaredConstructor(
                    android.os.Handler.class, String.class);
            ctor.setAccessible(true);
            return ctor.newInstance(new android.os.Handler(android.os.Looper.getMainLooper()), "Main");
        } catch (ClassNotFoundException ignored) {
        } catch (Throwable t) {
            log("W", "Coroutine HandlerContext seed failed: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
        return null;
    }

    private static int seedCoroutineMainDispatcherField(Class<?> loaderClass,
            Class<?> dispatcherClass, Object dispatcher) {
        int seeded = 0;
        for (java.lang.reflect.Field field : safeGetDeclaredFields(loaderClass)) {
            int modifiers = field.getModifiers();
            if (!java.lang.reflect.Modifier.isStatic(modifiers)
                    || !dispatcherClass.isAssignableFrom(field.getType())) {
                continue;
            }
            field.setAccessible(true);
            if (putStaticObjectFieldUnsafe(field, dispatcher)
                    || setStaticObjectField(field, dispatcher)) {
                seeded++;
            }
        }
        return seeded;
    }

    private static boolean putStaticObjectFieldUnsafe(java.lang.reflect.Field field, Object value) {
        try {
            Object unsafe = getUnsafeSingleton("sun.misc.Unsafe");
            Object base = unsafe.getClass()
                    .getMethod("staticFieldBase", java.lang.reflect.Field.class)
                    .invoke(unsafe, field);
            long offset = ((Long) unsafe.getClass()
                    .getMethod("staticFieldOffset", java.lang.reflect.Field.class)
                    .invoke(unsafe, field)).longValue();
            unsafe.getClass()
                    .getMethod("putObject", Object.class, long.class, Object.class)
                    .invoke(unsafe, base, Long.valueOf(offset), value);
            return true;
        } catch (Throwable unsafeFailure) {
            return false;
        }
    }

    private static void seedCoroutineExceptionHandlers(final ClassLoader cl) {
        if (cl == null) {
            return;
        }
        final String[] classNames = {
                "kotlinx.coroutines.internal.CoroutineExceptionHandlerImplKt",
                "kotlinx.coroutines.internal.CoroutineExceptionHandlerImpl_commonKt"
        };
        for (String className : classNames) {
            try {
                Class<?> klass = Class.forName(className, true, cl);
                int seeded = 0;
                for (java.lang.reflect.Field field : safeGetDeclaredFields(klass)) {
                    if (!java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }
                    Class<?> type = field.getType();
                    Object replacement = coroutineEmptyValueFor(type);
                    if (replacement == null) {
                        continue;
                    }
                    field.setAccessible(true);
                    Object current = field.get(null);
                    if (current != null) {
                        continue;
                    }
                    if (setStaticObjectField(field, replacement)) {
                        seeded++;
                    }
                }
                if (seeded > 0) {
                    log("I", "Coroutine exception handlers seeded " + className
                            + " fields=" + seeded);
                }
            } catch (ClassNotFoundException ignored) {
            } catch (Throwable t) {
                log("W", "Coroutine exception handler seed failed " + className + ": "
                        + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
        }
    }

    private static Object coroutineEmptyValueFor(Class<?> type) {
        if (type == null) {
            return null;
        }
        if (java.util.Iterator.class.isAssignableFrom(type)) {
            return java.util.Collections.emptyIterator();
        }
        if (java.util.List.class.isAssignableFrom(type)) {
            return java.util.Collections.emptyList();
        }
        if (java.util.Set.class.isAssignableFrom(type)) {
            return java.util.Collections.emptySet();
        }
        if (java.util.Collection.class.isAssignableFrom(type)
                || java.lang.Iterable.class.isAssignableFrom(type)) {
            return java.util.Collections.emptyList();
        }
        return null;
    }

    private static boolean setStaticObjectField(java.lang.reflect.Field field, Object value) {
        try {
            field.set(null, value);
            return true;
        } catch (Throwable directFailure) {
            try {
                Object unsafe = getUnsafeSingleton("sun.misc.Unsafe");
                Object base = unsafe.getClass()
                        .getMethod("staticFieldBase", java.lang.reflect.Field.class)
                        .invoke(unsafe, field);
                long offset = ((Long) unsafe.getClass()
                        .getMethod("staticFieldOffset", java.lang.reflect.Field.class)
                        .invoke(unsafe, field)).longValue();
                unsafe.getClass()
                        .getMethod("putObject", Object.class, long.class, Object.class)
                        .invoke(unsafe, base, Long.valueOf(offset), value);
                return true;
            } catch (Throwable unsafeFailure) {
                log("W", "Static field seed failed " + field.getName() + ": "
                        + directFailure.getClass().getSimpleName() + " / "
                        + unsafeFailure.getClass().getSimpleName());
                return false;
            }
        }
    }

    private static Object getUnsafeSingleton(String className) throws Exception {
        Class<?> unsafeClass = Class.forName(className);
        java.lang.reflect.Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        return unsafeField.get(null);
    }

    public static void seedMcdonaldsApplicationContext(final ClassLoader cl,
            final Context context) {
        if (cl == null || context == null) {
            return;
        }
        seedMcdonaldsJustFlipContextState(cl, context);
        try {
            Class<?> appCtx = cl.loadClass(
                    "com.mcdonalds.mcdcoreapp.common.ApplicationContext");
            int seeded = 0;
            try {
                java.lang.reflect.Method setter = appCtx.getDeclaredMethod(
                        "b", android.content.Context.class, boolean.class);
                setter.setAccessible(true);
                setter.invoke(null, context, Boolean.TRUE);
                seeded++;
                log("I", "McD ApplicationContext.b(context,true) invoked");
            } catch (Throwable t) {
                log("D", "McD ApplicationContext setter seed skipped: "
                        + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
            for (java.lang.reflect.Field field : safeGetDeclaredFields(appCtx)) {
                int modifiers = field.getModifiers();
                if (!java.lang.reflect.Modifier.isStatic(modifiers)
                        || java.lang.reflect.Modifier.isFinal(modifiers)) {
                    continue;
                }
                Class<?> type = field.getType();
                if (type == null || !type.isAssignableFrom(context.getClass())) {
                    continue;
                }
                field.setAccessible(true);
                if (putStaticObjectFieldUnsafe(field, context)
                        || setStaticObjectField(field, context)) {
                    seeded++;
                    log("I", "McD ApplicationContext." + field.getName()
                            + " = " + context.getClass().getSimpleName());
                }
            }
            if (seeded == 0) {
                log("D", "McD ApplicationContext seed found no null Context fields");
            }
        } catch (Throwable t) {
            log("W", "McD ApplicationContext seed failed: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void seedMcdonaldsJustFlipContextState(final ClassLoader cl,
            final Context context) {
        if (cl == null || context == null) {
            return;
        }
        try {
            Class<?> contextProvider = cl.loadClass(
                    "com.mcdonalds.justflip_kmm.flag_data.ContextProviderKt");
            int seeded = 0;
            for (java.lang.reflect.Field field : safeGetDeclaredFields(contextProvider)) {
                int modifiers = field.getModifiers();
                if (!java.lang.reflect.Modifier.isStatic(modifiers)
                        || java.lang.reflect.Modifier.isFinal(modifiers)) {
                    continue;
                }
                Class<?> type = field.getType();
                if (type == null || !type.isAssignableFrom(context.getClass())) {
                    continue;
                }
                field.setAccessible(true);
                if (putStaticObjectFieldUnsafe(field, context)
                        || setStaticObjectField(field, context)) {
                    seeded++;
                    log("I", "McD JustFlip ContextProviderKt." + field.getName()
                            + " = " + context.getClass().getSimpleName());
                }
            }
            if (seeded == 0) {
                log("D", "McD JustFlip ContextProvider seed found no Context fields");
            }
        } catch (ClassNotFoundException ignored) {
        } catch (Throwable t) {
            log("W", "McD JustFlip ContextProvider seed failed: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void seedMcdonaldsAppConfigurationState(final ClassLoader cl) {
        if (cl == null) {
            return;
        }
        try {
            Class<?> manager =
                    cl.loadClass("com.mcdonalds.mcdcoreapp.common.services.AppConfigurationManager");
            Class<?> appConfiguration =
                    cl.loadClass("com.mcdonalds.common.interactor.AppConfiguration");
            final java.util.Map<String, Object> config = new java.util.HashMap<>();
            java.util.ArrayList<Object> markets = new java.util.ArrayList<>();
            markets.add(newMcdMarketMap(cl));
            config.put("markets", markets);
            config.put("marketId", "usdap_prod");
            config.put("market", "US");
            config.put("country", "US");
            config.put("language", "en-US");
            config.put("applicationName", "McDonald's");
            config.put("applicationVersion", "7.0.0");
            config.put("clientId", "westlake-offline");
            config.put("clientSecret", "westlake-offline");
            config.put("authUrl", "https://us-prod.api.mcd.com/v1/security/auth/token");
            config.put("baseUrl", "https://us-prod.api.mcd.com/exp/v1/");
            config.put("akamaiEnabled", Boolean.FALSE);
            config.put("user_interface_build.applicationmenu", newMcdApplicationMenu(cl));
            config.put("user_interface.applicationmenu", newMcdApplicationMenu(cl));
            config.put("user_interface.order.menu", new java.util.ArrayList<Object>());
            config.put("homeDashboardSections", newMcdHomeDashboardSections(cl));
            config.put("AppFeature.AppParameter", newMcdAppFeatureAppParameter(cl));

            Object proxy = java.lang.reflect.Proxy.newProxyInstance(
                    appConfiguration.getClassLoader() != null
                            ? appConfiguration.getClassLoader() : cl,
                    new Class<?>[] { appConfiguration },
                    new java.lang.reflect.InvocationHandler() {
                        public Object invoke(Object proxy, java.lang.reflect.Method method,
                                Object[] args) {
                            String name = method.getName();
                            if ("toString".equals(name)) return "WestlakeMcDAppConfiguration";
                            if ("hashCode".equals(name)) return Integer.valueOf(0);
                            if ("equals".equals(name)) {
                                return Boolean.valueOf(proxy == (args != null && args.length > 0
                                        ? args[0] : null));
                            }
                            if ("s".equals(name) && args != null && args.length > 0
                                    && args[0] instanceof java.util.Map) {
                                java.util.Map<?, ?> update = (java.util.Map<?, ?>) args[0];
                                for (java.util.Map.Entry<?, ?> entry : update.entrySet()) {
                                    if (entry.getKey() != null) {
                                        config.put(String.valueOf(entry.getKey()), entry.getValue());
                                    }
                                }
                                return null;
                            }
                            if ("l".equals(name)) {
                                return new java.util.HashMap<String, Object>(config);
                            }
                            if ("g".equals(name)) {
                                return java.util.Locale.US;
                            }
                            if ("o".equals(name)) {
                                return java.text.NumberFormat.getCurrencyInstance(java.util.Locale.US);
                            }
                            String key = args != null && args.length > 0 && args[0] != null
                                    ? String.valueOf(args[0]) : "";
                            if ("v".equals(name) || "t".equals(name)) {
                                Object value = config.get(key);
                                if (value != null) {
                                    log("I", "MCD_CONFIG_GET method=" + name + " key=" + key
                                            + " value=" + mcdBoundaryValueSummary(value));
                                    return value;
                                }
                                if ("user_interface_build.applicationmenu".equals(key)
                                        || "user_interface.applicationmenu".equals(key)) {
                                    Object menu = newMcdApplicationMenu(cl);
                                    log("I", "MCD_CONFIG_GET method=" + name + " key=" + key
                                            + " value=" + mcdBoundaryValueSummary(menu));
                                    return menu;
                                }
                                if ("markets".equals(key)) {
                                    java.util.ArrayList<Object> fallbackMarkets =
                                            new java.util.ArrayList<>();
                                    fallbackMarkets.add(newMcdMarketMap(cl));
                                    log("I", "MCD_CONFIG_GET method=" + name + " key=" + key
                                            + " value=" + mcdBoundaryValueSummary(fallbackMarkets));
                                    return fallbackMarkets;
                                }
                                log("I", "MCD_CONFIG_GET method=" + name + " key=" + key
                                        + " value=null");
                                return null;
                            }
                            if ("u".equals(name) || "k".equals(name) || "m".equals(name)
                                    || "n".equals(name)) {
                                Object value = config.get(key);
                                return value != null ? String.valueOf(value) : "";
                            }
                            if ("f".equals(name)) {
                                return "en-US";
                            }
                            if ("w".equals(name) || "x".equals(name)) {
                                return "US";
                            }
                            if ("A".equals(name) || "q".equals(name)) {
                                Object value = config.get(key);
                                return Boolean.valueOf(value instanceof Boolean
                                        && ((Boolean) value).booleanValue());
                            }
                            if ("p".equals(name)) {
                                Object value = config.get(key);
                                return Integer.valueOf(value instanceof Number
                                        ? ((Number) value).intValue()
                                        : 0);
                            }
                            if ("z".equals(name)) {
                                Object value = config.get(key);
                                return Long.valueOf(value instanceof Number
                                        ? ((Number) value).longValue()
                                        : 0L);
                            }
                            if ("r".equals(name)) {
                                Object value = config.get(key);
                                return Double.valueOf(value instanceof Number
                                        ? ((Number) value).doubleValue()
                                        : 0d);
                            }
                            return defaultMcdProxyReturn(method.getReturnType(), cl,
                                    "AppConfiguration." + name);
                        }
                    });

            java.lang.reflect.Field field = manager.getDeclaredField("a");
            field.setAccessible(true);
            field.set(null, proxy);
            log("I", "McD AppConfigurationManager seeded with offline US market config");
        } catch (Throwable t) {
            log("W", "McD AppConfigurationManager seed failed: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    private static java.util.List<Object> newMcdApplicationMenu(ClassLoader cl) {
        return new java.util.ArrayList<Object>();
    }

    @SuppressWarnings("unchecked")
    private static java.util.Map<Object, Object> newMcdLinkedTreeMap(ClassLoader cl) {
        try {
            Class<?> linkedTreeMap = cl.loadClass("com.google.gson.internal.LinkedTreeMap");
            Object instance = linkedTreeMap.getDeclaredConstructor().newInstance();
            if (instance instanceof java.util.Map) {
                return (java.util.Map<Object, Object>) instance;
            }
        } catch (Throwable ignored) {
        }
        return new java.util.HashMap<Object, Object>();
    }

    private static java.util.ArrayList<Object> newMcdHomeDashboardSections(ClassLoader cl) {
        java.util.ArrayList<Object> sections = new java.util.ArrayList<>();
        sections.add(newMcdHomeDashboardSection(cl, "DEALS", true));
        sections.add(newMcdHomeDashboardSection(cl, "MENU", true));
        sections.add(newMcdHomeDashboardSection(cl, "POPULAR", false));
        return sections;
    }

    private static Object newMcdHomeDashboardSection(ClassLoader cl, String name, boolean enabled) {
        java.util.Map<Object, Object> section = newMcdLinkedTreeMap(cl);
        section.put("Name", name);
        section.put("Enabled", Boolean.valueOf(enabled));
        return section;
    }

    private static Object newMcdAppFeatureAppParameter(ClassLoader cl) {
        java.util.Map<Object, Object> root = newMcdLinkedTreeMap(cl);
        Object config = newMcdOperationModeConfig(cl);
        root.put("BOTH", config);
        root.put("US", config);
        root.put("usdap_prod", config);
        root.put("default", config);
        root.put("string_2132085665", config);
        return root;
    }

    private static Object newMcdOperationModeConfig(ClassLoader cl) {
        java.util.Map<Object, Object> config = newMcdLinkedTreeMap(cl);
        java.util.ArrayList<Object> modes = new java.util.ArrayList<>();
        java.util.Map<Object, Object> mode = newMcdLinkedTreeMap(cl);
        mode.put("Name", "operationMode");
        mode.put("Value", "3");
        modes.add(mode);
        config.put("modes", modes);
        return config;
    }

    private static String mcdBoundaryValueSummary(Object value) {
        if (value == null) {
            return "null";
        }
        try {
            if (value instanceof java.util.Collection) {
                return value.getClass().getName() + "[size="
                        + ((java.util.Collection<?>) value).size() + "]";
            }
            if (value instanceof java.util.Map) {
                return value.getClass().getName() + "[size="
                        + ((java.util.Map<?, ?>) value).size() + "]";
            }
            if (value instanceof CharSequence || value instanceof Number
                    || value instanceof Boolean) {
                return String.valueOf(value);
            }
            return value.getClass().getName();
        } catch (Throwable ignored) {
            return value.getClass().getName();
        }
    }

    static void seedMcdonaldsJustFlipState(final ClassLoader cl) {
        if (cl == null) {
            return;
        }
        try {
            Class<?> justFlipClass = cl.loadClass("com.mcdonalds.justflip_kmm.JustFlip");
            Class<?> resolverClass =
                    cl.loadClass("com.mcdonalds.justflip_kmm.flag_providers.FlagResolver");
            Object resolver = java.lang.reflect.Proxy.newProxyInstance(
                    resolverClass.getClassLoader() != null ? resolverClass.getClassLoader() : cl,
                    new Class<?>[] { resolverClass },
                    new java.lang.reflect.InvocationHandler() {
                        public Object invoke(Object proxy, java.lang.reflect.Method method,
                                Object[] args) {
                            String name = method.getName();
                            if ("toString".equals(name)) return "WestlakeJustFlipResolver";
                            if ("hashCode".equals(name)) return Integer.valueOf(0);
                            if ("equals".equals(name)) {
                                return Boolean.valueOf(proxy == (args != null && args.length > 0
                                        ? args[0] : null));
                            }
                            if ("getFlagWithAttributes".equals(name)) {
                                String flagKey = args != null && args.length > 0
                                        && args[0] != null ? String.valueOf(args[0]) : "";
                                log("I", "MCD_JUSTFLIP_FLAG method=" + name + " key="
                                        + flagKey + " value=off");
                                return "off";
                            }
                            if ("getFlagWithConfiguration".equals(name)) {
                                String flagKey = args != null && args.length > 0
                                        && args[0] != null ? String.valueOf(args[0]) : "";
                                String override = mcdJustFlipConfigOverride(flagKey);
                                log("I", "MCD_JUSTFLIP_FLAG method=" + name + " key="
                                        + flagKey + " value="
                                        + (override != null ? override : "null"));
                                return override;
                            }
                            return defaultMcdProxyReturn(method.getReturnType(), cl,
                                    "JustFlipResolver." + name);
                        }
                    });
            final Object resolverRef = resolver;
            java.util.Map<Object, Object> resolvers =
                    new java.util.AbstractMap<Object, Object>() {
                        public Object get(Object key) {
                            return resolverRef;
                        }

                        public boolean containsKey(Object key) {
                            return true;
                        }

                        public java.util.Set<java.util.Map.Entry<Object, Object>> entrySet() {
                            return java.util.Collections.emptySet();
                        }
                    };
            java.lang.reflect.Constructor<?> ctor = justFlipClass.getDeclaredConstructor(
                    java.util.Map.class, String.class, java.util.List.class);
            ctor.setAccessible(true);
            Object justFlip = ctor.newInstance(resolvers, "westlake-offline",
                    java.util.Collections.emptyList());

            boolean seeded = false;
            try {
                java.lang.reflect.Method setter = justFlipClass.getDeclaredMethod(
                        "k", justFlipClass);
                setter.setAccessible(true);
                setter.invoke(null, justFlip);
                seeded = true;
            } catch (Throwable ignored) {
            }
            try {
                java.lang.reflect.Field singleton = justFlipClass.getDeclaredField("o");
                singleton.setAccessible(true);
                if (putStaticObjectFieldUnsafe(singleton, justFlip)
                        || setStaticObjectField(singleton, justFlip)) {
                    seeded = true;
                }
            } catch (Throwable t) {
                log("D", "McD JustFlip singleton field seed skipped: "
                        + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
            if (seeded) {
                log("I", "McD JustFlip singleton seeded with offline flag resolver");
            }
        } catch (ClassNotFoundException ignored) {
        } catch (Throwable t) {
            log("W", "McD JustFlip seed failed: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    private static String mcdJustFlipConfigOverride(String flagKey) {
        if (flagKey == null) {
            return null;
        }
        if ("maxQtyOnBasket".equals(flagKey)) {
            return "{\"maxQtyOnBasket\":99}";
        }
        if ("maxItemQuantity".equals(flagKey)) {
            return "{\"maxItemQuantity\":99}";
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static java.util.Map<String, Object> newMcdMarketMap(ClassLoader cl) {
        java.util.Map<String, Object> market = null;
        try {
            Class<?> linkedTreeMap = cl.loadClass("com.google.gson.internal.LinkedTreeMap");
            Object instance = linkedTreeMap.getDeclaredConstructor().newInstance();
            if (instance instanceof java.util.Map) {
                market = (java.util.Map<String, Object>) instance;
            }
        } catch (Throwable ignored) {
        }
        if (market == null) {
            market = new java.util.HashMap<>();
        }
        java.util.ArrayList<String> languages = new java.util.ArrayList<>();
        languages.add("en-US");
        languages.add("en");
        market.put("marketId", "usdap_prod");
        market.put("market", "US");
        market.put("country", "US");
        market.put("language", "en-US");
        market.put("languageSupported", languages);
        market.put("applicationName", "McDonald's");
        market.put("applicationVersion", "7.0.0");
        market.put("clientId", "westlake-offline");
        market.put("clientSecret", "westlake-offline");
        market.put("authUrl", "https://us-prod.api.mcd.com/v1/security/auth/token");
        market.put("baseUrl", "https://us-prod.api.mcd.com/exp/v1/");
        return market;
    }

    private static void seedMcdonaldsSdkCoreReady(ClassLoader cl, Context context) {
        if (cl == null || context == null) {
            return;
        }
        try {
            Class<?> coreManager =
                    cl.loadClass("com.mcdonalds.androidsdk.core.internal.CoreManager");
            java.lang.reflect.Field contextField = coreManager.getDeclaredField("b");
            contextField.setAccessible(true);
            if (contextField.get(null) == null) {
                contextField.set(null, context);
                log("I", "McD CoreManager context seeded for SDKManager.t()");
            }
            seedMcdonaldsSdkPersistenceState(cl);
        } catch (Throwable t) {
            log("W", "McD CoreManager seed failed: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    private static void seedMcdonaldsSdkPersistenceState(ClassLoader cl) {
        if (cl == null) {
            return;
        }
        try {
            Class<?> coreManager =
                    cl.loadClass("com.mcdonalds.androidsdk.core.internal.CoreManager");
            Class<?> settingsClass =
                    cl.loadClass("com.mcdonalds.androidsdk.core.configuration.model.SDKSettings");
            Class<?> modulesClass =
                    cl.loadClass("com.mcdonalds.androidsdk.core.configuration.model.ModuleConfigurations");
            Class<?> persistenceClass =
                    cl.loadClass("com.mcdonalds.androidsdk.core.configuration.model.PersistenceConfiguration");

            Object settings = null;
            try {
                java.lang.reflect.Method getSettings = coreManager.getDeclaredMethod("k");
                getSettings.setAccessible(true);
                settings = getSettings.invoke(null);
            } catch (Throwable ignored) {
            }
            if (settings == null) {
                java.lang.reflect.Constructor<?> ctor = settingsClass.getDeclaredConstructor();
                ctor.setAccessible(true);
                settings = ctor.newInstance();
                java.lang.reflect.Method setSettings =
                        coreManager.getDeclaredMethod("v", settingsClass);
                setSettings.setAccessible(true);
                setSettings.invoke(null, settings);
            }

            Object modules = null;
            try {
                java.lang.reflect.Method getModules =
                        settingsClass.getDeclaredMethod("getModuleConfigurations");
                getModules.setAccessible(true);
                modules = getModules.invoke(settings);
            } catch (Throwable ignored) {
            }
            if (modules == null) {
                java.lang.reflect.Constructor<?> ctor = modulesClass.getDeclaredConstructor();
                ctor.setAccessible(true);
                modules = ctor.newInstance();
                try {
                    java.lang.reflect.Method setModules =
                            settingsClass.getDeclaredMethod("setModuleConfigurations", modulesClass);
                    setModules.setAccessible(true);
                    setModules.invoke(settings, modules);
                } catch (Throwable directFailure) {
                    java.lang.reflect.Field modulesField =
                            settingsClass.getDeclaredField("moduleConfigurations");
                    modulesField.setAccessible(true);
                    modulesField.set(settings, modules);
                }
            }

            Object persistence = null;
            try {
                java.lang.reflect.Method getPersistence =
                        modulesClass.getDeclaredMethod("getPersistence");
                getPersistence.setAccessible(true);
                persistence = getPersistence.invoke(modules);
            } catch (Throwable ignored) {
            }
            if (persistence == null) {
                java.lang.reflect.Constructor<?> ctor =
                        persistenceClass.getDeclaredConstructor();
                ctor.setAccessible(true);
                persistence = ctor.newInstance();
                java.lang.reflect.Field persistenceField =
                        modulesClass.getDeclaredField("persistence");
                persistenceField.setAccessible(true);
                persistenceField.set(modules, persistence);
            }

            try {
                java.lang.reflect.Field encrypted =
                        persistenceClass.getDeclaredField("encrypted");
                encrypted.setAccessible(true);
                encrypted.setBoolean(persistence, false);
            } catch (Throwable ignored) {
            }
            seedMcdonaldsSdkOrderingConfiguration(cl, modules);
            seedMcdonaldsSdkAccountViewProfileEndpoint(cl);
            log("I", "McD SDK persistence config seeded encrypted=false");
        } catch (ClassNotFoundException ignored) {
        } catch (Throwable t) {
            log("W", "McD SDK persistence seed failed: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    private static void seedMcdonaldsSdkOrderingConfiguration(ClassLoader cl, Object modules) {
        if (cl == null || modules == null) {
            return;
        }
        try {
            Object ordering = null;
            try {
                java.lang.reflect.Method getOrdering =
                        modules.getClass().getDeclaredMethod("getOrdering");
                getOrdering.setAccessible(true);
                ordering = getOrdering.invoke(modules);
            } catch (Throwable ignored) {
            }
            if (ordering == null) {
                Class<?> orderingClass = cl.loadClass(
                        "com.mcdonalds.androidsdk.core.configuration.model.OrderingConfiguration");
                ordering = orderingClass.getDeclaredConstructor().newInstance();
                setMcdObjectField(modules, "ordering", ordering);
            }
            setMcdObjectField(ordering, "cartResponseTTL", Long.valueOf(1800L));
            setMcdObjectField(ordering, "maxCachedOrders", Integer.valueOf(10));
            setMcdObjectField(ordering, "maxCachedRestaurantCatalog", Integer.valueOf(10));
            setMcdObjectField(ordering, "catalogSnsTopic", "");
            log("I", "McD SDK ordering config seeded cartResponseTTL=1800");
        } catch (Throwable t) {
            log("W", "McD SDK ordering config seed failed: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    private static void seedMcdonaldsSdkAccountViewProfileEndpoint(ClassLoader cl) {
        if (cl == null) {
            return;
        }
        try {
            Class<?> accountModuleClass =
                    cl.loadClass("com.mcdonalds.androidsdk.account.module.AccountModule");
            java.lang.reflect.Method getModule = accountModuleClass.getDeclaredMethod("e");
            getModule.setAccessible(true);
            Object module = getModule.invoke(null);
            Object api = ensureMcdSdkApiConfiguration(cl, module, "account");
            if (api == null) {
                return;
            }
            java.util.List<Object> endpoints = ensureMcdSdkEndpointList(api);
            if (!hasMcdSdkEndpoint(endpoints, "viewProfile")) {
                endpoints.add(newMcdSdkEndpoint(cl, "viewProfile",
                        "customer/profile", "GET"));
            }
            java.lang.reflect.Method setConfig = cl.loadClass(
                    "com.mcdonalds.androidsdk.configuration.factory.ConfigurationModule")
                    .getDeclaredMethod("b", cl.loadClass(
                            "com.mcdonalds.androidsdk.core.configuration.model.ApiConfiguration"));
            setConfig.setAccessible(true);
            setConfig.invoke(module, api);
            log("I", "McD SDK account endpoint seeded viewProfile");
        } catch (ClassNotFoundException ignored) {
        } catch (Throwable t) {
            log("W", "McD SDK account endpoint seed failed: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    private static Object ensureMcdSdkApiConfiguration(ClassLoader cl, Object module,
            String moduleName) throws Exception {
        if (cl == null || module == null) {
            return null;
        }
        Class<?> configurationModule =
                cl.loadClass("com.mcdonalds.androidsdk.configuration.factory.ConfigurationModule");
        java.lang.reflect.Field configField = configurationModule.getDeclaredField("a");
        configField.setAccessible(true);
        Object api = configField.get(module);
        if (api == null) {
            Class<?> apiClass = cl.loadClass(
                    "com.mcdonalds.androidsdk.core.configuration.model.ApiConfiguration");
            api = apiClass.getDeclaredConstructor().newInstance();
            configField.set(module, api);
        }
        setMcdObjectField(api, "name", moduleName);
        setMcdObjectField(api, "baseURL", "https://us-prod.api.mcd.com/exp/v1/");
        return api;
    }

    @SuppressWarnings("unchecked")
    private static java.util.List<Object> ensureMcdSdkEndpointList(Object api)
            throws Exception {
        java.lang.reflect.Field endpointsField =
                findMcdField(api.getClass(), "endPoints");
        endpointsField.setAccessible(true);
        Object endpoints = endpointsField.get(api);
        if (!(endpoints instanceof java.util.List)) {
            endpoints = new java.util.ArrayList<Object>();
            endpointsField.set(api, endpoints);
        }
        return (java.util.List<Object>) endpoints;
    }

    private static boolean hasMcdSdkEndpoint(java.util.List<Object> endpoints, String name) {
        if (endpoints == null || name == null) {
            return false;
        }
        for (Object endpoint : endpoints) {
            if (endpoint == null) {
                continue;
            }
            try {
                java.lang.reflect.Method getName = endpoint.getClass().getDeclaredMethod("getName");
                getName.setAccessible(true);
                Object value = getName.invoke(endpoint);
                if (name.equals(value)) {
                    return true;
                }
            } catch (Throwable ignored) {
            }
        }
        return false;
    }

    private static Object newMcdSdkEndpoint(ClassLoader cl, String name, String path,
            String method) throws Exception {
        Class<?> endpointClass = cl.loadClass(
                "com.mcdonalds.androidsdk.core.configuration.model.EndPointSetting");
        Object endpoint = endpointClass.getDeclaredConstructor().newInstance();
        setMcdObjectField(endpoint, "name", name);
        setMcdObjectField(endpoint, "path", path);
        setMcdObjectField(endpoint, "method", method);
        setMcdObjectField(endpoint, "akamaiEnabled", Boolean.FALSE);
        return endpoint;
    }

    private static boolean setMcdObjectField(Object target, String fieldName, Object value) {
        if (target == null || fieldName == null) {
            return false;
        }
        try {
            java.lang.reflect.Field field = findMcdField(target.getClass(), fieldName);
            field.setAccessible(true);
            field.set(target, value);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static java.lang.reflect.Field findMcdField(Class<?> type, String fieldName)
            throws NoSuchFieldException {
        Class<?> current = type;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }

    private static void seedMcdonaldsClickstreamState(ClassLoader cl, Application app) {
        if (cl == null) {
            return;
        }
        try {
            ClassLoader prior = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(cl);
            try {
                Class<?> helperClass =
                        cl.loadClass("com.mcdonalds.mcdcoreapp.analytics.ClickstreamDataHelper");
                int seeded = 0;
                for (java.lang.reflect.Field f : safeGetDeclaredFields(helperClass)) {
                    if (!java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
                    f.setAccessible(true);
                    if (f.get(null) != null) continue;
                    Class<?> fType = f.getType();
                    Object value = defaultMcdProxyReturn(fType, cl,
                            "ClickstreamDataHelper." + f.getName());
                    if (value != null) {
                        f.set(null, value);
                        seeded++;
                    }
                }

                Object domain = null;
                try {
                    java.lang.reflect.Method getDomain = helperClass.getDeclaredMethod("u");
                    getDomain.setAccessible(true);
                    domain = getDomain.invoke(null);
                    log("I", "ClickstreamDataHelper.u() seeded: "
                            + (domain != null ? domain.getClass().getName() : "null"));
                } catch (Throwable t) {
                    log("W", "ClickstreamDataHelper.u() verify failed: "
                            + t.getClass().getSimpleName() + ": " + t.getMessage());
                }

                seedMcdonaldsAnalyticsProvider(cl, app, domain);
                log("I", "ClickstreamDataHelper seeded fields=" + seeded);
            } finally {
                Thread.currentThread().setContextClassLoader(prior);
            }
        } catch (Throwable t) {
            log("W", "ClickstreamDataHelper seed failed: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    private static void seedMcdonaldsAnalyticsProvider(ClassLoader cl, Application app,
            Object domain) {
        if (cl == null || domain == null) {
            return;
        }
        try {
            Class<?> providerHelper =
                    cl.loadClass("com.mcdonalds.analytics.utils.AnalyticsDataProviderHelper");
            Class<?> providerType =
                    cl.loadClass("com.mcdonalds.analytics.interfaces.McDMParticleWrapperProvider");
            Object provider = java.lang.reflect.Proxy.newProxyInstance(
                    providerType.getClassLoader() != null ? providerType.getClassLoader() : cl,
                    new Class<?>[] { providerType },
                    new java.lang.reflect.InvocationHandler() {
                        public Object invoke(Object proxy, java.lang.reflect.Method method,
                                Object[] args) {
                            if ("toString".equals(method.getName())) {
                                return "WestlakeMcDMParticleWrapperProvider";
                            }
                            if ("hashCode".equals(method.getName())) return Integer.valueOf(0);
                            if ("equals".equals(method.getName())) {
                                return Boolean.valueOf(proxy == (args != null && args.length > 0
                                        ? args[0] : null));
                            }
                            if ("a".equals(method.getName())
                                    && method.getParameterTypes().length == 0) {
                                return domain;
                            }
                            return defaultMcdProxyReturn(method.getReturnType(), cl,
                                    "AnalyticsDataProviderHelper." + method.getName());
                        }
                    });

            int seeded = 0;
            for (java.lang.reflect.Field f : safeGetDeclaredFields(providerHelper)) {
                if (!java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
                f.setAccessible(true);
                if (providerType.isAssignableFrom(f.getType())) {
                    f.set(null, provider);
                    seeded++;
                }
            }
            if (seeded > 0) {
                log("I", "AnalyticsDataProviderHelper provider seeded fields=" + seeded);
            }
        } catch (Throwable t) {
            log("D", "AnalyticsDataProviderHelper seed skipped: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    private static void seedMcdonaldsDeepLinkRouter(ClassLoader cl) {
        if (cl == null) {
            return;
        }
        try {
            ClassLoader prior = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(cl);
            try {
                Class<?> jsonClass = cl.loadClass("org.json.JSONObject");
                Object root = jsonClass.getDeclaredConstructor().newInstance();
                Object routes = jsonClass.getDeclaredConstructor().newInstance();
                Object defaultRoute = jsonClass.getDeclaredConstructor().newInstance();
                java.lang.reflect.Method put = jsonClass.getMethod(
                        "put", String.class, Object.class);
                put.invoke(root, "routes", routes);
                put.invoke(root, "defaultRoute", defaultRoute);

                Class<?> routerClass = cl.loadClass(
                        "com.mcdonalds.mcdcoreapp.config.deeplink.DeepLinkRouterObject");
                Object router = routerClass.getDeclaredMethod("b").invoke(null);
                java.lang.reflect.Method set = routerClass.getDeclaredMethod("c", jsonClass);
                set.setAccessible(true);
                set.invoke(router, root);
                log("I", "DeepLinkRouterObject seeded with empty routes/defaultRoute");
            } finally {
                Thread.currentThread().setContextClassLoader(prior);
            }
        } catch (Throwable t) {
            log("W", "DeepLinkRouterObject seed failed: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    private void ensureNamedComponentActivitySavedStateReady(Activity activity) {
        if (activity == null) {
            return;
        }
        if (isMinimalSplashActivity(activity)) {
            return;
        }
        try {
            WestlakeLauncher.noteMarker("CV WAT precreate ensure class before");
            Class<?> componentActivityClass =
                    findNamedClassOnHierarchy(activity.getClass(), "androidx.activity.ComponentActivity");
            WestlakeLauncher.noteMarker(componentActivityClass != null
                    ? "CV WAT precreate ensure class nonnull"
                    : "CV WAT precreate ensure class null");
            if (componentActivityClass == null) {
                return;
            }
            if (ensureNamedSavedStateViaOwnerContract(activity, componentActivityClass)) {
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

    private boolean ensureNamedSavedStateViaOwnerContract(Activity activity,
            Class<?> componentActivityClass) {
        Object registry = null;
        try {
            WestlakeLauncher.noteMarker("CV WAT precreate owner begin");
            WestlakeLauncher.noteMarker("CV WAT precreate owner direct instanceof before");
            if (activity instanceof androidx.savedstate.SavedStateRegistryOwner) {
                WestlakeLauncher.noteMarker("CV WAT precreate owner direct get before");
                registry = ((androidx.savedstate.SavedStateRegistryOwner) activity)
                        .getSavedStateRegistry();
                WestlakeLauncher.noteMarker(registry != null
                        ? "CV WAT precreate owner direct get nonnull"
                        : "CV WAT precreate owner direct get null");
            } else {
                WestlakeLauncher.noteMarker("CV WAT precreate owner direct instanceof false");
            }
            if (registry == null) {
                WestlakeLauncher.noteMarker("CV WAT precreate owner invoke get before");
                registry = invokeNoArgIfPresent(activity, componentActivityClass,
                        "getSavedStateRegistry");
                WestlakeLauncher.noteMarker(registry != null
                        ? "CV WAT precreate owner invoke get nonnull"
                        : "CV WAT precreate owner invoke get null");
            }
            if (registry == null) {
                return false;
            }
            WestlakeLauncher.noteMarker("CV WAT precreate owner ready");
            return true;
        } catch (Throwable t) {
            WestlakeLauncher.dumpThrowable(
                    "[WestlakeActivityThread] saved-state owner contract", t);
            return false;
        }
    }

    private void probeNamedComponentActivitySavedStateReady(Activity activity, int maxStep) {
        WestlakeLauncher.noteMarker("CV WAT precreate probe begin level=" + maxStep);
        if (activity == null) {
            WestlakeLauncher.noteMarker("CV WAT precreate probe null activity");
            return;
        }
        if (isMinimalSplashActivity(activity)) {
            WestlakeLauncher.noteMarker("CV WAT precreate probe minimal splash");
            return;
        }
        try {
            WestlakeLauncher.noteMarker("CV WAT precreate probe step1 before class");
            Class<?> componentActivityClass =
                    findNamedClassOnHierarchy(activity.getClass(), "androidx.activity.ComponentActivity");
            WestlakeLauncher.noteMarker(componentActivityClass != null
                    ? "CV WAT precreate probe step1 after class"
                    : "CV WAT precreate probe step1 class null");
            if (maxStep <= 1 || componentActivityClass == null) {
                return;
            }

            if (maxStep >= 20 && maxStep < 30) {
                probeNamedFieldRead(activity, componentActivityClass,
                        "savedStateRegistryController", maxStep);
                return;
            }

            WestlakeLauncher.noteMarker("CV WAT precreate probe step2 before get controller");
            Object controller = getNamedField(activity, componentActivityClass,
                    "savedStateRegistryController");
            WestlakeLauncher.noteMarker(controller != null
                    ? "CV WAT precreate probe step2 controller nonnull"
                    : "CV WAT precreate probe step2 controller null");
            if (maxStep <= 2) {
                return;
            }

            if (controller == null) {
                WestlakeLauncher.noteMarker("CV WAT precreate probe step3 before loader");
                ClassLoader loader = chooseClassLoader(componentActivityClass);
                WestlakeLauncher.noteMarker(loader != null
                        ? "CV WAT precreate probe step3 loader nonnull"
                        : "CV WAT precreate probe step3 loader null");
                if (maxStep <= 3) {
                    return;
                }

                WestlakeLauncher.noteMarker("CV WAT precreate probe step4 before new controller");
                controller = newSavedStateRegistryController(loader, activity);
                WestlakeLauncher.noteMarker(controller != null
                        ? "CV WAT precreate probe step4 controller nonnull"
                        : "CV WAT precreate probe step4 controller null");
                if (maxStep <= 4) {
                    return;
                }

                if (controller != null) {
                    WestlakeLauncher.noteMarker("CV WAT precreate probe step5 before set");
                    setNamedField(activity, componentActivityClass, "savedStateRegistryController",
                            controller);
                    WestlakeLauncher.noteMarker("CV WAT precreate probe step5 after set");
                    if (maxStep <= 5) {
                        return;
                    }

                    WestlakeLauncher.noteMarker("CV WAT precreate probe step6 before readback");
                    controller = getNamedField(activity, componentActivityClass,
                            "savedStateRegistryController");
                    WestlakeLauncher.noteMarker(controller != null
                            ? "CV WAT precreate probe step6 readback nonnull"
                            : "CV WAT precreate probe step6 readback null");
                    if (maxStep <= 6) {
                        return;
                    }
                }
            }

            if (controller != null) {
                WestlakeLauncher.noteMarker("CV WAT precreate probe step7 before attach");
                invokeNoArgIfPresent(controller, "c", "performAttach");
                WestlakeLauncher.noteMarker("CV WAT precreate probe step7 after attach");
                if (maxStep <= 7) {
                    return;
                }

                WestlakeLauncher.noteMarker("CV WAT precreate probe step8 before enable");
                enableNamedSavedStateHandles(chooseClassLoader(controller.getClass()), activity);
                WestlakeLauncher.noteMarker("CV WAT precreate probe step8 after enable");
            }
            WestlakeLauncher.noteMarker("CV WAT precreate probe end");
        } catch (Throwable t) {
            WestlakeLauncher.noteMarker("CV WAT precreate probe throwable "
                    + throwableSummary(t));
            WestlakeLauncher.dumpThrowable(
                    "[WestlakeActivityThread] probeNamedComponentActivitySavedStateReady", t);
        }
    }

    private void probeNamedFieldRead(Object target, Class<?> owner, String fieldName,
            int maxStep) throws Exception {
        WestlakeLauncher.noteMarker("CV WAT field probe begin level=" + maxStep);
        WestlakeLauncher.noteMarker("CV WAT field probe step21 before find");
        java.lang.reflect.Field field = findFieldOnHierarchy(owner, fieldName);
        WestlakeLauncher.noteMarker(field != null
                ? "CV WAT field probe step21 field nonnull"
                : "CV WAT field probe step21 field null");
        if (maxStep <= 21 || field == null) {
            return;
        }

        WestlakeLauncher.noteMarker("CV WAT field probe step22 before accessible");
        field.setAccessible(true);
        WestlakeLauncher.noteMarker("CV WAT field probe step22 after accessible");
        if (maxStep <= 22) {
            return;
        }

        WestlakeLauncher.noteMarker("CV WAT field probe step23 before get");
        Object value = field.get(target);
        WestlakeLauncher.noteMarker(value != null
                ? "CV WAT field probe step23 value nonnull"
                : "CV WAT field probe step23 value null");
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
     * V2 Step 6: WestlakeActivity owns attach() with 6-arg overload (Step 2 added
     * the 6-arg variant alongside the AOSP 18-arg). No reflection, no try/catch,
     * no field-set fallback path. Activity.attach() IS our code via classpath
     * shadowing (framework_duplicates.txt removed android/app/Activity).
     *
     * V1 deletions:
     *   - strictStandalone / runMcdonaldsLifecycle / strictSkipLifecycle flags
     *   - shimAttachOk try/catch + NoSuchMethodError detection
     *   - setInstanceField(activity, ContextWrapper.class, "mBase", ...) reflection
     *   - setInstanceField(activity, Activity.class, "mApplication"/"mIntent"/
     *     "mComponent"/"mFinished"/"mDestroyed", ...) reflection
     *   - wireStandaloneActivityResources (V1 hack — Step 4's WestlakeResources is
     *     now plumbed via activity.getResources())
     *   - PF301 strict markers (V2 doesn't have strict/control modes)
     *   - ensureActivityWindow + initializeAndroidxActivityState helpers
     */
    private void attachActivity(Activity activity, Context baseContext,
                                 Application app, Intent intent,
                                 ComponentName component) {
        WestlakeLauncher.trace("[WestlakeActivityThread] V2 attachActivity begin: "
                + activity.getClass().getName());
        activity.attach(baseContext, app, intent, component, /*window*/ null, mInstrumentation);
        WestlakeLauncher.trace("[WestlakeActivityThread] V2 attachActivity done");
    }

    private boolean isMinimalSplashActivity(Activity activity) {
        if (activity == null) {
            return false;
        }
        String activityName = activity.getClass().getName();
        return activityName != null
                && activityName.endsWith("SplashActivity")
                && !isMcdonaldsPackageOrClass(activityName);
    }

    private boolean isDashboardHostActivity(Activity activity) {
        if (activity == null) {
            return false;
        }
        String activityName = activity.getClass().getName();
        return activityName != null && activityName.endsWith("HomeDashboardActivity");
    }

    private boolean initializeNamedCoreComponentActivityState(Activity activity) throws Exception {
        Class<?> coreActivityClass =
                findNamedClassOnHierarchy(activity.getClass(), "androidx.core.app.ComponentActivity");
        if (coreActivityClass == null) {
            return false;
        }
        ClassLoader loader = chooseClassLoader(coreActivityClass);
        WestlakeLauncher.trace("[WestlakeActivityThread] AndroidX core init lifecycle");
        Object lifecycleRegistry = newNamedLifecycleRegistry(loader, activity,
                "[WestlakeActivityThread] AndroidX core lifecycle registry");
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
        WestlakeLauncher.trace("[WestlakeActivityThread] AndroidX init saved state");
        Object savedStateController = newSavedStateRegistryController(loader, activity);
        if (savedStateController == null) {
            log("I", "Skipping partial AndroidX component state for "
                    + componentActivityClass.getName());
            return false;
        }
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
        seedNamedComponentActivityConstructorState(activity, componentActivityClass, loader,
                savedStateReadback);
        invokeNoArgIfPresent(savedStateReadback, "c", "performAttach");
        enableNamedSavedStateHandles(chooseClassLoader(savedStateReadback.getClass()), activity);
        log("I", "AndroidX saved-state attach invoked for "
                + componentActivityClass.getName());
        return true;
    }

    private boolean initializeNamedFragmentActivityState(Activity activity) throws Exception {
        if (activity == null) {
            return false;
        }
        if (!isDashboardHostActivity(activity)) {
            log("I", "Skipping AndroidX fragment bootstrap for "
                    + activity.getClass().getName());
            return false;
        }
        Class<?> fragmentActivityClass =
                findNamedClassOnHierarchy(activity.getClass(), "androidx.fragment.app.FragmentActivity");
        if (fragmentActivityClass == null) {
            return false;
        }
        ClassLoader loader = chooseClassLoader(fragmentActivityClass);
        WestlakeLauncher.trace("[WestlakeActivityThread] AndroidX fragment init start "
                + fragmentActivityClass.getName());
        Object controller = getNamedField(activity, fragmentActivityClass, "mFragments");
        if (controller != null) {
            log("I", "Skipping AndroidX fragment bootstrap with preexisting controller "
                    + describeObject(controller));
            return false;
        }
        if (controller == null) {
            controller = newNamedFragmentController(loader, fragmentActivityClass, activity);
            if (controller != null) {
                setNamedField(activity, fragmentActivityClass, "mFragments", controller);
                log("I", "Seeded AndroidX fragment controller for "
                        + fragmentActivityClass.getName());
            }
        }
        if (controller == null) {
            log("W", "AndroidX fragment controller seed unavailable for "
                    + fragmentActivityClass.getName());
            return false;
        }
        WestlakeLauncher.trace("[WestlakeActivityThread] AndroidX fragment controller ready");
        Object lifecycleRegistry = getNamedField(activity, fragmentActivityClass,
                "mFragmentLifecycleRegistry");
        if (lifecycleRegistry == null) {
            lifecycleRegistry = newNamedLifecycleRegistry(loader, activity,
                    "[WestlakeActivityThread] AndroidX fragment lifecycle registry");
            setNamedField(activity, fragmentActivityClass, "mFragmentLifecycleRegistry",
                    lifecycleRegistry);
        }
        WestlakeLauncher.trace("[WestlakeActivityThread] AndroidX fragment lifecycle ready");
        setBooleanNamedField(fragmentActivityClass, activity, "mStopped", true);
        setBooleanNamedField(fragmentActivityClass, activity, "mCreated", false);
        setBooleanNamedField(fragmentActivityClass, activity, "mResumed", false);
        attachNamedFragmentHost(loader, controller);
        log("I", "AndroidX fragment host/controller seeded for "
                + fragmentActivityClass.getName());
        return controller != null || lifecycleRegistry != null;
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

    private void seedNamedComponentActivityConstructorState(Activity activity,
                                                            Class<?> componentActivityClass,
                                                            ClassLoader loader,
                                                            Object savedStateController)
            throws Exception {
        if (activity == null || componentActivityClass == null || loader == null) {
            return;
        }

        Object contextAwareHelper = getNamedField(activity, componentActivityClass,
                "contextAwareHelper");
        if (contextAwareHelper == null) {
            contextAwareHelper = newNamedInstance(loader,
                    "androidx.activity.contextaware.ContextAwareHelper");
            setNamedField(activity, componentActivityClass, "contextAwareHelper",
                    contextAwareHelper);
        }
        // Real ComponentActivity publishes context availability during onCreate().
        // Doing it here causes addOnContextAvailableListener() callbacks from
        // FragmentActivity.init() to fire synchronously while we are still seeding
        // constructor-time host state on an Unsafe-allocated activity.

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

        Object reportFullyDrawnExecutor = getNamedField(activity, componentActivityClass,
                "reportFullyDrawnExecutor");
        if (reportFullyDrawnExecutor == null) {
            reportFullyDrawnExecutor = invokeNoArgIfPresent(activity, componentActivityClass, "M");
            setNamedFieldIfNull(activity, componentActivityClass, "reportFullyDrawnExecutor",
                    reportFullyDrawnExecutor);
        }

        setNamedFieldIfNull(activity, componentActivityClass, "fullyDrawnReporter$delegate",
                newNamedLazy(loader,
                        "androidx.activity.ComponentActivity$fullyDrawnReporter$2",
                        componentActivityClass, activity,
                        "[WestlakeActivityThread] fullyDrawnReporter delegate"));
        setNamedFieldIfNull(activity, componentActivityClass, "defaultViewModelProviderFactory$delegate",
                newNamedLazy(loader,
                        "androidx.activity.ComponentActivity$defaultViewModelProviderFactory$2",
                        componentActivityClass, activity,
                        "[WestlakeActivityThread] defaultViewModelProviderFactory delegate"));
        setNamedFieldIfNull(activity, componentActivityClass, "onBackPressedDispatcher$delegate",
                newNamedLazy(loader,
                        "androidx.activity.ComponentActivity$onBackPressedDispatcher$2",
                        componentActivityClass, activity,
                        "[WestlakeActivityThread] onBackPressedDispatcher delegate"));

        setNamedFieldIfNull(activity, componentActivityClass, "nextLocalRequestCode",
                new java.util.concurrent.atomic.AtomicInteger());
        setNamedFieldIfNull(activity, componentActivityClass, "activityResultRegistry",
                newNamedOwnedInstance(loader,
                        "androidx.activity.ComponentActivity$activityResultRegistry$1",
                        componentActivityClass, activity,
                        "[WestlakeActivityThread] activityResultRegistry"));

        setNamedFieldIfNull(activity, componentActivityClass, "onConfigurationChangedListeners",
                new java.util.concurrent.CopyOnWriteArrayList<>());
        setNamedFieldIfNull(activity, componentActivityClass, "onTrimMemoryListeners",
                new java.util.concurrent.CopyOnWriteArrayList<>());
        setNamedFieldIfNull(activity, componentActivityClass, "onNewIntentListeners",
                new java.util.concurrent.CopyOnWriteArrayList<>());
        setNamedFieldIfNull(activity, componentActivityClass, "onMultiWindowModeChangedListeners",
                new java.util.concurrent.CopyOnWriteArrayList<>());
        setNamedFieldIfNull(activity, componentActivityClass,
                "onPictureInPictureModeChangedListeners",
                new java.util.concurrent.CopyOnWriteArrayList<>());
        setNamedFieldIfNull(activity, componentActivityClass, "onUserLeaveHintListeners",
                new java.util.concurrent.CopyOnWriteArrayList<>());

        if (savedStateController != null) {
            invokeNoArgIfPresent(savedStateController, "c", "performAttach");
        }

        try {
            Class<?> listenerClass = loadNamedClass(loader,
                    "androidx.activity.contextaware.OnContextAvailableListener");
            Object listener = newNamedOwnedInstance(loader, "androidx.activity.e",
                    componentActivityClass, activity,
                    "[WestlakeActivityThread] activity result restore listener");
            if (listenerClass != null && listener != null) {
                invokeSingleArgIfPresent(activity, "addOnContextAvailableListener",
                        listenerClass, listener);
            }
        } catch (Throwable t) {
            WestlakeLauncher.dumpThrowable(
                    "[WestlakeActivityThread] addOnContextAvailableListener replay", t);
        }

        invokeNoArgIfPresent(activity, componentActivityClass, "N");
    }

    private void replayNamedFragmentActivityInit(Activity activity, Class<?> fragmentActivityClass) {
        if (activity == null || fragmentActivityClass == null) {
            return;
        }
        try {
            invokeNoArgIfPresent(activity, fragmentActivityClass, "init");
        } catch (Throwable t) {
            WestlakeLauncher.dumpThrowable(
                    "[WestlakeActivityThread] FragmentActivity.init replay failed", t);
        }
    }

    private Object newNamedFragmentController(ClassLoader loader, Class<?> fragmentActivityClass,
                                              Activity activity) {
        if (loader == null || fragmentActivityClass == null || activity == null) {
            return null;
        }
        try {
            Class<?> hostCallbackClass = loadNamedClass(loader, "androidx.fragment.app.FragmentHostCallback");
            Object hostCallbacks = newNamedInstance(loader,
                    "androidx.fragment.app.FragmentActivity$HostCallbacks",
                    new Class<?>[]{fragmentActivityClass},
                    new Object[]{activity});
            Class<?> controllerClass = loadNamedClass(loader, "androidx.fragment.app.FragmentController");
            java.lang.reflect.Method buildController =
                    findMethodOnHierarchy(controllerClass, "b", hostCallbackClass);
            if (buildController == null || !java.lang.reflect.Modifier.isStatic(buildController.getModifiers())) {
                return null;
            }
            buildController.setAccessible(true);
            return buildController.invoke(null, hostCallbacks);
        } catch (Throwable t) {
            WestlakeLauncher.dumpThrowable(
                    "[WestlakeActivityThread] AndroidX fragment controller seed failed", t);
            if (t instanceof java.lang.reflect.InvocationTargetException) {
                Throwable cause = ((java.lang.reflect.InvocationTargetException) t).getTargetException();
                if (cause != null) {
                    WestlakeLauncher.dumpThrowable(
                            "[WestlakeActivityThread] AndroidX fragment controller seed cause", cause);
                }
            }
            return null;
        }
    }

    private void attachNamedFragmentHost(ClassLoader loader, Object controller) {
        if (loader == null || controller == null) {
            return;
        }
        try {
            java.lang.reflect.Method getManager = findMethodOnHierarchy(controller.getClass(), "l");
            if (getManager == null) {
                return;
            }
            getManager.setAccessible(true);
            Object fragmentManager = getManager.invoke(controller);
            if (fragmentManager == null) {
                return;
            }
            java.lang.reflect.Field hostField = findFieldOnHierarchy(fragmentManager.getClass(), "x");
            Object currentHost = hostField != null ? hostField.get(fragmentManager) : null;
            if (currentHost != null) {
                return;
            }
            Class<?> fragmentClass = loadNamedClass(loader, "androidx.fragment.app.Fragment");
            java.lang.reflect.Method attachHost =
                    findMethodOnHierarchy(controller.getClass(), "a", fragmentClass);
            if (attachHost == null) {
                return;
            }
            attachHost.setAccessible(true);
            attachHost.invoke(controller, new Object[]{null});
            log("I", "AndroidX fragment host attached for "
                    + controller.getClass().getName());
        } catch (Throwable t) {
            WestlakeLauncher.dumpThrowable(
                    "[WestlakeActivityThread] AndroidX fragment host attach failed", t);
        }
    }

    private void setBooleanNamedField(Class<?> owner, Object target, String fieldName, boolean value)
            throws Exception {
        if (owner == null || target == null || fieldName == null) {
            return;
        }
        java.lang.reflect.Field field = findFieldOnHierarchy(owner, fieldName);
        if (field == null) {
            return;
        }
        field.setAccessible(true);
        try {
            field.setBoolean(target, value);
        } catch (Throwable ignored) {
            setFieldValue(target, field, Boolean.valueOf(value));
        }
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

    private Object newNamedLifecycleRegistry(ClassLoader loader, Object owner, String errorLabel) {
        if (loader == null || owner == null) {
            return null;
        }
        try {
            Class<?> lifecycleOwnerClass = loadNamedClass(loader, "androidx.lifecycle.LifecycleOwner");
            return newNamedInstance(loader, "androidx.lifecycle.LifecycleRegistry",
                    new Class<?>[]{lifecycleOwnerClass}, new Object[]{owner});
        } catch (Throwable ctorError) {
            WestlakeLauncher.dumpThrowable(errorLabel + " ctor", ctorError);
        }
        try {
            Class<?> registryClass = loadNamedClass(loader, "androidx.lifecycle.LifecycleRegistry");
            Object registry = com.westlake.engine.WestlakeLauncher.tryAllocInstance(registryClass);
            if (registry == null) {
                return null;
            }
            Object initializedState = loadNamedEnumConstant(loader,
                    "androidx.lifecycle.Lifecycle$State", "INITIALIZED");
            setNamedField(registry, registryClass, "b", Boolean.TRUE);
            setNamedFieldIfNull(registry, registryClass, "c",
                    newNamedInstance(loader, "androidx.arch.core.internal.FastSafeIterableMap"));
            setNamedFieldIfNull(registry, registryClass, "d", initializedState);
            setNamedFieldIfNull(registry, registryClass, "e", new java.lang.ref.WeakReference<>(owner));
            setNamedFieldIfNull(registry, registryClass, "i", new java.util.ArrayList<>());
            setNamedFieldIfNull(registry, registryClass, "j",
                    newNamedMutableStateFlowProxy(loader, initializedState));
            log("I", "Seeded constructor-free AndroidX LifecycleRegistry for "
                    + describeObject(owner));
            return registry;
        } catch (Throwable t) {
            WestlakeLauncher.dumpThrowable(errorLabel + " alloc", t);
            return null;
        }
    }

    private Object loadNamedEnumConstant(ClassLoader loader, String className, String constantName)
            throws Exception {
        Class<?> enumClass = loadNamedClass(loader, className);
        if (enumClass == null || constantName == null) {
            return null;
        }
        @SuppressWarnings({"unchecked", "rawtypes"})
        Object value = java.lang.Enum.valueOf((Class<? extends java.lang.Enum>) enumClass,
                constantName);
        return value;
    }

    private Object newNamedMutableStateFlowProxy(ClassLoader loader, final Object initialValue)
            throws Exception {
        final java.util.concurrent.atomic.AtomicReference<Object> valueRef =
                new java.util.concurrent.atomic.AtomicReference<>(initialValue);
        final Class<?> mutableStateFlowClass =
                loadNamedClass(loader, "kotlinx.coroutines.flow.MutableStateFlow");
        return java.lang.reflect.Proxy.newProxyInstance(
                loader,
                new Class<?>[]{mutableStateFlowClass},
                new java.lang.reflect.InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args)
                            throws Throwable {
                        String name = method.getName();
                        if ("setValue".equals(name)) {
                            if (args != null && args.length > 0) {
                                valueRef.set(args[0]);
                            }
                            return null;
                        }
                        if ("getValue".equals(name)) {
                            return valueRef.get();
                        }
                        if ("compareAndSet".equals(name)) {
                            Object expect = args != null && args.length > 0 ? args[0] : null;
                            Object update = args != null && args.length > 1 ? args[1] : null;
                            return Boolean.valueOf(valueRef.compareAndSet(expect, update));
                        }
                        if ("tryEmit".equals(name)) {
                            if (args != null && args.length > 0) {
                                valueRef.set(args[0]);
                            }
                            return Boolean.TRUE;
                        }
                        if ("emit".equals(name)) {
                            if (args != null && args.length > 0) {
                                valueRef.set(args[0]);
                            }
                            return null;
                        }
                        if ("resetReplayCache".equals(name)) {
                            return null;
                        }
                        if ("getReplayCache".equals(name)) {
                            return java.util.Collections.singletonList(valueRef.get());
                        }
                        if ("toString".equals(name)) {
                            return "WestlakeMutableStateFlow(" + valueRef.get() + ")";
                        }
                        if ("hashCode".equals(name)) {
                            return Integer.valueOf(System.identityHashCode(proxy));
                        }
                        if ("equals".equals(name)) {
                            return Boolean.valueOf(proxy == (args != null && args.length > 0 ? args[0] : null));
                        }
                        Class<?> returnType = method.getReturnType();
                        if (returnType == Boolean.TYPE) {
                            return Boolean.FALSE;
                        }
                        if (returnType == Integer.TYPE) {
                            return Integer.valueOf(0);
                        }
                        if (returnType == Long.TYPE) {
                            return Long.valueOf(0L);
                        }
                        if (returnType == Float.TYPE) {
                            return Float.valueOf(0f);
                        }
                        if (returnType == Double.TYPE) {
                            return Double.valueOf(0d);
                        }
                        return null;
                    }
                });
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
        return WestlakeLauncher.safeGuestFallbackClassLoader();
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

        // Catch both Exception and Error subclasses. NoSuchMethodError from
        // the APK's own bytecode (e.g. KitKat-libcore Locale.forLanguageTag
        // missing inside emoji2.onStart) is an Error, not an Exception —
        // without this widened catch, those NSMEs propagate to the launcher
        // and abort the lifecycle drive entirely. AOSP's production
        // ActivityThread catches Throwable in this path; matching that.
        Throwable startError = null;
        if (r.lifecycleState < ActivityClientRecord.STARTED) {
            try {
                mInstrumentation.callActivityOnStart(activity);
                r.lifecycleState = ActivityClientRecord.STARTED;
            } catch (Throwable t) {
                startError = t;
                log("E", "  performResumeActivity onStart raised: " + t);
                if (t instanceof Exception
                        && mInstrumentation.onException(activity, (Exception) t)) {
                    // swallowed by instrumentation; keep going to onResume.
                    startError = null;
                }
            }
        }

        // Mark resumed before onResume so nested lifecycle calls such as
        // Activity.recreate() observe the same state Android apps expect.
        r.lifecycleState = ActivityClientRecord.RESUMED;
        mResumedRecord = r;
        try {
            mInstrumentation.callActivityOnResume(activity);
            if (findRecord(activity) == r
                    && r.lifecycleState != ActivityClientRecord.DESTROYED) {
                r.lifecycleState = ActivityClientRecord.RESUMED;
                mResumedRecord = r;
                log("I", "  Resumed: " + r.className);
            }
        } catch (Throwable t) {
            log("E", "  performResumeActivity onResume raised: " + t);
            if (t instanceof Exception
                    && mInstrumentation.onException(activity, (Exception) t)) {
                // swallowed; lifecycle remains RESUMED-marked for FragmentManager.
                return;
            }
            // Both Exception (not swallowed) and Error: propagate Exception
            // wrapped, but Errors (NSME etc) shouldn't crash the launcher —
            // log and return so the caller can still drive the draw step.
            if (t instanceof Exception) {
                throw new RuntimeException("Resume failed for " + r.className, t);
            }
            // Error: log + swallow, matching AOSP's tolerance for
            // missing-API errors raised from app code.
        }

        // If onStart raised an unsendable Error (e.g. NSME), surface it AFTER
        // onResume has had its chance — but only as a logged warning, not a
        // thrown RuntimeException. The fragment-lifecycle path treats the
        // Activity as STARTED so FragmentManager.dispatchActivityCreated
        // can run, and the draw step still gets a decor view to render.
        if (startError != null && !(startError instanceof Exception)) {
            log("W", "  performResumeActivity onStart error tolerated: "
                    + startError.getClass().getName() + ": " + startError.getMessage());
            // Promote lifecycle to STARTED so FragmentManager sees a host in
            // STARTED state even though the app's own onStart body threw.
            r.lifecycleState = Math.max(r.lifecycleState, ActivityClientRecord.STARTED);
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

    public void recreateActivity(Activity activity) {
        ActivityClientRecord oldRecord = findRecord(activity);
        if (oldRecord == null) {
            log("W", "recreateActivity: no WAT record for " + activity);
            return;
        }
        if (mInstrumentation == null) {
            log("W", "recreateActivity: instrumentation not attached");
            return;
        }

        WestlakeLauncher.noteMarker("CV WAT recreate begin");
        String className = oldRecord.className;
        String packageName = oldRecord.packageName;
        Intent intent = oldRecord.intent != null ? oldRecord.intent : activity.getIntent();
        ActivityClientRecord caller = oldRecord.caller;
        int requestCode = oldRecord.requestCode;
        Bundle savedState = new Bundle();

        try {
            WestlakeLauncher.noteMarker("CV WAT recreate before save");
            mInstrumentation.callActivityOnSaveInstanceState(activity, savedState);
            oldRecord.savedState = savedState;
            WestlakeLauncher.noteMarker("CV WAT recreate after save");
        } catch (Throwable t) {
            WestlakeLauncher.noteMarker("CV WAT recreate save error");
            if (!mInstrumentation.onException(activity, t)) {
                log("E", "recreateActivity save failed: " + throwableSummary(t));
                return;
            }
        }

        try {
            if (oldRecord.lifecycleState == ActivityClientRecord.RESUMED) {
                WestlakeLauncher.noteMarker("CV WAT recreate before pause");
                mInstrumentation.callActivityOnPause(activity);
                oldRecord.lifecycleState = ActivityClientRecord.PAUSED;
                if (mResumedRecord == oldRecord) {
                    mResumedRecord = null;
                }
                WestlakeLauncher.noteMarker("CV WAT recreate after pause");
            }
            if (oldRecord.lifecycleState != ActivityClientRecord.STOPPED
                    && oldRecord.lifecycleState != ActivityClientRecord.DESTROYED) {
                WestlakeLauncher.noteMarker("CV WAT recreate before stop");
                mInstrumentation.callActivityOnStop(activity);
                oldRecord.lifecycleState = ActivityClientRecord.STOPPED;
                WestlakeLauncher.noteMarker("CV WAT recreate after stop");
            }
            WestlakeLauncher.noteMarker("CV WAT recreate before destroy");
            mInstrumentation.callActivityOnDestroy(activity);
            oldRecord.lifecycleState = ActivityClientRecord.DESTROYED;
            oldRecord.finished = true;
            synchronized (mActivities) {
                mActivities.remove(oldRecord.token);
            }
            WestlakeLauncher.noteMarker("CV WAT recreate after destroy");
        } catch (Throwable t) {
            WestlakeLauncher.noteMarker("CV WAT recreate teardown error");
            if (!mInstrumentation.onException(activity, t)) {
                log("E", "recreateActivity teardown failed: " + throwableSummary(t));
                return;
            }
        }

        WestlakeLauncher.noteMarker("CV WAT recreate before relaunch");
        Activity newActivity = performLaunchActivity(className, packageName, intent, savedState);
        if (newActivity == null) {
            WestlakeLauncher.noteMarker("CV WAT recreate relaunch null");
            return;
        }
        ActivityClientRecord newRecord = findRecord(newActivity);
        if (newRecord != null) {
            newRecord.caller = caller;
            newRecord.requestCode = requestCode;
        }
        WestlakeLauncher.noteMarker("CV WAT recreate after relaunch");
        performResumeActivity(newActivity);
        WestlakeLauncher.noteMarker("CV WAT recreate end");
    }

    // ── Full launch + resume convenience ───────────────────────────────────

    /**
     * Launch an activity and immediately drive it to the RESUMED state.
     * This is what most callers want: create -> start -> resume in one call.
     */
    public Activity launchAndResumeActivity(String className, String packageName,
                                             Intent intent, Bundle savedState) {
        final boolean strictStandalone = !WestlakeLauncher.isRealFrameworkFallbackAllowed();
        if (strictStandalone) {
            WestlakeLauncher.marker("PF301 strict WAT launchAndResume begin");
        }
        Activity activity = performLaunchActivity(className, packageName, intent, savedState);
        if (strictStandalone) {
            WestlakeLauncher.marker("PF301 strict WAT launchAndResume launch returned");
        }
        // M7-Step2 (2026-05-13): production launchers (sForceLifecycleEnabled
        // == true) want full create -> start -> resume. Pre-M7-Step2, only
        // the cutoff-canary lifecycle probe drove resume in strict mode.
        if (activity != null
                && (!strictStandalone
                        || sForceLifecycleEnabled
                        || isCutoffCanaryLifecycleProbe(packageName, className, intent))) {
            performResumeActivity(activity);
        }
        if (strictStandalone) {
            WestlakeLauncher.marker("PF301 strict WAT launchAndResume resume skipped");
            WestlakeLauncher.marker("PF301 strict WAT launchAndResume done");
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
        attachShared(this, packageName, appClassName, classLoader);
    }

    public static void attachStandalone(WestlakeActivityThread thread,
            String packageName, String appClassName, ClassLoader classLoader) {
        attachShared(thread, packageName, appClassName, classLoader);
    }

    private static void attachShared(WestlakeActivityThread thread,
            String packageName, String appClassName, ClassLoader classLoader) {
        final boolean strictStandalone = !WestlakeLauncher.isRealFrameworkFallbackAllowed();
        sCurrentActivityThread = thread;
        thread.mPackageName = packageName;
        thread.mClassLoader = classLoader;
        if (strictStandalone) {
            thread.mInstrumentation = new WestlakeInstrumentation(thread);
            WestlakeLauncher.marker("PF301 strict WAT attach instrumentation ready");
            thread.mLooper = null;
            return;
        } else {
            WestlakeLauncher.trace("[WestlakeActivityThread] attach step1 looper begin");
            thread.mLooper = Looper.getMainLooper();
            WestlakeLauncher.trace("[WestlakeActivityThread] attach step1 looper done");
        }
        if (classLoader != null) {
            if (!strictStandalone) {
                WestlakeLauncher.trace("[WestlakeActivityThread] attach step2 set ccl begin");
            }
            Thread.currentThread().setContextClassLoader(classLoader);
            if (!strictStandalone) {
                WestlakeLauncher.trace("[WestlakeActivityThread] attach step2 set ccl done");
            }
        }

        if (!strictStandalone) {
            WestlakeLauncher.trace("[WestlakeActivityThread] attach step3 instrumentation begin");
        }
        thread.mInstrumentation = new WestlakeInstrumentation(thread);
        if (!strictStandalone) {
            WestlakeLauncher.trace("[WestlakeActivityThread] attach step3 instrumentation done");
        }

        if (strictStandalone) {
            thread.mAppComponentFactory = new AppComponentFactory();
        } else {
            WestlakeLauncher.trace("[WestlakeActivityThread] attach step4 factory discovery begin");
            thread.mAppComponentFactory = thread.discoverAppComponentFactory(classLoader);
            WestlakeLauncher.trace("[WestlakeActivityThread] attach step4 factory discovery done");
        }

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
                    thread.mInitialApplication = existing;
                    log("I", "Reusing existing Application from MiniServer (skip makeApplication)");
                }
            }
        } catch (Exception ignored) {}
        if (thread.mInitialApplication == null) {
            PackageInfo pkgInfo = new PackageInfo(packageName, thread.getClassLoader(),
                    thread.mAppComponentFactory);
            thread.mInitialApplication = pkgInfo.makeApplication(false, appClassName, thread.mInstrumentation);
        }

        log("I", "Attached: pkg=" + packageName
                + " app=" + (thread.mInitialApplication != null
                        ? thread.mInitialApplication.getClass().getName() : "null")
                + " factory=" + (thread.mAppComponentFactory != null
                        ? thread.mAppComponentFactory.getClass().getName() : "default"));

        Object singleton = dagger.hilt.android.internal.managers.ApplicationComponentManager.singletonComponent;
        if (singleton != null) {
            try {
                ClassLoader appCl = thread.mInitialApplication != null ? thread.mInitialApplication.getClass().getClassLoader() : classLoader;
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
                    if (ctxField.get(null) == null && thread.mInitialApplication != null) {
                        ctxField.set(null, thread.mInitialApplication);
                        log("I", "Set ApplicationContext.a = " + thread.mInitialApplication.getClass().getSimpleName());
                    }
                } catch (Throwable t4) { log("W", "ApplicationContext init: " + t4.getMessage()); }
                // Also initialize ClickstreamDataHelper
                try {
                    thread.initStaticHelperFromSingleton(appCl, singleton,
                        "com.mcdonalds.mcdcoreapp.analytics.ClickstreamDataHelper");
                    log("I", "ClickstreamDataHelper initialized from singleton");
                } catch (Throwable t3) { log("W", "ClickstreamDataHelper init: " + t3.getMessage()); }
                // Fix Crypto on objects returned by the singleton
                // Crypto needs Context — create one with the Application context
                try {
                    Class<?> cryptoClass = appCl.loadClass("com.mcdonalds.mcdcoreapp.common.Crypto");
                    Object crypto = cryptoClass.getConstructor(android.content.Context.class)
                            .newInstance(thread.mInitialApplication);
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

        String manifestFactory = null;
        try {
            MiniServer server = MiniServer.peek();
            ApkInfo info = server != null ? server.getApkInfo() : null;
            manifestFactory = info != null ? info.appComponentFactoryClassName : null;
        } catch (Throwable ignored) {
        }
        AppComponentFactory factory = instantiateAppComponentFactory(manifestFactory, cl);
        if (factory != null) {
            return factory;
        }

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

    private static AppComponentFactory instantiateAppComponentFactory(
            String factoryClassName, ClassLoader cl) {
        if (factoryClassName == null || factoryClassName.length() == 0) {
            return null;
        }
        try {
            Class<?> factoryClass =
                    WestlakeLauncher.resolveAppClassChildFirstOrNull(factoryClassName);
            if (factoryClass == null && cl != null) {
                factoryClass = cl.loadClass(factoryClassName);
            }
            if (factoryClass == null) {
                factoryClass = Class.forName(factoryClassName, false, cl);
            }
            Object instance = factoryClass.getDeclaredConstructor().newInstance();
            if (instance instanceof AppComponentFactory) {
                return (AppComponentFactory) instance;
            }
            log("W", "Manifest AppComponentFactory is not an AppComponentFactory: "
                    + factoryClassName);
        } catch (Throwable t) {
            log("W", "Failed to instantiate AppComponentFactory "
                    + factoryClassName + ": " + t);
        }
        return null;
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
