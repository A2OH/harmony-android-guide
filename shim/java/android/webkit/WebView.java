package android.webkit;

public class WebView {
    public WebView() {}

    public static final int RENDERER_PRIORITY_BOUND = 0;
    public static final int RENDERER_PRIORITY_IMPORTANT = 0;
    public static final int RENDERER_PRIORITY_WAIVED = 0;
    public static final int SCHEME_GEO = 0;
    public static final int SCHEME_MAILTO = 0;
    public static final int SCHEME_TEL = 0;
    public void addJavascriptInterface(Object p0, Object p1) {}
    public boolean canGoBack() { return false; }
    public boolean canGoBackOrForward(Object p0) { return false; }
    public boolean canGoForward() { return false; }
    public void clearCache(Object p0) {}
    public static void clearClientCertPreferences(Object p0) {}
    public void clearFormData() {}
    public void clearHistory() {}
    public void clearMatches() {}
    public void clearSslPreferences() {}
    public void destroy() {}
    public static void disableWebView() {}
    public void documentHasImages(Object p0) {}
    public static void enableSlowWholeDocumentDraw() {}
    public void evaluateJavascript(Object p0, Object p1) {}
    public void findAllAsync(Object p0) {}
    public void findNext(Object p0) {}
    public void flingScroll(Object p0, Object p1) {}
    public int getProgress() { return 0; }
    public boolean getRendererPriorityWaivedWhenNotVisible() { return false; }
    public int getRendererRequestedPriority() { return 0; }
    public void goBack() {}
    public void goBackOrForward(Object p0) {}
    public void goForward() {}
    public void invokeZoomPicker() {}
    public boolean isPrivateBrowsingEnabled() { return false; }
    public void loadData(Object p0, Object p1, Object p2) {}
    public void loadDataWithBaseURL(Object p0, Object p1, Object p2, Object p3, Object p4) {}
    public void loadUrl(Object p0, Object p1) {}
    public void loadUrl(Object p0) {}
    public void onPause() {}
    public void onResume() {}
    public boolean pageDown(Object p0) { return false; }
    public boolean pageUp(Object p0) { return false; }
    public void pauseTimers() {}
    public void postUrl(Object p0, Object p1) {}
    public void postVisualStateCallback(Object p0, Object p1) {}
    public void postWebMessage(Object p0, Object p1) {}
    public void reload() {}
    public void removeJavascriptInterface(Object p0) {}
    public void requestFocusNodeHref(Object p0) {}
    public void requestImageRef(Object p0) {}
    public void resumeTimers() {}
    public void saveWebArchive(Object p0) {}
    public void saveWebArchive(Object p0, Object p1, Object p2) {}
    public static void setDataDirectorySuffix(Object p0) {}
    public void setDownloadListener(Object p0) {}
    public void setFindListener(Object p0) {}
    public void setInitialScale(Object p0) {}
    public void setNetworkAvailable(Object p0) {}
    public void setRendererPriorityPolicy(Object p0, Object p1) {}
    public static void setSafeBrowsingWhitelist(Object p0, Object p1) {}
    public void setTextClassifier(Object p0) {}
    public void setWebChromeClient(Object p0) {}
    public static void setWebContentsDebuggingEnabled(Object p0) {}
    public void setWebViewClient(Object p0) {}
    public void setWebViewRenderProcessClient(Object p0, Object p1) {}
    public void setWebViewRenderProcessClient(Object p0) {}
    public static void startSafeBrowsing(Object p0, Object p1) {}
    public void stopLoading() {}
    public void zoomBy(Object p0) {}
    public boolean zoomIn() { return false; }
    public boolean zoomOut() { return false; }
}
