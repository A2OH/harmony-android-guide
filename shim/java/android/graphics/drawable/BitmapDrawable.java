package android.graphics.drawable;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Shim: android.graphics.drawable.BitmapDrawable
 * OH mapping: PixelMap-backed drawing surface
 *
 * Draws a Bitmap into the drawable bounds using Canvas.drawBitmap.
 * Supports scaling to fill bounds (default) and tile modes.
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
    private boolean  filterBitmap = true;

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

    // ── Filter bitmap ────────────────────────────────────────────────────────

    public void setFilterBitmap(boolean filter) { this.filterBitmap = filter; }
    public boolean isFilterBitmap() { return filterBitmap; }

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

    // ── Draw — scales bitmap to fill bounds via Canvas.drawBitmap ────────────

    @Override
    public void draw(Canvas canvas) {
        if (canvas == null || bitmap == null) return;
        Rect b = getBounds();
        if (b.width() <= 0 || b.height() <= 0) return;

        Paint paint = new Paint();
        paint.setAlpha(alpha);

        // If tile mode is REPEAT, tile the bitmap across bounds
        if (tileModeX == TileMode.REPEAT || tileModeY == TileMode.REPEAT) {
            int bw = bitmap.getWidth();
            int bh = bitmap.getHeight();
            if (bw <= 0 || bh <= 0) return;
            canvas.save();
            canvas.clipRect(b.left, b.top, b.right, b.bottom);
            for (int y = b.top; y < b.bottom; y += bh) {
                for (int x = b.left; x < b.right; x += bw) {
                    canvas.drawBitmap(bitmap, (float) x, (float) y, paint);
                }
            }
            canvas.restore();
        } else {
            // Default: scale bitmap to fill bounds
            Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            canvas.drawBitmap(bitmap, src, b, paint);
        }
    }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "BitmapDrawable(bitmap=" + bitmap + ", gravity=" + gravity + ")";
    }
}
