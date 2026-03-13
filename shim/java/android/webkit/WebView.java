package android.webkit;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Shim: android.webkit.WebView
 * OH mapping: @ohos.web.webview.WebviewController
 *
 * On OpenHarmony, WebView rendering is handled by the ArkUI {@code <Web>}
 * component backed by a WebviewController instance.  This Java shim maintains
 * the browser state (URL history, title, navigation flags) that Android apps
 * read through Java APIs.  Actual rendering is delegated to the ArkUI Web
 * component; the bridge runtime keeps them in sync at runtime.
 *
 * No OHBridge JNI calls are made here — this is a pure-Java stub that tracks
 * state and fires Java-side callbacks, allowing business-logic code to be
 * compiled and unit-tested on JVM without a device.
 */
public class WebView extends android.view.View {

    // ArkUI node type for WebView (custom value — bridge maps it to Web component)
    private static final int NODE_TYPE_WEBVIEW = 100;

    // ── Internal navigation history ──
    private final List<String> history   = new ArrayList<>();
    private int                historyPos = -1;   // current position in history

    private String currentUrl   = null;
    private String currentTitle = null;

    // ── Clients ──
    private WebViewClient    webViewClient    = new WebViewClient();   // default
    private WebChromeClient  webChromeClient  = new WebChromeClient(); // default

    // ── Settings (one per WebView) ──
    private final WebSettings settings = new WebSettings();

    // ── Javascript interfaces ──
    private final Map<String, Object> jsInterfaces = new HashMap<>();

    // ── Destroyed flag ──
    private boolean destroyed = false;

    // ── Constructors ──

    /** Standard Android constructor — context is ignored in stub. */
    public WebView(Context context) {
        super(NODE_TYPE_WEBVIEW);
    }

    /** No-arg constructor for tests that don't supply a Context. */
    public WebView() {
        super(NODE_TYPE_WEBVIEW);
    }

    // ── Load methods ──

    /**
     * Loads the given URL.
     * OH equivalent: WebviewController.loadUrl(url)
     */
    public void loadUrl(String url) {
        checkNotDestroyed();
        if (url == null) return;

        // Trim any forward history entries beyond current position
        while (history.size() > historyPos + 1) {
            history.remove(history.size() - 1);
        }
        history.add(url);
        historyPos = history.size() - 1;
        currentUrl = url;

        // Notify client: page started
        webViewClient.onPageStarted(this, url, null);

        // Simulate completion
        webChromeClient.onProgressChanged(this, 100);
        webViewClient.onPageFinished(this, url);
    }

    /**
     * Loads the given HTML data.
     * OH equivalent: WebviewController.loadData(data, mimeType, encoding)
     */
    public void loadData(String data, String mimeType, String encoding) {
        checkNotDestroyed();
        loadUrl("about:blank");
    }

    /**
     * Loads HTML data with a base URL.
     * OH equivalent: WebviewController.loadDataWithBaseUrl(...)
     */
    public void loadDataWithBaseURL(String baseUrl, String data,
                                    String mimeType, String encoding,
                                    String historyUrl) {
        checkNotDestroyed();
        String target = (historyUrl != null) ? historyUrl
                       : (baseUrl   != null) ? baseUrl
                       : "about:blank";
        loadUrl(target);
    }

    // ── Navigation ──

    /**
     * Goes back one step in the history list.
     * OH equivalent: WebviewController.backward()
     */
    public void goBack() {
        checkNotDestroyed();
        if (canGoBack()) {
            historyPos--;
            currentUrl = history.get(historyPos);
            webViewClient.onPageStarted(this, currentUrl, null);
            webChromeClient.onProgressChanged(this, 100);
            webViewClient.onPageFinished(this, currentUrl);
        }
    }

    /**
     * Goes forward one step in the history list.
     * OH equivalent: WebviewController.forward()
     */
    public void goForward() {
        checkNotDestroyed();
        if (canGoForward()) {
            historyPos++;
            currentUrl = history.get(historyPos);
            webViewClient.onPageStarted(this, currentUrl, null);
            webChromeClient.onProgressChanged(this, 100);
            webViewClient.onPageFinished(this, currentUrl);
        }
    }

    /**
     * Returns true if there is at least one entry behind the current position.
     * OH equivalent: WebviewController.accessBackward()
     */
    public boolean canGoBack() {
        return historyPos > 0;
    }

    /**
     * Returns true if there is at least one entry ahead of the current position.
     * OH equivalent: WebviewController.accessForward()
     */
    public boolean canGoForward() {
        return historyPos >= 0 && historyPos < history.size() - 1;
    }

    /**
     * Reloads the current page.
     * OH equivalent: WebviewController.refresh()
     */
    public void reload() {
        checkNotDestroyed();
        if (currentUrl != null) {
            webViewClient.onPageStarted(this, currentUrl, null);
            webChromeClient.onProgressChanged(this, 100);
            webViewClient.onPageFinished(this, currentUrl);
        }
    }

    /**
     * Stops the current load.
     * OH equivalent: WebviewController.stop()
     */
    public void stopLoading() {
        checkNotDestroyed();
        // no-op in stub — load is synchronous in simulation
    }

    // ── State accessors ──

    /**
     * Returns the URL of the currently loaded page, or null.
     * OH equivalent: WebviewController.getUrl()
     */
    public String getUrl() {
        return currentUrl;
    }

    /**
     * Returns the title of the currently loaded page, or null.
     * OH equivalent: WebviewController.getTitle()
     */
    public String getTitle() {
        return currentTitle;
    }

    /**
     * Called by the bridge to push title updates received from the ArkUI Web
     * component back into this shim, firing any registered WebChromeClient.
     */
    public void pushTitle(String title) {
        this.currentTitle = title;
        if (webChromeClient != null) {
            webChromeClient.onReceivedTitle(this, title);
        }
    }

    // ── JavaScript ──

    /**
     * Evaluates JavaScript in the context of the current page.
     * OH equivalent: WebviewController.runJavaScript(script, callback)
     *
     * In this stub the callback is invoked immediately with a null result
     * (no JS engine at pure-Java test time).
     */
    public void evaluateJavascript(String script, ValueCallback<String> callback) {
        checkNotDestroyed();
        if (callback != null) {
            callback.onReceiveValue(null);
        }
    }

    /**
     * Registers a Java object so JavaScript can call its methods by {@code name}.
     * OH equivalent: WebviewController.registerJavaScriptProxy(...)
     */
    public void addJavascriptInterface(Object object, String name) {
        checkNotDestroyed();
        jsInterfaces.put(name, object);
    }

    // ── Clients ──

    /**
     * Sets the WebViewClient that receives page-lifecycle events.
     */
    public void setWebViewClient(WebViewClient client) {
        this.webViewClient = (client != null) ? client : new WebViewClient();
    }

    /**
     * Sets the WebChromeClient that receives UI-level browser events.
     */
    public void setWebChromeClient(WebChromeClient client) {
        this.webChromeClient = (client != null) ? client : new WebChromeClient();
    }

    // ── Settings ──

    /**
     * Returns the WebSettings object associated with this WebView.
     * OH equivalent: configuration attributes on the ArkUI {@code <Web>} element.
     */
    public WebSettings getSettings() {
        return settings;
    }

    // ── Lifecycle ──

    /**
     * Destroys the WebView and releases all associated resources.
     * OH equivalent: WebviewController.close() / component unmount.
     *
     * Overrides {@link android.view.View#destroy()} to also clear WebView
     * state before delegating to the base class.
     */
    @Override
    public void destroy() {
        this.destroyed = true;
        history.clear();
        historyPos = -1;
        currentUrl = null;
        currentTitle = null;
        jsInterfaces.clear();
        super.destroy();
    }

    // ── Internal helpers ──

    private void checkNotDestroyed() {
        if (destroyed) {
            throw new IllegalStateException("WebView has been destroyed");
        }
    }
}
