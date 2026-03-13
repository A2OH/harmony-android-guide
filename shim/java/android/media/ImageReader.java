package android.media;

import android.view.Surface;

/**
 * Android-compatible ImageReader shim. Stub for reading images from a Surface.
 */
public class ImageReader implements AutoCloseable {

    private final int mWidth;
    private final int mHeight;
    private final int mImageFormat;
    private final int mMaxImages;

    private ImageReader(int width, int height, int format, int maxImages) {
        mWidth = width;
        mHeight = height;
        mImageFormat = format;
        mMaxImages = maxImages;
    }

    public static ImageReader newInstance(int width, int height, int format, int maxImages) {
        return new ImageReader(width, height, format, maxImages);
    }

    public int getWidth() { return mWidth; }
    public int getHeight() { return mHeight; }
    public int getImageFormat() { return mImageFormat; }
    public int getMaxImages() { return mMaxImages; }

    public Image acquireLatestImage() { return null; }
    public Image acquireNextImage() { return null; }

    public Surface getSurface() { return new Surface(); }

    public void setOnImageAvailableListener(OnImageAvailableListener listener, Object handler) {
        // no-op
    }

    @Override
    public void close() {
        // no-op
    }

    /**
     * Callback interface for image availability.
     */
    public interface OnImageAvailableListener {
        void onImageAvailable(ImageReader reader);
    }
}
