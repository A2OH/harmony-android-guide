package android.graphics;

import java.io.InputStream;

/**
 * Shim: android.graphics.BitmapRegionDecoder
 * OH mapping: image.ImageSource (region decode via decodingOptions.desiredRegion)
 *
 * Decodes a rectangular region of a larger image without loading the entire
 * image into memory. All methods are stubs; actual decoding requires the
 * OH image bridge.
 */
public final class BitmapRegionDecoder {

    private final int mWidth;
    private final int mHeight;
    private boolean mRecycled;

    private BitmapRegionDecoder(int width, int height) {
        mWidth  = width;
        mHeight = height;
    }

    // -------------------------------------------------------------------------
    // Factory methods
    // -------------------------------------------------------------------------

    /**
     * Create a BitmapRegionDecoder from a byte array.
     *
     * @param data        encoded image bytes
     * @param offset      byte offset into {@code data}
     * @param length      number of bytes to use
     * @param isShareable hint; ignored in this shim
     * @return a new decoder instance (stub; no real decoding occurs)
     */
    public static BitmapRegionDecoder newInstance(
            byte[] data, int offset, int length, boolean isShareable) {
        // Stub: return a decoder with unknown dimensions (0×0).
        return new BitmapRegionDecoder(0, 0);
    }

    /**
     * Create a BitmapRegionDecoder from an InputStream.
     *
     * @param is          stream of encoded image data
     * @param isShareable hint; ignored in this shim
     * @return a new decoder instance (stub; no real decoding occurs)
     */
    public static BitmapRegionDecoder newInstance(
            InputStream is, boolean isShareable) {
        return new BitmapRegionDecoder(0, 0);
    }

    // -------------------------------------------------------------------------
    // Decode
    // -------------------------------------------------------------------------

    /**
     * Decode a rectangle of the image.
     *
     * @param rect    the region to decode (in image coordinates)
     * @param options BitmapFactory.Options or null; typed as Object to avoid
     *                hard dependency on BitmapFactory inner class resolution order
     * @return always {@code null} in this shim
     */
    public Bitmap decodeRegion(Rect rect, Object options) {
        if (mRecycled) throw new IllegalStateException("BitmapRegionDecoder has been recycled");
        return null;
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    /** Returns the width of the source image, or 0 if unknown. */
    public int getWidth() {
        if (mRecycled) throw new IllegalStateException("BitmapRegionDecoder has been recycled");
        return mWidth;
    }

    /** Returns the height of the source image, or 0 if unknown. */
    public int getHeight() {
        if (mRecycled) throw new IllegalStateException("BitmapRegionDecoder has been recycled");
        return mHeight;
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    /** Free resources associated with this decoder. */
    public void recycle() {
        mRecycled = true;
    }

    /** Returns true if {@link #recycle()} has been called. */
    public boolean isRecycled() {
        return mRecycled;
    }
}
