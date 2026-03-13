package android.view;

/**
 * Android-compatible MenuInflater shim.
 * Stub: inflate() is a no-op.
 */
public class MenuInflater {
    private final Object mContext;

    public MenuInflater(Object context) {
        mContext = context;
    }

    /** Inflate a menu resource into the given menu — no-op stub. */
    public void inflate(int menuRes, Object menu) {
        // no-op
    }
}
