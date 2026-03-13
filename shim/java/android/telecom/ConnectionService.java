package android.telecom;

import android.app.Service;

/**
 * Shim for android.telecom.ConnectionService.
 * An abstract Service that provides phone call connections to the Telecom system.
 *
 * Concrete subclasses must implement
 * {@link #onCreateIncomingConnection} and {@link #onCreateOutgoingConnection}.
 */
public abstract class ConnectionService extends Service {

    /**
     * Called by the Telecom framework to create a new incoming connection.
     *
     * @param connectionManagerPhoneAccount the phone account handle for the connection manager
     *                                      that is routing the call, or null if the call is not
     *                                      being routed through a connection manager
     * @param request                       the details of the incoming connection request
     * @return a new {@link Connection} object, or null to reject the call
     */
    public abstract Connection onCreateIncomingConnection(
            PhoneAccountHandle connectionManagerPhoneAccount,
            Object request);

    /**
     * Called by the Telecom framework to create a new outgoing connection.
     *
     * @param connectionManagerPhoneAccount the phone account handle for the connection manager
     *                                      routing the call, or null if no connection manager
     * @param request                       the details of the outgoing connection request
     * @return a new {@link Connection} object, or null to deny the call
     */
    public abstract Connection onCreateOutgoingConnection(
            PhoneAccountHandle connectionManagerPhoneAccount,
            Object request);

    /**
     * Called when a call has been added to this connection service.
     * Subclasses may override to receive notification.
     */
    public void onCreateIncomingConnectionFailed(
            PhoneAccountHandle connectionManagerPhoneAccount,
            Object request) {
        // no-op shim
    }

    /**
     * Called when an outgoing call has failed to be created.
     * Subclasses may override to receive notification.
     */
    public void onCreateOutgoingConnectionFailed(
            PhoneAccountHandle connectionManagerPhoneAccount,
            Object request) {
        // no-op shim
    }

    /**
     * Subclasses may override to supply an IBinder for bound service usage.
     * Returns null in this shim implementation.
     */
    public Object onBind(Object intent) {
        return null;
    }
}
