package android.net.rtp;

import java.net.InetAddress;

/**
 * Android-compatible RtpStream shim for A2OH migration.
 * All network operations are no-ops; state is held in memory only.
 */
public class RtpStream {

    public static final int MODE_NORMAL       = 0;
    public static final int MODE_SEND_ONLY    = 1;
    public static final int MODE_RECEIVE_ONLY = 2;

    private final InetAddress mLocalAddress;
    private int               mLocalPort     = 0;
    private InetAddress       mRemoteAddress = null;
    private int               mRemotePort    = 0;
    private int               mMode          = MODE_NORMAL;
    private boolean           mBusy          = false;

    public RtpStream(InetAddress address) {
        mLocalAddress = address;
    }

    public InetAddress getLocalAddress()  { return mLocalAddress; }
    public int         getLocalPort()     { return mLocalPort; }
    public InetAddress getRemoteAddress() { return mRemoteAddress; }
    public int         getRemotePort()    { return mRemotePort; }
    public boolean     isBusy()           { return mBusy; }
    public int         getMode()          { return mMode; }

    /**
     * Sets the mode. Must be one of MODE_NORMAL, MODE_SEND_ONLY, or MODE_RECEIVE_ONLY.
     */
    public void setMode(int mode) {
        mMode = mode;
    }

    /**
     * Associates the stream with a remote endpoint.
     */
    public void associate(InetAddress address, int port) {
        mRemoteAddress = address;
        mRemotePort    = port;
    }

    /** Releases all resources held by this stream. No-op in this stub. */
    public void release() {
        mBusy = false;
    }
}
