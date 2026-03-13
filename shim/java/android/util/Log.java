package android.util;

/**
 * Shim: android.util.Log → stdout fallback (standalone) or OHBridge (when available).
 * Safe to use without OHBridge loaded — no static dependency on OHBridge class.
 */
public final class Log {
    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;
    public static final int ASSERT = 7;

    private static Object bridge; // lazy-checked

    private Log() {}

    private static boolean tryBridge() {
        if (bridge != null) return true;
        try {
            Class<?> c = Class.forName("com.ohos.shim.bridge.OHBridge");
            if ((Boolean) c.getMethod("isNativeAvailable").invoke(null)) {
                bridge = c;
                return true;
            }
        } catch (Throwable t) { /* not available */ }
        return false;
    }

    private static void nativeLog(String level, String tag, String msg) {
        try {
            Class<?> c = (Class<?>) bridge;
            if ("D".equals(level)) c.getMethod("logDebug", String.class, String.class).invoke(null, tag, msg);
            else if ("I".equals(level)) c.getMethod("logInfo", String.class, String.class).invoke(null, tag, msg);
            else if ("W".equals(level)) c.getMethod("logWarn", String.class, String.class).invoke(null, tag, msg);
            else c.getMethod("logError", String.class, String.class).invoke(null, tag, msg);
        } catch (Throwable t) {
            bridge = null;
            System.out.println(level + "/" + tag + ": " + msg);
        }
    }

    private static void log(String level, String tag, String msg) {
        if (tryBridge()) {
            nativeLog(level, tag, msg);
        } else {
            System.out.println(level + "/" + tag + ": " + msg);
        }
    }

    public static int v(String tag, String msg) { log("D", tag, msg); return 0; }
    public static int v(String tag, String msg, Throwable tr) { log("D", tag, msg + '\n' + getStackTraceString(tr)); return 0; }
    public static int d(String tag, String msg) { log("D", tag, msg); return 0; }
    public static int d(String tag, String msg, Throwable tr) { log("D", tag, msg + '\n' + getStackTraceString(tr)); return 0; }
    public static int i(String tag, String msg) { log("I", tag, msg); return 0; }
    public static int i(String tag, String msg, Throwable tr) { log("I", tag, msg + '\n' + getStackTraceString(tr)); return 0; }
    public static int w(String tag, String msg) { log("W", tag, msg); return 0; }
    public static int w(String tag, String msg, Throwable tr) { log("W", tag, msg + '\n' + getStackTraceString(tr)); return 0; }
    public static int w(String tag, Throwable tr) { log("W", tag, getStackTraceString(tr)); return 0; }
    public static int e(String tag, String msg) { log("E", tag, msg); return 0; }
    public static int e(String tag, String msg, Throwable tr) { log("E", tag, msg + '\n' + getStackTraceString(tr)); return 0; }
    public static int wtf(String tag, String msg) { log("E", tag, "WTF: " + msg); return 0; }
    public static int wtf(String tag, Throwable tr) { log("E", tag, "WTF: " + getStackTraceString(tr)); return 0; }
    public static int wtf(String tag, String msg, Throwable tr) { log("E", tag, "WTF: " + msg + '\n' + getStackTraceString(tr)); return 0; }

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
            case DEBUG:   log("D", tag, msg); break;
            case INFO:    log("I", tag, msg); break;
            case WARN:    log("W", tag, msg); break;
            case ERROR:
            case ASSERT:  log("E", tag, msg); break;
            default:      log("D", tag, msg); break;
        }
        return 0;
    }
}
