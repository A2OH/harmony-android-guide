package android.renderscript;

/**
 * Android-compatible Type shim. Stub for RenderScript memory layout descriptor.
 */
public class Type {

    public static class Builder {
        private final RenderScript mRS;
        private final Element mElement;
        private int mX;
        private int mY;
        private int mZ;
        private boolean mMipmaps;

        public Builder(RenderScript rs, Element e) {
            mRS = rs;
            mElement = e;
            mX = 0;
            mY = 0;
            mZ = 0;
            mMipmaps = false;
        }

        public Builder setX(int value) {
            mX = value;
            return this;
        }

        public Builder setY(int value) {
            mY = value;
            return this;
        }

        public Builder setZ(int value) {
            mZ = value;
            return this;
        }

        public Builder setMipmaps(boolean value) {
            mMipmaps = value;
            return this;
        }

        public Type create() {
            return new Type(mElement, mX, mY, mZ, mMipmaps);
        }
    }

    private final Element mElement;
    private final int mX;
    private final int mY;
    private final int mZ;
    private final boolean mMipmaps;

    protected Type(Element element, int x, int y, int z, boolean mipmaps) {
        mElement = element;
        mX = x;
        mY = y;
        mZ = z;
        mMipmaps = mipmaps;
    }

    public int getX() {
        return mX;
    }

    public int getY() {
        return mY;
    }

    public int getZ() {
        return mZ;
    }

    public Element getElement() {
        return mElement;
    }

    public boolean hasMipmaps() {
        return mMipmaps;
    }

    public int getCount() {
        int count = mX;
        if (mY > 0) count *= mY;
        if (mZ > 0) count *= mZ;
        return count;
    }
}
