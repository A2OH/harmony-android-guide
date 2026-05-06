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

    private Activity instantiateActivity(Class<?> cls, String className, Intent intent) throws Throwable {
        Throwable firstError = null;
        try {
            if (CUTOFF_CANARY_ACTIVITY.equals(className)) {
                com.westlake.engine.WestlakeLauncher.noteMarker(
                        "CV instantiateActivity entry");
            }
            if (CUTOFF_CANARY_ACTIVITY.equals(className)) {
                com.westlake.engine.WestlakeLauncher.noteMarker(
                        "CV instantiateActivity before ctor lookup");
            }
            java.lang.reflect.Constructor<?> ctor = cls.getDeclaredConstructor();
            if (CUTOFF_CANARY_ACTIVITY.equals(className)) {
                com.westlake.engine.WestlakeLauncher.noteMarker(
                        "CV instantiateActivity after ctor lookup");
            }
            ctor.setAccessible(true);
            if (CUTOFF_CANARY_ACTIVITY.equals(className)) {
                com.westlake.engine.WestlakeLauncher.noteMarker(
                        "CV instantiateActivity before ctor new");
            }
            Activity activity = (Activity) ctor.newInstance();
            if (CUTOFF_CANARY_ACTIVITY.equals(className)) {
                com.westlake.engine.WestlakeLauncher.noteMarker(
                        "CV instantiateActivity ctor ok");
            }
            return activity;
        } catch (Throwable t) {
            if (firstError == null) {
                firstError = t;
            }
        }

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

        if (firstError != null) {
            throw firstError;
        }
        throw new InstantiationException("Failed to instantiate " + className);
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
            try {
                manager = app.componentManager();
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
        boolean mcdNonSplash = componentName != null
                && componentName.startsWith("com.mcdonalds.")
                && !componentName.endsWith(".SplashActivity");
        long createTimeoutMs = mcdNonSplash ? 15000L : 5000L;
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
            if (mcdNonSplash && timeoutContentInstalled) {
                Log.w(TAG, "performCreate leaving McD content onCreate thread alive for "
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
            if (isFinishedOrDestroyed(r)) {
                return;
            }
            try {
                // Try setContentView with the splash layout resource ID. Try a
                // sequence of common layout names so non-McD apps (e.g. noice)
                // pick up their actual main layout. Fall back to the McDonald's
                // splash ID as a generic last resort.
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
                r.activity.setContentView(layoutId);
                Log.d(TAG, "  tryRecoverContent: setContentView(0x" + Integer.toHexString(layoutId) + ") OK");
            } catch (Throwable ex) {
                Log.d(TAG, "  tryRecoverContent setContentView failed: " + ex.getMessage());
                // PF-noice (2026-05-04): when XML layout inflation fails due to
                // boot-class cascade (Charset/CoderResult), fall back to a
                // programmatic View tree that bypasses Resources entirely.
                // This gives at least visible "the app reached resume" pixels.
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
                    r.activity.setContentView(root);
                    Log.d(TAG, "  tryRecoverContent: programmatic LinearLayout fallback installed");
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
        try {
            Class<?> cls = activity.getClass();
            while (cls != null && cls != Object.class) {
                for (java.lang.reflect.Field f : cls.getDeclaredFields()) {
                    if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
                    Class<?> type = f.getType();
                    if (type.isPrimitive()) continue;
                    if (type == String.class) continue;
                    f.setAccessible(true);
                    if (f.get(activity) != null) continue;

                    if (type.isInterface()) {
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
                                filledAbstract++;
                                continue;
                            } catch (NoSuchMethodException | InstantiationException
                                     | IllegalAccessException
                                     | java.lang.reflect.InvocationTargetException ignored) {}
                            // Fall back to Unsafe.allocateInstance
                            Object stub = unsafeAllocateInstanceShared(type);
                            if (stub != null) {
                                f.set(activity, stub);
                                filledAbstract++;
                            }
                        } catch (Throwable ex) { /* skip */ }
                    }
                }
                cls = cls.getSuperclass();
            }
        } catch (Throwable t) { /* reflection failure */ }
        if (filled > 0 || filledAbstract > 0) {
            Log.d(TAG, "  fillNullFieldsWithProxies: filled " + filled + " interfaces, "
                    + filledAbstract + " abstract/concrete fields");
        }
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
