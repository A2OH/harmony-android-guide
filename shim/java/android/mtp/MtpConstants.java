package android.mtp;

/**
 * Android-compatible MtpConstants shim for A2OH migration.
 * Pure constants; no device access on OpenHarmony.
 */
public final class MtpConstants {

    // Object format codes
    public static final int FORMAT_UNDEFINED   = 0x3000;
    public static final int FORMAT_ASSOCIATION = 0x3001;
    public static final int FORMAT_TEXT        = 0x3004;
    public static final int FORMAT_HTML        = 0x3005;
    public static final int FORMAT_MP3         = 0x3009;
    public static final int FORMAT_AVI         = 0x300A;
    public static final int FORMAT_MPEG        = 0x300B;
    public static final int FORMAT_ASF         = 0x300C;
    public static final int FORMAT_JPEG        = 0x3801;
    public static final int FORMAT_TIFF        = 0x3802;
    public static final int FORMAT_PNG         = 0x380B;
    public static final int FORMAT_BMP         = 0x3804;

    // Operation codes
    public static final int OPERATION_GET_DEVICE_INFO      = 0x1001;
    public static final int OPERATION_OPEN_SESSION         = 0x1002;
    public static final int OPERATION_CLOSE_SESSION        = 0x1003;
    public static final int OPERATION_GET_STORAGE_IDS      = 0x1004;
    public static final int OPERATION_GET_STORAGE_INFO     = 0x1005;
    public static final int OPERATION_GET_NUM_OBJECTS      = 0x1006;
    public static final int OPERATION_GET_OBJECT_HANDLES   = 0x1007;
    public static final int OPERATION_GET_OBJECT_INFO      = 0x1008;
    public static final int OPERATION_GET_OBJECT           = 0x1009;
    public static final int OPERATION_DELETE_OBJECT        = 0x100B;
    public static final int OPERATION_SEND_OBJECT_INFO     = 0x100C;
    public static final int OPERATION_SEND_OBJECT          = 0x100D;

    // Response codes
    public static final int RESPONSE_OK                    = 0x2001;
    public static final int RESPONSE_GENERAL_ERROR         = 0x2002;
    public static final int RESPONSE_SESSION_NOT_OPEN      = 0x2003;
    public static final int RESPONSE_INVALID_TRANSACTION_ID = 0x2004;
    public static final int RESPONSE_OPERATION_NOT_SUPPORTED = 0x2005;
    public static final int RESPONSE_ACCESS_DENIED         = 0x200F;
    public static final int RESPONSE_OBJECT_NOT_FOUND      = 0x2009;
    public static final int RESPONSE_STORE_FULL            = 0x200C;

    private MtpConstants() {}

    /**
     * Returns true if the format code represents an abstract object
     * (i.e. a container such as an association/folder rather than a data object).
     */
    public static boolean isAbstractObject(int format) {
        return format == FORMAT_ASSOCIATION;
    }
}
