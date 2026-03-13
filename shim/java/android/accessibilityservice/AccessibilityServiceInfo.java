package android.accessibilityservice;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Parcel;
import android.os.Parcelable;

public class AccessibilityServiceInfo implements Parcelable {
    public static final int CAPABILITY_CAN_CONTROL_MAGNIFICATION = 0;
    public static final int CAPABILITY_CAN_PERFORM_GESTURES = 0;
    public static final int CAPABILITY_CAN_REQUEST_FILTER_KEY_EVENTS = 0;
    public static final int CAPABILITY_CAN_REQUEST_FINGERPRINT_GESTURES = 0;
    public static final int CAPABILITY_CAN_REQUEST_TOUCH_EXPLORATION = 0;
    public static final int CAPABILITY_CAN_RETRIEVE_WINDOW_CONTENT = 0;
    public static final int CAPABILITY_CAN_TAKE_SCREENSHOT = 0;
    public static final int DEFAULT = 0;
    public static final int FEEDBACK_ALL_MASK = 0;
    public static final int FEEDBACK_AUDIBLE = 0;
    public static final int FEEDBACK_BRAILLE = 0;
    public static final int FEEDBACK_GENERIC = 0;
    public static final int FEEDBACK_HAPTIC = 0;
    public static final int FEEDBACK_SPOKEN = 0;
    public static final int FEEDBACK_VISUAL = 0;
    public static final int FLAG_ENABLE_ACCESSIBILITY_VOLUME = 0;
    public static final int FLAG_INCLUDE_NOT_IMPORTANT_VIEWS = 0;
    public static final int FLAG_REPORT_VIEW_IDS = 0;
    public static final int FLAG_REQUEST_ACCESSIBILITY_BUTTON = 0;
    public static final int FLAG_REQUEST_FILTER_KEY_EVENTS = 0;
    public static final int FLAG_REQUEST_FINGERPRINT_GESTURES = 0;
    public static final int FLAG_REQUEST_MULTI_FINGER_GESTURES = 0;
    public static final int FLAG_REQUEST_SHORTCUT_WARNING_DIALOG_SPOKEN_FEEDBACK = 0;
    public static final int FLAG_REQUEST_TOUCH_EXPLORATION_MODE = 0;
    public static final int FLAG_RETRIEVE_INTERACTIVE_WINDOWS = 0;
    public static final int FLAG_SERVICE_HANDLES_DOUBLE_TAP = 0;
    public int eventTypes = 0;
    public int feedbackType = 0;
    public int flags = 0;
    public int notificationTimeout = 0;
    public int packageNames = 0;

    public AccessibilityServiceInfo() {}

    public static String capabilityToString(int p0) { return null; }
    public int describeContents() { return 0; }
    public static String feedbackTypeToString(int p0) { return null; }
    public static String flagToString(int p0) { return null; }
    public int getCapabilities() { return 0; }
    public String getId() { return null; }
    public int getInteractiveUiTimeoutMillis() { return 0; }
    public int getNonInteractiveUiTimeoutMillis() { return 0; }
    public ResolveInfo getResolveInfo() { return null; }
    public String getSettingsActivityName() { return null; }
    public String loadDescription(PackageManager p0) { return null; }
    public CharSequence loadSummary(PackageManager p0) { return null; }
    public void setInteractiveUiTimeoutMillis(int p0) {}
    public void setNonInteractiveUiTimeoutMillis(int p0) {}
    public void writeToParcel(Parcel p0, int p1) {}
}
