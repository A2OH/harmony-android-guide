package android.app;

/**
 * Android-compatible LocalActivityManager shim.
 *
 * LocalActivityManager is a deprecated helper class (removed from the public
 * SDK in API 28) that allowed an Activity to host multiple sub-Activities, each
 * rendering into a FrameLayout child. It was the predecessor of Fragments.
 *
 * In the shim layer all methods are no-ops or return {@code null}. The class is
 * provided so that legacy code using LocalActivityManager compiles without
 * modification.
 *
 * @deprecated Use {@link Fragment} and the Fragment back-stack instead.
 */
@Deprecated
public class LocalActivityManager {

    /**
     * @param activity   the parent Activity that will host the sub-Activities
     *                   (typed as Object to avoid a hard Activity dependency cycle)
     * @param singleMode if {@code true} only one Activity is kept alive at a time
     */
    public LocalActivityManager(Object activity, boolean singleMode) {
        // Stub — no state maintained in the shim layer.
    }

    // ── Lifecycle dispatch ────────────────────────────────────────────────────

    /**
     * Called by the host Activity from its {@code onCreate()} method.
     *
     * @param state the saved-instance Bundle (may be {@code null})
     */
    public void dispatchCreate(Object state) {}

    /**
     * Called by the host Activity from its {@code onResume()} method.
     */
    public void dispatchResume() {}

    /**
     * Called by the host Activity from its {@code onPause()} method.
     *
     * @param finishing {@code true} if the host is finishing
     */
    public void dispatchPause(boolean finishing) {}

    /**
     * Called by the host Activity from its {@code onStop()} method.
     */
    public void dispatchStop() {}

    /**
     * Called by the host Activity from its {@code onDestroy()} method.
     */
    public void dispatchDestroy(boolean finishing) {}

    // ── Sub-Activity management ───────────────────────────────────────────────

    /**
     * Start a sub-Activity identified by {@code id}.
     *
     * @param id     a unique string key for this sub-Activity instance
     * @param intent the Intent describing the sub-Activity to start
     *               (typed as Object to avoid pulling in the full Intent shim)
     * @return a Window object (always {@code null} in the shim)
     */
    public Object startActivity(String id, Object intent) {
        return null;
    }

    /**
     * Destroy the currently running sub-Activity.
     *
     * @return the Window of the Activity being destroyed, or {@code null}
     */
    public Object destroyCurrent() {
        return null;
    }

    /**
     * Destroy and remove all sub-Activities managed by this instance.
     */
    public void removeAllActivities() {}

    /**
     * Return the Activity that was most recently started, or {@code null}
     * if none has been started yet.
     *
     * @return {@code null} in the shim layer
     */
    public Object getCurrentActivity() {
        return null;
    }

    /**
     * Return the sub-Activity identified by {@code id}, or {@code null}
     * if it has not been started or has been destroyed.
     *
     * @param id the unique key passed to {@link #startActivity(String, Object)}
     * @return {@code null} in the shim layer
     */
    public Object getActivity(String id) {
        return null;
    }

    /**
     * Save the current state of all managed sub-Activities.
     *
     * @return a Bundle containing the saved state, or {@code null} in the shim
     */
    public Object saveInstanceState() {
        return null;
    }
}
