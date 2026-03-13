package android.app;

/**
 * Android-compatible ActionBar shim.
 * Abstract class; all methods are no-ops or return safe defaults.
 * The inner Tab class and OnNavigationListener interface are provided as stubs.
 */
public abstract class ActionBar {

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

    public abstract void setTitle(CharSequence title);
    public abstract void setTitle(int resId);

    public abstract void setSubtitle(CharSequence subtitle);
    public abstract void setSubtitle(int resId);

    // -------------------------------------------------------------------------
    // Display options
    // -------------------------------------------------------------------------

    public abstract void setDisplayHomeAsUpEnabled(boolean showHomeAsUp);
    public abstract void setHomeButtonEnabled(boolean enabled);
    public abstract void setDisplayShowTitleEnabled(boolean showTitle);
    public abstract void setDisplayOptions(int options);
    public abstract void setDisplayOptions(int options, int mask);

    // -------------------------------------------------------------------------
    // Visibility
    // -------------------------------------------------------------------------

    public abstract void show();
    public abstract void hide();
    public abstract boolean isShowing();

    // -------------------------------------------------------------------------
    // Dimensions
    // -------------------------------------------------------------------------

    public abstract int getHeight();

    // -------------------------------------------------------------------------
    // Navigation
    // -------------------------------------------------------------------------

    public abstract void setNavigationMode(int mode);
    public abstract int getNavigationMode();

    // -------------------------------------------------------------------------
    // Tabs
    // -------------------------------------------------------------------------

    public abstract void addTab(Tab tab);
    public abstract void addTab(Tab tab, boolean setSelected);
    public abstract void addTab(Tab tab, int position);
    public abstract void addTab(Tab tab, int position, boolean setSelected);
    public abstract void removeTab(Tab tab);
    public abstract void removeAllTabs();
    public abstract void selectTab(Tab tab);
    public abstract Tab getSelectedTab();
    public abstract Tab getTabAt(int index);
    public abstract int getTabCount();
    public abstract Tab newTab();

    // -------------------------------------------------------------------------
    // Inner: Tab
    // -------------------------------------------------------------------------

    /**
     * Represents a single tab in the ActionBar tab navigation mode.
     */
    public static abstract class Tab {

        public static final int INVALID_POSITION = -1;

        public abstract CharSequence getText();
        public abstract Tab setText(CharSequence text);
        public abstract Tab setText(int resId);

        public abstract Tab setIcon(int resId);
        public abstract Tab setIcon(Object icon);

        public abstract Tab setTag(Object obj);
        public abstract Object getTag();

        public abstract Tab setTabListener(TabListener listener);
        public abstract int getPosition();

        public abstract void select();
    }

    // -------------------------------------------------------------------------
    // Inner: TabListener
    // -------------------------------------------------------------------------

    /**
     * Callback interface for tab selection events.
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
     * Callback interface for list navigation mode.
     */
    public interface OnNavigationListener {
        boolean onNavigationItemSelected(int itemPosition, long itemId);
    }
}
