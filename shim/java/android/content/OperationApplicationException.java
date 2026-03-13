package android.content;

/**
 * Android-compatible OperationApplicationException shim.
 * Thrown when a batch of ContentProvider operations fails.
 */
public class OperationApplicationException extends Exception {

    private final int mNumSuccessfulYieldPoints;

    public OperationApplicationException() {
        super();
        mNumSuccessfulYieldPoints = 0;
    }

    public OperationApplicationException(String message) {
        super(message);
        mNumSuccessfulYieldPoints = 0;
    }

    public OperationApplicationException(String message, int numSuccessfulYieldPoints) {
        super(message);
        mNumSuccessfulYieldPoints = numSuccessfulYieldPoints;
    }

    public OperationApplicationException(String message, Throwable cause) {
        super(message, cause);
        mNumSuccessfulYieldPoints = 0;
    }

    public int getNumSuccessfulYieldPoints() {
        return mNumSuccessfulYieldPoints;
    }
}
