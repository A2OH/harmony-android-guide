package android.app;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * MiniActivityManager — manages the Activity back stack for a single app.
 *
 * Replaces Android's ActivityManagerService + ActivityTaskManagerService.
 * Handles:
 * - Activity instantiation via reflection
 * - Lifecycle dispatch (onCreate → onStart → onResume → onPause → onStop → onDestroy)
 * - Back stack navigation (push on startActivity, pop on finish)
 * - startActivityForResult / onActivityResult round-trip
 * - Only one Activity is "resumed" at a time
 */
public class MiniActivityManager {
    private static final String TAG = "MiniActivityManager";

    private final MiniServer mServer;
    private final ArrayList<ActivityRecord> mStack = new ArrayList<>();
    private final Map<String, Class<?>> mRegisteredClasses = new HashMap<>();

    /** Register an Activity class loaded from an external DEX/APK */
    public void registerActivityClass(String className, Class<?> cls) {
        mRegisteredClasses.put(className, cls);
    }
    private ActivityRecord mResumed;

    MiniActivityManager(MiniServer server) {
        mServer = server;
    }

    private static boolean isBootClassLoader(ClassLoader cl) {
        return cl == null || "java.lang.BootClassLoader".equals(cl.getClass().getName());
    }

    private ClassLoader resolveAppClassLoader() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (!isBootClassLoader(cl)) return cl;
        try {
            cl = ClassLoader.getSystemClassLoader();
            if (!isBootClassLoader(cl)) {
                Thread.currentThread().setContextClassLoader(cl);
                return cl;
            }
        } catch (Throwable ignored) {
        }
        Application app = mServer.getApplication();
        if (app != null) {
            cl = app.getClass().getClassLoader();
            if (!isBootClassLoader(cl)) return cl;
        }
        cl = MiniServer.class.getClassLoader();
        if (!isBootClassLoader(cl)) return cl;
        return getClass().getClassLoader();
    }

    private static Object getUnsafeSingleton(String className) throws Throwable {
        Class<?> unsafeClass = Class.forName(className);
        java.lang.reflect.Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        return unsafeField.get(null);
    }

    private Activity instantiateActivity(Class<?> cls, String className, Intent intent) throws Throwable {
        Throwable firstError = null;
        String[] unsafeClasses = {
                "jdk.internal.misc.Unsafe",
                "sun.misc.Unsafe"
        };
        for (String unsafeName : unsafeClasses) {
            try {
                Object unsafe = getUnsafeSingleton(unsafeName);
                return (Activity) unsafe.getClass()
                        .getMethod("allocateInstance", Class.class)
                        .invoke(unsafe, cls);
            } catch (Throwable t) {
                if (firstError == null) {
                    firstError = t;
                }
            }
        }

        try {
            java.lang.reflect.Constructor<?> ctor = cls.getDeclaredConstructor();
            ctor.setAccessible(true);
            return (Activity) ctor.newInstance();
        } catch (Throwable t) {
            if (firstError == null) {
                firstError = t;
            }
        }

        if (firstError != null) {
            throw firstError;
        }
        throw new InstantiationException("Failed to instantiate " + className);
    }

    /**
     * Start an Activity.
     * @param caller The calling Activity (null for initial launch)
     * @param intent The intent describing the Activity to start
     * @param requestCode -1 for startActivity, >= 0 for startActivityForResult
     */
    public void startActivity(Activity caller, Intent intent, int requestCode) {
        startActivity(caller, intent, requestCode, null);
    }

    public void startActivity(Activity caller, Intent intent, int requestCode, Class<?> preloadedClass) {
        if (intent == null) {
            Log.w(TAG, "startActivity: null intent");
            return;
        }
        if (caller == null && intent.getComponent() != null) {
            if (intent.getAction() == null || intent.getAction().isEmpty()) {
                intent.setAction(Intent.ACTION_MAIN);
            }
            if (!intent.hasCategory(Intent.CATEGORY_LAUNCHER)) {
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
            }
            if ((intent.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK) == 0) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        }
        ComponentName component = intent.getComponent();
        if (component == null) {
            // Implicit intent resolution via MiniPackageManager
            android.content.pm.MiniPackageManager pm = mServer.getPackageManager();
            if (pm != null) {
                android.content.pm.ResolveInfo ri = pm.resolveActivity(intent);
                if (ri != null && ri.resolvedComponentName != null) {
                    component = ri.resolvedComponentName;
                    intent.setComponent(component);
                }
            }
            if (component == null) {
                Log.w(TAG, "startActivity: cannot resolve intent, action=" + intent.getAction());
                return;
            }
        }

        String className = component.getClassName();
        String packageName = component.getPackageName();
        if (packageName == null || packageName.isEmpty()) {
            packageName = intent.getPackage();
        }
        if ((packageName == null || packageName.isEmpty()) && mServer.getApkInfo() != null) {
            packageName = mServer.getApkInfo().packageName;
        }
        if (packageName == null || packageName.isEmpty()) {
            packageName = mServer.getPackageName();
        }
        if ((packageName == null || packageName.isEmpty()) && mServer.getApplication() != null) {
            packageName = mServer.getApplication().getPackageName();
        }
        if (packageName == null || packageName.isEmpty()) {
            packageName = System.getProperty("westlake.apk.package");
        }
        if (packageName == null || packageName.isEmpty()) {
            packageName = "app";
        }
        if (!packageName.equals(component.getPackageName())) {
            component = new ComponentName(packageName, className);
            intent.setComponent(component);
        }
        if (intent.getPackage() == null || intent.getPackage().isEmpty()) {
            intent.setPackage(packageName);
        }
        Log.d(TAG, "startActivity: " + className
                + " action=" + intent.getAction()
                + " pkg=" + intent.getPackage());

        // Instantiate the Activity class
        Activity activity;
        Class<?> cls = preloadedClass;
        try {
            if (cls == null) {
                cls = mRegisteredClasses.get(className);
            }
            if (cls == null) {
                // Use context classloader (set by ART to app's PathClassLoader)
                // NOT boot classloader (MiniActivityManager is on boot classpath)
                ClassLoader cl = resolveAppClassLoader();
                if (isBootClassLoader(cl)) {
                    Log.e(TAG, "No app class loader for " + className);
                    return;
                }
                try {
                    cls = Class.forName(className, false, cl);
                } catch (ClassNotFoundException e) {
                    cls = cl.loadClass(className);
                }
            }
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (isBootClassLoader(cl)) {
                cl = resolveAppClassLoader();
            }
            if (!isBootClassLoader(cl)) {
                Thread.currentThread().setContextClassLoader(cl);
            }
            activity = instantiateActivity(cls, className, intent);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Activity class not found: " + className);
            return;
        } catch (Throwable e) {
            if (cls == null) {
                Log.e(TAG, "Failed to resolve " + className + " (" + e.getClass().getName() + ")");
                return;
            }
            Log.e(TAG, "Failed to instantiate " + className + " (" + e.getClass().getName() + ")");
            return;
        }

        // Seed framework state before attach/lifecycle code queries the Activity.
        ShimCompat.setActivityField(activity, "mIntent", intent);
        ShimCompat.setActivityField(activity, "mComponent", component);
        ShimCompat.setActivityField(activity, "mApplication", mServer.getApplication());
        ShimCompat.setActivityField(activity, "mFinished", Boolean.FALSE);
        ShimCompat.setActivityField(activity, "mDestroyed", Boolean.FALSE);

        // Inject DI fields directly from the singleton component
        // The Hilt injection mechanism fails because componentManager() silently catches errors.
        // We bypass Hilt entirely: get the singleton component and call inject directly.
        try {
            Object singleton = dagger.hilt.android.internal.managers.ApplicationComponentManager.singletonComponent;
            Log.d(TAG, "  Singleton component: " + (singleton != null ? singleton.getClass().getName() : "NULL"));
            if (singleton != null) {
                // The singleton implements DataSourceModuleProvider transitively (via SingletonC interface)
                // Try casting directly — if it works, the activity can access it too
                for (Class<?> iface : singleton.getClass().getInterfaces()) {
                    if (iface.getName().contains("DataSourceModuleProvider")) {
                        Log.d(TAG, "  Singleton DOES implement DataSourceModuleProvider!");
                    }
                }
                // Set null interface fields on the activity to the singleton (which implements them transitively)
                int injected = 0;
                Class<?> activityClass = activity.getClass();
                while (activityClass != null && activityClass != Object.class) {
                    for (java.lang.reflect.Field f : activityClass.getDeclaredFields()) {
                        if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
                        Class<?> ftype = f.getType();
                        if (!ftype.isInterface()) continue;
                        f.setAccessible(true);
                        if (f.get(activity) != null) continue;
                        // Check if singleton implements this interface (transitively)
                        if (ftype.isInstance(singleton)) {
                            f.set(activity, singleton);
                            Log.d(TAG, "  Set " + f.getName() + " = singleton (implements " + ftype.getSimpleName() + ")");
                            injected++;
                        }
                    }
                    activityClass = activityClass.getSuperclass();
                }
                Log.d(TAG, "  Injected " + injected + " fields from singleton component");
            }
        } catch (Throwable t) {
            Log.d(TAG, "  Direct DI injection failed: " + t.getMessage());
        }
        // Fill null DI fields
        dagger.hilt.android.internal.managers.ActivityComponentManager.fillNullInterfaceFields(activity);

        // Call Activity.attach() with real framework Context — creates PhoneWindow, wires lifecycle
        try {
            Class<?> atClass = Class.forName("android.app.ActivityThread");
            java.lang.reflect.Field sCurrentAT = atClass.getDeclaredField("sCurrentActivityThread");
            sCurrentAT.setAccessible(true);
            Object at = sCurrentAT.get(null);
            if (at != null) {
                // Use the real context (MCD package context if available, else system context)
                Object ctx = com.westlake.engine.WestlakeLauncher.sRealContext;
                if (ctx == null) ctx = atClass.getDeclaredMethod("getSystemContext").invoke(at);

                // Call the full Activity.attach() — it creates PhoneWindow internally
                // Find the right overload (19 or 20 params)
                java.lang.reflect.Method attachMethod = null;
                for (java.lang.reflect.Method m : Activity.class.getDeclaredMethods()) {
                    if (m.getName().equals("attach") && m.getParameterCount() >= 19) {
                        attachMethod = m;
                        break;
                    }
                }
                if (attachMethod != null) {
                    attachMethod.setAccessible(true);
                    // Build args: Context, ActivityThread, Instrumentation, token, ident,
                    // Application, Intent, ActivityInfo, title, parent, id,
                    // NonConfigInstances, Configuration, referrer, IVoiceInteractor,
                    // Window, ActivityConfigCallback, assistToken, shareableActivityToken[, taskFragToken]
                    android.content.pm.ActivityInfo ai = new android.content.pm.ActivityInfo();
                    String pkg = component.getPackageName();
                    if (pkg == null || pkg.isEmpty()) {
                        pkg = intent.getPackage();
                        if (pkg == null || pkg.isEmpty()) {
                            // Derive from class name: com.mcdonalds.app.SplashActivity → com.mcdonalds.app
                            String cn = component.getClassName();
                            int dot = cn != null ? cn.lastIndexOf('.') : -1;
                            pkg = dot > 0 ? cn.substring(0, dot) : "com.westlake.app";
                        }
                    }
                    ai.packageName = pkg;
                    ai.name = component.getClassName();
                    try {
                        java.lang.reflect.Field aiField = ai.getClass().getField("applicationInfo");
                        aiField.set(ai, ((android.content.Context) ctx).getApplicationInfo());
                    } catch (Throwable ignore) {}
                    try { ai.getClass().getField("theme").setInt(ai, 0x01030237); } catch (Throwable ignore) {}

                    Object[] args = new Object[attachMethod.getParameterCount()];
                    args[0] = ctx;       // context
                    args[1] = at;        // activityThread
                    args[2] = new android.app.Instrumentation(); // instrumentation
                    args[3] = new android.os.Binder(); // token
                    args[4] = 0;         // ident
                    args[5] = mServer.getApplication(); // application
                    args[6] = intent;    // intent
                    args[7] = ai;        // activityInfo
                    args[8] = activity.getClass().getSimpleName(); // title
                    // args[9..N] = null (parent, id, lastNonConfigInstances, config, referrer, etc.)

                    attachMethod.invoke(activity, args);
                    Log.d(TAG, "  Activity.attach() OK — Window: " + activity.getWindow());
                } else {
                    // Fallback: just attachBaseContext
                    java.lang.reflect.Method attachBase = android.content.ContextWrapper.class
                        .getDeclaredMethod("attachBaseContext", android.content.Context.class);
                    attachBase.setAccessible(true);
                    attachBase.invoke(activity, ctx);
                    Log.d(TAG, "  attachBaseContext fallback (no attach method found)");
                }
            }
        } catch (Throwable t) {
            Log.w(TAG, "  Activity.attach failed: " + t.getClass().getSimpleName() + ": " + t.getMessage());
            Throwable root = t;
            while (root.getCause() != null) root = root.getCause();
            if (root != t) Log.w(TAG, "  ROOT: " + root);
            // Fallback: just attachBaseContext + create PhoneWindow manually
            try {
                Class<?> atClass2 = Class.forName("android.app.ActivityThread");
                java.lang.reflect.Field f2 = atClass2.getDeclaredField("sCurrentActivityThread");
                f2.setAccessible(true);
                Object at2 = f2.get(null);
                Object ctx2 = com.westlake.engine.WestlakeLauncher.sRealContext;
                if (ctx2 == null && at2 != null) ctx2 = atClass2.getDeclaredMethod("getSystemContext").invoke(at2);
                if (ctx2 != null) {
                    java.lang.reflect.Method ab = android.content.ContextWrapper.class
                        .getDeclaredMethod("attachBaseContext", android.content.Context.class);
                    ab.setAccessible(true);
                    ab.invoke(activity, ctx2);
                    // Create PhoneWindow
                    Object pw = Class.forName("com.android.internal.policy.PhoneWindow")
                        .getConstructor(android.content.Context.class).newInstance(ctx2);
                    java.lang.reflect.Field mw = Activity.class.getDeclaredField("mWindow");
                    mw.setAccessible(true);
                    mw.set(activity, pw);
                    ((android.view.Window)pw).setCallback(activity);
                    Log.d(TAG, "  Fallback: Context + PhoneWindow attached");
                }
            } catch (Throwable t2) {
                Log.w(TAG, "  Fallback attach also failed: " + t2.getMessage());
            }
        }

        // Initialize framework state (via reflection — on real Android, Activity fields differ)
        ShimCompat.setActivityField(activity, "mIntent", intent);
        ShimCompat.setActivityField(activity, "mComponent", component);
        ShimCompat.setActivityField(activity, "mApplication", mServer.getApplication());
        ShimCompat.setActivityField(activity, "mFinished", Boolean.FALSE);
        ShimCompat.setActivityField(activity, "mDestroyed", Boolean.FALSE);

        // Create the ActivityRecord
        ActivityRecord record = new ActivityRecord();
        record.activity = activity;
        record.intent = intent;
        record.component = component;
        if (caller != null && requestCode >= 0) {
            record.caller = findRecord(caller);
            record.requestCode = requestCode;
        }

        // Pause the current top activity
        if (mResumed != null) {
            ActivityRecord prev = mResumed;
            performPause(prev);
            performStop(prev);
        }

        // Push and start the new activity — catch ALL exceptions to ensure Activity is usable
        mStack.add(record);
        mResumed = record; // Set early so getResumedActivity() works even if lifecycle crashes
        try {
            performCreate(record, null);
        } catch (Throwable e) {
            Log.e(TAG, "startActivity performCreate threw: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        // Check if onCreate set content — if not, skip start/resume (avoids lifecycle observer hangs)
        boolean hasContent = false;
        try {
            android.view.View decor = record.activity.getWindow() != null ? record.activity.getWindow().getDecorView() : null;
            hasContent = decor instanceof android.view.ViewGroup && ((android.view.ViewGroup) decor).getChildCount() > 0;
        } catch (Throwable e) { /* ignore */ }
        // Always continue lifecycle — NPE in super.onCreate() doesn't mean the activity is dead.
        // The activity might set content in onStart/onResume or via delayed navigation.
        try {
            performStart(record);
            Log.d(TAG, "  performStart DONE for " + className);
        } catch (Throwable e) {
            Log.e(TAG, "startActivity performStart threw: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        try {
            performResume(record);
            Log.d(TAG, "  performResume DONE for " + className);
        } catch (Throwable e) {
            Log.e(TAG, "startActivity performResume threw: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        if (!hasContent) {
            Log.w(TAG, "Note: onCreate didn't set content view (NPE in super) — splash fallback will be used if needed");
        }
    }

    /**
     * Finish an Activity. Pops it from the stack and resumes the previous one.
     */
    public void finishActivity(Activity activity) {
        if (activity == null) {
            Log.w(TAG, "finishActivity: null activity");
            return;
        }
        ActivityRecord record = findRecord(activity);
        if (record == null) {
            // Already finished or never in stack — idempotent
            return;
        }

        Log.d(TAG, "finishActivity: " + record.component.getClassName());

        // If this is the resumed activity, pause it first
        if (record == mResumed) {
            performPause(record);
        }
        performStop(record);
        performDestroy(record);

        // Deliver result to caller if startActivityForResult was used
        if (record.caller != null && record.requestCode >= 0) {
            Activity callerActivity = record.caller.activity;
            boolean callerDestroyed = ShimCompat.getActivityBooleanField(callerActivity, "mDestroyed", false);
            if (callerActivity != null && !callerDestroyed) {
                int resultCode = ShimCompat.getActivityIntField(activity, "mResultCode", 0);
                android.content.Intent resultData = ShimCompat.getActivityField(activity, "mResultData", (android.content.Intent) null);
                Log.i(TAG, "  delivering result: code=" + resultCode + " data=" + resultData + " reqCode=" + record.requestCode);
                callerActivity.onActivityResult(
                    record.requestCode,
                    resultCode,
                    resultData
                );
            }
        }

        // Remove from stack
        mStack.remove(record);

        // Resume the new top activity and force re-layout
        if (!mStack.isEmpty()) {
            ActivityRecord top = mStack.get(mStack.size() - 1);
            performRestart(top);
            performStart(top);
            performResume(top);
            // Force re-layout (data may have changed while paused)
            top.activity.invalidateLayout();
        }
    }

    /**
     * Handle back button press. Finishes the top activity.
     */
    public void onBackPressed() {
        if (mResumed != null && mResumed.activity != null) {
            Activity top = mResumed.activity;
            top.onBackPressed();
            // finish() is now idempotent and calls finishActivity internally
        }
    }

    /**
     * Register a pre-created Activity (bypass instantiation for Hilt apps).
     */
    public void registerActivity(Activity activity, String packageName, String className) {
        ComponentName component = new ComponentName(packageName, className);
        ShimCompat.setActivityField(activity, "mComponent", component);
        ShimCompat.setActivityField(activity, "mApplication", mServer.getApplication());
        ShimCompat.setActivityField(activity, "mFinished", Boolean.FALSE);
        ActivityRecord record = new ActivityRecord();
        record.activity = activity;
        record.component = component;
        mStack.add(record);
        mResumed = record;
        Log.d(TAG, "Registered proxy activity: " + className);
    }

    /**
     * Finish all activities (shutdown).
     */
    public void finishAll() {
        // Destroy from top to bottom
        while (!mStack.isEmpty()) {
            ActivityRecord top = mStack.get(mStack.size() - 1);
            if (top == mResumed) {
                performPause(top);
            }
            performStop(top);
            performDestroy(top);
            mStack.remove(mStack.size() - 1);
        }
        mResumed = null;
    }

    /** Get the currently resumed Activity, or null. */
    public Activity getResumedActivity() {
        return mResumed != null ? mResumed.activity : null;
    }

    /** Get the Activity stack size. */
    public int getStackSize() {
        return mStack.size();
    }

    /** Get an Activity by index (0 = bottom). */
    public Activity getActivity(int index) {
        if (index < 0 || index >= mStack.size()) return null;
        return mStack.get(index).activity;
    }

    // ── Lifecycle dispatch ──────────────────────────────────────────────────

    private void performCreate(ActivityRecord r, Bundle savedInstanceState) {
        Log.d(TAG, "  performCreate: " + r.component.getClassName());
        // Dispatch lifecycle: restore saved state + ON_CREATE
        dispatchLifecycleEvent(r.activity, "performRestore", savedInstanceState);
        boolean createNPE = false;

        // Run onCreate in thread with timeout (complex apps can hang in DI init)
        final Activity actRef = r.activity;
        final Bundle ssRef = savedInstanceState;
        final boolean[] done = { false };
        final Exception[] error = { null };
        Thread ocThread = new Thread(new Runnable() {
            public void run() {
                Thread.currentThread().setContextClassLoader(resolveAppClassLoader());
                try {
                    // Use reflection to bypass protected access check.
                    // On phone's dalvikvm64, Activity.onCreate is protected and
                    // inaccessible from shim classloader.
                    java.lang.reflect.Method ocm = android.app.Activity.class.getDeclaredMethod(
                        "onCreate", android.os.Bundle.class);
                    ocm.setAccessible(true);
                    ocm.invoke(actRef, ssRef);
                    done[0] = true;
                } catch (Throwable e) {
                    error[0] = (e instanceof Exception) ? (Exception) e : new RuntimeException(e);
                    done[0] = true;
                }
            }
        }, "ActivityOnCreate");
        ocThread.setDaemon(true);
        ocThread.start();
        try { ocThread.join(5000); } catch (InterruptedException ie) {}
        // CRITICAL: Stop the thread if still running to prevent GC deadlock.
        // SplashActivity.onCreate may catch UUID errors and continue with DI code
        // that never reaches a safepoint, causing nonconcurrent GC to freeze all threads.
        if (ocThread.isAlive()) {
            try { ocThread.stop(); } catch (Throwable t) { /* ThreadDeath expected */ }
        }

        if (!done[0]) {
            Log.w(TAG, "performCreate TIMEOUT (5s) for " + r.component.getClassName() + " — proceeding");
        } else if (error[0] instanceof NullPointerException) {
            Log.w(TAG, "performCreate NPE (non-fatal): " + error[0].getMessage());
            createNPE = true;
        } else if (error[0] != null) {
            Throwable root = error[0];
            while (root.getCause() != null) root = root.getCause();
            Log.e(TAG, "performCreate error: " + error[0].getClass().getSimpleName() + ": " + error[0].getMessage());
            Log.e(TAG, "performCreate ROOT: " + root.getClass().getName() + ": " + root.getMessage());
            root.printStackTrace(System.err);
        }

        // If onCreate NPE'd, try to manually set content view with the splash layout
        if (createNPE || !done[0]) {
            Log.d(TAG, "  tryRecoverContent: attempting manual setContentView for " + r.component.getClassName());
            // Fill null interface fields with dynamic Proxies (surviving DI failure)
            fillNullFieldsWithProxies(r.activity);
            // Retry onCreate with non-null stub fields
            try {
                Log.d(TAG, "  Retrying onCreate with stub DI fields...");
                r.activity.onCreate(null);
                Log.d(TAG, "  Retry onCreate SUCCESS");
            } catch (Throwable retryEx) {
                Log.d(TAG, "  Retry onCreate failed: " + retryEx.getClass().getSimpleName() + ": " + retryEx.getMessage());
            }
            try {
                // Try setContentView with the splash layout resource ID
                // First try via getIdentifier, then hardcoded McDonald's splash ID
                android.content.res.Resources res = r.activity.getResources();
                int layoutId = 0;
                if (res != null) {
                    layoutId = res.getIdentifier("activity_splash_screen", "layout",
                            r.component.getPackageName());
                }
                if (layoutId == 0) layoutId = 0x7f0e0530; // McDonald's splash layout
                r.activity.setContentView(layoutId);
                Log.d(TAG, "  tryRecoverContent: setContentView(0x" + Integer.toHexString(layoutId) + ") OK");
            } catch (Throwable ex) {
                Log.d(TAG, "  tryRecoverContent failed: " + ex.getMessage());
            }
            tryRecoverFragments(r.activity);
        }
        try {
            dispatchLifecycleEvent(r.activity, "ON_CREATE");
        } catch (Exception e) {
            Log.w(TAG, "performCreate lifecycle dispatch error: " + e.getMessage());
        }
        try {
            tryRecoverFragments(r.activity);
        } catch (Throwable t) {
            Log.d(TAG, "  tryRecoverFragments post-create failed: " + t);
        }
    }

    /**
     * After an NPE in onCreate (often from getSupportActionBar()), the fragment setup
     * code was skipped. Try to discover Fragment classes and add them to empty containers.
     */

    /**
     * Fill null fields in the activity (and superclasses) with dynamic Proxy stubs.
     * This recovers from DI injection failures by providing non-null stub implementations
     * for interface-typed fields.
     */
    private void fillNullFieldsWithProxies(Activity activity) {
        int filled = 0;
        try {
            Class<?> cls = activity.getClass();
            while (cls != null && cls != Object.class) {
                for (java.lang.reflect.Field f : cls.getDeclaredFields()) {
                    if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
                    Class<?> type = f.getType();
                    if (!type.isInterface()) continue;
                    f.setAccessible(true);
                    if (f.get(activity) != null) continue;
                    // Create a Proxy that returns null/0/false for all methods
                    try {
                        Object proxy = java.lang.reflect.Proxy.newProxyInstance(
                            type.getClassLoader(),
                            new Class<?>[]{ type },
                            new java.lang.reflect.InvocationHandler() {
                                @Override
                                public Object invoke(Object p, java.lang.reflect.Method m, Object[] args) {
                                    Class<?> rt = m.getReturnType();
                                    if (rt == void.class) return null;
                                    if (rt == boolean.class) return false;
                                    if (rt == int.class) return 0;
                                    if (rt == long.class) return 0L;
                                    if (rt == float.class) return 0f;
                                    if (rt == double.class) return 0.0;
                                    if (rt == String.class) return "";
                                    return null;
                                }
                            });
                        f.set(activity, proxy);
                        filled++;
                    } catch (Throwable ex) { /* skip this field */ }
                }
                cls = cls.getSuperclass();
            }
        } catch (Throwable t) { /* reflection failure */ }
        if (filled > 0) Log.d(TAG, "  fillNullFieldsWithProxies: filled " + filled + " null interface fields");
    }

    private void tryRecoverFragments(Activity activity) {
        try {
            // Initialize any null SharedPreferences fields on the activity
            // (these would have been set in onCreate code that was skipped due to NPE)
            initNullSharedPrefsFields(activity);

            // Check if content view exists but has empty fragment containers
            android.view.Window w = activity.getWindow();
            if (w == null) return;
            android.view.View decor = w.getDecorView();
            if (decor == null) return;

            // Look for empty FrameLayouts that should contain fragments
            // Common pattern: Activity has a FrameLayout(id=R.id.main_content) for the main fragment
            String pkg = activity.getPackageName();
            if (pkg == null) pkg = activity.getClass().getPackage().getName();

            // Counter app special case: two fragments in a DrawerLayout
            // Main content (0x7f0a004a) = CounterFragment, Drawer (0x7f0a004b) = CountersListFragment
            String actPkg = activity.getClass().getPackage().getName();
            if (tryCounterAppFragments(activity, decor, pkg)) return;

            int containerId = resolveFragmentContainerId(activity, decor, pkg);
            android.view.View containerView =
                    containerId != 0 ? decor.findViewById(containerId) : null;
            android.view.ViewGroup containerVg =
                    containerView instanceof android.view.ViewGroup
                            ? (android.view.ViewGroup) containerView : null;

            if (containerVg != null && containerVg.getChildCount() == 0) {
                Object existingFragment = findExistingActivityFragment(activity);
                if (existingFragment != null) {
                    if (attachRecoveredFragment(activity, existingFragment.getClass(),
                            existingFragment, containerVg)) {
                        Log.i(TAG, "  tryRecoverFragments: attached existing "
                                + existingFragment.getClass().getSimpleName()
                                + " to container 0x" + Integer.toHexString(containerId));
                        return;
                    }
                }
            }

            ArrayList<String> fragmentCandidates = new ArrayList<>();
            fragmentCandidates.add(actPkg + ".MainFragment");
            fragmentCandidates.add(actPkg + ".HomeFragment");
            fragmentCandidates.add(pkg + ".ui.MainFragment");
            fragmentCandidates.add(pkg + ".ui.HomeFragment");
            fragmentCandidates.add(pkg + ".fragment.MainFragment");
            if (actPkg.indexOf(".activity") > 0) {
                String fragmentPkg = actPkg.replace(".activity", ".fragment");
                String simple = activity.getClass().getSimpleName();
                if (simple.endsWith("Activity")) {
                    fragmentCandidates.add(fragmentPkg + "."
                            + simple.substring(0, simple.length() - "Activity".length()) + "Fragment");
                }
                fragmentCandidates.add(fragmentPkg + ".HomeDashboardFragment");
            }

            // Find FragmentManager via reflection (works for both support lib and framework)
            Object fragmentManager = null;
            try {
                java.lang.reflect.Method gsfm = activity.getClass().getMethod("getSupportFragmentManager");
                fragmentManager = gsfm.invoke(activity);
            } catch (Exception e) {
                // try framework FragmentManager
                try {
                    fragmentManager = activity.getFragmentManager();
                } catch (Exception e2) { /* ignore */ }
            }

            if (fragmentManager == null) {
                Log.d(TAG, "  tryRecoverFragments: no FragmentManager available");
            }

            for (String className : fragmentCandidates) {
                try {
                    Class<?> fragClass = resolveAppClassLoader().loadClass(className);
                    Object fragment = fragClass.newInstance();
                    if (containerVg != null && containerVg.getChildCount() == 0) {
                        if (attachRecoveredFragment(activity, fragClass, fragment, containerVg)) {
                            Log.i(TAG, "  tryRecoverFragments: manually attached "
                                    + fragClass.getSimpleName() + " to container 0x"
                                    + Integer.toHexString(containerId));
                            return;
                        }
                    }

                    if (fragmentManager == null) {
                        continue;
                    }

                    // Try to add via support FragmentManager
                    java.lang.reflect.Method beginTx = fragmentManager.getClass().getMethod("beginTransaction");
                    Object tx = beginTx.invoke(fragmentManager);

                    // Prefer the activity's declared fragment container before a generic empty frame.
                    if (containerId == 0) containerId = findEmptyFrameLayoutId(decor);
                    if (containerId == 0) containerId = findEmptyViewGroupId(decor);
                    if (containerId == 0) containerId = 0x7f0a004a; // fallback to common ID

                    // Try to find the replace(int, Fragment) method — walk up the hierarchy
                    // Also try all declared methods on the transaction in case of name matching
                    boolean added = false;
                    for (Class<?> c = fragClass; c != null && c != Object.class; c = c.getSuperclass()) {
                        try {
                            java.lang.reflect.Method replace = tx.getClass().getMethod("replace", int.class, c);
                            replace.invoke(tx, containerId, fragment);
                            added = true;
                            break;
                        } catch (NoSuchMethodException nsme) { /* try parent */ }
                    }
                    // Fallback: find any 'replace' method with matching arity and try it
                    if (!added) {
                        for (java.lang.reflect.Method m : tx.getClass().getMethods()) {
                            if (m.getName().equals("replace") && m.getParameterTypes().length == 2
                                    && m.getParameterTypes()[0] == int.class
                                    && m.getParameterTypes()[1].isAssignableFrom(fragClass)) {
                                m.invoke(tx, containerId, fragment);
                                added = true;
                                Log.d(TAG, "  tryRecoverFragments: used fallback replace(" + m.getParameterTypes()[1].getName() + ")");
                                break;
                            }
                        }
                    }

                    if (added) {
                        // Commit the transaction
                        java.lang.reflect.Method commit = tx.getClass().getMethod("commitAllowingStateLoss");
                        commit.invoke(tx);
                        // Execute pending transactions immediately
                        try {
                            java.lang.reflect.Method exec = fragmentManager.getClass().getMethod("executePendingTransactions");
                            exec.invoke(fragmentManager);
                        } catch (Exception e) { /* may fail, that's ok */ }
                        Log.i(TAG, "  tryRecoverFragments: added " + fragClass.getSimpleName() + " to container 0x" + Integer.toHexString(containerId));

                        // If the fragment's view wasn't attached by the FragmentManager,
                        // try to invoke onCreateView directly and add it
                        android.view.View postTxContainer = decor.findViewById(containerId);
                        if (postTxContainer instanceof android.view.ViewGroup) {
                            android.view.ViewGroup postTxGroup = (android.view.ViewGroup) postTxContainer;
                            if (postTxGroup.getChildCount() == 0) {
                                if (attachRecoveredFragment(activity, fragClass, fragment, postTxGroup)) {
                                    Log.i(TAG, "  tryRecoverFragments: manually attached "
                                            + fragClass.getSimpleName() + " after transaction");
                                } else {
                                    Log.d(TAG, "  tryRecoverFragments: manual view attach failed for "
                                            + fragClass.getSimpleName());
                                }
                            }
                        }
                        return;
                    }
                } catch (ClassNotFoundException e) {
                    // Try next candidate
                } catch (Exception e) {
                    Log.d(TAG, "  tryRecoverFragments: " + className + " failed: " + e);
                }
            }
            Log.d(TAG, "  tryRecoverFragments: no suitable Fragment class found");
        } catch (Exception e) {
            Log.d(TAG, "  tryRecoverFragments error: " + e);
        }
    }

    /**
     * Special handling for Counter app: put CounterFragment in main content,
     * CountersListFragment in drawer. Pass the first counter name as argument.
     */
    private boolean tryCounterAppFragments(Activity activity, android.view.View decor, String pkg) {
        try {
            Class<?> counterFragClass = null;
            Class<?> listFragClass = null;
            try { counterFragClass = resolveAppClassLoader().loadClass(pkg + ".ui.CounterFragment"); } catch (Exception e) {}
            try { listFragClass = resolveAppClassLoader().loadClass(pkg + ".ui.CountersListFragment"); } catch (Exception e) {}
            if (counterFragClass == null) return false;

            Log.i(TAG, "  tryCounterAppFragments: found CounterFragment + CountersListFragment");

            android.view.LayoutInflater inflater = android.view.LayoutInflater.from(activity);

            // Get first counter name from SharedPreferences
            android.content.SharedPreferences prefs = android.content.SharedPreferencesImpl.getInstance("counters");
            java.util.Map<String, ?> all = prefs.getAll();
            String firstCounter = all.isEmpty() ? "My Counter" : all.keySet().iterator().next();

            // Main content container (0x7f0a004a)
            android.view.View mainContainer = decor.findViewById(0x7f0a004a);
            if (mainContainer instanceof android.view.ViewGroup) {
                android.view.ViewGroup mc = (android.view.ViewGroup) mainContainer;
                Object counterFrag = counterFragClass.newInstance();

                // Set counter name via Bundle arguments
                android.os.Bundle args = new android.os.Bundle();
                args.putString("counterName", firstCounter);
                try {
                    java.lang.reflect.Method setArgs = counterFragClass.getMethod("setArguments", android.os.Bundle.class);
                    setArgs.invoke(counterFrag, args);
                } catch (Exception e) { /* may not have setArguments */ }

                // Attach fragment to activity (so getActivity() works in onCreateView)
                attachFragmentToActivity(counterFrag, counterFragClass, activity);
                // Ensure mApplication is set (may have been lost during AppCompat init)
                if (activity.getApplication() == null) {
                    Application app = MiniServer.get().getApplication();
                    ShimCompat.setActivityField(activity, "mApplication", app);
                    Log.i(TAG, "  re-set mApplication: " + app.getClass().getSimpleName());
                }

                // Force-set counters on whatever Application the Fragment sees
                try {
                    // Get the Application the Fragment will access
                    java.lang.reflect.Method ga = null;
                    for (Class<?> c = counterFragClass; c != null; c = c.getSuperclass()) {
                        try { ga = c.getMethod("getActivity"); break; } catch (NoSuchMethodException e) {}
                    }
                    if (ga != null) {
                        Object fragActivity = ga.invoke(counterFrag);
                        if (fragActivity != null) {
                            java.lang.reflect.Method gApp = fragActivity.getClass().getMethod("getApplication");
                            Object app = gApp.invoke(fragActivity);
                            if (app != null) {
                                java.lang.reflect.Field cf = app.getClass().getDeclaredField("counters");
                                cf.setAccessible(true);
                                if (cf.get(app) == null) {
                                    // Build counters from SharedPreferences
                                    java.util.LinkedHashMap<String, Integer> data = new java.util.LinkedHashMap<>();
                                    android.content.SharedPreferences sp = android.content.SharedPreferencesImpl.getInstance("counters");
                                    for (java.util.Map.Entry<String, ?> entry : sp.getAll().entrySet()) {
                                        if (entry.getValue() instanceof Integer)
                                            data.put(entry.getKey(), (Integer) entry.getValue());
                                    }
                                    cf.set(app, data);
                                    Log.i(TAG, "  force-set counters on Fragment's Application: " + data.keySet());
                                }
                            } else {
                                Log.w(TAG, "  Fragment's getApplication() returned null");
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.w(TAG, "  force-set counters failed: " + e.getMessage());
                }

                // Call onCreateView — if it crashes, inflate counter.xml directly
                android.view.View fragView = callOnCreateView(counterFrag, counterFragClass, inflater, mc);
                if (fragView == null) {
                    // Inflate counter.xml from APK and populate manually
                    Log.i(TAG, "  tryCounterAppFragments: inflating counter.xml directly");
                    fragView = inflater.inflate(0x7f030018, mc, false); // counter.xml
                    if (fragView != null) {
                        final android.content.SharedPreferences sp =
                            android.content.SharedPreferencesImpl.getInstance("counters");
                        Object val = sp.getAll().get(firstCounter);
                        final int[] count = { (val instanceof Integer) ? (Integer) val : 0 };

                        // Find views by ID from the inflated XML
                        android.widget.Button plusBtn = (android.widget.Button) fragView.findViewById(0x7f0a0042);
                        android.widget.Button minusBtn = (android.widget.Button) fragView.findViewById(0x7f0a0043);
                        final android.widget.TextView countTv = (android.widget.TextView) fragView.findViewById(0x7f0a0044);

                        if (countTv != null) {
                            countTv.setText(String.valueOf(count[0]));
                            countTv.setTextSize(96);
                            countTv.setTextColor(0xFFFFFFFF);
                            // Force-center the count's parent in whatever layout type
                            android.view.View countParent = (android.view.View) countTv.getParent();
                            if (countParent != null) {
                                android.view.ViewGroup.LayoutParams clp = countParent.getLayoutParams();
                                if (clp instanceof android.widget.RelativeLayout.LayoutParams) {
                                    ((android.widget.RelativeLayout.LayoutParams) clp).addRule(
                                        android.widget.RelativeLayout.CENTER_IN_PARENT, -1);
                                } else if (clp instanceof android.widget.FrameLayout.LayoutParams) {
                                    ((android.widget.FrameLayout.LayoutParams) clp).gravity = 0x11;
                                } else {
                                    android.widget.FrameLayout.LayoutParams flp =
                                        new android.widget.FrameLayout.LayoutParams(clp.width, clp.height);
                                    flp.gravity = 0x11;
                                    countParent.setLayoutParams(flp);
                                }
                            }
                        }
                        // Set button text and make them larger for usability
                        if (plusBtn != null) {
                            if (plusBtn.getText() == null || plusBtn.getText().length() == 0) plusBtn.setText("+");
                            plusBtn.setTextSize(48);
                            android.view.ViewGroup.LayoutParams plp = plusBtn.getLayoutParams();
                            if (plp != null) plp.height = 160;
                        }
                        if (minusBtn != null) {
                            if (minusBtn.getText() == null || minusBtn.getText().length() == 0) minusBtn.setText("\u2212");
                            minusBtn.setTextSize(48);
                            android.view.ViewGroup.LayoutParams mlp = minusBtn.getLayoutParams();
                            if (mlp != null) mlp.height = 160;
                        }
                        if (plusBtn != null) {
                            plusBtn.setOnClickListener(new android.view.View.OnClickListener() {
                                public void onClick(android.view.View v) {
                                    count[0]++;
                                    countTv.setText(String.valueOf(count[0]));
                                    sp.edit().putInt(firstCounter, count[0]).apply();
                                }
                            });
                        }
                        if (minusBtn != null) {
                            minusBtn.setOnClickListener(new android.view.View.OnClickListener() {
                                public void onClick(android.view.View v) {
                                    count[0]--;
                                    countTv.setText(String.valueOf(count[0]));
                                    sp.edit().putInt(firstCounter, count[0]).apply();
                                }
                            });
                        }
                        Log.i(TAG, "  tryCounterAppFragments: counter.xml inflated, count=" + count[0]);
                    }
                }
                if (fragView != null) {
                    mc.addView(fragView);
                    Log.i(TAG, "  tryCounterAppFragments: CounterFragment added to main, counter=" + firstCounter);
                }
            }

            // Drawer container (0x7f0a004b) — add CountersListFragment
            if (listFragClass != null) {
                android.view.View drawerContainer = decor.findViewById(0x7f0a004b);
                if (drawerContainer instanceof android.view.ViewGroup) {
                    android.view.ViewGroup dc = (android.view.ViewGroup) drawerContainer;
                    Object listFrag = listFragClass.newInstance();
                    attachFragmentToActivity(listFrag, listFragClass, activity);
                    android.view.View listView = callOnCreateView(listFrag, listFragClass, inflater, dc);
                    if (listView != null) {
                        dc.addView(listView);
                        callFragmentLifecycle(listFrag, listFragClass, listView);
                        populateListViews(listView);
                        Log.i(TAG, "  tryCounterAppFragments: CountersListFragment added to drawer");
                    }
                }
            }

            return true;
        } catch (Exception e) {
            Log.w(TAG, "  tryCounterAppFragments failed: " + e);
            return false;
        }
    }

    private int resolveFragmentContainerId(Activity activity, android.view.View decor, String pkg) {
        try {
            java.lang.reflect.Method m = activity.getClass().getMethod("getFragmentContainerId");
            Object result = m.invoke(activity);
            if (result instanceof Integer && ((Integer) result).intValue() != 0) {
                return ((Integer) result).intValue();
            }
        } catch (Throwable ignored) {
        }
        try {
            android.content.res.Resources res = activity.getResources();
            if (res != null) {
                String[] names = {
                        "home_dashboard_container",
                        "main_content",
                        "content_view",
                        "page_content",
                        "intermediate_layout_container"
                };
                for (int i = 0; i < names.length; i++) {
                    int id = res.getIdentifier(names[i], "id", pkg);
                    if (id != 0) {
                        return id;
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        int id = findEmptyFrameLayoutId(decor);
        if (id != 0) return id;
        return findEmptyViewGroupId(decor);
    }

    private Object findExistingActivityFragment(Activity activity) {
        for (Class<?> c = activity.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
            java.lang.reflect.Field[] fields = c.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                java.lang.reflect.Field f = fields[i];
                String typeName = f.getType().getName();
                String fieldName = f.getName();
                if ((typeName != null && typeName.contains("Fragment"))
                        || (fieldName != null && fieldName.contains("Fragment"))) {
                    try {
                        f.setAccessible(true);
                        Object value = f.get(activity);
                        if (value != null) {
                            return value;
                        }
                    } catch (Throwable ignored) {
                    }
                }
            }
        }
        return null;
    }

    private boolean attachRecoveredFragment(Activity activity, Class<?> fragClass, Object fragment,
            android.view.ViewGroup container) {
        try {
            attachFragmentToActivity(fragment, fragClass, activity);
            callFragmentCreate(fragment, fragClass);
            android.view.LayoutInflater inflater = android.view.LayoutInflater.from(activity);
            android.view.View fragView = callOnCreateView(fragment, fragClass, inflater, container);
            if (fragView == null) {
                return false;
            }
            container.removeAllViews();
            container.addView(fragView);
            callFragmentLifecycle(fragment, fragClass, fragView);
            populateListViews(fragView);
            return true;
        } catch (Throwable t) {
            Log.d(TAG, "  attachRecoveredFragment failed: " + t);
            return false;
        }
    }

    /** Set the mActivity/mHost field on a Fragment so getActivity() works */
    private void attachFragmentToActivity(Object fragment, Class<?> fragClass, Activity activity) {
        boolean attachedViaCallback = false;
        // Walk up the class hierarchy looking for mActivity or mHost fields
        for (Class<?> c = fragClass; c != null && c != Object.class; c = c.getSuperclass()) {
            // Try mActivity (framework Fragment)
            try {
                java.lang.reflect.Field f = c.getDeclaredField("mActivity");
                f.setAccessible(true);
                f.set(fragment, activity);
                Log.d(TAG, "  attachFragment: set mActivity on " + c.getSimpleName());
                return;
            } catch (NoSuchFieldException e) { /* try next */ }
            catch (Exception e) { /* try next */ }

            // Try mHost (support library Fragment uses FragmentHostCallback)
            // For support library, we need to call onAttach(Activity) instead
        }
        // Fallback: try calling onAttach(Activity) via reflection
        try {
            java.lang.reflect.Method onAttach = null;
            for (Class<?> c = fragClass; c != null; c = c.getSuperclass()) {
                try {
                    onAttach = c.getDeclaredMethod("onAttach", Activity.class);
                    break;
                } catch (NoSuchMethodException e) { /* try parent */ }
            }
            if (onAttach != null) {
                onAttach.setAccessible(true);
                onAttach.invoke(fragment, activity);
                attachedViaCallback = true;
                Log.d(TAG, "  attachFragment: called onAttach(Activity)");
            }
        } catch (Exception e) {
            Log.d(TAG, "  attachFragment: onAttach failed: " + e.getMessage());
        }
        if (!attachedViaCallback) {
            try {
                java.lang.reflect.Method onAttach = null;
                for (Class<?> c = fragClass; c != null; c = c.getSuperclass()) {
                    try {
                        onAttach = c.getDeclaredMethod("onAttach", android.content.Context.class);
                        break;
                    } catch (NoSuchMethodException e) { /* try parent */ }
                }
                if (onAttach != null) {
                    onAttach.setAccessible(true);
                    onAttach.invoke(fragment, activity);
                    Log.d(TAG, "  attachFragment: called onAttach(Context)");
                }
            } catch (Exception e) {
                Log.d(TAG, "  attachFragment: onAttach(Context) failed: " + e.getMessage());
            }
        }
    }

    private android.view.View callOnCreateView(Object fragment, Class<?> fragClass,
            android.view.LayoutInflater inflater, android.view.ViewGroup container) {
        for (Class<?> fc = fragClass; fc != null; fc = fc.getSuperclass()) {
            try {
                java.lang.reflect.Method ocv = fc.getDeclaredMethod("onCreateView",
                    android.view.LayoutInflater.class, android.view.ViewGroup.class, android.os.Bundle.class);
                ocv.setAccessible(true);
                return (android.view.View) ocv.invoke(fragment, inflater, container, null);
            } catch (NoSuchMethodException e) { /* try parent */ }
            catch (Exception e) {
                Throwable cause = e.getCause() != null ? e.getCause() : e;
                Log.w(TAG, "  callOnCreateView failed: " + cause);
                cause.printStackTrace();
                return null;
            }
        }
        return null;
    }

    private void callFragmentCreate(Object fragment, Class<?> fragClass) {
        tryCallMethod(fragment, fragClass, "onCreate",
                new Class<?>[]{android.os.Bundle.class},
                new Object[]{(android.os.Bundle) null});
    }

    /**
     * Call Fragment lifecycle methods that happen after onCreateView:
     * onViewCreated, onActivityCreated, onStart, onResume.
     * This is where adapters/data are typically set up.
     */
    private void callFragmentLifecycle(Object fragment, Class<?> fragClass, android.view.View view) {
        // onViewCreated(View, Bundle)
        tryCallMethod(fragment, fragClass, "onViewCreated",
            new Class<?>[]{android.view.View.class, android.os.Bundle.class},
            new Object[]{view, null});

        // onActivityCreated(Bundle) — this is where Counter sets up its adapter
        tryCallMethod(fragment, fragClass, "onActivityCreated",
            new Class<?>[]{android.os.Bundle.class},
            new Object[]{(android.os.Bundle) null});

        // onStart()
        tryCallMethod(fragment, fragClass, "onStart", new Class<?>[0], new Object[0]);

        // onResume()
        tryCallMethod(fragment, fragClass, "onResume", new Class<?>[0], new Object[0]);
    }

    private void tryCallMethod(Object obj, Class<?> cls, String name, Class<?>[] paramTypes, Object[] args) {
        for (Class<?> c = cls; c != null && c != Object.class; c = c.getSuperclass()) {
            try {
                java.lang.reflect.Method m = c.getDeclaredMethod(name, paramTypes);
                m.setAccessible(true);
                m.invoke(obj, args);
                Log.i(TAG, "  fragment lifecycle: " + name + " OK");
                return;
            } catch (NoSuchMethodException e) { /* try parent */ }
            catch (Exception e) {
                Log.w(TAG, "  fragment lifecycle: " + name + " failed: " + e.getMessage());
                return;
            }
        }
    }

    /**
     * Manually populate ListViews by calling adapter.getView() for each item.
     * The AOSP ListView.layoutChildren() is too complex for our shim,
     * so we do it explicitly after the adapter is set.
     */
    private void populateListViews(android.view.View root) {
        if (root instanceof android.widget.ListView) {
            android.widget.ListView lv = (android.widget.ListView) root;
            android.widget.ListAdapter adapter = lv.getAdapter();
            if (adapter != null && adapter.getCount() > 0 && lv.getChildCount() == 0) {
                int count = adapter.getCount();
                Log.i(TAG, "  populateListViews: " + count + " items in " + lv);
                for (int i = 0; i < count; i++) {
                    try {
                        android.view.View itemView = adapter.getView(i, null, lv);
                        if (itemView != null) {
                            lv.addView(itemView);
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "  populateListViews: getView(" + i + ") failed: " + e.getMessage());
                        break;
                    }
                }
            }
        }
        if (root instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) root;
            for (int i = 0; i < vg.getChildCount(); i++) {
                populateListViews(vg.getChildAt(i));
            }
        }
    }

    /** Find the first FrameLayout child with an ID that has no children */
    private int findEmptyFrameLayoutId(android.view.View v) {
        if (v instanceof android.widget.FrameLayout) {
            android.widget.FrameLayout fl = (android.widget.FrameLayout) v;
            if (fl.getId() != android.view.View.NO_ID && fl.getChildCount() == 0) {
                return fl.getId();
            }
        }
        if (v instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                int id = findEmptyFrameLayoutId(vg.getChildAt(i));
                if (id != 0) return id;
            }
        }
        return 0;
    }

    private int findEmptyViewGroupId(android.view.View v) {
        if (v instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) v;
            if (vg.getId() != android.view.View.NO_ID && vg.getChildCount() == 0) {
                return vg.getId();
            }
            for (int i = 0; i < vg.getChildCount(); i++) {
                int id = findEmptyViewGroupId(vg.getChildAt(i));
                if (id != 0) return id;
            }
        }
        return 0;
    }

    /**
     * Find and initialize any null SharedPreferences fields on the activity.
     * When onCreate NPEs before SP initialization, these fields stay null.
     */
    private void initNullSharedPrefsFields(Activity activity) {
        try {
            for (java.lang.reflect.Field f : activity.getClass().getDeclaredFields()) {
                if (f.getType().getName().equals("android.content.SharedPreferences")) {
                    f.setAccessible(true);
                    Object val = f.get(activity);
                    if (val == null) {
                        // Initialize with default SharedPreferences
                        android.content.SharedPreferences sp =
                            android.preference.PreferenceManager.getDefaultSharedPreferences(activity);
                        f.set(activity, sp);
                        Log.i(TAG, "  initNullSharedPrefsFields: initialized " + f.getName());
                    }
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "  initNullSharedPrefsFields error: " + e);
        }
    }

    private void performStart(ActivityRecord r) {
        Log.d(TAG, "  performStart: " + r.component.getClassName());
        ShimCompat.setActivityField(r.activity, "mStarted", Boolean.TRUE);
        // Run onStart with timeout (Fragment lifecycle can hang in interpreter)
        final Activity actRef = r.activity;
        final boolean[] startDone = { false };
        Thread startThread = new Thread(new Runnable() {
            public void run() {
                Thread.currentThread().setContextClassLoader(resolveAppClassLoader());
                try {
                    java.lang.reflect.Method m = Activity.class.getDeclaredMethod("onStart");
                    m.setAccessible(true);
                    m.invoke(actRef);
                    startDone[0] = true;
                } catch (Throwable e) { startDone[0] = true; Log.w(TAG, "onStart error: " + e.getMessage()); }
            }
        }, "ActivityOnStart");
        startThread.setDaemon(true);
        startThread.start();
        try { startThread.join(10000); } catch (InterruptedException ie) {}
        if (!startDone[0]) Log.w(TAG, "performStart TIMEOUT (10s) for " + r.component.getClassName());
        try {
            dispatchLifecycleEvent(r.activity, "ON_START");
        } catch (Exception e) {
            Log.w(TAG, "performStart lifecycle dispatch error: " + e.getMessage());
        }
    }

    private void performResume(ActivityRecord r) {
        Log.d(TAG, "  performResume: " + r.component.getClassName());
        ShimCompat.setActivityField(r.activity, "mResumed", Boolean.TRUE);
        mResumed = r;
        final Activity actRef = r.activity;
        final boolean[] resumeDone = { false };
        Thread resumeThread = new Thread(new Runnable() {
            public void run() {
                Thread.currentThread().setContextClassLoader(resolveAppClassLoader());
                try {
                    java.lang.reflect.Method m = Activity.class.getDeclaredMethod("onResume");
                    m.setAccessible(true);
                    m.invoke(actRef);
                    resumeDone[0] = true;
                } catch (Throwable e) { resumeDone[0] = true; Log.w(TAG, "onResume error: " + e.getMessage()); }
            }
        }, "ActivityOnResume");
        resumeThread.setDaemon(true);
        resumeThread.start();
        try { resumeThread.join(10000); } catch (InterruptedException ie) {}
        if (!resumeDone[0]) Log.w(TAG, "performResume TIMEOUT (10s) for " + r.component.getClassName());
        try {
            java.lang.reflect.Method m = Activity.class.getDeclaredMethod("onPostResume");
            m.setAccessible(true);
            m.invoke(r.activity);
        } catch (Throwable e) {
            Log.w(TAG, "onPostResume error (non-fatal): " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        try {
            dispatchLifecycleEvent(r.activity, "ON_RESUME");
        } catch (Throwable e) {
            Log.w(TAG, "performResume lifecycle dispatch error: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        Log.d(TAG, "  performResume completed for " + r.component.getClassName());
    }

    private void performPause(ActivityRecord r) {
        Log.d(TAG, "  performPause: " + r.component.getClassName());
        dispatchLifecycleEvent(r.activity, "ON_PAUSE");
        ShimCompat.setActivityField(r.activity, "mResumed", Boolean.FALSE);
        try {
            r.activity.onPause();
        } catch (IllegalAccessError e) {
            try {
                java.lang.reflect.Method m = Activity.class.getDeclaredMethod("onPause");
                m.setAccessible(true);
                m.invoke(r.activity);
            } catch (Exception ex) { Log.e(TAG, "performonPause reflection failed: " + ex); }
        }
        if (r == mResumed) {
            mResumed = null;
        }
    }

    private void performStop(ActivityRecord r) {
        if (ShimCompat.getActivityBooleanField(r.activity, "mStarted", false) == false) return;
        Log.d(TAG, "  performStop: " + r.component.getClassName());
        ShimCompat.setActivityField(r.activity, "mStarted", Boolean.FALSE);
        try {
            r.activity.onStop();
        } catch (IllegalAccessError e) {
            try {
                java.lang.reflect.Method m = Activity.class.getDeclaredMethod("onStop");
                m.setAccessible(true);
                m.invoke(r.activity);
            } catch (Exception ex) { Log.e(TAG, "performonStop reflection failed: " + ex); }
        }
        dispatchLifecycleEvent(r.activity, "ON_STOP");
    }

    private void performDestroy(ActivityRecord r) {
        Log.d(TAG, "  performDestroy: " + r.component.getClassName());
        ShimCompat.setActivityField(r.activity, "mDestroyed", Boolean.TRUE);
        try {
            r.activity.onDestroy();
        } catch (IllegalAccessError e) {
            try {
                java.lang.reflect.Method m = Activity.class.getDeclaredMethod("onDestroy");
                m.setAccessible(true);
                m.invoke(r.activity);
            } catch (Exception ex) { Log.e(TAG, "performonDestroy reflection failed: " + ex); }
        }
    }

    private void performRestart(ActivityRecord r) {
        Log.d(TAG, "  performRestart: " + r.component.getClassName());
        try {
            r.activity.onRestart();
        } catch (IllegalAccessError e) {
            try {
                java.lang.reflect.Method m = Activity.class.getDeclaredMethod("onRestart");
                m.setAccessible(true);
                m.invoke(r.activity);
            } catch (Exception ex) { /* optional */ }
        }
    }

    // ── Internal ────────────────────────────────────────────────────────────

    private ActivityRecord findRecord(Activity activity) {
        for (int i = mStack.size() - 1; i >= 0; i--) {
            if (mStack.get(i).activity == activity) {
                return mStack.get(i);
            }
        }
        return null;
    }

    /** Internal record tracking an Activity's state. */
    /** Dispatch lifecycle events — no-op for now, will be wired via Compose's lifecycle */
    private void dispatchLifecycleEvent(Activity activity, String eventName) {}
    private void dispatchLifecycleEvent(Activity activity, String action, Bundle state) {}

    static class ActivityRecord {
        Activity activity;
        Intent intent;
        ComponentName component;
        ActivityRecord caller;
        int requestCode = -1;
    }
}
