package android.app.admin;

import android.content.BroadcastReceiver;

/**
 * Android-compatible DeviceAdminReceiver shim.
 * Extends BroadcastReceiver; all lifecycle callbacks are empty stubs.
 */
public class DeviceAdminReceiver extends BroadcastReceiver {

    /** Broadcast sent when this receiver is enabled as a device administrator. */
    public static final String ACTION_DEVICE_ADMIN_ENABLED =
            "android.app.action.DEVICE_ADMIN_ENABLED";

    /** Broadcast sent when this receiver is disabled as a device administrator. */
    public static final String ACTION_DEVICE_ADMIN_DISABLED =
            "android.app.action.DEVICE_ADMIN_DISABLED";

    // -------------------------------------------------------------------------
    // Lifecycle callbacks — override in real implementation
    // -------------------------------------------------------------------------

    /**
     * Called after the administrator is first enabled.
     */
    public void onEnabled(Object context, Object intent) {}

    /**
     * Called prior to the administrator being disabled.
     */
    public void onDisabled(Object context, Object intent) {}

    /**
     * Called after the user has changed their password.
     */
    public void onPasswordChanged(Object context, Object intent) {}

    /**
     * Called after the user has failed at entering their password.
     */
    public void onPasswordFailed(Object context, Object intent) {}

    /**
     * Called after the user has successfully entered their password.
     */
    public void onPasswordSucceeded(Object context, Object intent) {}

    /**
     * Called when provisioning of a managed profile or device owner is complete.
     */
    public void onProfileProvisioningComplete(Object context, Object intent) {}

    /**
     * Dispatches the incoming broadcast to the appropriate callback.
     * Subclasses may override this or override the individual callbacks.
     */
    @Override
    public void onReceive(Object context, Object intent) {
        // stub — real dispatch would inspect intent action
    }
}
