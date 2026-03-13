package android.accessibilityservice;

public class AccessibilityService {
    public AccessibilityService() {}

    public static final int ERROR_TAKE_SCREENSHOT_INTERNAL_ERROR = 0;
    public static final int ERROR_TAKE_SCREENSHOT_INTERVAL_TIME_SHORT = 0;
    public static final int ERROR_TAKE_SCREENSHOT_INVALID_DISPLAY = 0;
    public static final int ERROR_TAKE_SCREENSHOT_NO_ACCESSIBILITY_ACCESS = 0;
    public static final int GESTURE_2_FINGER_DOUBLE_TAP = 0;
    public static final int GESTURE_2_FINGER_DOUBLE_TAP_AND_HOLD = 0;
    public static final int GESTURE_2_FINGER_SINGLE_TAP = 0;
    public static final int GESTURE_2_FINGER_SWIPE_DOWN = 0;
    public static final int GESTURE_2_FINGER_SWIPE_LEFT = 0;
    public static final int GESTURE_2_FINGER_SWIPE_RIGHT = 0;
    public static final int GESTURE_2_FINGER_SWIPE_UP = 0;
    public static final int GESTURE_2_FINGER_TRIPLE_TAP = 0;
    public static final int GESTURE_3_FINGER_DOUBLE_TAP = 0;
    public static final int GESTURE_3_FINGER_DOUBLE_TAP_AND_HOLD = 0;
    public static final int GESTURE_3_FINGER_SINGLE_TAP = 0;
    public static final int GESTURE_3_FINGER_SWIPE_DOWN = 0;
    public static final int GESTURE_3_FINGER_SWIPE_LEFT = 0;
    public static final int GESTURE_3_FINGER_SWIPE_RIGHT = 0;
    public static final int GESTURE_3_FINGER_SWIPE_UP = 0;
    public static final int GESTURE_3_FINGER_TRIPLE_TAP = 0;
    public static final int GESTURE_4_FINGER_DOUBLE_TAP = 0;
    public static final int GESTURE_4_FINGER_DOUBLE_TAP_AND_HOLD = 0;
    public static final int GESTURE_4_FINGER_SINGLE_TAP = 0;
    public static final int GESTURE_4_FINGER_SWIPE_DOWN = 0;
    public static final int GESTURE_4_FINGER_SWIPE_LEFT = 0;
    public static final int GESTURE_4_FINGER_SWIPE_RIGHT = 0;
    public static final int GESTURE_4_FINGER_SWIPE_UP = 0;
    public static final int GESTURE_4_FINGER_TRIPLE_TAP = 0;
    public static final int GESTURE_DOUBLE_TAP = 0;
    public static final int GESTURE_DOUBLE_TAP_AND_HOLD = 0;
    public static final int GESTURE_SWIPE_DOWN = 0;
    public static final int GESTURE_SWIPE_DOWN_AND_LEFT = 0;
    public static final int GESTURE_SWIPE_DOWN_AND_RIGHT = 0;
    public static final int GESTURE_SWIPE_DOWN_AND_UP = 0;
    public static final int GESTURE_SWIPE_LEFT = 0;
    public static final int GESTURE_SWIPE_LEFT_AND_DOWN = 0;
    public static final int GESTURE_SWIPE_LEFT_AND_RIGHT = 0;
    public static final int GESTURE_SWIPE_LEFT_AND_UP = 0;
    public static final int GESTURE_SWIPE_RIGHT = 0;
    public static final int GESTURE_SWIPE_RIGHT_AND_DOWN = 0;
    public static final int GESTURE_SWIPE_RIGHT_AND_LEFT = 0;
    public static final int GESTURE_SWIPE_RIGHT_AND_UP = 0;
    public static final int GESTURE_SWIPE_UP = 0;
    public static final int GESTURE_SWIPE_UP_AND_DOWN = 0;
    public static final int GESTURE_SWIPE_UP_AND_LEFT = 0;
    public static final int GESTURE_SWIPE_UP_AND_RIGHT = 0;
    public static final int GLOBAL_ACTION_BACK = 0;
    public static final int GLOBAL_ACTION_HOME = 0;
    public static final int GLOBAL_ACTION_LOCK_SCREEN = 0;
    public static final int GLOBAL_ACTION_NOTIFICATIONS = 0;
    public static final int GLOBAL_ACTION_POWER_DIALOG = 0;
    public static final int GLOBAL_ACTION_QUICK_SETTINGS = 0;
    public static final int GLOBAL_ACTION_RECENTS = 0;
    public static final int GLOBAL_ACTION_TAKE_SCREENSHOT = 0;
    public static final int GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN = 0;
    public static final int SERVICE_INTERFACE = 0;
    public static final int SERVICE_META_DATA = 0;
    public static final int SHOW_MODE_AUTO = 0;
    public static final int SHOW_MODE_HIDDEN = 0;
    public static final int SHOW_MODE_IGNORE_HARD_KEYBOARD = 0;
    public void disableSelf() {}
    public boolean dispatchGesture(Object p0, Object p1, Object p2) { return false; }
    public Object findFocus(Object p0) { return null; }
    public Object getRootInActiveWindow() { return null; }
    public Object getServiceInfo() { return null; }
    public Object getWindows() { return null; }
    public void onAccessibilityEvent(Object p0) {}
    public Object onBind(Object p0) { return null; }
    public boolean onGesture(Object p0) { return false; }
    public void onInterrupt() {}
    public boolean onKeyEvent(Object p0) { return false; }
    public void onServiceConnected() {}
    public void onSystemActionsChanged() {}
    public boolean performGlobalAction(Object p0) { return false; }
    public void setGestureDetectionPassthroughRegion(Object p0, Object p1) {}
    public void setServiceInfo(Object p0) {}
    public void setTouchExplorationPassthroughRegion(Object p0, Object p1) {}
    public void takeScreenshot(Object p0, Object p1, Object p2) {}
    public void onCancelled(Object p0) {}
    public void onCompleted(Object p0) {}
}
