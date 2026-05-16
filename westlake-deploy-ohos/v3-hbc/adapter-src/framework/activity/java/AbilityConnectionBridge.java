/*
 * AbilityConnectionBridge.java
 *
 * Reverse bridge: OH IAbilityConnection callbacks -> Android IServiceConnection.
 *
 * OH IAbilityConnection notifies the caller when a service ability
 * connects or disconnects. This maps directly to Android IServiceConnection.
 *
 * Mapping:
 *   OH IAbilityConnection.OnAbilityConnectDone -> Android IServiceConnection.connected
 *   OH IAbilityConnection.OnAbilityDisconnectDone -> Android IServiceConnection.connected (dead=true)
 *
 * This is the most direct 1:1 mapping among all callback interfaces.
 */
package adapter.activity;

import android.app.IServiceConnection;
import android.content.ComponentName;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class AbilityConnectionBridge {

    private static final String TAG = "OH_AbilityConnBridge";
    private final IServiceConnection mServiceConnection;

    public AbilityConnectionBridge(IServiceConnection serviceConnection) {
        mServiceConnection = serviceConnection;
    }

    // ============================================================
    // Category 1: Connection Callbacks (-> IServiceConnection)
    // ============================================================

    /**
     * [BRIDGED] OnAbilityConnectDone -> IServiceConnection.connected
     *
     * OH notifies that service ability connection is established.
     * Provides ElementName (bundleName + abilityName) and remote object.
     *
     * Android IServiceConnection.connected receives:
     *   - ComponentName (package + class)
     *   - IBinder (service proxy)
     *   - boolean dead (false for live connection)
     *
     * Conversion:
     *   OH ElementName.bundleName -> ComponentName.packageName
     *   OH ElementName.abilityName -> ComponentName.className
     *   OH remoteObject -> IBinder (wrap if needed)
     *   OH resultCode == 0 -> dead=false, otherwise dead=true
     */
    public void onAbilityConnectDone(String bundleName, String abilityName,
                                      IBinder remoteObject, int resultCode) {
        logBridged("OnAbilityConnectDone",
                "-> IServiceConnection.connected(" + bundleName + "/" + abilityName + ")");
        try {
            ComponentName componentName = new ComponentName(bundleName, abilityName);
            boolean dead = (resultCode != 0);
            mServiceConnection.connected(componentName, remoteObject, dead);
            Log.i(TAG, "Service connection forwarded: " + componentName
                    + " dead=" + dead);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to forward connection callback", e);
        }
    }

    /**
     * [BRIDGED] OnAbilityDisconnectDone -> IServiceConnection.connected (dead=true)
     *
     * OH notifies that service ability has disconnected.
     *
     * Android IServiceConnection does not have a separate "disconnected" method.
     * Instead, the ServiceConnection.onServiceDisconnected is triggered when
     * the service crashes. For clean disconnect, the connection is simply removed.
     *
     * Strategy: Call connected() with dead=true to trigger onServiceDisconnected.
     */
    public void onAbilityDisconnectDone(String bundleName, String abilityName,
                                         int resultCode) {
        logBridged("OnAbilityDisconnectDone",
                "-> IServiceConnection.connected(dead=true)");
        try {
            ComponentName componentName = new ComponentName(bundleName, abilityName);
            mServiceConnection.connected(componentName, null, true);
            Log.i(TAG, "Service disconnection forwarded: " + componentName);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to forward disconnection callback", e);
        }
    }

    // ==================== Utility ====================

    private void logBridged(String method, String target) {
        Log.d(TAG, "[BRIDGED] " + method + " " + target);
    }
}
