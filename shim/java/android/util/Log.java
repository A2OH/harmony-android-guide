package android.util;

import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.util.Log → @ohos.hilog
 * Tier 1 — direct mapping, thin wrapper.
 */
public final class Log {
    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;
    public static final int ASSERT = 7;

    private Log() {}

    public static int v(String tag, String msg) {
        OHBridge.logDebug(tag, msg);
        return 0;
    }

    public static int v(String tag, String msg, Throwable tr) {
        OHBridge.logDebug(tag, msg + '\n' + getStackTraceString(tr));
        return 0;
    }

    public static int d(String tag, String msg) {
        OHBridge.logDebug(tag, msg);
        return 0;
    }

    public static int d(String tag, String msg, Throwable tr) {
        OHBridge.logDebug(tag, msg + '\n' + getStackTraceString(tr));
        return 0;
    }

    public static int i(String tag, String msg) {
        OHBridge.logInfo(tag, msg);
        return 0;
    }

    public static int i(String tag, String msg, Throwable tr) {
        OHBridge.logInfo(tag, msg + '\n' + getStackTraceString(tr));
        return 0;
    }

    public static int w(String tag, String msg) {
        OHBridge.logWarn(tag, msg);
        return 0;
    }

    public static int w(String tag, String msg, Throwable tr) {
        OHBridge.logWarn(tag, msg + '\n' + getStackTraceString(tr));
        return 0;
    }

    public static int w(String tag, Throwable tr) {
        OHBridge.logWarn(tag, getStackTraceString(tr));
        return 0;
    }

    public static int e(String tag, String msg) {
        OHBridge.logError(tag, msg);
        return 0;
    }

    public static int e(String tag, String msg, Throwable tr) {
        OHBridge.logError(tag, msg + '\n' + getStackTraceString(tr));
        return 0;
    }

    public static int wtf(String tag, String msg) {
        OHBridge.logError(tag, "WTF: " + msg);
        return 0;
    }

    public static int wtf(String tag, Throwable tr) {
        OHBridge.logError(tag, "WTF: " + getStackTraceString(tr));
        return 0;
    }

    public static int wtf(String tag, String msg, Throwable tr) {
        OHBridge.logError(tag, "WTF: " + msg + '\n' + getStackTraceString(tr));
        return 0;
    }

    public static String getStackTraceString(Throwable tr) {
        if (tr == null) return "";
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        tr.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    public static boolean isLoggable(String tag, int level) {
        return level >= DEBUG;
    }

    public static int println(int priority, String tag, String msg) {
        switch (priority) {
            case VERBOSE:
            case DEBUG:   OHBridge.logDebug(tag, msg); break;
            case INFO:    OHBridge.logInfo(tag, msg); break;
            case WARN:    OHBridge.logWarn(tag, msg); break;
            case ERROR:
            case ASSERT:  OHBridge.logError(tag, msg); break;
            default:      OHBridge.logDebug(tag, msg); break;
        }
        return 0;
    }
}
