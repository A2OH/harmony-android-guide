package android.os;

/**
 * Android-compatible Process shim. Uses Java Runtime for process info.
 */
public class Process {
    public static final int FIRST_APPLICATION_UID = 10000;
    public static final int LAST_APPLICATION_UID = 19999;
    public static final int SYSTEM_UID = 1000;
    public static final int PHONE_UID = 1001;
    public static final int BLUETOOTH_UID = 1002;
    public static final int WIFI_UID = 1010;
    public static final int INVALID_UID = -1;

    public static final int SIGNAL_QUIT = 3;
    public static final int SIGNAL_KILL = 9;
    public static final int SIGNAL_USR1 = 10;

    public static final int THREAD_PRIORITY_DEFAULT = 0;
    public static final int THREAD_PRIORITY_LOWEST = 19;
    public static final int THREAD_PRIORITY_BACKGROUND = 10;
    public static final int THREAD_PRIORITY_FOREGROUND = -2;
    public static final int THREAD_PRIORITY_DISPLAY = -4;
    public static final int THREAD_PRIORITY_URGENT_DISPLAY = -8;
    public static final int THREAD_PRIORITY_AUDIO = -16;
    public static final int THREAD_PRIORITY_URGENT_AUDIO = -19;

    public static int myPid() {
        return (int) ProcessHandle.current().pid();
    }

    public static int myTid() {
        return (int) Thread.currentThread().getId();
    }

    public static int myUid() {
        return FIRST_APPLICATION_UID; // mock app UID
    }

    public static void setThreadPriority(int priority) {
        // Map Android priority to Java priority (rough mapping)
        int javaPriority;
        if (priority <= THREAD_PRIORITY_URGENT_AUDIO) javaPriority = Thread.MAX_PRIORITY;
        else if (priority <= THREAD_PRIORITY_FOREGROUND) javaPriority = Thread.NORM_PRIORITY + 2;
        else if (priority == THREAD_PRIORITY_DEFAULT) javaPriority = Thread.NORM_PRIORITY;
        else javaPriority = Thread.MIN_PRIORITY;
        Thread.currentThread().setPriority(Math.max(Thread.MIN_PRIORITY, Math.min(Thread.MAX_PRIORITY, javaPriority)));
    }

    public static void setThreadPriority(int tid, int priority) {
        // Only support current thread
        if (tid == myTid()) {
            setThreadPriority(priority);
        }
    }

    public static int getThreadPriority(int tid) {
        return THREAD_PRIORITY_DEFAULT; // stub
    }

    public static boolean supportsProcesses() {
        return true;
    }

    public static void killProcess(int pid) {
        ProcessHandle.of(pid).ifPresent(ProcessHandle::destroy);
    }

    public static void sendSignal(int pid, int signal) {
        if (signal == SIGNAL_KILL) {
            killProcess(pid);
        }
    }

    public static long getElapsedCpuTime() {
        return java.lang.management.ManagementFactory.getThreadMXBean()
            .getCurrentThreadCpuTime() / 1_000_000;
    }

    public static boolean isApplicationUid(int uid) {
        return uid >= FIRST_APPLICATION_UID && uid <= LAST_APPLICATION_UID;
    }

    public static boolean isIsolated() {
        return false;
    }
}
