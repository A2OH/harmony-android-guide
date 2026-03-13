package android.os;

/**
 * Android-compatible RemoteException shim.
 */
public class RemoteException extends Exception {
    public RemoteException() {
        super();
    }

    public RemoteException(String message) {
        super(message);
    }

    public RuntimeException rethrowAsRuntimeException() {
        return new RuntimeException(this);
    }

    public RuntimeException rethrowFromSystemServer() {
        return new RuntimeException("System server died: " + getMessage(), this);
    }
}
