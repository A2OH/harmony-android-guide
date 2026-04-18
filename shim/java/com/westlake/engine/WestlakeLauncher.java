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
    private static boolean sLastApplicationCtorBypassed;
    private static final String TAG = "WestlakeLauncher";
    private static final int SURFACE_WIDTH = 480;
    private static final int SURFACE_HEIGHT = 800;
    private static final String FRAMEWORK_POLICY_PROP = "westlake.framework.policy";
    private static final String FRAMEWORK_POLICY_WESTLAKE_ONLY = "westlake_only";
    private static native void nativeLog(String message);
    private static native boolean nativeCanOpenFile(String path);
    private static native byte[] nativeReadFileBytes(String path);
    private static native String nativeVmProperty(String key);
    private static native int nativeVmArgCount();
    private static native String nativeVmArg(int index);
    private static native ClassLoader nativeSystemClassLoader();
    private static native Class<?> nativeFindClass(String className);
    private static native Object nativeAllocInstance(Class<?> target);
    private static native boolean nativePatchClassNoop(String className, ClassLoader loader);
    private static native void nativePrimeLaunchConfig();
    private static native void nativePrintException(Throwable t);
    private static String sBootApkPath;
    private static String sBootActivityName;
    private static String sBootPackageName;
    private static String sBootManifestPath;
    private static String sBootResDir;
    private static final java.util.HashMap<String, String> sLaunchFileProps = new java.util.HashMap<String, String>();
    private static final java.util.ArrayList<Activity> sInstalledDashboardFallbacks =
            new java.util.ArrayList<Activity>();
    private static boolean sLaunchFileLoaded;
    private static boolean sLoggedAppClassLoaders;
    private static boolean sLoggedDashboardOwnership;
    private static boolean sDirectDashboardFallbackActive;
    public static byte[] splashImageData; // Raw image bytes for OP_IMAGE rendering
    /** Real Android context when running on app_process64 */
    public static Object sRealContext;
    /** Pre-rendered icons bitmap (PNG bytes) from real framework */
    private static byte[] realIconsPng;

    public static boolean isRealFrameworkFallbackAllowed() {
        return !FRAMEWORK_POLICY_WESTLAKE_ONLY.equals(System.getProperty(FRAMEWORK_POLICY_PROP));
    }

    private static ClassLoader engineClassLoader() {
        ClassLoader cl = WestlakeLauncher.class.getClassLoader();
        return cl != null ? cl : Object.class.getClassLoader();
    }

    private static void stderrLog(String message) {
        // Avoid java.io writes during standalone ART bootstrap. On the current
        // Westlake path, FileOutputStream.write() re-enters BlockGuard and
        // ThreadLocal initialization, which is still unstable and can derail
        // activity startup before the real failure is reached.
    }

    private static void startupLog(String message) {
        // Keep startup logging side-effect free. PrintStream/charset setup is not
        // reliable yet on the pure Westlake path, and pulling it in here aborts boot.
        // Also avoid android.util.Log here: once OHBridge is live, Log.i() routes
        // through native bridge logging and that path is still crash-prone during
        // early activity bootstrap.
        if (message == null) return;
        stderrLog("[WestlakeLauncher] " + message);
        try {
            nativeLog("[WestlakeLauncher] " + message);
        } catch (Throwable ignored) {
        }
    }

    private static void startupLog(String message, Throwable t) {
        startupLog(message + ": " + throwableTag(t));
    }

    private static String throwableTag(Throwable t) {
        return t == null ? "null" : t.getClass().getName();
    }

    private static String safeThrowableMessage(Throwable t) {
        if (t == null) {
            return null;
        }
        try {
            return t.getMessage();
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static String throwableSummary(Throwable t) {
        if (t == null) {
            return "null";
        }
        String message = safeThrowableMessage(t);
        if (message == null || message.isEmpty()) {
            return throwableTag(t);
        }
        return throwableTag(t) + ": " + message;
    }

    public static void dumpThrowable(String prefix, Throwable t) {
        if (prefix != null) {
            startupLog(prefix + ": " + throwableSummary(t));
        } else if (t != null) {
            startupLog(throwableSummary(t));
        }
    }

    private static void logThrowableFrames(String prefix, Throwable t, int maxFrames) {
        if (t == null) {
            return;
        }
        try {
            StackTraceElement[] frames = t.getStackTrace();
            if (frames == null) {
                return;
            }
            int count = Math.min(maxFrames, frames.length);
            for (int i = 0; i < count; i++) {
                StackTraceElement frame = frames[i];
                if (frame == null) {
                    continue;
                }
                startupLog(prefix + " #" + i + " " + frame.getClassName()
                        + "." + frame.getMethodName() + ":" + frame.getLineNumber());
            }
        } catch (Throwable ignored) {
        }
    }

    public static void trace(String message) {
        if (message == null) {
            return;
        }
        // Keep fine-grained lifecycle tracing off the JNI log bridge. The launch
        // path still trips interpreter/JNI faults when every WAT step re-enters
        // nativeLog(), so route trace-only diagnostics to stderr.
        stderrLog("[WestlakeLauncher] " + message);
    }

    public static Object tryAllocInstance(Class<?> target) {
        if (target == null) {
            return null;
        }
        try {
            startupLog("[WestlakeLauncher] tryAllocInstance begin: " + target.getName());
            Object instance = nativeAllocInstance(target);
            startupLog("[WestlakeLauncher] tryAllocInstance result: "
                    + (instance != null ? instance.getClass().getName() : "null"));
            return instance;
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] nativeAllocInstance failed for "
                    + target.getName() + ": " + throwableTag(t));
            return null;
        }
    }

    private static Object tryUnsafeAllocInstance(Class<?> target) {
        if (target == null) {
            return null;
        }
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            java.lang.reflect.Field field = unsafeClass.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Object unsafe = field.get(null);
            return unsafeClass.getMethod("allocateInstance", Class.class).invoke(unsafe, target);
        } catch (Throwable ignored) {
            try {
                Class<?> unsafeClass = Class.forName("jdk.internal.misc.Unsafe");
                java.lang.reflect.Field field = unsafeClass.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                Object unsafe = field.get(null);
                return unsafeClass.getMethod("allocateInstance", Class.class).invoke(unsafe, target);
            } catch (Throwable ignoredToo) {
                return null;
            }
        }
    }

    private static void primeAllocatedApplication(android.app.Application app) {
        if (app == null) {
            return;
        }
        try {
            java.lang.reflect.Field callbacksField =
                    android.app.Application.class.getDeclaredField("mCallbacks");
            callbacksField.setAccessible(true);
            if (callbacksField.get(app) == null) {
                callbacksField.set(app, new java.util.ArrayList());
            }
        } catch (Throwable ignored) {
        }
    }

    private static android.app.Application instantiateApplicationInstance(
            Class<?> appCls, String appClassName, boolean preferAllocation) throws Throwable {
        sLastApplicationCtorBypassed = false;
        Throwable ctorError = null;
        if (preferAllocation) {
            Object allocated = tryAllocInstance(appCls);
            if (!(allocated instanceof android.app.Application)) {
                allocated = tryUnsafeAllocInstance(appCls);
            }
            if (allocated instanceof android.app.Application) {
                android.app.Application app = (android.app.Application) allocated;
                primeAllocatedApplication(app);
                sLastApplicationCtorBypassed = true;
                startupLog("[WestlakeLauncher] Application allocated without ctor: " + appClassName);
                return app;
            }
        }
        try {
            Object instance = appCls.getDeclaredConstructor().newInstance();
            if (instance instanceof android.app.Application) {
                return (android.app.Application) instance;
            }
        } catch (Throwable t) {
            ctorError = t;
            startupLog("[WestlakeLauncher] Application ctor failed for " + appClassName
                    + ": " + throwableTag(t));
        }
        Object allocated = tryAllocInstance(appCls);
        if (!(allocated instanceof android.app.Application)) {
            allocated = tryUnsafeAllocInstance(appCls);
        }
        if (allocated instanceof android.app.Application) {
            android.app.Application app = (android.app.Application) allocated;
            primeAllocatedApplication(app);
            sLastApplicationCtorBypassed = true;
            startupLog("[WestlakeLauncher] Application ctor bypassed after failure: " + appClassName);
            return app;
        }
        if (ctorError != null) {
            throw ctorError;
        }
        throw new InstantiationException("Failed to instantiate application " + appClassName);
    }

    public static void patchProblematicAppClasses(ClassLoader loader) {
        if (loader == null) {
            return;
        }
        String[] classes = {
            "com.newrelic.agent.android.tracing.TraceMachine",
            "com.newrelic.agent.android.tracing.Trace",
            "com.newrelic.agent.android.NewRelic",
        };
        for (int i = 0; i < classes.length; i++) {
            String className = classes[i];
            try {
                if (nativePatchClassNoop(className, loader)) {
                    startupLog("[WestlakeLauncher] Patched " + className
                            + " on " + loaderTag(loader));
                }
            } catch (Throwable t) {
                startupLog("[WestlakeLauncher] Patch failed for " + className
                        + ": " + throwableTag(t));
            }
        }
    }

    private static String propOrSnapshot(String key, String snapshot) {
        try {
            String value = System.getProperty(key);
            if (value != null && !value.isEmpty()) {
                return value;
            }
        } catch (Throwable ignored) {
        }
        try {
            String value = nativeVmProperty(key);
            if (value != null && !value.isEmpty()) {
                return value;
            }
        } catch (Throwable ignored) {
        }
        String argFlag = argFlagForProperty(key);
        if (argFlag != null) {
            String value = nativeArgValue(argFlag);
            if (value != null && !value.isEmpty()) {
                return value;
            }
        }
        String fileValue = launchFileProperty(key);
        if (fileValue != null && !fileValue.isEmpty()) {
            return fileValue;
        }
        return snapshot;
    }

    private static String copyString(String value) {
        return value == null ? null : new String(value);
    }

    private static String normalizePackageName(String packageName) {
        if (packageName == null) {
            return null;
        }
        return packageName.isEmpty() ? null : packageName;
    }

    private static boolean isPlaceholderPackage(String packageName) {
        packageName = normalizePackageName(packageName);
        return packageName == null
                || "app".equals(packageName)
                || "com.example.app".equals(packageName);
    }

    private static String packageFromClassName(String className) {
        if (className == null || className.isEmpty()) {
            return null;
        }
        int dot = className.lastIndexOf('.');
        if (dot <= 0) {
            return null;
        }
        return className.substring(0, dot);
    }

    private static String canonicalPackageName(
            String packageName,
            String activityName,
            android.content.pm.ManifestParser.ManifestInfo manifestInfo) {
        packageName = normalizePackageName(packageName);
        if (!isPlaceholderPackage(packageName)) {
            return packageName;
        }
        if (manifestInfo != null
                && manifestInfo.packageName != null
                && !manifestInfo.packageName.isEmpty()) {
            return manifestInfo.packageName;
        }
        String derived = packageFromClassName(activityName);
        if (derived != null && !derived.isEmpty()) {
            return derived;
        }
        return packageName;
    }

    private static String stableLaunchPackage(
            String fallbackPackage,
            String activityName,
            android.content.pm.ManifestParser.ManifestInfo manifestInfo) {
        String candidate = normalizePackageName(sBootPackageName);
        if (candidate == null) {
            candidate = normalizePackageName(launchFileProperty("westlake.apk.package"));
        }
        if (candidate == null) {
            candidate = normalizePackageName(nativeArgValue("--apk-package"));
        }
        if (candidate == null) {
            try {
                candidate = normalizePackageName(System.getProperty("westlake.apk.package"));
            } catch (Throwable ignored) {
            }
        }
        if (candidate == null) {
            candidate = normalizePackageName(fallbackPackage);
        }
        return canonicalPackageName(candidate, activityName, manifestInfo);
    }

    private static String packageFallbackForKnownApps(String packageName, String activityName) {
        if (activityName != null && activityName.startsWith("com.mcdonalds.")) {
            return "com.mcdonalds.app";
        }
        return packageName;
    }

    private static String preferredLaunchPackage(
            String primaryPackage,
            String secondaryPackage,
            String activityName,
            android.content.pm.ManifestParser.ManifestInfo manifestInfo) {
        String candidate = stableLaunchPackage(primaryPackage, activityName, manifestInfo);
        candidate = packageFallbackForKnownApps(candidate, activityName);
        if (!isPlaceholderPackage(candidate)) {
            return candidate;
        }
        candidate = stableLaunchPackage(secondaryPackage, activityName, manifestInfo);
        candidate = packageFallbackForKnownApps(candidate, activityName);
        if (!isPlaceholderPackage(candidate)) {
            return candidate;
        }
        candidate = canonicalPackageName(primaryPackage, activityName, manifestInfo);
        candidate = packageFallbackForKnownApps(candidate, activityName);
        if (!isPlaceholderPackage(candidate)) {
            return candidate;
        }
        candidate = canonicalPackageName(secondaryPackage, activityName, manifestInfo);
        candidate = packageFallbackForKnownApps(candidate, activityName);
        if (!isPlaceholderPackage(candidate)) {
            return candidate;
        }
        return candidate;
    }

    private static void persistLaunchPackage(String packageName) {
        packageName = normalizePackageName(packageName);
        if (isPlaceholderPackage(packageName)) {
            return;
        }
        sBootPackageName = copyString(packageName);
        try {
            System.setProperty("westlake.apk.package", packageName);
        } catch (Throwable ignored) {
        }
        try {
            MiniServer.currentSetPackageName(packageName);
        } catch (Throwable ignored) {
        }
    }

    private static String argFlagForProperty(String key) {
        if ("westlake.apk.path".equals(key)) return "--apk-path";
        if ("westlake.apk.activity".equals(key)) return "--apk-activity";
        if ("westlake.apk.package".equals(key)) return "--apk-package";
        if ("westlake.apk.resdir".equals(key)) return "--apk-resdir";
        if ("westlake.apk.manifest".equals(key)) return "--apk-manifest";
        return null;
    }

    private static String launchFileProperty(String key) {
        if (!sLaunchFileLoaded) {
            sLaunchFileLoaded = true;
            String[] candidates = {
                "/data/local/tmp/westlake/westlake-launch.properties",
                "/data/local/tmp/westlake/launch.properties"
            };
            for (String path : candidates) {
                byte[] data = tryReadFileBytes(path);
                if (data == null || data.length == 0) {
                    continue;
                }
                try {
                    String text = new String(data);
                    int start = 0;
                    while (start <= text.length()) {
                        int end = text.indexOf('\n', start);
                        if (end < 0) {
                            end = text.length();
                        }
                        String line = text.substring(start, end).trim();
                        if (!line.isEmpty()) {
                            int eq = line.indexOf('=');
                            if (eq > 0 && eq < line.length() - 1) {
                                sLaunchFileProps.put(line.substring(0, eq), line.substring(eq + 1));
                            }
                        }
                        if (end >= text.length()) {
                            break;
                        }
                        start = end + 1;
                    }
                    startupLog("[WestlakeLauncher] Launch file loaded: " + path);
                    break;
                } catch (Throwable ignored) {
                }
            }
        }
        return sLaunchFileProps.get(key);
    }

    private static String argValue(String[] args, String flag) {
        if (args == null || flag == null) {
            return nativeArgValue(flag);
        }
        for (int i = 0; i + 1 < args.length; i++) {
            if (flag.equals(args[i])) {
                String value = args[i + 1];
                if (value != null && !value.isEmpty()) {
                    return value;
                }
                return null;
            }
        }
        return nativeArgValue(flag);
    }

    private static String nativeArgValue(String flag) {
        try {
            int count = nativeVmArgCount();
            for (int i = 0; i + 1 < count; i++) {
                String current = nativeVmArg(i);
                if (flag.equals(current)) {
                    String value = nativeVmArg(i + 1);
                    if (value != null && !value.isEmpty()) {
                        return value;
                    }
                    return null;
                }
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private static boolean isBootClassLoader(ClassLoader cl) {
        return cl == null || "java.lang.BootClassLoader".equals(cl.getClass().getName());
    }

    private static String loaderTag(ClassLoader cl) {
        if (cl == null) return "null";
        try {
            return cl.getClass().getName();
        } catch (Throwable t) {
            return "<error:" + throwableTag(t) + ">";
        }
    }

    private static void logClassOwnership(String label, Class<?> cls) {
        if (cls == null) {
            startupLog("[WestlakeLauncher] Ownership " + label + "=null");
            return;
        }
        try {
            Class<?> superCls = cls.getSuperclass();
            startupLog("[WestlakeLauncher] Ownership " + label
                    + " class=" + cls.getName()
                    + " loader=" + loaderTag(cls.getClassLoader())
                    + " super=" + (superCls != null ? superCls.getName() : "null")
                    + " superLoader=" + (superCls != null ? loaderTag(superCls.getClassLoader()) : "null"));
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] Ownership " + label + " failed", t);
        }
    }

    private static void logDashboardOwnershipOnce(Activity activity, Object fragment) {
        if (sLoggedDashboardOwnership) {
            return;
        }
        sLoggedDashboardOwnership = true;
        try {
            logClassOwnership("engine.FragmentActivity.ref", androidx.fragment.app.FragmentActivity.class);
            logClassOwnership("engine.Fragment.ref", androidx.fragment.app.Fragment.class);
            logClassOwnership("engine.FragmentManager.ref", androidx.fragment.app.FragmentManager.class);
            if (activity != null) {
                logClassOwnership("dashboard.activity", activity.getClass());
                startupLog("[WestlakeLauncher] Ownership dashboard.activity instanceof shim.FragmentActivity="
                        + (activity instanceof androidx.fragment.app.FragmentActivity));
            }
            if (fragment != null) {
                logClassOwnership("dashboard.fragment", fragment.getClass());
                startupLog("[WestlakeLauncher] Ownership dashboard.fragment instanceof shim.Fragment="
                        + (fragment instanceof androidx.fragment.app.Fragment));
            }
            Class<?> resolved = resolveAppClassOrNull("com.mcdonalds.homedashboard.fragment.HomeDashboardFragment");
            if (resolved != null) {
                logClassOwnership("dashboard.fragment.resolved", resolved);
                startupLog("[WestlakeLauncher] Ownership resolved fragment assignableTo shim.Fragment="
                        + androidx.fragment.app.Fragment.class.isAssignableFrom(resolved));
            }
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] Ownership dashboard snapshot failed", t);
        }
    }

    private static Class<?> findNamedClassOnHierarchy(Class<?> start, String className) {
        if (start == null || className == null || className.isEmpty()) {
            return null;
        }
        for (Class<?> c = start; c != null && c != Object.class; c = c.getSuperclass()) {
            if (className.equals(c.getName())) {
                return c;
            }
        }
        return null;
    }

    private static void logAppClassLoadersOnce() {
        if (sLoggedAppClassLoaders) {
            return;
        }
        sLoggedAppClassLoaders = true;
        try {
            String contextTag = loaderTag(Thread.currentThread().getContextClassLoader());
            String systemTag;
            try {
                systemTag = loaderTag(nativeSystemClassLoader());
            } catch (Throwable t) {
                systemTag = "<throws:" + throwableTag(t) + ">";
            }
            String engineTag = loaderTag(WestlakeLauncher.class.getClassLoader());
            startupLog("[WestlakeLauncher] Loader snapshot:"
                    + " context=" + contextTag
                    + " system=" + systemTag
                    + " engine=" + engineTag
                    + " classPath=" + System.getProperty("java.class.path"));
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] Loader snapshot failed", t);
        }
    }

    private static void ensureAppContextClassLoader() {
        ClassLoader current = Thread.currentThread().getContextClassLoader();
        if (!isBootClassLoader(current)) {
            return;
        }
        try {
            ClassLoader nativeLoader = nativeSystemClassLoader();
            if (!isBootClassLoader(nativeLoader)) {
                Thread.currentThread().setContextClassLoader(nativeLoader);
            }
        } catch (Throwable ignored) {
        }
    }

    private static ClassLoader appClassLoader() {
        logAppClassLoadersOnce();
        ensureAppContextClassLoader();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (!isBootClassLoader(cl)) {
            return cl;
        }
        try {
            cl = nativeSystemClassLoader();
        } catch (Throwable ignored) {
            cl = null;
        }
        return isBootClassLoader(cl) ? null : cl;
    }

    private static Class<?> loadAppClass(String className) throws ClassNotFoundException {
        ClassNotFoundException last = null;
        ClassLoader cl = appClassLoader();
        if (!isBootClassLoader(cl)) {
            try {
                return Class.forName(className, false, cl);
            } catch (ClassNotFoundException e) {
                last = e;
            }
            try {
                return cl.loadClass(className);
            } catch (ClassNotFoundException e) {
                last = e;
            }
        }
        try {
            Class<?> nativeCls = nativeFindClass(className);
            if (nativeCls != null) {
                return nativeCls;
            }
        } catch (Throwable ignored) {
        }
        if (last != null) {
            throw last;
        }
        throw new ClassNotFoundException(className + " (no app class loader)");
    }

    private static java.lang.reflect.Method findLoaderMethod(Class<?> start,
                                                             String methodName,
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

    private static Class<?> loadAppClassChildFirst(String className) throws ClassNotFoundException {
        ClassNotFoundException last = null;
        ClassLoader cl = appClassLoader();
        if (!isBootClassLoader(cl)) {
            try {
                java.lang.reflect.Method findLoaded =
                        ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
                findLoaded.setAccessible(true);
                Object loaded = findLoaded.invoke(cl, className);
                if (loaded instanceof Class<?>) {
                    return (Class<?>) loaded;
                }
            } catch (Throwable ignored) {
            }
            try {
                java.lang.reflect.Method findClass = findLoaderMethod(cl.getClass(), "findClass",
                        String.class);
                if (findClass != null) {
                    findClass.setAccessible(true);
                    Object direct = findClass.invoke(cl, className);
                    if (direct instanceof Class<?>) {
                        return (Class<?>) direct;
                    }
                }
            } catch (java.lang.reflect.InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof ClassNotFoundException) {
                    last = (ClassNotFoundException) cause;
                }
            } catch (Throwable ignored) {
            }
        }
        if (last != null) {
            throw last;
        }
        return loadAppClass(className);
    }

    public static Class<?> resolveAppClassOrNull(String className) {
        if (className == null || className.isEmpty()) {
            return null;
        }
        try {
            return loadAppClass(className);
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] resolveAppClassOrNull failed: "
                    + className + " -> " + throwableTag(t));
            return null;
        }
    }

    public static Class<?> resolveAppClassChildFirstOrNull(String className) {
        if (className == null || className.isEmpty()) {
            return null;
        }
        try {
            return loadAppClassChildFirst(className);
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] resolveAppClassChildFirstOrNull failed: "
                    + className + " -> " + throwableTag(t));
            return null;
        }
    }

    private static Intent buildLaunchIntent(String packageName, String className) {
        String resolvedPackage = packageName;
        String resolvedClass = className;
        if (resolvedClass != null && resolvedClass.startsWith(".")
                && resolvedPackage != null && !resolvedPackage.isEmpty()) {
            resolvedClass = resolvedPackage + resolvedClass;
        }
        if ((resolvedPackage == null || resolvedPackage.isEmpty())
                && resolvedClass != null && !resolvedClass.isEmpty()) {
            int dot = resolvedClass.lastIndexOf('.');
            if (dot > 0) {
                resolvedPackage = resolvedClass.substring(0, dot);
            }
        }
        if (resolvedPackage == null || resolvedPackage.isEmpty()) {
            resolvedPackage = MiniServer.currentPackageName();
        }
        if (resolvedPackage == null || resolvedPackage.isEmpty()) {
            resolvedPackage = sBootPackageName;
        }
        if (resolvedPackage == null || resolvedPackage.isEmpty()) {
            resolvedPackage = launchFileProperty("westlake.apk.package");
        }
        if (resolvedPackage == null || resolvedPackage.isEmpty()) {
            try {
                resolvedPackage = System.getProperty("westlake.apk.package");
            } catch (Throwable ignored) {
            }
        }
        resolvedPackage = packageFallbackForKnownApps(resolvedPackage, resolvedClass);
        if (resolvedPackage == null || resolvedPackage.isEmpty()
                || resolvedClass == null || resolvedClass.isEmpty()) {
            throw new IllegalArgumentException("launch component unresolved: pkg="
                    + packageName + " cls=" + className);
        }
        ComponentName component = new ComponentName(resolvedPackage, resolvedClass);
        Intent intent = Intent.makeMainActivity(component);
        intent.setPackage(resolvedPackage);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    private static boolean canOpenFile(String path) {
        if (path == null || path.isEmpty()) return false;
        try {
            return nativeCanOpenFile(path);
        } catch (Throwable ignored) {
        }
        java.io.FileInputStream fis = null;
        try {
            fis = new java.io.FileInputStream(path);
            return true;
        } catch (Throwable t) {
            return false;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Throwable ignored) {
                }
            }
        }
    }

    private static byte[] readFileBytes(String path) throws java.io.IOException {
        if (path == null || path.isEmpty()) {
            throw new java.io.IOException("Empty path");
        }
        try {
            byte[] data = nativeReadFileBytes(path);
            if (data != null) {
                return data;
            }
        } catch (Throwable ignored) {
        }
        java.io.FileInputStream fis = new java.io.FileInputStream(path);
        try {
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream(8192);
            byte[] buf = new byte[8192];
            while (true) {
                int n = fis.read(buf);
                if (n < 0) break;
                if (n == 0) continue;
                out.write(buf, 0, n);
            }
            return out.toByteArray();
        } finally {
            fis.close();
        }
    }

    private static byte[] tryReadFileBytes(String path) {
        try {
            return readFileBytes(path);
        } catch (Throwable t) {
            return null;
        }
    }

    private static String joinPath(String base, String relative) {
        if (base == null || relative == null || relative.isEmpty()) {
            return null;
        }
        return base.endsWith("/") ? (base + relative) : (base + "/" + relative);
    }

    private static String leafName(String path) {
        if (path == null || path.isEmpty()) {
            return path;
        }
        int slash = path.lastIndexOf('/');
        return slash >= 0 ? path.substring(slash + 1) : path;
    }

    private static String parentPath(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        int slash = path.lastIndexOf('/');
        if (slash <= 0) {
            return null;
        }
        return path.substring(0, slash);
    }

    private static String resolveReadableResDir(String preferredPath) {
        String[] candidates = {
            preferredPath,
            "/data/local/tmp/westlake/mcd_res",
            "/data/local/tmp/westlake/apk_res",
            System.getProperty("user.dir", ".") + "/apk_res"
        };
        for (String candidate : candidates) {
            if (candidate == null || candidate.isEmpty()) {
                continue;
            }
            if (canOpenFile(candidate + "/resources.arsc")) {
                return candidate;
            }
        }
        return null;
    }

    private static android.app.ApkInfo buildDexOnlyInfo(
            String packageName, String activityName, String resDir) {
        android.app.ApkInfo info = new android.app.ApkInfo();
        info.packageName = packageFallbackForKnownApps(
                canonicalPackageName(packageName, activityName, null), activityName);
        info.launcherActivity = activityName;
        info.assetDir = resDir;
        info.resDir = resDir;
        if (activityName != null && !activityName.isEmpty()) {
            info.activities.add(activityName);
        }
        return info;
    }

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
            startupLog("Hidden API exemptions set (all classes)");
        } catch (Throwable t) {
            startupLog("Hidden API bypass unavailable");
        }

        // Load framework native stubs — but SKIP if running under app_process64
        // (it already has libandroid_runtime.so with all real framework natives)
        boolean hasRealRuntime = false;
        boolean strictWestlake = FRAMEWORK_POLICY_WESTLAKE_ONLY.equals(
            System.getProperty(FRAMEWORK_POLICY_PROP));
        try {
            // Some strict-Westlake runs expose enough framework stubs for
            // Bitmap.createBitmap() to succeed. Treat the runtime as "real"
            // only when the real ActivityThread app_process entrypoints are
            // also present; our shim ActivityThread does not define them.
            android.graphics.Bitmap.createBitmap(1, 1, android.graphics.Bitmap.Config.ARGB_8888);
            Class<?> atProbe = Class.forName("android.app.ActivityThread");
            atProbe.getDeclaredMethod("systemMain");
            atProbe.getDeclaredMethod("getSystemContext");
            hasRealRuntime = true;
            startupLog("Real framework natives detected (app_process64)");
        } catch (Throwable t) { /* no real runtime — need stubs */ }

        // In strict Westlake mode the runtime already called JNI_OnLoad_framework,
        // so avoid System.load/System.loadLibrary here; those paths pull in
        // java.nio.file and currently crash during file-system bootstrap.
        if (strictWestlake) {
            startupLog("Framework stubs pre-registered by runtime");
        } else try {
            System.loadLibrary("framework_stubs");
            startupLog("Framework stubs loaded");
        } catch (Throwable t) {
            try {
                System.load("/data/local/tmp/westlake/libframework_stubs.so");
                startupLog("Framework stubs loaded (absolute)");
            } catch (Throwable t2) {
                startupLog("Framework stubs unavailable");
            }
        }

        try {
            nativePrimeLaunchConfig();
        } catch (Throwable ignored) {
        }
        String apkPath = argValue(args, "--apk-path");
        if (apkPath == null) apkPath = propOrSnapshot("westlake.apk.path", sBootApkPath);
        String activityName = argValue(args, "--apk-activity");
        if (activityName == null) activityName = propOrSnapshot("westlake.apk.activity", sBootActivityName);
        String packageName = argValue(args, "--apk-package");
        if (packageName == null) packageName = propOrSnapshot("westlake.apk.package", sBootPackageName);
        packageName = normalizePackageName(packageName);
        if (packageName == null || packageName.isEmpty()) packageName = "com.example.app";
        String manifestPath = argValue(args, "--apk-manifest");
        if (manifestPath == null) manifestPath = propOrSnapshot("westlake.apk.manifest", sBootManifestPath);
        String bootResDir = argValue(args, "--apk-resdir");
        if (bootResDir == null) bootResDir = propOrSnapshot("westlake.apk.resdir", sBootResDir);
        if (apkPath != null && !apkPath.isEmpty()) sBootApkPath = copyString(apkPath);
        if (activityName != null && !activityName.isEmpty()) sBootActivityName = copyString(activityName);
        if (packageName != null && !packageName.isEmpty()) sBootPackageName = copyString(packageName);
        if (manifestPath != null && !manifestPath.isEmpty()) sBootManifestPath = copyString(manifestPath);
        if (bootResDir != null && !bootResDir.isEmpty()) sBootResDir = copyString(bootResDir);
        boolean allowRealFrameworkFallback = isRealFrameworkFallbackAllowed();
        ensureAppContextClassLoader();

        // Initialize main thread Looper FIRST — before any class that checks isMainThread
        android.os.Looper.prepareMainLooper();

        startupLog("Starting on OHOS + ART ...");
        startupLog("APK: " + apkPath);
        startupLog("Activity: " + activityName);
        startupLog("Package: " + packageName);
        startupLog("Framework policy: "
            + (allowRealFrameworkFallback ? "allow_real" : "westlake_only"));

        if (hasRealRuntime && !allowRealFrameworkFallback) {
            startupLog("FATAL: real framework runtime detected in strict Westlake mode");
            System.exit(86);
            return;
        }

        // Create a real Android context using the full framework
        try {
            Class<?> atClass = Class.forName("android.app.ActivityThread");
            Object at;
            Object sysCtx;

            if (hasRealRuntime) {
                // app_process64: use systemMain() directly — real natives handle everything
                startupLog("Using ActivityThread.systemMain() (real runtime)");
                at = atClass.getDeclaredMethod("systemMain").invoke(null);
                sysCtx = atClass.getDeclaredMethod("getSystemContext").invoke(at);
                startupLog("SystemContext acquired from ActivityThread.systemMain()");
                // Try to create MCD package context for its resources
                if (allowRealFrameworkFallback && sysCtx instanceof android.content.Context) {
                    try {
                        // Use the installed MCD app's resources
                        android.content.Context mcdCtx = ((android.content.Context) sysCtx)
                            .createPackageContext("com.mcdonalds.app",
                                android.content.Context.CONTEXT_INCLUDE_CODE |
                                android.content.Context.CONTEXT_IGNORE_SECURITY);
                        startupLog("MCD context acquired");
                        startupLog("MCD resources available");
                        sRealContext = mcdCtx;
                    } catch (Throwable pe) {
                        startupLog("MCD context failed", pe);
                        sRealContext = (android.content.Context) sysCtx;
                    }
                }
            } else {
            // dalvikvm64: need stub ServiceManager + manual ActivityThread
            startupLog("Using manual ActivityThread (stub runtime)");

            // Inject stub ServiceManager
            Class<?> smClass = Class.forName("android.os.ServiceManager");
            java.lang.reflect.Field cacheField = smClass.getDeclaredField("sCache");
            cacheField.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.Map<String, android.os.IBinder> cache =
                (java.util.Map<String, android.os.IBinder>) cacheField.get(null);
            String[] services = {"activity","package","window","display","alarm","power",
                "connectivity","wifi","audio","vibrator","notification","accessibility",
                "input_method","input","clipboard","statusbar","deviceidle","device_policy",
                "content","account","user","sensor_privacy","job_scheduler","device_config",
                "color_display","uimode","overlay","autofill","batterystats","media_session",
                "textclassification","SurfaceFlinger","permission","appops","rollback",
                "usagestats","dropbox","companiondevice","trust","appwidget","wallpaper",
                "dreams","people","locale","telephony.registry"};
            for (String s : services) cache.put(s, new android.os.Binder());
            // Also set sServiceManager proxy
            java.lang.reflect.Field smField = smClass.getDeclaredField("sServiceManager");
            smField.setAccessible(true);
            java.lang.reflect.Method asInterface = Class.forName("android.os.ServiceManagerNative")
                .getDeclaredMethod("asInterface", android.os.IBinder.class);
            asInterface.setAccessible(true);
            smField.set(null, asInterface.invoke(null, new android.os.Binder()));
            startupLog("ServiceManager injected (" + services.length + " services)");

            // Create ActivityThread (dalvikvm64 path)
            java.lang.reflect.Constructor<?> atCtor = atClass.getDeclaredConstructor();
            atCtor.setAccessible(true);
            at = atCtor.newInstance();
            java.lang.reflect.Field sCurrentAT = atClass.getDeclaredField("sCurrentActivityThread");
            sCurrentAT.setAccessible(true);
            sCurrentAT.set(null, at);

            sysCtx = atClass.getDeclaredMethod("getSystemContext").invoke(at);
            startupLog("SystemContext acquired from stub ActivityThread");
            } // end of !hasRealRuntime else block
            if (allowRealFrameworkFallback && sysCtx instanceof android.content.Context && packageName != null) {
                android.content.Context realCtx = ((android.content.Context) sysCtx)
                    .createPackageContext(packageName, 3); // INCLUDE_CODE | IGNORE_SECURITY
                startupLog("Real Android context acquired");
                android.content.res.Resources realRes = realCtx.getResources();
                startupLog("Real resources acquired");
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
                    startupLog("Drew " + found + " real drawables");

                    // Compress to PNG and send via pipe
                    java.io.ByteArrayOutputStream pngOut = new java.io.ByteArrayOutputStream();
                    bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 90, pngOut);
                    byte[] png = pngOut.toByteArray();
                    startupLog("Real icons PNG prepared (" + png.length + " bytes)");

                    // Store for rendering after OHBridge is initialized
                    realIconsPng = png;
                    bmp.recycle();
                } catch (Throwable t) {
                    startupLog("Real drawable capture failed");
                }
            }
        } catch (Throwable t) {
            startupLog("Real context not available (custom ART)");
        }

        // Parse AndroidManifest.xml for Application class and component list
        android.content.pm.ManifestParser.ManifestInfo manifestInfo = null;
        manifestPath = propOrSnapshot("westlake.apk.manifest", sBootManifestPath);
        if (manifestPath != null && !manifestPath.isEmpty()) {
            try {
                byte[] data = readFileBytes(manifestPath);
                if (data != null && data.length > 0) {
                    manifestInfo = android.content.pm.ManifestParser.parse(data);
                    startupLog("Manifest: " + manifestInfo.applicationClass
                        + " (" + manifestInfo.activities.size() + " activities, "
                        + manifestInfo.providers.size() + " providers)");
                }
            } catch (Exception e) {
                startupLog("Manifest parse error", e);
            }
        }

        // Check native bridge
        boolean nativeOk = OHBridge.isNativeAvailable();
        startupLog("OHBridge native: " + (nativeOk ? "LOADED" : "UNAVAILABLE"));

        if (nativeOk) {
            int rc = 0;
            try { rc = OHBridge.arkuiInit(); } catch (UnsatisfiedLinkError e) { /* subprocess — no arkui */ }
            startupLog("arkuiInit() = " + rc);

            // If real icons were pre-rendered (app_process64), send immediately
            if (realIconsPng != null) {
                startupLog("Sending real icons frame...");
                try {
                    long surf = OHBridge.surfaceCreate(0, SURFACE_WIDTH, SURFACE_HEIGHT);
                    long canv = OHBridge.surfaceGetCanvas(surf);
                    OHBridge.canvasDrawImage(canv, realIconsPng, 0, 0, SURFACE_WIDTH, SURFACE_HEIGHT);
                    int flushResult = OHBridge.surfaceFlush(surf);
                    startupLog("Real icons frame sent! flush=" + flushResult + " (" + realIconsPng.length + " bytes)");
                } catch (Throwable t) {
                    startupLog("Real icons send error");
                }
                // Continue to Activity launch (don't block here — pipe stays open via render loop)
            }
        }

        // Load native Android framework resource engine
        try {
            System.loadLibrary("test_jni");
            startupLog("test_jni loaded OK!");
        } catch (Throwable t) {
            startupLog("test_jni failed");
        }
        try {
            System.loadLibrary("androidfw_jni");
            startupLog("libandroidfw_jni loaded");
        } catch (Throwable t) {
            startupLog("libandroidfw_jni failed");
        }

        String bootstrapApkPath = propOrSnapshot("westlake.apk.path", sBootApkPath);
        if (bootstrapApkPath != null && !bootstrapApkPath.isEmpty()) apkPath = bootstrapApkPath;
        String bootstrapActivity = propOrSnapshot("westlake.apk.activity", sBootActivityName);
        if (bootstrapActivity != null && !bootstrapActivity.isEmpty()) activityName = bootstrapActivity;
        String bootstrapPackage = stableLaunchPackage(packageName, activityName, manifestInfo);
        if (bootstrapPackage != null && !bootstrapPackage.isEmpty()) packageName = bootstrapPackage;
        String bootstrapManifest = propOrSnapshot("westlake.apk.manifest", sBootManifestPath);
        if (bootstrapManifest != null && !bootstrapManifest.isEmpty()) manifestPath = bootstrapManifest;
        String bootstrapResDir = propOrSnapshot("westlake.apk.resdir", sBootResDir);
        if (bootstrapResDir != null && !bootstrapResDir.isEmpty()) bootResDir = bootstrapResDir;
        packageName = canonicalPackageName(packageName, activityName, manifestInfo);
        packageName = packageFallbackForKnownApps(packageName, activityName);
        if (apkPath != null && !apkPath.isEmpty()) sBootApkPath = copyString(apkPath);
        if (activityName != null && !activityName.isEmpty()) sBootActivityName = copyString(activityName);
        if (packageName != null && !packageName.isEmpty()) sBootPackageName = copyString(packageName);
        if (manifestPath != null && !manifestPath.isEmpty()) sBootManifestPath = copyString(manifestPath);
        if (bootResDir != null && !bootResDir.isEmpty()) sBootResDir = copyString(bootResDir);
        startupLog("[WestlakeLauncher] Bootstrap props refreshed: apk=" + apkPath
            + " activity=" + activityName + " package=" + packageName + " resDir=" + bootResDir);

        // Initialize MiniServer
        startupLog("MiniServer init begin pkg=" + packageName);
        MiniServer server = MiniServer.init(packageName);
        startupLog("MiniServer init returned server=" + server);
        MiniActivityManager am = MiniServer.currentActivityManager();
        startupLog("MiniServer activityManager=" + am);
        if (server == null) {
            throw new IllegalStateException("MiniServer.init returned null");
        }
        if (am == null) {
            throw new IllegalStateException("MiniServer activity manager missing");
        }
        startupLog("MiniServer initialized");

        // Pre-seed SharedPreferences BEFORE any app code runs
        if ("me.tsukanov.counter".equals(packageName)) {
            android.content.SharedPreferences sp =
                android.content.SharedPreferencesImpl.getInstance("counters");
            if (sp.getAll().isEmpty()) {
                sp.edit().putInt("My Counter", 0)
                         .putInt("Steps", 42)
                         .putInt("Coffee", 3)
                         .apply();
                startupLog("Pre-seeded 3 counters");
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
            startupLog("[WestlakeLauncher] Application from manifest: " + appClassName);
        }
        if ("com.mcdonalds.app.application.McDMarketApplication".equals(appClassName)) {
            startupLog("[WestlakeLauncher] Skipping custom McDMarketApplication bootstrap");
            appClassName = null;
        }
        // Detect only explicit Hilt-generated Application classes here. The
        // broader McDonald's package/application heuristic regressed launch by
        // forcing an early ctor-bypassed Application path that was absent in
        // the last accepted baseline.
        boolean isHiltApp = false;
        if (appClassName != null) {
            if (appClassName.contains("Hilt_")) {
                isHiltApp = true;
                startupLog("[WestlakeLauncher] Hilt/Dagger app detected");
            }
        }

        if (appClassName != null) {
            try {
                Class<?> appCls = loadAppClass(appClassName);
                android.app.Application customApp = instantiateApplicationInstance(
                        appCls, appClassName, isHiltApp);
                // Attach real Android context as base (critical for app_process64 mode)
                if (sRealContext instanceof android.content.Context) {
                    try {
                        java.lang.reflect.Method attach = android.content.ContextWrapper.class
                            .getDeclaredMethod("attachBaseContext", android.content.Context.class);
                        attach.setAccessible(true);
                        attach.invoke(customApp, (android.content.Context) sRealContext);
                        startupLog("[WestlakeLauncher] Attached real context to Application");
                    } catch (Throwable t) {
                        startupLog("[WestlakeLauncher] attachBaseContext failed: " + throwableTag(t));
                    }
                }
                MiniServer.currentSetApplication(customApp);

                // Wire up resources + AssetManager BEFORE Application.onCreate()
                // so config files (gma_api_config.json, etc.) are accessible
                {
                    String earlyResDir = resolveReadableResDir(
                        propOrSnapshot("westlake.apk.resdir", sBootResDir));
                    if (earlyResDir != null) {
                        try {
                            android.app.ApkInfo earlyInfo = android.app.ApkLoader.loadFromExtracted(
                                earlyResDir, packageName, activityName);
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
                            startupLog("[WestlakeLauncher] Early resource/asset setup done (resDir=" + earlyResDir + ")");
                        } catch (Exception ex) {
                            startupLog("[WestlakeLauncher] Early resource setup failed: " + ex.getClass().getName());
                        }
                    }
                }

                // Run Application.onCreate with timeout for all apps. Skipping it
                // for ctor-bypassed McD/Hilt experiments regressed launch before
                // the last accepted baseline, so keep the normal threaded path.
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
                                startupLog("[WestlakeLauncher] Application.onCreate error: " + throwableTag(e));
                            }
                        }
                    }, "AppOnCreate");
                    appThread.setDaemon(true);
                    appThread.start();
                    int timeoutMs = isHiltApp ? 3000 : 5000; // Hilt DI should settle quickly
                    long startTime = System.currentTimeMillis();
                    int reportInterval = 10000; // 10s
                    while (!onCreateDone[0] && (System.currentTimeMillis() - startTime) < timeoutMs) {
                        try { appThread.join(reportInterval); } catch (InterruptedException ie) {}
                        if (!onCreateDone[0]) {
                            long elapsed = (System.currentTimeMillis() - startTime) / 1000;
                            startupLog("[WestlakeLauncher] Application.onCreate still running (" + elapsed + "s)...");
                        }
                    }
                    if (onCreateDone[0]) {
                        startupLog("[WestlakeLauncher] Application.onCreate done: " + appCls.getSimpleName()
                            + (onCreateError[0] != null
                            ? " (with error: " + throwableTag(onCreateError[0]) + ")"
                            : ""));
                    } else {
                        startupLog("[WestlakeLauncher] Application.onCreate TIMEOUT (" + timeoutMs + "ms)"
                            + " — continuing anyway (DI may be partial)");
                    }
                    // Force-kill the background thread to prevent CPU starvation and memory growth
                    if (!onCreateDone[0]) {
                        try { appThread.interrupt(); } catch (Throwable t) {}
                        try { appThread.stop(); } catch (Throwable t) {}  // deprecated but necessary
                        startupLog("[WestlakeLauncher] Killed Application.onCreate() thread");
                    }
                }
                // Force-set 'counters' field on CounterApplication (Counter app specific)
                try {
                    java.lang.reflect.Field cf = customApp.getClass().getDeclaredField("counters");
                    cf.setAccessible(true);
                    Object existing = cf.get(customApp);
                    if (existing == null && !counterData.isEmpty()) {
                        cf.set(customApp, counterData);
                        startupLog("[WestlakeLauncher] Force-set counters: " + counterData.keySet());
                    }
                } catch (Exception e) { /* not a counter app */ }
            } catch (ClassNotFoundException e) {
                startupLog("[WestlakeLauncher] Application class not found: " + appClassName);
            } catch (Throwable e) {
                startupLog("[WestlakeLauncher] Application error (caught)", e);
                // Continue without the custom Application
            }
        }

        // Load APK resources — use pre-extracted dir if available (dalvikvm has no ZipFile JNI)
        Activity launchedActivity = null;
        Class<?> resolvedActivityClass = null;
        String resolvedApkPath = propOrSnapshot("westlake.apk.path", sBootApkPath);
        if (resolvedApkPath != null && !resolvedApkPath.isEmpty()) {
            apkPath = resolvedApkPath;
        }
        String resolvedActivityName = propOrSnapshot("westlake.apk.activity", sBootActivityName);
        if (resolvedActivityName != null && !resolvedActivityName.isEmpty()) {
            activityName = resolvedActivityName;
        }
        String resolvedPackageName = stableLaunchPackage(packageName, activityName, manifestInfo);
        if (resolvedPackageName != null && !resolvedPackageName.isEmpty()) {
            packageName = resolvedPackageName;
        }
        packageName = canonicalPackageName(packageName, activityName, manifestInfo);
        packageName = packageFallbackForKnownApps(packageName, activityName);
        String resDir = propOrSnapshot("westlake.apk.resdir", sBootResDir);
        if (apkPath != null && !apkPath.isEmpty()) sBootApkPath = copyString(apkPath);
        if (activityName != null && !activityName.isEmpty()) sBootActivityName = copyString(activityName);
        if (packageName != null && !packageName.isEmpty()) sBootPackageName = copyString(packageName);
        if (resDir != null && !resDir.isEmpty()) sBootResDir = copyString(resDir);
        String launchApkPath = sBootApkPath != null ? copyString(sBootApkPath) : copyString(apkPath);
        String launchActivity = sBootActivityName != null ? copyString(sBootActivityName) : copyString(activityName);
        String launchPackage = stableLaunchPackage(packageName, launchActivity, manifestInfo);
        launchPackage = packageFallbackForKnownApps(launchPackage, launchActivity);
        String launchResDir = sBootResDir != null ? copyString(sBootResDir) : copyString(resDir);
        String targetPackageName = stableLaunchPackage(launchPackage, launchActivity, manifestInfo);
        targetPackageName = packageFallbackForKnownApps(targetPackageName, launchActivity);
        if (targetPackageName == null || targetPackageName.isEmpty()) {
            targetPackageName = "app";
        }
        String targetActivity = launchActivity;
        startupLog("[WestlakeLauncher] Resolved launch props: apk=" + launchApkPath
            + " activity=" + launchActivity + " package=" + launchPackage + " resDir=" + launchResDir);
        startupLog("[WestlakeLauncher] Launch snapshot: apk=" + sBootApkPath
            + " activity=" + sBootActivityName + " package=" + sBootPackageName + " resDir=" + sBootResDir);
        try {
            apkPath = launchApkPath;
            activityName = launchActivity;
            packageName = launchPackage;
            resDir = launchResDir;
            targetActivity = launchActivity;
            packageName = preferredLaunchPackage(
                    packageName, targetPackageName, targetActivity, manifestInfo);
            targetPackageName = preferredLaunchPackage(
                    targetPackageName, packageName, targetActivity, manifestInfo);
            if (apkPath == null) apkPath = "";
            if (isPlaceholderPackage(packageName)) {
                packageName = targetPackageName;
            }
            if (isPlaceholderPackage(targetPackageName)) {
                targetPackageName = packageName;
            }
            packageName = packageFallbackForKnownApps(packageName, targetActivity);
            targetPackageName = packageFallbackForKnownApps(targetPackageName, targetActivity);
            if (packageName == null || packageName.isEmpty()) {
                packageName = packageFallbackForKnownApps("app", targetActivity);
            }
            if (targetPackageName == null || targetPackageName.isEmpty()
                    || isPlaceholderPackage(targetPackageName)) {
                targetPackageName = packageName;
            }
            persistLaunchPackage(packageName);
            persistLaunchPackage(targetPackageName);
            startupLog("[WestlakeLauncher] Package handoff: launch=" + launchPackage
                    + " resolved=" + packageName + " target=" + targetPackageName);
            startupLog("[WestlakeLauncher] Loading APK: " + apkPath);
            startupLog("[WestlakeLauncher] ResDir: " + resDir);

            android.app.ApkInfo info;
            // Check resDir — also try fallback paths if the primary path isn't accessible
            boolean preferredResReadable = resDir != null && canOpenFile(resDir + "/resources.arsc");
            String effectiveResDir = resolveReadableResDir(resDir);
            if (resDir != null && !preferredResReadable) {
                startupLog("[WestlakeLauncher] ResDir not accessible: " + resDir);
            }
            if (effectiveResDir != null && resDir != null && !effectiveResDir.equals(resDir)) {
                startupLog("[WestlakeLauncher] Using fallback resDir: " + effectiveResDir);
            }
            if (effectiveResDir != null) {
                startupLog("[WestlakeLauncher] Using pre-extracted loader for " + effectiveResDir);
                // Use pre-extracted resources (host extracted them before spawning dalvikvm)
                info = android.app.ApkLoader.loadFromExtracted(
                    effectiveResDir, packageName, activityName);
                info.packageName = packageName;
                info.launcherActivity = activityName;
                if ((info.activities == null || info.activities.isEmpty())
                        && activityName != null && !activityName.isEmpty()) {
                    try {
                        info.activities.add(activityName);
                    } catch (Throwable ignored) {
                    }
                }
                // Also load split APK resources (xxxhdpi, en, etc.)
                android.content.res.Resources appRes2 = null;
                try {
                    android.app.Application currentApp = MiniServer.currentApplication();
                    if (currentApp != null) {
                        appRes2 = currentApp.getResources();
                    }
                } catch (Throwable t) {}
                for (String splitName : new String[]{"resources_xxxhdpi.arsc", "resources_en.arsc"}) {
                    String splitPath = effectiveResDir + "/" + splitName;
                    byte[] data = tryReadFileBytes(splitPath);
                    if (data != null && appRes2 != null) {
                        try {
                            android.content.res.ResourceTableParser.parse(data, appRes2);
                            startupLog("[WestlakeLauncher] Loaded split: " + splitName
                                + " (" + data.length + " bytes, entries=" + appRes2.getResourceTable().getStringCount() + ")");
                        } catch (Throwable t) {
                            startupLog("[WestlakeLauncher] Split error (" + splitName + ")", t);
                        }
                    } else {
                        startupLog("[WestlakeLauncher] Split " + splitName + " exists=" + (data != null) + " appRes=" + (appRes2 != null));
                    }
                }
                // Store ApkInfo on MiniServer so LayoutInflater can find resDir
                try {
                    java.lang.reflect.Field f = MiniServer.class.getDeclaredField("mApkInfo");
                    f.setAccessible(true);
                    f.set(server, info);
                } catch (Exception ex) { startupLog("[WestlakeLauncher] setApkInfo", ex); }
                startupLog("[WestlakeLauncher] Loaded from pre-extracted resources (resDir=" + info.resDir + ")");

                // Wire resources to Application (same as MiniServer.loadApk does)
                android.app.Application currentApp = MiniServer.currentApplication();
                android.content.res.Resources res = currentApp != null ? currentApp.getResources() : null;
                if (info.resourceTable != null) {
                    ShimCompat.loadResourceTable(res, (android.content.res.ResourceTable) info.resourceTable);
                    startupLog("[WestlakeLauncher] ResourceTable wired to Application");
                }
                // Set APK path for layout inflation (LayoutInflater reads AXML from here)
                if (res != null) {
                    ShimCompat.setApkPath(res, apkPath);
                }
                // Set asset dir for extracted res/ layouts
                if (info.assetDir != null && currentApp != null) {
                    ShimCompat.setAssetDir(currentApp.getAssets(), info.assetDir);
                }
            } else if (apkPath != null && apkPath.endsWith(".apk")) {
                startupLog("[WestlakeLauncher] Falling back to APK Zip loader");
                info = MiniServer.currentLoadApk(apkPath);
            } else {
                startupLog("[WestlakeLauncher] No readable extracted resources; continuing with dex-only metadata");
                info = buildDexOnlyInfo(packageName, activityName, resDir);
            }
            info.packageName = preferredLaunchPackage(
                    info.packageName, targetPackageName, targetActivity, manifestInfo);
            if (isPlaceholderPackage(packageName)) {
                packageName = info.packageName;
            }
            if (!isPlaceholderPackage(info.packageName)) {
                targetPackageName = info.packageName;
                packageName = info.packageName;
                System.setProperty("westlake.apk.package", info.packageName);
                MiniServer.currentSetPackageName(info.packageName);
            }
            startupLog("[WestlakeLauncher] APK loaded: " + info);
            startupLog("[WestlakeLauncher]   package: " + info.packageName);
            startupLog("[WestlakeLauncher]   activity count: " + info.activities.size());
            startupLog("[WestlakeLauncher]   launcher: " + info.launcherActivity);
            startupLog("[WestlakeLauncher]   dex count: " + info.dexPaths.size());

                // Determine which activity to launch (declared before try for catch visibility)
                if (targetActivity == null || targetActivity.isEmpty()) {
                    targetActivity = info.launcherActivity;
                }
	                if (targetActivity == null) {
	                    startupLog("[WestlakeLauncher] ERROR: No activity to launch");
	                    return;
	                }
                targetPackageName = preferredLaunchPackage(
                        info.packageName, targetPackageName, targetActivity, manifestInfo);
                targetPackageName = preferredLaunchPackage(
                        targetPackageName, packageName, targetActivity, manifestInfo);
                if (!isPlaceholderPackage(targetPackageName)) {
                    packageName = targetPackageName;
                    persistLaunchPackage(targetPackageName);
                }

		                startupLog("[WestlakeLauncher] Launching: " + targetActivity);

		                // For Hilt apps: skip real activity (constructor hangs in DI)
		                // Create a plain Activity with the app's splash content instead
		                boolean isHiltActivity = false;
			                try {
		                    Class<?> actCls = loadAppClass(targetActivity);
                        resolvedActivityClass = actCls;
                        am.registerActivityClass(targetActivity, actCls);
                        startupLog("[WestlakeLauncher] Resolved activity class via "
                            + actCls.getClassLoader());
		                    // Check if superclass chain contains "Hilt_"
		                    Class<?> sc = actCls.getSuperclass();
		                    while (sc != null) {
		                        if (sc.getName().contains("Hilt_")) { isHiltActivity = true; break; }
		                        sc = sc.getSuperclass();
		                    }
		                } catch (Exception e) {
                        startupLog("[WestlakeLauncher] Activity class resolve failed", e);
                    }

                    String appClass = null;
                    if (manifestInfo != null && manifestInfo.applicationClass != null) {
                        appClass = manifestInfo.applicationClass;
                    }
                    ClassLoader activityClassLoader = resolvedActivityClass != null
                        ? resolvedActivityClass.getClassLoader()
                        : Thread.currentThread().getContextClassLoader();
                    if (activityClassLoader == null) {
                        activityClassLoader = Thread.currentThread().getContextClassLoader();
                    }

                        boolean preferWat = isHiltActivity
                            || "com.mcdonalds.app".equals(targetPackageName);
		                if (preferWat) {
		                    // Use WestlakeActivityThread for Hilt apps — proper AOSP lifecycle with DI injection
		                    startupLog("[WestlakeLauncher] Using WestlakeActivityThread");
	                    final String fTarget2 = targetActivity;
	                    final String launchPkg2 = preferredLaunchPackage(
                                info.packageName, targetPackageName, fTarget2, manifestInfo);
                    final Activity[] result2 = { null };

	                    // Initialize WestlakeActivityThread (AOSP-style lifecycle)
	                    final android.app.WestlakeActivityThread wat = android.app.WestlakeActivityThread.currentActivityThread();
	                    if (wat.getInstrumentation() == null) {
                            startupLog("[WestlakeLauncher] WestlakeActivityThread attach begin");
                            try {
                                persistLaunchPackage(launchPkg2);
	                            wat.attach(launchPkg2, appClass, activityClassLoader);
	                            startupLog("[WestlakeLauncher] WestlakeActivityThread attached");
                            } catch (Throwable attachError) {
                                startupLog("[WestlakeLauncher] WestlakeActivityThread attach failed", attachError);
                                throw attachError;
                            }
	                    }

                    // Launch synchronously on main thread (no timeout needed)
                    try {
                        startupLog("[WestlakeLauncher] WAT launch args: pkg="
                                + launchPkg2 + " cls=" + fTarget2);
                        startupLog("[WestlakeLauncher] WAT launch via direct ActivityThread path");
                        Intent watIntent = new Intent(Intent.ACTION_MAIN);
                        watIntent.setComponent(new ComponentName(launchPkg2, fTarget2));
                        watIntent.setPackage(launchPkg2);
                        watIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startupLog("[WestlakeLauncher] WAT intent: pkg="
                                + watIntent.getPackage() + " cmp=" + watIntent.getComponent());
                        result2[0] = android.app.WestlakeActivityThread.launchActivity(
                                wat, fTarget2, launchPkg2, watIntent);
                        if (result2[0] != null) {
                            launchedActivity = result2[0];
                            startupLog("[WestlakeLauncher] Activity created: " + launchedActivity.getClass().getName());
                            // Queue dashboard navigation
                            android.app.WestlakeActivityThread.pendingDashboardClass =
                                "com.mcdonalds.homedashboard.activity.HomeDashboardActivity";
                        }
                    } catch (Throwable e) {
	                        dumpThrowable("[WestlakeLauncher] WestlakeActivityThread error", e);
                    }
		                    if (launchedActivity == null) {
		                        // Fallback
		                        Intent fallbackIntent = buildLaunchIntent(launchPkg2, fTarget2);
	                        am.startActivity(null, fallbackIntent, -1, resolvedActivityClass);
	                        launchedActivity = am.getResumedActivity();
	                    }
		                } else {
		                    String launchPkg = targetPackageName;
		                    Intent intent = buildLaunchIntent(launchPkg, targetActivity);
		                    am.startActivity(null, intent, -1, resolvedActivityClass);
		                    launchedActivity = am.getResumedActivity();
		                }
		        } catch (Exception e) {
            startupLog("[WestlakeLauncher] APK load error (non-fatal)", e);
            // Fallback: launch activity directly if class is on classpath
		            if (targetActivity != null && launchedActivity == null) {
		                try {
		                    String pkg = targetPackageName;
		                    Intent intent = buildLaunchIntent(pkg, targetActivity);
		                    am.startActivity(null, intent, -1, resolvedActivityClass);
	                    launchedActivity = am.getResumedActivity();
	                    startupLog("[WestlakeLauncher] Fallback launch OK: " + targetActivity);
                } catch (Exception e2) {
                    startupLog("[WestlakeLauncher] Fallback launch failed", e2);
                }
            }
        }

        // Try to get the launched activity even if errors occurred
        if (launchedActivity == null) {
            launchedActivity = am.getResumedActivity();
        }
        if (launchedActivity == null) {
            startupLog("[WestlakeLauncher] WARNING: No activity, rendering empty surface");
        }
        if (launchedActivity != null) {
            startupLog("[WestlakeLauncher] Activity launched: " + launchedActivity.getClass().getName());

            // Always load splash image for OP_IMAGE background rendering
            {
                String rDir = propOrSnapshot("westlake.apk.resdir", sBootResDir);
                if (rDir != null && splashImageData == null) {
                    String[] tryPaths = {"res/drawable/splash_screen.webp", "res/drawable-xxhdpi-v4/splash_screen.webp",
                            "res/drawable/splash_screen.png"};
                    for (String p : tryPaths) {
                        String path = joinPath(rDir, p);
                        byte[] data = tryReadFileBytes(path);
                        if (data != null && data.length > 0) {
                            splashImageData = data;
                            startupLog("[WestlakeLauncher] Loaded splash image: " + leafName(path)
                                    + " (" + splashImageData.length + " bytes)");
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
                startupLog("[WestlakeLauncher] No content view — trying to inflate real splash layout");
                android.view.View splashView = null;

                // Try to inflate the real splash layout from extracted res/
                try {
                    String rd = propOrSnapshot("westlake.apk.resdir", sBootResDir);
                    if (rd != null) {
                        String[] layoutNames = {
                            "activity_splash_screen", "splash_screen", "activity_splash",
                            "splash", "activity_main", "main"
                        };
                        for (String name : layoutNames) {
                            String layoutPath = joinPath(rd, "res/layout/" + name + ".xml");
                            byte[] axmlData = tryReadFileBytes(layoutPath);
                            if (axmlData != null && axmlData.length > 0) {
                                startupLog("[WestlakeLauncher] Found layout: " + name + ".xml (" + axmlData.length + " bytes)");
                                android.view.LayoutInflater inflater = android.view.LayoutInflater.from(launchedActivity);
                                android.content.res.BinaryXmlParser parser =
                                    new android.content.res.BinaryXmlParser(axmlData);
                                splashView = inflater.inflate(parser, null);
                                if (splashView != null) {
                                    startupLog("[WestlakeLauncher] Inflated real layout: " + splashView.getClass().getSimpleName());
                                    break;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    startupLog("[WestlakeLauncher] Layout inflate error", e);
                }

                // Load real splash image bytes (will be drawn directly via OP_IMAGE before view tree)
                {
                    String rDir = propOrSnapshot("westlake.apk.resdir", sBootResDir);
                    if (rDir != null) {
                        String[] tryPaths = {"res/drawable/splash_screen.webp", "res/drawable-xxhdpi-v4/splash_screen.webp",
                                "res/drawable-xhdpi-v4/splash_screen.webp", "res/drawable/splash_screen.png"};
                        for (String p : tryPaths) {
                            String path = joinPath(rDir, p);
                            byte[] data = tryReadFileBytes(path);
                            if (data != null && data.length > 0) {
                                splashImageData = data;
                                startupLog("[WestlakeLauncher] Loaded splash image: " + leafName(path)
                                        + " (" + splashImageData.length + " bytes)");
                                break;
                            }
                        }
                    }
                }

                // Fallback: programmatic McDonald's splash
                if (splashView == null) {
                    startupLog("[WestlakeLauncher] Using OHBridge direct render (no View tree)");
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
                            startupLog("[WestlakeLauncher] OHBridge splash frame sent!");
                        } catch (Throwable t) {
                            startupLog("[WestlakeLauncher] OHBridge render unavailable", t);
                        }
                    }
                    // Skip programmatic View fallback — go to render loop
                    splashView = null;
                }
                if (false) {
                    // Dead code — original View-based fallback kept for reference
                    startupLog("[WestlakeLauncher] UNREACHABLE programmatic splash");
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
                            startupLog("[WestlakeLauncher] Injected ContextImpl into Activity");
                        } catch (Throwable t3) {
                            startupLog("[WestlakeLauncher] Context inject failed", t3);
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
                        startupLog("[WestlakeLauncher] Set splash via Window.setContentView");
                    }
                } catch (Exception e) {
                    startupLog("[WestlakeLauncher] setContentView error", e);
                }
            }
        }

        // Render loop — render even if Activity partially failed
        if (nativeOk && launchedActivity != null) {
            startupLog("[WestlakeLauncher] Creating surface " + SURFACE_WIDTH + "x" + SURFACE_HEIGHT);
            try {
                // Call onSurfaceCreated — may not exist on real framework Activity
                launchedActivity.onSurfaceCreated(0L, SURFACE_WIDTH, SURFACE_HEIGHT);
                launchedActivity.renderFrame();
            } catch (Throwable e) {
                startupLog("[WestlakeLauncher] Initial render: " + e.getClass().getSimpleName() + " (framework Activity — using OHBridge direct)");
            }
            startupLog("[WestlakeLauncher] Initial frame rendered");
            startupLog("[WestlakeLauncher] Entering event loop...");
            renderLoop(launchedActivity, am);
        } else {
            startupLog("[WestlakeLauncher] Running in headless mode (no native bridge)");
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
                String layoutPath = joinPath(resDir, "res/layout/" + name + ".xml");
                byte[] axmlData = tryReadFileBytes(layoutPath);
                if (axmlData != null && axmlData.length > 0) {
                    startupLog("[WestlakeLauncher] Trying real layout: " + name + ".xml (" + axmlData.length + " bytes)");
                    try {
                        android.view.LayoutInflater inflater = android.view.LayoutInflater.from(activity);
                        android.content.res.BinaryXmlParser parser =
                            new android.content.res.BinaryXmlParser(axmlData);
                        splashView = inflater.inflate(parser, null);
                        if (splashView != null) {
                            startupLog("[WestlakeLauncher] Inflated real splash: " + splashView.getClass().getSimpleName()
                                + " children=" + (splashView instanceof android.view.ViewGroup
                                    ? ((android.view.ViewGroup) splashView).getChildCount() : 0));
                            break;
                        }
                    } catch (Exception e) {
                        startupLog("[WestlakeLauncher] Layout inflate error (" + name + ")", e);
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
                        byte[] data2 = tryReadFileBytes(joinPath(resDir, "res/layout/splash_screen_view.xml"));
                        if (data2 != null && data2.length > 0) {
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
                                    startupLog("[WestlakeLauncher] Injected branded splash into fragment container");
                                } else {
                                    ((android.view.ViewGroup) splashView).addView(branded,
                                        new android.view.ViewGroup.LayoutParams(-1, -1));
                                    startupLog("[WestlakeLauncher] Injected branded splash into root");
                                }
                            }
                        }
                    } catch (Exception e) {
                        startupLog("[WestlakeLauncher] Splash content inflate error", e);
                    }
                }

                activity.getWindow().setContentView(splashView);
                startupLog("[WestlakeLauncher] Set real splash layout as content");
                return; // Success — use real layout
            } catch (Exception e) {
                startupLog("[WestlakeLauncher] setContentView error", e);
            }
        }

        // Fallback to hardcoded UI
        startupLog("[WestlakeLauncher] No real splash found — using hardcoded menu");
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
        startupLog("[WestlakeLauncher] Built interactive McDonald's UI (" + items.length + " menu items)");
    }

    private static int resolveAppResourceId(Activity activity, String type, String name) {
        if (activity == null || type == null || name == null) {
            return 0;
        }
        try {
            android.content.res.Resources res = activity.getResources();
            if (res == null) {
                return 0;
            }
            String[] packages = {
                    activity.getPackageName(),
                    propOrSnapshot("westlake.apk.package", sBootPackageName),
                    sBootPackageName,
                    "com.mcdonalds.app",
                    "com.mcdonalds.homedashboard"
            };
            for (int i = 0; i < packages.length; i++) {
                String pkg = packages[i];
                if (pkg == null || pkg.isEmpty()) {
                    continue;
                }
                int id = res.getIdentifier(name, type, pkg);
                if (id != 0) {
                    return id;
                }
            }
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] resolveAppResourceId(" + type + "/" + name + ")", t);
        }
        if ("layout".equals(type)) {
            if ("activity_home_dashboard".equals(name)) return 0x7f0e0058;
            if ("activity_base".equals(name)) return 0x7f0e0038;
            if ("base_layout".equals(name)) return 0x7f0e00ee;
            if ("fragment_home_dashboard".equals(name)) return 0x7f0e027d;
        } else if ("id".equals(type)) {
            if ("home_dashboard_container".equals(name)) return 0x7f0b0ae8;
            if ("intermediate_layout_container".equals(name)) return 0x7f0b0b83;
            if ("immersive_container".equals(name)) return 0x7f0b0b68;
            if ("nestedScrollView".equals(name)) return 0x7f0b0f0b;
            if ("page_content".equals(name)) return 0x7f0b11e0;
            if ("page_content_holder".equals(name)) return 0x7f0b11e1;
            if ("parent_container".equals(name)) return 0x7f0b11fa;
            if ("sections_container".equals(name)) return 0x7f0b16c5;
        }
        return 0;
    }

    private static android.widget.TextView dashboardText(
            android.content.Context context,
            String text,
            float size,
            int color) {
        android.widget.TextView view = new android.widget.TextView(context);
        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(color);
        return view;
    }

    private static void addDashboardOfferRow(
            android.content.Context context,
            android.view.ViewGroup parent,
            String title,
            String price) {
        android.widget.LinearLayout row = new android.widget.LinearLayout(context);
        row.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        row.setPadding(0, 24, 0, 8);

        android.widget.TextView titleView = dashboardText(context, title, 18, 0xFF333333);
        android.widget.TextView priceView = dashboardText(context, price, 18, 0xFF555555);

        android.widget.LinearLayout.LayoutParams titleParams =
                llp(0, -2, 1f);
        row.addView(titleView, titleParams);
        row.addView(priceView,
                llp(-2, -2));

        parent.addView(row,
                llp(-1, -2));
    }

    private static android.widget.LinearLayout.LayoutParams llp(int width, int height) {
        return new android.widget.LinearLayout.LayoutParams(width, height, 0f);
    }

    private static android.widget.LinearLayout.LayoutParams llp(
            int width, int height, float weight) {
        android.widget.LinearLayout.LayoutParams params =
                new android.widget.LinearLayout.LayoutParams(width, height, weight);
        params.width = width;
        params.height = height;
        params.weight = weight;
        return params;
    }

    private static android.widget.LinearLayout.LayoutParams dashboardCanvasLp() {
        android.widget.LinearLayout.LayoutParams params =
                new android.widget.LinearLayout.LayoutParams(-1, SURFACE_HEIGHT);
        params.width = -1;
        params.height = SURFACE_HEIGHT;
        return params;
    }

    private static int resolveAppColor(
            Activity activity, String name, int fallbackColor) {
        if (activity == null || name == null) {
            return fallbackColor;
        }
        int colorId = resolveAppResourceId(activity, "color", name);
        if (colorId == 0) {
            return fallbackColor;
        }
        try {
            android.content.res.Resources res = activity.getResources();
            if (res != null) {
                return res.getColor(colorId);
            }
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] resolveAppColor(" + name + ")", t);
        }
        return fallbackColor;
    }

    private static int resolveAppDimension(
            Activity activity, String name, int fallbackPx) {
        if (activity == null || name == null) {
            return fallbackPx;
        }
        int dimenId = resolveAppResourceId(activity, "dimen", name);
        if (dimenId == 0) {
            return fallbackPx;
        }
        try {
            android.content.res.Resources res = activity.getResources();
            if (res != null) {
                return res.getDimensionPixelOffset(dimenId);
            }
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] resolveAppDimension(" + name + ")", t);
        }
        return fallbackPx;
    }

    private static void drawDashboardSkeleton(
            android.content.Context context,
            android.graphics.Canvas canvas,
            int width,
            int height) {
        if (canvas == null || width <= 0 || height <= 0) {
            return;
        }
        float density = safeDensity(context);
        android.graphics.Paint fill = new android.graphics.Paint();
        fill.setStyle(android.graphics.Paint.Style.FILL);
        fill.setAntiAlias(true);

        android.graphics.Paint dividerPaint = new android.graphics.Paint();
        dividerPaint.setColor(0xFFE3E3E3);
        dividerPaint.setStrokeWidth(Math.max(1f, density));

        fill.setColor(0xFFF7F7F7);
        canvas.drawRect(0, 0, width, height, fill);

        int pad = dpPx(context, 18);
        int y = dpPx(context, 24);

        fill.setColor(0xFFDA291C);
        int heroHeight = dpPx(context, 104);
        canvas.drawRect(pad, y, width - pad, y + heroHeight, fill);
        fill.setColor(0xFFFFC72C);
        canvas.drawRect(pad + dpPx(context, 18), y + dpPx(context, 20),
                pad + dpPx(context, 118), y + dpPx(context, 34), fill);
        fill.setColor(0xFFFFE2DE);
        canvas.drawRect(pad + dpPx(context, 18), y + dpPx(context, 48),
                width - pad - dpPx(context, 90), y + dpPx(context, 58), fill);

        y += heroHeight + dpPx(context, 18);
        int[] accentColors = {
                0xFFF5C518,
                0xFFDA291C,
                0xFFFFBC0D,
                0xFF6F6F6F,
        };
        for (int i = 0; i < accentColors.length; i++) {
            fill.setColor(0xFFFFFFFF);
            int rowHeight = dpPx(context, 62);
            canvas.drawRect(pad, y, width - pad, y + rowHeight, fill);
            fill.setColor(accentColors[i]);
            canvas.drawRect(pad + dpPx(context, 16), y + dpPx(context, 18),
                    pad + dpPx(context, 52), y + dpPx(context, 44), fill);
            fill.setColor(0xFF27251F);
            canvas.drawRect(pad + dpPx(context, 64), y + dpPx(context, 16),
                    width - pad - dpPx(context, 120), y + dpPx(context, 26), fill);
            fill.setColor(0xFF777777);
            canvas.drawRect(pad + dpPx(context, 64), y + dpPx(context, 34),
                    width - pad - dpPx(context, 160), y + dpPx(context, 42), fill);
            fill.setColor(0xFFB0B0B0);
            canvas.drawRect(width - pad - dpPx(context, 72), y + dpPx(context, 22),
                    width - pad - dpPx(context, 16), y + dpPx(context, 40), fill);
            canvas.drawLine(pad, y + rowHeight, width - pad, y + rowHeight, dividerPaint);
            y += rowHeight + dpPx(context, 10);
        }

        fill.setColor(0xFFE9E9E9);
        int footerTop = height - dpPx(context, 54);
        canvas.drawRect(pad, footerTop, width - pad, footerTop + dpPx(context, 34), fill);
        fill.setColor(0xFF888888);
        int tabWidth = (width - (pad * 2) - dpPx(context, 32)) / 5;
        for (int i = 0; i < 5; i++) {
            int left = pad + dpPx(context, 8) + (i * tabWidth);
            canvas.drawRect(left, footerTop + dpPx(context, 10),
                    left + dpPx(context, 26), footerTop + dpPx(context, 18), fill);
        }
    }

    private static android.view.View buildProgrammaticDashboardFallbackRoot(Activity activity) {
        if (activity == null) {
            return null;
        }
        startupLog("[WestlakeLauncher] buildDashboardRoot begin");
        final int idHomeContainer = 0x7f0b0ae8;

        android.widget.LinearLayout root = new android.widget.LinearLayout(activity) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int desiredWidth = SURFACE_WIDTH;
                int desiredHeight = Math.max(SURFACE_HEIGHT, dpPx(getContext(), 640));
                int measuredWidth = android.view.View.resolveSize(desiredWidth, widthMeasureSpec);
                int measuredHeight = android.view.View.resolveSize(desiredHeight, heightMeasureSpec);
                setMeasuredDimension(measuredWidth, measuredHeight);
            }

            @Override
            protected void onDraw(android.graphics.Canvas canvas) {
                super.onDraw(canvas);
                drawDashboardSkeleton(getContext(), canvas, getWidth(), getHeight());
            }
        };
        startupLog("[WestlakeLauncher] buildDashboardRoot root new");
        root.setWillNotDraw(false);
        root.setMinimumHeight(dpPx(activity, 420));
        root.setId(idHomeContainer);
        root.setOrientation(android.widget.LinearLayout.VERTICAL);
        startupLog("[WestlakeLauncher] buildDashboardRoot root orientation");
        startupLog("[WestlakeLauncher] buildDashboardRoot root ready");
        startupLog("[WestlakeLauncher] buildDashboardRoot done");

        return root;
    }

    private static Object findDashboardFragmentInstance(Activity activity) {
        if (activity == null) {
            return null;
        }
        for (Class<?> c = activity.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
            java.lang.reflect.Field[] fields = c.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                java.lang.reflect.Field f = fields[i];
                try {
                    f.setAccessible(true);
                    Object value = f.get(activity);
                    if (value != null) {
                        String typeName = value.getClass().getName();
                        if (typeName != null && typeName.endsWith("HomeDashboardFragment")) {
                            return value;
                        }
                    }
                } catch (Throwable ignored) {
                }
            }
        }
        return null;
    }

    private static java.lang.reflect.Field findFieldOnHierarchy(Class<?> type, String name) {
        for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            try {
                java.lang.reflect.Field field = c.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException ignored) {
            } catch (Throwable ignored) {
                return null;
            }
        }
        return null;
    }

    private static void seedHiltFragmentContext(Object fragment, Activity activity) {
        if (fragment == null || activity == null) {
            return;
        }
        try {
            Class<?> fragClass = fragment.getClass();
            boolean isHomeDashboardFragment =
                    "com.mcdonalds.homedashboard.fragment.HomeDashboardFragment".equals(
                            fragClass.getName());
            if (isHomeDashboardFragment) {
                java.lang.reflect.Field contextWrapperField =
                        findFieldOnHierarchy(fragClass, "D");
                java.lang.reflect.Field contextFixField =
                        findFieldOnHierarchy(fragClass, "E");
                if (contextWrapperField != null && contextWrapperField.get(fragment) == null) {
                    try {
                        ClassLoader cl = fragClass.getClassLoader();
                        Class<?> managerClass = cl != null
                                ? cl.loadClass("dagger.hilt.android.internal.managers.FragmentComponentManager")
                                : Class.forName("dagger.hilt.android.internal.managers.FragmentComponentManager");
                        java.lang.reflect.Method[] methods = managerClass.getDeclaredMethods();
                        for (int i = 0; i < methods.length; i++) {
                            java.lang.reflect.Method method = methods[i];
                            if (method == null
                                    || !"b".equals(method.getName())
                                    || !java.lang.reflect.Modifier.isStatic(method.getModifiers())
                                    || method.getParameterTypes().length != 2) {
                                continue;
                            }
                            method.setAccessible(true);
                            Object wrapper = method.invoke(null, activity, fragment);
                            if (wrapper != null) {
                                contextWrapperField.set(fragment, wrapper);
                                startupLog("[WestlakeLauncher] Seeded Hilt fragment context wrapper");
                                break;
                            }
                        }
                    } catch (Throwable wrapperError) {
                        startupLog("[WestlakeLauncher] seedHiltFragmentContext wrapper",
                                wrapperError);
                    }
                }
                if (contextFixField != null) {
                    contextFixField.setBoolean(fragment, true);
                }
            }
            java.lang.reflect.Field componentLockField =
                    findFieldOnHierarchy(fragClass, "I");
            java.lang.reflect.Field injectedField =
                    findFieldOnHierarchy(fragClass, "J");
            if (componentLockField != null && componentLockField.get(fragment) == null) {
                componentLockField.set(fragment, new Object());
                startupLog("[WestlakeLauncher] Seeded Hilt fragment component lock");
            }
            if (injectedField != null) {
                boolean skipInject = isHomeDashboardFragment;
                injectedField.setBoolean(fragment, skipInject);
                if (skipInject) {
                    startupLog("[WestlakeLauncher] Skipping Hilt inject for HomeDashboardFragment attach");
                }
            }
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] seedHiltFragmentContext", t);
        }
    }

    private static void seedHomeDashboardFragmentCtorState(Object fragment) {
        if (fragment == null) {
            return;
        }
        try {
            Class<?> fragClass = fragment.getClass();
            if (!"com.mcdonalds.homedashboard.fragment.HomeDashboardFragment".equals(
                    fragClass.getName())) {
                return;
            }
            ClassLoader cl = fragClass.getClassLoader();
            java.lang.reflect.Field disposablesField =
                    findFieldOnHierarchy(fragClass, "K0");
            if (disposablesField != null && disposablesField.get(fragment) == null) {
                try {
                    Object disposables = tryCtorSeedAlloc(
                            cl, "io.reactivex.disposables.CompositeDisposable");
                    if (disposables != null) {
                        disposablesField.set(fragment, disposables);
                    }
                } catch (Throwable fieldError) {
                    startupLog("[WestlakeLauncher] seedHomeDashboardFragmentCtorState K0",
                            fieldError);
                }
            }
            java.lang.reflect.Field listField =
                    findFieldOnHierarchy(fragClass, "O0");
            if (listField != null && listField.get(fragment) == null) {
                try {
                    listField.set(fragment, new java.util.ArrayList<>());
                } catch (Throwable fieldError) {
                    startupLog("[WestlakeLauncher] seedHomeDashboardFragmentCtorState O0",
                            fieldError);
                }
            }
            java.lang.reflect.Field dealsProviderField =
                    findFieldOnHierarchy(fragClass, "T0");
            if (dealsProviderField != null && dealsProviderField.get(fragment) == null) {
                try {
                    Object dealsProvider = tryCtorSeedAlloc(
                            cl, "com.mcdonalds.homedashboard.deals.DealsFragmentProvider");
                    if (dealsProvider != null) {
                        dealsProviderField.set(fragment, dealsProvider);
                    }
                } catch (Throwable fieldError) {
                    startupLog("[WestlakeLauncher] seedHomeDashboardFragmentCtorState T0",
                            fieldError);
                }
            }
            java.lang.reflect.Field shouldTrackViewField =
                    findFieldOnHierarchy(fragClass, "mShouldTrackView");
            if (shouldTrackViewField != null) {
                try {
                    shouldTrackViewField.setBoolean(fragment, true);
                } catch (Throwable fieldError) {
                    startupLog("[WestlakeLauncher] seedHomeDashboardFragmentCtorState mShouldTrackView",
                            fieldError);
                }
            }
            startupLog("[WestlakeLauncher] Seeded HomeDashboardFragment ctor fields");
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] seedHomeDashboardFragmentCtorState", t);
        }
    }

    private static Object tryCtorSeedAlloc(ClassLoader cl, String className) {
        if (className == null || className.isEmpty()) {
            return null;
        }
        try {
            Class<?> target = cl != null ? cl.loadClass(className) : Class.forName(className);
            Object instance = tryAllocInstance(target);
            if (instance == null) {
                instance = tryUnsafeAllocInstance(target);
            }
            return instance;
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] tryCtorSeedAlloc " + className, t);
            return null;
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void seedSupportFragmentBaseState(Object fragment, Activity activity) {
        if (fragment == null) {
            return;
        }
        try {
            if (fragment instanceof androidx.fragment.app.Fragment) {
                java.lang.reflect.Field activityField =
                        findFieldOnHierarchy(fragment.getClass(), "mActivity");
                java.lang.reflect.Field hostField =
                        findFieldOnHierarchy(fragment.getClass(), "mHost");
                java.lang.reflect.Field parentManagerField =
                        findFieldOnHierarchy(fragment.getClass(), "mFragmentManager");
                java.lang.reflect.Field childManagerField =
                        findFieldOnHierarchy(fragment.getClass(), "mChildFragmentManager");
                java.lang.reflect.Field menuVisibleField =
                        findFieldOnHierarchy(fragment.getClass(), "mMenuVisible");
                java.lang.reflect.Field userVisibleHintField =
                        findFieldOnHierarchy(fragment.getClass(), "mUserVisibleHint");
                java.lang.reflect.Field lifecycleRegistryField =
                        findFieldOnHierarchy(fragment.getClass(), "mLifecycleRegistry");
                java.lang.reflect.Field activityContextField =
                        findFieldOnHierarchy(fragment.getClass(), "mActivityContext");
                java.lang.reflect.Field saveStateCallbackField =
                        findFieldOnHierarchy(fragment.getClass(), "mSaveStateCallback");

                androidx.fragment.app.FragmentActivity hostActivity =
                        activity instanceof androidx.fragment.app.FragmentActivity
                                ? (androidx.fragment.app.FragmentActivity) activity
                                : null;

                if (activityField != null && hostActivity != null && activityField.get(fragment) == null) {
                    activityField.set(fragment, hostActivity);
                }
                if (hostField != null && activity != null && hostField.get(fragment) == null) {
                    hostField.set(fragment, activity);
                }
                if (activityContextField != null && activity != null
                        && activityContextField.get(fragment) == null) {
                    activityContextField.set(fragment, activity);
                }
                if (saveStateCallbackField != null && activity != null
                        && saveStateCallbackField.get(fragment) == null) {
                    saveStateCallbackField.set(fragment, activity);
                }
                startupLog("[WestlakeLauncher] seedSupportFragmentBaseState shim host/activity ready");

                androidx.fragment.app.FragmentManager parentManager =
                        hostActivity != null ? hostActivity.getSupportFragmentManager() : null;
                if (parentManagerField != null && parentManager != null && parentManagerField.get(fragment) == null) {
                    parentManagerField.set(fragment, parentManager);
                }
                if (hostActivity != null) {
                    ((androidx.fragment.app.Fragment) fragment).getChildFragmentManager();
                } else if (childManagerField != null && childManagerField.get(fragment) == null) {
                    childManagerField.set(fragment, new androidx.fragment.app.FragmentManager());
                }
                startupLog("[WestlakeLauncher] seedSupportFragmentBaseState child-manager ready");

                if (lifecycleRegistryField != null && lifecycleRegistryField.get(fragment) == null) {
                    lifecycleRegistryField.set(fragment,
                            new androidx.lifecycle.LifecycleRegistry(
                                    (androidx.lifecycle.LifecycleOwner) fragment));
                }
                if (menuVisibleField != null) {
                    menuVisibleField.setBoolean(fragment, true);
                }
                if (userVisibleHintField != null) {
                    userVisibleHintField.setBoolean(fragment, true);
                }
                startupLog("[WestlakeLauncher] Seeded support Fragment base fields");
                return;
            }

            Class<?> fragmentBaseClass = null;
            for (Class<?> c = fragment.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
                if ("androidx.fragment.app.Fragment".equals(c.getName())) {
                    fragmentBaseClass = c;
                    break;
                }
            }
            if (fragmentBaseClass == null) {
                return;
            }

            ClassLoader cl = fragment.getClass().getClassLoader();
            if (cl == null) {
                cl = fragmentBaseClass.getClassLoader();
            }
            java.lang.reflect.Field whoField = findFieldOnHierarchy(fragmentBaseClass, "mWho");
            java.lang.reflect.Field stateField = findFieldOnHierarchy(fragmentBaseClass, "mState");
            java.lang.reflect.Field childManagerField =
                    findFieldOnHierarchy(fragmentBaseClass, "mChildFragmentManager");
            java.lang.reflect.Field menuVisibleField =
                    findFieldOnHierarchy(fragmentBaseClass, "mMenuVisible");
            java.lang.reflect.Field userVisibleHintField =
                    findFieldOnHierarchy(fragmentBaseClass, "mUserVisibleHint");
            java.lang.reflect.Field maxStateField =
                    findFieldOnHierarchy(fragmentBaseClass, "mMaxState");
            java.lang.reflect.Field liveDataField =
                    findFieldOnHierarchy(fragmentBaseClass, "mViewLifecycleOwnerLiveData");
            java.lang.reflect.Field nextLocalRequestCodeField =
                    findFieldOnHierarchy(fragmentBaseClass, "mNextLocalRequestCode");
            java.lang.reflect.Field onPreAttachedListenersField =
                    findFieldOnHierarchy(fragmentBaseClass, "mOnPreAttachedListeners");

            if (stateField != null) {
                stateField.setInt(fragment, -1);
            }
            if (whoField != null && whoField.get(fragment) == null) {
                whoField.set(fragment, java.util.UUID.randomUUID().toString());
            }
            startupLog("[WestlakeLauncher] seedSupportFragmentBaseState who/state ready");
            if (childManagerField != null && childManagerField.get(fragment) == null) {
                Class<?> fmImplClass;
                try {
                    fmImplClass = cl != null
                            ? cl.loadClass("androidx.fragment.app.FragmentManagerImpl")
                            : Class.forName("androidx.fragment.app.FragmentManagerImpl");
                } catch (Throwable noImpl) {
                    fmImplClass = cl != null
                            ? cl.loadClass("androidx.fragment.app.FragmentManager")
                            : Class.forName("androidx.fragment.app.FragmentManager");
                }
                Object childManager = null;
                try {
                    childManager = tryAllocInstance(fmImplClass);
                    if (childManager == null) {
                        childManager = tryUnsafeAllocInstance(fmImplClass);
                    }
                } catch (Throwable allocError) {
                    startupLog("[WestlakeLauncher] seedSupportFragmentBaseState child-manager alloc",
                            allocError);
                }
                if (childManager != null) {
                    childManagerField.set(fragment, childManager);
                }
            }
            if (menuVisibleField != null) {
                menuVisibleField.setBoolean(fragment, true);
            }
            if (userVisibleHintField != null) {
                userVisibleHintField.setBoolean(fragment, true);
            }
            startupLog("[WestlakeLauncher] seedSupportFragmentBaseState child-manager ready");
            if (maxStateField != null && maxStateField.get(fragment) == null) {
                Class<?> lifecycleStateClass = cl != null
                        ? cl.loadClass("androidx.lifecycle.Lifecycle$State")
                        : Class.forName("androidx.lifecycle.Lifecycle$State");
                @SuppressWarnings("unchecked")
                Class<? extends java.lang.Enum> enumClass =
                        (Class<? extends java.lang.Enum>) lifecycleStateClass;
                Object resumed = java.lang.Enum.valueOf(enumClass, "RESUMED");
                maxStateField.set(fragment, resumed);
            }
            startupLog("[WestlakeLauncher] seedSupportFragmentBaseState max-state ready");
            if (liveDataField != null && liveDataField.get(fragment) == null) {
                Class<?> mutableLiveDataClass = cl != null
                        ? cl.loadClass("androidx.lifecycle.MutableLiveData")
                        : Class.forName("androidx.lifecycle.MutableLiveData");
                java.lang.reflect.Constructor<?> liveDataCtor =
                        mutableLiveDataClass.getDeclaredConstructor();
                liveDataCtor.setAccessible(true);
                liveDataField.set(fragment, liveDataCtor.newInstance());
            }
            if (nextLocalRequestCodeField != null && nextLocalRequestCodeField.get(fragment) == null) {
                nextLocalRequestCodeField.set(fragment, new java.util.concurrent.atomic.AtomicInteger());
            }
            if (onPreAttachedListenersField != null && onPreAttachedListenersField.get(fragment) == null) {
                onPreAttachedListenersField.set(fragment, new java.util.ArrayList());
            }
            startupLog("[WestlakeLauncher] Seeded support Fragment base fields");
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] seedSupportFragmentBaseState", t);
        }
    }

    private static void primeSupportFragmentHost(Activity activity) {
        if (activity == null) {
            return;
        }
        try {
            if (activity instanceof androidx.fragment.app.FragmentActivity) {
                try {
                    androidx.fragment.app.FragmentManager fm =
                            ((androidx.fragment.app.FragmentActivity) activity).getSupportFragmentManager();
                    java.lang.reflect.Field hostField =
                            findFieldOnHierarchy(fm != null ? fm.getClass() : null, "mHost");
                    if (hostField != null && hostField.get(fm) == null) {
                        hostField.set(fm, activity);
                    }
                } catch (Throwable directShimError) {
                    startupLog("[WestlakeLauncher] primeSupportFragmentHost direct manager", directShimError);
                }
                startupLog("[WestlakeLauncher] Primed shim support FragmentManager host");
            }

            Class<?> fragmentActivityClass = null;
            for (Class<?> c = activity.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
                if ("androidx.fragment.app.FragmentActivity".equals(c.getName())) {
                    fragmentActivityClass = c;
                    break;
                }
            }
            if (fragmentActivityClass == null) {
                return;
            }
            startupLog("[WestlakeLauncher] primeSupportFragmentHost class="
                    + fragmentActivityClass.getName());

            ClassLoader cl = fragmentActivityClass.getClassLoader();
            java.lang.reflect.Field fragmentsField =
                    findFieldOnHierarchy(fragmentActivityClass, "mFragments");
            if (fragmentsField == null) {
                startupLog("[WestlakeLauncher] primeSupportFragmentHost mFragments field missing");
                return;
            }

            Object controller = fragmentsField.get(activity);
            if (controller == null) {
                Class<?> hostCallbacksClass =
                        cl.loadClass("androidx.fragment.app.FragmentActivity$HostCallbacks");
                java.lang.reflect.Constructor<?> hostCtor =
                        hostCallbacksClass.getDeclaredConstructor(fragmentActivityClass);
                hostCtor.setAccessible(true);
                Object hostCallbacks = hostCtor.newInstance(activity);

                Class<?> hostCallbackClass =
                        cl.loadClass("androidx.fragment.app.FragmentHostCallback");
                Class<?> controllerClass =
                        cl.loadClass("androidx.fragment.app.FragmentController");
                java.lang.reflect.Method buildController =
                        controllerClass.getDeclaredMethod("b", hostCallbackClass);
                buildController.setAccessible(true);
                controller = buildController.invoke(null, hostCallbacks);
                fragmentsField.set(activity, controller);
                startupLog("[WestlakeLauncher] Created FragmentActivity.mFragments controller");
            } else {
                startupLog("[WestlakeLauncher] Reusing FragmentActivity.mFragments controller");
            }

            java.lang.reflect.Field lifecycleField =
                    findFieldOnHierarchy(fragmentActivityClass, "mFragmentLifecycleRegistry");
            if (lifecycleField != null && lifecycleField.get(activity) == null) {
                lifecycleField.set(activity, new androidx.lifecycle.LifecycleRegistry(
                        (androidx.lifecycle.LifecycleOwner) activity));
            }
            java.lang.reflect.Field stoppedField =
                    findFieldOnHierarchy(fragmentActivityClass, "mStopped");
            if (stoppedField != null) {
                stoppedField.setBoolean(activity, true);
            }

            java.lang.reflect.Method getManager = controller.getClass().getDeclaredMethod("l");
            getManager.setAccessible(true);
            Object fragmentManager = getManager.invoke(controller);
            java.lang.reflect.Field hostField =
                    findFieldOnHierarchy(fragmentManager.getClass(), "x");
            if (hostField != null && hostField.get(fragmentManager) == null) {
                Class<?> fragmentClass = cl.loadClass("androidx.fragment.app.Fragment");
                java.lang.reflect.Method attachHost =
                        controller.getClass().getDeclaredMethod("a", fragmentClass);
                attachHost.setAccessible(true);
                attachHost.invoke(controller, new Object[]{null});
                startupLog("[WestlakeLauncher] Attached support FragmentManager host");
            } else {
                startupLog("[WestlakeLauncher] Support FragmentManager host already attached");
            }
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] primeSupportFragmentHost", t);
        }
    }

    private static void attachFragmentToActivity(Object fragment, Class<?> fragClass, Activity activity) {
        if (fragment == null || fragClass == null || activity == null) {
            return;
        }
        try {
            java.lang.reflect.Method onAttach = null;
            for (Class<?> c = fragClass; c != null; c = c.getSuperclass()) {
                try {
                    onAttach = c.getDeclaredMethod("onAttach", android.content.Context.class);
                    break;
                } catch (NoSuchMethodException ignored) {
                }
            }
            if (onAttach != null) {
                onAttach.setAccessible(true);
                onAttach.invoke(fragment, activity);
                startupLog("[WestlakeLauncher] HomeDashboardFragment onAttach(Context) invoked");
                return;
            }
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] HomeDashboardFragment onAttach(Context)", t);
        }
        try {
            java.lang.reflect.Method onAttach = null;
            for (Class<?> c = fragClass; c != null; c = c.getSuperclass()) {
                try {
                    onAttach = c.getDeclaredMethod("onAttach", Activity.class);
                    break;
                } catch (NoSuchMethodException ignored) {
                }
            }
            if (onAttach != null) {
                onAttach.setAccessible(true);
                onAttach.invoke(fragment, activity);
                startupLog("[WestlakeLauncher] HomeDashboardFragment onAttach(Activity) invoked");
            }
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] HomeDashboardFragment onAttach(Activity)", t);
        }
    }

    private static void invokeFragmentLifecycleMethod(
            Object fragment,
            Class<?> fragClass,
            String name,
            Class<?>[] paramTypes,
            Object[] args) {
        if (fragment == null || fragClass == null || name == null) {
            return;
        }
        for (Class<?> c = fragClass; c != null && c != Object.class; c = c.getSuperclass()) {
            try {
                java.lang.reflect.Method m = c.getDeclaredMethod(name, paramTypes);
                m.setAccessible(true);
                m.invoke(fragment, args);
                startupLog("[WestlakeLauncher] HomeDashboardFragment " + name + "() invoked");
                return;
            } catch (NoSuchMethodException ignored) {
            } catch (Throwable t) {
                startupLog("[WestlakeLauncher] HomeDashboardFragment " + name, t);
                return;
            }
        }
    }

    private static boolean tryAttachHomeDashboardFragment(
            Activity activity,
            android.view.ViewGroup homeContainer) {
        if (activity == null || homeContainer == null) {
            return false;
        }
        try {
            Object fragment = findDashboardFragmentInstance(activity);
            Class<?> fragClass = fragment != null ? fragment.getClass()
                    : loadAppClass("com.mcdonalds.homedashboard.fragment.HomeDashboardFragment");
            if (fragClass == null) {
                return false;
            }
            if (fragment == null) {
                try {
                    fragment = fragClass.getDeclaredConstructor().newInstance();
                } catch (Throwable ctorError) {
                    fragment = tryAllocInstance(fragClass);
                    if (fragment == null) {
                        startupLog("[WestlakeLauncher] HomeDashboardFragment ctor/alloc failed", ctorError);
                        return false;
                    }
                }
            }

            logDashboardOwnershipOnce(activity, fragment);
            seedHomeDashboardFragmentCtorState(fragment);
            seedSupportFragmentBaseState(fragment, activity);
            seedHiltFragmentContext(fragment, activity);

            boolean fragmentManagerAttempted = false;
            Class<?> runtimeFragmentClass =
                    findNamedClassOnHierarchy(fragment.getClass(), "androidx.fragment.app.Fragment");
            Class<?> runtimeFragmentActivityClass =
                    findNamedClassOnHierarchy(activity.getClass(), "androidx.fragment.app.FragmentActivity");
            if (runtimeFragmentClass != null && runtimeFragmentActivityClass != null) {
                try {
                    fragmentManagerAttempted = true;
                    primeSupportFragmentHost(activity);
                    startupLog("[WestlakeLauncher] HomeDashboardFragment activity class="
                            + activity.getClass().getName());
                    int containerId = homeContainer.getId();
                    if (containerId == 0) {
                        containerId = resolveAppResourceId(activity, "id", "home_dashboard_container");
                        if (containerId != 0) {
                            homeContainer.setId(containerId);
                        }
                    }
                    if (containerId != 0) {
                        android.view.View liveContainer = activity.findViewById(containerId);
                        if (liveContainer == null) {
                            startupLog("[WestlakeLauncher] HomeDashboardFragment container missing: 0x"
                                    + Integer.toHexString(containerId));
                            return false;
                        }
                        java.lang.reflect.Method getSupportFragmentManager =
                                runtimeFragmentActivityClass.getMethod("getSupportFragmentManager");
                        Object fm = getSupportFragmentManager.invoke(activity);
                        if (fm == null) {
                            startupLog("[WestlakeLauncher] HomeDashboardFragment support FragmentManager is null");
                            return false;
                        }
                        startupLog("[WestlakeLauncher] HomeDashboardFragment support FragmentManager ready");
                        startupLog("[WestlakeLauncher] HomeDashboardFragment support FragmentManager class="
                                + (fm != null ? fm.getClass().getName() : "null"));
                        Object tx;
                        try {
                            java.lang.reflect.Method beginTransaction =
                                    fm.getClass().getMethod("beginTransaction");
                            tx = beginTransaction.invoke(fm);
                            startupLog("[WestlakeLauncher] HomeDashboardFragment transaction begin");
                        } catch (Throwable beginError) {
                            startupLog("[WestlakeLauncher] HomeDashboardFragment beginTransaction", beginError);
                            throw beginError;
                        }
                        Object existingFragment = null;
                        try {
                            java.lang.reflect.Method findFragmentById =
                                    fm.getClass().getMethod("findFragmentById", int.class);
                            existingFragment = findFragmentById.invoke(fm, containerId);
                            if (existingFragment == null) {
                                java.lang.reflect.Method findFragmentByTag =
                                        fm.getClass().getMethod("findFragmentByTag", String.class);
                                existingFragment = findFragmentByTag.invoke(fm, "HomeDashboardFragment");
                            }
                        } catch (Throwable lookupError) {
                            startupLog("[WestlakeLauncher] HomeDashboardFragment existing lookup", lookupError);
                        }
                        if (existingFragment != null && existingFragment != fragment) {
                            startupLog("[WestlakeLauncher] HomeDashboardFragment transaction=replace");
                            java.lang.reflect.Method replace = tx.getClass().getMethod(
                                    "replace", int.class, runtimeFragmentClass, String.class);
                            replace.invoke(tx, containerId, fragment, "HomeDashboardFragment");
                        } else {
                            startupLog("[WestlakeLauncher] HomeDashboardFragment transaction=add");
                            java.lang.reflect.Method add = tx.getClass().getMethod(
                                    "add", int.class, runtimeFragmentClass, String.class);
                            add.invoke(tx, containerId, fragment, "HomeDashboardFragment");
                        }
                        startupLog("[WestlakeLauncher] HomeDashboardFragment transaction commitNow");
                        java.lang.reflect.Method commitNowAllowingStateLoss =
                                tx.getClass().getMethod("commitNowAllowingStateLoss");
                        commitNowAllowingStateLoss.invoke(tx);
                        startupLog("[WestlakeLauncher] HomeDashboardFragment transaction committed");
                        java.lang.reflect.Method getView =
                                runtimeFragmentClass.getMethod("getView");
                        Object fragViewObj = getView.invoke(fragment);
                        android.view.View fragView =
                                fragViewObj instanceof android.view.View
                                        ? (android.view.View) fragViewObj
                                        : null;
                        if (fragView != null || homeContainer.getChildCount() > 0) {
                            startupLog("[WestlakeLauncher] HomeDashboardFragment attached via FragmentManager");
                            return true;
                        }
                        startupLog("[WestlakeLauncher] HomeDashboardFragment FragmentManager attach produced no view");
                    }
                } catch (Throwable fmError) {
                    startupLog("[WestlakeLauncher] HomeDashboardFragment FragmentManager attach", fmError);
                    logThrowableFrames("[WestlakeLauncher] HomeDashboardFragment FragmentManager attach",
                            fmError, 12);
                }
            }

            if (fragmentManagerAttempted && runtimeFragmentClass != null) {
                startupLog("[WestlakeLauncher] HomeDashboardFragment skipping manual re-attach after FragmentManager failure");
                return false;
            }

            for (Class<?> c = activity.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
                try {
                    java.lang.reflect.Field f = c.getDeclaredField("mHomeDashboardFragment");
                    f.setAccessible(true);
                    if (f.get(activity) == null) {
                        f.set(activity, fragment);
                    }
                    break;
                } catch (NoSuchFieldException ignored) {
                } catch (Throwable ignored) {
                    break;
                }
            }

            attachFragmentToActivity(fragment, fragClass, activity);
            invokeFragmentLifecycleMethod(fragment, fragClass, "onCreate",
                    new Class<?>[]{android.os.Bundle.class},
                    new Object[]{null});

            android.view.LayoutInflater inflater = activity.getLayoutInflater();
            java.lang.reflect.Method onCreateView = null;
            for (Class<?> c = fragClass; c != null && c != Object.class; c = c.getSuperclass()) {
                try {
                    onCreateView = c.getDeclaredMethod("onCreateView",
                            android.view.LayoutInflater.class,
                            android.view.ViewGroup.class,
                            android.os.Bundle.class);
                    break;
                } catch (NoSuchMethodException ignored) {
                }
            }
            if (onCreateView == null) {
                return false;
            }
            onCreateView.setAccessible(true);
            Object fragViewObj = onCreateView.invoke(fragment, inflater, homeContainer, null);
            if (!(fragViewObj instanceof android.view.View)) {
                startupLog("[WestlakeLauncher] HomeDashboardFragment onCreateView returned null");
                return false;
            }

            android.view.View fragView = (android.view.View) fragViewObj;
            homeContainer.removeAllViews();
            homeContainer.addView(fragView);
            startupLog("[WestlakeLauncher] HomeDashboardFragment view attached: "
                    + fragView.getClass().getName());

            invokeFragmentLifecycleMethod(fragment, fragClass, "onViewCreated",
                    new Class<?>[]{android.view.View.class, android.os.Bundle.class},
                    new Object[]{fragView, null});
            invokeFragmentLifecycleMethod(fragment, fragClass, "onActivityCreated",
                    new Class<?>[]{android.os.Bundle.class},
                    new Object[]{null});
            invokeFragmentLifecycleMethod(fragment, fragClass, "onStart",
                    new Class<?>[0], new Object[0]);
            invokeFragmentLifecycleMethod(fragment, fragClass, "onResume",
                    new Class<?>[0], new Object[0]);
            return homeContainer.getChildCount() > 0;
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] tryAttachHomeDashboardFragment", t);
            logThrowableFrames("[WestlakeLauncher] tryAttachHomeDashboardFragment", t, 12);
            return false;
        }
    }

    private static boolean installProgrammaticHomeDashboardFragment(
            Activity activity,
            android.view.ViewGroup homeContainer) {
        if (activity == null || homeContainer == null) {
            return false;
        }
        try {
            homeContainer.removeAllViews();
            startupLog("[WestlakeLauncher] buildProgrammaticHomeDashboardFragment cleared");
            if (tryAttachHomeDashboardFragment(activity, homeContainer)) {
                startupLog("[WestlakeLauncher] Programmatic HomeDashboardFragment attached");
                return true;
            }
            startupLog("[WestlakeLauncher] Skipping fragment_home_dashboard shell inflate after attach failure");
            int sectionsContainerId = resolveAppResourceId(activity, "id", "sections_container");
            android.view.View content = buildDashboardCanvasContent(activity);
            if (content == null) {
                startupLog("[WestlakeLauncher] buildProgrammaticHomeDashboardFragment content=null");
                return false;
            }
            if (sectionsContainerId != 0) {
                content.setId(sectionsContainerId);
            }
            android.view.ViewGroup.LayoutParams homeParams;
            if (homeContainer instanceof android.widget.LinearLayout) {
                homeParams = new android.widget.LinearLayout.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            } else if (homeContainer instanceof android.widget.FrameLayout) {
                homeParams = new android.widget.FrameLayout.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            } else if (homeContainer instanceof android.widget.RelativeLayout) {
                homeParams = new android.widget.RelativeLayout.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            } else {
                homeParams = new android.view.ViewGroup.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            String homeClassName = homeContainer.getClass().getName();
            if (homeClassName != null
                    && homeClassName.startsWith("com.westlake.engine.WestlakeLauncher$")) {
                startupLog("[WestlakeLauncher] installProgrammaticHomeDashboardFragment skipping launcher-owned root");
                return false;
            }
            homeContainer.addView(content, homeParams);
            startupLog("[WestlakeLauncher] buildProgrammaticHomeDashboardFragment created minimal scaffold");
            if (homeContainer.getChildCount() > 0) {
                startupLog("[WestlakeLauncher] Programmatic fragment_home_dashboard installed");
                return true;
            }
            startupLog("[WestlakeLauncher] Programmatic fragment_home_dashboard left home container empty");
            return false;
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] installProgrammaticHomeDashboardFragment", t);
            logThrowableFrames("[WestlakeLauncher] installProgrammaticHomeDashboardFragment", t, 10);
            return false;
        }
    }

    private static int dpPx(android.content.Context context, int dp) {
        float density = 1f;
        try {
            if (context != null && context.getResources() != null
                    && context.getResources().getDisplayMetrics() != null
                    && context.getResources().getDisplayMetrics().density > 0f) {
                density = context.getResources().getDisplayMetrics().density;
            }
        } catch (Throwable ignored) {
        }
        return Math.max(1, (int) (dp * density + 0.5f));
    }

    private static android.view.View buildDashboardCanvasContent(
            final android.content.Context context) {
        android.view.View view = new android.view.View(context) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int desiredWidth = SURFACE_WIDTH;
                int desiredHeight = Math.max(SURFACE_HEIGHT, dpPx(context, 640));
                int measuredWidth = android.view.View.resolveSize(desiredWidth, widthMeasureSpec);
                int measuredHeight = android.view.View.resolveSize(desiredHeight, heightMeasureSpec);
                setMeasuredDimension(measuredWidth, measuredHeight);
            }

            @Override
            protected void onDraw(android.graphics.Canvas canvas) {
                super.onDraw(canvas);
                drawDashboardSkeleton(context, canvas, getWidth(), getHeight());
            }
        };
        view.setMinimumHeight(dpPx(context, 420));
        return view;
    }

    private static float safeDensity(android.content.Context context) {
        try {
            if (context != null
                    && context.getResources() != null
                    && context.getResources().getDisplayMetrics() != null
                    && context.getResources().getDisplayMetrics().density > 0f) {
                return context.getResources().getDisplayMetrics().density;
            }
        } catch (Throwable ignored) {
        }
        return 1f;
    }

    private static boolean isDashboardFallbackInstalled(Activity activity) {
        if (activity == null) {
            return false;
        }
        try {
            synchronized (sInstalledDashboardFallbacks) {
                for (int i = 0; i < sInstalledDashboardFallbacks.size(); i++) {
                    if (sInstalledDashboardFallbacks.get(i) == activity) {
                        return true;
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    private static void markDashboardFallbackInstalled(Activity activity) {
        if (activity == null) {
            return;
        }
        try {
            synchronized (sInstalledDashboardFallbacks) {
                for (int i = 0; i < sInstalledDashboardFallbacks.size(); i++) {
                    if (sInstalledDashboardFallbacks.get(i) == activity) {
                        return;
                    }
                }
                sInstalledDashboardFallbacks.add(activity);
            }
        } catch (Throwable ignored) {
        }
    }

    private static android.view.View safeFindViewById(Activity activity, int id, String label) {
        if (activity == null || id == 0) {
            return null;
        }
        try {
            return activity.findViewById(id);
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] safeFindViewById(" + label + ") error", t);
            return null;
        }
    }

    private static android.view.View safeFindViewById(android.view.View root, int id, String label) {
        if (root == null || id == 0) {
            return null;
        }
        try {
            return root.findViewById(id);
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] safeFindViewById(" + label + ") root error", t);
            return null;
        }
    }

    private static boolean installDashboardViewFallback(Activity activity) {
        if (activity == null) {
            return false;
        }
        try {
            int homeContainerId = resolveAppResourceId(activity, "id", "home_dashboard_container");
            int pageContentId = resolveAppResourceId(activity, "id", "page_content");
            int contentLayoutId = resolveAppResourceId(activity, "layout", "activity_home_dashboard");
            android.view.View installedRoot = null;
            startupLog("[WestlakeLauncher] installDashboardViewFallback ids: home=0x"
                    + Integer.toHexString(homeContainerId) + " page=0x"
                    + Integer.toHexString(pageContentId) + " layout=0x"
                    + Integer.toHexString(contentLayoutId));

            android.view.View containerView =
                    safeFindViewById(activity, homeContainerId, "home_dashboard_container.initial");
            startupLog("[WestlakeLauncher] installDashboardViewFallback initial container="
                    + (containerView != null ? containerView.getClass().getName() : "null"));
            if (false && !(containerView instanceof android.view.ViewGroup) && contentLayoutId != 0) {
                try {
                    startupLog("[WestlakeLauncher] installDashboardViewFallback direct setContentView(activity_home_dashboard)");
                    activity.setContentView(contentLayoutId);
                    containerView = safeFindViewById(activity, homeContainerId,
                            "home_dashboard_container.after_direct");
                    startupLog("[WestlakeLauncher] installDashboardViewFallback post-direct container="
                            + (containerView != null ? containerView.getClass().getName() : "null"));
                } catch (Throwable directSetContentViewError) {
                    startupLog("[WestlakeLauncher] installDashboardViewFallback direct setContentView error",
                            directSetContentViewError);
                    logThrowableFrames("[WestlakeLauncher] installDashboardViewFallback direct setContentView",
                            directSetContentViewError, 12);
                }
            }
            if (!(containerView instanceof android.view.ViewGroup)) {
                startupLog("[WestlakeLauncher] installDashboardViewFallback building root");
                android.view.View root = buildProgrammaticDashboardFallbackRoot(activity);
                if (root != null) {
                    installedRoot = root;
                    startupLog("[WestlakeLauncher] installDashboardViewFallback root class="
                            + root.getClass().getName());
                    startupLog("[WestlakeLauncher] installDashboardViewFallback setContentView begin");
                    activity.setContentView(root);
                    startupLog("[WestlakeLauncher] installDashboardViewFallback setContentView done");
                    startupLog("[WestlakeLauncher] Installed programmatic dashboard root");
                    containerView = safeFindViewById(root, homeContainerId,
                            "home_dashboard_container.after_root");
                    if (!(containerView instanceof android.view.ViewGroup)
                            && root instanceof android.view.ViewGroup) {
                        containerView = root;
                        startupLog("[WestlakeLauncher] installDashboardViewFallback using root as container");
                    }
                    startupLog("[WestlakeLauncher] installDashboardViewFallback post-root container="
                            + (containerView != null ? containerView.getClass().getName() : "null"));
                }
            }
            if (containerView == null
                    && pageContentId != 0
                    && contentLayoutId != 0) {
                android.view.View pageContentView =
                        safeFindViewById(activity, pageContentId, "page_content");
                startupLog("[WestlakeLauncher] installDashboardViewFallback pageContent="
                        + (pageContentView != null ? pageContentView.getClass().getName() : "null"));
                if (pageContentView instanceof android.view.ViewGroup) {
                    android.view.ViewGroup pageContent = (android.view.ViewGroup) pageContentView;
                    if (pageContent.getChildCount() == 0) {
                        startupLog("[WestlakeLauncher] installDashboardViewFallback inflating activity_home_dashboard");
                        activity.getLayoutInflater().inflate(contentLayoutId, pageContent, true);
                        startupLog("[WestlakeLauncher] Inflated activity_home_dashboard into page_content");
                    }
                    containerView = safeFindViewById(activity, homeContainerId,
                            "home_dashboard_container.after_inflate");
                    startupLog("[WestlakeLauncher] installDashboardViewFallback post-inflate container="
                            + (containerView != null ? containerView.getClass().getName() : "null"));
                }
            }
            if (!(containerView instanceof android.view.ViewGroup)) {
                startupLog("[WestlakeLauncher] installDashboardViewFallback no container");
                return false;
            }

            int intermediateId = resolveAppResourceId(activity, "id", "intermediate_layout_container");
            android.view.View intermediate =
                    safeFindViewById(activity, intermediateId, "intermediate_layout_container");
            if (intermediate != null) {
                intermediate.setVisibility(android.view.View.GONE);
            }

            android.view.ViewGroup homeContainer = (android.view.ViewGroup) containerView;
            homeContainer.setVisibility(android.view.View.VISIBLE);

            int fragmentLayoutId = resolveAppResourceId(activity, "layout", "fragment_home_dashboard");
            startupLog("[WestlakeLauncher] installDashboardViewFallback fragmentLayout=0x"
                    + Integer.toHexString(fragmentLayoutId));
            boolean installedProgrammaticFragment = false;
            if (homeContainer.getChildCount() == 0 && fragmentLayoutId != 0) {
                startupLog("[WestlakeLauncher] Skipping direct fragment_home_dashboard inflate");
                installedProgrammaticFragment =
                        installProgrammaticHomeDashboardFragment(activity, homeContainer);
            }

            if (installedProgrammaticFragment && homeContainer.getChildCount() > 0) {
                startupLog("[WestlakeLauncher] installDashboardViewFallback using programmatic dashboard scaffold");
                return true;
            }
            if (installedProgrammaticFragment) {
                startupLog("[WestlakeLauncher] installDashboardViewFallback programmatic scaffold incomplete");
            }

            int sectionsId = resolveAppResourceId(activity, "id", "sections_container");
            android.view.View sectionsView =
                    safeFindViewById(installedRoot, sectionsId, "sections_container.root");
            if (sectionsView == null) {
                sectionsView = safeFindViewById(activity, sectionsId, "sections_container");
            }
            startupLog("[WestlakeLauncher] installDashboardViewFallback sections="
                    + (sectionsView != null ? sectionsView.getClass().getName() : "null"));
            if (installedProgrammaticFragment
                    && sectionsView instanceof android.view.ViewGroup
                    && ((android.view.ViewGroup) sectionsView).getChildCount() > 0) {
                startupLog("[WestlakeLauncher] installDashboardViewFallback using real dashboard scaffold");
                return true;
            }
            if (sectionsView == null && homeContainer.getChildCount() > 0) {
                startupLog("[WestlakeLauncher] installDashboardViewFallback keeping real home container children="
                        + homeContainer.getChildCount());
                return true;
            }
            if (!(sectionsView instanceof android.view.ViewGroup)) {
                sectionsView = homeContainer;
                startupLog("[WestlakeLauncher] installDashboardViewFallback using home container as fallback sections");
            }

            android.view.ViewGroup sections = (android.view.ViewGroup) sectionsView;
            if (sections.getChildCount() > 0) {
                startupLog("[WestlakeLauncher] installDashboardViewFallback keeping existing sections children="
                        + sections.getChildCount());
                return true;
            }
            sections.removeAllViews();
            startupLog("[WestlakeLauncher] installDashboardViewFallback cleared sections");

            if (sections == homeContainer) {
                String sectionClassName = sections.getClass().getName();
                if (sectionClassName != null
                        && sectionClassName.startsWith("com.westlake.engine.WestlakeLauncher$")) {
                    sections.setWillNotDraw(false);
                    startupLog("[WestlakeLauncher] Dashboard fallback using drawable home container root");
                    return true;
                }
            }

            android.content.Context context = activity;
            sections.addView(buildDashboardCanvasContent(context),
                    dashboardCanvasLp());

            startupLog("[WestlakeLauncher] Dashboard fallback injected into real view tree");
            return true;
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] installDashboardViewFallback error", t);
            logThrowableFrames("[WestlakeLauncher] installDashboardViewFallback", t, 12);
            return false;
        }
    }

    private static boolean shouldUseTextOnlyDashboardMenu(Activity activity) {
        try {
            String prop = System.getProperty("westlake.text_menu_only");
            if (prop != null && ("1".equals(prop) || "true".equalsIgnoreCase(prop))) {
                return activity != null;
            }
        } catch (Throwable ignored) {
        }
        try {
            String env = System.getenv("WESTLAKE_TEXT_MENU_ONLY");
            if (env != null && ("1".equals(env) || "true".equalsIgnoreCase(env))) {
                return activity != null;
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    private static void drawTextOnlyDashboardMenu() {
        if (!OHBridge.isNativeAvailable()) {
            return;
        }
        long surf = OHBridge.surfaceCreate(0, SURFACE_WIDTH, SURFACE_HEIGHT);
        long canv = OHBridge.surfaceGetCanvas(surf);
        long font = OHBridge.fontCreate();
        long pen = OHBridge.penCreate();
        long brush = OHBridge.brushCreate();

        OHBridge.canvasDrawColor(canv, 0xFFF5F5F5);

        OHBridge.fontSetSize(font, 30);
        OHBridge.penSetColor(pen, 0xFFDA291C);
        OHBridge.canvasDrawText(canv, "McDonald's", 36, 90, font, pen, brush);

        OHBridge.fontSetSize(font, 18);
        OHBridge.penSetColor(pen, 0xFF333333);
        OHBridge.canvasDrawText(canv, "Text-only menu", 36, 130, font, pen, brush);

        OHBridge.fontSetSize(font, 20);
        OHBridge.penSetColor(pen, 0xFF111111);
        OHBridge.canvasDrawText(canv, "Big Mac Combo        $5.99", 36, 210, font, pen, brush);
        OHBridge.canvasDrawText(canv, "2 for $6 Mix & Match", 36, 260, font, pen, brush);
        OHBridge.canvasDrawText(canv, "Free Medium Fries    Reward", 36, 310, font, pen, brush);
        OHBridge.canvasDrawText(canv, "10 pc McNuggets      $6.79", 36, 360, font, pen, brush);
        OHBridge.canvasDrawText(canv, "Quarter Pounder      $6.49", 36, 410, font, pen, brush);
        OHBridge.canvasDrawText(canv, "McFlurry OREO        $3.49", 36, 460, font, pen, brush);

        OHBridge.fontSetSize(font, 16);
        OHBridge.penSetColor(pen, 0xFF666666);
        OHBridge.canvasDrawText(canv, "Home   Deals   Order   Rewards   More", 24, 730, font, pen, brush);

        OHBridge.surfaceFlush(surf);
        sDirectDashboardFallbackActive = true;
        startupLog("[WestlakeLauncher] Text-only dashboard menu drawn via OHBridge");
    }

    private static final String[] TOUCH_PATHS_FALLBACK = {
        "/data/local/tmp/a2oh/touch.dat",
        "/sdcard/westlake_touch.dat"
    };

    /**
     * Build a visible McDonald's dashboard UI on the activity's content view.
     * Uses the real base_layout container and adds mock menu content.
     */
    public static void populateDashboardFallback(Activity activity) {
        if (isDashboardFallbackInstalled(activity)) {
            startupLog("[WestlakeLauncher] Dashboard fallback already installed for "
                    + activity.getClass().getName());
            return;
        }
        sDirectDashboardFallbackActive = false;
        try {
            if (shouldUseTextOnlyDashboardMenu(activity)) {
                drawTextOnlyDashboardMenu();
                markDashboardFallbackInstalled(activity);
                return;
            }
            if (installDashboardViewFallback(activity)) {
                markDashboardFallbackInstalled(activity);
                startupLog("[WestlakeLauncher] Dashboard fallback UI populated in container");
                return;
            }
            if (activity == null || !OHBridge.isNativeAvailable()) {
                return;
            }
            long surf = OHBridge.surfaceCreate(0, SURFACE_WIDTH, SURFACE_HEIGHT);
            long canv = OHBridge.surfaceGetCanvas(surf);
            long font = OHBridge.fontCreate();
            long pen = OHBridge.penCreate();
            long brush = OHBridge.brushCreate();

            OHBridge.canvasDrawColor(canv, 0xFFF5F5F5);

            OHBridge.fontSetSize(font, 30);
            OHBridge.penSetColor(pen, 0xFFDA291C);
            OHBridge.canvasDrawText(canv, "McDonald's", 36, 90, font, pen, brush);

            OHBridge.fontSetSize(font, 18);
            OHBridge.penSetColor(pen, 0xFF333333);
            OHBridge.canvasDrawText(canv, "Dashboard fallback", 36, 130, font, pen, brush);

            OHBridge.fontSetSize(font, 20);
            OHBridge.penSetColor(pen, 0xFF111111);
            OHBridge.canvasDrawText(canv, "Big Mac Combo        $5.99", 36, 220, font, pen, brush);
            OHBridge.canvasDrawText(canv, "2 for $6 Mix & Match", 36, 275, font, pen, brush);
            OHBridge.canvasDrawText(canv, "Free Medium Fries", 36, 330, font, pen, brush);
            OHBridge.canvasDrawText(canv, "McFlurry OREO        $3.49", 36, 385, font, pen, brush);

            OHBridge.fontSetSize(font, 16);
            OHBridge.penSetColor(pen, 0xFF666666);
            OHBridge.canvasDrawText(canv, "Home   Deals   Order   Rewards   More", 24, 730, font, pen, brush);

            OHBridge.surfaceFlush(surf);
            sDirectDashboardFallbackActive = true;
            markDashboardFallbackInstalled(activity);
            startupLog("[WestlakeLauncher] Dashboard fallback drawn via OHBridge");
        } catch (Throwable t) {
            sDirectDashboardFallbackActive = false;
            try { nativePrintException(t); } catch (Throwable ignored) {}
            startupLog("[WestlakeLauncher] populateDashboardFallback error", t);
        }
    }

    private static void renderLoop(Activity initialActivity, MiniActivityManager am) {
        if (android.app.WestlakeActivityThread.pendingDashboardClass != null) {
            String dashClass = android.app.WestlakeActivityThread.pendingDashboardClass;
            android.app.WestlakeActivityThread.pendingDashboardClass = null;
            startupLog("[WestlakeLauncher] Launching pending dashboard: " + dashClass);
            startupLog("[WestlakeLauncher] renderLoop initial before dashboard="
                    + (initialActivity != null ? initialActivity.getClass().getName() : "null"));
            Activity dash = null;
            try {
                android.app.WestlakeActivityThread wat = android.app.WestlakeActivityThread.currentActivityThread();
                Intent dashIntent = new Intent();
                dashIntent.setComponent(new ComponentName("com.mcdonalds.app", dashClass));
                dash = android.app.WestlakeActivityThread.launchActivity(
                        wat, dashClass, "com.mcdonalds.app", dashIntent);
            } catch (Throwable t) {
                startupLog("[WestlakeLauncher] Dashboard launchActivity error", t);
                logThrowableFrames("[WestlakeLauncher] Dashboard launchActivity", t, 12);
            }
            if (dash != null) {
                try {
                    Activity previous = initialActivity;
                    initialActivity = dash;
                    startupLog("[WestlakeLauncher] Dashboard active: " + dash.getClass().getName());
                    Activity resumed = null;
                    try { resumed = am.getResumedActivity(); } catch (Throwable ignored) {}
                    startupLog("[WestlakeLauncher] Dashboard resumed after launch="
                            + (resumed != null ? resumed.getClass().getName() : "null"));
                    boolean fallbackAlreadyInstalled = false;
                    try {
                        fallbackAlreadyInstalled = isDashboardFallbackInstalled(dash);
                    } catch (Throwable installedLookupError) {
                        startupLog("[WestlakeLauncher] Dashboard fallback lookup error", installedLookupError);
                        logThrowableFrames("[WestlakeLauncher] Dashboard fallback lookup",
                                installedLookupError, 8);
                    }
                    if (!fallbackAlreadyInstalled) {
                        try {
                            populateDashboardFallback(dash);
                        } catch (Throwable fallbackError) {
                            startupLog("[WestlakeLauncher] Dashboard fallback install error", fallbackError);
                            logThrowableFrames("[WestlakeLauncher] Dashboard fallback install",
                                    fallbackError, 8);
                        }
                    } else {
                        startupLog("[WestlakeLauncher] Dashboard fallback already present before render loop");
                    }
                    startupLog("[WestlakeLauncher] Dashboard fallback handoff ready");
                    // Let the normal render loop drive the first dashboard frame.
                    // The older eager render/demo path was crashing inside the
                    // guest before the real view tree had stabilized.
                    splashImageData = null;
                    if (previous != null && previous != dash) {
                        try {
                            previous.onSurfaceDestroyed();
                            startupLog("[WestlakeLauncher] Previous activity surface destroyed");
                        } catch (Throwable previousDestroyError) {
                            startupLog("[WestlakeLauncher] Previous surface destroy error",
                                    previousDestroyError);
                        }
                    }
                    startupLog("[WestlakeLauncher] Dashboard pre-first-frame direct="
                            + sDirectDashboardFallbackActive);
                    if (!sDirectDashboardFallbackActive) {
                        try {
                            dash.onSurfaceCreated(0L, SURFACE_WIDTH, SURFACE_HEIGHT);
                            dash.renderFrame();
                            startupLog("[WestlakeLauncher] Dashboard first frame requested");
                        } catch (Throwable frameError) {
                            startupLog("[WestlakeLauncher] Dashboard first render error", frameError);
                            logThrowableFrames("[WestlakeLauncher] Dashboard first render", frameError, 8);
                        }
                    } else {
                        startupLog("[WestlakeLauncher] Dashboard frame is direct OHBridge fallback");
                    }
                } catch (Throwable t) {
                    startupLog("[WestlakeLauncher] Dashboard handoff error", t);
                    logThrowableFrames("[WestlakeLauncher] Dashboard handoff", t, 12);
                }
            }
        }

        if (sDirectDashboardFallbackActive) {
            startupLog("[WestlakeLauncher] Direct dashboard fallback active");
            while (true) {
                try { Thread.sleep(1000); } catch (InterruptedException e) { break; }
            }
            return;
        }

        // Check if Activity has our shim methods (onSurfaceCreated/renderFrame)
        boolean hasShimMethods = true;
        try { initialActivity.getClass().getMethod("onSurfaceCreated", long.class, int.class, int.class); }
        catch (NoSuchMethodException e) { hasShimMethods = false; }
        if (initialActivity != null) {
            startupLog("[WestlakeLauncher] renderLoop activity="
                    + initialActivity.getClass().getName()
                    + (hasShimMethods ? " shim=yes" : " shim=no"));
        } else {
            startupLog("[WestlakeLauncher] renderLoop activity=null");
        }

        if (initialActivity != null && hasShimMethods) {
            try {
                String initialName = initialActivity.getClass().getName();
                if (initialName != null && initialName.toLowerCase().contains("dashboard")) {
                    splashImageData = null;
                }
            } catch (Throwable ignored) {
            }
            try {
                initialActivity.onSurfaceDestroyed();
            } catch (Throwable ignored) {
            }
            try {
                initialActivity.onSurfaceCreated(0L, SURFACE_WIDTH, SURFACE_HEIGHT);
                startupLog("[WestlakeLauncher] renderLoop surface primed");
            } catch (Throwable surfaceError) {
                startupLog("[WestlakeLauncher] renderLoop surface prime error", surfaceError);
            }
        }

        if (!hasShimMethods) {
            startupLog("[WestlakeLauncher] Framework Activity — OHBridge-only render loop");
            // Simple keep-alive loop: the splash was already sent via OHBridge
            // Touch events can still be processed
            while (true) {
                try { Thread.sleep(1000); } catch (InterruptedException e) { break; }
            }
            return;
        }

        long frameCount = 0;
        int lastTouchSeq = -1;
        int lastTextSize = -1;
        int lastTextHash = 0;

        // Prefer WESTLAKE_TOUCH env var (set by Compose host)
        String envTouch = System.getenv("WESTLAKE_TOUCH");
        String touchPath = null;
        if (envTouch != null && !envTouch.isEmpty()) {
            touchPath = envTouch;
            startupLog("[WestlakeLauncher] Touch file (env): " + envTouch);
        } else {
            touchPath = TOUCH_PATHS_FALLBACK[0];
            startupLog("[WestlakeLauncher] Touch file (default): " + touchPath);
        }
        String touchDir = parentPath(touchPath);
        String textPath = joinPath(touchDir, "westlake_text.dat");

        // After splash: if we have real icons from app_process64, render them
        if (realIconsPng != null && com.ohos.shim.bridge.OHBridge.isNativeAvailable()) {
            try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
            try {
                long surf = com.ohos.shim.bridge.OHBridge.surfaceCreate(0, SURFACE_WIDTH, SURFACE_HEIGHT);
                long canv = com.ohos.shim.bridge.OHBridge.surfaceGetCanvas(surf);
                com.ohos.shim.bridge.OHBridge.canvasDrawImage(canv, realIconsPng, 0, 0, SURFACE_WIDTH, SURFACE_HEIGHT);
                com.ohos.shim.bridge.OHBridge.surfaceFlush(surf);
                startupLog("[WestlakeLauncher] Real icons frame rendered! (" + realIconsPng.length + " bytes)");
            } catch (Throwable t) {
                startupLog("[WestlakeLauncher] Real icons render error", t);
            }
        }

        boolean needsRender = (initialActivity != null);
        long downTime = 0;
        int lastTouchY = 0;
        int scrollOffset = 0;
        int totalDragDistance = 0;
        android.view.View lastDecorView = null;

        Activity current = initialActivity; // prefer WAT-created activity
        if (current == null) current = am.getResumedActivity();
        startupLog("[WestlakeLauncher] renderLoop current="
                + (current != null ? current.getClass().getName() : "null"));

        while (true) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                break;
            }
            if (current == null) {
                startupLog("[WestlakeLauncher] No resumed activity, exiting");
                break;
            }

            // Check for text input from host (long-press dialog)
            byte[] textBuf = canOpenFile(textPath) ? tryReadFileBytes(textPath) : null;
            if (textBuf != null && textBuf.length > 0) {
                try {
                    int textHash = java.util.Arrays.hashCode(textBuf);
                    if (textBuf.length != lastTextSize || textHash != lastTextHash) {
                        lastTextSize = textBuf.length;
                        lastTextHash = textHash;
                        String inputText = new String(textBuf, "UTF-8").trim();
                        if (inputText.length() > 0) {
                            android.view.View decor = null;
                            try { decor = current.getWindow().getDecorView(); } catch (Exception e5) {}
                            if (decor != null) {
                                android.widget.EditText et = findEditText(decor);
                                if (et != null) {
                                    et.setText(inputText);
                                    startupLog("[WestlakeLauncher] Text input: '" + inputText + "' -> " + et);
                                    needsRender = true;
                                } else {
                                    startupLog("[WestlakeLauncher] Text input: no EditText found");
                                }
                            }
                        }
                    }
                } catch (Exception e5) {
                    startupLog("[WestlakeLauncher] Text input error: " + e5);
                }
            } else {
                lastTextSize = -1;
                lastTextHash = 0;
            }

            // Check for touch events
            byte[] buf = canOpenFile(touchPath) ? tryReadFileBytes(touchPath) : null;
            if (buf != null && buf.length >= 16) {
                try {
                    java.nio.ByteBuffer bb = java.nio.ByteBuffer.wrap(buf, 0, 16);
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
                            startupLog("[WestlakeLauncher] Touch DOWN at (" + x + "," + y + ")");
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
                            startupLog("[WestlakeLauncher] Touch UP at (" + x + "," + y + ")");
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
                                                    startupLog("[WestlakeLauncher] ListView item " + pos + " clicked");
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
                    }
                } catch (Exception e) {
                    startupLog("[WestlakeLauncher] touch read error: " + e);
                }
            }

            if (needsRender) {
                Activity next = current;
                Activity resumed = null;
                try { resumed = am.getResumedActivity(); } catch (Throwable ignored) {}
                if (resumed != null) {
                    next = resumed;
                }
                if (next != null) {
                    if (next != current) {
                        try { next.onSurfaceCreated(0L, SURFACE_WIDTH, SURFACE_HEIGHT); } catch (Exception e2) {}
                        startupLog("[WestlakeLauncher] Navigated to " + next.getClass().getSimpleName());
                        current = next;
                    }
                    try { current.renderFrame(); } catch (Exception e2) {
                        if (frameCount < 5) startupLog("[WestlakeLauncher] renderFrame error", e2);
                        if (frameCount < 2) {
                            logThrowableFrames("[WestlakeLauncher] renderFrame", e2, 12);
                        }
                    }
                }
                needsRender = false;
            }

            frameCount++;
        }
        startupLog("[WestlakeLauncher] Render loop ended");
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
        startupLog("[WestlakeLauncher] Found " + sections.size() + " sections to fill");

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
            startupLog("[WestlakeLauncher] Real icons: " + png.length + " bytes, " + found + " icons");

            if (com.ohos.shim.bridge.OHBridge.isNativeAvailable()) {
                long surf = com.ohos.shim.bridge.OHBridge.surfaceCreate(0, SURFACE_WIDTH, SURFACE_HEIGHT);
                long canv = com.ohos.shim.bridge.OHBridge.surfaceGetCanvas(surf);
                com.ohos.shim.bridge.OHBridge.canvasDrawImage(canv, png, 0, 0, SURFACE_WIDTH, SURFACE_HEIGHT);
                com.ohos.shim.bridge.OHBridge.surfaceFlush(surf);
                startupLog("[WestlakeLauncher] Real icons frame sent!");
            }
            bmp.recycle();
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] renderRealIconsScreen error", t);
        }
    }

    private static android.graphics.Paint newPaint(int color) {
        android.graphics.Paint p = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        p.setColor(color);
        return p;
    }
}
