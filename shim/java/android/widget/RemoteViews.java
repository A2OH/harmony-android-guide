package android.widget;
import android.app.PendingIntent;
import android.opengl.Visibility;
import android.view.View;
import android.view.ViewGroup;
import android.app.PendingIntent;
import android.opengl.Visibility;
import android.view.View;
import android.view.ViewGroup;

import android.app.PendingIntent;

/**
 * Android-compatible RemoteViews shim.
 * Stub — layout manipulations are recorded but never applied to a real view tree.
 * apply() always returns null.
 */
public class RemoteViews {

    private final String mPackageName;
    private final int    mLayoutId;

    public RemoteViews(String packageName, int layoutId) {
        mPackageName = packageName;
        mLayoutId    = layoutId;
    }

    public String getPackage()   { return mPackageName; }
    public int    getLayoutId()  { return mLayoutId; }

    // --- Text / image ---

    /** Sets the text of a TextView identified by viewId. No-op in shim. */
    public void setTextViewText(int viewId, CharSequence text) {}

    /** Sets the image resource of an ImageView. No-op in shim. */
    public void setImageViewResource(int viewId, int srcId) {}

    /** Sets a text color on a TextView. No-op in shim. */
    public void setTextColor(int viewId, int color) {}

    // --- Visibility ---

    /** Sets the visibility of a view (View.VISIBLE / INVISIBLE / GONE). No-op in shim. */
    public void setViewVisibility(int viewId, int visibility) {}

    // --- Click ---

    /** Assigns a PendingIntent to fire when the view is clicked. No-op in shim. */
    public void setOnClickPendingIntent(int viewId, PendingIntent pendingIntent) {}

    // --- Generic setters ---

    /** Calls an arbitrary int-setter method via reflection on the real platform. No-op in shim. */
    public void setInt(int viewId, String methodName, int value) {}

    /** Calls an arbitrary boolean-setter method via reflection on the real platform. No-op in shim. */
    public void setBoolean(int viewId, String methodName, boolean value) {}

    // --- Child view management ---

    /** Adds a child RemoteViews to a ViewGroup identified by viewId. No-op in shim. */
    public void addView(int viewId, RemoteViews nestedView) {}

    /** Removes all children from a ViewGroup identified by viewId. No-op in shim. */
    public void removeAllViews(int viewId) {}

    // --- Apply ---

    /**
     * Inflate and apply the RemoteViews to a parent.
     * Always returns null in the shim — no real view hierarchy available.
     */
    public Object apply(Object context, Object parent) {
        return null;
    }
}
