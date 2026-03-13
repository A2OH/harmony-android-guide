package android.net;

/**
 * Shim: android.net.Network — minimal stub.
 * Maps to @ohos.net.connection — wraps a numeric network handle/ID.
 *
 * On real Android, Network wraps a low-level netId. Here we just hold an int
 * so that code which passes Network objects around continues to compile and run.
 */
public final class Network {

    private final int netId;

    public Network(int netId) {
        this.netId = netId;
    }

    /** Returns the internal network handle (OH netId / connection id). */
    public int getNetId() {
        return netId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Network)) return false;
        return netId == ((Network) o).netId;
    }

    @Override
    public int hashCode() {
        return netId;
    }

    @Override
    public String toString() {
        return "Network{netId=" + netId + "}";
    }
}
