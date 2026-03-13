package android.view.accessibility;
import android.os.Handler;
import java.util.List;

public final class AccessibilityManager {
    public static final int FLAG_CONTENT_CONTROLS = 0;
    public static final int FLAG_CONTENT_ICONS = 0;
    public static final int FLAG_CONTENT_TEXT = 0;

    public AccessibilityManager() {}

    public void addAccessibilityRequestPreparer(AccessibilityRequestPreparer p0) {}
    public boolean addAccessibilityStateChangeListener(Object p0) { return false; }
    public void addAccessibilityStateChangeListener(Object p0, Handler p1) {}
    public boolean addTouchExplorationStateChangeListener(Object p0) { return false; }
    public void addTouchExplorationStateChangeListener(Object p0, Handler p1) {}
    public List<?> getEnabledAccessibilityServiceList(int p0) { return null; }
    public List<?> getInstalledAccessibilityServiceList() { return null; }
    public int getRecommendedTimeoutMillis(int p0, int p1) { return 0; }
    public void interrupt() {}
    public static boolean isAccessibilityButtonSupported() { return false; }
    public boolean isEnabled() { return false; }
    public boolean isTouchExplorationEnabled() { return false; }
    public void removeAccessibilityRequestPreparer(AccessibilityRequestPreparer p0) {}
    public boolean removeAccessibilityStateChangeListener(Object p0) { return false; }
    public boolean removeTouchExplorationStateChangeListener(Object p0) { return false; }
    public void sendAccessibilityEvent(AccessibilityEvent p0) {}
}
