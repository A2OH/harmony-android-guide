package android.app;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * ShimCompat — reflection-based helpers for calling shim-only methods/fields safely.
 *
 * When running on a real Android phone, classes in android.* resolve to the phone's
 * framework (boot classloader), NOT our shim. Methods like Application.setPackageName(),
 * Resources.loadResourceTable(), AssetManager.setAssetDir(), and fields like
 * Activity.mIntent don't exist in the real framework. Direct calls throw
 * NoSuchMethodError / NoSuchFieldError.
 *
 * These helpers use reflection with try/catch so the code degrades gracefully on ART.
 */
public class ShimCompat {

    private static final String TAG = "ShimCompat";
    private static Boolean sIsRealAndroid = null;

    /**
     * Detect if we are running on a real Android framework (ART) vs our shim.
     * On a real phone, android.app.Application will NOT have setPackageName().
     */
    static boolean isRealAndroid() {
        if (sIsRealAndroid == null) {
            try {
                Application.class.getDeclaredMethod("setPackageName", String.class);
                sIsRealAndroid = false; // shim — method exists
            } catch (NoSuchMethodException e) {
                sIsRealAndroid = true;  // real Android — method missing
            }
        }
        return sIsRealAndroid;
    }

    /**
     * Call Application.setPackageName(pkg) if available (shim), otherwise no-op.
     */
    public static void setPackageName(Application app, String pkg) {
        try {
            Method m = null;
            for (Class<?> c = app.getClass(); c != null; c = c.getSuperclass()) {
                try {
                    m = c.getDeclaredMethod("setPackageName", String.class);
                    break;
                } catch (NoSuchMethodException ignored) {
                }
            }
            if (m == null) {
                throw new NoSuchMethodException("setPackageName");
            }
            m.setAccessible(true);
            m.invoke(app, pkg);
        } catch (Exception e) {
            // On real Android, Application doesn't have setPackageName — ignore
            log("setPackageName not available (real Android?): " + e.getClass().getSimpleName());
        }
    }

    /**
     * Call Resources.loadResourceTable(table) if available (shim), otherwise no-op.
     */
    public static void loadResourceTable(android.content.res.Resources res, Object table) {
        try {
            Method m = res.getClass().getMethod("loadResourceTable",
                    android.content.res.ResourceTable.class);
            m.setAccessible(true);
            m.invoke(res, table);
        } catch (Exception e) {
            log("loadResourceTable not available: " + e.getClass().getSimpleName());
        }
    }

    /**
     * Call Resources.setApkPath(path) if available (shim), otherwise no-op.
     * Required for ApkResourceLoader.loadLayout() to read AXML from the APK ZIP.
     */
    public static void setApkPath(android.content.res.Resources res, String path) {
        try {
            Method m = res.getClass().getMethod("setApkPath", String.class);
            m.setAccessible(true);
            m.invoke(res, path);
        } catch (Exception e) {
            log("setApkPath not available: " + e.getClass().getSimpleName());
        }
    }

    /**
     * Call AssetManager.setAssetDir(path) if available (shim), otherwise no-op.
     */
    public static void setAssetDir(android.content.res.AssetManager assets, String path) {
        try {
            Method m = assets.getClass().getMethod("setAssetDir", String.class);
            m.setAccessible(true);
            m.invoke(assets, path);
        } catch (Exception e) {
            log("setAssetDir not available: " + e.getClass().getSimpleName());
        }
    }

    /**
     * Set a field on an Activity by name via reflection. Falls back silently if the
     * field doesn't exist (real Android's Activity has different internals).
     */
    public static void setActivityField(Activity activity, String fieldName, Object value) {
        try {
            Field f = findField(activity.getClass(), fieldName);
            if (f != null) {
                f.setAccessible(true);
                f.set(activity, value);
            }
        } catch (Exception e) {
            log("setActivityField(" + fieldName + ") failed: " + e.getClass().getSimpleName());
        }
    }

    /**
     * Get a field value from an Activity by name via reflection.
     * Returns defaultValue if the field doesn't exist.
     */
    @SuppressWarnings("unchecked")
    static <T> T getActivityField(Activity activity, String fieldName, T defaultValue) {
        try {
            Field f = findField(activity.getClass(), fieldName);
            if (f != null) {
                f.setAccessible(true);
                Object val = f.get(activity);
                return (T) val;
            }
        } catch (Exception e) {
            log("getActivityField(" + fieldName + ") failed: " + e.getClass().getSimpleName());
        }
        return defaultValue;
    }

    /**
     * Get a boolean field from an Activity. Returns defaultValue if not found.
     */
    static boolean getActivityBooleanField(Activity activity, String fieldName, boolean defaultValue) {
        try {
            Field f = findField(activity.getClass(), fieldName);
            if (f != null) {
                f.setAccessible(true);
                return f.getBoolean(activity);
            }
        } catch (Exception e) {
            log("getActivityBooleanField(" + fieldName + ") failed: " + e.getClass().getSimpleName());
        }
        return defaultValue;
    }

    /**
     * Get an int field from an Activity. Returns defaultValue if not found.
     */
    static int getActivityIntField(Activity activity, String fieldName, int defaultValue) {
        try {
            Field f = findField(activity.getClass(), fieldName);
            if (f != null) {
                f.setAccessible(true);
                return f.getInt(activity);
            }
        } catch (Exception e) {
            log("getActivityIntField(" + fieldName + ") failed: " + e.getClass().getSimpleName());
        }
        return defaultValue;
    }

    /**
     * Walk the class hierarchy to find a declared field.
     */
    private static Field findField(Class<?> clazz, String name) {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    private static void log(String msg) {
        // Avoid stdio during early Westlake bootstrap. Reflection failures here are
        // expected on some paths and logging them through PrintStream can crash boot.
    }
}
