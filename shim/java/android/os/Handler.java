package android.os;
import android.util.Printer;

public class Handler {

    public interface Callback {
        boolean handleMessage(Message msg);
    }

    private Looper mLooper;
    private Callback mCallback;

    public Handler() {
        mLooper = Looper.myLooper();
    }

    public Handler(Looper looper) {
        mLooper = looper;
    }

    public Handler(Callback callback) {
        mLooper = Looper.myLooper();
        mCallback = callback;
    }

    public Handler(Looper looper, Callback callback) {
        mLooper = looper;
        mCallback = callback;
    }

    // Keep old constructor for source compat with code passing Object
    public Handler(Looper looper, Object callback) {
        mLooper = looper;
        if (callback instanceof Callback) mCallback = (Callback) callback;
    }

    public static Handler createAsync(Looper looper) {
        return new Handler(looper);
    }

    public static Handler createAsync(Looper looper, Callback callback) {
        return new Handler(looper, callback);
    }

    public Looper getLooper() { return mLooper; }

    public void dispatchMessage(Message msg) {
        if (msg.callback != null) {
            msg.callback.run();
        } else if (mCallback != null) {
            if (mCallback.handleMessage(msg)) return;
        } else {
            handleMessage(msg);
        }
    }

    public void dump(Printer p0, String p1) {}

    public void handleMessage(Message msg) {}

    public boolean hasCallbacks(Runnable p0) { return false; }
    public boolean hasMessages(int p0) { return false; }
    public boolean hasMessages(int p0, Object p1) { return false; }

    public boolean post(Runnable r) {
        return sendMessageDelayed(Message.obtain(this, r), 0);
    }

    public boolean postAtFrontOfQueue(Runnable r) {
        return sendMessageDelayed(Message.obtain(this, r), 0);
    }

    public boolean postAtTime(Runnable r, long uptimeMillis) {
        Message msg = Message.obtain(this, r);
        msg.target = this;
        Looper looper = mLooper != null ? mLooper : Looper.getMainLooper();
        return looper.getQueue().enqueueMessage(msg, uptimeMillis);
    }

    public boolean postAtTime(Runnable r, Object token, long uptimeMillis) {
        return postAtTime(r, uptimeMillis);
    }

    public boolean postDelayed(Runnable r, long delayMillis) {
        return sendMessageDelayed(Message.obtain(this, r), delayMillis);
    }

    public boolean postDelayed(Runnable r, Object token, long delayMillis) {
        return postDelayed(r, delayMillis);
    }

    public void removeCallbacks(Runnable p0) {}
    public void removeCallbacks(Runnable p0, Object p1) {}
    public void removeCallbacksAndMessages(Object p0) {}
    public void removeMessages(int p0) {}
    public void removeMessages(int p0, Object p1) {}

    public boolean sendEmptyMessage(int what) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.target = this;
        return sendMessage(msg);
    }

    public boolean sendEmptyMessageAtTime(int what, long uptimeMillis) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.target = this;
        Looper looper = mLooper != null ? mLooper : Looper.getMainLooper();
        return looper.getQueue().enqueueMessage(msg, uptimeMillis);
    }

    public boolean sendEmptyMessageDelayed(int what, long delayMillis) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.target = this;
        return sendMessageDelayed(msg, delayMillis);
    }

    public boolean sendMessage(Message msg) {
        return sendMessageDelayed(msg, 0);
    }

    public boolean sendMessageAtFrontOfQueue(Message msg) {
        return sendMessageDelayed(msg, 0);
    }

    public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
        msg.target = this;
        Looper looper = mLooper != null ? mLooper : Looper.getMainLooper();
        return looper.getQueue().enqueueMessage(msg, uptimeMillis);
    }

    public boolean sendMessageDelayed(Message msg, long delayMillis) {
        msg.target = this;
        long when = SystemClock.uptimeMillis() + delayMillis;
        Looper looper = mLooper != null ? mLooper : Looper.getMainLooper();
        return looper.getQueue().enqueueMessage(msg, when);
    }

    public Message obtainMessage(int what) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.target = this;
        return msg;
    }

    public Message obtainMessage(int what, Object obj) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        msg.target = this;
        return msg;
    }

    public Message obtainMessage(int what, int arg1, int arg2) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.target = this;
        return msg;
    }
}
