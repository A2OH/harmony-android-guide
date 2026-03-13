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
}
