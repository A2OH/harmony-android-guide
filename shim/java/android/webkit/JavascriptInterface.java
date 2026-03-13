package android.webkit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Shim: android.webkit.JavascriptInterface
 * OH mapping: @ohos.web.webview.WebviewController.registerJavaScriptProxy
 *
 * Annotation used to mark Java methods that are safe to be exposed to
 * JavaScript running inside a WebView via
 * {@link WebView#addJavascriptInterface(Object, String)}.
 *
 * On OpenHarmony, the equivalent mechanism is
 * {@code WebviewController.registerJavaScriptProxy(object, name, methodList)};
 * the bridge runtime scans for this annotation to build the methodList
 * automatically.
 *
 * Only methods annotated with {@code @JavascriptInterface} are exposed;
 * any public method without this annotation is inaccessible from JavaScript
 * (this matches Android API level 17+ behaviour).
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JavascriptInterface {
    // Marker annotation — no elements.
}
