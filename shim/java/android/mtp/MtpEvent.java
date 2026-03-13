package android.mtp;

public class MtpEvent {
    public static final int EVENT_CANCEL_TRANSACTION = 0;
    public static final int EVENT_CAPTURE_COMPLETE = 0;
    public static final int EVENT_DEVICE_INFO_CHANGED = 0;
    public static final int EVENT_DEVICE_PROP_CHANGED = 0;
    public static final int EVENT_DEVICE_RESET = 0;
    public static final int EVENT_OBJECT_ADDED = 0;
    public static final int EVENT_OBJECT_INFO_CHANGED = 0;
    public static final int EVENT_OBJECT_PROP_CHANGED = 0;
    public static final int EVENT_OBJECT_PROP_DESC_CHANGED = 0;
    public static final int EVENT_OBJECT_REFERENCES_CHANGED = 0;
    public static final int EVENT_OBJECT_REMOVED = 0;
    public static final int EVENT_REQUEST_OBJECT_TRANSFER = 0;
    public static final int EVENT_STORAGE_INFO_CHANGED = 0;
    public static final int EVENT_STORE_ADDED = 0;
    public static final int EVENT_STORE_FULL = 0;
    public static final int EVENT_STORE_REMOVED = 0;
    public static final int EVENT_UNDEFINED = 0;
    public static final int EVENT_UNREPORTED_STATUS = 0;

    public MtpEvent() {}

    public int getDevicePropCode() { return 0; }
    public int getEventCode() { return 0; }
    public int getObjectFormatCode() { return 0; }
    public int getObjectHandle() { return 0; }
    public int getObjectPropCode() { return 0; }
    public int getParameter1() { return 0; }
    public int getParameter2() { return 0; }
    public int getParameter3() { return 0; }
    public int getStorageId() { return 0; }
    public int getTransactionId() { return 0; }
}
