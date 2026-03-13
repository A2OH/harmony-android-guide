package android.net.wifi.hotspot2;

/**
 * Android-compatible PasspointConfiguration shim. Stub.
 */
public class PasspointConfiguration {

    /** Stub for HomeSp (Home Service Provider) configuration. */
    public static class HomeSp {
        private String fqdn;
        private String friendlyName;
        private long[] roamingConsortiumOis;

        public String getFqdn()                     { return fqdn; }
        public void setFqdn(String fqdn)            { this.fqdn = fqdn; }

        public String getFriendlyName()             { return friendlyName; }
        public void setFriendlyName(String name)    { this.friendlyName = name; }

        public long[] getRoamingConsortiumOis()     { return roamingConsortiumOis; }
        public void setRoamingConsortiumOis(long[] ois) { this.roamingConsortiumOis = ois; }
    }

    /** Stub for Credential configuration. */
    public static class Credential {
        private String realm;
        private Object userCredential;
        private Object certCredential;
        private Object simCredential;

        public String getRealm()                    { return realm; }
        public void setRealm(String realm)          { this.realm = realm; }

        public Object getUserCredential()           { return userCredential; }
        public void setUserCredential(Object cred)  { this.userCredential = cred; }

        public Object getCertCredential()           { return certCredential; }
        public void setCertCredential(Object cred)  { this.certCredential = cred; }

        public Object getSimCredential()            { return simCredential; }
        public void setSimCredential(Object cred)   { this.simCredential = cred; }
    }

    private HomeSp homeSp;
    private Credential credential;

    public HomeSp getHomeSp()                       { return homeSp; }
    public void setHomeSp(HomeSp homeSp)            { this.homeSp = homeSp; }

    public Credential getCredential()               { return credential; }
    public void setCredential(Credential credential) { this.credential = credential; }
}
