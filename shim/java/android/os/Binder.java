package android.os;

import java.io.FileDescriptor;

/**
 * Android-compatible Binder shim. Base class for remotable objects.
 * In-process stub — no real IPC. All identity operations use the JVM process.
 */
public class Binder implements IBinder {
    private static final ThreadLocal<long[]> sCallerIdentity = new ThreadLocal<>();

    private String mDescriptor = "";

    public Binder() {}

    public Binder(String descriptor) {
        mDescriptor = descriptor != null ? descriptor : "";
    }

    // --- Static identity helpers (mirrors android.os.Binder statics) ---

    public static int getCallingPid() {
        return Process.myPid();
    }

    public static int getCallingUid() {
        return Process.myUid();
    }

    public static long clearCallingIdentity() {
        long[] prev = sCallerIdentity.get();
        long token = (prev != null) ? ((long) prev[0] << 32 | (prev[1] & 0xFFFFFFFFL)) : 0L;
        sCallerIdentity.set(new long[]{Process.myPid(), Process.myUid()});
        return token;
    }

    public static void restoreCallingIdentity(long token) {
        int pid = (int) (token >> 32);
        int uid = (int) (token & 0xFFFFFFFFL);
        sCallerIdentity.set(new long[]{pid, uid});
    }

    public static void flushPendingCommands() {
        // no-op in shim
    }

    public static void joinThreadPool() {
        // no-op in shim
    }

    // --- IBinder implementation ---

    @Override
    public String getInterfaceDescriptor() throws RemoteException {
        return mDescriptor;
    }

    @Override
    public boolean pingBinder() {
        return true;
    }

    @Override
    public boolean isBinderAlive() {
        return true;
    }

    @Override
    public IInterface queryLocalInterface(String descriptor) {
        if (mDescriptor.equals(descriptor) && this instanceof IInterface) {
            return (IInterface) this;
        }
        return null;
    }

    @Override
    public void dump(FileDescriptor fd, String[] args) throws RemoteException {
        // no-op stub
    }

    @Override
    public void dumpAsync(FileDescriptor fd, String[] args) throws RemoteException {
        // no-op stub
    }

    @Override
    public boolean transact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        return onTransact(code, data, reply, flags);
    }

    @Override
    public void linkToDeath(IBinder.DeathRecipient recipient, int flags) throws RemoteException {
        // no-op — in-process binder never dies
    }

    @Override
    public boolean unlinkToDeath(IBinder.DeathRecipient recipient, int flags) {
        return true;
    }

    /**
     * Override in subclasses to handle incoming transactions.
     */
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        return false;
    }

    public void attachInterface(IInterface owner, String descriptor) {
        mDescriptor = descriptor != null ? descriptor : "";
    }
}
