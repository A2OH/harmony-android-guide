package android.content.pm;

// Shim: android.content.pm.ShortcutManager
// Android-to-OpenHarmony migration compatibility stub.

import java.util.Collections;
import java.util.List;

public class ShortcutManager {

    public ShortcutManager() {
    }

    /** Returns the list of dynamic shortcuts published by the calling app. */
    public List<ShortcutInfo> getDynamicShortcuts() {
        return Collections.emptyList();
    }

    /** Returns all pinned shortcuts from the calling app that are visible to the launcher. */
    public List<ShortcutInfo> getPinnedShortcuts() {
        return Collections.emptyList();
    }

    /**
     * Publish the provided list of shortcuts, replacing any existing dynamic shortcuts.
     *
     * @return true if the operation succeeded; false if the rate limit is exceeded.
     */
    public boolean setDynamicShortcuts(List<ShortcutInfo> shortcutInfoList) {
        return true;
    }

    /**
     * Publish the provided list of dynamic shortcuts, adding to any existing dynamic shortcuts.
     *
     * @return true if the operation succeeded; false if the rate limit is exceeded.
     */
    public boolean addDynamicShortcuts(List<ShortcutInfo> shortcutInfoList) {
        return true;
    }

    /** Deletes dynamic shortcuts by ID. */
    public void removeDynamicShortcuts(List<String> shortcutIds) {
    }

    /** Removes all dynamic shortcuts published by the calling app. */
    public void removeAllDynamicShortcuts() {
    }

    /** Returns true if the app has exceeded its shortcut-update rate limit. */
    public boolean isRateLimitingActive() {
        return false;
    }

    /** Returns the maximum number of shortcuts allowed per activity. */
    public int getMaxShortcutCountPerActivity() {
        return 5;
    }
}
