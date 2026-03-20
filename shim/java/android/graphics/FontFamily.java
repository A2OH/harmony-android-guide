package android.graphics;

/**
 * Stub: FontFamily — native font family handle.
 * Used by Typeface to create font families from system fonts.
 */
public class FontFamily {

    long mNativePtr;

    public FontFamily() {}
    public FontFamily(String[] langs, int variant) {}

    public boolean addFontFromAssetManager(android.content.res.AssetManager mgr, String path,
            int cookie, boolean isAsset, int ttcIndex, int weight, int isItalic,
            android.graphics.fonts.FontVariationAxis[] axes) {
        return false;
    }

    public boolean addFontFromBuffer(java.nio.ByteBuffer font, int ttcIndex,
            android.graphics.fonts.FontVariationAxis[] axes, int weight, int italic) {
        return false;
    }

    public boolean addFont(String path, int ttcIndex, android.graphics.fonts.FontVariationAxis[] axes,
            int weight, int italic) {
        return false;
    }

    public boolean freeze() { return true; }

    public void abortCreation() {}

    public long getNativePtr() { return mNativePtr; }
}
