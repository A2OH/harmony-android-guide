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
            if ("V".equals(level) || "D".equals(level))
                c.getMethod("logDebug", String.class, String.class).invoke(null, tag, msg);
            else if ("I".equals(level))
                c.getMethod("logInfo", String.class, String.class).invoke(null, tag, msg);
            else if ("W".equals(level))
                c.getMethod("logWarn", String.class, String.class).invoke(null, tag, msg);
            else
                c.getMethod("logError", String.class, String.class).invoke(null, tag, msg);
        } catch (Throwable t) {
            bridge = null;
            String line = level + "/" + tag + ": " + msg;
            if ("W".equals(level) || "E".equals(level) || "A".equals(level)) {
                System.err.println(line);
            } else {
                System.out.println(line);
            }
        }
    }

    private static int log(String level, String tag, String msg) {
        String line = level + "/" + tag + ": " + msg;
        if (tryBridge()) {
            nativeLog(level, tag, msg);
        } else if ("W".equals(level) || "E".equals(level) || "A".equals(level)) {
            System.err.println(line);
        } else {
            System.out.println(line);
        }
        return line.length();
    }

    public static int v(String tag, String msg) { return log("V", tag, msg); }
    public static int v(String tag, String msg, Throwable tr) { return log("V", tag, msg + '\n' + getStackTraceString(tr)); }
    public static int d(String tag, String msg) { return log("D", tag, msg); }
    public static int d(String tag, String msg, Throwable tr) { return log("D", tag, msg + '\n' + getStackTraceString(tr)); }
    public static int i(String tag, String msg) { return log("I", tag, msg); }
    public static int i(String tag, String msg, Throwable tr) { return log("I", tag, msg + '\n' + getStackTraceString(tr)); }
    public static int w(String tag, String msg) { return log("W", tag, msg); }
    public static int w(String tag, String msg, Throwable tr) { return log("W", tag, msg + '\n' + getStackTraceString(tr)); }
    public static int w(String tag, Throwable tr) { return log("W", tag, getStackTraceString(tr)); }
    public static int e(String tag, String msg) { return log("E", tag, msg); }
    public static int e(String tag, String msg, Throwable tr) { return log("E", tag, msg + '\n' + getStackTraceString(tr)); }
    public static int wtf(String tag, String msg) { return log("E", tag, "WTF: " + msg); }
    public static int wtf(String tag, Throwable tr) { return log("E", tag, "WTF: " + getStackTraceString(tr)); }
    public static int wtf(String tag, String msg, Throwable tr) { return log("E", tag, "WTF: " + msg + '\n' + getStackTraceString(tr)); }

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
            case VERBOSE: return log("V", tag, msg);
            case DEBUG:   return log("D", tag, msg);
            case INFO:    return log("I", tag, msg);
            case WARN:    return log("W", tag, msg);
            case ERROR:   return log("E", tag, msg);
            case ASSERT:  return log("A", tag, msg);
            default:      return log("V", tag, msg);
        }
    }
}
