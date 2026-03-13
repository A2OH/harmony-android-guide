package android.hardware.usb;

public class UsbDeviceConnection {
    public UsbDeviceConnection() {}

    public int bulkTransfer(UsbEndpoint p0, byte[] p1, int p2, int p3) { return 0; }
    public int bulkTransfer(UsbEndpoint p0, byte[] p1, int p2, int p3, int p4) { return 0; }
    public boolean claimInterface(UsbInterface p0, boolean p1) { return false; }
    public void close() {}
    public int controlTransfer(int p0, int p1, int p2, int p3, byte[] p4, int p5, int p6) { return 0; }
    public int controlTransfer(int p0, int p1, int p2, int p3, byte[] p4, int p5, int p6, int p7) { return 0; }
    public int getFileDescriptor() { return 0; }
    public byte[] getRawDescriptors() { return new byte[0]; }
    public String getSerial() { return null; }
    public boolean releaseInterface(UsbInterface p0) { return false; }
    public UsbRequest requestWait() { return null; }
    public UsbRequest requestWait(long p0) { return null; }
    public boolean setConfiguration(UsbConfiguration p0) { return false; }
    public boolean setInterface(UsbInterface p0) { return false; }
}
