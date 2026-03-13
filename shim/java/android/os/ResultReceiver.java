package android.os;

/**
 * Android-compatible ResultReceiver shim. Receives result callbacks.
 */
public class ResultReceiver {
    private final Handler mHandler;

    public ResultReceiver(Handler handler) {
        mHandler = handler;
    }

    public final void send(final int resultCode, final Bundle resultData) {
        if (mHandler != null) {
            mHandler.post(() -> onReceiveResult(resultCode, resultData));
        } else {
            onReceiveResult(resultCode, resultData);
        }
    }

    protected void onReceiveResult(int resultCode, Bundle resultData) {
        // Override in subclasses
    }
}
