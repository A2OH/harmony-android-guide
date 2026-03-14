package android.app;

import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible ActivityManager shim. Returns mock/stub process and memory info.
 * Tier-C stub: all data is mock; no OpenHarmony bridge required.
 */
public class ActivityManager {

    // -------------------------------------------------------------------------
    // MemoryInfo inner class
    // -------------------------------------------------------------------------

    public static class MemoryInfo {
        /** Available memory in bytes. */
        public long availMem;
        /** Total memory in bytes. */
        public long totalMem;
        /** Threshold (low-memory watermark) in bytes. */
        public long threshold;
        /** Whether the system is in a low-memory state. */
        public boolean lowMemory;

        public MemoryInfo() {
            // Mock values representative of a mid-range OHOS device
            totalMem    = 4L * 1024 * 1024 * 1024; // 4 GB
            availMem    = 1L * 1024 * 1024 * 1024; // 1 GB available
            threshold   = 128L * 1024 * 1024;       // 128 MB low watermark
            lowMemory   = availMem < threshold;
        }
    }

    // -------------------------------------------------------------------------
    // RunningAppProcessInfo inner class
    // -------------------------------------------------------------------------

    public static class RunningAppProcessInfo {
        /** Constant for a foreground process. */
        public static final int IMPORTANCE_FOREGROUND         = 100;
        /** Constant for a foreground service process. */
        public static final int IMPORTANCE_FOREGROUND_SERVICE = 125;
        /** Constant for a top sleeping process. */
        public static final int IMPORTANCE_TOP_SLEEPING       = 150;
        /** Constant for a visible process. */
        public static final int IMPORTANCE_VISIBLE            = 200;
        /** Constant for a perceptible process. */
        public static final int IMPORTANCE_PERCEPTIBLE        = 230;
        /** Constant for a process that cannot save state. */
        public static final int IMPORTANCE_CANT_SAVE_STATE    = 270;
        /** Constant for a service process. */
        public static final int IMPORTANCE_SERVICE            = 300;
        /** Constant for a cached process. */
        public static final int IMPORTANCE_CACHED             = 400;
        /** @deprecated Use {@link #IMPORTANCE_CACHED} instead. */
        public static final int IMPORTANCE_BACKGROUND         = 400;
        /** @deprecated Use {@link #IMPORTANCE_CACHED} instead. */
        public static final int IMPORTANCE_EMPTY              = 500;
        /** Constant for a process that is gone. */
        public static final int IMPORTANCE_GONE               = 1000;

        /** The name of the process. */
        public String processName;
        /** The pid of the process. */
        public int pid;
        /** The uid of the process. */
        public int uid;
        /** All packages running in the process. */
        public String[] pkgList;
        /** The relative importance of this process. */
        public int importance;
        /** An additional ordering for importance among processes of the same level. */
        public int lru;
        /** Last trim level reported to the process. */
        public int lastTrimLevel;
        /** Reason code for importance. */
        public int importanceReasonCode;
        /** Process ID of the reason component. */
        public int importanceReasonPid;

        /** Reason constants. */
        public static final int REASON_UNKNOWN = 0;
        public static final int REASON_PROVIDER = 1;
        public static final int REASON_SERVICE = 2;

        public RunningAppProcessInfo() {}

        public RunningAppProcessInfo(String processName, int pid, String[] pkgList) {
            this.processName = processName;
            this.pid = pid;
            this.pkgList = pkgList;
            this.importance = IMPORTANCE_FOREGROUND;
            this.uid = android.os.Process.myUid();
        }
    }

    // -------------------------------------------------------------------------
    // RunningServiceInfo inner class (commonly used)
    // -------------------------------------------------------------------------

    public static class RunningServiceInfo {
        /** The service component name. */
        public android.content.ComponentName service;
        /** The pid of the process this service runs in. */
        public int pid;
        /** The uid of the process this service runs in. */
        public int uid;
        /** The name of the process this service runs in. */
        public String process;
        /** Whether the service is foreground. */
        public boolean foreground;
        /** Time when the service was first active. */
        public long activeSince;
        /** Set to true if the service has been started. */
        public boolean started;
        /** Number of clients connected to the service. */
        public int clientCount;
        /** Number of times the service's process has crashed. */
        public int crashCount;
        /** Time when service was last active. */
        public long lastActivityTime;
        /** Running duration in milliseconds. */
        public long restarting;

        public RunningServiceInfo() {}
    }

    // -------------------------------------------------------------------------
    // RunningTaskInfo inner class
    // -------------------------------------------------------------------------

    public static class RunningTaskInfo {
        /** The component launched as the first activity in the task. */
        public android.content.ComponentName baseActivity;
        /** The activity component at the top of the history stack. */
        public android.content.ComponentName topActivity;
        /** Number of activities in this task. */
        public int numActivities;
        /** Number of activities that are currently running in this task. */
        public int numRunning;
        /** Unique task id. */
        public int id;

        public RunningTaskInfo() {}
    }

    // -------------------------------------------------------------------------
    // ActivityManager API
    // -------------------------------------------------------------------------

    /**
     * Populates the given MemoryInfo with current memory statistics.
     */
    public void getMemoryInfo(MemoryInfo outInfo) {
        if (outInfo == null) return;
        // Use mock device-level values representative of a mid-range device
        outInfo.totalMem  = 4L * 1024 * 1024 * 1024; // 4 GB device total
        outInfo.availMem  = 1L * 1024 * 1024 * 1024;  // 1 GB available
        outInfo.threshold = 128L * 1024 * 1024;         // 128 MB low watermark
        outInfo.lowMemory = outInfo.availMem < outInfo.threshold;
    }

    /**
     * Returns a list of running application processes. Returns a single entry
     * representing the current process.
     */
    public List<RunningAppProcessInfo> getRunningAppProcesses() {
        List<RunningAppProcessInfo> list = new ArrayList<RunningAppProcessInfo>();
        RunningAppProcessInfo self = new RunningAppProcessInfo(
                "com.example.app",
                android.os.Process.myPid(),
                new String[]{"com.example.app"});
        self.uid = android.os.Process.myUid();
        self.lru = 0;
        self.lastTrimLevel = 0;
        self.importanceReasonCode = RunningAppProcessInfo.REASON_UNKNOWN;
        list.add(self);
        return list;
    }

    /**
     * Returns a list of running services. Returns an empty list (stub).
     */
    public List<RunningServiceInfo> getRunningServices(int maxNum) {
        return new ArrayList<RunningServiceInfo>();
    }

    /**
     * Returns a list of running tasks. Returns an empty list (stub).
     * @deprecated As of API 21.
     */
    public List<RunningTaskInfo> getRunningTasks(int maxNum) {
        return new ArrayList<RunningTaskInfo>();
    }

    /**
     * Returns true if this is a low-RAM device.
     */
    public boolean isLowRamDevice() {
        return false; // stub: assume normal device
    }

    /**
     * Returns the memory class of the current device in megabytes.
     * This is the per-app heap limit for standard apps.
     */
    public int getMemoryClass() {
        return 256; // mock: 256 MB per-app heap limit
    }

    /**
     * Returns the large memory class, used when android:largeHeap="true".
     */
    public int getLargeMemoryClass() {
        return 512;
    }

    /**
     * Requests that the system kill background processes associated with the
     * given package. Stub: logs the request.
     */
    public void killBackgroundProcesses(String packageName) {
        System.out.println("[ActivityManager] killBackgroundProcesses: " + packageName);
    }

    /**
     * Returns the current trim level that the process is at. Stub returns 0
     * (TRIM_MEMORY_COMPLETE not needed).
     */
    public static final int TRIM_MEMORY_COMPLETE        = 80;
    public static final int TRIM_MEMORY_MODERATE        = 60;
    public static final int TRIM_MEMORY_BACKGROUND      = 40;
    public static final int TRIM_MEMORY_UI_HIDDEN       = 20;
    public static final int TRIM_MEMORY_RUNNING_CRITICAL = 15;
    public static final int TRIM_MEMORY_RUNNING_LOW     = 10;
    public static final int TRIM_MEMORY_RUNNING_MODERATE = 5;

    /**
     * Clears the application's user data. Stub: no-op, returns false.
     */
    public boolean clearApplicationUserData() {
        return false;
    }

    /**
     * Returns the app tasks for the calling application. Stub: returns empty list.
     */
    public List<Object> getAppTasks() {
        return new ArrayList<Object>();
    }
}
