package android.view.accessibility;

public class AccessibilityNodeInfo {
    public AccessibilityNodeInfo() {}

    public static final int ACTION_ACCESSIBILITY_FOCUS = 64;
    public static final String ACTION_ARGUMENT_COLUMN_INT = "ACTION_ARGUMENT_COLUMN_INT";
    public static final String ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN = "ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN";
    public static final String ACTION_ARGUMENT_HTML_ELEMENT_STRING = "ACTION_ARGUMENT_HTML_ELEMENT_STRING";
    public static final String ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT = "ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT";
    public static final String ACTION_ARGUMENT_MOVE_WINDOW_X = "ACTION_ARGUMENT_MOVE_WINDOW_X";
    public static final String ACTION_ARGUMENT_MOVE_WINDOW_Y = "ACTION_ARGUMENT_MOVE_WINDOW_Y";
    public static final String ACTION_ARGUMENT_PRESS_AND_HOLD_DURATION_MILLIS_INT = "ACTION_ARGUMENT_PRESS_AND_HOLD_DURATION_MILLIS_INT";
    public static final String ACTION_ARGUMENT_PROGRESS_VALUE = "ACTION_ARGUMENT_PROGRESS_VALUE";
    public static final String ACTION_ARGUMENT_ROW_INT = "ACTION_ARGUMENT_ROW_INT";
    public static final String ACTION_ARGUMENT_SELECTION_END_INT = "ACTION_ARGUMENT_SELECTION_END_INT";
    public static final String ACTION_ARGUMENT_SELECTION_START_INT = "ACTION_ARGUMENT_SELECTION_START_INT";
    public static final String ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE = "ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE";
    public static final int ACTION_CLEAR_ACCESSIBILITY_FOCUS = 128;
    public static final int ACTION_CLEAR_FOCUS = 2;
    public static final int ACTION_CLEAR_SELECTION = 8;
    public static final int ACTION_CLICK = 16;
    public static final int ACTION_COLLAPSE = 0x00080000;
    public static final int ACTION_COPY = 0x00004000;
    public static final int ACTION_CUT = 0x00010000;
    public static final int ACTION_DISMISS = 0x00100000;
    public static final int ACTION_EXPAND = 0x00040000;
    public static final int ACTION_FOCUS = 1;
    public static final int ACTION_LONG_CLICK = 32;
    public static final int ACTION_NEXT_AT_MOVEMENT_GRANULARITY = 256;
    public static final int ACTION_NEXT_HTML_ELEMENT = 1024;
    public static final int ACTION_PASTE = 0x00008000;
    public static final int ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY = 512;
    public static final int ACTION_PREVIOUS_HTML_ELEMENT = 2048;
    public static final int ACTION_SCROLL_BACKWARD = 8192;
    public static final int ACTION_SCROLL_FORWARD = 4096;
    public static final int ACTION_SELECT = 4;
    public static final int ACTION_SET_SELECTION = 0x00020000;
    public static final int ACTION_SET_TEXT = 0x00200000;
    public static final String EXTRA_DATA_RENDERING_INFO_KEY = "android.view.accessibility.extra.DATA_RENDERING_INFO_KEY";
    public static final String EXTRA_DATA_TEXT_CHARACTER_LOCATION_ARG_LENGTH = "android.view.accessibility.extra.DATA_TEXT_CHARACTER_LOCATION_ARG_LENGTH";
    public static final String EXTRA_DATA_TEXT_CHARACTER_LOCATION_ARG_START_INDEX = "android.view.accessibility.extra.DATA_TEXT_CHARACTER_LOCATION_ARG_START_INDEX";
    public static final String EXTRA_DATA_TEXT_CHARACTER_LOCATION_KEY = "android.view.accessibility.extra.DATA_TEXT_CHARACTER_LOCATION_KEY";
    public static final int FOCUS_ACCESSIBILITY = 2;
    public static final int FOCUS_INPUT = 1;
    public static final int FLAG_INCLUDE_NOT_IMPORTANT_VIEWS = 0x00000002;
    public static final int FLAG_REPORT_VIEW_IDS = 0x00000008;
    public static final int MOVEMENT_GRANULARITY_CHARACTER = 1;
    public static final int MOVEMENT_GRANULARITY_LINE = 4;
    public static final int MOVEMENT_GRANULARITY_PAGE = 16;
    public static final int MOVEMENT_GRANULARITY_PARAGRAPH = 8;
    public static final int MOVEMENT_GRANULARITY_WORD = 2;
    public void addAction(AccessibilityAction action) {}
    public void addAction(int action) {}
    public void addAction(Object p0) {}

    public static final class AccessibilityAction {
        public static final AccessibilityAction ACTION_SCROLL_FORWARD = new AccessibilityAction(4096, null);
        public static final AccessibilityAction ACTION_SCROLL_BACKWARD = new AccessibilityAction(8192, null);
        public static final AccessibilityAction ACTION_SCROLL_UP = new AccessibilityAction(0x01000000, null);
        public static final AccessibilityAction ACTION_SCROLL_DOWN = new AccessibilityAction(0x01000001, null);
        public static final AccessibilityAction ACTION_SCROLL_LEFT = new AccessibilityAction(0x01000002, null);
        public static final AccessibilityAction ACTION_SCROLL_RIGHT = new AccessibilityAction(0x01000003, null);
        public static final AccessibilityAction ACTION_CLICK = new AccessibilityAction(16, null);
        public static final AccessibilityAction ACTION_LONG_CLICK = new AccessibilityAction(32, null);
        public static final AccessibilityAction ACTION_CONTEXT_CLICK = new AccessibilityAction(0x0102003c, null);
        public static final AccessibilityAction ACTION_SHOW_ON_SCREEN = new AccessibilityAction(0x01020036, null);
        public static final AccessibilityAction ACTION_SHOW_TOOLTIP = new AccessibilityAction(0x01020044, null);
        public static final AccessibilityAction ACTION_HIDE_TOOLTIP = new AccessibilityAction(0x01020045, null);
        public static final AccessibilityAction ACTION_FOCUS = new AccessibilityAction(1, null);
        public static final AccessibilityAction ACTION_CLEAR_FOCUS = new AccessibilityAction(2, null);
        public static final AccessibilityAction ACTION_SELECT = new AccessibilityAction(4, null);
        public static final AccessibilityAction ACTION_CLEAR_SELECTION = new AccessibilityAction(8, null);
        public static final AccessibilityAction ACTION_ACCESSIBILITY_FOCUS = new AccessibilityAction(64, null);
        public static final AccessibilityAction ACTION_CLEAR_ACCESSIBILITY_FOCUS = new AccessibilityAction(128, null);
        public static final AccessibilityAction ACTION_NEXT_AT_MOVEMENT_GRANULARITY = new AccessibilityAction(256, null);
        public static final AccessibilityAction ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY = new AccessibilityAction(512, null);
        public static final AccessibilityAction ACTION_NEXT_HTML_ELEMENT = new AccessibilityAction(1024, null);
        public static final AccessibilityAction ACTION_PREVIOUS_HTML_ELEMENT = new AccessibilityAction(2048, null);
        public static final AccessibilityAction ACTION_SET_SELECTION = new AccessibilityAction(0x00020000, null);
        public static final AccessibilityAction ACTION_EXPAND = new AccessibilityAction(0x00040000, null);
        public static final AccessibilityAction ACTION_COLLAPSE = new AccessibilityAction(0x00080000, null);
        public static final AccessibilityAction ACTION_DISMISS = new AccessibilityAction(0x00100000, null);
        public static final AccessibilityAction ACTION_SET_TEXT = new AccessibilityAction(0x00200000, null);
        public static final AccessibilityAction ACTION_COPY = new AccessibilityAction(0x00004000, null);
        public static final AccessibilityAction ACTION_CUT = new AccessibilityAction(0x00010000, null);
        public static final AccessibilityAction ACTION_PASTE = new AccessibilityAction(0x00008000, null);
        public static final AccessibilityAction ACTION_IME_ENTER = new AccessibilityAction(0x01000007, null);
        public static final AccessibilityAction ACTION_PAGE_UP = new AccessibilityAction(0x01000008, null);
        public static final AccessibilityAction ACTION_PAGE_DOWN = new AccessibilityAction(0x01000009, null);
        public static final AccessibilityAction ACTION_PAGE_LEFT = new AccessibilityAction(0x0100000a, null);
        public static final AccessibilityAction ACTION_PAGE_RIGHT = new AccessibilityAction(0x0100000b, null);
        public static final AccessibilityAction ACTION_PRESS_AND_HOLD = new AccessibilityAction(0x0100000c, null);

        private final int mActionId;
        private final CharSequence mLabel;

        public AccessibilityAction(int actionId, CharSequence label) {
            mActionId = actionId;
            mLabel = label;
        }

        public int getId() { return mActionId; }
        public CharSequence getLabel() { return mLabel; }
    }
    public void addChild(Object p0) {}
    public void addChild(Object p0, Object p1) {}
    public void addChildUnchecked(android.view.View child) {}
    public void setExtraRenderingInfo(ExtraRenderingInfo info) {}
    public boolean canOpenPopup() { return false; }
    public int describeContents() { return 0; }
    public Object findAccessibilityNodeInfosByText(Object p0) { return null; }
    public Object findAccessibilityNodeInfosByViewId(Object p0) { return null; }
    public Object findFocus(Object p0) { return null; }
    public Object focusSearch(Object p0) { return null; }
    public Object getActionList() { return null; }
    public Object getAvailableExtraData() { return null; }
    public void getBoundsInScreen(Object p0) {}
    public Object getChild(Object p0) { return null; }
    public int getChildCount() { return 0; }
    public CharSequence getClassName() { return null; }
    public Object getCollectionInfo() { return null; }
    public Object getCollectionItemInfo() { return null; }
    public CharSequence getContentDescription() { return null; }
    public int getDrawingOrder() { return 0; }
    public CharSequence getError() { return null; }
    public Object getExtras() { return null; }
    public CharSequence getHintText() { return null; }
    public int getInputType() { return 0; }
    public Object getLabelFor() { return null; }
    public Object getLabeledBy() { return null; }
    public int getLiveRegion() { return 0; }
    public int getMaxTextLength() { return 0; }
    public int getMovementGranularities() { return 0; }
    public Object getPackageName() { return null; }
    public Object getParent() { return null; }
    public Object getRangeInfo() { return null; }
    public CharSequence getText() { return null; }
    public int getTextSelectionEnd() { return 0; }
    public int getTextSelectionStart() { return 0; }
    public Object getTraversalAfter() { return null; }
    public Object getTraversalBefore() { return null; }
    public String getViewIdResourceName() { return null; }
    public Object getWindow() { return null; }
    public int getWindowId() { return 0; }
    public boolean isAccessibilityFocused() { return false; }
    public boolean isCheckable() { return false; }
    public boolean isChecked() { return false; }
    public boolean isClickable() { return false; }
    public boolean isContentInvalid() { return false; }
    public boolean isContextClickable() { return false; }
    public boolean isDismissable() { return false; }
    public boolean isEditable() { return false; }
    public boolean isEnabled() { return false; }
    public boolean isFocusable() { return false; }
    public boolean isFocused() { return false; }
    public boolean isHeading() { return false; }
    public boolean isImportantForAccessibility() { return false; }
    public boolean isLongClickable() { return false; }
    public boolean isMultiLine() { return false; }
    public boolean isPassword() { return false; }
    public boolean isScreenReaderFocusable() { return false; }
    public boolean isScrollable() { return false; }
    public boolean isSelected() { return false; }
    public boolean isShowingHintText() { return false; }
    public boolean isTextEntryKey() { return false; }
    public boolean isVisibleToUser() { return false; }
    public static AccessibilityNodeInfo obtain(android.view.View source) { return new AccessibilityNodeInfo(); }
    public static AccessibilityNodeInfo obtain(android.view.View root, int virtualDescendantId) { return new AccessibilityNodeInfo(); }
    public static AccessibilityNodeInfo obtain() { return new AccessibilityNodeInfo(); }
    public static AccessibilityNodeInfo obtain(AccessibilityNodeInfo info) { return new AccessibilityNodeInfo(); }
    public boolean performAction(Object p0) { return false; }
    public boolean performAction(Object p0, Object p1) { return false; }
    public void recycle() {}
    public boolean refresh() { return false; }
    public boolean refreshWithExtraData(Object p0, Object p1) { return false; }
    public boolean removeAction(Object p0) { return false; }
    public boolean removeChild(Object p0) { return false; }
    public boolean removeChild(Object p0, Object p1) { return false; }
    public void setAccessibilityFocused(Object p0) {}
    public void setAvailableExtraData(Object p0) {}
    public void setBoundsInScreen(Object p0) {}
    public void setCanOpenPopup(Object p0) {}
    public void setCheckable(Object p0) {}
    public void setChecked(Object p0) {}
    public void setClassName(Object p0) {}
    public void setClickable(Object p0) {}
    public void setCollectionInfo(Object p0) {}
    public void setCollectionItemInfo(Object p0) {}
    public void setContentDescription(Object p0) {}
    public void setContentInvalid(Object p0) {}
    public void setContextClickable(Object p0) {}
    public void setDismissable(Object p0) {}
    public void setDrawingOrder(Object p0) {}
    public void setEditable(Object p0) {}
    public void setEnabled(Object p0) {}
    public void setError(Object p0) {}
    public void setFocusable(Object p0) {}
    public void setFocused(Object p0) {}
    public void setHeading(Object p0) {}
    public void setHintText(Object p0) {}
    public void setImportantForAccessibility(Object p0) {}
    public void setInputType(Object p0) {}
    public void setLabelFor(Object p0) {}
    public void setLabelFor(Object p0, Object p1) {}
    public void setLabeledBy(Object p0) {}
    public void setLabeledBy(Object p0, Object p1) {}
    public void setLiveRegion(Object p0) {}
    public void setLongClickable(Object p0) {}
    public void setMaxTextLength(Object p0) {}
    public void setMovementGranularities(Object p0) {}
    public void setMultiLine(Object p0) {}
    public void setPackageName(Object p0) {}
    public void setPaneTitle(Object p0) {}
    public void setParent(Object p0) {}
    public void setParent(Object p0, Object p1) {}
    public void setPassword(Object p0) {}
    public void setRangeInfo(Object p0) {}
    public void setScreenReaderFocusable(Object p0) {}
    public void setScrollable(Object p0) {}
    public void setSelected(Object p0) {}
    public void setShowingHintText(Object p0) {}
    public void setSource(Object p0) {}
    public void setSource(Object p0, Object p1) {}
    public void setStateDescription(Object p0) {}
    public void setText(Object p0) {}
    public void setTextEntryKey(Object p0) {}
    public void setTextSelection(Object p0, Object p1) {}
    public void setTooltipText(Object p0) {}
    public void setTouchDelegateInfo(Object p0) {}
    public void setTraversalAfter(Object p0) {}
    public void setTraversalAfter(Object p0, Object p1) {}
    public void setTraversalBefore(Object p0) {}
    public void setTraversalBefore(Object p0, Object p1) {}
    public void setViewIdResourceName(Object p0) {}
    public void setVisibleToUser(Object p0) {}
    public void writeToParcel(Object p0, Object p1) {}

    // Static utility methods needed by View.java
    public static int getVirtualDescendantId(long accessibilityNodeId) {
        return (int) accessibilityNodeId;
    }

    public static long makeNodeId(int viewId, int virtualDescendantId) {
        return (((long) viewId) << 32) | virtualDescendantId;
    }

    // Instance methods needed by View.java
    public void getBoundsInParent(android.graphics.Rect outBounds) {
        if (outBounds != null) outBounds.setEmpty();
    }
    public void setBoundsInParent(android.graphics.Rect bounds) {}
    public long getChildId(int index) { return 0; }
    public java.util.List<Long> getChildNodeIds() { return new java.util.ArrayList<Long>(); }
    public long getSourceNodeId() { return 0; }

    /** Stub class for touch delegate info. */
    public static class TouchDelegateInfo {
        public TouchDelegateInfo(java.util.Map<android.graphics.Region, android.view.View> targetMap) {}
        public int getRegionCount() { return 0; }
        public android.graphics.Region getRegionAt(int index) { return null; }
    }

    public static class ExtraRenderingInfo {
        private ExtraRenderingInfo() {}
        public static ExtraRenderingInfo obtain() { return new ExtraRenderingInfo(); }
        public float getTextSizeInPx() { return 0f; }
        public int getTextSizeUnit() { return 0; }
        public void setTextSizeInPx(float textSize) {}
        public void setTextSizeUnit(int unit) {}
        public void setLayoutSize(int width, int height) {}
        public android.util.Size getLayoutSize() { return null; }
    }
}
