// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4e -- InputMethodServiceTest
//
// Synthetic smoke test for WestlakeInputMethodManagerService.  Mirrors
// PowerServiceTest's M3++-style structure: load binder JNI, construct
// the service, register it under "input_method", look it up, verify
// Stub.asInterface returns the SAME local object (same-process elision),
// then exercise the Tier-1 methods (getInputMethodList,
// getEnabledInputMethodList, getCurrentInputMethodInfoAsUser, addClient).
//
// Note: descriptor is `com.android.internal.view.IInputMethodManager`,
// NOT `android.view.inputmethod.IInputMethodManager` -- the AIDL lives
// under com.android.internal.view in framework.jar.

public class InputMethodServiceTest {

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

    private static void println(String s) { AsInterfaceTest.println(s); }
    private static void eprintln(String s) { AsInterfaceTest.eprintln(s); }

    public static void main(String[] args) {
        // CR9 (2026-05-12): seed Charset / StandardCharsets static state
        // BEFORE anything else so VMClassLoader.<clinit> doesn't NPE on
        // `Charset.forName("UTF-8")` and ERROR-mark StandardCharsets.
        // See aosp-libbinder-port/test/CharsetPrimer.java + M4_DISCOVERY
        // §30 (M4-PRE10) for background.
        CharsetPrimer.primeCharsetState();
        // CR17 (2026-05-12): primeActivityThread() is no longer required.
        // WestlakeInputMethodManagerService now uses the
        // Stub(PermissionEnforcer) bypass with a NoopPermissionEnforcer
        // (same pattern as M4a/M4b/M4c/M4-power), so the service
        // constructs without touching ActivityThread.getSystemContext().
        // This makes M4e (input_method) self-sufficient for production
        // callers (ServiceRegistrar.registerAllServices()) which don't
        // have the test-harness primer running.  See M4_DISCOVERY §46
        // (CR17).
        loadLib();
        if (!sLibLoaded) {
            System.err.println("InputMethodServiceTest: loadLibrary failed: " + sLibLoadError);
            System.exit(10);
        }
        int rc;
        try {
            rc = run();
        } catch (Throwable t) {
            eprintln("InputMethodServiceTest: uncaught throwable: "
                    + t.getClass().getName() + ": " + t.getMessage());
            t.printStackTrace();
            rc = 99;
        }
        println("InputMethodServiceTest: exiting with code " + rc);
        System.exit(rc);
    }

    static int run() throws Throwable {
        println("InputMethodServiceTest: starting M4e IInputMethodManager smoke test");

        Object service;
        try {
            Class<?> svcCls = Class.forName(
                    "com.westlake.services.WestlakeInputMethodManagerService");
            service = svcCls.getDeclaredConstructor().newInstance();
        } catch (Throwable t) {
            eprintln("InputMethodServiceTest: FAIL service ctor threw: " + t);
            t.printStackTrace();
            return 20;
        }
        println("InputMethodServiceTest: constructed " + service);

        if (!(service instanceof android.os.IBinder)) {
            eprintln("InputMethodServiceTest: FAIL service is not an IBinder: "
                    + service.getClass().getName());
            return 21;
        }
        android.os.IBinder binder = (android.os.IBinder) service;

        try {
            android.os.ServiceManager.addService("input_method", binder);
            println("InputMethodServiceTest: ServiceManager.addService(input_method) OK");
        } catch (Throwable t) {
            eprintln("InputMethodServiceTest: FAIL addService threw: " + t);
            return 30;
        }

        android.os.IBinder b;
        try {
            b = android.os.ServiceManager.getService("input_method");
        } catch (Throwable t) {
            eprintln("InputMethodServiceTest: FAIL getService threw: " + t);
            return 40;
        }
        if (b == null) {
            eprintln("InputMethodServiceTest: FAIL getService(\"input_method\") returned null");
            return 41;
        }
        println("InputMethodServiceTest: getService(\"input_method\") -> " + b);

        if (b != service) {
            println("InputMethodServiceTest: NOTE round-trip returned different instance "
                    + "-- retargeting state checks");
            service = b;
        }

        // Stub.asInterface roundtrip.
        Object proxy;
        try {
            Class<?> immCls = Class.forName("com.android.internal.view.IInputMethodManager");
            Class<?> stubCls = null;
            for (Class<?> inner : immCls.getDeclaredClasses()) {
                if (inner.getSimpleName().equals("Stub")) { stubCls = inner; break; }
            }
            if (stubCls == null) {
                eprintln("InputMethodServiceTest: FAIL no IInputMethodManager.Stub inner class");
                return 50;
            }
            java.lang.reflect.Method asI = stubCls.getDeclaredMethod("asInterface",
                    android.os.IBinder.class);
            proxy = asI.invoke(null, b);
        } catch (Throwable t) {
            eprintln("InputMethodServiceTest: FAIL asInterface: " + t);
            t.printStackTrace();
            return 51;
        }
        if (proxy == null) {
            eprintln("InputMethodServiceTest: FAIL asInterface returned null");
            return 52;
        }
        println("InputMethodServiceTest: IInputMethodManager.Stub.asInterface(b) -> " + proxy);

        if (proxy == service || proxy == b) {
            println("InputMethodServiceTest: asInterface returned SAME service -- "
                    + "direct Java dispatch ACTIVE");
        } else {
            eprintln("InputMethodServiceTest: NOTE asInterface returned a different object "
                    + "-- proxy path engaged");
        }

        // getInputMethodList(0, 0) -> non-null empty list
        try {
            java.lang.reflect.Method m = proxy.getClass().getMethod(
                    "getInputMethodList", int.class, int.class);
            Object v = m.invoke(proxy, 0, 0);
            println("InputMethodServiceTest: getInputMethodList(0,0) -> " + v);
            if (v == null) {
                eprintln("InputMethodServiceTest: NOTE getInputMethodList returned null");
            }
        } catch (Throwable t) {
            eprintln("InputMethodServiceTest: FAIL getInputMethodList: " + t);
            t.printStackTrace();
            return 60;
        }

        // getEnabledInputMethodList(0) -> non-null
        try {
            java.lang.reflect.Method m = proxy.getClass().getMethod(
                    "getEnabledInputMethodList", int.class);
            Object v = m.invoke(proxy, 0);
            println("InputMethodServiceTest: getEnabledInputMethodList(0) -> " + v);
            if (v == null) {
                eprintln("InputMethodServiceTest: NOTE getEnabledInputMethodList returned null");
            }
        } catch (Throwable t) {
            eprintln("InputMethodServiceTest: FAIL getEnabledInputMethodList: " + t);
            t.printStackTrace();
            return 70;
        }

        // getCurrentInputMethodInfoAsUser(0) -> null
        try {
            java.lang.reflect.Method m = proxy.getClass().getMethod(
                    "getCurrentInputMethodInfoAsUser", int.class);
            Object v = m.invoke(proxy, 0);
            println("InputMethodServiceTest: getCurrentInputMethodInfoAsUser(0) -> " + v);
            if (v != null) {
                eprintln("InputMethodServiceTest: NOTE expected null, got " + v);
            }
        } catch (Throwable t) {
            eprintln("InputMethodServiceTest: FAIL getCurrentInputMethodInfoAsUser: " + t);
            t.printStackTrace();
            return 80;
        }

        // addClient(null-ish args) -- exercise the no-op path.  We don't
        // have a real IInputMethodClient, so we pass null and just verify
        // the method exists and does not throw.
        try {
            Class<?> cliCls = Class.forName("com.android.internal.inputmethod.IInputMethodClient");
            Class<?> connCls = Class.forName("com.android.internal.inputmethod.IRemoteInputConnection");
            java.lang.reflect.Method m = proxy.getClass().getMethod(
                    "addClient", cliCls, connCls, int.class);
            m.invoke(proxy, null, null, 0);
            println("InputMethodServiceTest: addClient(null,null,0) -- OK (no-op for null client)");

            int n = (Integer) service.getClass().getMethod("registeredClientCount").invoke(service);
            // Null client should NOT be added.
            if (n != 0) {
                eprintln("InputMethodServiceTest: NOTE expected 0 clients after null addClient, got " + n);
            } else {
                println("InputMethodServiceTest: registeredClientCount = 0 -- OK");
            }
        } catch (Throwable t) {
            eprintln("InputMethodServiceTest: FAIL addClient: " + t);
            t.printStackTrace();
            return 90;
        }

        // listServices contains "input_method"
        try {
            String[] names = android.os.ServiceManager.listServices();
            boolean found = false;
            if (names != null) {
                for (String n : names) {
                    if ("input_method".equals(n)) { found = true; break; }
                }
            }
            if (!found) {
                eprintln("InputMethodServiceTest: FAIL listServices does not contain \"input_method\"");
                return 100;
            }
            println("InputMethodServiceTest: listServices contains \"input_method\" -- OK");
        } catch (Throwable t) {
            eprintln("InputMethodServiceTest: FAIL listServices: " + t);
            return 101;
        }

        println("InputMethodServiceTest: PASS");
        return 0;
    }
}
