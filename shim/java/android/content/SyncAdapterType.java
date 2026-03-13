package android.content;

/**
 * Android-compatible SyncAdapterType shim. Stub.
 */
public class SyncAdapterType {
    public final String authority;
    public final String accountType;
    public final boolean isKey;
    public final boolean userVisible;
    public final boolean supportsUploading;
    public final boolean allowParallelSyncs;

    public SyncAdapterType(String authority, String accountType) {
        this.authority = authority;
        this.accountType = accountType;
        this.isKey = false;
        this.userVisible = true;
        this.supportsUploading = true;
        this.allowParallelSyncs = false;
    }

    private SyncAdapterType(String authority, String accountType, boolean isKey) {
        this.authority = authority;
        this.accountType = accountType;
        this.isKey = isKey;
        this.userVisible = false;
        this.supportsUploading = false;
        this.allowParallelSyncs = false;
    }

    public static SyncAdapterType newKey(String authority, String accountType) {
        return new SyncAdapterType(authority, accountType, true);
    }

    public boolean isUserVisible() {
        return userVisible;
    }

    public boolean supportsUploading() {
        return supportsUploading;
    }

    public boolean allowParallelSyncs() {
        return allowParallelSyncs;
    }

    @Override
    public String toString() {
        return "SyncAdapterType{authority=" + authority
                + ", accountType=" + accountType + "}";
    }
}
