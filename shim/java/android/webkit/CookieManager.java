package android.webkit;

/**
 * Android-compatible CookieManager stub.
 */
public class CookieManager {
    private boolean mAcceptCookie = true;

    public static CookieManager getInstance() {
        return new CookieManager();
    }

    public void setCookie(String url, String value) { /* stub */ }
    public String getCookie(String url) { return null; }
    public void removeAllCookies(Object callback) { /* stub */ }
    public boolean hasCookies() { return false; }

    public void setAcceptCookie(boolean accept) { mAcceptCookie = accept; }
    public boolean acceptCookie() { return mAcceptCookie; }

    public void flush() { /* stub */ }
}
