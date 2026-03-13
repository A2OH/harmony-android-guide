package android.view;

import android.content.Context;
import android.content.ContextWrapper;

/**
 * Shim: android.view.ContextThemeWrapper — pure Java stub.
 * A context wrapper that allows modification of the theme from what is in the
 * wrapped context. Extends android.content.ContextWrapper.
 */
public class ContextThemeWrapper extends ContextWrapper {

    private int mThemeResource;

    /** Create a new ContextThemeWrapper with no theme and no base context. */
    public ContextThemeWrapper() {
        super(null);
    }

    /**
     * Create a new ContextThemeWrapper with the given base context and theme.
     * @param base the base context
     * @param themeResId the theme resource id
     */
    public ContextThemeWrapper(Context base, int themeResId) {
        super(base);
        mThemeResource = themeResId;
    }

    /**
     * Set the theme resource for this context.
     * @param resid the theme resource id
     */
    public void setTheme(int resid) {
        mThemeResource = resid;
    }

    /** Return the theme resource id. */
    public int getThemeResId() {
        return mThemeResource;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }
}
