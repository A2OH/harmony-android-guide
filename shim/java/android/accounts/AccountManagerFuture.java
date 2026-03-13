package android.accounts;

import java.util.concurrent.TimeUnit;

/**
 * Android-compatible AccountManagerFuture shim. Stub interface.
 */
public interface AccountManagerFuture<V> {
    V getResult() throws AccountsException;
    V getResult(long timeout, TimeUnit unit) throws AccountsException;
    boolean cancel(boolean mayInterruptIfRunning);
    boolean isCancelled();
    boolean isDone();
}
