// SPDX-License-Identifier: Apache-2.0
//
// Westlake CR27 -- DiscoverWrapperBase (extracted from NoiceDiscoverWrapper, 2026-05-13)
//
// Codex review #2 Tier 3 finding (PHASE_1_STATUS.md):
//   "NoiceDiscoverWrapper.java is still sensible as a discovery harness, but
//    at 1174 LOC it is now carrying app config, boot sequencing, service
//    registration, synthetic activity/application info, and phase
//    orchestration.  It should become manifest-driven before it is reused
//    for a second app."
//
// CR27 splits the original NoiceDiscoverWrapper into:
//   * DiscoverWrapperBase (this file) -- generic phase orchestration that
//     reads a Properties manifest (Java Properties format) for all
//     per-app configuration.
//   * NoiceDiscoverWrapper.java -- a ~30 LOC thin shim that points the
//     base class at noice.discover.properties.
//   * McdDiscoverWrapper.java -- a ~30 LOC thin shim that points the
//     base class at mcd.discover.properties (the second consumer; this
//     is what the codex review asked for).
//
// Manifest schema (see noice.discover.properties / mcd.discover.properties):
//   app.packageName=...        -- AndroidManifest <manifest package=>.
//   app.apkPath=...            -- absolute path to the deployed .apk on phone.
//   app.applicationClass=...   -- AndroidManifest <application android:name=>.
//   app.mainActivityClass=...  -- main launcher activity class FQN.
//   app.targetSdkVersion=NN    -- compileSdk/targetSdk for the app (int).
//   app.dataDir=...            -- writable data dir (override; default is
//                                 /data/local/tmp/westlake/<packageName>).
//   phase.probeServices=a,b,c  -- comma-separated probe-service names.
//                                 Optional; defaults to the curated set.
//   phase.classloadCandidates=a,b,c  -- comma-separated FQN classes for
//                                 PHASE B classload probing.  Optional;
//                                 defaults to just app.applicationClass +
//                                 app.mainActivityClass.
//
// Discovery layers (verbatim from NoiceDiscoverWrapper):
//
//   PHASE A: PROBE -- call ServiceManager.getService(name) for the typical
//            ~70 system services every Android app touches via Context
//            .getSystemService.
//
//   PHASE B: CLASSLOAD -- load the app's classes.dex with PathClassLoader
//            and try to resolve key classes.
//
//   PHASE C: APPLICATION-CTOR -- reflectively call new <appClass>().
//
//   PHASE D: APPLICATION-ATTACH -- attachBaseContext(WestlakeContextImpl).
//
//   PHASE E: APPLICATION-ONCREATE -- invoke onCreate().
//
//   PHASE F: FRAMEWORK-SINGLETONS -- drive AOSP framework singletons.
//
//   PHASE G: MAINACTIVITY-LAUNCH -- Instrumentation.newActivity ->
//            Activity.attach -> Activity.onCreate.
//
// All output via printf-style helpers (System.out.println throws NPE
// in this dalvikvm).

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import dalvik.system.PathClassLoader;

public class DiscoverWrapperBase {
    // ------------ Native helpers (provided by the subclass) ------------
    //
    // CR27 (2026-05-13): the actual JNI-registered println/eprintln natives
    // live on the subclass (NoiceDiscoverWrapper.println, McdDiscoverWrapper
    // .println) because art-latest/stubs/binder_jni_stub.cc binds them by
    // class name -- and the brief explicitly forbids modifying art-latest.
    // The subclass passes its native print callbacks here via the
    // {@link Printer} interface before calling {@link #runFromManifest}.
    // The base class calls println/eprintln through that interface so all
    // phase orchestration stays in this file.
    public interface Printer {
        void println(String s);
        void eprintln(String s);
    }

    static Printer PRINTER;

    static void println(String s) {
        if (PRINTER != null) PRINTER.println(s);
        else System.out.println(s);
    }
    static void eprintln(String s) {
        if (PRINTER != null) PRINTER.eprintln(s);
        else System.err.println(s);
    }

    static boolean libLoaded = false;
    static String libLoadError = null;

    // ===== Manifest-driven per-app configuration (populated in runFromManifest) =====
    static String APP_PACKAGE_NAME;
    static String APP_APK_PATH;
    static String APP_CLASS;
    static String ACTIVITY_CLASS;
    static String APP_DATA_DIR;
    static int APP_TARGET_SDK = 33;
    static String[] PROBE_SERVICES;
    static String[] CLASSLOAD_CANDIDATES;

    // ===== Default probe-service list (matches the curated noice list) =====
    static final String[] DEFAULT_PROBE_SERVICES = {
        "activity",            // ActivityManager
        "package",             // PackageManager
        "window",              // WindowManager
        "display",             // DisplayManager
        "input_method",        // InputMethodManager
        "notification",        // NotificationManager
        "permission",          // PermissionManager
        "permissionmgr",       // (alternate)
        "audio",               // AudioManager / AudioFlinger
        "media.audio_flinger", // AudioFlinger (native)
        "media.audio_policy",  // AudioPolicy (native)
        "media_session",       // MediaSession
        "media",               // MediaPlayerService
        "media_router",
        "media_session_manager",
        "telephony",
        "telephony.registry",
        "phone",
        "wifi",
        "connectivity",
        "alarm",
        "vibrator_manager",
        "vibrator",
        "power",
        "battery",
        "battery_stats",
        "input",
        "sensor",
        "sensorservice",
        "user",
        "location",
        "search",
        "appops",
        "appwidget",
        "clipboard",
        "content_capture",
        "device_policy",
        "dropbox",
        "fingerprint",
        "biometric",
        "keystore",
        "uimode",
        "usagestats",
        "uri_grants",
        "device_identifiers",
        "settings",            // Settings provider
        "dialog",              // (UiAutomation-related)
        "graphicsstats",
        "gpu",
        "thermalservice",
        "shortcut",
        "jobscheduler",
        "trust",
        "voiceinteraction",
        "wallpaper",
        "webviewupdate",
        "tv_input",
        "textservices",
        "textclassification",
        "autofill",
        "credential",
        "role",
        "rolemanager",
        "companion_device",
        "stats",
        "statscompanion",
        "SurfaceFlinger",      // ISurfaceComposer (native)
        "GpuService",          // (native)
        "android.security.keystore",
        "deviceidle",
    };

    // ===== State accumulated through the run =====
    static final Map<String, String> probedResults = new LinkedHashMap<>();
    static final List<String> caughtFailures = new ArrayList<>();
    static final List<String> phaseResults = new ArrayList<>();

    static PathClassLoader appPCL;
    static Object appInstance;
    static Class<?> appCls;
    static Object mainActivityInstance;
    static Class<?> mainActivityCls;

    /**
     * Read the manifest file at {@code manifestPath}, populate per-app
     * configuration, and drive every PHASE in sequence.  Each phase is
     * wrapped in its own try/catch so a failure in one does not abort
     * the run -- printFinalReport() always executes.
     *
     * This is the single entry point that thin subclasses (e.g.
     * {@code NoiceDiscoverWrapper}, {@code McdDiscoverWrapper}) call from
     * their {@code main(String[])}.
     */
    public static void runFromManifest(String manifestPath, Printer printer) {
        PRINTER = printer;
        runFromManifest(manifestPath);
    }

    public static void runFromManifest(String manifestPath) {
        loadLib();
        if (!libLoaded) {
            System.err.println("DiscoverWrapperBase: failed to load native lib: "
                    + libLoadError);
            System.exit(10);
        }
        if (!loadManifest(manifestPath)) {
            System.err.println("DiscoverWrapperBase: failed to load manifest at "
                    + manifestPath);
            System.exit(11);
        }

        // M4-PRE10 (2026-05-12) / CR9 (2026-05-12): seed Charset static state
        // BEFORE anything else runs.  The discovery harness does not go
        // through WestlakeLauncher.installSafeStandardStreams (which already
        // invokes primeCharsetState in the normal app-boot path), so
        // Charset.UTF_8 / Charset.defaultCharset / Charset.cache2 can be
        // null when AOSP framework code first reaches them.  See
        // CharsetPrimer.java + M4_DISCOVERY.md §30.
        CharsetPrimer.primeCharsetState();

        // CR17 (2026-05-12): defense-in-depth -- M4 service ctors use the
        // Stub(PermissionEnforcer) bypass and no longer need
        // primeActivityThread(), but the discovery harness goes much further
        // into AOSP framework code paths (PathClassLoader ctor chain,
        // Activity.attach, etc.) which may transitively read ActivityThread
        // state.  Retain the primer call.  See M4_DISCOVERY §46/§48 for the
        // CR17/CR18 bisection.
        CharsetPrimer.primeActivityThread();

        // M4a / M4-power: register Westlake services (activity, power, ...)
        // BEFORE PHASE A probes them, so the probe reports them as "found"
        // and any Hilt/AndroidX framework code that asInterface()s them gets
        // a real Java service object back (same-process Stub.asInterface
        // elision -> direct Java dispatch, no Parcel hop).  Best-effort:
        // each service is independently try/catch'd inside ServiceRegistrar.
        try {
            int registered =
                com.westlake.services.ServiceRegistrar.registerAllServices();
            println("DiscoverWrapperBase: ServiceRegistrar registered "
                    + registered + " M4 services");
        } catch (Throwable t) {
            eprintln("DiscoverWrapperBase: ServiceRegistrar failed: " + t);
        }

        try { phaseA_probeServices(); }
        catch (Throwable t) { recordFailure("PHASE A -- service probing", t); }
        try { phaseB_classLoad(); }
        catch (Throwable t) { recordFailure("PHASE B -- classloading app", t); }
        try { phaseC_applicationCtor(); }
        catch (Throwable t) { recordFailure("PHASE C -- Application ctor", t); }
        try { phaseDE_attachAndOnCreate(); }
        catch (Throwable t) { recordFailure("PHASE D/E -- attach + onCreate", t); }
        try { phaseF_frameworkSingletons(); }
        catch (Throwable t) { recordFailure("PHASE F -- framework singletons", t); }
        try { phaseG_mainActivityLaunch(); }
        catch (Throwable t) { recordFailure("PHASE G -- MainActivity launch driver", t); }

        printFinalReport();
        println("DiscoverWrapperBase: DONE");
        System.exit(0);
    }

    static void loadLib() {
        try {
            System.loadLibrary("android_runtime_stub");
            libLoaded = true;
        } catch (UnsatisfiedLinkError e) {
            libLoadError = e.toString();
        }
    }

    /**
     * Parse the Properties manifest file and populate the static APP_*
     * fields.  Returns false on any I/O or schema failure (caller exits
     * with code 11).
     */
    static boolean loadManifest(String manifestPath) {
        File mf = new File(manifestPath);
        if (!mf.exists()) {
            System.err.println("DiscoverWrapperBase: manifest missing: " + manifestPath);
            return false;
        }
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(mf)) {
            props.load(fis);
        } catch (IOException e) {
            System.err.println("DiscoverWrapperBase: manifest read failed: " + e);
            return false;
        }

        APP_PACKAGE_NAME = req(props, "app.packageName");
        APP_APK_PATH = req(props, "app.apkPath");
        APP_CLASS = req(props, "app.applicationClass");
        ACTIVITY_CLASS = req(props, "app.mainActivityClass");
        if (APP_PACKAGE_NAME == null || APP_APK_PATH == null
                || APP_CLASS == null || ACTIVITY_CLASS == null) {
            return false;
        }

        APP_DATA_DIR = props.getProperty(
                "app.dataDir", "/data/local/tmp/westlake/" + APP_PACKAGE_NAME);
        try {
            APP_TARGET_SDK = Integer.parseInt(
                    props.getProperty("app.targetSdkVersion", "33").trim());
        } catch (NumberFormatException nfe) {
            APP_TARGET_SDK = 33;
        }

        // PROBE_SERVICES: comma-separated names, or default curated list.
        String probeCsv = props.getProperty("phase.probeServices");
        if (probeCsv != null && !probeCsv.trim().isEmpty()) {
            PROBE_SERVICES = splitCsv(probeCsv);
        } else {
            PROBE_SERVICES = DEFAULT_PROBE_SERVICES;
        }

        // CLASSLOAD_CANDIDATES: comma-separated FQNs.
        String clCsv = props.getProperty("phase.classloadCandidates");
        if (clCsv != null && !clCsv.trim().isEmpty()) {
            CLASSLOAD_CANDIDATES = splitCsv(clCsv);
        } else {
            // Sensible default: at least probe the application + main activity.
            CLASSLOAD_CANDIDATES = new String[] { APP_CLASS, ACTIVITY_CLASS };
        }

        println("DiscoverWrapperBase: manifest loaded from " + manifestPath);
        println("  app.packageName     = " + APP_PACKAGE_NAME);
        println("  app.apkPath         = " + APP_APK_PATH);
        println("  app.applicationClass= " + APP_CLASS);
        println("  app.mainActivityClass=" + ACTIVITY_CLASS);
        println("  app.targetSdkVersion= " + APP_TARGET_SDK);
        println("  app.dataDir         = " + APP_DATA_DIR);
        println("  probeServices count = " + PROBE_SERVICES.length);
        println("  classloadCandidates = " + Arrays.toString(CLASSLOAD_CANDIDATES));
        return true;
    }

    static String req(Properties p, String key) {
        String v = p.getProperty(key);
        if (v == null || v.trim().isEmpty()) {
            System.err.println("DiscoverWrapperBase: required manifest key missing: " + key);
            return null;
        }
        return v.trim();
    }

    static String[] splitCsv(String csv) {
        String[] raw = csv.split(",");
        List<String> out = new ArrayList<>(raw.length);
        for (String s : raw) {
            String t = s.trim();
            if (!t.isEmpty()) out.add(t);
        }
        return out.toArray(new String[0]);
    }

    static void recordFailure(String phase, Throwable t) {
        String msg = phase + " threw " + t.getClass().getName() + ": " + t.getMessage();
        eprintln("DISCOVER-FAIL: " + msg);
        caughtFailures.add(msg);
        // Walk the cause chain.  PFCUT no-ops Throwable.fillInStackTrace, so
        // getStackTrace() typically returns []. Defensive: dump what we have,
        // then also try ART-internal backtrace fields via reflection.
        Throwable c = t;
        int depth = 0;
        while (c != null && depth < 6) {
            eprintln("  cause[" + depth + "]: " + c.getClass().getName() + ": "
                    + c.getMessage());
            StackTraceElement[] st = null;
            try { st = c.getStackTrace(); } catch (Throwable ignored) {}
            if (st != null && st.length > 0) {
                int n = Math.min(12, st.length);
                for (int i = 0; i < n; ++i) {
                    eprintln("    at " + st[i]);
                }
            } else {
                // PFCUT-noop case: dump ART internal Throwable fields by reflection.
                dumpThrowableInternals(c);
            }
            // Also print any suppressed exceptions (sometimes JIT/intrinsics
            // attach more context via addSuppressed).
            try {
                Throwable[] sup = c.getSuppressed();
                if (sup != null && sup.length > 0) {
                    for (Throwable s : sup) {
                        eprintln("    suppressed: " + s.getClass().getName() + ": "
                                + s.getMessage());
                    }
                }
            } catch (Throwable ignored) {}
            Throwable next;
            try { next = c.getCause(); } catch (Throwable ignored) { next = null; }
            if (next == c) break;
            c = next;
            ++depth;
        }
        // Inside InvocationTargetException? try .getTargetException() which
        // preserves the original user-thrown chain better than getCause.
        if (t instanceof java.lang.reflect.InvocationTargetException) {
            Throwable tt = ((java.lang.reflect.InvocationTargetException) t).getTargetException();
            if (tt != null && tt != t.getCause()) {
                eprintln("  targetException: " + tt.getClass().getName() + ": " + tt.getMessage());
                dumpThrowableInternals(tt);
            }
        }
    }

    /**
     * Dump fields of a Throwable that survive PFCUT's fillInStackTrace no-op.
     * dalvik's Throwable carries an internal backtrace field (often a long[]
     * of dex_pc + method-id pairs) even when stackTrace[] is empty.
     */
    static void dumpThrowableInternals(Throwable c) {
        if (c == null) return;
        // Look at every declared field on Throwable (and its impl class) +
        // print any non-null reference / non-zero primitive.
        Class<?> cls = c.getClass();
        int rank = 0;
        while (cls != null && rank < 4 && cls != Object.class) {
            java.lang.reflect.Field[] fields;
            try { fields = cls.getDeclaredFields(); }
            catch (Throwable ignored) { break; }
            for (java.lang.reflect.Field f : fields) {
                if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
                try {
                    f.setAccessible(true);
                    Object v = f.get(c);
                    if (v == null) continue;
                    String desc;
                    if (v instanceof long[]) {
                        long[] arr = (long[]) v;
                        StringBuilder sb = new StringBuilder("long[" + arr.length + "]{");
                        int show = Math.min(16, arr.length);
                        for (int i = 0; i < show; ++i) {
                            if (i > 0) sb.append(",");
                            sb.append("0x").append(Long.toHexString(arr[i]));
                        }
                        if (arr.length > show) sb.append(",...");
                        sb.append("}");
                        desc = sb.toString();
                    } else if (v instanceof int[]) {
                        int[] arr = (int[]) v;
                        desc = "int[" + arr.length + "]";
                    } else if (v instanceof Object[]) {
                        Object[] arr = (Object[]) v;
                        desc = arr.getClass().getSimpleName() + "[" + arr.length + "]";
                    } else {
                        desc = v.getClass().getSimpleName() + ": "
                                + v.toString().replace('\n', ' ');
                        if (desc.length() > 200) desc = desc.substring(0, 197) + "...";
                    }
                    eprintln("    [" + cls.getSimpleName() + "." + f.getName()
                            + "] " + desc);
                } catch (Throwable ignored) {}
            }
            cls = cls.getSuperclass();
            ++rank;
        }
    }

    // ===========================================================================
    // PHASE A -- probe all interesting system service names
    // ===========================================================================
    static void phaseA_probeServices() {
        println("=== PHASE A: probing " + PROBE_SERVICES.length + " typical Android service names ===");

        String[] registered;
        try {
            registered = android.os.ServiceManager.listServices();
            println("PHASE A: servicemanager has " + (registered == null ? 0 : registered.length)
                    + " services pre-registered:");
            if (registered != null) {
                for (String s : registered) println("  registered: " + s);
            }
        } catch (Throwable t) {
            recordFailure("listServices()", t);
        }

        int hits = 0, misses = 0;
        for (String name : PROBE_SERVICES) {
            android.os.IBinder b = null;
            String result;
            try {
                b = android.os.ServiceManager.getService(name);
                result = (b != null) ? "found=" + b.toString() : "null";
            } catch (Throwable t) {
                result = "THREW " + t.getClass().getSimpleName() + ": " + t.getMessage();
            }
            probedResults.put(name, result);
            if (b != null) ++hits; else ++misses;
            println("  probe: " + name + " -> " + result);
        }
        println("PHASE A: total " + PROBE_SERVICES.length + " probes; hits=" + hits + " misses=" + misses);
        phaseResults.add("PHASE A: " + hits + "/" + PROBE_SERVICES.length + " services resolved");
    }

    // ===========================================================================
    // PHASE B -- classloading the app
    // ===========================================================================
    static void phaseB_classLoad() {
        File apk = new File(APP_APK_PATH);
        if (!apk.exists()) {
            throw new RuntimeException("APK missing: " + APP_APK_PATH);
        }
        println("=== PHASE B: classload app from " + APP_APK_PATH + " ===");
        try {
            appPCL = new PathClassLoader(APP_APK_PATH,
                    DiscoverWrapperBase.class.getClassLoader());
            // CR24 (2026-05-13): DO NOT stringify appPCL via concatenation.
            // The patched BaseDexClassLoader.toString JNI lambda
            // (loader_to_string in art-latest/patches/runtime/runtime.cc:2750)
            // is reachable through String.valueOf(appPCL), and on the
            // discover-harness call path the JNI function-table slot for
            // NewStringUTF gets overwritten with the kPFCutStaleNativeEntry
            // sentinel 0xfffffffffffffb17 BEFORE the lambda body's null/
            // sentinel guard runs.  Result: SIGBUS BUS_ADRALN with
            // PC=sentinel.  Use identityHashCode + getClass().getName()
            // instead -- neither invokes Object.toString.  See
            // diagnostics/CR24_phaseB_hang_diagnostic.md.
            //
            // NB: CR26 (2026-05-13) updates the substrate fix path:
            // art-latest/patches/runtime/runtime.cc loader_to_string now
            // bypasses the env->functions JNI vtable entirely (uses
            // mirror::String::AllocFromModifiedUtf8 + JNIEnvExt::
            // AddLocalReference) -- see PF-arch-055.  Empirically the
            // SIGBUS no longer reproduces when this workaround is reverted
            // and the CR26-rebuilt dalvikvm is deployed (CR26 verification
            // run: PHASE A-G4 all reached, exit 0).  The workaround is
            // retained here as belt-and-suspenders because:
            //   1. it is harmless (logs are still informative);
            //   2. it protects against any future regression that
            //      re-poisons env->functions on a different call path;
            //   3. CR27's regression had run pre-CR26 and observed the
            //      SIGBUS; the substrate fix landed AFTER CR27.
            // See diagnostics/CR26_pfcut_sentinel_writer.md.
            println("PHASE B: PathClassLoader created (identity hash=0x"
                    + Integer.toHexString(System.identityHashCode(appPCL))
                    + ", class=" + appPCL.getClass().getName() + ")");
        } catch (Throwable t) {
            recordFailure("PathClassLoader(app.apk)", t);
            phaseResults.add("PHASE B: FAILED (PathClassLoader ctor)");
            return;
        }

        int ok = 0;
        for (String c : CLASSLOAD_CANDIDATES) {
            try {
                Class<?> cls = Class.forName(c, false, appPCL);
                println("PHASE B:  loadable: " + c + " -> " + cls);
                ++ok;
            } catch (Throwable t) {
                println("PHASE B:  NOT loadable: " + c + " -> " + t.getClass().getSimpleName()
                        + ": " + t.getMessage());
            }
        }
        phaseResults.add("PHASE B: " + ok + "/" + CLASSLOAD_CANDIDATES.length + " classes loadable");
    }

    // ===========================================================================
    // PHASE C -- Application ctor
    // ===========================================================================
    static void phaseC_applicationCtor() {
        if (appPCL == null) {
            println("=== PHASE C: skipped (no PCL from B) ===");
            phaseResults.add("PHASE C: SKIPPED");
            return;
        }
        println("=== PHASE C: invoke " + APP_CLASS + ".<init>() ===");
        try {
            appCls = Class.forName(APP_CLASS, true, appPCL);
            println("PHASE C:  class init OK: " + appCls);
        } catch (Throwable t) {
            recordFailure("Class.forName(" + APP_CLASS + ", true)", t);
            phaseResults.add("PHASE C: FAILED (Class.forName trigger)");
            return;
        }
        try {
            Constructor<?> ctor = appCls.getDeclaredConstructor();
            ctor.setAccessible(true);
            appInstance = ctor.newInstance();
            println("PHASE C:  Application ctor OK: " + appInstance);
            phaseResults.add("PHASE C: PASSED -- " + APP_CLASS + " instantiated");
        } catch (Throwable t) {
            recordFailure("new " + APP_CLASS + "()", t);
            phaseResults.add("PHASE C: FAILED (ctor)");
        }
    }

    // ===========================================================================
    // PHASE D + E -- attach + onCreate
    // ===========================================================================
    static void phaseDE_attachAndOnCreate() {
        if (appInstance == null) {
            println("=== PHASE D/E: skipped (no Application instance from C) ===");
            phaseResults.add("PHASE D/E: SKIPPED");
            return;
        }
        println("=== PHASE D/E: invoke Application.attach() + onCreate() ===");

        // We CAN'T use Application.attach(Context) directly: that method does
        //   attachBaseContext(context);
        //   mLoadedApk = ContextImpl.getImpl(context).mPackageInfo;
        // and the second line ClassCastExceptions because our impl is NOT a
        // real ContextImpl.  Instead we call attachBaseContext(Context)
        // (protected method on ContextWrapper) directly via reflection.
        Class<?> ctxCls = null;
        Method attachBaseMethod = null;
        try {
            ctxCls = Class.forName("android.content.Context", false, appPCL);
            attachBaseMethod = findMethodWalkSupers(appCls,
                    "attachBaseContext", new Class<?>[]{ ctxCls });
            if (attachBaseMethod == null) {
                println("PHASE D: attachBaseContext(Context) method NOT found in superclass chain");
            } else {
                println("PHASE D: attachBaseContext(Context) method located: " + attachBaseMethod);
            }
        } catch (Throwable t) {
            recordFailure("locate attachBaseContext(Context)", t);
        }

        Object proxyContext = null;
        if (ctxCls != null) {
            try {
                proxyContext = buildProxyContext(ctxCls);
                println("PHASE D: built WestlakeContextImpl: " + proxyContext);
            } catch (Throwable t) {
                recordFailure("buildProxyContext", t);
            }
        }

        if (attachBaseMethod != null && proxyContext != null) {
            try {
                attachBaseMethod.setAccessible(true);
                attachBaseMethod.invoke(appInstance, proxyContext);
                println("PHASE D: attachBaseContext(westlakeContext) OK");
                // M4-PRE11 (2026-05-12): publish the Application back to the
                // WestlakeContextImpl so Hilt's
                // dagger.hilt.android.internal.Contexts.getApplication(Context)
                // walk can resolve the Application from MainActivity.onCreate.
                try {
                    Method setApp = proxyContext.getClass().getMethod(
                            "setAttachedApplication",
                            Class.forName("android.app.Application"));
                    setApp.invoke(proxyContext, appInstance);
                    println("PHASE D: WestlakeContextImpl.setAttachedApplication("
                            + appInstance.getClass().getSimpleName() + ") OK");
                } catch (Throwable wire) {
                    recordFailure("WestlakeContextImpl.setAttachedApplication", wire);
                }
                try {
                    Method gpn = appCls.getMethod("getPackageName");
                    Object pn = gpn.invoke(appInstance);
                    println("PHASE D: post-attach Application.getPackageName() -> " + pn);
                } catch (Throwable probe) {
                    recordFailure("post-attach getPackageName probe", probe);
                }
                phaseResults.add("PHASE D: PASSED (attachBaseContext with WestlakeContextImpl)");
            } catch (Throwable t) {
                recordFailure("Application.attachBaseContext(westlakeContext)", t);
                phaseResults.add("PHASE D: FAILED at attachBaseContext");
            }
        } else {
            println("PHASE D: skipping attach (couldn't build attachBaseContext method or proxy ctx)");
            phaseResults.add("PHASE D: SKIPPED");
        }

        Method onCreateMethod = null;
        try {
            onCreateMethod = appCls.getMethod("onCreate");
        } catch (Throwable t) {
            recordFailure("getMethod(onCreate)", t);
            phaseResults.add("PHASE E: FAILED (onCreate method not found)");
            return;
        }
        try {
            println("PHASE E: calling " + APP_CLASS + ".onCreate()");
            onCreateMethod.invoke(appInstance);
            println("PHASE E: onCreate() returned cleanly (unexpected!)");
            phaseResults.add("PHASE E: PASSED unexpectedly");
        } catch (Throwable t) {
            recordFailure(APP_CLASS + ".onCreate()", t);
            phaseResults.add("PHASE E: FAILED (expected -- diagnoses missing service)");
        }
    }

    /**
     * Build a Context to use as the base context for the app's Application.
     * Uses WestlakeContextImpl from aosp-shim.dex (a Context subclass that
     * provides minimum impls of every Context method).  packageName /
     * apkPath / dataDir / targetSdk are passed via the constructor -- this
     * method is fully generic across apps (per-app config comes from the
     * manifest, not from baked-in constants).
     */
    static Object buildProxyContext(Class<?> ctxCls) throws Throwable {
        Class<?> implCls = Class.forName(
                "com.westlake.services.WestlakeContextImpl");
        Constructor<?> ctor = implCls.getConstructor(
                String.class,         // packageName
                String.class,         // apkPath
                String.class,         // dataDir
                ClassLoader.class,    // classLoader
                int.class);           // targetSdk
        return ctor.newInstance(APP_PACKAGE_NAME, APP_APK_PATH, APP_DATA_DIR,
                appPCL, APP_TARGET_SDK);
    }

    // ===========================================================================
    // PHASE F -- framework Singletons
    // ===========================================================================
    static void phaseF_frameworkSingletons() {
        println("");
        println("=== PHASE F: drive AOSP framework Singletons (binder transaction discovery) ===");
        int probedTransactions = 0;
        int caughtFailuresHere = 0;

        // 1. ActivityManager.getService() -- should call ServiceManager.getService("activity")
        try {
            Class<?> amCls = Class.forName("android.app.ActivityManager");
            Method getSvc = amCls.getMethod("getService");
            getSvc.setAccessible(true);
            Object am = getSvc.invoke(null);
            println("PHASE F:  ActivityManager.getService() returned " + am);
            ++probedTransactions;
            if (am != null) {
                drivenInvoke(am, "getRunningTasks", new Class<?>[] { int.class }, new Object[] { 10 },
                        "IActivityManager.getRunningTasks(maxNum=10)");
                drivenInvoke(am, "getCurrentUser", new Class<?>[] {}, new Object[] {},
                        "IActivityManager.getCurrentUser()");
            }
        } catch (Throwable t) {
            ++caughtFailuresHere;
            recordFailure("PHASE F: ActivityManager.getService chain", t);
        }

        // 2. ActivityThread.getPackageManager() -- drives ServiceManager.getService("package")
        try {
            Class<?> atCls = Class.forName("android.app.ActivityThread");
            Method getPm = atCls.getMethod("getPackageManager");
            getPm.setAccessible(true);
            Object pm = getPm.invoke(null);
            println("PHASE F:  ActivityThread.getPackageManager() returned " + pm);
            ++probedTransactions;
            if (pm != null) {
                drivenInvoke(pm, "getApplicationInfo", new Class<?>[] { String.class, int.class, int.class },
                        new Object[] { APP_PACKAGE_NAME, 0, 0 },
                        "IPackageManager.getApplicationInfo(" + APP_PACKAGE_NAME + ")");
                drivenInvoke(pm, "getInstalledPackages",
                        new Class<?>[] { int.class, int.class }, new Object[] { 0, 0 },
                        "IPackageManager.getInstalledPackages(flags=0)");
            }
        } catch (Throwable t) {
            ++caughtFailuresHere;
            recordFailure("PHASE F: ActivityThread.getPackageManager chain", t);
        }

        // 3. WindowManagerGlobal.getWindowManagerService() -- drives getService("window")
        try {
            Class<?> wmgCls = Class.forName("android.view.WindowManagerGlobal");
            Method getWms = wmgCls.getMethod("getWindowManagerService");
            getWms.setAccessible(true);
            Object wms = getWms.invoke(null);
            println("PHASE F:  WindowManagerGlobal.getWindowManagerService() returned " + wms);
            ++probedTransactions;
            if (wms != null) {
                drivenInvoke(wms, "isViewServerRunning", null, new Object[] {},
                        "IWindowManager.isViewServerRunning()");
            }
        } catch (Throwable t) {
            ++caughtFailuresHere;
            recordFailure("PHASE F: WindowManagerGlobal.getWindowManagerService chain", t);
        }

        // 4. DisplayManagerGlobal.getInstance() -- drives getService("display")
        try {
            Class<?> dmgCls = Class.forName("android.hardware.display.DisplayManagerGlobal");
            Method getInst = dmgCls.getMethod("getInstance");
            getInst.setAccessible(true);
            Object dmg = getInst.invoke(null);
            println("PHASE F:  DisplayManagerGlobal.getInstance() returned " + dmg);
            ++probedTransactions;
            if (dmg != null) {
                drivenInvoke(dmg, "getDisplayIds", null, new Object[] {},
                        "DisplayManagerGlobal.getDisplayIds()");
            }
        } catch (Throwable t) {
            ++caughtFailuresHere;
            recordFailure("PHASE F: DisplayManagerGlobal chain", t);
        }

        // 5. AudioManager -- try AudioSystem.getMasterMute()
        try {
            Class<?> asCls = Class.forName("android.media.AudioSystem");
            Method m = null;
            try {
                m = asCls.getMethod("getMasterMute");
            } catch (NoSuchMethodException nsme) { /* fall through */ }
            if (m != null) {
                m.setAccessible(true);
                Object r = m.invoke(null);
                println("PHASE F:  AudioSystem.getMasterMute() returned " + r);
                ++probedTransactions;
            }
        } catch (Throwable t) {
            ++caughtFailuresHere;
            recordFailure("PHASE F: AudioSystem chain", t);
        }

        // 6. InputMethodManager class probe (no Context needed).
        try {
            Class<?> immCls = Class.forName("android.view.inputmethod.InputMethodManager");
            println("PHASE F:  InputMethodManager class loadable: " + immCls);
            ++probedTransactions;
        } catch (Throwable t) {
            ++caughtFailuresHere;
            recordFailure("PHASE F: InputMethodManager class probe", t);
        }

        // 7. IActivityManager.Stub.asInterface(null) sanity probe.
        try {
            Class<?> iamStubCls = Class.forName("android.app.IActivityManager$Stub");
            Method asI = iamStubCls.getMethod("asInterface", Class.forName("android.os.IBinder"));
            Object r = asI.invoke(null, new Object[]{ null });
            println("PHASE F:  IActivityManager.Stub.asInterface(null) -> " + r);
        } catch (Throwable t) {
            ++caughtFailuresHere;
            recordFailure("PHASE F: IActivityManager.Stub.asInterface(null)", t);
        }

        phaseResults.add("PHASE F: drove " + probedTransactions + " framework Singletons, "
                + caughtFailuresHere + " failed");
    }

    // ===========================================================================
    // PHASE G -- MainActivity launch driver (M4-PRE3, 2026-05-12)
    // ===========================================================================
    static void phaseG_mainActivityLaunch() {
        println("");
        println("=== PHASE G: MainActivity launch driver (M4-PRE3) ===");

        // ----------------------------- G1 prereq ------------------------------
        boolean looperReady = false;
        try {
            Class<?> looperCls = Class.forName("android.os.Looper");
            Method getMain = looperCls.getMethod("getMainLooper");
            Object existing = getMain.invoke(null);
            if (existing == null) {
                try {
                    Method prep = looperCls.getMethod("prepareMainLooper");
                    prep.invoke(null);
                    Object now = getMain.invoke(null);
                    println("PHASE G:  Looper.prepareMainLooper() -> getMainLooper now: " + now);
                    looperReady = now != null;
                } catch (Throwable inner) {
                    Throwable c = inner;
                    while (c.getCause() != null && c != c.getCause()) c = c.getCause();
                    println("PHASE G:  Looper.prepareMainLooper FAILED (continuing): "
                            + c.getClass().getSimpleName() + ": " + c.getMessage());
                }
            } else {
                println("PHASE G:  main Looper already prepared: " + existing);
                looperReady = true;
            }
        } catch (Throwable t) {
            recordFailure("PHASE G: Looper inspection", t);
        }

        // ----------------------------- G2 newActivity ------------------------
        if (appPCL == null) {
            println("PHASE G: SKIPPED (no PathClassLoader from B)");
            phaseResults.add("PHASE G: SKIPPED (no app PCL)");
            return;
        }
        if (appInstance == null) {
            println("PHASE G: SKIPPED (no app Application from C/D)");
            phaseResults.add("PHASE G: SKIPPED (no app Application)");
            return;
        }
        if (!looperReady) {
            phaseResults.add("PHASE G: NOTE -- main Looper unavailable; "
                    + "MainActivity ctor will likely fail at Handler. "
                    + "M4-PRE4 candidate: MessageQueue.nativeInit JNI bridge.");
        }

        Object proxyContext = null;
        try {
            Class<?> ctxCls = Class.forName("android.content.Context");
            proxyContext = buildProxyContext(ctxCls);
            println("PHASE G:  rebuilt WestlakeContextImpl: " + proxyContext);
            if (appInstance != null) {
                try {
                    Method setApp = proxyContext.getClass().getMethod(
                            "setAttachedApplication",
                            Class.forName("android.app.Application"));
                    setApp.invoke(proxyContext, appInstance);
                    println("PHASE G:  WestlakeContextImpl.setAttachedApplication("
                            + appInstance.getClass().getSimpleName() + ") OK");
                } catch (Throwable wire) {
                    recordFailure("PHASE G: WestlakeContextImpl.setAttachedApplication", wire);
                }
            }
        } catch (Throwable t) {
            recordFailure("PHASE G: buildProxyContext", t);
            phaseResults.add("PHASE G: FAILED at buildProxyContext");
            return;
        }

        Object launchIntent = null;
        try {
            Class<?> componentNameCls = Class.forName("android.content.ComponentName");
            Constructor<?> cnCtor = componentNameCls.getConstructor(String.class, String.class);
            Object componentName = cnCtor.newInstance(APP_PACKAGE_NAME, ACTIVITY_CLASS);

            Class<?> intentCls = Class.forName("android.content.Intent");
            Constructor<?> intentCtor = intentCls.getConstructor();
            launchIntent = intentCtor.newInstance();
            Method setComponent = intentCls.getMethod("setComponent", componentNameCls);
            setComponent.invoke(launchIntent, componentName);
            println("PHASE G:  built Intent with ComponentName: " + launchIntent);
        } catch (Throwable t) {
            recordFailure("PHASE G: build Intent+ComponentName", t);
            phaseResults.add("PHASE G: FAILED at Intent construction");
            return;
        }

        try {
            Class<?> instrCls = Class.forName("android.app.Instrumentation");
            Constructor<?> instrCtor = instrCls.getConstructor();
            Object instrumentation = instrCtor.newInstance();
            Method newActivity = instrCls.getMethod("newActivity",
                    ClassLoader.class, String.class,
                    Class.forName("android.content.Intent"));
            mainActivityInstance = newActivity.invoke(instrumentation,
                    appPCL, ACTIVITY_CLASS, launchIntent);
            mainActivityCls = mainActivityInstance.getClass();
            println("PHASE G:  Instrumentation.newActivity(MainActivity) -> "
                    + mainActivityInstance);
            println("PHASE G:    class: " + mainActivityCls.getName());
            println("PHASE G:    superclass: " + mainActivityCls.getSuperclass().getName());
            phaseResults.add("PHASE G2: PASSED -- MainActivity instantiated via Instrumentation.newActivity");
        } catch (Throwable t) {
            recordFailure("PHASE G2: Instrumentation.newActivity(MainActivity)", t);
            phaseResults.add("PHASE G2: FAILED (newActivity)");
            return;
        }

        // ----------------------------- G3 Activity.attach --------------------
        Method attachMethod = locateActivityAttach(mainActivityCls);
        if (attachMethod == null) {
            recordFailure("PHASE G3: Activity.attach locate",
                    new NoSuchMethodException("no Activity.attach with Context first arg"));
            phaseResults.add("PHASE G3: FAILED (attach not found)");
            return;
        }
        Class<?>[] paramTypes = attachMethod.getParameterTypes();
        println("PHASE G3: located Activity.attach with " + paramTypes.length + " params");
        for (int i = 0; i < paramTypes.length; ++i) {
            println("    arg[" + i + "] " + paramTypes[i].getName());
        }

        Object[] attachArgs = buildAttachArgs(paramTypes, proxyContext, launchIntent);
        if (attachArgs == null) {
            phaseResults.add("PHASE G3: FAILED (couldn't build attach args)");
            return;
        }

        try {
            attachMethod.setAccessible(true);
            attachMethod.invoke(mainActivityInstance, attachArgs);
            println("PHASE G3: Activity.attach(...) returned cleanly");
            phaseResults.add("PHASE G3: PASSED -- Activity.attach succeeded");
        } catch (Throwable t) {
            recordFailure("PHASE G3: Activity.attach", t);
            phaseResults.add("PHASE G3: FAILED -- attach threw "
                    + t.getClass().getSimpleName()
                    + " (likely M4-PRE4 candidate; see log)");
            return;
        }

        // ----------------------------- G4 Activity.onCreate ------------------
        try {
            println("PHASE G4: calling MainActivity.onCreate(null)");
            Method onCreate = mainActivityCls.getMethod("onCreate",
                    Class.forName("android.os.Bundle"));
            onCreate.setAccessible(true);
            // Climb superclasses + print where onCreate is declared (Hilt-generated
            // subclasses often own the implementation we want).
            try {
                eprintln("PHASE G4: onCreate declared in: "
                        + onCreate.getDeclaringClass().getName());
            } catch (Throwable ignored) {}
            // CR54 (2026-05-13): drive LifecycleRegistry to CREATED state BEFORE
            // invoking user's onCreate. ComponentActivity ctor inits mLifecycleRegistry
            // at INITIALIZED; framework's Activity.performCreate moves it to CREATED
            // via handleLifecycleEvent(ON_CREATE). Skipping that means ActivityResultRegistry's
            // register() reads lifecycle state from an uninitialized observer table → NPE
            // "Attempt to read from null array". Call handleLifecycleEvent reflectively.
            try {
                Method getLifecycle = mainActivityCls.getMethod("getLifecycle");
                getLifecycle.setAccessible(true);
                Object lifecycle = getLifecycle.invoke(mainActivityInstance);
                if (lifecycle != null) {
                    eprintln("PHASE G4: getLifecycle() -> " + lifecycle.getClass().getName());
                    // Dump lifecycle's methods (R8-obfuscated) + key fields
                    try {
                        Class<?> lc = lifecycle.getClass();
                        int n = 0;
                        while (lc != null && lc != Object.class && n < 40) {
                            for (Method m : lc.getDeclaredMethods()) {
                                Class<?>[] pt = m.getParameterTypes();
                                StringBuilder sig = new StringBuilder();
                                for (int i = 0; i < pt.length; i++) {
                                    if (i > 0) sig.append(",");
                                    sig.append(pt[i].getSimpleName());
                                }
                                eprintln("PHASE G4:   lc.m=" + lc.getSimpleName() + "." + m.getName() + "(" + sig + ") -> " + m.getReturnType().getSimpleName());
                                n++;
                                if (n >= 40) break;
                            }
                            lc = lc.getSuperclass();
                        }
                        Class<?> lf = lifecycle.getClass();
                        int fc = 0;
                        while (lf != null && lf != Object.class && fc < 30) {
                            for (java.lang.reflect.Field df : lf.getDeclaredFields()) {
                                df.setAccessible(true);
                                Object fv = null;
                                try { fv = df.get(lifecycle); } catch (Throwable ignored) {}
                                eprintln("PHASE G4:   lc.f=" + lf.getSimpleName() + "." + df.getName() + ":" + df.getType().getSimpleName() + " = " + (fv == null ? "null" : (fv.getClass().getSimpleName() + (fv.getClass().isArray() ? "[" + java.lang.reflect.Array.getLength(fv) + "]" : ""))));
                                fc++;
                                if (fc >= 30) break;
                            }
                            lf = lf.getSuperclass();
                        }
                    } catch (Throwable ignored) {}
                    // Lookup Lifecycle.Event.ON_CREATE
                    Class<?> eventCls = Class.forName("androidx.lifecycle.Lifecycle$Event");
                    Object onCreateEvent = null;
                    for (Object enumVal : eventCls.getEnumConstants()) {
                        if ("ON_CREATE".equals(enumVal.toString())) {
                            onCreateEvent = enumVal; break;
                        }
                    }
                    if (onCreateEvent != null) {
                        // Match by parameter-type name containing "Event" (R8 may load a
                        // separate copy of Lifecycle$Event under the same dex).
                        Method handle = null;
                        Class<?> walk = lifecycle.getClass();
                        while (walk != null && walk != Object.class && handle == null) {
                            for (Method m : walk.getDeclaredMethods()) {
                                Class<?>[] pt = m.getParameterTypes();
                                if (pt.length != 1) continue;
                                if (m.getReturnType() != void.class) continue;
                                String pname = pt[0].getSimpleName();
                                String pfull = pt[0].getName();
                                // Match any parameter type whose name ends with "Event"
                                // (handles obfuscated copies + framework's Lifecycle$Event).
                                if (pname.endsWith("Event") || pfull.contains("Lifecycle$Event")) {
                                    handle = m;
                                    break;
                                }
                            }
                            walk = walk.getSuperclass();
                        }
                        if (handle != null) {
                            handle.setAccessible(true);
                            try {
                                handle.invoke(lifecycle, onCreateEvent);
                                eprintln("PHASE G4: Lifecycle " + handle.getDeclaringClass().getSimpleName() + "." + handle.getName() + "(ON_CREATE) OK");
                            } catch (IllegalArgumentException iae) {
                                // Parameter type mismatch — R8 has a separate Event class.
                                // Try to find the obfuscated Event class and convert.
                                Class<?> targetEventCls = handle.getParameterTypes()[0];
                                Object converted = null;
                                if (targetEventCls.isEnum()) {
                                    for (Object ev : targetEventCls.getEnumConstants()) {
                                        if ("ON_CREATE".equals(ev.toString())) { converted = ev; break; }
                                    }
                                }
                                if (converted != null) {
                                    handle.invoke(lifecycle, converted);
                                    eprintln("PHASE G4: Lifecycle " + handle.getName() + "(ON_CREATE via " + targetEventCls.getSimpleName() + ") OK");
                                } else {
                                    eprintln("PHASE G4: lifecycle handleEvent invoke failed: " + iae.getMessage());
                                }
                            }
                        } else {
                            eprintln("PHASE G4: no single-Event-arg void method found on lifecycle");
                        }
                    } else {
                        eprintln("PHASE G4: Lifecycle.Event.ON_CREATE enum constant not found");
                    }
                } else {
                    eprintln("PHASE G4: getLifecycle() returned null");
                }
            } catch (Throwable t) {
                eprintln("PHASE G4: lifecycle pre-drive skipped: "
                        + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
            // CR53 (2026-05-13): pre-seed androidx ActivityResultRegistry.
            // Discovery traced G4 NPE to ComponentActivity.<init> calling
            // registerForActivityResult before any onRestoreInstanceState has
            // populated mActivityResultRegistry.mPendingResults arrays. Call
            // onRestoreInstanceState(null) reflectively so the registry sets
            // its internal arrays to empty rather than leaving them null.
            try {
                Object reg = null;
                try {
                    Method getReg = mainActivityCls.getMethod("getActivityResultRegistry");
                    getReg.setAccessible(true);
                    reg = getReg.invoke(mainActivityInstance);
                } catch (NoSuchMethodException nsme) {
                    // Try field walk on parent ComponentActivity
                    Class<?> walk = mainActivityCls;
                    while (walk != null && reg == null) {
                        for (java.lang.reflect.Field f : walk.getDeclaredFields()) {
                            String n = f.getName();
                            if (n.equals("mActivityResultRegistry")
                                    || n.contains("ResultRegistry")) {
                                f.setAccessible(true);
                                try { reg = f.get(mainActivityInstance); } catch (Throwable ignored) {}
                                if (reg != null) break;
                            }
                        }
                        walk = walk.getSuperclass();
                    }
                }
                if (reg != null) {
                    eprintln("PHASE G4: pre-seed reg class=" + reg.getClass().getName());
                    // Dump ALL methods to identify the obfuscated onRestoreInstanceState
                    try {
                        Class<?> dumpC = reg.getClass();
                        int methCount = 0;
                        while (dumpC != null && dumpC != Object.class && methCount < 40) {
                            for (Method dm : dumpC.getDeclaredMethods()) {
                                Class<?>[] dpt = dm.getParameterTypes();
                                StringBuilder sig = new StringBuilder();
                                for (int i = 0; i < dpt.length; i++) {
                                    if (i > 0) sig.append(",");
                                    sig.append(dpt[i].getSimpleName());
                                }
                                eprintln("PHASE G4:   reg.m=" + dumpC.getSimpleName() + "." + dm.getName() + "(" + sig + ") -> " + dm.getReturnType().getSimpleName());
                                methCount++;
                                if (methCount >= 40) break;
                            }
                            dumpC = dumpC.getSuperclass();
                        }
                        // Also dump fields
                        Class<?> dumpF = reg.getClass();
                        int fldCount = 0;
                        while (dumpF != null && dumpF != Object.class && fldCount < 30) {
                            for (java.lang.reflect.Field df : dumpF.getDeclaredFields()) {
                                df.setAccessible(true);
                                Object fv = null;
                                try { fv = df.get(reg); } catch (Throwable ignored) {}
                                eprintln("PHASE G4:   reg.f=" + dumpF.getSimpleName() + "." + df.getName() + ":" + df.getType().getSimpleName() + " = " + (fv == null ? "null" : fv.getClass().getSimpleName() + "@" + System.identityHashCode(fv)));
                                fldCount++;
                                if (fldCount >= 30) break;
                            }
                            dumpF = dumpF.getSuperclass();
                        }
                    } catch (Throwable ignored) {}
                    Class<?> bundleCls = Class.forName("android.os.Bundle");
                    // Search ALL methods on ALL ancestors taking single Bundle (R8 may obfuscate
                    // the method name to a single letter). Try each non-getter; on InvocationTargetException
                    // log + continue; if any succeeds without throwing, count as pre-seed.
                    Class<?> walk = reg.getClass();
                    int tried = 0, seeded = 0;
                    while (walk != null && walk != Object.class) {
                        for (Method m : walk.getDeclaredMethods()) {
                            Class<?>[] pt = m.getParameterTypes();
                            if (pt.length != 1) continue;
                            if (!bundleCls.isAssignableFrom(pt[0])) continue;
                            String name = m.getName();
                            // Skip getters (start with 'get') + skip very-short obfuscated names of 1 char if not in this class
                            if (name.startsWith("get")) continue;
                            try {
                                m.setAccessible(true);
                                m.invoke(reg, new Object[] { null });
                                eprintln("PHASE G4: pre-seed called " + walk.getSimpleName() + "." + name + "(Bundle) OK");
                                seeded++;
                            } catch (Throwable t) {
                                eprintln("PHASE G4: pre-seed " + walk.getSimpleName() + "." + name + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
                            }
                            tried++;
                            if (tried > 12) break;
                        }
                        if (tried > 12) break;
                        walk = walk.getSuperclass();
                    }
                    eprintln("PHASE G4: pre-seed Bundle-method walk tried=" + tried + " seeded=" + seeded);
                } else {
                    eprintln("PHASE G4: getActivityResultRegistry() returned null or absent");
                }
            } catch (Throwable seedEx) {
                eprintln("PHASE G4: ActivityResultRegistry pre-seed skipped: "
                        + seedEx.getClass().getName() + ": " + seedEx.getMessage());
            }
            onCreate.invoke(mainActivityInstance, new Object[] { null });
            println("PHASE G4: onCreate(null) returned cleanly (unexpected!)");
            phaseResults.add("PHASE G4: PASSED unexpectedly");
        } catch (Throwable t) {
            recordFailure("PHASE G4: MainActivity.onCreate(null)", t);
            phaseResults.add("PHASE G4: FAILED (expected -- diagnoses needed service)");
        }
    }

    /**
     * Locate Activity.attach across known API-level signatures.  AOSP
     * Activity.attach is package-private + @hide; signature varies:
     *   Android 11: 18 args
     *   Android 13+: 19-20 args
     *   V2 substrate (WestlakeActivity, post-V2-Step2): 6 args
     *     (Context, Application, Intent, ComponentName, Window, Instrumentation).
     * CR29-1 (2026-05-13): lower gate from >=10 to >=6 + validate first
     * 4 param types so we accept BOTH the framework 18+arg shape AND the
     * V2 6-arg shape.  We still prefer the longer signature when both
     * are declared on the same class (defense; V2 should never expose
     * both, but harmless).
     */
    static Method locateActivityAttach(Class<?> startCls) {
        Class<?> c = startCls;
        while (c != null) {
            Method best = null;
            for (Method m : c.getDeclaredMethods()) {
                if (!m.getName().equals("attach")) continue;
                Class<?>[] pt = m.getParameterTypes();
                // V2 substrate (6-arg) OR framework (18+ arg).
                if (pt.length < 6) continue;
                // Validate first 4 params -- shared prefix of both shapes:
                //   (Context, Application, Intent, ComponentName, ...)
                if (!"android.content.Context".equals(pt[0].getName())) continue;
                if (!"android.app.Application".equals(pt[1].getName())) continue;
                if (!"android.content.Intent".equals(pt[2].getName())) continue;
                if (!"android.content.ComponentName".equals(pt[3].getName())) continue;
                if (best == null || pt.length > best.getParameterTypes().length) {
                    best = m;
                }
            }
            if (best != null) return best;
            c = c.getSuperclass();
        }
        return null;
    }

    /**
     * Construct minimum args for Activity.attach based on its reflected
     * parameter types.  Returns null if any required arg can't be built.
     */
    static Object[] buildAttachArgs(Class<?>[] paramTypes, Object context, Object intent) {
        Object[] out = new Object[paramTypes.length];
        try {
            for (int i = 0; i < paramTypes.length; ++i) {
                String name = paramTypes[i].getName();
                if (paramTypes[i].isPrimitive()) {
                    if (name.equals("int") || name.equals("long")
                            || name.equals("short") || name.equals("byte")) {
                        out[i] = 0;
                    } else if (name.equals("boolean")) {
                        out[i] = false;
                    } else {
                        out[i] = null;
                    }
                    continue;
                }
                switch (name) {
                    case "android.content.Context":
                        out[i] = context;
                        break;
                    case "android.app.ActivityThread":
                        out[i] = null;
                        break;
                    case "android.app.Instrumentation":
                        out[i] = Class.forName("android.app.Instrumentation")
                                .getConstructor().newInstance();
                        break;
                    case "android.os.IBinder":
                        out[i] = Class.forName("android.os.Binder")
                                .getConstructor().newInstance();
                        break;
                    case "android.app.Application":
                        out[i] = appInstance;
                        break;
                    case "android.content.Intent":
                        out[i] = intent;
                        break;
                    case "android.content.pm.ActivityInfo":
                        out[i] = buildActivityInfo();
                        break;
                    case "android.content.res.Configuration":
                        out[i] = Class.forName("android.content.res.Configuration")
                                .getConstructor().newInstance();
                        break;
                    case "java.lang.CharSequence":
                    case "java.lang.String":
                        out[i] = null;
                        break;
                    case "android.app.Activity":
                        out[i] = null;
                        break;
                    default:
                        out[i] = null;
                        break;
                }
                println("PHASE G3:    attach arg[" + i + "] " + name + " = " + out[i]);
            }
            return out;
        } catch (Throwable t) {
            recordFailure("PHASE G3: buildAttachArgs", t);
            return null;
        }
    }

    /**
     * Build a minimum ActivityInfo for Activity.attach.  All per-app fields
     * (packageName, processName, targetSdkVersion, sourceDir/publicSourceDir,
     * dataDir, name=ACTIVITY_CLASS) are sourced from the loaded manifest, NOT
     * baked into this method.  This is what makes the harness reusable.
     */
    static Object buildActivityInfo() throws Throwable {
        Class<?> aiCls = Class.forName("android.content.pm.ActivityInfo");
        Object ai = aiCls.getConstructor().newInstance();

        Class<?> appInfoCls = Class.forName("android.content.pm.ApplicationInfo");
        Object appInfo = appInfoCls.getConstructor().newInstance();
        java.lang.reflect.Field pn = appInfoCls.getField("packageName");
        pn.set(appInfo, APP_PACKAGE_NAME);
        java.lang.reflect.Field pnInfo = appInfoCls.getField("processName");
        pnInfo.set(appInfo, APP_PACKAGE_NAME);
        java.lang.reflect.Field tsdk = appInfoCls.getField("targetSdkVersion");
        tsdk.setInt(appInfo, APP_TARGET_SDK);
        java.lang.reflect.Field uid = appInfoCls.getField("uid");
        uid.setInt(appInfo, android.os.Process.myUid());
        java.lang.reflect.Field src = appInfoCls.getField("sourceDir");
        src.set(appInfo, APP_APK_PATH);
        java.lang.reflect.Field pub = appInfoCls.getField("publicSourceDir");
        pub.set(appInfo, APP_APK_PATH);
        java.lang.reflect.Field dd = appInfoCls.getField("dataDir");
        dd.set(appInfo, APP_DATA_DIR);

        java.lang.reflect.Field aiField = Class.forName("android.content.pm.ComponentInfo")
                .getField("applicationInfo");
        aiField.set(ai, appInfo);
        java.lang.reflect.Field nameField = Class.forName("android.content.pm.PackageItemInfo")
                .getField("name");
        nameField.set(ai, ACTIVITY_CLASS);
        java.lang.reflect.Field pkgField = Class.forName("android.content.pm.PackageItemInfo")
                .getField("packageName");
        pkgField.set(ai, APP_PACKAGE_NAME);
        return ai;
    }

    static void drivenInvoke(Object target, String method, Class<?>[] argTypes,
                             Object[] args, String description) {
        try {
            Class<?> cls = target.getClass();
            Method m = findMethodWalkInterfaces(cls, method, argTypes);
            if (m == null) {
                println("PHASE F:    " + description + " -- method not found on " + cls.getName());
                return;
            }
            m.setAccessible(true);
            Object r = m.invoke(target, args);
            println("PHASE F:    " + description + " -> " + r);
        } catch (Throwable t) {
            Throwable c = t;
            while (c.getCause() != null && c != c.getCause()) c = c.getCause();
            println("PHASE F:    " + description + " -> THREW "
                    + c.getClass().getSimpleName() + ": " + c.getMessage());
        }
    }

    static Method findMethodWalkInterfaces(Class<?> cls, String name, Class<?>[] argTypes) {
        Method m = findMethodWalkSupers(cls, name, argTypes);
        if (m != null) return m;
        for (Class<?> iface : cls.getInterfaces()) {
            try {
                if (argTypes != null) {
                    return iface.getMethod(name, argTypes);
                } else {
                    for (Method im : iface.getMethods()) {
                        if (im.getName().equals(name) && im.getParameterCount() == 0) {
                            return im;
                        }
                    }
                }
            } catch (NoSuchMethodException ignored) { /* try next */ }
        }
        for (Method m2 : cls.getMethods()) {
            if (m2.getName().equals(name)
                    && (argTypes == null ? m2.getParameterCount() == 0
                                          : m2.getParameterCount() == argTypes.length)) {
                return m2;
            }
        }
        return null;
    }

    static Method findMethodWalkSupers(Class<?> cls, String name, Class<?>[] argTypes) {
        Class<?> c = cls;
        while (c != null) {
            try {
                Method m = c.getDeclaredMethod(name, argTypes);
                m.setAccessible(true);
                return m;
            } catch (NoSuchMethodException nsme) {
                c = c.getSuperclass();
            }
        }
        return null;
    }

    // ===========================================================================
    // Final report
    // ===========================================================================
    static void printFinalReport() {
        println("");
        println("============================================================");
        println("DISCOVERY REPORT -- service probes + failures");
        println("  app.packageName    = " + APP_PACKAGE_NAME);
        println("  app.applicationClass=" + APP_CLASS);
        println("============================================================");
        println("");
        println("Phase outcomes:");
        for (String r : phaseResults) println("  " + r);
        println("");
        println("Service-name lookup results (PHASE A probes):");
        int found = 0;
        for (Map.Entry<String, String> e : probedResults.entrySet()) {
            if (!"null".equals(e.getValue())) {
                ++found;
                println("  FOUND  " + e.getKey() + " -> " + e.getValue());
            }
        }
        println("  (" + found + " of " + probedResults.size() + " probed services were found)");
        println("");
        println("Failures encountered (caused by missing services or framework gaps):");
        int n = 0;
        for (String f : caughtFailures) {
            println("  [" + (++n) + "] " + f);
        }
        if (caughtFailures.isEmpty()) {
            println("  (none -- nothing reached failure path)");
        }
        println("");
        println("============================================================");
        println("END OF DISCOVERY REPORT");
        println("============================================================");
    }
}
