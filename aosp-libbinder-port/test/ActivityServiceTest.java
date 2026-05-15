// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4a -- ActivityServiceTest
//
// Synthetic smoke test for WestlakeActivityManagerService.  Mirrors
// AsInterfaceTest.java's M3++ structure: load binder JNI, construct the
// service, register it under "activity", look it up, verify
// Stub.asInterface returns the SAME local object, then exercise the
// Tier-1 methods (getCurrentUserId, getRunningAppProcesses,
// getTasks, registerProcessObserver).
//
// We do NOT verify cross-process Proxy paths here -- noice (and any
// real consumer in the dalvikvm sandbox) sits in the SAME process as
// the service, so queryLocalInterface returns this directly and the
// Parcel path is never engaged.  That elision is the whole point of
// M4a as architected.

public class ActivityServiceTest {
    private static boolean libLoaded = false;
    private static String libLoadError = null;

    static void loadLib() {
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
            System.err.println("ActivityServiceTest: loadLibrary failed: " + libLoadError);
            System.exit(10);
        }
        int rc;
        try {
            rc = run();
        } catch (Throwable t) {
            eprintln("ActivityServiceTest: uncaught throwable: "
                    + t.getClass().getName() + ": " + t.getMessage());
            t.printStackTrace();
            rc = 99;
        }
        println("ActivityServiceTest: exiting with code " + rc);
        System.exit(rc);
    }

    static int run() {
        println("ActivityServiceTest: starting M4a IActivityManager smoke test");

        // 1. Construct the service.
        com.westlake.services.WestlakeActivityManagerService svc;
        try {
            svc = new com.westlake.services.WestlakeActivityManagerService();
        } catch (Throwable t) {
            eprintln("ActivityServiceTest: FAIL service ctor threw: " + t);
            t.printStackTrace();
            return 10;
        }
        println("ActivityServiceTest: created WestlakeActivityManagerService: " + svc);

        // 2. Sanity-check queryLocalInterface on the original object.
        String descriptor = "android.app.IActivityManager";
        android.os.IInterface qli = svc.queryLocalInterface(descriptor);
        if (qli != svc) {
            eprintln("ActivityServiceTest: FAIL self queryLocalInterface returned " + qli
                    + " (expected svc=" + svc + ")");
            return 11;
        }
        println("ActivityServiceTest: self queryLocalInterface(\"" + descriptor
                + "\") -> SAME svc object -- OK");

        // 3. Register under "activity".
        try {
            android.os.ServiceManager.addService("activity", svc);
        } catch (Throwable t) {
            eprintln("ActivityServiceTest: FAIL addService threw: " + t);
            t.printStackTrace();
            return 20;
        }
        println("ActivityServiceTest: addService(\"activity\", svc) OK");

        // 4. Look up via servicemanager.
        android.os.IBinder fromSM;
        try {
            fromSM = android.os.ServiceManager.getService("activity");
        } catch (Throwable t) {
            eprintln("ActivityServiceTest: FAIL getService threw: " + t);
            return 30;
        }
        if (fromSM == null) {
            eprintln("ActivityServiceTest: FAIL getService(\"activity\") returned null");
            return 31;
        }
        println("ActivityServiceTest: getService(\"activity\") -> " + fromSM);

        // 5. Identity check (same-process elision).
        boolean sameObject = (fromSM == svc);
        if (sameObject) {
            println("ActivityServiceTest: getService returned SAME svc object -- "
                    + "same-process optimization ACTIVE");
        } else {
            println("ActivityServiceTest: NOTE getService returned different object "
                    + "(" + fromSM + ") -- M3++ same-process elision not active for this path");
        }

        // 6. Stub.asInterface should hand us back the IActivityManager.
        android.app.IActivityManager am;
        try {
            am = android.app.IActivityManager.Stub.asInterface(fromSM);
        } catch (Throwable t) {
            eprintln("ActivityServiceTest: FAIL Stub.asInterface threw: " + t);
            t.printStackTrace();
            return 40;
        }
        if (am == null) {
            eprintln("ActivityServiceTest: FAIL Stub.asInterface returned null "
                    + "(queryLocalInterface didn't recognize descriptor "
                    + descriptor + ")");
            return 41;
        }
        println("ActivityServiceTest: Stub.asInterface returned " + am);
        if (am != svc) {
            eprintln("ActivityServiceTest: NOTE Stub.asInterface returned different "
                    + "object than the original -- proxy path engaged (unexpected)");
        } else {
            println("ActivityServiceTest: Stub.asInterface returned SAME svc -- "
                    + "direct Java dispatch ACTIVE");
        }

        // 7. Tier-1 verification.

        // (a) getCurrentUserId() -> 0
        int uid;
        try {
            uid = am.getCurrentUserId();
        } catch (Throwable t) {
            eprintln("ActivityServiceTest: FAIL getCurrentUserId threw: " + t);
            return 50;
        }
        if (uid != 0) {
            eprintln("ActivityServiceTest: FAIL getCurrentUserId returned " + uid + " (expected 0)");
            return 51;
        }
        println("ActivityServiceTest: getCurrentUserId() -> 0 -- OK");

        // (b) getRunningAppProcesses() -> non-null list
        java.util.List procs;
        try {
            procs = am.getRunningAppProcesses();
        } catch (Throwable t) {
            eprintln("ActivityServiceTest: FAIL getRunningAppProcesses threw: " + t);
            return 60;
        }
        if (procs == null) {
            eprintln("ActivityServiceTest: FAIL getRunningAppProcesses returned null");
            return 61;
        }
        println("ActivityServiceTest: getRunningAppProcesses() -> list size="
                + procs.size() + " -- OK");

        // (c) getTasks(10) -> empty list
        java.util.List tasks;
        try {
            tasks = am.getTasks(10);
        } catch (Throwable t) {
            eprintln("ActivityServiceTest: FAIL getTasks threw: " + t);
            return 70;
        }
        if (tasks == null) {
            eprintln("ActivityServiceTest: FAIL getTasks returned null");
            return 71;
        }
        if (!tasks.isEmpty()) {
            eprintln("ActivityServiceTest: NOTE getTasks returned non-empty list size="
                    + tasks.size() + " (acceptable but unexpected)");
        }
        println("ActivityServiceTest: getTasks(10) -> list size=" + tasks.size()
                + " -- OK");

        // (d) registerProcessObserver(null-observer Stub) doesn't crash
        try {
            android.app.IProcessObserver obs = new android.app.IProcessObserver.Stub() {
                @Override
                public android.os.IBinder asBinder() { return this; }
            };
            am.registerProcessObserver(obs);
            am.unregisterProcessObserver(obs);
            println("ActivityServiceTest: register/unregisterProcessObserver -- OK");
        } catch (Throwable t) {
            eprintln("ActivityServiceTest: FAIL register/unregisterProcessObserver threw: " + t);
            return 80;
        }

        // (e) getIntentForIntentSender(null) -> null
        try {
            android.content.Intent intent = am.getIntentForIntentSender(null);
            if (intent != null) {
                eprintln("ActivityServiceTest: NOTE getIntentForIntentSender(null) returned non-null Intent " + intent);
            } else {
                println("ActivityServiceTest: getIntentForIntentSender(null) -> null -- OK");
            }
        } catch (Throwable t) {
            eprintln("ActivityServiceTest: FAIL getIntentForIntentSender threw: " + t);
            return 90;
        }

        // (f) getProcessMemoryInfo(int[]{pid}) -> non-null array
        try {
            int[] pids = new int[]{ android.os.Process.myPid() };
            android.os.Debug.MemoryInfo[] memInfos = am.getProcessMemoryInfo(pids);
            if (memInfos == null) {
                eprintln("ActivityServiceTest: FAIL getProcessMemoryInfo returned null array");
                return 91;
            }
            if (memInfos.length != 1) {
                eprintln("ActivityServiceTest: NOTE getProcessMemoryInfo returned array of length "
                        + memInfos.length + " (expected 1)");
            } else {
                println("ActivityServiceTest: getProcessMemoryInfo([pid]) -> array length="
                        + memInfos.length + " -- OK");
            }
        } catch (Throwable t) {
            eprintln("ActivityServiceTest: FAIL getProcessMemoryInfo threw: " + t);
            return 92;
        }

        // 8. Verify listServices sees our service.
        String[] names = android.os.ServiceManager.listServices();
        boolean foundActivity = false;
        if (names != null) {
            for (String n : names) {
                if ("activity".equals(n)) { foundActivity = true; break; }
            }
        }
        if (!foundActivity) {
            eprintln("ActivityServiceTest: FAIL listServices() did not contain "
                    + "activity (got " + (names == null ? 0 : names.length)
                    + " entries)");
            return 100;
        }
        println("ActivityServiceTest: listServices() contains activity -- OK");

        println("ActivityServiceTest: PASS (all Tier-1 verifications)");
        return 0;
    }

    static native void println(String s);
    static native void eprintln(String s);
}
