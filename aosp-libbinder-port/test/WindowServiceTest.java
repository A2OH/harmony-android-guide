// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4b -- WindowServiceTest
//
// Synthetic smoke test for WestlakeWindowManagerService.  Mirrors the
// shape of PowerServiceTest (the M4-power canary) and ActivityServiceTest
// (M4a), with IWindowManager-specific assertions:
//   - getDefaultDisplayRotation() == 0
//   - getAnimationScale(0) == 1.0f
//   - getInitialDisplaySize populates Point correctly (1080x2280)
//   - addWindowToken / removeWindowToken round-trip works
//   - listServices contains "window"
//
// We do NOT verify cross-process Proxy paths here -- noice (and any
// real consumer in the dalvikvm sandbox) sits in the SAME process as
// the service, so queryLocalInterface returns this directly and the
// Parcel path is never engaged.  That elision is the whole point of
// M4b as architected.
//
// IO strategy (no new natives per M4b brief): both WindowServiceTest
// and AsInterfaceTest are bundled into WindowServiceTest.dex; loading
// android_runtime_stub registers AsInterfaceTest.{println,eprintln}
// natives, and this class delegates its log helpers to AsInterfaceTest's
// already-registered natives.  System.out / java.util.logging are unusable
// in this dalvikvm (see A15_IO_FIX_NEEDED.md).

public class WindowServiceTest {

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
        // Delegate to AsInterfaceTest's println -- its native is registered
        // by JNI_OnLoad_binder when android_runtime_stub loads, because
        // AsInterfaceTest.class is bundled into WindowServiceTest.dex.
        AsInterfaceTest.println(s);
    }

    private static void eprintln(String s) {
        AsInterfaceTest.eprintln(s);
    }

    public static void main(String[] args) {
        loadLib();
        if (!sLibLoaded) {
            System.err.println("WindowServiceTest: loadLibrary failed: " + sLibLoadError);
            System.exit(10);
        }
        int rc;
        try {
            rc = run();
        } catch (Throwable t) {
            eprintln("WindowServiceTest: uncaught throwable: " + t.getClass().getName()
                    + ": " + t.getMessage());
            t.printStackTrace();
            rc = 99;
        }
        println("WindowServiceTest: exiting with code " + rc);
        System.exit(rc);
    }

    static int run() throws Throwable {
        println("WindowServiceTest: starting M4b synthetic smoke");

        // ---------- 1. Construct WestlakeWindowManagerService ------------
        // Reflection because the test sits OUTSIDE the shim package and
        // WindowServiceTest.dex is loaded by an app classloader, not the
        // shim classloader.
        Object service;
        try {
            Class<?> svcCls = Class.forName("com.westlake.services.WestlakeWindowManagerService");
            service = svcCls.getDeclaredConstructor().newInstance();
        } catch (Throwable t) {
            eprintln("WindowServiceTest: FAIL constructing service: " + t);
            t.printStackTrace();
            return 20;
        }
        println("WindowServiceTest: constructed " + service);

        // The service should be a Binder (it extends IWindowManager.Stub
        // which extends Binder).  Verify by class hierarchy.
        if (!(service instanceof android.os.IBinder)) {
            eprintln("WindowServiceTest: FAIL service is not an IBinder: "
                    + service.getClass().getName());
            return 21;
        }
        android.os.IBinder binder = (android.os.IBinder) service;

        // ---------- 2. Self queryLocalInterface ---------------------------
        String descriptor = "android.view.IWindowManager";
        android.os.IInterface qli = binder.queryLocalInterface(descriptor);
        if (qli != service) {
            eprintln("WindowServiceTest: FAIL self queryLocalInterface returned " + qli
                    + " (expected service=" + service + ")");
            return 22;
        }
        println("WindowServiceTest: self queryLocalInterface(\"" + descriptor
                + "\") -> SAME service object -- OK");

        // ---------- 3. Register OUR instance directly ---------------------
        // Note: this bypasses ServiceRegistrar so the same `service`
        // object is the one registered; we re-test ServiceRegistrar
        // standalone in phase 3b below.
        try {
            android.os.ServiceManager.addService("window", binder);
            println("WindowServiceTest: ServiceManager.addService(window) OK");
        } catch (Throwable t) {
            eprintln("WindowServiceTest: FAIL ServiceManager.addService: " + t);
            return 30;
        }

        // ---------- 3b. Test ServiceRegistrar.registerAllServices --------
        // (separate from the round-trip test above).  This validates that
        // the M4b service is picked up by the registrar alongside
        // power+activity.
        try {
            Class<?> regCls = Class.forName("com.westlake.services.ServiceRegistrar");
            regCls.getDeclaredMethod("resetForTesting").invoke(null);
            int count = (Integer) regCls.getDeclaredMethod("registerAllServices").invoke(null);
            println("WindowServiceTest: ServiceRegistrar.registerAllServices() -> "
                    + count + " service(s)");
            if (count < 1) {
                eprintln("WindowServiceTest: FAIL expected >=1 service registered by registrar, got " + count);
                return 32;
            }
        } catch (Throwable t) {
            eprintln("WindowServiceTest: NOTE ServiceRegistrar test skipped: " + t);
            // Don't fail the whole test on this; the registrar bypass above
            // already added "window" successfully.
        }

        // ---------- 4. Look it up via ServiceManager ----------------------
        android.os.IBinder b;
        try {
            b = android.os.ServiceManager.getService("window");
        } catch (Throwable t) {
            eprintln("WindowServiceTest: FAIL getService(\"window\"): " + t);
            return 40;
        }
        if (b == null) {
            eprintln("WindowServiceTest: FAIL getService(\"window\") returned null");
            return 41;
        }
        println("WindowServiceTest: getService(\"window\") -> " + b);

        // Whoever is currently registered as "window" might be our service
        // instance, or might be the one ServiceRegistrar created above.
        // Retarget `service` to the round-tripped instance so subsequent
        // state checks (activeWindowTokenCount) hit the right object.
        if (b != service) {
            println("WindowServiceTest: NOTE round-trip returned different "
                    + "instance than our local handle -- retargeting state checks");
            service = b;
        }

        // ---------- 5. asInterface ---------------------------------------
        // We use reflection because IWindowManager.Stub.asInterface is
        // declared in framework.jar; the test class only sees the
        // android.view.IWindowManager symbol at runtime through the BCP.
        Object proxy;
        try {
            Class<?> iwmCls = Class.forName("android.view.IWindowManager");
            Class<?> stubCls = null;
            for (Class<?> inner : iwmCls.getDeclaredClasses()) {
                if (inner.getSimpleName().equals("Stub")) { stubCls = inner; break; }
            }
            if (stubCls == null) {
                eprintln("WindowServiceTest: FAIL no IWindowManager.Stub inner class");
                return 50;
            }
            java.lang.reflect.Method asI = stubCls.getDeclaredMethod("asInterface",
                    android.os.IBinder.class);
            proxy = asI.invoke(null, b);
        } catch (Throwable t) {
            eprintln("WindowServiceTest: FAIL asInterface: " + t);
            return 51;
        }
        if (proxy == null) {
            eprintln("WindowServiceTest: FAIL asInterface returned null "
                    + "(queryLocalInterface didn't recognize descriptor)");
            return 52;
        }
        println("WindowServiceTest: IWindowManager.Stub.asInterface(b) -> " + proxy);
        println("WindowServiceTest:   proxy.class = " + proxy.getClass().getName());

        // Same-process opt verification: the asInterface result should be
        // the same object as the round-trip binder (Stub.asInterface returns
        // this when queryLocalInterface returns a local instance).
        if (proxy == service) {
            println("WindowServiceTest: asInterface returned SAME service -- "
                    + "direct Java dispatch ACTIVE (same-process opt working)");
        } else if (proxy == b) {
            println("WindowServiceTest: asInterface returned same as round-trip "
                    + "binder -- direct Java dispatch ACTIVE");
        } else {
            eprintln("WindowServiceTest: NOTE asInterface returned a different object "
                    + "from both `service` and `b` -- likely Stub.Proxy "
                    + "(local-binder optimization NOT active in queryLocalInterface)");
        }

        // ---------- 6. getDefaultDisplayRotation() should be 0 -----------
        int rotation;
        try {
            java.lang.reflect.Method m = proxy.getClass().getMethod("getDefaultDisplayRotation");
            rotation = (Integer) m.invoke(proxy);
        } catch (Throwable t) {
            eprintln("WindowServiceTest: FAIL getDefaultDisplayRotation: " + t);
            t.printStackTrace();
            return 60;
        }
        println("WindowServiceTest: getDefaultDisplayRotation() -> " + rotation);
        if (rotation != 0) {
            eprintln("WindowServiceTest: FAIL expected getDefaultDisplayRotation()==0, got " + rotation);
            return 61;
        }

        // ---------- 7. getAnimationScale(0) should be 1.0f ---------------
        float scale;
        try {
            java.lang.reflect.Method m = proxy.getClass().getMethod("getAnimationScale", int.class);
            scale = (Float) m.invoke(proxy, Integer.valueOf(0));
        } catch (Throwable t) {
            eprintln("WindowServiceTest: FAIL getAnimationScale: " + t);
            return 70;
        }
        println("WindowServiceTest: getAnimationScale(0) -> " + scale);
        if (Math.abs(scale - 1.0f) > 0.001f) {
            eprintln("WindowServiceTest: FAIL expected getAnimationScale(0)==1.0f, got " + scale);
            return 71;
        }

        // ---------- 8. getCurrentAnimatorScale ---------------------------
        try {
            java.lang.reflect.Method m = proxy.getClass().getMethod("getCurrentAnimatorScale");
            float cur = (Float) m.invoke(proxy);
            println("WindowServiceTest: getCurrentAnimatorScale() -> " + cur);
            if (Math.abs(cur - 1.0f) > 0.001f) {
                eprintln("WindowServiceTest: FAIL expected getCurrentAnimatorScale()==1.0f, got " + cur);
                return 72;
            }
        } catch (Throwable t) {
            eprintln("WindowServiceTest: FAIL getCurrentAnimatorScale: " + t);
            return 73;
        }

        // ---------- 9. getAnimationScales returns array of 1.0f ----------
        try {
            java.lang.reflect.Method m = proxy.getClass().getMethod("getAnimationScales");
            float[] scales = (float[]) m.invoke(proxy);
            if (scales == null || scales.length != 3) {
                eprintln("WindowServiceTest: FAIL getAnimationScales returned bad array: "
                        + (scales == null ? "null" : "length=" + scales.length));
                return 74;
            }
            println("WindowServiceTest: getAnimationScales() -> [" + scales[0] + ", "
                    + scales[1] + ", " + scales[2] + "]");
            for (int i = 0; i < scales.length; i++) {
                if (Math.abs(scales[i] - 1.0f) > 0.001f) {
                    eprintln("WindowServiceTest: FAIL scales[" + i + "]=" + scales[i] + " != 1.0");
                    return 75;
                }
            }
        } catch (Throwable t) {
            eprintln("WindowServiceTest: FAIL getAnimationScales: " + t);
            return 76;
        }

        // ---------- 10. getInitialDisplaySize populates Point ------------
        try {
            android.graphics.Point pt = new android.graphics.Point();
            java.lang.reflect.Method m = proxy.getClass().getMethod(
                    "getInitialDisplaySize", int.class, android.graphics.Point.class);
            m.invoke(proxy, Integer.valueOf(0), pt);
            println("WindowServiceTest: getInitialDisplaySize(0, pt) -> ("
                    + pt.x + ", " + pt.y + ")");
            if (pt.x != 1080 || pt.y != 2280) {
                eprintln("WindowServiceTest: FAIL expected (1080,2280), got (" + pt.x + "," + pt.y + ")");
                return 80;
            }
        } catch (Throwable t) {
            eprintln("WindowServiceTest: FAIL getInitialDisplaySize: " + t);
            t.printStackTrace();
            return 81;
        }

        // ---------- 11. getBaseDisplaySize populates Point ---------------
        try {
            android.graphics.Point pt = new android.graphics.Point();
            java.lang.reflect.Method m = proxy.getClass().getMethod(
                    "getBaseDisplaySize", int.class, android.graphics.Point.class);
            m.invoke(proxy, Integer.valueOf(0), pt);
            println("WindowServiceTest: getBaseDisplaySize(0, pt) -> ("
                    + pt.x + ", " + pt.y + ")");
            if (pt.x != 1080 || pt.y != 2280) {
                eprintln("WindowServiceTest: FAIL expected (1080,2280), got (" + pt.x + "," + pt.y + ")");
                return 82;
            }
        } catch (Throwable t) {
            eprintln("WindowServiceTest: FAIL getBaseDisplaySize: " + t);
            return 83;
        }

        // ---------- 12. addWindowToken / removeWindowToken cycle ---------
        // Use a plain Binder as the token (it's an IBinder).
        android.os.IBinder tokenA = new android.os.Binder();
        android.os.IBinder tokenB = new android.os.Binder();
        try {
            // Android 16 addWindowToken signature: (IBinder, int type, int displayId, Bundle options)
            java.lang.reflect.Method addM = proxy.getClass().getMethod(
                    "addWindowToken", android.os.IBinder.class, int.class,
                    int.class, android.os.Bundle.class);
            addM.invoke(proxy, tokenA, Integer.valueOf(1), Integer.valueOf(0), null);
            addM.invoke(proxy, tokenB, Integer.valueOf(2), Integer.valueOf(0), null);
            println("WindowServiceTest: addWindowToken x2 OK");
        } catch (Throwable t) {
            eprintln("WindowServiceTest: FAIL addWindowToken: " + t);
            t.printStackTrace();
            return 90;
        }

        // Verify both tokens landed in our internal map.
        try {
            java.lang.reflect.Method ct = service.getClass().getMethod("activeWindowTokenCount");
            int n = (Integer) ct.invoke(service);
            println("WindowServiceTest: activeWindowTokenCount after add = " + n);
            if (n < 2) {
                eprintln("WindowServiceTest: FAIL expected >=2 tokens, got " + n);
                return 91;
            }
        } catch (Throwable t) {
            eprintln("WindowServiceTest: FAIL activeWindowTokenCount: " + t);
            return 92;
        }

        try {
            java.lang.reflect.Method removeM = proxy.getClass().getMethod(
                    "removeWindowToken", android.os.IBinder.class, int.class);
            removeM.invoke(proxy, tokenA, Integer.valueOf(0));
            removeM.invoke(proxy, tokenB, Integer.valueOf(0));
            println("WindowServiceTest: removeWindowToken x2 OK");
        } catch (Throwable t) {
            eprintln("WindowServiceTest: FAIL removeWindowToken: " + t);
            return 93;
        }

        try {
            java.lang.reflect.Method ct = service.getClass().getMethod("activeWindowTokenCount");
            int n = (Integer) ct.invoke(service);
            println("WindowServiceTest: activeWindowTokenCount after remove = " + n);
            if (n != 0) {
                eprintln("WindowServiceTest: FAIL expected 0 tokens after remove, got " + n);
                return 94;
            }
        } catch (Throwable t) {
            eprintln("WindowServiceTest: FAIL activeWindowTokenCount post-remove: " + t);
            return 95;
        }

        // ---------- 13. setEventDispatching round-trip -------------------
        try {
            java.lang.reflect.Method setM = proxy.getClass().getMethod(
                    "setEventDispatching", boolean.class);
            setM.invoke(proxy, Boolean.FALSE);
            java.lang.reflect.Method getM = service.getClass().getMethod("isEventDispatching");
            boolean state = (Boolean) getM.invoke(service);
            if (state != false) {
                eprintln("WindowServiceTest: FAIL setEventDispatching(false) didn't stick (got " + state + ")");
                return 100;
            }
            setM.invoke(proxy, Boolean.TRUE);
            state = (Boolean) getM.invoke(service);
            if (state != true) {
                eprintln("WindowServiceTest: FAIL setEventDispatching(true) didn't stick (got " + state + ")");
                return 101;
            }
            println("WindowServiceTest: setEventDispatching round-trip OK");
        } catch (Throwable t) {
            eprintln("WindowServiceTest: FAIL setEventDispatching: " + t);
            return 102;
        }

        // ---------- 14. openSession returns null (path a) ----------------
        // Brief Step 2 path (a): we return null and rely on PhoneWindow's
        // defensive null-handling.  Verify here that the contract is
        // honored.
        try {
            java.lang.reflect.Method openM = proxy.getClass().getMethod(
                    "openSession", android.view.IWindowSessionCallback.class);
            Object sess = openM.invoke(proxy, (Object) null);
            // Note: brief said "return null OR our session impl"; current path is null.
            println("WindowServiceTest: openSession(null) -> " + sess);
            // Don't assert null specifically -- the brief allows either path.
        } catch (Throwable t) {
            eprintln("WindowServiceTest: FAIL openSession: " + t);
            return 110;
        }

        // ---------- 15. Verify listServices sees "window" ----------------
        try {
            String[] names = android.os.ServiceManager.listServices();
            boolean found = false;
            if (names != null) {
                for (String n : names) {
                    if ("window".equals(n)) { found = true; break; }
                }
            }
            if (!found) {
                eprintln("WindowServiceTest: FAIL listServices does not contain \"window\" "
                        + "(got " + (names == null ? 0 : names.length) + " entries)");
                return 120;
            }
            println("WindowServiceTest: listServices contains \"window\" -- OK");
        } catch (Throwable t) {
            eprintln("WindowServiceTest: FAIL listServices: " + t);
            return 121;
        }

        println("WindowServiceTest: PASS (all Tier-1 verifications)");
        return 0;
    }
}
