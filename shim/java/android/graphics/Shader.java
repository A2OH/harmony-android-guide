package android.graphics;

/**
 * Shim: android.graphics.Shader
 * OH mapping: drawing.OH_Drawing_ShaderEffect
 *
 * Base class for gradient and bitmap shaders.  Pure Java stub — no native
 * rendering; the local matrix is tracked in a Matrix instance.
 */
public class Shader {

    // ── TileMode ─────────────────────────────────────────────────────────────

    public enum TileMode {
        /** Replicate the edge color if the shader draws outside its bounds. */
        CLAMP,
        /** Repeat the shader image horizontally and vertically. */
        REPEAT,
        /** Repeat the shader image, alternating mirror images. */
        MIRROR
    }

    // ── State ────────────────────────────────────────────────────────────────

    private Matrix mLocalMatrix;

    // ── Constructor ──────────────────────────────────────────────────────────

    public Shader() {}

    // ── Local matrix ─────────────────────────────────────────────────────────

    /**
     * Set the shader's local transformation matrix.
     * @param localM matrix to copy (may be null to clear)
     */
    public void setLocalMatrix(Matrix localM) {
        if (localM == null) {
            mLocalMatrix = null;
        } else {
            if (mLocalMatrix == null) mLocalMatrix = new Matrix();
            mLocalMatrix = new Matrix(localM);
        }
    }

    /**
     * Copy the shader's local matrix into the supplied matrix.
     * @param localM output matrix; if null a new Matrix is returned
     * @return true if the shader has a non-identity local matrix
     */
    public boolean getLocalMatrix(Matrix localM) {
        if (mLocalMatrix != null && !mLocalMatrix.isIdentity()) {
            if (localM != null) localM = new Matrix(mLocalMatrix);
            return true;
        }
        return false;
    }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(localMatrix=" + mLocalMatrix + ")";
    }
}
