package android.media;

import java.io.Closeable;

/**
 * Shim stub for android.media.ImageWriter.
 * Allows an application to produce Image data into a Surface for consumption
 * by another component such as a camera or OpenGL.
 */
public class ImageWriter implements Closeable {

    /**
     * Listener interface for receiving notification when the ImageWriter is ready
     * to accept more images (i.e., an image has been released back to the writer).
     */
    public interface OnImageReleasedListener {
        /**
         * Callback that is called when an input Image is returned to the ImageWriter.
         *
         * @param writer the ImageWriter that is ready to accept more images
         */
        void onImageReleased(ImageWriter writer);
    }

    private final Object mSurface;
    private final int mMaxImages;
    private int mFormat;
    private boolean mClosed = false;

    private ImageWriter(Object surface, int maxImages) {
        this.mSurface = surface;
        this.mMaxImages = maxImages;
        this.mFormat = 0; // ImageFormat.UNKNOWN
    }

    /**
     * Create a new ImageWriter.
     *
     * @param surface   the destination Surface this writer produces Image data into
     * @param maxImages the maximum number of images the user will want to access simultaneously
     * @return a new ImageWriter instance
     */
    public static ImageWriter newInstance(Object surface, int maxImages) {
        if (surface == null) {
            throw new IllegalArgumentException("surface must not be null");
        }
        if (maxImages < 1) {
            throw new IllegalArgumentException("maxImages must be at least 1");
        }
        return new ImageWriter(surface, maxImages);
    }

    /**
     * Set the listener to be invoked when the ImageWriter is ready to accept more images.
     *
     * @param listener the listener to set, or null to clear
     * @param handler  the handler on which to invoke the listener (represented as Object)
     */
    public void setOnImageReleasedListener(OnImageReleasedListener listener, Object handler) {
        // stub
    }

    /**
     * Dequeue the next available input Image for the application to produce data into.
     *
     * @return the next available input Image, represented as Object
     */
    public Object dequeueInputImage() {
        return null; // stub
    }

    /**
     * Queue an input Image back to the ImageWriter for the downstream consumer to access.
     *
     * @param image the Image to queue (represented as Object)
     */
    public void queueInputImage(Object image) {
        // stub
    }

    /**
     * Get the image format of this ImageWriter.
     *
     * @return the image format (e.g., ImageFormat constant)
     */
    public int getFormat() {
        return mFormat;
    }

    /**
     * Get the maximum number of images this writer can hold.
     *
     * @return the maximum number of images
     */
    public int getMaxImages() {
        return mMaxImages;
    }

    @Override
    public void close() {
        mClosed = true;
    }
}
