package android.net;

/**
 * Android-compatible LocalSocketAddress shim for OpenHarmony migration.
 * Represents an address for a {@link LocalSocket} in a Unix domain socket namespace.
 */
public class LocalSocketAddress {

    /**
     * The namespace within which a Unix domain socket address lives.
     */
    public enum Namespace {
        /** Abstract namespace (Linux-specific; name has no filesystem representation). */
        ABSTRACT,
        /** Reserved namespace. */
        RESERVED,
        /** Filesystem namespace (name is a filesystem path). */
        FILESYSTEM
    }

    private final String mName;
    private final Namespace mNamespace;

    /**
     * Constructs a LocalSocketAddress in the {@link Namespace#ABSTRACT} namespace.
     *
     * @param name the socket address name
     */
    public LocalSocketAddress(String name) {
        this(name, Namespace.ABSTRACT);
    }

    /**
     * Constructs a LocalSocketAddress in the specified namespace.
     *
     * @param name      the socket address name
     * @param namespace the namespace in which the address lives
     */
    public LocalSocketAddress(String name, Namespace namespace) {
        if (name == null) {
            throw new NullPointerException("name must not be null");
        }
        if (namespace == null) {
            throw new NullPointerException("namespace must not be null");
        }
        mName = name;
        mNamespace = namespace;
    }

    /** Returns the name of this socket address. */
    public String getName() {
        return mName;
    }

    /** Returns the namespace of this socket address. */
    public Namespace getNamespace() {
        return mNamespace;
    }

    @Override
    public String toString() {
        return mNamespace + ":" + mName;
    }
}
