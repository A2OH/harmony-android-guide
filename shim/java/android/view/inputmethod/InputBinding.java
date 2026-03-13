package android.view.inputmethod;

/**
 * Android-compatible InputBinding shim.
 * Information given to an InputMethod about a client connecting to it.
 */
public class InputBinding {
    private InputConnection mConnection;
    private int mUid;
    private int mPid;

    public InputBinding(InputConnection conn, int uid, int pid) {
        mConnection = conn;
        mUid = uid;
        mPid = pid;
    }

    public InputConnection getConnection() {
        return mConnection;
    }

    public int getUid() {
        return mUid;
    }

    public int getPid() {
        return mPid;
    }
}
