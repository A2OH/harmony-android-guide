package android.view.accessibility;

public interface AccessibilityEventSource {
    void sendAccessibilityEvent(int p0);
    void sendAccessibilityEventUnchecked(AccessibilityEvent p0);
}
