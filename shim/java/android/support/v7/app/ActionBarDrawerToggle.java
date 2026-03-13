package android.support.v7.app;

/**
 * Shim: android.support.v7.app.ActionBarDrawerToggle
 *
 * Ties a DrawerLayout to the action bar so that navigation-drawer open/close
 * is reflected in the home button indicator.  All visual feedback is stubbed
 * — only state tracking is implemented.
 */
public class ActionBarDrawerToggle {

    private boolean mDrawerIndicatorEnabled = true;
    private CharSequence mOpenDescription;
    private CharSequence mCloseDescription;

    /**
     * Construct a new ActionBarDrawerToggle.
     *
     * @param activity    the host Activity
     * @param drawerLayout the DrawerLayout to tie to the ActionBar
     * @param openDrawerContentDescRes  string resource for the open-drawer action
     * @param closeDrawerContentDescRes string resource for the close-drawer action
     */
    public ActionBarDrawerToggle(Object activity,
                                  Object drawerLayout,
                                  int openDrawerContentDescRes,
                                  int closeDrawerContentDescRes) {
        this.mOpenDescription  = "@" + openDrawerContentDescRes;
        this.mCloseDescription = "@" + closeDrawerContentDescRes;
    }

    /**
     * Construct with an explicit Toolbar as the action bar.
     *
     * @param activity    the host Activity
     * @param drawerLayout the DrawerLayout to tie to the ActionBar
     * @param toolbar      the Toolbar acting as the ActionBar
     * @param openDrawerContentDescRes  string resource for the open-drawer action
     * @param closeDrawerContentDescRes string resource for the close-drawer action
     */
    public ActionBarDrawerToggle(Object activity,
                                  Object drawerLayout,
                                  Object toolbar,
                                  int openDrawerContentDescRes,
                                  int closeDrawerContentDescRes) {
        this.mOpenDescription  = "@" + openDrawerContentDescRes;
        this.mCloseDescription = "@" + closeDrawerContentDescRes;
    }

    // ── Lifecycle ──

    /**
     * Synchronise the indicator / arrow state after the drawer state has changed.
     * Call this from {@code onPostCreate} or after restoring instance state.
     */
    public void syncState() {
        // Stub — visual sync handled by OH bridge
    }

    /**
     * Called when the Activity configuration changes.
     *
     * @param newConfig the new configuration
     */
    public void onConfigurationChanged(Object newConfig) {
        // Stub
    }

    // ── DrawerListener delegation ──

    /**
     * This method should be called from the host Activity's
     * {@code onOptionsItemSelected} to handle the toggle press.
     *
     * @param item the menu item that was selected
     * @return {@code true} if the toggle consumed the event
     */
    public boolean onOptionsItemSelected(Object item) {
        return false; // Stub
    }

    // ── Indicator ──

    public void setDrawerIndicatorEnabled(boolean enable) {
        this.mDrawerIndicatorEnabled = enable;
    }

    public boolean isDrawerIndicatorEnabled() {
        return mDrawerIndicatorEnabled;
    }

    public void setHomeAsUpIndicator(Object indicator) {
        // Stub
    }

    public void setHomeAsUpIndicator(int resId) {
        // Stub
    }

    public void setToolbarNavigationClickListener(android.view.View.OnClickListener listener) {
        // Stub
    }

    // ── DrawerListener interface ──

    /**
     * Returns this toggle as a DrawerLayout.DrawerListener for registration.
     */
    public android.support.v4.widget.DrawerLayout.DrawerListener getDrawerListener() {
        return new android.support.v4.widget.DrawerLayout.DrawerListener() {
            @Override public void onDrawerSlide(android.view.View drawerView, float slideOffset) {}
            @Override public void onDrawerOpened(android.view.View drawerView) {}
            @Override public void onDrawerClosed(android.view.View drawerView) {}
            @Override public void onDrawerStateChanged(int newState) {}
        };
    }
}
