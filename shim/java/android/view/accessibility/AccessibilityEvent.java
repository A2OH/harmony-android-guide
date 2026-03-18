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

    // Additional event types
    public static final int TYPE_VIEW_LONG_CLICKED      = 0x00000002;
    public static final int TYPE_VIEW_SELECTED          = 0x00000004;
    public static final int TYPE_VIEW_HOVER_ENTER        = 0x00000080;
    public static final int TYPE_VIEW_HOVER_EXIT         = 0x00000100;
    public static final int TYPE_VIEW_TEXT_SELECTION_CHANGED = 0x00002000;
    public static final int TYPE_VIEW_ACCESSIBILITY_FOCUSED = 0x00008000;
    public static final int TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED = 0x00010000;
    public static final int TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY = 0x00020000;
    public static final int TYPE_ANNOUNCEMENT           = 0x00004000;
    public static final int TYPE_WINDOW_CONTENT_CHANGED  = 0x00000800;
    public static final int TYPE_VIEW_CONTEXT_CLICKED   = 0x00800000;

    // Content change types
    public static final int CONTENT_CHANGE_TYPE_UNDEFINED = 0x00000000;
    public static final int CONTENT_CHANGE_TYPE_SUBTREE = 0x00000001;
    public static final int CONTENT_CHANGE_TYPE_TEXT = 0x00000002;
    public static final int CONTENT_CHANGE_TYPE_CONTENT_DESCRIPTION = 0x00000004;
    public static final int CONTENT_CHANGE_TYPE_STATE_DESCRIPTION = 0x00000040;
    public static final int CONTENT_CHANGE_TYPE_PANE_TITLE = 0x00000008;
    public static final int CONTENT_CHANGE_TYPE_PANE_APPEARED = 0x00000010;
    public static final int CONTENT_CHANGE_TYPE_PANE_DISAPPEARED = 0x00000020;

    private int mEventType;
    private CharSequence mPackageName;
    private long mEventTime;
    private int mAction;
    private int mContentChangeTypes;
    private int mMovementGranularity;
    private int mScrollDeltaX;
    private int mScrollDeltaY;

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

    // --- Additional methods for View.java compilation ---
    public void setAction(int action) { mAction = action; }
    public int getAction() { return mAction; }
    public void setContentChangeTypes(int changeTypes) { mContentChangeTypes = changeTypes; }
    public int getContentChangeTypes() { return mContentChangeTypes; }
    public void setMovementGranularity(int granularity) { mMovementGranularity = granularity; }
    public int getMovementGranularity() { return mMovementGranularity; }
    public void setCurrentItemIndex(int index) {}
    public void setEnabled(boolean enabled) {}
    public void setScrollDeltaX(int dx) { mScrollDeltaX = dx; }
    public void setScrollDeltaY(int dy) { mScrollDeltaY = dy; }
    public int getScrollDeltaX() { return mScrollDeltaX; }
    public int getScrollDeltaY() { return mScrollDeltaY; }

    @Override
    public String toString() {
        return "AccessibilityEvent{type=0x" + Integer.toHexString(mEventType)
                + ", pkg=" + mPackageName + "}";
    }
}
