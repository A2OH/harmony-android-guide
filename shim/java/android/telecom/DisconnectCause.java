package android.telecom;

/**
 * Android-compatible DisconnectCause shim. Stub implementation for mock testing.
 */
public class DisconnectCause {

    /** Disconnected because of an error. */
    public static final int ERROR = 0;
    /** Disconnected by the local side. */
    public static final int LOCAL = 1;
    /** Disconnected by the remote side. */
    public static final int REMOTE = 2;
    /** Disconnected because it was canceled. */
    public static final int CANCELED = 3;
    /** Disconnected because it was missed (not answered). */
    public static final int MISSED = 4;
    /** Disconnected because it was rejected. */
    public static final int REJECTED = 5;
    /** Disconnected because the remote side was busy. */
    public static final int BUSY = 6;
    /** Disconnected for a restricted reason. */
    public static final int RESTRICTED = 7;
    /** Disconnected for an unspecified other reason. */
    public static final int OTHER = 8;
    /** Disconnected for an unknown reason. */
    public static final int UNKNOWN = 9;

    private final int mCode;
    private final CharSequence mLabel;
    private final CharSequence mDescription;
    private final String mReason;

    /**
     * Constructs a DisconnectCause with a code only.
     */
    public DisconnectCause(int code) {
        this(code, null, null, null);
    }

    /**
     * Constructs a DisconnectCause with full detail.
     */
    public DisconnectCause(int code, CharSequence label, CharSequence description, String reason) {
        mCode = code;
        mLabel = label;
        mDescription = description;
        mReason = reason;
    }

    /** Returns the code identifying the disconnect cause. */
    public int getCode() {
        return mCode;
    }

    /** Returns a human-readable label for the disconnect cause, or null. */
    public CharSequence getLabel() {
        return mLabel;
    }

    /** Returns a longer human-readable description, or null. */
    public CharSequence getDescription() {
        return mDescription;
    }

    /** Returns the reason string (machine-readable), or null. */
    public String getReason() {
        return mReason;
    }

    @Override
    public String toString() {
        return "DisconnectCause{code=" + mCode + ", label=" + mLabel
                + ", reason=" + mReason + "}";
    }
}
