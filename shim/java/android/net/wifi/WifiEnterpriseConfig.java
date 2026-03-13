package android.net.wifi;

import java.security.cert.X509Certificate;

/**
 * Android-compatible WifiEnterpriseConfig shim. Stub — no-op enterprise Wi-Fi config.
 */
public class WifiEnterpriseConfig {

    // -------------------------------------------------------------------------
    // EAP inner class
    // -------------------------------------------------------------------------

    public static final class Eap {
        private Eap() {}

        public static final int PEAP = 0;
        public static final int TLS  = 1;
        public static final int TTLS = 2;
        public static final int PWD  = 3;
        public static final int SIM  = 4;
        public static final int AKA  = 5;
    }

    // -------------------------------------------------------------------------
    // Phase2 inner class
    // -------------------------------------------------------------------------

    public static final class Phase2 {
        private Phase2() {}

        public static final int NONE     = 0;
        public static final int MSCHAPV2 = 3;
        public static final int GTC      = 4;
    }

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    private int    eapMethod    = Eap.PEAP;
    private int    phase2Method = Phase2.NONE;
    private String identity     = "";
    private String password     = "";
    private X509Certificate caCertificate     = null;
    private X509Certificate clientCertificate = null;

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    public WifiEnterpriseConfig() {}

    public void setEapMethod(int eapMethod) {
        this.eapMethod = eapMethod;
    }

    public int getEapMethod() {
        return eapMethod;
    }

    public void setPhase2Method(int phase2Method) {
        this.phase2Method = phase2Method;
    }

    public int getPhase2Method() {
        return phase2Method;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getIdentity() {
        return identity;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setCaCertificate(X509Certificate cert) {
        this.caCertificate = cert;
    }

    public X509Certificate getCaCertificate() {
        return caCertificate;
    }

    public void setClientCertificate(X509Certificate cert) {
        this.clientCertificate = cert;
    }

    public X509Certificate getClientCertificate() {
        return clientCertificate;
    }
}
