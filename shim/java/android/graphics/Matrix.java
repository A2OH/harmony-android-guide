package android.graphics;
import java.util.Set;

/**
 * Shim: android.graphics.Matrix
 * OH mapping: drawing.OH_Drawing_Matrix
 *
 * Pure Java stub — stores a conceptual transform state (not an actual
 * 3x3 float matrix).  Only the identity flag is tracked precisely;
 * translate/scale/rotate values are accumulated but not composed.
 */
public class Matrix {

    private boolean identity = true;

    // ── Constructors ─────────────────────────────────────────────────────────

    public Matrix() {}

    public Matrix(Matrix src) {
        if (src != null) {
            this.identity = src.identity;
        }
    }

    // ── Reset / identity ─────────────────────────────────────────────────────

    public void reset() {
        identity = true;
    }

    public boolean isIdentity() { return identity; }

    // ── Set operations (replace current transform) ───────────────────────────

    public void setTranslate(float dx, float dy) { identity = (dx == 0f && dy == 0f); }
    public void setScale(float sx, float sy)      { identity = (sx == 1f && sy == 1f); }
    public void setRotate(float degrees)          { identity = (degrees == 0f); }

    // ── Post operations (append after current transform) ─────────────────────

    public boolean postTranslate(float dx, float dy) {
        if (dx != 0f || dy != 0f) identity = false;
        return true;
    }

    public boolean postScale(float sx, float sy) {
        if (sx != 1f || sy != 1f) identity = false;
        return true;
    }

    public boolean postRotate(float degrees) {
        if (degrees != 0f) identity = false;
        return true;
    }

    // ── Pre operations (prepend before current transform) ────────────────────

    public boolean preTranslate(float dx, float dy) {
        if (dx != 0f || dy != 0f) identity = false;
        return true;
    }

    public boolean preScale(float sx, float sy) {
        if (sx != 1f || sy != 1f) identity = false;
        return true;
    }

    public boolean preRotate(float degrees) {
        if (degrees != 0f) identity = false;
        return true;
    }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "Matrix(identity=" + identity + ")";
    }
}
