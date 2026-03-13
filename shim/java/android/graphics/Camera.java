package android.graphics;

/**
 * Android-compatible Camera shim. 3D transformation helper that computes
 * a Matrix from rotations and translations in 3D space.
 */
public class Camera {
    private float mLocationX;
    private float mLocationY;
    private float mLocationZ = -8.0f;

    public Camera() {}

    public void save() {}

    public void restore() {}

    public void translate(float x, float y, float z) {}

    public void rotateX(float deg) {}

    public void rotateY(float deg) {}

    public void rotateZ(float deg) {}

    public void rotate(float x, float y, float z) {
        rotateX(x);
        rotateY(y);
        rotateZ(z);
    }

    public void getMatrix(Matrix matrix) {
        if (matrix != null) {
            matrix.reset();
        }
    }

    public void applyToCanvas(Canvas canvas) {}

    public float dotWithNormal(float dx, float dy, float dz) {
        return 0f;
    }

    public void setLocation(float x, float y, float z) {
        mLocationX = x;
        mLocationY = y;
        mLocationZ = z;
    }

    public float getLocationX() { return mLocationX; }
    public float getLocationY() { return mLocationY; }
    public float getLocationZ() { return mLocationZ; }
}
