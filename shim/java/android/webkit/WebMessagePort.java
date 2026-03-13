package android.webkit;

/**
 * Shim for android.webkit.WebMessagePort.
 * Represents one end of a HTML5 MessageChannel used to communicate between
 * a WebView and JavaScript.
 *
 * <p>Concrete implementations must provide {@link #postMessage(WebMessage)} and
 * {@link #setWebMessageCallback(WebMessageCallback)}.
 */
public class WebMessagePort {

    /**
     * Posts a {@link WebMessage} to the entangled port (the JavaScript side).
     *
     * @param message the message to send
     */
    public void postMessage(WebMessage message) {}

    /**
     * Registers a callback that will be invoked when a message arrives from
     * the JavaScript side.
     *
     * @param callback the callback to register, or {@code null} to clear it
     */
    public void setWebMessageCallback(WebMessageCallback callback) {}

    // ------------------------------------------------------------------
    // Inner callback class
    // ------------------------------------------------------------------

    /**
     * Shim for android.webkit.WebMessagePort.WebMessageCallback.
     * Subclass and override {@link #onMessage(WebMessagePort, WebMessage)} to
     * receive messages from the JavaScript side.
     */
    public abstract static class WebMessageCallback {

        /**
         * Called on the main thread when a message arrives on {@code port}.
         *
         * @param port    the local port on which the message was received
         * @param message the received message
         */
        public void onMessage(WebMessagePort port, WebMessage message) {
            // Default: no-op
        }
    }
}
