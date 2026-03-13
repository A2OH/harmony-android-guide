package android.webkit;

import java.io.InputStream;

/**
 * Android-compatible WebResourceResponse stub.
 */
public class WebResourceResponse {
    private String mMimeType;
    private String mEncoding;
    private InputStream mData;
    private int mStatusCode = 200;
    private String mReasonPhrase = "OK";

    public WebResourceResponse(String mimeType, String encoding, InputStream data) {
        mMimeType = mimeType;
        mEncoding = encoding;
        mData = data;
    }

    public String getMimeType() { return mMimeType; }
    public String getEncoding() { return mEncoding; }
    public InputStream getData() { return mData; }

    public void setStatusCodeAndReasonPhrase(int statusCode, String reasonPhrase) {
        mStatusCode = statusCode;
        mReasonPhrase = reasonPhrase;
    }

    public int getStatusCode() { return mStatusCode; }
    public String getReasonPhrase() { return mReasonPhrase; }
}
