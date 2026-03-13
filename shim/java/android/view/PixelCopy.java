package android.view;

import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * Android-compatible PixelCopy shim.
 * OH mapping: screenshot / buffer-copy APIs (not directly mapped; stub only).
 *
 * Provides a mechanism for applications to copy the content of a Surface or
 * View into a Bitmap without blocking the main thread.
 */
public final class PixelCopy {

    // ── Result codes ──────────────────────────────────────────────────────────

    /** The copy request completed successfully. */
    public static final int SUCCESS                 = 0;
    /** An unknown error occurred. */
    public static final int ERROR_UNKNOWN           = 1;
    /** The request timed out. */
    public static final int ERROR_TIMEOUT           = 2;
    /** The source Surface produced no data. */
    public static final int ERROR_SOURCE_NO_DATA    = 3;
    /** The source Surface is invalid. */
    public static final int ERROR_SOURCE_INVALID    = 4;
    /** The destination Bitmap is invalid. */
    public static final int ERROR_DESTINATION_INVALID = 5;

    // ── Callback interface ────────────────────────────────────────────────────

    /**
     * Callback invoked when a PixelCopy request has completed (or failed).
     */
    public interface OnPixelCopyFinishedListener {
        /**
         * Called with the result of the pixel copy request.
         *
         * @param copyResult  one of the result code constants defined in {@link PixelCopy}.
         */
        void onPixelCopyFinished(int copyResult);
    }

    // ── Static request methods ────────────────────────────────────────────────

    /**
     * Requests a copy of the pixels from {@code source} into {@code dest}.
     * The callback is called on the supplied {@code listenerThread} executor.
     *
     * @param source          the Surface to copy from.
     * @param dest            the Bitmap to copy into; must be non-null and non-recycled.
     * @param listener        called with the result when the request completes.
     * @param listenerThread  executor on which to invoke the listener.
     */
    public static void request(Surface source, Bitmap dest,
                               OnPixelCopyFinishedListener listener,
                               android.os.Handler listenerThread) {
        if (source == null || !source.isValid()) {
            if (listener != null) listener.onPixelCopyFinished(ERROR_SOURCE_INVALID);
            return;
        }
        if (dest == null || dest.isRecycled()) {
            if (listener != null) listener.onPixelCopyFinished(ERROR_DESTINATION_INVALID);
            return;
        }
        // Shim: immediately report success (no actual pixel transfer)
        if (listener != null) listener.onPixelCopyFinished(SUCCESS);
    }

    /**
     * Requests a copy of the pixels within {@code srcRect} from {@code source}
     * into {@code dest}.
     *
     * @param source          the Surface to copy from.
     * @param srcRect         the sub-region of the surface to copy; null means the whole surface.
     * @param dest            the Bitmap to copy into.
     * @param listener        called with the result when the request completes.
     * @param listenerThread  executor on which to invoke the listener.
     */
    public static void request(Surface source, Rect srcRect, Bitmap dest,
                               OnPixelCopyFinishedListener listener,
                               android.os.Handler listenerThread) {
        request(source, dest, listener, listenerThread);
    }

    /**
     * Requests a copy of the pixels from a SurfaceView's Surface into {@code dest}.
     *
     * @param source          the SurfaceView to copy from.
     * @param dest            the Bitmap to copy into.
     * @param listener        called with the result when the request completes.
     * @param listenerThread  executor on which to invoke the listener.
     */
    public static void request(SurfaceView source, Bitmap dest,
                               OnPixelCopyFinishedListener listener,
                               android.os.Handler listenerThread) {
        if (source == null) {
            if (listener != null) listener.onPixelCopyFinished(ERROR_SOURCE_INVALID);
            return;
        }
        request(source.getHolder().getSurface(), dest, listener, listenerThread);
    }

    // Private constructor — utility class
    private PixelCopy() {}
}
