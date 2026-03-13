package android.content;
import android.os.IBinder;

public interface ServiceConnection {
    void onBindingDied(ComponentName p0);
    void onNullBinding(ComponentName p0);
    void onServiceConnected(ComponentName p0, IBinder p1);
    void onServiceDisconnected(ComponentName p0);
}
