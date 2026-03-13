package android.graphics;

/**
 * Android-compatible PathMeasure shim. Measures and extracts segments from paths.
 */
public class PathMeasure {
    private Path mPath;
    private boolean mForceClosed;

    public PathMeasure() {}

    public PathMeasure(Path path, boolean forceClosed) {
        mPath = path;
        mForceClosed = forceClosed;
    }

    public void setPath(Path path, boolean forceClosed) {
        mPath = path;
        mForceClosed = forceClosed;
    }

    public float getLength() {
        return 0f;
    }

    public boolean getPosTan(float distance, float[] pos, float[] tan) {
        if (pos != null && pos.length >= 2) {
            pos[0] = 0f;
            pos[1] = 0f;
        }
        if (tan != null && tan.length >= 2) {
            tan[0] = 1f;
            tan[1] = 0f;
        }
        return false;
    }

    public boolean getMatrix(float distance, Matrix matrix, int flags) {
        return false;
    }

    public boolean getSegment(float startD, float stopD, Path dst, boolean startWithMoveTo) {
        return false;
    }

    public boolean isClosed() {
        return mForceClosed;
    }

    public boolean nextContour() {
        return false;
    }

    public static final int POSITION_MATRIX_FLAG = 0x01;
    public static final int TANGENT_MATRIX_FLAG = 0x02;
}
