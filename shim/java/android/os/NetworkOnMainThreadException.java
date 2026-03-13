package android.os;

/**
 * Shim: android.os.NetworkOnMainThreadException — thrown when an application
 * attempts to perform a networking operation on its main thread.
 */
public class NetworkOnMainThreadException extends RuntimeException {

    public NetworkOnMainThreadException() {
        super();
    }
}
