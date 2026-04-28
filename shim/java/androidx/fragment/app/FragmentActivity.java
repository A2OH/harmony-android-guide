package androidx.fragment.app;

import android.os.Bundle;
import androidx.activity.ComponentActivity;

/**
 * AndroidX FragmentActivity stub. Extends android.app.Activity and provides
 * getSupportFragmentManager() for Jetpack fragment navigation.
 */
public class FragmentActivity extends ComponentActivity {

    private FragmentManager mSupportFragmentManager;

    public FragmentActivity() {
        super();
    }

    /**
     * Returns the support FragmentManager for managing androidx fragments.
     */
    public FragmentManager getSupportFragmentManager() {
        if (mSupportFragmentManager == null) {
            mSupportFragmentManager = new FragmentManager();
            mSupportFragmentManager.setHost(this);
        }
        return mSupportFragmentManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Dispatch onBackPressed to support fragments before handling locally.
     */
    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            return;
        }
        super.onBackPressed();
    }
}
