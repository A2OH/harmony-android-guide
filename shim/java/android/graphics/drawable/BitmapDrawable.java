package android.graphics.drawable;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Shader;
import android.service.quicksettings.Tile;
import android.view.Gravity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Shader;
import android.service.quicksettings.Tile;
import android.view.Gravity;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Shim: android.graphics.drawable.BitmapDrawable
 * OH mapping: PixelMap-backed drawing surface
 *
 * Pure Java stub — stores bitmap, gravity, and tile mode; draw() is a no-op.
 */
public class BitmapDrawable extends Drawable {

    // ── Tile mode constants (mirrors android.graphics.Shader.TileMode) ───────

    public enum TileMode {
        CLAMP,
        REPEAT,
        MIRROR
    }

    // ── State ────────────────────────────────────────────────────────────────

    private Bitmap   bitmap;
    private int      gravity  = 0;   // android.view.Gravity.NO_GRAVITY
    private TileMode tileModeX = null;
    private TileMode tileModeY = null;
    private int      alpha    = 0xFF;

    // ── Constructors ─────────────────────────────────────────────────────────

    public BitmapDrawable() {}

    public BitmapDrawable(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    // ── Bitmap ───────────────────────────────────────────────────────────────

    public Bitmap getBitmap() { return bitmap; }

    public void setBitmap(Bitmap bitmap) { this.bitmap = bitmap; }

    // ── Gravity ──────────────────────────────────────────────────────────────

    public int getGravity() { return gravity; }

    public void setGravity(int gravity) { this.gravity = gravity; }

    // ── Tile mode ────────────────────────────────────────────────────────────

    public void setTileModeXY(TileMode xMode, TileMode yMode) {
        this.tileModeX = xMode;
        this.tileModeY = yMode;
    }

    public void setTileModeX(TileMode xMode) { this.tileModeX = xMode; }
    public void setTileModeY(TileMode yMode) { this.tileModeY = yMode; }

    public TileMode getTileModeX() { return tileModeX; }
    public TileMode getTileModeY() { return tileModeY; }

    // ── Intrinsic size ───────────────────────────────────────────────────────

    @Override
    public int getIntrinsicWidth() {
        return bitmap != null ? bitmap.getWidth() : -1;
    }

    @Override
    public int getIntrinsicHeight() {
        return bitmap != null ? bitmap.getHeight() : -1;
    }

    // ── Alpha ────────────────────────────────────────────────────────────────

    @Override
    public int getAlpha() { return alpha; }

    @Override
    public void setAlpha(int alpha) { this.alpha = alpha & 0xFF; }

    // ── Draw (no-op stub) ────────────────────────────────────────────────────

    @Override
    public void draw(Canvas canvas) { /* no-op */ }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "BitmapDrawable(bitmap=" + bitmap + ", gravity=" + gravity + ")";
    }
}
