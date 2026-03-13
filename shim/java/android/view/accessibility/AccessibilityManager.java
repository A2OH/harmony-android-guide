package android.view.accessibility;

import java.util.Collections;
import java.util.List;

/**
 * Android-compatible AccessibilityManager shim.
 * System service for accessibility state queries and event dispatch.
 */
public class AccessibilityManager {

    // ── Listener interfaces ─────────────────────────────────────────────────

    /** Callback for accessibility-enabled state changes. */
    public interface AccessibilityStateChangeListener {
        void onAccessibilityStateChanged(boolean enabled);
    }

    /** Callback for touch-exploration-enabled state changes. */
    public interface TouchExplorationStateChangeListener {
        void onTouchExplorationStateChanged(boolean enabled);
    }

    // ── State (all false / empty in shim) ───────────────────────────────────

    private boolean mEnabled             = false;
    private boolean mTouchExplorationEnabled = false;

    public AccessibilityManager() {}

    // ── Public API ──────────────────────────────────────────────────────────

    /**
     * Return whether accessibility is enabled on the device.
     * Always {@code false} in this shim.
     */
    public boolean isEnabled() {
        return mEnabled;
    }

    /**
     * Return whether touch exploration (TalkBack explore-by-touch) is active.
     * Always {@code false} in this shim.
     */
    public boolean isTouchExplorationEnabled() {
        return mTouchExplorationEnabled;
    }

    /**
     * Dispatch an accessibility event. No-op in this shim.
     */
    public void sendAccessibilityEvent(AccessibilityEvent event) {
        System.out.println("[AccessibilityManager] sendAccessibilityEvent: " + event);
    }

    /**
     * Return all installed accessibility services.
     * Returns an empty list in this shim.
     */
    public List<android.accessibilityservice.AccessibilityServiceInfo>
            getInstalledAccessibilityServiceList() {
        return Collections.emptyList();
    }

    /**
     * Return accessibility services enabled for the given feedback type mask.
     * Returns an empty list in this shim.
     */
    public List<android.accessibilityservice.AccessibilityServiceInfo>
            getEnabledAccessibilityServiceList(int feedbackTypeFlags) {
        return Collections.emptyList();
    }

    /**
     * Register a listener for accessibility-enabled state changes.
     * No-op in this shim (listener is never called).
     */
    public boolean addAccessibilityStateChangeListener(
            AccessibilityStateChangeListener listener) {
        return true;
    }

    /**
     * Unregister a previously registered listener.
     * No-op in this shim.
     */
    public boolean removeAccessibilityStateChangeListener(
            AccessibilityStateChangeListener listener) {
        return true;
    }

    /**
     * Register a listener for touch-exploration state changes.
     * No-op in this shim.
     */
    public boolean addTouchExplorationStateChangeListener(
            TouchExplorationStateChangeListener listener) {
        return true;
    }

    /**
     * Unregister a previously registered touch-exploration listener.
     * No-op in this shim.
     */
    public boolean removeTouchExplorationStateChangeListener(
            TouchExplorationStateChangeListener listener) {
        return true;
    }
}
