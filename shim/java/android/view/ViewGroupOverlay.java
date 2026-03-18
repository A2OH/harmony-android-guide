package android.view;

import android.content.Context;

/**
 * Shim: android.view.ViewGroupOverlay -- overlay layer for a ViewGroup.
 */
public class ViewGroupOverlay extends ViewOverlay {

    public ViewGroupOverlay() {}
    public ViewGroupOverlay(Context context, View hostView) {}

    public void add(View view) {
        // no-op
    }

    public void remove(View view) {
        // no-op
    }
}
