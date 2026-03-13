package android.app.admin;

/**
 * Android-compatible SecurityLog shim. Stub — all events return defaults.
 */
public class SecurityLog {

    public static final int TAG_ADB_SHELL_INTERACTIVE = 210001;
    public static final int TAG_ADB_SHELL_CMD = 210002;
    public static final int TAG_SYNC_RECV_FILE = 210003;
    public static final int TAG_SYNC_SEND_FILE = 210004;
    public static final int TAG_APP_PROCESS_START = 210005;
    public static final int TAG_KEYGUARD_DISMISSED = 210006;
    public static final int TAG_KEYGUARD_DISMISS_AUTH_ATTEMPT = 210007;
    public static final int TAG_KEYGUARD_SECURED = 210008;

    public static class SecurityEvent {
        public int getTag() { return 0; }
        public long getTimeNanos() { return 0L; }
        public Object getData() { return null; }
        public int getLogLevel() { return 0; }
    }
}
