package android.graphics;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Config;
import android.util.Size;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Config;
import android.util.Size;

import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Android-compatible ImageDecoder shim. Stub; no actual image decoding.
 */
public final class ImageDecoder {

    // ------------------------------------------------------------------ //
    // Interfaces
    // ------------------------------------------------------------------ //

    public interface OnHeaderDecodedListener {
        void onHeaderDecoded(ImageDecoder decoder, ImageInfo info, Source source);
    }

    public interface OnPartialImageListener {
        boolean onPartialImage(DecodeException exception);
    }

    // ------------------------------------------------------------------ //
    // Inner class: Source
    // ------------------------------------------------------------------ //

    public static abstract class Source {
        Source() {}
    }

    // ------------------------------------------------------------------ //
    // Inner class: ImageInfo
    // ------------------------------------------------------------------ //

    public static final class ImageInfo {
    public ImageInfo(Object... args) {}
        private int mWidth;
        private int mHeight;

        ImageInfo(int width, int height) {
            mWidth = width;
            mHeight = height;
        }

        public int getSize() { return mWidth * mHeight; }
        // No getWidth/getHeight on the public API — size is returned via android.util.Size
        // but to avoid pulling in more deps, expose them directly here for stub purposes.
        public int getWidth()  { return mWidth; }
        public int getHeight() { return mHeight; }
    }

    // ------------------------------------------------------------------ //
    // Inner class: DecodeException
    // ------------------------------------------------------------------ //

    public static final class DecodeException extends IOException {
        public static final int SOURCE_EXCEPTION      = 1;
        public static final int SOURCE_INCOMPLETE     = 2;
        public static final int SOURCE_MALFORMED_DATA = 3;

        private final int mError;

        DecodeException(int error, String msg) {
            super(msg);
            mError = error;
        }

        public int getError() { return mError; }
        public Source getSource() { return null; }
    }

    // ------------------------------------------------------------------ //
    // Static factory methods for Source
    // ------------------------------------------------------------------ //

    public static Source createSource(byte[] data) {
        return new Source() {};
    }

    public static Source createSource(byte[] data, int offset, int length) {
        return new Source() {};
    }

    public static Source createSource(File file) {
        return new Source() {};
    }

    public static Source createSource(InputStream is) {
        return new Source() {};
    }

    public static Source createSource(URI uri) {
        return new Source() {};
    }

    public static Source createSource(android.content.ContentResolver cr, android.net.Uri uri) {
        return new Source() {};
    }

    public static Source createSource(android.content.ContentResolver cr, android.net.Uri uri, android.content.res.Resources res) {
        return new Source() {};
    }

    public static Source createSource(android.content.res.Resources res, InputStream is, int density) {
        return new Source() {};
    }

    public static Source createSource(android.content.res.Resources res, InputStream is) {
        return new Source() {};
    }

    // ALLOCATOR constants already defined above

    // ------------------------------------------------------------------ //
    // Static decode methods
    // ------------------------------------------------------------------ //

    /**
     * Decodes a Bitmap from the given Source. Stub returns a 1x1 ARGB_8888 bitmap.
     */
    public static Bitmap decodeBitmap(Source source) throws IOException {
        return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    }

    /**
     * Decodes a Bitmap with a listener callback.
     */
    public static Bitmap decodeBitmap(Source source, OnHeaderDecodedListener listener)
            throws IOException {
        if (listener != null) {
            ImageDecoder decoder = new ImageDecoder();
            ImageInfo info = new ImageInfo(1);
            listener.onHeaderDecoded(decoder, info, source);
        }
        return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    }

    /**
     * Decodes a Drawable from the given Source. Stub returns a minimal BitmapDrawable-like stub.
     */
    public static Drawable decodeDrawable(Source source) throws IOException {
        return new StubDrawable();
    }

    /**
     * Decodes a Drawable with a listener callback.
     */
    public static Drawable decodeDrawable(Source source, OnHeaderDecodedListener listener)
            throws IOException {
        if (listener != null) {
            ImageDecoder decoder = new ImageDecoder();
            ImageInfo info = new ImageInfo(1);
            listener.onHeaderDecoded(decoder, info, source);
        }
        return new StubDrawable();
    }

    // ------------------------------------------------------------------ //
    // Instance configuration (used inside OnHeaderDecodedListener)
    // ------------------------------------------------------------------ //

    private int mTargetWidth  = -1;
    private int mTargetHeight = -1;

    private ImageDecoder() {}

    public void setTargetSize(int width, int height) {
        mTargetWidth  = width;
        mTargetHeight = height;
    }

    public void setMutableRequired(boolean mutable) {}
    public void setAllocator(int allocator) {}
    public void setOnPartialImageListener(OnPartialImageListener listener) {}
    public void setOutPaddingRect(android.graphics.Rect padding) {}

    public static final int ALLOCATOR_DEFAULT  = 0;
    public static final int ALLOCATOR_SOFTWARE = 1;
    public static final int ALLOCATOR_SHARED_MEMORY = 2;
    public static final int ALLOCATOR_HARDWARE = 3;

    // ------------------------------------------------------------------ //
    // Private stub Drawable used by decodeDrawable
    // ------------------------------------------------------------------ //

    private static final class StubDrawable extends Drawable {
        @Override public void draw(Canvas canvas) {}
        @Override public void setAlpha(int alpha) {}
        @Override public int getAlpha() { return 255; }
        @Override public void setColorFilter(ColorFilter colorFilter) {}
        @Override public int getOpacity() { return 0; }
        @Override public int getIntrinsicWidth()  { return 1; }
        @Override public int getIntrinsicHeight() { return 1; }
    }
}
