// SPDX-License-Identifier: Apache-2.0
//
// Westlake M3 — HelloBinder.java
//
// Exercises the Java -> JNI -> libbinder -> /dev/vndbinder -> servicemanager
// end-to-end path on the OnePlus 6 dalvikvm.
//
// What we test:
//   1. Static-load libandroid_runtime_stub.so (which itself dlopens libbinder.so).
//   2. Call android.os.ServiceManager.listServices() — verify our SM is reachable
//      and at least the "manager" entry comes back.
//   3. Call android.os.ServiceManager.getService("westlake.test.echo") —
//      verify the binder registered by sm_registrar (preloaded by the boot
//      script) is returned non-null.
//   4. Print first 5 service names.
//   5. Exit 0 on success, non-zero on any failure.
//
// Why native println?  System.out.println() in this standalone dalvikvm
// build raises NPE because Charset.newEncoder() returns null.  Our native
// lib provides println/eprintln helpers that bypass the Java I/O stack.
//
// Run with:
//   dalvikvm -Xbootclasspath:core-oj.jar:core-libart.jar:core-icu4j.jar:bouncycastle.jar \
//            -cp aosp-shim.dex:HelloBinder.dex HelloBinder

public class HelloBinder {
    // Loaded lazily — must happen before our first println() call.
    private static boolean libLoaded = false;
    private static String libLoadError = null;

    private static void loadLib() {
        if (libLoaded) return;
        try {
            System.loadLibrary("android_runtime_stub");
            libLoaded = true;
        } catch (UnsatisfiedLinkError e) {
            libLoadError = e.toString();
        }
    }

    public static void main(String[] args) {
        loadLib();
        if (!libLoaded) {
            // No way to log; just exit non-zero.
            System.exit(10);
        }
        int rc;
        try {
            rc = run();
        } catch (Throwable t) {
            eprintln("HelloBinder: uncaught throwable: " + t.getClass().getName()
                    + ": " + t.getMessage());
            t.printStackTrace();
            rc = 99;
        }
        println("HelloBinder: exiting with code " + rc);
        System.exit(rc);
    }

    static int run() {
        println("HelloBinder: starting M3 end-to-end test");

        // android.os.ServiceManager comes from aosp-shim.dex (our shim
        // ServiceManager.java, see M3_NOTES.md).  It internally dlopen()s
        // libbinder.so via libandroid_runtime_stub.so dependencies.
        String[] services;
        try {
            services = android.os.ServiceManager.listServices();
        } catch (Throwable t) {
            eprintln("HelloBinder: FAIL listServices threw: " + t);
            return 20;
        }
        if (services == null) {
            eprintln("HelloBinder: FAIL listServices() returned null");
            return 21;
        }
        println("HelloBinder: listServices() returned " + services.length + " entries:");
        int limit = Math.min(5, services.length);
        for (int i = 0; i < limit; ++i) {
            println("  [" + i + "] " + services[i]);
        }
        if (services.length == 0) {
            eprintln("HelloBinder: FAIL listServices() returned 0 entries (servicemanager not reachable?)");
            return 22;
        }
        boolean foundManager = false;
        boolean foundEcho = false;
        for (String s : services) {
            if ("manager".equals(s)) foundManager = true;
            if ("westlake.test.echo".equals(s)) foundEcho = true;
        }
        if (!foundManager) {
            eprintln("HelloBinder: FAIL 'manager' not in service list");
            return 23;
        }
        println("HelloBinder: listServices contains \"manager\" — SM reachable");

        // Look up the test service.
        android.os.IBinder echo = null;
        try {
            echo = android.os.ServiceManager.getService("westlake.test.echo");
        } catch (Throwable t) {
            eprintln("HelloBinder: FAIL getService(\"westlake.test.echo\") threw: " + t);
            return 30;
        }
        if (echo == null) {
            eprintln("HelloBinder: FAIL getService(\"westlake.test.echo\") returned null");
            if (foundEcho) {
                eprintln("  but listServices DID contain it — check getService JNI");
            } else {
                eprintln("  and listServices did NOT contain it — check sm_registrar preload");
            }
            return 31;
        }
        println("HelloBinder: getService(\"westlake.test.echo\") -> " + echo
                + " (non-null)");

        println("HelloBinder: PASS");
        return 0;
    }

    static native void println(String s);
    static native void eprintln(String s);
}
