package android.renderscript;

/**
 * Android-compatible Element shim. Stub for RenderScript data element descriptors.
 */
public class Element {

    public enum DataType {
        NONE,
        FLOAT_16,
        FLOAT_32,
        FLOAT_64,
        SIGNED_8,
        SIGNED_16,
        SIGNED_32,
        SIGNED_64,
        UNSIGNED_8,
        UNSIGNED_16,
        UNSIGNED_32,
        UNSIGNED_64,
        BOOLEAN,
        UNSIGNED_5_6_5,
        UNSIGNED_5_5_5_1,
        UNSIGNED_4_4_4_4,
        MATRIX_4X4,
        MATRIX_3X3,
        MATRIX_2X2,
        RS_ELEMENT,
        RS_TYPE,
        RS_ALLOCATION,
        RS_SAMPLER,
        RS_SCRIPT,
        RS_MESH,
        RS_PROGRAM_FRAGMENT,
        RS_PROGRAM_VERTEX,
        RS_PROGRAM_RASTER,
        RS_PROGRAM_STORE,
        RS_FONT
    }

    public enum DataKind {
        USER,
        PIXEL_L,
        PIXEL_A,
        PIXEL_LA,
        PIXEL_RGB,
        PIXEL_RGBA,
        PIXEL_DEPTH,
        PIXEL_YUV
    }

    private final DataType mType;
    private final DataKind mKind;
    private final int mVectorSize;

    protected Element(DataType type, DataKind kind, int vectorSize) {
        mType = type;
        mKind = kind;
        mVectorSize = vectorSize;
    }

    public static Element U8(RenderScript rs) {
        return new Element(DataType.UNSIGNED_8, DataKind.USER, 1);
    }

    public static Element U16(RenderScript rs) {
        return new Element(DataType.UNSIGNED_16, DataKind.USER, 1);
    }

    public static Element U32(RenderScript rs) {
        return new Element(DataType.UNSIGNED_32, DataKind.USER, 1);
    }

    public static Element I8(RenderScript rs) {
        return new Element(DataType.SIGNED_8, DataKind.USER, 1);
    }

    public static Element I16(RenderScript rs) {
        return new Element(DataType.SIGNED_16, DataKind.USER, 1);
    }

    public static Element I32(RenderScript rs) {
        return new Element(DataType.SIGNED_32, DataKind.USER, 1);
    }

    public static Element F32(RenderScript rs) {
        return new Element(DataType.FLOAT_32, DataKind.USER, 1);
    }

    public static Element F64(RenderScript rs) {
        return new Element(DataType.FLOAT_64, DataKind.USER, 1);
    }

    public static Element RGBA_8888(RenderScript rs) {
        return new Element(DataType.UNSIGNED_8, DataKind.PIXEL_RGBA, 4);
    }

    public static Element RGB_888(RenderScript rs) {
        return new Element(DataType.UNSIGNED_8, DataKind.PIXEL_RGB, 3);
    }

    public static Element A_8(RenderScript rs) {
        return new Element(DataType.UNSIGNED_8, DataKind.PIXEL_A, 1);
    }

    public DataType getDataType() {
        return mType;
    }

    public DataKind getDataKind() {
        return mKind;
    }

    public int getVectorSize() {
        return mVectorSize;
    }
}
