package android.hardware.usb;

/**
 * Android-compatible UsbDevice stub.
 * Represents a USB device attached to the Android device.
 */
public class UsbDevice {

    // Device classes (USB spec)
    public static final int USB_CLASS_PER_INTERFACE = 0;
    public static final int USB_CLASS_AUDIO         = 1;
    public static final int USB_CLASS_COMM          = 2;
    public static final int USB_CLASS_HID           = 3;
    public static final int USB_CLASS_PHYSICAL      = 5;
    public static final int USB_CLASS_STILL_IMAGE   = 6;
    public static final int USB_CLASS_PRINTER       = 7;
    public static final int USB_CLASS_MASS_STORAGE  = 8;
    public static final int USB_CLASS_HUB           = 9;
    public static final int USB_CLASS_CDC_DATA      = 10;
    public static final int USB_CLASS_MISC          = 0xef;
    public static final int USB_CLASS_VENDOR_SPEC   = 0xff;

    private final String mDeviceName;
    private final int    mVendorId;
    private final int    mProductId;
    private final int    mDeviceClass;
    private final int    mDeviceSubclass;
    private final int    mDeviceProtocol;
    private final UsbInterface[] mInterfaces;

    public UsbDevice(String deviceName, int vendorId, int productId,
                     int deviceClass, int deviceSubclass, int deviceProtocol,
                     UsbInterface[] interfaces) {
        mDeviceName     = deviceName;
        mVendorId       = vendorId;
        mProductId      = productId;
        mDeviceClass    = deviceClass;
        mDeviceSubclass = deviceSubclass;
        mDeviceProtocol = deviceProtocol;
        mInterfaces     = interfaces != null ? interfaces : new UsbInterface[0];
    }

    /** Convenience constructor with minimal required fields. */
    public UsbDevice(String deviceName, int vendorId, int productId) {
        this(deviceName, vendorId, productId, USB_CLASS_PER_INTERFACE, 0, 0, null);
    }

    public String getDeviceName()    { return mDeviceName; }
    public int    getVendorId()      { return mVendorId; }
    public int    getProductId()     { return mProductId; }
    public int    getDeviceClass()   { return mDeviceClass; }
    public int    getDeviceSubclass(){ return mDeviceSubclass; }
    public int    getDeviceProtocol(){ return mDeviceProtocol; }
    public int    getInterfaceCount(){ return mInterfaces.length; }

    public UsbInterface getInterface(int index) {
        if (index < 0 || index >= mInterfaces.length) return null;
        return mInterfaces[index];
    }

    @Override
    public String toString() {
        return "UsbDevice{name=" + mDeviceName
             + ", vendorId=0x" + Integer.toHexString(mVendorId)
             + ", productId=0x" + Integer.toHexString(mProductId) + "}";
    }

    // -----------------------------------------------------------------------
    // UsbInterface (nested for convenience; mirrors android.hardware.usb.UsbInterface)
    // -----------------------------------------------------------------------

    public static final class UsbInterface {
        private final int    mId;
        private final int    mInterfaceClass;
        private final int    mInterfaceSubclass;
        private final int    mInterfaceProtocol;
        private final UsbEndpoint[] mEndpoints;

        public UsbInterface(int id, int interfaceClass, int interfaceSubclass,
                            int interfaceProtocol, UsbEndpoint[] endpoints) {
            mId                = id;
            mInterfaceClass    = interfaceClass;
            mInterfaceSubclass = interfaceSubclass;
            mInterfaceProtocol = interfaceProtocol;
            mEndpoints         = endpoints != null ? endpoints : new UsbEndpoint[0];
        }

        public int getId()               { return mId; }
        public int getInterfaceClass()   { return mInterfaceClass; }
        public int getInterfaceSubclass(){ return mInterfaceSubclass; }
        public int getInterfaceProtocol(){ return mInterfaceProtocol; }
        public int getEndpointCount()    { return mEndpoints.length; }

        public UsbEndpoint getEndpoint(int index) {
            if (index < 0 || index >= mEndpoints.length) return null;
            return mEndpoints[index];
        }
    }

    // -----------------------------------------------------------------------
    // UsbEndpoint (nested)
    // -----------------------------------------------------------------------

    public static final class UsbEndpoint {
        public static final int USB_DIR_OUT = 0;
        public static final int USB_DIR_IN  = 0x80;

        public static final int USB_ENDPOINT_XFER_CONTROL = 0;
        public static final int USB_ENDPOINT_XFER_ISOC    = 1;
        public static final int USB_ENDPOINT_XFER_BULK    = 2;
        public static final int USB_ENDPOINT_XFER_INT     = 3;

        private final int mAddress;
        private final int mType;
        private final int mDirection;
        private final int mMaxPacketSize;
        private final int mInterval;

        public UsbEndpoint(int address, int type, int direction,
                           int maxPacketSize, int interval) {
            mAddress       = address;
            mType          = type;
            mDirection     = direction;
            mMaxPacketSize = maxPacketSize;
            mInterval      = interval;
        }

        public int getAddress()       { return mAddress; }
        public int getType()          { return mType; }
        public int getDirection()     { return mDirection; }
        public int getMaxPacketSize() { return mMaxPacketSize; }
        public int getInterval()      { return mInterval; }
    }
}
