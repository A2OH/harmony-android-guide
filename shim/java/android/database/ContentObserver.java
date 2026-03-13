package android.database;
import android.net.Uri;
import android.os.Handler;

public class ContentObserver {
    public ContentObserver(Handler p0) {}

    public boolean deliverSelfNotifications() { return false; }
    public void dispatchChange(boolean p0, Uri p1) {}
    public void dispatchChange(boolean p0, Uri p1, int p2) {}
    public void onChange(boolean p0) {}
    public void onChange(boolean p0, Uri p1) {}
    public void onChange(boolean p0, Uri p1, int p2) {}
}
