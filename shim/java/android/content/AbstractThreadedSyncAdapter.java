package android.content;
import android.accounts.Account;
import android.os.Bundle;
import android.os.IBinder;

public class AbstractThreadedSyncAdapter {
    public AbstractThreadedSyncAdapter(Context p0, boolean p1) {}
    public AbstractThreadedSyncAdapter(Context p0, boolean p1, boolean p2) {}

    public Context getContext() { return null; }
    public IBinder getSyncAdapterBinder() { return null; }
    public void onPerformSync(Account p0, Bundle p1, String p2, ContentProviderClient p3, SyncResult p4) {}
    public void onSecurityException(Account p0, Bundle p1, String p2, SyncResult p3) {}
    public void onSyncCanceled() {}
    public void onSyncCanceled(Thread p0) {}
}
