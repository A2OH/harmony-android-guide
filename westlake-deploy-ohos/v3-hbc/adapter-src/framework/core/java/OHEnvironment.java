/*
 * OHEnvironment.java
 *
 * Minimal utility class serving as the environment detection and
 * native library loading entry point for the OH adapter layer.
 */

package adapter.core;

import android.os.SystemProperties;

/**
 * Static utility for detecting an OH environment, loading the adapter
 * bridge library, and managing the adapter lifecycle.
 */
public final class OHEnvironment {

    private static final String TAG = "OHEnvironment";
    private static final String PROP_OH_ADAPTER_ENABLED = "persist.oh.adapter.enabled";

    static {
        System.loadLibrary("oh_adapter_bridge");
    }

    private OHEnvironment() {
        // Prevent instantiation.
    }

    /**
     * Returns {@code true} when running inside an OH environment.
     * Checks the system property first; falls back to the native probe.
     */
    public static boolean isOHEnvironment() {
        String prop = SystemProperties.get(PROP_OH_ADAPTER_ENABLED);
        if ("true".equals(prop)) {
            return true;
        }
        if ("false".equals(prop)) {
            return false;
        }
        // Property not set or unrecognised value – ask native side.
        return nativeIsOHEnvironment();
    }

    /** Native probe for OH environment detection. */
    public static native boolean nativeIsOHEnvironment();

    /**
     * Initializes the OH IPC framework and connects to OH services.
     * Must be called early during process startup.
     */
    public static void initialize() {
        nativeInitialize();
        nativeConnectToOHServices();
    }

    /** Initialize the OH IPC framework. */
    public static native boolean nativeInitialize();

    /** Connect to OH services. */
    public static native boolean nativeConnectToOHServices();

    /**
     * Attaches an application to the OH adapter layer so that
     * subsequent calls can be routed correctly.
     *
     * The {@code thread} object MUST be an {@code IApplicationThread.Stub}
     * (Android-side local Binder stub).  It is wrapped by an
     * {@code AppSchedulerAdapter} on the native side which is then registered
     * with OH AppMgr via IAppMgr.AttachApplication.  All subsequent
     * lifecycle / scheduling callbacks from OH AppMgr are forwarded back to
     * this thread object via JNI, so it must remain alive for the lifetime of
     * the process.
     *
     * Returns true iff the IPC was actually delivered to OH AppMgrService
     * (not merely "no exception").  Callers should propagate the failure so
     * the adapter never falsely reports a successful attach.
     *
     * @param thread      Android IApplicationThread.Stub instance
     * @param pid         process id of the application
     * @param uid         user id of the application
     * @param packageName application package name
     */
    public static boolean attachApplication(Object thread, int pid, int uid,
            String packageName) {
        return nativeAttachApplication(thread, pid, uid, packageName);
    }

    /** Native call to attach an application to the adapter layer. */
    public static native boolean nativeAttachApplication(Object thread, int pid,
            int uid, String packageName);

    /**
     * Shuts down the OH adapter layer and releases associated resources.
     */
    public static void shutdown() {
        nativeShutdown();
    }

    /** Native call to shut down the adapter layer. */
    public static native void nativeShutdown();

    /**
     * Notifies the native side of an application state change.
     * Used by LifecycleAdapter to forward lifecycle events.
     *
     * @param state the new application state
     */
    public static native void nativeNotifyAppState(int state);

    // ============================================================
    // B.40 (2026-04-29 EOD+2): reflection-based adapter factories.
    //
    // L5 patches in AOSP framework.jar (ActivityManager / ActivityTaskManager
    // / ActivityThread / WindowManagerGlobal) used to import + new-construct
    // adapter classes directly, e.g.
    //   import adapter.activity.ActivityManagerAdapter;
    //   ...
    //   return new ActivityManagerAdapter();
    // This required ALL adapter classes to live in BCP oh-adapter-framework.jar
    // because framework.jar is BCP and Java imports resolve via the loading
    // classloader.  Result: every adapter change forced a boot-image rebuild
    // (multi-day, risky per memory feedback_boot_image_full_rebuild_risk.md).
    //
    // B.40 fix: only OHEnvironment stays in BCP.  L5 patches now call the
    // factories below, which use reflection + system classloader (PathClassLoader)
    // to instantiate adapter classes loaded from oh-adapter-runtime.jar (non-BCP).
    // After this refactor, adapter changes only require recompiling
    // oh-adapter-runtime.jar — no boot-image rebuild.
    //
    // Returns Object so L5 patches can cast to BCP IActivityManager / IPackage-
    // Manager / IWindowManager / IWindowSession (all BCP types; cast works
    // because adapter classes extend BCP IXxx.Stub).  Returns null on failure
    // so L5 patches can fall back to stock AOSP behavior.
    // ============================================================

    private static Object newAdapterReflective(String className) {
        // Dual-classloader strategy (B.40 new + OLD fallback).  Old scheme
        // = adapter classes in BCP oh-adapter-framework.jar (loaded via
        // OHEnvironment's classloader = BCP).  New scheme (B.40) = adapter
        // classes in oh-adapter-runtime.jar (loaded via system classloader
        // = PathClassLoader).  Try new first; if that fails, fall back to
        // old.  Net effect: this factory works regardless of which scheme
        // the build script materialized — no code change needed to roll
        // back from B.40 to OLD scheme (just rebuild jars accordingly).
        Throwable firstErr = null;
        // Path A — B.40 new scheme: system classloader (PathClassLoader)
        try {
            ClassLoader cl = ClassLoader.getSystemClassLoader();
            if (cl != null && cl != OHEnvironment.class.getClassLoader()) {
                Class<?> cls = Class.forName(className, true, cl);
                return cls.getDeclaredConstructor().newInstance();
            }
        } catch (Throwable t) {
            firstErr = t;
        }
        // Path B — OLD scheme: BCP classloader (OHEnvironment's own)
        try {
            Class<?> cls = Class.forName(className);
            return cls.getDeclaredConstructor().newInstance();
        } catch (Throwable t2) {
            // Both schemes failed.  Use stderr (redirected to hilog by
            // AppSpawnXInit.redirectLogStreamsToHiLog at B.35.B) so the
            // failure is visible without depending on Log.* (which may
            // route through broken liblog→hilog bridge in fork child).
            System.err.println("[OHEnvironment] newAdapterReflective(" + className + ") both schemes failed");
            if (firstErr != null) {
                System.err.println("  Path A (system CL): " + firstErr);
            }
            System.err.println("  Path B (BCP CL): " + t2);
            return null;
        }
    }

    /** L5 ActivityManager patch entry — replaces {@code new ActivityManagerAdapter()}. */
    public static Object getActivityManagerAdapter() {
        return newAdapterReflective("adapter.activity.ActivityManagerAdapter");
    }

    /** L5 ActivityTaskManager patch entry. */
    public static Object getActivityTaskManagerAdapter() {
        return newAdapterReflective("adapter.activity.ActivityTaskManagerAdapter");
    }

    /** L5 ActivityThread patch entry — replaces {@code new PackageManagerAdapter()}. */
    public static Object getPackageManagerAdapter() {
        return newAdapterReflective("adapter.packagemanager.PackageManagerAdapter");
    }

    /** L5 WindowManagerGlobal patch entry — replaces {@code new WindowManagerAdapter()}. */
    public static Object getWindowManagerAdapter() {
        return newAdapterReflective("adapter.window.WindowManagerAdapter");
    }

    /** L5 WindowManagerGlobal patch entry — replaces {@code new WindowSessionAdapter()}. */
    public static Object getWindowSessionAdapter() {
        return newAdapterReflective("adapter.window.WindowSessionAdapter");
    }
}
