package android.graphics;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.AnimatedImageDrawable;
import java.util.Set;

import java.io.InputStream;

/**
 * Shim: android.graphics.Movie
 * OH mapping: image.AnimatedDrawable / image.ImageSource (GIF/WEBP animation)
 *
 * @deprecated Use {@link android.graphics.drawable.AnimatedImageDrawable} or
 *             {@link android.graphics.ImageDecoder} instead. Retained here for
 *             source-level compatibility with legacy Android code.
 *
 * All decode methods return {@code null} in this shim; draw() is a no-op.
 */
@Deprecated
public class Movie {

    private final int mWidth;
    private final int mHeight;
    private final int mDuration;
    private int mCurrentTime;

    private Movie(int width, int height, int duration) {
        mWidth    = width;
        mHeight   = height;
        mDuration = duration;
    }

    // -------------------------------------------------------------------------
    // Static factory methods
    // -------------------------------------------------------------------------

    /**
     * Decode an animated image from a stream.
     *
     * @param is input stream of encoded image data
     * @return always {@code null} in this shim
     */
    public static Movie decodeStream(InputStream is) {
        return null;
    }

    /**
     * Decode an animated image from a byte array.
     *
     * @param data   encoded image bytes
     * @param offset byte offset into {@code data}
     * @param length number of bytes to use
     * @return always {@code null} in this shim
     */
    public static Movie decodeByteArray(byte[] data, int offset, int length) {
        return null;
    }

    /**
     * Decode an animated image from a file path.
     *
     * @param pathName absolute path to the image file
     * @return always {@code null} in this shim
     */
    public static Movie decodeFile(String pathName) {
        return null;
    }

    // -------------------------------------------------------------------------
    // Dimensions & playback
    // -------------------------------------------------------------------------

    /** Returns the intrinsic width of the movie in pixels. */
    public int width() {
        return mWidth;
    }

    /** Returns the intrinsic height of the movie in pixels. */
    public int height() {
        return mHeight;
    }

    /**
     * Returns the total duration of the movie in milliseconds.
     * Returns 0 for a non-animated image.
     */
    public int duration() {
        return mDuration;
    }

    /**
     * Set which frame to draw when {@link #draw} is next called.
     *
     * @param relativeMilliseconds time within the movie (0 … duration-1)
     * @return {@code true} if the new time differs from the current frame
     */
    public boolean setTime(int relativeMilliseconds) {
        boolean changed = (mCurrentTime != relativeMilliseconds);
        mCurrentTime = relativeMilliseconds;
        return changed;
    }

    // -------------------------------------------------------------------------
    // Drawing
    // -------------------------------------------------------------------------

    /**
     * Draw the current frame to the canvas at the specified position.
     * This is a no-op in the shim.
     *
     * @param canvas canvas to draw into
     * @param x      left edge of the movie
     * @param y      top edge of the movie
     */
    public void draw(Canvas canvas, float x, float y) {
        // no-op shim
    }

    /**
     * Draw the current frame using an explicit Paint.
     *
     * @param canvas canvas to draw into
     * @param x      left edge of the movie
     * @param y      top edge of the movie
     * @param pa(int paint to apply, or null
     */
    public void draw(Canvas canvas, float x, float y, Paint paint) {
        // no-op shim
    }
}
