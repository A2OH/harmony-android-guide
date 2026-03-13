package android.os;

/**
 * Android-compatible ConditionVariable shim. Pure Java synchronization primitive.
 */
public class ConditionVariable {
    private volatile boolean mCondition;

    public ConditionVariable() {
        mCondition = false;
    }

    public ConditionVariable(boolean state) {
        mCondition = state;
    }

    public synchronized void open() {
        mCondition = true;
        notifyAll();
    }

    public synchronized void close() {
        mCondition = false;
    }

    public synchronized void block() {
        while (!mCondition) {
            try { wait(); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    public synchronized boolean block(long timeoutMs) {
        if (mCondition) return true;
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (!mCondition) {
            long remaining = deadline - System.currentTimeMillis();
            if (remaining <= 0) return mCondition;
            try { wait(remaining); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return mCondition;
            }
        }
        return true;
    }
}
