package android.os;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Android-compatible AsyncTask shim.
 * Uses a simple background thread. Callbacks are delivered on the thread
 * that called execute() (matches Android's main-thread contract well enough
 * for headless / migration use cases).
 *
 * Type parameters:
 *   Params  – input parameter type passed to doInBackground()
 *   Progress – intermediate progress type published via publishProgress()
 *   Result  – output type returned by doInBackground() and received by onPostExecute()
 */
public abstract class AsyncTask<Params, Progress, Result> {

    public enum Status { PENDING, RUNNING, FINISHED }

    private static final Executor DEFAULT_EXECUTOR =
            Executors.newCachedThreadPool(r -> {
                Thread t = new Thread(r, "AsyncTask-thread");
                t.setDaemon(true);
                return t;
            });

    private volatile Status mStatus = Status.PENDING;
    private volatile boolean mCancelled = false;

    // --- Abstract ---

    @SuppressWarnings("unchecked")
    protected abstract Result doInBackground(Params... params);

    // --- Optional overrides ---

    protected void onPreExecute() {}

    protected void onPostExecute(Result result) {}

    @SuppressWarnings("unchecked")
    protected void onProgressUpdate(Progress... values) {}

    protected void onCancelled(Result result) { onCancelled(); }

    protected void onCancelled() {}

    // --- Public API ---

    public final Status getStatus() { return mStatus; }

    public final boolean isCancelled() { return mCancelled; }

    public final boolean cancel(boolean mayInterruptIfRunning) {
        if (mStatus == Status.FINISHED) return false;
        mCancelled = true;
        return true;
    }

    @SafeVarargs
    public final AsyncTask<Params, Progress, Result> execute(Params... params) {
        return executeOnExecutor(DEFAULT_EXECUTOR, params);
    }

    @SafeVarargs
    public final AsyncTask<Params, Progress, Result> executeOnExecutor(
            Executor exec, Params... params) {
        if (mStatus != Status.PENDING) {
            throw new IllegalStateException(
                    "AsyncTask can only be executed once. Status=" + mStatus);
        }
        mStatus = Status.RUNNING;
        onPreExecute();
        exec.execute(() -> {
            Result result = null;
            try {
                result = doInBackground(params);
            } catch (Exception e) {
                mCancelled = true;
            } finally {
                mStatus = Status.FINISHED;
                final Result finalResult = result;
                if (mCancelled) {
                    onCancelled(finalResult);
                } else {
                    onPostExecute(finalResult);
                }
            }
        });
        return this;
    }

    @SafeVarargs
    protected final void publishProgress(Progress... values) {
        if (!mCancelled) {
            onProgressUpdate(values);
        }
    }
}
