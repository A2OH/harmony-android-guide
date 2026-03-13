package android.app;

/**
 * Android-compatible LoaderManager shim. Stub abstract — all operations are no-ops.
 */
public abstract class LoaderManager {

    public abstract Object initLoader(int id, Object args, LoaderCallbacks<?> callback);
    public abstract Object restartLoader(int id, Object args, LoaderCallbacks<?> callback);
    public abstract void destroyLoader(int id);
    public abstract Object getLoader(int id);

    public interface LoaderCallbacks<D> {
        Object onCreateLoader(int id, Object args);
        void onLoadFinished(Object loader, D data);
        void onLoaderReset(Object loader);
    }
}
