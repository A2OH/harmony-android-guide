package android.webkit;

import java.io.InputStream;
import java.util.Map;

/**
 * Shim: android.webkit.WebResourceResponse
 * OH mapping: @ohos.web.webview.WebResourceResponse
 *
 * Encapsulates an HTTP response that the app can return from
 * {@link WebViewClient#shouldInterceptRequest} to intercept resource loads.
 * On OpenHarmony the equivalent is WebResourceResponse in the ArkTS
 * onLoadIntercept / shouldInterceptRequest events.
 *
 * Supports both the legacy 3-argument constructor (MIME type, encoding, data)
 * and the 6-argument constructor that includes a status code, reason phrase,
 * and response headers.
 */
public class WebResourceResponse {

    // ── Fields ──

    private String      mimeType;
    private String      encoding;
    private InputStream data;
    private int         statusCode   = 200;
    private String      reasonPhrase = "OK";
    private Map<String, String> responseHeaders = null;

    // ── Constructors ──

    /**
     * Constructs a response with a MIME type, encoding, and body stream.
     * The status code defaults to 200 OK.
     *
     * OH equivalent: new WebResourceResponse(mimeType, encoding, data)
     *
     * @param mimeType  MIME type of the response (e.g. {@code "text/html"})
     * @param encoding  character encoding (e.g. {@code "UTF-8"})
     * @param data      body as an InputStream, or null for an empty body
     */
    public WebResourceResponse(String mimeType, String encoding, InputStream data) {
        this.mimeType = mimeType;
        this.encoding = encoding;
        this.data     = data;
    }

    /**
     * Constructs a response with full HTTP metadata.
     *
     * OH equivalent: new WebResourceResponse(mimeType, encoding, statusCode, reasonPhrase, headers, data)
     *
     * @param mimeType        MIME type of the response
     * @param encoding        character encoding
     * @param statusCode      HTTP status code (e.g. 200, 404)
     * @param reasonPhrase    HTTP reason phrase (e.g. {@code "OK"}, {@code "Not Found"})
     * @param responseHeaders HTTP response headers, or null
     * @param data            body as an InputStream, or null
     */
    public WebResourceResponse(String mimeType, String encoding,
                               int statusCode, String reasonPhrase,
                               Map<String, String> responseHeaders,
                               InputStream data) {
        this.mimeType         = mimeType;
        this.encoding         = encoding;
        this.statusCode       = statusCode;
        this.reasonPhrase     = reasonPhrase;
        this.responseHeaders  = responseHeaders;
        this.data             = data;
    }

    // ── Getters ──

    /** Returns the MIME type of the response. */
    public String getMimeType() {
        return mimeType;
    }

    /** Sets the MIME type of the response. */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /** Returns the character encoding of the response. */
    public String getEncoding() {
        return encoding;
    }

    /** Sets the character encoding of the response. */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /** Returns the body as an InputStream, or null if there is no body. */
    public InputStream getData() {
        return data;
    }

    /** Sets the body stream. */
    public void setData(InputStream data) {
        this.data = data;
    }

    /** Returns the HTTP status code. */
    public int getStatusCode() {
        return statusCode;
    }

    /** Returns the HTTP reason phrase (e.g. {@code "OK"}). */
    public String getReasonPhrase() {
        return reasonPhrase;
    }

    /**
     * Sets the HTTP status code and reason phrase together.
     *
     * @param statusCode    HTTP status code
     * @param reasonPhrase  corresponding HTTP reason phrase
     */
    public void setStatusCodeAndReasonPhrase(int statusCode, String reasonPhrase) {
        this.statusCode   = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    /** Returns the response headers map, or null if none were set. */
    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    /** Sets the response headers. */
    public void setResponseHeaders(Map<String, String> headers) {
        this.responseHeaders = headers;
    }
}
