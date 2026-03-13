package android.se.omapi;

/**
 * Android-compatible Reader shim. Represents a Secure Element reader (slot) in the Open Mobile API.
 */
public class Reader {

    private final String mName;
    private final SEService mService;

    public Reader(String name, SEService service) {
        mName    = name;
        mService = service;
    }

    /**
     * Returns the name of this reader (e.g. "SIM1", "eSE1").
     */
    public String getName() {
        return mName;
    }

    /**
     * Opens a new session with the Secure Element accessible through this reader.
     *
     * @return a new {@link Session} instance
     * @throws Exception if the SE is not present or not accessible
     */
    public Session openSession() throws Exception {
        if (!isSecureElementPresent()) {
            throw new Exception("[Reader] No secure element present in " + mName);
        }
        return new Session(this);
    }

    /**
     * Closes all sessions opened on this reader.
     */
    public void closeSessions() {
        System.out.println("[Reader] closeSessions: " + mName);
    }

    /**
     * Returns whether a Secure Element is present in this reader slot.
     */
    public boolean isSecureElementPresent() {
        return false; // no physical SE in shim environment
    }

    /**
     * Returns the {@link SEService} that created this reader.
     */
    public SEService getSEService() {
        return mService;
    }
}
