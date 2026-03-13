package android.app;

import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible ActivityManager shim. Returns mock/stub process and memory info.
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
        public static final int IMPORTANCE_FOREGROUND         = 100;
        public static final int IMPORTANCE_FOREGROUND_SERVICE = 125;
        public static final int IMPORTANCE_TOP_SLEEPING       = 150;
        public static final int IMPORTANCE_VISIBLE            = 200;
        public static final int IMPORTANCE_PERCEPTIBLE        = 230;
        public static final int IMPORTANCE_CANT_SAVE_STATE    = 270;
        public static final int IMPORTANCE_SERVICE            = 300;
        public static final int IMPORTANCE_CACHED             = 400;
        public static final int IMPORTANCE_GONE               = 1000;

        public String processName;
        public int pid;
        public int uid;
        public String[] pkgList;
        public int importance;
        public int lru;

        public RunningAppProcessInfo() {}

        public RunningAppProcessInfo(String processName, int pid, String[] pkgList) {
            this.processName = processName;
            this.pid = pid;
            this.pkgList = pkgList;
            this.importance = IMPORTANCE_FOREGROUND;
        }
    }

    // -------------------------------------------------------------------------
    // ActivityManager API
    // -------------------------------------------------------------------------

    public void getMemoryInfo(MemoryInfo outInfo) {
        if (outInfo == null) return;
        MemoryInfo mock = new MemoryInfo();
        outInfo.availMem  = mock.availMem;
        outInfo.totalMem  = mock.totalMem;
        outInfo.threshold = mock.threshold;
        outInfo.lowMemory = mock.lowMemory;
    }

    public List<RunningAppProcessInfo> getRunningAppProcesses() {
        List<RunningAppProcessInfo> list = new ArrayList<>();
        // Return a single mock entry representing the current process
        RunningAppProcessInfo self = new RunningAppProcessInfo(
                "com.example.app",
                android.os.Process.myPid(),
                new String[]{"com.example.app"});
        list.add(self);
        return list;
    }

    public boolean isLowRamDevice() {
        return false; // stub: assume normal device
    }

    /**
     * Returns the memory class of the current device in megabytes.
     * Typical values: 64, 96, 128, 192, 256, 384, 512.
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

    public void killBackgroundProcesses(String packageName) {
        System.out.println("[ActivityManager] killBackgroundProcesses: " + packageName);
    }
}
