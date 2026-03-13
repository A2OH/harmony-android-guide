package android.hardware.usb;

public class UsbManager {
    public UsbManager() {}

    public static final int ACTION_USB_ACCESSORY_ATTACHED = 0;
    public static final int ACTION_USB_ACCESSORY_DETACHED = 0;
    public static final int ACTION_USB_DEVICE_ATTACHED = 0;
    public static final int ACTION_USB_DEVICE_DETACHED = 0;
    public static final int EXTRA_ACCESSORY = 0;
    public static final int EXTRA_DEVICE = 0;
    public static final int EXTRA_PERMISSION_GRANTED = 0;
    public Object getAccessoryList() { return null; }
    public Object getDeviceList() { return null; }
    public boolean hasPermission(Object p0) { return false; }
    public Object openAccessory(Object p0) { return null; }
    public Object openDevice(Object p0) { return null; }
    public void requestPermission(Object p0, Object p1) {}
}
