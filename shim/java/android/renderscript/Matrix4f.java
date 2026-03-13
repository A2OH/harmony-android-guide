package android.renderscript;

/**
 * Android-compatible Matrix4f shim. Stub for 4x4 float matrix used in RenderScript.
 */
public class Matrix4f {

    private final float[] mMat;

    public Matrix4f() {
        mMat = new float[16];
        loadIdentity();
    }

    public Matrix4f(float[] dataArray) {
        mMat = new float[16];
        if (dataArray != null && dataArray.length >= 16) {
// FIXME: // FIXME: // FIXME:             System.arraycopy(dataArray, 0, mMat, 0);
        }
    }

    public void loadIdentity() {
        for (int i = 0; i < 16; i++) mMat[i] = 0f;
        mMat[0] = 1f;
        mMat[5] = 1f;
        mMat[10] = 1f;
        mMat[15] = 1f;
    }

    public float get(int i, int j) {
        return mMat[i * 4 + j];
    }

    public void set(int i, int j, float v) {
        mMat[i * 4 + j] = v;
    }

    public float[] getArray() {
        return mMat;
    }
}
