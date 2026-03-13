package android.webkit;

import android.graphics.Bitmap;

/**
 * Shim: android.webkit.WebViewClient
 * OH mapping: @ohos.web.webview.WebviewController (event callbacks)
 *
 * Apps subclass WebViewClient and override the methods they care about.
 * The bridge runtime calls these methods in response to ArkUI Web component
 * events (onPageBegin, onPageEnd, onErrorReceive, onLoadIntercept).
 *
 * Default implementations match the Android documentation:
 *   - shouldOverrideUrlLoading → false  (let the WebView load the URL)
 *   - all void callbacks → no-op
 */
public class WebViewClient {

    /**
     * Called when a new URL is about to be loaded.
     * Return true to cancel the load (override URL handling yourself),
     * false to let the WebView proceed normally.
     *
     * OH equivalent: Web.onLoadIntercept
     */
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return false;
    }

    /**
     * Called when a page starts loading.
     * OH equivalent: Web.onPageBegin
     *
     * @param favicon  always null in this shim (no favicon support yet)
     */
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        // default no-op
    }

    /**
     * Called when a page has finished loading.
     * OH equivalent: Web.onPageEnd
     */
    public void onPageFinished(WebView view, String url) {
        // default no-op
    }

    /**
     * Called when the WebView encounters a load error.
     * OH equivalent: Web.onErrorReceive
     */
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        // default no-op
    }
}
