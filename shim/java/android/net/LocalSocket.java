package android.net;

import java.io.*;
import java.net.Socket;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * Android-compatible LocalSocket shim. Wraps a TCP loopback socket.
 */
public class LocalSocket implements Closeable {
    private Socket mSocket;

    public LocalSocket() {
        mSocket = new Socket();
    }

    LocalSocket(Socket socket) {
        mSocket = socket;
    }

    public void connect(String name, int port) throws IOException {
        mSocket.connect(new java.net.InetSocketAddress("127.0.0.1", port));
    }

    public InputStream getInputStream() throws IOException {
        return mSocket.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return mSocket.getOutputStream();
    }

    public boolean isConnected() {
        return mSocket.isConnected();
    }

    public boolean isClosed() {
        return mSocket.isClosed();
    }

    @Override
    public void close() throws IOException {
        mSocket.close();
    }
}
