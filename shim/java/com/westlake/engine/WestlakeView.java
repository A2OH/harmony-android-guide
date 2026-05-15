/*
 * PF-arch-025: Westlake-owned view-root map (Option B).
 *
 * Activity.setContentView → Window.setContentView → Window.getDecorView()
 * is fundamentally broken in standalone dalvikvm: Activity.attach() can't
 * run without real Context, so mWindow stays null or an abstract Window
 * stub. Rather than rebuilding PhoneWindow + Theme + decor inside our shim,
 * we own rendering — store the activity's view tree in a map here, walk it
 * ourselves from the OHBridge render loop.
 *
 * Apps that call activity.setContentView() still fail at the Window layer
 * (their problem to handle). Shim code that needs to install a content
 * view — tryRecoverContent's programmatic fallback, manual inflate paths
 * — uses WestlakeView.setRoot(activity, view) instead.
 */
package com.westlake.engine;

import java.util.Map;
import java.util.WeakHashMap;
import android.app.Activity;
import android.view.View;

public final class WestlakeView {
    private WestlakeView() {}

    /* Weak-keyed so a destroyed Activity's root view is GC'd without leaking. */
    private static final Map<Activity, View> sRoots = new WeakHashMap<>();

    /* Parallel storage for the data-oriented WestlakeNode tree — the
     * renderer prefers this when present (more useful than empty stub View). */
    private static final Map<Activity, WestlakeNode> sNodes = new WeakHashMap<>();

    /* Parallel storage for the activity's parsed ResourceTable, for renderer lookups. */
    private static final Map<Activity, android.content.res.ResourceTable> sArsc = new WeakHashMap<>();

    public static synchronized void setNode(Activity activity, WestlakeNode root) {
        if (activity == null) return;
        if (root == null) {
            sNodes.remove(activity);
            return;
        }
        sNodes.put(activity, root);
    }

    public static synchronized WestlakeNode getNode(Activity activity) {
        if (activity == null) return null;
        return sNodes.get(activity);
    }

    public static synchronized void setArsc(Activity activity, android.content.res.ResourceTable arsc) {
        if (activity == null) return;
        if (arsc == null) {
            sArsc.remove(activity);
            return;
        }
        sArsc.put(activity, arsc);
    }

    public static synchronized android.content.res.ResourceTable getArsc(Activity activity) {
        if (activity == null) return null;
        return sArsc.get(activity);
    }

    /* PF-arch-031: parallel storage for the activity's resolved WestlakeTheme. */
    private static final Map<Activity, WestlakeTheme> sThemes = new WeakHashMap<>();

    public static synchronized void setTheme(Activity activity, WestlakeTheme theme) {
        if (activity == null) return;
        if (theme == null) { sThemes.remove(activity); return; }
        sThemes.put(activity, theme);
    }

    public static synchronized WestlakeTheme getTheme(Activity activity) {
        if (activity == null) return null;
        return sThemes.get(activity);
    }

    public static synchronized void setRoot(Activity activity, View root) {
        if (activity == null) return;
        if (root == null) {
            sRoots.remove(activity);
            return;
        }
        sRoots.put(activity, root);
    }

    public static synchronized View getRoot(Activity activity) {
        if (activity == null) return null;
        return sRoots.get(activity);
    }

    public static synchronized boolean hasRoot(Activity activity) {
        if (activity == null) return false;
        return sRoots.containsKey(activity);
    }

    public static synchronized void clear(Activity activity) {
        if (activity == null) return;
        sRoots.remove(activity);
    }
}
