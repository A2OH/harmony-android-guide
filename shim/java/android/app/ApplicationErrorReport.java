package android.app;
import android.os.Build;
import android.os.Build;

/**
 * Android-compatible ApplicationErrorReport shim.
 * Describes an error that occurred in an application — used by the platform
 * to report ANR, crashes, battery-drain and running-process events.
 *
 * In the shim layer all reporting methods are no-ops; the class provides the
 * public API surface so that code that constructs or inspects error reports
 * compiles without modification.
 */
public class ApplicationErrorReport {

    // ── Error type constants ──────────────────────────────────────────────────

    /** No error. */
    public static final int TYPE_NONE = 0;

    /** An unhandled exception (crash). */
    public static final int TYPE_CRASH = 1;

    /** Application Not Responding. */
    public static final int TYPE_ANR = 2;

    /** Excessive battery drain. */
    public static final int TYPE_BATTERY = 3;

    /** A running process report. */
    public static final int TYPE_RUNNING_SERVICE = 5;

    // ── Fields ───────────────────────────────────────────────────────────────

    /** One of the {@code TYPE_*} constants above. */
    public int type;

    /** Package name of the application that reported the error. */
    public String packageName;

    /** Package name of the installer of the application. */
    public String installerPackageName;

    /** Name of the process in which the error occurred. */
    public String processName;

    /** Wall-clock time at which the error was reported (ms since epoch). */
    public long time;

    /**
     * Whether the application is a system application.
     * {@code true} if the app has the {@code android.Manifest.permission.SYSTEM} flag.
     */
    public boolean systemApp;

    /** Details about a crash, populated when {@link #type} == {@link #TYPE_CRASH}. */
    public CrashInfo crashInfo;

    // ── Constructor ──────────────────────────────────────────────────────────

    public ApplicationErrorReport() {}

    // ── Inner classes ────────────────────────────────────────────────────────

    /**
     * Describes a crash that occurred in an application.
     */
    public static class CrashInfo {

        /** Class name of the exception that caused the crash. */
        public String exceptionClassName;

        /** Human-readable message associated with the exception. */
        public String exceptionMessage;

        /** Source file name in which the exception was thrown. */
        public String throwFileName;

        /** Class name from which the exception was thrown. */
        public String throwClassName;

        /** Method name from which the exception was thrown. */
        public String throwMethodName;

        /** Line number at which the exception was thrown. */
        public int throwLineNumber;

        /** Full stack trace of the crash. */
        public String stackTrace;

        public CrashInfo() {}

        /**
         * Convenience constructor that populates all fields from a {@link Throwable}.
         *
         * @param tr the throwable that caused the crash
         */
        public CrashInfo(Throwable tr) {
            if (tr == null) return;
            exceptionClassName = tr.getClass().getName();
            exceptionMessage   = tr.getMessage();

            StackTraceElement[] frames = tr.getStackTrace();
            if (frames != null && frames.length > 0) {
                StackTraceElement top = frames[0];
                throwFileName  = top.getFileName();
                throwClassName = top.getClassName();
                throwMethodName = top.getMethodName();
                throwLineNumber = top.getLineNumber();
            }

            // Build a simple stack-trace string
            StringBuilder sb = new StringBuilder();
            sb.append(tr.toString()).append('\n');
            if (frames != null) {
                for (StackTraceElement frame : frames) {
                    sb.append("\tat ").append(frame.toString()).append('\n');
                }
            }
            stackTrace = sb.toString();
        }
    }

    /**
     * Describes a battery-drain event. Populated when
     * {@link #type} == {@link #TYPE_BATTERY}.
     */
    public static class BatteryInfo {

        /** Usec used while screen was on. */
        public long usageTimeMs;

        /** Application-reported duration. */
        public long durationMicroSeconds;

        /** Whether the application is still running. */
        public boolean interactive;

        /** Checkin-formatted battery usage statistics. */
        public String usageDetails;

        /** Description of the battery checkin data. */
        public String checkinDetails;

        public BatteryInfo() {}
    }

    /**
     * Describes an ANR event. Populated when {@link #type} == {@link #TYPE_ANR}.
     */
    public static class AnrInfo {

        /** Activity name. */
        public String activity;

        /** Description of the cause of the ANR. */
        public String cause;

        /** Detailed information about the ANR. */
        public String info;

        public AnrInfo() {}
    }

    /**
     * Describes a running-service event. Populated when
     * {@link #type} == {@link #TYPE_RUNNING_SERVICE}.
     */
    public static class RunningServiceInfo {

        /** Duration the service has been running (ms). */
        public long durationMillis;

        /** Short message describing the running service. */
        public String shortMsg;

        public RunningServiceInfo() {}
    }

    /** Parcelable crash info passed by IActivityManager.handleApplicationCrash.
     *  Stub for compile-time only; framework.jar provides the real class. */
    public static class ParcelableCrashInfo {
        public ParcelableCrashInfo() {}
    }
}
