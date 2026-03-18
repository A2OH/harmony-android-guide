package android.view.accessibility;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Parcel;
import android.os.Parcelable;

public final class AccessibilityWindowInfo implements Parcelable {
    public static final int TYPE_ACCESSIBILITY_OVERLAY = 0;
    public static final int TYPE_APPLICATION = 0;
    public static final int UNDEFINED_WINDOW_ID = -1;
    public static final int TYPE_INPUT_METHOD = 0;
    public static final int TYPE_SPLIT_SCREEN_DIVIDER = 0;
    public static final int TYPE_SYSTEM = 0;

    public AccessibilityWindowInfo() {}
    public AccessibilityWindowInfo(AccessibilityWindowInfo p0) {}

    public int describeContents() { return 0; }
    public AccessibilityNodeInfo getAnchor() { return null; }
    public void getBoundsInScreen(Rect p0) {}
    public AccessibilityWindowInfo getChild(int p0) { return null; }
    public int getChildCount() { return 0; }
    public int getDisplayId() { return 0; }
    public int getId() { return 0; }
    public int getLayer() { return 0; }
    public AccessibilityWindowInfo getParent() { return null; }
    public void getRegionInScreen(Region p0) {}
    public AccessibilityNodeInfo getRoot() { return null; }
    public int getType() { return 0; }
    public boolean isAccessibilityFocused() { return false; }
    public boolean isActive() { return false; }
    public boolean isFocused() { return false; }
    public boolean isInPictureInPictureMode() { return false; }
    public static AccessibilityWindowInfo obtain() { return null; }
    public static AccessibilityWindowInfo obtain(AccessibilityWindowInfo p0) { return null; }
    public void recycle() {}
    public void writeToParcel(Parcel p0, int p1) {}
}
