package android.net;

public final class Ikev2VpnProfile extends PlatformVpnProfile {
    public Ikev2VpnProfile() {}

    public int getMaxMtu() { return 0; }
    public boolean isBypassable() { return false; }
    public boolean isMetered() { return false; }
}
