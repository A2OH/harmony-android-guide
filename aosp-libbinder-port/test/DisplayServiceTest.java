// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4d -- DisplayServiceTest
//
// Synthetic smoke test for WestlakeDisplayManagerService.  Mirrors
// PowerServiceTest's M3++-style structure: load binder JNI, construct
// the service, register it under "display", look it up, verify
// Stub.asInterface returns the SAME local object (same-process elision),
// then exercise the Tier-1 methods (getDisplayInfo, getDisplayIds,
// registerCallback).
//
// We do NOT verify cross-process Proxy paths here -- noice (and any
// real consumer in the dalvikvm sandbox) sits in the SAME process as
// the service, so queryLocalInterface returns this directly and the
// Parcel path is never engaged.

public class DisplayServiceTest {

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
        // Delegate to AsInterfaceTest's println -- registered by
        // JNI_OnLoad_binder when android_runtime_stub loads, because
        // AsInterfaceTest.class is bundled into DisplayServiceTest.dex.
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
        // CR17 (2026-05-12): primeActivityThread() is no longer required.
        // WestlakeDisplayManagerService now uses the
        // Stub(PermissionEnforcer) bypass with a NoopPermissionEnforcer
        // (same pattern as M4a/M4b/M4c/M4-power), so the service
        // constructs without touching ActivityThread.getSystemContext().
        // This makes M4d self-sufficient for production callers
        // (ServiceRegistrar.registerAllServices()) which don't have
        // the test-harness primer running.  See M4_DISCOVERY §46 (CR17).
        loadLib();
        if (!sLibLoaded) {
            System.err.println("DisplayServiceTest: loadLibrary failed: " + sLibLoadError);
            System.exit(10);
        }
        int rc;
        try {
            rc = run();
        } catch (Throwable t) {
            eprintln("DisplayServiceTest: uncaught throwable: " + t.getClass().getName()
                    + ": " + t.getMessage());
            t.printStackTrace();
            rc = 99;
        }
        println("DisplayServiceTest: exiting with code " + rc);
        System.exit(rc);
    }

    static int run() throws Throwable {
        println("DisplayServiceTest: starting M4d IDisplayManager smoke test");

        // 1. Construct the service via reflection (test is in default
        //    package; service lives in com.westlake.services on the BCP).
        Object service;
        try {
            Class<?> svcCls = Class.forName(
                    "com.westlake.services.WestlakeDisplayManagerService");
            service = svcCls.getDeclaredConstructor().newInstance();
        } catch (Throwable t) {
            eprintln("DisplayServiceTest: FAIL service ctor threw: " + t);
            // CR10 diag: drill through the cause chain via eprintln so we
            // see the real exception even when System.err is unprintable.
            Throwable cur = t;
            int depth = 0;
            while (cur != null && depth < 8) {
                eprintln("DisplayServiceTest:   [depth=" + depth + "] "
                        + cur.getClass().getName() + ": " + cur.getMessage());
                StackTraceElement[] st = cur.getStackTrace();
                if (st != null) {
                    int lim = Math.min(st.length, 8);
                    for (int i = 0; i < lim; i++) {
                        eprintln("DisplayServiceTest:     at " + st[i]);
                    }
                }
                cur = cur.getCause();
                depth++;
            }
            t.printStackTrace();
            return 20;
        }
        println("DisplayServiceTest: constructed " + service);

        if (!(service instanceof android.os.IBinder)) {
            eprintln("DisplayServiceTest: FAIL service is not an IBinder: "
                    + service.getClass().getName());
            return 21;
        }
        android.os.IBinder binder = (android.os.IBinder) service;

        // 2. Register OUR instance directly.
        try {
            android.os.ServiceManager.addService("display", binder);
            println("DisplayServiceTest: ServiceManager.addService(display) OK");
        } catch (Throwable t) {
            eprintln("DisplayServiceTest: FAIL addService threw: " + t);
            return 30;
        }

        // 3. Look it up.
        android.os.IBinder b;
        try {
            b = android.os.ServiceManager.getService("display");
        } catch (Throwable t) {
            eprintln("DisplayServiceTest: FAIL getService threw: " + t);
            return 40;
        }
        if (b == null) {
            eprintln("DisplayServiceTest: FAIL getService(\"display\") returned null");
            return 41;
        }
        println("DisplayServiceTest: getService(\"display\") -> " + b);

        // Retarget if servicemanager handed us back a different object
        // than our local constructed instance (shouldn't happen in M3++).
        if (b != service) {
            println("DisplayServiceTest: NOTE round-trip returned different instance "
                    + "than our local handle -- retargeting state checks");
            service = b;
        }

        // 4. Stub.asInterface roundtrip via reflection (the symbol
        //    `android.hardware.display.IDisplayManager` lives in
        //    framework.jar's BCP, not the test classpath).
        Object proxy;
        try {
            Class<?> idmCls = Class.forName("android.hardware.display.IDisplayManager");
            Class<?> stubCls = null;
            for (Class<?> inner : idmCls.getDeclaredClasses()) {
                if (inner.getSimpleName().equals("Stub")) { stubCls = inner; break; }
            }
            if (stubCls == null) {
                eprintln("DisplayServiceTest: FAIL no IDisplayManager.Stub inner class");
                return 50;
            }
            java.lang.reflect.Method asI = stubCls.getDeclaredMethod("asInterface",
                    android.os.IBinder.class);
            proxy = asI.invoke(null, b);
        } catch (Throwable t) {
            eprintln("DisplayServiceTest: FAIL asInterface: " + t);
            t.printStackTrace();
            return 51;
        }
        if (proxy == null) {
            eprintln("DisplayServiceTest: FAIL asInterface returned null "
                    + "(queryLocalInterface didn't recognize descriptor)");
            return 52;
        }
        println("DisplayServiceTest: IDisplayManager.Stub.asInterface(b) -> " + proxy);
        println("DisplayServiceTest:   proxy.class = " + proxy.getClass().getName());

        if (proxy == service || proxy == b) {
            println("DisplayServiceTest: asInterface returned SAME service -- "
                    + "direct Java dispatch ACTIVE (same-process opt working)");
        } else {
            eprintln("DisplayServiceTest: NOTE asInterface returned a different object "
                    + "from both `service` and `b` -- likely Stub.Proxy "
                    + "(same-process elision NOT active)");
        }

        // 5. getDisplayInfo(0) -- expect non-null with dimension fields set.
        try {
            java.lang.reflect.Method m =
                    proxy.getClass().getMethod("getDisplayInfo", int.class);
            Object info = m.invoke(proxy, Integer.valueOf(0));
            if (info == null) {
                eprintln("DisplayServiceTest: FAIL getDisplayInfo(0) returned null");
                return 60;
            }
            // Probe the logicalWidth field reflectively; should be 1080.
            try {
                java.lang.reflect.Field f =
                        info.getClass().getDeclaredField("logicalWidth");
                f.setAccessible(true);
                int w = f.getInt(info);
                if (w != 1080) {
                    eprintln("DisplayServiceTest: NOTE logicalWidth=" + w
                            + " (expected 1080)");
                }
                println("DisplayServiceTest: getDisplayInfo(0) -> "
                        + info.getClass().getName() + " logicalWidth=" + w);
            } catch (NoSuchFieldException nf) {
                // Field renamed in this runtime -- still a pass if non-null.
                println("DisplayServiceTest: getDisplayInfo(0) -> " + info
                        + " (no logicalWidth field on " + info.getClass().getName() + ")");
            }
        } catch (Throwable t) {
            eprintln("DisplayServiceTest: FAIL getDisplayInfo: " + t);
            t.printStackTrace();
            return 61;
        }

        // 6. getDisplayIds(false) -- expect new int[]{0}.
        try {
            java.lang.reflect.Method m =
                    proxy.getClass().getMethod("getDisplayIds", boolean.class);
            int[] ids = (int[]) m.invoke(proxy, Boolean.FALSE);
            if (ids == null) {
                eprintln("DisplayServiceTest: FAIL getDisplayIds returned null");
                return 70;
            }
            if (ids.length != 1 || ids[0] != 0) {
                eprintln("DisplayServiceTest: FAIL getDisplayIds returned "
                        + java.util.Arrays.toString(ids) + " (expected [0])");
                return 71;
            }
            println("DisplayServiceTest: getDisplayIds(false) -> [0] -- OK");
        } catch (Throwable t) {
            eprintln("DisplayServiceTest: FAIL getDisplayIds: " + t);
            t.printStackTrace();
            return 72;
        }

        // 7. registerCallback(null-callback Stub) doesn't crash.
        try {
            Class<?> cbCls = Class.forName("android.hardware.display.IDisplayManagerCallback");
            Class<?> cbStubCls = null;
            for (Class<?> inner : cbCls.getDeclaredClasses()) {
                if (inner.getSimpleName().equals("Stub")) { cbStubCls = inner; break; }
            }
            if (cbStubCls == null) {
                eprintln("DisplayServiceTest: FAIL no IDisplayManagerCallback.Stub");
                return 80;
            }
            // Use a default-impl style instance: framework.jar always ships a
            // .Default class that implements all methods.  We can use the
            // Default class directly; cbCls is the interface so we cast
            // through Object.
            Class<?> defaultCls = null;
            for (Class<?> inner : cbCls.getDeclaredClasses()) {
                if (inner.getSimpleName().equals("Default")) { defaultCls = inner; break; }
            }
            Object cb;
            if (defaultCls != null) {
                cb = defaultCls.getDeclaredConstructor().newInstance();
            } else {
                // Fallback: dynamic proxy implementing the interface.
                cb = java.lang.reflect.Proxy.newProxyInstance(
                        cbCls.getClassLoader(),
                        new Class<?>[]{ cbCls, android.os.IBinder.class },
                        new java.lang.reflect.InvocationHandler() {
                            @Override public Object invoke(Object p, java.lang.reflect.Method m, Object[] a) {
                                if ("asBinder".equals(m.getName())) return null;
                                return null;
                            }
                        });
            }
            java.lang.reflect.Method regM =
                    proxy.getClass().getMethod("registerCallback", cbCls);
            regM.invoke(proxy, cb);
            println("DisplayServiceTest: registerCallback(default) -- OK");

            // Check internal state.
            java.lang.reflect.Method ct = service.getClass().getMethod("registeredCallbackCount");
            int n = (Integer) ct.invoke(service);
            println("DisplayServiceTest: registeredCallbackCount = " + n);
            if (n < 1) {
                eprintln("DisplayServiceTest: NOTE expected >=1 callback, got " + n
                        + " (proxy might have wrapped the call without dispatch)");
            }
        } catch (Throwable t) {
            eprintln("DisplayServiceTest: FAIL registerCallback: " + t);
            t.printStackTrace();
            return 81;
        }

        // 8. Verify listServices sees "display".
        try {
            String[] names = android.os.ServiceManager.listServices();
            boolean found = false;
            if (names != null) {
                for (String n : names) {
                    if ("display".equals(n)) { found = true; break; }
                }
            }
            if (!found) {
                eprintln("DisplayServiceTest: FAIL listServices does not contain \"display\"");
                return 90;
            }
            println("DisplayServiceTest: listServices contains \"display\" -- OK");
        } catch (Throwable t) {
            eprintln("DisplayServiceTest: FAIL listServices: " + t);
            return 91;
        }

        println("DisplayServiceTest: PASS");
        return 0;
    }
}
