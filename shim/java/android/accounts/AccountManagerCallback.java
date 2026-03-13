package android.accounts;

/**
 * Android-compatible AccountManagerCallback shim. Stub interface.
 */
public interface AccountManagerCallback<V> {
    void run(AccountManagerFuture<V> future);
}
