package android.os;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Android-compatible CountDownTimer shim. Pure Java using java.util.Timer.
 */
public class CountDownTimer {
    private final long mMillisInFuture;
    private final long mCountdownInterval;
    private Timer mTimer;
    private boolean mCancelled;

    public CountDownTimer(long millisInFuture, long countDownInterval) {
        mMillisInFuture = millisInFuture;
        mCountdownInterval = countDownInterval;
    }

    public synchronized final CountDownTimer start() {
        mCancelled = false;
        if (mMillisInFuture <= 0) {
            onFinish();
            return this;
        }
        mTimer = new Timer(true);
        final long startTime = System.currentTimeMillis();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                synchronized (CountDownTimer.this) {
                    if (mCancelled) return;
                    long elapsed = System.currentTimeMillis() - startTime;
                    long remaining = mMillisInFuture - elapsed;
                    if (remaining <= 0) {
                        cancel();
                        onFinish();
                    } else {
                        onTick(remaining);
                    }
                }
            }
        }, 0, mCountdownInterval);
        return this;
    }

    public synchronized final void cancel() {
        mCancelled = true;
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    public void onTick(long millisUntilFinished) {}
    public void onFinish() {}
}
