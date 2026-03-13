package android.se.omapi;

import java.util.concurrent.Executor;

/**
 * Android-compatible SEService shim. Stub for Open Mobile API Secure Element service.
 */
public class SEService {

    private boolean mConnected = false;
    private final OnConnectedListener mListener;

    /**
     * Listener interface that is called when the SEService is connected and ready to use.
     */
    public interface OnConnectedListener {
        void onConnected();
    }

    /**
     * Creates a new SEService instance and initiates connection.
     *
     * @param context  the caller's context (typed as Object for shim compatibility)
     * @param executor executor used to dispatch onConnected callback
     * @param listener callback invoked when the service is ready
     */
    public SEService(Object context, Executor executor, OnConnectedListener listener) {
        mListener = listener;
        // In a real device this would bind to the SE service asynchronously.
        // In the shim we immediately simulate a connected state via the executor.
        if (executor != null && listener != null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    mConnected = true;
                    mListener.onConnected();
                }
            });
        }
    }

    /**
     * Returns all available {@link Reader} instances.
     */
    public Reader[] getReaders() {
        return new Reader[0];
    }

    /**
     * Returns whether the SEService is connected and ready.
     */
    public boolean isConnected() {
        return mConnected;
    }

    /**
     * Returns the version string of the underlying Open Mobile API implementation.
     */
    public String getVersion() {
        return "3.3";
    }

    /**
     * Shuts down the connection to the SE service. After this call, this SEService object
     * is no longer usable.
     */
    public void shutdown() {
        mConnected = false;
        System.out.println("[SEService] shutdown");
    }
}
