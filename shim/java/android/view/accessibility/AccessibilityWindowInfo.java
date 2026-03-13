package android.view.accessibility;

import android.graphics.Rect;

/**
 * Shim: android.view.accessibility.AccessibilityWindowInfo
 *
 * Describes a window visible to accessibility services. This shim stores
 * the common fields and exposes no-op factory / lifecycle methods.
 */
public class AccessibilityWindowInfo {

    // ── Window type constants ────────────────────────────────────────────────

    /** The window is an application window. */
    public static final int TYPE_APPLICATION = 1;
    /** The window is an input method window (soft keyboard, etc.). */
    public static final int TYPE_INPUT_METHOD = 2;
    /** The window belongs to an accessibility service itself. */
    public static final int TYPE_ACCESSIBILITY_OVERLAY = 3;
    /** The window is a system-level window (status bar, navigation bar). */
    public static final int TYPE_SYSTEM = 4;
    /** The window is a split-screen divider. */
    public static final int TYPE_SPLIT_SCREEN_DIVIDER = 5;

    // ── State ────────────────────────────────────────────────────────────────

    private int mId = -1;
    private int mType = TYPE_APPLICATION;
    private CharSequence mTitle;
    private final Rect mBoundsInScreen = new Rect();
    private boolean mFocused;
    private boolean mActive;

    protected AccessibilityWindowInfo() {}

    // ── Factory methods ──────────────────────────────────────────────────────

    /** Obtain a new AccessibilityWindowInfo instance. */
    public static AccessibilityWindowInfo obtain() {
        return new AccessibilityWindowInfo();
    }

    /** Return this instance to the recycle pool. No-op in this shim. */
    public void recycle() {
        // no-op
    }

    // ── Accessors ────────────────────────────────────────────────────────────

    /**
     * Return the window type (one of the TYPE_* constants).
     *
     * @return the window type
     */
    public int getType() {
        return mType;
    }

    /** Set the window type. */
    public void setType(int type) {
        mType = type;
    }

    /**
     * Return the title of the window, or null if none is set.
     *
     * @return the window title
     */
    public CharSequence getTitle() {
        return mTitle;
    }

    /** Set the window title. */
    public void setTitle(CharSequence title) {
        mTitle = title;
    }

    /**
     * Return the unique identifier of this window in the current session.
     *
     * @return the window id, or -1 if not yet assigned
     */
    public int getId() {
        return mId;
    }

    /** Set the unique window identifier. */
    public void setId(int id) {
        mId = id;
    }

    /**
     * Populate {@code outBounds} with the bounds of this window in screen
     * coordinates.
     *
     * @param outBounds the Rect to receive the bounds
     */
    public void getBoundsInScreen(Rect outBounds) {
        if (outBounds != null) {
            outBounds.set(mBoundsInScreen);
        }
    }

    /** Set the screen-coordinate bounds of this window. */
    public void setBoundsInScreen(Rect bounds) {
        if (bounds != null) {
            mBoundsInScreen.set(bounds);
        }
    }

    /** Return whether this window has input focus. */
    public boolean isFocused() {
        return mFocused;
    }

    /** Set whether this window has input focus. */
    public void setFocused(boolean focused) {
        mFocused = focused;
    }

    /** Return whether this window is the active (foreground) window. */
    public boolean isActive() {
        return mActive;
    }

    /** Set whether this window is the active window. */
    public void setActive(boolean active) {
        mActive = active;
    }

    @Override
    public String toString() {
        return "AccessibilityWindowInfo{id=" + mId
                + ", type=" + mType
                + ", title=" + mTitle
                + ", bounds=" + mBoundsInScreen + "}";
    }
}
