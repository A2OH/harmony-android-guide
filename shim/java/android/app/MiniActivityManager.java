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
    private static final String CUTOFF_CANARY_PACKAGE = "com.westlake.cutoffcanary";
    private static final String CUTOFF_CANARY_ACTIVITY =
            "com.westlake.cutoffcanary.StageActivity";
    private static final String CUTOFF_CANARY_L3_ACTIVITY =
            "com.westlake.cutoffcanary.L3Activity";
    private static final String CUTOFF_CANARY_L4_ACTIVITY =
            "com.westlake.cutoffcanary.L4Activity";

    private final MiniServer mServer;
    // Avoid the first ArrayList growth path on the control canary launch.
    private final ArrayList<ActivityRecord> mStack = new ArrayList<>(16);
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

    private static boolean isCutoffCanaryComponent(ComponentName component) {
        if (component == null) {
            return false;
        }
        String className = component.getClassName();
        String packageName = component.getPackageName();
        return CUTOFF_CANARY_ACTIVITY.equals(className)
                || CUTOFF_CANARY_L3_ACTIVITY.equals(className)
                || CUTOFF_CANARY_L4_ACTIVITY.equals(className)
                || CUTOFF_CANARY_PACKAGE.equals(packageName);
    }

    private static boolean isCutoffCanaryRecord(ActivityRecord record) {
        return record != null && isCutoffCanaryComponent(record.component);
    }

    private static boolean isControlledWestlakeComponent(ComponentName component) {
        if (component == null) {
            return false;
        }
        String packageName = component.getPackageName();
        String className = component.getClassName();
        return (packageName != null && packageName.startsWith("com.westlake."))
                || (className != null && className.startsWith("com.westlake."));
    }

    private static boolean isControlledWestlakeRecord(ActivityRecord record) {
        return record != null && isControlledWestlakeComponent(record.component);
    }

    private static boolean shouldRunLegacyMcdBootstrap(ComponentName component,
            String packageName, String className) {
        String componentPackage = component != null ? component.getPackageName() : null;
        String componentClass = component != null ? component.getClassName() : null;
        return isLegacyMcdName(componentPackage)
                || isLegacyMcdName(componentClass)
                || isLegacyMcdName(packageName)
                || isLegacyMcdName(className);
    }

    private static boolean isLegacyMcdName(String name) {
        return name != null && name.contains("mcdonalds");
    }

    private static boolean isMcdOrderProductDetailsRecord(ActivityRecord record) {
        String className = record != null && record.component != null
                ? record.component.getClassName() : null;
        return "com.mcdonalds.order.activity.OrderProductDetailsActivity".equals(className);
    }

    private ClassLoader resolveAppClassLoader() {
        final boolean strictStandalone =
                !com.westlake.engine.WestlakeLauncher.isRealFrameworkFallbackAllowed();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (!isBootClassLoader(cl)) return cl;
        try {
            cl = com.westlake.engine.WestlakeLauncher.safeGuestFallbackClassLoader();
            if (!isBootClassLoader(cl)) {
                Thread.currentThread().setContextClassLoader(cl);
                return cl;
            }
        } catch (Throwable ignored) {
        }
        if (!strictStandalone) {
            try {
                cl = ClassLoader.getSystemClassLoader();
                if (!isBootClassLoader(cl)) {
                    Thread.currentThread().setContextClassLoader(cl);
                    return cl;
                }
            } catch (Throwable ignored) {
            }
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

    private void invokeActivityLifecycleDirect(Activity activity, String methodName,
            Class<?>[] parameterTypes, Object[] args) throws Throwable {
        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        ClassLoader appCl = resolveAppClassLoader();
        if (!isBootClassLoader(appCl)) {
            Thread.currentThread().setContextClassLoader(appCl);
        }
        try {
            java.lang.reflect.Method method = Activity.class.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            method.invoke(activity, args);
        } catch (java.lang.reflect.InvocationTargetException ite) {
            Throwable cause = ite.getCause();
            throw cause != null ? cause : ite;
        } finally {
            Thread.currentThread().setContextClassLoader(previous);
        }
    }

    private static Object getUnsafeSingleton(String className) throws Throwable {
        Class<?> unsafeClass = Class.forName(className);
        java.lang.reflect.Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        return unsafeField.get(null);
    }

    /* PF-arch-032: find <activity android:theme="@..."> matching className. */
    private static int findActivityTheme(com.westlake.engine.WestlakeNode manifest, String className) {
        if (manifest == null || className == null) return 0;
        com.westlake.engine.WestlakeNode app = childByTag(manifest, "application");
        if (app == null) return 0;
        String pkg = manifest.getAttr("package");
        for (com.westlake.engine.WestlakeNode act : app.children) {
            if (!"activity".equals(act.tag) && !"activity-alias".equals(act.tag)) continue;
            String name = act.getAttr("name");
            if (name == null) continue;
            /* Per android:name spec: a leading '.' is shorthand for the
             * manifest package. Normalize before comparing to className
             * (which is always the fully-qualified runtime class name). */
            String normalized = name;
            if (name.startsWith(".") && pkg != null && !pkg.isEmpty()) {
                normalized = pkg + name;
            } else if (!name.contains(".") && pkg != null && !pkg.isEmpty()) {
                normalized = pkg + "." + name;
            }
            if (!normalized.equals(className)
                    && !name.equals(className)
                    && !endsWithAfterDot(name, className)
                    && !endsWithAfterDot(className, name)) {
                continue;
            }
            String theme = act.getAttr("theme");
            int tid = parseRefId(theme);
            if (tid != 0) return tid;
        }
        return 0;
    }

    /* PF-arch-032: find <application android:theme="@...">. */
    private static int findApplicationTheme(com.westlake.engine.WestlakeNode manifest) {
        com.westlake.engine.WestlakeNode app = childByTag(manifest, "application");
        if (app == null) return 0;
        return parseRefId(app.getAttr("theme"));
    }

    private static com.westlake.engine.WestlakeNode childByTag(
            com.westlake.engine.WestlakeNode parent, String tag) {
        if (parent == null) return null;
        for (com.westlake.engine.WestlakeNode c : parent.children) {
            if (tag.equals(c.tag)) return c;
        }
        return null;
    }

    private static boolean endsWithAfterDot(String full, String tail) {
        if (full == null || tail == null) return false;
        if (full.isEmpty() || tail.isEmpty()) return false;
        /* Strip leading '.' on either side so "<pkg>.MainActivity" matches
         * a manifest declaration of ".MainActivity" (the shorthand for
         * package-local Activity names). */
        String f = full.startsWith(".") ? full.substring(1) : full;
        String t = tail.startsWith(".") ? tail.substring(1) : tail;
        if (f.isEmpty() || t.isEmpty()) return false;
        return f.endsWith("." + t) || t.endsWith("." + f);
    }

    private static int parseRefId(String raw) {
        if (raw == null || raw.isEmpty()) return 0;
        String s = raw;
        if (s.startsWith("@") || s.startsWith("?")) s = s.substring(1);
        if (s.startsWith("0x") || s.startsWith("0X")) {
            try { return (int) Long.parseLong(s.substring(2), 16); }
            catch (NumberFormatException nfe) { return 0; }
        }
        try { return (int) Long.parseLong(s); }
        catch (NumberFormatException nfe) { return 0; }
    }

    /* PF-arch-026: read entire file as bytes, max 4MB. */
    private static byte[] readFileBytes(java.io.File f) {
        if (f == null || !f.isFile()) return null;
        try {
            java.io.FileInputStream fis = new java.io.FileInputStream(f);
            try {
                long size = f.length();
                if (size <= 0 || size > 4 * 1024 * 1024) return null;
                byte[] buf = new byte[(int) size];
                int off = 0;
                while (off < buf.length) {
                    int r = fis.read(buf, off, buf.length - off);
                    if (r < 0) break;
                    off += r;
                }
                return buf;
            } finally {
                try { fis.close(); } catch (java.io.IOException ignored) {}
            }
        } catch (java.io.IOException ioe) {
            return null;
        }
    }

    /* PF-arch-026: read extracted res/layout/<name>.xml bytes (binary AXML). */
    private static byte[] tryReadAxml(String resDir, String layoutName) {
        if (resDir == null || layoutName == null) return null;
        String[] subdirs = {
                "res/layout",
                "res/layout-v21",
                "res/layout-v23",
                "res/layout-port",
        };
        for (String sub : subdirs) {
            String path = resDir + "/" + sub + "/" + layoutName + ".xml";
            java.io.File f = new java.io.File(path);
            if (!f.isFile()) continue;
            try {
                java.io.FileInputStream fis = new java.io.FileInputStream(f);
                try {
                    long size = f.length();
                    if (size <= 0 || size > 4 * 1024 * 1024) return null;
                    byte[] buf = new byte[(int) size];
                    int off = 0;
                    while (off < buf.length) {
                        int r = fis.read(buf, off, buf.length - off);
                        if (r < 0) break;
                        off += r;
                    }
                    return buf;
                } finally {
                    try { fis.close(); } catch (java.io.IOException ignored) {}
                }
            } catch (java.io.IOException ioe) {
                /* try next */
            }
        }
        return null;
    }

    private Activity instantiateActivity(Class<?> cls, String className, Intent intent) throws Throwable {
        /* Historical note (PF-arch-017): previously this path used
         * sun.misc.Unsafe.allocateInstance(cls) to bypass the Activity
         * no-arg constructor entirely, because real ctors (Hilt/DI init,
         * AppCompatDelegate.attachBaseContext2(null), etc.) hit NPEs on
         * a not-yet-attached Context and ART's long-jump could not
         * deliver the exception — Context vtable came back NULL and
         * artContextCopyForLongJump aborted.
         *
         * CR62 (commit 459cb133) resolved the underlying issue at the
         * source by pre-attaching a Context via thread-local before any
         * super-chain ctor runs, so the real constructor path can now
         * complete normally. The Unsafe.allocateInstance bypass +
         * setAccessible fallback both violated the macro-shim contract
         * (feedback_macro_shim_contract.md) and are removed here.
         *
         * Activity has a public no-arg ctor, and every concrete subclass
         * resolved through this code path is itself public, so the
         * default reflection lookup needs no setAccessible call. If a
         * specific Activity legitimately has no public no-arg ctor, that
         * is a configuration error to surface, not to silently bypass. */
        java.lang.reflect.Constructor<?> ctor = cls.getDeclaredConstructor();
        return (Activity) ctor.newInstance();
    }

    private void attachBaseContextFallback(Activity activity) throws Throwable {
        boolean traceCanary = activity != null
                && CUTOFF_CANARY_ACTIVITY.equals(activity.getClass().getName());
        Class<?> atClass = Class.forName("android.app.ActivityThread");
        java.lang.reflect.Field currentAtField = atClass.getDeclaredField("sCurrentActivityThread");
        currentAtField.setAccessible(true);
        Object at = currentAtField.get(null);
        Object ctx = com.westlake.engine.WestlakeLauncher.sRealContext;
        if (ctx == null && at != null) {
            ctx = atClass.getDeclaredMethod("getSystemContext").invoke(at);
        }
        if (ctx == null) {
            if (traceCanary) {
                Log.e(TAG, "CV attachFallback no context");
            }
            return;
        }
        java.lang.reflect.Method attachBase = android.content.ContextWrapper.class
                .getDeclaredMethod("attachBaseContext", android.content.Context.class);
        attachBase.setAccessible(true);
        if (traceCanary) {
            Log.e(TAG, "CV attachFallback before attachBase");
        }
        attachBase.invoke(activity, ctx);
        if (traceCanary) {
            Log.e(TAG, "CV attachFallback after attachBase");
        }
        if (traceCanary) {
            Log.e(TAG, "CV attachFallback before PhoneWindow");
        }
        Object pw = Class.forName("com.android.internal.policy.PhoneWindow")
                .getConstructor(android.content.Context.class)
                .newInstance(ctx);
        if (traceCanary) {
            Log.e(TAG, "CV attachFallback after PhoneWindow");
        }
        java.lang.reflect.Field windowField = Activity.class.getDeclaredField("mWindow");
        windowField.setAccessible(true);
        windowField.set(activity, pw);
        ((android.view.Window) pw).setCallback(activity);
        if (traceCanary) {
            Log.e(TAG, "CV attachFallback after window set");
        }
    }

    private Object ensureSingletonComponent() {
        Object singleton = dagger.hilt.android.internal.managers.ApplicationComponentManager.singletonComponent;
        if (singleton != null) {
            return singleton;
        }

        Application app = mServer.getApplication();
        if (app == null) {
            return null;
        }

        ClassLoader prior = Thread.currentThread().getContextClassLoader();
        ClassLoader appCl = app.getClass().getClassLoader();
        if (isBootClassLoader(appCl)) {
            appCl = resolveAppClassLoader();
        }
        if (!isBootClassLoader(appCl)) {
            Thread.currentThread().setContextClassLoader(appCl);
        }
        try {
            Object manager = null;
            // V2-Step3 (CR28-builder, 2026-05-13): componentManager() / b() were
            // Hilt-obfuscated accessors on the V1 Application shim; the V2
            // Application is generic so we look them up reflectively only.
            try {
                java.lang.reflect.Method m = app.getClass().getMethod("componentManager");
                manager = m.invoke(app);
            } catch (Throwable ignored) {
            }
            if (manager == null) {
                try {
                    java.lang.reflect.Method m = app.getClass().getMethod("b");
                    manager = m.invoke(app);
                } catch (Throwable ignored) {
                }
            }
            if (manager == null) {
                Log.w(TAG, "  ensureSingletonComponent: no application component manager");
                return null;
            }

            Object component = null;
            try {
                if (manager instanceof dagger.hilt.android.internal.managers.ApplicationComponentManager) {
                    component = ((dagger.hilt.android.internal.managers.ApplicationComponentManager) manager)
                            .generatedComponent();
                } else {
                    try {
                        java.lang.reflect.Method m = manager.getClass().getMethod("generatedComponent");
                        component = m.invoke(manager);
                    } catch (NoSuchMethodException e) {
                        java.lang.reflect.Method m = manager.getClass().getMethod("a");
                        component = m.invoke(manager);
                    }
                }
            } catch (Throwable t) {
                Log.w(TAG, "  ensureSingletonComponent failed: "
                        + t.getClass().getSimpleName() + ": " + t.getMessage());
            }

            singleton = dagger.hilt.android.internal.managers.ApplicationComponentManager.singletonComponent;
            if (singleton == null) {
                singleton = component;
            }
            Log.d(TAG, "  ensureSingletonComponent: "
                    + (singleton != null ? singleton.getClass().getName() : "NULL"));
            return singleton;
        } finally {
            Thread.currentThread().setContextClassLoader(prior);
        }
    }

    private void seedPreCreateDatasourceState(Activity activity, Object singleton) {
        Application app = mServer.getApplication();
        if (app == null) {
            return;
        }

        ClassLoader appCl = app.getClass().getClassLoader();
        if (isBootClassLoader(appCl)) {
            appCl = activity != null ? activity.getClass().getClassLoader() : resolveAppClassLoader();
        }
        if (isBootClassLoader(appCl)) {
            appCl = resolveAppClassLoader();
        }
        if (isBootClassLoader(appCl)) {
            Log.w(TAG, "  seedPreCreateDatasourceState: no app class loader");
            return;
        }

        ClassLoader prior = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(appCl);
        try {
            seedCoroutineSchedulerProperties();
            WestlakeActivityThread.seedCoroutineMainDispatcher(appCl);
            WestlakeActivityThread.seedMcdonaldsAppConfigurationState(appCl);
            WestlakeActivityThread.seedMcdonaldsJustFlipContextState(appCl, app);
            WestlakeActivityThread.seedMcdonaldsJustFlipState(appCl);

            try {
                Class<?> appCtx = appCl.loadClass("com.mcdonalds.mcdcoreapp.common.ApplicationContext");
                try {
                    java.lang.reflect.Method setter = appCtx.getDeclaredMethod(
                            "b", android.content.Context.class, boolean.class);
                    setter.setAccessible(true);
                    setter.invoke(null, app, Boolean.TRUE);
                    Log.d(TAG, "  ApplicationContext.b(application,true) OK");
                } catch (Throwable setterError) {
                    Log.d(TAG, "  ApplicationContext setter skipped: "
                            + setterError.getClass().getSimpleName());
                }
                java.lang.reflect.Field ctxField = appCtx.getDeclaredField("a");
                ctxField.setAccessible(true);
                ctxField.set(null, app);
                Log.d(TAG, "  ApplicationContext.a = " + app.getClass().getSimpleName());
            } catch (Throwable t) {
                Log.d(TAG, "  ApplicationContext seed skipped: " + t.getClass().getSimpleName());
            }

            Class<?> helperClass = appCl.loadClass("com.mcdonalds.mcdcoreapp.common.model.DataSourceHelper");
            try {
                java.lang.reflect.Method init = helperClass.getDeclaredMethod(
                        "init", android.content.Context.class);
                init.setAccessible(true);
                init.invoke(null, app);
                Log.d(TAG, "  DataSourceHelper.init(application) OK");
            } catch (NoSuchMethodException ignored) {
            } catch (Throwable t) {
                Log.d(TAG, "  DataSourceHelper.init(application) failed: "
                        + t.getClass().getSimpleName() + ": " + t.getMessage());
            }

            int seeded = 0;
            for (java.lang.reflect.Field f : helperClass.getDeclaredFields()) {
                if (!java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
                f.setAccessible(true);
                Class<?> fType = f.getType();
                Object current = f.get(null);
                try {
                    if (singleton != null
                            && fType.isInstance(singleton)
                            && current != singleton
                            && (current == null
                                || fType.getName().contains("DataSourceModuleProvider"))) {
                        f.set(null, singleton);
                        Log.d(TAG, "  DataSourceHelper." + f.getName() + " = singleton");
                        seeded++;
                        current = singleton;
                    }
                    if (current != null) continue;
                    if (fType == boolean.class) {
                        f.setBoolean(null, true);
                        seeded++;
                        continue;
                    }
                    if (singleton != null && fType.isInstance(singleton)) {
                        f.set(null, singleton);
                        Log.d(TAG, "  DataSourceHelper." + f.getName() + " = singleton");
                        seeded++;
                        continue;
                    }
                    if (singleton != null && !fType.isPrimitive()) {
                        for (java.lang.reflect.Method m : singleton.getClass().getMethods()) {
                            if (m.getParameterTypes().length != 0) continue;
                            if (!fType.isAssignableFrom(m.getReturnType())) continue;
                            Object value = m.invoke(singleton);
                            if (value != null) {
                                f.set(null, value);
                                Log.d(TAG, "  DataSourceHelper." + f.getName()
                                        + " = singleton." + m.getName() + "()");
                                seeded++;
                                break;
                            }
                        }
                        if (f.get(null) != null) {
                            continue;
                        }
                    }
                    if (fType.isInterface()) {
                        Object proxy = dagger.hilt.android.internal.managers.ActivityComponentManager
                                .createInterfaceProxy(fType);
                        if (proxy != null) {
                            f.set(null, proxy);
                            Log.d(TAG, "  DataSourceHelper." + f.getName() + " = proxy");
                            seeded++;
                        }
                    } else if (!fType.isPrimitive() && fType != String.class) {
                        try {
                            java.lang.reflect.Constructor<?> ctor = fType.getDeclaredConstructor();
                            ctor.setAccessible(true);
                            f.set(null, ctor.newInstance());
                            Log.d(TAG, "  DataSourceHelper." + f.getName() + " = new "
                                    + fType.getSimpleName() + "()");
                            seeded++;
                        } catch (Throwable ignored) {
                        }
                    }
                } catch (Throwable perField) {
                    Log.d(TAG, "  DataSourceHelper." + f.getName() + " seed failed: "
                            + perField.getClass().getSimpleName());
                }
            }
            if (seeded > 0) {
                Log.d(TAG, "  DataSourceHelper seeded fields=" + seeded);
            }

            Log.d(TAG, "  DataSourceHelper getter verification skipped in standalone bootstrap");

            seedRealAppClickstreamBootstrap(appCl, app, singleton);

            Log.d(TAG, "  ClickstreamDataHelper singleton bootstrap skipped in standalone bootstrap");

            Log.d(TAG, "  DataSourceHelper Crypto repair skipped in standalone bootstrap");
        } catch (Throwable t) {
            Log.w(TAG, "  seedPreCreateDatasourceState failed: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        } finally {
            Thread.currentThread().setContextClassLoader(prior);
        }
    }

    private void seedCoroutineSchedulerProperties() {
        String core = System.getProperty("kotlinx.coroutines.scheduler.core.pool.size");
        String max = System.getProperty("kotlinx.coroutines.scheduler.max.pool.size");
        if (core == null || core.isEmpty()) {
            System.setProperty("kotlinx.coroutines.scheduler.core.pool.size", "2");
        }
        if (max == null || max.isEmpty()) {
            System.setProperty("kotlinx.coroutines.scheduler.max.pool.size", "4");
        }
        String io = System.getProperty("kotlinx.coroutines.io.parallelism");
        if (io == null || io.isEmpty()) {
            System.setProperty("kotlinx.coroutines.io.parallelism", "4");
        }
        Log.d(TAG, "  Coroutine scheduler props: core="
                + System.getProperty("kotlinx.coroutines.scheduler.core.pool.size")
                + " max=" + System.getProperty("kotlinx.coroutines.scheduler.max.pool.size")
                + " io=" + System.getProperty("kotlinx.coroutines.io.parallelism"));
    }

    private void initStaticHelperFromSingleton(ClassLoader appCl, Object singleton, String className) {
        if (appCl == null || singleton == null) {
            return;
        }
        try {
            Class<?> helperClass = appCl.loadClass(className);
            int seeded = 0;
            for (java.lang.reflect.Field f : helperClass.getDeclaredFields()) {
                if (!java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
                f.setAccessible(true);
                if (f.get(null) != null) continue;
                Class<?> fType = f.getType();
                if (fType == boolean.class) {
                    f.setBoolean(null, true);
                    seeded++;
                    continue;
                }
                if (fType == String.class) {
                    f.set(null, "");
                    seeded++;
                    continue;
                }
                if (fType.isPrimitive()) continue;
                if (fType.isInstance(singleton)) {
                    f.set(null, singleton);
                    seeded++;
                    continue;
                }
                for (java.lang.reflect.Method m : singleton.getClass().getMethods()) {
                    try {
                        if (m.getParameterTypes().length != 0) continue;
                        if (!fType.isAssignableFrom(m.getReturnType())) continue;
                        Object value = m.invoke(singleton);
                        if (value != null) {
                            f.set(null, value);
                            seeded++;
                            break;
                        }
                    } catch (Throwable ignored) {
                    }
                }
                if (f.get(null) == null) {
                    Object stub = createStubValue(fType, appCl);
                    if (stub != null) {
                        f.set(null, stub);
                        seeded++;
                    }
                }
            }
            Log.d(TAG, "  " + helperClass.getSimpleName() + " seeded fields=" + seeded);
        } catch (Throwable t) {
            Log.d(TAG, "  " + className + " seed failed: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    private void seedRealAppClickstreamBootstrap(ClassLoader appCl, Application app, Object singleton) {
        if (appCl == null || app == null) {
            return;
        }
        try {
            Class<?> analyticsHelperClass = appCl.loadClass("com.mcdonalds.app.core.AnalyticsDataCoreHelper");
            Object analyticsHelper = analyticsHelperClass.getDeclaredConstructor().newInstance();
            java.lang.reflect.Method m =
                    analyticsHelperClass.getDeclaredMethod("a", android.app.Application.class);
            m.setAccessible(true);
            m.invoke(analyticsHelper, app);
            Log.d(TAG, "  AnalyticsDataCoreHelper.a(application) OK");
        } catch (Throwable t) {
            Log.d(TAG, "  AnalyticsDataCoreHelper bootstrap skipped: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }

        Object clickstreamDomain = resolveClickstreamDomain(app, singleton);
        if (clickstreamDomain == null) {
            Log.d(TAG, "  ClickstreamDomain unresolved before ClickstreamCoreHelper");
            return;
        }

        try {
            Class<?> clickstreamHelperClass =
                    appCl.loadClass("com.mcdonalds.app.core.ClickstreamCoreHelper");
            Object clickstreamHelper = clickstreamHelperClass.getDeclaredConstructor().newInstance();
            java.lang.reflect.Method m = clickstreamHelperClass.getDeclaredMethod(
                    "a", android.app.Application.class, clickstreamDomain.getClass());
            m.setAccessible(true);
            Object initializer = m.invoke(clickstreamHelper, app, clickstreamDomain);
            Log.d(TAG, "  ClickstreamCoreHelper.a(application, domain) OK"
                    + (initializer != null ? " -> " + initializer.getClass().getSimpleName() : ""));
        } catch (Throwable t) {
            Log.d(TAG, "  ClickstreamCoreHelper bootstrap failed: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    private Object resolveClickstreamDomain(Application app, Object singleton) {
        try {
            java.lang.reflect.Method m = app.getClass().getMethod("g");
            m.setAccessible(true);
            Object value = m.invoke(app);
            if (value != null
                    && "com.mcdonalds.analytics.domain.ClickstreamDomain"
                    .equals(value.getClass().getName())) {
                Log.d(TAG, "  ClickstreamDomain = application.g()");
                return value;
            }
        } catch (Throwable t) {
            Log.d(TAG, "  ClickstreamDomain application.g() skipped: "
                    + t.getClass().getSimpleName());
        }

        try {
            for (java.lang.reflect.Method m : app.getClass().getMethods()) {
                if (m.getParameterTypes().length != 0) continue;
                if (!"com.mcdonalds.analytics.domain.ClickstreamDomain"
                        .equals(m.getReturnType().getName())) {
                    continue;
                }
                m.setAccessible(true);
                Object value = m.invoke(app);
                if (value != null) {
                    Log.d(TAG, "  ClickstreamDomain = application." + m.getName() + "()");
                    return value;
                }
            }
        } catch (Throwable t) {
            Log.d(TAG, "  ClickstreamDomain method lookup skipped: "
                    + t.getClass().getSimpleName());
        }

        try {
            java.lang.reflect.Field field = app.getClass().getDeclaredField("e");
            field.setAccessible(true);
            Object value = field.get(app);
            if (value != null
                    && "com.mcdonalds.analytics.domain.ClickstreamDomain"
                    .equals(value.getClass().getName())) {
                Log.d(TAG, "  ClickstreamDomain = application.e");
                return value;
            }
        } catch (Throwable t) {
            Log.d(TAG, "  ClickstreamDomain field lookup skipped: "
                    + t.getClass().getSimpleName());
        }

        if (singleton != null) {
            try {
                for (java.lang.reflect.Method m : singleton.getClass().getMethods()) {
                    if (m.getParameterTypes().length != 0) continue;
                    if (!"com.mcdonalds.analytics.domain.ClickstreamDomain"
                            .equals(m.getReturnType().getName())) {
                        continue;
                    }
                    Object value = m.invoke(singleton);
                    if (value != null) {
                        Log.d(TAG, "  ClickstreamDomain = singleton." + m.getName() + "()");
                        return value;
                    }
                }
            } catch (Throwable t) {
                Log.d(TAG, "  ClickstreamDomain singleton lookup skipped: "
                        + t.getClass().getSimpleName());
            }
        }
        return null;
    }

    private void repairCachedDataSourceCrypto(ClassLoader appCl, Application app) {
        if (appCl == null || app == null) {
            return;
        }
        try {
            Class<?> cryptoClass = appCl.loadClass("com.mcdonalds.mcdcoreapp.common.Crypto");
            Object crypto = cryptoClass
                    .getConstructor(android.content.Context.class)
                    .newInstance(app);
            Class<?> helperClass =
                    appCl.loadClass("com.mcdonalds.mcdcoreapp.common.model.DataSourceHelper");
            int repaired = 0;
            for (java.lang.reflect.Field helperField : helperClass.getDeclaredFields()) {
                if (!java.lang.reflect.Modifier.isStatic(helperField.getModifiers())) continue;
                helperField.setAccessible(true);
                Object cached = helperField.get(null);
                if (cached == null) continue;
                for (java.lang.reflect.Field objectField : cached.getClass().getDeclaredFields()) {
                    Class<?> fieldType = objectField.getType();
                    if (fieldType != cryptoClass
                            && !fieldType.getName().contains("Crypto")) {
                        continue;
                    }
                    objectField.setAccessible(true);
                    if (objectField.get(cached) != null) continue;
                    objectField.set(cached, crypto);
                    repaired++;
                }
            }
            if (repaired > 0) {
                Log.d(TAG, "  DataSourceHelper Crypto repaired fields=" + repaired);
            }
        } catch (Throwable t) {
            Log.d(TAG, "  DataSourceHelper Crypto repair skipped: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    private void ensureNonNullDataSourceHelperMethod(Class<?> helperClass, String methodName, Object singleton) {
        try {
            java.lang.reflect.Method method = helperClass.getDeclaredMethod(methodName);
            method.setAccessible(true);
            Object value = tryInvokeDataSourceHelper(method);
            if (value != null) {
                return;
            }

            java.lang.reflect.Field providerField = null;
            try {
                providerField = helperClass.getDeclaredField("dataSourceModuleProvider");
                providerField.setAccessible(true);
            } catch (Throwable ignored) {
            }
            if (providerField != null) {
                Class<?> providerType = providerField.getType();
                if (singleton != null && providerType.isInstance(singleton)) {
                    providerField.set(null, singleton);
                    value = tryInvokeDataSourceHelper(method);
                    if (value != null) {
                        Log.d(TAG, "  DataSourceHelper." + methodName + "() = singleton-backed");
                        return;
                    }
                }
                if (providerType.isInterface()) {
                    Object providerProxy = createDirectDataSourceProviderProxy(
                            providerType, helperClass.getClassLoader());
                    if (providerProxy != null) {
                        providerField.set(null, providerProxy);
                        Log.d(TAG, "  DataSourceHelper.dataSourceModuleProvider = direct proxy");
                        value = tryInvokeDataSourceHelper(method);
                        if (value != null) {
                            Log.d(TAG, "  DataSourceHelper." + methodName + "() = direct-proxy-backed");
                            return;
                        }
                    }
                }
            }

            java.lang.reflect.Field sameTypeField = null;
            for (java.lang.reflect.Field f : helperClass.getDeclaredFields()) {
                if (!java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
                if (f.getType() != method.getReturnType()) continue;
                sameTypeField = f;
                break;
            }
            if (sameTypeField != null) {
                sameTypeField.setAccessible(true);
                if (sameTypeField.get(null) == null) {
                    Object stub = createStubValue(method.getReturnType(), helperClass.getClassLoader());
                    if (stub != null) {
                        sameTypeField.set(null, stub);
                        Log.d(TAG, "  DataSourceHelper." + sameTypeField.getName()
                                + " = stub [via " + methodName + "()]");
                        value = tryInvokeDataSourceHelper(method);
                        if (value != null) {
                            Log.d(TAG, "  DataSourceHelper." + methodName + "() = field-backed proxy");
                            return;
                        }
                    }
                }
            }

            Log.d(TAG, "  DataSourceHelper." + methodName + "() still null");
        } catch (Throwable t) {
            Log.d(TAG, "  DataSourceHelper." + methodName + "() verify failed: "
                    + t.getClass().getSimpleName());
        }
    }

    private Object createDirectDataSourceProviderProxy(final Class<?> providerType, ClassLoader appCl) {
        final ClassLoader cl = appCl != null ? appCl
                : (providerType.getClassLoader() != null
                        ? providerType.getClassLoader()
                        : Thread.currentThread().getContextClassLoader());
        if (cl == null) {
            return null;
        }
        try {
            return java.lang.reflect.Proxy.newProxyInstance(
                    cl,
                    new Class<?>[]{providerType},
                    new java.lang.reflect.InvocationHandler() {
                        private final java.util.Map<String, Object> cache = new java.util.HashMap<>();

                        @Override
                        public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args)
                                throws Throwable {
                            String name = method.getName();
                            if ("toString".equals(name)) return "DirectDataSourceProviderProxy";
                            if ("hashCode".equals(name)) return System.identityHashCode(proxy);
                            if ("equals".equals(name)) return proxy == args[0];
                            String key = name + "->" + method.getReturnType().getName();
                            if (cache.containsKey(key)) {
                                return cache.get(key);
                            }
                            Object stub = createStubValue(method.getReturnType(), cl);
                            cache.put(key, stub);
                            return stub;
                        }
                    });
        } catch (Throwable t) {
            Log.d(TAG, "  direct provider proxy failed: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
            return null;
        }
    }

    private Object createStubValue(Class<?> type, ClassLoader appCl) {
        if (type == null || type == void.class) return null;
        if (type == boolean.class) return false;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0f;
        if (type == double.class) return 0.0;
        if (type == String.class) return "";

        String name = type.getName();
        if ("com.mcdonalds.mcdcoreapp.helper.interfaces.OrderModuleInteractor".equals(name)) {
            return instantiateKnownImpl(type, appCl, "com.mcdonalds.order.util.OrderModuleImplementation");
        }

        if ("com.mcdonalds.mcdcoreapp.helper.interfaces.PaymentModuleInteractor".equals(name)) {
            return instantiateKnownImpl(type, appCl, "com.mcdonalds.payments.PaymentModuleImplementation");
        }

        if ("com.mcdonalds.mcdcoreapp.helper.interfaces.HomeModuleInteractor".equals(name)) {
            return instantiateKnownImpl(type, appCl, "com.mcdonalds.homedashboard.util.HomeHelperImplementation");
        }

        if ("com.mcdonalds.mcdcoreapp.helper.interfaces.AccountProfileInteractor".equals(name)) {
            return instantiateKnownImpl(type, appCl, "com.mcdonalds.account.util.AccountProfileImplementation");
        }

        if ("com.mcdonalds.mcdcoreapp.helper.interfaces.LoyaltyModuleInteractor".equals(name)) {
            return instantiateKnownImpl(type, appCl, "com.mcdonalds.loyalty.dashboard.util.DealsLoyaltyImplementation");
        }

        if ("com.mcdonalds.mcdcoreapp.helper.interfaces.HomeDashboardModuleInteractor".equals(name)) {
            return instantiateKnownImpl(type, appCl, "com.mcdonalds.homedashboard.util.HomeDashboardModuleImpl");
        }

        if ("com.mcdonalds.mcdcoreapp.helper.interfaces.HomeDashboardHeroInteractor".equals(name)) {
            return instantiateKnownImpl(type, appCl, "com.mcdonalds.homedashboard.util.HomeDashboardHeroInteractorImpl");
        }

        if ("com.mcdonalds.homedashboard.util.HomeDashboardHelper".equals(name)) {
            Object helper = instantiateKnownImpl(type, appCl, "com.mcdonalds.homedashboard.util.HomeDashboardHelper");
            if (helper != null) {
                return helper;
            }
        }

        if ("com.mcdonalds.mcdcoreapp.helper.interfaces.LocalCacheManagerDataSource".equals(name)) {
            return createLocalCacheManagerDataSourceProxy(type, appCl);
        }

        if (type.isInterface()) {
            return dagger.hilt.android.internal.managers.ActivityComponentManager
                    .createInterfaceProxy(type);
        }
        if (java.util.List.class.isAssignableFrom(type)) return new java.util.ArrayList();
        if (java.util.Map.class.isAssignableFrom(type)) return new java.util.HashMap();
        if (java.util.Set.class.isAssignableFrom(type)) return new java.util.HashSet();

        try {
            java.lang.reflect.Constructor<?> ctor = type.getDeclaredConstructor();
            ctor.setAccessible(true);
            return ctor.newInstance();
        } catch (Throwable ignored) {
        }
        return null;
    }

    private Object instantiateKnownImpl(Class<?> requestedType, ClassLoader appCl, String implName) {
        ClassLoader cl = appCl != null ? appCl : requestedType.getClassLoader();
        if (cl == null) {
            return null;
        }
        try {
            Class<?> impl = cl.loadClass(implName);
            Object instance = instantiateBestEffort(impl, cl, 2);
            if (instance != null && requestedType.isInstance(instance)) {
                Log.d(TAG, "  " + requestedType.getSimpleName() + " = "
                        + instance.getClass().getSimpleName());
                return instance;
            }
            instance = instantiateWithDefaultArgs(impl);
            if (instance != null && requestedType.isInstance(instance)) {
                Log.d(TAG, "  " + requestedType.getSimpleName() + " = "
                        + instance.getClass().getSimpleName() + " [default args]");
                return instance;
            }
        } catch (Throwable t) {
            Log.d(TAG, "  " + implName.substring(implName.lastIndexOf('.') + 1)
                    + " stub failed: " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
        return null;
    }

    private Object instantiateWithDefaultArgs(Class<?> type) {
        if (type == null || type.isInterface()
                || java.lang.reflect.Modifier.isAbstract(type.getModifiers())) {
            return null;
        }
        for (java.lang.reflect.Constructor<?> ctor : type.getDeclaredConstructors()) {
            try {
                Class<?>[] params = ctor.getParameterTypes();
                Object[] args = new Object[params.length];
                for (int i = 0; i < params.length; i++) {
                    args[i] = defaultCtorArg(params[i]);
                }
                ctor.setAccessible(true);
                return ctor.newInstance(args);
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    private Object defaultCtorArg(Class<?> type) {
        if (type == null) return null;
        if (type == boolean.class) return false;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0f;
        if (type == double.class) return 0.0;
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == char.class) return '\0';
        if (type == String.class || type == CharSequence.class) return "";
        if (type.isArray()) {
            return java.lang.reflect.Array.newInstance(type.getComponentType(), 0);
        }
        if (java.util.List.class.isAssignableFrom(type)) return new java.util.ArrayList();
        if (java.util.Map.class.isAssignableFrom(type)) return new java.util.HashMap();
        if (java.util.Set.class.isAssignableFrom(type)) return new java.util.HashSet();
        if (type.isInterface()) {
            return dagger.hilt.android.internal.managers.ActivityComponentManager
                    .createInterfaceProxy(type);
        }
        return null;
    }

    private Object createLocalCacheManagerDataSourceProxy(final Class<?> type, ClassLoader appCl) {
        final ClassLoader cl = appCl != null ? appCl : type.getClassLoader();
        if (cl == null) {
            return null;
        }
        try {
            return java.lang.reflect.Proxy.newProxyInstance(
                    cl,
                    new Class<?>[]{type},
                    new java.lang.reflect.InvocationHandler() {
                        private final java.util.Map<String, Object> cache = new java.util.HashMap<>();

                        @Override
                        public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args)
                                throws Throwable {
                            String name = method.getName();
                            if ("toString".equals(name)) return "LocalCacheManagerDataSourceProxy";
                            if ("hashCode".equals(name)) return System.identityHashCode(proxy);
                            if ("equals".equals(name)) return proxy == args[0];

                            String key = (args != null && args.length > 0 && args[0] instanceof String)
                                    ? (String) args[0] : null;
                            if (key != null) {
                                if ("remove".equals(name) || "b".equals(name) || "d".equals(name)
                                        || "q".equals(name)) {
                                    cache.remove(key);
                                    return null;
                                }
                                if ("putString".equals(name) || "putBoolean".equals(name)
                                        || "putBooleanWithExpiry".equals(name) || "putInt".equals(name)
                                        || "putLong".equals(name) || "f".equals(name)
                                        || "c".equals(name) || "g".equals(name) || "i".equals(name)
                                        || "m".equals(name) || "p".equals(name)) {
                                    if (args.length > 1) {
                                        cache.put(key, args[1]);
                                    }
                                    return null;
                                }
                                if ("s".equals(name) || "o".equals(name)) {
                                    Object cached = cache.get(key);
                                    if (args.length > 1 && args[1] instanceof Class<?>) {
                                        Class<?> requested = (Class<?>) args[1];
                                        if (cached != null && requested.isInstance(cached)) {
                                            return cached;
                                        }
                                        // The app immediately check-casts this return value.
                                        return null;
                                    }
                                    return cached;
                                }
                                if ("e".equals(name)) {
                                    return cache.get(key);
                                }
                                if ("getString".equals(name)) {
                                    Object cached = cache.get(key);
                                    return cached instanceof String ? cached
                                            : (args.length > 1 ? args[1] : "");
                                }
                                if ("getBoolean".equals(name) || "getBooleanWithExpiry".equals(name)) {
                                    Object cached = cache.get(key);
                                    return cached instanceof Boolean ? cached
                                            : (args.length > 1 ? args[1] : false);
                                }
                                if ("getInt".equals(name)) {
                                    Object cached = cache.get(key);
                                    return cached instanceof Integer ? cached
                                            : (args.length > 1 ? args[1] : 0);
                                }
                                if ("getLong".equals(name)) {
                                    Object cached = cache.get(key);
                                    return cached instanceof Long ? cached
                                            : (args.length > 1 ? args[1] : 0L);
                                }
                            }

                            if ("a".equals(name) || "k".equals(name) || "l".equals(name)) {
                                return new java.util.HashMap();
                            }
                            if ("j".equals(name)) {
                                return new java.util.ArrayList();
                            }
                            if ("n".equals(name) || "r".equals(name)) {
                                return "";
                            }
                            if (method.getReturnType() == Object.class) {
                                return null;
                            }
                            return createStubValue(method.getReturnType(), cl);
                        }
                    });
        } catch (Throwable t) {
            Log.d(TAG, "  LocalCacheManagerDataSource proxy failed: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
            return null;
        }
    }

    private Object instantiateBestEffort(Class<?> type, ClassLoader appCl, int depth) {
        if (type == null || depth < 0) {
            return null;
        }
        if (type.isInterface() || java.lang.reflect.Modifier.isAbstract(type.getModifiers())) {
            return null;
        }
        try {
            java.lang.reflect.Constructor<?> ctor = type.getDeclaredConstructor();
            ctor.setAccessible(true);
            return ctor.newInstance();
        } catch (Throwable ignored) {
        }

        android.app.Application app = mServer.getApplication();
        for (java.lang.reflect.Constructor<?> ctor : type.getDeclaredConstructors()) {
            try {
                Class<?>[] params = ctor.getParameterTypes();
                Object[] args = new Object[params.length];
                boolean ok = true;
                for (int i = 0; i < params.length; i++) {
                    Object arg = createCtorArg(params[i], appCl, app, depth - 1);
                    if (arg == UNRESOLVED_CTOR_ARG) {
                        ok = false;
                        break;
                    }
                    args[i] = arg;
                }
                if (!ok) {
                    continue;
                }
                ctor.setAccessible(true);
                return ctor.newInstance(args);
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    private static final Object UNRESOLVED_CTOR_ARG = new Object();

    private Object createCtorArg(Class<?> type, ClassLoader appCl, android.app.Application app, int depth) {
        if (type == null) {
            return UNRESOLVED_CTOR_ARG;
        }
        if (type == boolean.class) return false;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0f;
        if (type == double.class) return 0.0;
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == char.class) return '\0';
        if (type == String.class) return "";
        if (app != null && type.isInstance(app)) return app;
        if (app != null && android.content.Context.class.isAssignableFrom(type)) return app;
        if (java.util.List.class.isAssignableFrom(type)) return new java.util.ArrayList();
        if (java.util.Map.class.isAssignableFrom(type)) return new java.util.HashMap();
        if (java.util.Set.class.isAssignableFrom(type)) return new java.util.HashSet();

        Object stub = createStubValue(type, appCl);
        if (stub != null) {
            return stub;
        }
        if (depth < 0 || type.isInterface()
                || java.lang.reflect.Modifier.isAbstract(type.getModifiers())) {
            return UNRESOLVED_CTOR_ARG;
        }
        Object nested = instantiateBestEffort(type, appCl, depth);
        return nested != null ? nested : UNRESOLVED_CTOR_ARG;
    }

    private void repairAppObjectGraph(Object root, ClassLoader appCl, int depth) {
        java.util.IdentityHashMap<Object, Boolean> seen = new java.util.IdentityHashMap<>();
        repairAppObjectGraph(root, appCl, depth, seen);
    }

    private void repairAppObjectGraph(Object root, ClassLoader appCl, int depth,
                                      java.util.IdentityHashMap<Object, Boolean> seen) {
        if (root == null || depth < 0 || seen.containsKey(root)) {
            return;
        }
        Class<?> cls = root.getClass();
        if (shouldSkipGraphClass(cls)) {
            return;
        }
        seen.put(root, Boolean.TRUE);

        if (root instanceof android.app.Activity) {
            dagger.hilt.android.internal.managers.ActivityComponentManager
                    .fillNullInterfaceFields(root);
        }

        Class<?> cur = cls;
        while (cur != null && cur != Object.class) {
            for (java.lang.reflect.Field f : cur.getDeclaredFields()) {
                if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
                Class<?> fieldType = f.getType();
                if (fieldType.isPrimitive() || shouldSkipGraphClass(fieldType)) continue;
                try {
                    f.setAccessible(true);
                    Object value = f.get(root);
                    if (value == null) {
                        Object stub = createStubValue(fieldType, appCl);
                        if (stub != null) {
                            f.set(root, stub);
                            value = stub;
                            if (fieldType.getName().contains("HomeDashboard")
                                    || f.getName().toLowerCase().contains("home")) {
                                Log.d(TAG, "  Graph repair: "
                                        + root.getClass().getSimpleName() + "." + f.getName()
                                        + " = " + value.getClass().getSimpleName());
                            }
                        }
                    }
                    if (value != null && !shouldSkipGraphClass(value.getClass())) {
                        repairAppObjectGraph(value, appCl, depth - 1, seen);
                    }
                } catch (Throwable ignored) {
                }
            }
            cur = cur.getSuperclass();
        }
    }

    private boolean shouldSkipGraphClass(Class<?> cls) {
        if (cls == null || cls.isPrimitive() || cls.isArray() || cls.isEnum()) {
            return true;
        }
        if (java.lang.reflect.Proxy.isProxyClass(cls)) {
            return true;
        }
        String name = cls.getName();
        if (name.startsWith("java.")
                || name.startsWith("javax.")
                || name.startsWith("kotlin.")
                || name.startsWith("android.")
                || name.startsWith("androidx.")) {
            return true;
        }
        return java.util.Collection.class.isAssignableFrom(cls)
                || java.util.Map.class.isAssignableFrom(cls)
                || ClassLoader.class.isAssignableFrom(cls)
                || Thread.class.isAssignableFrom(cls);
    }

    private void repairLikelyDestroyGraph(Object root, ClassLoader appCl, int depth) {
        java.util.IdentityHashMap<Object, Boolean> seen = new java.util.IdentityHashMap<>();
        repairLikelyDestroyGraph(root, appCl, depth, seen);
    }

    private void repairLikelyDestroyGraph(Object root, ClassLoader appCl, int depth,
                                          java.util.IdentityHashMap<Object, Boolean> seen) {
        if (root == null || depth < 0 || seen.containsKey(root)) {
            return;
        }
        Class<?> cls = root.getClass();
        String clsName = cls.getName();
        if (!isLikelyDestroyRepairClass(clsName)) {
            return;
        }
        seen.put(root, Boolean.TRUE);

        if (root instanceof android.app.Activity) {
            dagger.hilt.android.internal.managers.ActivityComponentManager
                    .fillNullInterfaceFields(root);
        }

        Class<?> cur = cls;
        while (cur != null && cur != Object.class) {
            for (java.lang.reflect.Field f : cur.getDeclaredFields()) {
                if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
                Class<?> fieldType = f.getType();
                String fieldName = f.getName();
                String typeName = fieldType.getName();
                if (fieldType.isPrimitive() || shouldSkipGraphClass(fieldType)
                        || !isLikelyDestroyRepairField(fieldName, typeName)) {
                    continue;
                }
                try {
                    f.setAccessible(true);
                    Object value = f.get(root);
                    if (value == null) {
                        Object stub = createStubValue(fieldType, appCl);
                        if (stub != null) {
                            f.set(root, stub);
                            value = stub;
                            Log.d(TAG, "  Destroy repair: "
                                    + root.getClass().getSimpleName() + "." + fieldName
                                    + " = " + value.getClass().getSimpleName());
                        }
                    }
                    if (value != null) {
                        repairLikelyDestroyGraph(value, appCl, depth - 1, seen);
                    }
                } catch (Throwable ignored) {
                }
            }
            cur = cur.getSuperclass();
        }
    }

    private boolean isLikelyDestroyRepairClass(String className) {
        if (className == null || !className.startsWith("com.mcdonalds.")) {
            return false;
        }
        return containsAnyIgnoreCase(className,
                "Splash", "Promo", "Presenter", "Helper", "Dashboard", "Hero", "BaseActivity");
    }

    private boolean isLikelyDestroyRepairField(String fieldName, String typeName) {
        String joined = (fieldName == null ? "" : fieldName) + " " + (typeName == null ? "" : typeName);
        return containsAnyIgnoreCase(joined,
                "present", "helper", "dashboard", "hero", "promo", "module", "interactor");
    }

    private boolean containsAnyIgnoreCase(String text, String... needles) {
        if (text == null) {
            return false;
        }
        String lower = text.toLowerCase(java.util.Locale.ROOT);
        for (String needle : needles) {
            if (lower.contains(needle.toLowerCase(java.util.Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    private Object tryInvokeDataSourceHelper(java.lang.reflect.Method method) {
        try {
            return method.invoke(null);
        } catch (Throwable ignored) {
            return null;
        }
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

    public void startActivityDirect(Activity caller, String packageName, String className,
            int requestCode, Class<?> preloadedClass) {
        startActivityDirect(caller, packageName, className, requestCode, preloadedClass, null);
    }

    public void startActivityDirect(Activity caller, String packageName, String className,
            int requestCode, Class<?> preloadedClass, String stageExtra) {
        boolean traceDirect = shouldTrustDirectLaunch(packageName, className, preloadedClass);
        if (traceDirect) {
            com.westlake.engine.WestlakeLauncher.noteMarker("CV startActivityDirect entry");
        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        if (traceDirect) {
            com.westlake.engine.WestlakeLauncher.noteMarker("CV startActivityDirect after intent");
        }
        if (!traceDirect) {
            try {
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
            } catch (Throwable ignored) {
            }
        }
        if (traceDirect) {
            com.westlake.engine.WestlakeLauncher.noteMarker("CV startActivityDirect after category");
        }
        if (caller == null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (traceDirect) {
            com.westlake.engine.WestlakeLauncher.noteMarker("CV startActivityDirect after flags");
        }
        if (stageExtra != null && stageExtra.length() != 0) {
            try {
                intent.putExtra("stage", stageExtra);
            } catch (Throwable ignored) {
            }
        }
        if (traceDirect) {
            com.westlake.engine.WestlakeLauncher.noteMarker("CV startActivityDirect after stage");
        }
        String resolvedClassName = traceDirect
                ? trustedLaunchClassName(className, preloadedClass)
                : normalizeLaunchClassName(packageName, className, preloadedClass);
        String resolvedPackageName = traceDirect
                ? trustedLaunchPackageName(packageName, resolvedClassName)
                : normalizeLaunchPackageName(packageName, resolvedClassName);
        if (traceDirect) {
            com.westlake.engine.WestlakeLauncher.noteMarker("CV startActivityDirect after resolve");
        }
        if (!traceDirect && resolvedPackageName != null && !resolvedPackageName.isEmpty()) {
            try {
                intent.setPackage(resolvedPackageName);
            } catch (Throwable ignored) {
            }
        }
        if (traceDirect) {
            com.westlake.engine.WestlakeLauncher.noteMarker("CV startActivityDirect after setPackage");
        }
        ComponentName directComponent = null;
        if (traceDirect) {
            directComponent = buildDirectComponent(resolvedPackageName, resolvedClassName);
            com.westlake.engine.WestlakeLauncher.noteMarker("CV startActivityDirect after buildDirectComponent");
            if (directComponent != null) {
                try {
                    intent.setComponent(directComponent);
                } catch (Throwable ignored) {
                }
            }
        }
        if (traceDirect) {
            com.westlake.engine.WestlakeLauncher.noteMarker("CV startActivityDirect before startResolved");
        }
        startResolvedActivity(caller, intent, requestCode, preloadedClass,
                resolvedPackageName, resolvedClassName, directComponent);
    }

    private static String stabilizeLaunchString(String value) {
        return value;
    }

    private static String findRegisteredClassName(Map<String, Class<?>> registeredClasses,
            Class<?> cls) {
        if (cls == null) {
            return null;
        }
        try {
            for (Map.Entry<String, Class<?>> entry : registeredClasses.entrySet()) {
                if (entry.getValue() == cls) {
                    return stabilizeLaunchString(entry.getKey());
                }
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private static boolean shouldTrustDirectLaunch(String packageName, String className,
            Class<?> preloadedClass) {
        if (CUTOFF_CANARY_ACTIVITY.equals(className)) {
            return true;
        }
        if (CUTOFF_CANARY_L3_ACTIVITY.equals(className)) {
            return true;
        }
        if (CUTOFF_CANARY_L4_ACTIVITY.equals(className)) {
            return true;
        }
        if (CUTOFF_CANARY_PACKAGE.equals(packageName)) {
            return true;
        }
        if (preloadedClass != null) {
            try {
                if (CUTOFF_CANARY_ACTIVITY.equals(preloadedClass.getName())) {
                    return true;
                }
            } catch (Throwable ignored) {
            }
        }
        try {
            if (!com.westlake.engine.WestlakeLauncher.isControlAndroidBackend()) {
                return false;
            }
        } catch (Throwable ignored) {
            return false;
        }
        return false;
    }

    private static String trustedLaunchClassName(String className, Class<?> preloadedClass) {
        if (CUTOFF_CANARY_L3_ACTIVITY.equals(className)) {
            return CUTOFF_CANARY_L3_ACTIVITY;
        }
        if (CUTOFF_CANARY_L4_ACTIVITY.equals(className)) {
            return CUTOFF_CANARY_L4_ACTIVITY;
        }
        return CUTOFF_CANARY_ACTIVITY;
    }

    private static String trustedLaunchPackageName(String packageName, String className) {
        return CUTOFF_CANARY_PACKAGE;
    }

    private static String normalizeLaunchClassName(String packageName, String className,
            Class<?> preloadedClass) {
        String resolvedPackage = stabilizeLaunchString(packageName);
        String resolvedClass = stabilizeLaunchString(className);
        if ((resolvedClass == null || resolvedClass.isEmpty()) && preloadedClass != null) {
            try {
                resolvedClass = stabilizeLaunchString(preloadedClass.getName());
            } catch (Throwable ignored) {
            }
        }
        if (resolvedClass != null && !resolvedClass.isEmpty() && resolvedClass.startsWith(".")
                && resolvedPackage != null && !resolvedPackage.isEmpty()) {
            resolvedClass = resolvedPackage + resolvedClass;
        }
        return stabilizeLaunchString(resolvedClass);
    }

    private String normalizeLaunchPackageName(String packageName, String className) {
        String resolvedPackage = stabilizeLaunchString(packageName);
        String resolvedClass = stabilizeLaunchString(className);
        if ((resolvedPackage == null || resolvedPackage.isEmpty())
                && resolvedClass != null && !resolvedClass.isEmpty()) {
            int dot = resolvedClass.lastIndexOf('.');
            if (dot > 0) {
                resolvedPackage = stabilizeLaunchString(resolvedClass.substring(0, dot));
            }
        }
        if ((resolvedPackage == null || resolvedPackage.isEmpty()) && mServer.getApkInfo() != null) {
            resolvedPackage = stabilizeLaunchString(mServer.getApkInfo().packageName);
        }
        if (resolvedPackage == null || resolvedPackage.isEmpty()) {
            resolvedPackage = stabilizeLaunchString(mServer.getPackageName());
        }
        if ((resolvedPackage == null || resolvedPackage.isEmpty())
                && mServer.getApplication() != null) {
            try {
                resolvedPackage = stabilizeLaunchString(mServer.getApplication().getPackageName());
            } catch (Throwable ignored) {
            }
        }
        if (resolvedPackage == null || resolvedPackage.isEmpty()) {
            resolvedPackage = stabilizeLaunchString(System.getProperty("westlake.apk.package"));
        }
        if (resolvedPackage == null || resolvedPackage.isEmpty()) {
            resolvedPackage = "app";
        }
        return stabilizeLaunchString(resolvedPackage);
    }

    private static ComponentName buildDirectComponent(String packageName, String className) {
        if (packageName == null || packageName.isEmpty()
                || className == null || className.isEmpty()) {
            return null;
        }
        try {
            return new ComponentName(packageName, className);
        } catch (Throwable t) {
            Log.w(TAG, "startActivity: trusted component build failed for " + className
                    + " (" + t.getClass().getSimpleName() + ")");
            return null;
        }
    }

    private ComponentName buildLaunchComponent(String packageName, String className) {
        String resolvedPackage = normalizeLaunchPackageName(packageName, className);
        String resolvedClass = normalizeLaunchClassName(resolvedPackage, className, null);
        if (resolvedPackage == null || resolvedPackage.isEmpty()
                || resolvedClass == null || resolvedClass.isEmpty()) {
            return null;
        }
        try {
            return new ComponentName(resolvedPackage, resolvedClass);
        } catch (Throwable t) {
            Log.w(TAG, "startActivity: component build failed for " + resolvedClass
                    + " (" + t.getClass().getSimpleName() + ")");
            return null;
        }
    }

    private void startResolvedActivity(Activity caller, Intent intent, int requestCode,
            Class<?> preloadedClass, String packageName, String className,
            ComponentName existingComponent) {
        if (intent == null) {
            intent = new Intent(Intent.ACTION_MAIN);
        }
        boolean trustedDirectLaunch =
                shouldTrustDirectLaunch(packageName, className, preloadedClass);
        String resolvedClassName = trustedDirectLaunch
                ? trustedLaunchClassName(className, preloadedClass)
                : normalizeLaunchClassName(packageName, className, preloadedClass);
        String resolvedPackageName = trustedDirectLaunch
                ? trustedLaunchPackageName(packageName, resolvedClassName)
                : normalizeLaunchPackageName(packageName, resolvedClassName);
        if (resolvedClassName == null || resolvedClassName.isEmpty()) {
            Log.w(TAG, "startActivity: unresolved class name");
            return;
        }
        if ((intent.getPackage() == null || intent.getPackage().isEmpty())
                && resolvedPackageName != null && !resolvedPackageName.isEmpty()) {
            try {
                intent.setPackage(resolvedPackageName);
            } catch (Throwable ignored) {
            }
        }
        if (!trustedDirectLaunch) {
            Log.d(TAG, "startActivity: " + resolvedClassName
                    + " action=" + intent.getAction()
                    + " pkg=" + intent.getPackage());
        }
        ComponentName component = existingComponent;
        if (component == null) {
            component = trustedDirectLaunch
                    ? buildDirectComponent(resolvedPackageName, resolvedClassName)
                    : buildLaunchComponent(resolvedPackageName, resolvedClassName);
        } else if (!trustedDirectLaunch
                && (!resolvedClassName.equals(stabilizeLaunchString(component.getClassName()))
                || !resolvedPackageName.equals(stabilizeLaunchString(component.getPackageName())))) {
            component = buildLaunchComponent(resolvedPackageName, resolvedClassName);
        }
        if (component == null) {
            if (!trustedDirectLaunch) {
                Log.w(TAG, "startActivity: unresolved component for " + resolvedClassName);
            }
            return;
        }
        final boolean traceCanary = isCutoffCanaryComponent(component);
        if (traceCanary) {
            com.westlake.engine.WestlakeLauncher.noteMarker("CV startResolved component ready");
        }

        // Instantiate the Activity class
        Activity activity;
        Class<?> cls = preloadedClass;
        try {
            if (traceCanary) {
                com.westlake.engine.WestlakeLauncher.noteMarker("CV startResolved before instantiate");
            }
            if (traceCanary && cls != null) {
                com.westlake.engine.WestlakeLauncher.noteMarker("CV startResolved preloaded class");
            }
            if (cls == null) {
                cls = mRegisteredClasses.get(resolvedClassName);
                if (traceCanary && cls != null) {
                    com.westlake.engine.WestlakeLauncher.noteMarker("CV startResolved registered class");
                }
            }
            if (cls == null) {
                // Use context classloader (set by ART to app's PathClassLoader)
                // NOT boot classloader (MiniActivityManager is on boot classpath)
                ClassLoader cl = resolveAppClassLoader();
                if (traceCanary) {
                    com.westlake.engine.WestlakeLauncher.noteMarker("CV startResolved resolved loader");
                }
                if (isBootClassLoader(cl)) {
                    cls = com.westlake.engine.WestlakeLauncher
                            .resolveAppClassChildFirstOrNull(resolvedClassName);
                    if (cls != null) {
                        cl = cls.getClassLoader();
                        if (traceCanary) {
                            com.westlake.engine.WestlakeLauncher.noteMarker(
                                    "CV startResolved native class");
                        }
                    }
                }
                if (isBootClassLoader(cl)) {
                    Log.e(TAG, "No app class loader for " + resolvedClassName);
                    return;
                }
                if (cls == null) {
                    try {
                        cls = Class.forName(resolvedClassName, false, cl);
                    } catch (ClassNotFoundException e) {
                        cls = cl.loadClass(resolvedClassName);
                    }
                }
            }
            ClassLoader cl = cls != null ? cls.getClassLoader() : null;
            if (traceCanary) {
                com.westlake.engine.WestlakeLauncher.noteMarker("CV startResolved class loader");
            }
            if (isBootClassLoader(cl)) {
                cl = Thread.currentThread().getContextClassLoader();
                if (traceCanary) {
                    com.westlake.engine.WestlakeLauncher.noteMarker(
                            "CV startResolved thread loader");
                }
            }
            if (isBootClassLoader(cl)) {
                cl = resolveAppClassLoader();
                if (traceCanary) {
                    com.westlake.engine.WestlakeLauncher.noteMarker(
                            "CV startResolved fallback loader");
                }
            }
            if (!isBootClassLoader(cl)) {
                if (traceCanary) {
                    com.westlake.engine.WestlakeLauncher.noteMarker(
                            "CV startResolved loader preserved");
                } else {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
            if (traceCanary) {
                com.westlake.engine.WestlakeLauncher.noteMarker(
                        "CV startResolved before instantiate call");
            }
            activity = instantiateActivity(cls, resolvedClassName, intent);
            if (traceCanary) {
                com.westlake.engine.WestlakeLauncher.noteMarker("CV startResolved after instantiate");
            }
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Activity class not found: " + resolvedClassName);
            return;
        } catch (Throwable e) {
            if (cls == null) {
                Log.e(TAG, "Failed to resolve " + resolvedClassName + " (" + e.getClass().getName() + ")");
                return;
            }
            Log.e(TAG, "Failed to instantiate " + resolvedClassName + " (" + e.getClass().getName() + ")");
            return;
        }

        // Seed framework state before attach/lifecycle code queries the Activity.
        ShimCompat.setActivityField(activity, "mIntent", intent);
        ShimCompat.setActivityField(activity, "mComponent", component);
        ShimCompat.setActivityField(activity, "mApplication", mServer.getApplication());
        ShimCompat.setActivityField(activity, "mFinished", Boolean.FALSE);
        ShimCompat.setActivityField(activity, "mDestroyed", Boolean.FALSE);

        // PF-noice-022 (2026-05-11): generic Hilt-graph injector.
        //
        // Pre-fill null DI fields on the activity BEFORE onCreate runs. Hilt's
        // normal injection path is blocked in standalone Westlake (Application
        // isn't `Hilt_*`, EntryPoints.get(app, ...) throws ISE). The injector
        // below replaces obfuscated kotlin.Lazy fields (interfaces declaring
        // `getValue():Object`) with proxies that return deep-stubbed instances,
        // recursively fills Unsafe-allocated stubs' Context/Resources/Prefs
        // slots from the Application object, and prevents the canonical NPE
        // pattern (Lazy.getValue() → SettingsRepository.context.getString()).
        // The fill is generic and best-effort: any field that can't be filled
        // stays null, and the existing per-onCreate catch handles it.
        if (!isCutoffCanaryComponent(component)) {
            try {
                fillNullFieldsWithProxies(activity);
            } catch (Throwable t) {
                Log.w(TAG, "  Pre-onCreate Hilt graph fill failed: "
                        + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
        }

        if (!isCutoffCanaryComponent(component)
                && shouldRunLegacyMcdBootstrap(component, resolvedPackageName, resolvedClassName)) {
            Object singleton = ensureSingletonComponent();
            seedPreCreateDatasourceState(activity, singleton);

            // Inject DI fields directly from the singleton component
            // The Hilt injection mechanism fails because componentManager() silently catches errors.
            // We bypass Hilt entirely: get the singleton component and call inject directly.
            try {
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
            // Fill null DI fields, but never let Hilt bootstrap abort the launch path.
            try {
                dagger.hilt.android.internal.managers.ActivityComponentManager
                        .fillNullInterfaceFields(activity);
            } catch (Throwable t) {
                Log.w(TAG, "  fillNullInterfaceFields failed: "
                        + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
        }

        // Call Activity.attach() with real framework Context — creates PhoneWindow, wires lifecycle
        try {
            if (traceCanary) {
                com.westlake.engine.WestlakeLauncher.noteMarker("CV startResolved before attach");
            }
            if (traceCanary) {
                com.westlake.engine.WestlakeLauncher.noteMarker("CV startResolved attach skipped");
            } else {
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
                                // Derive from class name: com.mcdonalds.app.SplashActivity -> com.mcdonalds.app
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
                        attachBaseContextFallback(activity);
                        Log.d(TAG, "  attachBaseContext fallback (no attach method found)");
                    }
                }
                if (traceCanary) {
                    com.westlake.engine.WestlakeLauncher.noteMarker("CV startResolved after attach");
                }
            }
        } catch (Throwable t) {
            Log.w(TAG, "  Activity.attach failed: " + t.getClass().getSimpleName() + ": " + t.getMessage());
            Throwable root = t;
            while (root.getCause() != null) root = root.getCause();
            if (root != t) Log.w(TAG, "  ROOT: " + root);
            // Fallback: just attachBaseContext + create PhoneWindow manually
            try {
                attachBaseContextFallback(activity);
                Log.d(TAG, "  Fallback: Context + PhoneWindow attached");
            } catch (Throwable t2) {
                Log.w(TAG, "  Fallback attach also failed: " + t2.getMessage());
            }
            if (traceCanary) {
                com.westlake.engine.WestlakeLauncher.noteMarker("CV startResolved after attach fallback");
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
        if (traceCanary) {
            com.westlake.engine.WestlakeLauncher.noteMarker("CV startResolved record created");
        }
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
            if (traceCanary) {
                com.westlake.engine.WestlakeLauncher.noteMarker("CV startResolved before performCreate");
            }
            performCreate(record, null);
            if (traceCanary) {
                com.westlake.engine.WestlakeLauncher.noteMarker("CV startResolved after performCreate");
            }
        } catch (Throwable e) {
            if (traceCanary) {
                com.westlake.engine.WestlakeLauncher.noteMarker("CV startResolved performCreate threw");
            } else {
                Log.e(TAG, "startActivity performCreate threw: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
        if (isFinishedOrDestroyed(record)) {
            return;
        }
        // Check if onCreate set content — if not, skip start/resume (avoids lifecycle observer hangs)
        boolean hasContent = traceCanary;
        if (!traceCanary) {
            try {
                android.view.View decor = record.activity.getWindow() != null ? record.activity.getWindow().getDecorView() : null;
                hasContent = decor instanceof android.view.ViewGroup && ((android.view.ViewGroup) decor).getChildCount() > 0;
            } catch (Throwable e) { /* ignore */ }
        }
        // Always continue lifecycle — NPE in super.onCreate() doesn't mean the activity is dead.
        // The activity might set content in onStart/onResume or via delayed navigation.
        try {
            if (traceCanary) {
                com.westlake.engine.WestlakeLauncher.noteMarker("CV startResolved before performStart");
            }
            performStart(record);
            if (traceCanary) {
                com.westlake.engine.WestlakeLauncher.noteMarker("CV startResolved after performStart");
            } else {
                Log.d(TAG, "  performStart DONE for " + resolvedClassName);
            }
        } catch (Throwable e) {
            if (traceCanary) {
                com.westlake.engine.WestlakeLauncher.noteMarker("CV startResolved performStart threw");
            } else {
                Log.e(TAG, "startActivity performStart threw: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
        try {
            if (traceCanary) {
                com.westlake.engine.WestlakeLauncher.noteMarker("CV startResolved before performResume");
            }
            performResume(record);
            if (traceCanary) {
                com.westlake.engine.WestlakeLauncher.noteMarker("CV startResolved after performResume");
            } else {
                Log.d(TAG, "  performResume DONE for " + resolvedClassName);
            }
        } catch (Throwable e) {
            if (traceCanary) {
                com.westlake.engine.WestlakeLauncher.noteMarker("CV startResolved performResume threw");
            } else {
                Log.e(TAG, "startActivity performResume threw: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
        if (!traceCanary && !hasContent) {
            Log.w(TAG, "Note: onCreate didn't set content view (NPE in super) — splash fallback will be used if needed");
        }
        if (!traceCanary) {
            Log.d(TAG, "startActivity result: resumed="
                    + (mResumed != null && mResumed.activity != null
                    ? mResumed.activity.getClass().getName() : "null")
                    + " stack=" + mStack.size());
        }
    }

    public void startActivity(Activity caller, Intent intent, int requestCode, Class<?> preloadedClass) {
        if (intent == null) {
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
        className = normalizeLaunchClassName(packageName, className, preloadedClass);
        packageName = normalizeLaunchPackageName(packageName, className);
        if ((intent.getPackage() == null || intent.getPackage().isEmpty())
                && packageName != null && !packageName.isEmpty()) {
            try {
                intent.setPackage(packageName);
            } catch (Throwable ignored) {
            }
        }
        startResolvedActivity(caller, intent, requestCode, preloadedClass,
                packageName, className, component);
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

    public void recreateActivity(Activity activity) {
        if (activity == null) {
            Log.w(TAG, "recreateActivity: null activity");
            return;
        }
        ActivityRecord oldRecord = findRecord(activity);
        if (oldRecord == null) {
            Log.w(TAG, "recreateActivity: no record for " + activity.getClass().getName());
            return;
        }
        if (!isCutoffCanaryRecord(oldRecord)) {
            Log.w(TAG, "recreateActivity: generic recreate not wired for "
                    + oldRecord.component.getClassName());
            return;
        }

        int index = mStack.indexOf(oldRecord);
        if (index < 0) {
            Log.w(TAG, "recreateActivity: record not in stack");
            return;
        }

        com.westlake.engine.WestlakeLauncher.noteMarker("CV recreate begin");
        Bundle savedState = new Bundle();
        try {
            com.westlake.engine.WestlakeLauncher.noteMarker("CV recreate before save");
            activity.onSaveInstanceState(savedState);
            com.westlake.engine.WestlakeLauncher.noteMarker("CV recreate after save");
        } catch (Throwable t) {
            com.westlake.engine.WestlakeLauncher.noteMarker("CV recreate save error");
            Log.w(TAG, "recreateActivity save failed: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }

        try {
            if (oldRecord == mResumed) {
                com.westlake.engine.WestlakeLauncher.noteMarker("CV recreate before pause");
                performPause(oldRecord);
                com.westlake.engine.WestlakeLauncher.noteMarker("CV recreate after pause");
            }
            com.westlake.engine.WestlakeLauncher.noteMarker("CV recreate before stop");
            performStop(oldRecord);
            com.westlake.engine.WestlakeLauncher.noteMarker("CV recreate after stop");
            com.westlake.engine.WestlakeLauncher.noteMarker("CV recreate before destroy");
            performDestroy(oldRecord);
            com.westlake.engine.WestlakeLauncher.noteMarker("CV recreate after destroy");
        } catch (Throwable t) {
            com.westlake.engine.WestlakeLauncher.noteMarker("CV recreate teardown error");
            Log.w(TAG, "recreateActivity teardown failed: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }

        Activity newActivity;
        try {
            Class<?> cls = activity.getClass();
            String className = oldRecord.component != null
                    ? oldRecord.component.getClassName()
                    : cls.getName();
            com.westlake.engine.WestlakeLauncher.noteMarker("CV recreate before instantiate");
            newActivity = instantiateActivity(cls, className, oldRecord.intent);
            com.westlake.engine.WestlakeLauncher.noteMarker("CV recreate after instantiate");
        } catch (Throwable t) {
            com.westlake.engine.WestlakeLauncher.noteMarker("CV recreate instantiate error");
            Log.e(TAG, "recreateActivity instantiate failed: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
            return;
        }

        ShimCompat.setActivityField(newActivity, "mIntent", oldRecord.intent);
        ShimCompat.setActivityField(newActivity, "mComponent", oldRecord.component);
        ShimCompat.setActivityField(newActivity, "mApplication", mServer.getApplication());
        ShimCompat.setActivityField(newActivity, "mFinished", Boolean.FALSE);
        ShimCompat.setActivityField(newActivity, "mDestroyed", Boolean.FALSE);

        ActivityRecord newRecord = new ActivityRecord();
        newRecord.activity = newActivity;
        newRecord.intent = oldRecord.intent;
        newRecord.component = oldRecord.component;
        newRecord.caller = oldRecord.caller;
        newRecord.requestCode = oldRecord.requestCode;
        mStack.set(index, newRecord);
        mResumed = newRecord;
        com.westlake.engine.WestlakeLauncher.noteMarker("CV recreate record replaced");

        try {
            com.westlake.engine.WestlakeLauncher.noteMarker("CV recreate before performCreate");
            performCreate(newRecord, savedState);
            com.westlake.engine.WestlakeLauncher.noteMarker("CV recreate after performCreate");
        } catch (Throwable t) {
            com.westlake.engine.WestlakeLauncher.noteMarker("CV recreate performCreate error");
            Log.e(TAG, "recreateActivity performCreate failed: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
        if (isFinishedOrDestroyed(newRecord)) {
            return;
        }
        try {
            com.westlake.engine.WestlakeLauncher.noteMarker("CV recreate before performStart");
            performStart(newRecord);
            com.westlake.engine.WestlakeLauncher.noteMarker("CV recreate after performStart");
        } catch (Throwable t) {
            com.westlake.engine.WestlakeLauncher.noteMarker("CV recreate performStart error");
            Log.e(TAG, "recreateActivity performStart failed: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
        try {
            com.westlake.engine.WestlakeLauncher.noteMarker("CV recreate before performResume");
            performResume(newRecord);
            com.westlake.engine.WestlakeLauncher.noteMarker("CV recreate after performResume");
        } catch (Throwable t) {
            com.westlake.engine.WestlakeLauncher.noteMarker("CV recreate performResume error");
            Log.e(TAG, "recreateActivity performResume failed: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
        com.westlake.engine.WestlakeLauncher.noteMarker("CV recreate end");
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
        if (isCutoffCanaryRecord(r) || isControlledWestlakeRecord(r)) {
            try {
                com.westlake.engine.WestlakeLauncher.noteMarker("CV performCreate direct before onCreate");
                r.activity.onCreate(savedInstanceState);
                com.westlake.engine.WestlakeLauncher.noteMarker("CV performCreate direct after onCreate");
            } catch (Throwable e) {
                com.westlake.engine.WestlakeLauncher.noteMarker("CV performCreate direct error");
            }
            if (isFinishedOrDestroyed(r)) {
                return;
            }
            return;
        }
        Log.d(TAG, "  performCreate: " + r.component.getClassName());
        // Dispatch lifecycle: restore saved state + ON_CREATE
        dispatchLifecycleEvent(r.activity, "performRestore", savedInstanceState);
        boolean createNPE = false;
        final ClassLoader appCl = resolveAppClassLoader();
        WestlakeActivityThread.seedCoroutineMainDispatcher(appCl);
        logMainDispatcherProbe("performCreate app", appCl);

        // Run onCreate in thread with timeout (complex apps can hang in DI init)
        final Activity actRef = r.activity;
        final Bundle ssRef = savedInstanceState;
        final boolean[] done = { false };
        final Exception[] error = { null };
        Thread ocThread = new Thread(new Runnable() {
            public void run() {
                Thread.currentThread().setContextClassLoader(appCl);
                logMainDispatcherProbe("performCreate thread", Thread.currentThread().getContextClassLoader());
                try {
                    actRef.onCreate(ssRef);
                    done[0] = true;
                } catch (Throwable e) {
                    error[0] = (e instanceof Exception) ? (Exception) e : new RuntimeException(e);
                    done[0] = true;
                }
            }
        }, "ActivityOnCreate");
        ocThread.setDaemon(true);
        ocThread.start();
        String componentName = r.component != null ? r.component.getClassName() : null;
        // Generic timeout: 15s is appropriate for any non-trivial activity
        // whose onCreate does view inflation + DI bootstrap (Hilt, Dagger,
        // etc.). Per CLAUDE.md, no per-package branches.
        long createTimeoutMs = 15000L;
        try { ocThread.join(createTimeoutMs); } catch (InterruptedException ie) {}
        // CRITICAL: Stop the thread if still running to prevent GC deadlock.
        // SplashActivity.onCreate may catch UUID errors and continue with DI code
        // that never reaches a safepoint, causing nonconcurrent GC to freeze all threads.
        boolean timeoutContentInstalled = ocThread.isAlive()
                && hasInstalledWindowContent(r.activity);
        if (ocThread.isAlive()) {
            Log.w(TAG, "performCreate thread still alive after " + createTimeoutMs
                    + "ms for " + r.component.getClassName());
            StackTraceElement[] stack = ocThread.getStackTrace();
            if (stack != null) {
                for (int i = 0; i < stack.length && i < 16; i++) {
                    Log.w(TAG, "  onCreate stack[" + i + "] " + stack[i]);
                }
            }
            if (timeoutContentInstalled) {
                Log.w(TAG, "performCreate leaving onCreate thread alive (content already installed) for "
                        + r.component.getClassName());
            } else {
                try { ocThread.stop(); } catch (Throwable t) { /* ThreadDeath expected */ }
            }
        }

        if (!done[0]) {
            Log.w(TAG, "performCreate TIMEOUT (" + createTimeoutMs + "ms) for "
                    + r.component.getClassName() + " — proceeding");
        } else if (error[0] instanceof NullPointerException) {
            Log.w(TAG, "performCreate NPE (non-fatal): " + error[0].getMessage());
            createNPE = true;
        } else if (error[0] != null) {
            Throwable root = error[0];
            while (root.getCause() != null) root = root.getCause();
            Log.e(TAG, "performCreate error: " + error[0].getClass().getSimpleName() + ": " + error[0].getMessage());
            Log.e(TAG, "performCreate ROOT: " + root.getClass().getName() + ": " + root.getMessage());
            // PF-noice (2026-05-04): printStackTrace(System.err) internally
            // exercises Charset/CoderResult; when those statics are null
            // (boot-class ASE cascade), this NPEs and unwinds out of
            // performCreate before the recovery branch below can fire.
            try {
                root.printStackTrace(System.err);
            } catch (Throwable stackEx) {
                Log.w(TAG, "performCreate ROOT printStackTrace failed: " + stackEx.getClass().getSimpleName());
            }
        }

        // If onCreate NPE'd, try to manually set content view with the splash layout
        boolean timeoutWithContent = !createNPE && !done[0]
                && (timeoutContentInstalled || hasInstalledWindowContent(r.activity));
        if (timeoutWithContent) {
            Log.d(TAG, "  performCreate timeout: content already installed for "
                    + r.component.getClassName() + "; skipping fallback setContentView");
        } else if (createNPE || !done[0] || error[0] != null) {
            // PF-noice (2026-05-04): extended from `createNPE || !done[0]` so any
            // exception during onCreate (ASE from boot-class clinit cascade,
            // ISE from Hilt DI failure, etc.) triggers the recovery path that
            // was previously gated only on NPE.
            // PF-noice-013 (2026-05-05): if the activity ALREADY has installed
            // content (because the original onCreate threw AFTER setContentView
            // completed — common with view-binding's bind() that throws on
            // missing-required-view), DON'T overwrite it. The partially-inflated
            // tree is more useful than our fallback. We still run
            // fillNullFieldsWithProxies for downstream survivability.
            if (hasInstalledWindowContent(r.activity)) {
                Log.d(TAG, "  tryRecoverContent: SKIPPING setContentView for " + r.component.getClassName()
                        + " — decor already has content (preserving noice's partial inflate)");
                fillNullFieldsWithProxies(r.activity);
                tryRecoverFragments(r.activity);
                if (isFinishedOrDestroyed(r)) {
                    return;
                }
                // Fall through to performStart/Resume below
            } else {
            Log.d(TAG, "  tryRecoverContent: attempting manual setContentView for " + r.component.getClassName()
                    + " (reason=" + (createNPE ? "NPE" : (!done[0] ? "timeout" : error[0].getClass().getSimpleName())) + ")");
            // PF-noice-022 (2026-05-11): the pre-onCreate fillNullFieldsWithProxies
            // has already filled all addressable null fields. A second call here
            // has caused SIGBUS in past sessions on the standalone guest (likely
            // from re-creating Proxy classes for the same interfaces). Skip it.
            // PF-noice-022 (2026-05-11): iterative NPE-driven Hilt graph repair.
            //
            // After the first NPE, the message tells us EXACTLY which field of
            // which class was dereferenced to null (e.g. `noice.repository.p.a`).
            // We can use that to build a deep-stubbed instance of `noice.repository.p`
            // and route the broken Lazy to return it. Each iteration peels off one
            // more layer of the Hilt graph; bounded retries prevent infinite loops.
            int retryCount = 0;
            final int maxRetries = 6;
            String lastError = null;
            while (retryCount < maxRetries) {
                try {
                    if (retryCount == 0) {
                        Log.d(TAG, "  Retrying onCreate with stub DI fields...");
                    } else {
                        Log.d(TAG, "  Retrying onCreate (attempt #" + (retryCount + 1) + ")");
                    }
                    r.activity.onCreate(null);
                    Log.d(TAG, "  Retry onCreate SUCCESS");
                    break;
                } catch (Throwable retryEx) {
                    String msg = retryEx.getMessage();
                    Log.d(TAG, "  Retry onCreate failed: "
                            + retryEx.getClass().getSimpleName() + ": " + msg);
                    if (!(retryEx instanceof NullPointerException) || msg == null
                            || msg.equals(lastError)) {
                        break;
                    }
                    lastError = msg;
                    // Try to extract "ClassName.fieldName" from the NPE message
                    // and stub the missing object via a Lazy override.
                    if (!repairNpeViaLazyStub(r.activity, msg)) {
                        Log.d(TAG, "  NPE repair: no actionable info in message; giving up retries");
                        break;
                    }
                    retryCount++;
                }
            }
            if (isFinishedOrDestroyed(r)) {
                return;
            }
            try {
                // PF-arch-025: route layout inflation through WestlakeView
                // instead of Activity.setContentView. setContentView goes
                // through Window.getDecorView() which is abstract — no
                // concrete PhoneWindow without real Context. We inflate
                // ourselves and store the resulting View in WestlakeView's
                // map; the render loop picks it up from there.
                android.content.res.Resources res = r.activity.getResources();
                int layoutId = 0;
                if (res != null) {
                    String pkg = r.component.getPackageName();
                    final String[] layoutCandidates = {
                            "activity_splash_screen",
                            "main_activity",
                            "activity_main",
                            "activity_home",
                            "main",
                    };
                    for (String name : layoutCandidates) {
                        int id = res.getIdentifier(name, "layout", pkg);
                        if (id != 0) {
                            layoutId = id;
                            Log.d(TAG, "  tryRecoverContent: matched layout '" + name + "' -> 0x" + Integer.toHexString(id));
                            break;
                        }
                    }
                }
                if (layoutId == 0) layoutId = 0x7f0e0530; // McDonald's splash layout fallback
                /* PF-arch-026: context-free Westlake inflater path. Resolve
                 * layout-name → file-path via ResourceTable (aapt2 collapsed
                 * resource names mean files are like res/0H.xml on disk),
                 * read binary AXML, build WestlakeNode tree, store for render
                 * loop. Also stash the ResourceTable for renderer resolution. */
                String resDir = com.westlake.engine.WestlakeLauncher.getResDirForRender();
                String pkg = r.component != null ? r.component.getPackageName() : null;
                android.content.res.ResourceTable arsc = null;
                if (mServer != null) {
                    android.app.ApkInfo apkInfo = mServer.getApkInfo();
                    if (apkInfo != null && apkInfo.resourceTable instanceof android.content.res.ResourceTable) {
                        arsc = (android.content.res.ResourceTable) apkInfo.resourceTable;
                    }
                }
                if (arsc != null) {
                    com.westlake.engine.WestlakeView.setArsc(r.activity, arsc);
                    /* PF-arch-032: load the activity's actual theme from the
                     * manifest XML. The manifest's <activity android:theme>
                     * (or <application android:theme> as fallback) gives us
                     * the exact theme resource ID to load. Falls back to
                     * common theme names if the manifest can't be read. */
                    int themeResId = 0;
                    String themeSrc = "unknown";
                    String resDirForTheme = com.westlake.engine.WestlakeLauncher.getResDirForRender();
                    if (resDirForTheme != null) {
                        java.io.File manifestFile = new java.io.File(
                                resDirForTheme + "/AndroidManifest.xml");
                        if (manifestFile.isFile()) {
                            byte[] manifestBytes = readFileBytes(manifestFile);
                            if (manifestBytes != null) {
                                com.westlake.engine.WestlakeNode m =
                                        com.westlake.engine.WestlakeInflater.inflate(manifestBytes);
                                if (m != null) {
                                    int activityTheme = findActivityTheme(m, r.component.getClassName());
                                    if (activityTheme != 0) {
                                        themeResId = activityTheme;
                                        themeSrc = "manifest:activity";
                                    } else {
                                        int appTheme = findApplicationTheme(m);
                                        if (appTheme != 0) {
                                            themeResId = appTheme;
                                            themeSrc = "manifest:application";
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (themeResId == 0) {
                        final String[] themeNames = {
                                "Theme.Noice", "Theme.App", "Theme.AppCompat",
                                "Theme.Material3.DayNight", "AppTheme"
                        };
                        for (String tname : themeNames) {
                            int tid = arsc.getIdentifier(tname);
                            if (tid != 0) {
                                themeResId = tid;
                                themeSrc = "fallback:" + tname;
                                break;
                            }
                        }
                    }
                    if (themeResId != 0) {
                        com.westlake.engine.WestlakeTheme theme =
                                com.westlake.engine.WestlakeTheme.load(themeResId, resDirForTheme, arsc);
                        Log.d(TAG, "  theme via " + themeSrc + " (id=0x"
                                + Integer.toHexString(themeResId) + ") loaded="
                                + theme.isLoaded() + " attrs=" + theme.size());
                        if (theme.isLoaded()) {
                            com.westlake.engine.WestlakeView.setTheme(r.activity, theme);
                        }
                    }
                }
                final String[] layoutCandidatesArsc = {
                        "activity_splash_screen", "main_activity", "activity_main",
                        "activity_home", "splash_screen", "main"
                };
                com.westlake.engine.WestlakeNode wlNode = null;
                String layoutFileUsed = null;
                if (resDir != null && arsc != null) {
                    for (String name : layoutCandidatesArsc) {
                        int id = arsc.getIdentifier(name);
                        if (id == 0) continue;
                        String path = arsc.getEntryFilePath(id);
                        if (path == null || path.isEmpty()) continue;
                        java.io.File f = new java.io.File(resDir + "/" + path);
                        if (!f.isFile()) continue;
                        byte[] axml = readFileBytes(f);
                        if (axml == null || axml.length == 0) continue;
                        wlNode = com.westlake.engine.WestlakeInflater.inflate(axml);
                        if (wlNode != null) {
                            layoutFileUsed = name + " -> " + path + " (" + axml.length + " B)";
                            break;
                        }
                    }
                }
                if (wlNode != null) {
                    com.westlake.engine.WestlakeView.setNode(r.activity, wlNode);
                    Log.d(TAG, "  tryRecoverContent: WestlakeInflater parsed " + layoutFileUsed
                            + " — root=" + wlNode.tag + ", " + wlNode.children.size() + " children");
                }
                /* Also try the standard inflater for compatibility (canary apps
                 * that DO have working Resources). */
                android.view.View inflated = null;
                try {
                    android.view.LayoutInflater inflater = android.view.LayoutInflater.from(r.activity);
                    inflated = inflater.inflate(layoutId, null);
                } catch (Throwable e) {
                    /* Standard inflater needs Context — expected to fail here. */
                }
                if (inflated != null) {
                    com.westlake.engine.WestlakeView.setRoot(r.activity, inflated);
                    Log.d(TAG, "  tryRecoverContent: standard inflated 0x" + Integer.toHexString(layoutId)
                            + " -> " + inflated.getClass().getSimpleName());
                } else if (wlNode == null) {
                    Log.d(TAG, "  tryRecoverContent: neither inflater produced output");
                }
            } catch (Throwable ex) {
                Log.d(TAG, "  tryRecoverContent inflate failed: " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
                /* PF-arch-025: Context-free stub fallback. When LayoutInflater
                 * can't run (no Resources/Context), build a marker view tree
                 * via Unsafe.allocateInstance — the render loop just needs
                 * SOMETHING in WestlakeView's map so it doesn't fall back to
                 * "no View tree" mode. The stub view is empty but lets the
                 * pipeline progress. */
                try {
                    Object unsafe = getUnsafeSingleton("sun.misc.Unsafe");
                    android.view.ViewGroup root = (android.view.ViewGroup) unsafe.getClass()
                            .getMethod("allocateInstance", Class.class)
                            .invoke(unsafe, com.westlake.engine.WestlakeStubView.class);
                    com.westlake.engine.WestlakeView.setRoot(r.activity, root);
                    Log.d(TAG, "  tryRecoverContent: Unsafe-allocated WestlakeStubView root stored in WestlakeView");
                    return;
                } catch (Throwable stubEx) {
                    Log.d(TAG, "  tryRecoverContent stub-view fallback failed: "
                            + stubEx.getClass().getSimpleName() + ": " + stubEx.getMessage());
                }
                // Legacy programmatic fallback below (kept for canary apps that have Resources working).
                try {
                    android.widget.LinearLayout root = new android.widget.LinearLayout(r.activity);
                    root.setOrientation(android.widget.LinearLayout.VERTICAL);
                    root.setBackgroundColor(0xFF1A237E); // deep indigo so it's distinguishable
                    android.widget.LinearLayout.LayoutParams lp =
                            new android.widget.LinearLayout.LayoutParams(
                                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT);
                    root.setLayoutParams(lp);
                    android.widget.TextView tv = new android.widget.TextView(r.activity);
                    tv.setText(r.component.getClassName() + "\nWestlake guest dalvikvm\nresumed (programmatic fallback)");
                    tv.setTextColor(0xFFFFFFFF);
                    tv.setTextSize(18.0f);
                    tv.setPadding(40, 80, 40, 40);
                    root.addView(tv,
                            new android.widget.LinearLayout.LayoutParams(
                                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));
                    android.widget.TextView marker = new android.widget.TextView(r.activity);
                    marker.setText("PF-noice programmatic fallback active");
                    marker.setTextColor(0xFF80CBC4);
                    marker.setTextSize(12.0f);
                    marker.setPadding(40, 8, 40, 8);
                    root.addView(marker,
                            new android.widget.LinearLayout.LayoutParams(
                                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));
                    /* PF-arch-025: bypass Window/setContentView entirely.
                     * activity.setContentView → window.getDecorView() fails
                     * (abstract); store the root in WestlakeView's map so the
                     * render loop can pick it up directly. */
                    try {
                        r.activity.setContentView(root);
                        Log.d(TAG, "  tryRecoverContent: programmatic LinearLayout via setContentView");
                    } catch (Throwable swcv) {
                        com.westlake.engine.WestlakeView.setRoot(r.activity, root);
                        Log.d(TAG, "  tryRecoverContent: programmatic LinearLayout via WestlakeView.setRoot (setContentView failed: "
                                + swcv.getClass().getSimpleName() + ")");
                    }
                } catch (Throwable progEx) {
                    Log.d(TAG, "  tryRecoverContent programmatic fallback failed: "
                            + progEx.getClass().getSimpleName() + ": " + progEx.getMessage());
                }
            }
            tryRecoverFragments(r.activity);
            }  // end PF-noice-013 else (no pre-installed content)
        }
        if (isFinishedOrDestroyed(r)) {
            return;
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

    private boolean hasInstalledWindowContent(Activity activity) {
        try {
            if (activity == null || activity.getWindow() == null) {
                return false;
            }
            android.view.View decor = activity.getWindow().getDecorView();
            if (!(decor instanceof android.view.ViewGroup)) {
                return false;
            }
            android.view.ViewGroup group = (android.view.ViewGroup) decor;
            return group.getChildCount() > 0;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private void logMainDispatcherProbe(String label, ClassLoader cl) {
        final String serviceName = "META-INF/services/kotlinx.coroutines.internal.MainDispatcherFactory";
        try {
            Log.d(TAG, "  " + label + " cl=" + classLoaderTag(cl));
            if (cl == null) {
                return;
            }
            java.util.Enumeration<java.net.URL> resources = cl.getResources(serviceName);
            int count = 0;
            while (resources != null && resources.hasMoreElements() && count < 8) {
                Log.d(TAG, "  " + label + " service[" + count + "]=" + resources.nextElement());
                count++;
            }
            if (count == 0) {
                Log.d(TAG, "  " + label + " service not found");
            }
            try {
                Class<?> factory = Class.forName(
                        "kotlinx.coroutines.android.AndroidDispatcherFactory",
                        false,
                        cl);
                Log.d(TAG, "  " + label + " factoryClass=" + factory + " loader="
                        + classLoaderTag(factory.getClassLoader()));
            } catch (Throwable t) {
                Log.d(TAG, "  " + label + " factoryClass failed: "
                        + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
        } catch (Throwable t) {
            Log.d(TAG, "  " + label + " probe failed: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    private static String classLoaderTag(ClassLoader cl) {
        if (cl == null) {
            return "<null>";
        }
        return cl.getClass().getName()
                + "@"
                + Integer.toHexString(System.identityHashCode(cl));
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
        int filledAbstract = 0;
        int lazyReplaced = 0;
        try {
            Class<?> cls = activity.getClass();
            while (cls != null && cls != Object.class) {
                for (java.lang.reflect.Field f : cls.getDeclaredFields()) {
                    if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
                    Class<?> type = f.getType();
                    if (type.isPrimitive()) continue;
                    if (type == String.class) continue;
                    f.setAccessible(true);
                    Object currentValue = f.get(activity);

                    // PF-noice-022 (2026-05-11): for kotlin.Lazy-shaped interfaces
                    // (`getValue():Object` + few methods), REPLACE even non-null
                    // values. Hilt-generated activities initialize these Lazy
                    // fields in <init> with a lambda that calls
                    // EntryPoints.get(app, ...).get(); the lambda silently fails
                    // and getValue() returns null (because we lack the Hilt
                    // singleton component), causing onCreate to NPE. Replacing
                    // the Lazy with a stub that returns a deep-stubbed instance
                    // of the inferred T lets onCreate proceed.
                    if (type.isInterface() && isKotlinLazyInterface(type)) {
                        if (currentValue == null || !lazyReturnsNonNull(currentValue)) {
                            try {
                                Object lazyStub = buildLazyStub(activity, f, type);
                                if (lazyStub != null) {
                                    f.set(activity, lazyStub);
                                    lazyReplaced++;
                                    continue;
                                }
                            } catch (Throwable ignored) { }
                        }
                    }
                    if (currentValue != null) continue;

                    if (type.isInterface()) {
                        // (kotlin.Lazy case handled above for null values too)
                        // Existing path: Proxy.newProxyInstance for interfaces
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
                    } else if (java.lang.reflect.Modifier.isAbstract(type.getModifiers())
                               || (type.getModifiers() & 0x0400) != 0) {
                        // PF-noice-016 (2026-05-05): handle abstract class fields
                        // (Kotlin lateinit on sealed/abstract types — e.g. noice's
                        // subscriptionBillingProvider). Use Unsafe.allocateInstance
                        // to get a zero-init non-null instance of the EXACT type
                        // without running any constructor. Methods called on the
                        // returned instance will likely NPE; that's OK because
                        // the immediate goal is to satisfy the lateinit null-check
                        // so onCreate's setContentView path can run.
                        try {
                            Object stub = unsafeAllocateInstanceShared(type);
                            if (stub != null) {
                                f.set(activity, stub);
                                // PF-noice-022: recursively fill the stub's fields
                                // so accessing stub.foo doesn't NPE again.
                                deepFillStubFields(stub, 2);
                                filledAbstract++;
                            }
                        } catch (Throwable ex) { /* skip */ }
                    } else {
                        // Non-abstract, non-interface, non-primitive object field.
                        // Same Unsafe.allocateInstance fallback — covers concrete
                        // classes without no-arg constructors (e.g. final classes
                        // with @Inject fields). Lower priority than abstract since
                        // these tend to have constructors that work via
                        // newInstance, but try anyway.
                        try {
                            // First try a no-arg constructor if available
                            try {
                                java.lang.reflect.Constructor<?> ctor =
                                        type.getDeclaredConstructor();
                                ctor.setAccessible(true);
                                Object stub = ctor.newInstance();
                                f.set(activity, stub);
                                deepFillStubFields(stub, 2);
                                filledAbstract++;
                                continue;
                            } catch (NoSuchMethodException | InstantiationException
                                     | IllegalAccessException
                                     | java.lang.reflect.InvocationTargetException ignored) {}
                            // Fall back to Unsafe.allocateInstance
                            Object stub = unsafeAllocateInstanceShared(type);
                            if (stub != null) {
                                f.set(activity, stub);
                                deepFillStubFields(stub, 2);
                                filledAbstract++;
                            }
                        } catch (Throwable ex) { /* skip */ }
                    }
                }
                cls = cls.getSuperclass();
            }
        } catch (Throwable t) { /* reflection failure */ }
        if (filled > 0 || filledAbstract > 0 || lazyReplaced > 0) {
            Log.d(TAG, "  fillNullFieldsWithProxies: filled " + filled + " interfaces, "
                    + filledAbstract + " abstract/concrete fields, "
                    + lazyReplaced + " Lazy<T> replacements");
        }
    }

    // PF-noice-022 (2026-05-11): Hilt-graph injector helpers.
    //
    // Hilt-injected activity fields are normally populated by a generated
    // *_MembersInjector. In the Westlake standalone runtime, the Hilt
    // singleton component (`Application.componentManager().c()`) can't bind
    // because the Application isn't `Hilt_NoiceApplication` (loadAppClass
    // SIGBUSes — see PF-arch-010). When MainActivity.onCreate dereferences
    // a kotlin.Lazy field that was wired to `EntryPoints.get(app, ...).get()`,
    // the missing entry-point throws IllegalStateException inside getValue(),
    // and the activity NPEs on the cached repository's Context field.
    //
    // The graph-injector below works generically: it walks the activity's
    // null fields, replaces kotlin.Lazy-shaped interfaces with a custom Lazy
    // that produces a deep-stubbed instance, and recursively fills Context-,
    // SharedPreferences-, and Resources-typed slots on those stubs with the
    // Application instance. No app-specific names appear here — the only
    // signals are method shape (`getValue():Object`) and field type families.

    /**
     * Parse an NPE message of the form:
     *   "Attempt to read from field 'Lcom/foo/Bar; com.foo.Baz.x' on a null object reference ..."
     * Extract `com.foo.Baz` (the owner class of the null-dereferenced field) and
     * build a deep-stubbed instance via Unsafe.allocateInstance + deepFillStubFields.
     * Then find any Lazy field on the activity that, when getValue() is called,
     * returns null or throws — and patch its initializer to return our stub.
     *
     * Returns true if we made progress (i.e. patched at least one Lazy); false
     * otherwise (caller should stop retrying).
     */
    private boolean repairNpeViaLazyStub(Activity activity, String npeMessage) {
        if (npeMessage == null) return false;
        // NPE messages on ART look like:
        //   "Attempt to read from field 'TYPE pkg.Cls.field' on a null object reference in method ..."
        // The owner class name is between the space-after-TYPE and the last '.'.
        String ownerClassName = extractNpeOwnerClass(npeMessage);
        if (ownerClassName == null) return false;
        // PF-noice-022: prefer the activity's own classloader's loadClass()
        // over Class.forName(...) — the latter has historically SIGBUSed on
        // our standalone guest for app DEX classes due to caller-sensitive
        // intrinsics. Even loadClass can crash; wrap everything.
        ClassLoader cl = activity.getClass().getClassLoader();
        Class<?> ownerClass = null;
        // Search the activity's class hierarchy for a field whose declared
        // type's name matches ownerClassName — that gives us the Class<?>
        // WITHOUT any cross-CL lookup (which is what SIGBUSes).
        try {
            Class<?> walk = activity.getClass();
            while (walk != null && walk != Object.class && ownerClass == null) {
                for (java.lang.reflect.Field f : walk.getDeclaredFields()) {
                    Class<?> t = f.getType();
                    if (t != null && ownerClassName.equals(t.getName())) {
                        ownerClass = t;
                        break;
                    }
                }
                walk = walk.getSuperclass();
            }
        } catch (Throwable ignored) { }
        // PF-noice-022 (2026-05-11): unconditional class-lookup paths (loadClass,
        // resolveAppClassOrNull, native findClass) have all SIGBUSed for unseen
        // app-DEX classes on the standalone guest (e.g. noice.repository.p — a
        // Hilt-bound type only reachable transitively via Lazy<T>). Since the
        // standalone Westlake runtime can't safely resolve such classes from a
        // bare name, we bail out and let the existing tryRecoverContent
        // fallbacks (WestlakeInflater + WestlakeView.setRoot) take over.
        if (ownerClass == null) {
            Log.d(TAG, "  NPE repair: " + ownerClassName + " not directly reachable; skipping");
            return false;
        }
        // Build a stub of ownerClass.
        Object stub = buildDeepStubInstance(ownerClass, activity);
        if (stub == null) {
            Log.d(TAG, "  NPE repair: can't allocate " + ownerClassName);
            return false;
        }
        // Find a Lazy field on the activity (or super) that returns null/throws
        // when getValue() is called. Replace it with a Lazy that returns `stub`.
        // Track whether we patched anything.
        boolean patched = false;
        try {
            Class<?> cls = activity.getClass();
            while (cls != null && cls != Object.class) {
                for (java.lang.reflect.Field f : cls.getDeclaredFields()) {
                    if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
                    Class<?> type = f.getType();
                    if (!type.isInterface()) continue;
                    if (!isKotlinLazyInterface(type)) continue;
                    f.setAccessible(true);
                    Object existing = f.get(activity);
                    boolean broken = existing == null || !lazyReturnsValueAssignableTo(existing, ownerClass);
                    if (!broken) continue;
                    Object lazyStub = buildFixedLazy(type, ownerClass, stub, activity);
                    if (lazyStub != null) {
                        f.set(activity, lazyStub);
                        patched = true;
                        Log.d(TAG, "  NPE repair: patched Lazy field "
                                + cls.getSimpleName() + "." + f.getName()
                                + " -> " + ownerClass.getSimpleName());
                    }
                }
                cls = cls.getSuperclass();
            }
        } catch (Throwable t) {
            Log.d(TAG, "  NPE repair: walk failed: " + t.getClass().getSimpleName());
        }
        return patched;
    }

    /** Parse the owner class name out of an ART NPE message. */
    private static String extractNpeOwnerClass(String msg) {
        // Look for the pattern: "'<TYPE> <OWNER.FIELD>'"
        int q1 = msg.indexOf('\'');
        if (q1 < 0) return null;
        int q2 = msg.indexOf('\'', q1 + 1);
        if (q2 < 0) return null;
        String quoted = msg.substring(q1 + 1, q2);
        // quoted = "android.content.Context com.foo.Bar.fieldName"
        int sp = quoted.indexOf(' ');
        if (sp < 0) return null;
        String full = quoted.substring(sp + 1); // "com.foo.Bar.fieldName"
        int lastDot = full.lastIndexOf('.');
        if (lastDot < 0) return null;
        return full.substring(0, lastDot);
    }

    /** True if Lazy.getValue() returns an instance assignable to expected. */
    private boolean lazyReturnsValueAssignableTo(Object lazy, Class<?> expected) {
        try {
            java.lang.reflect.Method m = lazy.getClass().getMethod("getValue");
            m.setAccessible(true);
            Object v = m.invoke(lazy);
            return v != null && expected.isInstance(v);
        } catch (Throwable t) {
            return false;
        }
    }

    /** Build a Lazy-shaped proxy that returns a fixed pre-built stub value. */
    private Object buildFixedLazy(Class<?> lazyInterface, Class<?> valueType,
                                  final Object value, Activity activity) {
        try {
            ClassLoader cl = lazyInterface.getClassLoader();
            if (cl == null) cl = activity.getClass().getClassLoader();
            if (cl == null) cl = Thread.currentThread().getContextClassLoader();
            return java.lang.reflect.Proxy.newProxyInstance(cl,
                    new Class<?>[]{ lazyInterface },
                    new java.lang.reflect.InvocationHandler() {
                        @Override
                        public Object invoke(Object p, java.lang.reflect.Method m, Object[] args) {
                            String name = m.getName();
                            Class<?> rt = m.getReturnType();
                            if ("toString".equals(name)) return "FixedLazy[" + valueType.getSimpleName() + "]";
                            if ("hashCode".equals(name)) return System.identityHashCode(p);
                            if ("equals".equals(name)) return args != null && args.length > 0 && p == args[0];
                            if (rt == boolean.class) return Boolean.TRUE;
                            if (rt == void.class) return null;
                            if (rt == int.class) return 0;
                            if (rt == long.class) return 0L;
                            if (rt == float.class) return 0f;
                            if (rt == double.class) return 0.0;
                            if (rt == String.class) return "";
                            return value;
                        }
                    });
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Best-effort check whether an existing Lazy already produces a non-null
     * value. We call `getValue()` reflectively. If it succeeds with a non-null
     * return, the Lazy is healthy and we leave it alone. On any throw or null
     * return, we replace it with our stub.
     */
    private boolean lazyReturnsNonNull(Object lazy) {
        if (lazy == null) return false;
        try {
            java.lang.reflect.Method m = lazy.getClass().getMethod("getValue");
            m.setAccessible(true);
            Object v = m.invoke(lazy);
            return v != null;
        } catch (Throwable t) {
            return false;
        }
    }

    /** True if the given interface looks like kotlin.Lazy<T>. */
    private boolean isKotlinLazyInterface(Class<?> type) {
        if (type == null || !type.isInterface()) return false;
        String name = type.getName();
        if ("kotlin.Lazy".equals(name)) return true;
        // Obfuscated kotlin.Lazy (e.g. noice's `f7.b`): identify by signature.
        // The Kotlin Lazy<T> interface declares exactly:
        //     T getValue();
        //     boolean isInitialized();
        // Stripping the original method-name annotations after R8 leaves at
        // minimum `getValue()Ljava/lang/Object;` — that's the marker we use.
        try {
            java.lang.reflect.Method[] methods = type.getDeclaredMethods();
            boolean hasGetValue = false;
            for (java.lang.reflect.Method m : methods) {
                if (m.getParameterCount() != 0) continue;
                if (m.getReturnType() == Object.class && "getValue".equals(m.getName())) {
                    hasGetValue = true;
                    break;
                }
            }
            return hasGetValue && methods.length <= 4;
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * Build a Proxy that implements the given Lazy-shaped interface and
     * memoises a deep-stubbed instance of the inferred value type.
     *
     * Value type inference (in order):
     * 1) Generic type argument of the field signature (e.g. `Lf7/b<Tnoice/...>`)
     * 2) The Lazy implementation class's `value` field type
     * 3) Fall back to Object.class — getValue() returns null but doesn't NPE.
     */
    private Object buildLazyStub(final Activity activity,
                                 java.lang.reflect.Field f,
                                 final Class<?> lazyInterface) {
        final Class<?> valueType = inferLazyValueType(f);
        final Object[] cached = new Object[1];
        try {
            ClassLoader cl = lazyInterface.getClassLoader();
            if (cl == null) cl = activity.getClass().getClassLoader();
            if (cl == null) cl = Thread.currentThread().getContextClassLoader();
            java.lang.reflect.InvocationHandler h = new java.lang.reflect.InvocationHandler() {
                @Override
                public Object invoke(Object proxy, java.lang.reflect.Method m, Object[] args) {
                    String name = m.getName();
                    Class<?> rt = m.getReturnType();
                    if ("toString".equals(name)) return "LazyStub[" + (valueType != null ? valueType.getSimpleName() : "?") + "]";
                    if ("hashCode".equals(name)) return System.identityHashCode(proxy);
                    if ("equals".equals(name)) return args != null && args.length > 0 && proxy == args[0];
                    if ("isInitialized".equals(name) || rt == boolean.class) return Boolean.TRUE;
                    // getValue() and any zero-arg Object-returning method
                    if (rt == void.class) return null;
                    if (rt == int.class) return 0;
                    if (rt == long.class) return 0L;
                    if (rt == float.class) return 0f;
                    if (rt == double.class) return 0.0;
                    if (rt == String.class) return "";
                    synchronized (cached) {
                        if (cached[0] == null) {
                            cached[0] = buildDeepStubInstance(valueType, activity);
                        }
                        return cached[0];
                    }
                }
            };
            return java.lang.reflect.Proxy.newProxyInstance(cl,
                    new Class<?>[]{ lazyInterface }, h);
        } catch (Throwable t) {
            return null;
        }
    }

    /** Infer the Lazy<T> value type from the field's generic signature. */
    private Class<?> inferLazyValueType(java.lang.reflect.Field f) {
        try {
            java.lang.reflect.Type gt = f.getGenericType();
            if (gt instanceof java.lang.reflect.ParameterizedType) {
                java.lang.reflect.Type[] args = ((java.lang.reflect.ParameterizedType) gt)
                        .getActualTypeArguments();
                if (args != null && args.length > 0) {
                    java.lang.reflect.Type a0 = args[0];
                    if (a0 instanceof Class<?>) return (Class<?>) a0;
                    if (a0 instanceof java.lang.reflect.ParameterizedType) {
                        Object raw = ((java.lang.reflect.ParameterizedType) a0).getRawType();
                        if (raw instanceof Class<?>) return (Class<?>) raw;
                    }
                }
            }
        } catch (Throwable ignored) { }
        return null;
    }

    /**
     * Allocate an instance of the given type via Unsafe (no constructor),
     * then recursively populate Context/SharedPreferences/Resources fields
     * with the activity's Application and primitive-ish defaults so that
     * the consuming code can do `.field.getString(...)` without NPE.
     */
    private Object buildDeepStubInstance(Class<?> type, Activity activity) {
        if (type == null || type == Object.class) return null;
        if (type.isInterface()) {
            try {
                return java.lang.reflect.Proxy.newProxyInstance(
                        type.getClassLoader(),
                        new Class<?>[]{ type },
                        new java.lang.reflect.InvocationHandler() {
                            @Override public Object invoke(Object p, java.lang.reflect.Method m, Object[] a) {
                                return defaultReturnFor(m.getReturnType());
                            }
                        });
            } catch (Throwable t) {
                return null;
            }
        }
        try {
            Object instance = unsafeAllocateInstanceShared(type);
            if (instance == null) return null;
            deepFillStubFields(instance, 2);
            return instance;
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Recursively fill null fields on a stub instance (bounded depth).
     * Targets:
     *  - Context-typed → set to mServer.getApplication() if available
     *  - SharedPreferences → set to a stub (or app's getSharedPreferences if
     *    we can call it without further NPE)
     *  - Resources → set to app.getResources() if available
     *  - Other interfaces → proxy
     *  - Primitives → leave alone (already zero-init)
     *  - String → empty string
     */
    private void deepFillStubFields(Object stub, int depth) {
        if (stub == null || depth < 0) return;
        Class<?> cls = stub.getClass();
        if (cls == null) return;
        // Skip framework/library objects — we only want to fix app DTOs.
        String stubName = cls.getName();
        if (stubName.startsWith("java.") || stubName.startsWith("kotlin.")
                || stubName.startsWith("android.") || stubName.startsWith("androidx.")
                || stubName.startsWith("dagger.")) {
            return;
        }
        android.app.Application app = mServer != null ? mServer.getApplication() : null;
        // Use the Application as fake Context; fall back to sRealContext.
        android.content.Context fakeCtx = app;
        if (fakeCtx == null) {
            try {
                Object ctx = com.westlake.engine.WestlakeLauncher.sRealContext;
                if (ctx instanceof android.content.Context) {
                    fakeCtx = (android.content.Context) ctx;
                }
            } catch (Throwable ignored) { }
        }
        try {
            Class<?> cur = cls;
            while (cur != null && cur != Object.class) {
                String curName = cur.getName();
                if (curName.startsWith("java.") || curName.startsWith("kotlin.")
                        || curName.startsWith("android.") || curName.startsWith("androidx.")) {
                    break;
                }
                for (java.lang.reflect.Field f : cur.getDeclaredFields()) {
                    if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
                    Class<?> type = f.getType();
                    if (type.isPrimitive()) continue;
                    try {
                        f.setAccessible(true);
                        if (f.get(stub) != null) continue;
                        Object value = makeFieldValue(type, fakeCtx, depth - 1);
                        if (value != null) {
                            f.set(stub, value);
                        }
                    } catch (Throwable ignored) { }
                }
                cur = cur.getSuperclass();
            }
        } catch (Throwable ignored) { }
    }

    /** Make a non-null stub value of the given type for deep-fill. */
    private Object makeFieldValue(Class<?> type, android.content.Context fakeCtx, int depth) {
        if (type == null) return null;
        if (type == String.class) return "";
        if (type == CharSequence.class) return "";
        if (type == Object.class) return null;
        if (java.util.List.class.isAssignableFrom(type)) return new java.util.ArrayList<>();
        if (java.util.Map.class.isAssignableFrom(type)) return new java.util.HashMap<>();
        if (java.util.Set.class.isAssignableFrom(type)) return new java.util.HashSet<>();
        // Context-shaped fields: route to the Application instance.
        if (fakeCtx != null) {
            if (type.isInstance(fakeCtx)) return fakeCtx;
            if (android.content.Context.class.isAssignableFrom(type)) return fakeCtx;
        }
        // SharedPreferences → try app's SharedPreferences; fall back to proxy.
        if (type == android.content.SharedPreferences.class) {
            if (fakeCtx != null) {
                try {
                    return fakeCtx.getSharedPreferences("__westlake_stub__", 0);
                } catch (Throwable ignored) { }
            }
            return buildSharedPreferencesStub();
        }
        // Resources → use the app's Resources if we can get them.
        if (type == android.content.res.Resources.class) {
            if (fakeCtx != null) {
                try { return fakeCtx.getResources(); } catch (Throwable ignored) { }
            }
            return null;
        }
        // Generic interfaces → proxy.
        if (type.isInterface()) {
            try {
                return java.lang.reflect.Proxy.newProxyInstance(
                        type.getClassLoader(),
                        new Class<?>[]{ type },
                        new java.lang.reflect.InvocationHandler() {
                            @Override public Object invoke(Object p, java.lang.reflect.Method m, Object[] a) {
                                return defaultReturnFor(m.getReturnType());
                            }
                        });
            } catch (Throwable ignored) { return null; }
        }
        if (depth < 0) return null;
        // Skip framework / library namespaces — Unsafe.allocateInstance on
        // e.g. java.io.File can break the runtime, and any framework class
        // typically needs a constructor to be functional.
        String tn = type.getName();
        if (tn.startsWith("java.") || tn.startsWith("javax.") || tn.startsWith("sun.")
                || tn.startsWith("kotlin.") || tn.startsWith("kotlinx.")
                || tn.startsWith("android.") || tn.startsWith("androidx.")
                || tn.startsWith("dagger.") || tn.startsWith("com.google.")
                || tn.startsWith("org.")) {
            return null;
        }
        // Concrete: best-effort Unsafe allocation.
        try {
            Object inst = unsafeAllocateInstanceShared(type);
            if (inst != null) deepFillStubFields(inst, depth - 1);
            return inst;
        } catch (Throwable ignored) { return null; }
    }

    /** SharedPreferences stub backed by an in-memory map. */
    private Object buildSharedPreferencesStub() {
        try {
            return java.lang.reflect.Proxy.newProxyInstance(
                    MiniActivityManager.class.getClassLoader(),
                    new Class<?>[]{ android.content.SharedPreferences.class },
                    new java.lang.reflect.InvocationHandler() {
                        final java.util.Map<String, Object> map = new java.util.HashMap<>();
                        @Override public Object invoke(Object p, java.lang.reflect.Method m, Object[] args) {
                            String name = m.getName();
                            Class<?> rt = m.getReturnType();
                            if ("getString".equals(name) && args != null && args.length >= 2) {
                                Object v = map.get((String) args[0]);
                                return v instanceof String ? v : args[1];
                            }
                            if ("getInt".equals(name) && args != null && args.length >= 2) {
                                Object v = map.get((String) args[0]);
                                return v instanceof Integer ? v : args[1];
                            }
                            if ("getBoolean".equals(name) && args != null && args.length >= 2) {
                                Object v = map.get((String) args[0]);
                                return v instanceof Boolean ? v : args[1];
                            }
                            if ("getLong".equals(name) && args != null && args.length >= 2) {
                                Object v = map.get((String) args[0]);
                                return v instanceof Long ? v : args[1];
                            }
                            if ("getFloat".equals(name) && args != null && args.length >= 2) {
                                Object v = map.get((String) args[0]);
                                return v instanceof Float ? v : args[1];
                            }
                            if ("contains".equals(name)) return Boolean.FALSE;
                            if ("getAll".equals(name)) return new java.util.HashMap<>();
                            if ("edit".equals(name)) {
                                // Return an Editor that doesn't crash.
                                return java.lang.reflect.Proxy.newProxyInstance(
                                        MiniActivityManager.class.getClassLoader(),
                                        new Class<?>[]{ android.content.SharedPreferences.Editor.class },
                                        new java.lang.reflect.InvocationHandler() {
                                            @Override public Object invoke(Object p, java.lang.reflect.Method m, Object[] a) {
                                                String nm = m.getName();
                                                Class<?> ret = m.getReturnType();
                                                if (nm.startsWith("put") && a != null && a.length >= 2 && a[0] instanceof String) {
                                                    map.put((String) a[0], a[1]);
                                                }
                                                if (nm.equals("remove") && a != null && a.length >= 1 && a[0] instanceof String) {
                                                    map.remove((String) a[0]);
                                                }
                                                if (nm.equals("clear")) {
                                                    map.clear();
                                                }
                                                if (nm.equals("commit")) return Boolean.TRUE;
                                                if (nm.equals("apply")) return null;
                                                return ret.isInstance(p) ? p : null;
                                            }
                                        });
                            }
                            return defaultReturnFor(rt);
                        }
                    });
        } catch (Throwable t) {
            return null;
        }
    }

    /** Default return value for a method type (proxy fallback). */
    private static Object defaultReturnFor(Class<?> rt) {
        if (rt == void.class) return null;
        if (rt == boolean.class) return Boolean.FALSE;
        if (rt == int.class) return 0;
        if (rt == long.class) return 0L;
        if (rt == float.class) return 0f;
        if (rt == double.class) return 0.0;
        if (rt == byte.class) return (byte) 0;
        if (rt == short.class) return (short) 0;
        if (rt == char.class) return '\0';
        if (rt == String.class) return "";
        if (java.util.List.class.isAssignableFrom(rt)) return new java.util.ArrayList<>();
        if (java.util.Map.class.isAssignableFrom(rt)) return new java.util.HashMap<>();
        if (java.util.Set.class.isAssignableFrom(rt)) return new java.util.HashSet<>();
        return null;
    }

    // PF-noice-016 (2026-05-05): cached Unsafe.allocateInstance handle.
    // sun.misc.Unsafe is hidden API on Android but exposed via reflection.
    private static volatile Object sUnsafeInstance;
    private static volatile java.lang.reflect.Method sUnsafeAllocateInstanceMethod;

    private static Object unsafeAllocateInstanceShared(Class<?> type) throws Throwable {
        if (sUnsafeAllocateInstanceMethod == null) {
            synchronized (MiniActivityManager.class) {
                if (sUnsafeAllocateInstanceMethod == null) {
                    Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
                    java.lang.reflect.Field theUnsafe =
                            unsafeClass.getDeclaredField("theUnsafe");
                    theUnsafe.setAccessible(true);
                    sUnsafeInstance = theUnsafe.get(null);
                    sUnsafeAllocateInstanceMethod =
                            unsafeClass.getMethod("allocateInstance", Class.class);
                }
            }
        }
        try {
            return sUnsafeAllocateInstanceMethod.invoke(sUnsafeInstance, type);
        } catch (java.lang.reflect.InvocationTargetException ite) {
            // Some abstract types reject allocateInstance; return null to skip
            return null;
        }
    }

    // PF-noice-018 (2026-05-05) — REMOVED. The user explicitly rejected a
    // programmatic mock UI as "not noice"; real noice UI requires either
    // (a) fix AXML inflater so all required views materialize, or
    // (b) bytecode-rewrite noice's view-binding to suppress
    //     Missing-required-view throws (deep — DEX rewrite at runtime), or
    // (c) hook View.findViewById globally to never return null
    //     (highly invasive — affects every app/path).
    // None landed this session.
    private void installNoicePackageMockUi_DISABLED(Activity activity) {
        String pkg = activity.getPackageName();
        if (pkg == null) return;
        if (!(pkg.startsWith("com.github.ashutoshgngwr.")
                || pkg.startsWith("com.trynoice."))) {
            return;
        }
        android.view.Window window = activity.getWindow();
        if (window == null) return;
        android.view.View decor = window.getDecorView();
        if (!(decor instanceof android.view.ViewGroup)) return;
        android.view.ViewGroup decorGroup = (android.view.ViewGroup) decor;

        android.content.Context ctx = activity;

        // Root vertical LinearLayout, dark grey background (noice theme)
        android.widget.LinearLayout root = new android.widget.LinearLayout(ctx);
        root.setOrientation(android.widget.LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFF1E1E1E);
        root.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT));

        // Title bar
        android.widget.TextView title = new android.widget.TextView(ctx);
        title.setText("Noice");
        title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 24);
        title.setTextColor(0xFFFAA13E);
        title.setPadding(48, 80, 48, 48);
        root.addView(title, new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));

        // Subtitle
        android.widget.TextView subtitle = new android.widget.TextView(ctx);
        subtitle.setText("Soothing soundscapes");
        subtitle.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14);
        subtitle.setTextColor(0xFFAAAAAA);
        subtitle.setPadding(48, 0, 48, 32);
        root.addView(subtitle, new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));

        // Sound cards (mock list)
        String[] soundNames = { "Rain", "Forest", "Ocean", "Fire", "Wind", "Stream" };
        int[] cardColors = { 0xFF1976D2, 0xFF388E3C, 0xFF0288D1, 0xFFD84315, 0xFF7B1FA2, 0xFF00838F };
        for (int i = 0; i < soundNames.length; i++) {
            android.widget.LinearLayout card = new android.widget.LinearLayout(ctx);
            card.setOrientation(android.widget.LinearLayout.HORIZONTAL);
            card.setBackgroundColor(0xFF2D2D2D);
            card.setPadding(48, 32, 48, 32);
            card.setGravity(android.view.Gravity.CENTER_VERTICAL);
            android.widget.LinearLayout.LayoutParams cardLp =
                    new android.widget.LinearLayout.LayoutParams(
                            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            cardLp.setMargins(48, 0, 48, 16);
            root.addView(card, cardLp);

            // Icon: small colored square
            android.widget.TextView icon = new android.widget.TextView(ctx);
            icon.setBackgroundColor(cardColors[i]);
            icon.setText(" ");
            icon.setMinimumWidth(96);
            icon.setMinimumHeight(96);
            android.widget.LinearLayout.LayoutParams iconLp =
                    new android.widget.LinearLayout.LayoutParams(96, 96);
            iconLp.setMargins(0, 0, 32, 0);
            card.addView(icon, iconLp);

            // Name
            android.widget.TextView name = new android.widget.TextView(ctx);
            name.setText(soundNames[i]);
            name.setTextColor(0xFFFFFFFF);
            name.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 16);
            card.addView(name, new android.widget.LinearLayout.LayoutParams(
                    0,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f));

            // "Play" indicator
            android.widget.TextView play = new android.widget.TextView(ctx);
            play.setText("▶"); // ▶
            play.setTextColor(0xFFFAA13E);
            play.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 20);
            card.addView(play, new android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));
        }

        // Bottom nav strip
        android.widget.LinearLayout bottomNav = new android.widget.LinearLayout(ctx);
        bottomNav.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        bottomNav.setBackgroundColor(0xFF121212);
        bottomNav.setPadding(0, 24, 0, 24);
        android.widget.LinearLayout.LayoutParams navLp =
                new android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
        navLp.weight = 0;
        navLp.gravity = android.view.Gravity.BOTTOM;
        // Add bottom nav at bottom of root via reverse-order — root is vertical
        // top-down. We push it after the cards (above), then a spacer flex.
        // Actually for a simple paint, just add it at end of root.
        String[] navItems = { "Home", "Library", "Premium" };
        for (String item : navItems) {
            android.widget.TextView tab = new android.widget.TextView(ctx);
            tab.setText(item);
            tab.setTextColor(0xFFFAA13E);
            tab.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14);
            tab.setGravity(android.view.Gravity.CENTER);
            tab.setPadding(0, 16, 0, 16);
            bottomNav.addView(tab, new android.widget.LinearLayout.LayoutParams(
                    0,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f));
        }
        // Spacer to push bottom nav down
        android.view.View spacer = new android.view.View(ctx);
        root.addView(spacer, new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f));
        root.addView(bottomNav, navLp);

        // Inject directly into decor (bypasses setContentView's broken path)
        try {
            decorGroup.addView(root);
            Log.d(TAG, "  installNoicePackageMockUi: programmatic mock UI installed (" + soundNames.length + " sound cards)");
        } catch (Throwable addEx) {
            Log.d(TAG, "  installNoicePackageMockUi addView failed: " + addEx.getClass().getSimpleName());
            // Fallback: try via Window.setContentView's installStandaloneChild
            try {
                window.setContentView(root);
                Log.d(TAG, "  installNoicePackageMockUi: setContentView fallback succeeded");
            } catch (Throwable scvEx) {
                Log.d(TAG, "  installNoicePackageMockUi setContentView fallback failed: "
                        + scvEx.getClass().getSimpleName());
            }
        }
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
            if (pkg != null && pkg.startsWith("com.mcdonalds.")) {
                Log.d(TAG, "  tryRecoverFragments: skipping generic fragment recovery for McD "
                        + activity.getClass().getName());
                return;
            }
            // PF-noice-011 (2026-05-05): noice's FragmentManager reflection
            // hangs in Hilt's component-manager init (which deadlocks on
            // stuck Unsafe.park-ing coroutine workers). Skip the generic
            // fragment recovery path for noice — let the launcher's PF301
            // fallback paint a visible fallback frame instead.
            if (pkg != null
                    && (pkg.startsWith("com.github.ashutoshgngwr.")
                            || pkg.startsWith("com.trynoice."))) {
                Log.d(TAG, "  tryRecoverFragments: skipping for noice "
                        + activity.getClass().getName()
                        + " (Hilt reflective init deadlock workaround)");
                return;
            }

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
            if (pkg != null && pkg.startsWith("com.mcdonalds.")) {
                return false;
            }
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
        if (isCutoffCanaryRecord(r)) {
            ShimCompat.setActivityField(r.activity, "mStarted", Boolean.TRUE);
            try {
                com.westlake.engine.WestlakeLauncher.noteMarker("CV performStart direct before onStart");
                r.activity.onStart();
                com.westlake.engine.WestlakeLauncher.noteMarker("CV performStart direct after onStart");
            } catch (Throwable e) {
                com.westlake.engine.WestlakeLauncher.noteMarker("CV performStart direct error");
            }
            return;
        }
        Log.d(TAG, "  performStart: " + r.component.getClassName());
        ShimCompat.setActivityField(r.activity, "mStarted", Boolean.TRUE);
        // Run onStart with timeout (Fragment lifecycle can hang in interpreter)
        final Activity actRef = r.activity;
        final boolean mcdPdp = isMcdOrderProductDetailsRecord(r);
        final boolean[] startDone = { false };
        Thread startThread = new Thread(new Runnable() {
            public void run() {
                Thread.currentThread().setContextClassLoader(resolveAppClassLoader());
                try {
                    actRef.onStart();
                    startDone[0] = true;
                } catch (Throwable e) {
                    startDone[0] = true;
                    Log.w(TAG, "onStart error: " + e.getMessage());
                    if (mcdPdp) {
                        Log.w(TAG, "onStart root for McD PDP: "
                                + e.getClass().getName() + ": " + e.getMessage());
                        StackTraceElement[] stack = e.getStackTrace();
                        if (stack != null) {
                            for (int i = 0; i < stack.length && i < 12; i++) {
                                Log.w(TAG, "  onStart frame[" + i + "] " + stack[i]);
                            }
                        }
                        noteProof("MCD_PDP_ACTIVITY_ONSTART_ERROR"
                                + " error=" + safeToken(e.getClass().getName())
                                + " message=" + safeToken(e.getMessage()));
                    }
                }
            }
        }, "ActivityOnStart");
        startThread.setDaemon(true);
        startThread.start();
        long startWaitMs = mcdPdp ? 35000L : 10000L;
        try { startThread.join(startWaitMs); } catch (InterruptedException ie) {}
        if (!startDone[0] && mcdPdp && startWaitMs < 35000L) {
            boolean contentInstalled = hasInstalledWindowContent(actRef);
            try {
                com.westlake.engine.WestlakeLauncher.noteMarker(
                        "MCD_ORDER_PDP_START_WAIT_EXTEND content="
                                + contentInstalled + " elapsedMs=" + startWaitMs);
            } catch (Throwable ignored) {
            }
            long extraWaitMs = contentInstalled ? 3500L : 6500L;
            try { startThread.join(extraWaitMs); } catch (InterruptedException ie) {}
            startWaitMs += extraWaitMs;
        }
        if (!startDone[0] && mcdPdp && startWaitMs < 35000L
                && !hasInstalledWindowContent(actRef)) {
            try { startThread.join(3500L); } catch (InterruptedException ie) {}
            startWaitMs += 3500L;
        }
        if (!startDone[0]) {
            Log.w(TAG, "performStart TIMEOUT (" + startWaitMs + "ms) for "
                    + r.component.getClassName());
            if (mcdPdp) {
                try {
                    com.westlake.engine.WestlakeLauncher.noteMarker(
                            "MCD_ORDER_PDP_START_EARLY_CONTINUE content="
                                    + hasInstalledWindowContent(actRef));
                } catch (Throwable ignored) {
                }
            }
        }
        try {
            dispatchLifecycleEvent(r.activity, "ON_START");
        } catch (Exception e) {
            Log.w(TAG, "performStart lifecycle dispatch error: " + e.getMessage());
        }
        dispatchMcdPdpObserverBridge(r, "ON_START", "performStart");
    }

    private void performResume(ActivityRecord r) {
        if (isCutoffCanaryRecord(r)) {
            ShimCompat.setActivityField(r.activity, "mResumed", Boolean.TRUE);
            mResumed = r;
            try {
                com.westlake.engine.WestlakeLauncher.noteMarker("CV performResume direct before onResume");
                r.activity.onResume();
                com.westlake.engine.WestlakeLauncher.noteMarker("CV performResume direct after onResume");
            } catch (Throwable e) {
                com.westlake.engine.WestlakeLauncher.noteMarker("CV performResume direct error");
            }
            return;
        }
        Log.d(TAG, "  performResume: " + r.component.getClassName());
        ShimCompat.setActivityField(r.activity, "mResumed", Boolean.TRUE);
        mResumed = r;
        final Activity actRef = r.activity;
        final boolean[] resumeDone = { false };
        Thread resumeThread = new Thread(new Runnable() {
            public void run() {
                Thread.currentThread().setContextClassLoader(resolveAppClassLoader());
                try {
                    actRef.onResume();
                    resumeDone[0] = true;
                } catch (Throwable e) { resumeDone[0] = true; Log.w(TAG, "onResume error: " + e.getMessage()); }
            }
        }, "ActivityOnResume");
        resumeThread.setDaemon(true);
        resumeThread.start();
        try { resumeThread.join(10000); } catch (InterruptedException ie) {}
        if (!resumeDone[0]) Log.w(TAG, "performResume TIMEOUT (10s) for " + r.component.getClassName());
        try {
            r.activity.onPostResume();
        } catch (Throwable e) {
            Log.w(TAG, "onPostResume error (non-fatal): " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        try {
            dispatchLifecycleEvent(r.activity, "ON_RESUME");
        } catch (Throwable e) {
            Log.w(TAG, "performResume lifecycle dispatch error: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        dispatchMcdPdpObserverBridge(r, "ON_RESUME", "performResume");
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
        Log.d(TAG, "  performDestroy repair begin");
        repairLikelyDestroyGraph(r.activity, resolveAppClassLoader(), 4);
        Log.d(TAG, "  performDestroy repair done");
        if (shouldBypassOnDestroy(r)) {
            return;
        }
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

    private boolean shouldBypassOnDestroy(ActivityRecord r) {
        if (r == null || r.component == null) {
            return false;
        }
        String className = r.component.getClassName();
        return "com.mcdonalds.mcdcoreapp.common.activity.SplashActivity".equals(className);
    }

    private boolean isFinishedOrDestroyed(ActivityRecord r) {
        if (r == null || r.activity == null) {
            return true;
        }
        return ShimCompat.getActivityBooleanField(r.activity, "mFinished", false)
                || ShimCompat.getActivityBooleanField(r.activity, "mDestroyed", false)
                || findRecord(r.activity) == null;
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
    /**
     * Dispatch AndroidX lifecycle events for activities that are driven by
     * MiniActivityManager instead of the platform ActivityThread. This is
     * intentionally shaped like Android's LifecycleRegistry path: callers see
     * getLifecycle().handleLifecycleEvent(event), or the obfuscated alias used by
     * the app-bundled AndroidX runtime.
     */
    private void dispatchLifecycleEvent(Activity activity, String eventName) {
        dispatchLifecycleOwnerEvent(activity, eventName);
    }
    private void dispatchLifecycleEvent(Activity activity, String action, Bundle state) {}

    private boolean dispatchLifecycleOwnerEvent(Object owner, String eventName) {
        if (owner == null || eventName == null || !eventName.startsWith("ON_")) {
            return false;
        }
        try {
            java.lang.reflect.Method getLifecycle = findNoArgMethod(
                    owner.getClass(), "getLifecycle");
            if (getLifecycle == null) {
                return false;
            }
            getLifecycle.setAccessible(true);
            Object lifecycle = getLifecycle.invoke(owner);
            if (lifecycle == null) {
                return false;
            }
            ClassLoader cl = lifecycle.getClass().getClassLoader();
            if (cl == null) {
                cl = resolveAppClassLoader();
            }
            Class<?> eventClass = Class.forName("androidx.lifecycle.Lifecycle$Event", false, cl);
            Object event = java.lang.Enum.valueOf((Class) eventClass, eventName);
            boolean dispatched = invokeOneArgLifecycleMethod(
                    lifecycle, "handleLifecycleEvent", event)
                    || invokeOneArgLifecycleMethod(lifecycle, "l", event)
                    || markLifecycleState(lifecycle, eventName, cl);
            boolean viewDispatched = dispatchViewLifecycleOwnerEvent(owner, eventName);
            return dispatched || viewDispatched;
        } catch (Throwable t) {
            return false;
        }
    }

    private boolean dispatchViewLifecycleOwnerEvent(Object owner, String eventName) {
        if (owner == null || eventName == null) {
            return false;
        }
        try {
            java.lang.reflect.Method getViewLifecycleOwner = findNoArgMethod(
                    owner.getClass(), "getViewLifecycleOwner");
            if (getViewLifecycleOwner == null) {
                return false;
            }
            getViewLifecycleOwner.setAccessible(true);
            Object viewOwner = getViewLifecycleOwner.invoke(owner);
            if (viewOwner == null || viewOwner == owner) {
                return false;
            }
            invokeNoArg(viewOwner, "b");
            invokeNoArg(viewOwner, "a");
            java.lang.reflect.Method getLifecycle = findNoArgMethod(
                    viewOwner.getClass(), "getLifecycle");
            if (getLifecycle == null) {
                return false;
            }
            getLifecycle.setAccessible(true);
            Object lifecycle = getLifecycle.invoke(viewOwner);
            if (lifecycle == null) {
                return false;
            }
            ClassLoader cl = lifecycle.getClass().getClassLoader();
            if (cl == null) {
                cl = resolveAppClassLoader();
            }
            Class<?> eventClass = Class.forName("androidx.lifecycle.Lifecycle$Event", false, cl);
            Object event = java.lang.Enum.valueOf((Class) eventClass, eventName);
            boolean dispatched = invokeOneArgLifecycleMethod(
                    lifecycle, "handleLifecycleEvent", event)
                    || invokeOneArgLifecycleMethod(lifecycle, "l", event);
            boolean ownerDispatched = false;
            if (!dispatched) {
                ownerDispatched = invokeOneArgLifecycleMethod(viewOwner, "handleLifecycleEvent", event)
                        || invokeOneArgLifecycleMethod(viewOwner, "a", event)
                        || invokeOneArgLifecycleMethod(viewOwner, "c", event)
                        || invokeOneArgLifecycleMethod(viewOwner, "f", event);
            }
            boolean marked = false;
            if (!dispatched && !ownerDispatched) {
                marked = markLifecycleState(lifecycle, eventName, cl)
                        || markLifecycleState(viewOwner, eventName, cl);
            }
            return dispatched || ownerDispatched || marked;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private boolean invokeOneArgLifecycleMethod(Object target, String name, Object arg) {
        if (target == null || name == null || arg == null) {
            return false;
        }
        for (Class<?> c = target.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
            java.lang.reflect.Method[] methods = c.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                java.lang.reflect.Method method = methods[i];
                if (!name.equals(method.getName())) {
                    continue;
                }
                Class<?>[] params = method.getParameterTypes();
                if (params.length != 1 || !params[0].isAssignableFrom(arg.getClass())) {
                    continue;
                }
                try {
                    method.setAccessible(true);
                    method.invoke(target, arg);
                    return true;
                } catch (Throwable ignored) {
                }
            }
        }
        return false;
    }

    private boolean markLifecycleState(Object lifecycle, String eventName, ClassLoader cl) {
        try {
            Class<?> stateClass = Class.forName("androidx.lifecycle.Lifecycle$State", false, cl);
            String stateName = lifecycleStateForEvent(eventName);
            if (stateName == null) {
                return false;
            }
            Object state = java.lang.Enum.valueOf((Class) stateClass, stateName);
            return invokeOneArgLifecycleMethod(lifecycle, "setCurrentState", state)
                    || invokeOneArgLifecycleMethod(lifecycle, "markState", state)
                    || invokeOneArgLifecycleMethod(lifecycle, "q", state)
                    || invokeOneArgLifecycleMethod(lifecycle, "p", state)
                    || invokeOneArgLifecycleMethod(lifecycle, "n", state)
                    || invokeOneArgLifecycleMethod(lifecycle, "o", state)
                    || invokeOneArgLifecycleMethod(lifecycle, "d", state)
                    || invokeOneArgLifecycleMethod(lifecycle, "f", state)
                    || invokeOneArgLifecycleMethod(lifecycle, "g", state);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static String lifecycleStateForEvent(String eventName) {
        if ("ON_CREATE".equals(eventName) || "ON_STOP".equals(eventName)) {
            return "CREATED";
        }
        if ("ON_START".equals(eventName) || "ON_PAUSE".equals(eventName)) {
            return "STARTED";
        }
        if ("ON_RESUME".equals(eventName)) {
            return "RESUMED";
        }
        if ("ON_DESTROY".equals(eventName)) {
            return "DESTROYED";
        }
        return null;
    }

    private void dispatchMcdPdpObserverBridge(ActivityRecord r, String eventName, String reason) {
        if (!isMcdOrderProductDetailsRecord(r) || r.activity == null) {
            return;
        }
        ArrayList<Object> fragments = new ArrayList<>();
        collectActivityFragments(r.activity, fragments, 0);
        int pdpCount = 0;
        int dispatchCount = 0;
        for (int i = 0; i < fragments.size(); i++) {
            Object fragment = fragments.get(i);
            if (fragment == null) {
                continue;
            }
            String name = fragment.getClass().getName();
            boolean pdp = name != null && name.contains("OrderPDPFragment");
            if (pdp) {
                pdpCount++;
                setBooleanFieldIfPresent(fragment, "mAdded", true);
                if ("ON_START".equals(eventName) || "ON_RESUME".equals(eventName)) {
                    setBooleanFieldIfPresent(fragment, "mStarted", true);
                }
                if ("ON_RESUME".equals(eventName)) {
                    setBooleanFieldIfPresent(fragment, "mResumed", true);
                }
            }
            boolean dispatched = dispatchLifecycleOwnerEvent(fragment, eventName);
            if (dispatched) {
                dispatchCount++;
            }
            if (pdp) {
                noteProof("MCD_PDP_OBSERVER_BRIDGE"
                        + " event=" + eventName
                        + " reason=" + safeToken(reason)
                        + " fragment=" + safeToken(name)
                        + " dispatched=" + dispatched
                        + " added=" + getBooleanFieldIfPresent(fragment, "mAdded")
                        + " started=" + getBooleanFieldIfPresent(fragment, "mStarted")
                        + " resumed=" + getBooleanFieldIfPresent(fragment, "mResumed"));
            }
        }
        noteProof("MCD_PDP_OBSERVER_BRIDGE_SUMMARY"
                + " event=" + eventName
                + " reason=" + safeToken(reason)
                + " fragments=" + fragments.size()
                + " pdp=" + pdpCount
                + " dispatched=" + dispatchCount);
    }

    private void collectActivityFragments(Activity activity, ArrayList<Object> out, int depth) {
        if (activity == null || depth > 4) {
            return;
        }
        collectDirectFragmentFields(activity, out);
        Object manager = invokeNoArg(activity, "getSupportFragmentManager");
        collectFragmentsFromManager(manager, out, depth);
        manager = invokeNoArg(activity, "getFragmentManager");
        collectFragmentsFromManager(manager, out, depth);
    }

    private void collectFragmentsFromManager(Object manager, ArrayList<Object> out, int depth) {
        if (manager == null || depth > 4) {
            return;
        }
        collectFragmentsFromCollection(invokeNoArg(manager, "getFragments"), out, depth);
        collectFragmentsFromCollection(invokeNoArg(manager, "G0"), out, depth);
        for (Class<?> c = manager.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
            java.lang.reflect.Field[] fields = c.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                java.lang.reflect.Field field = fields[i];
                try {
                    field.setAccessible(true);
                    Object value = field.get(manager);
                    if (value instanceof java.util.Collection) {
                        collectFragmentsFromCollection(value, out, depth);
                    }
                } catch (Throwable ignored) {
                }
            }
        }
    }

    private void collectFragmentsFromCollection(Object value, ArrayList<Object> out, int depth) {
        if (!(value instanceof java.util.Collection)) {
            return;
        }
        java.util.Iterator<?> iterator = ((java.util.Collection<?>) value).iterator();
        while (iterator.hasNext()) {
            Object candidate = iterator.next();
            if (!isFragmentLike(candidate) || containsIdentity(out, candidate)) {
                continue;
            }
            out.add(candidate);
            Object childManager = invokeNoArg(candidate, "getChildFragmentManager");
            collectFragmentsFromManager(childManager, out, depth + 1);
        }
    }

    private void collectDirectFragmentFields(Activity activity, ArrayList<Object> out) {
        for (Class<?> c = activity.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
            java.lang.reflect.Field[] fields = c.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                java.lang.reflect.Field field = fields[i];
                try {
                    field.setAccessible(true);
                    Object value = field.get(activity);
                    if (isFragmentLike(value) && !containsIdentity(out, value)) {
                        out.add(value);
                    }
                } catch (Throwable ignored) {
                }
            }
        }
    }

    private static boolean isFragmentLike(Object value) {
        if (value == null) {
            return false;
        }
        String name = value.getClass().getName();
        return name != null
                && name.contains("Fragment")
                && !name.contains("FragmentManager")
                && !name.contains("FragmentTransaction");
    }

    private static boolean containsIdentity(ArrayList<Object> list, Object value) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == value) {
                return true;
            }
        }
        return false;
    }

    private Object invokeNoArg(Object target, String name) {
        if (target == null || name == null) {
            return null;
        }
        try {
            java.lang.reflect.Method method = findNoArgMethod(target.getClass(), name);
            if (method == null) {
                return null;
            }
            method.setAccessible(true);
            return method.invoke(target);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static java.lang.reflect.Method findNoArgMethod(Class<?> type, String name) {
        for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            java.lang.reflect.Method[] methods = c.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                java.lang.reflect.Method method = methods[i];
                if (name.equals(method.getName()) && method.getParameterTypes().length == 0) {
                    return method;
                }
            }
        }
        return null;
    }

    private static void setBooleanFieldIfPresent(Object target, String name, boolean value) {
        try {
            java.lang.reflect.Field field = findField(target.getClass(), name);
            if (field != null && field.getType() == boolean.class) {
                field.setAccessible(true);
                field.setBoolean(target, value);
            }
        } catch (Throwable ignored) {
        }
    }

    private static boolean getBooleanFieldIfPresent(Object target, String name) {
        try {
            java.lang.reflect.Field field = findField(target.getClass(), name);
            if (field != null && field.getType() == boolean.class) {
                field.setAccessible(true);
                return field.getBoolean(target);
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    private static java.lang.reflect.Field findField(Class<?> type, String name) {
        for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            try {
                return c.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
            }
        }
        return null;
    }

    private static void noteProof(String marker) {
        try {
            com.westlake.engine.WestlakeLauncher.marker(marker);
            com.westlake.engine.WestlakeLauncher.appendCutoffCanaryMarker(marker);
        } catch (Throwable ignored) {
        }
    }

    private static String safeToken(Object value) {
        if (value == null) {
            return "null";
        }
        String s = String.valueOf(value);
        if (s.length() == 0) {
            return "empty";
        }
        StringBuilder out = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')
                    || (ch >= '0' && ch <= '9') || ch == '.' || ch == '_'
                    || ch == '-' || ch == ':' || ch == '=') {
                out.append(ch);
            } else {
                out.append('_');
            }
        }
        return out.toString();
    }

    static class ActivityRecord {
        Activity activity;
        Intent intent;
        ComponentName component;
        ActivityRecord caller;
        int requestCode = -1;
    }
}
