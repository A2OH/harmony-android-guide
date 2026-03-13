package android.webkit;
import android.graphics.Bitmap;
import android.graphics.Bitmap;
import java.net.URL;

/**
 * Shim for android.webkit.WebHistoryItem.
 * Represents a single item in the back/forward navigation history.
 */
public class WebHistoryItem implements Cloneable {

    private final String mUrl;
    private final String mOriginalUrl;
    private final String mTitle;
    private final Object mFavicon;

    public WebHistoryItem(String url, String originalUrl, String title, Object favicon) {
        mUrl = url;
        mOriginalUrl = originalUrl;
        mTitle = title;
        mFavicon = favicon;
    }

    /** Returns the URL of this history item. */
    public String getUrl() {
        return mUrl;
    }

    /** Returns the original URL before any redirects. */
    public String getOriginalUrl() {
        return mOriginalUrl;
    }

    /** Returns the document title of this history item. */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns the favicon for this history item as a Bitmap (typed as Object
     * to avoid a hard dependency on android.graphics.Bitmap).
     */
    public Object getFavicon() {
        return mFavicon;
    }

    @Override
    public WebHistoryItem clone() {
        try {
            return (WebHistoryItem) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
}
