package android.webkit;

/**
 * Shim: android.webkit.WebChromeClient
 * OH mapping: @ohos.web.webview.WebviewController (progress / title events)
 *
 * Apps subclass WebChromeClient to receive UI-level browser events such as
 * load progress updates and title changes.
 *
 * OH equivalents:
 *   onProgressChanged → Web.onProgressChange
 *   onReceivedTitle   → Web.onTitleReceive
 */
public class WebChromeClient {

    /**
     * Called when the page loading progress changes.
     * @param newProgress  0–100
     */
    public void onProgressChanged(WebView view, int newProgress) {
        // default no-op
    }

    /**
     * Called when the document title changes.
     */
    public void onReceivedTitle(WebView view, String title) {
        // default no-op
    }
}
