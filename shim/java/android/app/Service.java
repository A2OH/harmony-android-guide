package android.app;

public class Service {
    private boolean mStopRequested;
    private int mLastStartId;

    public Service() {}

    public static final int START_CONTINUATION_MASK = 0;
    public static final int START_FLAG_REDELIVERY = 1;
    public static final int START_FLAG_RETRY = 2;
    public static final int START_NOT_STICKY = 2;
    public static final int START_REDELIVER_INTENT = 3;
    public static final int START_STICKY = 1;
    public static final int START_STICKY_COMPATIBILITY = 0;
    public static final int STOP_FOREGROUND_DETACH = 2;
    public static final int STOP_FOREGROUND_REMOVE = 1;

    public void dump(Object p0, Object p1, Object p2) {}
    public Object getApplication() { return null; }
    public int getForegroundServiceType() { return 0; }
    public void onConfigurationChanged(Object p0) {}
    public void onCreate() {}
    public void onDestroy() {}
    public void onLowMemory() {}
    public void onRebind(Object p0) {}

    public int onStartCommand(android.content.Intent intent, int flags, int startId) {
        mLastStartId = startId;
        return START_STICKY;
    }

    public android.os.IBinder onBind(android.content.Intent intent) { return null; }

    public void onTaskRemoved(Object p0) {}
    public void onTrimMemory(Object p0) {}
    public boolean onUnbind(Object p0) { return false; }
    public void startForeground(Object p0, Object p1) {}
    public void startForeground(Object p0, Object p1, Object p2) {}
    public void stopForeground(Object p0) {}

    public void stopSelf() {
        mStopRequested = true;
        onDestroy();
    }

    public boolean stopSelf(int startId) {
        if (startId == mLastStartId) {
            mStopRequested = true;
            onDestroy();
            return true;
        }
        return false;
    }

    public boolean isStopRequested() { return mStopRequested; }

    public boolean stopSelfResult(int startId) {
        if (startId == mLastStartId) {
            mStopRequested = true;
            onDestroy();
            return true;
        }
        return false;
    }
}
