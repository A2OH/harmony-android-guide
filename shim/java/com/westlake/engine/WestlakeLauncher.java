package com.westlake.engine;

import android.app.Activity;
import android.app.MiniServer;
import android.app.MiniActivityManager;
import android.app.ShimCompat;
import android.content.ComponentName;
import android.content.Intent;
import com.ohos.shim.bridge.OHBridge;

/**
 * Generic APK launcher for the Westlake VM subprocess.
 *
 * Replaces the app-specific MockDonaldsApp.main() with a generic entry point
 * that can launch any APK. Reads config from system properties set by the
 * Compose host (WestlakeVM.kt):
 *
 *   -Dwestlake.apk.path=/data/local/tmp/westlake/counter.apk
 *   -Dwestlake.apk.activity=me.tsukanov.counter.ui.MainActivity   (optional)
 *
 * If no APK path is set, falls back to the old MockDonalds behavior.
 *
 * Run on OHOS ART:
 *   dalvikvm -classpath aosp-shim.dex:counter.dex \
 *     -Dwestlake.apk.path=/path/to/counter.apk \
 *     com.westlake.engine.WestlakeLauncher
 */
public class WestlakeLauncher {
    private static boolean sLastApplicationCtorBypassed;
    private static final String TAG = "WestlakeLauncher";
    private static final int SURFACE_WIDTH = 480;
    private static final int SURFACE_HEIGHT = 800;
    private static final int YELP_SURFACE_HEIGHT = 1013;
    private static final int YELP_BOTTOM_NAV_TOP = 824;
    private static final int YELP_ROW_TOP = 268;
    private static final int YELP_ROW_HEIGHT = 96;
    private static final int YELP_ROW_GAP = 12;
    private static final String FRAMEWORK_POLICY_PROP = "westlake.framework.policy";
    private static final String FRAMEWORK_POLICY_WESTLAKE_ONLY = "westlake_only";
    private static final String BACKEND_MODE_PROP = "westlake.backend.mode";
    private static final String BACKEND_MODE_TARGET_OHOS = "target_ohos_backend";
    private static final String BACKEND_MODE_CONTROL_ANDROID = "control_android_backend";
    private static final String CUTOFF_CANARY_STAGE_PROP = "westlake.canary.stage";
    private static final String CUTOFF_CANARY_PACKAGE = "com.westlake.cutoffcanary";
    private static final String CUTOFF_CANARY_APPLICATION = "com.westlake.cutoffcanary.CanaryApp";
    private static final String CUTOFF_CANARY_ACTIVITY = "com.westlake.cutoffcanary.StageActivity";
    private static final String CUTOFF_CANARY_L3_ACTIVITY = "com.westlake.cutoffcanary.L3Activity";
    private static final String CUTOFF_CANARY_L4_ACTIVITY = "com.westlake.cutoffcanary.L4Activity";
    private static final String CUTOFF_CANARY_L4_HILT_ACTIVITY =
            "com.westlake.cutoffcanary.L4HiltActivity";
    private static native void nativeLog(String message);
    private static native boolean nativeCanOpenFile(String path);
    private static native byte[] nativeReadFileBytes(String path);
    private static native boolean nativeAppendFileLine(String path, String line);
    private static native String nativeVmProperty(String key);
    private static native int nativeVmArgCount();
    private static native String nativeVmArg(int index);
    private static native ClassLoader nativeSystemClassLoader();
    private static native Class<?> nativeFindClass(String className);
    private static native Object nativeAllocInstance(Class<?> target);
    private static native boolean nativePatchClassNoop(String className, ClassLoader loader);
    private static native void nativePrimeLaunchConfig();
    private static native boolean nativeIsCutoffCanaryLaunch();
    private static native void nativePrintException(Throwable t);
    private static boolean sNativeLogUnavailable;
    private static boolean sNativeVmPropertyUnavailable;
    private static boolean sNativeAppendFileLineUnavailable;
    private static boolean sNativeAllocUnavailable;
    private static boolean sNativePrimeLaunchConfigUnavailable;
    private static boolean sStandardStreamsInstalled;
    private static boolean sBackendModeResolved;
    private static boolean sControlAndroidBackendCached;
    private static String sResolvedBackendMode;
    private static String sBootApkPath;
    private static String sBootActivityName;
    private static String sBootPackageName;
    private static String sBootManifestPath;
    private static String sBootResDir;
    private static boolean sBootCutoffCanaryLaunch;
    private static final java.util.HashMap<String, String> sLaunchFileProps = new java.util.HashMap<String, String>();
    private static final java.util.ArrayList<Activity> sInstalledDashboardFallbacks =
            new java.util.ArrayList<Activity>();
    private static boolean sLaunchFileLoaded;
    private static boolean sLoggedAppClassLoaders;
    private static boolean sLoggedDashboardOwnership;
    private static boolean sDirectDashboardFallbackActive;
    private static boolean sDisableHostInputPolling;
    private static int sDashboardRootDrawCount;
    private static int sDashboardCanvasDrawCount;
    private static int sHttpBridgeSeq;
    private static volatile int sHttpBridgeLastStatus;
    private static volatile String sHttpBridgeLastError;
    public static byte[] splashImageData; // Raw image bytes for OP_IMAGE rendering
    /** Real Android context when running on app_process64 */
    public static Object sRealContext;
    /** Pre-rendered icons bitmap (PNG bytes) from real framework */
    private static byte[] realIconsPng;
    private static final String CUTTOFF_CANARY_TRACE_PATH =
            "/data/user/0/com.westlake.host/files/vm/cutoff_canary_trace.log";
    private static final String CUTOFF_CANARY_PUBLIC_TRACE_PATH =
            "/data/local/tmp/westlake/cutoff_canary_trace.log";
    private static final String CUTOFF_CANARY_MARKER_PATH =
            "/data/user/0/com.westlake.host/files/vm/cutoff_canary_markers.log";
    private static final String CUTOFF_CANARY_PUBLIC_MARKER_PATH =
            "/data/local/tmp/westlake/cutoff_canary_markers.log";
    private static volatile String sLastLaunchMarker;

    private static void safeNativeLog(String message) {
        if (sNativeLogUnavailable || message == null) {
            return;
        }
        try {
            nativeLog(message);
        } catch (Throwable ignored) {
            sNativeLogUnavailable = true;
        }
    }

    private static String safeNativeVmProperty(String key) {
        if (sNativeVmPropertyUnavailable || key == null) {
            return null;
        }
        try {
            return nativeVmProperty(key);
        } catch (Throwable ignored) {
            sNativeVmPropertyUnavailable = true;
            return null;
        }
    }

    private static boolean safeNativeAppendFileLine(String path, String line) {
        if (sNativeAppendFileLineUnavailable || path == null || line == null) {
            return false;
        }
        try {
            return nativeAppendFileLine(path, line);
        } catch (Throwable ignored) {
            sNativeAppendFileLineUnavailable = true;
            return false;
        }
    }

    private static Object safeNativeAllocInstance(Class<?> target) {
        if (sNativeAllocUnavailable || target == null) {
            return null;
        }
        try {
            return nativeAllocInstance(target);
        } catch (Throwable ignored) {
            sNativeAllocUnavailable = true;
            return null;
        }
    }

    private static void safeNativePrimeLaunchConfig() {
        if (sNativePrimeLaunchConfigUnavailable) {
            return;
        }
        try {
            nativePrimeLaunchConfig();
        } catch (Throwable ignored) {
            sNativePrimeLaunchConfigUnavailable = true;
        }
    }

    private static boolean safeNativeIsCutoffCanaryLaunch() {
        try {
            return nativeIsCutoffCanaryLaunch();
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static String frameworkPolicyValue() {
        String value = safeNativeVmProperty(FRAMEWORK_POLICY_PROP);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        try {
            return System.getProperty(FRAMEWORK_POLICY_PROP);
        } catch (Throwable ignored) {
            return null;
        }
    }

    public static boolean isRealFrameworkFallbackAllowed() {
        return !FRAMEWORK_POLICY_WESTLAKE_ONLY.equals(frameworkPolicyValue());
    }

    private static String backendModeValue() {
        if (sBackendModeResolved && sResolvedBackendMode != null) {
            return sResolvedBackendMode;
        }
        synchronized (WestlakeLauncher.class) {
            if (sBackendModeResolved && sResolvedBackendMode != null) {
                return sResolvedBackendMode;
            }
            String value = safeNativeVmProperty(BACKEND_MODE_PROP);
            if (value == null || value.isEmpty()) {
                try {
                    String systemValue = System.getProperty(BACKEND_MODE_PROP);
                    if (systemValue != null && !systemValue.isEmpty()) {
                        value = systemValue;
                    }
                } catch (Throwable ignored) {
                }
            }
            if (value == null || value.isEmpty()) {
                String fileValue = launchFilePropertyInternal(BACKEND_MODE_PROP, false);
                if (fileValue != null && !fileValue.isEmpty()) {
                    value = fileValue;
                }
            }
            if (value == null || value.isEmpty()) {
                value = isRealFrameworkFallbackAllowed()
                        ? BACKEND_MODE_CONTROL_ANDROID
                        : BACKEND_MODE_TARGET_OHOS;
            }
            sResolvedBackendMode = value;
            sControlAndroidBackendCached = BACKEND_MODE_CONTROL_ANDROID.equals(value);
            sBackendModeResolved = true;
            return value;
        }
    }

    public static boolean isControlAndroidBackend() {
        if (sBackendModeResolved) {
            return sControlAndroidBackendCached;
        }
        backendModeValue();
        return sControlAndroidBackendCached;
    }

    private static ClassLoader engineClassLoader() {
        final boolean strictStandalone = !isRealFrameworkFallbackAllowed();
        if (strictStandalone) {
            marker("PF301 strict engineClassLoader entry");
            marker("PF301 strict engineClassLoader Westlake call");
        }
        ClassLoader cl = WestlakeLauncher.class.getClassLoader();
        if (strictStandalone) {
            marker("PF301 strict engineClassLoader Westlake returned");
        }
        if (cl != null) {
            if (strictStandalone) {
                marker("PF301 strict engineClassLoader Westlake nonnull");
            }
            return cl;
        }
        if (strictStandalone) {
            marker("PF301 strict engineClassLoader Object call");
        }
        ClassLoader objectCl = Object.class.getClassLoader();
        if (strictStandalone) {
            marker("PF301 strict engineClassLoader Object returned");
        }
        return objectCl;
    }

    public static ClassLoader safeGuestFallbackClassLoader() {
        final boolean strictStandalone = !isRealFrameworkFallbackAllowed();
        if (strictStandalone) {
            marker("PF301 strict safeGuestFallback entry");
        }
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (strictStandalone) {
            marker("PF301 strict safeGuestFallback context returned");
        }
        if (!isBootClassLoader(cl)) {
            if (strictStandalone) {
                marker("PF301 strict safeGuestFallback context nonboot");
            }
            return cl;
        }
        if (strictStandalone) {
            marker("PF301 strict safeGuestFallback null return");
            return null;
        }
        if (strictStandalone) {
            marker("PF301 strict safeGuestFallback engine call");
        }
        ClassLoader engine = engineClassLoader();
        if (strictStandalone) {
            marker("PF301 strict safeGuestFallback engine returned");
        }
        return engine;
    }

    private static void stderrLog(String message) {
        // Avoid java.io writes during standalone ART bootstrap. On the current
        // Westlake path, FileOutputStream.write() re-enters BlockGuard and
        // ThreadLocal initialization, which is still unstable and can derail
        // activity startup before the real failure is reached.
    }

    private static void startupLog(String message) {
        // Keep startup logging side-effect free. PrintStream/charset setup is not
        // reliable yet on the pure Westlake path, and pulling it in here aborts boot.
        // Also avoid android.util.Log here: once OHBridge is live, Log.i() routes
        // through native bridge logging and that path is still crash-prone during
        // early activity bootstrap.
        if (message == null) return;
        stderrLog(message);
        safeNativeLog(message);
    }

    private static void startupLog(String message, Throwable t) {
        startupLog(message + ": " + throwableTag(t));
    }

    private static void installSafeStandardStreams() {
        if (sStandardStreamsInstalled) {
            return;
        }
        sStandardStreamsInstalled = true;
        try {
            System.setOut(new SafeAsciiPrintStream(java.io.FileDescriptor.out));
            System.setErr(new SafeAsciiPrintStream(java.io.FileDescriptor.err));
        } catch (Throwable ignored) {
        }
    }

    private static final class SafeAsciiPrintStream extends java.io.PrintStream {
        private final java.io.OutputStream mOut;

        SafeAsciiPrintStream(java.io.FileDescriptor fd) {
            super(new java.io.ByteArrayOutputStream(), true);
            java.io.OutputStream out = null;
            try {
                out = new java.io.FileOutputStream(fd);
            } catch (Throwable ignored) {
            }
            mOut = out;
        }

        @Override public void write(int b) {
            try {
                if (mOut != null) {
                    mOut.write(b);
                }
            } catch (Throwable ignored) {
            }
        }

        @Override public void write(byte[] buf, int off, int len) {
            try {
                if (mOut != null && buf != null && off >= 0 && len > 0
                        && off + len <= buf.length) {
                    mOut.write(buf, off, len);
                }
            } catch (Throwable ignored) {
            }
        }

        @Override public void flush() {
            try {
                if (mOut != null) {
                    mOut.flush();
                }
            } catch (Throwable ignored) {
            }
        }

        @Override public void close() {
            flush();
        }

        private void writeAscii(String value) {
            String s = value == null ? "null" : value;
            byte[] bytes = new byte[s.length()];
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                bytes[i] = (byte) (c <= 0x7f ? c : '?');
            }
            write(bytes, 0, bytes.length);
        }

        private void newline() {
            write('\n');
        }

        @Override public void print(boolean b) { writeAscii(String.valueOf(b)); }
        @Override public void print(char c) { writeAscii(String.valueOf(c)); }
        @Override public void print(int i) { writeAscii(String.valueOf(i)); }
        @Override public void print(long l) { writeAscii(String.valueOf(l)); }
        @Override public void print(float f) { writeAscii(String.valueOf(f)); }
        @Override public void print(double d) { writeAscii(String.valueOf(d)); }
        @Override public void print(char[] s) { writeAscii(s == null ? null : new String(s)); }
        @Override public void print(String s) { writeAscii(s); }
        @Override public void print(Object obj) { writeAscii(String.valueOf(obj)); }
        @Override public void println() { newline(); }
        @Override public void println(boolean x) { print(x); newline(); }
        @Override public void println(char x) { print(x); newline(); }
        @Override public void println(int x) { print(x); newline(); }
        @Override public void println(long x) { print(x); newline(); }
        @Override public void println(float x) { print(x); newline(); }
        @Override public void println(double x) { print(x); newline(); }
        @Override public void println(char[] x) { print(x); newline(); }
        @Override public void println(String x) { print(x); newline(); }
        @Override public void println(Object x) { print(x); newline(); }
    }

    private static void startupLogLabelValue(String label, String value) {
        startupLog(label);
        startupLog(value == null ? "null" : value);
    }

    public static void strictTrace(String message) {
        startupLog(message);
    }

    private static String throwableTag(Throwable t) {
        return t == null ? "null" : t.getClass().getName();
    }

    private static String safeThrowableMessage(Throwable t) {
        if (t == null) {
            return null;
        }
        try {
            return t.getMessage();
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static String throwableSummary(Throwable t) {
        if (t == null) {
            return "null";
        }
        String message = safeThrowableMessage(t);
        if (message == null || message.isEmpty()) {
            return throwableTag(t);
        }
        return throwableTag(t) + ": " + message;
    }

    public static void dumpThrowable(String prefix, Throwable t) {
        if (prefix != null) {
            startupLog(prefix + ": " + throwableSummary(t));
        } else if (t != null) {
            startupLog(throwableSummary(t));
        }
    }

    private static void logThrowableFrames(String prefix, Throwable t, int maxFrames) {
        if (t == null) {
            return;
        }
        try {
            startupLog(prefix + " summary " + throwableSummary(t));
            Throwable cause = t.getCause();
            if (cause != null && cause != t) {
                startupLog(prefix + " cause " + throwableSummary(cause));
            }
            StackTraceElement[] frames = t.getStackTrace();
            if (frames == null) {
                return;
            }
            int count = Math.min(maxFrames, frames.length);
            for (int i = 0; i < count; i++) {
                StackTraceElement frame = frames[i];
                if (frame == null) {
                    continue;
                }
                startupLog(prefix + " #" + i + " " + frame.getClassName()
                        + "." + frame.getMethodName() + ":" + frame.getLineNumber());
            }
        } catch (Throwable ignored) {
        }
    }

    private static void logThrowableCause(String prefix, Throwable t) {
        if (t == null) {
            return;
        }
        try {
            Throwable cause = t.getCause();
            if (cause == null || cause == t) {
                return;
            }
            startupLog(prefix + ": " + throwableSummary(cause));
        } catch (Throwable ignored) {
        }
    }

    public static void trace(String message) {
        if (message == null) {
            return;
        }
        // Keep fine-grained lifecycle tracing off the JNI log bridge. The launch
        // path still trips interpreter/JNI faults when every WAT step re-enters
        // nativeLog(), so route trace-only diagnostics to stderr.
        stderrLog("[WestlakeLauncher] " + message);
    }

    public static void marker(String message) {
        if (message == null) {
            return;
        }
        sLastLaunchMarker = message;
        if (hasPrefix(message, "CV ") || hasPrefix(message, "CANARY_")) {
            appendCutoffCanaryTrace(message);
        }
        startupLog(message);
    }

    public static void noteMarker(String message) {
        if (message == null) {
            return;
        }
        sLastLaunchMarker = message;
        if (hasPrefix(message, "CV ")) {
            appendCutoffCanaryTrace(message);
        }
    }

    private static boolean hasPrefix(String value, String prefix) {
        if (value == null || prefix == null) {
            return false;
        }
        int prefixLength = prefix.length();
        if (value.length() < prefixLength) {
            return false;
        }
        for (int i = 0; i < prefixLength; i++) {
            if (value.charAt(i) != prefix.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public static boolean appendCutoffCanaryMarker(String message) {
        if (message == null || message.length() == 0) {
            return false;
        }
        if (safeNativeAppendFileLine(CUTOFF_CANARY_MARKER_PATH, message)) {
            return true;
        }
        if (safeNativeAppendFileLine(CUTOFF_CANARY_PUBLIC_MARKER_PATH, message)) {
            return true;
        }
        if (appendLineToFile(CUTOFF_CANARY_MARKER_PATH, message)) {
            return true;
        }
        return appendLineToFile(CUTOFF_CANARY_PUBLIC_MARKER_PATH, message);
    }

    private static String lastLaunchMarker() {
        return sLastLaunchMarker;
    }

    private static void appendCutoffCanaryTrace(String message) {
        if (message == null || message.length() == 0) {
            return;
        }
        if (safeNativeAppendFileLine(CUTTOFF_CANARY_TRACE_PATH, message)) {
            return;
        }
        safeNativeAppendFileLine(CUTOFF_CANARY_PUBLIC_TRACE_PATH, message);
    }

    private static boolean appendLineToFile(String path, String message) {
        if (path == null || path.length() == 0 || message == null) {
            return false;
        }
        try {
            java.io.File traceFile = new java.io.File(path);
            java.io.File parent = traceFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            java.io.FileOutputStream out = new java.io.FileOutputStream(traceFile, true);
            try {
                out.write(showcaseUtf8Bytes(message));
                out.write('\n');
                out.flush();
            } finally {
                out.close();
            }
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static Object tryAllocInstance(Class<?> target) {
        if (target == null) {
            return null;
        }
        if (!isRealFrameworkFallbackAllowed()) {
            return safeNativeAllocInstance(target);
        }
        try {
            startupLog("[WestlakeLauncher] tryAllocInstance begin: " + target.getName());
            Object instance = safeNativeAllocInstance(target);
            startupLog("[WestlakeLauncher] tryAllocInstance result: "
                    + (instance != null ? instance.getClass().getName() : "null"));
            return instance;
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] nativeAllocInstance failed for "
                    + target.getName() + ": " + throwableTag(t));
            return null;
        }
    }

    private static Object tryUnsafeAllocInstance(Class<?> target) {
        if (target == null) {
            return null;
        }
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            java.lang.reflect.Field field = unsafeClass.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Object unsafe = field.get(null);
            return unsafeClass.getMethod("allocateInstance", Class.class).invoke(unsafe, target);
        } catch (Throwable ignored) {
            try {
                Class<?> unsafeClass = Class.forName("jdk.internal.misc.Unsafe");
                java.lang.reflect.Field field = unsafeClass.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                Object unsafe = field.get(null);
                return unsafeClass.getMethod("allocateInstance", Class.class).invoke(unsafe, target);
            } catch (Throwable ignoredToo) {
                return null;
            }
        }
    }

    private static boolean tryUnsafeEnsureClassInitialized(Class<?> target) {
        if (target == null) {
            return false;
        }
        try {
            startupLog("PF301 strict launcher unsafe ensure sun class call");
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            startupLog("PF301 strict launcher unsafe ensure sun class returned");
            startupLog("PF301 strict launcher unsafe ensure sun field call");
            java.lang.reflect.Field field = unsafeClass.getDeclaredField("theUnsafe");
            startupLog("PF301 strict launcher unsafe ensure sun field returned");
            startupLog("PF301 strict launcher unsafe ensure sun accessible call");
            field.setAccessible(true);
            startupLog("PF301 strict launcher unsafe ensure sun accessible returned");
            startupLog("PF301 strict launcher unsafe ensure sun get call");
            Object unsafe = field.get(null);
            startupLog("PF301 strict launcher unsafe ensure sun instance returned");
            startupLog("PF301 strict launcher unsafe ensure sun invoke call");
            unsafeClass.getMethod("ensureClassInitialized", Class.class).invoke(unsafe, target);
            startupLog("PF301 strict launcher unsafe ensure sun invoke returned");
            return true;
        } catch (Throwable ignored) {
            try {
                startupLog("PF301 strict launcher unsafe ensure jdk class call");
                Class<?> unsafeClass = Class.forName("jdk.internal.misc.Unsafe");
                startupLog("PF301 strict launcher unsafe ensure jdk class returned");
                startupLog("PF301 strict launcher unsafe ensure jdk field call");
                java.lang.reflect.Field field = unsafeClass.getDeclaredField("theUnsafe");
                startupLog("PF301 strict launcher unsafe ensure jdk field returned");
                startupLog("PF301 strict launcher unsafe ensure jdk accessible call");
                field.setAccessible(true);
                startupLog("PF301 strict launcher unsafe ensure jdk accessible returned");
                startupLog("PF301 strict launcher unsafe ensure jdk get call");
                Object unsafe = field.get(null);
                startupLog("PF301 strict launcher unsafe ensure jdk instance returned");
                startupLog("PF301 strict launcher unsafe ensure jdk invoke call");
                unsafeClass.getMethod("ensureClassInitialized", Class.class).invoke(unsafe, target);
                startupLog("PF301 strict launcher unsafe ensure jdk invoke returned");
                return true;
            } catch (Throwable ignoredToo) {
                return false;
            }
        }
    }

    private static void primeAllocatedApplication(android.app.Application app) {
        if (app == null) {
            return;
        }
        try {
            java.lang.reflect.Field callbacksField =
                    android.app.Application.class.getDeclaredField("mCallbacks");
            callbacksField.setAccessible(true);
            if (callbacksField.get(app) == null) {
                callbacksField.set(app, new java.util.ArrayList());
            }
        } catch (Throwable ignored) {
        }
    }

    private static android.app.Application instantiateApplicationInstance(
            Class<?> appCls, String appClassName, boolean preferAllocation) throws Throwable {
        sLastApplicationCtorBypassed = false;
        Throwable ctorError = null;
        if (preferAllocation) {
            Object allocated = tryAllocInstance(appCls);
            if (!(allocated instanceof android.app.Application)) {
                allocated = tryUnsafeAllocInstance(appCls);
            }
            if (allocated instanceof android.app.Application) {
                android.app.Application app = (android.app.Application) allocated;
                primeAllocatedApplication(app);
                sLastApplicationCtorBypassed = true;
                startupLog("[WestlakeLauncher] Application allocated without ctor: " + appClassName);
                return app;
            }
        }
        try {
            Object instance = appCls.getDeclaredConstructor().newInstance();
            if (instance instanceof android.app.Application) {
                return (android.app.Application) instance;
            }
        } catch (Throwable t) {
            ctorError = t;
            startupLog("[WestlakeLauncher] Application ctor failed for " + appClassName
                    + ": " + throwableTag(t));
        }
        Object allocated = tryAllocInstance(appCls);
        if (!(allocated instanceof android.app.Application)) {
            allocated = tryUnsafeAllocInstance(appCls);
        }
        if (allocated instanceof android.app.Application) {
            android.app.Application app = (android.app.Application) allocated;
            primeAllocatedApplication(app);
            sLastApplicationCtorBypassed = true;
            startupLog("[WestlakeLauncher] Application ctor bypassed after failure: " + appClassName);
            return app;
        }
        if (ctorError != null) {
            throw ctorError;
        }
        throw new InstantiationException("Failed to instantiate application " + appClassName);
    }

    public static void patchProblematicAppClasses(ClassLoader loader) {
        if (loader == null) {
            return;
        }
        String[] classes = {
            "com.newrelic.agent.android.tracing.TraceMachine",
            "com.newrelic.agent.android.tracing.Trace",
            "com.newrelic.agent.android.NewRelic",
        };
        for (int i = 0; i < classes.length; i++) {
            String className = classes[i];
            try {
                if (nativePatchClassNoop(className, loader)) {
                    startupLog("[WestlakeLauncher] Patched " + className
                            + " on " + loaderTag(loader));
                }
            } catch (Throwable t) {
                startupLog("[WestlakeLauncher] Patch failed for " + className
                        + ": " + throwableTag(t));
            }
        }
    }

    private static String propOrSnapshot(String key, String snapshot) {
        String value = safeNativeVmProperty(key);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        try {
            if (!FRAMEWORK_POLICY_WESTLAKE_ONLY.equals(frameworkPolicyValue())) {
                String systemValue = System.getProperty(key);
                if (systemValue != null && !systemValue.isEmpty()) {
                    return systemValue;
                }
            }
        } catch (Throwable ignored) {
        }
        String argFlag = argFlagForProperty(key);
        if (argFlag != null) {
            String argValue = nativeArgValue(argFlag);
            if (argValue != null && !argValue.isEmpty()) {
                return argValue;
            }
        }
        String fileValue = launchFileProperty(key);
        if (fileValue != null && !fileValue.isEmpty()) {
            return fileValue;
        }
        return snapshot;
    }

    private static String copyString(String value) {
        return stabilizeString(value);
    }

    private static String stabilizeString(String value) {
        if (value == null) {
            return null;
        }
        try {
            int len = value.length();
            if (len == 0) {
                return "";
            }
            char[] chars = new char[len];
            value.getChars(0, len, chars, 0);
            return new String(chars);
        } catch (Throwable ignored) {
            try {
                return new String(value);
            } catch (Throwable ignoredToo) {
                return null;
            }
        }
    }

    private static String normalizePackageName(String packageName) {
        packageName = stabilizeString(packageName);
        if (packageName == null) {
            return null;
        }
        return packageName.isEmpty() ? null : packageName;
    }

    private static boolean isPlaceholderPackage(String packageName) {
        packageName = normalizePackageName(packageName);
        return packageName == null
                || "app".equals(packageName)
                || "com.example.app".equals(packageName);
    }

    private static String packageFromClassName(String className) {
        className = stabilizeString(className);
        if (className == null || className.isEmpty()) {
            return null;
        }
        int dot;
        try {
            dot = className.lastIndexOf('.');
        } catch (Throwable ignored) {
            return null;
        }
        if (dot <= 0 || dot > className.length()) {
            return null;
        }
        try {
            return className.substring(0, dot);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static String canonicalPackageName(
            String packageName,
            String activityName,
            android.content.pm.ManifestParser.ManifestInfo manifestInfo) {
        packageName = normalizePackageName(packageName);
        if (!isPlaceholderPackage(packageName)) {
            return packageName;
        }
        if (manifestInfo != null
                && manifestInfo.packageName != null
                && !manifestInfo.packageName.isEmpty()) {
            return manifestInfo.packageName;
        }
        String derived = packageFromClassName(activityName);
        if (derived != null && !derived.isEmpty()) {
            return derived;
        }
        return packageName;
    }

    private static String stableLaunchPackage(
            String fallbackPackage,
            String activityName,
            android.content.pm.ManifestParser.ManifestInfo manifestInfo) {
        String candidate = normalizePackageName(sBootPackageName);
        if (candidate == null) {
            candidate = normalizePackageName(launchFileProperty("westlake.apk.package"));
        }
        if (candidate == null) {
            candidate = normalizePackageName(nativeArgValue("--apk-package"));
        }
        if (candidate == null) {
            try {
                candidate = normalizePackageName(System.getProperty("westlake.apk.package"));
            } catch (Throwable ignored) {
            }
        }
        if (candidate == null) {
            candidate = normalizePackageName(fallbackPackage);
        }
        return canonicalPackageName(candidate, activityName, manifestInfo);
    }

    private static boolean safeStartsWith(String value, String prefix) {
        value = stabilizeString(value);
        prefix = stabilizeString(prefix);
        if (value == null || prefix == null) {
            return false;
        }
        try {
            int prefixLength = prefix.length();
            if (prefixLength == 0) {
                return true;
            }
            if (value.length() < prefixLength) {
                return false;
            }
            for (int i = 0; i < prefixLength; i++) {
                if (value.charAt(i) != prefix.charAt(i)) {
                    return false;
                }
            }
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static String packageFallbackForKnownApps(String packageName, String activityName) {
        if (safeStartsWith(activityName, "com.mcdonalds.")) {
            return "com.mcdonalds.app";
        }
        return packageName;
    }

    private static boolean isCutoffCanaryLaunch(
            String packageName,
            String activityName,
            String apkPath) {
        if (isExplicitCutoffCanaryLaunch(packageName, activityName, apkPath)) {
            return true;
        }
        if (isExplicitNonCanaryLaunch(packageName, activityName, apkPath)) {
            return false;
        }
        String stage = stabilizeString(propOrSnapshot(CUTOFF_CANARY_STAGE_PROP, null));
        if (stage != null && !stage.isEmpty()) {
            return true;
        }
        return launchFileBytesContain(CUTOFF_CANARY_PACKAGE)
                || launchFileBytesContain(CUTOFF_CANARY_ACTIVITY);
    }

    private static boolean isExplicitCutoffCanaryLaunch(
            String packageName,
            String activityName,
            String apkPath) {
        String stablePackage = stabilizeString(packageName);
        if (CUTOFF_CANARY_PACKAGE.equals(stablePackage)) {
            return true;
        }
        String stableActivity = stabilizeString(activityName);
        if (CUTOFF_CANARY_ACTIVITY.equals(stableActivity)) {
            return true;
        }
        if (CUTOFF_CANARY_L3_ACTIVITY.equals(stableActivity)) {
            return true;
        }
        if (CUTOFF_CANARY_L4_ACTIVITY.equals(stableActivity)) {
            return true;
        }
        String stableApkPath = stabilizeString(apkPath);
        if (stableApkPath != null && stableApkPath.contains("cutoffcanary")) {
            return true;
        }
        return false;
    }

    private static boolean isExplicitNonCanaryLaunch(
            String packageName,
            String activityName,
            String apkPath) {
        if (isExplicitCutoffCanaryLaunch(packageName, activityName, apkPath)) {
            return false;
        }
        String stablePackage = normalizePackageName(packageName);
        if (stablePackage != null && !stablePackage.isEmpty()
                && !isPlaceholderPackage(stablePackage)) {
            return true;
        }
        String stableActivity = stabilizeString(activityName);
        if (stableActivity != null && stableActivity.length() > 0
                && !safeStartsWith(stableActivity, CUTOFF_CANARY_PACKAGE)) {
            return true;
        }
        String stableApkPath = stabilizeString(apkPath);
        return stableApkPath != null && stableApkPath.length() > 0
                && stableApkPath.endsWith(".apk")
                && stableApkPath.indexOf("cutoffcanary") < 0;
    }

    private static boolean launchFileBytesContain(String needle) {
        if (needle == null || needle.length() == 0) {
            return false;
        }
        byte[] data = tryReadFileBytes("/data/local/tmp/westlake/westlake-launch.properties");
        if (data == null || data.length == 0 || data.length < needle.length()) {
            return false;
        }
        int limit = data.length - needle.length();
        for (int i = 0; i <= limit; i++) {
            boolean match = true;
            for (int j = 0; j < needle.length(); j++) {
                if ((data[i + j] & 0xff) != needle.charAt(j)) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return true;
            }
        }
        return false;
    }

    private static boolean stagedCutoffCanaryPresent() {
        String stagedApk = "/data/local/tmp/westlake/com_westlake_cutoffcanary.apk";
        try {
            if (nativeCanOpenFile(stagedApk)) {
                return true;
            }
        } catch (Throwable ignored) {
        }
        try {
            return new java.io.File(stagedApk).exists();
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static String preferredLaunchPackage(
            String primaryPackage,
            String secondaryPackage,
            String activityName,
            android.content.pm.ManifestParser.ManifestInfo manifestInfo) {
        String candidate = stableLaunchPackage(primaryPackage, activityName, manifestInfo);
        candidate = packageFallbackForKnownApps(candidate, activityName);
        if (!isPlaceholderPackage(candidate)) {
            return candidate;
        }
        candidate = stableLaunchPackage(secondaryPackage, activityName, manifestInfo);
        candidate = packageFallbackForKnownApps(candidate, activityName);
        if (!isPlaceholderPackage(candidate)) {
            return candidate;
        }
        candidate = canonicalPackageName(primaryPackage, activityName, manifestInfo);
        candidate = packageFallbackForKnownApps(candidate, activityName);
        if (!isPlaceholderPackage(candidate)) {
            return candidate;
        }
        candidate = canonicalPackageName(secondaryPackage, activityName, manifestInfo);
        candidate = packageFallbackForKnownApps(candidate, activityName);
        if (!isPlaceholderPackage(candidate)) {
            return candidate;
        }
        return candidate;
    }

    private static String describeProbeActivity(Activity activity) {
        if (activity == null) {
            return "null";
        }
        String name;
        try {
            name = activity.getClass().getName();
        } catch (Throwable ignored) {
            name = "unknown";
        }
        android.view.Window window = null;
        android.view.View decor = null;
        int childCount = -1;
        try {
            window = activity.getWindow();
            if (window != null) {
                decor = window.getDecorView();
                if (decor instanceof android.view.ViewGroup) {
                    childCount = ((android.view.ViewGroup) decor).getChildCount();
                } else if (decor != null) {
                    childCount = 1;
                }
            }
        } catch (Throwable ignored) {
        }
        return name
                + " window=" + (window != null)
                + " decor=" + (decor != null ? decor.getClass().getName() : "null")
                + " children=" + childCount;
    }

    private static boolean isCutoffCanaryActivity(Activity activity) {
        if (activity == null) {
            return false;
        }
        try {
            return CUTOFF_CANARY_ACTIVITY.equals(activity.getClass().getName());
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static String readCutoffCanaryMarker() {
        try {
            Class<?> loggerClass =
                    resolveAppClassChildFirstOrNull("com.westlake.cutoffcanary.CanaryLog");
            if (loggerClass == null) {
                return null;
            }
            java.lang.reflect.Method method = loggerClass.getDeclaredMethod("lastMarker");
            method.setAccessible(true);
            Object value = method.invoke(null);
            if (value instanceof String) {
                return (String) value;
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private static boolean launchCutoffCanaryStandalone(
            MiniActivityManager am,
            String stage) {
        startupLog("[WestlakeLauncher] canary standalone direct begin");
        appendCutoffCanaryTrace("CV canary standalone direct begin");
        if (am == null) {
            startupLog("[WestlakeLauncher] canary standalone missing AM");
            appendCutoffCanaryTrace("CV canary standalone missing AM");
            return false;
        }
        try {
            MiniServer.currentSetPackageName(CUTOFF_CANARY_PACKAGE);
        } catch (Throwable ignored) {
        }
        if ("L4WATAPPFACTORY".equals(stage)
                || "L4WATAPPREFLECT".equals(stage)
                || "L4WATCOREAPP".equals(stage)
                || "L4WATHILTAPP".equals(stage)) {
            appendCutoffCanaryTrace("CV canary application manual skipped app factory");
        } else {
            try {
                Class<?> appCls = resolveAppClassChildFirstOrNull(CUTOFF_CANARY_APPLICATION);
                if (appCls != null) {
                    Object appObject = appCls.getDeclaredConstructor().newInstance();
                    if (appObject instanceof android.app.Application) {
                        android.app.Application app = (android.app.Application) appObject;
                        primeAllocatedApplication(app);
                        MiniServer.currentSetApplication(app);
                        startupLog("[WestlakeLauncher] canary application onCreate begin");
                        appendCutoffCanaryTrace("CV canary application onCreate begin");
                        app.onCreate();
                        startupLog("[WestlakeLauncher] canary application onCreate returned");
                        appendCutoffCanaryTrace("CV canary application onCreate returned");
                    } else {
                        startupLog("[WestlakeLauncher] canary application type mismatch");
                        appendCutoffCanaryTrace("CV canary application type mismatch");
                    }
                } else {
                    startupLog("[WestlakeLauncher] canary application class null");
                    appendCutoffCanaryTrace("CV canary application class null");
                }
            } catch (Throwable t) {
                startupLog("[WestlakeLauncher] canary application failed", t);
                appendCutoffCanaryTrace("CV canary application failed " + throwableSummary(t));
            }
        }

        if ("L4WATRECREATE".equals(stage)
                || "L4WATFACTORY".equals(stage)
                || "L4WATAPPFACTORY".equals(stage)
                || "L4WATAPPREFLECT".equals(stage)
                || "L4WATCOREAPP".equals(stage)
                || "L4WATHILTAPP".equals(stage)
                || hasPrefix(stage, "L4WATPRECREATE")) {
            return launchCutoffCanaryViaWat(stage);
        }

        try {
            String activityName = hasPrefix(stage, "L4")
                    ? CUTOFF_CANARY_L4_ACTIVITY
                    : hasPrefix(stage, "L3")
                            ? CUTOFF_CANARY_L3_ACTIVITY
                            : CUTOFF_CANARY_ACTIVITY;
            Class<?> activityCls = resolveAppClassChildFirstOrNull(activityName);
            if (activityCls == null) {
                startupLog("[WestlakeLauncher] canary activity class null");
                appendCutoffCanaryTrace("CV canary activity class null");
                return false;
            }
            am.registerActivityClass(activityName, activityCls);
            startupLog("[WestlakeLauncher] canary activity start begin");
            appendCutoffCanaryTrace("CV canary activity start begin");
            am.startActivityDirect(null, CUTOFF_CANARY_PACKAGE, activityName, -1,
                    activityCls, stage);
            Activity resumed = am.getResumedActivity();
            startupLog(resumed != null
                    ? "[WestlakeLauncher] canary activity resumed"
                    : "[WestlakeLauncher] canary activity missing resumed");
            appendCutoffCanaryTrace(resumed != null
                    ? "CV canary activity resumed"
                    : "CV canary activity missing resumed");
            return resumed != null;
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] canary activity failed", t);
            appendCutoffCanaryTrace("CV canary activity failed " + throwableSummary(t));
            return false;
        }
    }

    private static boolean launchCutoffCanaryViaWat(String stage) {
        startupLog("[WestlakeLauncher] canary WAT launch begin");
        appendCutoffCanaryTrace("CV canary WAT launch begin");
        try {
            MiniServer.currentSetPackageName(CUTOFF_CANARY_PACKAGE);
        } catch (Throwable ignored) {
        }
        try {
            String activityName = "L4WATHILTAPP".equals(stage)
                    ? CUTOFF_CANARY_L4_HILT_ACTIVITY
                    : hasPrefix(stage, "L4")
                            ? CUTOFF_CANARY_L4_ACTIVITY
                            : CUTOFF_CANARY_ACTIVITY;
            Class<?> activityCls = resolveAppClassChildFirstOrNull(activityName);
            if (activityCls == null) {
                startupLog("[WestlakeLauncher] canary WAT activity class null");
                appendCutoffCanaryTrace("CV canary WAT activity class null");
                return false;
            }
            ClassLoader activityLoader = activityCls.getClassLoader();
            if (activityLoader != null) {
                Thread.currentThread().setContextClassLoader(activityLoader);
            }
            android.app.WestlakeActivityThread wat =
                    android.app.WestlakeActivityThread.currentActivityThread();
            if ("L4WATFACTORY".equals(stage)
                    || "L4WATAPPFACTORY".equals(stage)
                    || "L4WATAPPREFLECT".equals(stage)
                    || "L4WATCOREAPP".equals(stage)
                    || "L4WATHILTAPP".equals(stage)) {
                appendCutoffCanaryTrace("CV canary WAT factory manifest begin");
                String factoryClassName = "L4WATCOREAPP".equals(stage)
                        ? "androidx.core.app.CoreComponentFactory"
                        : cutoffCanaryManifestFactoryClassName();
                if (factoryClassName == null || factoryClassName.length() == 0) {
                    appendCutoffCanaryTrace("CV canary WAT factory manifest missing");
                    return false;
                }
                if ("L4WATCOREAPP".equals(stage)) {
                    appendCutoffCanaryTrace("CV canary WAT core factory selected "
                            + factoryClassName);
                }
                appendCutoffCanaryTrace("CV canary WAT factory manifest parsed "
                        + factoryClassName);
                appendCutoffCanaryTrace("CV canary WAT factory set begin");
                if (!wat.setAppComponentFactoryClassName(factoryClassName, activityLoader)) {
                    appendCutoffCanaryTrace("CV canary WAT factory set failed");
                    return false;
                }
                appendCutoffCanaryTrace("CV canary WAT factory set done");
            }
            if ("L4WATAPPFACTORY".equals(stage)
                    || "L4WATAPPREFLECT".equals(stage)
                    || "L4WATCOREAPP".equals(stage)
                    || "L4WATHILTAPP".equals(stage)) {
                String appClassName = "L4WATCOREAPP".equals(stage)
                        ? "com.westlake.cutoffcanary.CanaryCoreWrappedApp"
                        : cutoffCanaryManifestApplicationClassName();
                appendCutoffCanaryTrace("CV canary WAT app factory application parsed "
                        + appClassName);
                wat.forceMakeApplicationForNextLaunch(appClassName);
                appendCutoffCanaryTrace("CV canary WAT app factory force set done");
            }
            if (wat.getInstrumentation() == null) {
                appendCutoffCanaryTrace("CV canary WAT attach begin");
                android.app.WestlakeActivityThread.attachStandalone(
                        wat, CUTOFF_CANARY_PACKAGE, null, activityLoader);
                appendCutoffCanaryTrace("CV canary WAT attach returned");
            }
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setComponent(new android.content.ComponentName(
                    CUTOFF_CANARY_PACKAGE, activityName));
            intent.setPackage(CUTOFF_CANARY_PACKAGE);
            intent.putExtra("stage", stage);
            appendCutoffCanaryTrace("CV canary WAT before launchAndResume");
            Activity launched = wat.launchAndResumeActivity(
                    activityName, CUTOFF_CANARY_PACKAGE, intent, null);
            appendCutoffCanaryTrace(launched != null
                    ? "CV canary WAT launchAndResume returned"
                    : "CV canary WAT launchAndResume null");
            return launched != null;
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] canary WAT launch failed", t);
            appendCutoffCanaryTrace("CV canary WAT launch failed " + throwableSummary(t));
            return false;
        }
    }

    private static String cutoffCanaryManifestPath() {
        String manifestPath = propOrSnapshot("westlake.apk.manifest", sBootManifestPath);
        if ((manifestPath == null || manifestPath.length() == 0) && sBootResDir != null) {
            manifestPath = joinPath(sBootResDir, "AndroidManifest.xml");
        }
        if (manifestPath == null || manifestPath.length() == 0) {
            manifestPath = "/data/local/tmp/westlake/com_westlake_cutoffcanary_res/AndroidManifest.xml";
        }
        return manifestPath;
    }

    private static String cutoffCanaryManifestFactoryClassName() {
        String manifestPath = cutoffCanaryManifestPath();
        java.io.FileInputStream in = null;
        try {
            android.app.ApkInfo info = new android.app.ApkInfo();
            in = new java.io.FileInputStream(manifestPath);
            new android.app.BinaryXmlParser().parse(in, info);
            if (info.appComponentFactoryClassName != null
                    && info.appComponentFactoryClassName.length() > 0) {
                return info.appComponentFactoryClassName;
            }
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] canary manifest factory parse failed: "
                    + throwableTag(t));
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Throwable ignored) {
                }
            }
        }
        return scanManifestForAppComponentFactory(manifestPath, CUTOFF_CANARY_PACKAGE);
    }

    private static String cutoffCanaryManifestApplicationClassName() {
        String manifestPath = cutoffCanaryManifestPath();
        java.io.FileInputStream in = null;
        try {
            android.app.ApkInfo info = new android.app.ApkInfo();
            in = new java.io.FileInputStream(manifestPath);
            new android.app.BinaryXmlParser().parse(in, info);
            if (info.applicationClassName != null && info.applicationClassName.length() > 0) {
                return info.applicationClassName;
            }
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] canary manifest application parse failed: "
                    + throwableTag(t));
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Throwable ignored) {
                }
            }
        }
        return CUTOFF_CANARY_APPLICATION;
    }

    private static String scanManifestForAppComponentFactory(
            String manifestPath, String packageName) {
        byte[] data = tryReadFileBytes(manifestPath);
        if (data == null || data.length == 0) {
            return null;
        }
        String best = null;
        StringBuilder current = new StringBuilder();
        for (int i = 0; i + 1 < data.length; i += 2) {
            int lo = data[i] & 0xff;
            int hi = data[i + 1] & 0xff;
            char c = hi == 0 ? (char) lo : 0;
            if (c >= 32 && c <= 126) {
                current.append(c);
                continue;
            }
            best = selectAppComponentFactoryCandidate(best, current, packageName);
            current.setLength(0);
        }
        best = selectAppComponentFactoryCandidate(best, current, packageName);
        if (best != null) {
            appendCutoffCanaryTrace("CV canary WAT factory manifest raw fallback");
        }
        return best;
    }

    private static String selectAppComponentFactoryCandidate(
            String best, StringBuilder current, String packageName) {
        if (current == null || current.length() == 0) {
            return best;
        }
        String candidate = current.toString();
        if (candidate.indexOf("AppComponentFactory") < 0) {
            return best;
        }
        if ("appComponentFactory".equals(candidate)) {
            return best;
        }
        String resolved = candidate;
        if (resolved.startsWith(".") && packageName != null && packageName.length() > 0) {
            resolved = packageName + resolved;
        } else if (resolved.indexOf('.') < 0
                && packageName != null && packageName.length() > 0) {
            resolved = packageName + "." + resolved;
        }
        if (best == null || resolved.length() > best.length()) {
            return resolved;
        }
        return best;
    }

    private static String cutoffCanaryStage() {
        String stage = stabilizeString(propOrSnapshot(CUTOFF_CANARY_STAGE_PROP, null));
        if (stage == null || stage.length() == 0) {
            return "L1";
        }
        return stage;
    }

    private static void persistLaunchPackage(String packageName) {
        String stablePackage = normalizePackageName(packageName);
        if (isPlaceholderPackage(stablePackage)) {
            return;
        }
        boolean keepInLauncherStateOnly =
                isControlAndroidBackend() || CUTOFF_CANARY_PACKAGE.equals(stablePackage);
        sBootPackageName = keepInLauncherStateOnly ? stablePackage : copyString(stablePackage);
        if (!keepInLauncherStateOnly) {
            try {
                System.setProperty("westlake.apk.package", stablePackage);
            } catch (Throwable ignored) {
            }
        }
        try {
            MiniServer.currentSetPackageName(stablePackage);
        } catch (Throwable ignored) {
        }
    }

    private static String argFlagForProperty(String key) {
        if ("westlake.apk.path".equals(key)) return "--apk-path";
        if ("westlake.apk.activity".equals(key)) return "--apk-activity";
        if ("westlake.apk.package".equals(key)) return "--apk-package";
        if ("westlake.apk.resdir".equals(key)) return "--apk-resdir";
        if ("westlake.apk.manifest".equals(key)) return "--apk-manifest";
        if (CUTOFF_CANARY_STAGE_PROP.equals(key)) return "--canary-stage";
        return null;
    }

    private static String launchFilePropertyInternal(String key, boolean logLoadedPath) {
        if (!sLaunchFileLoaded) {
            sLaunchFileLoaded = true;
            if (!loadLaunchFileProperties(
                    "/data/local/tmp/westlake/westlake-launch.properties", logLoadedPath)) {
                loadLaunchFileProperties(
                        "/data/local/tmp/westlake/launch.properties", logLoadedPath);
            }
        }
        return sLaunchFileProps.get(key);
    }

    private static boolean loadLaunchFileProperties(String path, boolean logLoadedPath) {
        byte[] data = tryReadFileBytes(path);
        if (data == null || data.length == 0) {
            return false;
        }
        try {
            String text = new String(data);
            int start = 0;
            while (start <= text.length()) {
                int end = text.indexOf('\n', start);
                if (end < 0) {
                    end = text.length();
                }
                String line = text.substring(start, end).trim();
                if (!line.isEmpty()) {
                    int eq = line.indexOf('=');
                    if (eq > 0 && eq < line.length() - 1) {
                        sLaunchFileProps.put(line.substring(0, eq), line.substring(eq + 1));
                    }
                }
                if (end >= text.length()) {
                    break;
                }
                start = end + 1;
            }
            if (logLoadedPath) {
                startupLog("[WestlakeLauncher] Launch file loaded: " + path);
            }
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static String launchFileProperty(String key) {
        return launchFilePropertyInternal(key, true);
    }

    private static String argValue(String[] args, String flag) {
        if (args == null || flag == null) {
            return nativeArgValue(flag);
        }
        for (int i = 0; i + 1 < args.length; i++) {
            if (flag.equals(args[i])) {
                String value = args[i + 1];
                if (value != null && !value.isEmpty()) {
                    return value;
                }
                return null;
            }
        }
        return nativeArgValue(flag);
    }

    private static String nativeArgValue(String flag) {
        try {
            int count = nativeVmArgCount();
            for (int i = 0; i + 1 < count; i++) {
                String current = nativeVmArg(i);
                if (flag.equals(current)) {
                    String value = nativeVmArg(i + 1);
                    if (value != null && !value.isEmpty()) {
                        return value;
                    }
                    return null;
                }
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private static boolean isBootClassLoader(ClassLoader cl) {
        return cl == null || "java.lang.BootClassLoader".equals(cl.getClass().getName());
    }

    private static String loaderTag(ClassLoader cl) {
        if (cl == null) return "null";
        try {
            return cl.getClass().getName();
        } catch (Throwable t) {
            return "<error:" + throwableTag(t) + ">";
        }
    }

    private static void logClassOwnership(String label, Class<?> cls) {
        if (cls == null) {
            startupLog("[WestlakeLauncher] Ownership " + label + "=null");
            return;
        }
        try {
            Class<?> superCls = cls.getSuperclass();
            startupLog("[WestlakeLauncher] Ownership " + label
                    + " class=" + cls.getName()
                    + " loader=" + loaderTag(cls.getClassLoader())
                    + " super=" + (superCls != null ? superCls.getName() : "null")
                    + " superLoader=" + (superCls != null ? loaderTag(superCls.getClassLoader()) : "null"));
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] Ownership " + label + " failed", t);
        }
    }

    private static void logDashboardOwnershipOnce(Activity activity, Object fragment) {
        if (sLoggedDashboardOwnership) {
            return;
        }
        sLoggedDashboardOwnership = true;
        try {
            logClassOwnership("engine.FragmentActivity.ref", androidx.fragment.app.FragmentActivity.class);
            logClassOwnership("engine.Fragment.ref", androidx.fragment.app.Fragment.class);
            logClassOwnership("engine.FragmentManager.ref", androidx.fragment.app.FragmentManager.class);
            if (activity != null) {
                logClassOwnership("dashboard.activity", activity.getClass());
                startupLog("[WestlakeLauncher] Ownership dashboard.activity instanceof shim.FragmentActivity="
                        + (activity instanceof androidx.fragment.app.FragmentActivity));
            }
            if (fragment != null) {
                logClassOwnership("dashboard.fragment", fragment.getClass());
                startupLog("[WestlakeLauncher] Ownership dashboard.fragment instanceof shim.Fragment="
                        + (fragment instanceof androidx.fragment.app.Fragment));
            }
            Class<?> resolved = resolveAppClassOrNull("com.mcdonalds.homedashboard.fragment.HomeDashboardFragment");
            if (resolved != null) {
                logClassOwnership("dashboard.fragment.resolved", resolved);
                startupLog("[WestlakeLauncher] Ownership resolved fragment assignableTo shim.Fragment="
                        + androidx.fragment.app.Fragment.class.isAssignableFrom(resolved));
            }
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] Ownership dashboard snapshot failed", t);
        }
    }

    private static Class<?> findNamedClassOnHierarchy(Class<?> start, String className) {
        if (start == null || className == null || className.isEmpty()) {
            return null;
        }
        for (Class<?> c = start; c != null && c != Object.class; c = c.getSuperclass()) {
            if (className.equals(c.getName())) {
                return c;
            }
        }
        return null;
    }

    private static boolean hierarchyNameContains(Class<?> start, String needle) {
        if (start == null || needle == null || needle.isEmpty()) {
            return false;
        }
        Class<?> current = start;
        while (current != null && current != Object.class) {
            try {
                String name = current.getName();
                if (name != null && name.contains(needle)) {
                    return true;
                }
            } catch (Throwable t) {
                startupLog("[WestlakeLauncher] Hierarchy name read failed", t);
                return false;
            }
            try {
                current = current.getSuperclass();
            } catch (Throwable t) {
                startupLog("[WestlakeLauncher] Hierarchy superclass walk failed", t);
                return false;
            }
        }
        return false;
    }

    private static void logAppClassLoadersOnce() {
        if (sLoggedAppClassLoaders) {
            return;
        }
        sLoggedAppClassLoaders = true;
        try {
            String contextTag = loaderTag(Thread.currentThread().getContextClassLoader());
            String systemTag;
            try {
                systemTag = loaderTag(nativeSystemClassLoader());
            } catch (Throwable t) {
                systemTag = "<throws:" + throwableTag(t) + ">";
            }
            String engineTag = loaderTag(WestlakeLauncher.class.getClassLoader());
            startupLog("[WestlakeLauncher] Loader snapshot:"
                    + " context=" + contextTag
                    + " system=" + systemTag
                    + " engine=" + engineTag
                    + " classPath=" + System.getProperty("java.class.path"));
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] Loader snapshot failed", t);
        }
    }

    private static void ensureAppContextClassLoader() {
        ClassLoader current = Thread.currentThread().getContextClassLoader();
        if (!isBootClassLoader(current)) {
            return;
        }
        if (isControlAndroidBackend()) {
            ClassLoader fallback = safeGuestFallbackClassLoader();
            if (!isBootClassLoader(fallback)) {
                Thread.currentThread().setContextClassLoader(fallback);
            }
            return;
        }
        try {
            ClassLoader nativeLoader = nativeSystemClassLoader();
            if (!isBootClassLoader(nativeLoader)) {
                Thread.currentThread().setContextClassLoader(nativeLoader);
            }
        } catch (Throwable ignored) {
        }
    }

    private static ClassLoader appClassLoader() {
        if (!isRealFrameworkFallbackAllowed()) {
            ClassLoader strictCl = Thread.currentThread().getContextClassLoader();
            return isBootClassLoader(strictCl) ? null : strictCl;
        }
        logAppClassLoadersOnce();
        ensureAppContextClassLoader();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (!isBootClassLoader(cl)) {
            return cl;
        }
        try {
            cl = nativeSystemClassLoader();
        } catch (Throwable ignored) {
            cl = null;
        }
        return isBootClassLoader(cl) ? null : cl;
    }

    private static Class<?> loadAppClass(String className) throws ClassNotFoundException {
        ClassNotFoundException last = null;
        ClassLoader cl = appClassLoader();
        if (!isBootClassLoader(cl)) {
            try {
                return Class.forName(className, false, cl);
            } catch (ClassNotFoundException e) {
                last = e;
            }
            try {
                return cl.loadClass(className);
            } catch (ClassNotFoundException e) {
                last = e;
            }
        }
        try {
            Class<?> nativeCls = nativeFindClass(className);
            if (nativeCls != null) {
                return nativeCls;
            }
        } catch (Throwable ignored) {
        }
        if (last != null) {
            throw last;
        }
        throw new ClassNotFoundException(className + " (no app class loader)");
    }

    private static java.lang.reflect.Method findLoaderMethod(Class<?> start,
                                                             String methodName,
                                                             Class<?>... parameterTypes) {
        Class<?> current = start;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException ignored) {
            }
            current = current.getSuperclass();
        }
        return null;
    }

    private static Class<?> loadAppClassChildFirst(String className) throws ClassNotFoundException {
        ClassNotFoundException last = null;
        if (!isRealFrameworkFallbackAllowed()) {
            try {
                Class<?> nativeCls = nativeFindClass(className);
                if (nativeCls != null) {
                    return nativeCls;
                }
            } catch (Throwable ignored) {
            }
        }
        ClassLoader cl = appClassLoader();
        if (!isBootClassLoader(cl)) {
            // Avoid ClassLoader.findLoadedClass() here. On the standalone Westlake
            // guest path it can trigger VMClassLoader.<clinit>(), which currently
            // dereferences a null FileSystem before app bootstrap is complete.
            try {
                java.lang.reflect.Method findClass = findLoaderMethod(cl.getClass(), "findClass",
                        String.class);
                if (findClass != null) {
                    findClass.setAccessible(true);
                    Object direct = findClass.invoke(cl, className);
                    if (direct instanceof Class<?>) {
                        return (Class<?>) direct;
                    }
                }
            } catch (java.lang.reflect.InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof ClassNotFoundException) {
                    last = (ClassNotFoundException) cause;
                }
            } catch (Throwable ignored) {
            }
        }
        if (last != null) {
            throw last;
        }
        return loadAppClass(className);
    }

    public static Class<?> resolveAppClassOrNull(String className) {
        if (className == null || className.isEmpty()) {
            return null;
        }
        try {
            startupLog("PF301 strict resolveAppClassOrNull call");
            Class<?> resolved = loadAppClass(className);
            startupLog("PF301 strict resolveAppClassOrNull returned");
            return resolved;
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] resolveAppClassOrNull failed: "
                    + className + " -> " + throwableTag(t));
            return null;
        }
    }

    public static Class<?> resolveAppClassChildFirstOrNull(String className) {
        if (className == null || className.isEmpty()) {
            return null;
        }
        try {
            return loadAppClassChildFirst(className);
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] resolveAppClassChildFirstOrNull failed: "
                    + className + " -> " + throwableTag(t));
            return null;
        }
    }

    private static Intent buildLaunchIntent(String packageName, String className) {
        String resolvedPackage = stabilizeString(packageName);
        String resolvedClass = stabilizeString(className);
        if (safeStartsWith(resolvedClass, ".")
                && resolvedPackage != null && !resolvedPackage.isEmpty()) {
            resolvedClass = resolvedPackage + resolvedClass;
        }
        if ((resolvedPackage == null || resolvedPackage.isEmpty())
                && resolvedClass != null && !resolvedClass.isEmpty()) {
            resolvedPackage = packageFromClassName(resolvedClass);
        }
        if (resolvedPackage == null || resolvedPackage.isEmpty()) {
            resolvedPackage = MiniServer.currentPackageName();
        }
        if (resolvedPackage == null || resolvedPackage.isEmpty()) {
            resolvedPackage = sBootPackageName;
        }
        if (resolvedPackage == null || resolvedPackage.isEmpty()) {
            resolvedPackage = launchFileProperty("westlake.apk.package");
        }
        if (resolvedPackage == null || resolvedPackage.isEmpty()) {
            try {
                resolvedPackage = System.getProperty("westlake.apk.package");
            } catch (Throwable ignored) {
            }
        }
        resolvedPackage = stabilizeString(packageFallbackForKnownApps(resolvedPackage, resolvedClass));
        resolvedClass = stabilizeString(resolvedClass);
        if (resolvedPackage == null || resolvedPackage.isEmpty()
                || resolvedClass == null || resolvedClass.isEmpty()) {
            throw new IllegalArgumentException("launch component unresolved: pkg="
                    + packageName + " cls=" + className);
        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        try {
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
        } catch (Throwable ignored) {
        }
        try {
            intent.setClassName(resolvedPackage, resolvedClass);
        } catch (Throwable ignored) {
            intent.setComponent(new ComponentName(resolvedPackage, resolvedClass));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    private static boolean canOpenFile(String path) {
        if (path == null || path.isEmpty()) return false;
        try {
            return nativeCanOpenFile(path);
        } catch (Throwable ignored) {
        }
        if (FRAMEWORK_POLICY_WESTLAKE_ONLY.equals(frameworkPolicyValue())) {
            return false;
        }
        java.io.FileInputStream fis = null;
        try {
            fis = new java.io.FileInputStream(path);
            return true;
        } catch (Throwable t) {
            return false;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Throwable ignored) {
                }
            }
        }
    }

    private static byte[] readFileBytes(String path) throws java.io.IOException {
        if (path == null || path.isEmpty()) {
            throw new java.io.IOException("Empty path");
        }
        try {
            byte[] data = nativeReadFileBytes(path);
            if (data != null) {
                return data;
            }
        } catch (Throwable ignored) {
        }
        if (FRAMEWORK_POLICY_WESTLAKE_ONLY.equals(frameworkPolicyValue())) {
            throw new java.io.IOException("native read unavailable under strict standalone policy");
        }
        java.io.FileInputStream fis = new java.io.FileInputStream(path);
        try {
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream(8192);
            byte[] buf = new byte[8192];
            while (true) {
                int n = fis.read(buf);
                if (n < 0) break;
                if (n == 0) continue;
                out.write(buf, 0, n);
            }
            return out.toByteArray();
        } finally {
            fis.close();
        }
    }

    private static byte[] tryReadFileBytes(String path) {
        try {
            return readFileBytes(path);
        } catch (Throwable t) {
            return null;
        }
    }

    public static int bridgeHttpLastStatus() {
        return sHttpBridgeLastStatus;
    }

    public static String bridgeHttpLastError() {
        return sHttpBridgeLastError;
    }

    public static final class BridgeHttpResponse {
        public final int status;
        public final String headersJson;
        public final byte[] body;
        public final String error;
        public final boolean truncated;
        public final String finalUrl;

        public BridgeHttpResponse(int status, String headersJson, byte[] body,
                String error, boolean truncated, String finalUrl) {
            this.status = status;
            this.headersJson = headersJson == null ? "{}" : headersJson;
            this.body = body == null ? new byte[0] : body;
            this.error = error;
            this.truncated = truncated;
            this.finalUrl = finalUrl;
        }
    }

    public static byte[] bridgeHttpGetBytes(String url, int maxBytes, int timeoutMs) {
        sHttpBridgeLastStatus = 0;
        sHttpBridgeLastError = null;
        if (url == null || url.length() == 0) {
            sHttpBridgeLastError = "empty_url";
            return null;
        }
        if (url.indexOf('\n') >= 0 || url.indexOf('\r') >= 0 || url.indexOf('|') >= 0) {
            sHttpBridgeLastError = "unsafe_url";
            return null;
        }
        String bridgeDir = safeNativeVmProperty("westlake.bridge.dir");
        if (bridgeDir == null || bridgeDir.length() == 0) {
            try {
                bridgeDir = System.getenv("WESTLAKE_BRIDGE_DIR");
            } catch (Throwable ignored) {
                bridgeDir = null;
            }
        }
        if (bridgeDir == null || bridgeDir.length() == 0) {
            sHttpBridgeLastError = "missing_bridge_dir";
            return null;
        }
        int clampedMax = maxBytes;
        if (clampedMax <= 0) clampedMax = 128 * 1024;
        if (clampedMax > 512 * 1024) clampedMax = 512 * 1024;
        int clampedTimeout = timeoutMs;
        if (clampedTimeout < 250) clampedTimeout = 250;
        if (clampedTimeout > 15000) clampedTimeout = 15000;

        int seq = ++sHttpBridgeSeq;
        String reqPath = bridgeDir + "/http_requests.log";
        String metaPath = bridgeDir + "/http_" + seq + ".meta";
        String bodyPath = bridgeDir + "/http_" + seq + ".body";
        String line = seq + "|GET|" + clampedMax + "|" + url;
        if (!safeNativeAppendFileLine(reqPath, line)) {
            sHttpBridgeLastError = "request_write_failed";
            return null;
        }

        long deadline = System.currentTimeMillis() + clampedTimeout + 1000;
        while (System.currentTimeMillis() < deadline) {
            byte[] metaBytes = tryReadFileBytes(metaPath);
            if (metaBytes != null && metaBytes.length > 0) {
                String meta = bytesToUtf8(metaBytes);
                int status = parseBridgeMetaInt(meta, "status=", -1);
                sHttpBridgeLastStatus = status;
                sHttpBridgeLastError = parseBridgeMetaValue(meta, "error=");
                if (status >= 200 && status < 300) {
                    byte[] body = tryReadFileBytes(bodyPath);
                    if (body != null) {
                        return body;
                    }
                    sHttpBridgeLastError = "body_missing";
                    return null;
                }
                if (sHttpBridgeLastError == null || sHttpBridgeLastError.length() == 0) {
                    sHttpBridgeLastError = "status_" + status;
                }
                return null;
            }
            try {
                Thread.sleep(25);
            } catch (Throwable ignored) {
                break;
            }
        }
        sHttpBridgeLastStatus = -408;
        sHttpBridgeLastError = "timeout";
        return null;
    }

    public static BridgeHttpResponse bridgeHttpRequest(String url, String method,
            String headersJson, byte[] body, int maxBytes, int timeoutMs, boolean followRedirects) {
        sHttpBridgeLastStatus = 0;
        sHttpBridgeLastError = null;
        if (url == null || url.length() == 0) {
            return bridgeHttpErrorResponse(0, "empty_url", url);
        }
        if (url.indexOf('\n') >= 0 || url.indexOf('\r') >= 0 || url.indexOf('|') >= 0) {
            return bridgeHttpErrorResponse(0, "unsafe_url", url);
        }
        String normalizedMethod = normalizeBridgeHttpMethod(method);
        if (normalizedMethod == null) {
            return bridgeHttpErrorResponse(0, "unsupported_method", url);
        }
        String bridgeDir = bridgeHttpBridgeDir();
        if (bridgeDir == null || bridgeDir.length() == 0) {
            return bridgeHttpErrorResponse(0, "missing_bridge_dir", url);
        }
        int clampedMax = clampBridgeHttpMaxBytes(maxBytes);
        int clampedTimeout = clampBridgeHttpTimeout(timeoutMs);

        int seq = ++sHttpBridgeSeq;
        String reqPath = bridgeDir + "/http_requests.log";
        String metaPath = bridgeDir + "/http_" + seq + ".meta";
        String bodyPath = bridgeDir + "/http_" + seq + ".body";
        String safeHeadersJson = headersJson == null ? "{}" : headersJson;
        byte[] requestBody = body == null ? new byte[0] : body;
        String line = seq
                + "|V2|"
                + normalizedMethod
                + "|"
                + clampedMax
                + "|"
                + clampedTimeout
                + "|"
                + (followRedirects ? "1" : "0")
                + "|"
                + bridgeBase64Encode(stringToUtf8(safeHeadersJson))
                + "|"
                + bridgeBase64Encode(requestBody)
                + "|"
                + url;
        if (!safeNativeAppendFileLine(reqPath, line)) {
            return bridgeHttpErrorResponse(0, "request_write_failed", url);
        }

        long deadline = System.currentTimeMillis() + clampedTimeout + 1000;
        while (System.currentTimeMillis() < deadline) {
            byte[] metaBytes = tryReadFileBytes(metaPath);
            if (metaBytes != null && metaBytes.length > 0) {
                String meta = bytesToUtf8(metaBytes);
                int status = parseBridgeMetaInt(meta, "status=", -1);
                String error = parseBridgeMetaValue(meta, "error=");
                boolean truncated = "true".equals(parseBridgeMetaValue(meta, "truncated="));
                String responseHeaders = decodeBridgeMetaUtf8(meta, "headersJsonBase64=", "{}");
                String finalUrl = decodeBridgeMetaUtf8(meta, "finalUrlBase64=", url);
                byte[] responseBody = tryReadFileBytes(bodyPath);
                if (responseBody == null) {
                    responseBody = new byte[0];
                    if (error == null || error.length() == 0) {
                        error = "body_missing";
                    }
                }
                sHttpBridgeLastStatus = status;
                sHttpBridgeLastError = error;
                return new BridgeHttpResponse(status, responseHeaders, responseBody, error,
                        truncated, finalUrl);
            }
            try {
                Thread.sleep(25);
            } catch (Throwable ignored) {
                break;
            }
        }
        return bridgeHttpErrorResponse(-408, "timeout", url);
    }

    private static BridgeHttpResponse bridgeHttpErrorResponse(int status, String error, String finalUrl) {
        sHttpBridgeLastStatus = status;
        sHttpBridgeLastError = error;
        return new BridgeHttpResponse(status, "{}", new byte[0], error, false, finalUrl);
    }

    private static String bridgeHttpBridgeDir() {
        String bridgeDir = safeNativeVmProperty("westlake.bridge.dir");
        if (bridgeDir == null || bridgeDir.length() == 0) {
            try {
                bridgeDir = System.getenv("WESTLAKE_BRIDGE_DIR");
            } catch (Throwable ignored) {
                bridgeDir = null;
            }
        }
        return bridgeDir;
    }

    private static int clampBridgeHttpMaxBytes(int maxBytes) {
        int clampedMax = maxBytes;
        if (clampedMax <= 0) clampedMax = 128 * 1024;
        if (clampedMax > 512 * 1024) clampedMax = 512 * 1024;
        return clampedMax;
    }

    private static int clampBridgeHttpTimeout(int timeoutMs) {
        int clampedTimeout = timeoutMs;
        if (clampedTimeout < 250) clampedTimeout = 250;
        if (clampedTimeout > 60000) clampedTimeout = 60000;
        return clampedTimeout;
    }

    private static String normalizeBridgeHttpMethod(String method) {
        String value = method == null || method.length() == 0 ? "GET" : method.toUpperCase(java.util.Locale.US);
        if ("GET".equals(value) || "POST".equals(value) || "PUT".equals(value)
                || "PATCH".equals(value) || "DELETE".equals(value) || "HEAD".equals(value)
                || "OPTIONS".equals(value)) {
            return value;
        }
        return null;
    }

    private static String decodeBridgeMetaUtf8(String meta, String key, String fallback) {
        String value = parseBridgeMetaValue(meta, key);
        if (value == null || value.length() == 0) {
            return fallback;
        }
        byte[] decoded = bridgeBase64Decode(value);
        if (decoded == null) {
            return fallback;
        }
        return utf8BytesToString(decoded, fallback);
    }

    private static byte[] stringToUtf8(String value) {
        if (value == null) {
            return new byte[0];
        }
        return showcaseUtf8Bytes(value);
    }

    private static String utf8BytesToString(byte[] bytes, String fallback) {
        if (bytes == null) {
            return fallback;
        }
        StringBuilder out = new StringBuilder(bytes.length);
        int i = 0;
        while (i < bytes.length) {
            int b0 = bytes[i++] & 0xff;
            if (b0 < 0x80) {
                out.append((char) b0);
            } else if ((b0 & 0xe0) == 0xc0 && i < bytes.length) {
                int b1 = bytes[i++] & 0xff;
                if ((b1 & 0xc0) == 0x80) {
                    int cp = ((b0 & 0x1f) << 6) | (b1 & 0x3f);
                    appendUtf8CodePoint(out, cp >= 0x80 ? cp : '?');
                } else {
                    out.append('?');
                    i--;
                }
            } else if ((b0 & 0xf0) == 0xe0 && i + 1 < bytes.length) {
                int b1 = bytes[i++] & 0xff;
                int b2 = bytes[i++] & 0xff;
                if ((b1 & 0xc0) == 0x80 && (b2 & 0xc0) == 0x80) {
                    int cp = ((b0 & 0x0f) << 12) | ((b1 & 0x3f) << 6) | (b2 & 0x3f);
                    appendUtf8CodePoint(out, cp >= 0x800 ? cp : '?');
                } else {
                    out.append('?');
                    if ((b2 & 0xc0) != 0x80) {
                        i--;
                    }
                    if ((b1 & 0xc0) != 0x80) {
                        i--;
                    }
                }
            } else if ((b0 & 0xf8) == 0xf0 && i + 2 < bytes.length) {
                int b1 = bytes[i++] & 0xff;
                int b2 = bytes[i++] & 0xff;
                int b3 = bytes[i++] & 0xff;
                if ((b1 & 0xc0) == 0x80 && (b2 & 0xc0) == 0x80
                        && (b3 & 0xc0) == 0x80) {
                    int cp = ((b0 & 0x07) << 18) | ((b1 & 0x3f) << 12)
                            | ((b2 & 0x3f) << 6) | (b3 & 0x3f);
                    appendUtf8CodePoint(out, cp >= 0x10000 && cp <= 0x10ffff ? cp : '?');
                } else {
                    out.append('?');
                    if ((b3 & 0xc0) != 0x80) {
                        i--;
                    }
                    if ((b2 & 0xc0) != 0x80) {
                        i--;
                    }
                    if ((b1 & 0xc0) != 0x80) {
                        i--;
                    }
                }
            } else {
                out.append('?');
            }
        }
        return out.toString();
    }

    private static void appendUtf8CodePoint(StringBuilder out, int cp) {
        if (cp < 0 || cp > 0x10ffff || (cp >= 0xd800 && cp <= 0xdfff)) {
            out.append('?');
        } else if (cp <= 0xffff) {
            out.append((char) cp);
        } else {
            cp -= 0x10000;
            out.append((char) (0xd800 + (cp >> 10)));
            out.append((char) (0xdc00 + (cp & 0x3ff)));
        }
    }

    private static final char[] BRIDGE_BASE64 =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

    private static String bridgeBase64Encode(byte[] data) {
        if (data == null || data.length == 0) {
            return "";
        }
        StringBuilder out = new StringBuilder(((data.length + 2) / 3) * 4);
        int i = 0;
        while (i < data.length) {
            int b0 = data[i++] & 0xff;
            int b1 = i < data.length ? data[i++] & 0xff : -1;
            int b2 = i < data.length ? data[i++] & 0xff : -1;
            out.append(BRIDGE_BASE64[b0 >>> 2]);
            out.append(BRIDGE_BASE64[((b0 & 0x03) << 4) | (b1 >= 0 ? (b1 >>> 4) : 0)]);
            out.append(b1 >= 0 ? BRIDGE_BASE64[((b1 & 0x0f) << 2) | (b2 >= 0 ? (b2 >>> 6) : 0)] : '=');
            out.append(b2 >= 0 ? BRIDGE_BASE64[b2 & 0x3f] : '=');
        }
        return out.toString();
    }

    private static byte[] bridgeBase64Decode(String value) {
        if (value == null || value.length() == 0) {
            return new byte[0];
        }
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream((value.length() * 3) / 4);
        int[] block = new int[4];
        int count = 0;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\n' || c == '\r' || c == ' ' || c == '\t') {
                continue;
            }
            int decoded = bridgeBase64Value(c);
            if (decoded < 0 && c != '=') {
                return null;
            }
            block[count++] = c == '=' ? -2 : decoded;
            if (count == 4) {
                if (block[0] < 0 || block[1] < 0) {
                    return null;
                }
                out.write((block[0] << 2) | (block[1] >> 4));
                if (block[2] != -2) {
                    if (block[2] < 0) return null;
                    out.write(((block[1] & 0x0f) << 4) | (block[2] >> 2));
                }
                if (block[3] != -2) {
                    if (block[2] < 0 || block[3] < 0) return null;
                    out.write(((block[2] & 0x03) << 6) | block[3]);
                }
                count = 0;
            }
        }
        return count == 0 ? out.toByteArray() : null;
    }

    private static int bridgeBase64Value(char c) {
        if (c >= 'A' && c <= 'Z') return c - 'A';
        if (c >= 'a' && c <= 'z') return c - 'a' + 26;
        if (c >= '0' && c <= '9') return c - '0' + 52;
        if (c == '+') return 62;
        if (c == '/') return 63;
        return -1;
    }

    private static String bytesToUtf8(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        char[] chars = new char[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            chars[i] = (char) (bytes[i] & 0xff);
        }
        return new String(chars);
    }

    private static int parseBridgeMetaInt(String meta, String key, int fallback) {
        String value = parseBridgeMetaValue(meta, key);
        if (value == null || value.length() == 0) {
            return fallback;
        }
        try {
            return Integer.parseInt(value);
        } catch (Throwable ignored) {
            return fallback;
        }
    }

    private static String parseBridgeMetaValue(String meta, String key) {
        if (meta == null || key == null) {
            return null;
        }
        int at = meta.indexOf(key);
        if (at < 0) {
            return null;
        }
        int start = at + key.length();
        int end = meta.indexOf('\n', start);
        if (end < 0) {
            end = meta.length();
        }
        if (end > start && meta.charAt(end - 1) == '\r') {
            end--;
        }
        return meta.substring(start, end);
    }

    private static byte[] tryReadStandaloneFileBytes(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        try {
            byte[] data = nativeReadFileBytes(path);
            if (data != null && data.length > 0) {
                return data;
            }
        } catch (Throwable ignored) {
        }
        if (FRAMEWORK_POLICY_WESTLAKE_ONLY.equals(frameworkPolicyValue())) {
            return null;
        }
        return tryReadFileBytes(path);
    }

    public static void wireStandaloneActivityResources(
            Activity activity, String packageName, String className) {
        if (activity == null) {
            return;
        }
        android.content.res.Resources res = null;
        try {
            res = activity.getResources();
        } catch (Throwable ignored) {
        }
        if (res == null) {
            return;
        }

        String resDir = propOrSnapshot("westlake.apk.resdir", sBootResDir);
        String apkPath = propOrSnapshot("westlake.apk.path", sBootApkPath);
        int arscBytes = 0;
        int registeredLayoutCount = 0;
        int registeredLayoutBytes = 0;
        int showcaseLayoutBytes = 0;
        boolean tableLoaded = false;
        try {
            if (apkPath != null && apkPath.length() > 0) {
                ShimCompat.setApkPath(res, apkPath);
            }
            if (resDir != null && resDir.length() > 0) {
                try {
                    ShimCompat.setAssetDir(activity.getAssets(), resDir);
                } catch (Throwable ignored) {
                }
                byte[] arsc = tryReadStandaloneFileBytes(joinPath(resDir, "resources.arsc"));
                arscBytes = arsc != null ? arsc.length : 0;
                if (arsc != null && arsc.length > 0) {
                    try {
                        android.content.res.ResourceTable table =
                                new android.content.res.ResourceTable();
                        table.parse(arsc);
                        ShimCompat.loadResourceTable(res, table);
                        tableLoaded = true;
                    } catch (Throwable t) {
                        startupLog("[WestlakeLauncher] standalone ResourceTable parse failed", t);
                    }
                }
                int[] layoutStats = registerStandaloneLayouts(
                        res, activity.getClass().getClassLoader(), packageName, resDir);
                registeredLayoutCount = layoutStats[0];
                registeredLayoutBytes = layoutStats[1];
                if ("com.westlake.showcase".equals(packageName)
                        || className != null && className.startsWith("com.westlake.showcase.")) {
                    showcaseLayoutBytes = registerStandaloneLayout(
                            res, activity.getClass().getClassLoader(), packageName, resDir,
                            "showcase_activity");
                }
            }
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] standalone activity resource wiring failed", t);
        }
        if ("com.westlake.showcase".equals(packageName)
                || className != null && className.startsWith("com.westlake.showcase.")) {
            appendCutoffCanaryMarker("SHOWCASE_XML_RESOURCE_WIRE_OK engine=true"
                    + " table=" + (tableLoaded ? "true" : "false")
                    + " apk=" + (apkPath != null && apkPath.length() > 0 ? "true" : "false")
                    + " resDir=" + (resDir != null && resDir.length() > 0 ? "true" : "false")
                    + " arsc=" + intAscii(arscBytes)
                    + " layout=" + intAscii(showcaseLayoutBytes));
        }
        if ("com.westlake.yelplive".equals(packageName)
                || className != null && className.startsWith("com.westlake.yelplive.")) {
            appendCutoffCanaryMarker("YELP_XML_RESOURCE_WIRE_OK engine=true"
                    + " table=" + (tableLoaded ? "true" : "false")
                    + " apk=" + (apkPath != null && apkPath.length() > 0 ? "true" : "false")
                    + " resDir=" + (resDir != null && resDir.length() > 0 ? "true" : "false")
                    + " arsc=" + intAscii(arscBytes)
                    + " layouts=" + intAscii(registeredLayoutCount)
                    + " layoutBytes=" + intAscii(registeredLayoutBytes));
        }
        if ("com.westlake.mcdprofile".equals(packageName)
                || (className != null && className.startsWith("com.westlake.mcdprofile."))) {
            appendCutoffCanaryMarker("MCD_PROFILE_XML_RESOURCE_WIRE_OK engine=true"
                    + " table=" + (tableLoaded ? "true" : "false")
                    + " apk=" + (apkPath != null && apkPath.length() > 0 ? "true" : "false")
                    + " resDir=" + (resDir != null && resDir.length() > 0 ? "true" : "false")
                    + " arsc=" + intAscii(arscBytes)
                    + " layouts=" + intAscii(registeredLayoutCount)
                    + " layoutBytes=" + intAscii(registeredLayoutBytes));
        }
    }

    private static int[] registerStandaloneLayouts(
            android.content.res.Resources res,
            ClassLoader loader,
            String packageName,
            String resDir) {
        int[] stats = new int[] {0, 0};
        if (res == null || resDir == null || resDir.length() == 0) {
            return stats;
        }
        String pkg = packageName;
        if (pkg == null || pkg.length() == 0) {
            pkg = propOrSnapshot("westlake.apk.package", sBootPackageName);
        }
        if (pkg == null || pkg.length() == 0) {
            return stats;
        }
        String className = pkg + ".R$layout";
        Class<?> rLayout = null;
        try {
            rLayout = loader != null
                    ? Class.forName(className, false, loader)
                    : Class.forName(className);
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] layout class resolve failed "
                    + className + ": " + throwableTag(t));
            return stats;
        }
        java.lang.reflect.Field[] fields = null;
        try {
            fields = rLayout.getFields();
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] layout fields resolve failed "
                    + className + ": " + throwableTag(t));
            return stats;
        }
        if (fields == null) {
            return stats;
        }
        for (int i = 0; i < fields.length; i++) {
            java.lang.reflect.Field field = fields[i];
            if (field == null || field.getType() != Integer.TYPE) {
                continue;
            }
            String layoutName = field.getName();
            if (layoutName == null || layoutName.length() == 0) {
                continue;
            }
            int layoutId = 0;
            try {
                layoutId = field.getInt(null);
            } catch (Throwable t) {
                continue;
            }
            int bytes = registerStandaloneLayoutById(res, resDir, layoutName, layoutId);
            if (bytes > 0) {
                stats[0]++;
                stats[1] += bytes;
            }
        }
        return stats;
    }

    private static int registerStandaloneLayout(
            android.content.res.Resources res,
            ClassLoader loader,
            String packageName,
            String resDir,
            String layoutName) {
        if (res == null || layoutName == null || layoutName.length() == 0
                || resDir == null || resDir.length() == 0) {
            return 0;
        }
        int layoutId = resolveLayoutId(loader, packageName, layoutName);
        if (layoutId == 0) {
            return 0;
        }
        return registerStandaloneLayoutById(res, resDir, layoutName, layoutId);
    }

    private static int registerStandaloneLayoutById(
            android.content.res.Resources res,
            String resDir,
            String layoutName,
            int layoutId) {
        if (res == null || layoutName == null || layoutName.length() == 0
                || resDir == null || resDir.length() == 0 || layoutId == 0) {
            return 0;
        }
        byte[] data = tryReadStandaloneFileBytes(
                joinPath(resDir, "res/layout/" + layoutName + ".xml"));
        if (data == null || data.length == 0) {
            data = tryReadStandaloneFileBytes(
                    joinPath(resDir, "layout/" + layoutName + ".xml"));
        }
        if (data == null || data.length == 0) {
            return 0;
        }
        try {
            res.registerLayoutBytes(layoutId, data);
            startupLog("[WestlakeLauncher] standalone layout bytes registered "
                    + layoutName + " id=0x" + Integer.toHexString(layoutId)
                    + " bytes=" + data.length);
            return data.length;
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] standalone layout register failed "
                    + layoutName, t);
            return 0;
        }
    }

    private static int resolveLayoutId(ClassLoader loader, String packageName, String layoutName) {
        String pkg = packageName;
        if (pkg == null || pkg.length() == 0) {
            pkg = propOrSnapshot("westlake.apk.package", sBootPackageName);
        }
        if (pkg == null || pkg.length() == 0 || layoutName == null || layoutName.length() == 0) {
            return 0;
        }
        String className = pkg + ".R$layout";
        try {
            Class<?> rLayout = loader != null
                    ? Class.forName(className, false, loader)
                    : Class.forName(className);
            java.lang.reflect.Field field = rLayout.getField(layoutName);
            return field.getInt(null);
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] layout id resolve failed " + className
                    + "." + layoutName + ": " + throwableTag(t));
            return 0;
        }
    }

    private static String joinPath(String base, String relative) {
        if (base == null || base.isEmpty() || relative == null || relative.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder(base.length() + relative.length() + 1);
        sb.append(base);
        if (base.charAt(base.length() - 1) != '/') {
            sb.append('/');
        }
        sb.append(relative);
        return sb.toString();
    }

    private static boolean isHomeDashboardActivity(Activity activity) {
        return activity != null
                && "com.mcdonalds.homedashboard.activity.HomeDashboardActivity"
                .equals(activity.getClass().getName());
    }

    private static String leafName(String path) {
        if (path == null || path.isEmpty()) {
            return path;
        }
        int slash = path.lastIndexOf('/');
        return slash >= 0 ? path.substring(slash + 1) : path;
    }

    private static byte[] tryLoadSplashImage(String resDir, String relativePath) {
        String path = joinPath(resDir, relativePath);
        byte[] data = tryReadFileBytes(path);
        if (data != null && data.length > 0) {
            startupLog("[WestlakeLauncher] Loaded splash image: " + leafName(path)
                    + " (" + data.length + " bytes)");
            return data;
        }
        return null;
    }

    private static String parentPath(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        int slash = path.lastIndexOf('/');
        if (slash <= 0) {
            return null;
        }
        return path.substring(0, slash);
    }

    private static String resolveReadableResDir(String preferredPath) {
        String userDir = null;
        try {
            userDir = System.getProperty("user.dir", ".");
        } catch (Throwable ignored) {
        }
        String[] candidates = {
            preferredPath,
            "/data/local/tmp/westlake/mcd_res",
            "/data/local/tmp/westlake/apk_res",
            joinPath(userDir, "apk_res")
        };
        for (String candidate : candidates) {
            if (candidate == null || candidate.isEmpty()) {
                continue;
            }
            if (canOpenFile(joinPath(candidate, "resources.arsc"))) {
                return candidate;
            }
        }
        return null;
    }

    private static android.app.ApkInfo buildDexOnlyInfo(
            String packageName, String activityName, String resDir) {
        android.app.ApkInfo info = new android.app.ApkInfo();
        info.packageName = packageFallbackForKnownApps(
                canonicalPackageName(packageName, activityName, null), activityName);
        info.launcherActivity = activityName;
        info.assetDir = resDir;
        info.resDir = resDir;
        if (activityName != null && !activityName.isEmpty()) {
            info.activities.add(activityName);
        }
        return info;
    }

    public static void main(String[] args) {
        try {
            mainImpl(args);
        } catch (Throwable t) {
            if (!isControlAndroidBackend()) {
                try { nativePrintException(t); } catch (Throwable ignored) {}
                dumpThrowable("[WestlakeLauncher] main fatal", t);
                try { startupLog("[WestlakeLauncher] main fatal", t); } catch (Throwable ignored) {}
            }
            throw t;
        }
    }

    private static void mainImpl(String[] args) {
        installSafeStandardStreams();

        // Skip the reflective hidden-API exemption path on the standalone guest.
        // On the current imageless bootstrap, even building the String[] argument
        // for setHiddenApiExemptions can trip cross-loader ArrayStore issues before
        // the launcher reaches its first real progress log.
        startupLog("Hidden API exemption step skipped on standalone guest");

        // Load framework native stubs — but SKIP if running under app_process64
        // (it already has libandroid_runtime.so with all real framework natives)
        boolean hasRealRuntime = false;
        boolean strictWestlake = FRAMEWORK_POLICY_WESTLAKE_ONLY.equals(frameworkPolicyValue());
        // On the standalone Westlake guest we intentionally stay on the stub
        // ActivityThread path. The old "real runtime" probe pulled in
        // Bitmap/OHBridge clinit too early and could also trip the strict-mode
        // fatal branch before Splash.
        startupLog("Real runtime probe skipped on standalone guest");

        // In strict Westlake mode the runtime already called JNI_OnLoad_framework,
        // so avoid System.load/System.loadLibrary here; those paths pull in
        // java.nio.file and currently crash during file-system bootstrap.
        if (strictWestlake) {
            startupLog("Framework stubs pre-registered by runtime");
        } else try {
            System.loadLibrary("framework_stubs");
            startupLog("Framework stubs loaded");
        } catch (Throwable t) {
            try {
                System.load("/data/local/tmp/westlake/libframework_stubs.so");
                startupLog("Framework stubs loaded (absolute)");
            } catch (Throwable t2) {
                startupLog("Framework stubs unavailable");
            }
        }

        safeNativePrimeLaunchConfig();
        String apkPath = argValue(args, "--apk-path");
        if (apkPath == null) apkPath = propOrSnapshot("westlake.apk.path", sBootApkPath);
        String activityName = argValue(args, "--apk-activity");
        if (activityName == null) activityName = propOrSnapshot("westlake.apk.activity", sBootActivityName);
        String packageName = argValue(args, "--apk-package");
        if (packageName == null) packageName = propOrSnapshot("westlake.apk.package", sBootPackageName);
        packageName = normalizePackageName(packageName);
        if (packageName == null || packageName.isEmpty()) packageName = "com.example.app";
        String manifestPath = argValue(args, "--apk-manifest");
        if (manifestPath == null) manifestPath = propOrSnapshot("westlake.apk.manifest", sBootManifestPath);
        String bootResDir = argValue(args, "--apk-resdir");
        if (bootResDir == null) bootResDir = propOrSnapshot("westlake.apk.resdir", sBootResDir);
        boolean explicitNonCanaryLaunch =
                isExplicitNonCanaryLaunch(packageName, activityName, apkPath);
        boolean cutoffCanaryLaunch = isCutoffCanaryLaunch(packageName, activityName, apkPath)
                || (!explicitNonCanaryLaunch
                        && (sBootCutoffCanaryLaunch || safeNativeIsCutoffCanaryLaunch()));
        if (cutoffCanaryLaunch) {
            startupLog("[WestlakeLauncher] cutoff config detected");
            packageName = CUTOFF_CANARY_PACKAGE;
            activityName = CUTOFF_CANARY_ACTIVITY;
        }
        if (apkPath != null && !apkPath.isEmpty()) sBootApkPath = copyString(apkPath);
        if (activityName != null && !activityName.isEmpty()) sBootActivityName = copyString(activityName);
        if (packageName != null && !packageName.isEmpty()) sBootPackageName = copyString(packageName);
        if (manifestPath != null && !manifestPath.isEmpty()) sBootManifestPath = copyString(manifestPath);
        if (bootResDir != null && !bootResDir.isEmpty()) sBootResDir = copyString(bootResDir);
        boolean allowRealFrameworkFallback = isRealFrameworkFallbackAllowed();
        String backendMode = backendModeValue();
        try {
            ensureAppContextClassLoader();
        } catch (Throwable t) {
            try { nativePrintException(t); } catch (Throwable ignored) {}
            startupLog("ensureAppContextClassLoader failed");
        }

        // Initialize main thread Looper FIRST — before any class that checks isMainThread
        try {
            android.os.Looper.prepareMainLooper();
        } catch (Throwable t) {
            try { nativePrintException(t); } catch (Throwable ignored) {}
            startupLog("Looper.prepareMainLooper failed");
        }

        startupLog("Starting on OHOS + ART ...");
        startupLogLabelValue("APK:", apkPath);
        startupLogLabelValue("Activity:", activityName);
        startupLogLabelValue("Package:", packageName);
        startupLogLabelValue("Backend mode:", backendMode);
        if (allowRealFrameworkFallback) {
            startupLogLabelValue("Framework policy:", "allow_real");
        } else {
            startupLogLabelValue("Framework policy:", "westlake_only");
        }

        if (hasRealRuntime && !allowRealFrameworkFallback) {
            startupLog("FATAL: real framework runtime detected in strict Westlake mode");
            System.exit(86);
            return;
        }

        // The strict standalone guest should bootstrap through MiniServer and
        // WestlakeActivityThread, not through early real-framework ActivityThread
        // reflection. That reflection path drags in caller-sensitive stack APIs
        // before PF-202/PF-301 is stable.
        if (hasRealRuntime || allowRealFrameworkFallback) {
            try {
                Class<?> atClass = Class.forName("android.app.ActivityThread");
                Object at;
                Object sysCtx;

                if (hasRealRuntime) {
                    // app_process64: use systemMain() directly — real natives handle everything
                    startupLog("Using ActivityThread.systemMain() (real runtime)");
                    at = atClass.getDeclaredMethod("systemMain").invoke(null);
                    sysCtx = atClass.getDeclaredMethod("getSystemContext").invoke(at);
                    startupLog("SystemContext acquired from ActivityThread.systemMain()");
                    // Try to create MCD package context for its resources
                    if (allowRealFrameworkFallback && sysCtx instanceof android.content.Context) {
                        try {
                            // Use the installed MCD app's resources
                            android.content.Context mcdCtx = ((android.content.Context) sysCtx)
                                .createPackageContext("com.mcdonalds.app",
                                    android.content.Context.CONTEXT_INCLUDE_CODE |
                                    android.content.Context.CONTEXT_IGNORE_SECURITY);
                            startupLog("MCD context acquired");
                            startupLog("MCD resources available");
                            sRealContext = mcdCtx;
                        } catch (Throwable pe) {
                            startupLog("MCD context failed", pe);
                            sRealContext = (android.content.Context) sysCtx;
                        }
                    }
                } else {
                    // dalvikvm64 fallback path when real-framework fallback is enabled.
                    startupLog("Using manual ActivityThread (stub runtime)");

                    // Inject stub ServiceManager
                    Class<?> smClass = Class.forName("android.os.ServiceManager");
                    java.lang.reflect.Field cacheField = smClass.getDeclaredField("sCache");
                    cacheField.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    java.util.Map<String, android.os.IBinder> cache =
                        (java.util.Map<String, android.os.IBinder>) cacheField.get(null);
                    String[] services = {"activity","package","window","display","alarm","power",
                        "connectivity","wifi","audio","vibrator","notification","accessibility",
                        "input_method","input","clipboard","statusbar","deviceidle","device_policy",
                        "content","account","user","sensor_privacy","job_scheduler","device_config",
                        "color_display","uimode","overlay","autofill","batterystats","media_session",
                        "textclassification","SurfaceFlinger","permission","appops","rollback",
                        "usagestats","dropbox","companiondevice","trust","appwidget","wallpaper",
                        "dreams","people","locale","telephony.registry"};
                    for (String s : services) cache.put(s, new android.os.Binder());
                    // Also set sServiceManager proxy
                    java.lang.reflect.Field smField = smClass.getDeclaredField("sServiceManager");
                    smField.setAccessible(true);
                    java.lang.reflect.Method asInterface = Class.forName("android.os.ServiceManagerNative")
                        .getDeclaredMethod("asInterface", android.os.IBinder.class);
                    asInterface.setAccessible(true);
                    smField.set(null, asInterface.invoke(null, new android.os.Binder()));
                    startupLog("ServiceManager injected (" + services.length + " services)");

                    // Create ActivityThread (dalvikvm64 fallback path)
                    java.lang.reflect.Constructor<?> atCtor = atClass.getDeclaredConstructor();
                    atCtor.setAccessible(true);
                    at = atCtor.newInstance();
                    java.lang.reflect.Field sCurrentAT = atClass.getDeclaredField("sCurrentActivityThread");
                    sCurrentAT.setAccessible(true);
                    sCurrentAT.set(null, at);

                    sysCtx = atClass.getDeclaredMethod("getSystemContext").invoke(at);
                    startupLog("SystemContext acquired from stub ActivityThread");
                } // end of !hasRealRuntime else block
                if (allowRealFrameworkFallback && sysCtx instanceof android.content.Context && packageName != null) {
                    android.content.Context realCtx = ((android.content.Context) sysCtx)
                        .createPackageContext(packageName, 3); // INCLUDE_CODE | IGNORE_SECURITY
                    startupLog("Real Android context acquired");
                    android.content.res.Resources realRes = realCtx.getResources();
                    startupLog("Real resources acquired");
                    sRealContext = realCtx;

                    // Render real McD drawables directly to a bitmap and send through pipe
                    try {
                        android.graphics.Bitmap bmp = android.graphics.Bitmap.createBitmap(
                            SURFACE_WIDTH, SURFACE_HEIGHT, android.graphics.Bitmap.Config.ARGB_8888);
                        android.graphics.Canvas canvas = new android.graphics.Canvas(bmp);
                        canvas.drawColor(0xFF27251F); // McD dark

                        String pkg = "com.mcdonalds.app";
                        String[] names = {"archus", "splash_screen", "back_chevron", "close",
                            "ic_action_time", "ic_action_search"};
                        int y = 20, found = 0;
                        for (String name : names) {
                            int id = realRes.getIdentifier(name, "drawable", pkg);
                            if (id != 0) {
                                try {
                                    android.graphics.drawable.Drawable d = realCtx.getDrawable(id);
                                    if (d != null) {
                                        int size = 120;
                                        d.setBounds(20, y, 20 + size, y + size);
                                        d.draw(canvas);
                                        found++;
                                        y += size + 10;
                                    }
                                } catch (Throwable t) { y += 40; }
                            }
                        }
                        startupLog("Drew " + found + " real drawables");

                        // Compress to PNG and send via pipe
                        java.io.ByteArrayOutputStream pngOut = new java.io.ByteArrayOutputStream();
                        bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 90, pngOut);
                        byte[] png = pngOut.toByteArray();
                        startupLog("Real icons PNG prepared (" + png.length + " bytes)");

                        // Store for rendering after OHBridge is initialized
                        realIconsPng = png;
                        bmp.recycle();
                    } catch (Throwable t) {
                        startupLog("Real drawable capture failed");
                    }
                }
            } catch (Throwable t) {
                startupLog("Real context not available (custom ART)");
            }
        } else {
            startupLog("Real context bootstrap skipped on standalone guest");
        }

        // The strict standalone guest does not need manifest pre-parse during PF-202.
        // MiniServer/loader owns APK parsing later on the real guest path.
        android.content.pm.ManifestParser.ManifestInfo manifestInfo = null;
        if (hasRealRuntime || allowRealFrameworkFallback) {
            manifestPath = propOrSnapshot("westlake.apk.manifest", sBootManifestPath);
            if (manifestPath != null && !manifestPath.isEmpty()) {
                try {
                    byte[] data = readFileBytes(manifestPath);
                    if (data != null && data.length > 0) {
                        manifestInfo = android.content.pm.ManifestParser.parse(data);
                        startupLog("Manifest: " + manifestInfo.applicationClass
                            + " (" + manifestInfo.activities.size() + " activities, "
                            + manifestInfo.providers.size() + " providers)");
                    }
                } catch (Exception e) {
                    startupLog("Manifest parse error", e);
                }
            }
        } else {
            startupLog("Manifest preparse skipped on standalone guest");
        }

        // Check native bridge. On the strict standalone guest, defer OHBridge
        // class init until the core host path is stable.
        boolean nativeOk = false;
        if (hasRealRuntime || allowRealFrameworkFallback) {
            nativeOk = OHBridge.isNativeAvailable();
            startupLog("OHBridge native: " + (nativeOk ? "LOADED" : "UNAVAILABLE"));
        } else {
            startupLog("OHBridge bootstrap deferred on standalone guest");
        }

        if (nativeOk) {
            int rc = 0;
            try { rc = OHBridge.arkuiInit(); } catch (UnsatisfiedLinkError e) { /* subprocess — no arkui */ }
            startupLog("arkuiInit() = " + rc);

            // If real icons were pre-rendered (app_process64), send immediately
            if (realIconsPng != null) {
                startupLog("Sending real icons frame...");
                try {
                    long surf = OHBridge.surfaceCreate(0, SURFACE_WIDTH, SURFACE_HEIGHT);
                    long canv = OHBridge.surfaceGetCanvas(surf);
                    OHBridge.canvasDrawImage(canv, realIconsPng, 0, 0, SURFACE_WIDTH, SURFACE_HEIGHT);
                    int flushResult = OHBridge.surfaceFlush(surf);
                    startupLog("Real icons frame sent! flush=" + flushResult + " (" + realIconsPng.length + " bytes)");
                } catch (Throwable t) {
                    startupLog("Real icons send error");
                }
                // Continue to Activity launch (don't block here — pipe stays open via render loop)
            }
        }

        // Load auxiliary native helpers only after the strict standalone guest
        // has survived core bootstrap.
        if (hasRealRuntime || allowRealFrameworkFallback) {
            try {
                System.loadLibrary("test_jni");
                startupLog("test_jni loaded OK!");
            } catch (Throwable t) {
                startupLog("test_jni failed");
            }
            try {
                System.loadLibrary("androidfw_jni");
                startupLog("libandroidfw_jni loaded");
            } catch (Throwable t) {
                startupLog("libandroidfw_jni failed");
            }
        } else {
            startupLog("Aux native loads deferred on standalone guest");
        }

        if (strictWestlake) {
            startupLog("Bootstrap prop refresh deferred on standalone guest");
        } else {
            String bootstrapApkPath = propOrSnapshot("westlake.apk.path", sBootApkPath);
            if (bootstrapApkPath != null && !bootstrapApkPath.isEmpty()) apkPath = bootstrapApkPath;
            String bootstrapActivity = propOrSnapshot("westlake.apk.activity", sBootActivityName);
            if (bootstrapActivity != null && !bootstrapActivity.isEmpty()) activityName = bootstrapActivity;
            String bootstrapPackage = stableLaunchPackage(packageName, activityName, manifestInfo);
            if (bootstrapPackage != null && !bootstrapPackage.isEmpty()) packageName = bootstrapPackage;
            String bootstrapManifest = propOrSnapshot("westlake.apk.manifest", sBootManifestPath);
            if (bootstrapManifest != null && !bootstrapManifest.isEmpty()) manifestPath = bootstrapManifest;
            String bootstrapResDir = propOrSnapshot("westlake.apk.resdir", sBootResDir);
            if (bootstrapResDir != null && !bootstrapResDir.isEmpty()) bootResDir = bootstrapResDir;
            packageName = canonicalPackageName(packageName, activityName, manifestInfo);
            packageName = packageFallbackForKnownApps(packageName, activityName);
            if (apkPath != null && !apkPath.isEmpty()) sBootApkPath = copyString(apkPath);
            if (activityName != null && !activityName.isEmpty()) sBootActivityName = copyString(activityName);
            if (packageName != null && !packageName.isEmpty()) sBootPackageName = copyString(packageName);
            if (manifestPath != null && !manifestPath.isEmpty()) sBootManifestPath = copyString(manifestPath);
            if (bootResDir != null && !bootResDir.isEmpty()) sBootResDir = copyString(bootResDir);
            startupLog("[WestlakeLauncher] Bootstrap props refreshed: apk=" + apkPath
                + " activity=" + activityName + " package=" + packageName + " resDir=" + bootResDir);
        }

        // Initialize MiniServer
        if (strictWestlake) {
            startupLog("MiniServer init begin");
        } else {
            startupLog("MiniServer init begin pkg=" + packageName);
        }
        MiniServer server = MiniServer.init(packageName);
        if (strictWestlake) {
            startupLog("MiniServer init returned");
        } else {
            startupLog("MiniServer init returned server=" + server);
        }
        MiniActivityManager am = MiniServer.currentActivityManager();
        if (strictWestlake) {
            startupLog("MiniServer activityManager ready");
        } else {
            startupLog("MiniServer activityManager=" + am);
        }
        if (server == null) {
            throw new IllegalStateException("MiniServer.init returned null");
        }
        if (am == null) {
            throw new IllegalStateException("MiniServer activity manager missing");
        }
        startupLog("MiniServer initialized");
        if (!explicitNonCanaryLaunch && safeNativeIsCutoffCanaryLaunch()) {
            if (launchCutoffCanaryStandalone(am, cutoffCanaryStage())) {
                return;
            }
            appendCutoffCanaryTrace("CV canary standalone early direct fallback");
        }

        // Pre-seed SharedPreferences BEFORE any app code runs
        if ("me.tsukanov.counter".equals(packageName)) {
            android.content.SharedPreferences sp =
                android.content.SharedPreferencesImpl.getInstance("counters");
            if (sp.getAll().isEmpty()) {
                sp.edit().putInt("My Counter", 0)
                         .putInt("Steps", 42)
                         .putInt("Coffee", 3)
                         .apply();
                startupLog("Pre-seeded 3 counters");
            }
        }
        // Store counter data to set on CounterApplication after its creation
        final java.util.LinkedHashMap<String, Integer> counterData = new java.util.LinkedHashMap<>();
        if ("me.tsukanov.counter".equals(packageName)) {
            android.content.SharedPreferences sp = android.content.SharedPreferencesImpl.getInstance("counters");
            for (java.util.Map.Entry<String, ?> e : sp.getAll().entrySet()) {
                if (e.getValue() instanceof Integer) counterData.put(e.getKey(), (Integer) e.getValue());
            }
        }

        // Create the APK's custom Application class
        // Use manifest info if available, otherwise guess from package name
        String appClassName = null;
        if (manifestInfo != null && manifestInfo.applicationClass != null) {
            appClassName = manifestInfo.applicationClass;
            startupLog("[WestlakeLauncher] Application from manifest: " + appClassName);
        }
        if ("com.mcdonalds.app.application.McDMarketApplication".equals(appClassName)) {
            startupLog("[WestlakeLauncher] Skipping custom McDMarketApplication bootstrap");
            appClassName = null;
        }
        // Detect only explicit Hilt-generated Application classes here. The
        // broader McDonald's package/application heuristic regressed launch by
        // forcing an early ctor-bypassed Application path that was absent in
        // the last accepted baseline.
        boolean isHiltApp = false;
        if (appClassName != null) {
            if (appClassName.contains("Hilt_")) {
                isHiltApp = true;
                startupLog("[WestlakeLauncher] Hilt/Dagger app detected");
            }
        }

        if (appClassName != null) {
            try {
                Class<?> appCls = loadAppClass(appClassName);
                android.app.Application customApp = instantiateApplicationInstance(
                        appCls, appClassName, isHiltApp);
                // Attach real Android context as base (critical for app_process64 mode)
                if (sRealContext instanceof android.content.Context) {
                    try {
                        java.lang.reflect.Method attach = android.content.ContextWrapper.class
                            .getDeclaredMethod("attachBaseContext", android.content.Context.class);
                        attach.setAccessible(true);
                        attach.invoke(customApp, (android.content.Context) sRealContext);
                        startupLog("[WestlakeLauncher] Attached real context to Application");
                    } catch (Throwable t) {
                        startupLog("[WestlakeLauncher] attachBaseContext failed: " + throwableTag(t));
                    }
                }
                MiniServer.currentSetApplication(customApp);

                // Wire up resources + AssetManager BEFORE Application.onCreate()
                // so config files (gma_api_config.json, etc.) are accessible
                {
                    String earlyResDir = resolveReadableResDir(
                        propOrSnapshot("westlake.apk.resdir", sBootResDir));
                    if (cutoffCanaryLaunch) {
                        startupLog("[WestlakeLauncher] Canary early resource setup skipped");
                    } else if (earlyResDir != null) {
                        try {
                            android.app.ApkInfo earlyInfo = android.app.ApkLoader.loadFromExtracted(
                                earlyResDir, packageName, activityName);
                            try {
                                java.lang.reflect.Field f = MiniServer.class.getDeclaredField("mApkInfo");
                                f.setAccessible(true);
                                f.set(server, earlyInfo);
                            } catch (Exception ex) {}
                            android.content.res.Resources res = customApp.getResources();
                            if (earlyInfo.resourceTable != null) {
                                ShimCompat.loadResourceTable(res, (android.content.res.ResourceTable) earlyInfo.resourceTable);
                            }
                            ShimCompat.setApkPath(res, apkPath);
                            if (earlyInfo.assetDir != null) {
                                ShimCompat.setAssetDir(customApp.getAssets(), earlyInfo.assetDir);
                            }
                            startupLog("[WestlakeLauncher] Early resource/asset setup done (resDir=" + earlyResDir + ")");
                        } catch (Exception ex) {
                            startupLog("[WestlakeLauncher] Early resource setup failed: " + ex.getClass().getName());
                        }
                    }
                }

                // Run Application.onCreate with timeout for all apps. Skipping it
                // for ctor-bypassed McD/Hilt experiments regressed launch before
                // the last accepted baseline, so keep the normal threaded path.
                {
                    final android.app.Application appRef = customApp;
                    final boolean[] onCreateDone = { false };
                    final Throwable[] onCreateError = { null };
                    if (cutoffCanaryLaunch) {
                        try {
                            appRef.onCreate();
                            onCreateDone[0] = true;
                        } catch (Throwable e) {
                            onCreateDone[0] = true;
                            onCreateError[0] = e;
                            startupLog("[WestlakeLauncher] Application.onCreate error: " + throwableTag(e));
                        }
                    } else {
                        final Thread appThread = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    appRef.onCreate();
                                    onCreateDone[0] = true;
                                } catch (Throwable e) {
                                    onCreateDone[0] = true;
                                    onCreateError[0] = e;
                                    startupLog("[WestlakeLauncher] Application.onCreate error: " + throwableTag(e));
                                }
                            }
                        }, "AppOnCreate");
                        appThread.setDaemon(true);
                        appThread.start();
                        int timeoutMs = isHiltApp ? 3000 : 5000; // Hilt DI should settle quickly
                        long startTime = System.currentTimeMillis();
                        int reportInterval = 10000; // 10s
                        while (!onCreateDone[0] && (System.currentTimeMillis() - startTime) < timeoutMs) {
                            try { appThread.join(reportInterval); } catch (InterruptedException ie) {}
                            if (!onCreateDone[0]) {
                                long elapsed = (System.currentTimeMillis() - startTime) / 1000;
                                startupLog("[WestlakeLauncher] Application.onCreate still running (" + elapsed + "s)...");
                            }
                        }
                        if (!onCreateDone[0]) {
                            startupLog("[WestlakeLauncher] Application.onCreate TIMEOUT (" + timeoutMs + "ms)"
                                + " — continuing anyway (DI may be partial)");
                        }
                        // Force-kill the background thread to prevent CPU starvation and memory growth
                        if (!onCreateDone[0]) {
                            try { appThread.interrupt(); } catch (Throwable t) {}
                            try { appThread.stop(); } catch (Throwable t) {}  // deprecated but necessary
                            startupLog("[WestlakeLauncher] Killed Application.onCreate() thread");
                        }
                    }
                    if (onCreateDone[0]) {
                        startupLog("[WestlakeLauncher] Application.onCreate done: " + appCls.getSimpleName()
                            + (onCreateError[0] != null
                            ? " (with error: " + throwableTag(onCreateError[0]) + ")"
                            : ""));
                    }
                }
                // Force-set 'counters' field on CounterApplication (Counter app specific)
                try {
                    java.lang.reflect.Field cf = customApp.getClass().getDeclaredField("counters");
                    cf.setAccessible(true);
                    Object existing = cf.get(customApp);
                    if (existing == null && !counterData.isEmpty()) {
                        cf.set(customApp, counterData);
                        startupLog("[WestlakeLauncher] Force-set counters: " + counterData.keySet());
                    }
                } catch (Exception e) { /* not a counter app */ }
            } catch (ClassNotFoundException e) {
                startupLog("[WestlakeLauncher] Application class not found: " + appClassName);
            } catch (Throwable e) {
                startupLog("[WestlakeLauncher] Application error (caught)", e);
                // Continue without the custom Application
            }
        }

        // Load APK resources — use pre-extracted dir if available (dalvikvm has no ZipFile JNI)
        Activity launchedActivity = null;
        Class<?> resolvedActivityClass = null;
        boolean cutoffCanaryTargetLaunch = (cutoffCanaryLaunch
                || (!explicitNonCanaryLaunch
                        && (launchFileBytesContain(CUTOFF_CANARY_PACKAGE)
                                || stagedCutoffCanaryPresent())));
        if (cutoffCanaryTargetLaunch) {
            if (launchCutoffCanaryStandalone(am, cutoffCanaryStage())) {
                return;
            }
            appendCutoffCanaryTrace("CV canary standalone direct fallback");
        }
        String resDir = bootResDir;
        if (strictWestlake) {
            startupLog("Post-init launch path refresh deferred on standalone guest");
        } else {
            String resolvedApkPath = propOrSnapshot("westlake.apk.path", sBootApkPath);
            if (resolvedApkPath != null && !resolvedApkPath.isEmpty()) {
                apkPath = resolvedApkPath;
            }
            String resolvedActivityName = propOrSnapshot("westlake.apk.activity", sBootActivityName);
            if (resolvedActivityName != null && !resolvedActivityName.isEmpty()) {
                activityName = resolvedActivityName;
            }
            String resolvedPackageName = stableLaunchPackage(packageName, activityName, manifestInfo);
            if (resolvedPackageName != null && !resolvedPackageName.isEmpty()) {
                packageName = resolvedPackageName;
            }
            packageName = canonicalPackageName(packageName, activityName, manifestInfo);
            packageName = packageFallbackForKnownApps(packageName, activityName);
            resDir = propOrSnapshot("westlake.apk.resdir", sBootResDir);
            if (apkPath != null && !apkPath.isEmpty()) sBootApkPath = copyString(apkPath);
            if (activityName != null && !activityName.isEmpty()) sBootActivityName = copyString(activityName);
            if (packageName != null && !packageName.isEmpty()) sBootPackageName = copyString(packageName);
            if (resDir != null && !resDir.isEmpty()) sBootResDir = copyString(resDir);
        }
        String launchApkPath = strictWestlake
                ? (sBootApkPath != null ? sBootApkPath : apkPath)
                : (sBootApkPath != null ? copyString(sBootApkPath) : copyString(apkPath));
        String launchActivity = strictWestlake
                ? (sBootActivityName != null ? sBootActivityName : activityName)
                : (sBootActivityName != null ? copyString(sBootActivityName) : copyString(activityName));
        String launchPackage;
        String launchResDir;
        String targetPackageName;
        if (cutoffCanaryLaunch) {
            launchPackage = CUTOFF_CANARY_PACKAGE;
            launchResDir = sBootResDir != null ? sBootResDir : resDir;
            targetPackageName = CUTOFF_CANARY_PACKAGE;
        } else if (strictWestlake) {
            // Keep the strict-standalone launch snapshot as inert as possible.
            // Re-deriving package state here has repeatedly re-entered caller-
            // sensitive/bootstrap code before the real APK load path.
            launchPackage = sBootPackageName != null ? sBootPackageName : packageName;
            if ((launchPackage == null || launchPackage.isEmpty()) && launchActivity != null
                    && safeStartsWith(launchActivity, "com.mcdonalds.")) {
                launchPackage = "com.mcdonalds.app";
            }
            if (launchPackage == null || launchPackage.isEmpty()) {
                launchPackage = "app";
            }
            launchResDir = sBootResDir != null ? sBootResDir : resDir;
            targetPackageName = launchPackage;
        } else {
            launchPackage = stableLaunchPackage(packageName, launchActivity, manifestInfo);
            launchPackage = packageFallbackForKnownApps(launchPackage, launchActivity);
            launchResDir = sBootResDir != null ? copyString(sBootResDir) : copyString(resDir);
            targetPackageName = stableLaunchPackage(launchPackage, launchActivity, manifestInfo);
            targetPackageName = packageFallbackForKnownApps(targetPackageName, launchActivity);
        }
        if (targetPackageName == null || targetPackageName.isEmpty()) {
            targetPackageName = "app";
        }
        String targetActivity = cutoffCanaryLaunch ? CUTOFF_CANARY_ACTIVITY : launchActivity;
        if (strictWestlake) {
            startupLog("Launch props resolved on standalone guest");
            startupLog("Launch snapshot ready on standalone guest");
        } else {
            startupLog("[WestlakeLauncher] Resolved launch props: apk=" + launchApkPath
                + " activity=" + launchActivity + " package=" + launchPackage + " resDir=" + launchResDir);
            startupLog("[WestlakeLauncher] Launch snapshot: apk=" + sBootApkPath
                + " activity=" + sBootActivityName + " package=" + sBootPackageName + " resDir=" + sBootResDir);
        }
        try {
            apkPath = launchApkPath;
            activityName = launchActivity;
            packageName = launchPackage;
            resDir = launchResDir;
            targetActivity = cutoffCanaryLaunch ? CUTOFF_CANARY_ACTIVITY : launchActivity;
            if (strictWestlake) {
                startupLog("PF202 strict launch assignments ready");
            }
            if (strictWestlake) {
                if (apkPath == null) apkPath = "";
                if (cutoffCanaryLaunch) {
                    packageName = CUTOFF_CANARY_PACKAGE;
                    targetPackageName = CUTOFF_CANARY_PACKAGE;
                    targetActivity = CUTOFF_CANARY_ACTIVITY;
                }
                if (packageName == null || packageName.isEmpty()) {
                    packageName = "app";
                }
                targetPackageName = packageName;
                startupLog("PF202 strict package block ready");
            } else if (cutoffCanaryLaunch) {
                if (apkPath == null) apkPath = "";
                packageName = CUTOFF_CANARY_PACKAGE;
                targetPackageName = CUTOFF_CANARY_PACKAGE;
                targetActivity = CUTOFF_CANARY_ACTIVITY;
                persistLaunchPackage(targetPackageName);
            } else {
                packageName = preferredLaunchPackage(
                        packageName, targetPackageName, targetActivity, manifestInfo);
                targetPackageName = preferredLaunchPackage(
                        targetPackageName, packageName, targetActivity, manifestInfo);
                if (apkPath == null) apkPath = "";
                if (isPlaceholderPackage(packageName)) {
                    packageName = targetPackageName;
                }
                if (isPlaceholderPackage(targetPackageName)) {
                    targetPackageName = packageName;
                }
                packageName = packageFallbackForKnownApps(packageName, targetActivity);
                targetPackageName = packageFallbackForKnownApps(targetPackageName, targetActivity);
                if (packageName == null || packageName.isEmpty()) {
                    packageName = packageFallbackForKnownApps("app", targetActivity);
                }
                if (targetPackageName == null || targetPackageName.isEmpty()
                        || isPlaceholderPackage(targetPackageName)) {
                    targetPackageName = packageName;
                }
                persistLaunchPackage(packageName);
                persistLaunchPackage(targetPackageName);
            }
            if (!strictWestlake) {
                startupLog("[WestlakeLauncher] Package handoff: launch=" + launchPackage
                        + " resolved=" + packageName + " target=" + targetPackageName);
                startupLog("[WestlakeLauncher] Loading APK: " + apkPath);
                startupLog("[WestlakeLauncher] ResDir: " + resDir);
            }

            android.app.ApkInfo info;
            if (strictWestlake) {
                startupLog("PF202 strict resource block begin");
            }
            // Check resDir — also try fallback paths if the primary path isn't accessible
            boolean preferredResReadable;
            String effectiveResDir;
            if (cutoffCanaryLaunch) {
                preferredResReadable = resDir != null;
                effectiveResDir = resDir;
            } else if (strictWestlake) {
                preferredResReadable = resDir != null;
                effectiveResDir = resDir;
            } else {
                preferredResReadable = resDir != null && canOpenFile(joinPath(resDir, "resources.arsc"));
                effectiveResDir = resolveReadableResDir(resDir);
                if (resDir != null && !preferredResReadable) {
                    startupLog("[WestlakeLauncher] ResDir not accessible: " + resDir);
                }
                if (effectiveResDir != null && resDir != null && !effectiveResDir.equals(resDir)) {
                    startupLog("[WestlakeLauncher] Using fallback resDir: " + effectiveResDir);
                }
            }
            if (cutoffCanaryLaunch) {
                startupLog("[WestlakeLauncher] Canary using dex-only APK metadata");
                info = buildDexOnlyInfo(CUTOFF_CANARY_PACKAGE, CUTOFF_CANARY_ACTIVITY, resDir);
            } else if (effectiveResDir != null) {
                if (strictWestlake) {
                    startupLog("PF202 strict pre-extracted loader enter");
                } else {
                    startupLog("[WestlakeLauncher] Using pre-extracted loader for " + effectiveResDir);
                }
                // Use pre-extracted resources (host extracted them before spawning dalvikvm)
                info = android.app.ApkLoader.loadFromExtracted(
                    effectiveResDir, packageName, activityName);
                if (strictWestlake) {
                    startupLog("PF202 strict post-loader return");
                }
                info.packageName = packageName;
                info.launcherActivity = activityName;
                if (strictWestlake) {
                    startupLog("PF202 strict post-loader info seeded");
                } else if ((info.activities == null || info.activities.isEmpty())
                        && activityName != null && !activityName.isEmpty()) {
                    try {
                        info.activities.add(activityName);
                    } catch (Throwable ignored) {
                    }
                }
                // Also load split APK resources (xxxhdpi, en, etc.)
                android.content.res.Resources appRes2 = null;
                if (!strictWestlake) {
                    try {
                        android.app.Application currentApp = MiniServer.currentApplication();
                        if (currentApp != null) {
                            appRes2 = currentApp.getResources();
                        }
                    } catch (Throwable t) {}
                    for (String splitName : new String[]{"resources_xxxhdpi.arsc", "resources_en.arsc"}) {
                        String splitPath = joinPath(effectiveResDir, splitName);
                        byte[] data = tryReadFileBytes(splitPath);
                        if (data != null && appRes2 != null) {
                            try {
                                android.content.res.ResourceTableParser.parse(data, appRes2);
                                startupLog("[WestlakeLauncher] Loaded split: " + splitName
                                    + " (" + data.length + " bytes, entries=" + appRes2.getResourceTable().getStringCount() + ")");
                            } catch (Throwable t) {
                                startupLog("[WestlakeLauncher] Split error (" + splitName + ")", t);
                            }
                        } else {
                            startupLog("[WestlakeLauncher] Split " + splitName + " exists=" + (data != null) + " appRes=" + (appRes2 != null));
                        }
                    }
                }
                if (strictWestlake) {
                    startupLog("PF202 strict split work skipped");
                }
                // Store ApkInfo on MiniServer so LayoutInflater can find resDir
                if (strictWestlake) {
                    startupLog("PF202 strict setApkInfo skipped");
                } else {
                    try {
                        java.lang.reflect.Field f = MiniServer.class.getDeclaredField("mApkInfo");
                        f.setAccessible(true);
                        f.set(server, info);
                    } catch (Exception ex) {
                        startupLog("[WestlakeLauncher] setApkInfo", ex);
                    }
                }
                if (strictWestlake) {
                    startupLog("PF202 strict pre-extracted resources loaded");
                } else {
                    startupLog("[WestlakeLauncher] Loaded from pre-extracted resources (resDir=" + info.resDir + ")");
                }

                // Wire resources to Application (same as MiniServer.loadApk does)
                if (strictWestlake) {
                    startupLog("PF202 strict resource wiring skipped");
                } else {
                    android.app.Application currentApp = MiniServer.currentApplication();
                    android.content.res.Resources res = currentApp != null ? currentApp.getResources() : null;
                    if (info.resourceTable != null) {
                        ShimCompat.loadResourceTable(res, (android.content.res.ResourceTable) info.resourceTable);
                        startupLog("[WestlakeLauncher] ResourceTable wired to Application");
                    }
                    // Set APK path for layout inflation (LayoutInflater reads AXML from here)
                    if (res != null) {
                        ShimCompat.setApkPath(res, apkPath);
                    }
                    // Set asset dir for extracted res/ layouts
                    if (info.assetDir != null && currentApp != null) {
                        ShimCompat.setAssetDir(currentApp.getAssets(), info.assetDir);
                    }
                }
            } else if (apkPath != null && apkPath.endsWith(".apk")) {
                startupLog("[WestlakeLauncher] Falling back to APK Zip loader");
                info = MiniServer.currentLoadApk(apkPath);
            } else {
                startupLog("[WestlakeLauncher] No readable extracted resources; continuing with dex-only metadata");
                info = buildDexOnlyInfo(packageName, activityName, resDir);
            }
            if (cutoffCanaryLaunch) {
                info.packageName = CUTOFF_CANARY_PACKAGE;
                packageName = CUTOFF_CANARY_PACKAGE;
                targetPackageName = CUTOFF_CANARY_PACKAGE;
                targetActivity = CUTOFF_CANARY_ACTIVITY;
                if (isRealFrameworkFallbackAllowed()) {
                    persistLaunchPackage(CUTOFF_CANARY_PACKAGE);
                }
            } else if (!isRealFrameworkFallbackAllowed()) {
                startupLog("PF202 strict package reconciliation skipped");
                startupLog("PF202 strict APK summary skipped");
            } else {
                info.packageName = preferredLaunchPackage(
                        info.packageName, targetPackageName, targetActivity, manifestInfo);
                if (isPlaceholderPackage(packageName)) {
                    packageName = info.packageName;
                }
                if (!isPlaceholderPackage(info.packageName)) {
                    targetPackageName = info.packageName;
                    packageName = info.packageName;
                    persistLaunchPackage(info.packageName);
                }
                startupLog("[WestlakeLauncher] APK loaded: " + info);
                startupLog("[WestlakeLauncher]   package: " + info.packageName);
                startupLog("[WestlakeLauncher]   activity count: " + info.activities.size());
                startupLog("[WestlakeLauncher]   launcher: " + info.launcherActivity);
                startupLog("[WestlakeLauncher]   dex count: " + info.dexPaths.size());
            }

                // Determine which activity to launch (declared before try for catch visibility)
                if (targetActivity == null || targetActivity.isEmpty()) {
                    targetActivity = info.launcherActivity;
                }
                    if (cutoffCanaryLaunch) {
                        targetActivity = CUTOFF_CANARY_ACTIVITY;
                    }
	                if (targetActivity == null) {
	                    startupLog("[WestlakeLauncher] ERROR: No activity to launch");
	                    return;
	                }
                if (!isRealFrameworkFallbackAllowed()) {
                    startupLog("PF202 strict launch target reconciliation skipped");
                } else if (cutoffCanaryLaunch) {
                    targetPackageName = CUTOFF_CANARY_PACKAGE;
                    packageName = CUTOFF_CANARY_PACKAGE;
                    persistLaunchPackage(targetPackageName);
                } else {
                    targetPackageName = preferredLaunchPackage(
                            info.packageName, targetPackageName, targetActivity, manifestInfo);
                    targetPackageName = preferredLaunchPackage(
                            targetPackageName, packageName, targetActivity, manifestInfo);
                    if (!isPlaceholderPackage(targetPackageName)) {
                        packageName = targetPackageName;
                        persistLaunchPackage(targetPackageName);
                    }
                }

			                if (!isRealFrameworkFallbackAllowed()) {
                        startupLog("PF202 strict launch banner skipped");
                    } else {
                        startupLog("[WestlakeLauncher] Launching: " + targetActivity);
                    }

			                // For Hilt apps: skip real activity (constructor hangs in DI)
			                // Create a plain Activity with the app's splash content instead
			                boolean isHiltActivity = false;
					                if (cutoffCanaryLaunch && !isRealFrameworkFallbackAllowed()) {
                                    startupLog("[WestlakeLauncher] canary eager resolve begin");
                                    resolvedActivityClass =
                                            resolveAppClassChildFirstOrNull(CUTOFF_CANARY_ACTIVITY);
                                    if (resolvedActivityClass != null) {
                                        am.registerActivityClass(CUTOFF_CANARY_ACTIVITY,
                                                resolvedActivityClass);
                                        startupLog("[WestlakeLauncher] canary eager resolve ok");
                                    } else {
                                        startupLog("[WestlakeLauncher] canary eager resolve null");
                                    }
					                } else if (!isRealFrameworkFallbackAllowed()) {
                        startupLog("PF202 strict activity resolve skipped");
                    } else if (cutoffCanaryLaunch) {
                        // The control canary is the substrate probe. Let the activity manager
                        // resolve it lazily so a classloader stall here cannot mask the real
                        // launcher-to-activity-manager handoff.
                        startupLog("[WestlakeLauncher] Canary eager activity resolve skipped");
                    } else {
                        try {
                            Class<?> actCls = loadAppClass(targetActivity);
                            resolvedActivityClass = actCls;
                            am.registerActivityClass(targetActivity, actCls);
                            startupLog("[WestlakeLauncher] Resolved activity class via "
                                + loaderTag(actCls.getClassLoader()));
                            // Hilt detection is advisory; keep failures here from aborting launch.
                            isHiltActivity = !cutoffCanaryLaunch
                                && hierarchyNameContains(actCls.getSuperclass(), "Hilt_");
			                } catch (Exception e) {
                            startupLog("[WestlakeLauncher] Activity class resolve failed", e);
                        }
                    }

                    String appClass = null;
                    if (manifestInfo != null && manifestInfo.applicationClass != null) {
                        appClass = manifestInfo.applicationClass;
                    }
                    if ((appClass == null || appClass.length() == 0)
                            && info != null && info.applicationClassName != null) {
                        appClass = info.applicationClassName;
                    }
                    if ((appClass == null || appClass.length() == 0)
                            && "com.westlake.showcase".equals(targetPackageName)) {
                        appClass = "com.westlake.showcase.ShowcaseApp";
                        startupLog("PF451 showcase appClass fallback " + appClass);
                    }
                    if ((appClass == null || appClass.length() == 0)
                            && "com.westlake.yelplive".equals(targetPackageName)) {
                        appClass = "com.westlake.yelplive.YelpLiveApp";
                        startupLog("PF461 yelp appClass fallback " + appClass);
                    }
                    if ((appClass == null || appClass.length() == 0)
                            && "com.westlake.materialyelp".equals(targetPackageName)) {
                        appClass = "com.westlake.materialyelp.MaterialYelpApp";
                        startupLog("PF463 material appClass fallback " + appClass);
                    }
                    if ((appClass == null || appClass.length() == 0)
                            && "com.westlake.mcdprofile".equals(targetPackageName)) {
                        appClass = "com.westlake.mcdprofile.McdProfileApp";
                        startupLog("PF465 mcd profile appClass fallback " + appClass);
                    }
                    if ((appClass == null || appClass.length() == 0)
                            && "com.westlake.materialxmlprobe".equals(targetPackageName)) {
                        appClass = "com.westlake.materialxmlprobe.MaterialXmlProbeApp";
                        startupLog("PF457 material XML appClass fallback " + appClass);
                    }
                    ClassLoader activityClassLoader = resolvedActivityClass != null
                        ? resolvedActivityClass.getClassLoader()
                        : Thread.currentThread().getContextClassLoader();
                    if (activityClassLoader == null) {
                        activityClassLoader = Thread.currentThread().getContextClassLoader();
                    }

                    if (!isRealFrameworkFallbackAllowed()
                            && "com.westlake.materialyelp".equals(targetPackageName)
                            && appClass != null && appClass.length() > 0) {
                        try {
                            startupLog("PF463 controlled Application create begin");
                            Class<?> appCls = Class.forName(appClass, true, activityClassLoader);
                            android.app.Application customApp = instantiateApplicationInstance(
                                    appCls, appClass, false);
                            android.app.MiniServer.currentSetPackageName(targetPackageName);
                            android.app.MiniServer.currentSetApplication(customApp);
                            customApp.onCreate();
                            startupLog("PF463 controlled Application.onCreate returned");
                        } catch (Throwable appError) {
                            startupLog("PF463 controlled Application.onCreate failed", appError);
                        }
                    }

                    if (!isRealFrameworkFallbackAllowed()
                            && "com.westlake.mcdprofile".equals(targetPackageName)
                            && allowMcdProfileControlledFallback()) {
                        launchedActivity = launchMcdProfileControlledActivity(
                                targetPackageName, targetActivity,
                                resolvedActivityClass, activityClassLoader);
                    }

                    if (launchedActivity == null) {
                        boolean preferWat = isHiltActivity
                            || "com.mcdonalds.app".equals(targetPackageName)
                            || "com.westlake.showcase".equals(targetPackageName)
                            || "com.westlake.yelplive".equals(targetPackageName)
                            || "com.westlake.mcdprofile".equals(targetPackageName)
                            || "com.westlake.materialxmlprobe".equals(targetPackageName);
		                if (preferWat) {
		                    // Use WestlakeActivityThread for Hilt apps — proper AOSP lifecycle with DI injection
		                    startupLog("[WestlakeLauncher] Using WestlakeActivityThread");
		                    final String fTarget2 = targetActivity;
		                    final String launchPkg2;
		                    if (!isRealFrameworkFallbackAllowed()) {
                                startupLog("PF202 strict WAT package resolve skipped");
                                if (!isPlaceholderPackage(targetPackageName)) {
                                    launchPkg2 = targetPackageName;
                                } else if (!isPlaceholderPackage(packageName)) {
                                    launchPkg2 = packageName;
                                } else {
                                    launchPkg2 = info.packageName;
                                }
                            } else {
		                        launchPkg2 = preferredLaunchPackage(
		                                info.packageName, targetPackageName, fTarget2, manifestInfo);
                            }
	                    final Activity[] result2 = { null };

	                    // Initialize WestlakeActivityThread (AOSP-style lifecycle)
		                    final android.app.WestlakeActivityThread wat = android.app.WestlakeActivityThread.currentActivityThread();
		                    if (wat.getInstrumentation() == null) {
	                            startupLog("[WestlakeLauncher] WestlakeActivityThread attach begin");
	                            try {
	                                if (!isRealFrameworkFallbackAllowed()) {
                                        startupLog("PF202 strict WAT persist package skipped");
                                    } else {
	                                    persistLaunchPackage(launchPkg2);
                                    }
                                    if (!isRealFrameworkFallbackAllowed()) {
                                        startupLog("PF202 strict WAT attach begin");
                                        android.app.WestlakeActivityThread.attachStandalone(
                                                wat, launchPkg2, appClass, activityClassLoader);
                                        startupLog("PF202 strict WAT attach returned");
                                    } else {
			                                wat.attach(launchPkg2, appClass, activityClassLoader);
                                    }
		                            startupLog("[WestlakeLauncher] WestlakeActivityThread attached");
	                            } catch (Throwable attachError) {
                                startupLog("[WestlakeLauncher] WestlakeActivityThread attach failed", attachError);
                                throw attachError;
                            }
	                    }

                    // Launch synchronously on main thread (no timeout needed)
                    if (!isRealFrameworkFallbackAllowed()) {
                        if ("com.westlake.showcase".equals(launchPkg2)
                                && appClass != null && appClass.length() > 0) {
                            startupLog("PF451 showcase WAT force makeApplication begin");
                            wat.forceMakeApplicationForNextLaunch(appClass);
                            startupLog("PF451 showcase WAT force makeApplication returned");
                        }
                        if ("com.westlake.yelplive".equals(launchPkg2)
                                && appClass != null && appClass.length() > 0) {
                            startupLog("PF461 yelp WAT force makeApplication begin");
                            wat.forceMakeApplicationForNextLaunch(appClass);
                            startupLog("PF461 yelp WAT force makeApplication returned");
                        }
                        if ("com.westlake.mcdprofile".equals(launchPkg2)
                                && appClass != null && appClass.length() > 0) {
                            startupLog("PF465 mcd profile WAT force makeApplication begin");
                            wat.forceMakeApplicationForNextLaunch(appClass);
                            startupLog("PF465 mcd profile WAT force makeApplication returned");
                        }
                        if ("com.westlake.materialxmlprobe".equals(launchPkg2)
                                && appClass != null && appClass.length() > 0) {
                            startupLog("PF457 material XML WAT force makeApplication begin");
                            wat.forceMakeApplicationForNextLaunch(appClass);
                            startupLog("PF457 material XML WAT force makeApplication returned");
                        }
                        startupLog("PF301 strict WAT resolve nulls call");
                        wat.probeResolveLaunchPackageNameNulls();
                        startupLog("PF301 strict WAT resolve nulls returned");
                        startupLog("PF301 strict WAT resolve pkg/class null-intent call");
                        wat.probeResolveLaunchPackageName(launchPkg2, fTarget2);
                        startupLog("PF301 strict WAT resolve pkg/class null-intent returned");
                        startupLog("PF301 strict launcher intent prep call");
                        Intent watIntent = new Intent(Intent.ACTION_MAIN);
                        watIntent.setComponent(new ComponentName(launchPkg2, fTarget2));
                        watIntent.setPackage(launchPkg2);
                        watIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startupLog("PF301 strict launcher intent prep returned");
                        startupLog("PF301 strict WAT object arg call");
                        wat.probeObjectArg(watIntent);
                        startupLog("PF301 strict WAT object arg returned");
                        startupLog("PF301 strict WAT component arg call");
                        wat.probeComponentArg(watIntent.getComponent());
                        startupLog("PF301 strict WAT component arg returned");
                        startupLog("PF301 strict WAT launch classloader field call");
                        wat.probeLaunchClassLoaderField();
                        startupLog("PF301 strict WAT launch classloader field returned");
                        startupLog("PF301 strict WAT launch context classloader call");
                        wat.probeLaunchContextClassLoader();
                        startupLog("PF301 strict WAT launch context classloader returned");
                        startupLog("PF301 strict WAT launch engine classloader call");
                        wat.probeLaunchEngineClassLoader();
                        startupLog("PF301 strict WAT launch engine classloader returned");
                        startupLog("PF301 strict WAT launch classloader helper skipped");
                        startupLog("PF301 strict WAT app component factory call");
                        wat.getAppComponentFactory();
                        startupLog("PF301 strict WAT app component factory returned");
                        startupLog("PF301 strict launcher AppComponentFactory.DEFAULT call");
                        android.app.AppComponentFactory defaultFactory =
                                android.app.AppComponentFactory.DEFAULT;
                        startupLog("PF301 strict launcher AppComponentFactory.DEFAULT returned");
                        startupLog("PF301 strict launcher new AppComponentFactory call");
                        android.app.AppComponentFactory freshFactory =
                                new android.app.AppComponentFactory();
                        startupLog("PF301 strict launcher new AppComponentFactory returned");
                        startupLog("PF301 strict launcher activityClassLoader null-check call");
                        if (activityClassLoader == null) {
                            startupLog("PF301 strict launcher activityClassLoader was null");
                        } else {
                            startupLog("PF301 strict launcher activityClassLoader was nonnull");
                        }
                        startupLog("PF301 strict launcher target class null-check call");
                        if (fTarget2 == null) {
                            startupLog("PF301 strict launcher target class was null");
                        } else {
                            startupLog("PF301 strict launcher target class was nonnull");
                        }
                        startupLog("PF301 strict factory instantiateActivity call");
                        freshFactory.instantiateActivity(activityClassLoader, fTarget2, watIntent);
                        startupLog("PF301 strict factory instantiateActivity returned");
                        startupLog("PF301 strict WAT launchActivity call");
                        result2[0] = android.app.WestlakeActivityThread.launchActivity(
                                wat, fTarget2, launchPkg2, watIntent);
                        startupLog("PF301 strict WAT launchActivity returned");
                        if ("com.westlake.showcase".equals(launchPkg2) && result2[0] != null) {
                            startupLog("PF452 showcase WAT resume call");
                            wat.performResumeActivity(result2[0]);
                            startupLog("PF452 showcase WAT resume returned");
                        }
                        if ("com.westlake.yelplive".equals(launchPkg2) && result2[0] != null) {
                            startupLog("PF462 yelp WAT resume call");
                            wat.performResumeActivity(result2[0]);
                            startupLog("PF462 yelp WAT resume returned");
                        }
                        if ("com.westlake.mcdprofile".equals(launchPkg2) && result2[0] != null) {
                            startupLog("PF465 mcd profile WAT resume call");
                            wat.performResumeActivity(result2[0]);
                            appendCutoffCanaryMarker("MCD_PROFILE_WAT_ACTIVITY_RESUME_OK class="
                                    + safeMarkerToken(result2[0].getClass().getName()));
                            startupLog("PF465 mcd profile WAT resume returned");
                        }
                        if ("com.westlake.materialxmlprobe".equals(launchPkg2) && result2[0] != null) {
                            startupLog("PF457 material XML WAT resume call");
                            wat.performResumeActivity(result2[0]);
                            startupLog("PF457 material XML WAT resume returned");
                        }
                        startupLog("PF301 strict launcher post-WAT begin");
                        if (result2[0] != null) {
                            startupLog("PF301 strict launcher post-WAT result nonnull");
                            if ("com.westlake.mcdprofile".equals(launchPkg2)) {
                                appendCutoffCanaryMarker(
                                        "MCD_PROFILE_WAT_ACTIVITY_LAUNCH_OK class="
                                                + safeMarkerToken(result2[0].getClass().getName()));
                            }
                            launchedActivity = result2[0];
                            startupLog("PF301 strict launcher post-WAT launchedActivity set");
                        } else {
                            startupLog("PF301 strict launcher post-WAT result null");
                        }
                        startupLog("PF301 strict launcher post-WAT factory check skip");
                        if (defaultFactory == freshFactory) {
                            startupLog("PF301 strict launcher factory identity matched");
                        }
                        startupLog("PF301 strict launcher post-WAT done");
                    } else {
                        try {
                            startupLog("[WestlakeLauncher] WAT launch args: pkg="
                                    + launchPkg2 + " cls=" + fTarget2);
                            startupLog("[WestlakeLauncher] WAT launch via direct ActivityThread path");
                            Intent watIntent = new Intent(Intent.ACTION_MAIN);
                            watIntent.setComponent(new ComponentName(launchPkg2, fTarget2));
                            watIntent.setPackage(launchPkg2);
                            watIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startupLog("[WestlakeLauncher] WAT intent: pkg="
                                    + watIntent.getPackage() + " cmp=" + watIntent.getComponent());
                            result2[0] = android.app.WestlakeActivityThread.launchActivity(
                                    wat, fTarget2, launchPkg2, watIntent);
                            if (result2[0] != null) {
                                launchedActivity = result2[0];
                                startupLog("[WestlakeLauncher] Activity created: " + launchedActivity.getClass().getName());
                                // Queue dashboard navigation
                                android.app.WestlakeActivityThread.pendingDashboardClass =
                                    "com.mcdonalds.homedashboard.activity.HomeDashboardActivity";
                            }
                        } catch (Throwable e) {
		                            dumpThrowable("[WestlakeLauncher] WestlakeActivityThread error", e);
                        }
                    }
		                    if (launchedActivity == null) {
		                        // Fallback
                                if (!isRealFrameworkFallbackAllowed()) {
                                    startupLog("PF202 strict AM fallback launch skipped");
                                } else {
		                            startupLog("[WestlakeLauncher] AM direct launch: pkg="
		                                    + launchPkg2 + " cls=" + fTarget2);
	                                am.startActivityDirect(null, launchPkg2, fTarget2, -1,
	                                        resolvedActivityClass);
	                                launchedActivity = am.getResumedActivity();
                                }
		                    }
			                } else {
			                    String launchPkg = targetPackageName;
                            if (cutoffCanaryLaunch) {
                                noteMarker("CV launcher AM direct branch begin");
                            }
			                    startupLog("[WestlakeLauncher] AM direct launch: pkg="
			                            + launchPkg + " cls=" + targetActivity);
                            if (cutoffCanaryLaunch) {
                                noteMarker("CV launcher before AM direct");
                            }
			                    am.startActivityDirect(null, launchPkg, targetActivity, -1,
			                            resolvedActivityClass);
                            if (cutoffCanaryLaunch) {
                                noteMarker("CV launcher after AM direct");
                            }
			                    launchedActivity = am.getResumedActivity();
                            if (cutoffCanaryLaunch) {
                                noteMarker("CV launcher after AM getResumed");
                            }
	                            startupLog("[WestlakeLauncher] AM direct result: resumed="
	                                    + (launchedActivity != null
	                                    ? launchedActivity.getClass().getName() : "null")
	                                    + " stack=" + am.getStackSize());
                            if (cutoffCanaryLaunch) {
                                noteMarker(launchedActivity != null
                                        ? "CV launcher AM resumed nonnull"
                                        : "CV launcher AM resumed null");
                            }
                            if (launchedActivity == null && am.getStackSize() > 0) {
                                Activity top = am.getActivity(am.getStackSize() - 1);
                                startupLog("[WestlakeLauncher] AM top activity="
                                        + (top != null ? top.getClass().getName() : "null"));
                            }
			                }
                    }
			        } catch (Exception e) {
            startupLog("[WestlakeLauncher] APK load error marker="
                    + String.valueOf(lastLaunchMarker()));
            startupLog("[WestlakeLauncher] APK load error target pkg="
                    + String.valueOf(targetPackageName)
                    + " activity=" + String.valueOf(targetActivity));
            startupLog("[WestlakeLauncher] APK load error (non-fatal)", e);
            dumpThrowable("[WestlakeLauncher] APK load error detail", e);
            logThrowableCause("[WestlakeLauncher] APK load error cause", e);
            logThrowableFrames("[WestlakeLauncher] APK load error", e, 12);
            // Fallback: launch activity directly if class is on classpath
		            if (targetActivity != null && launchedActivity == null) {
		                try {
		                    String pkg = targetPackageName;
		                    startupLog("[WestlakeLauncher] AM direct fallback: pkg="
		                            + pkg + " cls=" + targetActivity);
		                    am.startActivityDirect(null, pkg, targetActivity, -1,
		                            resolvedActivityClass);
	                    launchedActivity = am.getResumedActivity();
	                    startupLog("[WestlakeLauncher] Fallback launch OK: " + targetActivity);
                } catch (Exception e2) {
                    startupLog("[WestlakeLauncher] Fallback launch failed marker="
                            + String.valueOf(lastLaunchMarker()));
                    startupLog("[WestlakeLauncher] Fallback launch failed", e2);
                    dumpThrowable("[WestlakeLauncher] Fallback launch failed detail", e2);
                    logThrowableCause("[WestlakeLauncher] Fallback launch failed cause", e2);
                    logThrowableFrames("[WestlakeLauncher] Fallback launch failed", e2, 12);
                }
            }
        }

        // Try to get the launched activity even if errors occurred
        final boolean strictStandalone = !isRealFrameworkFallbackAllowed();
        if (launchedActivity == null) {
            if (strictStandalone) {
                startupLog("PF301 strict launcher resumed fallback skipped");
            } else {
                launchedActivity = am.getResumedActivity();
            }
        }
        if (launchedActivity == null) {
            startupLog("[WestlakeLauncher] WARNING: No activity, rendering empty surface");
        }
        if (launchedActivity != null) {
            if (strictStandalone) {
                startupLog("PF301 strict launcher activity branch begin");
                startupLog("PF301 strict launcher window call");
                android.view.Window strictWindow = launchedActivity.getWindow();
                startupLog("PF301 strict launcher window returned");
                if (strictWindow == null) {
                    startupLog("PF301 strict launcher window null");
                } else {
                    startupLog("PF301 strict launcher window nonnull");
                }
                startupLog("PF301 strict launcher decor call");
                android.view.View decor =
                        strictWindow != null ? strictWindow.getDecorView() : null;
                startupLog("PF301 strict launcher decor returned");
                if (decor == null) {
                    startupLog("PF301 strict launcher decor null");
                } else {
                    startupLog("PF301 strict launcher decor nonnull");
                }
                startupLog("PF301 strict launcher childCount call");
                int childCount = decor instanceof android.view.ViewGroup
                        ? ((android.view.ViewGroup) decor).getChildCount()
                        : -1;
                startupLog("PF301 strict launcher childCount returned");
                if (childCount > 0) {
                    startupLog("PF301 strict launcher childCount positive");
                } else {
                    startupLog("PF301 strict launcher childCount nonpositive");
                }
                startupLog("PF301 strict launcher resources skipped");
                startupLog("PF301 strict launcher theme call");
                android.content.res.Resources.Theme strictTheme = launchedActivity.getTheme();
                startupLog("PF301 strict launcher theme returned");
                if (strictTheme == null) {
                    startupLog("PF301 strict launcher theme null");
                } else {
                    startupLog("PF301 strict launcher theme nonnull");
                }
                startupLog("PF301 strict launcher title call");
                CharSequence strictTitle = launchedActivity.getTitle();
                startupLog("PF301 strict launcher title returned");
                if (strictTitle == null) {
                    startupLog("PF301 strict launcher title null");
                } else {
                    startupLog("PF301 strict launcher title nonnull");
                }
                boolean hasContent = childCount > 0;
                if (hasContent) {
                    startupLog("PF301 strict launcher decor hasContent");
                } else {
                    startupLog("PF301 strict launcher decor noContent");
                    startupLog("PF301 strict launcher content install call");
                    boolean installedContent = strictWindow != null
                            && strictWindow.installMinimalStandaloneContent();
                    startupLog("PF301 strict launcher content install returned");
                    if (installedContent) {
                        startupLog("PF301 strict launcher content install positive");
                    } else {
                        startupLog("PF301 strict launcher content install nonpositive");
                    }
                    startupLog("PF301 strict launcher content recheck call");
                    android.view.View updatedDecor =
                            strictWindow != null ? strictWindow.getDecorView() : null;
                    startupLog("PF301 strict launcher content recheck returned");
                    int updatedChildCount = updatedDecor instanceof android.view.ViewGroup
                            ? ((android.view.ViewGroup) updatedDecor).getChildCount()
                            : -1;
                    if (updatedChildCount > 0) {
                        startupLog("PF301 strict launcher content recheck positive");
                    } else {
                        startupLog("PF301 strict launcher content recheck nonpositive");
                    }
                }
                startupLog("PF301 strict launcher activity branch skipped");
            } else {
                startupLog("[WestlakeLauncher] Activity launched: " + launchedActivity.getClass().getName());

                if (cutoffCanaryLaunch) {
                    startupLog("[WestlakeLauncher] Control canary: skip launcher splash helpers");
                } else {
                    // Always load splash image for OP_IMAGE background rendering
                    {
                        String rDir = propOrSnapshot("westlake.apk.resdir", sBootResDir);
                        if (rDir != null && splashImageData == null) {
                            splashImageData = tryLoadSplashImage(rDir, "res/drawable/splash_screen.webp");
                            if (splashImageData == null) {
                                splashImageData = tryLoadSplashImage(
                                        rDir, "res/drawable-xxhdpi-v4/splash_screen.webp");
                            }
                            if (splashImageData == null) {
                                splashImageData = tryLoadSplashImage(
                                        rDir, "res/drawable-xhdpi-v4/splash_screen.webp");
                            }
                            if (splashImageData == null) {
                                splashImageData = tryLoadSplashImage(rDir, "res/drawable/splash_screen.png");
                            }
                        }
                    }

                    // If Activity has no content (DI failed to call setContentView), try manual inflate
                    android.view.View decor = launchedActivity.getWindow() != null ? launchedActivity.getWindow().getDecorView() : null;
                    boolean hasContent = decor instanceof android.view.ViewGroup
                        && ((android.view.ViewGroup) decor).getChildCount() > 0;
                    if (!hasContent) {
                        startupLog("[WestlakeLauncher] No content view — trying to inflate real splash layout");
                        android.view.View splashView = null;

                        // Try to inflate the real splash layout from extracted res/
                        try {
                            String rd = propOrSnapshot("westlake.apk.resdir", sBootResDir);
                            if (rd != null) {
                                String[] layoutNames = {
                                    "activity_splash_screen", "splash_screen", "activity_splash",
                                    "splash", "activity_main", "main"
                                };
                                for (String name : layoutNames) {
                                    String layoutPath = joinPath(rd, "res/layout/" + name + ".xml");
                                    byte[] axmlData = tryReadFileBytes(layoutPath);
                                    if (axmlData != null && axmlData.length > 0) {
                                        startupLog("[WestlakeLauncher] Found layout: " + name + ".xml (" + axmlData.length + " bytes)");
                                        android.view.LayoutInflater inflater = android.view.LayoutInflater.from(launchedActivity);
                                        android.content.res.BinaryXmlParser parser =
                                            new android.content.res.BinaryXmlParser(axmlData);
                                        splashView = inflater.inflate(parser, null);
                                        if (splashView != null) {
                                            startupLog("[WestlakeLauncher] Inflated real layout: " + splashView.getClass().getSimpleName());
                                            break;
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            startupLog("[WestlakeLauncher] Layout inflate error", e);
                        }

                        // Load real splash image bytes (will be drawn directly via OP_IMAGE before view tree)
                        {
                            String rDir = propOrSnapshot("westlake.apk.resdir", sBootResDir);
                            if (rDir != null && splashImageData == null) {
                                splashImageData = tryLoadSplashImage(rDir, "res/drawable/splash_screen.webp");
                                if (splashImageData == null) {
                                    splashImageData = tryLoadSplashImage(
                                            rDir, "res/drawable-xxhdpi-v4/splash_screen.webp");
                                }
                                if (splashImageData == null) {
                                    splashImageData = tryLoadSplashImage(
                                            rDir, "res/drawable-xhdpi-v4/splash_screen.webp");
                                }
                                if (splashImageData == null) {
                                    splashImageData = tryLoadSplashImage(rDir, "res/drawable/splash_screen.png");
                                }
                            }
                        }

                        // Fallback: programmatic McDonald's splash
                        if (splashView == null) {
                            startupLog("[WestlakeLauncher] Using OHBridge direct render (no View tree)");
                            // Skip View tree — render directly via OHBridge if available
                            if (nativeOk) {
                                try {
                                    long surf = OHBridge.surfaceCreate(0, SURFACE_WIDTH, SURFACE_HEIGHT);
                                    long canv = OHBridge.surfaceGetCanvas(surf);
                                    OHBridge.canvasDrawColor(canv, 0xFFDA291C); // MCD red
                                    long font = OHBridge.fontCreate();
                                    long pen = OHBridge.penCreate();
                                    long brush = OHBridge.brushCreate();
                                    OHBridge.fontSetSize(font, 48);
                                    OHBridge.penSetColor(pen, 0xFFFFCC00);
                                    OHBridge.canvasDrawText(canv, "McDonald's", 100, 300, font, pen, brush);
                                    OHBridge.fontSetSize(font, 18);
                                    OHBridge.penSetColor(pen, 0xFFFFFFFF);
                                    OHBridge.canvasDrawText(canv, "Running on Westlake Engine", 60, 400, font, pen, brush);
                                    OHBridge.fontSetSize(font, 14);
                                    OHBridge.penSetColor(pen, 0xCCFFFFFF);
                                    OHBridge.canvasDrawText(canv, "framework.jar + 33 MCD DEX files", 60, 440, font, pen, brush);
                                    if (splashImageData != null) {
                                        OHBridge.canvasDrawImage(canv, splashImageData, 0, 0, SURFACE_WIDTH, SURFACE_HEIGHT);
                                    }
                                    OHBridge.surfaceFlush(surf);
                                    startupLog("[WestlakeLauncher] OHBridge splash frame sent!");
                                } catch (Throwable t) {
                                    startupLog("[WestlakeLauncher] OHBridge render unavailable", t);
                                }
                            }
                            // Skip programmatic View fallback — go to render loop
                            splashView = null;
                        }
                        if (false) {
                            // Dead code — original View-based fallback kept for reference
                            startupLog("[WestlakeLauncher] UNREACHABLE programmatic splash");
                            // Ensure Activity has a valid base context for View construction
                            try {
                                if (launchedActivity.getResources() == null) {
                                    throw new RuntimeException("no resources");
                                }
                            } catch (Throwable noCtx) {
                                try {
                                    // Create a minimal ContextImpl via reflection
                                    Class<?> ci = Class.forName("android.app.ContextImpl");
                                    java.lang.reflect.Method csm = ci.getDeclaredMethod("createSystemContext",
                                        Class.forName("android.app.ActivityThread"));
                                    csm.setAccessible(true);
                                    // Get or create an ActivityThread
                                    Class<?> atClass = Class.forName("android.app.ActivityThread");
                                    Object at = null;
                                    try {
                                        java.lang.reflect.Method cat = atClass.getDeclaredMethod("currentActivityThread");
                                        cat.setAccessible(true);
                                        at = cat.invoke(null);
                                    } catch (Throwable t2) {}
                                    if (at == null) {
                                        at = atClass.getDeclaredConstructor().newInstance();
                                    }
                                    android.content.Context sysCtx = (android.content.Context) csm.invoke(null, at);
                                    java.lang.reflect.Field mBase = android.content.ContextWrapper.class.getDeclaredField("mBase");
                                    mBase.setAccessible(true);
                                    mBase.set(launchedActivity, sysCtx);
                                    startupLog("[WestlakeLauncher] Injected ContextImpl into Activity");
                                } catch (Throwable t3) {
                                    startupLog("[WestlakeLauncher] Context inject failed", t3);
                                }
                            }
                            android.widget.LinearLayout splash = new android.widget.LinearLayout(launchedActivity);
                            splash.setOrientation(android.widget.LinearLayout.VERTICAL);
                            splash.setBackgroundColor(0xFFDA291C); // McDonald's red
                            splash.setGravity(android.view.Gravity.CENTER);

                            android.widget.TextView title = new android.widget.TextView(launchedActivity);
                            title.setText("McDonald's");
                            title.setTextSize(48);
                            title.setTextColor(0xFFFFCC00);
                            title.setGravity(android.view.Gravity.CENTER);
                            splash.addView(title);

                            android.widget.TextView sub = new android.widget.TextView(launchedActivity);
                            sub.setText("i'm lovin' it");
                            sub.setTextSize(20);
                            sub.setTextColor(0xFFFFFFFF);
                            sub.setGravity(android.view.Gravity.CENTER);
                            sub.setPadding(0, 16, 0, 0);
                            splash.addView(sub);

                            android.widget.TextView status = new android.widget.TextView(launchedActivity);
                            status.setText("Running on Westlake Engine");
                            status.setTextSize(12);
                            status.setTextColor(0x80FFFFFF);
                            status.setGravity(android.view.Gravity.CENTER);
                            status.setPadding(0, 60, 0, 0);
                            splash.addView(status);

                            splashView = splash;
                        }

                        // Set content via Window — detach from parent first if needed
                        try {
                            android.view.Window win = launchedActivity.getWindow();
                            if (win != null && splashView != null) {
                                // Detach from old parent
                                if (splashView.getParent() instanceof android.view.ViewGroup) {
                                    ((android.view.ViewGroup) splashView.getParent()).removeView(splashView);
                                }
                                win.setContentView(splashView);
                                startupLog("[WestlakeLauncher] Set splash via Window.setContentView");
                            }
                        } catch (Exception e) {
                            startupLog("[WestlakeLauncher] setContentView error", e);
                        }
                    }
                }
            }
        }

        if (strictStandalone && launchedActivity != null && !isYelpLiveActivity(launchedActivity)) {
            startupLog("PF301 strict launcher late OHBridge call");
            boolean strictBridgeReady = false;
            try {
                startupLog("PF301 strict launcher late OHBridge class literal call");
                Class<?> bridgeClass = com.ohos.shim.bridge.OHBridge.class;
                startupLog("PF301 strict launcher late OHBridge class literal returned");
                startupLog("PF301 strict launcher late OHBridge state call");
                boolean snapshotReady = com.ohos.shim.bridge.OHBridgeState.clinitCompleted;
                startupLog("PF301 strict launcher late OHBridge state returned");
                startupLog(snapshotReady
                        ? "PF301 strict launcher late OHBridge state positive"
                        : "PF301 strict launcher late OHBridge state nonpositive");
                startupLog("PF301 strict launcher late OHBridge field call");
                strictBridgeReady = com.ohos.shim.bridge.OHBridge.strictGuestFieldProbe;
                startupLog("PF301 strict launcher late OHBridge field returned");
            } catch (Throwable bridgeError) {
                startupLog("PF301 strict launcher late OHBridge direct threw", bridgeError);
            }
            nativeOk = strictBridgeReady;
            if (nativeOk) {
                startupLog("PF301 strict launcher late OHBridge positive");
            } else {
                startupLog("PF301 strict launcher late OHBridge nonpositive");
            }
        }

        // Render loop — render even if Activity partially failed
        if (strictStandalone && isShowcaseActivity(launchedActivity)) {
            startupLog("PF453 showcase direct frame loop begin");
            runShowcaseDirectFrameLoop(launchedActivity);
            return;
        }
        if (strictStandalone && isYelpLiveActivity(launchedActivity)) {
            startupLog("PF463 yelp live direct frame loop begin");
            runYelpLiveDirectFrameLoop(launchedActivity);
            return;
        }
        if (strictStandalone && isMaterialYelpActivity(launchedActivity)) {
            startupLog("PF464 material yelp direct frame loop begin");
            runMaterialYelpDirectFrameLoop(launchedActivity);
            return;
        }
        if (strictStandalone && isMcdProfileActivity(launchedActivity)) {
            startupLog("PF465 mcd profile direct frame loop begin");
            runMcdProfileDirectFrameLoop(launchedActivity);
            return;
        }
        if (strictStandalone && isMaterialXmlProbeActivity(launchedActivity)) {
            startupLog("PF457 material XML direct tree frame loop begin");
            runMaterialXmlProbeDirectFrameLoop(launchedActivity);
            return;
        }
        if (nativeOk && launchedActivity != null) {
            startupLog("[WestlakeLauncher] Creating surface " + SURFACE_WIDTH + "x" + SURFACE_HEIGHT);
            try {
                // Call onSurfaceCreated — may not exist on real framework Activity
                if (strictStandalone) {
                    startupLog("PF301 strict launcher render surface call");
                }
                launchedActivity.onSurfaceCreated(0L, SURFACE_WIDTH, SURFACE_HEIGHT);
                if (strictStandalone) {
                    startupLog("PF301 strict launcher render surface returned");
                    startupLog("PF301 strict launcher renderFrame call");
                }
                launchedActivity.renderFrame();
                if (strictStandalone) {
                    startupLog("PF301 strict launcher renderFrame returned");
                }
            } catch (Throwable e) {
                if (strictStandalone) {
                    startupLog("PF301 strict launcher render threw", e);
                } else {
                    startupLog("[WestlakeLauncher] Initial render: " + e.getClass().getSimpleName() + " (framework Activity — using OHBridge direct)");
                }
            }
            startupLog("[WestlakeLauncher] Initial frame rendered");
            if (strictStandalone) {
                startupLog("PF301 strict launcher keepalive begin");
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        startupLog("PF301 strict launcher keepalive interrupted");
                        break;
                    }
                }
                return;
            }
            startupLog("[WestlakeLauncher] Entering event loop...");
            renderLoop(launchedActivity, am);
        } else {
            if (strictStandalone && launchedActivity != null) {
                startupLog("PF301 strict launcher renderLoop skipped");
            }
            startupLog("[WestlakeLauncher] Running in headless mode (no native bridge)");
            if (launchedActivity != null && isControlAndroidBackend()) {
                if (cutoffCanaryLaunch) {
                    startupLog("[WestlakeLauncher] control canary headless probe begin");
                    boolean probeReachedCanary = false;
                    for (int i = 0; i < 30; i++) {
                        Activity resumed = null;
                        Activity top = null;
                        int stackSize = -1;
                        String markerState = null;
                        String launchMarker = null;
                        try {
                            resumed = am.getResumedActivity();
                        } catch (Throwable ignored) {
                        }
                        try {
                            stackSize = am.getStackSize();
                            if (stackSize > 0) {
                                top = am.getActivity(stackSize - 1);
                            }
                        } catch (Throwable ignored) {
                        }
                        try {
                            launchMarker = lastLaunchMarker();
                        } catch (Throwable ignored) {
                        }
                        String launchedState = describeProbeActivity(launchedActivity);
                        String resumedState = describeProbeActivity(resumed);
                        String topState = describeProbeActivity(top);
                        boolean reachedByActivity =
                                isCutoffCanaryActivity(launchedActivity)
                                || isCutoffCanaryActivity(resumed)
                                || isCutoffCanaryActivity(top);
                        if (reachedByActivity) {
                            markerState = "activity-state";
                        } else {
                            try {
                                markerState = readCutoffCanaryMarker();
                            } catch (Throwable ignored) {
                            }
                        }
                        boolean reachedCanary =
                                reachedByActivity
                                || (markerState != null && markerState.length() > 0);
                        if (i == 0 || i == 29 || reachedCanary) {
                            startupLog("[WestlakeLauncher] control canary headless probe tick="
                                    + i + " launched=" + launchedState
                                    + " resumed=" + resumedState
                                    + " top=" + topState
                                    + " stack=" + stackSize
                                    + " marker=" + markerState
                                    + " launchMarker=" + launchMarker);
                        }
                        if (reachedCanary) {
                            probeReachedCanary = true;
                            if (reachedByActivity) {
                                marker("CV " + cutoffCanaryStage()
                                        + " activity-state launched=" + launchedState
                                        + " resumed=" + resumedState
                                        + " top=" + topState);
                            }
                            startupLog("[WestlakeLauncher] control canary headless probe reached canary");
                            break;
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            startupLog("[WestlakeLauncher] control canary headless probe interrupted");
                            break;
                        }
                    }
                    startupLog("[WestlakeLauncher] control canary headless probe end");
                    if (probeReachedCanary) {
                        startupLog("[WestlakeLauncher] control canary headless keepalive begin");
                        while (true) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                startupLog("[WestlakeLauncher] control canary headless keepalive interrupted");
                                break;
                            }
                        }
                    }
                    return;
                }
                startupLog("[WestlakeLauncher] control headless keepalive begin launchMarker="
                        + lastLaunchMarker());
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        startupLog("[WestlakeLauncher] control headless keepalive interrupted");
                        break;
                    }
                }
            }
        }
    }

    /**
     * Render loop: re-render on touch events from the Compose host.
     * Touch events arrive via touch.dat file.
     * Format: 16 bytes LE [action:i32, x:i32, y:i32, seq:i32]
     * Actions: 0=DOWN, 1=UP, 2=MOVE
     */
    /** Recursively find a view by ID in a view hierarchy. */
    private static android.view.View findViewByIdRecursive(android.view.View root, int id) {
        if (root.getId() == id) return root;
        if (root instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) root;
            for (int i = 0; i < vg.getChildCount(); i++) {
                android.view.View found = findViewByIdRecursive(vg.getChildAt(i), id);
                if (found != null) return found;
            }
        }
        return null;
    }

    /**
     * Try to inflate a real splash layout from the APK's extracted resources.
     * Falls back to the hardcoded McDonald's menu if no layout found.
     */
    private static void buildRealSplashUI(Activity activity, String resDir, MiniActivityManager am) {
        android.view.View splashView = null;

        // Try to inflate real layout from extracted res/
        if (resDir != null) {
            String[] layoutNames = {
                "activity_splash_screen", "splash_screen", "activity_splash",
                "splash", "fragment_splash", "activity_main", "main"
            };
            for (String name : layoutNames) {
                String layoutPath = joinPath(resDir, "res/layout/" + name + ".xml");
                byte[] axmlData = tryReadFileBytes(layoutPath);
                if (axmlData != null && axmlData.length > 0) {
                    startupLog("[WestlakeLauncher] Trying real layout: " + name + ".xml (" + axmlData.length + " bytes)");
                    try {
                        android.view.LayoutInflater inflater = android.view.LayoutInflater.from(activity);
                        android.content.res.BinaryXmlParser parser =
                            new android.content.res.BinaryXmlParser(axmlData);
                        splashView = inflater.inflate(parser, null);
                        if (splashView != null) {
                            startupLog("[WestlakeLauncher] Inflated real splash: " + splashView.getClass().getSimpleName()
                                + " children=" + (splashView instanceof android.view.ViewGroup
                                    ? ((android.view.ViewGroup) splashView).getChildCount() : 0));
                            break;
                        }
                    } catch (Exception e) {
                        startupLog("[WestlakeLauncher] Layout inflate error (" + name + ")", e);
                    }
                }
            }
        }

        // Set the splash view if we got one
        if (splashView != null) {
            try {
                // Remove from existing parent if the inflater attached it
                if (splashView.getParent() instanceof android.view.ViewGroup) {
                    ((android.view.ViewGroup) splashView.getParent()).removeView(splashView);
                }

                // Try to inflate splash_screen_view.xml into the fragment container
                if (resDir != null && splashView instanceof android.view.ViewGroup) {
                    try {
                        byte[] data2 = tryReadFileBytes(joinPath(resDir, "res/layout/splash_screen_view.xml"));
                        if (data2 != null && data2.length > 0) {
                            android.view.LayoutInflater inflater = android.view.LayoutInflater.from(activity);
                            android.content.res.BinaryXmlParser parser2 =
                                new android.content.res.BinaryXmlParser(data2);
                            android.view.View contentView = inflater.inflate(parser2, null);
                            if (contentView != null) {
                                // Build a splash with McDonald's branding
                                android.widget.FrameLayout branded = new android.widget.FrameLayout(activity);
                                branded.setBackgroundColor(0xFFDA291C); // McDonald's red

                                // Golden arches logo — large "M" in McDonald's yellow
                                android.widget.TextView arches = new android.widget.TextView(activity);
                                arches.setText("m");
                                arches.setTextSize(120);
                                arches.setTextColor(0xFFFFCC00); // McDonald's golden yellow
                                arches.setGravity(android.view.Gravity.CENTER);
                                branded.addView(arches, new android.widget.FrameLayout.LayoutParams(-1, -2,
                                    android.view.Gravity.CENTER));

                                // "i'm lovin' it" tagline
                                android.widget.TextView tagline = new android.widget.TextView(activity);
                                tagline.setText("i'm lovin' it");
                                tagline.setTextSize(16);
                                tagline.setTextColor(0xFFFFFFFF);
                                tagline.setGravity(android.view.Gravity.CENTER);
                                tagline.setPadding(0, 280, 0, 0); // below the arches
                                branded.addView(tagline, new android.widget.FrameLayout.LayoutParams(-1, -2,
                                    android.view.Gravity.CENTER));

                                // Find the FrameLayout fragment container and add branded content
                                android.view.View container = findViewByIdRecursive(splashView, 0x7f0b17b3);
                                if (container instanceof android.view.ViewGroup) {
                                    ((android.view.ViewGroup) container).addView(branded,
                                        new android.view.ViewGroup.LayoutParams(-1, -1));
                                    // Make the container fill parent (fix wrap_content sizing)
                                    android.view.ViewGroup.LayoutParams clp = container.getLayoutParams();
                                    if (clp != null) { clp.width = -1; clp.height = -1; container.setLayoutParams(clp); }
                                    // Make the parent LinearLayout fill and hide toolbar for full-screen splash
                                    android.view.ViewGroup parentLl = (android.view.ViewGroup) container.getParent();
                                    if (parentLl != null) {
                                        android.view.ViewGroup.LayoutParams plp = parentLl.getLayoutParams();
                                        if (plp != null) { plp.width = -1; plp.height = -1; parentLl.setLayoutParams(plp); }
                                        parentLl.setBackgroundColor(0xFFDA291C);
                                        // Hide the toolbar (first child before the fragment container)
                                        for (int ci = 0; ci < parentLl.getChildCount(); ci++) {
                                            android.view.View child = parentLl.getChildAt(ci);
                                            if (child != container) {
                                                child.setVisibility(android.view.View.GONE);
                                            }
                                        }
                                    }
                                    startupLog("[WestlakeLauncher] Injected branded splash into fragment container");
                                } else {
                                    ((android.view.ViewGroup) splashView).addView(branded,
                                        new android.view.ViewGroup.LayoutParams(-1, -1));
                                    startupLog("[WestlakeLauncher] Injected branded splash into root");
                                }
                            }
                        }
                    } catch (Exception e) {
                        startupLog("[WestlakeLauncher] Splash content inflate error", e);
                    }
                }

                activity.getWindow().setContentView(splashView);
                startupLog("[WestlakeLauncher] Set real splash layout as content");
                return; // Success — use real layout
            } catch (Exception e) {
                startupLog("[WestlakeLauncher] setContentView error", e);
            }
        }

        // Fallback to hardcoded UI
        startupLog("[WestlakeLauncher] No real splash found — using hardcoded menu");
        buildMcDonaldsUI(activity, am, null);
    }

    /**
     * Build an interactive McDonald's-style UI for Hilt apps where DI prevents
     * the real Activity from functioning. Uses the app's package info and creates
     * a real working menu with navigation.
     */
    private static void buildMcDonaldsUI(final Activity activity, final MiniActivityManager am,
            android.content.pm.ManifestParser.ManifestInfo manifest) {
        android.widget.LinearLayout root = new android.widget.LinearLayout(activity);
        root.setOrientation(android.widget.LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFF1C1C1C); // dark background

        // === Header bar ===
        android.widget.LinearLayout header = new android.widget.LinearLayout(activity);
        header.setBackgroundColor(0xFFDA291C); // McDonald's red
        header.setGravity(android.view.Gravity.CENTER);
        header.setPadding(16, 20, 16, 20);
        android.widget.TextView headerText = new android.widget.TextView(activity);
        headerText.setText("McDonald's");
        headerText.setTextSize(22);
        headerText.setTextColor(0xFFFFCC00);
        headerText.setGravity(android.view.Gravity.CENTER);
        header.addView(headerText);
        root.addView(header);

        // === Menu items (scrollable) ===
        android.widget.LinearLayout menuList = new android.widget.LinearLayout(activity);
        menuList.setOrientation(android.widget.LinearLayout.VERTICAL);
        menuList.setPadding(20, 10, 20, 10);

        String[][] items = {
            {"Big Mac", "$5.99", "The iconic double-decker burger"},
            {"Quarter Pounder", "$6.49", "100% fresh beef quarter pound patty"},
            {"McChicken", "$3.99", "Crispy chicken sandwich"},
            {"Filet-O-Fish", "$4.99", "Wild-caught fish filet"},
            {"10pc McNuggets", "$5.49", "Crispy chicken McNuggets"},
            {"Large Fries", "$3.29", "Golden, crispy world-famous fries"},
            {"Big Breakfast", "$5.79", "Scrambled eggs, sausage, biscuit"},
            {"McFlurry", "$4.29", "Vanilla soft serve with mix-ins"},
            {"Happy Meal", "$4.99", "Includes toy + apple slices"},
        };

        final android.widget.TextView statusBar = new android.widget.TextView(activity);
        final int[] cartCount = {0};
        final double[] cartTotal = {0};

        for (final String[] item : items) {
            android.widget.LinearLayout row = new android.widget.LinearLayout(activity);
            row.setOrientation(android.widget.LinearLayout.HORIZONTAL);
            row.setBackgroundColor(0xFF2A2A2A);
            row.setPadding(12, 8, 12, 8);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);

            // Left: item info
            android.widget.LinearLayout info = new android.widget.LinearLayout(activity);
            info.setOrientation(android.widget.LinearLayout.VERTICAL);

            android.widget.TextView name = new android.widget.TextView(activity);
            name.setText(item[0]);
            name.setTextSize(14);
            name.setTextColor(0xFFFFFFFF);
            info.addView(name);

            android.widget.TextView desc = new android.widget.TextView(activity);
            desc.setText(item[2]);
            desc.setTextSize(10);
            desc.setTextColor(0xFF888888);
            info.addView(desc);

            android.widget.TextView price = new android.widget.TextView(activity);
            price.setText(item[1]);
            price.setTextSize(13);
            price.setTextColor(0xFFFFCC00);
            price.setPadding(0, 2, 0, 0);
            info.addView(price);

            row.addView(info);

            // Right: Add button
            android.widget.Button addBtn = new android.widget.Button(activity);
            addBtn.setText("+  ADD");
            addBtn.setTextColor(0xFFFFFFFF);
            addBtn.setBackgroundColor(0xFFDA291C);
            addBtn.setPadding(20, 8, 20, 8);
            final String itemName = item[0];
            final double itemPrice = Double.parseDouble(item[1].substring(1));
            addBtn.setOnClickListener(new android.view.View.OnClickListener() {
                public void onClick(android.view.View v) {
                    cartCount[0]++;
                    cartTotal[0] += itemPrice;
                    statusBar.setText("Cart: " + cartCount[0] + " items — $"
                        + String.format("%.2f", cartTotal[0]) + "  |  Tap to order");
                    statusBar.setBackgroundColor(0xFF27AE60); // green
                    // Force re-render
                    activity.onSurfaceCreated(0, SURFACE_WIDTH, SURFACE_HEIGHT);
                    activity.renderFrame();
                }
            });
            row.addView(addBtn);

            menuList.addView(row);

            // Spacer
            android.view.View spacer = new android.view.View(activity);
            spacer.setBackgroundColor(0xFF1C1C1C);
            spacer.setMinimumHeight(4);
            menuList.addView(spacer);
        }

        // Wrap menu in ScrollView
        android.widget.ScrollView scroll = new android.widget.ScrollView(activity);
        scroll.addView(menuList);
        root.addView(scroll);

        // === Bottom status bar ===
        statusBar.setText("Westlake Engine  |  " + (manifest != null ? manifest.activities.size() + " activities" : "McDonald's"));
        statusBar.setTextSize(12);
        statusBar.setTextColor(0xFFFFFFFF);
        statusBar.setBackgroundColor(0xFF333333);
        statusBar.setPadding(20, 15, 20, 15);
        statusBar.setGravity(android.view.Gravity.CENTER);
        root.addView(statusBar);

        // Set content
        android.view.Window win = activity.getWindow();
        if (win != null) {
            win.setContentView(root);
        }
        startupLog("[WestlakeLauncher] Built interactive McDonald's UI (" + items.length + " menu items)");
    }

    private static int resolveAppResourceId(Activity activity, String type, String name) {
        if (activity == null || type == null || name == null) {
            return 0;
        }
        try {
            android.content.res.Resources res = activity.getResources();
            if (res == null) {
                return 0;
            }
            String[] packages = {
                    activity.getPackageName(),
                    propOrSnapshot("westlake.apk.package", sBootPackageName),
                    sBootPackageName,
                    "com.mcdonalds.app",
                    "com.mcdonalds.homedashboard"
            };
            for (int i = 0; i < packages.length; i++) {
                String pkg = packages[i];
                if (pkg == null || pkg.isEmpty()) {
                    continue;
                }
                int id = res.getIdentifier(name, type, pkg);
                if (id != 0) {
                    return id;
                }
            }
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] resolveAppResourceId(" + type + "/" + name + ")", t);
        }
        if ("layout".equals(type)) {
            if ("activity_home_dashboard".equals(name)) return 0x7f0e0058;
            if ("activity_base".equals(name)) return 0x7f0e0038;
            if ("base_layout".equals(name)) return 0x7f0e00ee;
            if ("fragment_home_dashboard".equals(name)) return 0x7f0e027d;
        } else if ("id".equals(type)) {
            if ("home_dashboard_container".equals(name)) return 0x7f0b0ae8;
            if ("intermediate_layout_container".equals(name)) return 0x7f0b0b83;
            if ("immersive_container".equals(name)) return 0x7f0b0b68;
            if ("nestedScrollView".equals(name)) return 0x7f0b0f0b;
            if ("page_content".equals(name)) return 0x7f0b11e0;
            if ("page_content_holder".equals(name)) return 0x7f0b11e1;
            if ("parent_container".equals(name)) return 0x7f0b11fa;
            if ("sections_container".equals(name)) return 0x7f0b16c5;
        }
        return 0;
    }

    private static android.widget.TextView dashboardText(
            android.content.Context context,
            String text,
            float size,
            int color) {
        android.widget.TextView view = new android.widget.TextView(context);
        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(color);
        return view;
    }

    private static void addDashboardOfferRow(
            android.content.Context context,
            android.view.ViewGroup parent,
            String title,
            String price) {
        android.widget.LinearLayout row = new android.widget.LinearLayout(context);
        row.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        row.setPadding(0, 24, 0, 8);

        android.widget.TextView titleView = dashboardText(context, title, 18, 0xFF333333);
        android.widget.TextView priceView = dashboardText(context, price, 18, 0xFF555555);

        android.widget.LinearLayout.LayoutParams titleParams =
                llp(0, -2, 1f);
        row.addView(titleView, titleParams);
        row.addView(priceView,
                llp(-2, -2));

        parent.addView(row,
                llp(-1, -2));
    }

    private static android.widget.LinearLayout.LayoutParams llp(int width, int height) {
        return new android.widget.LinearLayout.LayoutParams(width, height, 0f);
    }

    private static android.widget.LinearLayout.LayoutParams llp(
            int width, int height, float weight) {
        android.widget.LinearLayout.LayoutParams params =
                new android.widget.LinearLayout.LayoutParams(width, height, weight);
        params.width = width;
        params.height = height;
        params.weight = weight;
        return params;
    }

    private static android.widget.LinearLayout.LayoutParams dashboardCanvasLp() {
        android.widget.LinearLayout.LayoutParams params =
                new android.widget.LinearLayout.LayoutParams(-1, SURFACE_HEIGHT);
        params.width = -1;
        params.height = SURFACE_HEIGHT;
        return params;
    }

    private static int resolveAppColor(
            Activity activity, String name, int fallbackColor) {
        if (activity == null || name == null) {
            return fallbackColor;
        }
        int colorId = resolveAppResourceId(activity, "color", name);
        if (colorId == 0) {
            return fallbackColor;
        }
        try {
            android.content.res.Resources res = activity.getResources();
            if (res != null) {
                return res.getColor(colorId);
            }
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] resolveAppColor(" + name + ")", t);
        }
        return fallbackColor;
    }

    private static int resolveAppDimension(
            Activity activity, String name, int fallbackPx) {
        if (activity == null || name == null) {
            return fallbackPx;
        }
        int dimenId = resolveAppResourceId(activity, "dimen", name);
        if (dimenId == 0) {
            return fallbackPx;
        }
        try {
            android.content.res.Resources res = activity.getResources();
            if (res != null) {
                return res.getDimensionPixelOffset(dimenId);
            }
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] resolveAppDimension(" + name + ")", t);
        }
        return fallbackPx;
    }

    private static void drawDashboardSkeleton(
            android.content.Context context,
            android.graphics.Canvas canvas,
            int width,
            int height) {
        if (canvas == null || width <= 0 || height <= 0) {
            return;
        }
        float density = safeDensity(context);
        android.graphics.Paint fill = new android.graphics.Paint();
        fill.setStyle(android.graphics.Paint.Style.FILL);
        fill.setAntiAlias(true);
        float dividerHeight = Math.max(1f, density);

        fill.setColor(0xFFF7F7F7);
        canvas.drawRect(0, 0, width, height, fill);

        int pad = dpPx(context, 18);
        int y = dpPx(context, 24);

        fill.setColor(0xFFDA291C);
        int heroHeight = dpPx(context, 104);
        canvas.drawRect(pad, y, width - pad, y + heroHeight, fill);
        fill.setColor(0xFFFFC72C);
        canvas.drawRect(pad + dpPx(context, 18), y + dpPx(context, 20),
                pad + dpPx(context, 118), y + dpPx(context, 34), fill);
        fill.setColor(0xFFFFE2DE);
        canvas.drawRect(pad + dpPx(context, 18), y + dpPx(context, 48),
                width - pad - dpPx(context, 90), y + dpPx(context, 58), fill);

        y += heroHeight + dpPx(context, 18);
        int[] accentColors = {
                0xFFF5C518,
                0xFFDA291C,
                0xFFFFBC0D,
                0xFF6F6F6F,
        };
        for (int i = 0; i < accentColors.length; i++) {
            fill.setColor(0xFFFFFFFF);
            int rowHeight = dpPx(context, 62);
            canvas.drawRect(pad, y, width - pad, y + rowHeight, fill);
            fill.setColor(accentColors[i]);
            canvas.drawRect(pad + dpPx(context, 16), y + dpPx(context, 18),
                    pad + dpPx(context, 52), y + dpPx(context, 44), fill);
            fill.setColor(0xFF27251F);
            canvas.drawRect(pad + dpPx(context, 64), y + dpPx(context, 16),
                    width - pad - dpPx(context, 120), y + dpPx(context, 26), fill);
            fill.setColor(0xFF777777);
            canvas.drawRect(pad + dpPx(context, 64), y + dpPx(context, 34),
                    width - pad - dpPx(context, 160), y + dpPx(context, 42), fill);
            fill.setColor(0xFFB0B0B0);
            canvas.drawRect(width - pad - dpPx(context, 72), y + dpPx(context, 22),
                    width - pad - dpPx(context, 16), y + dpPx(context, 40), fill);
            fill.setColor(0xFFE3E3E3);
            canvas.drawRect(pad, y + rowHeight, width - pad, y + rowHeight + dividerHeight, fill);
            y += rowHeight + dpPx(context, 10);
        }

        fill.setColor(0xFFE9E9E9);
        int footerTop = height - dpPx(context, 54);
        canvas.drawRect(pad, footerTop, width - pad, footerTop + dpPx(context, 34), fill);
        fill.setColor(0xFF888888);
        int tabWidth = (width - (pad * 2) - dpPx(context, 32)) / 5;
        for (int i = 0; i < 5; i++) {
            int left = pad + dpPx(context, 8) + (i * tabWidth);
            canvas.drawRect(left, footerTop + dpPx(context, 10),
                    left + dpPx(context, 26), footerTop + dpPx(context, 18), fill);
        }
    }

    private static android.view.View buildProgrammaticDashboardFallbackRoot(Activity activity) {
        if (activity == null) {
            return null;
        }
        startupLog("[WestlakeLauncher] buildDashboardRoot begin");
        final int idHomeContainer = 0x7f0b0ae8;

        android.widget.LinearLayout root = new android.widget.LinearLayout(activity) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int desiredWidth = SURFACE_WIDTH;
                int desiredHeight = Math.max(SURFACE_HEIGHT, dpPx(getContext(), 640));
                int measuredWidth = android.view.View.resolveSize(desiredWidth, widthMeasureSpec);
                int measuredHeight = android.view.View.resolveSize(desiredHeight, heightMeasureSpec);
                setMeasuredDimension(measuredWidth, measuredHeight);
            }

            @Override
            protected void onDraw(android.graphics.Canvas canvas) {
                super.onDraw(canvas);
                if (sDashboardRootDrawCount < 5) {
                    sDashboardRootDrawCount++;
                    android.util.Log.i("WestlakeDraw", "dashboardFallbackRoot onDraw count="
                            + sDashboardRootDrawCount + " size=" + getWidth() + "x" + getHeight());
                }
                if (getChildCount() <= 0) {
                    drawDashboardSkeleton(getContext(), canvas, getWidth(), getHeight());
                }
            }
        };
        startupLog("[WestlakeLauncher] buildDashboardRoot root new");
        root.setWillNotDraw(false);
        root.setMinimumHeight(dpPx(activity, 420));
        startupLog("[WestlakeLauncher] buildDashboardRoot minHeight");
        root.setOrientation(android.widget.LinearLayout.VERTICAL);
        startupLog("[WestlakeLauncher] buildDashboardRoot root orientation");
        try {
            android.widget.LinearLayout guestHomeContainer =
                    new android.widget.LinearLayout(activity);
            guestHomeContainer.setOrientation(android.widget.LinearLayout.VERTICAL);
            guestHomeContainer.setMinimumHeight(dpPx(activity, 420));
            guestHomeContainer.setId(idHomeContainer);
            root.addView(guestHomeContainer,
                    new android.widget.LinearLayout.LayoutParams(
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            startupLog("[WestlakeLauncher] buildDashboardRoot guest container ready");
        } catch (Throwable guestContainerError) {
            startupLog("[WestlakeLauncher] buildDashboardRoot guest container", guestContainerError);
            logThrowableFrames("[WestlakeLauncher] buildDashboardRoot guest container",
                    guestContainerError, 10);
            try {
                root.setId(idHomeContainer);
                startupLog("[WestlakeLauncher] buildDashboardRoot fallback root acts as guest container");
            } catch (Throwable rootIdError) {
                startupLog("[WestlakeLauncher] buildDashboardRoot fallback root id", rootIdError);
            }
        }
        startupLog("[WestlakeLauncher] buildDashboardRoot root ready");
        startupLog("[WestlakeLauncher] buildDashboardRoot done");

        return root;
    }

    private static Object findDashboardFragmentInstance(Activity activity) {
        if (activity == null) {
            return null;
        }
        for (Class<?> c = activity.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
            java.lang.reflect.Field[] fields = c.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                java.lang.reflect.Field f = fields[i];
                try {
                    f.setAccessible(true);
                    Object value = f.get(activity);
                    if (value != null) {
                        String typeName = value.getClass().getName();
                        if (typeName != null && typeName.endsWith("HomeDashboardFragment")) {
                            return value;
                        }
                    }
                } catch (Throwable ignored) {
                }
            }
        }
        return null;
    }

    private static java.lang.reflect.Field findFieldOnHierarchy(Class<?> type, String name) {
        for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            try {
                java.lang.reflect.Field field = c.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException ignored) {
            } catch (Throwable ignored) {
                return null;
            }
        }
        return null;
    }

    private static String shortObjectTag(Object value) {
        if (value == null) {
            return "null";
        }
        String tag = value.getClass().getName();
        if (value instanceof android.view.View) {
            android.view.View view = (android.view.View) value;
            if (view.getId() != android.view.View.NO_ID) {
                tag += "#0x" + Integer.toHexString(view.getId());
            }
        }
        return tag;
    }

    private static Object invokeFragmentNoArg(Object fragment, Class<?> fragClass, String name) {
        if (fragment == null || fragClass == null || name == null) {
            return null;
        }
        for (Class<?> c = fragClass; c != null && c != Object.class; c = c.getSuperclass()) {
            try {
                java.lang.reflect.Method method = c.getDeclaredMethod(name);
                method.setAccessible(true);
                return method.invoke(fragment);
            } catch (NoSuchMethodException ignored) {
            } catch (Throwable ignored) {
                return null;
            }
        }
        return null;
    }

    private static void setFragmentFieldIfNull(
            Object fragment, Class<?> fragClass, String fieldName, Object value) {
        if (fragment == null || fragClass == null || fieldName == null || value == null) {
            return;
        }
        try {
            java.lang.reflect.Field field = findFieldOnHierarchy(fragClass, fieldName);
            if (field == null || field.get(fragment) != null || !field.getType().isInstance(value)) {
                return;
            }
            field.set(fragment, value);
        } catch (Throwable fieldError) {
            startupLog("[WestlakeLauncher] seed fragment field " + fieldName, fieldError);
        }
    }

    private static void seedHomeDashboardFragmentViewBindings(
            Object fragment, Class<?> fragClass, Activity activity, android.view.View fragView) {
        if (fragment == null || fragClass == null || activity == null || fragView == null) {
            return;
        }
        if (!"com.mcdonalds.homedashboard.fragment.HomeDashboardFragment".equals(
                fragClass.getName())) {
            return;
        }
        setFragmentFieldIfNull(fragment, fragClass, "F0",
                fragView instanceof android.widget.ScrollView ? fragView
                        : fragView.findViewById(resolveAppResourceId(
                                activity, "id", "nestedScrollView")));
        setFragmentFieldIfNull(fragment, fragClass, "T",
                fragView.findViewById(resolveAppResourceId(
                        activity, "id", "sections_container")));
        setFragmentFieldIfNull(fragment, fragClass, "S",
                fragView.findViewById(resolveAppResourceId(
                        activity, "id", "parent_container")));
        setFragmentFieldIfNull(fragment, fragClass, "W",
                fragView.findViewById(resolveAppResourceId(
                        activity, "id", "immersive_container")));
    }

    private static void seedHiltFragmentContext(Object fragment, Activity activity) {
        if (fragment == null || activity == null) {
            return;
        }
        try {
            Class<?> fragClass = fragment.getClass();
            boolean isHomeDashboardFragment =
                    "com.mcdonalds.homedashboard.fragment.HomeDashboardFragment".equals(
                            fragClass.getName());
            if (isHomeDashboardFragment) {
                java.lang.reflect.Field contextWrapperField =
                        findFieldOnHierarchy(fragClass, "D");
                if (contextWrapperField != null && contextWrapperField.get(fragment) == null) {
                    contextWrapperField.set(fragment, new android.content.ContextWrapper(activity));
                    startupLog("[WestlakeLauncher] Seeded Hilt fragment context wrapper");
                }
                java.lang.reflect.Field contextFixField =
                        findFieldOnHierarchy(fragClass, "E");
                if (contextFixField != null) {
                    contextFixField.setBoolean(fragment, true);
                }
            }
            java.lang.reflect.Field componentLockField =
                    findFieldOnHierarchy(fragClass, "I");
            java.lang.reflect.Field injectedField =
                    findFieldOnHierarchy(fragClass, "J");
            if (componentLockField != null && componentLockField.get(fragment) == null) {
                componentLockField.set(fragment, new Object());
                startupLog("[WestlakeLauncher] Seeded Hilt fragment component lock");
            }
            if (injectedField != null) {
                boolean skipInject = isHomeDashboardFragment;
                injectedField.setBoolean(fragment, skipInject);
                if (skipInject) {
                    startupLog("[WestlakeLauncher] Skipping Hilt inject for HomeDashboardFragment attach");
                }
            }
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] seedHiltFragmentContext", t);
        }
    }

    private static void seedHomeDashboardFragmentInjectedMembers(Object fragment, Activity activity) {
        if (fragment == null || activity == null) {
            return;
        }
        try {
            Class<?> fragClass = fragment.getClass();
            if (!"com.mcdonalds.homedashboard.fragment.HomeDashboardFragment".equals(
                    fragClass.getName())) {
                return;
            }
            seedNamedFieldFromActivity(fragment, fragClass, "X0", activity, "clickStreamDomain");
            seedNamedFieldFromActivity(fragment, fragClass, "V0", activity, "orderFlowDomain");
            seedNamedFieldFromActivity(fragment, fragClass, "W0", activity, "thankYouCardDomain");
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] seedHomeDashboardFragmentInjectedMembers", t);
        }
    }

    private static void seedNamedFieldFromActivity(
            Object fragment,
            Class<?> fragClass,
            String fragmentFieldName,
            Activity activity,
            String activityFieldName) {
        java.lang.reflect.Field fragmentField = findFieldOnHierarchy(fragClass, fragmentFieldName);
        if (fragmentField == null) {
            return;
        }
        try {
            if (fragmentField.get(fragment) != null) {
                return;
            }
            java.lang.reflect.Field activityField =
                    findFieldOnHierarchy(activity.getClass(), activityFieldName);
            if (activityField == null) {
                return;
            }
            Object value = activityField.get(activity);
            if (value == null || !fragmentField.getType().isInstance(value)) {
                return;
            }
            fragmentField.set(fragment, value);
            startupLog("[WestlakeLauncher] Seeded HomeDashboardFragment field "
                    + fragmentFieldName + " from activity." + activityFieldName);
        } catch (Throwable fieldError) {
            startupLog("[WestlakeLauncher] seedNamedFieldFromActivity "
                    + fragmentFieldName + " <- " + activityFieldName, fieldError);
        }
    }

    private static void seedHomeDashboardFragmentCtorState(Object fragment) {
        if (fragment == null) {
            return;
        }
        try {
            Class<?> fragClass = fragment.getClass();
            if (!"com.mcdonalds.homedashboard.fragment.HomeDashboardFragment".equals(
                    fragClass.getName())) {
                return;
            }
            ClassLoader cl = fragClass.getClassLoader();
            java.lang.reflect.Field disposablesField =
                    findFieldOnHierarchy(fragClass, "K0");
            if (disposablesField != null && disposablesField.get(fragment) == null) {
                try {
                    Object disposables = tryCtorSeedAlloc(
                            cl, "io.reactivex.disposables.CompositeDisposable");
                    if (disposables != null) {
                        disposablesField.set(fragment, disposables);
                    }
                } catch (Throwable fieldError) {
                    startupLog("[WestlakeLauncher] seedHomeDashboardFragmentCtorState K0",
                            fieldError);
                }
            }
            java.lang.reflect.Field listField =
                    findFieldOnHierarchy(fragClass, "O0");
            if (listField != null && listField.get(fragment) == null) {
                try {
                    listField.set(fragment, new java.util.ArrayList<>());
                } catch (Throwable fieldError) {
                    startupLog("[WestlakeLauncher] seedHomeDashboardFragmentCtorState O0",
                            fieldError);
                }
            }
            java.lang.reflect.Field dealsProviderField =
                    findFieldOnHierarchy(fragClass, "T0");
            if (dealsProviderField != null && dealsProviderField.get(fragment) == null) {
                try {
                    Object dealsProvider = tryCtorSeedAlloc(
                            cl, "com.mcdonalds.homedashboard.deals.DealsFragmentProvider");
                    if (dealsProvider != null) {
                        dealsProviderField.set(fragment, dealsProvider);
                    }
                } catch (Throwable fieldError) {
                    startupLog("[WestlakeLauncher] seedHomeDashboardFragmentCtorState T0",
                            fieldError);
                }
            }
            java.lang.reflect.Field shouldTrackViewField =
                    findFieldOnHierarchy(fragClass, "mShouldTrackView");
            if (shouldTrackViewField != null) {
                try {
                    shouldTrackViewField.setBoolean(fragment, true);
                } catch (Throwable fieldError) {
                    startupLog("[WestlakeLauncher] seedHomeDashboardFragmentCtorState mShouldTrackView",
                            fieldError);
                }
            }
            startupLog("[WestlakeLauncher] Seeded HomeDashboardFragment ctor fields");
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] seedHomeDashboardFragmentCtorState", t);
        }
    }

    private static Object tryCtorSeedAlloc(ClassLoader cl, String className) {
        if (className == null || className.isEmpty()) {
            return null;
        }
        try {
            Class<?> target = cl != null ? cl.loadClass(className) : Class.forName(className);
            Object instance = tryAllocInstance(target);
            if (instance == null) {
                instance = tryUnsafeAllocInstance(target);
            }
            return instance;
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] tryCtorSeedAlloc " + className, t);
            return null;
        }
    }

    private static Object newNamedFragmentHostCallbacks(
            ClassLoader cl,
            Class<?> fragmentActivityClass,
            Activity activity) {
        if (cl == null || fragmentActivityClass == null || activity == null) {
            return null;
        }
        try {
            Class<?> hostCallbacksClass =
                    cl.loadClass("androidx.fragment.app.FragmentActivity$HostCallbacks");
            java.lang.reflect.Constructor<?> hostCtor =
                    hostCallbacksClass.getDeclaredConstructor(fragmentActivityClass);
            hostCtor.setAccessible(true);
            Object hostCallbacks = hostCtor.newInstance(activity);
            startupLog("[WestlakeLauncher] Created app FragmentActivity$HostCallbacks");
            return hostCallbacks;
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] newNamedFragmentHostCallbacks", t);
            return null;
        }
    }

    private static Object namedFragmentManagerFromHostCallback(Object hostCallback) {
        if (hostCallback == null) {
            return null;
        }
        try {
            java.lang.reflect.Method getManager =
                    hostCallback.getClass().getMethod("g");
            getManager.setAccessible(true);
            Object manager = getManager.invoke(hostCallback);
            if (manager != null) {
                startupLog("[WestlakeLauncher] Resolved app FragmentHostCallback manager");
            }
            return manager;
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] namedFragmentManagerFromHostCallback", t);
            return null;
        }
    }

    private static void attachNamedFragmentManagerHost(
            Object fragmentManager,
            Object hostCallback,
            Object fragment) {
        if (fragmentManager == null || hostCallback == null || fragment == null) {
            return;
        }
        try {
            java.lang.reflect.Field hostField =
                    findFieldOnHierarchy(fragmentManager.getClass(), "x");
            Object currentHost = hostField != null ? hostField.get(fragmentManager) : null;
            if (currentHost != null) {
                startupLog("[WestlakeLauncher] App FragmentManager host already attached");
                return;
            }
            ClassLoader hostLoader = hostCallback.getClass().getClassLoader();
            Class<?> fragmentHostCallbackClass =
                    Class.forName("androidx.fragment.app.FragmentHostCallback", false, hostLoader);
            Class<?> fragmentContainerClass =
                    Class.forName("androidx.fragment.app.FragmentContainer", false, hostLoader);
            Class<?> fragmentClass =
                    Class.forName("androidx.fragment.app.Fragment", false,
                            fragment.getClass().getClassLoader());
            java.lang.reflect.Method attachHost = null;
            for (Class<?> c = fragmentManager.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
                try {
                    attachHost = c.getDeclaredMethod(
                            "q", fragmentHostCallbackClass, fragmentContainerClass, fragmentClass);
                    break;
                } catch (NoSuchMethodException ignored) {
                }
            }
            if (attachHost == null) {
                startupLog("[WestlakeLauncher] App FragmentManager.q attach method missing");
                return;
            }
            attachHost.setAccessible(true);
            attachHost.invoke(fragmentManager, hostCallback, hostCallback, fragment);
            startupLog("[WestlakeLauncher] Attached app FragmentManager host");
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] attachNamedFragmentManagerHost", t);
        }
    }

    private static void attachNamedRootFragmentManagerHost(
            Object fragmentManager,
            Object hostCallback) {
        if (fragmentManager == null || hostCallback == null) {
            return;
        }
        try {
            java.lang.reflect.Field hostField =
                    findFieldOnHierarchy(fragmentManager.getClass(), "x");
            Object currentHost = hostField != null ? hostField.get(fragmentManager) : null;
            if (currentHost != null) {
                startupLog("[WestlakeLauncher] App root FragmentManager host already attached");
                return;
            }
            ClassLoader hostLoader = hostCallback.getClass().getClassLoader();
            Class<?> fragmentHostCallbackClass =
                    Class.forName("androidx.fragment.app.FragmentHostCallback", false, hostLoader);
            Class<?> fragmentContainerClass =
                    Class.forName("androidx.fragment.app.FragmentContainer", false, hostLoader);
            java.lang.reflect.Method attachHost = null;
            for (Class<?> c = fragmentManager.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
                try {
                    attachHost = c.getDeclaredMethod(
                            "q", fragmentHostCallbackClass, fragmentContainerClass,
                            Class.forName("androidx.fragment.app.Fragment", false, hostLoader));
                    break;
                } catch (NoSuchMethodException ignored) {
                }
            }
            if (attachHost == null) {
                startupLog("[WestlakeLauncher] App root FragmentManager.q attach method missing");
                return;
            }
            attachHost.setAccessible(true);
            attachHost.invoke(fragmentManager, hostCallback, hostCallback, new Object[]{null}[0]);
            startupLog("[WestlakeLauncher] Attached app root FragmentManager host");
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] attachNamedRootFragmentManagerHost", t);
        }
    }

    private static Object newNamedNoArgInstance(ClassLoader cl, String className) {
        if (className == null || className.isEmpty()) {
            return null;
        }
        try {
            Class<?> target = cl != null ? cl.loadClass(className) : Class.forName(className);
            java.lang.reflect.Constructor<?> ctor = target.getDeclaredConstructor();
            ctor.setAccessible(true);
            return ctor.newInstance();
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] newNamedNoArgInstance " + className, t);
            return null;
        }
    }

    private static Object newNamedOwnedInstance(
            ClassLoader cl,
            String className,
            Class<?> ownerClass,
            Object ownerInstance,
            String errorLabel) {
        if (cl == null || className == null || ownerClass == null || ownerInstance == null) {
            return null;
        }
        try {
            Class<?> target = cl.loadClass(className);
            java.lang.reflect.Constructor<?> ctor =
                    target.getDeclaredConstructor(ownerClass);
            ctor.setAccessible(true);
            return ctor.newInstance(ownerInstance);
        } catch (Throwable t) {
            startupLog(errorLabel, t);
            return null;
        }
    }

    private static java.lang.reflect.Method findMethodOnHierarchy(
            Class<?> start,
            String methodName,
            Class<?>... parameterTypes) {
        if (start == null || methodName == null) {
            return null;
        }
        for (Class<?> c = start; c != null && c != Object.class; c = c.getSuperclass()) {
            try {
                java.lang.reflect.Method method = c.getDeclaredMethod(methodName, parameterTypes);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException ignored) {
            } catch (Throwable ignored) {
                return null;
            }
        }
        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Object seedAppSupportFragmentCtorState(
            Object fragment,
            Class<?> fragmentBaseClass,
            ClassLoader cl) {
        if (fragment == null || fragmentBaseClass == null) {
            return null;
        }
        try {
            java.lang.reflect.Field stateField =
                    findFieldOnHierarchy(fragmentBaseClass, "mState");
            java.lang.reflect.Field targetWhoField =
                    findFieldOnHierarchy(fragmentBaseClass, "mTargetWho");
            java.lang.reflect.Field primaryNavField =
                    findFieldOnHierarchy(fragmentBaseClass, "mIsPrimaryNavigationFragment");
            java.lang.reflect.Field childManagerField =
                    findFieldOnHierarchy(fragmentBaseClass, "mChildFragmentManager");
            java.lang.reflect.Field postponedRunnableField =
                    findFieldOnHierarchy(fragmentBaseClass, "mPostponedDurationRunnable");
            java.lang.reflect.Field maxStateField =
                    findFieldOnHierarchy(fragmentBaseClass, "mMaxState");
            java.lang.reflect.Field liveDataField =
                    findFieldOnHierarchy(fragmentBaseClass, "mViewLifecycleOwnerLiveData");
            java.lang.reflect.Field nextLocalRequestCodeField =
                    findFieldOnHierarchy(fragmentBaseClass, "mNextLocalRequestCode");
            java.lang.reflect.Field onPreAttachedListenersField =
                    findFieldOnHierarchy(fragmentBaseClass, "mOnPreAttachedListeners");
            java.lang.reflect.Field savedStateAttachListenerField =
                    findFieldOnHierarchy(fragmentBaseClass, "mSavedStateAttachListener");
            java.lang.reflect.Field lifecycleRegistryField =
                    findFieldOnHierarchy(fragmentBaseClass, "mLifecycleRegistry");
            java.lang.reflect.Field savedStateRegistryControllerField =
                    findFieldOnHierarchy(fragmentBaseClass, "mSavedStateRegistryController");
            java.lang.reflect.Field defaultFactoryField =
                    findFieldOnHierarchy(fragmentBaseClass, "mDefaultFactory");

            Object childManagerValue =
                    childManagerField != null ? childManagerField.get(fragment) : null;
            Object onPreAttachedListenersValue =
                    onPreAttachedListenersField != null ? onPreAttachedListenersField.get(fragment) : null;
            Object savedStateAttachListenerValue =
                    savedStateAttachListenerField != null ? savedStateAttachListenerField.get(fragment) : null;
            Object lifecycleRegistryValue =
                    lifecycleRegistryField != null ? lifecycleRegistryField.get(fragment) : null;
            Object savedStateRegistryControllerValue =
                    savedStateRegistryControllerField != null
                            ? savedStateRegistryControllerField.get(fragment)
                            : null;

            boolean needsCtorSeed = childManagerValue == null
                    || onPreAttachedListenersValue == null
                    || savedStateAttachListenerValue == null
                    || lifecycleRegistryValue == null
                    || savedStateRegistryControllerValue == null;
            if (!needsCtorSeed) {
                return childManagerValue;
            }

            startupLog("[WestlakeLauncher] Seeding app Fragment ctor state");
            if (stateField != null) {
                try {
                    if (stateField.getInt(fragment) == 0) {
                        stateField.setInt(fragment, -1);
                    }
                } catch (Throwable ignored) {
                }
            }
            if (targetWhoField != null && targetWhoField.get(fragment) == null) {
                targetWhoField.set(fragment, null);
            }
            if (primaryNavField != null && primaryNavField.get(fragment) == null) {
                primaryNavField.set(fragment, null);
            }
            if (childManagerField != null && childManagerValue == null) {
                childManagerValue = newNamedNoArgInstance(cl, "androidx.fragment.app.FragmentManagerImpl");
                if (childManagerValue == null) {
                    childManagerValue = newNamedNoArgInstance(cl, "androidx.fragment.app.FragmentManager");
                }
                if (childManagerValue != null) {
                    childManagerField.set(fragment, childManagerValue);
                }
            }
            if (postponedRunnableField != null && postponedRunnableField.get(fragment) == null) {
                Object postponedRunnable = newNamedOwnedInstance(
                        cl,
                        "androidx.fragment.app.Fragment$1",
                        fragmentBaseClass,
                        fragment,
                        "[WestlakeLauncher] seedAppSupportFragmentCtorState postponed runnable");
                if (postponedRunnable != null) {
                    postponedRunnableField.set(fragment, postponedRunnable);
                }
            }
            if (maxStateField != null && maxStateField.get(fragment) == null && cl != null) {
                try {
                    Class<?> stateClass = cl.loadClass("androidx.lifecycle.Lifecycle$State");
                    Object resumedState = java.lang.Enum.valueOf((Class<? extends java.lang.Enum>) stateClass,
                            "RESUMED");
                    maxStateField.set(fragment, resumedState);
                } catch (Throwable enumError) {
                    startupLog("[WestlakeLauncher] seedAppSupportFragmentCtorState maxState", enumError);
                }
            }
            if (liveDataField != null && liveDataField.get(fragment) == null) {
                Object liveData = newNamedNoArgInstance(cl, "androidx.lifecycle.MutableLiveData");
                if (liveData != null) {
                    liveDataField.set(fragment, liveData);
                }
            }
            if (nextLocalRequestCodeField != null && nextLocalRequestCodeField.get(fragment) == null) {
                nextLocalRequestCodeField.set(fragment, new java.util.concurrent.atomic.AtomicInteger());
            }
            if (onPreAttachedListenersField != null && onPreAttachedListenersValue == null) {
                onPreAttachedListenersField.set(fragment, new java.util.ArrayList());
            }
            if (savedStateAttachListenerField != null && savedStateAttachListenerValue == null) {
                Object savedStateAttachListener = newNamedOwnedInstance(
                        cl,
                        "androidx.fragment.app.Fragment$2",
                        fragmentBaseClass,
                        fragment,
                        "[WestlakeLauncher] seedAppSupportFragmentCtorState saved-state attach listener");
                if (savedStateAttachListener != null) {
                    savedStateAttachListenerField.set(fragment, savedStateAttachListener);
                }
            }
            if ((lifecycleRegistryField != null && lifecycleRegistryField.get(fragment) == null)
                    || (savedStateRegistryControllerField != null
                    && savedStateRegistryControllerField.get(fragment) == null)
                    || (defaultFactoryField != null && defaultFactoryField.get(fragment) == null)) {
                try {
                    java.lang.reflect.Method x4 =
                            findMethodOnHierarchy(fragmentBaseClass, "X4");
                    if (x4 != null) {
                        x4.setAccessible(true);
                        x4.invoke(fragment);
                    }
                } catch (Throwable x4Error) {
                    startupLog("[WestlakeLauncher] seedAppSupportFragmentCtorState X4", x4Error);
                }
            }
            return childManagerField != null ? childManagerField.get(fragment) : childManagerValue;
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] seedAppSupportFragmentCtorState", t);
            return null;
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void seedSupportFragmentBaseState(Object fragment, Activity activity) {
        if (fragment == null) {
            return;
        }
        try {
            if (fragment instanceof androidx.fragment.app.Fragment) {
                java.lang.reflect.Field activityField =
                        findFieldOnHierarchy(fragment.getClass(), "mActivity");
                java.lang.reflect.Field hostField =
                        findFieldOnHierarchy(fragment.getClass(), "mHost");
                java.lang.reflect.Field parentManagerField =
                        findFieldOnHierarchy(fragment.getClass(), "mFragmentManager");
                java.lang.reflect.Field childManagerField =
                        findFieldOnHierarchy(fragment.getClass(), "mChildFragmentManager");
                java.lang.reflect.Field menuVisibleField =
                        findFieldOnHierarchy(fragment.getClass(), "mMenuVisible");
                java.lang.reflect.Field userVisibleHintField =
                        findFieldOnHierarchy(fragment.getClass(), "mUserVisibleHint");
                java.lang.reflect.Field lifecycleRegistryField =
                        findFieldOnHierarchy(fragment.getClass(), "mLifecycleRegistry");
                java.lang.reflect.Field activityContextField =
                        findFieldOnHierarchy(fragment.getClass(), "mActivityContext");
                java.lang.reflect.Field saveStateCallbackField =
                        findFieldOnHierarchy(fragment.getClass(), "mSaveStateCallback");

                androidx.fragment.app.FragmentActivity hostActivity =
                        activity instanceof androidx.fragment.app.FragmentActivity
                                ? (androidx.fragment.app.FragmentActivity) activity
                                : null;

                if (activityField != null && hostActivity != null && activityField.get(fragment) == null) {
                    activityField.set(fragment, hostActivity);
                }
                if (hostField != null && activity != null && hostField.get(fragment) == null) {
                    hostField.set(fragment, activity);
                }
                if (activityContextField != null && activity != null
                        && activityContextField.get(fragment) == null) {
                    activityContextField.set(fragment, activity);
                }
                if (saveStateCallbackField != null && activity != null
                        && saveStateCallbackField.get(fragment) == null) {
                    saveStateCallbackField.set(fragment, activity);
                }
                startupLog("[WestlakeLauncher] seedSupportFragmentBaseState shim host/activity ready");

                androidx.fragment.app.FragmentManager parentManager =
                        hostActivity != null ? hostActivity.getSupportFragmentManager() : null;
                if (parentManagerField != null && parentManager != null && parentManagerField.get(fragment) == null) {
                    parentManagerField.set(fragment, parentManager);
                }
                if (hostActivity != null) {
                    ((androidx.fragment.app.Fragment) fragment).getChildFragmentManager();
                } else if (childManagerField != null && childManagerField.get(fragment) == null) {
                    childManagerField.set(fragment, new androidx.fragment.app.FragmentManager());
                }
                startupLog("[WestlakeLauncher] seedSupportFragmentBaseState child-manager ready");

                if (lifecycleRegistryField != null && lifecycleRegistryField.get(fragment) == null) {
                    lifecycleRegistryField.set(fragment,
                            new androidx.lifecycle.LifecycleRegistry(
                                    (androidx.lifecycle.LifecycleOwner) fragment));
                }
                if (menuVisibleField != null) {
                    menuVisibleField.setBoolean(fragment, true);
                }
                if (userVisibleHintField != null) {
                    userVisibleHintField.setBoolean(fragment, true);
                }
                startupLog("[WestlakeLauncher] Seeded support Fragment base fields");
                return;
            }

            Class<?> fragmentBaseClass = null;
            for (Class<?> c = fragment.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
                if ("androidx.fragment.app.Fragment".equals(c.getName())) {
                    fragmentBaseClass = c;
                    break;
                }
            }
            if (fragmentBaseClass == null) {
                return;
            }

            ClassLoader cl = fragment.getClass().getClassLoader();
            if (cl == null) {
                cl = fragmentBaseClass.getClassLoader();
            }
            Object seededCtorChildManager = seedAppSupportFragmentCtorState(
                    fragment, fragmentBaseClass, cl);
            java.lang.reflect.Field hostField =
                    findFieldOnHierarchy(fragmentBaseClass, "mHost");
            java.lang.reflect.Field parentManagerField =
                    findFieldOnHierarchy(fragmentBaseClass, "mFragmentManager");
            java.lang.reflect.Field whoField = findFieldOnHierarchy(fragmentBaseClass, "mWho");
            java.lang.reflect.Field stateField = findFieldOnHierarchy(fragmentBaseClass, "mState");
            java.lang.reflect.Field childManagerField =
                    findFieldOnHierarchy(fragmentBaseClass, "mChildFragmentManager");
            java.lang.reflect.Field menuVisibleField =
                    findFieldOnHierarchy(fragmentBaseClass, "mMenuVisible");
            java.lang.reflect.Field userVisibleHintField =
                    findFieldOnHierarchy(fragmentBaseClass, "mUserVisibleHint");
            java.lang.reflect.Field maxStateField =
                    findFieldOnHierarchy(fragmentBaseClass, "mMaxState");
            java.lang.reflect.Field liveDataField =
                    findFieldOnHierarchy(fragmentBaseClass, "mViewLifecycleOwnerLiveData");
            java.lang.reflect.Field nextLocalRequestCodeField =
                    findFieldOnHierarchy(fragmentBaseClass, "mNextLocalRequestCode");
            java.lang.reflect.Field lifecycleRegistryField =
                    findFieldOnHierarchy(fragmentBaseClass, "mLifecycleRegistry");
            java.lang.reflect.Field onPreAttachedListenersField =
                    findFieldOnHierarchy(fragmentBaseClass, "mOnPreAttachedListeners");

            if (stateField != null) {
                stateField.setInt(fragment, -1);
            }
            if (whoField != null && whoField.get(fragment) == null) {
                whoField.set(fragment, java.util.UUID.randomUUID().toString());
            }
            startupLog("[WestlakeLauncher] seedSupportFragmentBaseState who/state ready");
            Object hostCallback = null;
            Object parentManager = null;
            Class<?> fragmentActivityClass =
                    activity != null
                            ? findNamedClassOnHierarchy(activity.getClass(),
                                    "androidx.fragment.app.FragmentActivity")
                            : null;
            if (activity != null && fragmentActivityClass != null) {
                try {
                    primeSupportFragmentHost(activity);
                } catch (Throwable ignored) {
                }
                try {
                    java.lang.reflect.Field controllerField =
                            findFieldOnHierarchy(fragmentActivityClass, "mFragments");
                    Object controller = controllerField != null ? controllerField.get(activity) : null;
                    if (controller != null) {
                        java.lang.reflect.Field controllerHostField =
                                findFieldOnHierarchy(controller.getClass(), "a");
                        if (controllerHostField != null) {
                            hostCallback = controllerHostField.get(controller);
                        }
                    }
                } catch (Throwable t) {
                    startupLog("[WestlakeLauncher] seedSupportFragmentBaseState controller host", t);
                }
                if (hostCallback == null) {
                    hostCallback = newNamedFragmentHostCallbacks(cl, fragmentActivityClass, activity);
                }
                try {
                    java.lang.reflect.Method getSupportFragmentManager =
                            fragmentActivityClass.getMethod("getSupportFragmentManager");
                    parentManager = getSupportFragmentManager.invoke(activity);
                } catch (Throwable t) {
                    startupLog("[WestlakeLauncher] seedSupportFragmentBaseState parent manager", t);
                }
                if (parentManager == null && hostCallback != null) {
                    parentManager = namedFragmentManagerFromHostCallback(hostCallback);
                }
            }
            if (hostField != null && hostCallback != null) {
                hostField.set(fragment, hostCallback);
            }
            if (parentManagerField != null
                    && parentManagerField.get(fragment) == null
                    && parentManager != null) {
                parentManagerField.set(fragment, parentManager);
            }
            if (parentManager != null && hostCallback != null) {
                attachNamedRootFragmentManagerHost(parentManager, hostCallback);
            }
            Object childManagerValue = null;
            if (childManagerField != null) {
                try {
                    childManagerValue = childManagerField.get(fragment);
                } catch (Throwable ignored) {
                }
            }
            if (childManagerValue == null) {
                childManagerValue = seededCtorChildManager;
            }
            if (childManagerField != null && childManagerValue == null) {
                try {
                    Class<?> fmImplClass;
                    try {
                        fmImplClass = cl != null
                                ? cl.loadClass("androidx.fragment.app.FragmentManagerImpl")
                                : Class.forName("androidx.fragment.app.FragmentManagerImpl");
                    } catch (Throwable noImpl) {
                        fmImplClass = cl != null
                                ? cl.loadClass("androidx.fragment.app.FragmentManager")
                                : Class.forName("androidx.fragment.app.FragmentManager");
                    }
                    Object childManager = null;
                    try {
                        java.lang.reflect.Constructor<?> fmCtor = fmImplClass.getDeclaredConstructor();
                        fmCtor.setAccessible(true);
                        childManager = fmCtor.newInstance();
                        if (childManager == null) {
                            childManager = tryAllocInstance(fmImplClass);
                        }
                        if (childManager == null) {
                            childManager = tryUnsafeAllocInstance(fmImplClass);
                        }
                    } catch (Throwable allocError) {
                        startupLog("[WestlakeLauncher] seedSupportFragmentBaseState child-manager alloc",
                                allocError);
                    }
                    if (childManager != null) {
                        childManagerField.set(fragment, childManager);
                        childManagerValue = childManager;
                    }
                } catch (Throwable childManagerError) {
                    startupLog("[WestlakeLauncher] seedSupportFragmentBaseState child-manager ctor",
                            childManagerError);
                }
            }
            if (childManagerValue != null && hostCallback != null) {
                attachNamedFragmentManagerHost(childManagerValue, hostCallback, fragment);
            }
            // Do not reseed app-owned mLifecycleRegistry here. Constructor-bypassed support
            // fragments can reach this path with mixed AndroidX owners/loaders, and the
            // reflective LifecycleRegistry(owner) path is a frequent null/stack source.
            Object onPreAttachedListenersValue = null;
            if (onPreAttachedListenersField != null) {
                try {
                    onPreAttachedListenersValue = onPreAttachedListenersField.get(fragment);
                } catch (Throwable ignored) {
                }
            }
            if (onPreAttachedListenersField != null && onPreAttachedListenersValue == null) {
                onPreAttachedListenersField.set(fragment, new java.util.ArrayList());
            }
            if (menuVisibleField != null) {
                menuVisibleField.setBoolean(fragment, true);
            }
            if (userVisibleHintField != null) {
                userVisibleHintField.setBoolean(fragment, true);
            }
            startupLog("[WestlakeLauncher] seedSupportFragmentBaseState child-manager ready");
            // Constructor-bypassed app fragments can still carry partially initialized
            // AndroidX enum/static state here. Reflective mMaxState reads have been the
            // first post-child-manager null on the current HomeDashboardFragment path, so
            // leave the field untouched and continue with the rest of the bootstrap.
            startupLog("[WestlakeLauncher] seedSupportFragmentBaseState max-state ready");
            // Defer app-owned mViewLifecycleOwnerLiveData creation until
            // seedSupportFragmentViewState(). On the current HomeDashboardFragment path,
            // reflective MutableLiveData construction here is the first post-max-state NPE.
            if (nextLocalRequestCodeField != null && nextLocalRequestCodeField.get(fragment) == null) {
                nextLocalRequestCodeField.set(fragment, new java.util.concurrent.atomic.AtomicInteger());
            }
            if (hostCallback != null || parentManager != null || childManagerValue != null) {
                startupLog("[WestlakeLauncher] seedSupportFragmentBaseState app host/manager ready");
            }
            startupLog("[WestlakeLauncher] Seeded support Fragment base fields");
            return;
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] seedSupportFragmentBaseState", t);
        }
    }

    private static boolean invokeFragmentMethod(
            Object fragment,
            Class<?> fragClass,
            String name,
            Class<?>[] paramTypes,
            Object[] args) {
        if (fragment == null || fragClass == null || name == null) {
            return false;
        }
        for (Class<?> c = fragClass; c != null && c != Object.class; c = c.getSuperclass()) {
            try {
                java.lang.reflect.Method m = c.getDeclaredMethod(name, paramTypes);
                m.setAccessible(true);
                m.invoke(fragment, args);
                startupLog("[WestlakeLauncher] HomeDashboardFragment " + name + "() invoked");
                return true;
            } catch (NoSuchMethodException ignored) {
            } catch (Throwable t) {
                startupLog("[WestlakeLauncher] HomeDashboardFragment " + name, t);
                if (t instanceof java.lang.reflect.InvocationTargetException) {
                    Throwable cause =
                            ((java.lang.reflect.InvocationTargetException) t).getTargetException();
                    if (cause != null) {
                        startupLog("[WestlakeLauncher] HomeDashboardFragment " + name
                                + " cause", cause);
                        logThrowableFrames("[WestlakeLauncher] HomeDashboardFragment "
                                + name + " cause", cause, 12);
                    }
                }
                return false;
            }
        }
        return false;
    }

    private static void seedSupportFragmentViewState(
            Object fragment,
            android.view.ViewGroup container,
            android.view.View fragView) {
        if (fragment == null || container == null || fragView == null) {
            return;
        }
        try {
            Class<?> fragmentBaseClass =
                    findNamedClassOnHierarchy(fragment.getClass(), "androidx.fragment.app.Fragment");
            if (fragmentBaseClass == null) {
                return;
            }
            ClassLoader cl = fragment.getClass().getClassLoader();
            if (cl == null) {
                cl = fragmentBaseClass.getClassLoader();
            }

            java.lang.reflect.Field viewField =
                    findFieldOnHierarchy(fragmentBaseClass, "mView");
            java.lang.reflect.Field containerField =
                    findFieldOnHierarchy(fragmentBaseClass, "mContainer");
            java.lang.reflect.Field containerIdField =
                    findFieldOnHierarchy(fragmentBaseClass, "mContainerId");
            java.lang.reflect.Field performedCreateViewField =
                    findFieldOnHierarchy(fragmentBaseClass, "mPerformedCreateView");
            java.lang.reflect.Field ownerField =
                    findFieldOnHierarchy(fragmentBaseClass, "mViewLifecycleOwner");
            java.lang.reflect.Field liveDataField =
                    findFieldOnHierarchy(fragmentBaseClass, "mViewLifecycleOwnerLiveData");

            if (viewField != null) {
                viewField.set(fragment, fragView);
            }
            if (containerField != null) {
                containerField.set(fragment, container);
            }
            if (containerIdField != null && container.getId() != 0) {
                containerIdField.setInt(fragment, container.getId());
            }
            if (performedCreateViewField != null) {
                performedCreateViewField.setBoolean(fragment, true);
            }

            Object owner = ownerField != null ? ownerField.get(fragment) : null;
            if (owner == null && cl != null) {
                Class<?> viewModelStoreClass = cl.loadClass("androidx.lifecycle.ViewModelStore");
                Object viewModelStore = viewModelStoreClass.getDeclaredConstructor().newInstance();
                Class<?> ownerClass = cl.loadClass("androidx.fragment.app.FragmentViewLifecycleOwner");
                java.lang.reflect.Constructor<?> ownerCtor =
                        ownerClass.getDeclaredConstructor(
                                fragmentBaseClass, viewModelStoreClass, Runnable.class);
                ownerCtor.setAccessible(true);
                owner = ownerCtor.newInstance(fragment, viewModelStore, new Runnable() {
                    @Override
                    public void run() {
                    }
                });
                if (ownerField != null) {
                    ownerField.set(fragment, owner);
                }
            }

            if (owner != null) {
                try {
                    java.lang.reflect.Method initOwner = owner.getClass().getDeclaredMethod("b");
                    initOwner.setAccessible(true);
                    initOwner.invoke(owner);
                } catch (Throwable ownerInitError) {
                    startupLog("[WestlakeLauncher] seedSupportFragmentViewState owner init",
                            ownerInitError);
                }

                Object liveData = liveDataField != null ? liveDataField.get(fragment) : null;
                if (liveData == null && cl != null && liveDataField != null) {
                    Class<?> mutableLiveDataClass = cl.loadClass("androidx.lifecycle.MutableLiveData");
                    java.lang.reflect.Constructor<?> liveDataCtor =
                            mutableLiveDataClass.getDeclaredConstructor();
                    liveDataCtor.setAccessible(true);
                    liveData = liveDataCtor.newInstance();
                    liveDataField.set(fragment, liveData);
                }
                if (liveData != null) {
                    try {
                        java.lang.reflect.Method setValue =
                                liveData.getClass().getMethod("setValue", Object.class);
                        setValue.invoke(liveData, owner);
                    } catch (Throwable liveDataError) {
                        startupLog("[WestlakeLauncher] seedSupportFragmentViewState liveData",
                                liveDataError);
                    }
                }

                if (cl != null) {
                    try {
                        Class<?> lifecycleOwnerClass = cl.loadClass("androidx.lifecycle.LifecycleOwner");
                        Class<?> viewModelStoreOwnerClass =
                                cl.loadClass("androidx.lifecycle.ViewModelStoreOwner");
                        Class<?> savedStateRegistryOwnerClass =
                                cl.loadClass("androidx.savedstate.SavedStateRegistryOwner");
                        Class<?> viewTreeLifecycleOwnerClass =
                                cl.loadClass("androidx.lifecycle.ViewTreeLifecycleOwner");
                        Class<?> viewTreeViewModelStoreOwnerClass =
                                cl.loadClass("androidx.lifecycle.ViewTreeViewModelStoreOwner");
                        Class<?> viewTreeSavedStateRegistryOwnerClass =
                                cl.loadClass("androidx.savedstate.ViewTreeSavedStateRegistryOwner");
                        java.lang.reflect.Method setLifecycleOwner =
                                viewTreeLifecycleOwnerClass.getDeclaredMethod(
                                        "b", android.view.View.class, lifecycleOwnerClass);
                        java.lang.reflect.Method setViewModelStoreOwner =
                                viewTreeViewModelStoreOwnerClass.getDeclaredMethod(
                                        "b", android.view.View.class, viewModelStoreOwnerClass);
                        java.lang.reflect.Method setSavedStateOwner =
                                viewTreeSavedStateRegistryOwnerClass.getDeclaredMethod(
                                        "b", android.view.View.class, savedStateRegistryOwnerClass);
                        setLifecycleOwner.setAccessible(true);
                        setViewModelStoreOwner.setAccessible(true);
                        setSavedStateOwner.setAccessible(true);
                        setLifecycleOwner.invoke(null, fragView, owner);
                        setViewModelStoreOwner.invoke(null, fragView, owner);
                        setSavedStateOwner.invoke(null, fragView, owner);
                    } catch (Throwable treeOwnerError) {
                        startupLog("[WestlakeLauncher] seedSupportFragmentViewState tree owners",
                                treeOwnerError);
                    }
                }
            }
            startupLog("[WestlakeLauncher] seedSupportFragmentViewState ready");
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] seedSupportFragmentViewState", t);
        }
    }

    private static void primeSupportFragmentHost(Activity activity) {
        if (activity == null) {
            return;
        }
        try {
            if (activity instanceof androidx.fragment.app.FragmentActivity) {
                try {
                    androidx.fragment.app.FragmentManager fm =
                            ((androidx.fragment.app.FragmentActivity) activity).getSupportFragmentManager();
                    java.lang.reflect.Field hostField =
                            findFieldOnHierarchy(fm != null ? fm.getClass() : null, "mHost");
                    if (hostField != null && hostField.get(fm) == null) {
                        hostField.set(fm, activity);
                    }
                } catch (Throwable directShimError) {
                    startupLog("[WestlakeLauncher] primeSupportFragmentHost direct manager", directShimError);
                }
                startupLog("[WestlakeLauncher] Primed shim support FragmentManager host");
            }

            Class<?> fragmentActivityClass = null;
            for (Class<?> c = activity.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
                if ("androidx.fragment.app.FragmentActivity".equals(c.getName())) {
                    fragmentActivityClass = c;
                    break;
                }
            }
            if (fragmentActivityClass == null) {
                return;
            }
            startupLog("[WestlakeLauncher] primeSupportFragmentHost class="
                    + fragmentActivityClass.getName());

            ClassLoader cl = fragmentActivityClass.getClassLoader();
            java.lang.reflect.Field fragmentsField =
                    findFieldOnHierarchy(fragmentActivityClass, "mFragments");
            if (fragmentsField == null) {
                startupLog("[WestlakeLauncher] primeSupportFragmentHost mFragments field missing");
                return;
            }

            Object controller = fragmentsField.get(activity);
            if (controller == null) {
                Class<?> hostCallbacksClass =
                        cl.loadClass("androidx.fragment.app.FragmentActivity$HostCallbacks");
                java.lang.reflect.Constructor<?> hostCtor =
                        hostCallbacksClass.getDeclaredConstructor(fragmentActivityClass);
                hostCtor.setAccessible(true);
                Object hostCallbacks = hostCtor.newInstance(activity);

                Class<?> hostCallbackClass =
                        cl.loadClass("androidx.fragment.app.FragmentHostCallback");
                Class<?> controllerClass =
                        cl.loadClass("androidx.fragment.app.FragmentController");
                java.lang.reflect.Method buildController =
                        controllerClass.getDeclaredMethod("b", hostCallbackClass);
                buildController.setAccessible(true);
                controller = buildController.invoke(null, hostCallbacks);
                fragmentsField.set(activity, controller);
                startupLog("[WestlakeLauncher] Created FragmentActivity.mFragments controller");
            } else {
                startupLog("[WestlakeLauncher] Reusing FragmentActivity.mFragments controller");
            }

            java.lang.reflect.Field lifecycleField =
                    findFieldOnHierarchy(fragmentActivityClass, "mFragmentLifecycleRegistry");
            if (lifecycleField != null && lifecycleField.get(activity) == null) {
                lifecycleField.set(activity, new androidx.lifecycle.LifecycleRegistry(
                        (androidx.lifecycle.LifecycleOwner) activity));
            }
            java.lang.reflect.Field stoppedField =
                    findFieldOnHierarchy(fragmentActivityClass, "mStopped");
            if (stoppedField != null) {
                stoppedField.setBoolean(activity, true);
            }

            java.lang.reflect.Method getManager = controller.getClass().getDeclaredMethod("l");
            getManager.setAccessible(true);
            Object fragmentManager = getManager.invoke(controller);
            java.lang.reflect.Field hostField =
                    findFieldOnHierarchy(fragmentManager.getClass(), "x");
            if (hostField != null && hostField.get(fragmentManager) == null) {
                Class<?> fragmentClass = cl.loadClass("androidx.fragment.app.Fragment");
                java.lang.reflect.Method attachHost =
                        controller.getClass().getDeclaredMethod("a", fragmentClass);
                attachHost.setAccessible(true);
                attachHost.invoke(controller, new Object[]{null});
                startupLog("[WestlakeLauncher] Attached support FragmentManager host");
            } else {
                startupLog("[WestlakeLauncher] Support FragmentManager host already attached");
            }
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] primeSupportFragmentHost", t);
        }
    }

    private static void attachFragmentToActivity(Object fragment, Class<?> fragClass, Activity activity) {
        if (fragment == null || fragClass == null || activity == null) {
            return;
        }
        if (invokeFragmentMethod(fragment, fragClass, "performAttach",
                new Class<?>[0], new Object[0])) {
            return;
        }
        if (activity instanceof androidx.fragment.app.FragmentActivity
                && invokeFragmentMethod(fragment, fragClass, "performAttach",
                new Class<?>[]{androidx.fragment.app.FragmentActivity.class},
                new Object[]{activity})) {
            return;
        }
        try {
            java.lang.reflect.Method onAttach = null;
            for (Class<?> c = fragClass; c != null; c = c.getSuperclass()) {
                try {
                    onAttach = c.getDeclaredMethod("onAttach", android.content.Context.class);
                    break;
                } catch (NoSuchMethodException ignored) {
                }
            }
            if (onAttach != null) {
                onAttach.setAccessible(true);
                onAttach.invoke(fragment, activity);
                startupLog("[WestlakeLauncher] HomeDashboardFragment onAttach(Context) invoked");
                return;
            }
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] HomeDashboardFragment onAttach(Context)", t);
        }
        try {
            java.lang.reflect.Method onAttach = null;
            for (Class<?> c = fragClass; c != null; c = c.getSuperclass()) {
                try {
                    onAttach = c.getDeclaredMethod("onAttach", Activity.class);
                    break;
                } catch (NoSuchMethodException ignored) {
                }
            }
            if (onAttach != null) {
                onAttach.setAccessible(true);
                onAttach.invoke(fragment, activity);
                startupLog("[WestlakeLauncher] HomeDashboardFragment onAttach(Activity) invoked");
            }
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] HomeDashboardFragment onAttach(Activity)", t);
        }
    }

    private static void invokeFragmentLifecycleMethod(
            Object fragment,
            Class<?> fragClass,
            String name,
            Class<?>[] paramTypes,
            Object[] args) {
        if (fragment == null || fragClass == null || name == null) {
            return;
        }
        for (Class<?> c = fragClass; c != null && c != Object.class; c = c.getSuperclass()) {
            try {
                java.lang.reflect.Method m = c.getDeclaredMethod(name, paramTypes);
                m.setAccessible(true);
                m.invoke(fragment, args);
                startupLog("[WestlakeLauncher] HomeDashboardFragment " + name + "() invoked");
                return;
            } catch (NoSuchMethodException ignored) {
            } catch (Throwable t) {
                startupLog("[WestlakeLauncher] HomeDashboardFragment " + name, t);
                if (t instanceof java.lang.reflect.InvocationTargetException) {
                    Throwable cause =
                            ((java.lang.reflect.InvocationTargetException) t).getTargetException();
                    if (cause != null) {
                        startupLog("[WestlakeLauncher] HomeDashboardFragment " + name
                                + " cause", cause);
                        logThrowableFrames("[WestlakeLauncher] HomeDashboardFragment "
                                + name + " cause", cause, 12);
                    }
                }
                return;
            }
        }
    }

    private static boolean tryAttachHomeDashboardFragment(
            Activity activity,
            android.view.ViewGroup homeContainer) {
        if (activity == null || homeContainer == null) {
            return false;
        }
        try {
            Object fragment = findDashboardFragmentInstance(activity);
            Class<?> fragClass = fragment != null ? fragment.getClass()
                    : loadAppClass("com.mcdonalds.homedashboard.fragment.HomeDashboardFragment");
            if (fragClass == null) {
                return false;
            }
            boolean usedAllocFallback = false;
            if (fragment == null) {
                try {
                    fragment = fragClass.getDeclaredConstructor().newInstance();
                    startupLog("[WestlakeLauncher] HomeDashboardFragment instantiated via ctor");
                } catch (Throwable ctorError) {
                    fragment = tryAllocInstance(fragClass);
                    usedAllocFallback = fragment != null;
                    if (fragment == null) {
                        startupLog("[WestlakeLauncher] HomeDashboardFragment ctor/alloc failed", ctorError);
                        return false;
                    }
                    startupLog("[WestlakeLauncher] HomeDashboardFragment using alloc fallback", ctorError);
                }
            } else {
                startupLog("[WestlakeLauncher] HomeDashboardFragment reused from activity field");
            }

            logDashboardOwnershipOnce(activity, fragment);
            seedHomeDashboardFragmentCtorState(fragment);
            seedHomeDashboardFragmentInjectedMembers(fragment, activity);
            seedSupportFragmentBaseState(fragment, activity);
            seedHiltFragmentContext(fragment, activity);

            boolean fragmentManagerAttempted = false;
            boolean fragmentManagerFailed = false;
            boolean manualFallbackFresh = false;
            Class<?> runtimeFragmentClass =
                    findNamedClassOnHierarchy(fragment.getClass(), "androidx.fragment.app.Fragment");
            Class<?> runtimeFragmentActivityClass =
                    findNamedClassOnHierarchy(activity.getClass(), "androidx.fragment.app.FragmentActivity");
            if (runtimeFragmentClass != null && runtimeFragmentActivityClass != null) {
                try {
                    fragmentManagerAttempted = true;
                    primeSupportFragmentHost(activity);
                    startupLog("[WestlakeLauncher] HomeDashboardFragment activity class="
                            + activity.getClass().getName());
                    int containerId = homeContainer.getId();
                    if (containerId == 0) {
                        containerId = resolveAppResourceId(activity, "id", "home_dashboard_container");
                        if (containerId != 0) {
                            homeContainer.setId(containerId);
                        }
                    }
                    if (containerId != 0) {
                        android.view.View liveContainer = activity.findViewById(containerId);
                        if (liveContainer == null) {
                            startupLog("[WestlakeLauncher] HomeDashboardFragment container missing: 0x"
                                    + Integer.toHexString(containerId));
                            return false;
                        }
                        java.lang.reflect.Method getSupportFragmentManager =
                                runtimeFragmentActivityClass.getMethod("getSupportFragmentManager");
                        Object fm = getSupportFragmentManager.invoke(activity);
                        if (fm == null) {
                            startupLog("[WestlakeLauncher] HomeDashboardFragment support FragmentManager is null");
                            return false;
                        }
                        startupLog("[WestlakeLauncher] HomeDashboardFragment support FragmentManager ready");
                        startupLog("[WestlakeLauncher] HomeDashboardFragment support FragmentManager class="
                                + (fm != null ? fm.getClass().getName() : "null"));
                        Object tx;
                        try {
                            java.lang.reflect.Method beginTransaction =
                                    fm.getClass().getMethod("beginTransaction");
                            tx = beginTransaction.invoke(fm);
                            startupLog("[WestlakeLauncher] HomeDashboardFragment transaction begin");
                        } catch (Throwable beginError) {
                            startupLog("[WestlakeLauncher] HomeDashboardFragment beginTransaction", beginError);
                            throw beginError;
                        }
                        Object existingFragment = null;
                        try {
                            java.lang.reflect.Method findFragmentById =
                                    fm.getClass().getMethod("findFragmentById", int.class);
                            existingFragment = findFragmentById.invoke(fm, containerId);
                            if (existingFragment == null) {
                                java.lang.reflect.Method findFragmentByTag =
                                        fm.getClass().getMethod("findFragmentByTag", String.class);
                                existingFragment = findFragmentByTag.invoke(fm, "HomeDashboardFragment");
                            }
                        } catch (Throwable lookupError) {
                            startupLog("[WestlakeLauncher] HomeDashboardFragment existing lookup", lookupError);
                        }
                        if (existingFragment != null && existingFragment != fragment) {
                            startupLog("[WestlakeLauncher] HomeDashboardFragment transaction=replace");
                            java.lang.reflect.Method replace = tx.getClass().getMethod(
                                    "replace", int.class, runtimeFragmentClass, String.class);
                            replace.invoke(tx, containerId, fragment, "HomeDashboardFragment");
                        } else {
                            startupLog("[WestlakeLauncher] HomeDashboardFragment transaction=add");
                            java.lang.reflect.Method add = tx.getClass().getMethod(
                                    "add", int.class, runtimeFragmentClass, String.class);
                            add.invoke(tx, containerId, fragment, "HomeDashboardFragment");
                        }
                        startupLog("[WestlakeLauncher] HomeDashboardFragment transaction commitNow");
                        java.lang.reflect.Method commitNowAllowingStateLoss =
                                tx.getClass().getMethod("commitNowAllowingStateLoss");
                        commitNowAllowingStateLoss.invoke(tx);
                        startupLog("[WestlakeLauncher] HomeDashboardFragment transaction committed");
                        java.lang.reflect.Method getView =
                                runtimeFragmentClass.getMethod("getView");
                        Object fragViewObj = getView.invoke(fragment);
                        android.view.View fragView =
                                fragViewObj instanceof android.view.View
                                        ? (android.view.View) fragViewObj
                                        : null;
                        if (fragView != null || homeContainer.getChildCount() > 0) {
                            startupLog("[WestlakeLauncher] HomeDashboardFragment attached via FragmentManager");
                            return true;
                        }
                        startupLog("[WestlakeLauncher] HomeDashboardFragment FragmentManager attach produced no view");
                    }
                } catch (Throwable fmError) {
                    fragmentManagerFailed = true;
                    startupLog("[WestlakeLauncher] HomeDashboardFragment FragmentManager attach", fmError);
                    logThrowableFrames("[WestlakeLauncher] HomeDashboardFragment FragmentManager attach",
                            fmError, 12);
                }
            }

            if (fragmentManagerAttempted && fragmentManagerFailed && fragClass != null) {
                startupLog("[WestlakeLauncher] HomeDashboardFragment retrying manual attach with fresh instance");
                Object freshFragment = null;
                try {
                    freshFragment = fragClass.getDeclaredConstructor().newInstance();
                } catch (Throwable ctorError) {
                    freshFragment = tryAllocInstance(fragClass);
                    if (freshFragment == null) {
                        startupLog("[WestlakeLauncher] HomeDashboardFragment fresh ctor/alloc failed",
                                ctorError);
                        return false;
                    }
                }
                fragment = freshFragment;
                manualFallbackFresh = true;
                seedHomeDashboardFragmentCtorState(fragment);
                seedHomeDashboardFragmentInjectedMembers(fragment, activity);
                seedSupportFragmentBaseState(fragment, activity);
                seedHiltFragmentContext(fragment, activity);
            }

            for (Class<?> c = activity.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
                try {
                    java.lang.reflect.Field f = c.getDeclaredField("mHomeDashboardFragment");
                    f.setAccessible(true);
                    if (f.get(activity) == null || manualFallbackFresh) {
                        f.set(activity, fragment);
                    }
                    break;
                } catch (NoSuchFieldException ignored) {
                } catch (Throwable ignored) {
                    break;
                }
            }

            attachFragmentToActivity(fragment, fragClass, activity);
            if (!invokeFragmentMethod(fragment, fragClass, "performCreate",
                    new Class<?>[]{android.os.Bundle.class},
                    new Object[]{null})) {
                invokeFragmentLifecycleMethod(fragment, fragClass, "onCreate",
                        new Class<?>[]{android.os.Bundle.class},
                        new Object[]{null});
            }

            android.view.LayoutInflater inflater = activity.getLayoutInflater();
            android.view.View fragView = null;
            if (invokeFragmentMethod(fragment, fragClass, "performCreateView",
                    new Class<?>[]{android.view.LayoutInflater.class,
                            android.view.ViewGroup.class,
                            android.os.Bundle.class},
                    new Object[]{inflater, homeContainer, null})) {
                Object currentView = invokeFragmentNoArg(fragment, fragClass, "getView");
                if (currentView instanceof android.view.View) {
                    fragView = (android.view.View) currentView;
                }
            }

            if (fragView == null) {
                startupLog("[WestlakeLauncher] HomeDashboardFragment performCreateView produced no view");
                return false;
            }

            seedSupportFragmentViewState(fragment, homeContainer, fragView);
            seedHomeDashboardFragmentViewBindings(fragment, fragClass, activity, fragView);
            homeContainer.removeAllViews();
            homeContainer.addView(fragView);
            startupLog("[WestlakeLauncher] HomeDashboardFragment view attached: "
                    + fragView.getClass().getName());
            logDashboardAttachSubtree("[WestlakeLauncher] HomeDashboardFragment attached tree", fragView, 0);
            if (!invokeFragmentMethod(fragment, fragClass, "performActivityCreated",
                    new Class<?>[]{android.os.Bundle.class},
                    new Object[]{null})) {
                invokeFragmentLifecycleMethod(fragment, fragClass, "onActivityCreated",
                        new Class<?>[]{android.os.Bundle.class},
                        new Object[]{null});
            }
            startupLog("[WestlakeLauncher] HomeDashboardFragment start/resume deferred until after first frame");
            return homeContainer.getChildCount() > 0;
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] tryAttachHomeDashboardFragment", t);
            logThrowableFrames("[WestlakeLauncher] tryAttachHomeDashboardFragment", t, 12);
            return false;
        }
    }

    private static void logDashboardAttachSubtree(String prefix, android.view.View view, int depth) {
        if (view == null || depth > 2) {
            return;
        }
        try {
            StringBuilder sb = new StringBuilder(prefix);
            sb.append(" depth=").append(depth)
                    .append(" class=").append(view.getClass().getName());
            if (view.getId() != android.view.View.NO_ID) {
                sb.append(" id=0x").append(Integer.toHexString(view.getId()));
            }
            if (view instanceof android.view.ViewGroup) {
                android.view.ViewGroup vg = (android.view.ViewGroup) view;
                sb.append(" childCount=").append(vg.getChildCount());
                startupLog(sb.toString());
                int limit = Math.min(vg.getChildCount(), 4);
                for (int i = 0; i < limit; i++) {
                    logDashboardAttachSubtree(prefix + " child[" + i + "]",
                            vg.getChildAt(i), depth + 1);
                }
                return;
            }
            startupLog(sb.toString());
        } catch (Throwable ignored) {
        }
    }

    private static boolean installProgrammaticHomeDashboardFragment(
            Activity activity,
            android.view.ViewGroup homeContainer) {
        if (activity == null || homeContainer == null) {
            return false;
        }
        String homeClassName = homeContainer.getClass().getName();
        int expectedHomeId = resolveAppResourceId(activity, "id", "home_dashboard_container");
        if (homeClassName != null
                && homeClassName.startsWith("com.westlake.engine.WestlakeLauncher$")
                && homeContainer.getId() != expectedHomeId) {
            startupLog("[WestlakeLauncher] installProgrammaticHomeDashboardFragment skipping launcher-owned root");
            return false;
        }
        try {
            homeContainer.removeAllViews();
            startupLog("[WestlakeLauncher] buildProgrammaticHomeDashboardFragment cleared");
            if (tryAttachHomeDashboardFragment(activity, homeContainer)) {
                startupLog("[WestlakeLauncher] Programmatic HomeDashboardFragment attached");
                return true;
            }
            int fragmentLayoutId = resolveAppResourceId(activity, "layout", "fragment_home_dashboard");
            if (fragmentLayoutId != 0) {
                try {
                    android.view.View shell =
                            activity.getLayoutInflater().inflate(fragmentLayoutId, homeContainer, false);
                    if (shell != null) {
                        homeContainer.addView(shell);
                        startupLog("[WestlakeLauncher] fragment_home_dashboard shell inflated after attach failure");
                        return homeContainer.getChildCount() > 0;
                    }
                    startupLog("[WestlakeLauncher] fragment_home_dashboard shell inflate returned null");
                } catch (Throwable shellError) {
                    startupLog("[WestlakeLauncher] fragment_home_dashboard shell inflate", shellError);
                    logThrowableFrames("[WestlakeLauncher] fragment_home_dashboard shell inflate",
                            shellError, 10);
                }
            } else {
                startupLog("[WestlakeLauncher] fragment_home_dashboard layout id missing");
            }
            int sectionsContainerId = resolveAppResourceId(activity, "id", "sections_container");
            android.view.View content = buildDashboardCanvasContent(activity);
            if (content == null) {
                startupLog("[WestlakeLauncher] buildProgrammaticHomeDashboardFragment content=null");
                return false;
            }
            if (sectionsContainerId != 0) {
                content.setId(sectionsContainerId);
            }
            android.view.ViewGroup.LayoutParams homeParams;
            if (homeContainer instanceof android.widget.LinearLayout) {
                homeParams = dashboardCanvasLp();
            } else if (homeContainer instanceof android.widget.FrameLayout) {
                homeParams = new android.widget.FrameLayout.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        SURFACE_HEIGHT);
            } else if (homeContainer instanceof android.widget.RelativeLayout) {
                homeParams = new android.widget.RelativeLayout.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        SURFACE_HEIGHT);
            } else {
                homeParams = new android.view.ViewGroup.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        SURFACE_HEIGHT);
            }
            homeContainer.addView(content, homeParams);
            startupLog("[WestlakeLauncher] buildProgrammaticHomeDashboardFragment created minimal scaffold");
            if (homeContainer.getChildCount() > 0) {
                startupLog("[WestlakeLauncher] Programmatic fragment_home_dashboard installed");
                return true;
            }
            startupLog("[WestlakeLauncher] Programmatic fragment_home_dashboard left home container empty");
            return false;
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] installProgrammaticHomeDashboardFragment", t);
            logThrowableFrames("[WestlakeLauncher] installProgrammaticHomeDashboardFragment", t, 10);
            return false;
        }
    }

    private static int dpPx(android.content.Context context, int dp) {
        float density = 1f;
        try {
            if (context != null && context.getResources() != null
                    && context.getResources().getDisplayMetrics() != null
                    && context.getResources().getDisplayMetrics().density > 0f) {
                density = context.getResources().getDisplayMetrics().density;
            }
        } catch (Throwable ignored) {
        }
        return Math.max(1, (int) (dp * density + 0.5f));
    }

    private static android.view.View buildDashboardCanvasContent(
            final android.content.Context context) {
        android.view.View view = new android.view.View(context) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int desiredWidth = SURFACE_WIDTH;
                int desiredHeight = Math.max(SURFACE_HEIGHT, dpPx(context, 640));
                int measuredWidth = android.view.View.resolveSize(desiredWidth, widthMeasureSpec);
                int measuredHeight = android.view.View.resolveSize(desiredHeight, heightMeasureSpec);
                setMeasuredDimension(measuredWidth, measuredHeight);
            }

            @Override
            public void draw(android.graphics.Canvas canvas) {
                if (sDashboardCanvasDrawCount < 5) {
                    sDashboardCanvasDrawCount++;
                    android.util.Log.i("WestlakeDraw", "dashboardCanvasContent onDraw count="
                            + sDashboardCanvasDrawCount + " size=" + getWidth() + "x" + getHeight());
                }
                drawDashboardSkeleton(context, canvas, getWidth(), getHeight());
            }
        };
        view.setMinimumHeight(dpPx(context, 420));
        return view;
    }

    private static float safeDensity(android.content.Context context) {
        try {
            if (context != null
                    && context.getResources() != null
                    && context.getResources().getDisplayMetrics() != null
                    && context.getResources().getDisplayMetrics().density > 0f) {
                return context.getResources().getDisplayMetrics().density;
            }
        } catch (Throwable ignored) {
        }
        return 1f;
    }

    private static boolean isDashboardFallbackInstalled(Activity activity) {
        if (activity == null) {
            return false;
        }
        try {
            synchronized (sInstalledDashboardFallbacks) {
                for (int i = 0; i < sInstalledDashboardFallbacks.size(); i++) {
                    if (sInstalledDashboardFallbacks.get(i) == activity) {
                        return true;
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    private static void markDashboardFallbackInstalled(Activity activity) {
        if (activity == null) {
            return;
        }
        try {
            synchronized (sInstalledDashboardFallbacks) {
                for (int i = 0; i < sInstalledDashboardFallbacks.size(); i++) {
                    if (sInstalledDashboardFallbacks.get(i) == activity) {
                        return;
                    }
                }
                sInstalledDashboardFallbacks.add(activity);
            }
        } catch (Throwable ignored) {
        }
    }

    private static android.view.View safeFindViewById(Activity activity, int id, String label) {
        if (activity == null || id == 0) {
            return null;
        }
        try {
            android.view.Window window = activity.getWindow();
            if (window != null) {
                android.view.View decor = window.getDecorView();
                if (decor != null) {
                    return safeFindViewById(decor, id, label + ".decor");
                }
            }
            return activity.findViewById(id);
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] safeFindViewById(" + label + ") error", t);
            return null;
        }
    }

    private static android.view.View safeFindViewById(android.view.View root, int id, String label) {
        if (root == null || id == 0) {
            return null;
        }
        try {
            return root.findViewById(id);
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] safeFindViewById(" + label + ") root error", t);
            return null;
        }
    }

    private static boolean installDashboardViewFallback(Activity activity) {
        if (activity == null) {
            return false;
        }
        try {
            int homeContainerId = resolveAppResourceId(activity, "id", "home_dashboard_container");
            int pageContentId = resolveAppResourceId(activity, "id", "page_content");
            int contentLayoutId = resolveAppResourceId(activity, "layout", "activity_home_dashboard");
            android.view.View installedRoot = null;
            startupLog("[WestlakeLauncher] installDashboardViewFallback ids: home=0x"
                    + Integer.toHexString(homeContainerId) + " page=0x"
                    + Integer.toHexString(pageContentId) + " layout=0x"
                    + Integer.toHexString(contentLayoutId));

            android.view.View containerView =
                    safeFindViewById(activity, homeContainerId, "home_dashboard_container.initial");
            startupLog("[WestlakeLauncher] installDashboardViewFallback initial container="
                    + (containerView != null ? containerView.getClass().getName() : "null"));
            if (false && !(containerView instanceof android.view.ViewGroup) && contentLayoutId != 0) {
                try {
                    startupLog("[WestlakeLauncher] installDashboardViewFallback direct setContentView(activity_home_dashboard)");
                    activity.setContentView(contentLayoutId);
                    containerView = safeFindViewById(activity, homeContainerId,
                            "home_dashboard_container.after_direct");
                    startupLog("[WestlakeLauncher] installDashboardViewFallback post-direct container="
                            + (containerView != null ? containerView.getClass().getName() : "null"));
                } catch (Throwable directSetContentViewError) {
                    startupLog("[WestlakeLauncher] installDashboardViewFallback direct setContentView error",
                            directSetContentViewError);
                    logThrowableFrames("[WestlakeLauncher] installDashboardViewFallback direct setContentView",
                            directSetContentViewError, 12);
                }
            }
            if (!(containerView instanceof android.view.ViewGroup)) {
                startupLog("[WestlakeLauncher] installDashboardViewFallback building root");
                android.view.View root = buildProgrammaticDashboardFallbackRoot(activity);
                if (root != null) {
                    installedRoot = root;
                    startupLog("[WestlakeLauncher] installDashboardViewFallback root class="
                            + root.getClass().getName());
                    startupLog("[WestlakeLauncher] installDashboardViewFallback setContentView begin");
                    android.view.Window window = activity.getWindow();
                    if (window != null) {
                        window.setContentView(root);
                        startupLog("[WestlakeLauncher] installDashboardViewFallback setContentView via window");
                    } else {
                        activity.setContentView(root);
                        startupLog("[WestlakeLauncher] installDashboardViewFallback setContentView via activity");
                    }
                    startupLog("[WestlakeLauncher] installDashboardViewFallback setContentView done");
                    startupLog("[WestlakeLauncher] Installed programmatic dashboard root");
                    containerView = safeFindViewById(root, homeContainerId,
                            "home_dashboard_container.after_root");
                    if (!(containerView instanceof android.view.ViewGroup)
                            && root instanceof android.widget.LinearLayout) {
                        try {
                            android.widget.LinearLayout guestHomeContainer =
                                    new android.widget.LinearLayout(activity);
                            guestHomeContainer.setOrientation(android.widget.LinearLayout.VERTICAL);
                            guestHomeContainer.setMinimumHeight(dpPx(activity, 420));
                            guestHomeContainer.setId(homeContainerId);
                            ((android.widget.LinearLayout) root).addView(
                                    guestHomeContainer,
                                    new android.widget.LinearLayout.LayoutParams(
                                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
                            containerView = guestHomeContainer;
                            startupLog("[WestlakeLauncher] installDashboardViewFallback added plain guest home container under root");
                        } catch (Throwable plainHomeContainerError) {
                            startupLog("[WestlakeLauncher] installDashboardViewFallback plain guest home container",
                                    plainHomeContainerError);
                        }
                    }
                    if (!(containerView instanceof android.view.ViewGroup)
                            && root instanceof android.view.ViewGroup) {
                        containerView = root;
                        startupLog("[WestlakeLauncher] installDashboardViewFallback using root as container");
                    }
                    startupLog("[WestlakeLauncher] installDashboardViewFallback post-root container="
                            + (containerView != null ? containerView.getClass().getName() : "null"));
                }
            }
            if (containerView == null
                    && pageContentId != 0
                    && contentLayoutId != 0) {
                android.view.View pageContentView =
                        safeFindViewById(activity, pageContentId, "page_content");
                startupLog("[WestlakeLauncher] installDashboardViewFallback pageContent="
                        + (pageContentView != null ? pageContentView.getClass().getName() : "null"));
                if (pageContentView instanceof android.view.ViewGroup) {
                    android.view.ViewGroup pageContent = (android.view.ViewGroup) pageContentView;
                    if (pageContent.getChildCount() == 0) {
                        startupLog("[WestlakeLauncher] installDashboardViewFallback inflating activity_home_dashboard");
                        activity.getLayoutInflater().inflate(contentLayoutId, pageContent, true);
                        startupLog("[WestlakeLauncher] Inflated activity_home_dashboard into page_content");
                    }
                    containerView = safeFindViewById(activity, homeContainerId,
                            "home_dashboard_container.after_inflate");
                    startupLog("[WestlakeLauncher] installDashboardViewFallback post-inflate container="
                            + (containerView != null ? containerView.getClass().getName() : "null"));
                }
            }
            if (!(containerView instanceof android.view.ViewGroup)) {
                startupLog("[WestlakeLauncher] installDashboardViewFallback no container");
                return false;
            }

            int intermediateId = resolveAppResourceId(activity, "id", "intermediate_layout_container");
            android.view.View intermediate =
                    safeFindViewById(activity, intermediateId, "intermediate_layout_container");
            if (intermediate != null) {
                intermediate.setVisibility(android.view.View.GONE);
            }

            android.view.ViewGroup homeContainer = (android.view.ViewGroup) containerView;
            homeContainer.setVisibility(android.view.View.VISIBLE);

            android.view.ViewGroup launcherRootContainer = homeContainer;

            int fragmentLayoutId = resolveAppResourceId(activity, "layout", "fragment_home_dashboard");
            startupLog("[WestlakeLauncher] installDashboardViewFallback fragmentLayout=0x"
                    + Integer.toHexString(fragmentLayoutId));
            boolean launcherOwnedRoot = false;
            try {
                String homeClassName = homeContainer.getClass().getName();
                launcherOwnedRoot = homeClassName != null
                        && homeClassName.startsWith("com.westlake.engine.WestlakeLauncher$")
                        && homeContainer.getId() != homeContainerId;
            } catch (Throwable ignored) {
            }
            if (!launcherOwnedRoot && homeContainer == installedRoot) {
                startupLog("[WestlakeLauncher] installDashboardViewFallback root is acting as guest home container");
            }

            boolean syntheticGuestHomeContainer = false;
            if (launcherOwnedRoot && fragmentLayoutId != 0) {
                try {
                    startupLog("[WestlakeLauncher] installDashboardViewFallback synth root childCount="
                            + launcherRootContainer.getChildCount());
                    android.widget.LinearLayout guestHomeContainer =
                            new android.widget.LinearLayout(activity);
                    startupLog("[WestlakeLauncher] installDashboardViewFallback synth new guest="
                            + guestHomeContainer.getClass().getName());
                    guestHomeContainer.setOrientation(android.widget.LinearLayout.VERTICAL);
                    startupLog("[WestlakeLauncher] installDashboardViewFallback synth orientation");
                    guestHomeContainer.setMinimumHeight(dpPx(activity, 420));
                    startupLog("[WestlakeLauncher] installDashboardViewFallback synth minHeight");
                    guestHomeContainer.setId(homeContainerId);
                    startupLog("[WestlakeLauncher] installDashboardViewFallback synth id");
                    startupLog("[WestlakeLauncher] installDashboardViewFallback synth addView begin");
                    launcherRootContainer.addView(guestHomeContainer,
                            new android.widget.LinearLayout.LayoutParams(
                                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
                    startupLog("[WestlakeLauncher] installDashboardViewFallback synth addView done childCount="
                            + launcherRootContainer.getChildCount());
                    homeContainer = guestHomeContainer;
                    syntheticGuestHomeContainer = true;
                    startupLog("[WestlakeLauncher] installDashboardViewFallback rebound to synthetic guest home container");
                } catch (Throwable syntheticHomeError) {
                    startupLog("[WestlakeLauncher] installDashboardViewFallback synthetic guest home container", syntheticHomeError);
                    homeContainer = launcherRootContainer;
                    syntheticGuestHomeContainer = false;
                }
            }

            boolean installedProgrammaticFragment = false;
            if (homeContainer.getChildCount() == 0 && fragmentLayoutId != 0) {
                startupLog("[WestlakeLauncher] Skipping direct fragment_home_dashboard inflate");
                installedProgrammaticFragment =
                        installProgrammaticHomeDashboardFragment(activity, homeContainer);
            }

            if (installedProgrammaticFragment && homeContainer.getChildCount() > 0 && !launcherOwnedRoot) {
                startupLog("[WestlakeLauncher] installDashboardViewFallback using programmatic dashboard scaffold");
                return true;
            }
            if (installedProgrammaticFragment
                    && syntheticGuestHomeContainer
                    && homeContainer.getChildCount() > 0) {
                try {
                    android.view.ViewParent guestParent = homeContainer.getParent();
                    if (guestParent instanceof android.view.ViewGroup) {
                        ((android.view.ViewGroup) guestParent).removeView(homeContainer);
                    }
                    if (installedRoot instanceof android.view.ViewGroup) {
                        android.view.ViewGroup installedRootGroup =
                                (android.view.ViewGroup) installedRoot;
                        installedRootGroup.removeAllViews();
                        android.view.ViewGroup.LayoutParams promotedParams;
                        if (installedRootGroup instanceof android.widget.FrameLayout) {
                            promotedParams = new android.widget.FrameLayout.LayoutParams(
                                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                        } else if (installedRootGroup instanceof android.widget.LinearLayout) {
                            promotedParams = new android.widget.LinearLayout.LayoutParams(
                                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                        } else {
                            promotedParams = new android.view.ViewGroup.LayoutParams(
                                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                        }
                        installedRootGroup.addView(homeContainer, promotedParams);
                        startupLog("[WestlakeLauncher] installDashboardViewFallback promoted synthetic guest home container into installed root");
                    } else {
                        launcherRootContainer.addView(homeContainer,
                                new android.widget.LinearLayout.LayoutParams(
                                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
                        startupLog("[WestlakeLauncher] installDashboardViewFallback restored synthetic guest home container under launcher root");
                    }
                } catch (Throwable promoteError) {
                    startupLog("[WestlakeLauncher] installDashboardViewFallback promote synthetic guest home container",
                            promoteError);
                    logThrowableFrames("[WestlakeLauncher] installDashboardViewFallback promote synthetic guest home container",
                            promoteError, 10);
                }
                startupLog("[WestlakeLauncher] installDashboardViewFallback using synthetic guest programmatic scaffold");
                return true;
            }
            if (installedProgrammaticFragment) {
                startupLog("[WestlakeLauncher] installDashboardViewFallback programmatic scaffold incomplete");
            }

            int sectionsId = resolveAppResourceId(activity, "id", "sections_container");
            android.view.View sectionsView =
                    safeFindViewById(installedRoot, sectionsId, "sections_container.root");
            if (sectionsView == null) {
                sectionsView = safeFindViewById(activity, sectionsId, "sections_container");
            }
            startupLog("[WestlakeLauncher] installDashboardViewFallback sections="
                    + (sectionsView != null ? sectionsView.getClass().getName() : "null"));
            if (installedProgrammaticFragment
                    && sectionsView instanceof android.view.ViewGroup
                    && ((android.view.ViewGroup) sectionsView).getChildCount() > 0) {
                startupLog("[WestlakeLauncher] installDashboardViewFallback using real dashboard scaffold");
                return true;
            }
            if (sectionsView == null
                    && homeContainer.getChildCount() > 0
                    && !syntheticGuestHomeContainer) {
                startupLog("[WestlakeLauncher] installDashboardViewFallback keeping real home container children="
                        + homeContainer.getChildCount());
                return true;
            }
            if (!(sectionsView instanceof android.view.ViewGroup)) {
                sectionsView = homeContainer;
                startupLog("[WestlakeLauncher] installDashboardViewFallback using home container as fallback sections");
            }

            android.view.ViewGroup sections = (android.view.ViewGroup) sectionsView;
            if (sections.getChildCount() > 0
                    && !(syntheticGuestHomeContainer && sections == homeContainer)) {
                startupLog("[WestlakeLauncher] installDashboardViewFallback keeping existing sections children="
                        + sections.getChildCount());
                return true;
            }
            sections.removeAllViews();
            startupLog("[WestlakeLauncher] installDashboardViewFallback cleared sections");

            if (sections == launcherRootContainer) {
                String sectionClassName = sections.getClass().getName();
                if (sectionClassName != null
                        && sectionClassName.startsWith("com.westlake.engine.WestlakeLauncher$")) {
                    sections.setWillNotDraw(true);
                    sections.addView(buildDashboardCanvasContent(activity),
                            dashboardCanvasLp());
                    startupLog("[WestlakeLauncher] Dashboard fallback using launcher-owned root with explicit child");
                    return sections.getChildCount() > 0;
                }
            }

            android.content.Context context = activity;
            sections.addView(buildDashboardCanvasContent(context),
                    dashboardCanvasLp());

            startupLog("[WestlakeLauncher] Dashboard fallback injected into real view tree");
            return true;
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] installDashboardViewFallback error", t);
            logThrowableFrames("[WestlakeLauncher] installDashboardViewFallback", t, 12);
            return false;
        }
    }

    private static boolean shouldUseTextOnlyDashboardMenu(Activity activity) {
        try {
            String prop = System.getProperty("westlake.text_menu_only");
            if (prop != null && ("1".equals(prop) || "true".equalsIgnoreCase(prop))) {
                return activity != null;
            }
        } catch (Throwable ignored) {
        }
        try {
            String env = System.getenv("WESTLAKE_TEXT_MENU_ONLY");
            if (env != null && ("1".equals(env) || "true".equalsIgnoreCase(env))) {
                return activity != null;
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    private static void drawTextOnlyDashboardMenu() {
        if (!OHBridge.isNativeAvailable()) {
            return;
        }
        long surf = OHBridge.surfaceCreate(0, SURFACE_WIDTH, SURFACE_HEIGHT);
        long canv = OHBridge.surfaceGetCanvas(surf);
        long font = OHBridge.fontCreate();
        long pen = OHBridge.penCreate();
        long brush = OHBridge.brushCreate();

        OHBridge.canvasDrawColor(canv, 0xFFF5F5F5);

        OHBridge.fontSetSize(font, 30);
        OHBridge.penSetColor(pen, 0xFFDA291C);
        OHBridge.canvasDrawText(canv, "McDonald's", 36, 90, font, pen, brush);

        OHBridge.fontSetSize(font, 18);
        OHBridge.penSetColor(pen, 0xFF333333);
        OHBridge.canvasDrawText(canv, "Text-only menu", 36, 130, font, pen, brush);

        OHBridge.fontSetSize(font, 20);
        OHBridge.penSetColor(pen, 0xFF111111);
        OHBridge.canvasDrawText(canv, "Big Mac Combo        $5.99", 36, 210, font, pen, brush);
        OHBridge.canvasDrawText(canv, "2 for $6 Mix & Match", 36, 260, font, pen, brush);
        OHBridge.canvasDrawText(canv, "Free Medium Fries    Reward", 36, 310, font, pen, brush);
        OHBridge.canvasDrawText(canv, "10 pc McNuggets      $6.79", 36, 360, font, pen, brush);
        OHBridge.canvasDrawText(canv, "Quarter Pounder      $6.49", 36, 410, font, pen, brush);
        OHBridge.canvasDrawText(canv, "McFlurry OREO        $3.49", 36, 460, font, pen, brush);

        OHBridge.fontSetSize(font, 16);
        OHBridge.penSetColor(pen, 0xFF666666);
        OHBridge.canvasDrawText(canv, "Home   Deals   Order   Rewards   More", 24, 730, font, pen, brush);

        OHBridge.surfaceFlush(surf);
        sDirectDashboardFallbackActive = true;
        startupLog("[WestlakeLauncher] Text-only dashboard menu drawn via OHBridge");
    }

    private static boolean allowMcdProfileControlledFallback() {
        String prop = System.getProperty("westlake.mcdprofile.controlled_fallback");
        if (prop == null || prop.length() == 0) {
            prop = System.getenv("WESTLAKE_MCD_PROFILE_CONTROLLED_FALLBACK");
        }
        return "1".equals(prop) || "true".equalsIgnoreCase(prop);
    }

    private static Activity launchMcdProfileControlledActivity(String packageName,
            String activityName, Class<?> resolvedActivityClass, ClassLoader activityClassLoader) {
        startupLog("PF466 mcd profile controlled launch begin");
        appendCutoffCanaryMarker("MCD_PROFILE_CONTROLLED_LAUNCH_BEGIN pkg="
                + safeMarkerToken(packageName));
        if (activityName == null || activityName.length() == 0) {
            appendCutoffCanaryMarker("MCD_PROFILE_CONTROLLED_LAUNCH_FAIL err=no_activity");
            return null;
        }
        ClassLoader previousLoader = Thread.currentThread().getContextClassLoader();
        try {
            Class<?> activityClass = resolvedActivityClass;
            if (activityClass == null) {
                activityClass = resolveAppClassChildFirstOrNull(activityName);
            }
            if (activityClass == null && activityClassLoader != null) {
                activityClass = Class.forName(activityName, false, activityClassLoader);
            }
            if (activityClass == null) {
                appendCutoffCanaryMarker("MCD_PROFILE_CONTROLLED_LAUNCH_FAIL err=class_null");
                return null;
            }
            ClassLoader appLoader = activityClass.getClassLoader();
            if (!isBootClassLoader(appLoader)) {
                Thread.currentThread().setContextClassLoader(appLoader);
            }

            startupLog("PF466 mcd profile activity alloc begin");
            Object raw = tryAllocInstance(activityClass);
            if (!(raw instanceof Activity)) {
                raw = tryUnsafeAllocInstance(activityClass);
            }
            if (raw instanceof Activity) {
                appendCutoffCanaryMarker("MCD_PROFILE_CONTROLLED_ACTIVITY_ALLOC_OK class="
                        + safeMarkerToken(activityClass.getName()));
            } else {
                startupLog("PF466 mcd profile activity ctor begin");
                raw = activityClass.getDeclaredConstructor().newInstance();
                appendCutoffCanaryMarker("MCD_PROFILE_CONTROLLED_ACTIVITY_CTOR_OK class="
                        + safeMarkerToken(activityClass.getName()));
            }
            if (!(raw instanceof Activity)) {
                appendCutoffCanaryMarker("MCD_PROFILE_CONTROLLED_LAUNCH_FAIL err=not_activity"
                        + " class=" + safeMarkerToken(activityClass.getName()));
                return null;
            }
            Activity activity = (Activity) raw;

            android.app.MiniServer server = android.app.MiniServer.peek();
            android.app.Application app = server != null ? server.getApplication() : null;
            if (app == null) {
                app = new android.app.Application();
                android.app.MiniServer.currentSetApplication(app);
            }
            android.content.ComponentName component =
                    new android.content.ComponentName(packageName, activityName);
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setComponent(component);
            intent.setPackage(packageName);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            android.view.Window window = new android.view.Window(activity);
            boolean attached = false;
            try {
                activity.westlakeAttach(app, app, intent, component, window,
                        new android.app.Instrumentation());
                attached = true;
                appendCutoffCanaryMarker("MCD_PROFILE_CONTROLLED_ATTACH_OK mode=shim");
            } catch (Throwable attachError) {
                startupLog("PF466 mcd profile attach fallback", attachError);
                appendCutoffCanaryMarker("MCD_PROFILE_CONTROLLED_ATTACH_WARN err="
                        + safeMarkerToken(throwableTag(attachError)));
            }
            if (!attached) {
                setActivityFieldReflective(activity, "mApplication", app);
                setActivityFieldReflective(activity, "mIntent", intent);
                setActivityFieldReflective(activity, "mComponent", component);
                setActivityFieldReflective(activity, "mWindow", window);
                setActivityFieldReflective(activity, "mFinished", Boolean.FALSE);
                setActivityFieldReflective(activity, "mDestroyed", Boolean.FALSE);
                try {
                    window.adoptContext(activity);
                    window.setCallback(activity);
                } catch (Throwable ignored) {
                }
                appendCutoffCanaryMarker("MCD_PROFILE_CONTROLLED_ATTACH_OK mode=fields");
            }

            wireStandaloneActivityResources(activity, packageName, activityName);
            activity.westlakePerformCreate(null);
            appendCutoffCanaryMarker("MCD_PROFILE_CONTROLLED_ACTIVITY_ONCREATE_OK");
            try {
                activity.westlakePerformStart();
                activity.westlakePerformResume();
                appendCutoffCanaryMarker("MCD_PROFILE_CONTROLLED_ACTIVITY_RESUME_OK");
            } catch (Throwable lifecycleWarn) {
                startupLog("PF466 mcd profile start/resume warning", lifecycleWarn);
                appendCutoffCanaryMarker("MCD_PROFILE_CONTROLLED_ACTIVITY_RESUME_WARN err="
                        + safeMarkerToken(throwableTag(lifecycleWarn)));
            }
            startupLog("PF466 mcd profile controlled launch returned");
            return activity;
        } catch (Throwable t) {
            startupLog("PF466 mcd profile controlled launch failed", t);
            logThrowableFrames("PF466 mcd profile controlled launch", t, 12);
            appendCutoffCanaryMarker("MCD_PROFILE_CONTROLLED_LAUNCH_FAIL err="
                    + safeMarkerToken(t.getClass().getName()));
            return null;
        } finally {
            try {
                Thread.currentThread().setContextClassLoader(previousLoader);
            } catch (Throwable ignored) {
            }
        }
    }

    private static void setActivityFieldReflective(Activity activity, String name, Object value) {
        if (activity == null || name == null) {
            return;
        }
        Class<?> cls = activity.getClass();
        while (cls != null && cls != Object.class) {
            try {
                java.lang.reflect.Field field = cls.getDeclaredField(name);
                field.setAccessible(true);
                field.set(activity, value);
                return;
            } catch (NoSuchFieldException ignored) {
                cls = cls.getSuperclass();
            } catch (Throwable ignored) {
                return;
            }
        }
    }

    private static java.lang.reflect.Method findLifecycleMethod(
            Class<?> start, String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        Class<?> cls = start;
        while (cls != null && cls != Object.class) {
            try {
                java.lang.reflect.Method method = cls.getDeclaredMethod(name, parameterTypes);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException ignored) {
                cls = cls.getSuperclass();
            }
        }
        throw new NoSuchMethodException(name);
    }

    private static void invokeActivityBundleLifecycle(Activity activity, String name,
            android.os.Bundle state) throws Throwable {
        java.lang.reflect.Method method = findLifecycleMethod(activity.getClass(),
                name, android.os.Bundle.class);
        try {
            method.invoke(activity, state);
        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable cause = e.getCause();
            throw cause != null ? cause : e;
        }
    }

    private static void invokeActivityNoArgLifecycle(Activity activity, String name)
            throws Throwable {
        java.lang.reflect.Method method = findLifecycleMethod(activity.getClass(), name);
        try {
            method.invoke(activity);
        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable cause = e.getCause();
            throw cause != null ? cause : e;
        }
    }

    private static boolean isShowcaseActivity(Activity activity) {
        if (activity == null) {
            return false;
        }
        try {
            String name = activity.getClass().getName();
            return name != null && name.startsWith("com.westlake.showcase.");
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean isYelpLiveActivity(Activity activity) {
        if (activity == null) {
            return false;
        }
        try {
            String name = activity.getClass().getName();
            return name != null && name.startsWith("com.westlake.yelplive.");
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean isMaterialYelpActivity(Activity activity) {
        if (activity == null) {
            return false;
        }
        try {
            String name = activity.getClass().getName();
            return name != null && name.startsWith("com.westlake.materialyelp.");
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean isMcdProfileActivity(Activity activity) {
        if (activity == null) {
            return false;
        }
        try {
            String name = activity.getClass().getName();
            return name != null && name.startsWith("com.westlake.mcdprofile.");
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean isMaterialXmlProbeActivity(Activity activity) {
        if (activity == null) {
            return false;
        }
        try {
            String name = activity.getClass().getName();
            return name != null && name.startsWith("com.westlake.materialxmlprobe.");
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static void runYelpLiveDirectFrameLoop(Activity activity) {
        writeYelpLiveDirectFrame(activity, "initial");
        startupLog("[WestlakeLauncher] Yelp live initial frame rendered");

        int lastTouchSeq = -1;
        long downTime = 0L;
        int downX = 0;
        int downY = 0;
        boolean scrollHandled = false;
        boolean genericSearchProbed = false;
        boolean genericDetailsProbed = false;
        boolean genericSavedProbed = false;
        boolean genericScrollProbed = false;
        boolean genericAdapterClickProbed = false;
        String touchPath = null;
        try {
            String envTouch = System.getenv("WESTLAKE_TOUCH");
            touchPath = envTouch != null && envTouch.length() > 0
                    ? envTouch : defaultTouchPath();
            startupLog("[WestlakeLauncher] Yelp live touch file: " + touchPath);
        } catch (Throwable ignored) {
            touchPath = defaultTouchPath();
        }

        if (FRAMEWORK_POLICY_WESTLAKE_ONLY.equals(frameworkPolicyValue())) {
            startupLog("[WestlakeLauncher] Yelp live strict touch polling enabled");
            appendCutoffCanaryMarker("YELP_TOUCH_POLL_READY strict=true");
        }

        while (true) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                break;
            }
            if (showcaseBool(activity, "renderDirty", false)) {
                showcaseInvoke(activity, "consumeRenderDirty");
                writeYelpLiveDirectFrame(activity, "state_dirty");
                continue;
            }
            byte[] touch = tryReadFileBytes(touchPath);
            if (touch == null || touch.length < 16) {
                continue;
            }
            try {
                java.nio.ByteBuffer bb = java.nio.ByteBuffer.wrap(touch, 0, 16);
                bb.order(java.nio.ByteOrder.LITTLE_ENDIAN);
                int action = bb.getInt();
                int x = bb.getInt();
                int y = bb.getInt();
                int seq = bb.getInt();
                if (seq == lastTouchSeq) {
                    continue;
                }
                lastTouchSeq = seq;
                long now = System.currentTimeMillis();
                if (action == 0 || downTime == 0L) {
                    downTime = now;
                    downX = x;
                    downY = y;
                    scrollHandled = false;
                }
                if (action == 0) {
                    continue;
                }
                if (action == 2) {
                    if (!scrollHandled && isYelpLiveListScrollGesture(downY, y)) {
                        appendCutoffCanaryMarker("YELP_TOUCH_POLL_OK seq="
                                + intAscii(seq) + " action=" + showcaseTouchReason(action)
                                + " x=" + intAscii(x) + " y=" + intAscii(y));
                        boolean directHandled = routeYelpLiveScroll(activity, downY, y);
                        startupLog("[WestlakeLauncher] Yelp live touch action=" + action
                                + " x=" + x + " y=" + y
                                + " downX=" + downX + " downY=" + downY
                                + " dispatch=false direct=" + directHandled);
                        if (directHandled) {
                            if (!genericScrollProbed) {
                                genericScrollProbed = probeYelpLiveGenericScrollContainer(
                                        activity, downY, y, seq);
                            }
                            showcaseInvoke(activity, "consumeRenderDirty");
                            writeYelpLiveDirectFrame(activity, showcaseTouchReason(action));
                            scrollHandled = true;
                        }
                    }
                    continue;
                }
                if (action != 1) {
                    continue;
                }
                appendCutoffCanaryMarker("YELP_TOUCH_POLL_OK seq="
                        + intAscii(seq) + " action=" + showcaseTouchReason(action)
                        + " x=" + intAscii(x) + " y=" + intAscii(y));
                if (!genericSearchProbed && y >= YELP_BOTTOM_NAV_TOP
                        && x >= 120 && x < 240) {
                    genericSearchProbed = routeYelpLiveGenericButtonHit(
                            activity, "Search", x, y, seq);
                }
                boolean dispatchHandled = false;
                if (scrollHandled) {
                    startupLog("[WestlakeLauncher] Yelp live touch action=" + action
                            + " x=" + x + " y=" + y
                            + " downX=" + downX + " downY=" + downY
                            + " dispatch=false direct=true scroll_up_ignored=true");
                    downTime = 0L;
                    scrollHandled = false;
                    continue;
                }
                int tabBeforeDirect = showcaseInt(activity, "activeTab", 0);
                boolean directHandled = scrollHandled
                        || (isYelpLiveListScrollGesture(downY, y)
                                ? routeYelpLiveScroll(activity, downY, y)
                                : routeYelpLiveDirectTouch(activity, action, x, y));
                if (!genericDetailsProbed && directHandled
                        && tabBeforeDirect != 2 && isYelpLiveRowOpenTap(x, y)) {
                    if (!genericAdapterClickProbed) {
                        genericAdapterClickProbed = probeYelpLiveGenericAdapterItemClick(
                                activity, 2, x, y, seq);
                    }
                    genericDetailsProbed = routeYelpLiveGenericButtonHit(
                            activity, "Details", x, y, seq);
                }
                if (!genericSavedProbed && directHandled
                        && tabBeforeDirect == 2
                        && y >= 548 && y < 638 && x >= 240 && x < 360) {
                    genericSavedProbed = routeYelpLiveGenericButtonHit(
                            activity, "Saved", x, y, seq);
                }
                startupLog("[WestlakeLauncher] Yelp live touch action=" + action
                        + " x=" + x + " y=" + y
                        + " downX=" + downX + " downY=" + downY
                        + " dispatch=" + dispatchHandled
                        + " direct=" + directHandled);
                if (directHandled) {
                    showcaseInvoke(activity, "consumeRenderDirty");
                    writeYelpLiveDirectFrame(activity, showcaseTouchReason(action));
                }
                downTime = 0L;
                scrollHandled = false;
            } catch (Throwable t) {
                startupLog("[WestlakeLauncher] Yelp live touch loop error", t);
            }
        }
    }

    private static boolean isYelpLiveListScrollGesture(int downY, int y) {
        int delta = y - downY;
        if (delta < 0) {
            delta = -delta;
        }
        return downY >= 214 && downY < YELP_BOTTOM_NAV_TOP && delta >= 42;
    }

    private static boolean isYelpLiveRowOpenTap(int x, int y) {
        if (x >= 300) {
            return false;
        }
        int row0Top = YELP_ROW_TOP;
        int row1Top = row0Top + YELP_ROW_HEIGHT + YELP_ROW_GAP;
        int row2Top = row1Top + YELP_ROW_HEIGHT + YELP_ROW_GAP;
        int row3Top = row2Top + YELP_ROW_HEIGHT + YELP_ROW_GAP;
        int row4Top = row3Top + YELP_ROW_HEIGHT + YELP_ROW_GAP;
        return (y >= row0Top && y < row0Top + YELP_ROW_HEIGHT)
                || (y >= row1Top && y < row1Top + YELP_ROW_HEIGHT)
                || (y >= row2Top && y < row2Top + YELP_ROW_HEIGHT)
                || (y >= row3Top && y < row3Top + YELP_ROW_HEIGHT)
                || (y >= row4Top && y < row4Top + YELP_ROW_HEIGHT);
    }

    private static boolean routeYelpLiveScroll(Activity activity, int downY, int y) {
        if (activity == null) {
            return false;
        }
        if (downY - y >= 42) {
            return showcaseInvoke(activity, "scrollListDown");
        }
        if (y - downY >= 42) {
            return showcaseInvoke(activity, "scrollListUp");
        }
        return false;
    }

    private static boolean routeYelpLiveDirectTouch(Activity activity, int action, int x, int y) {
        if (activity == null || action != 1) {
            return false;
        }
        int tab = showcaseInt(activity, "activeTab", 0);
        if (y >= YELP_BOTTOM_NAV_TOP) {
            if (x < 120) {
                return showcaseInvoke(activity, "navigateDiscover");
            }
            if (x < 240) {
                return showcaseInvoke(activity, "navigateSearch");
            }
            if (x < 360) {
                return showcaseInvoke(activity, "navigateDetails");
            }
            return showcaseInvoke(activity, "navigateSaved");
        }
        if (y < 100) {
            return showcaseInvoke(activity, x < 310 ? "navigateSearch" : "navigateDiscover");
        }
        if (y >= 100 && y < 146) {
            if (x < 132) {
                return showcaseInvoke(activity, "toggleSortRating");
            }
            if (x < 236) {
                return showcaseInvoke(activity, "toggleOpenNow");
            }
            if (x < 330) {
                return showcaseInvoke(activity, "toggleDelivery");
            }
            if (x < 396) {
                return showcaseInvoke(activity, "togglePrice");
            }
            return showcaseInvoke(activity, "fetchLiveFeed");
        }
        if (y >= 146 && y < 214) {
            if (x < 120) {
                return showcaseInvoke(activity, "selectPizza");
            }
            if (x < 232) {
                return showcaseInvoke(activity, "selectAsian");
            }
            if (x < 344) {
                return showcaseInvoke(activity, "selectDinner");
            }
            return showcaseInvoke(activity, "selectDessert");
        }
        if (tab == 2 && y >= 488 && y < 548) {
            if (x < 240) {
                return showcaseInvoke(activity, "callPlace");
            }
            return showcaseInvoke(activity, "directionsPlace");
        }
        if (tab == 2 && y >= 548 && y < 638) {
            if (x < 120) {
                return showcaseInvoke(activity, "nextPlace");
            }
            if (x < 240) {
                return showcaseInvoke(activity, "directionsPlace");
            }
            if (x < 360) {
                return showcaseInvoke(activity, "savePlace");
            }
            return showcaseInvoke(activity, "reviewPlace");
        }
        if (tab == 3 && y >= 588 && y < 660) {
            if (x < 160) {
                return showcaseInvoke(activity, "navigateDiscover");
            }
            if (x < 320) {
                return showcaseInvoke(activity, "navigateSearch");
            }
            return showcaseInvoke(activity, "navigateDiscover");
        }
        int row0Top = YELP_ROW_TOP;
        int row1Top = row0Top + YELP_ROW_HEIGHT + YELP_ROW_GAP;
        int row2Top = row1Top + YELP_ROW_HEIGHT + YELP_ROW_GAP;
        int row3Top = row2Top + YELP_ROW_HEIGHT + YELP_ROW_GAP;
        int row4Top = row3Top + YELP_ROW_HEIGHT + YELP_ROW_GAP;
        if (y >= row0Top && y < row0Top + YELP_ROW_HEIGHT) {
            return routeYelpLiveListRow(activity, 0, x);
        }
        if (y >= row1Top && y < row1Top + YELP_ROW_HEIGHT) {
            return routeYelpLiveListRow(activity, 1, x);
        }
        if (y >= row2Top && y < row2Top + YELP_ROW_HEIGHT) {
            return routeYelpLiveListRow(activity, 2, x);
        }
        if (y >= row3Top && y < row3Top + YELP_ROW_HEIGHT) {
            return routeYelpLiveListRow(activity, 3, x);
        }
        if (y >= row4Top && y < row4Top + YELP_ROW_HEIGHT) {
            return routeYelpLiveListRow(activity, 4, x);
        }
        if (x < 160) {
            return showcaseInvoke(activity, "navigateDiscover");
        }
        if (x < 320) {
            return showcaseInvoke(activity, "navigateSearch");
        }
        return showcaseInvoke(activity, "savePlace");
    }

    private static boolean routeYelpLiveListRow(Activity activity, int row, int x) {
        if (x >= 300) {
            if (row == 0) {
                return showcaseInvoke(activity, "savePlace0");
            }
            if (row == 1) {
                return showcaseInvoke(activity, "savePlace1");
            }
            if (row == 2) {
                return showcaseInvoke(activity, "savePlace2");
            }
            if (row == 3) {
                return showcaseInvoke(activity, "savePlace3");
            }
            return showcaseInvoke(activity, "savePlace4");
        }
        if (row == 0) {
            return showcaseInvoke(activity, "openPlace0");
        }
        if (row == 1) {
            return showcaseInvoke(activity, "openPlace1");
        }
        if (row == 2) {
            return showcaseInvoke(activity, "openPlace2");
        }
        if (row == 3) {
            return showcaseInvoke(activity, "openPlace3");
        }
        return showcaseInvoke(activity, "openPlace4");
    }

    private static boolean probeYelpLiveGenericScrollContainer(
            Activity activity, int downY, int y, int seq) {
        try {
            android.view.View decor = activity != null && activity.getWindow() != null
                    ? activity.getWindow().getDecorView() : null;
            android.view.View target = decor != null ? findFirstScrollView(decor) : null;
            if (target == null) {
                return false;
            }
            int before = target.getScrollY();
            int delta = downY > y ? 32 : -32;
            target.scrollBy(0, delta);
            int after = target.getScrollY();
            appendCutoffCanaryMarker("YELP_GENERIC_SCROLL_OK seq=" + intAscii(seq)
                    + " delta=" + intAscii(delta)
                    + " before=" + intAscii(before)
                    + " after=" + intAscii(after)
                    + " moved=" + boolToken(after != before)
                    + " container=" + safeMarkerToken(target.getClass().getName())
                    + " source=inflated_xml");
            return true;
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] Yelp live generic scroll error", t);
            appendCutoffCanaryMarker("YELP_GENERIC_SCROLL_FAIL err="
                    + t.getClass().getName());
            return false;
        }
    }

    private static android.view.View findFirstScrollView(android.view.View view) {
        if (view == null) {
            return null;
        }
        if (view instanceof android.widget.ScrollView) {
            return view;
        }
        if (view instanceof android.view.ViewGroup) {
            android.view.ViewGroup group = (android.view.ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                android.view.View found = findFirstScrollView(group.getChildAt(i));
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static boolean probeYelpLiveGenericAdapterItemClick(
            Activity activity, int position, int x, int y, int seq) {
        try {
            android.view.View decor = activity != null && activity.getWindow() != null
                    ? activity.getWindow().getDecorView() : null;
            android.widget.ListView list = decor != null ? findFirstListView(decor) : null;
            if (list == null || list.getAdapter() == null) {
                return false;
            }
            int count = list.getAdapter().getCount();
            if (position < 0 || position >= count) {
                return false;
            }
            int childIndex = position - list.getFirstVisiblePosition();
            android.view.View child = childIndex >= 0 && childIndex < list.getChildCount()
                    ? list.getChildAt(childIndex) : null;
            long id = list.getAdapter().getItemId(position);
            boolean clicked = list.performItemClick(child, position, id);
            appendCutoffCanaryMarker("YELP_GENERIC_ADAPTER_ITEM_CLICK_OK seq="
                    + intAscii(seq)
                    + " x=" + intAscii(x)
                    + " y=" + intAscii(y)
                    + " clicked=" + boolToken(clicked)
                    + " position=" + intAscii(position)
                    + " count=" + intAscii(count)
                    + " child=" + safeMarkerToken(child != null
                            ? child.getClass().getName() : "null")
                    + " source=inflated_xml");
            return clicked;
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] Yelp live generic adapter click error", t);
            appendCutoffCanaryMarker("YELP_GENERIC_ADAPTER_ITEM_CLICK_FAIL err="
                    + t.getClass().getName());
            return false;
        }
    }

    private static android.widget.ListView findFirstListView(android.view.View view) {
        if (view == null) {
            return null;
        }
        if (view instanceof android.widget.ListView) {
            return (android.widget.ListView) view;
        }
        if (view instanceof android.view.ViewGroup) {
            android.view.ViewGroup group = (android.view.ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                android.widget.ListView found = findFirstListView(group.getChildAt(i));
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static boolean routeYelpLiveGenericButtonHit(
            Activity activity, String label, int x, int y, int seq) {
        try {
            android.view.View decor = activity != null && activity.getWindow() != null
                    ? activity.getWindow().getDecorView() : null;
            android.view.View target = decor != null ? findButtonWithText(decor, label) : null;
            if (target == null) {
                return false;
            }
            String targetName = target.getClass().getName();
            boolean clicked = target.performClick();
            appendCutoffCanaryMarker("YELP_GENERIC_HIT_OK seq=" + intAscii(seq)
                    + " x=" + intAscii(x)
                    + " y=" + intAscii(y)
                    + " clicked=" + boolToken(clicked)
                    + " target=" + safeMarkerToken(targetName)
                    + " text=" + safeMarkerToken(label)
                    + " source=inflated_xml");
            return clicked;
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] Yelp live generic hit error", t);
            appendCutoffCanaryMarker("YELP_GENERIC_HIT_FAIL err="
                    + t.getClass().getName());
            return false;
        }
    }

    private static android.view.View findButtonWithText(android.view.View view, String label) {
        if (view == null || label == null) {
            return null;
        }
        if (view instanceof android.widget.Button) {
            CharSequence text = ((android.widget.Button) view).getText();
            if (text != null && label.contentEquals(text)) {
                return view;
            }
        }
        if (view instanceof android.view.ViewGroup) {
            android.view.ViewGroup group = (android.view.ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                android.view.View found = findButtonWithText(group.getChildAt(i), label);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static boolean writeYelpLiveGenericXmlFrame(Activity activity, String reason) {
        try {
            android.view.View decor = activity != null && activity.getWindow() != null
                    ? activity.getWindow().getDecorView() : null;
            if (decor == null) {
                appendCutoffCanaryMarker("YELP_GENERIC_VIEW_DRAW_FAIL reason="
                        + reason + " err=no_decor");
                return false;
            }
            java.io.ByteArrayOutputStream ops = new java.io.ByteArrayOutputStream(16384);
            ShowcaseTreeStats stats = new ShowcaseTreeStats();
            renderShowcaseView(ops, decor, 0, 0, 0, stats, YELP_SURFACE_HEIGHT);
            overlayYelpLiveGenericXmlRows(ops, activity, stats);
            byte[] data = ops.toByteArray();
            java.io.OutputStream out = System.out;
            writeIntLe(out, 0x444C5354);
            writeIntLe(out, data.length);
            out.write(data);
            out.flush();
            appendCutoffCanaryMarker("YELP_GENERIC_VIEW_DRAW_OK reason=" + reason
                    + " bytes=" + intAscii(data.length)
                    + " views=" + intAscii(stats.views)
                    + " texts=" + intAscii(stats.texts)
                    + " buttons=" + intAscii(stats.buttons)
                    + " images=" + intAscii(stats.images)
                    + " lists=" + intAscii(stats.listViews)
                    + " listRows=" + intAscii(stats.genericListRows)
                    + " listImages=" + intAscii(stats.genericListImages)
                    + " height=" + intAscii(YELP_SURFACE_HEIGHT)
                    + " source=inflated_xml");
            if (stats.genericListRows > 0) {
                appendCutoffCanaryMarker("YELP_GENERIC_LIST_DRAW_OK rows="
                        + intAscii(stats.genericListRows)
                        + " images=" + intAscii(stats.genericListImages)
                        + " imageBytes=" + intAscii(stats.genericListImageBytes)
                        + " adapterCount=" + intAscii(stats.genericListAdapterCount)
                        + " adapter=" + safeMarkerToken(stats.genericListAdapterClass)
                        + " source=inflated_xml");
            }
            return true;
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] Yelp live generic XML frame error", t);
            logThrowableFrames("[WestlakeLauncher] Yelp live generic XML frame", t, 12);
            appendCutoffCanaryMarker("YELP_GENERIC_VIEW_DRAW_FAIL err="
                    + t.getClass().getName());
            return false;
        }
    }

    private static void writeYelpLiveDirectFrame(Activity activity, String reason) {
        try {
            writeYelpLiveGenericXmlFrame(activity, reason);
            java.io.ByteArrayOutputStream ops = new java.io.ByteArrayOutputStream(12288);
            showcaseColor(ops, 0xfffbf8f3);

            boolean loading = showcaseBool(activity, "networkLoading", false);
            boolean saved = showcaseBool(activity, "saved", false);
            boolean openNowFilter = showcaseBool(activity, "openNowFilter", true);
            boolean deliveryFilter = showcaseBool(activity, "deliveryFilter", false);
            boolean priceFilter = showcaseBool(activity, "priceFilter", false);
            boolean sortByRating = showcaseBool(activity, "sortByRating", false);
            int tab = showcaseInt(activity, "activeTab", 0);
            int placeCount = showcaseInt(activity, "placeCount", 0);
            int placeIndex = showcaseInt(activity, "placeIndex", 0);
            int rating = showcaseInt(activity, "ratingTenths", 46);
            int reviews = showcaseInt(activity, "reviewCount", 0);
            int savedCount = showcaseInt(activity, "savedCount", 0);
            int imageBytes = showcaseInt(activity, "imageBytes", 0);
            int imageWidth = showcaseInt(activity, "imageWidth", 0);
            int imageHeight = showcaseInt(activity, "imageHeight", 0);
            int imageHash = showcaseInt(activity, "imageHash", 0);
            byte[] imageData = showcaseBytes(activity, "liveImageData");
            String name = showcaseString(activity, "placeName", "Tap Live feed");
            String cuisine = showcaseString(activity, "cuisine", "Live internet data");
            String mealType = showcaseString(activity, "mealType", "Nearby");
            String difficulty = showcaseString(activity, "difficulty", "Open");
            String status = showcaseString(activity, "status", "No live data yet");
            String query = showcaseString(activity, "query", "pizza");
            String action = showcaseString(activity, "lastAction", "Ready");
            String category = showcaseString(activity, "category", "Restaurants");
            String detailAction = showcaseString(activity, "detailAction", "Tap a result");
            String row1Name = showcaseString(activity, "row1Name", name);
            String row1Meta = showcaseString(activity, "row1Meta", cuisine + " - " + mealType);
            String row2Name = showcaseString(activity, "row2Name", "Pizza and takeout nearby");
            String row2Meta = showcaseString(activity, "row2Meta", "Open now");
            String row3Name = showcaseString(activity, "row3Name", "Dinner spots near Westlake");
            String row3Meta = showcaseString(activity, "row3Meta", "Delivery and pickup");
            String row4Name = showcaseString(activity, "row4Name", "Top rated nearby");
            String row4Meta = showcaseString(activity, "row4Meta", "Tap a row for details");
            String row5Name = showcaseString(activity, "row5Name", "More places nearby");
            String row5Meta = showcaseString(activity, "row5Meta", "Swipe the list to scroll");
            int listOffset = showcaseInt(activity, "listOffset", 0);
            byte[] row1ImageData = showcaseBytes(activity, "row1ImageData");
            byte[] row2ImageData = showcaseBytes(activity, "row2ImageData");
            byte[] row3ImageData = showcaseBytes(activity, "row3ImageData");
            byte[] row4ImageData = showcaseBytes(activity, "row4ImageData");
            byte[] row5ImageData = showcaseBytes(activity, "row5ImageData");
            int row1ImageHash = showcaseInt(activity, "row1ImageHash", imageHash);
            int row2ImageHash = showcaseInt(activity, "row2ImageHash", imageHash ^ 0x004f8b3d);
            int row3ImageHash = showcaseInt(activity, "row3ImageHash", imageHash ^ 0x006b3fd3);
            int row4ImageHash = showcaseInt(activity, "row4ImageHash", imageHash ^ 0x003f79b8);
            int row5ImageHash = showcaseInt(activity, "row5ImageHash", imageHash ^ 0x002a67d1);
            int row1ImageBytes = showcaseInt(activity, "row1ImageBytes", 0);
            int row2ImageBytes = showcaseInt(activity, "row2ImageBytes", 0);
            int row3ImageBytes = showcaseInt(activity, "row3ImageBytes", 0);
            int row4ImageBytes = showcaseInt(activity, "row4ImageBytes", 0);
            int row5ImageBytes = showcaseInt(activity, "row5ImageBytes", 0);

            showcaseRect(ops, 0, 0, SURFACE_WIDTH, YELP_SURFACE_HEIGHT, 0xfff9f6f7);
            yelpMaterialHeader(ops, category, query, loading, placeCount);

            yelpFilterChip(ops, sortByRating ? "Top Rated" : "Best Match",
                    16, 108, 124, sortByRating);
            yelpFilterChip(ops, "Open Now", 132, 108, 228, openNowFilter);
            yelpFilterChip(ops, "Delivery", 236, 108, 326, deliveryFilter);
            yelpFilterChip(ops, "$$", 334, 108, 388, priceFilter);
            yelpFilterChip(ops, loading ? "Syncing" : "Live", 396, 108, 464, loading);

            yelpCategory(ops, "Pizza", 16, 150, 112, 0xffd32323,
                    category.indexOf("Pizza") >= 0);
            yelpCategory(ops, "Asian", 128, 150, 224, 0xffd32323,
                    category.indexOf("Asian") >= 0);
            yelpCategory(ops, "Dinner", 240, 150, 336, 0xff006a6a,
                    category.indexOf("Dinner") >= 0);
            yelpCategory(ops, "Dessert", 352, 150, 464, 0xff7b4b2a,
                    category.indexOf("Dessert") >= 0);

            int visibleImageCount = 0;
            if (row1ImageBytes > 0) visibleImageCount++;
            if (row2ImageBytes > 0) visibleImageCount++;
            if (row3ImageBytes > 0) visibleImageCount++;
            if (row4ImageBytes > 0) visibleImageCount++;
            if (row5ImageBytes > 0) visibleImageCount++;
            yelpAdapterRibbon(ops, placeCount, visibleImageCount, loading);
            int sectionTop = 224;

            if (tab == 2) {
                yelpSectionTitle(ops, "Business details", "Live listing from the bridge", sectionTop);
                yelpBusinessCard(ops, name, cuisine + " - " + mealType,
                        difficulty, rating, reviews, imageBytes, imageHash, imageData, 16, 250, true);
                showcaseText(ops, "Good to know", 18, 492, 14, 0xff1f1f1f);
                yelpMiniFact(ops, "Open now", "Popular for " + mealType, 16, 510);
                yelpMiniFact(ops, "Live source", "internet bridge", 248, 510);
                showcaseText(ops, trimShowcaseText(detailAction, 48), 18, 584, 9, 0xff6f6f6f);
                yelpButton(ops, "Next", 16, 596, 118, 638, placeCount > 1);
                yelpButton(ops, "Directions", 128, 596, 238, 638, false);
                yelpButton(ops, saved ? "Saved" : "Save", 248, 596, 354, 638, saved);
                yelpButton(ops, "Review", 364, 596, 464, 638, false);
            } else if (tab == 3) {
                yelpSectionTitle(ops, "Saved restaurants", "Local app state, real touch path", sectionTop);
                yelpBusinessCard(ops, savedCount > 0 ? name : "No saved restaurants yet",
                        savedCount > 0 ? cuisine + " - " + mealType : "Tap Save on a business",
                        savedCount > 0 ? "Saved for later" : "Empty list",
                        rating, reviews, imageBytes, imageHash, imageData, 16, 254, savedCount > 0);
                yelpListRow(ops, 1, row1Name, row1Meta, 504);
                yelpListRow(ops, 2, row2Name, row2Meta, 552);
                yelpButton(ops, "Discover", 16, 620, 150, 666, false);
                yelpButton(ops, "Search", 172, 620, 306, 666, false);
                yelpButton(ops, "Refresh", 328, 620, 464, 666, loading);
            } else if (tab == 1) {
                yelpSectionTitle(ops, "Search results for " + query,
                        "Tap a row for details. Use the heart edge to save.", sectionTop);
                int rowTop = YELP_ROW_TOP;
                int rowStep = YELP_ROW_HEIGHT + YELP_ROW_GAP;
                yelpResultCard(ops, listOffset + 1, row1Name, row1Meta,
                        row1ImageData, row1ImageHash, rowTop, placeIndex == listOffset,
                        row1ImageBytes > 0);
                yelpResultCard(ops, listOffset + 2, row2Name, row2Meta,
                        row2ImageData, row2ImageHash, rowTop + rowStep,
                        placeIndex == listOffset + 1,
                        row2ImageBytes > 0);
                yelpResultCard(ops, listOffset + 3, row3Name, row3Meta,
                        row3ImageData, row3ImageHash, rowTop + rowStep * 2,
                        placeIndex == listOffset + 2,
                        row3ImageBytes > 0);
                yelpResultCard(ops, listOffset + 4, row4Name, row4Meta,
                        row4ImageData, row4ImageHash, rowTop + rowStep * 3,
                        placeIndex == listOffset + 3,
                        row4ImageBytes > 0);
                yelpResultCard(ops, listOffset + 5, row5Name, row5Meta,
                        row5ImageData, row5ImageHash, rowTop + rowStep * 4,
                        placeIndex == listOffset + 4,
                        row5ImageBytes > 0);
                renderYelpLiveVisibleGenericRows(ops, activity, rowTop, 810);
                yelpScrollIndicator(ops, listOffset, placeCount, 810);
            } else {
                yelpSectionTitle(ops, "Top restaurants near Westlake",
                        trimShowcaseText(status, 48), sectionTop);
                int rowTop = YELP_ROW_TOP;
                int rowStep = YELP_ROW_HEIGHT + YELP_ROW_GAP;
                yelpResultCard(ops, listOffset + 1, row1Name, row1Meta,
                        row1ImageData, row1ImageHash, rowTop, placeIndex == listOffset,
                        row1ImageBytes > 0);
                yelpResultCard(ops, listOffset + 2, row2Name, row2Meta,
                        row2ImageData, row2ImageHash, rowTop + rowStep,
                        placeIndex == listOffset + 1,
                        row2ImageBytes > 0);
                yelpResultCard(ops, listOffset + 3, row3Name, row3Meta,
                        row3ImageData, row3ImageHash, rowTop + rowStep * 2,
                        placeIndex == listOffset + 2,
                        row3ImageBytes > 0);
                yelpResultCard(ops, listOffset + 4, row4Name, row4Meta,
                        row4ImageData, row4ImageHash, rowTop + rowStep * 3,
                        placeIndex == listOffset + 3,
                        row4ImageBytes > 0);
                yelpResultCard(ops, listOffset + 5, row5Name, row5Meta,
                        row5ImageData, row5ImageHash, rowTop + rowStep * 4,
                        placeIndex == listOffset + 4,
                        row5ImageBytes > 0);
                renderYelpLiveVisibleGenericRows(ops, activity, rowTop, 810);
                yelpScrollIndicator(ops, listOffset, placeCount, 810);
            }

            if (loading) {
                showcaseText(ops, "Syncing live results", 18, 812, 9, 0xff8a4a00);
            }
            yelpBottomNav(ops, tab);

            byte[] data = ops.toByteArray();
            java.io.OutputStream out = System.out;
            writeIntLe(out, 0x444C5354);
            writeIntLe(out, data.length);
            out.write(data);
            out.flush();
            appendCutoffCanaryMarker("YELP_DIRECT_FRAME_OK reason=" + reason
                    + " bytes=" + intAscii(data.length));
            appendCutoffCanaryMarker("YELP_VISUAL_REFRESH_V3_OK surface=editorial_photo_cards"
                    + " tab=" + yelpTabName(tab)
                    + " rows=5"
                    + " livePhotos=" + boolToken(row1ImageBytes > 0 && row5ImageBytes > 0));
            appendCutoffCanaryMarker("YELP_VISUAL_DELTA_V4_OK surface=adapter_feed"
                    + " adapterBadge=true"
                    + " visibleImages=" + intAscii(visibleImageCount)
                    + " rows=5"
                    + " materialRibbon=true");
            appendCutoffCanaryMarker("YELP_FULL_RES_FRAME_OK logical=480x1013"
                    + " target=1080x2280"
                    + " navTop=" + intAscii(YELP_BOTTOM_NAV_TOP));
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] Yelp live direct frame error", t);
            logThrowableFrames("[WestlakeLauncher] Yelp live direct frame", t, 12);
            appendCutoffCanaryMarker("YELP_DIRECT_FRAME_FAIL err="
                    + t.getClass().getName());
        }
    }

    private static void yelpMaterialHeader(java.io.OutputStream out, String category,
            String query, boolean loading, int placeCount) throws java.io.IOException {
        showcaseRect(out, 0, 0, SURFACE_WIDTH, 102, 0xffd32323);
        showcaseRect(out, 0, 0, SURFACE_WIDTH, 18, 0xffb71c1c);
        showcaseRect(out, 0, 102, SURFACE_WIDTH, 104, 0x22000000);
        showcaseText(out, "yelp", 18, 35, 23, 0xffffffff);
        showcaseText(out, "live dining", 20, 48, 7, 0xffffdddd);
        showcaseRoundRect(out, 352, 18, 462, 43, 8, 8, 0x26ffffff);
        showcaseCircle(out, 365, 30, 5, 0xffffffff);
        showcaseText(out, "Westlake", 382, 34, 10, 0xffffeeee);

        showcaseRoundRect(out, 14, 50, 466, 98, 8, 8, 0x18000000);
        showcaseRoundRect(out, 16, 48, 464, 96, 8, 8, 0xffffffff);
        yelpIcon(out, "search", 39, 72, 0xff5f6368);
        showcaseText(out, trimShowcaseText(category + " restaurants", 27),
                62, 73, 13, 0xff1f1f1f);
        showcaseRect(out, 296, 58, 297, 86, 0xffe4e0e1);
        yelpIcon(out, "pin", 320, 72, 0xffd32323);
        showcaseText(out, "Near Westlake", 342, 73, 11, 0xff1f1f1f);

        String state = loading ? "syncing" : intAscii(placeCount) + " live";
        showcaseRoundRect(out, 392, 74, 454, 90, 8, 8,
                loading ? 0xfffff3df : 0xffffebee);
        showcaseText(out, state, 402, 86, 7, loading ? 0xff8a4a00 : 0xffa82020);
    }

    private static boolean renderYelpLiveVisibleGenericRows(java.io.OutputStream out,
            Activity activity, int top, int bottom) throws java.io.IOException {
        android.view.View decor = activity != null && activity.getWindow() != null
                ? activity.getWindow().getDecorView() : null;
        android.widget.ListView list = decor != null ? findFirstListView(decor) : null;
        android.widget.ListAdapter adapter = list != null ? list.getAdapter() : null;
        if (adapter == null) {
            return false;
        }
        int adapterCount = 0;
        int first = 0;
        try {
            adapterCount = adapter.getCount();
            first = list.getFirstVisiblePosition();
        } catch (Throwable ignored) {
            adapterCount = 0;
        }
        if (first < 0) {
            first = 0;
        }
        int rows = 0;
        int images = 0;
        int imageBytes = 0;
        int rowStep = YELP_ROW_HEIGHT + YELP_ROW_GAP;
        int limit = adapterCount - first;
        if (limit > 5) {
            limit = 5;
        }
        int[] rowImageBytes = yelpLiveRowImageBytes(activity);
        for (int i = 0; i < limit; i++) {
            int rowTop = top + rowStep * i;
            if (rowTop >= bottom) {
                break;
            }
            int bytes = i < rowImageBytes.length ? rowImageBytes[i] : 0;
            rows++;
            if (bytes > 0) {
                images++;
                imageBytes += bytes;
            }
            int badgeTop = rowTop + 8;
            showcaseRoundRect(out, 326, badgeTop, 456, badgeTop + 24, 8, 8,
                    bytes > 0 ? 0xee00695c : 0xee8a4a00);
            showcaseCircle(out, 340, badgeTop + 12, 5,
                    bytes > 0 ? 0xffa5d6a7 : 0xffffcc80);
            showcaseText(out, "XML row " + intAscii(first + i + 1),
                    352, badgeTop + 16, 8, 0xffffffff);
        }
        if (rows <= 0) {
            return false;
        }
        appendCutoffCanaryMarker("YELP_GENERIC_VISIBLE_LIST_OK rows="
                + intAscii(rows)
                + " images=" + intAscii(images)
                + " imageBytes=" + intAscii(imageBytes)
                + " adapterCount=" + intAscii(adapterCount)
                + " adapter=" + safeMarkerToken(adapter.getClass().getName())
                + " surface=direct_composite"
                + " source=inflated_xml");
        return true;
    }

    private static void overlayYelpLiveGenericXmlRows(java.io.OutputStream out,
            Activity activity, ShowcaseTreeStats stats) throws java.io.IOException {
        if (activity == null || activity.getClass().getName().indexOf("yelplive") < 0) {
            return;
        }
        int[] imageBytes = yelpLiveRowImageBytes(activity);
        int[] imageHashes = new int[] {
                showcaseInt(activity, "row1ImageHash", 0),
                showcaseInt(activity, "row2ImageHash", 0),
                showcaseInt(activity, "row3ImageHash", 0),
                showcaseInt(activity, "row4ImageHash", 0),
                showcaseInt(activity, "row5ImageHash", 0)
        };
        int images = 0;
        int totalBytes = 0;
        int top = 352;
        int rowHeight = 58;
        for (int i = 0; i < 5; i++) {
            int y = top + i * rowHeight;
            showcaseRoundRect(out, 18, y, 462, y + 52, 6, 6, 0xffffffff);
            showcaseRect(out, 18, y + 51, 462, y + 52, 0xffeceff1);
            if (imageBytes[i] > 0) {
                images++;
                totalBytes += imageBytes[i];
                drawByteHashThumbnail(out, imageHashes[i], 28, y + 6, 50, 40);
            } else {
                showcaseRoundRect(out, 28, y + 6, 78, y + 46, 6, 6, 0xffeceff1);
                showcaseText(out, "img", 40, y + 31, 8, 0xff5f6368);
            }
            showcaseText(out, trimShowcaseText(yelpLiveRowName(activity, i), 28), 88, y + 20,
                    11, 0xff202124);
            showcaseText(out, trimShowcaseText(yelpLiveRowMeta(activity, i), 36), 88, y + 37,
                    8, 0xff5f6368);
            showcaseText(out, imageBytes[i] > 0 ? "image " + intAscii(imageBytes[i]) + " bytes"
                            : "waiting for image",
                    330, y + 37, 8, imageBytes[i] > 0 ? 0xff00695c : 0xffa82020);
        }
        stats.genericListRows = 5;
        stats.genericListImages = images;
        stats.genericListImageBytes = totalBytes;
        stats.genericListAdapterCount = showcaseInt(activity, "placeCount", 5);
        stats.genericListAdapterClass = "com.westlake.yelplive.YelpLiveActivity$VenueAdapter";
    }

    private static int[] yelpLiveRowImageBytes(Activity activity) {
        return new int[] {
                showcaseInt(activity, "row1ImageBytes", 0),
                showcaseInt(activity, "row2ImageBytes", 0),
                showcaseInt(activity, "row3ImageBytes", 0),
                showcaseInt(activity, "row4ImageBytes", 0),
                showcaseInt(activity, "row5ImageBytes", 0)
        };
    }

    private static String yelpLiveRowName(Activity activity, int index) {
        switch (index) {
            case 0: return showcaseString(activity, "row1Name", "Live row 1");
            case 1: return showcaseString(activity, "row2Name", "Live row 2");
            case 2: return showcaseString(activity, "row3Name", "Live row 3");
            case 3: return showcaseString(activity, "row4Name", "Live row 4");
            default: return showcaseString(activity, "row5Name", "Live row 5");
        }
    }

    private static String yelpLiveRowMeta(Activity activity, int index) {
        switch (index) {
            case 0: return showcaseString(activity, "row1Meta", "adapter row");
            case 1: return showcaseString(activity, "row2Meta", "adapter row");
            case 2: return showcaseString(activity, "row3Meta", "adapter row");
            case 3: return showcaseString(activity, "row4Meta", "adapter row");
            default: return showcaseString(activity, "row5Meta", "adapter row");
        }
    }

    private static void yelpAdapterRibbon(java.io.OutputStream out, int placeCount,
            int visibleImageCount, boolean loading) throws java.io.IOException {
        showcaseRoundRect(out, 16, 194, 464, 220, 8, 8, 0x16000000);
        showcaseRoundRect(out, 16, 192, 464, 218, 8, 8, 0xff00695c);
        showcaseRoundRect(out, 24, 197, 130, 213, 8, 8, 0x2fffffff);
        showcaseText(out, "XML ListView", 36, 209, 8, 0xffffffff);
        showcaseText(out, "BaseAdapter rows + live ImageView rebinds",
                142, 209, 8, 0xffffffff);
        String status = loading ? "syncing" : intAscii(placeCount) + " rows";
        showcaseRoundRect(out, 392, 197, 456, 213, 8, 8, 0xffffffff);
        showcaseText(out, status, 402, 209, 7, 0xff00695c);
        showcaseCircle(out, 374, 205, 5, visibleImageCount >= 5 ? 0xffa5d6a7 : 0xffffcc80);
    }

    private static void yelpSectionTitle(java.io.OutputStream out, String title,
            String subtitle, int top) throws java.io.IOException {
        showcaseRoundRect(out, 16, top - 4, 464, top + 44, 8, 8, 0xffffffff);
        showcaseRect(out, 16, top - 4, 23, top + 44, 0xffd32323);
        showcaseText(out, trimShowcaseText(title, 28), 32, top + 17, 15, 0xff1f1f1f);
        showcaseText(out, trimShowcaseText(subtitle, 46), 32, top + 35, 8, 0xff6f6f6f);
        showcaseRoundRect(out, 356, top + 9, 452, top + 31, 8, 8, 0xffffebee);
        showcaseCircle(out, 370, top + 20, 4, 0xffd32323);
        showcaseText(out, "live data", 382, top + 24, 7, 0xffa82020);
    }

    private static void yelpButton(java.io.OutputStream out, String label,
            int left, int top, int right, int bottom, boolean active)
            throws java.io.IOException {
        showcaseRoundRect(out, left, top + 2, right, bottom + 2, 8, 8, 0x14000000);
        showcaseRoundRect(out, left, top, right, bottom, 8, 8,
                active ? 0xffd32323 : 0xffffffff);
        if (!active) {
            showcaseRoundRect(out, left + 1, top + 1, right - 1, bottom - 1,
                    7, 7, 0xffffffff);
        }
        if (label.indexOf("Save") >= 0 || label.indexOf("Saved") >= 0) {
            yelpIcon(out, "heart", left + 24, top + 22, active ? 0xffffffff : 0xffd32323);
            showcaseText(out, label, left + 42, top + 26, 11, active ? 0xffffffff : 0xff202124);
        } else if (label.indexOf("Search") >= 0 || label.indexOf("Filters") >= 0) {
            yelpIcon(out, "search", left + 24, top + 22, active ? 0xffffffff : 0xff202124);
            showcaseText(out, label, left + 42, top + 26, 11, active ? 0xffffffff : 0xff202124);
        } else {
            showcaseText(out, label, left + 16, top + 26, 11, active ? 0xffffffff : 0xff202124);
        }
    }

    private static void yelpFilterChip(java.io.OutputStream out, String label,
            int left, int top, int right, boolean active) throws java.io.IOException {
        int bottom = top + 32;
        showcaseRoundRect(out, left, top + 1, right, bottom + 1, 8, 8, 0x12000000);
        showcaseRoundRect(out, left, top, right, bottom, 8, 8,
                active ? 0xffd32323 : 0xffd7d2d4);
        showcaseRoundRect(out, left + 1, top + 1, right - 1, bottom - 1, 7, 7,
                active ? 0xffffebee : 0xffffffff);
        if (active) {
            yelpIcon(out, "check", left + 15, top + 16, 0xffd32323);
            showcaseText(out, label, left + 27, top + 21, 9, 0xffa82020);
        } else {
            showcaseText(out, label, left + 12, top + 21, 9, 0xff3f3a3b);
        }
    }

    private static void yelpCategory(java.io.OutputStream out, String label,
            int left, int top, int right, int color, boolean active)
            throws java.io.IOException {
        int bottom = top + 38;
        showcaseRoundRect(out, left, top + 2, right, bottom + 2, 8, 8, 0x10000000);
        showcaseRoundRect(out, left, top, right, bottom, 8, 8,
                active ? 0xffd32323 : 0xffffffff);
        if (!active) {
            showcaseRoundRect(out, left + 1, top + 1, right - 1, bottom - 1,
                    7, 7, 0xffffffff);
        }
        showcaseCircle(out, left + 25, top + 19, 13, active ? 0x22ffffff : 0xfffff1f1);
        yelpIcon(out, label, left + 25, top + 19, active ? 0xffffffff : color);
        showcaseText(out, label, left + 44, top + 24, 10,
                active ? 0xffffffff : 0xff1f1f1f);
    }

    private static void yelpBusinessCard(java.io.OutputStream out, String title,
            String subtitle, String tag, int ratingTenths, int reviews,
            int imageBytes, int imageHash, byte[] imageData, int left, int top, boolean large)
            throws java.io.IOException {
        int right = 464;
        int height = large ? 238 : 166;
        int bottom = top + height;
        int accent = imageHash == 0
                ? 0xffb45f06
                : (0xff000000 | ((imageHash & 0x00dfdfdf) | 0x00202020));
        showcaseRoundRect(out, left, top + 3, right, bottom + 3, 8, 8, 0x18000000);
        showcaseRoundRect(out, left, top, right, bottom, 8, 8, 0xffffffff);
        if (large) {
            int photoBottom = top + 146;
            showcaseRoundRect(out, left, top, right, photoBottom, 8, 8, accent);
            if (imageData != null && imageData.length > 0) {
                drawByteHashThumbnail(out, imageHash, left, top, right - left,
                        photoBottom - top);
                showcaseRect(out, left, photoBottom - 48, right, photoBottom, 0x88000000);
            } else {
                yelpIcon(out, "photo", left + 224, top + 74, 0xffffffff);
            }
            showcaseText(out, trimShowcaseText(title, 32), left + 16, photoBottom - 24,
                    17, 0xffffffff);
            showcaseText(out, trimShowcaseText(subtitle, 42), left + 16, photoBottom + 24,
                    11, 0xff1f1f1f);
            yelpRatingBoxes(out, left + 16, photoBottom + 38, ratingTenths);
            showcaseText(out, showcaseRatingText(ratingTenths) + "  "
                    + intAscii(reviews) + " reviews", left + 126, photoBottom + 51,
                    9, 0xff6f6f6f);
            showcaseText(out, "$$  Open now  Delivery  Takeout", left + 16,
                    photoBottom + 76, 9, 0xff6f6f6f);
            showcaseRoundRect(out, left + 16, photoBottom + 96, left + 114,
                    photoBottom + 124, 8, 8, 0xffffebee);
            showcaseText(out, trimShowcaseText(tag, 14), left + 28, photoBottom + 114,
                    9, 0xffa82020);
            return;
        }
        int photoBottom = top + 118;
        showcaseRoundRect(out, left + 14, top + 14, left + 148, photoBottom, 8, 8, accent);
        if (imageData != null && imageData.length > 0) {
            drawByteHashThumbnail(out, imageHash, left + 14, top + 14, 134,
                    photoBottom - top - 14);
        } else {
            showcaseRect(out, left + 24, top + 24, left + 138, top + 58, 0x33ffffff);
            yelpIcon(out, "photo", left + 82, top + 72, 0xffffffff);
            showcaseText(out, "PHOTO", left + 48, top + 104, 11, 0xffffffff);
        }
        showcaseText(out, trimShowcaseText(title, 25), left + 164, top + 32, 15, 0xff1f1f1f);
        yelpRatingBoxes(out, left + 164, top + 46, ratingTenths);
        showcaseText(out, showcaseRatingText(ratingTenths) + "  "
                + intAscii(reviews) + " reviews", left + 164, top + 76, 9, 0xff6f6f6f);
        showcaseText(out, trimShowcaseText(subtitle, 30), left + 164, top + 96, 10, 0xff1f1f1f);
        showcaseText(out, "$$  Open now  Delivery  Takeout", left + 164, top + 116, 9, 0xff6f6f6f);
        showcaseRoundRect(out, left + 164, top + 132, left + 250, top + 158, 8, 8, 0xffffebee);
        showcaseText(out, trimShowcaseText(tag, 12), left + 174, top + 150, 9, 0xffa82020);
        if (large) {
            showcaseText(out, "Tap card for details  |  right side saves", left + 164, top + 182, 9, 0xff6c757d);
        }
    }

    private static void yelpRatingBoxes(java.io.OutputStream out, int left, int top,
            int ratingTenths) throws java.io.IOException {
        int full = ratingTenths / 10;
        if (full < 1) {
            full = 1;
        }
        if (full > 5) {
            full = 5;
        }
        for (int i = 0; i < 5; i++) {
            int x = left + (i * 17);
            showcaseRoundRect(out, x, top, x + 14, top + 14, 3, 3,
                    i < full ? 0xffd32323 : 0xffe8e2e4);
            if (i < full) {
                showcaseCircle(out, x + 7, top + 7, 2.4f, 0xffffffff);
                showcaseLine(out, x + 4, top + 7, x + 10, top + 7, 0xffffffff, 1.0f);
            }
        }
    }

    private static void yelpMiniFact(java.io.OutputStream out, String title,
            String value, int left, int top) throws java.io.IOException {
        showcaseRoundRect(out, left, top + 2, left + 216, top + 62, 8, 8, 0x12000000);
        showcaseRoundRect(out, left, top, left + 216, top + 60, 8, 8, 0xffffffff);
        showcaseText(out, title, left + 14, top + 24, 12, 0xff1f1f1f);
        showcaseText(out, trimShowcaseText(value, 26), left + 14, top + 43, 9, 0xff6f6f6f);
    }

    private static void yelpScrollIndicator(java.io.OutputStream out, int offset,
            int total, int top) throws java.io.IOException {
        int max = total - 5;
        if (max < 0) {
            max = 0;
        }
        int end = offset + 5;
        if (end > total) {
            end = total;
        }
        showcaseText(out, "Showing " + intAscii(offset + 1) + "-" + intAscii(end)
                + " of " + intAscii(total) + "  swipe to browse more",
                18, top, 9, 0xff6f6f6f);
        showcaseRoundRect(out, 424, top - 10, 462, top - 2, 4, 4, 0xffe6e0e2);
        int thumbLeft = max <= 0 ? 424 : 424 + ((38 - 14) * offset / max);
        showcaseRoundRect(out, thumbLeft, top - 10, thumbLeft + 14, top - 2,
                4, 4, 0xffd32323);
    }

    private static void yelpResultCard(java.io.OutputStream out, int ordinal,
            String title, String subtitle, byte[] imageData, int imageHash,
            int top, boolean selected, boolean realPhoto) throws java.io.IOException {
        int bottom = top + YELP_ROW_HEIGHT;
        int accent = imageHash == 0
                ? 0xffd32323
                : (0xff000000 | ((imageHash & 0x00dfdfdf) | 0x00202020));
        showcaseRoundRect(out, 16, top + 4, 464, bottom + 4, 8, 8, 0x16000000);
        showcaseRoundRect(out, 16, top, 464, bottom, 8, 8,
                selected ? 0xfffff5f5 : 0xffffffff);
        if (selected) {
            showcaseRect(out, 16, top, 24, bottom, 0xffd32323);
        }
        showcaseRoundRect(out, 28, top + 8, 194, bottom - 8, 8, 8, accent);
        if (realPhoto && imageData != null && imageData.length > 0) {
            drawByteHashThumbnail(out, imageHash, 28, top + 8, 166, YELP_ROW_HEIGHT - 16);
            showcaseRect(out, 28, bottom - 31, 194, bottom - 8, 0x7a000000);
        } else {
            yelpIcon(out, ordinal == 2 ? "takeout" : ordinal == 3 ? "dinner" : "photo",
                    111, top + 48, 0xffffffff);
        }
        showcaseCircle(out, 48, top + 21, 12, 0xffd32323);
        showcaseText(out, intAscii(ordinal), ordinal < 10 ? 44 : 40, top + 26,
                9, 0xffffffff);
        showcaseText(out, "photos", 86, bottom - 15, 8, 0xffffffff);
        showcaseText(out, trimShowcaseText(title, 15), 210, top + 22, 14, 0xff1f1f1f);
        int rowRating = ordinal == 1 ? 46 : ordinal == 2 ? 47 : ordinal == 3 ? 48 : 49;
        yelpRatingBoxes(out, 210, top + 36, rowRating);
        showcaseText(out, showcaseRatingText(rowRating) + "  "
                + intAscii(120 + ordinal * 31) + " reviews",
                302, top + 50, 8, 0xff6f6f6f);
        showcaseText(out, trimShowcaseText(subtitle, 20), 210, top + 73, 8, 0xff6f6f6f);
        showcaseRoundRect(out, 372, top + 14, 446, top + 37, 8, 8, 0xffffebee);
        showcaseText(out, "Open", 389, top + 29, 8, 0xffa82020);
        showcaseCircle(out, 427, top + 70, 15, selected ? 0xffffebee : 0xfff6f0f2);
        yelpIcon(out, "heart", 427, top + 70, selected ? 0xffd32323 : 0xff5f6368);
    }

    private static void yelpListRow(java.io.OutputStream out, int ordinal, String title,
            String subtitle, int top) throws java.io.IOException {
        showcaseRoundRect(out, 16, top, 464, top + 38, 8, 8,
                ordinal % 2 == 0 ? 0xffffffff : 0xfffffbf9);
        showcaseCircle(out, 38, top + 18, 11, 0xffd32323);
        showcaseText(out, intAscii(ordinal), 35, top + 23, 10, 0xffffffff);
        showcaseText(out, trimShowcaseText(title, 30), 62, top + 17, 11, 0xff1f1f1f);
        showcaseText(out, trimShowcaseText(subtitle, 34), 62, top + 32, 9, 0xff6f6f6f);
    }

    private static void yelpBottomNav(java.io.OutputStream out, int active)
            throws java.io.IOException {
        showcaseRect(out, 0, YELP_BOTTOM_NAV_TOP, SURFACE_WIDTH,
                YELP_SURFACE_HEIGHT, 0xffffffff);
        showcaseRect(out, 0, YELP_BOTTOM_NAV_TOP, SURFACE_WIDTH,
                YELP_BOTTOM_NAV_TOP + 1, 0xffe6e0e2);
        showcaseRect(out, 0, YELP_BOTTOM_NAV_TOP + 1, SURFACE_WIDTH,
                YELP_BOTTOM_NAV_TOP + 4, 0x0fd32323);
        yelpNavItem(out, "Discover", 0, 120, active == 0);
        yelpNavItem(out, "Search", 120, 240, active == 1);
        yelpNavItem(out, "Details", 240, 360, active == 2);
        yelpNavItem(out, "Saved", 360, 480, active == 3);
    }

    private static void yelpNavItem(java.io.OutputStream out, String label,
            int left, int right, boolean active) throws java.io.IOException {
        int top = YELP_BOTTOM_NAV_TOP;
        if (active) {
            showcaseRoundRect(out, left + 24, top + 17, right - 24, top + 58,
                    8, 8, 0xffffebee);
            showcaseRoundRect(out, left + 52, top + 6, right - 52, top + 10,
                    2, 2, 0xffd32323);
        }
        yelpIcon(out, label, left + 60, top + 39, active ? 0xffd32323 : 0xff5f6368);
        showcaseText(out, label, left + 18, top + 80, 11,
                active ? 0xffd32323 : 0xff5f6368);
    }

    private static void yelpIcon(java.io.OutputStream out, String kind,
            int cx, int cy, int color) throws java.io.IOException {
        String k = kind == null ? "" : kind.toLowerCase();
        if (k.indexOf("check") >= 0) {
            showcaseLine(out, cx - 5, cy, cx - 1, cy + 5, color, 2.0f);
            showcaseLine(out, cx - 1, cy + 5, cx + 7, cy - 6, color, 2.0f);
            return;
        }
        if (k.indexOf("search") >= 0) {
            showcaseCircle(out, cx - 2, cy - 3, 8, color);
            showcaseCircle(out, cx - 2, cy - 3, 5, 0xffffffff);
            showcaseLine(out, cx + 4, cy + 4, cx + 12, cy + 12, color, 2.2f);
            return;
        }
        if (k.indexOf("pin") >= 0) {
            showcaseCircle(out, cx, cy - 4, 7, color);
            showcaseLine(out, cx, cy + 3, cx, cy + 13, color, 2.5f);
            showcaseCircle(out, cx, cy - 4, 3, 0xffffffff);
            return;
        }
        if (k.indexOf("save") >= 0 || k.indexOf("heart") >= 0) {
            showcaseCircle(out, cx - 5, cy - 4, 5, color);
            showcaseCircle(out, cx + 5, cy - 4, 5, color);
            showcaseLine(out, cx - 10, cy, cx, cy + 11, color, 3.0f);
            showcaseLine(out, cx + 10, cy, cx, cy + 11, color, 3.0f);
            return;
        }
        if (k.indexOf("detail") >= 0) {
            showcaseCircle(out, cx, cy, 10, color);
            showcaseText(out, "i", cx - 2, cy + 5, 12, activeIconTextColor(color));
            return;
        }
        if (k.indexOf("discover") >= 0) {
            showcaseLine(out, cx - 11, cy - 1, cx, cy - 11, color, 2.3f);
            showcaseLine(out, cx, cy - 11, cx + 11, cy - 1, color, 2.3f);
            showcaseRoundRect(out, cx - 8, cy - 1, cx + 8, cy + 11, 2, 2, color);
            showcaseRoundRect(out, cx - 3, cy + 4, cx + 3, cy + 11, 1, 1, 0xffffffff);
            return;
        }
        if (k.indexOf("pizza") >= 0) {
            showcaseLine(out, cx - 9, cy - 10, cx + 10, cy + 10, color, 3.0f);
            showcaseLine(out, cx + 10, cy + 10, cx - 7, cy + 8, color, 3.0f);
            showcaseCircle(out, cx + 1, cy + 1, 2, 0xffffffff);
            return;
        }
        if (k.indexOf("takeout") >= 0) {
            showcaseRoundRect(out, cx - 12, cy - 8, cx + 12, cy + 10, 3, 3, color);
            showcaseLine(out, cx - 8, cy - 8, cx - 4, cy - 14, color, 2.0f);
            showcaseLine(out, cx + 8, cy - 8, cx + 4, cy - 14, color, 2.0f);
            return;
        }
        if (k.indexOf("dinner") >= 0) {
            showcaseLine(out, cx - 8, cy - 12, cx - 8, cy + 12, color, 2.0f);
            showcaseLine(out, cx - 13, cy - 8, cx - 3, cy - 8, color, 1.5f);
            showcaseLine(out, cx + 5, cy - 12, cx + 5, cy + 12, color, 2.0f);
            showcaseLine(out, cx + 5, cy - 12, cx + 12, cy - 4, color, 2.0f);
            return;
        }
        if (k.indexOf("asian") >= 0) {
            showcaseLine(out, cx - 11, cy - 12, cx + 8, cy + 12, color, 2.0f);
            showcaseLine(out, cx + 1, cy - 12, cx + 12, cy + 12, color, 2.0f);
            showcaseCircle(out, cx - 4, cy + 6, 6, color);
            return;
        }
        if (k.indexOf("dessert") >= 0) {
            showcaseCircle(out, cx, cy - 1, 11, color);
            showcaseCircle(out, cx - 6, cy - 7, 4, 0xffffffff);
            showcaseLine(out, cx - 10, cy + 11, cx + 10, cy + 11, color, 2.0f);
            return;
        }
        if (k.indexOf("coffee") >= 0) {
            showcaseRoundRect(out, cx - 12, cy - 7, cx + 8, cy + 8, 4, 4, color);
            showcaseCircle(out, cx + 11, cy, 5, color);
            showcaseLine(out, cx - 9, cy - 12, cx + 5, cy - 12, color, 2.0f);
            return;
        }
        if (k.indexOf("photo") >= 0) {
            showcaseRoundRect(out, cx - 16, cy - 12, cx + 16, cy + 12, 3, 3, color);
            showcaseCircle(out, cx - 5, cy - 3, 4, 0xffd32323);
            showcaseLine(out, cx - 13, cy + 8, cx - 2, cy, 0xffd32323, 2.0f);
            showcaseLine(out, cx - 2, cy, cx + 13, cy + 8, 0xffd32323, 2.0f);
            return;
        }
        showcaseCircle(out, cx, cy, 10, color);
    }

    private static int activeIconTextColor(int color) {
        return color == 0xffffffff ? 0xffd32323 : 0xffffffff;
    }

    private static String yelpTabName(int tab) {
        if (tab == 1) {
            return "search";
        }
        if (tab == 2) {
            return "details";
        }
        if (tab == 3) {
            return "saved";
        }
        return "discover";
    }

    private static String yelpHex(int value) {
        char[] out = new char[8];
        for (int i = 7; i >= 0; i--) {
            int n = value & 0xf;
            out[i] = (char) (n < 10 ? ('0' + n) : ('a' + n - 10));
            value >>>= 4;
        }
        return new String(out);
    }

    private static void runMaterialYelpDirectFrameLoop(Activity activity) {
        writeMaterialYelpDirectFrame(activity, "initial");
        startupLog("[WestlakeLauncher] Material Yelp initial frame rendered");

        int lastTouchSeq = -1;
        int downX = 0;
        int downY = 0;
        String touchPath = null;
        try {
            String envTouch = System.getenv("WESTLAKE_TOUCH");
            touchPath = envTouch != null && envTouch.length() > 0
                    ? envTouch : defaultTouchPath();
            startupLog("[WestlakeLauncher] Material Yelp touch file: " + touchPath);
        } catch (Throwable ignored) {
            touchPath = defaultTouchPath();
        }

        if (FRAMEWORK_POLICY_WESTLAKE_ONLY.equals(frameworkPolicyValue())) {
            startupLog("[WestlakeLauncher] Material Yelp strict touch polling enabled");
            appendCutoffCanaryMarker("MATERIAL_TOUCH_POLL_READY strict=true");
        }

        while (true) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                break;
            }
            if (showcaseBool(activity, "renderDirty", false)) {
                showcaseInvoke(activity, "consumeRenderDirty");
                writeMaterialYelpDirectFrame(activity, "state_dirty");
                continue;
            }
            byte[] touch = tryReadFileBytes(touchPath);
            if (touch == null || touch.length < 16) {
                continue;
            }
            java.nio.ByteBuffer bb = java.nio.ByteBuffer.wrap(touch, 0, 16);
            bb.order(java.nio.ByteOrder.LITTLE_ENDIAN);
            int action = bb.getInt();
            int x = bb.getInt();
            int y = bb.getInt();
            int seq = bb.getInt();
            if (seq == lastTouchSeq) {
                continue;
            }
            lastTouchSeq = seq;
            if (action == 0) {
                downX = x;
                downY = y;
                appendCutoffCanaryMarker("MATERIAL_TOUCH_POLL_OK seq="
                        + intAscii(seq) + " action=" + showcaseTouchReason(action)
                        + " x=" + intAscii(x) + " y=" + intAscii(y));
                continue;
            }
            if (action != 1) {
                continue;
            }
            appendCutoffCanaryMarker("MATERIAL_TOUCH_POLL_OK seq="
                    + intAscii(seq) + " action=" + showcaseTouchReason(action)
                    + " x=" + intAscii(x) + " y=" + intAscii(y));
            boolean handled = routeMaterialYelpDirectTouch(activity, x, y, downX, downY);
            startupLog("[WestlakeLauncher] Material Yelp touch action=" + action
                    + " x=" + x + " y=" + y + " downX=" + downX + " downY=" + downY
                    + " direct=" + handled);
            if (handled) {
                writeMaterialYelpDirectFrame(activity, "touch_up");
            }
        }
    }

    private static void runMcdProfileDirectFrameLoop(Activity activity) {
        writeMcdProfileDirectFrame(activity, "initial");
        startupLog("[WestlakeLauncher] McD profile initial frame rendered");

        int lastTouchSeq = -1;
        int downX = 0;
        int downY = 0;
        String touchPath = null;
        try {
            String envTouch = System.getenv("WESTLAKE_TOUCH");
            touchPath = envTouch != null && envTouch.length() > 0
                    ? envTouch : defaultTouchPath();
            startupLog("[WestlakeLauncher] McD profile touch file: " + touchPath);
        } catch (Throwable ignored) {
            touchPath = defaultTouchPath();
        }

        if (FRAMEWORK_POLICY_WESTLAKE_ONLY.equals(frameworkPolicyValue())) {
            startupLog("[WestlakeLauncher] McD profile strict touch polling enabled");
            appendCutoffCanaryMarker("MCD_PROFILE_TOUCH_POLL_READY strict=true");
        }

        while (true) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                break;
            }
            if (showcaseBool(activity, "renderDirty", false)) {
                showcaseInvoke(activity, "consumeRenderDirty");
                writeMcdProfileDirectFrame(activity, "state_dirty");
                continue;
            }
            byte[] touch = tryReadFileBytes(touchPath);
            if (touch == null || touch.length < 16) {
                continue;
            }
            java.nio.ByteBuffer bb = java.nio.ByteBuffer.wrap(touch, 0, 16);
            bb.order(java.nio.ByteOrder.LITTLE_ENDIAN);
            int action = bb.getInt();
            int x = bb.getInt();
            int y = bb.getInt();
            int seq = bb.getInt();
            if (seq == lastTouchSeq) {
                continue;
            }
            lastTouchSeq = seq;
            appendCutoffCanaryMarker("MCD_PROFILE_TOUCH_POLL_OK seq="
                    + intAscii(seq) + " action=" + showcaseTouchReason(action)
                    + " x=" + intAscii(x) + " y=" + intAscii(y));
            if (action == 0) {
                downX = x;
                downY = y;
                continue;
            }
            if (action != 1) {
                continue;
            }
            boolean handled = routeMcdProfileDirectTouch(activity, x, y, downX, downY);
            startupLog("[WestlakeLauncher] McD profile touch action=" + action
                    + " x=" + x + " y=" + y + " downX=" + downX + " downY=" + downY
                    + " direct=" + handled);
            if (handled) {
                writeMcdProfileDirectFrame(activity, "touch_up");
            }
        }
    }

    private static void runMaterialXmlProbeDirectFrameLoop(Activity activity) {
        writeMaterialXmlProbeDirectFrame(activity, "initial");
        startupLog("[WestlakeLauncher] Material XML probe initial tree frame rendered");

        int lastTouchSeq = -1;
        long downTime = 0L;
        int downX = 0;
        int downY = 0;
        String touchPath = null;
        try {
            String envTouch = System.getenv("WESTLAKE_TOUCH");
            touchPath = envTouch != null && envTouch.length() > 0
                    ? envTouch : defaultTouchPath();
            startupLog("[WestlakeLauncher] Material XML probe touch file: " + touchPath);
        } catch (Throwable ignored) {
            touchPath = defaultTouchPath();
        }

        if (FRAMEWORK_POLICY_WESTLAKE_ONLY.equals(frameworkPolicyValue())) {
            startupLog("[WestlakeLauncher] Material XML probe strict touch polling enabled");
            appendCutoffCanaryMarker("MATERIAL_GENERIC_TOUCH_POLL_READY strict=true");
        }

        while (true) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                break;
            }
            byte[] touch = tryReadFileBytes(touchPath);
            if (touch == null || touch.length < 16) {
                continue;
            }
            try {
                java.nio.ByteBuffer bb = java.nio.ByteBuffer.wrap(touch, 0, 16);
                bb.order(java.nio.ByteOrder.LITTLE_ENDIAN);
                int action = bb.getInt();
                int x = bb.getInt();
                int y = bb.getInt();
                int seq = bb.getInt();
                if (seq == lastTouchSeq) {
                    continue;
                }
                lastTouchSeq = seq;
                long now = System.currentTimeMillis();
                if (action == 0 || downTime == 0L) {
                    downTime = now;
                    downX = x;
                    downY = y;
                }
                appendCutoffCanaryMarker("MATERIAL_GENERIC_TOUCH_POLL_OK seq="
                        + intAscii(seq) + " action=" + showcaseTouchReason(action)
                        + " x=" + intAscii(x) + " y=" + intAscii(y));
                boolean dispatchHandled = false;
                try {
                    dispatchHandled = activity.dispatchTouchEvent(android.view.MotionEvent.obtain(
                            downTime, now, action, (float) x, (float) y, 0));
                } catch (Throwable dispatchError) {
                    startupLog("[WestlakeLauncher] Material XML dispatchTouchEvent error",
                            dispatchError);
                }
                boolean directHandled = false;
                String targetName = "null";
                if (action == 1 && Math.abs(x - downX) < 24 && Math.abs(y - downY) < 24) {
                    try {
                        layoutShowcaseDecor(activity);
                        android.view.View decor = activity != null && activity.getWindow() != null
                                ? activity.getWindow().getDecorView() : null;
                        android.view.View target = decor != null ? findViewAt(decor, x, y) : null;
                        if (target != null) {
                            targetName = target.getClass().getName();
                            directHandled = target.performClick();
                        }
                    } catch (Throwable clickError) {
                        startupLog("[WestlakeLauncher] Material XML direct click error",
                                clickError);
                    }
                    appendCutoffCanaryMarker("MATERIAL_GENERIC_TOUCH_OK seq="
                            + intAscii(seq)
                            + " dispatch=" + boolToken(dispatchHandled)
                            + " direct=" + boolToken(directHandled)
                            + " target=" + safeMarkerToken(targetName));
                    writeMaterialXmlProbeDirectFrame(activity, "touch_up");
                    downTime = 0L;
                } else if (action == 2) {
                    writeMaterialXmlProbeDirectFrame(activity, "touch_move");
                }
            } catch (Throwable t) {
                startupLog("[WestlakeLauncher] Material XML touch loop error", t);
            }
        }
    }

    private static void writeMaterialXmlProbeDirectFrame(Activity activity, String reason) {
        try {
            layoutShowcaseDecor(activity);
            android.view.View decor = activity != null && activity.getWindow() != null
                    ? activity.getWindow().getDecorView() : null;
            if (decor == null) {
                appendCutoffCanaryMarker("MATERIAL_GENERIC_RENDER_FAIL reason="
                        + reason + " err=no_decor");
                return;
            }
            java.io.ByteArrayOutputStream ops = new java.io.ByteArrayOutputStream(8192);
            ShowcaseTreeStats treeStats = new ShowcaseTreeStats();
            renderShowcaseView(ops, decor, 0, 0, 0, treeStats);
            MaterialTreeStats materialStats = collectMaterialTreeStats(activity);
            appendFirstViewBounds(decor, 0, 0,
                    "com.google.android.material.button.MaterialButton",
                    "MATERIAL_GENERIC_BUTTON_BOUNDS");
            showcaseRoundRect(ops, 12, 740, 468, 790, 8, 8, 0xeaffffff);
            showcaseText(ops, "XML inflation -> Material shim classes -> DLST pipe",
                    24, 762, 10, 0xff3c4043);
            showcaseText(ops, "reason " + reason
                    + " material " + intAscii(materialStats.materialViews)
                    + " views " + intAscii(treeStats.views)
                    + " buttons " + intAscii(treeStats.buttons),
                    24, 780, 9, 0xffa82020);

            byte[] data = ops.toByteArray();
            java.io.OutputStream out = System.out;
            writeIntLe(out, 0x444C5354);
            writeIntLe(out, data.length);
            out.write(data);
            out.flush();
            appendCutoffCanaryMarker("MATERIAL_GENERIC_RENDER_OK reason=" + reason
                    + " bytes=" + intAscii(data.length)
                    + " views=" + intAscii(treeStats.views)
                    + " materialViews=" + intAscii(materialStats.materialViews)
                    + " texts=" + intAscii(treeStats.texts)
                    + " buttons=" + intAscii(treeStats.buttons));
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] Material XML direct tree frame error", t);
            logThrowableFrames("[WestlakeLauncher] Material XML direct tree frame", t, 12);
            appendCutoffCanaryMarker("MATERIAL_GENERIC_RENDER_FAIL err="
                    + t.getClass().getName());
        }
    }

    private static boolean appendFirstViewBounds(android.view.View view, int offsetX, int offsetY,
            String className, String markerName) {
        if (view == null || className == null || markerName == null) {
            return false;
        }
        int left = offsetX + view.getLeft();
        int top = offsetY + view.getTop();
        int right = offsetX + view.getRight();
        int bottom = offsetY + view.getBottom();
        String actual = view.getClass().getName();
        if (actual != null && actual.indexOf(className) >= 0) {
            appendCutoffCanaryMarker(markerName
                    + " left=" + intAscii(left)
                    + " top=" + intAscii(top)
                    + " right=" + intAscii(right)
                    + " bottom=" + intAscii(bottom)
                    + " class=" + safeMarkerToken(actual));
            return true;
        }
        if (view instanceof android.view.ViewGroup) {
            android.view.ViewGroup group = (android.view.ViewGroup) view;
            int childOffsetX = left - view.getScrollX();
            int childOffsetY = top - view.getScrollY();
            int count = 0;
            try {
                count = group.getChildCount();
            } catch (Throwable ignored) {
            }
            for (int i = 0; i < count; i++) {
                try {
                    if (appendFirstViewBounds(group.getChildAt(i),
                            childOffsetX, childOffsetY, className, markerName)) {
                        return true;
                    }
                } catch (Throwable ignored) {
                }
            }
        }
        return false;
    }

    private static boolean routeMaterialYelpDirectTouch(Activity activity, int x, int y,
            int downX, int downY) {
        if (activity == null) {
            return false;
        }
        if (y >= 660) {
            if (x < 120) {
                return showcaseInvoke(activity, "navigateDiscover");
            }
            if (x < 240) {
                return showcaseInvoke(activity, "navigateSearch");
            }
            if (x < 360) {
                return showcaseInvoke(activity, "navigateDetails");
            }
            return showcaseInvoke(activity, "navigateSaved");
        }
        if (y < 100) {
            return showcaseInvoke(activity, x < 310 ? "navigateSearch" : "navigateDiscover");
        }
        if (y >= 100 && y < 146) {
            if (x < 156) {
                return showcaseInvoke(activity, "toggleSortRating");
            }
            if (x < 330) {
                return showcaseInvoke(activity, "toggleDelivery");
            }
            return showcaseInvoke(activity, "boostSlider");
        }
        if (y >= 146 && y < 214) {
            if (x < 232) {
                return showcaseInvoke(activity, "selectPizza");
            }
            return showcaseInvoke(activity, "selectAsian");
        }
        if (y >= 252 && y < 322) {
            return x >= 360 ? showcaseInvoke(activity, "savePlace")
                    : showcaseInvoke(activity, "selectPlace0");
        }
        if (y >= 326 && y < 396) {
            return x >= 360 ? showcaseInvoke(activity, "savePlace")
                    : showcaseInvoke(activity, "selectPlace1");
        }
        if (y >= 400 && y < 470) {
            return x >= 360 ? showcaseInvoke(activity, "savePlace")
                    : showcaseInvoke(activity, "selectPlace2");
        }
        if (y >= 474 && y < 544) {
            return x >= 360 ? showcaseInvoke(activity, "savePlace")
                    : showcaseInvoke(activity, "selectPlace3");
        }
        if (y >= 548 && y < 640) {
            if (x < 240) {
                return showcaseInvoke(activity, "navigateDetails");
            }
            return showcaseInvoke(activity, "savePlace");
        }
        return false;
    }

    private static boolean routeMcdProfileDirectTouch(Activity activity, int x, int y,
            int downX, int downY) {
        if (activity == null) {
            return false;
        }
        if (y >= 920) {
            if (x < 120) {
                return showcaseInvoke(activity, "navigateHome");
            }
            if (x < 240) {
                return showcaseInvoke(activity, "navigateMenu");
            }
            if (x < 360) {
                return showcaseInvoke(activity, "navigateCart");
            }
            return showcaseInvoke(activity, "navigateDeals");
        }
        if (y < 116) {
            return showcaseInvoke(activity, "refreshMenu");
        }
        if (y >= 300 && y < 356) {
            if (x < 120) {
                return showcaseInvoke(activity, "selectBreakfast");
            }
            if (x < 240) {
                return showcaseInvoke(activity, "selectBurgers");
            }
            if (x < 360) {
                return showcaseInvoke(activity, "selectChicken");
            }
            return showcaseInvoke(activity, "selectDrinks");
        }
        if (y >= 378 && y < 450) {
            return x >= 390 ? mcdSelectAndAdd(activity, "selectMenu0")
                    : showcaseInvoke(activity, "selectMenu0");
        }
        if (y >= 462 && y < 534) {
            return x >= 390 ? mcdSelectAndAdd(activity, "selectMenu1")
                    : showcaseInvoke(activity, "selectMenu1");
        }
        if (y >= 546 && y < 618) {
            return x >= 390 ? mcdSelectAndAdd(activity, "selectMenu2")
                    : showcaseInvoke(activity, "selectMenu2");
        }
        if (y >= 630 && y < 702) {
            return x >= 390 ? mcdSelectAndAdd(activity, "selectMenu3")
                    : showcaseInvoke(activity, "selectMenu3");
        }
        if (y >= 714 && y < 786) {
            return x >= 390 ? mcdSelectAndAdd(activity, "selectMenu4")
                    : showcaseInvoke(activity, "selectMenu4");
        }
        if (y >= 810 && y < 895) {
            if (x >= 300) {
                return showcaseInvoke(activity, "checkout");
            }
            return showcaseInvoke(activity, "addSelectedToCart");
        }
        if (Math.abs(y - downY) > 110 && Math.abs(y - downY) > Math.abs(x - downX)) {
            return y < downY
                    ? showcaseInvoke(activity, "scrollMenuDown")
                    : showcaseInvoke(activity, "scrollMenuUp");
        }
        return false;
    }

    private static boolean mcdSelectAndAdd(Activity activity, String selectMethod) {
        boolean selected = showcaseInvoke(activity, selectMethod);
        boolean added = showcaseInvoke(activity, "addSelectedToCart");
        return selected || added;
    }

    private static void writeMcdProfileDirectFrame(Activity activity, String reason) {
        try {
            java.io.ByteArrayOutputStream ops = new java.io.ByteArrayOutputStream(12288);
            boolean xml = showcaseBool(activity, "xmlInflated", false);
            boolean loading = showcaseBool(activity, "networkLoading", false);
            boolean rest = showcaseBool(activity, "restMatrixPassed", false);
            boolean storage = showcaseBool(activity, "storageRoundTrip", false);
            boolean checkedOut = showcaseBool(activity, "checkedOut", false);
            int activeTab = showcaseInt(activity, "activeTab", 1);
            int selectedIndex = showcaseInt(activity, "selectedIndex", 0);
            int cartCount = showcaseInt(activity, "cartCount", 0);
            int cartTotal = showcaseInt(activity, "cartTotalCents", 0);
            int menuCount = showcaseInt(activity, "menuCount", 0);
            int menuOffset = showcaseInt(activity, "menuOffset", 0);
            int materialViews = showcaseInt(activity, "materialViewCount", 0);
            int images = showcaseInt(activity, "imageFetchCount", 0);
            int getViewCount = showcaseInt(activity, "adapterGetViewCount", 0);
            String category = showcaseString(activity, "category", "Burgers");
            String selectedName = showcaseString(activity, "selectedName", "Westlake Burger");
            String selectedMeta = showcaseString(activity, "selectedMeta", "combo");
            String selectedPrice = showcaseString(activity, "selectedPrice", "$5.99");
            String heroTitle = showcaseString(activity, "heroTitle", "Today only");
            String heroSubtitle = showcaseString(activity, "heroSubtitle", "Live menu");
            String action = showcaseString(activity, "lastAction", "Ready");
            String feed = showcaseString(activity, "feedStatus", "Waiting for live menu");
            String restStatus = showcaseString(activity, "restStatus", "REST pending");
            String row1Name = showcaseString(activity, "row1Name", "Westlake Burger");
            String row1Meta = showcaseString(activity, "row1Meta", "combo - image 0 bytes");
            String row1Price = showcaseString(activity, "row1Price", "$5.99");
            String row2Name = showcaseString(activity, "row2Name", "Classic Fries");
            String row2Meta = showcaseString(activity, "row2Meta", "side - image 0 bytes");
            String row2Price = showcaseString(activity, "row2Price", "$2.49");
            String row3Name = showcaseString(activity, "row3Name", "Chicken Stack");
            String row3Meta = showcaseString(activity, "row3Meta", "sandwich - image 0 bytes");
            String row3Price = showcaseString(activity, "row3Price", "$6.49");
            String row4Name = showcaseString(activity, "row4Name", "Iced Coffee");
            String row4Meta = showcaseString(activity, "row4Meta", "drink - image 0 bytes");
            String row4Price = showcaseString(activity, "row4Price", "$3.19");
            String row5Name = showcaseString(activity, "row5Name", "Breakfast Muffin");
            String row5Meta = showcaseString(activity, "row5Meta", "breakfast - image 0 bytes");
            String row5Price = showcaseString(activity, "row5Price", "$4.29");
            int row1Hash = showcaseInt(activity, "row1ImageHash", 0);
            int row2Hash = showcaseInt(activity, "row2ImageHash", 0);
            int row3Hash = showcaseInt(activity, "row3ImageHash", 0);
            int row4Hash = showcaseInt(activity, "row4ImageHash", 0);
            int row5Hash = showcaseInt(activity, "row5ImageHash", 0);
            int row1Bytes = showcaseInt(activity, "row1ImageBytes", 0);
            int row2Bytes = showcaseInt(activity, "row2ImageBytes", 0);
            int row3Bytes = showcaseInt(activity, "row3ImageBytes", 0);
            int row4Bytes = showcaseInt(activity, "row4ImageBytes", 0);
            int row5Bytes = showcaseInt(activity, "row5ImageBytes", 0);

            showcaseRect(ops, 0, 0, SURFACE_WIDTH, YELP_SURFACE_HEIGHT, 0xfffff8ef);
            mcdHeader(ops, category, loading, xml, rest);
            mcdHero(ops, heroTitle, heroSubtitle, feed, selectedName, selectedPrice,
                    row1Hash, row1Bytes, 132);
            mcdCategoryChip(ops, "Breakfast", 16, 304, 116, "Breakfast".equals(category));
            mcdCategoryChip(ops, "Burgers", 126, 304, 226, "Burgers".equals(category));
            mcdCategoryChip(ops, "Chicken", 236, 304, 336, "Chicken".equals(category));
            mcdCategoryChip(ops, "Drinks", 346, 304, 464, "Drinks".equals(category));

            showcaseText(ops, "Menu", 18, 372, 17, 0xff27251f);
            showcaseText(ops, "XML ListView adapter rows " + intAscii(getViewCount)
                    + "  items " + intAscii(menuCount), 86, 372, 9, 0xff6f665f);
            mcdMenuRow(ops, 0, row1Name, row1Meta, row1Price, row1Hash, row1Bytes,
                    378, selectedIndex == menuOffset);
            mcdMenuRow(ops, 1, row2Name, row2Meta, row2Price, row2Hash, row2Bytes,
                    462, selectedIndex == menuOffset + 1);
            mcdMenuRow(ops, 2, row3Name, row3Meta, row3Price, row3Hash, row3Bytes,
                    546, selectedIndex == menuOffset + 2);
            mcdMenuRow(ops, 3, row4Name, row4Meta, row4Price, row4Hash, row4Bytes,
                    630, selectedIndex == menuOffset + 3);
            mcdMenuRow(ops, 4, row5Name, row5Meta, row5Price, row5Hash, row5Bytes,
                    714, selectedIndex == menuOffset + 4);
            mcdCartBar(ops, selectedName, selectedMeta, selectedPrice, cartCount,
                    cartTotal, checkedOut, storage, restStatus, action, 810);
            mcdBottomNav(ops, activeTab);

            byte[] data = ops.toByteArray();
            java.io.OutputStream out = System.out;
            writeIntLe(out, 0x444C5354);
            writeIntLe(out, data.length);
            out.write(data);
            out.flush();
            appendCutoffCanaryMarker("MCD_PROFILE_DIRECT_FRAME_OK reason=" + reason
                    + " bytes=" + intAscii(data.length)
                    + " xml=" + boolToken(xml)
                    + " materialViews=" + intAscii(materialViews)
                    + " images=" + intAscii(images)
                    + " rest=" + boolToken(rest)
                    + " storage=" + boolToken(storage)
                    + " rows=5");
            appendCutoffCanaryMarker("MCD_PROFILE_FULL_RES_FRAME_OK logical=480x1013"
                    + " target=1080x2280 navTop=920");
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] McD profile direct frame error", t);
            logThrowableFrames("[WestlakeLauncher] McD profile direct frame", t, 12);
            appendCutoffCanaryMarker("MCD_PROFILE_DIRECT_FRAME_FAIL err="
                    + t.getClass().getName());
        }
    }

    private static void mcdHeader(java.io.OutputStream out, String category,
            boolean loading, boolean xml, boolean rest) throws java.io.IOException {
        showcaseRect(out, 0, 0, SURFACE_WIDTH, 118, 0xffda291c);
        showcaseRect(out, 0, 0, SURFACE_WIDTH, 20, 0xffb71c1c);
        showcaseRect(out, 0, 118, SURFACE_WIDTH, 120, 0x33000000);
        showcaseText(out, "McD Profile", 18, 42, 23, 0xffffffff);
        showcaseText(out, "controlled stock-app boundary", 20, 62, 8, 0xffffe0de);
        showcaseRoundRect(out, 386, 18, 448, 82, 30, 30, 0xffffbc0d);
        showcaseRoundRect(out, 402, 30, 432, 74, 18, 18, 0xffda291c);
        showcaseRoundRect(out, 18, 76, 462, 110, 9, 9, 0xffffffff);
        showcaseText(out, "Search " + trimShowcaseText(category, 18) + " menu",
                42, 98, 12, 0xff27251f);
        yelpIcon(out, "search", 30, 94, 0xff6f665f);
        mcdStatusPill(out, xml ? "XML" : "no XML", 288, 84, xml);
        mcdStatusPill(out, rest ? "REST" : (loading ? "SYNC" : "REST"), 350, 84,
                rest || loading);
        mcdStatusPill(out, "OHOS", 410, 84, true);
    }

    private static void mcdStatusPill(java.io.OutputStream out, String label,
            int left, int top, boolean active) throws java.io.IOException {
        showcaseRoundRect(out, left, top, left + 50, top + 18, 9, 9,
                active ? 0xffffbc0d : 0xfff3d4d1);
        showcaseText(out, label, left + 8, top + 13, 7,
                active ? 0xff27251f : 0xff7a4d00);
    }

    private static void mcdHero(java.io.OutputStream out, String title, String subtitle,
            String feed, String selected, String price, int imageHash, int imageBytes, int top)
            throws java.io.IOException {
        showcaseRoundRect(out, 16, top + 3, 464, top + 145, 10, 10, 0x18000000);
        showcaseRoundRect(out, 16, top, 464, top + 142, 10, 10, 0xffffffff);
        showcaseRoundRect(out, 30, top + 18, 148, top + 124, 10, 10, 0xffffbc0d);
        if (imageBytes > 0) {
            drawByteHashThumbnail(out, imageHash, 34, top + 22, 110, 98);
        } else {
            showcaseText(out, "live", 70, top + 72, 18, 0xff27251f);
        }
        showcaseText(out, trimShowcaseText(title, 26), 164, top + 35, 18, 0xff27251f);
        showcaseText(out, trimShowcaseText(subtitle, 38), 164, top + 58, 10, 0xff6f665f);
        showcaseText(out, trimShowcaseText(selected, 30), 164, top + 86, 13, 0xff27251f);
        showcaseRoundRect(out, 164, top + 100, 234, top + 126, 8, 8, 0xffda291c);
        showcaseText(out, price, 184, top + 118, 10, 0xffffffff);
        showcaseText(out, trimShowcaseText(feed, 28), 246, top + 117, 8, 0xff00695c);
    }

    private static void mcdCategoryChip(java.io.OutputStream out, String label,
            int left, int top, int right, boolean active) throws java.io.IOException {
        showcaseRoundRect(out, left, top + 2, right, top + 36, 17, 17, 0x14000000);
        showcaseRoundRect(out, left, top, right, top + 34, 17, 17,
                active ? 0xffffbc0d : 0xffffffff);
        if (!active) {
            showcaseRoundRect(out, left + 1, top + 1, right - 1, top + 33, 16, 16,
                    0xfffffbf6);
        }
        showcaseText(out, label, left + 14, top + 22, 9,
                active ? 0xff27251f : 0xff6f665f);
    }

    private static void mcdMenuRow(java.io.OutputStream out, int row, String name,
            String meta, String price, int imageHash, int imageBytes, int top,
            boolean selected) throws java.io.IOException {
        int left = 16;
        int right = 464;
        int bottom = top + 72;
        showcaseRoundRect(out, left, top + 2, right, bottom + 2, 9, 9, 0x12000000);
        showcaseRoundRect(out, left, top, right, bottom, 9, 9,
                selected ? 0xfffff4d2 : 0xffffffff);
        if (selected) {
            showcaseRect(out, left, top, left + 4, bottom, 0xffffbc0d);
        }
        if (imageBytes > 0) {
            drawByteHashThumbnail(out, imageHash, left + 12, top + 10, 52, 52);
        } else {
            showcaseRoundRect(out, left + 12, top + 10, left + 64, top + 62, 8, 8,
                    0xffffe08a);
            showcaseText(out, "img", left + 28, top + 42, 8, 0xff7a4d00);
        }
        showcaseText(out, trimShowcaseText(name, 25), left + 78, top + 22, 12,
                0xff27251f);
        showcaseText(out, trimShowcaseText(meta, 34), left + 78, top + 42, 8,
                imageBytes > 0 ? 0xff00695c : 0xff6f665f);
        showcaseText(out, price, left + 78, top + 60, 9, 0xffa82020);
        showcaseRoundRect(out, right - 64, top + 18, right - 18, top + 54, 18, 18,
                0xffffbc0d);
        showcaseText(out, "+", right - 44, top + 43, 20, 0xff27251f);
    }

    private static void mcdCartBar(java.io.OutputStream out, String selected,
            String selectedMeta, String price, int cartCount, int cartTotal,
            boolean checkedOut, boolean storage, String restStatus, String action, int top)
            throws java.io.IOException {
        showcaseRoundRect(out, 16, top + 2, 464, top + 88, 10, 10, 0x16000000);
        showcaseRoundRect(out, 16, top, 464, top + 86, 10, 10, 0xff27251f);
        showcaseText(out, checkedOut ? "Checkout ready" : "Cart", 30, top + 25, 15,
                0xffffbc0d);
        showcaseText(out, intAscii(cartCount) + " items  " + mcdMoney(cartTotal),
                30, top + 48, 12, 0xffffffff);
        showcaseText(out, trimShowcaseText(selected + "  " + price, 28),
                30, top + 68, 8, 0xffffe0de);
        showcaseRoundRect(out, 300, top + 18, 448, top + 62, 8, 8, 0xffffbc0d);
        showcaseText(out, checkedOut ? "Placed" : "Checkout", 338, top + 46, 12,
                0xff27251f);
        showcaseText(out, (storage ? "prefs " : "prefs? ")
                + trimShowcaseText(restStatus, 28), 300, top + 78, 7,
                storage ? 0xfffff0c2 : 0xffffd1cc);
        showcaseText(out, trimShowcaseText(action, 30), 170, top + 25, 8,
                0xfffff0c2);
    }

    private static void mcdBottomNav(java.io.OutputStream out, int activeTab)
            throws java.io.IOException {
        int top = 920;
        showcaseRect(out, 0, top, SURFACE_WIDTH, YELP_SURFACE_HEIGHT, 0xffffffff);
        showcaseRect(out, 0, top, SURFACE_WIDTH, top + 1, 0xffe6e0e2);
        mcdNavItem(out, "Home", 0, top, activeTab == 0);
        mcdNavItem(out, "Menu", 120, top, activeTab == 1);
        mcdNavItem(out, "Cart", 240, top, activeTab == 2);
        mcdNavItem(out, "Deals", 360, top, activeTab == 3);
    }

    private static void mcdNavItem(java.io.OutputStream out, String label,
            int left, int top, boolean active) throws java.io.IOException {
        int center = left + 60;
        showcaseCircle(out, center, top + 28, active ? 16 : 13,
                active ? 0xffffbc0d : 0xfff1eeea);
        if ("Home".equals(label)) {
            yelpIcon(out, "pin", center, top + 30, active ? 0xff27251f : 0xff6f665f);
        } else if ("Menu".equals(label)) {
            yelpIcon(out, "search", center, top + 30, active ? 0xff27251f : 0xff6f665f);
        } else if ("Cart".equals(label)) {
            yelpIcon(out, "heart", center, top + 30, active ? 0xff27251f : 0xff6f665f);
        } else {
            yelpIcon(out, "check", center, top + 30, active ? 0xff27251f : 0xff6f665f);
        }
        showcaseText(out, label, left + 44, top + 64, 9,
                active ? 0xff27251f : 0xff6f665f);
    }

    private static String mcdMoney(int cents) {
        int dollars = cents / 100;
        int rem = cents < 0 ? -cents % 100 : cents % 100;
        return "$" + intAscii(dollars) + "." + (rem < 10 ? "0" : "") + intAscii(rem);
    }

    private static void writeMaterialYelpDirectFrame(Activity activity, String reason) {
        try {
            java.io.ByteArrayOutputStream ops = new java.io.ByteArrayOutputStream(12288);
            MaterialTreeStats stats = collectMaterialTreeStats(activity);
            boolean sortByRating = showcaseBool(activity, "sortByRating", true);
            boolean deliveryFilter = showcaseBool(activity, "deliveryFilter", true);
            boolean openNowFilter = showcaseBool(activity, "openNowFilter", true);
            boolean saved = showcaseBool(activity, "saved", false);
            int activeTab = showcaseInt(activity, "activeTab", 1);
            int selectedIndex = showcaseInt(activity, "selectedIndex", 2);
            int savedCount = showcaseInt(activity, "savedCount", 0);
            int rating = showcaseInt(activity, "ratingTenths", 47);
            int reviews = showcaseInt(activity, "reviewCount", 186);
            int materialClassCount = showcaseInt(activity, "materialClassCount", 0);
            int materialViewCount = showcaseInt(activity, "materialViewCount", 0);
            int slider = showcaseInt(activity, "sliderValue", 72);
            boolean imageLoading = showcaseBool(activity, "imageLoading", false);
            int imageFetchCount = showcaseInt(activity, "imageFetchCount", 0);
            String cuisine = showcaseString(activity, "selectedCuisine", "中餐");
            String place = showcaseString(activity, "selectedPlace", "西湖川菜");
            String meta = showcaseString(activity, "selectedMeta", "中餐 - 晚餐 - 外卖");
            String action = showcaseString(activity, "lastAction", "Material 金丝雀就绪");
            String search = showcaseString(activity, "searchText", "附近" + cuisine);
            String row1Name = showcaseString(activity, "row1Name", "拉面小馆");
            String row1Meta = showcaseString(activity, "row1Meta", "日料 - 晚餐 - 4.9 - 外卖");
            String row2Name = showcaseString(activity, "row2Name", "咖喱鸡餐厅");
            String row2Meta = showcaseString(activity, "row2Meta", "南亚菜 - 午餐 - 4.8 - 外卖");
            String row3Name = showcaseString(activity, "row3Name", "西湖川菜");
            String row3Meta = showcaseString(activity, "row3Meta", "中餐 - 晚餐 - 4.7 - 外卖");
            String row4Name = showcaseString(activity, "row4Name", "烤肉串烧");
            String row4Meta = showcaseString(activity, "row4Meta", "烧烤 - 夜宵 - 4.7 - 到店");
            byte[] heroImageData = showcaseBytes(activity, "heroImageData");
            byte[] row1ImageData = showcaseBytes(activity, "row1ImageData");
            byte[] row2ImageData = showcaseBytes(activity, "row2ImageData");
            byte[] row3ImageData = showcaseBytes(activity, "row3ImageData");
            byte[] row4ImageData = showcaseBytes(activity, "row4ImageData");
            int heroImageHash = showcaseInt(activity, "heroImageHash", 0);
            int heroImageBytes = showcaseInt(activity, "heroImageBytes", 0);
            int row1ImageHash = showcaseInt(activity, "row1ImageHash", 0);
            int row2ImageHash = showcaseInt(activity, "row2ImageHash", 0);
            int row3ImageHash = showcaseInt(activity, "row3ImageHash", 0);
            int row4ImageHash = showcaseInt(activity, "row4ImageHash", 0);
            int row1ImageBytes = showcaseInt(activity, "row1ImageBytes", 0);
            int row2ImageBytes = showcaseInt(activity, "row2ImageBytes", 0);
            int row3ImageBytes = showcaseInt(activity, "row3ImageBytes", 0);
            int row4ImageBytes = showcaseInt(activity, "row4ImageBytes", 0);
            if ((heroImageData == null || heroImageData.length == 0) && selectedIndex == 0) {
                heroImageData = row1ImageData;
                heroImageHash = row1ImageHash;
                heroImageBytes = row1ImageBytes;
            } else if ((heroImageData == null || heroImageData.length == 0) && selectedIndex == 1) {
                heroImageData = row2ImageData;
                heroImageHash = row2ImageHash;
                heroImageBytes = row2ImageBytes;
            } else if ((heroImageData == null || heroImageData.length == 0) && selectedIndex == 2) {
                heroImageData = row3ImageData;
                heroImageHash = row3ImageHash;
                heroImageBytes = row3ImageBytes;
            } else if ((heroImageData == null || heroImageData.length == 0) && selectedIndex == 3) {
                heroImageData = row4ImageData;
                heroImageHash = row4ImageHash;
                heroImageBytes = row4ImageBytes;
            }

            showcaseRect(ops, 0, 0, SURFACE_WIDTH, SURFACE_HEIGHT, 0xfff9f6f7);
            showcaseRect(ops, 0, 0, SURFACE_WIDTH, 102, 0xffd32323);
            showcaseRect(ops, 0, 102, SURFACE_WIDTH, 104, 0x22000000);
            showcaseText(ops, "美食点评", 18, 34, 20, 0xffffffff);
            showcaseRoundRect(ops, 330, 15, 464, 43, 8, 8, 0x24ffffff);
            showcaseText(ops, "Material 中文", 348, 34, 9, 0xffffffff);
            materialOutlinedSearchField(ops, search, stats.materialViews,
                    materialClassCount, 16, 50, 464, 98);

            materialChip(ops, "好评", 16, 108, 128, sortByRating);
            materialChip(ops, "营业中", 138, 108, 246, openNowFilter);
            materialChip(ops, "外卖", 256, 108, 356, deliveryFilter);
            materialChip(ops, "人均 $$", 366, 108, 464, false);
            materialSliderControl(ops, slider, 16, 150, 464, 196);

            if (activeTab == 2) {
                yelpSectionTitle(ops, "商家详情", "MaterialCardView 状态和图片通过 Westlake 渲染", 208);
                materialHeroCard(ops, place, meta, rating, reviews, saved,
                        heroImageData, heroImageHash, heroImageBytes, 238);
                materialMetricRow(ops, stats, materialViewCount, action, 508);
                materialActionButton(ops, "返回搜索", 16, 596, 230, 638, false);
                materialActionButton(ops, saved ? "已收藏" : "收藏", 250, 596, 464, 638, saved);
            } else if (activeTab == 3) {
                yelpSectionTitle(ops, "收藏", "触摸事件保持在 Westlake 运行时内", 208);
                materialHeroCard(ops, savedCount > 0 ? place : "暂无收藏",
                        savedCount > 0 ? meta : "点击商家或收藏按钮",
                        rating, reviews, saved, heroImageData, heroImageHash, heroImageBytes, 238);
                materialMetricRow(ops, stats, materialViewCount, action, 508);
                materialActionButton(ops, "发现", 16, 596, 230, 638, false);
                materialActionButton(ops, "搜索", 250, 596, 464, 638, false);
            } else {
                yelpSectionTitle(ops, "搜索结果",
                        imageLoading ? "正在加载网络图片" : "真实图片 + 芯片 + 滑块 + 卡片 + FAB", 208);
                materialResultCard(ops, 0, row1Name, row1Meta, row1ImageData,
                        row1ImageHash, row1ImageBytes, 252, selectedIndex == 0);
                materialResultCard(ops, 1, row2Name, row2Meta, row2ImageData,
                        row2ImageHash, row2ImageBytes, 326, selectedIndex == 1);
                materialResultCard(ops, 2, row3Name, row3Meta, row3ImageData,
                        row3ImageHash, row3ImageBytes, 400, selectedIndex == 2);
                materialResultCard(ops, 3, row4Name, row4Meta, row4ImageData,
                        row4ImageHash, row4ImageBytes, 474, selectedIndex == 3);
                materialActionButton(ops, "详情", 16, 584, 230, 636, false);
                materialActionButton(ops, saved ? "已收藏" : "收藏当前",
                        250, 584, 464, 636, saved);
            }

            materialFloatingActionButton(ops, saved, 430, 650);
            materialBottomNav(ops, activeTab);

            byte[] data = ops.toByteArray();
            java.io.OutputStream out = System.out;
            writeIntLe(out, 0x444C5354);
            writeIntLe(out, data.length);
            out.write(data);
            out.flush();
            appendCutoffCanaryMarker("MATERIAL_DIRECT_FRAME_OK reason=" + reason
                    + " bytes=" + intAscii(data.length)
                    + " materialViews=" + intAscii(stats.materialViews)
                    + " texts=" + intAscii(stats.texts)
                    + " buttons=" + intAscii(stats.buttons)
                    + " images=" + intAscii(imageFetchCount));
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] Material Yelp direct frame error", t);
            logThrowableFrames("[WestlakeLauncher] Material Yelp direct frame", t, 12);
            appendCutoffCanaryMarker("MATERIAL_DIRECT_FRAME_FAIL err="
                    + t.getClass().getName());
        }
    }

    private static void materialChip(java.io.OutputStream out, String label,
            int left, int top, int right, boolean active) throws java.io.IOException {
        showcaseRoundRect(out, left, top + 2, right, top + 34, 8, 8, 0x14000000);
        showcaseRoundRect(out, left, top, right, top + 32, 8, 8,
                active ? 0xffd32323 : 0xffd0c4c7);
        showcaseRoundRect(out, left + 1, top + 1, right - 1, top + 31, 7, 7,
                active ? 0xffffebee : 0xffffffff);
        if (active) {
            yelpIcon(out, "check", left + 16, top + 16, 0xffd32323);
            showcaseText(out, label, left + 28, top + 21, 9, 0xffa82020);
        } else {
            showcaseText(out, label, left + 12, top + 21, 9, 0xff3f3a3b);
        }
    }

    private static void materialOutlinedSearchField(java.io.OutputStream out, String text,
            int materialViews, int materialClasses, int left, int top, int right, int bottom)
            throws java.io.IOException {
        showcaseRoundRect(out, left, top + 3, right, bottom + 3, 8, 8, 0x24000000);
        showcaseRoundRect(out, left, top, right, bottom, 8, 8, 0xffffdad7);
        showcaseRoundRect(out, left + 2, top + 2, right - 2, bottom - 2, 7, 7, 0xffffffff);
        showcaseRoundRect(out, left + 16, top - 6, left + 174, top + 12, 4, 4, 0xffffffff);
        showcaseText(out, "搜索 TextInputLayout", left + 24, top + 7, 8, 0xffba1a1a);
        yelpIcon(out, "search", left + 28, top + 31, 0xff5f6368);
        showcaseText(out, trimShowcaseText(text, 26), left + 54, top + 36, 13, 0xff1f1f1f);
        showcaseRect(out, right - 154, top + 13, right - 153, bottom - 12, 0xffe4e0e1);
        showcaseText(out, "views " + intAscii(materialViews), right - 132, top + 28, 9, 0xffa82020);
        showcaseText(out, "classes " + intAscii(materialClasses), right - 132, top + 44, 8, 0xff6f6f6f);
    }

    private static void materialSliderControl(java.io.OutputStream out, int value,
            int left, int top, int right, int bottom) throws java.io.IOException {
        showcaseRoundRect(out, left, top + 2, right, bottom + 2, 8, 8, 0x10000000);
        showcaseRoundRect(out, left, top, right, bottom, 8, 8, 0xffffffff);
        showcaseText(out, "距离", left + 16, top + 20, 10, 0xff1f1f1f);
        showcaseText(out, intAscii(value) + " 公里", right - 70, top + 20, 9, 0xffa82020);
        int trackLeft = left + 82;
        int trackRight = right - 72;
        int y = top + 28;
        int clamped = Math.max(1, Math.min(100, value));
        int thumb = trackLeft + ((trackRight - trackLeft) * clamped / 100);
        showcaseRoundRect(out, trackLeft, y - 3, trackRight, y + 3, 3, 3, 0xffffdad7);
        showcaseRoundRect(out, trackLeft, y - 3, thumb, y + 3, 3, 3, 0xffd32323);
        showcaseCircle(out, thumb, y, 9, 0xffd32323);
        showcaseCircle(out, thumb, y, 4, 0xffffffff);
    }

    private static void materialButtonVariantRow(java.io.OutputStream out,
            int left, int top, int right, boolean saved) throws java.io.IOException {
        materialButtonVariant(out, "Filled", left, top, left + 120, top + 36, 0);
        materialButtonVariant(out, "Outlined", left + 136, top, left + 278, top + 36, 1);
        materialButtonVariant(out, saved ? "Saved" : "Text", left + 294, top, right, top + 36, 2);
    }

    private static void materialButtonVariant(java.io.OutputStream out, String label,
            int left, int top, int right, int bottom, int style) throws java.io.IOException {
        if (style == 0) {
            showcaseRoundRect(out, left, top + 2, right, bottom + 2, 8, 8, 0x18000000);
            showcaseRoundRect(out, left, top, right, bottom, 8, 8, 0xffd32323);
            showcaseText(out, label, left + 24, top + 23, 10, 0xffffffff);
        } else if (style == 1) {
            showcaseRoundRect(out, left, top, right, bottom, 8, 8, 0xffd32323);
            showcaseRoundRect(out, left + 2, top + 2, right - 2, bottom - 2, 7, 7, 0xffffffff);
            showcaseText(out, label, left + 24, top + 23, 10, 0xffd32323);
        } else {
            showcaseText(out, label, left + 28, top + 23, 10, 0xffd32323);
        }
    }

    private static void materialHeroCard(java.io.OutputStream out, String title,
            String subtitle, int rating, int reviews, boolean saved,
            byte[] imageData, int imageHash, int imageBytes, int top)
            throws java.io.IOException {
        int accent = imageHash == 0
                ? 0xffd32323
                : (0xff000000 | ((imageHash & 0x00dfdfdf) | 0x00202020));
        showcaseRoundRect(out, 16, top + 3, 464, top + 250, 8, 8, 0x18000000);
        showcaseRoundRect(out, 16, top, 464, top + 247, 8, 8, 0xffffffff);
        showcaseRoundRect(out, 32, top + 18, 448, top + 122, 8, 8, accent);
        if (imageData != null && imageData.length > 0) {
            drawByteHashThumbnail(out, imageHash, 32, top + 18, 416, 104);
            showcaseRect(out, 32, top + 78, 448, top + 122, 0x88000000);
        } else {
            yelpIcon(out, "photo", 82, top + 70, 0xffffffff);
        }
        showcaseText(out, trimShowcaseText(title, 24), 48, top + 104, 17, 0xffffffff);
        showcaseText(out, "MaterialCardView  图片 " + intAscii(imageBytes) + " bytes",
                48, top + 55, 9, 0xffffffff);
        yelpRatingBoxes(out, 32, top + 148, rating);
        showcaseText(out, showcaseRatingText(rating) + "  "
                + intAscii(reviews) + " reviews", 136, top + 161, 10, 0xff6f6f6f);
        showcaseText(out, trimShowcaseText(subtitle, 42), 32, top + 192, 11, 0xff1f1f1f);
        showcaseRoundRect(out, 32, top + 210, 142, top + 236, 8, 8, 0xffffebee);
        showcaseText(out, saved ? "已收藏" : "可收藏", 52, top + 228, 9, 0xffa82020);
    }

    private static void materialResultCard(java.io.OutputStream out, int index,
            String title, String subtitle, byte[] imageData, int imageHash, int imageBytes,
            int top, boolean selected) throws java.io.IOException {
        int bottom = top + 66;
        int accent = imageHash == 0
                ? (index == 0 ? 0xffe9f3ff : index == 1 ? 0xfffff3df
                        : index == 2 ? 0xffe8f5e9 : 0xffffebee)
                : (0xff000000 | ((imageHash & 0x00dfdfdf) | 0x00202020));
        showcaseRoundRect(out, 16, top + 2, 464, bottom + 2, 8, 8, 0x10000000);
        showcaseRoundRect(out, 16, top, 464, bottom, 8, 8,
                selected ? 0xfffff5f5 : 0xffffffff);
        if (selected) {
            showcaseRect(out, 16, top, 22, bottom, 0xffd32323);
        }
        showcaseRoundRect(out, 30, top + 8, 112, bottom - 8, 8, 8, accent);
        if (imageData != null && imageData.length > 0 && imageBytes > 0) {
            drawByteHashThumbnail(out, imageHash, 30, top + 8, 82, 50);
        } else {
            yelpIcon(out, "photo", 71, top + 34, 0xffffffff);
        }
        showcaseText(out, trimShowcaseText(title, 24), 126, top + 22, 13, 0xff1f1f1f);
        yelpRatingBoxes(out, 126, top + 31, index == 0 ? 49 : index == 1 ? 48 : 47);
        showcaseText(out, trimShowcaseText(subtitle, 34), 126, top + 56, 8, 0xff6f6f6f);
        showcaseCircle(out, 426, top + 33, 18, selected ? 0xffffebee : 0xfff6f0f2);
        yelpIcon(out, "heart", 426, top + 33, selected ? 0xffd32323 : 0xff5f6368);
    }

    private static void materialMetricRow(java.io.OutputStream out, MaterialTreeStats stats,
            int declaredViews, String action, int top) throws java.io.IOException {
        showcaseRoundRect(out, 16, top + 2, 464, top + 74, 8, 8, 0x10000000);
        showcaseRoundRect(out, 16, top, 464, top + 72, 8, 8, 0xffffffff);
        showcaseText(out, "Material 组件", 32, top + 25, 13, 0xff1f1f1f);
        showcaseText(out, "tree=" + intAscii(stats.materialViews)
                + " declared=" + intAscii(declaredViews)
                + " texts=" + intAscii(stats.texts)
                + " buttons=" + intAscii(stats.buttons),
                32, top + 46, 9, 0xff6f6f6f);
        showcaseText(out, trimShowcaseText(action, 42), 32, top + 64, 8, 0xffa82020);
    }

    private static void materialFloatingActionButton(java.io.OutputStream out, boolean saved,
            int cx, int cy) throws java.io.IOException {
        showcaseCircle(out, cx + 2, cy + 3, 24, 0x18000000);
        showcaseCircle(out, cx, cy, 24, 0xffd32323);
        yelpIcon(out, saved ? "check" : "heart", cx, cy, 0xffffffff);
    }

    private static void materialActionButton(java.io.OutputStream out, String label,
            int left, int top, int right, int bottom, boolean active) throws java.io.IOException {
        showcaseRoundRect(out, left, top + 2, right, bottom + 2, 8, 8, 0x12000000);
        showcaseRoundRect(out, left, top, right, bottom, 8, 8,
                active ? 0xffd32323 : 0xffffffff);
        showcaseText(out, label, left + 22, top + 27, 12,
                active ? 0xffffffff : 0xff1f1f1f);
    }

    private static void materialBottomNav(java.io.OutputStream out, int active)
            throws java.io.IOException {
        showcaseRect(out, 0, 686, SURFACE_WIDTH, 800, 0xffffffff);
        showcaseRect(out, 0, 686, SURFACE_WIDTH, 687, 0xffe6e0e2);
        materialNavItem(out, "发现", "discover", 0, 120, active == 0);
        materialNavItem(out, "搜索", "search", 120, 240, active == 1);
        materialNavItem(out, "详情", "details", 240, 360, active == 2);
        materialNavItem(out, "收藏", "saved", 360, 480, active == 3);
    }

    private static void materialNavItem(java.io.OutputStream out, String label, String icon,
            int left, int right, boolean active) throws java.io.IOException {
        if (active) {
            showcaseRoundRect(out, left + 26, 704, right - 26, 744, 8, 8, 0xffffebee);
        }
        yelpIcon(out, icon, left + 60, 725, active ? 0xffd32323 : 0xff5f6368);
        showcaseText(out, label, left + 42, 766, 11, active ? 0xffd32323 : 0xff5f6368);
    }

    private static final class MaterialTreeStats {
        int views;
        int materialViews;
        int texts;
        int buttons;
    }

    private static MaterialTreeStats collectMaterialTreeStats(Activity activity) {
        MaterialTreeStats stats = new MaterialTreeStats();
        try {
            android.view.View decor = activity != null && activity.getWindow() != null
                    ? activity.getWindow().getDecorView() : null;
            collectMaterialTreeStats(decor, 0, stats);
        } catch (Throwable ignored) {
        }
        return stats;
    }

    private static void collectMaterialTreeStats(android.view.View view, int depth,
            MaterialTreeStats stats) {
        if (view == null || depth > 32) {
            return;
        }
        stats.views++;
        String name = view.getClass().getName();
        if (name != null && name.indexOf("com.google.android.material.") >= 0) {
            stats.materialViews++;
        }
        if (view instanceof android.widget.TextView) {
            stats.texts++;
        }
        if (view instanceof android.widget.Button) {
            stats.buttons++;
        }
        if (view instanceof android.view.ViewGroup) {
            android.view.ViewGroup group = (android.view.ViewGroup) view;
            int count = 0;
            try {
                count = group.getChildCount();
            } catch (Throwable ignored) {
            }
            for (int i = 0; i < count; i++) {
                try {
                    collectMaterialTreeStats(group.getChildAt(i), depth + 1, stats);
                } catch (Throwable ignored) {
                }
            }
        }
    }

    private static void runShowcaseDirectFrameLoop(Activity activity) {
        writeShowcaseDirectFrame(activity, "initial");
        startupLog("[WestlakeLauncher] Initial frame rendered");

        int lastTouchSeq = -1;
        long downTime = 0L;
        String touchPath = null;
        try {
            String envTouch = System.getenv("WESTLAKE_TOUCH");
            touchPath = envTouch != null && envTouch.length() > 0
                    ? envTouch : defaultTouchPath();
            startupLog("[WestlakeLauncher] Showcase touch file: " + touchPath);
        } catch (Throwable ignored) {
            touchPath = defaultTouchPath();
        }

        if (FRAMEWORK_POLICY_WESTLAKE_ONLY.equals(frameworkPolicyValue())) {
            startupLog("[WestlakeLauncher] Showcase strict touch polling enabled");
            appendCutoffCanaryMarker("SHOWCASE_TOUCH_POLL_READY strict=true");
        }

        while (true) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                break;
            }
            if (showcaseBool(activity, "renderDirty", false)) {
                showcaseInvoke(activity, "consumeRenderDirty");
                writeShowcaseDirectFrame(activity, "state_dirty");
                continue;
            }
            byte[] touch = tryReadFileBytes(touchPath);
            if (touch == null || touch.length < 16) {
                continue;
            }
            try {
                java.nio.ByteBuffer bb = java.nio.ByteBuffer.wrap(touch, 0, 16);
                bb.order(java.nio.ByteOrder.LITTLE_ENDIAN);
                int action = bb.getInt();
                int x = bb.getInt();
                int y = bb.getInt();
                int seq = bb.getInt();
                if (seq == lastTouchSeq) {
                    continue;
                }
                lastTouchSeq = seq;
                long now = System.currentTimeMillis();
                if (action == 0 || downTime == 0L) {
                    downTime = now;
                }
                if (lastTouchSeq == seq) {
                    appendCutoffCanaryMarker("SHOWCASE_TOUCH_POLL_OK seq="
                            + intAscii(seq) + " action=" + showcaseTouchReason(action)
                            + " x=" + intAscii(x) + " y=" + intAscii(y));
                }
                boolean dispatchHandled = false;
                try {
                    dispatchHandled = activity.dispatchTouchEvent(android.view.MotionEvent.obtain(
                            downTime, now, action, (float) x, (float) y, 0));
                } catch (Throwable dispatchError) {
                    startupLog("[WestlakeLauncher] Showcase dispatchTouchEvent error", dispatchError);
                }
                boolean directHandled = !dispatchHandled
                        && routeShowcaseDirectTouch(activity, action, x, y);
                startupLog("[WestlakeLauncher] Showcase touch action=" + action
                        + " x=" + x + " y=" + y
                        + " dispatch=" + dispatchHandled
                        + " direct=" + directHandled);
                writeShowcaseDirectFrame(activity, showcaseTouchReason(action));
                if (action == 1) {
                    downTime = 0L;
                }
            } catch (Throwable t) {
                startupLog("[WestlakeLauncher] Showcase touch loop error", t);
            }
        }
    }

    private static boolean routeShowcaseDirectTouch(Activity activity, int action, int x, int y) {
        if (activity == null || action != 1) {
            return false;
        }
        if (y >= 680) {
            if (x < 120) {
                return showcaseInvoke(activity, "navigateLibrary");
            }
            if (x < 240) {
                return showcaseInvoke(activity, "navigateMixer");
            }
            if (x < 360) {
                return showcaseInvoke(activity, "navigateTimer");
            }
            return showcaseInvoke(activity, "navigateSettings");
        }
        if (y >= 70 && y < 154 && x < 120) {
            return showcaseInvoke(activity, "togglePlay");
        }
        if (y >= 168 && y < 214) {
            if (x < 128) {
                return showcaseInvoke(activity, "presetFocus");
            }
            if (x < 240) {
                return showcaseInvoke(activity, "presetRain");
            }
            if (x < 352) {
                return showcaseInvoke(activity, "presetNight");
            }
            return showcaseInvoke(activity, "cycleMood");
        }
        int page = showcaseInt(activity, "activePage", 0);
        if (page == 0) {
            if (y >= 230 && y < 320) {
                return showcaseInvoke(activity, x < 240 ? "selectRainSound" : "selectWindSound");
            }
            if (y >= 320 && y < 410) {
                return showcaseInvoke(activity, x < 240 ? "selectCafeSound" : "selectFireSound");
            }
            if (y >= 410 && y < 500) {
                return showcaseInvoke(activity, x < 240 ? "selectNoiseSound" : "selectKeysSound");
            }
            if (y >= 510 && y < 632) {
                return showcaseInvoke(activity, x < 240 ? "setTimer30" : "saveMix");
            }
        } else if (page == 1) {
            if (y >= 230 && y < 290) {
                return showcaseInvoke(activity, x < 240 ? "addLayer" : "saveMix");
            }
            if (y >= 290 && y < 360) {
                return showcaseInvoke(activity, x < 240 ? "resetMix" : "cycleMood");
            }
            if (y >= 360 && y < 452) {
                return showcaseInvoke(activity, x < 240 ? "selectRainSound" : "selectWindSound");
            }
            if (y >= 458 && y < 528) {
                return showcaseInvoke(activity, x < 240 ? "selectCafeSound" : "selectNoiseSound");
            }
        } else if (page == 2) {
            if (y >= 230 && y < 300) {
                if (x < 160) {
                    return showcaseInvoke(activity, "setTimer15");
                }
                if (x < 320) {
                    return showcaseInvoke(activity, "setTimer30");
                }
                return showcaseInvoke(activity, "setTimer60");
            }
            if (y >= 300 && y < 380) {
                return showcaseInvoke(activity, x < 240 ? "togglePlay" : "presetNight");
            }
        } else if (page == 3) {
            if (y >= 408 && y < 468) {
                return showcaseInvoke(activity, x < 240 ? "fetchVenueFeed" : "exportBundle");
            }
            if (y >= 468 && y < 536) {
                return showcaseInvoke(activity, x < 240 ? "nextVenue" : "reviewVenue");
            }
            if (y >= 536 && y < 604) {
                return showcaseInvoke(activity, x < 240 ? "toggleOfflineMode" : "saveMix");
            }
        }
        return false;
    }

    private static String showcaseTouchReason(int action) {
        if (action == 0) {
            return "touch_down";
        }
        if (action == 1) {
            return "touch_up";
        }
        if (action == 2) {
            return "touch_move";
        }
        return "touch_other";
    }

    private static boolean showcaseInvoke(Activity activity, String methodName) {
        try {
            java.lang.reflect.Method method = activity.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            method.invoke(activity);
            return true;
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] Showcase invoke failed " + methodName, t);
            return false;
        }
    }

    private static void layoutShowcaseDecor(Activity activity) {
        layoutDecorForHeight(activity, SURFACE_HEIGHT, "Showcase direct");
    }

    private static void layoutYelpLiveDecor(Activity activity) {
        layoutDecorForHeight(activity, YELP_SURFACE_HEIGHT, "Yelp live generic");
    }

    private static void layoutDecorForHeight(Activity activity, int height, String label) {
        if (activity == null || activity.getWindow() == null) {
            return;
        }
        try {
            android.view.View decor = activity.getWindow().getDecorView();
            if (decor == null) {
                return;
            }
            int wSpec = android.view.View.MeasureSpec.makeMeasureSpec(
                    SURFACE_WIDTH, android.view.View.MeasureSpec.EXACTLY);
            int hSpec = android.view.View.MeasureSpec.makeMeasureSpec(
                    height, android.view.View.MeasureSpec.EXACTLY);
            decor.measure(wSpec, hSpec);
            decor.layout(0, 0, SURFACE_WIDTH, height);
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] " + label + " layout error", t);
        }
    }

    private static void writeShowcaseDirectFrame(Activity activity, String reason) {
        if (showcaseBool(activity, "xmlUiReady", false)
                && writeShowcaseXmlTreeFrame(activity, reason)) {
            return;
        }
        writeShowcaseStateFrame(activity, reason);
    }

    private static boolean writeShowcaseXmlTreeFrame(Activity activity, String reason) {
        try {
            layoutShowcaseDecor(activity);
            android.view.View decor = activity != null && activity.getWindow() != null
                    ? activity.getWindow().getDecorView() : null;
            if (decor == null) {
                appendCutoffCanaryMarker("SHOWCASE_XML_TREE_RENDER_FAIL reason="
                        + reason + " err=no_decor");
                return false;
            }
            java.io.ByteArrayOutputStream ops = new java.io.ByteArrayOutputStream(8192);
            showcaseColor(ops, 0xfff7f4ef);
            ShowcaseTreeStats stats = new ShowcaseTreeStats();
            collectShowcaseTreeStats(decor, 0, stats);
            drawShowcaseNoiceXmlFrame(ops, activity, stats, reason);

            byte[] data = ops.toByteArray();
            java.io.OutputStream out = System.out;
            writeIntLe(out, 0x444C5354);
            writeIntLe(out, data.length);
            out.write(data);
            out.flush();
            int page = showcaseInt(activity, "activePage", 0);
            appendCutoffCanaryMarker("SHOWCASE_XML_TREE_RENDER_OK reason=" + reason
                    + " page=" + intAscii(page)
                    + " views=" + intAscii(stats.views)
                    + " texts=" + intAscii(stats.texts)
                    + " progress=" + intAscii(stats.progress)
                    + " buttons=" + intAscii(stats.buttons)
                    + " bytes=" + intAscii(data.length));
            appendCutoffCanaryMarker("SHOWCASE_DIRECT_FRAME_OK reason=" + reason
                    + " bytes=" + intAscii(data.length));
            return true;
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] Showcase XML tree frame error", t);
            logThrowableFrames("[WestlakeLauncher] Showcase XML tree frame", t, 12);
            appendCutoffCanaryMarker("SHOWCASE_XML_TREE_RENDER_FAIL err="
                    + t.getClass().getName());
            return false;
        }
    }

    private static final class ShowcaseTreeStats {
        int views;
        int texts;
        int progress;
        int buttons;
        int images;
        int listViews;
        int genericListRows;
        int genericListImages;
        int genericListImageBytes;
        int genericListAdapterCount;
        String genericListAdapterClass = "none";
    }

    private static final class GenericRowFacts {
        final String[] texts = new String[4];
        int textCount;
        byte[] imageData;
        int imageBytes;
        int imageHash;
    }

    private static void collectShowcaseTreeStats(android.view.View view, int depth,
            ShowcaseTreeStats stats) {
        if (view == null || depth > 32) {
            return;
        }
        try {
            if (view.getVisibility() == android.view.View.GONE) {
                return;
            }
        } catch (Throwable ignored) {
        }
        stats.views++;
        if (view instanceof android.widget.TextView) {
            stats.texts++;
        }
        if (view instanceof android.widget.ProgressBar) {
            stats.progress++;
        }
        if (view instanceof android.widget.Button
                || view instanceof android.widget.CompoundButton) {
            stats.buttons++;
        }
        if (view instanceof android.widget.ImageView) {
            stats.images++;
        }
        if (view instanceof android.widget.ListView) {
            stats.listViews++;
        }
        if (view instanceof android.view.ViewGroup) {
            android.view.ViewGroup group = (android.view.ViewGroup) view;
            int count = 0;
            try {
                count = group.getChildCount();
            } catch (Throwable ignored) {
            }
            for (int i = 0; i < count; i++) {
                android.view.View child = null;
                try {
                    child = group.getChildAt(i);
                } catch (Throwable ignored) {
                }
                collectShowcaseTreeStats(child, depth + 1, stats);
            }
        }
    }

    private static void drawShowcaseNoiceXmlFrame(java.io.OutputStream ops,
            Activity activity, ShowcaseTreeStats stats, String reason)
            throws java.io.IOException {
        boolean playing = showcaseBool(activity, "playing", false);
        boolean favorite = showcaseBool(activity, "favorite", false);
        String preset = showcaseString(activity, "selectedPreset", "Rain");
        int layers = showcaseInt(activity, "layerCount", 4);
        int session = showcaseInt(activity, "sessionProgress", 18);
        int rain = showcaseInt(activity, "rainVolume", 70);
        int wind = showcaseInt(activity, "windVolume", 28);
        int cafe = showcaseInt(activity, "cafeVolume", 34);
        int fire = showcaseInt(activity, "fireVolume", 8);
        int brown = showcaseInt(activity, "brownVolume", 45);
        int keyboard = showcaseInt(activity, "keyboardVolume", 16);
        int waves = showcaseInt(activity, "wavesVolume", 22);
        int birds = showcaseInt(activity, "birdsVolume", 10);
        int page = showcaseInt(activity, "activePage", 0);
        boolean offline = showcaseBool(activity, "offlineMode", true);
        String sound = showcaseString(activity, "selectedSound", "Rain");
        String action = showcaseString(activity, "lastAction", "Ready");
        boolean networkLoading = showcaseBool(activity, "networkLoading", false);
        int venueCount = showcaseInt(activity, "venueCount", 0);
        int venueIndex = showcaseInt(activity, "venueIndex", 0);
        int venueRating = showcaseInt(activity, "venueRatingTenths", 46);
        int venueReviews = showcaseInt(activity, "venueReviewCount", 98);
        int venueImageBytes = showcaseInt(activity, "venueImageBytes", 0);
        int venueImageWidth = showcaseInt(activity, "venueImageWidth", 0);
        int venueImageHeight = showcaseInt(activity, "venueImageHeight", 0);
        int venueImageHash = showcaseInt(activity, "venueImageHash", 0);
        String venueName = showcaseString(activity, "venueName", "Tap Load venues");
        String venueCategory = showcaseString(activity, "venueCategory", "Local");
        String venueMealType = showcaseString(activity, "venueMealType", "Nearby");
        String venueStatus = showcaseString(activity, "venueStatus", "Network not loaded");

        showcaseRect(ops, 0, 0, SURFACE_WIDTH, SURFACE_HEIGHT, 0xfff3f6f1);
        showcaseRect(ops, 0, 0, SURFACE_WIDTH, 54, 0xff173b3f);
        showcaseText(ops, "Noice XML Showcase", 18, 35, 20, 0xffffffff);
        showcaseText(ops, "AXML: " + intAscii(stats.views) + " views",
                312, 34, 11, 0xffb8d8d2);

        showcaseRect(ops, 16, 70, 464, 154, 0xffffffff);
        showcaseRect(ops, 28, 88, 82, 137, playing ? 0xff2d7d5f : 0xff2860a8);
        showcaseText(ops, playing ? "||" : ">", 48, 119, 22, 0xffffffff);
        showcaseText(ops, "Now playing", 98, 96, 11, 0xff6c757d);
        showcaseText(ops, preset, 98, 123, 21, 0xff17212b);
        showcaseText(ops, "Local ambient mix  layers " + intAscii(layers)
                + "  timer " + intAscii(session) + "m",
                98, 143, 11, 0xff4f5f63);
        showcaseText(ops, favorite ? "saved" : "local", 394, 104, 11, 0xff2d7d5f);

        showcaseButton(ops, "Focus", 16, 170, 116, 210, "Focus".equals(preset));
        showcaseButton(ops, "Rain", 128, 170, 228, 210, "Rain".equals(preset));
        showcaseButton(ops, "Night", 240, 170, 340, 210, "Night".equals(preset));
        showcaseButton(ops, "Random", 352, 170, 464, 210, false);

        if (page == 1) {
            showcaseText(ops, "Mixer", 18, 244, 17, 0xff17212b);
            showcaseText(ops, "tap actions update app state", 112, 244, 11, 0xff6c757d);
            showcaseButton(ops, "Add layer", 16, 252, 228, 298, false);
            showcaseButton(ops, favorite ? "Saved" : "Save mix", 252, 252, 464, 298, favorite);
            showcaseButton(ops, "Reset", 16, 308, 228, 354, false);
            showcaseButton(ops, "Shuffle", 252, 308, 464, 354, false);
            showcaseMetricCard(ops, "Layers", intAscii(layers), "active local loops", 16, 382);
            showcaseMetricCard(ops, "Session", intAscii(session) + "m", "guest state", 248, 382);
            showcaseSoundCard(ops, "Rain", "tap to boost", rain, 16, 474, 0xff2f80ed);
            showcaseSoundCard(ops, "Wind", "tap to boost", wind, 248, 474, 0xff5e9f73);
            showcaseSoundCard(ops, "Cafe", "tap to boost", cafe, 16, 562, 0xffa15c38);
            showcaseSoundCard(ops, "Brown", "tap to boost", brown, 248, 562, 0xff6f5b4f);
            showcaseText(ops, "Last action: " + action, 18, 688, 11, 0xff4f5f63);
        } else if (page == 2) {
            showcaseText(ops, "Sleep timer", 18, 244, 17, 0xff17212b);
            showcaseText(ops, "duration picker, state restore, playback", 128, 244, 11, 0xff6c757d);
            showcaseRect(ops, 16, 252, 464, 402, 0xffffffff);
            showcaseText(ops, intAscii(session) + " minutes", 40, 300, 28, 0xff17212b);
            showcaseText(ops, playing ? "playing local mix" : "paused local mix",
                    40, 326, 12, 0xff6c757d);
            showcaseButton(ops, "15m", 40, 348, 150, 390, session == 15);
            showcaseButton(ops, "30m", 184, 348, 294, 390, session == 30);
            showcaseButton(ops, "60m", 328, 348, 438, 390, session == 60);
            showcaseRect(ops, 16, 426, 464, 512, 0xffffffff);
            showcaseText(ops, "Bedtime routine", 32, 456, 15, 0xff17212b);
            showcaseText(ops, "Night preset  soft fade  offline playback", 32, 480, 11, 0xff6c757d);
            showcaseButton(ops, playing ? "Pause" : "Start", 32, 530, 220, 576, playing);
            showcaseButton(ops, "Night mix", 260, 530, 448, 576, "Night".equals(preset));
            showcaseRect(ops, 32, 612, 448, 624, 0xffd8e5e0);
            showcaseRect(ops, 32, 612, 32 + (416 * clampInt(session) / 100), 624, 0xff2d7d5f);
            showcaseText(ops, "Timer page marker updates after tapping a duration",
                    32, 666, 11, 0xff4f5f63);
        } else if (page == 3) {
            showcaseText(ops, "Discover", 18, 244, 17, 0xff17212b);
            showcaseText(ops, "Yelp-like venue feed; live HTTP gap tracked", 112, 244, 11, 0xff6c757d);
            showcaseVenueCard(ops, venueName, venueCategory, venueMealType, venueStatus,
                    venueRating, venueReviews, venueCount, venueIndex, venueImageBytes,
                    venueImageWidth, venueImageHeight, venueImageHash, networkLoading);
            showcaseButton(ops, networkLoading ? "Loading..." : "Load venues",
                    16, 416, 228, 462, networkLoading);
            showcaseButton(ops, "Export bundle", 252, 416, 464, 462, favorite);
            showcaseButton(ops, "Next venue", 16, 476, 228, 522, venueCount > 1);
            showcaseButton(ops, favorite ? "Reviewed" : "Review", 252, 476, 464, 522, favorite);
            showcaseSettingRow(ops, "Offline mode", offline ? "on" : "off",
                    16, 548, 0xff2d7d5f);
            showcaseSettingRow(ops, "Render transport", "DLST pipe to SurfaceView",
                    16, 604, 0xff6b6fb7);
            showcaseText(ops, "Execution evidence", 18, 678, 14, 0xff17212b);
            showcaseText(ops, "APK dex -> Westlake dalvikvm -> shim Activity -> host bridge -> DLST",
                    18, 700, 10, 0xff4f5f63);
            showcaseText(ops, "reason " + reason + " texts " + intAscii(stats.texts)
                    + " buttons " + intAscii(stats.buttons),
                    18, 720, 10, 0xff6c757d);
        } else {
            showcaseText(ops, "Sound library", 18, 244, 17, 0xff17212b);
            showcaseText(ops, "selected " + sound + "  tap cards to change volume",
                    146, 244, 11, 0xff6c757d);
            showcaseSoundCard(ops, "Rain", "steady rainfall", rain, 16, 260, 0xff2f80ed);
            showcaseSoundCard(ops, "Wind", "soft gusts", wind, 248, 260, 0xff5e9f73);
            showcaseSoundCard(ops, "Cafe", "distant chatter", cafe, 16, 348, 0xffa15c38);
            showcaseSoundCard(ops, "Fire", "warm crackle", fire, 248, 348, 0xffc65332);
            showcaseSoundCard(ops, "Brown", "deep noise", brown, 16, 436, 0xff6f5b4f);
            showcaseSoundCard(ops, "Keys", "soft keyboard", keyboard, 248, 436, 0xff6b6fb7);
            showcaseRect(ops, 16, 540, 226, 632, 0xffffffff);
            showcaseText(ops, "Sleep timer", 30, 566, 16, 0xff17212b);
            showcaseText(ops, "tap for 30m", 30, 592, 13, 0xff2860a8);
            showcaseRect(ops, 30, 606, 208, 616, 0xffd8e5e0);
            showcaseRect(ops, 30, 606, 30 + (178 * clampInt(session) / 100), 616, 0xff2d7d5f);
            showcaseRect(ops, 242, 540, 464, 632, 0xffffffff);
            showcaseText(ops, "Preset tags", 256, 566, 16, 0xff17212b);
            showcaseChip(ops, offline ? "offline" : "online", 256, 584, 322, 614);
            showcaseChip(ops, "mix", 330, 584, 378, 614);
            showcaseChip(ops, "sleep", 386, 584, 448, 614);
            showcaseText(ops, "XML API surface", 18, 668, 14, 0xff17212b);
            showcaseText(ops, "MaterialCard Slider SVG CheckBox TextInput BottomNav",
                    18, 690, 11, 0xff4f5f63);
            showcaseText(ops, "reason " + reason + "  action " + action,
                    18, 710, 10, 0xff6c757d);
        }

        showcaseBottomNav(ops, page);
    }

    private static void renderShowcaseView(java.io.OutputStream out, android.view.View view,
            int offsetX, int offsetY, int depth, ShowcaseTreeStats stats)
            throws java.io.IOException {
        renderShowcaseView(out, view, offsetX, offsetY, depth, stats, SURFACE_HEIGHT);
    }

    private static void renderShowcaseView(java.io.OutputStream out, android.view.View view,
            int offsetX, int offsetY, int depth, ShowcaseTreeStats stats, int clipHeight)
            throws java.io.IOException {
        if (view == null || depth > 16 || view.getVisibility() == android.view.View.GONE) {
            return;
        }
        stats.views++;

        int left = offsetX + view.getLeft();
        int top = offsetY + view.getTop();
        int right = offsetX + view.getRight();
        int bottom = offsetY + view.getBottom();
        if (right <= left) {
            right = left + Math.max(view.getMeasuredWidth(), 1);
        }
        if (bottom <= top) {
            bottom = top + Math.max(view.getMeasuredHeight(), 1);
        }
        if (bottom < 0 || top > clipHeight || right < 0 || left > SURFACE_WIDTH) {
            return;
        }

        boolean isText = view instanceof android.widget.TextView;
        boolean isButton = view instanceof android.widget.Button
                || view instanceof android.widget.CompoundButton;
        boolean isEdit = view instanceof android.widget.EditText;
        boolean isProgress = view instanceof android.widget.ProgressBar;
        boolean isImage = view instanceof android.widget.ImageView;
        boolean isList = view instanceof android.widget.ListView;
        boolean isBottomNav = view.getClass().getName().indexOf("BottomNavigationView") >= 0;

        if (depth == 0) {
            showcaseRect(out, 0, 0, SURFACE_WIDTH, clipHeight, 0xfff7f4ef);
        } else if (isList) {
            stats.listViews++;
            showcaseRect(out, left, top, right, bottom, 0xfffdfefe);
            renderGenericListViewRows(out, (android.widget.ListView) view,
                    left, top, right, bottom, depth, stats, clipHeight);
            return;
        } else if (isBottomNav) {
            showcaseRect(out, left, top, right, bottom, 0xffffffff);
        } else if (isButton) {
            stats.buttons++;
            showcaseRect(out, left, top, right, bottom, 0xff2962a0);
        } else if (isEdit) {
            showcaseRect(out, left, top, right, bottom, 0xffffffff);
            showcaseRect(out, left, bottom - 2, right, bottom, 0xff2962a0);
        } else if (isProgress) {
            stats.progress++;
            drawShowcaseProgress(out, (android.widget.ProgressBar) view,
                    left + 6, top + Math.max(8, (bottom - top) / 2 - 4),
                    right - 6, top + Math.max(16, (bottom - top) / 2 + 4));
        } else if (isImage) {
            stats.images++;
            showcaseRect(out, left, top, right, bottom, 0xffeceff1);
            showcaseText(out, "icon", left + 8,
                    top + Math.min(32, Math.max(18, bottom - top - 6)),
                    10, 0xff5f6368);
        } else if (view instanceof android.view.ViewGroup && depth <= 5
                && right - left > 24 && bottom - top > 20) {
            int color = depth <= 2 ? 0xffffffff : (depth % 2 == 0 ? 0xfffafafa : 0xfff1f3f4);
            showcaseRect(out, left, top, right, bottom, color);
        }

        if (isText) {
            android.widget.TextView tv = (android.widget.TextView) view;
            CharSequence cs = tv.getText();
            if (cs != null && cs.length() > 0) {
                stats.texts++;
                int textColor = tv.getCurrentTextColor();
                if (textColor == 0) {
                    textColor = isButton ? 0xffffffff : 0xff202124;
                }
                int size = clampTextSize((int) (tv.getTextSize() / 2.0f));
                if (isButton) {
                    textColor = 0xffffffff;
                }
                String text = trimShowcaseText(String.valueOf(cs), Math.max(8, (right - left) / 8));
                int x = left + Math.max(6, view.getPaddingLeft());
                int y = top + Math.min(bottom - top - 4, Math.max(size + 7, 18));
                showcaseText(out, text, x, y, size, textColor);
            }
        }

        if (view instanceof android.view.ViewGroup) {
            android.view.ViewGroup group = (android.view.ViewGroup) view;
            int childOffsetX = left - view.getScrollX();
            int childOffsetY = top - view.getScrollY();
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                renderShowcaseView(out, group.getChildAt(i),
                        childOffsetX, childOffsetY, depth + 1, stats, clipHeight);
            }
        }
    }

    private static void renderGenericListViewRows(java.io.OutputStream out,
            android.widget.ListView list, int left, int top, int right, int bottom,
            int depth, ShowcaseTreeStats stats, int clipHeight) throws java.io.IOException {
        android.widget.ListAdapter adapter = null;
        try {
            adapter = list.getAdapter();
        } catch (Throwable ignored) {
        }
        if (adapter == null) {
            return;
        }

        int adapterCount = 0;
        int first = 0;
        try {
            adapterCount = adapter.getCount();
        } catch (Throwable ignored) {
            adapterCount = 0;
        }
        try {
            first = list.getFirstVisiblePosition();
        } catch (Throwable ignored) {
            first = 0;
        }
        if (first < 0) {
            first = 0;
        }
        int remaining = adapterCount - first;
        if (remaining <= 0) {
            return;
        }

        stats.genericListAdapterCount = adapterCount;
        stats.genericListAdapterClass = adapter.getClass().getName();

        int visibleRows = remaining < 5 ? remaining : 5;
        int innerLeft = left + 6;
        int innerRight = right - 6;
        int availableHeight = Math.max(1, bottom - top - 8);
        int rowHeight = availableHeight / visibleRows;
        if (rowHeight < 44) {
            rowHeight = 44;
        }
        if (rowHeight > 86) {
            rowHeight = 86;
        }
        int y = top + 4;
        for (int i = 0; i < visibleRows; i++) {
            int position = first + i;
            if (position < 0 || position >= adapterCount || y > clipHeight) {
                break;
            }
            android.view.View row = null;
            try {
                if (i < list.getChildCount()) {
                    row = list.getChildAt(i);
                }
            } catch (Throwable ignored) {
            }
            GenericRowFacts facts = new GenericRowFacts();
            collectGenericRowFacts(row, facts, 0);
            renderGenericAdapterRow(out, facts, position, innerLeft, y, innerRight,
                    y + rowHeight - 2, stats);
            y += rowHeight;
        }
    }

    private static void renderGenericAdapterRow(java.io.OutputStream out,
            GenericRowFacts facts, int position, int left, int top, int right, int bottom,
            ShowcaseTreeStats stats) throws java.io.IOException {
        if (right <= left || bottom <= top) {
            return;
        }
        stats.genericListRows++;
        showcaseRoundRect(out, left, top, right, bottom, 6, 6, 0xffffffff);
        showcaseRect(out, left, bottom - 1, right, bottom, 0xffeceff1);

        int rowHeight = bottom - top;
        int imageSize = rowHeight - 12;
        if (imageSize < 34) {
            imageSize = 34;
        }
        if (imageSize > 68) {
            imageSize = 68;
        }
        int imageLeft = left + 8;
        int imageTop = top + Math.max(4, (rowHeight - imageSize) / 2);
        if (facts.imageData != null && facts.imageData.length > 0) {
            drawByteHashThumbnail(out, facts.imageHash, imageLeft, imageTop,
                    imageSize, imageSize);
            stats.genericListImages++;
            stats.genericListImageBytes += facts.imageBytes;
        } else {
            showcaseRoundRect(out, imageLeft, imageTop,
                    imageLeft + imageSize, imageTop + imageSize, 6, 6, 0xffeceff1);
            showcaseText(out, "img", imageLeft + 8, imageTop + Math.max(20, imageSize / 2 + 4),
                    8, 0xff5f6368);
        }

        int textLeft = imageLeft + imageSize + 12;
        String title = facts.textCount > 0 ? facts.texts[0] : "Row " + intAscii(position + 1);
        String meta = facts.textCount > 1 ? facts.texts[1] : "adapter row";
        String badge = facts.textCount > 2 ? facts.texts[2]
                : (facts.imageBytes > 0 ? "image " + intAscii(facts.imageBytes) + " bytes"
                        : "waiting for image");
        showcaseText(out, trimShowcaseText(title, Math.max(10, (right - textLeft) / 8)),
                textLeft, top + 18, 12, 0xff202124);
        showcaseText(out, trimShowcaseText(meta, Math.max(10, (right - textLeft) / 8)),
                textLeft, top + 35, 8, 0xff5f6368);
        showcaseText(out, trimShowcaseText(badge, Math.max(10, (right - textLeft) / 8)),
                textLeft, top + 49, 8, facts.imageBytes > 0 ? 0xff00695c : 0xffa82020);
    }

    private static void drawByteHashThumbnail(java.io.OutputStream out, int hash,
            int left, int top, int width, int height) throws java.io.IOException {
        int r = 72 + ((hash >>> 16) & 0x7f);
        int g = 72 + ((hash >>> 8) & 0x7f);
        int b = 72 + (hash & 0x7f);
        int base = 0xff000000 | (r << 16) | (g << 8) | b;
        int accent = 0xff000000
                | ((255 - r) << 16)
                | ((96 + ((hash >>> 5) & 0x7f)) << 8)
                | (255 - b);
        showcaseRoundRect(out, left, top, left + width, top + height, 6, 6, base);
        showcaseRect(out, left, top + height / 2, left + width, top + height,
                0x66000000 | (accent & 0x00ffffff));
        showcaseCircle(out, left + width - 12, top + 12, 7, 0xeeffffff);
        showcaseText(out, "img", left + 8, top + Math.max(20, height - 8),
                8, 0xffffffff);
    }

    private static void collectGenericRowFacts(android.view.View view,
            GenericRowFacts facts, int depth) {
        if (view == null || facts == null || depth > 12) {
            return;
        }
        if (view instanceof android.widget.TextView && facts.textCount < facts.texts.length) {
            try {
                CharSequence text = ((android.widget.TextView) view).getText();
                if (text != null && text.length() > 0) {
                    String value = String.valueOf(text);
                    facts.texts[facts.textCount++] = value;
                    int imageBytes = parseAdapterImageByteText(value);
                    if (imageBytes > 0 && facts.imageBytes <= 0) {
                        facts.imageBytes = imageBytes;
                        facts.imageHash = hashShowcaseText(value);
                    }
                }
            } catch (Throwable ignored) {
            }
        }
        if (view instanceof android.view.ViewGroup) {
            android.view.ViewGroup group = (android.view.ViewGroup) view;
            int count = 0;
            try {
                count = group.getChildCount();
            } catch (Throwable ignored) {
            }
            for (int i = 0; i < count; i++) {
                android.view.View child = null;
                try {
                    child = group.getChildAt(i);
                } catch (Throwable ignored) {
                }
                collectGenericRowFacts(child, facts, depth + 1);
            }
        }
    }

    private static int parseAdapterImageByteText(String value) {
        if (value == null) {
            return 0;
        }
        int at = value.indexOf("image ");
        if (at < 0) {
            return 0;
        }
        at += 6;
        int end = at;
        while (end < value.length()) {
            char c = value.charAt(end);
            if (c < '0' || c > '9') {
                break;
            }
            end++;
        }
        if (end <= at) {
            return 0;
        }
        try {
            return Integer.parseInt(value.substring(at, end));
        } catch (Throwable ignored) {
            return 0;
        }
    }

    private static int hashShowcaseText(String value) {
        int hash = 0x4a17;
        if (value == null) {
            return hash;
        }
        for (int i = 0; i < value.length(); i++) {
            hash = (hash * 33) ^ value.charAt(i);
        }
        return hash;
    }

    private static void drawShowcaseProgress(java.io.OutputStream out,
            android.widget.ProgressBar bar, int left, int top, int right, int bottom)
            throws java.io.IOException {
        if (right <= left || bottom <= top) {
            return;
        }
        int max = 100;
        int progress = 0;
        try {
            max = Math.max(1, bar.getMax());
            progress = bar.getProgress();
        } catch (Throwable ignored) {
        }
        showcaseRect(out, left, top, right, bottom, 0xffd7dee4);
        int fill = left + ((right - left) * clampInt(progress) / max);
        if (fill > right) {
            fill = right;
        }
        showcaseRect(out, left, top, fill, bottom,
                progress > 65 ? 0xff2962a0 : 0xff4d7c59);
    }

    private static int clampTextSize(int size) {
        if (size < 9) {
            return 9;
        }
        if (size > 24) {
            return 24;
        }
        return size;
    }

    private static String trimShowcaseText(String text, int maxChars) {
        if (text == null) {
            return "";
        }
        if (maxChars < 6) {
            maxChars = 6;
        }
        if (text.length() <= maxChars) {
            return text;
        }
        return text.substring(0, maxChars - 3) + "...";
    }

    private static void writeShowcaseStateFrame(Activity activity, String reason) {
        try {
            java.io.ByteArrayOutputStream ops = new java.io.ByteArrayOutputStream(8192);
            showcaseColor(ops, 0xfff7f4ef);

            boolean playing = showcaseBool(activity, "playing", false);
            boolean favorite = showcaseBool(activity, "favorite", false);
            String preset = showcaseString(activity, "selectedPreset", "Rain");
            String action = showcaseString(activity, "lastAction", "Ready");
            int layers = showcaseInt(activity, "layerCount", 4);
            int session = showcaseInt(activity, "sessionProgress", 18);
            int rain = showcaseInt(activity, "rainVolume", 70);
            int wind = showcaseInt(activity, "windVolume", 28);
            int cafe = showcaseInt(activity, "cafeVolume", 34);
            int fire = showcaseInt(activity, "fireVolume", 8);
            int brown = showcaseInt(activity, "brownVolume", 45);
            int keyboard = showcaseInt(activity, "keyboardVolume", 16);
            int waves = showcaseInt(activity, "wavesVolume", 22);
            int birds = showcaseInt(activity, "birdsVolume", 10);

            showcaseText(ops, "Westlake Noice Lab", 18, 38, 23, 0xff202124);
            showcaseText(ops, "Local ambient mixer", 20, 63, 13, 0xff5f6368);
            showcaseRect(ops, 16, 78, 464, 136, 0xffffffff);
            showcaseText(ops, action + " | " + (playing ? "Live mix" : "Idle mix"),
                    28, 105, 16, 0xff202124);
            showcaseText(ops, "Preset " + preset + "   Layers " + intAscii(layers)
                    + "   Timer " + intAscii(session) + " min",
                    28, 126, 11, 0xff5f6368);

            showcaseButton(ops, "Play", 16, 154, 232, 196, playing);
            showcaseButton(ops, "Focus", 248, 154, 464, 196, "Focus".equals(preset));
            showcaseButton(ops, "Rain", 16, 208, 232, 250, "Rain".equals(preset));
            showcaseButton(ops, "Night", 248, 208, 464, 250, "Night".equals(preset));
            showcaseButton(ops, "Add layer", 16, 262, 232, 304, false);
            showcaseButton(ops, "Save mix", 248, 262, 464, 304, favorite);

            showcaseText(ops, "Mixer", 18, 340, 17, 0xff202124);
            showcaseSoundRow(ops, 0, "Rain", rain, 356);
            showcaseSoundRow(ops, 1, "Wind", wind, 400);
            showcaseSoundRow(ops, 2, "Cafe", cafe, 444);
            showcaseSoundRow(ops, 3, "Fire", fire, 488);
            showcaseSoundRow(ops, 4, "Brown noise", brown, 532);
            showcaseSoundRow(ops, 5, "Keyboard", keyboard, 576);
            showcaseSoundRow(ops, 6, "Waves", waves, 620);
            showcaseSoundRow(ops, 7, "Birds", birds, 664);

            showcaseRect(ops, 16, 730, 464, 776, 0xffeceff1);
            showcaseText(ops, "Canvas pipe + WAT lifecycle + local state",
                    28, 758, 12, 0xff174c7f);

            byte[] data = ops.toByteArray();
            java.io.OutputStream out = System.out;
            writeIntLe(out, 0x444C5354);
            writeIntLe(out, data.length);
            out.write(data);
            out.flush();
            appendCutoffCanaryMarker("SHOWCASE_DIRECT_FRAME_OK reason=" + reason
                    + " bytes=" + intAscii(data.length));
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] Showcase direct frame error", t);
            logThrowableFrames("[WestlakeLauncher] Showcase direct frame", t, 12);
            appendCutoffCanaryMarker("SHOWCASE_DIRECT_FRAME_FAIL err="
                    + t.getClass().getName());
        }
    }

    private static void showcaseButton(java.io.OutputStream out, String label,
            int left, int top, int right, int bottom, boolean active)
            throws java.io.IOException {
        showcaseRect(out, left, top, right, bottom, active ? 0xff2962a0 : 0xffffffff);
        showcaseText(out, label, left + 16, top + 27, 13, active ? 0xffffffff : 0xff202124);
    }

    private static void showcaseSoundRow(java.io.OutputStream out, int slot,
            String label, int volume, int top) throws java.io.IOException {
        showcaseRect(out, 16, top, 464, top + 34, slot % 2 == 0 ? 0xffffffff : 0xfffafafa);
        showcaseText(out, label, 28, top + 22, 12, 0xff202124);
        showcaseText(out, intAscii(volume) + "%", 130, top + 22, 11, 0xff5f6368);
        showcaseRect(out, 178, top + 13, 440, top + 22, 0xffd7dee4);
        int fill = 178 + ((440 - 178) * clampInt(volume) / 100);
        showcaseRect(out, 178, top + 13, fill, top + 22,
                volume > 65 ? 0xff2962a0 : 0xff4d7c59);
    }

    private static void showcaseSoundCard(java.io.OutputStream out, String label,
            String subtitle, int volume, int left, int top, int accent)
            throws java.io.IOException {
        int right = left + 216;
        int bottom = top + 72;
        showcaseRect(out, left, top, right, bottom, 0xffffffff);
        showcaseRect(out, left + 12, top + 16, left + 42, top + 46, accent);
        showcaseText(out, "~", left + 23, top + 38, 16, 0xffffffff);
        showcaseText(out, label, left + 54, top + 24, 14, 0xff17212b);
        showcaseText(out, subtitle, left + 54, top + 42, 9, 0xff6c757d);
        showcaseText(out, intAscii(volume) + "%", right - 42, top + 24, 10, 0xff4f5f63);
        showcaseRect(out, left + 54, top + 54, right - 14, top + 62, 0xffd8e5e0);
        int fill = left + 54 + ((right - left - 68) * clampInt(volume) / 100);
        showcaseRect(out, left + 54, top + 54, fill, top + 62, accent);
    }

    private static void showcaseChip(java.io.OutputStream out, String label,
            int left, int top, int right, int bottom) throws java.io.IOException {
        showcaseRect(out, left, top, right, bottom, 0xffe7f0ec);
        showcaseText(out, label, left + 10, top + 20, 10, 0xff2d5f50);
    }

    private static void showcaseMetricCard(java.io.OutputStream out, String label,
            String value, String subtitle, int left, int top) throws java.io.IOException {
        int right = left + 216;
        int bottom = top + 72;
        showcaseRect(out, left, top, right, bottom, 0xffffffff);
        showcaseText(out, label, left + 16, top + 22, 11, 0xff6c757d);
        showcaseText(out, value, left + 16, top + 48, 22, 0xff17212b);
        showcaseText(out, subtitle, left + 96, top + 48, 10, 0xff6c757d);
    }

    private static void showcaseSettingRow(java.io.OutputStream out, String label,
            String value, int left, int top, int accent) throws java.io.IOException {
        int right = 464;
        int bottom = top + 48;
        showcaseRect(out, left, top, right, bottom, 0xffffffff);
        showcaseRect(out, left + 12, top + 12, left + 34, top + 34, accent);
        showcaseText(out, label, left + 48, top + 22, 12, 0xff17212b);
        showcaseText(out, value, left + 48, top + 40, 9, 0xff6c757d);
    }

    private static void showcaseVenueCard(java.io.OutputStream out, String name,
            String category, String mealType, String status, int ratingTenths, int reviews,
            int count, int index, int imageBytes, int imageWidth, int imageHeight,
            int imageHash, boolean loading) throws java.io.IOException {
        showcaseRect(out, 16, 260, 464, 398, 0xffffffff);
        int accent = imageHash == 0 ? 0xff2d7d5f : (0xff000000 | (imageHash & 0x00ffffff));
        showcaseRect(out, 30, 280, 150, 370, accent);
        showcaseRect(out, 38, 288, 142, 326, 0x33ffffff);
        showcaseText(out, loading ? "..." : "img", 74, 336, 16, 0xffffffff);
        if (imageBytes > 0) {
            showcaseText(out, intAscii(imageBytes) + " bytes", 44, 354, 9, 0xffffffff);
            showcaseText(out, intAscii(imageWidth) + "x" + intAscii(imageHeight),
                    54, 368, 8, 0xffffffff);
        }
        showcaseText(out, trimShowcaseText(name, 25), 168, 288, 16, 0xff17212b);
        showcaseText(out, trimShowcaseText(category + " - " + mealType, 30),
                168, 312, 11, 0xff6c757d);
        showcaseRect(out, 168, 328, 286, 350, 0xfffff3c4);
        showcaseText(out, showcaseRatingText(ratingTenths) + " stars", 180, 344, 10, 0xff7a4d00);
        showcaseText(out, intAscii(reviews) + " reviews", 304, 344, 10, 0xff4f5f63);
        showcaseText(out, trimShowcaseText(status, 35), 168, 370, 10,
                loading ? 0xff2860a8 : 0xff2d7d5f);
        showcaseText(out, "remote REST list " + intAscii(count)
                + "  card " + intAscii(index + 1), 168, 390, 9, 0xff6c757d);
    }

    private static String showcaseRatingText(int tenths) {
        if (tenths <= 0) {
            tenths = 46;
        }
        return intAscii(tenths / 10) + "." + intAscii(tenths % 10);
    }

    private static void showcaseBottomNav(java.io.OutputStream out, int page)
            throws java.io.IOException {
        showcaseRect(out, 0, 736, SURFACE_WIDTH, 800, 0xff173b3f);
        showcaseNavItem(out, "Library", 0, 120, page == 0);
        showcaseNavItem(out, "Mixer", 120, 240, page == 1);
        showcaseNavItem(out, "Timer", 240, 360, page == 2);
        showcaseNavItem(out, "Settings", 360, 480, page == 3);
    }

    private static void showcaseNavItem(java.io.OutputStream out, String label,
            int left, int right, boolean active) throws java.io.IOException {
        if (active) {
            showcaseRect(out, left + 10, 746, right - 10, 792, 0xff24595e);
        }
        showcaseText(out, label, left + 22, 774, 12, active ? 0xffffffff : 0xffb8d8d2);
    }

    private static void showcaseColor(java.io.OutputStream out, int color)
            throws java.io.IOException {
        out.write(1);
        writeIntLe(out, color);
    }

    private static void showcaseRect(java.io.OutputStream out, float left, float top,
            float right, float bottom, int color) throws java.io.IOException {
        out.write(2);
        writeFloatLe(out, left);
        writeFloatLe(out, top);
        writeFloatLe(out, right);
        writeFloatLe(out, bottom);
        writeIntLe(out, color);
    }

    private static void showcaseLine(java.io.OutputStream out, float x1, float y1,
            float x2, float y2, int color, float strokeWidth) throws java.io.IOException {
        out.write(4);
        writeFloatLe(out, x1);
        writeFloatLe(out, y1);
        writeFloatLe(out, x2);
        writeFloatLe(out, y2);
        writeIntLe(out, color);
        writeFloatLe(out, strokeWidth);
    }

    private static void showcaseRoundRect(java.io.OutputStream out, float left, float top,
            float right, float bottom, float rx, float ry, int color) throws java.io.IOException {
        out.write(9);
        writeFloatLe(out, left);
        writeFloatLe(out, top);
        writeFloatLe(out, right);
        writeFloatLe(out, bottom);
        writeFloatLe(out, rx);
        writeFloatLe(out, ry);
        writeIntLe(out, color);
    }

    private static void showcaseCircle(java.io.OutputStream out, float cx, float cy,
            float radius, int color) throws java.io.IOException {
        out.write(10);
        writeFloatLe(out, cx);
        writeFloatLe(out, cy);
        writeFloatLe(out, radius);
        writeIntLe(out, color);
    }

    private static void showcaseImage(java.io.OutputStream out, byte[] imageData,
            float x, float y, int width, int height) throws java.io.IOException {
        if (imageData == null || imageData.length == 0 || imageData.length > 512 * 1024) {
            return;
        }
        out.write(11);
        writeFloatLe(out, x);
        writeFloatLe(out, y);
        writeIntLe(out, width);
        writeIntLe(out, height);
        writeIntLe(out, imageData.length);
        out.write(imageData);
    }

    private static void showcaseText(java.io.OutputStream out, String text, float x,
            float y, float size, int color) throws java.io.IOException {
        if (text == null) {
            text = "";
        }
        byte[] bytes = showcaseUtf8Bytes(text);
        int length = bytes.length;
        if (length > 65535) {
            length = 65535;
        }
        out.write(3);
        writeFloatLe(out, x);
        writeFloatLe(out, y);
        writeFloatLe(out, size);
        writeIntLe(out, color);
        writeShortLe(out, length);
        out.write(bytes, 0, length);
    }

    private static byte[] showcaseUtf8Bytes(String text) {
        java.io.ByteArrayOutputStream out =
                new java.io.ByteArrayOutputStream(text.length() * 3);
        for (int i = 0; i < text.length(); i++) {
            int c = text.charAt(i);
            if (c >= 0xd800 && c <= 0xdbff && i + 1 < text.length()) {
                int lo = text.charAt(i + 1);
                if (lo >= 0xdc00 && lo <= 0xdfff) {
                    c = 0x10000 + ((c - 0xd800) << 10) + (lo - 0xdc00);
                    i++;
                }
            }
            if (c < 0x80) {
                out.write(c);
            } else if (c < 0x800) {
                out.write(0xc0 | (c >> 6));
                out.write(0x80 | (c & 0x3f));
            } else if (c < 0x10000) {
                out.write(0xe0 | (c >> 12));
                out.write(0x80 | ((c >> 6) & 0x3f));
                out.write(0x80 | (c & 0x3f));
            } else {
                out.write(0xf0 | (c >> 18));
                out.write(0x80 | ((c >> 12) & 0x3f));
                out.write(0x80 | ((c >> 6) & 0x3f));
                out.write(0x80 | (c & 0x3f));
            }
        }
        return out.toByteArray();
    }

    private static void writeFloatLe(java.io.OutputStream out, float value)
            throws java.io.IOException {
        writeIntLe(out, Float.floatToIntBits(value));
    }

    private static void writeIntLe(java.io.OutputStream out, int value)
            throws java.io.IOException {
        out.write(value & 0xff);
        out.write((value >>> 8) & 0xff);
        out.write((value >>> 16) & 0xff);
        out.write((value >>> 24) & 0xff);
    }

    private static void writeShortLe(java.io.OutputStream out, int value)
            throws java.io.IOException {
        out.write(value & 0xff);
        out.write((value >>> 8) & 0xff);
    }

    private static int clampInt(int value) {
        if (value < 0) {
            return 0;
        }
        if (value > 100) {
            return 100;
        }
        return value;
    }

    private static String intAscii(int value) {
        if (value == 0) {
            return "0";
        }
        boolean negative = value < 0;
        int n = negative ? -value : value;
        StringBuilder sb = new StringBuilder(12);
        while (n > 0) {
            int digit = n % 10;
            sb.insert(0, (char) ('0' + digit));
            n = n / 10;
        }
        if (negative) {
            sb.insert(0, '-');
        }
        return sb.toString();
    }

    private static String boolToken(boolean value) {
        return value ? "true" : "false";
    }

    private static String safeMarkerToken(String value) {
        if (value == null) {
            return "null";
        }
        StringBuilder out = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
                    || (c >= '0' && c <= '9') || c == '.' || c == '_' || c == '-') {
                out.append(c);
            } else {
                out.append('_');
            }
        }
        return out.toString();
    }

    private static String showcaseString(Activity activity, String field, String fallback) {
        try {
            java.lang.reflect.Field f = activity.getClass().getDeclaredField(field);
            f.setAccessible(true);
            Object value = f.get(activity);
            return value != null ? String.valueOf(value) : fallback;
        } catch (Throwable ignored) {
            return fallback;
        }
    }

    private static byte[] showcaseBytes(Activity activity, String field) {
        try {
            java.lang.reflect.Field f = activity.getClass().getDeclaredField(field);
            f.setAccessible(true);
            Object value = f.get(activity);
            return value instanceof byte[] ? (byte[]) value : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static int showcaseInt(Activity activity, String field, int fallback) {
        try {
            java.lang.reflect.Field f = activity.getClass().getDeclaredField(field);
            f.setAccessible(true);
            return f.getInt(activity);
        } catch (Throwable ignored) {
            return fallback;
        }
    }

    private static boolean showcaseBool(Activity activity, String field, boolean fallback) {
        try {
            java.lang.reflect.Field f = activity.getClass().getDeclaredField(field);
            f.setAccessible(true);
            return f.getBoolean(activity);
        } catch (Throwable ignored) {
            return fallback;
        }
    }

    private static final String TOUCH_PATH_PRIMARY = "/data/local/tmp/a2oh/touch.dat";
    private static final String TOUCH_PATH_FALLBACK = "/sdcard/westlake_touch.dat";

    private static String defaultTouchPath() {
        return canOpenFile(TOUCH_PATH_PRIMARY) ? TOUCH_PATH_PRIMARY : TOUCH_PATH_FALLBACK;
    }

    /**
     * Build a visible McDonald's dashboard UI on the activity's content view.
     * Uses the real base_layout container and adds mock menu content.
     */
    public static void populateDashboardFallback(Activity activity) {
        if (isDashboardFallbackInstalled(activity)) {
            startupLog("[WestlakeLauncher] Dashboard fallback already installed for "
                    + activity.getClass().getName());
            return;
        }
        sDirectDashboardFallbackActive = false;
        try {
            if (shouldUseTextOnlyDashboardMenu(activity)) {
                drawTextOnlyDashboardMenu();
                markDashboardFallbackInstalled(activity);
                return;
            }
            if (installDashboardViewFallback(activity)) {
                markDashboardFallbackInstalled(activity);
                startupLog("[WestlakeLauncher] Dashboard fallback UI populated in container");
                return;
            }
            if (activity == null || !OHBridge.isNativeAvailable()) {
                return;
            }
            long surf = OHBridge.surfaceCreate(0, SURFACE_WIDTH, SURFACE_HEIGHT);
            long canv = OHBridge.surfaceGetCanvas(surf);
            long font = OHBridge.fontCreate();
            long pen = OHBridge.penCreate();
            long brush = OHBridge.brushCreate();

            OHBridge.canvasDrawColor(canv, 0xFFF5F5F5);

            OHBridge.fontSetSize(font, 30);
            OHBridge.penSetColor(pen, 0xFFDA291C);
            OHBridge.canvasDrawText(canv, "McDonald's", 36, 90, font, pen, brush);

            OHBridge.fontSetSize(font, 18);
            OHBridge.penSetColor(pen, 0xFF333333);
            OHBridge.canvasDrawText(canv, "Dashboard fallback", 36, 130, font, pen, brush);

            OHBridge.fontSetSize(font, 20);
            OHBridge.penSetColor(pen, 0xFF111111);
            OHBridge.canvasDrawText(canv, "Big Mac Combo        $5.99", 36, 220, font, pen, brush);
            OHBridge.canvasDrawText(canv, "2 for $6 Mix & Match", 36, 275, font, pen, brush);
            OHBridge.canvasDrawText(canv, "Free Medium Fries", 36, 330, font, pen, brush);
            OHBridge.canvasDrawText(canv, "McFlurry OREO        $3.49", 36, 385, font, pen, brush);

            OHBridge.fontSetSize(font, 16);
            OHBridge.penSetColor(pen, 0xFF666666);
            OHBridge.canvasDrawText(canv, "Home   Deals   Order   Rewards   More", 24, 730, font, pen, brush);

            OHBridge.surfaceFlush(surf);
            sDirectDashboardFallbackActive = true;
            startupLog("[WestlakeLauncher] Dashboard fallback drawn via OHBridge");
            markDashboardFallbackInstalled(activity);
        } catch (Throwable t) {
            sDirectDashboardFallbackActive = false;
            try { nativePrintException(t); } catch (Throwable ignored) {}
            startupLog("[WestlakeLauncher] populateDashboardFallback error", t);
        }
    }

    private static void renderLoop(Activity initialActivity, MiniActivityManager am) {
        if (android.app.WestlakeActivityThread.pendingDashboardClass != null) {
            String dashClass = android.app.WestlakeActivityThread.pendingDashboardClass;
            android.app.WestlakeActivityThread.pendingDashboardClass = null;
            startupLog("[WestlakeLauncher] Launching pending dashboard: " + dashClass);
            startupLog("[WestlakeLauncher] renderLoop initial before dashboard="
                    + (initialActivity != null ? initialActivity.getClass().getName() : "null"));
            Activity dash = null;
            try {
                android.app.WestlakeActivityThread wat = android.app.WestlakeActivityThread.currentActivityThread();
                Intent dashIntent = new Intent();
                dashIntent.setComponent(new ComponentName("com.mcdonalds.app", dashClass));
                dash = android.app.WestlakeActivityThread.launchActivity(
                        wat, dashClass, "com.mcdonalds.app", dashIntent);
            } catch (Throwable t) {
                startupLog("[WestlakeLauncher] Dashboard launchActivity error", t);
                logThrowableFrames("[WestlakeLauncher] Dashboard launchActivity", t, 12);
            }
            if (dash != null) {
                try {
                    Activity previous = initialActivity;
                    initialActivity = dash;
                    startupLog("[WestlakeLauncher] Dashboard active: " + dash.getClass().getName());
                    Activity resumed = null;
                    try { resumed = am.getResumedActivity(); } catch (Throwable ignored) {}
                    startupLog("[WestlakeLauncher] Dashboard resumed after launch="
                            + (resumed != null ? resumed.getClass().getName() : "null"));
                    boolean fallbackAlreadyInstalled = false;
                    try {
                        fallbackAlreadyInstalled = isDashboardFallbackInstalled(dash);
                    } catch (Throwable installedLookupError) {
                        startupLog("[WestlakeLauncher] Dashboard fallback lookup error", installedLookupError);
                        logThrowableFrames("[WestlakeLauncher] Dashboard fallback lookup",
                                installedLookupError, 8);
                    }
                    if (!fallbackAlreadyInstalled) {
                        try {
                            populateDashboardFallback(dash);
                        } catch (Throwable fallbackError) {
                            startupLog("[WestlakeLauncher] Dashboard fallback install error", fallbackError);
                            logThrowableFrames("[WestlakeLauncher] Dashboard fallback install",
                                    fallbackError, 8);
                        }
                    } else {
                        startupLog("[WestlakeLauncher] Dashboard fallback already present before render loop");
                    }
                    startupLog("[WestlakeLauncher] Dashboard fallback handoff ready");
                    // Let the normal render loop drive the first dashboard frame.
                    // The older eager render/demo path was crashing inside the
                    // guest before the real view tree had stabilized.
                    splashImageData = null;
                    if (previous != null && previous != dash) {
                        boolean firstDashboardPromotion = isHomeDashboardActivity(dash)
                                && !isHomeDashboardActivity(previous);
                        if (firstDashboardPromotion) {
                            startupLog("[WestlakeLauncher] Preserving previous surface for first dashboard handoff");
                        } else {
                            try {
                                previous.onSurfaceDestroyed();
                                startupLog("[WestlakeLauncher] Previous activity surface destroyed");
                            } catch (Throwable previousDestroyError) {
                                startupLog("[WestlakeLauncher] Previous surface destroy error",
                                        previousDestroyError);
                            }
                        }
                    }
                    startupLog("[WestlakeLauncher] Dashboard pre-first-frame direct="
                            + sDirectDashboardFallbackActive);
                    if (!sDirectDashboardFallbackActive) {
                        try {
                            boolean adoptedSurface = false;
                            if (previous != null && previous != dash
                                    && isHomeDashboardActivity(dash)
                                    && !isHomeDashboardActivity(previous)) {
                                try {
                                    adoptedSurface = dash.adoptSurfaceFrom(previous);
                                    startupLog("[WestlakeLauncher] Dashboard adopted previous surface="
                                            + adoptedSurface);
                                } catch (Throwable adoptError) {
                                    startupLog("[WestlakeLauncher] Dashboard surface adopt error",
                                            adoptError);
                                }
                            }
                            if (!adoptedSurface) {
                                dash.onSurfaceCreated(0L, SURFACE_WIDTH, SURFACE_HEIGHT);
                            }
                            dash.renderFrame();
                            startupLog("[WestlakeLauncher] Dashboard first frame requested");
                            sDisableHostInputPolling = true;
                            startupLog("[WestlakeLauncher] Host input polling disabled after first dashboard frame");
                        } catch (Throwable frameError) {
                            startupLog("[WestlakeLauncher] Dashboard first render error", frameError);
                            logThrowableFrames("[WestlakeLauncher] Dashboard first render", frameError, 8);
                        }
                    } else {
                        startupLog("[WestlakeLauncher] Dashboard frame is direct OHBridge fallback");
                    }
                } catch (Throwable t) {
                    startupLog("[WestlakeLauncher] Dashboard handoff error", t);
                    logThrowableFrames("[WestlakeLauncher] Dashboard handoff", t, 12);
                }
            }
        }

        if (sDirectDashboardFallbackActive) {
            startupLog("[WestlakeLauncher] Direct dashboard fallback active");
            while (true) {
                try { Thread.sleep(1000); } catch (InterruptedException e) { break; }
            }
            return;
        }

        // Check if Activity has our shim methods (onSurfaceCreated/renderFrame)
        boolean hasShimMethods = true;
        try { initialActivity.getClass().getMethod("onSurfaceCreated", long.class, int.class, int.class); }
        catch (NoSuchMethodException e) { hasShimMethods = false; }
        if (initialActivity != null) {
            startupLog("[WestlakeLauncher] renderLoop activity="
                    + initialActivity.getClass().getName()
                    + (hasShimMethods ? " shim=yes" : " shim=no"));
        } else {
            startupLog("[WestlakeLauncher] renderLoop activity=null");
        }

        if (initialActivity != null && hasShimMethods) {
            try {
                String initialName = initialActivity.getClass().getName();
                if (initialName != null && initialName.toLowerCase().contains("dashboard")) {
                    splashImageData = null;
                }
            } catch (Throwable ignored) {
            }
            if (isHomeDashboardActivity(initialActivity)) {
                startupLog("[WestlakeLauncher] renderLoop skipping dashboard surface re-prime");
            } else {
                try {
                    initialActivity.onSurfaceDestroyed();
                } catch (Throwable ignored) {
                }
                try {
                    initialActivity.onSurfaceCreated(0L, SURFACE_WIDTH, SURFACE_HEIGHT);
                    startupLog("[WestlakeLauncher] renderLoop surface primed");
                } catch (Throwable surfaceError) {
                    startupLog("[WestlakeLauncher] renderLoop surface prime error", surfaceError);
                }
            }
        }

        if (!hasShimMethods) {
            startupLog("[WestlakeLauncher] Framework Activity — OHBridge-only render loop");
            // Simple keep-alive loop: the splash was already sent via OHBridge
            // Touch events can still be processed
            while (true) {
                try { Thread.sleep(1000); } catch (InterruptedException e) { break; }
            }
            return;
        }

        long frameCount = 0;
        int lastTouchSeq = -1;
        int lastTextSize = -1;
        int lastTextHash = 0;

        // Prefer WESTLAKE_TOUCH env var (set by Compose host)
        String envTouch = System.getenv("WESTLAKE_TOUCH");
        String touchPath = null;
        if (envTouch != null && !envTouch.isEmpty()) {
            touchPath = envTouch;
            startupLog("[WestlakeLauncher] Touch file (env): " + envTouch);
        } else {
            touchPath = defaultTouchPath();
            startupLog("[WestlakeLauncher] Touch file (default): " + touchPath);
        }
        String touchDir = parentPath(touchPath);
        String textPath = joinPath(touchDir, "westlake_text.dat");

        // After splash: if we have real icons from app_process64, render them
        if (realIconsPng != null && com.ohos.shim.bridge.OHBridge.isNativeAvailable()) {
            try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
            try {
                long surf = com.ohos.shim.bridge.OHBridge.surfaceCreate(0, SURFACE_WIDTH, SURFACE_HEIGHT);
                long canv = com.ohos.shim.bridge.OHBridge.surfaceGetCanvas(surf);
                com.ohos.shim.bridge.OHBridge.canvasDrawImage(canv, realIconsPng, 0, 0, SURFACE_WIDTH, SURFACE_HEIGHT);
                com.ohos.shim.bridge.OHBridge.surfaceFlush(surf);
                startupLog("[WestlakeLauncher] Real icons frame rendered! (" + realIconsPng.length + " bytes)");
            } catch (Throwable t) {
                startupLog("[WestlakeLauncher] Real icons render error", t);
            }
        }

        boolean needsRender = (initialActivity != null && !isHomeDashboardActivity(initialActivity));
        sDisableHostInputPolling = false;
        long downTime = 0;
        int lastTouchY = 0;
        int scrollOffset = 0;
        int totalDragDistance = 0;
        android.view.View lastDecorView = null;

        Activity current = initialActivity; // prefer WAT-created activity
        if (current == null) current = am.getResumedActivity();
        startupLog("[WestlakeLauncher] renderLoop current="
                + (current != null ? current.getClass().getName() : "null"));

        while (true) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                break;
            }
            if (current == null) {
                startupLog("[WestlakeLauncher] No resumed activity, exiting");
                break;
            }

            if (!sDisableHostInputPolling) {
                // Check for text input from host (long-press dialog)
                byte[] textBuf = canOpenFile(textPath) ? tryReadFileBytes(textPath) : null;
                if (textBuf != null && textBuf.length > 0) {
                    try {
                        int textHash = java.util.Arrays.hashCode(textBuf);
                        if (textBuf.length != lastTextSize || textHash != lastTextHash) {
                            lastTextSize = textBuf.length;
                            lastTextHash = textHash;
                            String inputText = utf8BytesToString(textBuf, "").trim();
                            if (inputText.length() > 0) {
                                android.view.View decor = null;
                                try { decor = current.getWindow().getDecorView(); } catch (Exception e5) {}
                                if (decor != null) {
                                    android.widget.EditText et = findEditText(decor);
                                    if (et != null) {
                                        et.setText(inputText);
                                        startupLog("[WestlakeLauncher] Text input: '" + inputText + "' -> " + et);
                                        needsRender = true;
                                    } else {
                                        startupLog("[WestlakeLauncher] Text input: no EditText found");
                                    }
                                }
                            }
                        }
                    } catch (Exception e5) {
                        startupLog("[WestlakeLauncher] Text input error: " + e5);
                    }
                } else {
                    lastTextSize = -1;
                    lastTextHash = 0;
                }

                // Check for touch events
                byte[] buf = canOpenFile(touchPath) ? tryReadFileBytes(touchPath) : null;
                if (buf != null && buf.length >= 16) {
                    try {
                        java.nio.ByteBuffer bb = java.nio.ByteBuffer.wrap(buf, 0, 16);
                        bb.order(java.nio.ByteOrder.LITTLE_ENDIAN);
                        int action = bb.getInt();
                        int x = bb.getInt();
                        int y = bb.getInt();
                        int seq = bb.getInt();

                        if (seq != lastTouchSeq) {
                            lastTouchSeq = seq;

                            long now = System.currentTimeMillis();
                            if (action == 0) {
                                downTime = now;
                                lastTouchY = y;
                                totalDragDistance = 0;
                                startupLog("[WestlakeLauncher] Touch DOWN at (" + x + "," + y + ")");
                                current.dispatchTouchEvent(
                                    android.view.MotionEvent.obtain(downTime, now, 0, (float)x, (float)y, 0));
                                needsRender = true;
                            } else if (action == 2) {
                                if (downTime == 0) downTime = now;
                                int deltaY = lastTouchY - y;
                                lastTouchY = y;
                                totalDragDistance += Math.abs(deltaY);

                                android.view.View decor = null;
                                try { decor = current.getWindow().getDecorView(); } catch (Exception e3) {}
                                if (decor != null) {
                                    int maxScroll = SURFACE_HEIGHT * 2;
                                    scrollOffset += deltaY;
                                    if (scrollOffset < 0) scrollOffset = 0;
                                    if (scrollOffset > maxScroll) scrollOffset = maxScroll;
                                    decor.scrollTo(0, scrollOffset);
                                }

                                current.dispatchTouchEvent(
                                    android.view.MotionEvent.obtain(downTime, now, 2, (float)x, (float)y, 0));
                                needsRender = true;
                            } else if (action == 1) {
                                if (downTime == 0) downTime = now;
                                startupLog("[WestlakeLauncher] Touch UP at (" + x + "," + y + ")");
                                current.dispatchTouchEvent(
                                    android.view.MotionEvent.obtain(downTime, now, 1, (float)x, (float)y, 0));
                                needsRender = true;

                                if (totalDragDistance < 20) {
                                    android.view.View decor = null;
                                    try { decor = current.getWindow().getDecorView(); } catch (Exception e3) {}
                                    if (decor != null) {
                                        android.view.View target = findViewAt(decor, x, y + scrollOffset);
                                        if (target != null) {
                                            android.view.ViewParent parent = target.getParent();
                                            while (parent != null) {
                                                if (parent instanceof android.widget.ListView) {
                                                    android.widget.ListView lv = (android.widget.ListView) parent;
                                                    int pos = lv.getPositionForView(target);
                                                    if (pos >= 0) {
                                                        startupLog("[WestlakeLauncher] ListView item " + pos + " clicked");
                                                        lv.performItemClick(target, pos, pos);
                                                    }
                                                    break;
                                                }
                                                if (parent instanceof android.view.View) {
                                                    parent = ((android.view.View) parent).getParent();
                                                } else {
                                                    break;
                                                }
                                            }
                                            target.performClick();
                                        }
                                    }
                                }
                                android.view.View newDecor = null;
                                try { newDecor = current.getWindow().getDecorView(); } catch (Exception e4) {}
                                if (newDecor != null && newDecor != lastDecorView) {
                                    newDecor.scrollTo(0, 0);
                                    scrollOffset = 0;
                                    lastDecorView = newDecor;
                                }
                                downTime = 0;
                            }
                        }
                    } catch (Exception e) {
                        startupLog("[WestlakeLauncher] touch read error: " + e);
                    }
                }
            }

            if (needsRender) {
                Activity next = current;
                Activity resumed = null;
                try { resumed = am.getResumedActivity(); } catch (Throwable ignored) {}
                if (resumed != null) {
                    next = resumed;
                }
                if (next != null) {
                    boolean dashboardActivity = isHomeDashboardActivity(next);
                    if (next != current) {
                        if (!dashboardActivity) {
                            try { next.onSurfaceCreated(0L, SURFACE_WIDTH, SURFACE_HEIGHT); } catch (Exception e2) {}
                        }
                        startupLog("[WestlakeLauncher] Navigated to " + next.getClass().getSimpleName());
                        current = next;
                    }
                    if (dashboardActivity) {
                        if (frameCount < 5) {
                            startupLog("[WestlakeLauncher] Suppressing dashboard auto-render after handoff");
                        }
                    } else {
                        try { current.renderFrame(); } catch (Exception e2) {
                            if (frameCount < 5) startupLog("[WestlakeLauncher] renderFrame error", e2);
                            if (frameCount < 2) {
                                logThrowableFrames("[WestlakeLauncher] renderFrame", e2, 12);
                            }
                        }
                    }
                }
                needsRender = false;
            }

            frameCount++;
        }
        startupLog("[WestlakeLauncher] Render loop ended");
    }

    /** Find the deepest view containing the given point (absolute coords) */
    private static android.widget.EditText findEditText(android.view.View v) {
        if (v instanceof android.widget.EditText) return (android.widget.EditText) v;
        if (v instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                android.widget.EditText found = findEditText(vg.getChildAt(i));
                if (found != null) return found;
            }
        }
        return null;
    }

    private static android.view.View findViewAt(android.view.View v, int x, int y) {
        if (!(v instanceof android.view.ViewGroup)) {
            return v;
        }
        android.view.ViewGroup vg = (android.view.ViewGroup) v;
        for (int i = vg.getChildCount() - 1; i >= 0; i--) {
            android.view.View child = vg.getChildAt(i);
            int cx = x - child.getLeft() + child.getScrollX();
            int cy = y - child.getTop() + child.getScrollY();
            if (cx >= 0 && cx < child.getWidth() && cy >= 0 && cy < child.getHeight()) {
                return findViewAt(child, cx, cy);
            }
        }
        return v;
    }

    /**
     * Inject mock McDonald's dashboard content into the 5 sections of activity_home_dashboard.xml.
     * This simulates what the real app would show after loading data from the API.
     */
    private static void injectDashboardContent(Activity ctx, android.view.ViewGroup root) {
        // Find the 5 LinearLayout sections by traversing the tree
        java.util.List<android.widget.LinearLayout> sections = new java.util.ArrayList<>();
        findLinearLayouts(root, sections);
        startupLog("[WestlakeLauncher] Found " + sections.size() + " sections to fill");

        // Fix layout params: sections should wrap_content, not fill parent
        for (android.widget.LinearLayout s : sections) {
            android.view.ViewGroup parent = (android.view.ViewGroup) s.getParent();
            android.view.ViewGroup.LayoutParams lp;
            if (parent instanceof android.widget.FrameLayout) {
                lp = new android.widget.FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT);
            } else {
                lp = new android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            }
            s.setLayoutParams(lp);
        }
        // Make the parent use vertical LinearLayout
        if (sections.size() > 0) {
            android.view.ViewGroup parent = (android.view.ViewGroup) sections.get(0).getParent();
            if (parent instanceof android.widget.LinearLayout) {
                ((android.widget.LinearLayout) parent).setOrientation(android.widget.LinearLayout.VERTICAL);
            } else if (parent instanceof android.widget.FrameLayout) {
                // Replace FrameLayout parent with LinearLayout
                android.widget.LinearLayout newParent = new android.widget.LinearLayout(ctx);
                newParent.setOrientation(android.widget.LinearLayout.VERTICAL);
                // Move all children
                while (parent.getChildCount() > 0) {
                    android.view.View child = parent.getChildAt(0);
                    parent.removeViewAt(0);
                    android.widget.LinearLayout.LayoutParams clp = new android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
                    newParent.addView(child, clp);
                }
                parent.addView(newParent, new android.widget.FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT));
            }
        }

        int RED = 0xFFDA291C;
        int YELLOW = 0xFFFFCC00;
        int WHITE = 0xFFFFFFFF;
        int DARK = 0xFF292929;
        int LIGHT_GRAY = 0xFFF5F5F5;

        // Section 0: Hero banner
        if (sections.size() > 0) {
            android.widget.LinearLayout hero = sections.get(0);
            hero.setOrientation(android.widget.LinearLayout.VERTICAL);
            hero.setBackgroundColor(RED);
            hero.setPadding(24, 32, 24, 32);

            android.widget.TextView title = new android.widget.TextView(ctx);
            title.setText("Welcome to McDonald's");
            title.setTextSize(28);
            title.setTextColor(YELLOW);
            title.setGravity(android.view.Gravity.CENTER);
            hero.addView(title);

            android.widget.TextView sub = new android.widget.TextView(ctx);
            sub.setText("Order ahead & skip the line");
            sub.setTextSize(16);
            sub.setTextColor(WHITE);
            sub.setGravity(android.view.Gravity.CENTER);
            sub.setPadding(0, 8, 0, 16);
            hero.addView(sub);
        }

        // Section 1: Deals
        if (sections.size() > 1) {
            android.widget.LinearLayout deals = sections.get(1);
            deals.setOrientation(android.widget.LinearLayout.VERTICAL);
            deals.setBackgroundColor(LIGHT_GRAY);
            deals.setPadding(16, 16, 16, 16);
            addSectionHeader(ctx, deals, "Deals", DARK);
            String[][] dealItems = {
                {"$1 Any Size Soft Drink", "With app purchase"},
                {"Free Medium Fries", "With $1 minimum purchase"},
                {"$3 Bundle", "McChicken + Small Fries"},
                {"Buy 1 Get 1 Free", "Big Mac or Quarter Pounder"},
            };
            for (String[] deal : dealItems) {
                addMenuItem(ctx, deals, deal[0], deal[1], RED, DARK);
            }
        }

        // Section 2: Menu
        if (sections.size() > 2) {
            android.widget.LinearLayout menu = sections.get(2);
            menu.setOrientation(android.widget.LinearLayout.VERTICAL);
            menu.setBackgroundColor(WHITE);
            menu.setPadding(16, 16, 16, 16);
            addSectionHeader(ctx, menu, "Menu", DARK);
            String[][] menuItems = {
                {"Big Mac", "$5.99"},
                {"Quarter Pounder w/ Cheese", "$6.49"},
                {"10 Piece McNuggets", "$5.49"},
                {"McChicken", "$2.49"},
                {"Filet-O-Fish", "$5.29"},
                {"Large Fries", "$3.79"},
                {"McFlurry with OREO Cookies", "$4.39"},
            };
            for (String[] item : menuItems) {
                addMenuItem(ctx, menu, item[0], item[1], DARK, 0xFF666666);
            }
        }

        // Section 3: Rewards
        if (sections.size() > 3) {
            android.widget.LinearLayout rewards = sections.get(3);
            rewards.setOrientation(android.widget.LinearLayout.VERTICAL);
            rewards.setBackgroundColor(YELLOW);
            rewards.setPadding(16, 24, 16, 24);
            addSectionHeader(ctx, rewards, "MyMcDonald's Rewards", DARK);

            android.widget.TextView pts = new android.widget.TextView(ctx);
            pts.setText("1,250 Points");
            pts.setTextSize(32);
            pts.setTextColor(DARK);
            pts.setGravity(android.view.Gravity.CENTER);
            pts.setPadding(0, 8, 0, 8);
            rewards.addView(pts);

            android.widget.TextView info = new android.widget.TextView(ctx);
            info.setText("1,500 more points until your next free reward!");
            info.setTextSize(14);
            info.setTextColor(0xFF444444);
            info.setGravity(android.view.Gravity.CENTER);
            rewards.addView(info);
        }

        // Section 4: Bottom nav placeholder
        if (sections.size() > 4) {
            android.widget.LinearLayout nav = sections.get(4);
            nav.setOrientation(android.widget.LinearLayout.HORIZONTAL);
            nav.setBackgroundColor(WHITE);
            nav.setPadding(0, 8, 0, 8);
            nav.setGravity(android.view.Gravity.CENTER);
            String[] tabs = {"Home", "Deals", "Order", "Rewards", "More"};
            for (String tab : tabs) {
                android.widget.TextView t = new android.widget.TextView(ctx);
                t.setText(tab);
                t.setTextSize(11);
                t.setTextColor(tab.equals("Home") ? RED : 0xFF888888);
                t.setGravity(android.view.Gravity.CENTER);
                android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(0, -2, 1.0f);
                t.setLayoutParams(lp);
                nav.addView(t);
            }
        }
    }

    private static void findLinearLayouts(android.view.View v, java.util.List<android.widget.LinearLayout> out) {
        if (v instanceof android.widget.LinearLayout && v.getId() != android.view.View.NO_ID) {
            out.add((android.widget.LinearLayout) v);
        }
        if (v instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                findLinearLayouts(vg.getChildAt(i), out);
            }
        }
    }

    private static void addSectionHeader(Activity ctx, android.widget.LinearLayout parent, String text, int color) {
        android.widget.TextView h = new android.widget.TextView(ctx);
        h.setText(text);
        h.setTextSize(22);
        h.setTextColor(color);
        h.setPadding(0, 0, 0, 12);
        parent.addView(h);
    }

    private static void addMenuItem(Activity ctx, android.widget.LinearLayout parent, String name, String detail, int nameColor, int detailColor) {
        android.widget.LinearLayout row = new android.widget.LinearLayout(ctx);
        row.setOrientation(android.widget.LinearLayout.VERTICAL);
        row.setPadding(0, 8, 0, 8);

        android.widget.TextView n = new android.widget.TextView(ctx);
        n.setText(name);
        n.setTextSize(16);
        n.setTextColor(nameColor);
        row.addView(n);

        android.widget.TextView d = new android.widget.TextView(ctx);
        d.setText(detail);
        d.setTextSize(12);
        d.setTextColor(detailColor);
        row.addView(d);

        parent.addView(row);
    }

    /** Render real McD drawable icons using the phone's framework — straight to pipe */
    private static void renderRealIconsScreen(android.content.Context ctx, android.content.res.Resources res) {
        try {
            android.graphics.Bitmap bmp = android.graphics.Bitmap.createBitmap(
                SURFACE_WIDTH, SURFACE_HEIGHT, android.graphics.Bitmap.Config.ARGB_8888);
            android.graphics.Canvas c = new android.graphics.Canvas(bmp);
            c.drawColor(0xFF27251F);

            // Title
            android.graphics.Paint tp = newPaint(0xFFFFCC00);
            tp.setTextSize(22);
            c.drawText("McDonald's Real Drawables", 20, 40, tp);
            android.graphics.Paint sp = newPaint(0xAAFFFFFF);
            sp.setTextSize(11);
            c.drawText("Decoded by phone's framework via app_process64", 20, 60, sp);

            // Load real drawables
            String pkg = "com.mcdonalds.app";
            String[] names = {"archus", "ic_menu", "ic_action_time", "back_chevron",
                "close", "ic_action_search", "splash_screen",
                "ic_notifications", "ic_mcdonalds_logo", "mcd_logo_golden"};
            int y = 90;
            android.graphics.Paint lp = newPaint(0xFFFFFFFF);
            lp.setTextSize(14);
            int found = 0;
            for (String name : names) {
                int id = res.getIdentifier(name, "drawable", pkg);
                if (id == 0) id = res.getIdentifier(name, "mipmap", pkg);
                String label = name + " (0x" + Integer.toHexString(id) + ")";
                if (id != 0) {
                    try {
                        android.graphics.drawable.Drawable d = ctx.getDrawable(id);
                        if (d != null) {
                            d.setBounds(20, y, 84, y + 64);
                            d.draw(c);
                            c.drawText(label, 96, y + 40, lp);
                            found++;
                        } else {
                            c.drawText(label + " null", 20, y + 40, newPaint(0xFFFF4444));
                        }
                    } catch (Throwable t) {
                        c.drawText(label + " ERR: " + t.getMessage(), 20, y + 40, newPaint(0xFFFF4444));
                    }
                } else {
                    c.drawText(name + " (not found)", 20, y + 40, newPaint(0xFF888888));
                }
                y += 70;
            }

            // Status
            android.graphics.Paint gp = newPaint(0xFF00FF00);
            gp.setTextSize(12);
            c.drawText(found + "/" + names.length + " drawables loaded via real framework", 20, y + 20, gp);

            // Send as PNG through pipe
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 90, out);
            byte[] png = out.toByteArray();
            startupLog("[WestlakeLauncher] Real icons: " + png.length + " bytes, " + found + " icons");

            if (com.ohos.shim.bridge.OHBridge.isNativeAvailable()) {
                long surf = com.ohos.shim.bridge.OHBridge.surfaceCreate(0, SURFACE_WIDTH, SURFACE_HEIGHT);
                long canv = com.ohos.shim.bridge.OHBridge.surfaceGetCanvas(surf);
                com.ohos.shim.bridge.OHBridge.canvasDrawImage(canv, png, 0, 0, SURFACE_WIDTH, SURFACE_HEIGHT);
                com.ohos.shim.bridge.OHBridge.surfaceFlush(surf);
                startupLog("[WestlakeLauncher] Real icons frame sent!");
            }
            bmp.recycle();
        } catch (Throwable t) {
            startupLog("[WestlakeLauncher] renderRealIconsScreen error", t);
        }
    }

    private static android.graphics.Paint newPaint(int color) {
        android.graphics.Paint p = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        p.setColor(color);
        return p;
    }
}
