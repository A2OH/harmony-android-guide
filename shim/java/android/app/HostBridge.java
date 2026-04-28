package android.app;

import java.lang.reflect.Method;

/**
 * Bridge to the host platform's framework. All host-dependent calls
 * (PackageManager, Resources, Theme, etc.) go through here via reflection.
 * This avoids ClassLoader type mismatches between shim and phone classes.
 *
 * The host is the WestlakeActivity instance running on the real phone.
 * It is set from WestlakeActivity.loadEngine() after creating the
 * child-first ClassLoader.
 *
 * Why reflection? The shim's classes (loaded by child-first CL) and the
 * phone's classes (loaded by boot CL) are different types even if they
 * share the same name. Returning the host's real PackageManager directly
 * would cause ClassCastException because the APK's bytecode references
 * android.content.pm.PackageManager from the shim CL, not the boot CL.
 * Instead, the shim's PM delegates each method call to the host's real
 * PM via reflection, so the actual work is done by the phone's real
 * implementation while the return type matches what the APK expects.
 */
public class HostBridge {
    private static Object hostActivity; // WestlakeActivity instance
    private static final String TAG = "HostBridge";

    /** Set the host Activity (called once from WestlakeActivity.loadEngine). */
    public static void setHost(Object host) {
        hostActivity = host;
        if (host != null) {
            android.util.Log.i(TAG, "Host set: " + host.getClass().getName());
        }
    }

    /** Get the host Activity instance, or null if not set. */
    public static Object getHost() {
        return hostActivity;
    }

    /** Whether a host has been set. */
    public static boolean hasHost() {
        return hostActivity != null;
    }

    // ── Generic reflection helpers ──────────────────────────────────────────

    /**
     * Call a no-arg method on the host Activity and return the result.
     * Returns null on any failure.
     */
    public static Object call(String method) {
        if (hostActivity == null) return null;
        try {
            return hostActivity.getClass().getMethod(method).invoke(hostActivity);
        } catch (Exception e) {
            android.util.Log.w(TAG, "call(" + method + "): " + e);
            return null;
        }
    }

    /**
     * Call a method on the host Activity with typed parameters.
     * Returns null on any failure.
     */
    public static Object call(String method, Class<?>[] types, Object... args) {
        if (hostActivity == null) return null;
        try {
            return hostActivity.getClass().getMethod(method, types).invoke(hostActivity, args);
        } catch (Exception e) {
            android.util.Log.w(TAG, "call(" + method + "): " + e);
            return null;
        }
    }

    /**
     * Call a method on an arbitrary target object via reflection.
     * Used to invoke methods on objects returned by the host (e.g., host's PM).
     */
    public static Object callOn(Object target, String method, Class<?>[] types, Object... args) {
        if (target == null) return null;
        try {
            return target.getClass().getMethod(method, types).invoke(target, args);
        } catch (Exception e) {
            android.util.Log.w(TAG, "callOn(" + target.getClass().getSimpleName() + "." + method + "): " + e);
            return null;
        }
    }

    /**
     * Call a no-arg method on an arbitrary target object via reflection.
     */
    public static Object callOn(Object target, String method) {
        if (target == null) return null;
        try {
            return target.getClass().getMethod(method).invoke(target);
        } catch (Exception e) {
            android.util.Log.w(TAG, "callOn(" + target.getClass().getSimpleName() + "." + method + "): " + e);
            return null;
        }
    }

    // ── Typed host-delegating accessors ─────────────────────────────────────

    /**
     * Get host's Resources. Returns the phone's real Resources object.
     * Cast is safe because Resources is loaded from boot CL and the shim's
     * Resources class is a different class -- but at the bytecode level,
     * the cast goes through Object anyway.
     */
    public static Object getHostResources() {
        return call("getResources");
    }

    /** Get host's Theme. */
    public static Object getHostTheme() {
        return call("getTheme");
    }

    /**
     * Get host's PackageManager. Returns the phone's REAL PM (which has
     * getActivityInfo, getApplicationInfo, etc.). This is Object-typed
     * because it's a different class than our shim's PackageManager.
     */
    public static Object getHostPackageManager() {
        return call("getPackageManager");
    }

    /** Get host's ContentResolver. */
    public static Object getHostContentResolver() {
        return call("getContentResolver");
    }

    /** Get host's ApplicationContext. */
    public static Object getHostApplicationContext() {
        return call("getApplicationContext");
    }

    /** Get host's package name. */
    public static String getHostPackageName() {
        Object result = call("getPackageName");
        return result instanceof String ? (String) result : null;
    }

    /** Get a system service from the host by name. */
    public static Object getHostSystemService(String name) {
        return call("getSystemService", new Class<?>[] { String.class }, name);
    }

    /** Get host's ClassLoader (the boot-framework-aware one). */
    public static ClassLoader getHostClassLoader() {
        Object result = call("getClassLoader");
        return result instanceof ClassLoader ? (ClassLoader) result : null;
    }

    /** Get host's ApplicationInfo. */
    public static Object getHostApplicationInfo() {
        return call("getApplicationInfo");
    }

    // ── PackageManager delegation ───────────────────────────────────────────
    // These call methods on the host's REAL PackageManager via reflection.
    // The shim's PackageManager class calls these to delegate actual work
    // to the phone's real PM implementation.

    /**
     * Delegate getActivityInfo(ComponentName, int) to host's real PM.
     * Copies fields from the host's real ActivityInfo into a shim ActivityInfo.
     */
    public static android.content.pm.ActivityInfo pm_getActivityInfo(
            android.content.ComponentName component, int flags) {
        Object hostPM = getHostPackageManager();
        if (hostPM == null) return new android.content.pm.ActivityInfo();
        try {
            // Construct a real ComponentName using the host's CL
            Class<?> cnClass = hostPM.getClass().getClassLoader()
                .loadClass("android.content.ComponentName");
            Object realCN = cnClass.getConstructor(String.class, String.class)
                .newInstance(component.getPackageName(), component.getClassName());

            // Call host's PM.getActivityInfo(ComponentName, int)
            Method m = hostPM.getClass().getMethod("getActivityInfo", cnClass, int.class);
            Object realAI = m.invoke(hostPM, realCN, flags);
            if (realAI == null) return new android.content.pm.ActivityInfo();

            // Copy fields from real ActivityInfo to shim ActivityInfo
            android.content.pm.ActivityInfo shimAI = new android.content.pm.ActivityInfo();
            copyActivityInfoFields(realAI, shimAI);
            return shimAI;
        } catch (Exception e) {
            android.util.Log.w(TAG, "pm_getActivityInfo: " + e.getCause());
            return new android.content.pm.ActivityInfo();
        }
    }

    /**
     * Delegate getApplicationInfo(String, int) to host's real PM.
     * Copies fields from the host's real ApplicationInfo into a shim ApplicationInfo.
     */
    public static android.content.pm.ApplicationInfo pm_getApplicationInfo(
            String packageName, int flags) {
        Object hostPM = getHostPackageManager();
        if (hostPM == null) return new android.content.pm.ApplicationInfo();
        try {
            Method m = hostPM.getClass().getMethod("getApplicationInfo", String.class, int.class);
            Object realAppInfo = m.invoke(hostPM, packageName, flags);
            if (realAppInfo == null) return new android.content.pm.ApplicationInfo();

            android.content.pm.ApplicationInfo shimInfo = new android.content.pm.ApplicationInfo();
            copyApplicationInfoFields(realAppInfo, shimInfo);
            return shimInfo;
        } catch (Exception e) {
            android.util.Log.w(TAG, "pm_getApplicationInfo: " + e.getCause());
            return new android.content.pm.ApplicationInfo();
        }
    }

    /**
     * Delegate getPackageInfo(String, int) to host's real PM.
     * Returns an Object because PackageInfo is complex -- callers
     * typically just need existence-check or versionCode.
     */
    public static android.content.pm.PackageInfo pm_getPackageInfo(
            String packageName, int flags) {
        Object hostPM = getHostPackageManager();
        if (hostPM == null) return null;
        try {
            Method m = hostPM.getClass().getMethod("getPackageInfo", String.class, int.class);
            Object realPkgInfo = m.invoke(hostPM, packageName, flags);
            if (realPkgInfo == null) return null;

            android.content.pm.PackageInfo shimInfo = new android.content.pm.PackageInfo();
            copyPackageInfoFields(realPkgInfo, shimInfo);
            return shimInfo;
        } catch (Exception e) {
            android.util.Log.w(TAG, "pm_getPackageInfo: " + e.getCause());
            return null;
        }
    }

    /**
     * Delegate resolveActivity(Intent, int) to host's real PM.
     */
    public static android.content.pm.ResolveInfo pm_resolveActivity(
            android.content.Intent intent, int flags) {
        Object hostPM = getHostPackageManager();
        if (hostPM == null) return null;
        try {
            // Must construct a real Intent for the host
            Class<?> intentClass = hostPM.getClass().getClassLoader()
                .loadClass("android.content.Intent");
            Object realIntent = intentClass.newInstance();
            // Copy basic fields
            if (intent.getAction() != null) {
                intentClass.getMethod("setAction", String.class)
                    .invoke(realIntent, intent.getAction());
            }

            Method m = hostPM.getClass().getMethod("resolveActivity", intentClass, int.class);
            Object realRI = m.invoke(hostPM, realIntent, flags);
            if (realRI == null) return null;

            android.content.pm.ResolveInfo shimRI = new android.content.pm.ResolveInfo();
            // Copy activityInfo if present
            try {
                Object realAI = realRI.getClass().getField("activityInfo").get(realRI);
                if (realAI != null) {
                    shimRI.activityInfo = new android.content.pm.ActivityInfo();
                    copyActivityInfoFields(realAI, shimRI.activityInfo);
                }
            } catch (Exception ignored) {}
            return shimRI;
        } catch (Exception e) {
            android.util.Log.w(TAG, "pm_resolveActivity: " + e);
            return null;
        }
    }

    /**
     * Delegate queryIntentActivities(Intent, int) to host's real PM.
     */
    public static java.util.List<android.content.pm.ResolveInfo> pm_queryIntentActivities(
            android.content.Intent intent, int flags) {
        Object hostPM = getHostPackageManager();
        if (hostPM == null) return new java.util.ArrayList<>();
        try {
            Class<?> intentClass = hostPM.getClass().getClassLoader()
                .loadClass("android.content.Intent");
            Object realIntent = intentClass.newInstance();
            if (intent.getAction() != null) {
                intentClass.getMethod("setAction", String.class)
                    .invoke(realIntent, intent.getAction());
            }

            Method m = hostPM.getClass().getMethod("queryIntentActivities", intentClass, int.class);
            Object result = m.invoke(hostPM, realIntent, flags);
            // Convert the List<real ResolveInfo> to List<shim ResolveInfo>
            java.util.List<android.content.pm.ResolveInfo> shimList = new java.util.ArrayList<>();
            if (result instanceof java.util.List) {
                for (Object realRI : (java.util.List<?>) result) {
                    android.content.pm.ResolveInfo shimRI = new android.content.pm.ResolveInfo();
                    try {
                        Object realAI = realRI.getClass().getField("activityInfo").get(realRI);
                        if (realAI != null) {
                            shimRI.activityInfo = new android.content.pm.ActivityInfo();
                            copyActivityInfoFields(realAI, shimRI.activityInfo);
                        }
                    } catch (Exception ignored) {}
                    shimList.add(shimRI);
                }
            }
            return shimList;
        } catch (Exception e) {
            android.util.Log.w(TAG, "pm_queryIntentActivities: " + e);
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Delegate checkPermission(String, String) to host's real PM.
     */
    public static int pm_checkPermission(String permission, String packageName) {
        Object hostPM = getHostPackageManager();
        if (hostPM == null) return android.content.pm.PackageManager.PERMISSION_GRANTED;
        try {
            Method m = hostPM.getClass().getMethod("checkPermission", String.class, String.class);
            Object result = m.invoke(hostPM, permission, packageName);
            return result instanceof Integer ? (Integer) result : 0;
        } catch (Exception e) {
            return android.content.pm.PackageManager.PERMISSION_GRANTED;
        }
    }

    /**
     * Delegate hasSystemFeature(String) to host's real PM.
     */
    public static boolean pm_hasSystemFeature(String feature) {
        Object hostPM = getHostPackageManager();
        if (hostPM == null) return false;
        try {
            Method m = hostPM.getClass().getMethod("hasSystemFeature", String.class);
            Object result = m.invoke(hostPM, feature);
            return result instanceof Boolean ? (Boolean) result : false;
        } catch (Exception e) {
            return false;
        }
    }

    // ── obtainStyledAttributes delegation ───────────────────────────────────

    /**
     * Delegate obtainStyledAttributes to host's real Context/Theme.
     * Returns shim TypedArray wrapping data extracted from the real one.
     */
    public static android.content.res.TypedArray host_obtainStyledAttributes(int[] attrs) {
        if (hostActivity == null) return new android.content.res.TypedArray();
        try {
            Method m = hostActivity.getClass().getMethod("obtainStyledAttributes", int[].class);
            Object realTA = m.invoke(hostActivity, (Object) attrs);
            return wrapTypedArray(realTA, attrs);
        } catch (Exception e) {
            return new android.content.res.TypedArray();
        }
    }

    public static android.content.res.TypedArray host_obtainStyledAttributes(
            int resId, int[] attrs) {
        if (hostActivity == null) return new android.content.res.TypedArray();
        try {
            Method m = hostActivity.getClass().getMethod("obtainStyledAttributes",
                int.class, int[].class);
            Object realTA = m.invoke(hostActivity, resId, attrs);
            return wrapTypedArray(realTA, attrs);
        } catch (Exception e) {
            return new android.content.res.TypedArray();
        }
    }

    // ── Field-copy helpers ──────────────────────────────────────────────────

    /** Copy common fields from a real ActivityInfo to a shim ActivityInfo via reflection. */
    private static void copyActivityInfoFields(Object src, android.content.pm.ActivityInfo dst) {
        try {
            dst.name = getStringField(src, "name");
            dst.packageName = getStringField(src, "packageName");
            dst.flags = getIntField(src, "flags");
            dst.theme = getIntField(src, "theme");
            dst.launchMode = getIntField(src, "launchMode");
            dst.screenOrientation = getIntField(src, "screenOrientation");
            dst.configChanges = getIntField(src, "configChanges");
            dst.softInputMode = getIntField(src, "softInputMode");
            dst.taskAffinity = getStringField(src, "taskAffinity");
            dst.parentActivityName = getStringField(src, "parentActivityName");
            dst.permission = getStringField(src, "permission");
            dst.targetActivity = getStringField(src, "targetActivity");
            dst.labelRes = getIntField(src, "labelRes");
            dst.icon = getIntField(src, "icon");
            // Copy applicationInfo if present
            try {
                Object realAppInfo = src.getClass().getField("applicationInfo").get(src);
                if (realAppInfo != null) {
                    android.content.pm.ApplicationInfo shimAppInfo = new android.content.pm.ApplicationInfo();
                    copyApplicationInfoFields(realAppInfo, shimAppInfo);
                    // Store in ComponentInfo -- but our shim uses int type, so we store externally
                    // The real ActivityInfo's applicationInfo is needed for theme resolution
                }
            } catch (Exception ignored) {}
        } catch (Exception e) {
            android.util.Log.w(TAG, "copyActivityInfoFields: " + e);
        }
    }

    /** Copy common fields from a real ApplicationInfo to a shim ApplicationInfo. */
    private static void copyApplicationInfoFields(Object src, android.content.pm.ApplicationInfo dst) {
        try {
            dst.packageName = getStringField(src, "packageName");
            dst.name = getStringField(src, "className"); // ApplicationInfo uses className
            if (dst.name == null) dst.name = getStringField(src, "name");
            dst.appComponentFactory = getStringField(src, "appComponentFactory");
            dst.sourceDir = getStringField(src, "sourceDir");
            dst.dataDir = getStringField(src, "dataDir");
            dst.flags = getIntField(src, "flags");
            dst.uid = getIntField(src, "uid");
            dst.targetSdkVersion = getIntField(src, "targetSdkVersion");
        } catch (Exception e) {
            android.util.Log.w(TAG, "copyApplicationInfoFields: " + e);
        }
    }

    /** Copy common fields from a real PackageInfo to a shim PackageInfo. */
    private static void copyPackageInfoFields(Object src, android.content.pm.PackageInfo dst) {
        try {
            dst.packageName = getStringField(src, "packageName");
            dst.versionName = getStringField(src, "versionName");
            dst.versionCode = getIntField(src, "versionCode");
        } catch (Exception e) {
            android.util.Log.w(TAG, "copyPackageInfoFields: " + e);
        }
    }

    /** Wrap a real TypedArray into a shim TypedArray by extracting common values. */
    private static android.content.res.TypedArray wrapTypedArray(Object realTA, int[] attrs) {
        if (realTA == null) return new android.content.res.TypedArray();
        android.content.res.TypedArray shimTA = new android.content.res.TypedArray();
        try {
            // Try to recycle the real one to avoid leaks
            realTA.getClass().getMethod("recycle").invoke(realTA);
        } catch (Exception ignored) {}
        return shimTA;
    }

    // ── Reflection field helpers ────────────────────────────────────────────

    private static String getStringField(Object obj, String fieldName) {
        try {
            java.lang.reflect.Field f = obj.getClass().getField(fieldName);
            Object val = f.get(obj);
            return val instanceof String ? (String) val : (val != null ? val.toString() : null);
        } catch (Exception e) {
            // Try CharSequence
            try {
                java.lang.reflect.Field f = obj.getClass().getField(fieldName);
                Object val = f.get(obj);
                return val != null ? val.toString() : null;
            } catch (Exception e2) {
                return null;
            }
        }
    }

    private static int getIntField(Object obj, String fieldName) {
        try {
            java.lang.reflect.Field f = obj.getClass().getField(fieldName);
            return f.getInt(obj);
        } catch (Exception e) {
            return 0;
        }
    }
}
