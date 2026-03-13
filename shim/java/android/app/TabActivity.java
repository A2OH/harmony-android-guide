package android.app;

import android.widget.TabHost;
import android.widget.TabWidget;

/**
 * Android-compatible TabActivity shim. Stub — tab management is no-op.
 */
public class TabActivity extends Activity {
    private TabHost mTabHost = new TabHost();
    private String mDefaultTab;

    public TabHost getTabHost() {
        return mTabHost;
    }

    public TabWidget getTabWidget() {
        return mTabHost.getTabWidget();
    }

    public void setDefaultTab(String tag) {
        mDefaultTab = tag;
    }

    public void setDefaultTab(int index) {}
}
