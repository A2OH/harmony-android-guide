package android.app;
import android.opengl.Visibility;
import android.view.Display;
import android.opengl.Visibility;
import android.view.Display;

/**
 * Android-compatible ActionBar shim.
 * Abstract class; all methods are no-ops or return safe defaults.
 * The inner Tab class and OnNavigationListener interface are provided as stubs.
 */
public class ActionBar {

    // --- Navigation mode constants ---
    public static final int NAVIGATION_MODE_STANDARD = 0;
    public static final int NAVIGATION_MODE_LIST     = 1;
    public static final int NAVIGATION_MODE_TABS     = 2;

    // --- Display option flags (subset of real API) ---
    public static final int DISPLAY_HOME_AS_UP          = 0x04;
    public static final int DISPLAY_SHOW_TITLE          = 0x08;
    public static final int DISPLAY_SHOW_HOME           = 0x02;
    public static final int DISPLAY_USE_LOGO            = 0x01;

    // -------------------------------------------------------------------------
    // Title / subtitle
    // -------------------------------------------------------------------------

    public void setTitle(CharSequence title) {}
    public void setTitle(int resId) {}

    public void setSubtitle(CharSequence subtitle) {}
    public void setSubtitle(int resId) {}

    // -------------------------------------------------------------------------
    // Display options
    // -------------------------------------------------------------------------

    public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {}
    public void setHomeButtonEnabled(boolean enabled) {}
    public void setDisplayShowTitleEnabled(boolean showTitle) {}
    public void setDisplayOptions(int options) {}
    public void setDisplayOptions(int options, int mask) {}

    // -------------------------------------------------------------------------
    // Visibility
    // -------------------------------------------------------------------------

    public void show() {}
    public void hide() {}
    public boolean isShowing() { return false; }

    // -------------------------------------------------------------------------
    // Dimensions
    // -------------------------------------------------------------------------

    public int getHeight() { return 0; }

    // -------------------------------------------------------------------------
    // Navigation
    // -------------------------------------------------------------------------

    public void setNavigationMode(int mode) {}
    public int getNavigationMode() { return 0; }

    // -------------------------------------------------------------------------
    // Tabs
    // -------------------------------------------------------------------------

    public void addTab(Tab tab) {}
    public void addTab(Tab tab, boolean setSelected) {}
    public void addTab(Tab tab, int position) {}
    public void addTab(Tab tab, int position, boolean setSelected) {}
    public void removeTab(Tab tab) {}
    public void removeAllTabs() {}
    public void selectTab(Tab tab) {}
    public Tab getSelectedTab() { return null; }
    public Tab getTabAt(int index) { return null; }
    public int getTabCount() { return 0; }
    public Tab newTab() { return null; }

    // -------------------------------------------------------------------------
    // Inner: Tab
    // -------------------------------------------------------------------------

    /**
     * Represents a single tab in the ActionBar tab navigation mode.
     */
    public static abstract class Tab {

        public static final int INVALID_POSITION = -1;

        public CharSequence getText() { return null; }
        public Tab setText(CharSequence text) { return null; }
        public Tab setText(int resId) { return null; }

        public Tab setIcon(int resId) { return null; }
        public Tab setIcon(Object icon) { return null; }

        public Tab setTag(Object obj) { return null; }
        public Object getTag() { return null; }

        public Tab setTabListener(TabListener listener) { return null; }
        public int getPosition() { return 0; }

        public void select() {}
    }

    // -------------------------------------------------------------------------
    // Inner: TabListener
    // -------------------------------------------------------------------------

    /**
     * Object interface for tab selection events.
     */
    public interface TabListener {
        void onTabSelected(Tab tab, Object ft);
        void onTabUnselected(Tab tab, Object ft);
        void onTabReselected(Tab tab, Object ft);
    }

    // -------------------------------------------------------------------------
    // Inner: OnNavigationListener
    // -------------------------------------------------------------------------

    /**
     * Object interface for list navigation mode.
     */
    public interface OnNavigationListener {
        boolean onNavigationItemSelected(int itemPosition, long itemId);
    }
}
