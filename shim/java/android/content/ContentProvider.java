package android.content;
import android.content.pm.PathPermission;
import android.content.pm.ProviderInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class ContentProvider implements ComponentCallbacks2 {
    public ContentProvider() {}

    public void attachInfo(Context p0, ProviderInfo p1) {}
    public int bulkInsert(Uri p0, ContentValues[] p1) { return 0; }
    public int delete(Uri p0, String p1, String[] p2) { return 0; }
    public int delete(Uri p0, Bundle p1) { return 0; }
    public void dump(FileDescriptor p0, PrintWriter p1, String[] p2) {}
    public boolean isTemporary() { return false; }
    public void onCallingPackageChanged() {}
    public void onConfigurationChanged(Configuration p0) {}
    public boolean onCreate() { return false; }
    public void onLowMemory() {}
    public void onTrimMemory(int p0) {}
    public boolean refresh(Uri p0, Bundle p1, CancellationSignal p2) { return false; }
    public void restoreCallingIdentity(Object p0) {}
    public void setPathPermissions(PathPermission[] p0) {}
    public void setReadPermission(String p0) {}
    public void setWritePermission(String p0) {}
    public void shutdown() {}
    public int update(Uri p0, ContentValues p1, String p2, String[] p3) { return 0; }
    public int update(Uri p0, ContentValues p1, Bundle p2) { return 0; }
}
