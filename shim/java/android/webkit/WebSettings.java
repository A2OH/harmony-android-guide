package android.webkit;

/**
 * Shim: android.webkit.WebSettings
 * OH mapping: @ohos.web.webview.WebviewController (settings sub-object)
 *
 * On OpenHarmony the Web component settings are configured directly on the
 * ArkUI Web element declaratively.  This shim stores all settings in memory
 * so that Java-side code can read/write them; the bridge runtime syncs them
 * to the ArkUI Web component when the view is created.
 */
public class WebSettings {

    // ── Cache-mode constants (mirrors Android API) ──
    public static final int LOAD_DEFAULT            = 0;
    public static final int LOAD_CACHE_ELSE_NETWORK = 1;
    public static final int LOAD_NO_CACHE           = 2;
    public static final int LOAD_CACHE_ONLY         = 3;

    // ── Stored settings ──
    private boolean javaScriptEnabled             = false;
    private boolean domStorageEnabled             = false;
    private boolean builtInZoomControls           = false;
    private boolean useWideViewPort               = false;
    private boolean loadWithOverviewMode          = false;
    private int     cacheMode                     = LOAD_DEFAULT;
    private String  userAgentString               = "Mozilla/5.0 (Linux; Android)";
    private boolean allowFileAccess               = true;
    private boolean mediaPlaybackRequiresUserGesture = true;

    // Package-private: only WebView should instantiate this.
    WebSettings() {}

    // ── JavaScript ──

    public void setJavaScriptEnabled(boolean flag) {
        this.javaScriptEnabled = flag;
    }

    public boolean getJavaScriptEnabled() {
        return javaScriptEnabled;
    }

    // ── DOM Storage ──

    public void setDomStorageEnabled(boolean flag) {
        this.domStorageEnabled = flag;
    }

    public boolean getDomStorageEnabled() {
        return domStorageEnabled;
    }

    // ── Zoom ──

    public void setBuiltInZoomControls(boolean enabled) {
        this.builtInZoomControls = enabled;
    }

    public boolean getBuiltInZoomControls() {
        return builtInZoomControls;
    }

    // ── Viewport ──

    public void setUseWideViewPort(boolean use) {
        this.useWideViewPort = use;
    }

    public boolean getUseWideViewPort() {
        return useWideViewPort;
    }

    public void setLoadWithOverviewMode(boolean overview) {
        this.loadWithOverviewMode = overview;
    }

    public boolean getLoadWithOverviewMode() {
        return loadWithOverviewMode;
    }

    // ── Cache ──

    public void setCacheMode(int mode) {
        this.cacheMode = mode;
    }

    public int getCacheMode() {
        return cacheMode;
    }

    // ── User-Agent ──

    public void setUserAgentString(String ua) {
        this.userAgentString = (ua != null) ? ua : "";
    }

    public String getUserAgentString() {
        return userAgentString;
    }

    // ── File access ──

    public void setAllowFileAccess(boolean allow) {
        this.allowFileAccess = allow;
    }

    public boolean getAllowFileAccess() {
        return allowFileAccess;
    }

    // ── Media ──

    public void setMediaPlaybackRequiresUserGesture(boolean require) {
        this.mediaPlaybackRequiresUserGesture = require;
    }

    public boolean getMediaPlaybackRequiresUserGesture() {
        return mediaPlaybackRequiresUserGesture;
    }
}
