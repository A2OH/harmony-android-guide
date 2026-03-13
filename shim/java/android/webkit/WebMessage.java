package android.webkit;

/**
 * Shim for android.webkit.WebMessage.
 * Represents a message passed via postMessage between a WebView and JavaScript.
 */
public class WebMessage {

    private final String mData;

    /**
     * Constructs a WebMessage carrying the given string payload.
     *
     * @param data the message payload
     */
    public WebMessage(String data) {
        mData = data;
    }

    /** Returns the string data of this message. */
    public String getData() {
        return mData;
    }
}
