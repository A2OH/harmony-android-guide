package android.os;

/**
 * Android-compatible CancellationSignal shim. Pure Java.
 */
public class CancellationSignal {
    private volatile boolean mCanceled;
    private OnCancelListener mOnCancelListener;

    public interface OnCancelListener {
        void onCancel();
    }

    public CancellationSignal() {}

    public boolean isCanceled() {
        return mCanceled;
    }

    public void cancel() {
        mCanceled = true;
        OnCancelListener listener = mOnCancelListener;
        if (listener != null) {
            listener.onCancel();
        }
    }

    public void setOnCancelListener(OnCancelListener listener) {
        mOnCancelListener = listener;
    }

    public void throwIfCanceled() {
        if (mCanceled) {
            throw new RuntimeException("Canceled");
        }
    }
}
