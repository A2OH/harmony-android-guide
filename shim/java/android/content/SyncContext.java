package android.content;
import android.os.Binder;
import android.os.IBinder;
import android.os.Binder;
import android.os.IBinder;

/**
 * Android-compatible SyncContext shim.
 * Provides context information for a sync operation and allows the sync adapter
 * to signal completion.
 * Stub — no IPC or system interaction.
 */
public class SyncContext {

    /**
     * The underlying binder object through which this context communicates
     * with the sync manager. Typed as Object to avoid Binder dependency.
     */
    private final Object mSyncContextBinder;

    public SyncContext(Object syncContextBinder) {
        mSyncContextBinder = syncContextBinder;
    }

    /**
     * Signal that the sync is finished.
     *
     * @param result the result of the sync operation; may be null in the shim.
     */
    public void onFinished(Object result) {
        // stub — no-op in shim layer
    }

    /**
     * Returns the IBinder for this sync context, used by the sync manager
     * to communicate back to the caller.
     *
     * @return the binder object (Object in this shim)
     */
    public Object getSyncContextBinder() {
        return mSyncContextBinder;
    }

    @Override
    public String toString() {
        return "SyncContext{binder=" + mSyncContextBinder + "}";
    }
}
