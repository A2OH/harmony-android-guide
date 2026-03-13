package android.view.accessibility;

import android.graphics.Rect;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible AccessibilityNodeInfo shim.
 * Describes a node (widget) in the accessibility tree.
 */
public class AccessibilityNodeInfo {

    // ── Fields ──────────────────────────────────────────────────────────────
    private CharSequence mClassName;
    private CharSequence mContentDescription;
    private CharSequence mText;
    private boolean mCheckable;
    private boolean mChecked;
    private boolean mClickable;
    private boolean mEnabled;
    private boolean mFocusable;
    private boolean mFocused;
    private boolean mVisibleToUser;
    private final Rect mBoundsInScreen = new Rect();
    private final List<AccessibilityNodeInfo> mChildren = new ArrayList<>();
    private final List<AccessibilityAction> mActions   = new ArrayList<>();
    private View mSource;

    protected AccessibilityNodeInfo() {}

    // ── Factory methods ─────────────────────────────────────────────────────

    /** Obtain a recycled or new node info. */
    public static AccessibilityNodeInfo obtain() {
        return new AccessibilityNodeInfo();
    }

    /** Obtain a node info initialised from the given View. */
    public static AccessibilityNodeInfo obtain(View source) {
        AccessibilityNodeInfo info = new AccessibilityNodeInfo();
        info.mSource = source;
        if (source != null) {
            info.mContentDescription = source.getContentDescription();
            info.mEnabled    = source.isEnabled();
            info.mFocusable  = source.isFocusable();
            info.mClickable  = source.isClickable();
            info.mChecked    = source.isChecked();
        }
        return info;
    }

    /** Return this node info to the recycle pool. No-op in this shim. */
    public void recycle() {
        // no-op
    }

    // ── Setters / getters ───────────────────────────────────────────────────

    public void setClassName(CharSequence className)             { mClassName = className; }
    public CharSequence getClassName()                           { return mClassName; }

    public void setContentDescription(CharSequence description)  { mContentDescription = description; }
    public CharSequence getContentDescription()                  { return mContentDescription; }

    public void setText(CharSequence text)                       { mText = text; }
    public CharSequence getText()                                { return mText; }

    public void setCheckable(boolean checkable)                  { mCheckable = checkable; }
    public boolean isCheckable()                                 { return mCheckable; }

    public void setChecked(boolean checked)                      { mChecked = checked; }
    public boolean isChecked()                                   { return mChecked; }

    public void setClickable(boolean clickable)                  { mClickable = clickable; }
    public boolean isClickable()                                 { return mClickable; }

    public void setEnabled(boolean enabled)                      { mEnabled = enabled; }
    public boolean isEnabled()                                   { return mEnabled; }

    public void setFocusable(boolean focusable)                  { mFocusable = focusable; }
    public boolean isFocusable()                                 { return mFocusable; }

    public void setFocused(boolean focused)                      { mFocused = focused; }
    public boolean isFocused()                                   { return mFocused; }

    public void setVisibleToUser(boolean visibleToUser)          { mVisibleToUser = visibleToUser; }
    public boolean isVisibleToUser()                             { return mVisibleToUser; }

    /** Copy the rect into the node's screen-bounds. */
    public void setBoundsInScreen(Rect bounds) {
        mBoundsInScreen.set(bounds);
    }

    /** Populate the supplied rect with the node's screen-bounds. */
    public void getBoundsInScreen(Rect outBounds) {
        outBounds.set(mBoundsInScreen);
    }

    /** Add a child node. */
    public void addChild(AccessibilityNodeInfo child) {
        if (child != null) mChildren.add(child);
    }

    /** Convenience: add a child sourced from a View. */
    public void addChild(View child) {
        mChildren.add(obtain(child));
    }

    /** Return the number of children. */
    public int getChildCount() { return mChildren.size(); }

    /** Return the child at position index. */
    public AccessibilityNodeInfo getChild(int index) { return mChildren.get(index); }

    /** Add an AccessibilityAction to this node. */
    public void addAction(AccessibilityAction action) {
        if (action != null) mActions.add(action);
    }

    /** Add an action by its id (wraps it in an AccessibilityAction). */
    public void addAction(int action) {
        mActions.add(new AccessibilityAction(action, null));
    }

    /** Return all actions associated with this node. */
    public List<AccessibilityAction> getActionList() {
        return mActions;
    }

    // ── Inner class: AccessibilityAction ────────────────────────────────────

    /**
     * Represents a possible action on an AccessibilityNodeInfo.
     */
    public static final class AccessibilityAction {

        // Standard action ids (subset matching the real API)
        public static final int ACTION_FOCUS           = 0x00000001;
        public static final int ACTION_CLEAR_FOCUS     = 0x00000002;
        public static final int ACTION_SELECT          = 0x00000004;
        public static final int ACTION_CLEAR_SELECTION = 0x00000008;
        public static final int ACTION_CLICK           = 0x00000010;
        public static final int ACTION_LONG_CLICK      = 0x00000020;
        public static final int ACTION_ACCESSIBILITY_FOCUS       = 0x00000040;
        public static final int ACTION_CLEAR_ACCESSIBILITY_FOCUS = 0x00000080;
        public static final int ACTION_NEXT_AT_MOVEMENT_GRANULARITY = 0x00000100;
        public static final int ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY = 0x00000200;
        public static final int ACTION_NEXT_HTML_ELEMENT     = 0x00000400;
        public static final int ACTION_PREVIOUS_HTML_ELEMENT = 0x00000800;
        public static final int ACTION_SCROLL_FORWARD  = 0x00001000;
        public static final int ACTION_SCROLL_BACKWARD = 0x00002000;
        public static final int ACTION_COPY            = 0x00004000;
        public static final int ACTION_PASTE           = 0x00008000;
        public static final int ACTION_CUT             = 0x00010000;
        public static final int ACTION_SET_SELECTION   = 0x00020000;
        public static final int ACTION_EXPAND          = 0x00040000;
        public static final int ACTION_COLLAPSE        = 0x00080000;
        public static final int ACTION_DISMISS         = 0x00100000;
        public static final int ACTION_SET_TEXT        = 0x00200000;

        // Pre-built singleton instances for common actions
        public static final AccessibilityAction ACTION_CLICK_INSTANCE =
                new AccessibilityAction(ACTION_CLICK, "click");
        public static final AccessibilityAction ACTION_LONG_CLICK_INSTANCE =
                new AccessibilityAction(ACTION_LONG_CLICK, "long click");
        public static final AccessibilityAction ACTION_SCROLL_FORWARD_INSTANCE =
                new AccessibilityAction(ACTION_SCROLL_FORWARD, "scroll forward");
        public static final AccessibilityAction ACTION_SCROLL_BACKWARD_INSTANCE =
                new AccessibilityAction(ACTION_SCROLL_BACKWARD, "scroll backward");

        private final int mId;
        private final CharSequence mLabel;

        public AccessibilityAction(int actionId, CharSequence label) {
            mId    = actionId;
            mLabel = label;
        }

        public int getId()           { return mId; }
        public CharSequence getLabel() { return mLabel; }

        @Override
        public String toString() {
            return "AccessibilityAction{id=0x" + Integer.toHexString(mId)
                    + ", label=" + mLabel + "}";
        }
    }

    @Override
    public String toString() {
        return "AccessibilityNodeInfo{class=" + mClassName
                + ", text=" + mText
                + ", enabled=" + mEnabled + "}";
    }
}
