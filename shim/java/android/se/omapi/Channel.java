package android.se.omapi;

/**
 * Android-compatible Channel shim. Represents an ISO 7816-4 channel to a Secure Element application.
 */
public class Channel {

    private final Session mSession;
    private final boolean mIsBasic;
    private boolean mOpen = true;
    private byte[] mSelectResponse = new byte[0];

    public Channel(Session session, boolean isBasic) {
        mSession = session;
        mIsBasic = isBasic;
    }

    /**
     * Transmits an APDU command to the SE and returns the APDU response.
     *
     * @param command the APDU command bytes
     * @return the APDU response bytes (SW1 SW2 at the end)
     * @throws Exception if the channel is closed or an I/O error occurs
     */
    public byte[] transmit(byte[] command) throws Exception {
        if (!mOpen) throw new Exception("[Channel] Channel is closed");
        // Return a stub SW OK response: 90 00
        return new byte[]{(byte) 0x90, (byte) 0x00};
    }

    /**
     * Closes this channel.
     */
    public void close() {
        mOpen = false;
        System.out.println("[Channel] close (basic=" + mIsBasic + ")");
    }

    /**
     * Returns whether this channel is open.
     */
    public boolean isOpen() {
        return mOpen;
    }

    /**
     * Returns the response to the SELECT command that was used to open this channel.
     */
    public byte[] getSelectResponse() {
        return mSelectResponse.clone();
    }

    /**
     * Returns whether this channel is the basic channel (channel 0).
     */
    public boolean isBasicChannel() {
        return mIsBasic;
    }

    /**
     * Returns the {@link Session} that was used to open this channel.
     */
    public Session getSession() {
        return mSession;
    }
}
