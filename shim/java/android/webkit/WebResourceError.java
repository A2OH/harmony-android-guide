package android.webkit;

/**
 * Shim: android.webkit.WebResourceError
 * OH mapping: @ohos.web.webview — error parameter in onErrorReceive
 *
 * Describes a resource load error delivered to
 * {@link WebViewClient#onReceivedError(WebView, WebResourceRequest, WebResourceError)}.
 * On OpenHarmony the equivalent information is provided by the error object in
 * the ArkUI Web component's {@code onErrorReceive} event.
 *
 * This class is abstract in the Android SDK; concrete instances are created by
 * the framework (or by the bridge runtime in an OH context).  Test code can
 * subclass it or create an anonymous implementation.
 *
 * Common error code constants mirror {@link android.webkit.WebViewClient}:
 * ERROR_UNKNOWN(-1), ERROR_HOST_LOOKUP(-2), ERROR_CONNECT(-6), etc.
 */
public abstract class WebResourceError {

    // ── Common error codes (mirrors WebViewClient constants) ──

    /** Generic error. */
    public static final int ERROR_UNKNOWN           = -1;
    /** Server or proxy hostname lookup failed. */
    public static final int ERROR_HOST_LOOKUP       = -2;
    /** Unsupported authentication scheme. */
    public static final int ERROR_UNSUPPORTED_AUTH_SCHEME = -3;
    /** User authentication failed. */
    public static final int ERROR_AUTHENTICATION    = -4;
    /** Proxy authentication failed. */
    public static final int ERROR_PROXY_AUTHENTICATION = -5;
    /** Failed to connect to the server. */
    public static final int ERROR_CONNECT           = -6;
    /** Failed to read or write to the server. */
    public static final int ERROR_IO                = -7;
    /** Connection timed out. */
    public static final int ERROR_TIMEOUT           = -8;
    /** Too many redirects. */
    public static final int ERROR_REDIRECT_LOOP     = -9;
    /** Unsupported URI scheme. */
    public static final int ERROR_UNSUPPORTED_SCHEME = -10;
    /** Failed to perform an SSL handshake. */
    public static final int ERROR_FAILED_SSL_HANDSHAKE = -11;
    /** Malformed URL. */
    public static final int ERROR_BAD_URL           = -12;
    /** Generic file error. */
    public static final int ERROR_FILE              = -13;
    /** File not found. */
    public static final int ERROR_FILE_NOT_FOUND    = -14;
    /** Too many requests. */
    public static final int ERROR_TOO_MANY_REQUESTS = -15;

    // ── Abstract API ──

    /**
     * Returns the error code.
     * OH equivalent: error.getErrorCode() in onErrorReceive
     */
    public abstract int getErrorCode();

    /**
     * Returns a human-readable description of the error.
     * OH equivalent: error.getErrorInfo() in onErrorReceive
     */
    public abstract CharSequence getDescription();
}
