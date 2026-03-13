package android.graphics.pdf;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.ParcelFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.ParcelFileDescriptor;
import java.io.Closeable;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.ParcelFileDescriptor;

import java.io.IOException;

/**
 * Android-compatible PdfRenderer shim. Stub; no actual PDF rendering.
 */
public final class PdfRenderer implements java.io.Closeable {

    public static final int RENDER_MODE_FOR_DISPLAY = 1;
    public static final int RENDER_MODE_FOR_PRINT   = 2;

    private final ParcelFileDescriptor mInput;
    private boolean mClosed;

    public PdfRenderer(ParcelFileDescriptor input) throws IOException {
        if (input == null) throw new IOException("ParcelFileDescriptor is null");
        mInput = input;
    }

    /**
     * Returns the number of pages in the document. Stub always returns 0.
     */
    public int getPageCount() {
        return 0;
    }

    /**
     * Opens and returns the page at the given index.
     */
    public Page openPage(int index) {
        return new Page(index);
    }

    @Override
    public void close() throws IOException {
        mClosed = true;
    }

    public boolean isClosed() { return mClosed; }

    // ------------------------------------------------------------------ //
    // Inner class: Page
    // ------------------------------------------------------------------ //

    public final class Page implements java.io.Closeable {
        private final int mIndex;
        private boolean mClosed;

        Page(int index) {
            mIndex = index;
        }

        /**
         * Renders this page to the given destination bitmap.
         *
         * @param destination the target bitmap (must be ARGB_8888)
         * @param destClip    optional clip rectangle within the destination, or null
         * @param transform   optional transform to apply, or null
         * @param renderMode  {@link #RENDER_MODE_FOR_DISPLAY} or {@link #RENDER_MODE_FOR_PRINT}
         */
        public void render(Bitmap destination, Rect destClip, Matrix transform, int renderMode) {
            // stub: no actual rendering
        }

        /** Returns the page width in points (stub: 612 = US Letter width). */
        public int getWidth() { return 612; }

        /** Returns the page height in points (stub: 792 = US Letter height). */
        public int getHeight() { return 792; }

        /** Returns the zero-based index of this page. */
        public int getIndex() { return mIndex; }

        @Override
        public void close() throws IOException {
            mClosed = true;
        }
    }
}
