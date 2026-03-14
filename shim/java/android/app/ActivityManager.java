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

    public static class RunningServiceInfo implements android.os.Parcelable {
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
        /** Bit flags for {@link #flags} field. */
        public static final int FLAG_STARTED = 1;
        /** Indicates this service is a foreground service. */
        public static final int FLAG_FOREGROUND = 2;
        /** Indicates this service is a system process. */
        public static final int FLAG_SYSTEM_PROCESS = 4;
        /** Indicates this service is a persistent process. */
        public static final int FLAG_PERSISTENT_PROCESS = 8;
        /** Combination of flags describing the running service. */
        public int flags;
        /** The package that is the client of this service. */
        public String clientPackage;
        /** The label to use for this service's client. */
        public int clientLabel;

        public RunningServiceInfo() {
            // Sensible defaults
            this.pid = android.os.Process.myPid();
            this.uid = android.os.Process.myUid();
            this.process = "";
            this.foreground = false;
            this.started = false;
            this.clientCount = 0;
            this.crashCount = 0;
            this.activeSince = 0;
            this.lastActivityTime = 0;
            this.restarting = 0;
            this.flags = 0;
            this.clientPackage = null;
            this.clientLabel = 0;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(android.os.Parcel dest, int flags) {
            // Write ComponentName as two strings (package, class)
            if (service != null) {
                dest.writeInt(1);
                dest.writeString(service.getPackageName());
                dest.writeString(service.getClassName());
            } else {
                dest.writeInt(0);
            }
            dest.writeInt(pid);
            dest.writeInt(uid);
            dest.writeString(process);
            dest.writeInt(foreground ? 1 : 0);
            dest.writeLong(activeSince);
            dest.writeInt(started ? 1 : 0);
            dest.writeInt(clientCount);
            dest.writeInt(crashCount);
            dest.writeLong(lastActivityTime);
            dest.writeLong(restarting);
            dest.writeInt(this.flags);
            dest.writeString(clientPackage);
            dest.writeInt(clientLabel);
        }

        public static final android.os.Parcelable.Creator<RunningServiceInfo> CREATOR =
            new android.os.Parcelable.Creator<RunningServiceInfo>() {
                @Override
                public RunningServiceInfo createFromParcel(android.os.Parcel source) {
                    RunningServiceInfo info = new RunningServiceInfo();
                    int hasService = source.readInt();
                    if (hasService != 0) {
                        String pkg = source.readString();
                        String cls = source.readString();
                        info.service = new android.content.ComponentName(pkg, cls);
                    }
                    info.pid = source.readInt();
                    info.uid = source.readInt();
                    info.process = source.readString();
                    info.foreground = source.readInt() != 0;
                    info.activeSince = source.readLong();
                    info.started = source.readInt() != 0;
                    info.clientCount = source.readInt();
                    info.crashCount = source.readInt();
                    info.lastActivityTime = source.readLong();
                    info.restarting = source.readLong();
                    info.flags = source.readInt();
                    info.clientPackage = source.readString();
                    info.clientLabel = source.readInt();
                    return info;
                }

                @Override
                public RunningServiceInfo[] newArray(int size) {
                    return new RunningServiceInfo[size];
                }
            };

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("RunningServiceInfo{");
            if (service != null) {
                sb.append("service=").append(service.flattenToShortString());
            }
            sb.append(", pid=").append(pid);
            sb.append(", uid=").append(uid);
            if (process != null && !process.isEmpty()) {
                sb.append(", process=").append(process);
            }
            if (foreground) sb.append(", foreground");
            if (started) sb.append(", started");
            if (clientCount > 0) sb.append(", clients=").append(clientCount);
            if (crashCount > 0) sb.append(", crashes=").append(crashCount);
            sb.append('}');
            return sb.toString();
        }
    }

    // -------------------------------------------------------------------------
    // ProcessErrorStateInfo inner class
    // -------------------------------------------------------------------------

    public static class ProcessErrorStateInfo {
        /** Condition constants. */
        public static final int NO_ERROR = 0;
        public static final int CRASHED = 1;
        public static final int NOT_RESPONDING = 2;

        /** The condition that the process is in. */
        public int condition;
        /** The process name in which the crash or error occurred. */
        public String processName;
        /** The pid of the process in error. */
        public int pid;
        /** The uid of the process in error. */
        public int uid;
        /** The tag of the crash (activity, service, etc). */
        public String tag;
        /** A short message describing the error condition. */
        public String shortMsg;
        /** A long message describing the error condition. */
        public String longMsg;
        /** The stack trace string. */
        public String stackTrace;

        public ProcessErrorStateInfo() {
            this.condition = NO_ERROR;
        }
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
