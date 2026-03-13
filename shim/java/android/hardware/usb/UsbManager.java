package android.hardware.usb;

import java.util.Collections;
import java.util.HashMap;

/**
 * Android-compatible UsbManager stub.
 * Manages access to USB devices and accessories.
 */
public class UsbManager {

    public static final String ACTION_USB_DEVICE_ATTACHED =
        "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    public static final String ACTION_USB_DEVICE_DETACHED =
        "android.hardware.usb.action.USB_DEVICE_DETACHED";
    public static final String ACTION_USB_ACCESSORY_ATTACHED =
        "android.hardware.usb.action.USB_ACCESSORY_ATTACHED";
    public static final String ACTION_USB_ACCESSORY_DETACHED =
        "android.hardware.usb.action.USB_ACCESSORY_DETACHED";

    public static final String EXTRA_DEVICE      = "device";
    public static final String EXTRA_ACCESSORY   = "accessory";
    public static final String EXTRA_PERMISSION_GRANTED = "permission";

    /** @param context unused in shim */
    public UsbManager(Object context) {}

    /**
     * Returns a map of all currently attached USB devices.
     * Always returns an empty map in the shim.
     */
    public HashMap<String, UsbDevice> getDeviceList() {
        return new HashMap<>();
    }

    /**
     * Opens a UsbDevice for communication.
     * Always returns null in the shim (no real USB hardware).
     */
    public UsbDeviceConnection openDevice(UsbDevice device) {
        return null;
    }

    /**
     * Returns whether the caller has permission to access the device.
     * Always returns false in the shim.
     */
    public boolean hasPermission(UsbDevice device) {
        return false;
    }

    /**
     * Requests permission to access the given USB device.
     * The shim does nothing; no callback will be fired.
     *
     * @param device      device to request permission for
     * @param pendingIntent PendingIntent (Object in shim to avoid dependency)
     */
    public void requestPermission(UsbDevice device, Object pendingIntent) {
        // No-op: no USB hardware in shim environment
    }

    // -----------------------------------------------------------------------
    // UsbDeviceConnection
    // -----------------------------------------------------------------------

    public static final class UsbDeviceConnection {
        private boolean mClosed = false;

        /** Closes the connection and releases any claimed interfaces. */
        public void close() {
            mClosed = true;
        }

        public boolean isClosed() { return mClosed; }

        /**
         * Claims an interface on the device.
         * Always returns false in the shim.
         */
        public boolean claimInterface(UsbDevice.UsbInterface iface, boolean force) {
            return false;
        }

        /**
         * Releases a previously claimed interface.
         * Always returns false in the shim.
         */
        public boolean releaseInterface(UsbDevice.UsbInterface iface) {
            return false;
        }

        /**
         * Performs a bulk transfer.
         * Always returns -1 (error) in the shim.
         */
        public int bulkTransfer(UsbDevice.UsbEndpoint endpoint, byte[] buffer,
                                int length, int timeout) {
            return -1;
        }

        /**
         * Performs a control transfer.
         * Always returns -1 (error) in the shim.
         */
        public int controlTransfer(int requestType, int request, int value,
                                   int index, byte[] buffer, int length, int timeout) {
            return -1;
        }

        /** Returns the serial number of the connected device, or null in the shim. */
        public String getSerial() {
            return null;
        }

        /** Returns the raw file descriptor, or -1 in the shim. */
        public int getFileDescriptor() {
            return -1;
        }
    }
}
