package android.net;

/**
 * Android-compatible Credentials shim for OpenHarmony migration.
 * Holds the PID, UID, and GID of the peer process on a Unix domain socket connection.
 */
public class Credentials {
    private final int mPid;
    private final int mUid;
    private final int mGid;

    /**
     * Constructs a Credentials object.
     *
     * @param pid the process ID of the peer
     * @param uid the user ID of the peer
     * @param gid the group ID of the peer
     */
    public Credentials(int pid, int uid, int gid) {
        mPid = pid;
        mUid = uid;
        mGid = gid;
    }

    /** Returns the process ID. */
    public int getPid() {
        return mPid;
    }

    /** Returns the user ID. */
    public int getUid() {
        return mUid;
    }

    /** Returns the group ID. */
    public int getGid() {
        return mGid;
    }

    @Override
    public String toString() {
        return "Credentials{pid=" + mPid + ", uid=" + mUid + ", gid=" + mGid + "}";
    }
}
