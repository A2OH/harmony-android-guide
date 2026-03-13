package android.os;

/**
 * Android-compatible Messenger shim. Sends Messages to a Handler across threads.
 */
public final class Messenger {
    private final Handler mHandler;
    private final IBinder mBinder;

    public Messenger(Handler handler) {
        mHandler = handler;
        mBinder  = new BinderStub(handler);
    }

    public Messenger(IBinder binder) {
        mBinder  = binder;
        mHandler = null;
    }

    public void send(Message message) {
        if (mHandler != null) {
            mHandler.sendMessage(message);
        }
    }

    public IBinder getBinder() {
        return mBinder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Messenger)) return false;
        return mBinder == ((Messenger) o).mBinder;
    }

    @Override
    public int hashCode() {
        return mBinder != null ? mBinder.hashCode() : 0;
    }

    // Minimal IBinder implementation backed by a Handler
    private static final class BinderStub implements IBinder {
        private final Handler mHandler;

        BinderStub(Handler handler) {
            mHandler = handler;
        }

        @Override public String getInterfaceDescriptor() { return "android.os.Messenger"; }
        @Override public boolean isBinderAlive()         { return true; }
        @Override public boolean pingBinder()            { return true; }
        @Override public IInterface queryLocalInterface(String descriptor) { return null; }
        @Override public void dump(java.io.FileDescriptor fd, String[] args) {}
        @Override public void dumpAsync(java.io.FileDescriptor fd, String[] args) {}
        @Override public boolean transact(int code, Parcel data, Parcel reply, int flags) { return false; }
        @Override public void linkToDeath(DeathRecipient recipient, int flags) {}
        @Override public boolean unlinkToDeath(DeathRecipient recipient, int flags) { return true; }
    }
}
