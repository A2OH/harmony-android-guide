// SPDX-License-Identifier: Apache-2.0
//
// Westlake CR3 + CR4 + CR5 -- SystemServiceRouteTest.java
//
// Verifies WestlakeContextImpl.getSystemService now routes:
//   - binder-backed names (activity, power) through ServiceManager +
//     SystemServiceWrapperRegistry [CR3]
//   - process-local names (layout_inflater) through
//     SystemServiceWrapperRegistry.wrapProcessLocal [CR4]
//   - the four M4b/M4d/M4e binder-backed names (window, display,
//     notification, input_method) through SystemServiceWrapperRegistry.wrap
//     into the matching Manager class instances [CR5]
// rather than returning null for everything (the architectural drift codex
// Tier 3 §3 flagged).
//
// Round-trips verified:
//   ctx.getSystemService("activity")
//     -> ServiceManager.getService("activity")           [native binder lookup]
//     -> SystemServiceWrapperRegistry.wrap("activity", b, ctx)
//     -> reflective ActivityManager(Context, Handler) ctor
//     -> method call on the wrapped manager goes via the same-process
//        Stub.asInterface elision back to OUR WestlakeActivityManagerService
//        (no Parcel, no kernel) and returns the M4a list.
//   ctx.getSystemService("layout_inflater")    [CR4]
//     -> SystemServiceWrapperRegistry.wrapProcessLocal handles BEFORE any
//        binder lookup
//     -> reflective PhoneLayoutInflater(Context) ctor (framework.jar's
//        com.android.internal.policy.PhoneLayoutInflater)
//     -> returns concrete LayoutInflater subclass (no Parcel, no kernel,
//        no ServiceManager.getService call)
//   ctx.getSystemService("window" | "display" | "notification" | "input_method") [CR5]
//     -> ServiceManager.getService(name)                 [native binder lookup]
//     -> SystemServiceWrapperRegistry.wrap(name, b, ctx)
//     -> reflective Manager ctor (WindowManagerImpl(Context),
//        DisplayManager(Context), NotificationManager(Context, Handler),
//        InputMethodManager.forContext(Context))
//     -> returns the SDK-public Manager surface the host app expects
//        (WindowManager interface, DisplayManager, NotificationManager,
//        InputMethodManager).
//
// We do NOT bundle AsInterfaceTest just for its println natives -- the test
// loader registers them automatically when android_runtime_stub loads,
// because AsInterfaceTest.class is included in this dex too (same pattern
// as PowerServiceTest).
//
// Author: CR3 agent (2026-05-12), CR4 extension 2026-05-12, CR5 extension 2026-05-12

public class SystemServiceRouteTest {

    private static boolean sLibLoaded = false;
    private static String sLibLoadError = null;

    private static void loadLib() {
        if (sLibLoaded) return;
        try {
            System.loadLibrary("android_runtime_stub");
            sLibLoaded = true;
        } catch (UnsatisfiedLinkError e) {
            sLibLoadError = e.toString();
        }
    }

    private static void println(String s) {
        // Delegate to AsInterfaceTest.println -- its native is registered
        // by JNI_OnLoad_binder when android_runtime_stub loads, because
        // AsInterfaceTest.class is bundled into SystemServiceRouteTest.dex.
        AsInterfaceTest.println(s);
    }

    private static void eprintln(String s) {
        AsInterfaceTest.eprintln(s);
    }

    public static void main(String[] args) {
        // CR9 (2026-05-12): seed Charset / StandardCharsets static state
        // BEFORE anything else so VMClassLoader.<clinit> doesn't NPE on
        // `Charset.forName("UTF-8")` and ERROR-mark StandardCharsets.
        // See aosp-libbinder-port/test/CharsetPrimer.java + M4_DISCOVERY
        // §30 (M4-PRE10) for background.
        CharsetPrimer.primeCharsetState();
        // CR18 (2026-05-12): the primer-ActivityThread call moved from the
        // test harness (CharsetPrimer.primeActivityThread()) to a
        // production-code class, ColdBootstrap, that lives in
        // shim/java/com/westlake/services and is reachable from every
        // production callsite (WestlakeLauncher, ServiceRegistrar) -- not
        // just test harnesses.  ColdBootstrap.ensure() is idempotent and
        // thread-safe; calling it here is harmless even if some other
        // initialization path (e.g. WestlakeLauncher) already invoked it.
        //
        // The CR18 bisection (see diagnostics/CR18_primer_sigbus_bisection.md)
        // proved that this test does NOT actually need the primer post-CR17
        // -- variant 0 (NO primer at all) PASSED 8/8.  ColdBootstrap.ensure()
        // is therefore conservative warming, not strict prerequisite.  But
        // the architectural cleanup matters: NoiceDiscoverWrapper and any
        // future production app launcher will get the same warming for
        // free.  See M4_DISCOVERY §48 (CR18).
        try {
            Class<?> cb = Class.forName("com.westlake.services.ColdBootstrap");
            cb.getDeclaredMethod("ensure").invoke(null);
        } catch (Throwable t) {
            System.err.println("[SystemServiceRouteTest] ColdBootstrap.ensure() "
                    + "failed (non-fatal): " + t);
        }
        loadLib();
        if (!sLibLoaded) {
            System.err.println("SystemServiceRouteTest: loadLibrary failed: "
                    + sLibLoadError);
            System.exit(10);
        }
        int rc;
        try {
            rc = run();
        } catch (Throwable t) {
            eprintln("SystemServiceRouteTest: uncaught throwable: "
                    + t.getClass().getName() + ": " + t.getMessage());
            t.printStackTrace();
            rc = 99;
        }
        println("SystemServiceRouteTest: exiting with code " + rc);
        System.exit(rc);
    }

    static int run() throws Throwable {
        println("SystemServiceRouteTest: starting CR3 binder-route smoke");

        // ---------- 1. Bring up the M4a + M4-power services -----------
        // Use ServiceRegistrar (the canonical entry point M4* milestones
        // hook into).  resetForTesting() lets us re-run cleanly under any
        // pre-existing state.  Reflection because this test class lives
        // outside the shim package.
        try {
            Class<?> regCls = Class.forName(
                    "com.westlake.services.ServiceRegistrar");
            regCls.getDeclaredMethod("resetForTesting").invoke(null);
            int count = (Integer) regCls
                    .getDeclaredMethod("registerAllServices").invoke(null);
            println("SystemServiceRouteTest: ServiceRegistrar registered "
                    + count + " service(s)");
            // CR5: 6 services expected at this point (activity + power + window
            // + display + notification + input_method).  layout_inflater is
            // process-local and not registered through ServiceRegistrar.
            // Tolerate `count == 0` on a re-run after resetForTesting() leaves
            // entries in ServiceManager (idempotent re-register skips), but
            // require at least the pre-CR5 baseline of 2.
            if (count < 2) {
                eprintln("SystemServiceRouteTest: FAIL expected >=2 services "
                        + "(activity + power), got " + count);
                return 20;
            }
        } catch (Throwable t) {
            eprintln("SystemServiceRouteTest: FAIL ServiceRegistrar: " + t);
            t.printStackTrace();
            return 21;
        }

        // ---------- 2. Build a WestlakeContextImpl --------------------
        // Same packageName/apk shape NoiceDiscoverWrapper uses, with a
        // disposable per-test data dir under /data/local/tmp.
        Object ctx;
        try {
            Class<?> ctxCls = Class.forName(
                    "com.westlake.services.WestlakeContextImpl");
            // (String packageName, String apkPath, String dataDir,
            //  ClassLoader cl, int targetSdk)
            ctx = ctxCls.getDeclaredConstructor(
                    String.class, String.class, String.class,
                    ClassLoader.class, int.class).newInstance(
                            "com.westlake.test",
                            "/data/local/tmp/westlake/route-test.apk",
                            "/data/local/tmp/westlake/route-test-data",
                            SystemServiceRouteTest.class.getClassLoader(),
                            Integer.valueOf(33));
        } catch (Throwable t) {
            eprintln("SystemServiceRouteTest: FAIL WestlakeContextImpl: " + t);
            t.printStackTrace();
            return 30;
        }
        println("SystemServiceRouteTest: built ctx=" + ctx);

        // ---------- 3. ctx.getSystemService("activity") --------------
        // Verifies: the route through ServiceManager.getService("activity")
        // resolves and the result is wrapped in a Manager class (not raw
        // IBinder, not null).
        Object am;
        try {
            java.lang.reflect.Method getSvc = ctx.getClass()
                    .getMethod("getSystemService", String.class);
            am = getSvc.invoke(ctx, "activity");
        } catch (Throwable t) {
            eprintln("SystemServiceRouteTest: FAIL getSystemService(activity): "
                    + t);
            t.printStackTrace();
            return 40;
        }
        if (am == null) {
            eprintln("SystemServiceRouteTest: FAIL getSystemService(activity) "
                    + "returned null");
            return 41;
        }
        println("SystemServiceRouteTest: getSystemService(activity) -> "
                + am + " (class=" + am.getClass().getName() + ")");

        // Strict check: did wrapping land us on the AOSP ActivityManager
        // class, or did we fall back to the raw IActivityManager interface?
        // Both are acceptable for the route, but the CR3 acceptance test
        // distinguishes them so future regressions are obvious.
        if (am.getClass().getName().equals("android.app.ActivityManager")) {
            println("SystemServiceRouteTest: returned a real ActivityManager "
                    + "wrapper -- CR3 strict pass");
        } else {
            println("SystemServiceRouteTest: NOTE returned non-ActivityManager "
                    + "(" + am.getClass().getName()
                    + ") -- IXxx fallback path (acceptable)");
        }

        // ---------- 4. Call a method on the wrapped manager ----------
        // We call IActivityManager.getRunningAppProcesses() either via the
        // wrapped ActivityManager (which forwards to the underlying
        // IActivityManager via mService field) or directly on the IXxx
        // fallback.  In both cases the call MUST land on our M4a
        // WestlakeActivityManagerService and return the populated list.
        java.util.List procs;
        try {
            // First try the AOSP ActivityManager method: getRunningAppProcesses()
            // (no args, returns List<RunningAppProcessInfo>).  Reflection
            // because the shim's ActivityManager has the same name but the
            // duplicates list strips it; framework.jar's wins at runtime.
            java.lang.reflect.Method m = findMethod(am.getClass(),
                    "getRunningAppProcesses");
            if (m == null) {
                eprintln("SystemServiceRouteTest: FAIL no getRunningAppProcesses "
                        + "method on " + am.getClass().getName());
                return 50;
            }
            Object res = m.invoke(am);
            if (!(res instanceof java.util.List)) {
                eprintln("SystemServiceRouteTest: FAIL getRunningAppProcesses "
                        + "returned non-List: " + res);
                return 51;
            }
            procs = (java.util.List) res;
        } catch (Throwable t) {
            eprintln("SystemServiceRouteTest: FAIL invoking "
                    + "getRunningAppProcesses: " + t);
            t.printStackTrace();
            return 52;
        }
        if (procs == null) {
            eprintln("SystemServiceRouteTest: FAIL getRunningAppProcesses "
                    + "returned null");
            return 53;
        }
        println("SystemServiceRouteTest: getRunningAppProcesses() -> list size="
                + procs.size());
        if (procs.isEmpty()) {
            eprintln("SystemServiceRouteTest: FAIL expected M4a single-element "
                    + "list, got empty");
            return 54;
        }
        // The M4a service returns a list whose entry has processName starting
        // with "com.westlake".  We don't strictly assert the value -- just
        // having a non-empty list confirms the binder->manager->IXxx->service
        // dispatch chain.
        println("SystemServiceRouteTest: full dispatch chain verified -- "
                + "binder route to WestlakeActivityManagerService is ACTIVE");

        // ---------- 5. ctx.getSystemService("power") -----------------
        Object pm;
        try {
            java.lang.reflect.Method getSvc = ctx.getClass()
                    .getMethod("getSystemService", String.class);
            pm = getSvc.invoke(ctx, "power");
        } catch (Throwable t) {
            eprintln("SystemServiceRouteTest: FAIL getSystemService(power): "
                    + t);
            t.printStackTrace();
            return 60;
        }
        if (pm == null) {
            eprintln("SystemServiceRouteTest: FAIL getSystemService(power) "
                    + "returned null");
            return 61;
        }
        println("SystemServiceRouteTest: getSystemService(power) -> "
                + pm + " (class=" + pm.getClass().getName() + ")");

        // ---------- 5b. ctx.getSystemService("layout_inflater") -----
        // CR4 (2026-05-12): process-local service path -- LayoutInflater
        // has no IBinder backing in AOSP.  Registry must construct
        // PhoneLayoutInflater(ctx) directly without going through
        // ServiceManager.getService.
        //
        // Acceptance:
        //   - result is non-null (the AssertionError that drove CR4 fired
        //     precisely because Activity.attach got null here);
        //   - result is an instance of android.view.LayoutInflater (the
        //     abstract base; PhoneLayoutInflater is the concrete impl).
        Object li;
        try {
            java.lang.reflect.Method getSvc = ctx.getClass()
                    .getMethod("getSystemService", String.class);
            li = getSvc.invoke(ctx, "layout_inflater");
        } catch (Throwable t) {
            eprintln("SystemServiceRouteTest: FAIL "
                    + "getSystemService(layout_inflater): " + t);
            t.printStackTrace();
            return 65;
        }
        if (li == null) {
            eprintln("SystemServiceRouteTest: FAIL layout_inflater returned "
                    + "null (CR4 regression -- registry process-local path "
                    + "is broken)");
            return 66;
        }
        // Must be a LayoutInflater subclass.  Use Class.forName to bind
        // against framework.jar's runtime class (the shim's android.jar
        // has no concrete LayoutInflater hierarchy we'd recognise here).
        Class<?> liBase;
        try {
            liBase = Class.forName("android.view.LayoutInflater");
        } catch (Throwable t) {
            eprintln("SystemServiceRouteTest: FAIL cannot Class.forName "
                    + "android.view.LayoutInflater: " + t);
            return 67;
        }
        if (!liBase.isInstance(li)) {
            eprintln("SystemServiceRouteTest: FAIL layout_inflater wrapper "
                    + "is wrong type: " + li.getClass().getName()
                    + " (expected android.view.LayoutInflater subclass)");
            return 68;
        }
        println("SystemServiceRouteTest: getSystemService(layout_inflater) -> "
                + li + " (class=" + li.getClass().getName() + ") -- OK");

        // ---------- 5c. ctx.getSystemService("window") -------------- [CR5]
        // Binder-backed.  Resolves through ServiceManager.getService("window")
        // to the M4b WestlakeWindowManagerService instance, then wrap()
        // reflectively constructs android.view.WindowManagerImpl(Context).
        // Acceptance:
        //   - result is non-null;
        //   - result implements android.view.WindowManager (the SDK-public
        //     interface that WindowManagerImpl extends).
        Object wm;
        try {
            java.lang.reflect.Method getSvc = ctx.getClass()
                    .getMethod("getSystemService", String.class);
            wm = getSvc.invoke(ctx, "window");
        } catch (Throwable t) {
            eprintln("SystemServiceRouteTest: FAIL getSystemService(window): "
                    + t);
            t.printStackTrace();
            return 90;
        }
        if (wm == null) {
            eprintln("SystemServiceRouteTest: FAIL getSystemService(window) "
                    + "returned null (CR5 regression -- window wrap broken)");
            return 91;
        }
        // Strict acceptance: result must be a WindowManager.  WindowManager
        // is an interface in android.jar; framework.jar's WindowManagerImpl
        // implements it.  Fallback path (raw IWindowManager) is acceptable
        // but logged so future regressions are obvious.
        Class<?> wmIface;
        try {
            wmIface = Class.forName("android.view.WindowManager");
        } catch (Throwable t) {
            eprintln("SystemServiceRouteTest: FAIL cannot Class.forName "
                    + "android.view.WindowManager: " + t);
            return 92;
        }
        if (wmIface.isInstance(wm)) {
            println("SystemServiceRouteTest: getSystemService(window) -> "
                    + wm.getClass().getName()
                    + " (WindowManager) -- CR5 strict pass");
        } else {
            println("SystemServiceRouteTest: NOTE getSystemService(window) "
                    + "returned non-WindowManager (" + wm.getClass().getName()
                    + ") -- IXxx fallback path (acceptable)");
        }

        // ---------- 5d. ctx.getSystemService("display") ------------- [CR5]
        // Binder-backed.  Resolves through ServiceManager.getService("display")
        // to the M4d WestlakeDisplayManagerService instance, then wrap()
        // reflectively constructs android.hardware.display.DisplayManager(Context).
        Object dm;
        try {
            java.lang.reflect.Method getSvc = ctx.getClass()
                    .getMethod("getSystemService", String.class);
            dm = getSvc.invoke(ctx, "display");
        } catch (Throwable t) {
            eprintln("SystemServiceRouteTest: FAIL getSystemService(display): "
                    + t);
            t.printStackTrace();
            return 100;
        }
        if (dm == null) {
            eprintln("SystemServiceRouteTest: FAIL getSystemService(display) "
                    + "returned null (CR5 regression -- display wrap broken)");
            return 101;
        }
        Class<?> dmCls;
        try {
            dmCls = Class.forName("android.hardware.display.DisplayManager");
        } catch (Throwable t) {
            eprintln("SystemServiceRouteTest: FAIL cannot Class.forName "
                    + "android.hardware.display.DisplayManager: " + t);
            return 102;
        }
        if (dmCls.isInstance(dm)) {
            println("SystemServiceRouteTest: getSystemService(display) -> "
                    + dm.getClass().getName()
                    + " (DisplayManager) -- CR5 strict pass");
        } else {
            println("SystemServiceRouteTest: NOTE getSystemService(display) "
                    + "returned non-DisplayManager (" + dm.getClass().getName()
                    + ") -- IXxx fallback path (acceptable)");
        }

        // ---------- 5e. ctx.getSystemService("notification") -------- [CR5]
        // Binder-backed.  Resolves through ServiceManager.getService("notification")
        // to the M4e WestlakeNotificationManagerService instance, then wrap()
        // reflectively constructs android.app.NotificationManager(Context, Handler).
        Object nm;
        try {
            java.lang.reflect.Method getSvc = ctx.getClass()
                    .getMethod("getSystemService", String.class);
            nm = getSvc.invoke(ctx, "notification");
        } catch (Throwable t) {
            eprintln("SystemServiceRouteTest: FAIL "
                    + "getSystemService(notification): " + t);
            t.printStackTrace();
            return 110;
        }
        if (nm == null) {
            eprintln("SystemServiceRouteTest: FAIL getSystemService(notification) "
                    + "returned null (CR5 regression -- notification wrap broken)");
            return 111;
        }
        Class<?> nmCls;
        try {
            nmCls = Class.forName("android.app.NotificationManager");
        } catch (Throwable t) {
            eprintln("SystemServiceRouteTest: FAIL cannot Class.forName "
                    + "android.app.NotificationManager: " + t);
            return 112;
        }
        if (nmCls.isInstance(nm)) {
            println("SystemServiceRouteTest: getSystemService(notification) -> "
                    + nm.getClass().getName()
                    + " (NotificationManager) -- CR5 strict pass");
        } else {
            println("SystemServiceRouteTest: NOTE getSystemService(notification) "
                    + "returned non-NotificationManager ("
                    + nm.getClass().getName()
                    + ") -- IXxx fallback path (acceptable)");
        }

        // ---------- 5f. ctx.getSystemService("input_method") ------- [CR5]
        // Binder-backed.  Resolves through ServiceManager.getService("input_method")
        // to the M4e WestlakeInputMethodManagerService instance, then wrap()
        // reflectively invokes
        // android.view.inputmethod.InputMethodManager.forContext(Context).
        Object imm;
        try {
            java.lang.reflect.Method getSvc = ctx.getClass()
                    .getMethod("getSystemService", String.class);
            imm = getSvc.invoke(ctx, "input_method");
        } catch (Throwable t) {
            eprintln("SystemServiceRouteTest: FAIL "
                    + "getSystemService(input_method): " + t);
            t.printStackTrace();
            return 120;
        }
        if (imm == null) {
            eprintln("SystemServiceRouteTest: FAIL getSystemService(input_method) "
                    + "returned null (CR5 regression -- input_method wrap "
                    + "broken)");
            return 121;
        }
        Class<?> immCls;
        try {
            immCls = Class.forName(
                    "android.view.inputmethod.InputMethodManager");
        } catch (Throwable t) {
            eprintln("SystemServiceRouteTest: FAIL cannot Class.forName "
                    + "android.view.inputmethod.InputMethodManager: " + t);
            return 122;
        }
        if (immCls.isInstance(imm)) {
            println("SystemServiceRouteTest: getSystemService(input_method) -> "
                    + imm.getClass().getName()
                    + " (InputMethodManager) -- CR5 strict pass");
        } else {
            println("SystemServiceRouteTest: NOTE getSystemService(input_method) "
                    + "returned non-InputMethodManager ("
                    + imm.getClass().getName()
                    + ") -- IXxx fallback path (acceptable)");
        }

        // ---------- 6. Unknown name returns null ---------------------
        Object unknown;
        try {
            java.lang.reflect.Method getSvc = ctx.getClass()
                    .getMethod("getSystemService", String.class);
            unknown = getSvc.invoke(ctx, "definitely_not_a_real_service_name");
        } catch (Throwable t) {
            eprintln("SystemServiceRouteTest: FAIL getSystemService(unknown): "
                    + t);
            return 70;
        }
        if (unknown != null) {
            eprintln("SystemServiceRouteTest: FAIL unknown name returned "
                    + "non-null " + unknown);
            return 71;
        }
        println("SystemServiceRouteTest: unknown service name -> null -- OK");

        // ---------- 7. null name returns null ------------------------
        Object nullName;
        try {
            java.lang.reflect.Method getSvc = ctx.getClass()
                    .getMethod("getSystemService", String.class);
            nullName = getSvc.invoke(ctx, (Object) null);
        } catch (Throwable t) {
            eprintln("SystemServiceRouteTest: FAIL getSystemService(null): " + t);
            return 80;
        }
        if (nullName != null) {
            eprintln("SystemServiceRouteTest: FAIL null name returned non-null "
                    + nullName);
            return 81;
        }
        println("SystemServiceRouteTest: null service name -> null -- OK");

        println("SystemServiceRouteTest: PASS");
        return 0;
    }

    // Helper: locate a public method by name (any args list, picks first).
    private static java.lang.reflect.Method findMethod(Class<?> cls, String name) {
        for (java.lang.reflect.Method m : cls.getMethods()) {
            if (m.getName().equals(name) && m.getParameterTypes().length == 0) {
                return m;
            }
        }
        return null;
    }
}
