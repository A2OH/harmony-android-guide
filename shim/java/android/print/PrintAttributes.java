package android.print;

/**
 * Android-compatible PrintAttributes shim.
 * Stores print job configuration; no actual printing.
 */
public final class PrintAttributes {

    public static final int COLOR_MODE_MONOCHROME = 1;
    public static final int COLOR_MODE_COLOR      = 2;

    // -------------------------------------------------------------------------
    // Inner class: MediaSize
    // -------------------------------------------------------------------------
    public static final class MediaSize {
        public static final MediaSize ISO_A0 = new MediaSize("ISO_A0", 84100, 118900);
        public static final MediaSize ISO_A1 = new MediaSize("ISO_A1", 59400, 84100);
        public static final MediaSize ISO_A2 = new MediaSize("ISO_A2", 42000, 59400);
        public static final MediaSize ISO_A3 = new MediaSize("ISO_A3", 29700, 42000);
        public static final MediaSize ISO_A4 = new MediaSize("ISO_A4", 21000, 29700);
        public static final MediaSize ISO_A5 = new MediaSize("ISO_A5", 14800, 21000);
        public static final MediaSize NA_LETTER = new MediaSize("NA_LETTER", 21590, 27940);
        public static final MediaSize NA_LEGAL  = new MediaSize("NA_LEGAL",  21590, 35560);
        public static final MediaSize NA_TABLOID = new MediaSize("NA_TABLOID", 27940, 43180);

        private final String mId;
        private final int mWidthMils;
        private final int mHeightMils;

        public MediaSize(String id, int widthMils, int heightMils) {
            mId        = id;
            mWidthMils = widthMils;
            mHeightMils = heightMils;
        }

        public String getId()          { return mId; }
        public int getWidthMils()      { return mWidthMils; }
        public int getHeightMils()     { return mHeightMils; }
        public boolean isPortrait()    { return mHeightMils >= mWidthMils; }
        public MediaSize asPortrait()  { return isPortrait() ? this : new MediaSize(mId, mHeightMils, mWidthMils); }
        public MediaSize asLandscape() { return isPortrait() ? new MediaSize(mId, mHeightMils, mWidthMils) : this; }

        @Override public String toString() {
            return "MediaSize{" + mId + ", " + mWidthMils + "x" + mHeightMils + "mils}";
        }
    }

    // -------------------------------------------------------------------------
    // Inner class: Resolution
    // -------------------------------------------------------------------------
    public static final class Resolution {
        private final String mId;
        private final String mLabel;
        private final int mHorizontalDpi;
        private final int mVerticalDpi;

        public Resolution(String id, String label, int horizontalDpi, int verticalDpi) {
            mId            = id;
            mLabel         = label;
            mHorizontalDpi = horizontalDpi;
            mVerticalDpi   = verticalDpi;
        }

        public String getId()            { return mId; }
        public String getLabel()         { return mLabel; }
        public int getHorizontalDpi()    { return mHorizontalDpi; }
        public int getVerticalDpi()      { return mVerticalDpi; }

        @Override public String toString() {
            return "Resolution{" + mId + ", " + mHorizontalDpi + "x" + mVerticalDpi + "dpi}";
        }
    }

    // -------------------------------------------------------------------------
    // Inner class: Margins
    // -------------------------------------------------------------------------
    public static final class Margins {
        public static final Margins NO_MARGINS = new Margins(0, 0, 0, 0);

        private final int mLeftMils;
        private final int mTopMils;
        private final int mRightMils;
        private final int mBottomMils;

        public Margins(int leftMils, int topMils, int rightMils, int bottomMils) {
            mLeftMils   = leftMils;
            mTopMils    = topMils;
            mRightMils  = rightMils;
            mBottomMils = bottomMils;
        }

        public int getLeftMils()   { return mLeftMils; }
        public int getTopMils()    { return mTopMils; }
        public int getRightMils()  { return mRightMils; }
        public int getBottomMils() { return mBottomMils; }

        @Override public String toString() {
            return "Margins{l=" + mLeftMils + ",t=" + mTopMils
                    + ",r=" + mRightMils + ",b=" + mBottomMils + "mils}";
        }
    }

    // -------------------------------------------------------------------------
    // PrintAttributes fields
    // -------------------------------------------------------------------------
    private final MediaSize mMediaSize;
    private final Resolution mResolution;
    private final Margins mMinMargins;
    private final int mColorMode;

    private PrintAttributes(Builder b) {
        mMediaSize   = b.mMediaSize;
        mResolution  = b.mResolution;
        mMinMargins  = b.mMinMargins;
        mColorMode   = b.mColorMode;
    }

    public MediaSize  getMediaSize()   { return mMediaSize; }
    public Resolution getResolution()  { return mResolution; }
    public Margins    getMinMargins()  { return mMinMargins; }
    public int        getColorMode()   { return mColorMode; }

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------
    public static final class Builder {
        private MediaSize  mMediaSize  = MediaSize.ISO_A4;
        private Resolution mResolution = new Resolution("RES_300", "300dpi", 300, 300);
        private Margins    mMinMargins = Margins.NO_MARGINS;
        private int        mColorMode  = COLOR_MODE_COLOR;

        public Builder setMediaSize(MediaSize mediaSize) {
            mMediaSize = mediaSize;
            return this;
        }

        public Builder setResolution(Resolution resolution) {
            mResolution = resolution;
            return this;
        }

        public Builder setMinMargins(Margins margins) {
            mMinMargins = margins;
            return this;
        }

        public Builder setColorMode(int colorMode) {
            mColorMode = colorMode;
            return this;
        }

        public PrintAttributes build() {
            return new PrintAttributes(this);
        }
    }

    @Override public String toString() {
        return "PrintAttributes{mediaSize=" + mMediaSize
                + ", resolution=" + mResolution
                + ", minMargins=" + mMinMargins
                + ", colorMode=" + mColorMode + "}";
    }
}
