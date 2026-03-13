package android.os;
import android.util.Size;
import android.util.Size;
import java.util.Set;

/**
 * Android-compatible Debug shim. Stub for debugging / profiling utilities.
 */
public final class Debug {

    private Debug() {}

    // ---- MemoryInfo ----
    public static class MemoryInfo {
        /** Proportional Set Size in kB */
        public int  dalvikPss;
        public int  nativePss;
        public int  otherPss;
        public int  dalvikPrivateDirty;
        public int  nativePrivateDirty;
        public int  otherPrivateDirty;
        public int  dalvikSharedDirty;
        public int  nativeSharedDirty;
        public int  otherSharedDirty;

        public int getTotalPss()          { return dalvikPss + nativePss + otherPss; }
        public int getTotalPrivateDirty() { return dalvikPrivateDirty + nativePrivateDirty + otherPrivateDirty; }
        public int getTotalSharedDirty()  { return dalvikSharedDirty + nativeSharedDirty + otherSharedDirty; }
    }

    // ---- Memory queries ----
    public static void getMemoryInfo(MemoryInfo memoryInfo) {
        // no-op stub; leave all fields at 0
    }

    public static long getNativeHeapSize() {
        return Runtime.getRuntime().totalMemory();
    }

    public static long getNativeHeapAllocatedSize() {
        long total = Runtime.getRuntime().totalMemory();
        long free  = Runtime.getRuntime().freeMemory();
        return total - free;
    }

    public static long getNativeHeapFreeSize() {
        return Runtime.getRuntime().freeMemory();
    }

    // ---- Method tracing ----
    public static void startMethodTracing() {
        System.out.println("[Debug] startMethodTracing");
    }

    public static void startMethodTracing(String traceName) {
        System.out.println("[Debug] startMethodTracing: " + traceName);
    }

    public static void startMethodTracing(String traceName, int bufferSize) {
        System.out.println("[Debug] startMethodTracing: " + traceName + " bufferSize=" + bufferSize);
    }

    public static void startMethodTracingSampling(String traceName, int bufferSize, int intervalUs) {
        System.out.println("[Debug] startMethodTracingSampling: " + traceName);
    }

    public static void stopMethodTracing() {
        System.out.println("[Debug] stopMethodTracing");
    }

    // ---- Debugger ----
    public static boolean isDebuggerConnected() {
        return false;
    }

    public static boolean waitingForDebugger() {
        return false;
    }

    public static void waitForDebugger() {
        // no-op
    }

    // ---- Thread / stack dumping ----
    public static String getCallers(int depth) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        StringBuilder sb = new StringBuilder();
        for (int i = 2; i < Math.min(trace.length, depth + 2); i++) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(trace[i].getMethodName()).append(':').append(trace[i].getLineNumber());
        }
        return sb.toString();
    }
}
