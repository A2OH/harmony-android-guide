package android.se.omapi;
import android.renderscript.Element;
import android.renderscript.Element;

/**
 * Android-compatible Session shim. Represents a connection session with a Secure Element.
 */
public class Session {

    private final Reader mReader;
    private boolean mClosed = false;

    public Session(Reader reader) {
        mReader = reader;
    }

    /**
     * Opens the basic channel (channel 0) to the SE.
     *
     * @param aid AID to select, or null to select the default application
     * @return the basic {@link Channel}
     * @throws Exception if the channel cannot be opened
     */
    public Channel openBasicChannel(byte[] aid) throws Exception {
        checkClosed();
        return new Channel(this, true);
    }

    /**
     * Opens the basic channel with an explicit P2 parameter.
     */
    public Channel openBasicChannel(byte[] aid, byte p2) throws Exception {
        checkClosed();
        return new Channel(this, true);
    }

    /**
     * Opens a logical channel to the SE, selecting the application identified by the AID.
     *
     * @param aid AID of the application to select
     * @return the logical {@link Channel}
     * @throws Exception if no logical channel is available or the AID is not found
     */
    public Channel openLogicalChannel(byte[] aid) throws Exception {
        checkClosed();
        return new Channel(this, false);
    }

    /**
     * Opens a logical channel with an explicit P2 parameter.
     */
    public Channel openLogicalChannel(byte[] aid, byte p2) throws Exception {
        checkClosed();
        return new Channel(this, false);
    }

    /**
     * Closes all channels opened on this session.
     */
    public void closeChannels() {
        System.out.println("[Session] closeChannels");
    }

    /**
     * Closes this session and all channels opened on it.
     */
    public void close() {
        mClosed = true;
        System.out.println("[Session] close");
    }

    /**
     * Returns the Answer-to-Reset (ATR) of the connected Secure Element, or null if unavailable.
     */
    public byte[] getATR() {
        return null;
    }

    /**
     * Returns whether this session is closed.
     */
    public boolean isClosed() {
        return mClosed;
    }

    /**
     * Returns the {@link Reader} that was used to create this session.
     */
    public Reader getReader() {
        return mReader;
    }

    private void checkClosed() throws Exception {
        if (mClosed) throw new Exception("[Session] Session is already closed");
    }
}
