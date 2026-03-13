package android.app.slice;

/**
 * Android-compatible SliceProvider shim. Abstract stub — no-op on OpenHarmony.
 *
 * SliceProvider is a ContentProvider subclass that apps implement to supply
 * Slice content to the system. There is no OH equivalent; this shim satisfies
 * compile-time dependencies only.
 *
 * ContentProvider is not available in this shim set, so SliceProvider extends
 * Object directly and re-declares the minimal surface needed.
 *
 * OH migration note: Use FormExtensionAbility for widget/card content instead.
 */
public abstract class SliceProvider {

    private Object mContext;

    // ── ContentProvider surface ────────────────────────────────────────────────

    /** Called by the system when the provider is first created. */
    public boolean onCreate() {
        return true;
    }

    /** Returns the context attached to this provider. */
    public Object getContext() {
        return mContext;
    }

    /** Called by the runtime to attach a context before onCreate(). */
    public void attachContext(Object context) {
        mContext = context;
    }

    // ── Slice-specific API ─────────────────────────────────────────────────────

    /**
     * Implemented by subclasses to return a Slice bound to the given URI.
     * Called by the system when a slice consumer requests content.
     *
     * @param sliceUri the URI of the slice to bind (typed as Object for shim compatibility)
     * @return the Slice to display, or null if the URI is not handled
     */
    public abstract Slice onBindSlice(Object sliceUri);

    /**
     * Called when a slice is pinned (a consumer is actively showing it).
     * Override to start any ongoing work needed to keep the slice up-to-date.
     *
     * @param sliceUri the URI of the slice that was pinned
     */
    public void onSlicePinned(Object sliceUri) {
        // default no-op
    }

    /**
     * Called when a slice is unpinned (all consumers have stopped showing it).
     * Override to stop any ongoing work started in onSlicePinned().
     *
     * @param sliceUri the URI of the slice that was unpinned
     */
    public void onSliceUnpinned(Object sliceUri) {
        // default no-op
    }
}
