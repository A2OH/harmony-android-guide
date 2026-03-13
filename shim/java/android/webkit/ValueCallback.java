package android.webkit;

/**
 * Shim: android.webkit.ValueCallback<Object>
 *
 * A simple callback interface used by WebView.evaluateJavascript() to
 * deliver asynchronous results.  Matches the Android API signature exactly.
 */
public interface ValueCallback<Object> {
    /** Called when the value is available. */
    void onReceiveValue(Object value);
}
