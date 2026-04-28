package android.app;

import android.content.Intent;
import android.util.Log;

import com.westlake.engine.WestlakeLauncher;

/**
 * AppComponentFactory -- controls instantiation of manifest elements.
 *
 * Mirrors AOSP's android.app.AppComponentFactory. This is the hook that
 * dependency injection frameworks (Hilt, Dagger) use to inject dependencies
 * into Activities, Services, and other components at creation time.
 *
 * Subclasses override instantiateActivity() etc. to perform injection before
 * the component's lifecycle methods are called. The default implementation
 * simply uses reflection to create instances.
 *
 * In Hilt apps, the generated HiltAppComponentFactory subclass is declared
 * in AndroidManifest.xml via android:appComponentFactory="...".
 */
public class AppComponentFactory {
    private static final String TAG = "AppComponentFactory";

    public AppComponentFactory() {}

    private static void maybeLogI(String message) {
        if (!WestlakeLauncher.isRealFrameworkFallbackAllowed()) {
            return;
        }
        Log.i(TAG, message);
    }

    private static void maybeLogW(String message) {
        if (!WestlakeLauncher.isRealFrameworkFallbackAllowed()) {
            return;
        }
        Log.w(TAG, message);
    }

    /**
     * Create an Activity instance. Hilt overrides this to inject dependencies.
     *
     * @param cl        The ClassLoader to use.
     * @param className The fully-qualified Activity class name.
     * @param intent    The launching Intent (may be null).
     * @return A new Activity instance.
     */
    public Activity instantiateActivity(ClassLoader cl, String className, Intent intent)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        WestlakeLauncher.marker("PF301 strict factory entry");
        Class<?> raw = null;
        try {
            raw = WestlakeLauncher.resolveAppClassChildFirstOrNull(className);
        } catch (Throwable resolveChildFirstError) {}
        if (raw == null && cl != null) {
            try {
                raw = WestlakeLauncher.resolveAppClassOrNull(className);
            } catch (Throwable resolveError) {}
        }
        if (raw == null && cl != null) {
            try {
                raw = cl.loadClass(className);
            } catch (Throwable loadError) {}
        }
        if (raw == null && cl != null) {
            try {
                raw = Class.forName(className, false, cl);
            } catch (Throwable classForNameError) {}
        }
        WestlakeLauncher.marker("PF301 strict factory post-resolve branch");
        if (raw == null) {
            WestlakeLauncher.marker("PF301 strict factory raw class was null");
            throw new InstantiationException(className + ": activity class unresolved");
        }
        WestlakeLauncher.marker("PF301 strict factory raw class was nonnull");
        WestlakeLauncher.marker("PF301 strict factory ctor activity call");
        try {
            java.lang.reflect.Constructor<?> ctor = raw.getDeclaredConstructor();
            ctor.setAccessible(true);
            Activity activity = Activity.class.cast(ctor.newInstance());
            WestlakeLauncher.marker("PF301 strict factory ctor activity returned");
            return activity;
        } catch (Throwable ctorError) {
            WestlakeLauncher.marker("PF301 strict factory ctor activity failed");
            maybeLogW("instantiateActivity constructor failed for " + className + ": "
                    + ctorError.getClass().getSimpleName() + ": " + ctorError.getMessage());
        }
        WestlakeLauncher.marker("PF301 strict factory allocate activity call");
        Object activityInstance = tryAllocateActivityInstance(raw, className);
        WestlakeLauncher.marker("PF301 strict factory allocate activity returned");
        if (activityInstance != null) {
            WestlakeLauncher.marker("PF301 strict factory cast activity call");
            Activity castActivity = (Activity) activityInstance;
            WestlakeLauncher.marker("PF301 strict factory cast activity returned");
            return castActivity;
        }
        throw new InstantiationException(className + ": activity ctor path disabled");
    }

    /**
     * Create an Application instance. Hilt overrides this to inject dependencies.
     *
     * @param cl        The ClassLoader to use.
     * @param className The fully-qualified Application class name.
     * @return A new Application instance.
     */
    public Application instantiateApplication(ClassLoader cl, String className)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        WestlakeLauncher.marker("PF301 strict factory application entry");
        Class<?> raw = null;
        try {
            raw = WestlakeLauncher.resolveAppClassChildFirstOrNull(className);
        } catch (Throwable resolveChildFirstError) {}
        if (raw == null && cl != null) {
            try {
                raw = WestlakeLauncher.resolveAppClassOrNull(className);
            } catch (Throwable resolveError) {}
        }
        if (raw == null && cl != null) {
            try {
                raw = Class.forName(className, false, cl);
            } catch (Throwable classForNameError) {}
        }
        if (raw == null && cl != null) {
            raw = cl.loadClass(className);
        }
        if (raw == null) {
            throw new InstantiationException(className + ": application class unresolved");
        }
        WestlakeLauncher.marker("PF301 strict factory application raw class nonnull");
        try {
            java.lang.reflect.Constructor<?> ctor = raw.getDeclaredConstructor();
            ctor.setAccessible(true);
            WestlakeLauncher.marker("PF301 strict factory application ctor call");
            Application app = Application.class.cast(ctor.newInstance());
            WestlakeLauncher.marker("PF301 strict factory application ctor returned");
            return app;
        } catch (Throwable ctorError) {
            WestlakeLauncher.marker("PF301 strict factory application ctor failed");
            maybeLogW("instantiateApplication constructor failed for " + className + ": "
                    + ctorError.getClass().getSimpleName() + ": " + ctorError.getMessage());
        }
        Object appInstance = tryAllocateComponentInstance(raw, className, "Application");
        if (appInstance != null) {
            return Application.class.cast(appInstance);
        }
        throw new InstantiationException(className + ": application ctor path disabled");
    }

    /**
     * Create a Service instance.
     *
     * @param cl        The ClassLoader to use.
     * @param className The fully-qualified Service class name.
     * @param intent    The launching Intent (may be null).
     * @return A new Service instance.
     */
    public Service instantiateService(ClassLoader cl, String className, Intent intent)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return instantiate(cl.loadClass(className), className, Service.class);
    }

    /**
     * Create a BroadcastReceiver instance.
     *
     * @param cl        The ClassLoader to use.
     * @param className The fully-qualified BroadcastReceiver class name.
     * @param intent    The launching Intent (may be null).
     * @return A new BroadcastReceiver instance.
     */
    public android.content.BroadcastReceiver instantiateReceiver(
            ClassLoader cl, String className, Intent intent)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return instantiate(cl.loadClass(className), className, android.content.BroadcastReceiver.class);
    }

    /**
     * Create a ContentProvider instance.
     *
     * @param cl        The ClassLoader to use.
     * @param className The fully-qualified ContentProvider class name.
     * @return A new ContentProvider instance.
     */
    public android.content.ContentProvider instantiateProvider(
            ClassLoader cl, String className)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return instantiate(cl.loadClass(className), className, android.content.ContentProvider.class);
    }

    private <T> T instantiate(Class<?> raw, String className, Class<T> type)
            throws InstantiationException, IllegalAccessException {
        try {
            java.lang.reflect.Constructor<?> ctor = raw.getDeclaredConstructor();
            ctor.setAccessible(true);
            return type.cast(ctor.newInstance());
        } catch (InstantiationException e) {
            Object unsafeInstance = tryAllocateInstance(raw);
            if (unsafeInstance != null) {
                return type.cast(unsafeInstance);
            }
            throw e;
        } catch (IllegalAccessException e) {
            Object unsafeInstance = tryAllocateInstance(raw);
            if (unsafeInstance != null) {
                return type.cast(unsafeInstance);
            }
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Error e) {
            throw e;
        } catch (Throwable t) {
            Object unsafeInstance = tryAllocateInstance(raw);
            if (unsafeInstance != null) {
                return type.cast(unsafeInstance);
            }
            InstantiationException wrapped = new InstantiationException(className + ": " + t);
            wrapped.initCause(t);
            throw wrapped;
        }
    }

    static Object tryAllocateInstance(Class<?> raw) {
        if (raw == null) {
            return null;
        }
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            java.lang.reflect.Field field = unsafeClass.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Object unsafe = field.get(null);
            return unsafeClass.getMethod("allocateInstance", Class.class).invoke(unsafe, raw);
        } catch (Throwable ignored) {
            try {
                Class<?> unsafeClass = Class.forName("jdk.internal.misc.Unsafe");
                java.lang.reflect.Field field = unsafeClass.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                Object unsafe = field.get(null);
                return unsafeClass.getMethod("allocateInstance", Class.class).invoke(unsafe, raw);
            } catch (Throwable ignoredToo) {
                return null;
            }
        }
    }

    private static Object tryAllocateComponentInstance(
            Class<?> raw, String className, String componentKind) {
        WestlakeLauncher.marker("PF301 strict factory native alloc call");
        Object nativeInstance = WestlakeLauncher.tryAllocInstance(raw);
        WestlakeLauncher.marker("PF301 strict factory native alloc returned");
        if (nativeInstance != null) {
            WestlakeLauncher.marker("PF301 strict factory native alloc nonnull");
            return nativeInstance;
        }

        WestlakeLauncher.marker("PF301 strict factory unsafe alloc call");
        Object unsafeInstance = tryAllocateInstance(raw);
        WestlakeLauncher.marker("PF301 strict factory unsafe alloc returned");
        if (unsafeInstance != null) {
            WestlakeLauncher.marker("PF301 strict factory unsafe alloc nonnull");
            return unsafeInstance;
        }

        WestlakeLauncher.marker("PF301 strict factory allocation failed");
        return null;
    }

    private static Object tryAllocateActivityInstance(Class<?> raw, String className) {
        return tryAllocateComponentInstance(raw, className, "Activity");
    }

    /** Default factory instance. Used when no custom factory is configured. */
    public static final AppComponentFactory DEFAULT = new AppComponentFactory();
}
