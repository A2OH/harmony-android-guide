package android.content;

/**
 * Android-compatible SyncStatusObserver shim.
 * Interface for observing sync status changes.
 */
public interface SyncStatusObserver {

    /**
     * Called when the sync status changes.
     *
     * @param which a bit-mask of sync status changes
     */
    void onStatusChanged(int which);
}
