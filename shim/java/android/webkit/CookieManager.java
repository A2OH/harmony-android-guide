package android.webkit;

import java.util.HashMap;
import java.util.Map;

/**
 * Shim: android.webkit.CookieManager
 * OH mapping: @ohos.web.webview.WebCookieManager
 *
 * Manages cookies for all WebView instances.  On OpenHarmony the equivalent is
 * WebCookieManager which is a static utility class on the webview package.
 * This shim stores cookies in-memory keyed by URL so that Java-side logic can
 * read and write them without a running browser; the bridge runtime syncs the
 * store to the real OH cookie jar at startup and flushes it on demand.
 */
public class CookieManager {

    // ── Singleton ──

    private static final CookieManager INSTANCE = new CookieManager();

    /** Returns the singleton CookieManager instance. */
    public static CookieManager getInstance() {
        return INSTANCE;
    }

    // ── Internal state ──

    /** Cookies keyed by URL string. Each value is a raw Set-Cookie header string. */
    private final Map<String, String> cookieStore = new HashMap<>();
    private boolean acceptCookie = true;

    // Package-private constructor — use getInstance().
    CookieManager() {}

    // ── Cookie CRUD ──

    /**
     * Sets a cookie for the given URL.
     * OH equivalent: WebCookieManager.setCookie(url, value)
     *
     * @param url    the URL for which the cookie is set
     * @param value  a cookie string in the form {@code name=value[; attr...]"}
     */
    public void setCookie(String url, String value) {
        if (url == null || value == null) return;
        cookieStore.put(url, value);
    }

    /**
     * Returns the cookies for the given URL, or null if none.
     * OH equivalent: WebCookieManager.fetchCookie(url)
     */
    public String getCookie(String url) {
        if (url == null) return null;
        return cookieStore.get(url);
    }

    /**
     * Returns true if there is at least one cookie stored.
     * OH equivalent: WebCookieManager.existCookie()
     */
    public boolean hasCookies() {
        return !cookieStore.isEmpty();
    }

    // ── Removal ──

    /**
     * Removes all cookies.
     * OH equivalent: WebCookieManager.clearAllCookiesSync()
     *
     * @deprecated  Use {@link #removeAllCookies(ValueCallback)} instead.
     */
    @Deprecated
    public void removeAllCookie() {
        cookieStore.clear();
    }

    /**
     * Removes all cookies asynchronously, invoking the callback with {@code true}
     * when at least one cookie was removed.
     * OH equivalent: WebCookieManager.clearAllCookies(callback)
     */
    public void removeAllCookies(ValueCallback<Boolean> callback) {
        boolean hadCookies = !cookieStore.isEmpty();
        cookieStore.clear();
        if (callback != null) {
            callback.onReceiveValue(hadCookies);
        }
    }

    /**
     * Removes all session cookies (cookies without a Max-Age or Expires attribute).
     * In this shim all cookies are treated as session cookies for simplicity.
     * OH equivalent: WebCookieManager.clearSessionCookie()
     */
    public void removeSessionCookies(ValueCallback<Boolean> callback) {
        // Stub: treat every stored cookie as a session cookie.
        removeAllCookies(callback);
    }

    // ── Accept policy ──

    /**
     * Sets whether the WebView should accept cookies.
     * OH equivalent: WebCookieManager.putAcceptCookieEnabled(accept)
     */
    public void setAcceptCookie(boolean accept) {
        this.acceptCookie = accept;
    }

    /**
     * Returns whether the WebView accepts cookies.
     * OH equivalent: WebCookieManager.isCookieAllowed()
     */
    public boolean acceptCookie() {
        return acceptCookie;
    }

    // ── Persistence ──

    /**
     * Flushes cookies to persistent storage.
     * OH equivalent: WebCookieManager.saveCookieSync()
     *
     * In this shim there is no persistent storage; this is a no-op.
     */
    public void flush() {
        // no-op in stub — no persistent store
    }
}
