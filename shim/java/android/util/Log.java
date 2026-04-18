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
    private static java.lang.reflect.Method westlakeNativeLog;
    private static boolean westlakeNativeLogResolved;

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
        }
    }

    private static boolean shouldFallbackToWestlake(String tag) {
        if (tag == null || tag.isEmpty()) {
            return false;
        }
        return tag.startsWith("Westlake")
                || "AppComponentFactory".equals(tag)
                || "ComponentActivity".equals(tag)
                || "SavedStateRegistry".equals(tag);
    }

    private static void westlakeLog(String line) {
        if (!shouldFallbackToWestlake(extractTag(line))) {
            return;
        }
        try {
            if (!westlakeNativeLogResolved) {
                westlakeNativeLogResolved = true;
                Class<?> launcher = Class.forName("com.westlake.engine.WestlakeLauncher");
                westlakeNativeLog = launcher.getDeclaredMethod("nativeLog", String.class);
                westlakeNativeLog.setAccessible(true);
            }
            if (westlakeNativeLog != null) {
                westlakeNativeLog.invoke(null, "[shim-log] " + line);
            }
        } catch (Throwable ignored) {
            westlakeNativeLog = null;
        }
    }

    private static String extractTag(String line) {
        if (line == null) {
            return null;
        }
        int slash = line.indexOf('/');
        int colon = line.indexOf(':');
        if (slash < 0 || colon <= slash + 1) {
            return null;
        }
        return line.substring(slash + 1, colon).trim();
    }

    private static int log(String level, String tag, String msg) {
        String line = level + "/" + tag + ": " + msg;
        if (tryBridge()) {
            nativeLog(level, tag, msg);
        } else {
            westlakeLog(line);
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
        StringBuilder sb = new StringBuilder();
        Throwable cur = tr;
        for (int depth = 0; cur != null && depth < 4; depth++) {
            if (depth > 0) {
                sb.append(" <- ");
            }
            try {
                sb.append(cur.getClass().getName());
            } catch (Throwable ignored) {
                sb.append("java.lang.Throwable");
            }
            try {
                String msg = cur.getMessage();
                if (msg != null && !msg.isEmpty()) {
                    sb.append(": ").append(msg);
                }
            } catch (Throwable ignored) {
            }
            try {
                Throwable next = cur.getCause();
                if (next == cur) {
                    break;
                }
                cur = next;
            } catch (Throwable ignored) {
                break;
            }
        }
        return sb.toString();
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
