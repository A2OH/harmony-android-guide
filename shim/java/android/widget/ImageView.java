package android.widget;
import android.view.View;
import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.widget.ImageView → ARKUI_NODE_IMAGE
 */
public class ImageView extends View {
    static final int NODE_TYPE_IMAGE = 4;
    static final int ATTR_IMAGE_SRC = 4000;

    private int imageResource;
    private String imageUri;
    private android.graphics.Bitmap mBitmap;
    private ScaleType mScaleType = ScaleType.FIT_CENTER;

    public ImageView() {
        super(NODE_TYPE_IMAGE);
    }

    public void setImageResource(int resId) {
        this.imageResource = resId;
        if (nativeHandle != 0) {
            OHBridge.nodeSetAttrInt(nativeHandle, ATTR_IMAGE_SRC, resId);
        }
    }

    public void setImageBitmap(android.graphics.Bitmap bm) {
        mBitmap = bm;
    }

    public void setImageURI(Object uri) {
        if (uri != null) {
            this.imageUri = uri.toString();
            if (nativeHandle != 0) {
                OHBridge.nodeSetAttrString(nativeHandle, ATTR_IMAGE_SRC, imageUri);
            }
        }
    }

    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
        if (mBitmap == null || mBitmap.isRecycled()) return;

        int vw = getWidth() - getPaddingLeft() - getPaddingRight();
        int vh = getHeight() - getPaddingTop() - getPaddingBottom();
        int bw = mBitmap.getWidth();
        int bh = mBitmap.getHeight();

        if (vw <= 0 || vh <= 0 || bw <= 0 || bh <= 0) {
            // Fallback: draw at origin if dimensions unknown
            canvas.drawBitmap(mBitmap, getPaddingLeft(), getPaddingTop(), null);
            return;
        }

        float dx = getPaddingLeft();
        float dy = getPaddingTop();
        float scale;

        switch (mScaleType) {
            case FIT_XY:
                // Stretch to fill
                canvas.save();
                canvas.translate(dx, dy);
                canvas.scale((float) vw / bw, (float) vh / bh);
                canvas.drawBitmap(mBitmap, 0, 0, null);
                canvas.restore();
                return;

            case CENTER:
                // Center without scaling
                dx += (vw - bw) / 2f;
                dy += (vh - bh) / 2f;
                canvas.drawBitmap(mBitmap, dx, dy, null);
                return;

            case CENTER_CROP:
                // Scale to fill, crop excess
                scale = Math.max((float) vw / bw, (float) vh / bh);
                dx += (vw - bw * scale) / 2f;
                dy += (vh - bh * scale) / 2f;
                canvas.save();
                canvas.translate(dx, dy);
                canvas.scale(scale, scale);
                canvas.drawBitmap(mBitmap, 0, 0, null);
                canvas.restore();
                return;

            case CENTER_INSIDE:
                // Scale down to fit if larger, otherwise center
                if (bw <= vw && bh <= vh) {
                    dx += (vw - bw) / 2f;
                    dy += (vh - bh) / 2f;
                    canvas.drawBitmap(mBitmap, dx, dy, null);
                    return;
                }
                // Fall through to FIT_CENTER behavior
            case FIT_CENTER:
            default:
                // Scale to fit within bounds, centered
                scale = Math.min((float) vw / bw, (float) vh / bh);
                dx += (vw - bw * scale) / 2f;
                dy += (vh - bh * scale) / 2f;
                canvas.save();
                canvas.translate(dx, dy);
                canvas.scale(scale, scale);
                canvas.drawBitmap(mBitmap, 0, 0, null);
                canvas.restore();
                return;

            case FIT_START:
                scale = Math.min((float) vw / bw, (float) vh / bh);
                canvas.save();
                canvas.translate(dx, dy);
                canvas.scale(scale, scale);
                canvas.drawBitmap(mBitmap, 0, 0, null);
                canvas.restore();
                return;

            case FIT_END:
                scale = Math.min((float) vw / bw, (float) vh / bh);
                dx += vw - bw * scale;
                dy += vh - bh * scale;
                canvas.save();
                canvas.translate(dx, dy);
                canvas.scale(scale, scale);
                canvas.drawBitmap(mBitmap, 0, 0, null);
                canvas.restore();
                return;

            case MATRIX:
                // Draw at origin with no transform (matrix would be applied externally)
                canvas.drawBitmap(mBitmap, dx, dy, null);
                return;
        }
    }

    /** ScaleType enum (subset) */
    public enum ScaleType {
        MATRIX, FIT_XY, FIT_START, FIT_CENTER, FIT_END,
        CENTER, CENTER_CROP, CENTER_INSIDE
    }

    public void setScaleType(ScaleType scaleType) {
        mScaleType = scaleType;
    }

    public ScaleType getScaleType() {
        return mScaleType;
    }
}
