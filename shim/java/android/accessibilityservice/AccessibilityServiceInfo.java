package android.accessibilityservice;

/**
 * Android-compatible AccessibilityServiceInfo shim.
 * Describes an accessibility service's capabilities and preferences.
 */
public class AccessibilityServiceInfo {

    // ── Feedback type constants ─────────────────────────────────────────────
    public static final int FEEDBACK_SPOKEN   = 0x00000001;
    public static final int FEEDBACK_HAPTIC   = 0x00000002;
    public static final int FEEDBACK_AUDIBLE  = 0x00000004;
    public static final int FEEDBACK_VISUAL   = 0x00000008;
    public static final int FEEDBACK_GENERIC  = 0x00000010;
    public static final int FEEDBACK_BRAILLE  = 0x00000020;
    /** Matches all feedback types. */
    public static final int FEEDBACK_ALL_MASK = 0xFFFFFFFF;

    // ── Flag constants ──────────────────────────────────────────────────────
    public static final int FLAG_REQUEST_TOUCH_EXPLORATION_MODE       = 0x00000004;
    public static final int FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY   = 0x00000008;
    public static final int FLAG_REQUEST_FILTER_KEY_EVENTS            = 0x00000020;
    public static final int FLAG_RETRIEVE_INTERACTIVE_WINDOWS         = 0x00000040;
    public static final int FLAG_ENABLE_ACCESSIBILITY_VOLUME          = 0x00000080;
    public static final int FLAG_REQUEST_ACCESSIBILITY_BUTTON         = 0x00000100;

    // ── Public fields (matching real API) ───────────────────────────────────

    /** Bitmask of event types this service wants to receive. */
    public int eventTypes;

    /** Bitmask of feedback types this service provides. */
    public int feedbackType;

    /** Service behavioural flags. */
    public int flags;

    /** Minimum time interval (ms) between two accessibility events of the same type. */
    public long notificationTimeout;

    // ── Optional metadata ───────────────────────────────────────────────────
    private String mId;
    private String mResolveInfo;

    public AccessibilityServiceInfo() {}

    /** Return a string identifier for this service info (package/class). */
    public String getId() { return mId; }

    @Override
    public String toString() {
        return "AccessibilityServiceInfo{"
                + "eventTypes=0x" + Integer.toHexString(eventTypes)
                + ", feedbackType=0x" + Integer.toHexString(feedbackType)
                + ", flags=0x" + Integer.toHexString(flags)
                + ", notificationTimeout=" + notificationTimeout + "}";
    }
}
