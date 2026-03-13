package android.content;

/**
 * Android-compatible Loader shim. Pure Java stub.
 * Abstract base class for loading data asynchronously.
 *
 * @param <D> the result data type
 */
public abstract class Loader<D> {

    /** Callback interface for load completion. */
    public interface OnLoadCompleteListener<D> {
        void onLoadComplete(Loader<D> loader, D data);
    }

    private final Object mContext;
    private final int mId;
    private boolean mStarted;
    private boolean mReset;
    private OnLoadCompleteListener<D> mListener;

    private static int sNextId = 1;

    public Loader(Object context) {
        mContext = context;
        mId      = sNextId++;
        mReset   = true;
    }

    // ──────────────────────────────────────────────────────────
    // Lifecycle
    // ──────────────────────────────────────────────────────────

    /** Called to start loading. Subclasses override to begin data load. */
    protected void onStartLoading() {}

    /** Called when the loader is stopped. */
    protected void onStopLoading() {}

    /** Called to reset (cancel + abandon) the load. */
    protected void onReset() {}

    /** Starts the load. Calls onStartLoading(). */
    public final void startLoading() {
        mStarted = true;
        mReset   = false;
        onStartLoading();
    }

    /** Stops the load. Calls onStopLoading(). */
    public final void stopLoading() {
        mStarted = false;
        onStopLoading();
    }

    /** Resets the loader to its unstarted state. Calls onReset(). */
    public final void reset() {
        mStarted = false;
        mReset   = true;
        onReset();
    }

    // ──────────────────────────────────────────────────────────
    // Result delivery
    // ──────────────────────────────────────────────────────────

    /**
     * Delivers a result to the registered listener.
     */
    public void deliverResult(D data) {
        if (mListener != null) {
            mListener.onLoadComplete(this, data);
        }
    }

    // ──────────────────────────────────────────────────────────
    // Listener
    // ──────────────────────────────────────────────────────────

    public void registerListener(int id, OnLoadCompleteListener<D> listener) {
        mListener = listener;
    }

    public void unregisterListener(OnLoadCompleteListener<D> listener) {
        if (mListener == listener) mListener = null;
    }

    // ──────────────────────────────────────────────────────────
    // Accessors
    // ──────────────────────────────────────────────────────────

    public int getId()         { return mId; }
    public Object getContext() { return mContext; }
    public boolean isStarted() { return mStarted; }
    public boolean isReset()   { return mReset; }
    public boolean isAbandoned() { return false; } // stub
}
