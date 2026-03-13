package android.graphics.pdf;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Canvas;
import android.graphics.Rect;

import android.graphics.Canvas;
import android.graphics.Rect;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible PdfDocument shim. Stub; no actual PDF generation.
 */
public class PdfDocument {

    private final List<Page> mPages = new ArrayList<>();
    private Page mCurrentPage;

    public PdfDocument() {}

    /**
     * Starts a new page using the given PageInfo descriptor.
     */
    public Page startPage(PageInfo pageInfo) {
        Page page = new Page(pageInfo);
        mCurrentPage = page;
        return page;
    }

    /**
     * Finishes the currently open page and adds it to the document.
     */
    public void finishPage(Page page) {
        if (page != null) {
            mPages.add(page);
        }
        mCurrentPage = null;
    }

    /**
     * Writes the document to the given output stream. Stub writes nothing.
     */
    public void writeTo(OutputStream out) throws IOException {
        // stub: no actual PDF bytes written
    }

    /**
     * Closes the document and releases resources.
     */
    public void close() {
        mPages.clear();
        mCurrentPage = null;
    }

    /**
     * Returns the list of pages in the document.
     */
    public List<Page> getPages() {
        return new ArrayList<>(mPages);
    }

    // ------------------------------------------------------------------ //
    // Inner class: Page
    // ------------------------------------------------------------------ //

    public static final class Page {
        private final PageInfo mPageInfo;
        private final Canvas mCanvas;

        Page(PageInfo pageInfo) {
            mPageInfo = pageInfo;
            mCanvas = new Canvas();
        }

        /**
         * Returns the Canvas for drawing on this page.
         */
        public Canvas getCanvas() {
            return mCanvas;
        }

        /**
         * Returns the PageInfo that describes this page's dimensions.
         */
        public PageInfo getInfo() {
            return mPageInfo;
        }
    }

    // ------------------------------------------------------------------ //
    // Inner class: PageInfo
    // ------------------------------------------------------------------ //

    public static final class PageInfo {
        private final int mPageWidth;
        private final int mPageHeight;
        private final int mPageNumber;
        private final Rect mContentRect;

        private PageInfo(Builder builder) {
            mPageWidth = builder.mPageWidth;
            mPageHeight = builder.mPageHeight;
            mPageNumber = builder.mPageNumber;
            mContentRect = builder.mContentRect != null
                    ? new Rect(builder.mContentRect)
                    : new Rect(0, 0, mPageWidth, mPageHeight);
        }

        public int getPageWidth() { return mPageWidth; }
        public int getPageHeight() { return mPageHeight; }
        public int getPageNumber() { return mPageNumber; }
        public Rect getContentRect() { return mContentRect; }

        public static final class Builder {
            private final int mPageWidth;
            private final int mPageHeight;
            private final int mPageNumber;
            private Rect mContentRect;

            public Builder(int pageWidth, int pageHeight, int pageNumber) {
                mPageWidth = pageWidth;
                mPageHeight = pageHeight;
                mPageNumber = pageNumber;
            }

            public Builder setContentRect(Rect contentRect) {
                mContentRect = contentRect;
                return this;
            }

            public PageInfo create() {
                return new PageInfo(this);
            }
        }
    }
}
