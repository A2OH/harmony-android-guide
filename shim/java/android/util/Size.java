package android.util;

/**
 * Android-compatible Size shim. Pure Java — immutable width×height pair.
 */
public class Size {
    private final int mWidth;
    private final int mHeight;

    public Size(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public static Size parseSize(String string) throws NumberFormatException {
        if (string == null) {
            throw new NumberFormatException("string must not be null");
        }
        int sep = string.indexOf('x');
        if (sep < 0) {
            sep = string.indexOf('*');
        }
        if (sep < 0) {
            throw new NumberFormatException("Invalid Size: \"" + string + "\"");
        }
        try {
            return new Size(Integer.parseInt(string.substring(0, sep)),
                            Integer.parseInt(string.substring(sep + 1)));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid Size: \"" + string + "\"");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Size)) return false;
        Size other = (Size) obj;
        return mWidth == other.mWidth && mHeight == other.mHeight;
    }

    @Override
    public int hashCode() {
        return mHeight ^ ((mWidth << (Integer.SIZE / 2)) | (mWidth >>> (Integer.SIZE / 2)));
    }

    @Override
    public String toString() {
        return mWidth + "x" + mHeight;
    }
}
