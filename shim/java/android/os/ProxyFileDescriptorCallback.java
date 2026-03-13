package android.os;

import java.io.IOException;

/**
 * Android-compatible ProxyFileDescriptorCallback shim. Abstract base class
 * for proxied file descriptor operations.
 */
public class ProxyFileDescriptorCallback {

    public int onGetSize() throws IOException {
        return 0;
    }

    public int onRead(long offset, int size, byte[] data) throws IOException {
        throw new IOException("onRead not implemented");
    }

    public int onWrite(long offset, int size, byte[] data) throws IOException {
        throw new IOException("onWrite not implemented");
    }

    public void onFsync() throws IOException {
        // no-op by default
    }

    public void onRelease() {}
}
