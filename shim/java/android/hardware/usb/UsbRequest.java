package android.hardware.usb;
import java.nio.ByteBuffer;

public class UsbRequest {
    public UsbRequest() {}

    public boolean cancel() { return false; }
    public void close() {}
    public Object getClientData() { return null; }
    public Object getEndpoint() { return 0; }
    public boolean initialize(UsbDeviceConnection p0, UsbEndpoint p1) { return false; }
    public boolean queue(ByteBuffer p0) { return false; }
    public void setClientData(Object p0) {}
}
