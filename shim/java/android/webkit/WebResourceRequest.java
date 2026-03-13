package android.webkit;

import android.net.Uri;

import java.util.Map;

/**
 * Shim: android.webkit.WebResourceRequest
 * OH mapping: @ohos.web.webview — WebResourceRequest parameter in onLoadIntercept
 *
 * Passed to {@link WebViewClient#shouldInterceptRequest} and related callbacks
 * to describe an in-flight resource load.  On OpenHarmony the equivalent is the
 * WebResourceRequest object delivered by the ArkUI Web component's intercept
 * events.
 *
 * This is a pure-interface shim; apps that need a concrete instance should
 * create an anonymous implementation or use the bridge-supplied object at
 * runtime.
 */
public interface WebResourceRequest {

    /**
     * Returns the URL being requested.
     * OH equivalent: WebResourceRequest.getRequestUrl()
     */
    Uri getUrl();

    /**
     * Returns true if this is a request for the main frame (top-level document).
     * OH equivalent: WebResourceRequest.isMainFrame()
     */
    boolean isForMainFrame();

    /**
     * Returns true if this request was triggered by a server-side redirect.
     * OH equivalent: WebResourceRequest.isRedirect()
     */
    boolean isRedirect();

    /**
     * Returns true if the request was initiated by a user gesture.
     * OH equivalent: WebResourceRequest.hasGesture()
     */
    boolean hasGesture();

    /**
     * Returns the HTTP method for this request (e.g. {@code "GET"}, {@code "POST"}).
     * OH equivalent: WebResourceRequest.getRequestMethod()
     */
    String getMethod();

    /**
     * Returns the HTTP request headers as an immutable map.
     * OH equivalent: WebResourceRequest.getRequestHeader()
     */
    Map<String, String> getRequestHeaders();
}
