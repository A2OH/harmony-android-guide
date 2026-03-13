package android.view.accessibility;

import java.util.List;
import java.util.Set;

/**
 * Android-compatible AccessibilityEvent shim.
 * Extends AccessibilityRecord as on the real platform.
 */
public class AccessibilityEvent extends AccessibilityRecord {

    // ── Event type constants ────────────────────────────────────────────────
    public static final int TYPE_VIEW_CLICKED              = 0x00000001;
    public static final int TYPE_VIEW_FOCUSED              = 0x00000008;
    public static final int TYPE_VIEW_TEXT_CHANGED         = 0x00000010;
    public static final int TYPE_WINDOW_STATE_CHANGED      = 0x00000020;
    public static final int TYPE_NOTIFICATION_STATE_CHANGED = 0x00000040;
    public static final int TYPE_VIEW_SCROLLED             = 0x00001000;

    private int mEventType;
    private CharSequence mPackageName;
    private long mEventTime;

    protected AccessibilityEvent() {}

    // ── Factory methods ─────────────────────────────────────────────────────

    /**
     * Obtain a recycled or new AccessibilityEvent.
     */
    public static AccessibilityEvent obtain() {
        return new AccessibilityEvent();
    }

    /**
     * Obtain a recycled or new AccessibilityEvent pre-populated with the
     * given event type.
     */
    public static AccessibilityEvent obtain(int eventType) {
        AccessibilityEvent event = new AccessibilityEvent();
        event.mEventType = eventType;
        event.mEventTime = System.currentTimeMillis();
        return event;
    }

    /**
     * Return this event to the recycle pool. No-op in this shim.
     */
    public void recycle() {
        // no-op: no pool managed in the shim
    }

    // ── Event type ──────────────────────────────────────────────────────────

    /** Set the event type (one of the TYPE_* constants). */
    public void setEventType(int eventType) {
        mEventType = eventType;
    }

    /** Return the event type. */
    public int getEventType() {
        return mEventType;
    }

    // ── Package name ────────────────────────────────────────────────────────

    /** Set the package name of the event source. */
    public void setPackageName(CharSequence packageName) {
        mPackageName = packageName;
    }

    /** Return the package name. */
    public CharSequence getPackageName() {
        return mPackageName;
    }

    // ── Event time ──────────────────────────────────────────────────────────

    /** Return the event time in milliseconds since boot. */
    public long getEventTime() {
        return mEventTime;
    }

    /** Set the event time. */
    public void setEventTime(long eventTime) {
        mEventTime = eventTime;
    }

    @Override
    public String toString() {
        return "AccessibilityEvent{type=0x" + Integer.toHexString(mEventType)
                + ", pkg=" + mPackageName + "}";
    }
}
