/*
 * OHServiceManager.java
 *
 * Real-routed adapter implementation of android.os.IServiceManager.
 * Replaces the Proxy.newProxyInstance hack (which spins indefinitely under
 * init service due to ART class generation issue with multi-interface Proxy
 * over @hide AIDL types).
 *
 * Routing strategy (per 2026-04-28 user feedback):
 *   - getService("connectivity") → null    (stub, OH has no analog, harmless skip in handleBindApplication)
 *   - getService("display")      → null    (stub for now; real OH IDisplayManagerSAID route TBD)
 *   - other names                → null    (default stub)
 *   - non-getService methods     → safe defaults (empty arrays, false, no-op)
 *
 * Future: route specific service names to OH SystemAbilityManager via JNI:
 *     adapter.core.OHEnvironment.nativeGetOHSystemAbility(int saId).  Per
 *     "feedback_blame_adapter_first" rule we adapt at the IPC interface,
 *     never modify android.os.ServiceManager itself.
 */
package adapter.core;

import android.os.IBinder;
import android.os.IServiceManager;
import android.os.IServiceCallback;
import android.os.IClientCallback;
import android.os.RemoteException;
import android.os.ConnectionInfo;
import android.os.ServiceDebugInfo;

public final class OHServiceManager extends IServiceManager.Stub {

    private static final String TAG = "OHServiceManager";

    public OHServiceManager() {
        System.err.println("[" + TAG + "] instantiated (real-routed adapter, not Proxy)");
    }

    // --- Service lookup ---

    @Override
    public IBinder getService(String name) throws RemoteException {
        IBinder b = lookupAdapter(name);
        if (b != null) {
            System.err.println("[" + TAG + "] getService(\"" + name + "\") → adapter (real-routed)");
            return b;
        }
        // Track per-service stub status for the inventory.  Once a service
        // has a real adapter, add it to lookupAdapter() switch below.
        // Production HelloWorld currently hits 4 stub services:
        //   connectivity        — Network state queries (low priority for HelloWorld)
        //   network_management  — Same family
        //   content_capture     — View tree capture (analytics, low priority)
        //   game                — Game mode (low priority)
        // Future P3: each requires real OH IPC bridge to corresponding OH SA.
        System.err.println("[" + TAG + "] getService(\"" + name + "\") → null (stub — see doc/shortcuts_inventory.html#chC)");
        return null;
    }

    @Override
    public IBinder checkService(String name) throws RemoteException {
        return lookupAdapter(name);
    }

    /**
     * Returns the adapter IBinder for service names that have a real OH route,
     * or null if no adapter exists yet.  Wired services are real adapters that
     * bridge to OH inner_api via JNI — not stubs.  Add new entries here when a
     * real adapter ships.
     *
     * 2026-04-29 (post-B.29): per feedback.txt line 4, ServiceManager 真适配
     * 扩展.  Wire all 4 core Adapters that already extend IXxx.Stub via
     * Reflection so OHServiceManager doesn't have a hard compile-time dep on
     * adapter.activity.* / adapter.window.* / adapter.packagemanager.* (which
     * live in oh-adapter-framework.jar, not in core / not always loaded
     * depending on caller).  Adapter classes are loaded by the same
     * PathClassLoader that loaded OHServiceManager so reflection lookup is
     * cheap (one Class.forName + getMethod cache miss on first call).
     *
     * Per feedback.txt line 12, services with no OH analog (connectivity /
     * network_management / content_capture / game) explicitly return null
     * here.  Framework code that tries getService(CONNECTIVITY_SERVICE) on
     * Hello World path is expected to handle null gracefully (boot-path
     * skips for these are登记 in shortcuts_inventory.html#chC).
     */
    private static IBinder lookupAdapter(String name) {
        if (name == null) return null;
        switch (name) {
            case "activity":
                return getAdapterBinder("adapter.activity.ActivityManagerAdapter");
            case "activity_task":
                return getAdapterBinder("adapter.activity.ActivityTaskManagerAdapter");
            case "package":
                return getAdapterBinder("adapter.packagemanager.PackageManagerAdapter");
            case "window":
                return getAdapterBinder("adapter.window.WindowManagerAdapter");
            case "display":
                return adapter.window.DisplayManagerAdapter.getInstance().asBinder();
            // 2026-04-30 G2.9: input_method real-routed via InputMethodManagerAdapter
            // returning NO_IME baseline. Editor / TextView startup paths require
            // a non-null IInputMethodManager binder or AOSP IMM throws
            // IllegalStateException("IInputMethodManager is not available").
            // See doc/window_manager_ipc_adapter_design.html §5.
            case "input_method":
                return adapter.window.InputMethodManagerAdapter.getInstance().asBinder();
            // Explicit stub-null whitelist (feedback.txt line 12): services
            // for which OH has no analog and HelloWorld path tolerates null.
            case "connectivity":
            case "network_management":
            case "content_capture":
            case "game":
                return null;
            default:
                return null;
        }
    }

    /**
     * Reflectively load Adapter class + invoke getInstance() + asBinder().
     * Returns null on any failure so framework code falls through to its
     * null-handling path.  Each Adapter caches its singleton internally so
     * subsequent calls are cheap field reads after the first reflection.
     */
    private static IBinder getAdapterBinder(String fqcn) {
        try {
            Class<?> cls = Class.forName(fqcn);
            Object inst = cls.getMethod("getInstance").invoke(null);
            return ((android.os.IBinder) inst);
        } catch (Throwable t) {
            System.err.println("[" + TAG + "] getAdapterBinder(" + fqcn + ") FAIL: " + t);
            return null;
        }
    }

    @Override
    public void addService(String name, IBinder service, boolean allowIsolated, int dumpPriority)
            throws RemoteException {
        // No-op: adapter does not register services with OH SAM via this path.
        System.err.println("[" + TAG + "] addService(\"" + name + "\") ignored");
    }

    @Override
    public String[] listServices(int dumpPriority) throws RemoteException {
        return new String[0];
    }

    @Override
    public void registerForNotifications(String name, IServiceCallback callback)
            throws RemoteException {
        // No-op
    }

    @Override
    public void unregisterForNotifications(String name, IServiceCallback callback)
            throws RemoteException {
        // No-op
    }

    @Override
    public boolean isDeclared(String name) throws RemoteException {
        return false;
    }

    @Override
    public String[] getDeclaredInstances(String iface) throws RemoteException {
        return new String[0];
    }

    @Override
    public String updatableViaApex(String name) throws RemoteException {
        return null;
    }

    @Override
    public String[] getUpdatableNames(String apexName) throws RemoteException {
        return new String[0];
    }

    @Override
    public ConnectionInfo getConnectionInfo(String name) throws RemoteException {
        return null;
    }

    @Override
    public void registerClientCallback(String name, IBinder service, IClientCallback callback)
            throws RemoteException {
        // No-op
    }

    @Override
    public void tryUnregisterService(String name, IBinder service) throws RemoteException {
        // No-op
    }

    @Override
    public ServiceDebugInfo[] getServiceDebugInfo() throws RemoteException {
        return new ServiceDebugInfo[0];
    }
}
