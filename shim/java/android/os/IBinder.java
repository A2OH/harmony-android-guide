package android.os;

import java.io.FileDescriptor;

/**
 * Shim: android.os.IBinder — interface for remotable objects.
 */
public interface IBinder {

    String getInterfaceDescriptor() throws RemoteException;
    boolean pingBinder();
    boolean isBinderAlive();
    IInterface queryLocalInterface(String descriptor);
    void dump(FileDescriptor fd, String[] args) throws RemoteException;
    void dumpAsync(FileDescriptor fd, String[] args) throws RemoteException;
    boolean transact(int code, Parcel data, Parcel reply, int flags) throws RemoteException;
    void linkToDeath(DeathRecipient recipient, int flags) throws RemoteException;
    boolean unlinkToDeath(DeathRecipient recipient, int flags);

    interface DeathRecipient {
        void binderDied();
    }
}
