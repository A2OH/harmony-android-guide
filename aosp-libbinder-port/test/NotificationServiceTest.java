// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4e -- NotificationServiceTest
//
// Synthetic smoke test for WestlakeNotificationManagerService.  Mirrors
// PowerServiceTest's M3++-style structure: load binder JNI, construct
// the service, register it under "notification", look it up, verify
// Stub.asInterface returns the SAME local object (same-process elision),
// then exercise the Tier-1 methods (areNotificationsEnabled, getZenMode,
// getEffectsSuppressor, getNotificationChannels, getNotificationChannel).

public class NotificationServiceTest {

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
        // WestlakeNotificationManagerService now uses the
        // Stub(PermissionEnforcer) bypass with a NoopPermissionEnforcer
        // (same pattern as M4a/M4b/M4c/M4-power), so the service
        // constructs without touching ActivityThread.getSystemContext().
        // This makes M4e (notification) self-sufficient for production
        // callers (ServiceRegistrar.registerAllServices()) which don't
        // have the test-harness primer running.  See M4_DISCOVERY §46
        // (CR17).
        loadLib();
        if (!sLibLoaded) {
            System.err.println("NotificationServiceTest: loadLibrary failed: " + sLibLoadError);
            System.exit(10);
        }
        int rc;
        try {
            rc = run();
        } catch (Throwable t) {
            eprintln("NotificationServiceTest: uncaught throwable: "
                    + t.getClass().getName() + ": " + t.getMessage());
            t.printStackTrace();
            rc = 99;
        }
        println("NotificationServiceTest: exiting with code " + rc);
        System.exit(rc);
    }

    static int run() throws Throwable {
        println("NotificationServiceTest: starting M4e INotificationManager smoke test");

        Object service;
        try {
            Class<?> svcCls = Class.forName(
                    "com.westlake.services.WestlakeNotificationManagerService");
            service = svcCls.getDeclaredConstructor().newInstance();
        } catch (Throwable t) {
            eprintln("NotificationServiceTest: FAIL service ctor threw: " + t);
            t.printStackTrace();
            return 20;
        }
        println("NotificationServiceTest: constructed " + service);

        if (!(service instanceof android.os.IBinder)) {
            eprintln("NotificationServiceTest: FAIL service is not an IBinder: "
                    + service.getClass().getName());
            return 21;
        }
        android.os.IBinder binder = (android.os.IBinder) service;

        try {
            android.os.ServiceManager.addService("notification", binder);
            println("NotificationServiceTest: ServiceManager.addService(notification) OK");
        } catch (Throwable t) {
            eprintln("NotificationServiceTest: FAIL addService threw: " + t);
            return 30;
        }

        android.os.IBinder b;
        try {
            b = android.os.ServiceManager.getService("notification");
        } catch (Throwable t) {
            eprintln("NotificationServiceTest: FAIL getService threw: " + t);
            return 40;
        }
        if (b == null) {
            eprintln("NotificationServiceTest: FAIL getService(\"notification\") returned null");
            return 41;
        }
        println("NotificationServiceTest: getService(\"notification\") -> " + b);

        if (b != service) {
            println("NotificationServiceTest: NOTE round-trip returned different instance "
                    + "-- retargeting state checks");
            service = b;
        }

        // Stub.asInterface roundtrip.
        Object proxy;
        try {
            Class<?> inmCls = Class.forName("android.app.INotificationManager");
            Class<?> stubCls = null;
            for (Class<?> inner : inmCls.getDeclaredClasses()) {
                if (inner.getSimpleName().equals("Stub")) { stubCls = inner; break; }
            }
            if (stubCls == null) {
                eprintln("NotificationServiceTest: FAIL no INotificationManager.Stub inner class");
                return 50;
            }
            java.lang.reflect.Method asI = stubCls.getDeclaredMethod("asInterface",
                    android.os.IBinder.class);
            proxy = asI.invoke(null, b);
        } catch (Throwable t) {
            eprintln("NotificationServiceTest: FAIL asInterface: " + t);
            t.printStackTrace();
            return 51;
        }
        if (proxy == null) {
            eprintln("NotificationServiceTest: FAIL asInterface returned null");
            return 52;
        }
        println("NotificationServiceTest: INotificationManager.Stub.asInterface(b) -> " + proxy);

        if (proxy == service || proxy == b) {
            println("NotificationServiceTest: asInterface returned SAME service -- "
                    + "direct Java dispatch ACTIVE");
        } else {
            eprintln("NotificationServiceTest: NOTE asInterface returned a different object "
                    + "-- proxy path engaged");
        }

        // areNotificationsEnabled("any.pkg") -> true
        try {
            java.lang.reflect.Method m = proxy.getClass().getMethod(
                    "areNotificationsEnabled", String.class);
            boolean v = (Boolean) m.invoke(proxy, "com.westlake.test");
            println("NotificationServiceTest: areNotificationsEnabled(\"com.westlake.test\") -> " + v);
            if (!v) {
                eprintln("NotificationServiceTest: FAIL expected true");
                return 61;
            }
        } catch (Throwable t) {
            eprintln("NotificationServiceTest: FAIL areNotificationsEnabled: " + t);
            t.printStackTrace();
            return 60;
        }

        // getZenMode() -> 0 (ZEN_MODE_OFF)
        try {
            java.lang.reflect.Method m = proxy.getClass().getMethod("getZenMode");
            int v = (Integer) m.invoke(proxy);
            println("NotificationServiceTest: getZenMode() -> " + v);
            if (v != 0) {
                eprintln("NotificationServiceTest: FAIL expected ZEN_MODE_OFF=0, got " + v);
                return 71;
            }
        } catch (Throwable t) {
            eprintln("NotificationServiceTest: FAIL getZenMode: " + t);
            t.printStackTrace();
            return 70;
        }

        // getEffectsSuppressor() -> null
        try {
            java.lang.reflect.Method m = proxy.getClass().getMethod("getEffectsSuppressor");
            Object v = m.invoke(proxy);
            println("NotificationServiceTest: getEffectsSuppressor() -> " + v);
            if (v != null) {
                eprintln("NotificationServiceTest: NOTE expected null, got " + v);
            }
        } catch (Throwable t) {
            eprintln("NotificationServiceTest: FAIL getEffectsSuppressor: " + t);
            t.printStackTrace();
            return 80;
        }

        // getNotificationChannels(...) -> non-null (empty list)
        try {
            java.lang.reflect.Method m = proxy.getClass().getMethod(
                    "getNotificationChannels", String.class, String.class, int.class);
            Object v = m.invoke(proxy, "com.westlake.test", "com.westlake.test", 0);
            println("NotificationServiceTest: getNotificationChannels(...) -> " + v);
            if (v == null) {
                eprintln("NotificationServiceTest: NOTE getNotificationChannels returned null "
                        + "(reflective empty ParceledListSlice fallback path was taken)");
            }
        } catch (Throwable t) {
            eprintln("NotificationServiceTest: FAIL getNotificationChannels: " + t);
            t.printStackTrace();
            return 90;
        }

        // getNotificationChannel(...) -> null
        try {
            java.lang.reflect.Method m = proxy.getClass().getMethod(
                    "getNotificationChannel", String.class, int.class, String.class, String.class);
            Object v = m.invoke(proxy, "com.westlake.test", 0, "com.westlake.test", "channel-id");
            println("NotificationServiceTest: getNotificationChannel(...) -> " + v);
            if (v != null) {
                eprintln("NotificationServiceTest: NOTE expected null, got " + v);
            }
        } catch (Throwable t) {
            eprintln("NotificationServiceTest: FAIL getNotificationChannel: " + t);
            t.printStackTrace();
            return 100;
        }

        // listServices contains "notification"
        try {
            String[] names = android.os.ServiceManager.listServices();
            boolean found = false;
            if (names != null) {
                for (String n : names) {
                    if ("notification".equals(n)) { found = true; break; }
                }
            }
            if (!found) {
                eprintln("NotificationServiceTest: FAIL listServices does not contain \"notification\"");
                return 110;
            }
            println("NotificationServiceTest: listServices contains \"notification\" -- OK");
        } catch (Throwable t) {
            eprintln("NotificationServiceTest: FAIL listServices: " + t);
            return 111;
        }

        println("NotificationServiceTest: PASS");
        return 0;
    }
}
