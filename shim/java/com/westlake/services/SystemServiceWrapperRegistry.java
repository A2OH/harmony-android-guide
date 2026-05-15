// SPDX-License-Identifier: Apache-2.0
//
// Westlake CR3 + CR4 + CR5 -- SystemServiceWrapperRegistry
//
// Bridges Westlake's Binder-backed system services to the AOSP Manager
// classes (ActivityManager wrapping IActivityManager, PowerManager wrapping
// IPowerManager, etc.).  Also handles process-local services that AOSP
// does NOT route through Binder (LayoutInflater, etc.).  This is the
// Westlake counterpart to AOSP's android.app.SystemServiceRegistry -- the
// lookup table that Context.getSystemService(String) consults to convert a
// service name into the manager object the app expects.
//
// Why this exists:
//   Codex review Tier 3 §3 flagged WestlakeContextImpl.getSystemService
//   as architecturally drifted: its doc comment promised a binder->manager
//   route via ServiceManager.getService, but the implementation returned
//   null for everything.  CR3 made the documented behaviour real for the
//   services currently registered (activity, power); CR4 extended it with
//   the process-local LayoutInflater case (M4-PRE8 wall, no AIDL backing);
//   CR5 extends it again with the four M4b/M4d/M4e binder-backed names
//   (window, display, notification, input_method) so the corresponding
//   Manager classes get instantiated for Activity.attach et al.
//
// Strategy:
//   1. PROCESS-LOCAL FIRST (CR4): some service names have no binder backing
//      in AOSP -- they're per-Context Java objects (LayoutInflater is the
//      canonical example).  wrapProcessLocal() handles these BEFORE the
//      ServiceManager.getService call so we never waste a binder lookup.
//   2. BINDER-BACKED (CR3 + CR5): for all other names, look up the IBinder via
//      android.os.ServiceManager.getService(name).  Wrap the binder in the
//      matching Manager class by reflectively invoking that class's
//      (private/package-private) ctor.  Reflection lets us bind to
//      framework.jar's runtime ctor signatures without imposing a
//      compile-time dependency on @hide APIs that the shim's compile-time
//      android.jar doesn't expose.
//   3. On wrapping failure (reflection error or unexpected ctor shape),
//      fall back to returning the IXxx interface (Stub.asInterface(binder)).
//      Some callers can cope with the raw interface; this is a known
//      degraded-but-still-functional path.
//
// What this is NOT:
//   - NOT a per-app branch site.  All routing is purely by Context-level
//     service name; the host APK is irrelevant.
//   - NOT a place to speculatively wrap services that no Westlake M4 step
//     has registered.  Add a case only when the matching M4* milestone
//     calls ServiceManager.addService(name, ...), OR when discovery
//     surfaces a process-local service that AOSP constructs per-Context.
//     See BINDER_PIVOT_DESIGN.md §3.2 for the discovery-driven add-on-demand
//     architecture.
//
// Author: CR3 agent (2026-05-12), CR4 extension 2026-05-12, CR5 extension 2026-05-12

package com.westlake.services;

import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ServiceManager;

import java.lang.reflect.Constructor;

/**
 * Helper that wraps a Binder-backed Westlake system service in the AOSP
 * Manager class the caller expects from Context.getSystemService(String).
 */
public final class SystemServiceWrapperRegistry {

    private SystemServiceWrapperRegistry() {}

    // Lazy main-thread Handler used by ActivityManager / PowerManager ctors.
    private static volatile Handler sMainHandler;

    /**
     * Look up the named service via ServiceManager and wrap its IBinder in
     * the matching Manager class, if any.  Returns null when the service is
     * not registered or no wrapping is implemented yet (AOSP behaviour for
     * unknown service names).
     *
     * <p>Process-local services (e.g. {@code layout_inflater}, {@code window})
     * are handled BEFORE the ServiceManager.getService lookup since they
     * have no AIDL/binder backing -- AOSP constructs them per-Context.  See
     * the CR4 layout_inflater path in {@link #wrapProcessLocal} for the
     * concrete example.
     *
     * @param name the Context.* SERVICE name (e.g. Context.ACTIVITY_SERVICE).
     * @param ctx  the Context to pass to the Manager constructor (typically
     *             the caller's WestlakeContextImpl).
     * @return     a Manager instance, an IXxx interface fallback, or null.
     */
    public static Object getSystemService(String name, Context ctx) {
        if (name == null) return null;

        // ------------------------------------------------------------------
        // CR4 (2026-05-12): Process-local services -- handle BEFORE the
        // ServiceManager.getService lookup since they have no binder backing.
        // AOSP's SystemServiceRegistry registers these as
        // ServiceFetcher/CachedServiceFetcher entries that simply instantiate
        // the manager class per-Context.  LayoutInflater is the canonical
        // example: there is no IBinder for it, no AIDL interface, no
        // remote stub -- it's pure process-local view inflation.
        // ------------------------------------------------------------------
        Object local = wrapProcessLocal(name, ctx);
        if (local != null) return local;

        IBinder binder;
        try {
            binder = ServiceManager.getService(name);
        } catch (Throwable t) {
            return null;
        }
        if (binder == null) return null;
        return wrap(name, binder, ctx);
    }

    /**
     * Returns a process-local manager instance for service names that have
     * no IBinder backing in AOSP (LayoutInflater, etc.).  Returns null when
     * {@code name} is not a process-local service; the caller should then
     * fall through to the ServiceManager.getService binder path.
     *
     * <p>This is the CR4 entry point.  Add a case here when a discovery hit
     * reveals a service that AOSP registers as ServiceFetcher/Cached rather
     * than as a binder service.  Do NOT route these through ServiceManager
     * -- doing so always returns null (no addService call ever happens for
     * a non-binder service) and the call would just fall through to the
     * binder path, masking the missing wrapper.
     */
    public static Object wrapProcessLocal(String name, Context ctx) {
        if (name == null) return null;

        // --- Context.LAYOUT_INFLATER_SERVICE ("layout_inflater") ---
        // Per AOSP SystemServiceRegistry: registered as a CachedServiceFetcher
        // that returns `PolicyManager.makeNewLayoutInflater(ctx)`, which in
        // turn returns `new PhoneLayoutInflater(ctx)`.  We reproduce that
        // exact construction here.  Reflection because PhoneLayoutInflater
        // lives in `com.android.internal.policy.*` which is not exposed by
        // the shim's compile-time android.jar; framework.jar provides it on
        // the runtime BCP.
        if ("layout_inflater".equals(name)) {
            return wrapLayoutInflater(ctx);
        }

        return null;
    }

    /**
     * Wrap an already-resolved IBinder.  Public mostly for unit tests that
     * want to verify wrapping without going through ServiceManager.
     */
    public static Object wrap(String name, IBinder binder, Context ctx) {
        if (binder == null) return null;
        if (name == null) return null;

        // --- Context.ACTIVITY_SERVICE ("activity") ---
        if ("activity".equals(name)) {
            return wrapActivity(binder, ctx);
        }

        // --- Context.POWER_SERVICE ("power") ---
        if ("power".equals(name)) {
            return wrapPower(binder, ctx);
        }

        // --- Context.WINDOW_SERVICE ("window") --- [CR5]
        if ("window".equals(name)) {
            return wrapWindowManager(binder, ctx);
        }

        // --- Context.DISPLAY_SERVICE ("display") --- [CR5]
        if ("display".equals(name)) {
            return wrapDisplayManager(binder, ctx);
        }

        // --- Context.NOTIFICATION_SERVICE ("notification") --- [CR5]
        if ("notification".equals(name)) {
            return wrapNotificationManager(binder, ctx);
        }

        // --- Context.INPUT_METHOD_SERVICE ("input_method") --- [CR5]
        if ("input_method".equals(name)) {
            return wrapInputMethodManager(binder, ctx);
        }

        // --- "package" --- [M4c]
        // Note: PackageManager from Context.getSystemService("package") is
        // rare; most callers use Context.getPackageManager() which is
        // routed via WestlakeContextImpl.getPackageManager() to
        // WestlakePackageManagerStub (M4-PRE5).  This entry exists for
        // completeness so callers that DO use the binder-backed route
        // (e.g. component code that looks up "package" by name) get a
        // PackageManager wrapper instead of the raw IPackageManager.
        if ("package".equals(name)) {
            return wrapPackageManager(binder, ctx);
        }

        // Add more cases here as M4* registers additional services.

        // Unknown name: AOSP returns null.  Mirror that behaviour.
        return null;
    }

    // -------------------------------------------------------------------------
    // ACTIVITY service wrapping
    // -------------------------------------------------------------------------
    //
    // Runtime target: framework.jar's android.app.ActivityManager has ctor
    //   ActivityManager(Context, Handler)         [Android 11..16: package-private]
    // We invoke it reflectively because:
    //   - At compile time the shim's android.app.ActivityManager has no such
    //     ctor (it's a Tier-C stub), so a direct `new ActivityManager(...)`
    //     would not compile.
    //   - At runtime the framework.jar class wins (per
    //     scripts/framework_duplicates.txt) and its ctor lookup succeeds.
    //
    // If reflection fails (ctor shape changed, Manager class missing), we
    // fall back to IActivityManager.Stub.asInterface(binder) so callers that
    // can cope with the raw interface still get something usable.

    private static Object wrapActivity(IBinder binder, Context ctx) {
        try {
            Class<?> amCls = Class.forName("android.app.ActivityManager");
            Constructor<?> ctor = amCls.getDeclaredConstructor(
                    Context.class, Handler.class);
            ctor.setAccessible(true);
            return ctor.newInstance(ctx, getMainHandler());
        } catch (Throwable t) {
            // Reflective wrapping failed.  Fall back to the IXxx interface.
            return asInterfaceQuiet("android.app.IActivityManager", binder);
        }
    }

    // -------------------------------------------------------------------------
    // POWER service wrapping
    // -------------------------------------------------------------------------
    //
    // Runtime target: framework.jar's android.os.PowerManager has ctor
    //   PowerManager(Context, IPowerManager, IThermalService, Handler)
    //     [Android 11..16: @hide public]
    // Same reflective rationale as ActivityManager.
    //
    // IThermalService: not yet registered by any Westlake milestone, so we
    // pass null.  The methods Westlake currently exercises (isInteractive,
    // acquireWakeLock, releaseWakeLock, getBrightnessConstraint) all use
    // mService (the IPowerManager), not mThermalService.  Callers that
    // invoke thermal APIs (e.g. getCurrentThermalStatus) will NPE -- that
    // is acceptable today; M4-power-thermal is a separate follow-up.

    private static Object wrapPower(IBinder binder, Context ctx) {
        try {
            Class<?> pmCls = Class.forName("android.os.PowerManager");
            Class<?> ipmCls = Class.forName("android.os.IPowerManager");
            Class<?> thermalCls = null;
            try {
                thermalCls = Class.forName("android.os.IThermalService");
            } catch (ClassNotFoundException notFound) {
                // shim has no IThermalService stub; framework.jar's real
                // class IS available at runtime via BCP, so this path
                // shouldn't fire in practice.  If it does, we cannot build
                // the ctor signature and must fall back below.
            }
            Object ipm = asInterfaceQuiet("android.os.IPowerManager", binder);
            if (thermalCls == null) {
                // Cannot find the IThermalService type token -> can't pick
                // the ctor.  Degrade to the IXxx fallback.
                return ipm;
            }
            Constructor<?> ctor = pmCls.getDeclaredConstructor(
                    Context.class, ipmCls, thermalCls, Handler.class);
            ctor.setAccessible(true);
            return ctor.newInstance(ctx, ipm, null /* IThermalService */,
                    getMainHandler());
        } catch (Throwable t) {
            return asInterfaceQuiet("android.os.IPowerManager", binder);
        }
    }

    // -------------------------------------------------------------------------
    // WINDOW service wrapping  [CR5]
    // -------------------------------------------------------------------------
    //
    // Runtime target: framework.jar's android.view.WindowManagerImpl is the
    // canonical Manager that wraps IWindowManager.  It's an @hide class
    // (the public android.view.WindowManager is an interface), so the shim
    // cannot reference it at compile time -- reflection only.
    //
    // Ctor shape (verified against AOSP android-16.0.0_r1
    // core/java/android/view/WindowManagerImpl.java):
    //   public WindowManagerImpl(Context context)
    //     -- delegates to a 4-arg internal ctor with displayWindowProxy=null,
    //        parentWindow=null, windowContextToken=null.
    //
    // We try the 1-arg ctor first.  If that's missing on some odd
    // framework.jar variant we degrade to the IXxx interface (callers that
    // expect `WindowManager` will fail, but the Activity.attach path that
    // motivates CR5 only calls `setWindowManager(...)` on `mWindow`, which
    // accepts the WindowManager surface AOSP wraps via WindowManagerImpl).

    private static Object wrapWindowManager(IBinder binder, Context ctx) {
        try {
            Class<?> wmiCls = Class.forName("android.view.WindowManagerImpl");
            // Try the 1-arg (Context) ctor first -- this is the AOSP
            // canonical public entry point for SystemServiceRegistry.
            try {
                Constructor<?> ctor = wmiCls.getDeclaredConstructor(Context.class);
                ctor.setAccessible(true);
                return ctor.newInstance(ctx);
            } catch (NoSuchMethodException nsme) {
                // Fall through to no-arg / IXxx fallback.
            }
            // Last resort: no-arg ctor.
            try {
                Constructor<?> ctor = wmiCls.getDeclaredConstructor();
                ctor.setAccessible(true);
                return ctor.newInstance();
            } catch (NoSuchMethodException nsme2) {
                // Fall through.
            }
            // Nothing matched -- return raw IWindowManager interface.
            return asInterfaceQuiet("android.view.IWindowManager", binder);
        } catch (Throwable t) {
            return asInterfaceQuiet("android.view.IWindowManager", binder);
        }
    }

    // -------------------------------------------------------------------------
    // DISPLAY service wrapping  [CR5]
    // -------------------------------------------------------------------------
    //
    // Runtime target: framework.jar's android.hardware.display.DisplayManager
    // is a `public final` Manager.  Its constructor is package-private:
    //   DisplayManager(Context context)
    //     -- calls into DisplayManagerGlobal.getInstance() lazily.
    //
    // DisplayManager is the SDK-public API; SystemServiceRegistry registers
    // it with the (Context) ctor (verified against AOSP
    // android-16.0.0_r1 core/java/android/hardware/display/DisplayManager.java).
    //
    // No IDisplayManager wrapping needed at the Manager layer -- the
    // Manager talks to DisplayManagerGlobal which talks to IDisplayManager,
    // and DisplayManagerGlobal looks up the "display" binder service
    // itself (lazily, on first method call).  We just need to give the
    // caller a DisplayManager wrapper.

    private static Object wrapDisplayManager(IBinder binder, Context ctx) {
        try {
            Class<?> dmCls = Class.forName(
                    "android.hardware.display.DisplayManager");
            try {
                Constructor<?> ctor = dmCls.getDeclaredConstructor(Context.class);
                ctor.setAccessible(true);
                return ctor.newInstance(ctx);
            } catch (NoSuchMethodException nsme) {
                // Some framework variants may expose a no-arg ctor.
                try {
                    Constructor<?> ctor = dmCls.getDeclaredConstructor();
                    ctor.setAccessible(true);
                    return ctor.newInstance();
                } catch (NoSuchMethodException nsme2) {
                    // fall through
                }
            }
            return asInterfaceQuiet(
                    "android.hardware.display.IDisplayManager", binder);
        } catch (Throwable t) {
            return asInterfaceQuiet(
                    "android.hardware.display.IDisplayManager", binder);
        }
    }

    // -------------------------------------------------------------------------
    // NOTIFICATION service wrapping  [CR5]
    // -------------------------------------------------------------------------
    //
    // Runtime target: framework.jar's android.app.NotificationManager has
    // package-private ctor (verified against AOSP android-16.0.0_r1
    // core/java/android/app/NotificationManager.java):
    //   NotificationManager(Context context, Handler handler)
    //     -- mContext/mHandler stored; INotificationManager looked up
    //        lazily via ServiceManager.getService("notification") on first
    //        method call.
    //
    // We pass the same shared main Handler we use for ActivityManager /
    // PowerManager.  If the (Context, Handler) ctor is missing we try
    // (Context) and finally fall back to the IXxx interface.

    private static Object wrapNotificationManager(IBinder binder, Context ctx) {
        try {
            Class<?> nmCls = Class.forName("android.app.NotificationManager");
            // Try the (Context, Handler) ctor (Android 11..16 canonical).
            try {
                Constructor<?> ctor = nmCls.getDeclaredConstructor(
                        Context.class, Handler.class);
                ctor.setAccessible(true);
                return ctor.newInstance(ctx, getMainHandler());
            } catch (NoSuchMethodException nsme) {
                // fall through
            }
            // Fall back to single-arg (Context).
            try {
                Constructor<?> ctor = nmCls.getDeclaredConstructor(Context.class);
                ctor.setAccessible(true);
                return ctor.newInstance(ctx);
            } catch (NoSuchMethodException nsme2) {
                // fall through
            }
            return asInterfaceQuiet("android.app.INotificationManager", binder);
        } catch (Throwable t) {
            return asInterfaceQuiet("android.app.INotificationManager", binder);
        }
    }

    // -------------------------------------------------------------------------
    // INPUT_METHOD service wrapping  [CR5]
    // -------------------------------------------------------------------------
    //
    // Runtime target: framework.jar's
    // android.view.inputmethod.InputMethodManager.  Ctor shape on Android
    // 16 (verified against AOSP android-16.0.0_r1
    // core/java/android/view/inputmethod/InputMethodManager.java):
    //   InputMethodManager(IInputMethodManager service, int displayId, Looper looper)
    //     -- the primary internal ctor, used by InputMethodManager#forContext.
    //
    // SystemServiceRegistry registers InputMethodManager via the
    // `forContext(Context)` factory rather than a direct ctor, because the
    // Manager is shared per-display (per AOSP comment "an IMM per display"
    // -- see android.view.inputmethod.InputMethodManager:1146).  We
    // reflectively try forContext(Context) first; if missing we try a
    // (Context) ctor and then fall back.

    private static Object wrapInputMethodManager(IBinder binder, Context ctx) {
        try {
            Class<?> immCls = Class.forName(
                    "android.view.inputmethod.InputMethodManager");
            // Try the static `forContext(Context)` factory (Android 11..16).
            try {
                java.lang.reflect.Method fc = immCls.getDeclaredMethod(
                        "forContext", Context.class);
                fc.setAccessible(true);
                Object imm = fc.invoke(null, ctx);
                if (imm != null) return imm;
            } catch (NoSuchMethodException nsme) {
                // fall through
            }
            // Try the static `forContextInternal(int displayId, Looper)`
            // factory pattern by passing displayId=0 + main Looper, if
            // exposed.  This is a less-common path but some pre-release
            // builds carry it.
            try {
                java.lang.reflect.Method fci = immCls.getDeclaredMethod(
                        "forContextInternal", int.class, Looper.class);
                fci.setAccessible(true);
                Object imm = fci.invoke(null, Integer.valueOf(0),
                        Looper.getMainLooper());
                if (imm != null) return imm;
            } catch (NoSuchMethodException nsme) {
                // fall through
            }
            // Try the (Context) ctor (older Android, retained for safety).
            try {
                Constructor<?> ctor = immCls.getDeclaredConstructor(Context.class);
                ctor.setAccessible(true);
                return ctor.newInstance(ctx);
            } catch (NoSuchMethodException nsme) {
                // fall through
            }
            // Try the (int displayId) ctor (Android 11..12 internal).
            try {
                Constructor<?> ctor = immCls.getDeclaredConstructor(int.class);
                ctor.setAccessible(true);
                return ctor.newInstance(Integer.valueOf(0));
            } catch (NoSuchMethodException nsme) {
                // fall through
            }
            // Last resort: raw IInputMethodManager (descriptor lives in
            // com.android.internal.view per M4e brief, NOT
            // android.view.inputmethod).
            return asInterfaceQuiet(
                    "com.android.internal.view.IInputMethodManager", binder);
        } catch (Throwable t) {
            return asInterfaceQuiet(
                    "com.android.internal.view.IInputMethodManager", binder);
        }
    }

    // -------------------------------------------------------------------------
    // PACKAGE service wrapping  [M4c]
    // -------------------------------------------------------------------------
    //
    // Runtime target: framework.jar's
    // android.app.ApplicationPackageManager wraps IPackageManager.  It's
    // an @hide class (the public android.content.pm.PackageManager is
    // abstract), so the shim cannot reference it at compile time --
    // reflection only.
    //
    // Ctor shape (verified against AOSP android-16.0.0_r1
    // core/java/android/app/ApplicationPackageManager.java):
    //   ApplicationPackageManager(ContextImpl context, IPackageManager pm)
    //
    // We try (ContextImpl, IPackageManager) first.  If that's missing
    // (some odd framework.jar variant) we degrade to the IXxx interface
    // (callers that expect `PackageManager` will fail in calls, but the
    // route at least reflects what the binder layer returned).

    private static Object wrapPackageManager(IBinder binder, Context ctx) {
        try {
            Class<?> apmCls = Class.forName(
                    "android.app.ApplicationPackageManager");
            Class<?> ipmCls = Class.forName("android.content.pm.IPackageManager");
            Object ipm = asInterfaceQuiet("android.content.pm.IPackageManager", binder);
            // ApplicationPackageManager(ContextImpl, IPackageManager): the
            // first param is the @hide concrete ContextImpl, not the
            // abstract Context.  Look up by reflection so the shim
            // compile-time doesn't need to import ContextImpl.
            Class<?> ctxImplCls;
            try {
                ctxImplCls = Class.forName("android.app.ContextImpl");
            } catch (ClassNotFoundException nf) {
                ctxImplCls = null;
            }
            if (ctxImplCls != null && ctxImplCls.isInstance(ctx)) {
                try {
                    Constructor<?> ctor = apmCls.getDeclaredConstructor(
                            ctxImplCls, ipmCls);
                    ctor.setAccessible(true);
                    return ctor.newInstance(ctx, ipm);
                } catch (NoSuchMethodException nsme) {
                    // fall through
                }
            }
            // Some variants accept a plain Context.  Try that.
            try {
                Constructor<?> ctor = apmCls.getDeclaredConstructor(
                        Context.class, ipmCls);
                ctor.setAccessible(true);
                return ctor.newInstance(ctx, ipm);
            } catch (NoSuchMethodException nsme) {
                // fall through
            }
            return ipm;
        } catch (Throwable t) {
            return asInterfaceQuiet("android.content.pm.IPackageManager", binder);
        }
    }

    // -------------------------------------------------------------------------
    // LAYOUT_INFLATER service (process-local, NOT binder-backed)  [CR4]
    // -------------------------------------------------------------------------
    //
    // Runtime target: framework.jar's
    //   com.android.internal.policy.PhoneLayoutInflater(Context context)
    // is a public single-arg constructor (extends android.view.LayoutInflater
    // which is abstract).  AOSP's SystemServiceRegistry uses
    //   PolicyManager.makeNewLayoutInflater(ctx)
    // which is itself a thin reflective wrapper around the same ctor.  We
    // call the ctor directly here -- one less reflection hop and the
    // PolicyManager class isn't reliably exposed on every framework.jar
    // build.
    //
    // Why reflection: the shim's compile-time android.jar does not expose
    // com.android.internal.* classes (they're @hide / @SystemApi), so a
    // direct `new PhoneLayoutInflater(ctx)` would not compile.  At runtime
    // framework.jar's PhoneLayoutInflater is reachable through the boot
    // classpath.
    //
    // Why not also cache: AOSP caches LayoutInflater per-Context via
    // CachedServiceFetcher; we let the caller cache.  WestlakeContextImpl
    // doesn't cache today -- if discovery shows the same Context getting
    // multiple LIs becomes a problem, add a per-Context cache there.

    private static Object wrapLayoutInflater(Context ctx) {
        try {
            Class<?> phoneLI = Class.forName(
                    "com.android.internal.policy.PhoneLayoutInflater");
            Constructor<?> ctor = phoneLI.getDeclaredConstructor(Context.class);
            ctor.setAccessible(true);
            return ctor.newInstance(ctx);
        } catch (Throwable t) {
            // PhoneLayoutInflater missing or ctor mismatch.  We cannot
            // instantiate LayoutInflater directly (it's abstract).  Log the
            // problem and return null; the caller will see the same
            // AssertionError that drove us to add this path in the first
            // place, but at least we'll have a stderr trace pointing at the
            // real cause.
            try {
                System.err.println("[CR4] PhoneLayoutInflater wrap failed: "
                        + t);
            } catch (Throwable ignored) {}
            return null;
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Get (or lazily build) a Handler bound to the main Looper.  Returns
     * null in the unlikely case where Looper.prepareMainLooper hasn't been
     * called and we can't prepare it here either (some Manager ctors
     * tolerate a null Handler; callers should not rely on that).
     */
    private static Handler getMainHandler() {
        Handler h = sMainHandler;
        if (h != null) return h;
        synchronized (SystemServiceWrapperRegistry.class) {
            if (sMainHandler == null) {
                Looper l = Looper.getMainLooper();
                if (l == null) {
                    try { Looper.prepareMainLooper(); } catch (Throwable ignored) {}
                    l = Looper.getMainLooper();
                }
                if (l != null) {
                    try { sMainHandler = new Handler(l); } catch (Throwable ignored) {}
                }
            }
            return sMainHandler;
        }
    }

    /**
     * Reflectively call IXxx.Stub.asInterface(binder).  Returns null on any
     * failure (class missing, no Stub inner class, no asInterface method, or
     * exception during invoke).  Used to fall back from wrapped-Manager to
     * raw-IXxx when Manager construction throws.
     */
    private static Object asInterfaceQuiet(String iName, IBinder binder) {
        try {
            Class<?> iCls = Class.forName(iName);
            Class<?> stubCls = null;
            for (Class<?> inner : iCls.getDeclaredClasses()) {
                if ("Stub".equals(inner.getSimpleName())) {
                    stubCls = inner;
                    break;
                }
            }
            if (stubCls == null) return null;
            return stubCls.getDeclaredMethod("asInterface", IBinder.class)
                    .invoke(null, binder);
        } catch (Throwable t) {
            return null;
        }
    }
}
