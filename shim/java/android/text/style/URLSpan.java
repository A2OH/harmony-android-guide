package android.text.style;

/**
 * Android-compatible URLSpan shim.
 * Marks a text range as a clickable hyperlink pointing to the given URL.
 */
public class URLSpan {

    private final String mURL;

    public URLSpan(String url) {
        mURL = url;
    }

    /** Returns the URL associated with this span. */
    public String getURL() {
        return mURL;
    }
}
