package android.content;

/**
 * Android-compatible SyncResult shim. Stub.
 */
public class SyncResult {
    public static final SyncResult ALREADY_IN_PROGRESS = new SyncResult(true);

    public boolean syncAlreadyInProgress;
    public boolean tooManyDeletions;
    public boolean tooManyRetries;
    public boolean databaseError;
    public boolean fullSyncRequested;
    public boolean partialSyncUnavailable;
    public boolean moreRecordsToGet;
    public long delayUntil;

    public SyncResult() {
        this.syncAlreadyInProgress = false;
    }

    private SyncResult(boolean syncAlreadyInProgress) {
        this.syncAlreadyInProgress = syncAlreadyInProgress;
    }

    public boolean hasError() {
        return hasSoftError() || hasHardError();
    }

    public boolean hasSoftError() {
        return syncAlreadyInProgress || moreRecordsToGet;
    }

    public boolean hasHardError() {
        return tooManyDeletions || tooManyRetries || databaseError
                || fullSyncRequested || partialSyncUnavailable;
    }

    @Override
    public String toString() {
        return "SyncResult{syncAlreadyInProgress=" + syncAlreadyInProgress
                + ", tooManyDeletions=" + tooManyDeletions
                + ", tooManyRetries=" + tooManyRetries
                + ", databaseError=" + databaseError
                + ", fullSyncRequested=" + fullSyncRequested
                + ", partialSyncUnavailable=" + partialSyncUnavailable
                + ", moreRecordsToGet=" + moreRecordsToGet
                + ", delayUntil=" + delayUntil + "}";
    }
}
