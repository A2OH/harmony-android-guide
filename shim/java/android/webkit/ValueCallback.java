package android.webkit;

/**
 * Shim: android.webkit.ValueCallback<T>
 *
 * A simple callback interface used by WebView.evaluateJavascript() to
 * deliver asynchronous results.  Matches the Android API signature exactly.
 */
public interface ValueCallback<T> {
    /** Called when the value is available. */
    void onReceiveValue(T value);
}
