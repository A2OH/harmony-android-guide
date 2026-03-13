package android.graphics;

/**
 * Shim: android.graphics.BitmapShader
 * OH mapping: drawing.OH_Drawing_ShaderEffect (image shader)
 *
 * Extends Shader.  Tiles a Bitmap as a texture source.
 * Pure Java stub — no actual rendering.
 */
public class BitmapShader extends Shader {

    private final Bitmap    bitmap;
    private final TileMode  tileModeX;
    private final TileMode  tileModeY;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Create a BitmapShader.
     *
     * @param bitmap    the bitmap to tile
     * @param tileModeX tile mode a(long the X axis
     * @param tileModeY tile mode a(long the Y axis
     */
    public BitmapShader(Bitmap bitmap, TileMode tileModeX, TileMode tileModeY) {
        if (bitmap == null) throw new NullPointerException("bitmap must not be null");
        this.bitmap     = bitmap;
        this.tileModeX  = (tileModeX != null) ? tileModeX : TileMode.CLAMP;
        this.tileModeY  = (tileModeY != null) ? tileModeY : TileMode.CLAMP;
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public Bitmap    getBitmap()    { return bitmap; }
    public TileMode  getTileModeX() { return tileModeX; }
    public TileMode  getTileModeY() { return tileModeY; }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "BitmapShader(" + bitmap + ", tileX=" + tileModeX + ", tileY=" + tileModeY + ")";
    }
}
