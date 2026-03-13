package android.os;
import android.util.Printer;

public class Handler {
    public Handler(android.os.Looper looper, Object callback) {}
    public Handler() {}
    public Handler(android.os.Looper looper) {}

    public void dispatchMessage(Message p0) {}
    public void dump(Printer p0, String p1) {}
    public void handleMessage(Message p0) {}
    public boolean hasCallbacks(Runnable p0) { return false; }
    public boolean hasMessages(int p0) { return false; }
    public boolean hasMessages(int p0, Object p1) { return false; }
    public boolean post(Runnable p0) { return false; }
    public boolean postAtFrontOfQueue(Runnable p0) { return false; }
    public boolean postAtTime(Runnable p0, long p1) { return false; }
    public boolean postAtTime(Runnable p0, Object p1, long p2) { return false; }
    public boolean postDelayed(Runnable p0, long p1) { return false; }
    public boolean postDelayed(Runnable p0, Object p1, long p2) { return false; }
    public void removeCallbacks(Runnable p0) {}
    public void removeCallbacks(Runnable p0, Object p1) {}
    public void removeCallbacksAndMessages(Object p0) {}
    public void removeMessages(int p0) {}
    public void removeMessages(int p0, Object p1) {}
    public boolean sendEmptyMessage(int p0) { return false; }
    public boolean sendEmptyMessageAtTime(int p0, long p1) { return false; }
    public boolean sendEmptyMessageDelayed(int p0, long p1) { return false; }
    public boolean sendMessage(Message p0) { return false; }
    public boolean sendMessageAtFrontOfQueue(Message p0) { return false; }
    public boolean sendMessageAtTime(Message p0, long p1) { return false; }
    public boolean sendMessageDelayed(Message p0, long p1) { return false; }
}
