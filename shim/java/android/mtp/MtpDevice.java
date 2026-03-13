package android.mtp;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDevice;

/**
 * Android-compatible MtpDevice shim for A2OH migration.
 * UsbDevice is represented as Object to avoid pulling in the usb package.
 * All operations are no-ops / return null/false.
 */
public final class MtpDevice {

    /** The underlying USB device (type-erased to Object for shim portability). */
    private final Object mUsbDevice;

    public MtpDevice(Object usbDevice) {
        mUsbDevice = usbDevice;
    }

    /** Opens the MTP session. Returns false in this stub. */
    public boolean open(Object usbDeviceConnection) {
        return false;
    }

    /** Closes the MTP session. */
    public void close() {}

    /** Returns device info or null. */
    public MtpDeviceInfo getDeviceInfo() {
        return null;
    }

    /** Returns storage IDs or null. */
    public int[] getStorageIds() {
        return null;
    }

    /**
     * Returns object handles for the given storage, format, and parent.
     * @param storageId  storage identifier
     * @param format     object format (0 = all)
     * @param objectHandle parent handle (0 = root)
     */
    public int[] getObjectHandles(int storageId, int format, int objectHandle) {
        return null;
    }

    /** Retrieves raw object data or null. */
    public byte[] getObject(int objectHandle, int objectSize) {
        return null;
    }

    /**
     * Sends raw object data.
     * @param objectHandle handle of the object slot created via sendObjectInfo
     * @param size         number of bytes to send
     * @param data         raw bytes
     * @return false in this stub
     */
    public boolean sendObject(int objectHandle, int size, byte[] data) {
        return false;
    }

    /** Deletes an object. Returns false in this stub. */
    public boolean deleteObject(int objectHandle) {
        return false;
    }

    /** Returns storage info for the given storage ID, or null. */
    public MtpStorageInfo getStorageInfo(int storageId) {
        return null;
    }

    /** Returns the underlying USB device (type-erased). */
    public Object getUsbDevice() {
        return mUsbDevice;
    }
}
