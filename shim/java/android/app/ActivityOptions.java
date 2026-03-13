package android.app;

import android.os.Bundle;

/**
 * Android-compatible ActivityOptions shim.
 * Provides transition and launch options for starting activities.
 * Stub implementation — no actual transitions in shim layer.
 */
public class ActivityOptions {

    private Bundle mBundle;

    private ActivityOptions() {
        mBundle = new Bundle();
    }

    /** Create a basic ActivityOptions with default values. */
    public static ActivityOptions makeBasic() {
        return new ActivityOptions();
    }

    /**
     * Create an ActivityOptions for a custom animation transition.
     * @param context   ignored in shim (Object to avoid dependency chain)
     * @param enterResId enter animation resource id
     * @param exitResId  exit animation resource id
     */
    public static ActivityOptions makeCustomAnimation(Object context,
            int enterResId, int exitResId) {
        ActivityOptions opts = new ActivityOptions();
        opts.mBundle.putInt("enterAnimResId", enterResId);
        opts.mBundle.putInt("exitAnimResId", exitResId);
        return opts;
    }

    /**
     * Create an ActivityOptions for a shared-element scene transition.
     * @param activity   ignored in shim (Object to avoid dependency chain)
     * @param sharedElements pairs of (View, String) — ignored in shim
     */
    public static ActivityOptions makeSceneTransitionAnimation(Object activity,
            Object... sharedElements) {
        return new ActivityOptions();
    }

    /** Convert to a Bundle that can be passed to startActivity(). */
    public Bundle toBundle() {
        return mBundle;
    }

    /**
     * Update this ActivityOptions from a Bundle.
     * @param options the options bundle
     */
    public void update(ActivityOptions otherOptions) {
        if (otherOptions != null && otherOptions.mBundle != null) {
            mBundle.putAll(otherOptions.mBundle);
        }
    }

    /**
     * Set the launch display ID for multi-display support.
     * @param launchDisplayId the display ID
     */
    public ActivityOptions setLaunchDisplayId(int launchDisplayId) {
        mBundle.putInt("launchDisplayId", launchDisplayId);
        return this;
    }
}
