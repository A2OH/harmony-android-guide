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
        Class<?> raw = cl.loadClass(className);
        Object activityInstance = tryAllocateActivityInstance(raw, className);
        if (activityInstance != null) {
            return Activity.class.cast(activityInstance);
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
        Class<?> raw = cl.loadClass(className);
        Object appInstance = tryAllocateComponentInstance(raw, className, "Application");
        if (appInstance != null) {
            return Application.class.cast(appInstance);
        }
        return instantiate(raw, className, Application.class);
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
        Object nativeInstance = WestlakeLauncher.tryAllocInstance(raw);
        if (nativeInstance != null) {
            Log.i(TAG, "instantiate" + componentKind + " via nativeAllocInstance: " + className);
            return nativeInstance;
        }

        Object unsafeInstance = tryAllocateInstance(raw);
        if (unsafeInstance != null) {
            Log.w(TAG, "instantiate" + componentKind + " via Java Unsafe fallback: " + className);
            return unsafeInstance;
        }

        Log.w(TAG, "instantiate" + componentKind + " allocation failed: " + className);
        return null;
    }

    private static Object tryAllocateActivityInstance(Class<?> raw, String className) {
        return tryAllocateComponentInstance(raw, className, "Activity");
    }

    /** Default factory instance. Used when no custom factory is configured. */
    public static final AppComponentFactory DEFAULT = new AppComponentFactory();
}
