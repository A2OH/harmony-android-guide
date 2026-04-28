package android.os;

/**
 * Minimal hidden ServiceManager interface used by Westlake bootstrap.
 */
public interface IServiceManager extends IInterface {
    IBinder getService(String name) throws RemoteException;
    IBinder checkService(String name) throws RemoteException;
    void addService(String name, IBinder service) throws RemoteException;
}
