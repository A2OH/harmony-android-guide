/*
 * ServiceInterceptor.java
 *
 * Runtime fallback for unmodified AOSP builds.
 * Replaces Android system service proxies with adapter implementations
 * via reflection, so that IPC calls are routed to OpenHarmony services.
 *
 * When the AOSP source is modified (see aosp_patches/), this class is
 * not needed — the Singleton.create() / getXxxService() methods return
 * adapters directly. This class is kept as a fallback for testing on
 * unmodified AOSP builds.
 */
package adapter.core;

import android.util.Log;

import adapter.activity.ActivityManagerAdapter;
import adapter.activity.ActivityTaskManagerAdapter;
import adapter.packagemanager.PackageManagerAdapter;
import adapter.window.WindowManagerAdapter;
import adapter.window.WindowSessionAdapter;

import java.lang.reflect.Field;

public class ServiceInterceptor {

    private static final String TAG = "OH_ServiceInterceptor";

    /**
     * Install adapters for all 5 bridged system services.
     * Each adapter extends IXxx.Stub and is set directly into the
     * framework's singleton/field, replacing the original Binder proxy.
     */
    public static void installInterceptors() {
        Log.i(TAG, "Installing service adapters...");

        installActivityManagerAdapter();
        installActivityTaskManagerAdapter();
        installPackageManagerAdapter();
        installWindowManagerAdapter();
        installWindowSessionAdapter();

        Log.i(TAG, "All service adapters installed");
    }

    private static void installActivityManagerAdapter() {
        try {
            ActivityManagerAdapter adapter = new ActivityManagerAdapter();
            replaceSingletonInstance(
                    "android.app.ActivityManager",
                    "IActivityManagerSingleton",
                    adapter);
            Log.i(TAG, "IActivityManager adapter installed");
        } catch (Exception e) {
            Log.e(TAG, "Failed to install IActivityManager adapter", e);
        }
    }

    private static void installActivityTaskManagerAdapter() {
        try {
            ActivityTaskManagerAdapter adapter = new ActivityTaskManagerAdapter();
            replaceSingletonInstance(
                    "android.app.ActivityTaskManager",
                    "IActivityTaskManagerSingleton",
                    adapter);
            Log.i(TAG, "IActivityTaskManager adapter installed");
        } catch (Exception e) {
            Log.e(TAG, "Failed to install IActivityTaskManager adapter", e);
        }
    }

    private static void installPackageManagerAdapter() {
        try {
            PackageManagerAdapter adapter = new PackageManagerAdapter();
            replaceStaticField(
                    "android.app.ActivityThread",
                    "sPackageManager",
                    adapter);
            Log.i(TAG, "IPackageManager adapter installed");
        } catch (Exception e) {
            Log.e(TAG, "Failed to install IPackageManager adapter", e);
        }
    }

    private static void installWindowManagerAdapter() {
        try {
            WindowManagerAdapter adapter = new WindowManagerAdapter();
            replaceStaticField(
                    "android.view.WindowManagerGlobal",
                    "sWindowManagerService",
                    adapter);
            Log.i(TAG, "IWindowManager adapter installed");
        } catch (Exception e) {
            Log.e(TAG, "Failed to install IWindowManager adapter", e);
        }
    }

    private static void installWindowSessionAdapter() {
        try {
            WindowSessionAdapter adapter = new WindowSessionAdapter();
            replaceStaticField(
                    "android.view.WindowManagerGlobal",
                    "sWindowSession",
                    adapter);
            Log.i(TAG, "IWindowSession adapter installed");
        } catch (Exception e) {
            Log.e(TAG, "Failed to install IWindowSession adapter", e);
        }
    }

    private static void replaceSingletonInstance(String managerClassName, String singletonFieldName,
                                                  Object adapterInstance) throws Exception {
        Class<?> mgr = Class.forName(managerClassName);
        Field singletonField = mgr.getDeclaredField(singletonFieldName);
        singletonField.setAccessible(true);
        Object singleton = singletonField.get(null);

        if (singleton == null) {
            Log.w(TAG, singletonFieldName + " is null, cannot install adapter");
            return;
        }

        Class<?> singletonClass = Class.forName("android.util.Singleton");
        Field instanceField = singletonClass.getDeclaredField("mInstance");
        instanceField.setAccessible(true);
        instanceField.set(singleton, adapterInstance);
    }

    private static void replaceStaticField(String className, String fieldName,
                                             Object adapterInstance) throws Exception {
        Class<?> clazz = Class.forName(className);
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, adapterInstance);
    }
}
