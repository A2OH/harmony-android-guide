package android.opengl;

/**
 * Android-compatible android.opengl.Matrix shim for A2OH migration.
 *
 * GL matrix math utilities operating on float[16] column-major arrays.
 * All implementations are fully functional (not no-op) so that GL math
 * works correctly in headless / test contexts.
 *
 * NOTE: This is android.opengl.Matrix, NOT android.graphics.Matrix.
 */
public class Matrix {

    private Matrix() {}

    /** Set matrix m to identity. */
    public static void setIdentityM(float[] m, int mOffset) {
        for (int i = 0; i < 16; i++) m[mOffset + i] = 0f;
        m[mOffset]      = 1f;
        m[mOffset + 5]  = 1f;
        m[mOffset + 10] = 1f;
        m[mOffset + 15] = 1f;
    }

    /**
     * Multiply two 4x4 matrices: result = lhs * rhs.
     * result, lhs and rhs may not overlap.
     */
    public static void multiplyMM(float[] result, int resultOffset,
            float[] lhs, int lhsOffset,
            float[] rhs, int rhsOffset) {
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                float sum = 0f;
                for (int k = 0; k < 4; k++) {
                    sum += lhs[lhsOffset + k * 4 + row] * rhs[rhsOffset + col * 4 + k];
                }
                result[resultOffset + col * 4 + row] = sum;
            }
        }
    }

    /**
     * Multiply matrix m (4x4) by vector v (length 4): result = m * v.
     */
    public static void multiplyMV(float[] resultVec, int resultVecOffset,
            float[] lhsMat, int lhsMatOffset,
            float[] rhsVec, int rhsVecOffset) {
        for (int row = 0; row < 4; row++) {
            float sum = 0f;
            for (int col = 0; col < 4; col++) {
                sum += lhsMat[lhsMatOffset + col * 4 + row] * rhsVec[rhsVecOffset + col];
            }
            resultVec[resultVecOffset + row] = sum;
        }
    }

    /** Apply translation (x, y, z) to matrix m (in place). */
    public static void translateM(float[] m, int mOffset, float x, float y, float z) {
        for (int i = 0; i < 4; i++) {
            m[mOffset + 12 + i] +=
                    m[mOffset + i]      * x +
                    m[mOffset + 4  + i] * y +
                    m[mOffset + 8  + i] * z;
        }
    }

    /**
     * Apply translation to src and store in dest.
     */
    public static void translateM(float[] dest, int destOffset,
            float[] src, int srcOffset,
            float x, float y, float z) {
        if (dest != src || destOffset != srcOffset) {
            System.arraycopy(src, srcOffset, dest, destOffset, 16);
        }
        translateM(dest, destOffset, x, y, z);
    }

    /** Apply rotation (angle in degrees, axis x/y/z) to matrix m (in place). */
    public static void rotateM(float[] m, int mOffset,
            float angle, float x, float y, float z) {
        float[] r = new float[16];
        setRotateM(r, 0, angle, x, y, z);
        float[] tmp = new float[16];
        multiplyMM(tmp, 0, m, mOffset, r, 0);
        System.arraycopy(tmp, 0, m, mOffset, 16);
    }

    /**
     * Apply rotation from src and store in dest.
     */
    public static void rotateM(float[] dest, int destOffset,
            float[] src, int srcOffset,
            float angle, float x, float y, float z) {
        float[] r = new float[16];
        setRotateM(r, 0, angle, x, y, z);
        multiplyMM(dest, destOffset, src, srcOffset, r, 0);
    }

    /** Build a rotation matrix into result. */
    public static void setRotateM(float[] rm, int rmOffset,
            float angle, float x, float y, float z) {
        float radians = (float) Math.toRadians(angle);
        float s = (float) Math.sin(radians);
        float c = (float) Math.cos(radians);
        float len = (float) Math.sqrt(x * x + y * y + z * z);
        if (len != 0f) { x /= len; y /= len; z /= len; }
        float nc = 1f - c;

        rm[rmOffset +  0] = x * x * nc + c;
        rm[rmOffset +  1] = y * x * nc + z * s;
        rm[rmOffset +  2] = z * x * nc - y * s;
        rm[rmOffset +  3] = 0f;
        rm[rmOffset +  4] = x * y * nc - z * s;
        rm[rmOffset +  5] = y * y * nc + c;
        rm[rmOffset +  6] = z * y * nc + x * s;
        rm[rmOffset +  7] = 0f;
        rm[rmOffset +  8] = x * z * nc + y * s;
        rm[rmOffset +  9] = y * z * nc - x * s;
        rm[rmOffset + 10] = z * z * nc + c;
        rm[rmOffset + 11] = 0f;
        rm[rmOffset + 12] = 0f;
        rm[rmOffset + 13] = 0f;
        rm[rmOffset + 14] = 0f;
        rm[rmOffset + 15] = 1f;
    }

    /** Apply scale to matrix m (in place). */
    public static void scaleM(float[] m, int mOffset, float x, float y, float z) {
        for (int i = 0; i < 4; i++) {
            m[mOffset + i]      *= x;
            m[mOffset + 4  + i] *= y;
            m[mOffset + 8  + i] *= z;
        }
    }

    /**
     * Apply scale from src and store in dest.
     */
    public static void scaleM(float[] dest, int destOffset,
            float[] src, int srcOffset,
            float x, float y, float z) {
        if (dest != src || destOffset != srcOffset) {
            System.arraycopy(src, srcOffset, dest, destOffset, 16);
        }
        scaleM(dest, destOffset, x, y, z);
    }

    /**
     * Build a look-at view matrix.
     */
    public static void setLookAtM(float[] rm, int rmOffset,
            float eyeX,    float eyeY,    float eyeZ,
            float centerX, float centerY, float centerZ,
            float upX,     float upY,     float upZ) {
        float fx = centerX - eyeX;
        float fy = centerY - eyeY;
        float fz = centerZ - eyeZ;
        float rl = 1f / (float) Math.sqrt(fx * fx + fy * fy + fz * fz);
        fx *= rl; fy *= rl; fz *= rl;

        float sx = fy * upZ - fz * upY;
        float sy = fz * upX - fx * upZ;
        float sz = fx * upY - fy * upX;
        rl = 1f / (float) Math.sqrt(sx * sx + sy * sy + sz * sz);
        sx *= rl; sy *= rl; sz *= rl;

        float ux = sy * fz - sz * fy;
        float uy = sz * fx - sx * fz;
        float uz = sx * fy - sy * fx;

        rm[rmOffset +  0] = sx;
        rm[rmOffset +  1] = ux;
        rm[rmOffset +  2] = -fx;
        rm[rmOffset +  3] = 0f;
        rm[rmOffset +  4] = sy;
        rm[rmOffset +  5] = uy;
        rm[rmOffset +  6] = -fy;
        rm[rmOffset +  7] = 0f;
        rm[rmOffset +  8] = sz;
        rm[rmOffset +  9] = uz;
        rm[rmOffset + 10] = -fz;
        rm[rmOffset + 11] = 0f;
        rm[rmOffset + 12] = -(sx * eyeX + sy * eyeY + sz * eyeZ);
        rm[rmOffset + 13] = -(ux * eyeX + uy * eyeY + uz * eyeZ);
        rm[rmOffset + 14] =  (fx * eyeX + fy * eyeY + fz * eyeZ);
        rm[rmOffset + 15] = 1f;
    }

    /**
     * Build a frustum (perspective) projection matrix.
     */
    public static void frustumM(float[] m, int offset,
            float left, float right, float bottom, float top,
            float near, float far) {
        float rWidth  = 1f / (right - left);
        float rHeight = 1f / (top   - bottom);
        float rDepth  = 1f / (near  - far);
        float x = 2f * near * rWidth;
        float y = 2f * near * rHeight;
        float A = (right + left)  * rWidth;
        float B = (top   + bottom) * rHeight;
        float C = (far   + near)  * rDepth;
        float D = 2f * far * near * rDepth;

        m[offset +  0] = x;  m[offset +  1] = 0f; m[offset +  2] = 0f; m[offset +  3] = 0f;
        m[offset +  4] = 0f; m[offset +  5] = y;  m[offset +  6] = 0f; m[offset +  7] = 0f;
        m[offset +  8] = A;  m[offset +  9] = B;  m[offset + 10] = C;  m[offset + 11] = -1f;
        m[offset + 12] = 0f; m[offset + 13] = 0f; m[offset + 14] = D;  m[offset + 15] = 0f;
    }

    /**
     * Build an orthographic projection matrix.
     */
    public static void orthoM(float[] m, int offset,
            float left, float right, float bottom, float top,
            float near, float far) {
        float rWidth  = 1f / (right - left);
        float rHeight = 1f / (top   - bottom);
        float rDepth  = 1f / (far   - near);

        m[offset +  0] = 2f * rWidth;
        m[offset +  1] = 0f;
        m[offset +  2] = 0f;
        m[offset +  3] = 0f;
        m[offset +  4] = 0f;
        m[offset +  5] = 2f * rHeight;
        m[offset +  6] = 0f;
        m[offset +  7] = 0f;
        m[offset +  8] = 0f;
        m[offset +  9] = 0f;
        m[offset + 10] = -2f * rDepth;
        m[offset + 11] = 0f;
        m[offset + 12] = -(right + left)  * rWidth;
        m[offset + 13] = -(top   + bottom) * rHeight;
        m[offset + 14] = -(far   + near)  * rDepth;
        m[offset + 15] = 1f;
    }

    /**
     * Build a perspective projection matrix from field-of-view.
     * @param fovy vertical field-of-view in degrees
     */
    public static void perspectiveM(float[] m, int offset,
            float fovy, float aspect, float zNear, float zFar) {
        float f = 1f / (float) Math.tan(Math.toRadians(fovy) / 2f);
        float rangeReciprocal = 1f / (zNear - zFar);

        m[offset +  0] = f / aspect;
        m[offset +  1] = 0f;
        m[offset +  2] = 0f;
        m[offset +  3] = 0f;
        m[offset +  4] = 0f;
        m[offset +  5] = f;
        m[offset +  6] = 0f;
        m[offset +  7] = 0f;
        m[offset +  8] = 0f;
        m[offset +  9] = 0f;
        m[offset + 10] = (zFar + zNear) * rangeReciprocal;
        m[offset + 11] = -1f;
        m[offset + 12] = 0f;
        m[offset + 13] = 0f;
        m[offset + 14] = 2f * zFar * zNear * rangeReciprocal;
        m[offset + 15] = 0f;
    }

    /**
     * Invert a 4x4 matrix. Returns true on success, false if non-invertible.
     */
    public static boolean invertM(float[] mInv, int mInvOffset,
            float[] m, int mOffset) {
        // Compute inverse via cofactor expansion
        float[] src = new float[16];
        System.arraycopy(m, mOffset, src, 0, 16);

        float[] dst = new float[16];
        dst[0]  =  cofactor(src, 1,2,3, 1,2,3);
        dst[1]  = -cofactor(src, 0,2,3, 1,2,3);
        dst[2]  =  cofactor(src, 0,1,3, 1,2,3);
        dst[3]  = -cofactor(src, 0,1,2, 1,2,3);
        dst[4]  = -cofactor(src, 1,2,3, 0,2,3);
        dst[5]  =  cofactor(src, 0,2,3, 0,2,3);
        dst[6]  = -cofactor(src, 0,1,3, 0,2,3);
        dst[7]  =  cofactor(src, 0,1,2, 0,2,3);
        dst[8]  =  cofactor(src, 1,2,3, 0,1,3);
        dst[9]  = -cofactor(src, 0,2,3, 0,1,3);
        dst[10] =  cofactor(src, 0,1,3, 0,1,3);
        dst[11] = -cofactor(src, 0,1,2, 0,1,3);
        dst[12] = -cofactor(src, 1,2,3, 0,1,2);
        dst[13] =  cofactor(src, 0,2,3, 0,1,2);
        dst[14] = -cofactor(src, 0,1,3, 0,1,2);
        dst[15] =  cofactor(src, 0,1,2, 0,1,2);

        float det = src[0]*dst[0] + src[1]*dst[4] + src[2]*dst[8] + src[3]*dst[12];
        if (det == 0f) return false;
        float invDet = 1f / det;
        for (int i = 0; i < 16; i++) mInv[mInvOffset + i] = dst[i] * invDet;
        return true;
    }

    /** Helper: 3x3 minor determinant selecting rows r0,r1,r2 and cols c0,c1,c2 from m[16]. */
    private static float cofactor(float[] m, int r0, int r1, int r2, int c0, int c1, int c2) {
        return m[r0 + c0*4] * (m[r1 + c1*4] * m[r2 + c2*4] - m[r2 + c1*4] * m[r1 + c2*4])
             - m[r1 + c0*4] * (m[r0 + c1*4] * m[r2 + c2*4] - m[r2 + c1*4] * m[r0 + c2*4])
             + m[r2 + c0*4] * (m[r0 + c1*4] * m[r1 + c2*4] - m[r1 + c1*4] * m[r0 + c2*4]);
    }

    /**
     * Transpose a 4x4 matrix (mTrans = transpose(m)).
     */
    public static void transposeM(float[] mTrans, int mTransOffset,
            float[] m, int mOffset) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                mTrans[mTransOffset + j * 4 + i] = m[mOffset + i * 4 + j];
            }
        }
    }

    /** Length of a 3-component vector. */
    public static float length(float x, float y, float z) {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }
}
