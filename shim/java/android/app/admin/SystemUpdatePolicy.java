package android.app.admin;

/**
 * Android-compatible SystemUpdatePolicy shim.
 * Stub — system update policies are stored locally but never enforced.
 */
public class SystemUpdatePolicy {
    public SystemUpdatePolicy(int type, int dummy) {}
    public SystemUpdatePolicy() {}

    // --- Policy type constants ---
    public static final int TYPE_INSTALL_AUTOMATIC = 1;
    public static final int TYPE_INSTALL_WINDOWED  = 2;
    public static final int TYPE_POSTPONE          = 3;

    private int policyType;
    private int windowStart;
    private int windowEnd;

    private SystemUpdatePolicy(int policyType, int windowStart, int windowEnd) {
        this.policyType = policyType;
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
    }

    // -------------------------------------------------------------------------
    // Factory methods
    // -------------------------------------------------------------------------

    /** Create a policy that installs updates automatically as soon as available. */
    public static SystemUpdatePolicy createAutomaticInstallPolicy() {
        return new SystemUpdatePolicy(TYPE_INSTALL_AUTOMATIC, 0);
    }

    /**
     * Create a policy that installs updates only within a daily maintenance window.
     *
     * @param startTime start of the window in minutes after midnight (0..1440)
     * @param endTime   end of the window in minutes after midnight (0..1440)
     */
    public static SystemUpdatePolicy createWindowedInstallPolicy(int startTime, int endTime) {
        return new SystemUpdatePolicy(TYPE_INSTALL_WINDOWED, startTime, endTime);
    }

    /** Create a policy that postpones installation for 30 days. */
    public static SystemUpdatePolicy createPostponeInstallPolicy() {
        return new SystemUpdatePolicy(TYPE_POSTPONE, 0);
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    /** Returns the policy type (one of TYPE_INSTALL_*). */
    public int getPolicyType() {
        return policyType;
    }

    /** Returns the start of the maintenance window in minutes after midnight. */
    public int getInstallWindowStart() {
        return windowStart;
    }

    /** Returns the end of the maintenance window in minutes after midnight. */
    public int getInstallWindowEnd() {
        return windowEnd;
    }

    @Override
    public String toString() {
        return "SystemUpdatePolicy{type=" + policyType
                + ", windowStart=" + windowStart
                + ", windowEnd=" + windowEnd + "}";
    }
}
