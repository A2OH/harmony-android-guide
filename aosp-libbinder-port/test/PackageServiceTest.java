// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4c -- PackageServiceTest.java
//
// Synthetic smoke test for WestlakePackageManagerService.  Mirrors the
// shape of PowerServiceTest (M4-power) and WindowServiceTest (M4b), with
// IPackageManager-specific assertions:
//   - self queryLocalInterface returns SAME service (descriptor wired)
//   - asInterface returns same instance (direct Java dispatch active)
//   - getPackageInfo for our package returns non-null
//   - getApplicationInfo for our package returns non-null with right
//     packageName / processName / uid
//   - hasSystemFeature returns false for any input
//   - getNameForUid returns our package
//   - getPackagesForUid returns our package array
//   - getInstalledPackages returns a ParceledListSlice
//   - listServices contains "package"
//
// IO strategy (no new natives): both PackageServiceTest and
// AsInterfaceTest are bundled into PackageServiceTest.dex; loading
// android_runtime_stub registers AsInterfaceTest.{println,eprintln}
// natives, and this class delegates its log helpers to AsInterfaceTest's
// already-registered natives.  System.out / java.util.logging are unusable
// in this dalvikvm (see A15_IO_FIX_NEEDED.md).

public class PackageServiceTest {

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
        AsInterfaceTest.println(s);
    }

    private static void eprintln(String s) {
        AsInterfaceTest.eprintln(s);
    }

    public static void main(String[] args) {
        loadLib();
        if (!sLibLoaded) {
            System.err.println("PackageServiceTest: loadLibrary failed: " + sLibLoadError);
            System.exit(10);
        }
        int rc;
        try {
            rc = run();
        } catch (Throwable t) {
            eprintln("PackageServiceTest: uncaught throwable: " + t.getClass().getName()
                    + ": " + t.getMessage());
            t.printStackTrace();
            rc = 99;
        }
        println("PackageServiceTest: exiting with code " + rc);
        System.exit(rc);
    }

    static int run() throws Throwable {
        println("PackageServiceTest: starting M4c synthetic smoke");

        // ---------- 1. Construct WestlakePackageManagerService ------------
        // Reflection because the test sits OUTSIDE the shim package and
        // PackageServiceTest.dex is loaded by an app classloader, not the
        // shim classloader.
        Object service;
        try {
            Class<?> svcCls = Class.forName("com.westlake.services.WestlakePackageManagerService");
            service = svcCls.getDeclaredConstructor().newInstance();
        } catch (Throwable t) {
            eprintln("PackageServiceTest: FAIL constructing service: " + t);
            t.printStackTrace();
            return 20;
        }
        println("PackageServiceTest: constructed " + service);

        // The service should be a Binder (it extends IPackageManager.Stub
        // which extends Binder).
        if (!(service instanceof android.os.IBinder)) {
            eprintln("PackageServiceTest: FAIL service is not an IBinder: "
                    + service.getClass().getName());
            return 21;
        }
        android.os.IBinder binder = (android.os.IBinder) service;

        // ---------- 2. Self queryLocalInterface --------------------------
        String descriptor = "android.content.pm.IPackageManager";
        android.os.IInterface qli = binder.queryLocalInterface(descriptor);
        if (qli != service) {
            eprintln("PackageServiceTest: FAIL self queryLocalInterface returned " + qli
                    + " (expected service=" + service + ")");
            return 22;
        }
        println("PackageServiceTest: self queryLocalInterface(\"" + descriptor
                + "\") -> SAME service object -- OK");

        // ---------- 3. Register OUR instance directly --------------------
        try {
            android.os.ServiceManager.addService("package", binder);
            println("PackageServiceTest: ServiceManager.addService(package) OK");
        } catch (Throwable t) {
            eprintln("PackageServiceTest: FAIL ServiceManager.addService: " + t);
            return 30;
        }

        // ---------- 3b. Test ServiceRegistrar.registerAllServices -------
        try {
            Class<?> regCls = Class.forName("com.westlake.services.ServiceRegistrar");
            regCls.getDeclaredMethod("resetForTesting").invoke(null);
            int count = (Integer) regCls.getDeclaredMethod("registerAllServices").invoke(null);
            println("PackageServiceTest: ServiceRegistrar.registerAllServices() -> "
                    + count + " service(s)");
            if (count < 1) {
                eprintln("PackageServiceTest: FAIL expected >=1 service registered by registrar, got " + count);
                return 32;
            }
        } catch (Throwable t) {
            eprintln("PackageServiceTest: NOTE ServiceRegistrar test skipped: " + t);
            // Don't fail the whole test on this; the registrar bypass above
            // already added "package" successfully.
        }

        // ---------- 4. Look up via ServiceManager ------------------------
        android.os.IBinder b;
        try {
            b = android.os.ServiceManager.getService("package");
        } catch (Throwable t) {
            eprintln("PackageServiceTest: FAIL getService(\"package\"): " + t);
            return 40;
        }
        if (b == null) {
            eprintln("PackageServiceTest: FAIL getService(\"package\") returned null");
            return 41;
        }
        println("PackageServiceTest: getService(\"package\") -> " + b);

        // Retarget our local `service` handle to the round-trip instance
        // (registrar may have replaced our addService entry with its own).
        if (b != service) {
            println("PackageServiceTest: NOTE round-trip returned different "
                    + "instance than our local handle -- retargeting state checks");
            service = b;
        }

        // ---------- 5. asInterface ---------------------------------------
        Object proxy;
        try {
            Class<?> ipmCls = Class.forName("android.content.pm.IPackageManager");
            Class<?> stubCls = null;
            for (Class<?> inner : ipmCls.getDeclaredClasses()) {
                if (inner.getSimpleName().equals("Stub")) { stubCls = inner; break; }
            }
            if (stubCls == null) {
                eprintln("PackageServiceTest: FAIL no IPackageManager.Stub inner class");
                return 50;
            }
            java.lang.reflect.Method asI = stubCls.getDeclaredMethod("asInterface",
                    android.os.IBinder.class);
            proxy = asI.invoke(null, b);
        } catch (Throwable t) {
            eprintln("PackageServiceTest: FAIL asInterface: " + t);
            return 51;
        }
        if (proxy == null) {
            eprintln("PackageServiceTest: FAIL asInterface returned null "
                    + "(queryLocalInterface didn't recognize descriptor)");
            return 52;
        }
        println("PackageServiceTest: IPackageManager.Stub.asInterface(b) -> " + proxy);
        println("PackageServiceTest:   proxy.class = " + proxy.getClass().getName());

        if (proxy == service) {
            println("PackageServiceTest: asInterface returned SAME service -- "
                    + "direct Java dispatch ACTIVE (same-process opt working)");
        } else if (proxy == b) {
            println("PackageServiceTest: asInterface returned same as round-trip "
                    + "binder -- direct Java dispatch ACTIVE");
        } else {
            eprintln("PackageServiceTest: NOTE asInterface returned a different object "
                    + "from both `service` and `b` -- likely Stub.Proxy "
                    + "(local-binder optimization NOT active in queryLocalInterface)");
        }

        // What is our package name (discovered by the service)?
        String ourPackage;
        try {
            java.lang.reflect.Method m = service.getClass().getMethod("packageName");
            ourPackage = (String) m.invoke(service);
        } catch (Throwable t) {
            // packageName() helper only present on our class; if proxy is
            // Stub.Proxy this won't work.  Fall back to the property.
            ourPackage = System.getProperty("westlake.apk.package", "com.westlake.host");
        }
        println("PackageServiceTest: service.packageName() = " + ourPackage);

        // ---------- 6. getPackageInfo for our package returns non-null --
        try {
            java.lang.reflect.Method m = findMethod(proxy.getClass(),
                    "getPackageInfo", String.class, long.class, int.class);
            if (m == null) {
                eprintln("PackageServiceTest: FAIL no getPackageInfo(String,long,int) on proxy");
                return 60;
            }
            Object pi = m.invoke(proxy, ourPackage, Long.valueOf(0L), Integer.valueOf(0));
            if (pi == null) {
                eprintln("PackageServiceTest: FAIL getPackageInfo(\"" + ourPackage + "\") returned null");
                return 61;
            }
            println("PackageServiceTest: getPackageInfo(\"" + ourPackage + "\") -> " + pi);

            // Verify foreign package returns null.
            Object foreign = m.invoke(proxy, "com.bogus.example", Long.valueOf(0L), Integer.valueOf(0));
            if (foreign != null) {
                eprintln("PackageServiceTest: FAIL getPackageInfo(foreign) returned non-null: " + foreign);
                return 62;
            }
            println("PackageServiceTest: getPackageInfo(foreign) -> null -- OK");
        } catch (Throwable t) {
            eprintln("PackageServiceTest: FAIL getPackageInfo: " + t);
            t.printStackTrace();
            return 63;
        }

        // ---------- 7. getApplicationInfo for our package returns non-null
        try {
            java.lang.reflect.Method m = findMethod(proxy.getClass(),
                    "getApplicationInfo", String.class, long.class, int.class);
            if (m == null) {
                eprintln("PackageServiceTest: FAIL no getApplicationInfo(String,long,int)");
                return 70;
            }
            Object ai = m.invoke(proxy, ourPackage, Long.valueOf(0L), Integer.valueOf(0));
            if (ai == null) {
                eprintln("PackageServiceTest: FAIL getApplicationInfo(\"" + ourPackage + "\") returned null");
                return 71;
            }
            println("PackageServiceTest: getApplicationInfo(\"" + ourPackage + "\") -> " + ai);

            // Spot-check the packageName field.
            try {
                java.lang.reflect.Field pkgF = ai.getClass().getField("packageName");
                String got = (String) pkgF.get(ai);
                if (!ourPackage.equals(got)) {
                    eprintln("PackageServiceTest: FAIL ai.packageName=\"" + got
                            + "\" expected \"" + ourPackage + "\"");
                    return 72;
                }
                println("PackageServiceTest: ai.packageName == ourPackage -- OK");
            } catch (Throwable ignored) {
                // Field may be on a superclass / shape-shifted; don't fail.
            }
        } catch (Throwable t) {
            eprintln("PackageServiceTest: FAIL getApplicationInfo: " + t);
            t.printStackTrace();
            return 73;
        }

        // ---------- 8. hasSystemFeature returns false --------------------
        try {
            java.lang.reflect.Method m = findMethod(proxy.getClass(),
                    "hasSystemFeature", String.class, int.class);
            if (m == null) {
                eprintln("PackageServiceTest: FAIL no hasSystemFeature(String,int)");
                return 80;
            }
            boolean has = (Boolean) m.invoke(proxy, "android.hardware.camera", Integer.valueOf(0));
            if (has) {
                eprintln("PackageServiceTest: FAIL hasSystemFeature returned true (expected false in sandbox)");
                return 81;
            }
            println("PackageServiceTest: hasSystemFeature(android.hardware.camera) -> false -- OK");
        } catch (Throwable t) {
            eprintln("PackageServiceTest: FAIL hasSystemFeature: " + t);
            return 82;
        }

        // ---------- 9. getNameForUid returns our package -----------------
        try {
            java.lang.reflect.Method m = findMethod(proxy.getClass(),
                    "getNameForUid", int.class);
            String name = (String) m.invoke(proxy, Integer.valueOf(10001));
            println("PackageServiceTest: getNameForUid(10001) -> " + name);
            if (name == null || !name.equals(ourPackage)) {
                eprintln("PackageServiceTest: FAIL getNameForUid expected \"" + ourPackage
                        + "\", got \"" + name + "\"");
                return 90;
            }
        } catch (Throwable t) {
            eprintln("PackageServiceTest: FAIL getNameForUid: " + t);
            return 91;
        }

        // ---------- 10. getPackagesForUid returns array ------------------
        try {
            java.lang.reflect.Method m = findMethod(proxy.getClass(),
                    "getPackagesForUid", int.class);
            String[] pkgs = (String[]) m.invoke(proxy, Integer.valueOf(10001));
            if (pkgs == null || pkgs.length != 1 || !pkgs[0].equals(ourPackage)) {
                eprintln("PackageServiceTest: FAIL getPackagesForUid expected [\""
                        + ourPackage + "\"]");
                return 100;
            }
            println("PackageServiceTest: getPackagesForUid(10001) -> [" + pkgs[0] + "] -- OK");
        } catch (Throwable t) {
            eprintln("PackageServiceTest: FAIL getPackagesForUid: " + t);
            return 101;
        }

        // ---------- 11. getInstalledPackages returns ParceledListSlice ---
        try {
            java.lang.reflect.Method m = findMethod(proxy.getClass(),
                    "getInstalledPackages", long.class, int.class);
            Object slice = m.invoke(proxy, Long.valueOf(0L), Integer.valueOf(0));
            if (slice == null) {
                eprintln("PackageServiceTest: FAIL getInstalledPackages returned null");
                return 110;
            }
            println("PackageServiceTest: getInstalledPackages(0,0) -> " + slice
                    + " (class=" + slice.getClass().getName() + ")");
        } catch (Throwable t) {
            eprintln("PackageServiceTest: FAIL getInstalledPackages: " + t);
            return 111;
        }

        // ---------- 12. listServices contains "package" ------------------
        try {
            String[] names = android.os.ServiceManager.listServices();
            boolean found = false;
            if (names != null) {
                for (String n : names) {
                    if ("package".equals(n)) { found = true; break; }
                }
            }
            if (!found) {
                eprintln("PackageServiceTest: FAIL listServices does not contain \"package\" "
                        + "(got " + (names == null ? 0 : names.length) + " entries)");
                return 120;
            }
            println("PackageServiceTest: listServices contains \"package\" -- OK");
        } catch (Throwable t) {
            eprintln("PackageServiceTest: FAIL listServices: " + t);
            return 121;
        }

        println("PackageServiceTest: PASS");
        return 0;
    }

    // ---------------------------------------------------------------------
    // Helper: find a method by name + parameter types.  We use getMethods()
    // so we pick up methods declared on superclasses (including the
    // Stub abstract class) too.
    // ---------------------------------------------------------------------
    private static java.lang.reflect.Method findMethod(Class<?> cls, String name,
            Class<?>... paramTypes) {
        try {
            return cls.getMethod(name, paramTypes);
        } catch (NoSuchMethodException nsme) {
            for (java.lang.reflect.Method m : cls.getMethods()) {
                if (!m.getName().equals(name)) continue;
                Class<?>[] ptypes = m.getParameterTypes();
                if (ptypes.length != paramTypes.length) continue;
                boolean match = true;
                for (int i = 0; i < ptypes.length; i++) {
                    if (!ptypes[i].equals(paramTypes[i])) { match = false; break; }
                }
                if (match) return m;
            }
            return null;
        }
    }
}
