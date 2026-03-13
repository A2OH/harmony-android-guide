package android.content;

/**
 * Android-compatible AsyncTaskLoader shim. Pure Java stub.
 * Abstract Loader that runs loadInBackground() on a background thread
 * (in real Android); in this shim the call is synchronous / no-op.
 *
 * @param <D> the result data type
 */
public abstract class AsyncTaskLoader<D> extends Loader<D> {

    private volatile boolean mCanceled;

    public AsyncTaskLoader(Object context) {
        super(context);
    }

    // ──────────────────────────────────────────────────────────
    // Abstract
    // ──────────────────────────────────────────────────────────

    /**
     * Subclasses implement this to perform the actual load.
     * In real Android this runs on a worker thread; here it is a stub.
     */
    public abstract D loadInBackground();

    // ──────────────────────────────────────────────────────────
    // Lifecycle overrides
    // ──────────────────────────────────────────────────────────

    @Override
    protected void onStartLoading() {
        // stub — in real implementation would schedule loadInBackground
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    /**
     * Called when a previously started load is canceled.
     * Subclasses may release resources here.
     */
    protected void onCanceled(D data) {
        // stub
    }

    @Override
    protected void onReset() {
        cancelLoad();
    }

    // ──────────────────────────────────────────────────────────
    // Load control
    // ──────────────────────────────────────────────────────────

    /**
     * Requests that the loader abandon its current load and start a fresh one.
     * Stub — no-op in shim layer.
     */
    public void forceLoad() {
        mCanceled = false;
        // In a real impl would re-schedule loadInBackground
    }

    /**
     * Attempts to cancel a pending load.
     * @return true if the cancel was posted (stub always returns false)
     */
    public boolean cancelLoad() {
        mCanceled = true;
        return false; // stub
    }

    public boolean isLoadInBackgroundCanceled() {
        return mCanceled;
    }
}
