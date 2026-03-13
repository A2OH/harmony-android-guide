package android.os;

/**
 * Shim: android.os.DeadObjectException — thrown when the object you are
 * calling has died, because its hosting process no longer exists or
 * because it has been explicitly destroyed.
 */
public class DeadObjectException extends RemoteException {

    public DeadObjectException() {
        super();
    }

    public DeadObjectException(String message) {
        super(message);
    }
}
