package android.app;

public class Service {
    public Service() {}

    public static final int START_CONTINUATION_MASK = 0;
    public static final int START_FLAG_REDELIVERY = 0;
    public static final int START_FLAG_RETRY = 0;
    public static final int START_NOT_STICKY = 0;
    public static final int START_REDELIVER_INTENT = 0;
    public static final int START_STICKY = 0;
    public static final int START_STICKY_COMPATIBILITY = 0;
    public static final int STOP_FOREGROUND_DETACH = 0;
    public static final int STOP_FOREGROUND_REMOVE = 0;
    public void dump(Object p0, Object p1, Object p2) {}
    public Object getApplication() { return null; }
    public int getForegroundServiceType() { return 0; }
    public void onConfigurationChanged(Object p0) {}
    public void onCreate() {}
    public void onDestroy() {}
    public void onLowMemory() {}
    public void onRebind(Object p0) {}
    public int onStartCommand(Object p0, Object p1, Object p2) { return 0; }
    public void onTaskRemoved(Object p0) {}
    public void onTrimMemory(Object p0) {}
    public boolean onUnbind(Object p0) { return false; }
    public void startForeground(Object p0, Object p1) {}
    public void startForeground(Object p0, Object p1, Object p2) {}
    public void stopForeground(Object p0) {}
    public void stopSelf() {}
    public void stopSelf(Object p0) {}
    public boolean stopSelfResult(Object p0) { return false; }
}
