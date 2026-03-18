package android.view.accessibility;

public class AccessibilityNodeInfo {
    public AccessibilityNodeInfo() {}

    public static final int ACTION_ACCESSIBILITY_FOCUS = 0;
    public static final int ACTION_ARGUMENT_COLUMN_INT = 0;
    public static final int ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN = 0;
    public static final int ACTION_ARGUMENT_HTML_ELEMENT_STRING = 0;
    public static final int ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT = 0;
    public static final int ACTION_ARGUMENT_MOVE_WINDOW_X = 0;
    public static final int ACTION_ARGUMENT_MOVE_WINDOW_Y = 0;
    public static final int ACTION_ARGUMENT_PRESS_AND_HOLD_DURATION_MILLIS_INT = 0;
    public static final int ACTION_ARGUMENT_PROGRESS_VALUE = 0;
    public static final int ACTION_ARGUMENT_ROW_INT = 0;
    public static final int ACTION_ARGUMENT_SELECTION_END_INT = 0;
    public static final int ACTION_ARGUMENT_SELECTION_START_INT = 0;
    public static final int ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE = 0;
    public static final int ACTION_CLEAR_ACCESSIBILITY_FOCUS = 0;
    public static final int ACTION_CLEAR_FOCUS = 0;
    public static final int ACTION_CLEAR_SELECTION = 0;
    public static final int ACTION_CLICK = 0;
    public static final int ACTION_COLLAPSE = 0;
    public static final int ACTION_COPY = 0;
    public static final int ACTION_CUT = 0;
    public static final int ACTION_DISMISS = 0;
    public static final int ACTION_EXPAND = 0;
    public static final int ACTION_FOCUS = 0;
    public static final int ACTION_LONG_CLICK = 0;
    public static final int ACTION_NEXT_AT_MOVEMENT_GRANULARITY = 0;
    public static final int ACTION_NEXT_HTML_ELEMENT = 0;
    public static final int ACTION_PASTE = 0;
    public static final int ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY = 0;
    public static final int ACTION_PREVIOUS_HTML_ELEMENT = 0;
    public static final int ACTION_SCROLL_BACKWARD = 8192;
    public static final int ACTION_SCROLL_FORWARD = 4096;
    public static final int ACTION_SELECT = 0;
    public static final int ACTION_SET_SELECTION = 0;
    public static final int ACTION_SET_TEXT = 0;
    public static final int EXTRA_DATA_RENDERING_INFO_KEY = 0;
    public static final int EXTRA_DATA_TEXT_CHARACTER_LOCATION_ARG_LENGTH = 0;
    public static final int EXTRA_DATA_TEXT_CHARACTER_LOCATION_ARG_START_INDEX = 0;
    public static final int EXTRA_DATA_TEXT_CHARACTER_LOCATION_KEY = 0;
    public static final int FOCUS_ACCESSIBILITY = 0;
    public static final int FOCUS_INPUT = 0;
    public static final int MOVEMENT_GRANULARITY_CHARACTER = 0;
    public static final int MOVEMENT_GRANULARITY_LINE = 0;
    public static final int MOVEMENT_GRANULARITY_PAGE = 0;
    public static final int MOVEMENT_GRANULARITY_PARAGRAPH = 0;
    public static final int MOVEMENT_GRANULARITY_WORD = 0;
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
    public Object getClassName() { return null; }
    public Object getCollectionInfo() { return null; }
    public Object getCollectionItemInfo() { return null; }
    public Object getContentDescription() { return null; }
    public int getDrawingOrder() { return 0; }
    public Object getError() { return null; }
    public Object getExtras() { return null; }
    public Object getHintText() { return null; }
    public int getInputType() { return 0; }
    public Object getLabelFor() { return null; }
    public Object getLabeledBy() { return null; }
    public int getLiveRegion() { return 0; }
    public int getMaxTextLength() { return 0; }
    public int getMovementGranularities() { return 0; }
    public Object getPackageName() { return null; }
    public Object getParent() { return null; }
    public Object getRangeInfo() { return null; }
    public Object getText() { return null; }
    public int getTextSelectionEnd() { return 0; }
    public int getTextSelectionStart() { return 0; }
    public Object getTraversalAfter() { return null; }
    public Object getTraversalBefore() { return null; }
    public Object getViewIdResourceName() { return null; }
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
    public static Object obtain(Object p0) { return null; }
    public static Object obtain(Object p0, Object p1) { return null; }
    public static Object obtain() { return null; }
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
}
