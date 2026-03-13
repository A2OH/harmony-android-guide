package android.graphics;

/**
 * Android-compatible ColorMatrix shim.
 * OH mapping: drawing.OH_Drawing_ColorMatrix
 *
 * A 4x5 matrix (stored as a float[20]) applied to RGBA color components.
 * Row-major order: [R', G', B', A'] = M * [R, G, B, A, 1].
 */
public class ColorMatrix {

    private static final int SIZE = 20;

    private final float[] array;

    /** Constructs an identity color matrix. */
    public ColorMatrix() {
        array = new float[SIZE];
        reset();
    }

    /** Constructs a color matrix from the supplied float[20] (copied). */
    public ColorMatrix(float[] src) {
        if (src == null || src.length != SIZE) {
            throw new IllegalArgumentException("ColorMatrix requires a float[20]");
        }
        array = src.clone();
    }

    // ── Accessors ──

    /** Returns the underlying float[20] array (live reference — do not resize). */
    public float[] getArray() { return array; }

    // ── Mutation ──

    /** Copies src into this matrix. */
    public void set(ColorMatrix src) {
        if (src == null) throw new NullPointerException("src must not be null");
        System.arraycopy(src.array, 0, array, 0, SIZE);
    }

    /** Copies the provided float[20] into this matrix. */
    public void set(float[] src) {
        if (src == null || src.length != SIZE) {
            throw new IllegalArgumentException("ColorMatrix.set requires a float[20]");
        }
        System.arraycopy(src, 0, array, 0, SIZE);
    }

    /** Resets to the identity matrix (no colour change). */
    public void reset() {
        for (int i = 0; i < SIZE; i++) array[i] = 0;
        // Diagonal: R→R, G→G, B→B, A→A
        array[0]  = 1; // R scale
        array[6]  = 1; // G scale
        array[12] = 1; // B scale
        array[18] = 1; // A scale
    }

    /**
     * Sets the matrix to scale each channel independently.
     *
     * @param rScale red   scale factor
     * @param gScale green scale factor
     * @param bScale blue  scale factor
     * @param aScale alpha scale factor
     */
    public void setScale(float rScale, float gScale, float bScale, float aScale) {
        for (int i = 0; i < SIZE; i++) array[i] = 0;
        array[0]  = rScale;
        array[6]  = gScale;
        array[12] = bScale;
        array[18] = aScale;
    }

    /**
     * Sets the matrix to change the saturation of colours.
     *
     * @param sat 0 = greyscale, 1 = identity, &gt;1 = over-saturated
     */
    public void setSaturation(float sat) {
        reset();
        float invSat = 1 - sat;
        float R = 0.213f * invSat;
        float G = 0.715f * invSat;
        float B = 0.072f * invSat;
        array[0]  = R + sat; array[1]  = G;       array[2]  = B;
        array[5]  = R;       array[6]  = G + sat;  array[7]  = B;
        array[10] = R;       array[11] = G;        array[12] = B + sat;
    }

    /**
     * Sets this matrix to the concatenation (product) of matA and matB.
     * Result = matA * matB.
     */
    public void setConcat(ColorMatrix matA, ColorMatrix matB) {
        float[] a = matA.array;
        float[] b = matB.array;
        float[] r = new float[SIZE];
        // 4×5 matrix multiply; the 5th column is a translation offset.
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                float sum = (j == 4) ? a[i * 5 + 4] : 0;
                for (int k = 0; k < 4; k++) {
                    sum += a[i * 5 + k] * b[k * 5 + j];
                }
                r[i * 5 + j] = sum;
            }
        }
        System.arraycopy(r, 0, array, 0, SIZE);
    }

    /** Post-concatenates matB: this = this * matB. */
    public void postConcat(ColorMatrix matB) {
        ColorMatrix tmp = new ColorMatrix(array);
        setConcat(tmp, matB);
    }

    /** Pre-concatenates matA: this = matA * this. */
    public void preConcat(ColorMatrix matA) {
        ColorMatrix tmp = new ColorMatrix(array);
        setConcat(matA, tmp);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ColorMatrix[\n");
        for (int row = 0; row < 4; row++) {
            sb.append("  [");
            for (int col = 0; col < 5; col++) {
                sb.append(array[row * 5 + col]);
                if (col < 4) sb.append(", ");
            }
            sb.append("]\n");
        }
        return sb.append("]").toString();
    }
}
