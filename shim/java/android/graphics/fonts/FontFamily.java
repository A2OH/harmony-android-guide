package android.graphics.fonts;

import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible FontFamily shim. Stub; no actual font loading.
 */
public final class FontFamily {

    private final List<Font> mFonts;

    private FontFamily(Builder builder) {
        mFonts = new ArrayList<>(builder.mFonts);
    }

    public List<Font> getFonts() {
        return new ArrayList<>(mFonts);
    }

    public int getSize() {
        return mFonts.size();
    }

    public Font getFont(int index) {
        return mFonts.get(index);
    }

    // ------------------------------------------------------------------ //
    // Inner class: Builder
    // ------------------------------------------------------------------ //

    public static final class Builder {
        final List<Font> mFonts = new ArrayList<>();

        public Builder() {}

        /**
         * Adds a font to the family being built.
         */
        public Builder addFont(Font font) {
            if (font != null) mFonts.add(font);
            return this;
        }

        public FontFamily build() {
            return new FontFamily(this);
        }
    }
}
