package android.content;

/**
 * Android-compatible AbstractThreadedSyncAdapter shim. Stub abstract class.
 */
public abstract class AbstractThreadedSyncAdapter {
    public AbstractThreadedSyncAdapter(Object context, boolean autoInitialize) {
        // stub
    }

    public AbstractThreadedSyncAdapter(Object context, boolean autoInitialize,
            boolean allowParallelSyncs) {
        // stub
    }

    public abstract void onPerformSync(Object account, Object extras,
            String authority, ContentProviderClient provider, SyncResult syncResult);

    public void onSyncCanceled() {
        // stub
    }

    public void onSyncCanceled(Thread thread) {
        // stub
    }
}
