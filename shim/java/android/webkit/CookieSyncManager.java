package android.webkit;

/**
 * Shim for android.webkit.CookieSyncManager.
 *
 * @deprecated Use {@link CookieManager} directly; CookieSyncManager is a no-op
 *             since Android API 21 (Lollipop).
 */
@Deprecated
public class CookieSyncManager {

    private static CookieSyncManager sInstance;

    protected CookieSyncManager() {}

    /**
     * Returns the singleton instance.
     *
     * @deprecated {@link #createInstance(Object)} must be called before this
     *             method on API levels below 21; on 21+ it is a no-op.
     */
    @Deprecated
    public static CookieSyncManager getInstance() {
        if (sInstance == null) {
            sInstance = new CookieSyncManager();
        }
        return sInstance;
    }

    /**
     * Creates the singleton CookieSyncManager using the given context.
     * Should be called from the application's main thread before any WebView
     * is created (required on API < 21, no-op on API >= 21).
     *
     * @param context an android.content.Context (typed as Object to avoid a
     *                hard dependency)
     * @return the singleton instance
     * @deprecated No longer necessary; retained for source compatibility.
     */
    @Deprecated
    public static CookieSyncManager createInstance(Object context) {
        if (sInstance == null) {
            sInstance = new CookieSyncManager();
        }
        return sInstance;
    }

    /**
     * Synchronises the cookie database to persistent storage immediately.
     *
     * @deprecated Use {@link CookieManager#flush()} instead.
     */
    @Deprecated
    public void sync() {
        // Stub: no-op in shim
    }

    /**
     * Starts periodic synchronisation of cookies to persistent storage.
     *
     * @deprecated Use {@link CookieManager#flush()} instead.
     */
    @Deprecated
    public void startSync() {
        // Stub: no-op in shim
    }

    /**
     * Stops periodic synchronisation.
     *
     * @deprecated Use {@link CookieManager#flush()} instead.
     */
    @Deprecated
    public void stopSync() {
        // Stub: no-op in shim
    }
}
