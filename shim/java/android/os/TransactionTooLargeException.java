package android.os;

/**
 * Shim: android.os.TransactionTooLargeException — thrown when a binder
 * transaction is too large for the transport buffer.
 */
public class TransactionTooLargeException extends RemoteException {

    public TransactionTooLargeException() {
        super();
    }

    public TransactionTooLargeException(String msg) {
        super(msg);
    }
}
