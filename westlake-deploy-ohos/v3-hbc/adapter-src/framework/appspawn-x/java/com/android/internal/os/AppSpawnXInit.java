/*
 * AppSpawnXInit.java
 *
 * Java-side initialization for appspawn-x hybrid spawner.
 * Called from native code (appspawnx_runtime.cpp) via JNI.
 *
 * Two entry points:
 * 1. preload() - Called in parent process during startup to preload
 *    Android framework classes, resources, shared libraries, and adapter layer.
 *    All preloaded data is shared with child processes via fork() COW.
 *
 * 2. initChild(procName, targetClass, targetSdkVersion) - Called in child process
 *    after fork() and OH security specialization. Initializes the Android runtime
 *    environment and launches the target main class (typically ActivityThread).
 */
package com.android.internal.os;

import android.os.Process;
import android.util.Log;
import dalvik.system.VMRuntime;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class AppSpawnXInit {

    private static final String TAG = "AppSpawnXInit";

    // ============================================================
    // B.32 (2026-04-29): direct hilog logger via JNI native method.
    //
    // System.err goes to /dev/null under init service.  Android Log.i goes
    // through the liblog.so → hilog bridge constructor which apparently
    // doesn't survive fork (B.31 verified zero D002000 hilog entries in
    // child).  This native method bypasses both: JNI direct call into
    // liboh_adapter_bridge.so → HiLogPrint(LOG_CORE, INFO, 0xD000F00, ...).
    //
    // **Why on AppSpawnXInit (non-BCP) and NOT on OHEnvironment (BCP)**:
    // adding a method to a BCP class changes its dex bytes → boot image
    // checksum mismatch → ART rejects boot image at startup with SIGABRT
    // (per memory feedback_art_bootimage_coupling.md).  AppSpawnXInit lives
    // in oh-adapter-runtime.jar (non-BCP, PathClassLoader-loaded), so its
    // bytes can change freely without rebuilding boot image.
    //
    // Bridge.so loads with parent appspawn-x's OHEnvironment.<clinit>;
    // child inherits all symbol bindings via fork.  JNI lookup of
    // Java_com_android_internal_os_AppSpawnXInit_nativeHiLog happens lazily
    // on first call (post-fork), should resolve fine.
    //
    // Filter via: hdc shell hilog | grep "AppSpawnXJava"
    // ============================================================
    public static native void nativeHiLog(String tag, String msg);

    private static volatile boolean sAppLogJniBroken = false;

    public static void appLog(String msg) {
        if (!sAppLogJniBroken) {
            try {
                nativeHiLog("AppSpawnXJava", "[B32-J] " + msg);
                return;
            } catch (Throwable t) {
                // UnsatisfiedLinkError or similar — fall back to stderr,
                // and remember so we don't keep retrying.
                sAppLogJniBroken = true;
                System.err.println("[appLog] nativeHiLog UNAVAILABLE, falling back to stderr: " + t);
                System.err.flush();
            }
        }
        System.err.println("[B32-J] " + msg);
        System.err.flush();
    }

    // ============================================================
    // B.35.B (2026-04-29 EOD+2): redirectLogStreamsToHiLog
    //
    // AOSP RuntimeInit.redirectLogStreams() replaces System.out/err with
    // AndroidPrintStream → Log.println → liblog → hilog.  In our adapter
    // the liblog→hilog bridge is broken across fork (per feedback.txt P3
    // backlog).  We bypass that by routing through nativeHiLog (direct
    // HiLogPrint via JNI).  Effect: any code in app classes (including
    // AOSP ActivityThread, our adapter classes) that calls System.err
    // (or System.out) becomes visible in hilog as
    //   "AppSpawnXJava: [stderr] <msg>"  /  "AppSpawnXJava: [stdout] <msg>"
    //
    // Must be called AFTER B.35.A explicit RegisterNatives runs in C++
    // (i.e., after preload returns to native).  Calling earlier causes
    // nativeHiLog UnsatisfiedLinkError silently caught + System.err
    // recursion → infinite loop.  Hence we call it as the FIRST line of
    // initChild (which runs after appspawnx_runtime.cpp:preload's
    // RegisterNatives has already bound nativeHiLog to the JNI impl).
    // ============================================================
    private static void redirectLogStreamsToHiLog() {
        // Only redirect if nativeHiLog is actually bound (i.e., not broken).
        // Probe by calling once; if it throws, leave streams alone.
        if (sAppLogJniBroken) {
            return;
        }
        try {
            nativeHiLog("AppSpawnXJava", "[B35-J] redirectLogStreamsToHiLog probe");
        } catch (Throwable t) {
            sAppLogJniBroken = true;
            return;
        }

        System.setOut(new PrintStream(new HiLogOutputStream("[stdout]"), true));
        System.setErr(new PrintStream(new HiLogOutputStream("[stderr]"), true));
    }

    /** Line-buffered OutputStream that emits each line via nativeHiLog. */
    private static final class HiLogOutputStream extends OutputStream {
        private final String prefix;
        private final StringBuilder buf = new StringBuilder(256);

        HiLogOutputStream(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public synchronized void write(int b) {
            if (b == '\n') {
                flushLine();
            } else if (b != '\r') {
                buf.append((char) (b & 0xff));
            }
        }

        @Override
        public synchronized void write(byte[] b, int off, int len) {
            for (int i = 0; i < len; i++) {
                write(b[off + i] & 0xff);
            }
        }

        @Override
        public synchronized void flush() {
            if (buf.length() > 0) {
                flushLine();
            }
        }

        private void flushLine() {
            if (buf.length() == 0) return;
            String line = buf.toString();
            buf.setLength(0);
            try {
                nativeHiLog("AppSpawnXJava", prefix + " " + line);
            } catch (Throwable t) {
                // Don't recurse; fall back to writing to fd 2 (likely /dev/null).
                // Stay silent to avoid noise.
            }
        }
    }

    // ============================================================
    // Parent Process: Preload (called once during startup)
    // ============================================================

    /**
     * Preload Android framework classes, resources, and adapter layer.
     * Called from native appspawnx_runtime.cpp in the parent daemon process.
     * All loaded classes/resources are shared with children via COW after fork().
     */
    public static void preload() {
        Log.i(TAG, "=== Preloading Android framework ===");
        long startTime = System.currentTimeMillis();

        // Override java.security provider list BEFORE any class triggers
        // sun.security.jca.Providers.<clinit>.  Conscrypt is not installed on
        // OH (no libconscrypt_jni.so), so the default 4-provider list fails
        // removeInvalid() and Providers.<clinit> throws AssertionError
        // permanently poisoning the Class.  We rewrite Security.props so that
        // only providers that actually load survive: BouncyCastle (pure-Java
        // in BCP) + CertPathProvider (pure-Java in core-oj.jar).  This must
        // run before preloadClasses (which triggers many <clinit> chains)
        // and is replicated to child via fork-COW of the Class state.
        overrideJcaProvidersForOH();

        // 2026-04-30 方向 3 真补救：populate ICUBinary.icuDataFiles at runtime.
        // ICUBinary.<clinit> reads ANDROID_I18N_ROOT/ANDROID_TZDATA_ROOT env
        // via AndroidDataFiles.generateIcuDataPath(); but ART boot image AOT
        // compilation pre-baked ICUBinary.<clinit> when these env weren't set
        // (dex2oat host build env), so icuDataFiles is frozen empty in boot image.
        // Runtime won't re-run <clinit> (image-class pre-init flag), so no ICU
        // .res lookup ever finds icudt72l.dat. We reflect-add the data files
        // at runtime so subsequent UResourceBundle / Typeface chains work.
        ensureICUDataLoaded();

        // Pre-init AssetManager.sSystem with a sentinel instance so
        // AssetManager.getSystem() / createSystemAssetsInZygoteLocked early-
        // return.  This avoids OverlayConfig.getZygoteInstance ->
        // PackagePartitions.<clinit> -> ICU ULocale chain, which on this
        // device hits IllegalAccessError at android.icu.impl.CacheValue
        // (apparent core-icu4j.jar dex inconsistency: NullValue inner
        // ctor reported inaccessible to its outer class).  Bypassing the
        // whole getSystem() machinery is more surgical than fixing ICU.
        preInitAssetManagerSystem();
        preInitTypefaceDefault();

        try {
            // 1. Preload classes (~7000 Android framework classes)
            Log.i(TAG, "Preloading classes...");
            preloadClasses();

            // 2. Preload system resources (Drawable, ColorStateList)
            Log.i(TAG, "Preloading resources...");
            preloadResources();

            // 3. Preload shared libraries
            Log.i(TAG, "Preloading shared libraries...");
            preloadSharedLibraries();

            // 4. Preload graphics driver
            Log.i(TAG, "Preloading graphics driver...");
            preloadGraphicsDriver();

            // 5. Preload JCA security providers — DISABLED (Phase 8 路线 1)
            //    Calling Security.getProviders() in parent triggers
            //    sun.security.jca.Providers.<clinit> which throws AssertionError
            //    "Unable to configure default providers" on OH (no java.security
            //    config file).  Java spec records the failure permanently on
            //    the Class object, so every later reference (incl. child after
            //    fork — Class state is COW'd) re-throws as NoClassDefFoundError.
            //    AA.22 hit this at NetworkSecurityConfigProvider.install ->
            //    Security.insertProviderAt and dead-locked MVP route 2.
            //    Skipping warmup keeps Providers virgin so child's first touch
            //    in handleBindApplication has a chance to either succeed (if
            //    OH ships JCA config later) or be caught at a single known
            //    site instead of the cached-failure mode.

            // 6. Preload adapter bridge (appspawn-x specific)
            Log.i(TAG, "Preloading adapter bridge...");
            preloadAdapterBridge();

        } catch (Throwable e) {
            // Catch Throwable (not just Exception) so Error subclasses like
            // AssertionError (from sun.security.jca.Providers.<clinit> when
            // OH has no JCA provider configuration) do not abort preload.
            // Individual step-level catches are preferable; this is the
            // last-resort safety net. See feedback_java_blocker_patch_skip.md
            // and doc/liboh_android_runtime_design.html §8 (Java 待补齐点).
            Log.e(TAG, "Preload failed (non-fatal)", e);
        }

        long elapsed = System.currentTimeMillis() - startTime;
        Log.i(TAG, "=== Preload complete in " + elapsed + "ms ===");

        // GC after preload to clean up temporary objects
        gcAndFinalize();
    }

    /**
     * Preload Android framework classes from /system/etc/preloaded-classes.
     * Uses Class.forName() to force class loading and initialization.
     * In a real build, this delegates to ZygoteInit.preloadClasses().
     */
    private static void preloadClasses() {
        // In production, read /system/etc/preloaded-classes and load each class.
        // For development, load essential framework classes directly.
        String[] essentialClasses = {
            "android.app.ActivityThread",
            "android.app.Application",
            "android.app.Activity",
            "android.app.Service",
            "android.content.ContentProvider",
            "android.content.BroadcastReceiver",
            "android.content.Intent",
            "android.content.ComponentName",
            "android.content.Context",
            "android.os.Bundle",
            "android.os.Handler",
            "android.os.Looper",
            "android.os.Message",
            "android.os.MessageQueue",
            "android.os.Binder",
            "android.os.IBinder",
            "android.os.Parcel",
            "android.os.Process",
            "android.view.View",
            "android.view.Window",
            "android.view.WindowManager",
            "android.widget.TextView",
            "android.widget.LinearLayout",
            "android.widget.FrameLayout",
            "android.graphics.Bitmap",
            "android.graphics.Canvas",
            "android.graphics.Paint",
            "android.graphics.drawable.Drawable",
            "android.util.Log",
            "android.net.Uri",
        };

        int loaded = 0;
        int failed = 0;
        for (String className : essentialClasses) {
            try {
                Class.forName(className, true, null);
                loaded++;
            } catch (ClassNotFoundException e) {
                // Expected for some classes not on boot classpath
                failed++;
            }
        }
        Log.i(TAG, "Preloaded " + loaded + " classes (" + failed + " not found)");

        // In full build, also call:
        // ZygoteInit.preloadClasses();
        // which reads the full preloaded-classes file (~7000 entries)
    }

    /**
     * Preload system resources (Drawable, ColorStateList).
     * In production, delegates to ZygoteInit.preloadResources().
     */
    private static void preloadResources() {
        // In production:
        // TypedArray ar = Resources.getSystem().obtainTypedArray(
        //     com.android.internal.R.array.preloaded_drawables);
        // ... iterate and load each drawable
        //
        // For development, this is a no-op placeholder.
        Log.d(TAG, "Resource preload: placeholder (full preload in production build)");
    }

    /**
     * Preload essential shared native libraries.
     *
     * 2026-04-22: The original AOSP-ported list (android / jnigraphics /
     * compiler_rt) is fully skipped. None of these .so files exist in the
     * adapter project — never cross-compiled to out/aosp_lib/, never on
     * device. Under shell-domain launch, System.loadLibrary on a missing
     * lib returns UnsatisfiedLinkError gracefully. Under init-service
     * launch (appspawn domain + AT_SECURE + systemscence namespace), musl
     * ld-musl-arm.so.1 dlopen_impl has a wild-pointer SIGSEGV at pc
     * 00068398 when resolving a not-found library path — confirmed by
     * cppcrash-3337 and cppcrash-3395 stacks (both same offset, both
     * SEGV_MAPERR in the libdexfile.so map region). See
     * doc/appspawn_x_design.html attempts and doc/build_patch_log.html
     * for the full diagnostic chain.
     *
     * These libs are AOSP NDK helpers (libandroid = AChoreographer /
     * ANativeWindow NDK, libjnigraphics = Bitmap native, libcompiler_rt =
     * LLVM runtime — folded into libc++ / ldso on OH musl). Hello World
     * TextView path does not use any of them, so skipping is a zero-cost
     * workaround. If future APKs need NDK window/bitmap, wire the real
     * libs in via liboh_android_runtime's self-implemented JNI — per
     * project_liboh_android_runtime.md (no reliance on AOSP libandroid).
     */
    private static void preloadSharedLibraries() {
        // Intentionally no-op. See block comment above.
        Log.d(TAG, "preloadSharedLibraries skipped (musl dlopen SEGV workaround)");
    }

    /**
     * Preload graphics driver (OpenGL/Vulkan).
     * In production, calls native method to load GPU driver.
     */
    private static void preloadGraphicsDriver() {
        // In production:
        // ZygoteInit.nativePreloadGraphicsDriver();
        // or maybePreloadGraphicsDriver()
        Log.d(TAG, "Graphics driver preload: placeholder");
    }

    /**
     * Warm up Java Cryptography Architecture providers.
     */
    /**
     * Rewrite java.security.Security.props so the static provider list
     * matches what OH actually has on the filesystem.  Default Android list:
     *   1. com.android.org.conscrypt.OpenSSLProvider   (needs libconscrypt_jni.so — MISSING)
     *   2. sun.security.provider.CertPathProvider      (pure Java, in core-oj.jar — OK)
     *   3. com.android.org.bouncycastle.jce.provider.BouncyCastleProvider (pure Java, in BCP — OK)
     *   4. com.android.org.conscrypt.JSSEProvider       (needs Conscrypt JNI — MISSING)
     *
     * Providers.<clinit> calls providerList.removeInvalid() and asserts that
     * the size doesn't shrink; conscrypt #1 + #4 fail to load so the assert
     * throws AssertionError, permanently poisoning the Class.  We pre-prune
     * the conscrypt entries before Providers ever runs so the assertion
     * sees the same size in/out.
     *
     * Implementation note: Security.props is private static final Properties.
     * The field is final but Properties is mutable, so we keep the same
     * object identity and only rewrite entries.
     *
     * 2026-04-29 (post-B.29, feedback.txt P3 evaluation): true 真补 OH options:
     *   (A) port Conscrypt to OH (cross-build libconscrypt_jni.so + JCA layer)
     *       — multi-week scope.
     *   (B) sediment as aosp_patches/.../security.properties.patch removing
     *       Conscrypt entries; require rebuild of core-oj.jar + boot image
     *       (feedback_boot_image_full_rebuild_risk.md flags this as risky).
     *   (C) retain current reflection (cheapest, contained at preload time,
     *       no boot image rebuild).
     * Choice: (C) tracked as P3 technical debt — see
     * doc/shortcuts_inventory.html (security category) +
     * doc/technical_decision_overview.html v1.6.
     */
    private static void overrideJcaProvidersForOH() {
        try {
            Class<?> sec = Class.forName("java.security.Security");  // triggers Security.<clinit>
            java.lang.reflect.Field propsField = sec.getDeclaredField("props");
            propsField.setAccessible(true);
            java.util.Properties props = (java.util.Properties) propsField.get(null);

            // Drop everything that begins with "security.provider." so we get
            // a clean slate; default list and any Conscrypt-bearing entries
            // both go.
            java.util.Iterator<Object> it = props.keySet().iterator();
            int dropped = 0;
            while (it.hasNext()) {
                Object k = it.next();
                if (k instanceof String && ((String) k).startsWith("security.provider.")) {
                    it.remove();
                    dropped++;
                }
            }
            // Install OH-viable providers only.  Order matters for default
            // selection (#1 = preferred); BouncyCastle is the most general so
            // it goes first.
            props.setProperty("security.provider.1",
                    "com.android.org.bouncycastle.jce.provider.BouncyCastleProvider");
            props.setProperty("security.provider.2",
                    "sun.security.provider.CertPathProvider");
            Log.i(TAG, "JCA providers overridden for OH (dropped " + dropped
                    + " original, installed BC + CertPath)");

            // Trigger Providers.<clinit> via Security.getProviders so BC is
            // instantiated, then patch BC to expose MessageDigest services.
            // Android's repackaged BC in this AOSP tree explicitly comments
            // out MessageDigest.SHA-1 / MD5 / SHA-256 / etc registrations
            // (search the .java files for "Android-removed: Unsupported
            // algorithm") because the Conscrypt provider is supposed to
            // own those algorithms.  OH has no Conscrypt → BC's SHA1$Digest
            // class is in the BCP dex but unreachable via JCA.  We register
            // the missing MessageDigest entries manually.
            java.security.Provider[] provs = java.security.Security.getProviders();
            StringBuilder sb = new StringBuilder("JCA active providers:");
            for (java.security.Provider p : provs) {
                sb.append(" [").append(p.getName()).append("=").append(p.getVersion()).append("]");
            }
            Log.i(TAG, sb.toString());

            java.security.Provider bc = java.security.Security.getProvider("BC");
            if (bc != null) {
                String DIGEST_PKG = "com.android.org.bouncycastle.jcajce.provider.digest.";
                // (algorithm name, BC class basename)
                String[][] digestMap = {
                    {"MD5",     "MD5"},
                    {"SHA-1",   "SHA1"},
                    {"SHA-224", "SHA224"},
                    {"SHA-256", "SHA256"},
                    {"SHA-384", "SHA384"},
                    {"SHA-512", "SHA512"},
                };
                for (String[] e : digestMap) {
                    bc.put("MessageDigest." + e[0], DIGEST_PKG + e[1] + "$Digest");
                }
                bc.put("Alg.Alias.MessageDigest.SHA1",   "SHA-1");
                bc.put("Alg.Alias.MessageDigest.SHA",    "SHA-1");
                bc.put("Alg.Alias.MessageDigest.SHA224", "SHA-224");
                bc.put("Alg.Alias.MessageDigest.SHA256", "SHA-256");
                bc.put("Alg.Alias.MessageDigest.SHA384", "SHA-384");
                bc.put("Alg.Alias.MessageDigest.SHA512", "SHA-512");
                Log.i(TAG, "BC: registered MessageDigest MD5/SHA-1/-224/-256/-384/-512");

                // B.20.r9: register CertificateFactory.X.509 — needed by
                // NetworkSecurityConfigProvider.handleNewApplication during
                // makeApplicationInner (handleBindApplication line 6976).
                // BC has the X.509 impl class but the AOSP-repackaged BC
                // explicitly comments out the registration ("Android-removed:
                // Unsupported algorithm") because Conscrypt is supposed to
                // own it. OH has no Conscrypt → register here.
                String X509_CLASS = "com.android.org.bouncycastle.jcajce.provider.asymmetric.x509.CertificateFactory";
                bc.put("CertificateFactory.X.509", X509_CLASS);
                bc.put("Alg.Alias.CertificateFactory.X509", "X.509");
                Log.i(TAG, "BC: registered CertificateFactory.X.509 = " + X509_CLASS);
                try {
                    java.security.cert.CertificateFactory cf = java.security.cert.CertificateFactory.getInstance("X.509");
                    Log.i(TAG, "X.509 CertificateFactory self-test OK provider=" + cf.getProvider().getName());
                } catch (Throwable cfT) {
                    Log.e(TAG, "X.509 self-test FAILED — NetworkSecurityConfig path will fail later", cfT);
                }
            } else {
                Log.e(TAG, "BC provider not found — SHA-1 patch skipped");
            }

            // Self-test
            try {
                java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-1");
                Log.i(TAG, "SHA-1 MessageDigest OK provider=" + md.getProvider().getName());
            } catch (Throwable mdT) {
                Log.e(TAG, "SHA-1 self-test FAILED — PackagePartitions will fail later", mdT);
            }
        } catch (Throwable t) {
            // Don't propagate — preload pipeline catches Throwable already
            // and continues, but a broken JCA override means handleBindApplication
            // may rehit AssertionError.  Log loudly.
            Log.e(TAG, "overrideJcaProvidersForOH FAILED", t);
        }
    }

    /**
     * Reflectively construct AssetManager via private sentinel ctor
     * (AssetManager(boolean)) and stuff it into the static
     * sSystem / sSystemApkAssets / sSystemApkAssetsSet fields.  The next
     * call to AssetManager.getSystem() then early-returns at the
     * "if (sSystem != null) return" check, skipping
     * createSystemAssetsInZygoteLocked entirely.  This is what bypasses
     * the OverlayConfig -> PackagePartitions -> ICU ULocale chain that
     * otherwise crashes the whole Resources init path on OH.
     */
    /**
     * B.20: pre-seed Typeface.sDefaultTypeface so handleBindApplication line
     * 6803's setSystemFontMap path doesn't NPE.  AOSP framework's
     * setSystemFontMap(Map) line 1410 calls create(sDefaultTypeface, 0); on
     * Android sDefaultTypeface is set up via DEFAULT_FAMILY in system fonts
     * (read from /system/etc/fonts.xml).  OH has no Android font configs so
     * the system font map ends up without DEFAULT_FAMILY → setDefault never
     * runs → sDefaultTypeface = null → create(null) → getDefault() returns
     * null → family.mStyle NPE at line 928.
     *
     * 2026-04-29 (post-B.29, feedback.txt P2 evaluation): true 真补 OH
     * requires populating the Typeface system font map from OH font configs +
     * porting Android Typeface_Builder/Family JNI machinery (multi-day work).
     * Tracked as P2 technical debt; current reflection pre-seed retained as
     * stop-gap because real fix scope is unbounded relative to HelloWorld
     * critical path.  See doc/shortcuts_inventory.html#chJ entry J2 +
     * doc/technical_decision_overview.html v1.6 for sediment.
     */
    /**
     * 2026-04-30 方向 3 真补救：reflect-add ICU data files at runtime.
     *
     * ART boot image AOT pre-runs ICUBinary.<clinit> at dex2oat time. At that
     * moment ANDROID_I18N_ROOT / ANDROID_TZDATA_ROOT were unset (host build
     * env), so AndroidDataFiles.generateIcuDataPath() returned a path that
     * found no .dat files, leaving the static icuDataFiles list empty in
     * boot image. Runtime never re-runs <clinit> (image-class pre-init flag).
     *
     * We reflect-call ICUBinary.addDataFilesFromPath at runtime to populate
     * icuDataFiles with the real device path /system/android/etc/icu/.
     * This must run before any UResourceBundle/Typeface lookup.
     */
    private static void ensureICUDataLoaded() {
        try {
            Class<?> icuBinaryCls = Class.forName("android.icu.impl.ICUBinary");
            java.lang.reflect.Field icuDataFilesField =
                    icuBinaryCls.getDeclaredField("icuDataFiles");
            icuDataFilesField.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.List<Object> list =
                    (java.util.List<Object>) icuDataFilesField.get(null);

            if (!list.isEmpty()) {
                Log.i(TAG, "ICUBinary.icuDataFiles already has " + list.size()
                        + " entries — skipping reflective load");
                return;
            }

            // 1) Try the standard addDataFilesFromPath path first (in case
            //    SELinux + listFiles work — depends on OH version / sepolicy).
            String icuPath = "/system/android/etc/icu";
            try {
                java.lang.reflect.Method addMethod =
                        icuBinaryCls.getDeclaredMethod("addDataFilesFromPath",
                                String.class, java.util.List.class);
                addMethod.setAccessible(true);
                addMethod.invoke(null, icuPath, list);
                Log.i(TAG, "addDataFilesFromPath returned " + list.size() + " entries");
            } catch (Throwable t) {
                Log.w(TAG, "addDataFilesFromPath failed: " + t);
            }

            if (!list.isEmpty()) {
                for (Object df : list) Log.d(TAG, "  ICU data file: " + df);
                return;
            }

            // 2) addDataFilesFromPath returned empty. This happens on OH when
            //    the standard ICU init path (boot image AOT-baked clinit
            //    against host build env) leaves icuDataFiles empty AND
            //    listFiles() in this domain doesn't enumerate the contents
            //    of /system/android/etc/icu (root cause TBD — possibly
            //    sepolicy difference between dir read vs file read).
            //
            //    Direct path: mmap the known .dat file and construct a
            //    PackageDataFile via reflection. This works for any
            //    Android app since the .dat file is the canonical ICU data
            //    container and DatPackageReader can serve all entries from it.
            java.io.File datFile = new java.io.File(icuPath, "icudt72l.dat");
            if (!datFile.canRead()) {
                Log.e(TAG, "ICU dat file unreadable: " + datFile);
                return;
            }
            java.nio.ByteBuffer bytes;
            try (java.io.FileInputStream fis = new java.io.FileInputStream(datFile);
                 java.nio.channels.FileChannel ch = fis.getChannel()) {
                bytes = ch.map(java.nio.channels.FileChannel.MapMode.READ_ONLY,
                               0, ch.size());
            }
            if (bytes == null) {
                Log.e(TAG, "Failed to mmap " + datFile);
                return;
            }

            // Validate via DatPackageReader.validate (package-private static).
            Class<?> dprCls = Class.forName("android.icu.impl.ICUBinary$DatPackageReader");
            java.lang.reflect.Method validate = dprCls.getDeclaredMethod(
                    "validate", java.nio.ByteBuffer.class);
            validate.setAccessible(true);
            Boolean ok = (Boolean) validate.invoke(null, bytes);
            if (ok == null || !ok) {
                Log.e(TAG, "DatPackageReader.validate rejected the .dat file");
                return;
            }

            // Construct PackageDataFile(item, bytes) reflectively.
            Class<?> pdfCls = Class.forName("android.icu.impl.ICUBinary$PackageDataFile");
            java.lang.reflect.Constructor<?> ctor = pdfCls.getDeclaredConstructor(
                    String.class, java.nio.ByteBuffer.class);
            ctor.setAccessible(true);
            Object packageDataFile = ctor.newInstance("icudt72l.dat", bytes);
            list.add(packageDataFile);

            Log.i(TAG, "ICUBinary.icuDataFiles populated via direct mmap: "
                    + list.size() + " entries from " + datFile);
        } catch (Throwable t) {
            Log.e(TAG, "ensureICUDataLoaded FAILED — Typeface/ULocale chain may NPE", t);
        }
    }

    private static void preInitTypefaceDefault() {
        // 2026-05-02 G2.14r: SKIP — fundamental conflict with G2.14q Path A.
        //
        // Original intent (pre-G2.14q): construct a fake Typeface with
        // native_instance=1L and pre-set sDefaultTypeface, to bypass AOSP
        // setSystemFontMap which would NPE on OH (no Android font configs).
        // That worked when libhwui's Typeface natives were the typeface_minimal_stub
        // version (returned fake handles, didn't deref).
        //
        // Post G2.14q+G2.14r: libhwui's real AOSP Typeface JNI is bound. The
        // Typeface(long ni) constructor calls nativeGetStyle(ni)/nativeGetWeight(ni)
        // internally — real impl does `toTypeface(handle)->fAPIStyle` which
        // dereferences (Typeface*)1 → SIGSEGV. So this fn cannot construct a
        // fake Typeface anymore.
        //
        // Workaround: skip this pre-init entirely. With fonts.xml symlink + real
        // libhwui Typeface, the standard Typeface.<clinit> path (setSystemFontMap)
        // should now work. If it doesn't, that's the next blocker to fix in the
        // AOSP graphics chain proper, not via reflection bypass here.
        Log.i(TAG, "Pre-init Typeface SKIPPED (G2.14r) — defer to standard "
                + "Typeface.<clinit> setSystemFontMap path (fonts.xml symlink + real libhwui)");
    }

    /**
     * 2026-04-29 (post-B.29, feedback.txt P2 evaluation): true 真补 OH would
     * require porting OverlayManagerService (OH has no equivalent) +
     * implementing OverlayConfig + PackagePartitions + ICU ULocale chain
     * compatibly with Android — multi-week work.  Current reflection
     * sentinel + framework-res ApkAssets pre-seed retained as the only
     * feasible stop-gap given OH's lack of Overlay infrastructure.
     *
     * Note: this preload-time path coexists with B.27's
     * initSystemAssetManager() which runs at child initChild time and uses
     * public API (AssetManager.getSystem() + setApkAssets) — that one is
     *真适配 of the post-fork sSystem state.  preInitAssetManagerSystem is
     * the parent-process bypass to avoid clinit-chain crashes during
     * preloadClasses; both are necessary while OH lacks Overlay services.
     *
     * Tracked as P2 technical debt — see doc/shortcuts_inventory.html chJ
     * entry J3 + doc/technical_decision_overview.html v1.6 sediment.
     */
    private static void preInitAssetManagerSystem() {
        try {
            Class<?> amClass = Class.forName("android.content.res.AssetManager");
            // private AssetManager(boolean sentinel)
            java.lang.reflect.Constructor<?> ctor = amClass.getDeclaredConstructor(boolean.class);
            ctor.setAccessible(true);
            Object sentinel = ctor.newInstance(true);

            java.lang.reflect.Field sSystem = amClass.getDeclaredField("sSystem");
            sSystem.setAccessible(true);
            sSystem.set(null, sentinel);

            // B.20.r7: Real fix — load AOSP framework-res.apk as a real ApkAssets
            // so AssetManager.Builder.build() at line 167 (systemApkAssets.length)
            // and System.arraycopy(systemApkAssets, ...) get a populated array,
            // not null. Falls back to empty if loadFromPath fails (still avoids
            // NPE because mApkAssets becomes ApkAssets[0] not null).
            Class<?> apkAssetsClass = Class.forName("android.content.res.ApkAssets");
            Object[] frameworkAssets;
            try {
                java.lang.reflect.Method loadFromPath = apkAssetsClass.getDeclaredMethod(
                        "loadFromPath", String.class, int.class);
                loadFromPath.setAccessible(true);
                final String FRAMEWORK_APK_PATH = "/system/android/framework/framework-res.apk";
                Object fwApk = loadFromPath.invoke(null, FRAMEWORK_APK_PATH, /* PROPERTY_SYSTEM */ 0x01);
                frameworkAssets = (Object[]) java.lang.reflect.Array.newInstance(apkAssetsClass, 1);
                frameworkAssets[0] = fwApk;
                Log.i(TAG, "Loaded real framework-res ApkAssets from " + FRAMEWORK_APK_PATH);
            } catch (Throwable e) {
                Log.w(TAG, "loadFromPath(framework-res.apk) failed; falling back to empty ApkAssets[]: " + e);
                frameworkAssets = (Object[]) java.lang.reflect.Array.newInstance(apkAssetsClass, 0);
            }

            // sSystemApkAssets : static ApkAssets[]
            java.lang.reflect.Field sSystemApkAssets = amClass.getDeclaredField("sSystemApkAssets");
            sSystemApkAssets.setAccessible(true);
            sSystemApkAssets.set(null, frameworkAssets);

            // sSystemApkAssetsSet : static ArraySet<ApkAssets>
            Class<?> arraySetClass = Class.forName("android.util.ArraySet");
            Object set = arraySetClass.getConstructor().newInstance();
            if (frameworkAssets.length > 0) {
                java.lang.reflect.Method addMethod = arraySetClass.getMethod("add", Object.class);
                addMethod.invoke(set, frameworkAssets[0]);
            }
            java.lang.reflect.Field sSystemApkAssetsSet = amClass.getDeclaredField("sSystemApkAssetsSet");
            sSystemApkAssetsSet.setAccessible(true);
            sSystemApkAssetsSet.set(null, set);

            // CRITICAL: sentinel.mApkAssets must be set so getSystem().getApkAssets()
            // returns the populated array (mOpen defaults to true → returns mApkAssets).
            // Without this, AssetManager.Builder.build line 167 NPEs on null.length.
            java.lang.reflect.Field mApkAssets = amClass.getDeclaredField("mApkAssets");
            mApkAssets.setAccessible(true);
            mApkAssets.set(sentinel, frameworkAssets);

            Log.i(TAG, "Pre-init AssetManager.sSystem (sentinel=" + sentinel
                    + ", mApkAssets.length=" + frameworkAssets.length
                    + ") — real framework-res loaded, bypasses OverlayConfig/PackagePartitions/ICU chain");
        } catch (Throwable t) {
            Log.e(TAG, "preInitAssetManagerSystem FAILED", t);
        }
    }

    private static void warmUpJcaProviders() {
        // OH has no JCA provider configuration files (java.security /
        // java.policy not shipped with our OpenJDK subset). Calling
        // Security.getProviders() triggers sun.security.jca.Providers.<clinit>
        // which throws AssertionError "Unable to configure default providers".
        // Catch Throwable so this does not abort the preload pipeline.
        // Hello World TextView path does not use JCA; this is a pending gap
        // for the full framework port. See doc/liboh_android_runtime_design.html
        // §8 (Java 待补齐点) entry "JCA providers".
        try {
            java.security.Security.getProviders();
            Log.d(TAG, "JCA providers warmed up");
        } catch (Throwable e) {
            Log.w(TAG, "JCA warmup skipped (OH has no JCA config, pending gap): " + e);
        }
    }

    /**
     * Preload adapter bridge (appspawn-x specific).
     * Caches adapter Java classes; the bridge .so is loaded transitively
     * through OHEnvironment.&lt;clinit&gt; when Class.forName triggers it.
     *
     * NOTE (2026-04-23): we intentionally do NOT call
     * {@code System.loadLibrary("oh_adapter_bridge")} here.
     * AppSpawnXInit lives in non-BCP oh-adapter-runtime.jar (PathClassLoader),
     * while OHEnvironment lives in BCP oh-adapter-framework.jar (bootstrap
     * classloader). If both invoked loadLibrary, ART would refuse the
     * second call with "already opened by ClassLoader X; can't open in
     * ClassLoader Y". Letting OHEnvironment self-load keeps bridge.so
     * bound to exactly one classloader (bootstrap/null), which also
     * matches child-process resolution after fork.
     */
    private static void preloadAdapterBridge() {
        // Cache adapter Java classes so they're COW-shared after fork.
        // OHEnvironment is first in the list so its <clinit> runs the
        // loadLibrary once, before any sibling class references it.
        String[] adapterClasses = {
            "adapter.core.OHEnvironment",
            "adapter.activity.ActivityManagerAdapter",
            "adapter.activity.ActivityTaskManagerAdapter",
            "adapter.window.WindowManagerAdapter",
            "adapter.window.WindowSessionAdapter",
            "adapter.activity.ServiceConnectionRegistry",
            "adapter.activity.AppSchedulerBridge",
            "adapter.activity.AbilitySchedulerBridge",
            "adapter.activity.AbilityConnectionBridge",
            "adapter.activity.IntentWantConverter",
            "adapter.activity.LifecycleAdapter",
        };

        int cached = 0;
        for (String cls : adapterClasses) {
            try {
                Class.forName(cls);
                cached++;
            } catch (ClassNotFoundException e) {
                Log.d(TAG, "Adapter class not found (OK in dev): " + cls);
            } catch (Throwable t) {
                // Most commonly NoClassDefFoundError wrapping ExceptionIn-
                // InitializerError when a sibling class's <clinit> can't
                // complete (e.g. missing OH service, missing helper class).
                // Swallowing here so the remaining adapter classes still
                // warm up; the child process will surface the concrete
                // missing piece when it actually tries to use this class.
                Log.w(TAG, "Skip adapter class (init failure): " + cls + " — " + t);
            }
        }
        Log.i(TAG, "Cached " + cached + " adapter classes");
    }

    /**
     * Force GC and finalization to clean up preload garbage
     * before entering the event loop (objects created during preload
     * that won't be needed are freed now, reducing COW page faults).
     */
    private static void gcAndFinalize() {
        // 2026-04-17: GC / finalization on our OH/musl ART hangs (both
        // runFinalizationSync and System.gc observed to block post-preload).
        // Hello World does not benefit from the pre-event-loop COW-cleanup
        // optimization this method was designed for. Skip entirely; log once
        // for traceability. Pending gap — see
        // doc/liboh_android_runtime_design.html §8.2 "Finalizer daemon".
        Log.d(TAG, "gcAndFinalize skipped (hangs on OH/musl ART, pending gap)");
    }

    // ============================================================
    // Child Process: Initialization (called after fork + specialize)
    // ============================================================

    /**
     * Initialize the child process after fork() and OH security specialization.
     * Called from native child_main.cpp via JNI.
     *
     * @param procName       Process name (e.g. "com.example.app")
     * @param targetClass    Main class to invoke (e.g. "android.app.ActivityThread")
     * @param targetSdkVersion Target SDK version for the app
     */
    public static void initChild(String procName, String targetClass, int targetSdkVersion) {
        // B.35.B (2026-04-29 EOD+2): AOSP-style redirectLogStreams equivalent.
        // AOSP RuntimeInit.zygoteInit calls redirectLogStreams() which replaces
        // System.out/err with AndroidPrintStream → Log.println → liblog → hilog.
        // We can't use AndroidPrintStream (it's @hide and depends on liblog→hilog
        // bridge that's broken across fork). Instead, route to nativeHiLog
        // (B.32 direct HiLogPrint via JNI) which we register via B.35.A
        // explicit dlopen handle.  This replaces System.err so any subsequent
        // ActivityThread.<clinit> / ActivityManagerAdapter.attachApplication
        // System.err.println ends up in hilog where we can see them.
        try {
            redirectLogStreamsToHiLog();
        } catch (Throwable t) {
            // Cannot use Log.e here (broken bridge); use direct nativeHiLog.
            appLog("redirectLogStreams failed: " + t);
        }

        // 2026-04-29 B.32: prior System.err checkpoints went to /dev/null under
        // init service.  Now route through OHEnvironment.nativeHiLog (direct
        // HiLogPrint, bypasses broken-after-fork liblog.so → hilog bridge).
        // First call also serves as the "Java entry visible" smoke test.
        appLog("J_initChild_entry proc=" + procName);
        Log.i(TAG, "initChild: proc=" + procName + " target=" + targetClass
                + " sdk=" + targetSdkVersion);

        try {
            // 1. Set process name
            appLog("J_before_setArgV0");
            Process.setArgV0(procName);
            appLog("J_after_setArgV0");

            // 2. Set target SDK version
            if (targetSdkVersion > 0) {
                VMRuntime.getRuntime().setTargetSdkVersion(targetSdkVersion);
            }
            appLog("J_after_setTargetSdkVersion");

            // 3. Common runtime initialization
            // In production: RuntimeInit.commonInit()
            // Sets default uncaught exception handler, timezone, etc.
            initCommonRuntime();
            appLog("J_after_initCommonRuntime");

            // 4. Initialize adapter layer (connects to OH system services)
            initAdapterLayer();
            appLog("J_after_initAdapterLayer");

            // 4b. Install IActivityManager / IPackageManager / IWindowManager
            //     Proxy-based stubs so ActivityThread.main + handleBindApplication +
            //     Activity.setContentView paths don't NPE on their respective
            //     ServiceManager.getService(...) calls. Each injection point is
            //     a static field on the owning class; we reflectively set them
            //     before main() runs so the null-checks pass on first use.
            //     Real adapter layer (oh-adapter-framework.jar's *Adapter classes)
            //     has native ctor dependencies (nativeGetOHAbilityManagerService
            //     in liboh_adapter_bridge.so) not guaranteed ready yet. Proxy
            //     stubs return safe defaults for all methods; hot ones (attachApp,
            //     addWindow, etc.) log when called so we know if Activity lifecycle
            //     actually exercises them. Swap to real adapters once bridge ready.
            appLog("J_before_installActivityManagerStub");
            installActivityManagerStub();
            appLog("J_after_installActivityManagerStub");
            installPackageManagerStub();
            appLog("J_after_installPackageManagerStub");
            installWindowManagerStub();
            appLog("J_after_installWindowManagerStub");
            // G2.14v (2026-05-07): InputManagerAdapter (extends IInputManager.Stub)
            // — reflectively injected into InputManagerGlobal.sInstance/sService so
            // PhoneWindow.preparePanel → KeyCharacterMap.load no longer NPEs.
            // See doc/Input_Adapter_design.html.
            installInputManagerAdapter();
            appLog("J_after_installInputManagerAdapter");
            // B.14 (2026-04-28): OHServiceManager 真适配子类（extends IServiceManager.Stub
            // — Stub 这里是 AIDL server-side 抽象基类，与 ActivityManagerAdapter
            // extends IActivityManager.Stub 同 pattern；不是 stub 占位）。替代了
            // 之前的 Proxy.newProxyInstance hack，避开 init service ART 类生成 spin。
            installServiceManagerAdapter();
            appLog("J_after_installServiceManagerAdapter");
            installActivityClientControllerStub();
            appLog("J_after_installActivityClientControllerStub");
            preInitSettingsCache();
            appLog("J_after_preInitSettingsCache");
            // B.26: appspawn-x system AssetManager init via public API path
            // (AssetManager.getSystem() + ApkAssets.loadFromPath() +
            // AssetManager.setApkAssets()).  We do NOT use the zygote-specific
            // createSystemAssetsInZygoteLocked because appspawn-x is not
            // zygote and OH lacks OverlayManagerService.
            initSystemAssetManager();
            appLog("J_after_installAdapters");

            // B.28 (2026-04-29): scheduleMainThreadProbe / runMainProbe / BindApplicationHelper
            // 全部删除，永不再用。
            //
            // 真实路径 = OH IPC 触发：
            //   Launcher / aa start
            //     → IAbilityManagerService::StartAbility
            //     → AppMgr::AttachApplication callback (oh_app_mgr_client.cpp, B.8 verified)
            //     → AppSchedulerAdapter::ScheduleLaunchAbility (C++)
            //     → AppSchedulerBridge.nativeOnScheduleLaunchAbility (JNI)
            //     → IApplicationThread.scheduleTransaction
            //     → ActivityThread.handleLaunchActivity
            //     → MainActivity.onCreate
            //
            // ActivityThread.main() 进 Looper.loop() 后会 idle 等待 OH 调入；
            // 不再由 Java 端 BindApplicationHelper 反射 fake 这条链。

            // 5. Find and invoke target class main()
            // This is equivalent to RuntimeInit.findStaticMain()
            appLog("J_before_invokeStaticMain target=" + targetClass);
            Log.i(TAG, "Launching " + targetClass + ".main()");
            invokeStaticMain(targetClass);
            appLog("J_after_invokeStaticMain (returned unexpectedly!)");

        } catch (Throwable t) {
            // Catch Throwable (not just Exception) so an Error from any of
            // the early init steps (Process / VMRuntime / OHEnvironment) is
            // visible in the log instead of silently bringing down the child
            // with no diagnostic. This still calls System.exit because
            // initChild failure means the child is unviable for an Android
            // app — but at least we know what failed.
            Log.e(TAG, "initChild failed", t);
            System.exit(1);
        }
    }

    /**
     * Minimal InvocationHandler that logs "hot" method hits and returns type-
     * appropriate defaults for everything else. <tt>label</tt> is used as a
     * log tag prefix and <tt>hotMethods</tt> is the set of method names that
     * get an explicit log line (others proceed silently).
     *
     * Reused by installActivityManagerStub / PackageManager / WindowManager
     * (Phase 7 #11 + Phase 8 #1). Returns null for asBinder() so the proxy
     * itself is returned as IBinder (since Proxy implements IBinder).
     */
    private static InvocationHandler makeStubHandler(final String label,
                                                     final java.util.Set<String> hotMethods) {
        return new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                String name = method.getName();
                if (hotMethods != null && hotMethods.contains(name)) {
                    System.err.println("[" + label + "] " + name + " (no-op)");
                    System.err.flush();
                }
                if ("asBinder".equals(name))   return proxy;
                if ("toString".equals(name))   return label;
                if ("hashCode".equals(name))   return System.identityHashCode(proxy);
                if ("equals".equals(name))     return args != null && args.length == 1 && args[0] == proxy;
                Class<?> rt = method.getReturnType();
                if (rt == boolean.class) return Boolean.FALSE;
                if (rt == byte.class)    return (byte) 0;
                if (rt == short.class)   return (short) 0;
                if (rt == int.class)     return 0;
                if (rt == long.class)    return 0L;
                if (rt == float.class)   return 0f;
                if (rt == double.class)  return 0d;
                if (rt == char.class)    return (char) 0;
                return null;   // void or reference
            }
        };
    }

    /**
     * Build a Proxy stub implementing the named IXxx interface (+ IBinder),
     * with <tt>hotMethods</tt> logged on call.
     */
    private static Object makeProxyStub(String label, String ifaceFqn,
                                        java.util.Set<String> hotMethods) throws Exception {
        System.err.println("[" + label + "_MPS] m1_before_loadIface " + ifaceFqn); System.err.flush();
        Class<?> iface = Class.forName(ifaceFqn);
        System.err.println("[" + label + "_MPS] m2_loaded_iface=" + iface); System.err.flush();
        Class<?> iBinder = Class.forName("android.os.IBinder");
        System.err.println("[" + label + "_MPS] m3_loaded_iBinder"); System.err.flush();
        Object handler = makeStubHandler(label, hotMethods);
        System.err.println("[" + label + "_MPS] m4_made_handler"); System.err.flush();
        Object proxy = java.lang.reflect.Proxy.newProxyInstance(
            iface.getClassLoader(),
            new Class<?>[]{ iface, iBinder },
            (java.lang.reflect.InvocationHandler) handler);
        System.err.println("[" + label + "_MPS] m5_proxy_made=" + proxy); System.err.flush();
        return proxy;
    }

    /**
     * Install a minimal IActivityManager singleton via reflection so that
     * ActivityManager.getService() returns a safe stub instead of tripping
     * ServiceManager.getService("activity") → sServiceManager-null NPE.
     *
     * Singleton pattern: ActivityManager.IActivityManagerSingleton is a
     * Singleton&lt;IActivityManager&gt; whose create() normally calls ServiceManager.
     * Pre-setting mInstance (from Singleton base) to our Proxy stub prevents
     * create() from ever running.
     */
    private static void installActivityManagerStub() {
        try {
            // Try to load the REAL adapter first.  Adapter ctor calls JNI
            // (nativeGetOHAbilityManagerService) which connects to OH
            // IAbilityManager via samgr; on failure we fall back to a Proxy
            // stub so the rest of init can proceed.
            Object iamImpl = null;
            try {
                // B.36 (2026-04-29 EOD+2): finer-grained error reporting to
                // distinguish ClassNotFound / class-init failure / method-not-found.
                Class<?> realAdapter;
                try {
                    realAdapter = Class.forName("adapter.activity.ActivityManagerAdapter");
                    System.err.println("[AdapterIAM-B36] CK1 Class.forName OK: " + realAdapter);
                } catch (Throwable t1) {
                    System.err.println("[AdapterIAM-B36] CK1 Class.forName FAILED: " + t1);
                    t1.printStackTrace(System.err);
                    throw t1;
                }
                // B.36 (2026-04-29 EOD+2): device's oh-adapter-framework.jar
                // (boot-image-matching version 97ab80) does NOT have
                // getInstance() — only public no-arg ctor.  source has both
                // (line 138-147 = getInstance, line 149-153 = ctor) but
                // device jar predates the getInstance() addition.  Fall
                // back to no-arg ctor; this gets a fresh instance, which is
                // what getInstance() does on first call anyway.  Singleton
                // semantics aren't critical for the IActivityManager
                // injection point — we only need ONE instance held by
                // IActivityManagerSingleton.mInstance.
                try {
                    iamImpl = realAdapter.getDeclaredConstructor().newInstance();
                    System.err.println("[AdapterIAM-B36] CK2 newInstance() OK: " + iamImpl);
                } catch (Throwable t2) {
                    System.err.println("[AdapterIAM-B36] CK2 newInstance() FAILED: " + t2);
                    Throwable cause = t2.getCause();
                    while (cause != null) {
                        System.err.println("[AdapterIAM-B36]   caused by: " + cause);
                        cause = cause.getCause();
                    }
                    t2.printStackTrace(System.err);
                    throw t2;
                }
                System.err.println("[AdapterIAM] real ActivityManagerAdapter (ctor): " + iamImpl);
            } catch (Throwable rt) {
                System.err.println("[AdapterIAM] real adapter ctor FAILED, falling back to Proxy stub: " + rt);
                java.util.Set<String> hot = new java.util.HashSet<>();
                hot.add("attachApplication");
                iamImpl = makeProxyStub("AdapterIAM-stub",
                        "android.app.IActivityManager", hot);
            }

            Class<?> amClass = Class.forName("android.app.ActivityManager");
            Class<?> singletonBase = Class.forName("android.util.Singleton");
            Field singletonField = amClass.getDeclaredField("IActivityManagerSingleton");
            singletonField.setAccessible(true);
            Object singleton = singletonField.get(null);

            Field instField = singletonBase.getDeclaredField("mInstance");
            instField.setAccessible(true);
            instField.set(singleton, iamImpl);

            System.err.println("[AdapterIAM] IActivityManagerSingleton.mInstance installed = " + iamImpl);
            System.err.flush();
        } catch (Throwable t) {
            System.err.println("[AdapterIAM] install FAILED: " + t);
            t.printStackTrace(System.err);
            System.err.flush();
        }
    }

    /**
     * Install a minimal IPackageManager via reflection on
     * ActivityThread.sPackageManager (static volatile field). Similar to the
     * ActivityManager path but bypasses the Singleton&lt;T&gt; indirection —
     * sPackageManager is set directly and ActivityThread.getPackageManager()
     * returns early on the non-null check.
     *
     * Covers all PackageManager accesses inside ActivityThread / LoadedApk /
     * ContextImpl, which would otherwise NPE on
     * ServiceManager.getService("package") during Activity bind.
     */
    private static void installPackageManagerStub() {
        try {
            Object pmImpl = null;
            try {
                Class<?> realAdapter = Class.forName("adapter.packagemanager.PackageManagerAdapter");
                pmImpl = realAdapter.getMethod("getInstance").invoke(null);
                System.err.println("[AdapterIPM] real PackageManagerAdapter (getInstance singleton): " + pmImpl);
            } catch (Throwable rt) {
                System.err.println("[AdapterIPM] real adapter ctor FAILED, falling back to Proxy stub: " + rt);
                java.util.Set<String> hot = new java.util.HashSet<>();
                hot.add("getApplicationInfo");
                hot.add("getActivityInfo");
                hot.add("resolveIntent");
                hot.add("resolveContentProvider");
                hot.add("getInstalledPackages");
                pmImpl = makeProxyStub("AdapterIPM-stub",
                        "android.content.pm.IPackageManager", hot);
            }

            Class<?> atClass = Class.forName("android.app.ActivityThread");
            Field f = atClass.getDeclaredField("sPackageManager");
            f.setAccessible(true);
            f.set(null, pmImpl);
            System.err.println("[AdapterIPM] ActivityThread.sPackageManager installed = " + pmImpl);
            System.err.flush();
        } catch (Throwable t) {
            System.err.println("[AdapterIPM] install FAILED: " + t);
            t.printStackTrace(System.err);
            System.err.flush();
        }
    }

    /**
     * Install a minimal IWindowManager via reflection on
     * WindowManagerGlobal.sWindowManagerService (private static field).
     * Prevents NPE when Activity / ViewRootImpl ask WindowManagerGlobal
     * .getWindowManagerService() before any real OH window binding is ready.
     */
    private static void installWindowManagerStub() {
        try {
            Object wmImpl = null;
            try {
                Class<?> realAdapter = Class.forName("adapter.window.WindowManagerAdapter");
                wmImpl = realAdapter.getMethod("getInstance").invoke(null);
                System.err.println("[AdapterIWM] real WindowManagerAdapter (getInstance singleton): " + wmImpl);
            } catch (Throwable rt) {
                System.err.println("[AdapterIWM] real adapter ctor FAILED, falling back to Proxy stub: " + rt);
                java.util.Set<String> hot = new java.util.HashSet<>();
                hot.add("addWindow");
                hot.add("removeWindow");
                hot.add("relayout");
                hot.add("getCurrentAnimatorScale");
                wmImpl = makeProxyStub("AdapterIWM-stub",
                        "android.view.IWindowManager", hot);
            }

            Class<?> wmgClass = Class.forName("android.view.WindowManagerGlobal");
            Field f = wmgClass.getDeclaredField("sWindowManagerService");
            f.setAccessible(true);
            f.set(null, wmImpl);
            System.err.println("[AdapterIWM] WindowManagerGlobal.sWindowManagerService installed = " + wmImpl);
            System.err.flush();
        } catch (Throwable t) {
            System.err.println("[AdapterIWM] install FAILED: " + t);
            t.printStackTrace(System.err);
            System.err.flush();
        }
    }

    /**
     * G2.14v (2026-05-07): install InputManagerAdapter (extends
     * IInputManager.Stub) into InputManagerGlobal.sInstance / sService via
     * reflection.  AOSP InputManagerGlobal.getInstance() lazily constructs
     * the singleton by querying ServiceManager.getServiceOrThrow("input"),
     * which throws on OH (no SA named "input") and leaves sInstance null —
     * causing PhoneWindow.preparePanel → KeyCharacterMap.load → NPE for any
     * Activity that inflates a default panel.
     *
     * Adapter lives in <code>adapter.window.InputManagerAdapter</code>
     * (oh-adapter-framework.jar), loaded reflectively to keep this runtime
     * jar free of compile-time deps on adapter.window.* (which lives on a
     * separate classloader during early init).
     *
     * Authoritative spec: doc/Input_Adapter_design.html §1.2 + §2.4.
     */
    private static void installInputManagerAdapter() {
        try {
            Object stub;
            try {
                Class<?> realAdapter = Class.forName("adapter.window.InputManagerAdapter");
                stub = realAdapter.getMethod("getInstance").invoke(null);
                System.err.println("[AdapterIIM] real InputManagerAdapter (getInstance singleton): "
                        + stub);
            } catch (Throwable rt) {
                // No Proxy fallback (feedback.txt P0 / project pattern).  Surface failure
                // so the install path is fixable.
                System.err.println("[AdapterIIM] real adapter unavailable: " + rt);
                Throwable c = rt;
                int depth = 0;
                while (c != null && depth < 5) {
                    System.err.println("[AdapterIIM][DEBUG] cause[" + depth + "]: " + c);
                    c.printStackTrace(System.err);
                    c = c.getCause();
                    depth++;
                }
                System.err.flush();
                return;
            }

            Class<?> imgClass = Class.forName("android.hardware.input.InputManagerGlobal");
            Class<?> iimClass = Class.forName("android.hardware.input.IInputManager");

            // Build an InputManagerGlobal instance wrapping the Stub.  AOSP 14
            // exposes a private (IInputManager) constructor used internally by
            // resetInstance(); reach it reflectively to avoid the public
            // getInstance() path that calls ServiceManager.getServiceOrThrow.
            Object imgInstance = null;
            try {
                java.lang.reflect.Constructor<?> ctor = imgClass.getDeclaredConstructor(iimClass);
                ctor.setAccessible(true);
                imgInstance = ctor.newInstance(stub);
            } catch (NoSuchMethodException nsme) {
                // Older AOSP layouts may use a different constructor; fall
                // back to nulling sInstance and stamping sService alone — the
                // Java side accepts that shape too.
                System.err.println("[AdapterIIM] InputManagerGlobal(IInputManager) ctor missing; "
                        + "stamping sService field only");
            }

            if (imgInstance != null) {
                try {
                    Field sInstance = imgClass.getDeclaredField("sInstance");
                    sInstance.setAccessible(true);
                    sInstance.set(null, imgInstance);
                } catch (NoSuchFieldException nsf) {
                    System.err.println("[AdapterIIM] InputManagerGlobal.sInstance not found "
                            + "(field renamed?): " + nsf);
                }
            }

            // sService holds the IInputManager Stub directly — present on most
            // AOSP 14 layouts.  Stamp regardless of whether sInstance was set.
            try {
                Field sService = imgClass.getDeclaredField("sService");
                sService.setAccessible(true);
                sService.set(null, stub);
            } catch (NoSuchFieldException nsf) {
                // Some layouts only expose mIm via instance; not a hard failure.
                System.err.println("[AdapterIIM] InputManagerGlobal.sService not found: " + nsf);
            }

            System.err.println("[AdapterIIM] InputManagerGlobal sInstance="
                    + imgInstance + " sService=" + stub);
            System.err.flush();
        } catch (Throwable t) {
            System.err.println("[AdapterIIM] install FAILED: " + t);
            t.printStackTrace(System.err);
            System.err.flush();
        }
    }

    /**
     * Install a Proxy IServiceManager into ServiceManager.sServiceManager so
     * downstream ServiceManager.getService(...) calls don't NPE.  Hello World
     * path triggers this via ContextImpl.createSystemContext →
     * ResourcesManager.getDisplayMetrics → DisplayManagerGlobal.getInstance →
     * ServiceManager.getService("display") → sServiceManager.getService(...).
     *
     * The stub returns null IBinder for every name, which DisplayManagerGlobal
     * tolerates (falls back to local DisplayInfo defaults).  Real OH service
     * discovery is the future replacement.
     */
    /**
     * B.14: install OHServiceManager (extends IServiceManager.Stub — AIDL
     * server-side abstract base, same pattern as ActivityManagerAdapter
     * extends IActivityManager.Stub) into ServiceManager.sServiceManager.
     *
     * Replaces an earlier Proxy.newProxyInstance approach that hung
     * indefinitely under init service (ART class generation spin).
     */
    /**
     * 2026-04-29 (post-B.29, feedback.txt P0): inject the real
     * ActivityClientControllerAdapter (extends IActivityClientController.Stub)
     * into ActivityClient.INTERFACE_SINGLETON so handleLaunchActivity has a
     * compile-time-checked, type-safe IBinder to call (vs the prior
     * Proxy.newProxyInstance + InvocationHandler stub flagged in feedback.txt
     * P0 as a violation of AA.23 decision and CLAUDE.md "Forward Bridge
     * Pattern (Class Inheritance)").
     *
     * Adapter lives in <code>adapter.activity.ActivityClientControllerAdapter</code>
     * (oh-adapter-framework.jar) and is loaded reflectively to keep this
     * runtime jar free of compile-time deps on adapter.activity.* (which is
     * in a separate classloader during early init).
     */
    private static void installActivityClientControllerStub() {
        System.err.println("[AdapterIACC] CK1_enter (real Adapter extends Stub)"); System.err.flush();
        try {
            Object stub;
            try {
                Class<?> realAdapter = Class.forName("adapter.activity.ActivityClientControllerAdapter");
                stub = realAdapter.getMethod("getInstance").invoke(null);
                System.err.println("[AdapterIACC] CK2_real Adapter (getInstance singleton) = " + stub);
            } catch (Throwable rt) {
                // Real adapter unavailable — must NOT fall back to Proxy stub
                // (feedback.txt P0).  Surface failure so install path is fixable.
                System.err.println("[AdapterIACC] CK2_FAIL real ActivityClientControllerAdapter unavailable: " + rt);
                Log.e(TAG, "installActivityClientControllerStub: real adapter required, no Proxy fallback", rt);
                return;
            }
            System.err.flush();

            // Reflectively store into ActivityClient.INTERFACE_SINGLETON.mKnownInstance
            Class<?> acCls = Class.forName("android.app.ActivityClient");
            java.lang.reflect.Field singletonField = acCls.getDeclaredField("INTERFACE_SINGLETON");
            singletonField.setAccessible(true);
            Object singleton = singletonField.get(null);
            // Singleton is ActivityClientControllerSingleton extending Singleton<IActivityClientController>
            // Field mKnownInstance is on the inner class
            java.lang.reflect.Field knownField = singleton.getClass().getDeclaredField("mKnownInstance");
            knownField.setAccessible(true);
            knownField.set(singleton, stub);
            System.err.println("[AdapterIACC] CK3_installed mKnownInstance = " + stub); System.err.flush();

            // Also inject into Singleton.mInstance (parent class field) so getKey doesn't recreate
            Class<?> singletonBase = Class.forName("android.util.Singleton");
            java.lang.reflect.Field mInstance = singletonBase.getDeclaredField("mInstance");
            mInstance.setAccessible(true);
            mInstance.set(singleton, stub);
            System.err.println("[AdapterIACC] CK4_installed Singleton.mInstance = " + stub); System.err.flush();
            Log.i(TAG, "ActivityClient INTERFACE_SINGLETON installed real Adapter = " + stub);
        } catch (Throwable t) {
            System.err.println("[AdapterIACC] FAIL: " + t); System.err.flush();
            Log.e(TAG, "installActivityClientControllerStub FAILED", t);
        }
    }

    /**
     * B.20.r21 (2026-04-28) — pre-populate Settings.Global / Settings.System
     * NameValueCache so framework lookups (PhoneWindow.<init> reads
     * DEVELOPMENT_RENDER_SHADOWS_IN_COMPOSITOR) don't trip into
     * ContentResolver.acquireProvider → ActivityThread.installProvider →
     * NPE on null ApplicationInfo (no real Settings provider in our process).
     *
     * NameValueCache.mValues is a Map keyed by setting name; if entry exists
     * it returns directly without ContentProvider call.
     */
    /**
     * Inject a Proxy IContentProvider into Settings.NameValueCache.mProviderHolder
     * so getStringForUser doesn't go through acquireProvider (which NPEs on
     * null ApplicationInfo in installProviderAuthoritiesLocked).  The Proxy
     * handles call(method="GET_global"|"GET_secure"|"GET_system", arg=settingName)
     * by returning a Bundle{value=defaultValue}.
     */
    private static void installSettingsContentProviderStub() {
        System.err.println("[SettingsCache] installSettingsContentProviderStub enter");
        try {
            Class<?> icpClass = Class.forName("android.content.IContentProvider");
            java.lang.reflect.InvocationHandler h = (proxy, method, args) -> {
                String mname = method.getName();
                if ("call".equals(mname)) {
                    // Build empty Bundle as response (lookup miss = use default)
                    Class<?> bundleCls = Class.forName("android.os.Bundle");
                    Object b = bundleCls.getConstructor().newInstance();
                    return b;
                }
                if ("query".equals(mname)) return null;
                Class<?> rt = method.getReturnType();
                if (rt == void.class) return null;
                if (rt == boolean.class) return Boolean.FALSE;
                if (rt == int.class) return 0;
                if (rt == long.class) return 0L;
                return null;
            };
            Object stub = java.lang.reflect.Proxy.newProxyInstance(
                    icpClass.getClassLoader(),
                    new Class<?>[]{icpClass},
                    h);
            // Inject into all three Settings holders
            for (String settingsCls : new String[]{
                    "android.provider.Settings$Global",
                    "android.provider.Settings$Secure",
                    "android.provider.Settings$System"}) {
                injectProviderHolder(settingsCls, stub);
            }
            System.err.println("[SettingsCache] IContentProvider Proxy installed in 3 holders");
        } catch (Throwable t) {
            System.err.println("[SettingsCache] installSettingsContentProviderStub FAIL: " + t);
        }
    }

    private static void injectProviderHolder(String settingsCls, Object stubProvider) {
        try {
            Class<?> cls = Class.forName(settingsCls);
            java.lang.reflect.Field cacheField = null;
            for (java.lang.reflect.Field f : cls.getDeclaredFields()) {
                if (f.getType().getSimpleName().equals("NameValueCache")
                        && java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
                    cacheField = f;
                    break;
                }
            }
            if (cacheField == null) return;
            cacheField.setAccessible(true);
            Object cache = cacheField.get(null);
            if (cache == null) return;
            // mProviderHolder field on cache (or super)
            java.lang.reflect.Field holderField = null;
            try { holderField = cache.getClass().getDeclaredField("mProviderHolder"); }
            catch (NoSuchFieldException e1) {
                try { holderField = cache.getClass().getSuperclass().getDeclaredField("mProviderHolder"); }
                catch (NoSuchFieldException e2) {}
            }
            if (holderField == null) return;
            holderField.setAccessible(true);
            Object holder = holderField.get(cache);
            if (holder == null) return;
            // Holder.mContentProvider is private IContentProvider
            java.lang.reflect.Field cpField = null;
            try { cpField = holder.getClass().getDeclaredField("mContentProvider"); }
            catch (NoSuchFieldException e) {
                for (java.lang.reflect.Field f : holder.getClass().getDeclaredFields()) {
                    if (f.getType().getName().equals("android.content.IContentProvider")) {
                        cpField = f; break;
                    }
                }
            }
            if (cpField == null) return;
            cpField.setAccessible(true);
            cpField.set(holder, stubProvider);
            System.err.println("[SettingsCache]   injected mContentProvider into " + settingsCls + " holder");
        } catch (Throwable t) {
            System.err.println("[SettingsCache]   injectProviderHolder " + settingsCls + " FAIL: " + t);
        }
    }

    private static void preInitSettingsCache() {
        System.err.println("[SettingsCache] enter");
        installSettingsContentProviderStub();
        try {
            // The settings PhoneWindow / Activity launch path actually reads.
            String[] globalKeys = {
                "render_shadows_in_compositor", // DEVELOPMENT_RENDER_SHADOWS_IN_COMPOSITOR
                "ANIMATION_DURATION_SCALE",
                "animator_duration_scale",
                "transition_animation_scale",
                "window_animation_scale",
            };
            primeSettingsCache("android.provider.Settings$Global", globalKeys, "1");
            String[] secureKeys = {
                "accessibility_enabled",
                "accessibility_display_inversion_enabled",
                "high_text_contrast_enabled",
            };
            primeSettingsCache("android.provider.Settings$Secure", secureKeys, "0");
            String[] systemKeys = {
                "font_scale",
                "screen_brightness_mode",
            };
            primeSettingsCache("android.provider.Settings$System", systemKeys, "0");
            Log.i(TAG, "Settings.Global/Secure/System NameValueCache primed (PhoneWindow lookups bypass ContentProvider)");
        } catch (Throwable t) {
            Log.e(TAG, "preInitSettingsCache failed", t);
        }
    }

    @SuppressWarnings("unchecked")
    private static void primeSettingsCache(String settingsClass, String[] keys, String defaultVal) {
        try {
            Class<?> cls = Class.forName(settingsClass);
            // Find the static NameValueCache instance (named sNameValueCache or similar)
            java.lang.reflect.Field cacheField = null;
            for (java.lang.reflect.Field f : cls.getDeclaredFields()) {
                if (f.getType().getSimpleName().equals("NameValueCache")
                        && java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
                    cacheField = f;
                    break;
                }
            }
            if (cacheField == null) {
                Log.w(TAG, "primeSettingsCache: no NameValueCache field on " + settingsClass);
                return;
            }
            cacheField.setAccessible(true);
            Object cache = cacheField.get(null);
            if (cache == null) {
                Log.w(TAG, "primeSettingsCache: " + settingsClass + " sNameValueCache is null");
                return;
            }
            // mValues is the ArrayMap<String, String> cache.  Match by name
            // explicitly (NameValueCache also has mGenerationTrackers ArrayMap
            // — must not put strings there or ClassCastException follows).
            java.lang.reflect.Field valuesField = null;
            Class<?> cacheCls = cache.getClass();
            try {
                valuesField = cacheCls.getDeclaredField("mValues");
            } catch (NoSuchFieldException e1) {
                try { valuesField = cacheCls.getSuperclass().getDeclaredField("mValues"); }
                catch (NoSuchFieldException e2) { /* leave null */ }
            }
            if (valuesField == null) {
                Log.w(TAG, "primeSettingsCache: no mValues field on NameValueCache");
                return;
            }
            valuesField.setAccessible(true);
            Object map = valuesField.get(cache);
            if (map instanceof java.util.Map) {
                java.util.Map<String, String> m = (java.util.Map<String, String>) map;
                synchronized (cache) {
                    for (String k : keys) m.put(k, defaultVal);
                }
                System.err.println("[SettingsCache] OK " + settingsClass + " populated " + keys.length + " keys, sample=" + m.get(keys[0]));
            } else {
                System.err.println("[SettingsCache] WARN " + settingsClass + " mValues is not a Map: " + (map != null ? map.getClass().getName() : "null"));
            }
        } catch (Throwable t) {
            System.err.println("[SettingsCache] FAIL " + settingsClass + ": " + t);
            Log.w(TAG, "primeSettingsCache " + settingsClass + " FAILED: " + t);
        }
    }

    private static void installServiceManagerAdapter() {
        System.err.println("[AdapterISM] CK1_enter (B.14 real OHServiceManager)"); System.err.flush();
        try {
            // Direct instantiation via reflection (class lives in oh-adapter-framework.jar
            // BCP — same loader as ServiceManager so direct field set works).
            Class<?> ohSmCls = Class.forName("adapter.core.OHServiceManager");
            Object smImpl = ohSmCls.getConstructor().newInstance();
            System.err.println("[AdapterISM] CK2_after_OHServiceManager_new = " + smImpl); System.err.flush();

            Class<?> smClass = Class.forName("android.os.ServiceManager");
            Field f = smClass.getDeclaredField("sServiceManager");
            f.setAccessible(true);
            f.set(null, smImpl);
            System.err.println("[AdapterISM] CK3_installed sServiceManager = " + smImpl); System.err.flush();
        } catch (Throwable t) {
            System.err.println("[AdapterISM] install FAILED: " + t);
            t.printStackTrace(System.err);
            System.err.flush();
        }
    }


    /**
     * Common runtime initialization for child process.
     * Equivalent to RuntimeInit.commonInit().
     */
    private static void initCommonRuntime() {
        // Set default uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            Log.e(TAG, "FATAL EXCEPTION in " + t.getName(), e);
            System.exit(1);
        });

        // In production, also:
        // - Set timezone from system property
        // - Configure log redirects (stdout/stderr -> Android log)
        // - Initialize security providers
        Log.d(TAG, "Common runtime initialized");
    }

    /**
     * Initialize the Android-OH adapter layer.
     * Calls OHEnvironment.initialize() to connect to OH system services.
     */
    private static void initAdapterLayer() {
        try {
            Class<?> envClass = Class.forName("adapter.core.OHEnvironment");
            Method initMethod = envClass.getMethod("initialize");
            initMethod.invoke(null);
            Log.i(TAG, "Adapter layer initialized");
        } catch (ClassNotFoundException e) {
            Log.w(TAG, "OHEnvironment not found - adapter layer not available");
        } catch (Throwable t) {
            // Catch Throwable (not just Exception) so Error subclasses like
            // UnsatisfiedLinkError (when bridge.so lacks
            // Java_adapter_core_OHEnvironment_nativeInitialize) don't escape
            // up through initChild and trigger the uncaught handler
            // System.exit(1). Hello World's Java code path doesn't depend on
            // adapter layer being live, so continue booting. Tracked as a
            // pending gap — implement nativeInitialize in bridge when
            // adapter-facing services (ActivityManagerAdapter etc.) need it.
            Log.w(TAG, "Failed to initialize adapter layer (continuing): " + t);
        }
    }

    /**
     * B.27 revision: getSystem() inside AOSP code already triggers lazy
     * init via createSystemAssetsInZygoteLocked.  Calling setApkAssets
     * again on top of that just duplicates entries (prepends current
     * sSystemApkAssets[] then appends our new array).
     *
     * Correct adapter-layer init: directly reset sSystemApkAssets[] via
     * reflection to a clean [framework-res] array, then push to sSystem
     * via nativeSetApkAssets — bypassing the prepend-then-append path
     * entirely.  This is the minimal surgery to align sSystem state with
     * what real zygote init produces, without invoking zygote method or
     * causing duplicate entries.
     */
    private static void initSystemAssetManager() {
        try {
            Class<?> apkAssetsClass = Class.forName("android.content.res.ApkAssets");
            Class<?> amClass = Class.forName("android.content.res.AssetManager");

            // 1. Force lazy init of sSystem so any partial state is cleared
            android.content.res.AssetManager sysAm =
                    android.content.res.AssetManager.getSystem();

            // 2. Inspect current sSystemApkAssets[]
            java.lang.reflect.Field sSystemApkAssetsF = amClass.getDeclaredField("sSystemApkAssets");
            sSystemApkAssetsF.setAccessible(true);
            Object[] cur = (Object[]) sSystemApkAssetsF.get(null);
            System.err.println("[InitSysAM] current sSystemApkAssets length=" + cur.length);

            // 3. Build clean [framework-res] array
            int PROPERTY_SYSTEM = 1;
            java.lang.reflect.Method loadFromPath = apkAssetsClass.getDeclaredMethod(
                    "loadFromPath", String.class, int.class);
            loadFromPath.setAccessible(true);

            // TODO B27: replace with OH BMS query for system framework APK path.
            String frameworkPath = "/system/android/framework/framework-res.apk";
            Object frameworkApkAssets = loadFromPath.invoke(null, frameworkPath, PROPERTY_SYSTEM);

            Object[] cleanSystemAssets = (Object[]) java.lang.reflect.Array.newInstance(
                    apkAssetsClass, 1);
            cleanSystemAssets[0] = frameworkApkAssets;

            // 4. Replace static field directly (AVOIDS setApkAssets prepend bug)
            sSystemApkAssetsF.set(null, cleanSystemAssets);

            // 5. Replace sSystemApkAssetsSet (correct type: android.util.ArraySet)
            try {
                java.lang.reflect.Field sSetF = amClass.getDeclaredField("sSystemApkAssetsSet");
                sSetF.setAccessible(true);
                Class<?> arraySetClass = Class.forName("android.util.ArraySet");
                Object setVal = arraySetClass.getDeclaredConstructor().newInstance();
                arraySetClass.getMethod("add", Object.class).invoke(setVal, frameworkApkAssets);
                sSetF.set(null, setVal);
            } catch (Throwable t) {
                System.err.println("[InitSysAM] sSystemApkAssetsSet update FAIL (non-fatal): " + t);
            }

            // 6. Trigger sSystem.setApkAssets(empty, true) so internal mApkAssets
            //    rebuilds from the now-clean sSystemApkAssets[], invalidateCaches=true
            //    forces PackageGroup vector full re-init.
            java.lang.reflect.Method setApkAssets = amClass.getDeclaredMethod(
                    "setApkAssets",
                    java.lang.reflect.Array.newInstance(apkAssetsClass, 0).getClass(),
                    boolean.class);
            setApkAssets.setAccessible(true);
            Object[] emptyUser = (Object[]) java.lang.reflect.Array.newInstance(apkAssetsClass, 0);
            setApkAssets.invoke(sysAm, emptyUser, true /*invalidateCaches*/);

            // 7. Verify post-state
            String[] paths = sysAm.getApkPaths();
            System.err.println("[InitSysAM] post-init sSystem apkPaths="
                    + java.util.Arrays.toString(paths)
                    + "  (should be exactly 1 framework-res, no duplicates)");
        } catch (Throwable t) {
            System.err.println("[InitSysAM] FAILED: " + t);
            t.printStackTrace();
        }
    }

    /**
     * Find and invoke the static main() method of the target class.
     * This does not return - the target class enters its event loop.
     * Equivalent to RuntimeInit.findStaticMain().
     */
    private static void invokeStaticMain(String className) throws Exception {
        // 2026-04-23 PHASE7_5: probe confirmed child path is healthy when
        // invokeStaticMain skips ActivityThread.main. Now restored to real
        // call, with checkpoints + try/catch Throwable to capture whatever
        // ActivityThread.main hits. The reflective call wraps target
        // exceptions in InvocationTargetException; unwrap so the actual
        // cause (UnsatisfiedLinkError, Binder failure, etc.) surfaces in
        // the log instead of being hidden by reflective wrapper.
        System.err.println("[CHILD_CK] J_invokeStaticMain_entry className=" + className);
        System.err.flush();

        Class<?> clazz;
        try {
            clazz = Class.forName(className);
            System.err.println("[CHILD_CK] J_invokeStaticMain_classForName_OK");
            System.err.flush();
        } catch (Throwable t) {
            System.err.println("[CHILD_CK] J_invokeStaticMain_classForName_FAIL: " + t);
            t.printStackTrace(System.err);
            System.err.flush();
            throw t;
        }

        Method mainMethod;
        try {
            mainMethod = clazz.getMethod("main", String[].class);
            System.err.println("[CHILD_CK] J_invokeStaticMain_getMethod_OK");
            System.err.flush();
        } catch (Throwable t) {
            System.err.println("[CHILD_CK] J_invokeStaticMain_getMethod_FAIL: " + t);
            t.printStackTrace(System.err);
            System.err.flush();
            throw t;
        }

        // Phase 7 #5 probe: ActivityThread.main throws NoClassDefFoundError
        // ("Class not found using the boot class loader; no stack trace
        // available") with the class name stripped by ART's pre-allocated
        // exception path. Walk candidate dependencies one-by-one; the first
        // forName failure points at the missing class. After diagnosis,
        // remove this probe block.
        String[] probeClasses = {
            "com.android.internal.os.RuntimeInit",
            "android.os.Looper",
            "android.os.MessageQueue",
            "android.os.Handler",
            "android.os.HandlerThread",
            "android.os.Binder",
            "android.os.ServiceManager",
            "android.os.UserHandle",
            "android.os.StrictMode",
            "android.app.LoadedApk",
            "android.app.ContextImpl",
            "android.app.ResourcesManager",
            "android.app.IActivityManager",
            "android.app.IApplicationThread",
            "android.app.ActivityManager",
            "android.app.ActivityTaskManager",
            "android.content.res.Resources",
            "android.content.res.ResourcesImpl",
            "android.view.Display",
            "android.view.WindowManager",
            "android.view.WindowManagerImpl",
            "android.view.SurfaceControl",
            "android.view.ThreadedRenderer",
            "android.hardware.display.DisplayManager",
            "com.android.internal.policy.PhoneWindow",
            "com.android.internal.policy.DecorView",
            // ActivityThread.main early references (lines 8146-8170 of AOSP).
            // NOTE 2026-04-23: my first probe used wrong package names; corrected
            // against ActivityThread.java imports. The previous "FAIL" results
            // were false positives — all these classes DO exist in framework.jar,
            // just at the packages below (not where I originally guessed):
            "dalvik.system.CloseGuard",
            "android.os.Environment",
            "com.android.org.conscrypt.TrustedCertificateStore",  // not android.security.*
            // initializeMainlineModules() refs (correct packages):
            "android.telephony.TelephonyFrameworkInitializer",
            "android.os.TelephonyServiceManager",
            "android.os.StatsFrameworkInitializer",
            "android.os.StatsServiceManager",
            "android.media.MediaFrameworkInitializer",            // not android.os.*
            "android.media.MediaFrameworkPlatformInitializer",
            "android.media.MediaServiceManager",                   // not android.app.*
            "android.bluetooth.BluetoothFrameworkInitializer",     // not android.os.*
            "android.os.BluetoothServiceManager",
            "android.nfc.NfcFrameworkInitializer",                 // not android.os.*
            "android.nfc.NfcServiceManager",
            "android.scheduling.SchedulingFrameworkInitializer",   // not android.app.*
            "android.provider.DeviceConfigInitializer",
            "android.provider.DeviceConfigServiceManager",
            // Other ActivityThread.main body refs:
            "com.android.internal.os.BinderCallsStats",
            "android.util.LogPrinter",
            "android.util.Log",
            "com.android.internal.os.RuntimeInit",
            "android.app.ActivityThread$AndroidOs",  // inner class — ActivityThread loads it
            // G2.13 (2026-04-30) revert: the +16 candidates I added here CAUSED
            // TranslationManager.<clinit> to fail with "No SecureRandom impl"
            // (bouncycastle JCA setup never ran in this process).  ART marks
            // clinit-failed classes as ERRONEOUS — all subsequent forName on
            // them throws preallocated NoClassDefFoundError (no stack trace).
            // So the probe was actively CAUSING the very NCDFE we were trying
            // to diagnose.  Probe only picks classes that are KNOWN-OK to load.
        };
        for (String p : probeClasses) {
            try {
                Class.forName(p);
                // System.err.println("[CHILD_CK] J_probe_OK " + p); System.err.flush();
            } catch (Throwable t) {
                // Print full cause chain — ExceptionInInitializerError wraps the
                // real <clinit> failure as cause (often null message but real type).
                StringBuilder sb = new StringBuilder();
                sb.append("[CHILD_CK] J_probe_FAIL ").append(p);
                Throwable cur = t;
                int depth = 0;
                while (cur != null && depth < 6) {
                    sb.append("\n  ").append(depth == 0 ? "" : "caused by: ")
                      .append(cur.getClass().getName()).append(": ")
                      .append(cur.getMessage());
                    cur = cur.getCause();
                    depth++;
                }
                System.err.println(sb.toString());
                t.printStackTrace(System.err);
                System.err.flush();
            }
        }
        System.err.println("[CHILD_CK] J_probe_complete");
        System.err.flush();

        String[] args = new String[0];

        System.err.println("[CHILD_CK] J_invokeStaticMain_BEFORE_main_invoke");
        System.err.flush();
        try {
            // ActivityThread.main blocks forever in Looper.loop() on success.
            mainMethod.invoke(null, (Object) args);
            System.err.println("[CHILD_CK] J_invokeStaticMain_main_returned (unexpected!)");
            System.err.flush();
        } catch (java.lang.reflect.InvocationTargetException ite) {
            Throwable cause = ite.getCause();
            System.err.println("[CHILD_CK] J_invokeStaticMain_main_threw: "
                    + (cause != null ? cause : ite));
            if (cause != null) {
                cause.printStackTrace(System.err);
            } else {
                ite.printStackTrace(System.err);
            }
            System.err.flush();
            throw ite;
        } catch (Throwable t) {
            System.err.println("[CHILD_CK] J_invokeStaticMain_main_unexpected_throwable: " + t);
            t.printStackTrace(System.err);
            System.err.flush();
            throw t;
        }
    }
}
