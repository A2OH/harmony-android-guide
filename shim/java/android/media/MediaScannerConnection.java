package android.media;
import android.net.Uri;
import android.net.Uri;

/**
 * Shim stub for android.media.MediaScannerConnection.
 * Provides a way for applications to pass a newly-created or downloaded media file
 * to the media scanner service.
 */
public class MediaScannerConnection {

    /**
     * Interface for receiving notification of completion of a media scan request.
     */
    public interface OnScanCompletedListener {
        /**
         * Called to notify the client when the media scanner has finished scanning a file.
         *
         * @param path the path to the file that has been scanned
         * @param uri  the Uri for the file if the scanning operation succeeded, null otherwise
         */
        void onScanCompleted(String path, Object uri);
    }

    /**
     * Interface for notifying clients of MediaScannerConnection about connection
     * and disconnection from the media scanner service, as well as scan completions.
     */
    public interface MediaScannerConnectionClient extends OnScanCompletedListener {
        /**
         * Called to notify the client when a connection to the MediaScanner service has been
         * established.
         */
        void onMediaScannerConnected();
    }

    private final Object mContext;
    private final MediaScannerConnectionClient mClient;
    private boolean mConnected = false;

    public MediaScannerConnection(Object context, MediaScannerConnectionClient client) {
        this.mContext = context;
        this.mClient = client;
    }

    /**
     * Convenience method for scanning a single file or multiple files, without needing
     * to connect to the MediaScanner service yourself.
     *
     * @param context   the application context
     * @param paths     the file paths to be scanned
     * @param mimeTypes optional MIME types for each path (may be null)
     * @param listener  optional listener to receive scan-completed callbacks (may be null)
     */
    public static void scanFile(Object context, String[] paths, String[] mimeTypes,
            OnScanCompletedListener listener) {
        // stub — notify listener immediately with null URIs
        if (listener != null && paths != null) {
            for (String path : paths) {
                listener.onScanCompleted(path, null);
            }
        }
    }

    public void connect() {
        mConnected = true;
        if (mClient != null) {
            mClient.onMediaScannerConnected();
        }
    }

    public void disconnect() {
        mConnected = false;
    }

    public boolean isConnected() {
        return mConnected;
    }

    /**
     * Object the media scanner to scan a file.
     *
     * @param path     the path to the file to be scanned
     * @param mimeType optional MIME type of the file (may be null)
     */
    public void scanFile(String path, String mimeType) {
        if (mClient != null) {
            mClient.onScanCompleted(path, null);
        }
    }
}
