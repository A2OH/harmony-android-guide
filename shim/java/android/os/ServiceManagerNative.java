package android.os;

import java.util.Map;

/**
 * Minimal hidden ServiceManagerNative shim. AOSP exposes a static
 * {@code asInterface(IBinder)} helper; Westlake reflects it during bootstrap.
 */
public final class ServiceManagerNative {
    private ServiceManagerNative() {}

    public static IServiceManager asInterface(IBinder binder) {
        if (binder instanceof IServiceManager) {
            return (IServiceManager) binder;
        }
        return new LocalServiceManager(binder);
    }

    private static final class LocalServiceManager extends Binder implements IServiceManager {
        private final IBinder remote;

        LocalServiceManager(IBinder remote) {
            super("android.os.IServiceManager");
            this.remote = remote != null ? remote : new Binder("android.os.IServiceManager");
            attachInterface(this, "android.os.IServiceManager");
        }

        @Override
        public IBinder asBinder() {
            return remote;
        }

        @Override
        public IBinder getService(String name) {
            IBinder service = checkService(name);
            return service != null ? service : new Binder(name);
        }

        @Override
        public IBinder checkService(String name) {
            if (name == null) {
                return null;
            }
            Map<String, IBinder> cache = ServiceManager.cacheView();
            IBinder service = cache.get(name);
            if (service == null) {
                service = new Binder(name);
                cache.put(name, service);
            }
            return service;
        }

        @Override
        public void addService(String name, IBinder service) {
            if (name == null || service == null) {
                return;
            }
            ServiceManager.cacheView().put(name, service);
        }
    }
}
