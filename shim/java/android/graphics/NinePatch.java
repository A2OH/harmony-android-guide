package android.graphics;

/**
 * Android-compatible NinePatch shim. Represents a resizable bitmap
 * with designated stretchable areas.
 */
public class NinePatch {
    private final Bitmap mBitmap;
    private final byte[] mChunk;
    private final String mSrcName;
    private Paint mPaint;

    public NinePatch(Bitmap bitmap, byte[] chunk) {
        this(bitmap, chunk, null);
    }

    public NinePatch(Bitmap bitmap, byte[] chunk, String srcName) {
        mBitmap = bitmap;
        mChunk = chunk;
        mSrcName = srcName;
    }

    public void setPaint(Paint p) {
        mPaint = p;
    }

    public Paint getPaint() {
        return mPaint;
    }

    public void draw(Canvas canvas, Rect location) {}

    public void draw(Canvas canvas, Rect location, Paint paint) {}

    public int getWidth() {
        return mBitmap != null ? mBitmap.getWidth() : 0;
    }

    public int getHeight() {
        return mBitmap != null ? mBitmap.getHeight() : 0;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public String getName() {
        return mSrcName;
    }

    public boolean hasAlpha() {
        return true;
    }

    public final Region getTransparentRegion(Rect bounds) {
        return null;
    }

    public static boolean isNinePatchChunk(byte[] chunk) {
        return chunk != null && chunk.length >= 32;
    }

    public int getDensity() { return mBitmap != null ? mBitmap.getDensity() : 160; }

    public static class InsetStruct {
        public final Rect opticalRect;
        public final Rect contentInsets;
        public final Rect outlineRect;
        public final float outlineRadius;
        public final float outlineAlpha;

        public InsetStruct(int ol, int ot, int or, int ob, int cl, int ct, int cr, int cb,
                int outlineL, int outlineT, int outlineR, int outlineB, float outlineRad,
                int layoutBoundsL, int layoutBoundsT, float decodeScale) {
            opticalRect = new Rect(ol, ot, or, ob);
            contentInsets = new Rect(cl, ct, cr, cb);
            outlineRect = new Rect(outlineL, outlineT, outlineR, outlineB);
            outlineRadius = outlineRad;
            outlineAlpha = 1.0f;
        }

        public static Rect scaleInsets(int l, int t, int r, int b, float scale) {
            return new Rect(
                (int)(l * scale), (int)(t * scale),
                (int)(r * scale), (int)(b * scale));
        }
    }
}
