package android.accounts;
import android.icu.util.TimeUnit;

public interface AccountManagerFuture {
    boolean cancel(boolean p0);
    Object getResult();
    Object getResult(long p0, TimeUnit p1);
    boolean isCancelled();
    boolean isDone();
}
