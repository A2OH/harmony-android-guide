package android.telephony.ims;

public final class ImsException extends Exception {
    public static final int CODE_ERROR_INVALID_SUBSCRIPTION = 0;
    public static final int CODE_ERROR_SERVICE_UNAVAILABLE = 0;
    public static final int CODE_ERROR_UNSPECIFIED = 0;
    public static final int CODE_ERROR_UNSUPPORTED_OPERATION = 0;

    public ImsException() {}

    public int getCode() { return 0; }
}
