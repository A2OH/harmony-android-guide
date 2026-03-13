package android.accessibilityservice;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Android-compatible AccessibilityService shim.
 * Abstract base class for accessibility services.
 * Subclasses must implement {@link #onAccessibilityEvent} and {@link #onInterrupt}.
 */
public abstract class AccessibilityService {

    // ── Global action constants ─────────────────────────────────────────────
    public static final int GLOBAL_ACTION_BACK          = 1;
    public static final int GLOBAL_ACTION_HOME          = 2;
    public static final int GLOBAL_ACTION_RECENTS       = 3;
    public static final int GLOBAL_ACTION_NOTIFICATIONS = 4;
    public static final int GLOBAL_ACTION_QUICK_SETTINGS       = 5;
    public static final int GLOBAL_ACTION_POWER_DIALOG         = 6;
    public static final int GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN  = 7;
    public static final int GLOBAL_ACTION_LOCK_SCREEN           = 8;
    public static final int GLOBAL_ACTION_TAKE_SCREENSHOT       = 9;

    // ── Abstract lifecycle ──────────────────────────────────────────────────

    /**
     * Called when an {@link AccessibilityEvent} matching the service's event
     * filter is received from the system.
     */
    public abstract void onAccessibilityEvent(AccessibilityEvent event);

    /**
     * Called when the system wants the service to stop processing events,
     * e.g. when accessibility focus moves to a new window.
     */
    public abstract void onInterrupt();

    // ── Service lifecycle hooks (empty defaults) ────────────────────────────

    /** Called when the service is connected. Override to initialise. */
    protected void onServiceConnected() {}

    // ── Global actions ──────────────────────────────────────────────────────

    /**
     * Perform a global action (e.g., pressing Back or Home).
     * Always returns {@code false} in this shim.
     *
     * @param action one of the {@code GLOBAL_ACTION_*} constants
     * @return {@code true} if the action was performed successfully
     */
    public final boolean performGlobalAction(int action) {
        System.out.println("[AccessibilityService] performGlobalAction: " + action);
        return false;
    }

    // ── Window queries ──────────────────────────────────────────────────────

    /**
     * Return the root node of the active window's view hierarchy.
     * Returns {@code null} in this shim.
     */
    public AccessibilityNodeInfo getRootInActiveWindow() {
        return null;
    }

    // ── Service metadata ────────────────────────────────────────────────────

    /**
     * Return the {@link AccessibilityServiceInfo} that describes this service.
     * Returns a default instance in this shim.
     */
    public final AccessibilityServiceInfo getServiceInfo() {
        return new AccessibilityServiceInfo();
    }

    /**
     * Set the {@link AccessibilityServiceInfo} for this service. No-op in shim.
     */
    public final void setServiceInfo(AccessibilityServiceInfo info) {
        // no-op
    }
}
