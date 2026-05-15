// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4 -- ServiceRegistrar
//
// Central place to register every Westlake M4 binder service with our
// ServiceManager.  Call ServiceRegistrar.registerAllServices() once at
// process startup (from WestlakeLauncher or from a test harness).
//
// Why one place: M4a (activity), M4b (window), M4c (package), M4d (display),
// M4-power (power), M4e (notification + input_method) each contribute one
// binder service.  Without a central registrar each milestone would either
// duplicate boot wiring or rely on side-effects of class init.  This
// helper makes the registration explicit, ordered, and idempotent.
//
// Idempotency: addService() with the same name on our servicemanager
// either replaces or no-ops (see aosp-libbinder-port/native/);
// registerAllServices() may be called more than once without harm.

package com.westlake.services;

import android.os.ServiceManager;

public final class ServiceRegistrar {

    // CR1-fix: codex Tier 1 HIGH-1 -- previously sRegistered was set to true
    // after registerAllServices() ran, regardless of which services actually
    // landed in servicemanager.  If addService failed silently (the old JNI
    // would mask any non-zero status), the boolean would lock us into a
    // "fully registered" state we could never recover from.  Now we track
    // each service individually so a partial failure leaves the rest of the
    // bringup retryable; the global "all done" flag flips only when every
    // present service actually registers.
    private static volatile boolean sAllRegistered = false;
    private static final java.util.Set<String> sRegisteredNames =
            java.util.Collections.synchronizedSet(new java.util.HashSet<String>());

    private ServiceRegistrar() {}

    /**
     * Registers every Westlake M4 service that has a Java implementation
     * available.  Safe to call multiple times -- already-registered names
     * are skipped, missing classes are skipped, and a per-service addService
     * failure does NOT poison subsequent retries of other services.
     *
     * @return number of services NEWLY registered by this call (0 if all
     *         previously-registered or no services available).
     */
    public static synchronized int registerAllServices() {
        if (sAllRegistered) return 0;
        int count = 0;
        int attempted = 0;
        int succeeded = 0;

        // Each service registration is independently try/catch'd so a
        // failure in one doesn't block the others.  Reflection is used
        // for services that may not yet have their compile-time AIDL
        // stubs present in shim/java/; using Class.forName lets us
        // probe-load and skip gracefully.

        // M4-power: IPowerManager under "power"
        Result rPower = tryRegister("power",
                "com.westlake.services.WestlakePowerManagerService");
        if (rPower != Result.MISSING) { attempted++; }
        if (rPower == Result.REGISTERED) { succeeded++; count++; }

        // M4a: IActivityManager under "activity"
        // (Added by M4a agent; uses no-arg constructor.  If the class
        // requires non-default args, M4a may need to update this call.)
        Result rActivity = tryRegister("activity",
                "com.westlake.services.WestlakeActivityManagerService");
        if (rActivity != Result.MISSING) { attempted++; }
        if (rActivity == Result.REGISTERED) { succeeded++; count++; }

        // M4b: IWindowManager under "window"
        // Tier-1 transactions needed for PhoneWindow.<init> + Activity.attach
        // (openSession, getInitialDisplaySize, getCurrentAnimatorScale,
        //  watchRotation, getDefaultDisplayRotation, addWindowToken,
        //  removeWindowToken, setEventDispatching).
        Result rWindow = tryRegister("window",
                "com.westlake.services.WestlakeWindowManagerService");
        if (rWindow != Result.MISSING) { attempted++; }
        if (rWindow == Result.REGISTERED) { succeeded++; count++; }

        // M4d: IDisplayManager under "display"
        // Tier-1: getDisplayInfo(0), getDisplayIds(false), registerCallback(...)
        // -- all observed during DisplayManagerGlobal lookups in onCreate /
        // density resolution paths.
        Result rDisplay = tryRegister("display",
                "com.westlake.services.WestlakeDisplayManagerService");
        if (rDisplay != Result.MISSING) { attempted++; }
        if (rDisplay == Result.REGISTERED) { succeeded++; count++; }

        // M4e: INotificationManager under "notification"
        // Tier-1: areNotificationsEnabled, getZenMode, getEffectsSuppressor,
        // getNotificationChannels, getNotificationChannel -- called by
        // NotificationManager.getInstance() and Notification.Builder
        // sanity-checks during onCreate.
        Result rNotification = tryRegister("notification",
                "com.westlake.services.WestlakeNotificationManagerService");
        if (rNotification != Result.MISSING) { attempted++; }
        if (rNotification == Result.REGISTERED) { succeeded++; count++; }

        // M4e: IInputMethodManager under "input_method"
        // (descriptor: com.android.internal.view.IInputMethodManager).
        // Tier-1: getInputMethodList, getEnabledInputMethodList,
        // getCurrentInputMethodInfoAsUser, addClient -- all observed
        // during EditText inflation and TextView focus paths.
        Result rInputMethod = tryRegister("input_method",
                "com.westlake.services.WestlakeInputMethodManagerService");
        if (rInputMethod != Result.MISSING) { attempted++; }
        if (rInputMethod == Result.REGISTERED) { succeeded++; count++; }

        // M4c: IPackageManager under "package"
        // Tier-1: getPackageInfo, getApplicationInfo, getInstalledPackages,
        // getInstalledApplications, resolveIntent, resolveService,
        // hasSystemFeature, getNameForUid, getPackagesForUid,
        // getInstallerPackageName, getServiceInfo, getActivityInfo,
        // getReceiverInfo, getProviderInfo, queryContentProviders --
        // expected to be hit by Hilt's package-monitor-callback and
        // binder-side PackageManager probes once discovery progresses past
        // M4-PRE14.  Note: distinct from M4-PRE5's WestlakePackageManagerStub
        // (Context.getPackageManager() local stub).
        Result rPackage = tryRegister("package",
                "com.westlake.services.WestlakePackageManagerService");
        if (rPackage != Result.MISSING) { attempted++; }
        if (rPackage == Result.REGISTERED) { succeeded++; count++; }

        // Only flip the "done" flag when every present service registered.
        // If a registration failed, leave sAllRegistered=false so callers can
        // call again later (after the underlying transient cleared, e.g.
        // servicemanager hadn't bound /dev/vndbinder yet).
        if (attempted == succeeded) {
            sAllRegistered = true;
        } else {
            System.err.println("[ServiceRegistrar] partial bringup: attempted="
                    + attempted + " succeeded=" + succeeded
                    + " -- retry will be allowed on next call");
        }
        return count;
    }

    /** Result of a single tryRegister call. */
    private enum Result {
        REGISTERED,   // newly registered (or already in sRegisteredNames)
        MISSING,      // class not on classpath; not counted against us
        FAILED        // class present but addService failed
    }

    /**
     * Best-effort registration of one service by class name.
     * Returns REGISTERED on success (or if already registered), MISSING if
     * the class isn't on the classpath (not an error), FAILED if the class
     * exists but registration threw.
     */
    private static Result tryRegister(String serviceName, String className) {
        if (sRegisteredNames.contains(serviceName)) {
            // Idempotent: a re-call after a prior success no-ops cleanly.
            return Result.REGISTERED;
        }
        try {
            Class<?> cls = Class.forName(className);
            Object svc = cls.getDeclaredConstructor().newInstance();
            if (!(svc instanceof android.os.IBinder)) {
                System.err.println("[ServiceRegistrar] " + className
                        + " is not an IBinder; skipping");
                return Result.FAILED;
            }
            // CR1-fix HIGH-1: addService now THROWS on non-zero native status.
            // Catch that here so a single bad service doesn't take down the
            // whole bringup, and only mark `serviceName` registered after a
            // verified success.
            ServiceManager.addService(serviceName, (android.os.IBinder) svc);
            sRegisteredNames.add(serviceName);
            return Result.REGISTERED;
        } catch (ClassNotFoundException notFound) {
            // Class hasn't been implemented yet; quietly skip.
            return Result.MISSING;
        } catch (Throwable t) {
            System.err.println("[ServiceRegistrar] register " + serviceName
                    + " (" + className + ") failed: " + t);
            return Result.FAILED;
        }
    }

    /** Reset for unit tests; not thread-safe vs concurrent registers. */
    public static synchronized void resetForTesting() {
        sAllRegistered = false;
        sRegisteredNames.clear();
    }
}
