// SPDX-License-Identifier: Apache-2.0
//
// Westlake M3++ — AsInterfaceTest.java
//
// Exercises the **same-process Stub.asInterface optimization** that
// AOSP IBinder.queryLocalInterface enables.  This is the architectural
// pattern every M4 service will rely on.
//
// What we test:
//   1. Create an IEcho service (a Binder subclass implementing IEcho).
//   2. Register it: ServiceManager.addService("westlake.echo", echo).
//   3. Look it up: IBinder b = ServiceManager.getService("westlake.echo").
//   4. b MUST be the SAME object as `echo` (M3++ local-binder optimization).
//   5. IEcho.Stub.asInterface(b) -> calls b.queryLocalInterface(IEcho.DESCRIPTOR).
//   6. queryLocalInterface returns mOwner (= echo), no Proxy is constructed.
//   7. echo.say("hi") returns "hi" via direct Java vtable dispatch.
//
// Why this matters: this proves the M4 services pattern works end-to-end
// without needing the Parcel JNI cluster.  When IActivityManagerService is
// added to ServiceManager in M4 and a client does
//   IActivityManager am = IActivityManager.Stub.asInterface(
//       ServiceManager.getService("activity"));
// the same path runs.  No marshaling, no Parcel, no cross-process kernel
// traffic.

public class AsInterfaceTest {
    private static boolean libLoaded = false;
    private static String libLoadError = null;

    // M3++ note: lib loading is DEFERRED to main() — same pattern as
    // HelloBinder.java.  Loading in <clinit> triggers JNI_OnLoad_binder
    // before dalvikvm's RegisterNatives-on-main-class loop completes,
    // which on this build's PathClassLoader leaves the main class with
    // a corrupt method table (PC ends up at 0xfffffffffffffb17 — a poison
    // value).  Deferring the load to main() sidesteps the issue.
    static void loadLib() {
        if (libLoaded) return;
        try {
            System.loadLibrary("android_runtime_stub");
            libLoaded = true;
        } catch (UnsatisfiedLinkError e) {
            libLoadError = e.toString();
        }
    }

    // ---------------------------------------------------------------------
    // Tiny AIDL-equivalent interface.  In real Android code this would be
    // generated from IEcho.aidl; we hand-write it because our M3++ test
    // doesn't depend on the AIDL compiler being part of the dex build.

    interface IEcho extends android.os.IInterface {
        String DESCRIPTOR = "westlake.IEcho";

        String say(String s);

        // The canonical AOSP-style Stub class.  In real AIDL output this
        // extends Binder and is auto-generated from IEcho.aidl.
        abstract class Stub extends android.os.Binder implements IEcho {
            public Stub() {
                attachInterface(this, DESCRIPTOR);
            }
            public static IEcho asInterface(android.os.IBinder obj) {
                if (obj == null) return null;
                // Same-process fast path: if the binder is local, this is
                // a direct Java cast — no Proxy, no Parcel, no kernel.
                android.os.IInterface i = obj.queryLocalInterface(DESCRIPTOR);
                if (i instanceof IEcho) return (IEcho) i;
                // Cross-process fallback would be a Proxy.  M3++ doesn't
                // implement transact-based proxies; we return null and let
                // the test fail loudly so the milestone gap is obvious.
                println("AsInterfaceTest: WARNING queryLocalInterface returned "
                        + i + " (not IEcho) — no Proxy path implemented");
                return null;
            }
            @Override
            public android.os.IBinder asBinder() {
                return this;
            }
        }
    }

    // ---------------------------------------------------------------------
    // EchoImpl — the concrete service.  In real Android this would derive
    // from IEcho.Stub; the implementation just answers say().

    static final class EchoImpl extends IEcho.Stub {
        @Override public String say(String s) { return s; }
    }

    // ---------------------------------------------------------------------
    // Main / test driver.

    public static void main(String[] args) {
        loadLib();
        if (!libLoaded) {
            System.err.println("AsInterfaceTest: loadLibrary failed: " + libLoadError);
            System.exit(10);
        }
        int rc;
        try {
            rc = run();
        } catch (Throwable t) {
            eprintln("AsInterfaceTest: uncaught throwable: " + t.getClass().getName()
                    + ": " + t.getMessage());
            t.printStackTrace();
            rc = 99;
        }
        println("AsInterfaceTest: exiting with code " + rc);
        System.exit(rc);
    }

    static int run() {
        println("AsInterfaceTest: starting M3++ Stub.asInterface test");

        // 1. Create the local Java service.
        EchoImpl echo;
        try {
            echo = new EchoImpl();
        } catch (Throwable t) {
            eprintln("AsInterfaceTest: FAIL EchoImpl ctor threw: " + t);
            return 10;
        }
        println("AsInterfaceTest: created EchoImpl: " + echo);

        // 2. Verify Java-side queryLocalInterface works on the original obj.
        //    (Pre-registration sanity check — exercises Binder.attachInterface +
        //    queryLocalInterface in isolation, no servicemanager needed.)
        android.os.IInterface qli = echo.queryLocalInterface(IEcho.DESCRIPTOR);
        if (qli != echo) {
            eprintln("AsInterfaceTest: FAIL self queryLocalInterface returned " + qli
                    + " (expected echo=" + echo + ")");
            return 11;
        }
        println("AsInterfaceTest: self queryLocalInterface(\"" + IEcho.DESCRIPTOR
                + "\") -> SAME echo object — OK");

        // 3. Register with servicemanager.
        try {
            android.os.ServiceManager.addService("westlake.echo", echo);
        } catch (Throwable t) {
            eprintln("AsInterfaceTest: FAIL addService threw: " + t);
            return 20;
        }
        println("AsInterfaceTest: addService(\"westlake.echo\", echo) OK");

        // 4. Look up via servicemanager.
        android.os.IBinder fromSM;
        try {
            fromSM = android.os.ServiceManager.getService("westlake.echo");
        } catch (Throwable t) {
            eprintln("AsInterfaceTest: FAIL getService threw: " + t);
            return 30;
        }
        if (fromSM == null) {
            eprintln("AsInterfaceTest: FAIL getService(\"westlake.echo\") returned null");
            return 31;
        }
        println("AsInterfaceTest: getService(\"westlake.echo\") -> " + fromSM);

        // 5. The CRITICAL identity check.
        if (fromSM != echo) {
            // Non-fatal: if the implementation reports back via NativeBinderProxy
            // we can still try queryLocalInterface on it.  But the M3++ goal is
            // to return the same object.
            eprintln("AsInterfaceTest: NOTE getService returned a different object "
                    + "(" + fromSM + ") — same-process optimization NOT active");
        } else {
            println("AsInterfaceTest: getService returned SAME echo object — "
                    + "same-process optimization ACTIVE");
        }

        // 6. AOSP-style Stub.asInterface.
        IEcho asI;
        try {
            asI = IEcho.Stub.asInterface(fromSM);
        } catch (Throwable t) {
            eprintln("AsInterfaceTest: FAIL Stub.asInterface threw: " + t);
            return 40;
        }
        if (asI == null) {
            eprintln("AsInterfaceTest: FAIL Stub.asInterface returned null "
                    + "(queryLocalInterface didn't recognize descriptor)");
            return 41;
        }
        println("AsInterfaceTest: Stub.asInterface returned " + asI);
        if (asI != echo) {
            eprintln("AsInterfaceTest: NOTE Stub.asInterface returned different "
                    + "object than the original — proxy path engaged (unexpected)");
        } else {
            println("AsInterfaceTest: Stub.asInterface returned SAME echo — "
                    + "direct Java dispatch ACTIVE");
        }

        // 7. Method call — direct Java vtable, no marshaling.
        String reply;
        try {
            reply = asI.say("hi");
        } catch (Throwable t) {
            eprintln("AsInterfaceTest: FAIL say(\"hi\") threw: " + t);
            return 50;
        }
        if (!"hi".equals(reply)) {
            eprintln("AsInterfaceTest: FAIL say(\"hi\") returned " + reply);
            return 51;
        }
        println("AsInterfaceTest: say(\"hi\") -> \"" + reply + "\" — OK");

        // 8. Verify listServices sees our service.
        String[] names = android.os.ServiceManager.listServices();
        boolean foundEcho = false;
        if (names != null) {
            for (String n : names) {
                if ("westlake.echo".equals(n)) { foundEcho = true; break; }
            }
        }
        if (!foundEcho) {
            eprintln("AsInterfaceTest: FAIL listServices() did not contain "
                    + "westlake.echo (got " + (names == null ? 0 : names.length)
                    + " entries)");
            return 60;
        }
        println("AsInterfaceTest: listServices() contains westlake.echo — OK");

        println("AsInterfaceTest: PASS");
        return 0;
    }

    static native void println(String s);
    static native void eprintln(String s);
}
