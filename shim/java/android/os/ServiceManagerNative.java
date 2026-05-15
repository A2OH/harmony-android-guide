// SPDX-License-Identifier: Apache-2.0
//
// Westlake M3 — shim/java/android/os/ServiceManagerNative.java
//
// Legacy compat: AOSP's framework.jar references `ServiceManagerNative
// .asInterface(IBinder)` from its own ServiceManager.java getIServiceManager()
// path.  We don't ship that path in M3 (our shim ServiceManager skips
// IServiceManager and goes Java -> JNI -> libbinder directly).  Keep this
// type around as an empty class so any reflective lookup (e.g., a framework
// utility class checking `Class.forName("android.os.ServiceManagerNative")`)
// still resolves.  Field IServiceManager.STUB_DESCRIPTOR keeps the binder
// transaction descriptor handy.

package android.os;

public final class ServiceManagerNative {
    private ServiceManagerNative() {}

    /**
     * Returns a no-op IServiceManager wrapper around the given binder.  Real
     * binder lookups go via ServiceManager.getService(name) — see that
     * class's docs for the M3 path.  Kept here only so legacy callers in
     * framework code don't ClassNotFoundException.
     */
    public static IServiceManager asInterface(IBinder binder) {
        return new IServiceManager() {
            @Override
            public IBinder getService(String name) throws RemoteException {
                return ServiceManager.getService(name);
            }

            @Override
            public IBinder checkService(String name) throws RemoteException {
                return ServiceManager.checkService(name);
            }

            @Override
            public void addService(String name, IBinder service) throws RemoteException {
                ServiceManager.addService(name, service);
            }

            @Override
            public IBinder asBinder() {
                return binder;
            }
        };
    }
}
