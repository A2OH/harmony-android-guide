package android.view.accessibility;
import android.os.Bundle;
import java.util.List;

public class AccessibilityNodeProvider {
    public static final int HOST_VIEW_ID = 0;

    public AccessibilityNodeProvider() {}

    public void addExtraDataToAccessibilityNodeInfo(int p0, AccessibilityNodeInfo p1, String p2, Bundle p3) {}
    public AccessibilityNodeInfo createAccessibilityNodeInfo(int p0) { return null; }
    public List<AccessibilityNodeInfo> findAccessibilityNodeInfosByText(String p0, int p1) { return null; }
    public AccessibilityNodeInfo findFocus(int p0) { return null; }
    public boolean performAction(int p0, int p1, Bundle p2) { return false; }
}
