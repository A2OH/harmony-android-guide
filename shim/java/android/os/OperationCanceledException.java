package android.os;

/**
 * Shim: android.os.OperationCanceledException — thrown when an operation
 * in progress is canceled.
 */
public class OperationCanceledException extends RuntimeException {

    public OperationCanceledException() {
        this(null);
    }

    public OperationCanceledException(String message) {
        super(message != null ? message : "The operation has been canceled.");
    }
}
