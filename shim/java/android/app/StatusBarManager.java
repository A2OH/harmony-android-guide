package android.app;

/**
 * Android-compatible StatusBarManager shim.
 * Stub system-service class; all operations are no-ops.
 */
public class StatusBarManager {

    // disable() flag constants (mirrors android.app.StatusBarManager)
    public static final int DISABLE_EXPAND            = 0x00010000;
    public static final int DISABLE_NOTIFICATION_ICONS = 0x00020000;
    public static final int DISABLE_NOTIFICATION_ALERTS = 0x00040000;
    public static final int DISABLE_SYSTEM_INFO       = 0x00100000;
    public static final int DISABLE_HOME              = 0x00200000;
    public static final int DISABLE_RECENT            = 0x01000000;
    public static final int DISABLE_BACK              = 0x00400000;
    public static final int DISABLE_CLOCK             = 0x00800000;
    public static final int DISABLE_SEARCH            = 0x02000000;
    public static final int DISABLE_NONE              = 0x00000000;

    /** Package-private constructor; obtain via Context.getSystemService(). */
    public StatusBarManager() {}

    /**
     * Disables one or more status bar features using the DISABLE_* flag constants.
     * No-op in this shim.
     */
    public void disable(int what) {
        // no-op
    }

    /**
     * Expands the notifications panel if it is collapsed.
     * No-op in this shim.
     */
    public void expandNotificationsPanel() {
        // no-op
    }

    /**
     * Collapses any expanded panels.
     * No-op in this shim.
     */
    public void collapsePanels() {
        // no-op
    }

    /**
     * Expands the settings panel.
     * No-op in this shim.
     */
    public void expandSettingsPanel() {
        // no-op
    }

    /**
     * Expands the settings panel to the specified subPanel.
     * No-op in this shim.
     */
    public void expandSettingsPanel(String subPanel) {
        // no-op
    }
}
