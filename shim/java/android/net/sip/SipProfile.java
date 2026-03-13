package android.net.sip;
import java.net.URI;

/**
 * Android-compatible SipProfile shim for A2OH migration.
 * Immutable value object built via Builder.
 */
public final class SipProfile {

    private final String mUriString;
    private final String mUserName;
    private final String mPassword;
    private final String mSipDomain;
    private final int    mPort;
    private final String mProtocol;
    private final String mProfileName;
    private final String mDisplayName;

    private SipProfile(Builder b) {
        mUriString   = b.mUriString;
        mUserName    = b.mUserName;
        mPassword    = b.mPassword;
        mSipDomain   = b.mSipDomain;
        mPort        = b.mPort;
        mProtocol    = b.mProtocol;
        mProfileName = b.mProfileName;
        mDisplayName = b.mDisplayName;
    }

    public String getUriString()   { return mUriString; }
    public String getUserName()    { return mUserName; }
    public String getPassword()    { return mPassword; }
    public String getSipDomain()   { return mSipDomain; }
    public int    getPort()        { return mPort; }
    public String getProtocol()    { return mProtocol; }
    public String getProfileName() { return mProfileName; }
    public String getDisplayName() { return mDisplayName; }

    // -----------------------------------------------------------------------

    public static final class Builder {

        private String mUriString;
        private String mUserName;
        private String mPassword;
        private String mSipDomain;
        private int    mPort = 5060;
        private String mProtocol = "UDP";
        private String mProfileName;
        private String mDisplayName;

        /**
         * @param uri(String SIP URI, e.g. "sip:user@example.com"
         */
        public Builder(String uriString) {
            mUriString = uriString;
            // Best-effort parse of user and domain from the URI
            if (uriString != null) {
                String body = uriString.startsWith("sip:") ? uriString.substring(4) : uriString;
                int at = body.indexOf('@');
                if (at >= 0) {
                    mUserName  = body.substring(0, at);
                    mSipDomain = body.substring(at + 1);
                } else {
                    mSipDomain = body;
                }
            }
        }

        public Builder setPassword(String password) {
            mPassword = password;
            return this;
        }

        public Builder setPort(int port) {
            mPort = port;
            return this;
        }

        public Builder setProtocol(String protocol) {
            mProtocol = protocol;
            return this;
        }

        public Builder setProfileName(String name) {
            mProfileName = name;
            return this;
        }

        public Builder setDisplayName(String name) {
            mDisplayName = name;
            return this;
        }

        public SipProfile build() {
            return new SipProfile(this);
        }
    }
}
