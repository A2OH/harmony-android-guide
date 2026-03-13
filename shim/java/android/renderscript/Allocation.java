package android.renderscript;

/**
 * Android-compatible Allocation shim. Stub for RenderScript memory allocation.
 */
public class Allocation {

    public static final int USAGE_SCRIPT = 1;
    public static final int USAGE_GRAPHICS_TEXTURE = 2;

    private final Type mType;
    private final int mUsage;

    protected Allocation(Type type, int usage) {
        mType = type;
        mUsage = usage;
    }

    public static Allocation createSized(RenderScript rs, Element e, int count) {
        Type t = new Type.Builder(rs, e).setX(count).create();
        return new Allocation(t, USAGE_SCRIPT);
    }

    public static Allocation createSized(RenderScript rs, Element e, int count, int usage) {
        Type t = new Type.Builder(rs, e).setX(count).create();
        return new Allocation(t, usage);
    }

    public static Allocation createTyped(RenderScript rs, Type type) {
        return new Allocation(type, USAGE_SCRIPT);
    }

    public static Allocation createTyped(RenderScript rs, Type type, int usage) {
        return new Allocation(type, usage);
    }

    public static Allocation createFromBitmap(RenderScript rs, Object bitmap) {
        return new Allocation(null, USAGE_SCRIPT);
    }

    public static Allocation createFromBitmap(RenderScript rs, Object bitmap, Object mips, int usage) {
        return new Allocation(null, usage);
    }

    public void copyFrom(int[] d) {}

    public void copyFrom(float[] d) {}

    public void copyFrom(short[] d) {}

    public void copyFrom(byte[] d) {}

    public void copyTo(int[] d) {}

    public void copyTo(float[] d) {}

    public void copyTo(short[] d) {}

    public void copyTo(byte[] d) {}

    public Type getType() {
        return mType;
    }

    public int getUsage() {
        return mUsage;
    }

    public void destroy() {}
}
