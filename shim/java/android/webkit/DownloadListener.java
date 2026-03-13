package android.webkit;

/**
 * Shim for android.webkit.DownloadListener.
 * Interface for listening to download events originating in a WebView.
 */
public interface DownloadListener {

    /**
     * Notify the host application that a file should be downloaded.
     *
     * @param url                the full URL of the content that should be
     *                           downloaded
     * @param userAgent          the user agent string of the downloading
     *                           application
     * @param contentDisposition the content-disposition http header, if
     *                           present
     * @param mimetype           the mime type of the content reported by the
     *                           server
     * @param contentLength      the file size reported by the server
     */
    void onDownloadStart(String url,
                         String userAgent,
                         String contentDisposition,
                         String mimetype,
                         long contentLength);
}
