package android.support.v7.app;

import android.app.Activity;

/**
 * Shim: android.support.v7.app.AppCompatActivity
 *
 * Extends the core Activity shim with AndroidX ActionBar / Theme support methods.
 * All ActionBar-specific methods are stubbed because the OH bridge handles
 * navigation-bar rendering natively.
 */
public class AppCompatActivity extends Activity {

    private ActionBar mSupportActionBar;

    // ── Support ActionBar ──

    /**
     * Returns the support ActionBar for this activity.
     * The shim returns a stub ActionBar instance.
     */
    public ActionBar getSupportActionBar() {
        if (mSupportActionBar == null) {
            mSupportActionBar = new ActionBar();
        }
        return mSupportActionBar;
    }

    /**
     * Set a Toolbar as the ActionBar for this Activity.
     * Accepts any Object so callers using android.widget.Toolbar or
     * androidx.appcompat.widget.Toolbar compile without casting.
     *
     * @param toolbar the Toolbar to set as the action bar (may be null)
     */
    public void setSupportActionBar(Object toolbar) {
        // Stub — visual integration handled by the OH bridge
    }

    // ── Delegate lifecycle hooks ──

    /**
     * Called when the options menu is created.  Override to populate the menu.
     */
    public boolean onCreateOptionsMenu(Object menu) {
        return true;
    }

    /**
     * Called when an options menu item is selected.
     */
    public boolean onOptionsItemSelected(Object item) {
        return false;
    }

    // ── Theme / feature stubs ──

    /**
     * Changes whether this activity is shown in a floating window.
     */
    public void setFinishOnTouchOutside(boolean finish) {
        // Stub
    }

    /**
     * Delegate to Activity's requestWindowFeature.
     */
    public boolean supportRequestWindowFeature(int featureId) {
        requestWindowFeature(featureId);
        return true;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Minimal ActionBar stub (returned by getSupportActionBar)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Stub ActionBar that satisfies the most common API calls.
     */
    public static class ActionBar {

        private CharSequence mTitle;
        private CharSequence mSubtitle;
        private boolean mShowHomeAsUp;
        private boolean mVisible = true;

        public void setTitle(CharSequence title) { this.mTitle = title; }
        public void setTitle(int resId)          { this.mTitle = "@" + resId; }
        public CharSequence getTitle()           { return mTitle; }

        public void setSubtitle(CharSequence subtitle) { this.mSubtitle = subtitle; }
        public void setSubtitle(int resId)             { this.mSubtitle = "@" + resId; }
        public CharSequence getSubtitle()              { return mSubtitle; }

        public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {
            this.mShowHomeAsUp = showHomeAsUp;
        }

        public void setHomeButtonEnabled(boolean enabled) {
            // Stub
        }

        public void setDisplayShowHomeEnabled(boolean showHome) {
            // Stub
        }

        public void setDisplayShowTitleEnabled(boolean showTitle) {
            // Stub
        }

        public void setDisplayUseLogoEnabled(boolean useLogo) {
            // Stub
        }

        public void setDisplayOptions(int options) {
            // Stub
        }

        public void setDisplayOptions(int options, int mask) {
            // Stub
        }

        public void setNavigationMode(int mode) {
            // Stub
        }

        public void setCustomView(Object view) {
            // Stub
        }

        public void show() { this.mVisible = true; }
        public void hide() { this.mVisible = false; }

        public boolean isShowing() { return mVisible; }
    }
}
