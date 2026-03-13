package android.content.pm;

import java.util.Collections;
import java.util.List;

/**
 * Android-compatible LauncherApps shim. Stub — no-op implementation for A2OH migration.
 */
public class LauncherApps {

    // -------------------------------------------------------------------------
    // ShortcutQuery inner class
    // -------------------------------------------------------------------------

    public static class ShortcutQuery {
        public static final int FLAG_MATCH_DYNAMIC    = 0x01;
        public static final int FLAG_MATCH_PINNED     = 0x02;
        public static final int FLAG_MATCH_MANIFEST   = 0x04;

        private long   mChangedSince;
        private String mPackage;
        private int    mFlags;

        public ShortcutQuery() {}

        public ShortcutQuery setChangedSince(long changedSince) {
            mChangedSince = changedSince;
            return this;
        }

        public ShortcutQuery setPackage(String packageName) {
            mPackage = packageName;
            return this;
        }

        public ShortcutQuery setQueryFlags(int queryFlags) {
            mFlags = queryFlags;
            return this;
        }
    }

    // -------------------------------------------------------------------------
    // Callback abstract inner class
    // -------------------------------------------------------------------------

    public static abstract class Callback {
        /** Called when a package is added or upgraded with a new set of shortcuts. */
        public abstract void onShortcutsChanged(String packageName, List<Object> shortcuts, Object user);

        /** Called when packages are added. */
        public void onPackageAdded(String packageName, Object user) {}

        /** Called when packages are removed. */
        public void onPackageRemoved(String packageName, Object user) {}

        /** Called when packages are changed. */
        public void onPackageChanged(String packageName, Object user) {}

        /** Called when packages' suspended state has changed. */
        public void onPackagesSuspended(String[] packageNames, Object user) {}

        /** Called when packages' suspended state has changed. */
        public void onPackagesUnsuspended(String[] packageNames, Object user) {}
    }

    // -------------------------------------------------------------------------
    // LauncherApps instance API
    // -------------------------------------------------------------------------

    public LauncherApps() {}

    /**
     * Returns a list of launchable activities that match the given intent.
     * @return empty list (stub)
     */
    public List<Object> getActivityList(String packageName, Object user) {
        return Collections.emptyList();
    }

    /**
     * Resolve the activity for an intent, given a user.
     * @return null (stub)
     */
    public ActivityInfo resolveActivity(Object intent, Object user) {
        return null; // stub
    }

    /**
     * Checks whether a package is still installed and enabled.
     * @return false (stub)
     */
    public boolean isPackageEnabled(String packageName, Object user) {
        return false; // stub
    }

    /**
     * Checks whether the activity described by the given component is enabled and exported.
     * @return false (stub)
     */
    public boolean isActivityEnabled(Object component, Object user) {
        return false; // stub
    }
}
