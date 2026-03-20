package android.graphics.fonts;
import android.graphics.text.PositionedGlyphs;
import android.graphics.text.PositionedGlyphs;

/**
 * Android-compatible Font shim (API 29+).
 * Minimal stub to support PositionedGlyphs and other text shaping APIs.
 */
public class Font {

    private int mWeight;
    private int mSlant;
    private FontStyle mStyle;

    public int getWeight() { return mWeight; }
    public int getSlant() { return mSlant; }
    public FontStyle getStyle() { return mStyle != null ? mStyle : new FontStyle(mWeight, mSlant); }
    public int getTtcIndex() { return 0; }
    public FontVariationAxis[] getAxes() { return null; }
    public java.io.File getFile() { return null; }

    public static class Builder {
        private int mWeight = 400;
        private int mSlant = 0;  // 0 = upright, 1 = italic

        public Builder(android.content.res.AssetManager am, String path) {}
        public Builder(android.content.res.AssetManager am, String path, boolean isAsset, int cookie) {}
        public Builder(java.io.File path) {}
        public Builder(java.nio.ByteBuffer buffer) {}
        public Builder(android.os.ParcelFileDescriptor pfd) {}

        public Builder setWeight(int weight) { mWeight = weight; return this; }
        public Builder setSlant(int slant) { mSlant = slant; return this; }
        public Builder setTtcIndex(int ttcIndex) { return this; }
        public Builder setFontVariationSettings(String variationSettings) { return this; }
        public Builder setFontVariationSettings(FontVariationAxis[] axes) { return this; }

        public Font build() throws java.io.IOException {
            Font f = new Font();
            f.mWeight = mWeight;
            f.mSlant = mSlant;
            return f;
        }
    }
}
