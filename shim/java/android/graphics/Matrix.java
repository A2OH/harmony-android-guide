package android.graphics;

/**
 * Shim: android.graphics.Matrix
 * Pure Java — real 3x3 affine transformation matrix.
 */
public class Matrix {

    // Index constants matching AOSP
    public static final int MSCALE_X = 0;
    public static final int MSKEW_X  = 1;
    public static final int MTRANS_X = 2;
    public static final int MSKEW_Y  = 3;
    public static final int MSCALE_Y = 4;
    public static final int MTRANS_Y = 5;
    public static final int MPERSP_0 = 6;
    public static final int MPERSP_1 = 7;
    public static final int MPERSP_2 = 8;

    // Row-major 3x3: [MSCALE_X, MSKEW_X, MTRANS_X,
    //                  MSKEW_Y, MSCALE_Y, MTRANS_Y,
    //                  MPERSP_0, MPERSP_1, MPERSP_2]
    private final float[] values = new float[9];

    public static final Matrix IDENTITY_MATRIX = new Matrix();

    public Matrix() {
        reset();
    }

    public Matrix(Matrix src) {
        if (src != null) {
            System.arraycopy(src.values, 0, values, 0, 9);
        } else {
            reset();
        }
    }

    // ── Reset / identity ────────────────────────────────────────────────

    public void reset() {
        for (int i = 0; i < 9; i++) values[i] = 0f;
        values[MSCALE_X] = 1f;
        values[MSCALE_Y] = 1f;
        values[MPERSP_2] = 1f;
    }

    public boolean isIdentity() {
        return values[MSCALE_X] == 1f && values[MSKEW_X]  == 0f && values[MTRANS_X] == 0f
            && values[MSKEW_Y]  == 0f && values[MSCALE_Y] == 1f && values[MTRANS_Y] == 0f
            && values[MPERSP_0] == 0f && values[MPERSP_1] == 0f && values[MPERSP_2] == 1f;
    }

    public boolean isAffine() {
        return values[MPERSP_0] == 0f && values[MPERSP_1] == 0f && values[MPERSP_2] == 1f;
    }

    // ── Get / Set values ────────────────────────────────────────────────

    public void getValues(float[] dst) {
        System.arraycopy(values, 0, dst, 0, 9);
    }

    public void setValues(float[] src) {
        System.arraycopy(src, 0, values, 0, 9);
    }

    public void set(Matrix src) {
        if (src != null) {
            System.arraycopy(src.values, 0, values, 0, 9);
        } else {
            reset();
        }
    }

    // ── Set operations (replace current transform) ──────────────────────

    public void setTranslate(float dx, float dy) {
        reset();
        values[MTRANS_X] = dx;
        values[MTRANS_Y] = dy;
    }

    public void setScale(float sx, float sy, float px, float py) {
        reset();
        values[MSCALE_X] = sx;
        values[MSCALE_Y] = sy;
        values[MTRANS_X] = px - sx * px;
        values[MTRANS_Y] = py - sy * py;
    }

    public void setScale(float sx, float sy) {
        reset();
        values[MSCALE_X] = sx;
        values[MSCALE_Y] = sy;
    }

    public void setRotate(float degrees, float px, float py) {
        double rad = Math.toRadians(degrees);
        float sin = (float) Math.sin(rad);
        float cos = (float) Math.cos(rad);
        reset();
        values[MSCALE_X] = cos;
        values[MSKEW_X]  = -sin;
        values[MTRANS_X] = px - cos * px + sin * py;
        values[MSKEW_Y]  = sin;
        values[MSCALE_Y] = cos;
        values[MTRANS_Y] = py - sin * px - cos * py;
    }

    public void setRotate(float degrees) {
        double rad = Math.toRadians(degrees);
        float sin = (float) Math.sin(rad);
        float cos = (float) Math.cos(rad);
        reset();
        values[MSCALE_X] = cos;
        values[MSKEW_X]  = -sin;
        values[MSKEW_Y]  = sin;
        values[MSCALE_Y] = cos;
    }

    public void setSkew(float kx, float ky, float px, float py) {
        reset();
        values[MSKEW_X]  = kx;
        values[MSKEW_Y]  = ky;
        values[MTRANS_X] = -kx * py;
        values[MTRANS_Y] = -ky * px;
    }

    public void setSkew(float kx, float ky) {
        reset();
        values[MSKEW_X] = kx;
        values[MSKEW_Y] = ky;
    }

    public boolean setConcat(Matrix a, Matrix b) {
        multiply(a.values, b.values, this.values);
        return true;
    }

    // ── Matrix multiplication helper ────────────────────────────────────

    private static void multiply(float[] a, float[] b, float[] result) {
        float[] tmp = new float[9];
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                tmp[r * 3 + c] = a[r * 3 + 0] * b[0 * 3 + c]
                               + a[r * 3 + 1] * b[1 * 3 + c]
                               + a[r * 3 + 2] * b[2 * 3 + c];
            }
        }
        System.arraycopy(tmp, 0, result, 0, 9);
    }

    // ── Post operations (this = this * other) ───────────────────────────

    public boolean postTranslate(float dx, float dy) {
        Matrix t = new Matrix();
        t.setTranslate(dx, dy);
        multiply(t.values, this.values, this.values);
        return true;
    }

    public boolean postScale(float sx, float sy, float px, float py) {
        Matrix t = new Matrix();
        t.setScale(sx, sy, px, py);
        multiply(t.values, this.values, this.values);
        return true;
    }

    public boolean postScale(float sx, float sy) {
        Matrix t = new Matrix();
        t.setScale(sx, sy);
        multiply(t.values, this.values, this.values);
        return true;
    }

    public boolean postRotate(float degrees, float px, float py) {
        Matrix t = new Matrix();
        t.setRotate(degrees, px, py);
        multiply(t.values, this.values, this.values);
        return true;
    }

    public boolean postRotate(float degrees) {
        Matrix t = new Matrix();
        t.setRotate(degrees);
        multiply(t.values, this.values, this.values);
        return true;
    }

    public boolean postSkew(float kx, float ky, float px, float py) {
        Matrix t = new Matrix();
        t.setSkew(kx, ky, px, py);
        multiply(t.values, this.values, this.values);
        return true;
    }

    public boolean postSkew(float kx, float ky) {
        Matrix t = new Matrix();
        t.setSkew(kx, ky);
        multiply(t.values, this.values, this.values);
        return true;
    }

    public boolean postConcat(Matrix other) {
        multiply(other.values, this.values, this.values);
        return true;
    }

    // ── Pre operations (this = other * this) ────────────────────────────

    public boolean preTranslate(float dx, float dy) {
        Matrix t = new Matrix();
        t.setTranslate(dx, dy);
        multiply(this.values, t.values, this.values);
        return true;
    }

    public boolean preScale(float sx, float sy, float px, float py) {
        Matrix t = new Matrix();
        t.setScale(sx, sy, px, py);
        multiply(this.values, t.values, this.values);
        return true;
    }

    public boolean preScale(float sx, float sy) {
        Matrix t = new Matrix();
        t.setScale(sx, sy);
        multiply(this.values, t.values, this.values);
        return true;
    }

    public boolean preRotate(float degrees, float px, float py) {
        Matrix t = new Matrix();
        t.setRotate(degrees, px, py);
        multiply(this.values, t.values, this.values);
        return true;
    }

    public boolean preRotate(float degrees) {
        Matrix t = new Matrix();
        t.setRotate(degrees);
        multiply(this.values, t.values, this.values);
        return true;
    }

    public boolean preSkew(float kx, float ky, float px, float py) {
        Matrix t = new Matrix();
        t.setSkew(kx, ky, px, py);
        multiply(this.values, t.values, this.values);
        return true;
    }

    public boolean preSkew(float kx, float ky) {
        Matrix t = new Matrix();
        t.setSkew(kx, ky);
        multiply(this.values, t.values, this.values);
        return true;
    }

    public boolean preConcat(Matrix other) {
        multiply(this.values, other.values, this.values);
        return true;
    }

    // ── Invert ──────────────────────────────────────────────────────────

    public boolean invert(Matrix inverse) {
        float[] inv = new float[9];
        float[] v = this.values;

        inv[0] = v[4] * v[8] - v[5] * v[7];
        inv[1] = v[2] * v[7] - v[1] * v[8];
        inv[2] = v[1] * v[5] - v[2] * v[4];
        inv[3] = v[5] * v[6] - v[3] * v[8];
        inv[4] = v[0] * v[8] - v[2] * v[6];
        inv[5] = v[2] * v[3] - v[0] * v[5];
        inv[6] = v[3] * v[7] - v[4] * v[6];
        inv[7] = v[1] * v[6] - v[0] * v[7];
        inv[8] = v[0] * v[4] - v[1] * v[3];

        float det = v[0] * inv[0] + v[1] * inv[3] + v[2] * inv[6];
        if (det == 0f) return false;

        float invDet = 1f / det;
        for (int i = 0; i < 9; i++) {
            inv[i] *= invDet;
        }

        if (inverse != null) {
            System.arraycopy(inv, 0, inverse.values, 0, 9);
        }
        return true;
    }

    // ── Map points ──────────────────────────────────────────────────────

    public void mapPoints(float[] dst, int dstIndex, float[] src, int srcIndex, int pointCount) {
        for (int i = 0; i < pointCount; i++) {
            float x = src[srcIndex + i * 2];
            float y = src[srcIndex + i * 2 + 1];
            dst[dstIndex + i * 2]     = values[MSCALE_X] * x + values[MSKEW_X] * y + values[MTRANS_X];
            dst[dstIndex + i * 2 + 1] = values[MSKEW_Y] * x + values[MSCALE_Y] * y + values[MTRANS_Y];
        }
    }

    public void mapPoints(float[] dst, float[] src) {
        mapPoints(dst, 0, src, 0, src.length / 2);
    }

    public void mapPoints(float[] pts) {
        mapPoints(pts, 0, pts, 0, pts.length / 2);
    }

    public float mapRadius(float radius) {
        float sx = values[MSCALE_X];
        float sy = values[MSCALE_Y];
        return radius * (float) Math.sqrt(sx * sx + sy * sy) / (float) Math.sqrt(2);
    }

    public boolean rectStaysRect() {
        return values[MSKEW_X] == 0f && values[MSKEW_Y] == 0f;
    }

    public boolean mapRect(RectF dst, RectF src) {
        float[] pts = {src.left, src.top, src.right, src.top, src.right, src.bottom, src.left, src.bottom};
        mapPoints(pts);
        float minX = pts[0], maxX = pts[0], minY = pts[1], maxY = pts[1];
        for (int i = 1; i < 4; i++) {
            float x = pts[i * 2], y = pts[i * 2 + 1];
            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
        }
        dst.set(minX, minY, maxX, maxY);
        return rectStaysRect();
    }

    public boolean mapRect(RectF rect) {
        return mapRect(rect, rect);
    }

    // ── Equals / hashCode / toString ────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Matrix)) return false;
        Matrix m = (Matrix) o;
        for (int i = 0; i < 9; i++) {
            if (Float.compare(values[i], m.values[i]) != 0) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int h = 17;
        for (float v : values) h = 31 * h + Float.floatToIntBits(v);
        return h;
    }

    @Override
    public String toString() {
        return "Matrix{[" +
            values[0] + ", " + values[1] + ", " + values[2] + "][" +
            values[3] + ", " + values[4] + ", " + values[5] + "][" +
            values[6] + ", " + values[7] + ", " + values[8] + "]}";
    }

    public String toShortString() {
        return "[" + values[0] + ", " + values[1] + ", " + values[2] + "][" +
               values[3] + ", " + values[4] + ", " + values[5] + "][" +
               values[6] + ", " + values[7] + ", " + values[8] + "]";
    }

    public enum ScaleToFit {
        FILL(0),
        START(1),
        CENTER(2),
        END(3);

        final int nativeInt;
        ScaleToFit(int nativeInt) { this.nativeInt = nativeInt; }
    }

    public boolean setRectToRect(RectF src, RectF dst, ScaleToFit stf) {
        if (src == null || dst == null) return false;
        // Simplified: compute a uniform scale + translate
        float srcW = src.width(), srcH = src.height();
        float dstW = dst.width(), dstH = dst.height();
        if (srcW <= 0 || srcH <= 0 || dstW <= 0 || dstH <= 0) {
            reset();
            return false;
        }
        float sx = dstW / srcW;
        float sy = dstH / srcH;
        float scale;
        float tx = 0, ty = 0;
        switch (stf) {
            case FILL:
                reset();
                values[MSCALE_X] = sx;
                values[MSCALE_Y] = sy;
                values[MTRANS_X] = dst.left - src.left * sx;
                values[MTRANS_Y] = dst.top - src.top * sy;
                return true;
            case START:
                scale = Math.min(sx, sy);
                tx = dst.left - src.left * scale;
                ty = dst.top - src.top * scale;
                break;
            case END:
                scale = Math.min(sx, sy);
                tx = dst.right - src.right * scale;
                ty = dst.bottom - src.bottom * scale;
                break;
            case CENTER:
            default:
                scale = Math.min(sx, sy);
                tx = dst.left + (dstW - srcW * scale) * 0.5f - src.left * scale;
                ty = dst.top + (dstH - srcH * scale) * 0.5f - src.top * scale;
                break;
        }
        reset();
        values[MSCALE_X] = scale;
        values[MSCALE_Y] = scale;
        values[MTRANS_X] = tx;
        values[MTRANS_Y] = ty;
        return true;
    }
}
