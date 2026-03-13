package android.graphics;

/**
 * Android-compatible ColorMatrixColorFilter shim.
 * OH mapping: drawing.OH_Drawing_ColorFilter (matrix variant)
 *
 * A ColorFilter that applies a 4×5 colour matrix to every pixel.
 */
public class ColorMatrixColorFilter extends ColorFilter {

    private final ColorMatrix colorMatrix;

    /**
     * Creates a filter from a raw float[20] color matrix (copied).
     *
     * @param array  20-element row-major 4×5 matrix
     */
    public ColorMatrixColorFilter(float[] array) {
        if (array == null || array.length != 20) {
            throw new IllegalArgumentException("ColorMatrixColorFilter requires a float[20]");
        }
        this.colorMatrix = new ColorMatrix(array);
    }

    /**
     * Creates a filter from an existing ColorMatrix (copied).
     *
     * @param colorMatrix  the source matrix; must not be null
     */
    public ColorMatrixColorFilter(ColorMatrix colorMatrix) {
        if (colorMatrix == null) throw new NullPointerException("colorMatrix must not be null");
        this.colorMatrix = new ColorMatrix(colorMatrix.getArray());
    }

    /** Returns a copy of the underlying ColorMatrix. */
    public ColorMatrix getColorMatrix() {
        return new ColorMatrix(colorMatrix.getArray());
    }

    /** Copies the filter matrix into the supplied ColorMatrix. */
    public void getColorMatrix(ColorMatrix dest) {
        if (dest == null) throw new NullPointerException("dest must not be null");
        dest.set(colorMatrix);
    }
}
