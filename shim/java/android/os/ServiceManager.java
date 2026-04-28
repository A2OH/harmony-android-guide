package android.os;

import java.util.HashMap;
import java.util.Map;

/**
 * Minimal hidden-API compatible ServiceManager shim.
 *
 * Westlake's bootstrap path reflects the hidden fields {@code sCache} and
 * {@code sServiceManager}. Keep those names and basic semantics so guest code
 * can treat this like the platform class instead of a compile-only stub.
 */
public final class ServiceManager {
    public static final HashMap<String, IBinder> sCache = new HashMap<String, IBinder>();
    public static IServiceManager sServiceManager = ServiceManagerNative.asInterface(new Binder());

    private ServiceManager() {}

    public static IBinder getService(String name) {
        IBinder service = checkService(name);
        return service != null ? service : new Binder(name);
    }

    public static void addService(String name, IBinder service) {
        if (name == null || service == null) {
            return;
        }
        sCache.put(name, service);
        IServiceManager sm = sServiceManager;
        if (sm != null) {
            try {
                sm.addService(name, service);
            } catch (Throwable ignored) {
            }
        }
    }

    public static IBinder checkService(String name) {
        if (name == null) {
            return null;
        }
        IBinder cached = sCache.get(name);
        if (cached != null) {
            return cached;
        }
        IServiceManager sm = sServiceManager;
        if (sm != null) {
            try {
                IBinder remote = sm.checkService(name);
                if (remote != null) {
                    sCache.put(name, remote);
                }
                return remote;
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    static Map<String, IBinder> cacheView() {
        return sCache;
    }
}
