package android.graphics.fonts;

import java.io.File;
import java.io.IOException;

/**
 * Android-compatible Font shim. Stub; no actual font loading.
 */
public final class Font {

    public static final int STYLE_NORMAL = 0;
    public static final int STYLE_ITALIC = 1;

    private final File mFile;
    private final int mWeight;
    private final int mSlant;
    private final int mTtcIndex;

    private Font(Builder builder) {
        mFile = builder.mFile;
        mWeight = builder.mWeight;
        mSlant = builder.mSlant;
        mTtcIndex = builder.mTtcIndex;
    }

    /**
     * Returns the style of this font as a packed int (weight | slant).
     * Stub returns the weight value.
     */
    public int getStyle() {
        return mWeight;
    }

    public File getFile() {
        return mFile;
    }

    public int getTtcIndex() {
        return mTtcIndex;
    }

    public int getWeight() { return mWeight; }
    public int getSlant()  { return mSlant; }

    // ------------------------------------------------------------------ //
    // Inner class: Builder
    // ------------------------------------------------------------------ //

    public static final class Builder {
        final File mFile;
        int mWeight  = 400;
        int mSlant   = STYLE_NORMAL;
        int mTtcIndex = 0;

        public Builder(File file) {
            mFile = file;
        }

        /**
         * Sets the weight (100–900) for the font.
         */
        public Builder setWeight(int weight) {
            mWeight = weight;
            return this;
        }

        /**
         * Sets the slant style: {@link #STYLE_NORMAL} or {@link #STYLE_ITALIC}.
         */
        public Builder setSlant(int slant) {
            mSlant = slant;
            return this;
        }

        /**
         * Sets the TTC index for font collections.
         */
        public Builder setTtcIndex(int ttcIndex) {
            mTtcIndex = ttcIndex;
            return this;
        }

        public Font build() throws IOException {
            return new Font(this);
        }
    }
}
