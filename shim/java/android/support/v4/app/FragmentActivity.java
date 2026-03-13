package android.support.v4.app;

import android.app.Activity;
import android.os.Bundle;

/**
 * Android-compatible FragmentActivity shim.
 *
 * FragmentActivity is the base class for activities that use the support-library
 * Fragment API (android.support.v4.app.Fragment). It extends Activity and adds
 * getSupportFragmentManager() to obtain the support FragmentManager.
 *
 * OH mapping:
 * - Activity lifecycle → UIAbility lifecycle + @Entry @Component page events.
 * - getSupportFragmentManager() → ArkUI Navigation stack / router.
 *
 * Apps that extend FragmentActivity should migrate to ArkUI's declarative
 * Navigation component on OpenHarmony, using NavPathStack for back-stack control.
 */
public class FragmentActivity extends Activity {

    private final FragmentManager mFragmentManager = new FragmentManager();

    // ── Fragment management ────────────────────────────────────────────────────

    /**
     * Returns the support FragmentManager for managing Fragments attached to
     * this activity.
     *
     * OH mapping: replace with ArkUI NavPathStack / Navigation component.
     *
     * @return the support FragmentManager for this activity
     */
    public FragmentManager getSupportFragmentManager() {
        return mFragmentManager;
    }

    // ── Lifecycle forwarding ───────────────────────────────────────────────────

    /**
     * Called when the activity is first created.
     * Subclasses must call super.onCreate(savedInstanceState).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart()   { super.onStart(); }

    @Override
    protected void onResume()  { super.onResume(); }

    @Override
    protected void onPause()   { super.onPause(); }

    @Override
    protected void onStop()    { super.onStop(); }

    @Override
    protected void onDestroy() { super.onDestroy(); }

    // ── Permissions result ────────────────────────────────────────────────────

    /**
     * Called when a permission request result is received. Fragments that
     * requested permissions via Fragment.requestPermissions() will have their
     * onRequestPermissionsResult() called from here.
     *
     * Stub — no-op in this shim. Override in subclasses as needed.
     */
    public void onRequestPermissionsResult(int requestCode,
                                            String[] permissions,
                                            int[] grantResults) {
        // no-op stub
    }

    // ── Activity result ───────────────────────────────────────────────────────

    /**
     * Called when an activity launched with startActivityForResult() returns.
     * Forwards the result to the appropriate Fragment if needed.
     *
     * Stub — no-op in this shim. Override in subclasses as needed.
     */
    public void onActivityResult(int requestCode, int resultCode, Object data) {
        // no-op stub
    }
}
