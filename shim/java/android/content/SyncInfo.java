package android.content;

/**
 * Android-compatible SyncInfo shim.
 * Represents information about a currently active sync operation.
 * Stub — holds data values only, no system sync interaction.
 */
public class SyncInfo {

    /**
     * The id of the SyncAdapter's authority row in the authority database.
     */
    public final int authorityId;

    /**
     * The {@link android.accounts.Account} that is being synced.
     * Typed as Object to avoid dependency on the Account shim.
     */
    public final Object account;

    /**
     * The content authority being synced (e.g. "com.android.contacts").
     */
    public final String authority;

    /**
     * The time at which the sync started, in milliseconds since boot
     * (as reported by {@code SystemClock.elapsedRealtime()}).
     */
    public final long startTime;

    public SyncInfo(int authorityId, Object account, String authority, long startTime) {
        this.authorityId = authorityId;
        this.account = account;
        this.authority = authority;
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "SyncInfo{authorityId=" + authorityId
                + ", account=" + account
                + ", authority=" + authority
                + ", startTime=" + startTime + "}";
    }
}
