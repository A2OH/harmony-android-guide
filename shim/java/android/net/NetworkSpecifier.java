package android.net;

/**
 * Android-compatible NetworkSpecifier shim.
 * Abstract base class for specifying properties of a network for use
 * in a {@link NetworkRequest}.
 */
public class NetworkSpecifier {

    public NetworkSpecifier() {}

    /**
     * Returns whether a given {@code NetworkSpecifier} can be satisfied by
     * this NetworkSpecifier.
     *
     * @param other the specifier to test against
     * @return {@code true} if satisfied
     */
    public boolean canBeSatisfiedBy(NetworkSpecifier other) { return false; }
}
