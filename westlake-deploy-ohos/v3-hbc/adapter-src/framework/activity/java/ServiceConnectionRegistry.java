/*
 * ServiceConnectionRegistry.java
 *
 * Singleton registry managing the mapping between Android IServiceConnection
 * and OH AbilityConnection for the Service adaptation layer.
 *
 * When an Android app calls bindService(), the adapter registers the
 * IServiceConnection here and obtains a local connectionId. This id is
 * passed to the native side which creates an OH AbilityConnection and
 * returns an OH-assigned connectionId. When OH fires onAbilityConnectDone /
 * onAbilityDisconnectDone, the native callback looks up the Android
 * IServiceConnection through this registry and dispatches the event.
 *
 * Reference:
 *   Android: core/java/android/app/LoadedApk.java (ServiceDispatcher)
 *   OH: ability_rt/interfaces/inner_api/ability_manager/include/ability_connection_stub.h
 */
package adapter.activity;

import android.app.IServiceConnection;
import android.content.ComponentName;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class ServiceConnectionRegistry {

    private static final String TAG = "OH_SvcConnRegistry";

    // Singleton
    private static final ServiceConnectionRegistry sInstance = new ServiceConnectionRegistry();

    public static ServiceConnectionRegistry getInstance() {
        return sInstance;
    }

    private ServiceConnectionRegistry() {}

    // ========================================================================
    // Inner class: ConnectionRecord
    // ========================================================================

    private static class ConnectionRecord {
        final IServiceConnection connection;
        final ComponentName targetComponent;
        int connectionId;      // assigned locally, shared with native side
        boolean bound;

        ConnectionRecord(IServiceConnection conn, ComponentName target) {
            this.connection = conn;
            this.targetComponent = target;
            this.connectionId = -1;
            this.bound = false;
        }
    }

    // ========================================================================
    // Fields
    // ========================================================================

    // Key = IServiceConnection.asBinder(), maps to ConnectionRecord
    private final Map<IBinder, ConnectionRecord> mConnections = new HashMap<>();

    // Key = connectionId (shared between Java and native), maps to ConnectionRecord
    private final Map<Integer, ConnectionRecord> mConnectionsById = new HashMap<>();

    private int mNextConnectionId = 1;

    // ========================================================================
    // Connection registration (called by ActivityManagerAdapter.bindService)
    // ========================================================================

    /**
     * Register an Android IServiceConnection and assign a local connectionId.
     * If the same connection (by Binder identity) is already registered,
     * returns the existing local connectionId without creating a duplicate.
     *
     * @param connection The Android IServiceConnection from the app
     * @param target     The target service ComponentName
     * @return local connectionId to pass to the native side
     */
    public synchronized int registerConnection(IServiceConnection connection,
                                                ComponentName target) {
        IBinder key = connection.asBinder();
        ConnectionRecord existing = mConnections.get(key);
        if (existing != null) {
            Log.d(TAG, "Connection already registered, connId=" + existing.connectionId
                    + " target=" + target);
            return existing.connectionId;
        }

        ConnectionRecord record = new ConnectionRecord(connection, target);
        int connId = mNextConnectionId++;
        record.connectionId = connId;

        mConnections.put(key, record);
        mConnectionsById.put(connId, record);

        Log.i(TAG, "Registered connection: connId=" + connId + " target=" + target);
        return connId;
    }

    /**
     * Unregister an Android IServiceConnection.
     * Removes the record from all maps.
     *
     * @param connection The Android IServiceConnection to remove
     * @return the OH connectionId (for native disconnect), or -1 if not found
     */
    public synchronized int unregisterConnection(IServiceConnection connection) {
        IBinder key = connection.asBinder();
        ConnectionRecord record = mConnections.remove(key);
        if (record == null) {
            Log.d(TAG, "unregisterConnection: not found");
            return -1;
        }

        mConnectionsById.remove(record.connectionId);

        Log.i(TAG, "Unregistered connection: connId=" + record.connectionId
                + " target=" + record.targetComponent);
        return record.connectionId;
    }

    // ========================================================================
    // OH -> Android callbacks (called from native via JNI)
    // ========================================================================

    /**
     * Called when OH reports that a service ability has connected.
     * Dispatches the event to the Android IServiceConnection.
     *
     * @param connectionId  The connection id (shared between Java and native)
     * @param bundleName    The OH bundle name of the connected ability
     * @param abilityName   The OH ability name of the connected ability
     * @param serviceBinder The IBinder representing the service
     */
    public synchronized void onServiceConnected(int connectionId, String bundleName,
                                                 String abilityName, IBinder serviceBinder) {
        ConnectionRecord record = mConnectionsById.get(connectionId);
        if (record == null) {
            Log.e(TAG, "onServiceConnected: no record for connectionId=" + connectionId);
            return;
        }

        ComponentName componentName = record.targetComponent;
        Log.i(TAG, "onServiceConnected: connId=" + connectionId
                + " bundle=" + bundleName + " ability=" + abilityName);

        try {
            record.connection.connected(componentName, serviceBinder, false);
            record.bound = true;
        } catch (RemoteException e) {
            Log.e(TAG, "onServiceConnected: RemoteException dispatching to app", e);
        }
    }

    /**
     * Called when OH reports that a service ability has disconnected.
     * Dispatches the event to the Android IServiceConnection with a null binder.
     *
     * @param connectionId The connection id (shared between Java and native)
     * @param bundleName   The OH bundle name of the disconnected ability
     * @param abilityName  The OH ability name of the disconnected ability
     */
    public synchronized void onServiceDisconnected(int connectionId, String bundleName,
                                                    String abilityName) {
        ConnectionRecord record = mConnectionsById.get(connectionId);
        if (record == null) {
            Log.e(TAG, "onServiceDisconnected: no record for connectionId=" + connectionId);
            return;
        }

        ComponentName componentName = record.targetComponent;
        Log.i(TAG, "onServiceDisconnected: connId=" + connectionId
                + " bundle=" + bundleName + " ability=" + abilityName);

        try {
            record.connection.connected(componentName, null, true);
            record.bound = false;
        } catch (RemoteException e) {
            Log.e(TAG, "onServiceDisconnected: RemoteException dispatching to app", e);
        }
    }

    // ========================================================================
    // Shutdown
    // ========================================================================

    /**
     * Disconnect and clear all registered connections.
     * Called during adapter shutdown via OHEnvironment.shutdown().
     */
    public synchronized void disconnectAll() {
        int count = mConnections.size();
        mConnections.clear();
        mConnectionsById.clear();
        Log.i(TAG, "Disconnected all connections, count=" + count);
    }
}
