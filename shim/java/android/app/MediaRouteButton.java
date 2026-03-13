package android.app;
import android.view.View;
import android.view.View;

import android.view.View;

/**
 * Android-compatible MediaRouteButton shim. Stub — media routing is no-op.
 */
public class MediaRouteButton extends View {
    private int mRouteTypes;

    public MediaRouteButton() {}
    public MediaRouteButton(Object context) { super(); }

    public void setRouteTypes(int types) {
        mRouteTypes = types;
    }

    public int getRouteTypes() {
        return mRouteTypes;
    }

    public void showDialog() {}
}
