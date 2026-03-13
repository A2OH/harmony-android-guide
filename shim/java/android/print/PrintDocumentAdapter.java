package android.print;

/**
 * Android-compatible PrintDocumentAdapter shim.
 * Abstract base — apps subclass this to supply print content.
 */
public abstract class PrintDocumentAdapter {

    // -------------------------------------------------------------------------
    // Inner class: LayoutResultCallback
    // -------------------------------------------------------------------------
    public static abstract class LayoutResultCallback {
        /** Call when layout succeeded. */
        public abstract void onLayoutFinished(PrintDocumentInfo info, boolean changed);

        /** Call when layout failed. */
        public abstract void onLayoutFailed(CharSequence error);

        /** Call when layout was cancelled. */
        public void onLayoutCancelled() {}
    }

    // -------------------------------------------------------------------------
    // Inner class: WriteResultCallback
    // -------------------------------------------------------------------------
    public static abstract class WriteResultCallback {
        /** Call when writing of pages succeeded. */
        public abstract void onWriteFinished(PageRange[] pages);

        /** Call when writing of pages failed. */
        public abstract void onWriteFailed(CharSequence error);

        /** Call when writing of pages was cancelled. */
        public void onWriteCancelled() {}
    }

    // -------------------------------------------------------------------------
    // Inner class: PageRange  (minimal stub required by WriteResultCallback)
    // -------------------------------------------------------------------------
    public static final class PageRange {
        public static final PageRange ALL_PAGES = new PageRange(0, Integer.MAX_VALUE);

        private final int mStart;
        private final int mEnd;

        public PageRange(int start, int end) {
            mStart = start;
            mEnd   = end;
        }

        public int getStart() { return mStart; }
        public int getEnd()   { return mEnd; }

        @Override public String toString() {
            return "PageRange{" + mStart + "–" + mEnd + "}";
        }
    }

    // -------------------------------------------------------------------------
    // Inner class: PrintDocumentInfo  (minimal stub)
    // -------------------------------------------------------------------------
    public static final class PrintDocumentInfo {
        public static final int CONTENT_TYPE_UNKNOWN  = 0;
        public static final int CONTENT_TYPE_DOCUMENT = 1;
        public static final int CONTENT_TYPE_PHOTO    = 2;

        private final String mName;
        private final int mPageCount;
        private final int mContentType;

        private PrintDocumentInfo(Builder b) {
            mName        = b.mName;
            mPageCount   = b.mPageCount;
            mContentType = b.mContentType;
        }

        public String getName()      { return mName; }
        public int getPageCount()    { return mPageCount; }
        public int getContentType()  { return mContentType; }

        public static final class Builder {
            private final String mName;
            private int mPageCount   = 1;
            private int mContentType = CONTENT_TYPE_DOCUMENT;

            public Builder(String name) { mName = name; }

            public Builder setPageCount(int pageCount) {
                mPageCount = pageCount;
                return this;
            }

            public Builder setContentType(int contentType) {
                mContentType = contentType;
                return this;
            }

            public PrintDocumentInfo build() {
                return new PrintDocumentInfo(this);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Abstract lifecycle callbacks
    // -------------------------------------------------------------------------

    /** Called when the print process starts. */
    public void onStart() {}

    /**
     * Called when the print attributes (e.g. page size) need to be determined.
     *
     * @param oldAttributes the previously used attributes (may be null on first call)
     * @param newAttributes the new requested attributes
     * @param extras        optional extras bundle (passed as Object to avoid dependency)
     * @param callback      result callback — must always be called
     */
    public abstract void onLayout(
            PrintAttributes oldAttributes,
            PrintAttributes newAttributes,
            Object          extras,
            LayoutResultCallback callback);

    /**
     * Called when the content should be written to the output file descriptor.
     *
     * @param pages    the pages to write
     * @param destination output destination (passed as Object to avoid ParcelFileDescriptor dep)
     * @param extras   optional extras bundle (passed as Object)
     * @param callback result callback — must always be called
     */
    public abstract void onWrite(
            PageRange[]       pages,
            Object            destination,
            Object            extras,
            WriteResultCallback callback);

    /** Called when the print process finishes (success or cancellation). */
    public void onFinish() {}
}
