package android.os;

/**
 * A2OH shim: UserHandle - represents an Android user identity.
 */
public final class UserHandle {

    /** The user id of this handle. */
    private final int mHandle;

    // ---- Well-known static instances ----------------------------------------

    /** Sentinel: the "all users" pseudo-handle. */
    public static final UserHandle ALL = new UserHandle(-1);

    /** Sentinel: the current foreground user. */
    public static final UserHandle CURRENT = new UserHandle(-2);

    /** The primary / system user (user 0). */
    public static final UserHandle SYSTEM = new UserHandle(0);

    // ---- Construction -------------------------------------------------------

    public UserHandle(int handle) {
        mHandle = handle;
    }

    // ---- Instance methods ---------------------------------------------------

    /** Returns the numeric user id for this handle. */
    public int getIdentifier() {
        return mHandle;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof UserHandle)) return false;
        return mHandle == ((UserHandle) obj).mHandle;
    }

    @Override
    public int hashCode() {
        return mHandle;
    }

    @Override
    public String toString() {
        return "UserHandle{" + mHandle + "}";
    }

    // ---- Static helpers -----------------------------------------------------

    /** Returns the user id of the calling process (always 0 in the shim). */
    public static int myUserId() {
        return 0;
    }

    /**
     * Returns the UserHandle for the user that owns the given UID.
     * On Android, user id = uid / 100000. The shim always returns user 0.
     */
    public static UserHandle getUserHandleForUid(int uid) {
        return SYSTEM;
    }
}
