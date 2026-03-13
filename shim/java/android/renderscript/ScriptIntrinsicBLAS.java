package android.renderscript;

public final class ScriptIntrinsicBLAS extends ScriptIntrinsic {
    public static final int CONJ_TRANSPOSE = 0;
    public static final int LEFT = 0;
    public static final int LOWER = 0;
    public static final int NON_UNIT = 0;
    public static final int NO_TRANSPOSE = 0;
    public static final int RIGHT = 0;
    public static final int TRANSPOSE = 0;
    public static final int UNIT = 0;
    public static final int UPPER = 0;

    public ScriptIntrinsicBLAS() {}

    public void BNNM(Allocation p0, int p1, Allocation p2, int p3, Allocation p4, int p5, int p6) {}
    public void CGBMV(int p0, int p1, int p2, Float2 p3, Allocation p4, Allocation p5, int p6, Float2 p7, Allocation p8, int p9) {}
    public void CGEMM(int p0, int p1, Float2 p2, Allocation p3, Allocation p4, Float2 p5, Allocation p6) {}
    public void CGEMV(int p0, Float2 p1, Allocation p2, Allocation p3, int p4, Float2 p5, Allocation p6, int p7) {}
    public void CGERC(Float2 p0, Allocation p1, int p2, Allocation p3, int p4, Allocation p5) {}
    public void CGERU(Float2 p0, Allocation p1, int p2, Allocation p3, int p4, Allocation p5) {}
    public void CHBMV(int p0, int p1, Float2 p2, Allocation p3, Allocation p4, int p5, Float2 p6, Allocation p7, int p8) {}
    public void CHEMM(int p0, int p1, Float2 p2, Allocation p3, Allocation p4, Float2 p5, Allocation p6) {}
    public void CHEMV(int p0, Float2 p1, Allocation p2, Allocation p3, int p4, Float2 p5, Allocation p6, int p7) {}
    public void CHER(int p0, float p1, Allocation p2, int p3, Allocation p4) {}
    public void CHER2(int p0, Float2 p1, Allocation p2, int p3, Allocation p4, int p5, Allocation p6) {}
    public void CHER2K(int p0, int p1, Float2 p2, Allocation p3, Allocation p4, float p5, Allocation p6) {}
    public void CHERK(int p0, int p1, float p2, Allocation p3, float p4, Allocation p5) {}
    public void CHPMV(int p0, Float2 p1, Allocation p2, Allocation p3, int p4, Float2 p5, Allocation p6, int p7) {}
    public void CHPR(int p0, float p1, Allocation p2, int p3, Allocation p4) {}
    public void CHPR2(int p0, Float2 p1, Allocation p2, int p3, Allocation p4, int p5, Allocation p6) {}
    public void CSYMM(int p0, int p1, Float2 p2, Allocation p3, Allocation p4, Float2 p5, Allocation p6) {}
    public void CSYR2K(int p0, int p1, Float2 p2, Allocation p3, Allocation p4, Float2 p5, Allocation p6) {}
    public void CSYRK(int p0, int p1, Float2 p2, Allocation p3, Float2 p4, Allocation p5) {}
    public void CTBMV(int p0, int p1, int p2, int p3, Allocation p4, Allocation p5, int p6) {}
    public void CTBSV(int p0, int p1, int p2, int p3, Allocation p4, Allocation p5, int p6) {}
    public void CTPMV(int p0, int p1, int p2, Allocation p3, Allocation p4, int p5) {}
    public void CTPSV(int p0, int p1, int p2, Allocation p3, Allocation p4, int p5) {}
    public void CTRMM(int p0, int p1, int p2, int p3, Float2 p4, Allocation p5, Allocation p6) {}
    public void CTRMV(int p0, int p1, int p2, Allocation p3, Allocation p4, int p5) {}
    public void CTRSM(int p0, int p1, int p2, int p3, Float2 p4, Allocation p5, Allocation p6) {}
    public void CTRSV(int p0, int p1, int p2, Allocation p3, Allocation p4, int p5) {}
    public void DGBMV(int p0, int p1, int p2, double p3, Allocation p4, Allocation p5, int p6, double p7, Allocation p8, int p9) {}
    public void DGEMM(int p0, int p1, double p2, Allocation p3, Allocation p4, double p5, Allocation p6) {}
    public void DGEMV(int p0, double p1, Allocation p2, Allocation p3, int p4, double p5, Allocation p6, int p7) {}
    public void DGER(double p0, Allocation p1, int p2, Allocation p3, int p4, Allocation p5) {}
    public void DSBMV(int p0, int p1, double p2, Allocation p3, Allocation p4, int p5, double p6, Allocation p7, int p8) {}
    public void DSPMV(int p0, double p1, Allocation p2, Allocation p3, int p4, double p5, Allocation p6, int p7) {}
    public void DSPR(int p0, double p1, Allocation p2, int p3, Allocation p4) {}
    public void DSPR2(int p0, double p1, Allocation p2, int p3, Allocation p4, int p5, Allocation p6) {}
    public void DSYMM(int p0, int p1, double p2, Allocation p3, Allocation p4, double p5, Allocation p6) {}
    public void DSYMV(int p0, double p1, Allocation p2, Allocation p3, int p4, double p5, Allocation p6, int p7) {}
    public void DSYR(int p0, double p1, Allocation p2, int p3, Allocation p4) {}
    public void DSYR2(int p0, double p1, Allocation p2, int p3, Allocation p4, int p5, Allocation p6) {}
    public void DSYR2K(int p0, int p1, double p2, Allocation p3, Allocation p4, double p5, Allocation p6) {}
    public void DSYRK(int p0, int p1, double p2, Allocation p3, double p4, Allocation p5) {}
    public void DTBMV(int p0, int p1, int p2, int p3, Allocation p4, Allocation p5, int p6) {}
    public void DTBSV(int p0, int p1, int p2, int p3, Allocation p4, Allocation p5, int p6) {}
    public void DTPMV(int p0, int p1, int p2, Allocation p3, Allocation p4, int p5) {}
    public void DTPSV(int p0, int p1, int p2, Allocation p3, Allocation p4, int p5) {}
    public void DTRMM(int p0, int p1, int p2, int p3, double p4, Allocation p5, Allocation p6) {}
    public void DTRMV(int p0, int p1, int p2, Allocation p3, Allocation p4, int p5) {}
    public void DTRSM(int p0, int p1, int p2, int p3, double p4, Allocation p5, Allocation p6) {}
    public void DTRSV(int p0, int p1, int p2, Allocation p3, Allocation p4, int p5) {}
    public void SGBMV(int p0, int p1, int p2, float p3, Allocation p4, Allocation p5, int p6, float p7, Allocation p8, int p9) {}
    public void SGEMM(int p0, int p1, float p2, Allocation p3, Allocation p4, float p5, Allocation p6) {}
    public void SGEMV(int p0, float p1, Allocation p2, Allocation p3, int p4, float p5, Allocation p6, int p7) {}
    public void SGER(float p0, Allocation p1, int p2, Allocation p3, int p4, Allocation p5) {}
    public void SSBMV(int p0, int p1, float p2, Allocation p3, Allocation p4, int p5, float p6, Allocation p7, int p8) {}
    public void SSPMV(int p0, float p1, Allocation p2, Allocation p3, int p4, float p5, Allocation p6, int p7) {}
    public void SSPR(int p0, float p1, Allocation p2, int p3, Allocation p4) {}
    public void SSPR2(int p0, float p1, Allocation p2, int p3, Allocation p4, int p5, Allocation p6) {}
    public void SSYMM(int p0, int p1, float p2, Allocation p3, Allocation p4, float p5, Allocation p6) {}
    public void SSYMV(int p0, float p1, Allocation p2, Allocation p3, int p4, float p5, Allocation p6, int p7) {}
    public void SSYR(int p0, float p1, Allocation p2, int p3, Allocation p4) {}
    public void SSYR2(int p0, float p1, Allocation p2, int p3, Allocation p4, int p5, Allocation p6) {}
    public void SSYR2K(int p0, int p1, float p2, Allocation p3, Allocation p4, float p5, Allocation p6) {}
    public void SSYRK(int p0, int p1, float p2, Allocation p3, float p4, Allocation p5) {}
    public void STBMV(int p0, int p1, int p2, int p3, Allocation p4, Allocation p5, int p6) {}
    public void STBSV(int p0, int p1, int p2, int p3, Allocation p4, Allocation p5, int p6) {}
    public void STPMV(int p0, int p1, int p2, Allocation p3, Allocation p4, int p5) {}
    public void STPSV(int p0, int p1, int p2, Allocation p3, Allocation p4, int p5) {}
    public void STRMM(int p0, int p1, int p2, int p3, float p4, Allocation p5, Allocation p6) {}
    public void STRMV(int p0, int p1, int p2, Allocation p3, Allocation p4, int p5) {}
    public void STRSM(int p0, int p1, int p2, int p3, float p4, Allocation p5, Allocation p6) {}
    public void STRSV(int p0, int p1, int p2, Allocation p3, Allocation p4, int p5) {}
    public void ZGBMV(int p0, int p1, int p2, Double2 p3, Allocation p4, Allocation p5, int p6, Double2 p7, Allocation p8, int p9) {}
    public void ZGEMM(int p0, int p1, Double2 p2, Allocation p3, Allocation p4, Double2 p5, Allocation p6) {}
    public void ZGEMV(int p0, Double2 p1, Allocation p2, Allocation p3, int p4, Double2 p5, Allocation p6, int p7) {}
    public void ZGERC(Double2 p0, Allocation p1, int p2, Allocation p3, int p4, Allocation p5) {}
    public void ZGERU(Double2 p0, Allocation p1, int p2, Allocation p3, int p4, Allocation p5) {}
    public void ZHBMV(int p0, int p1, Double2 p2, Allocation p3, Allocation p4, int p5, Double2 p6, Allocation p7, int p8) {}
    public void ZHEMM(int p0, int p1, Double2 p2, Allocation p3, Allocation p4, Double2 p5, Allocation p6) {}
    public void ZHEMV(int p0, Double2 p1, Allocation p2, Allocation p3, int p4, Double2 p5, Allocation p6, int p7) {}
    public void ZHER(int p0, double p1, Allocation p2, int p3, Allocation p4) {}
    public void ZHER2(int p0, Double2 p1, Allocation p2, int p3, Allocation p4, int p5, Allocation p6) {}
    public void ZHER2K(int p0, int p1, Double2 p2, Allocation p3, Allocation p4, double p5, Allocation p6) {}
    public void ZHERK(int p0, int p1, double p2, Allocation p3, double p4, Allocation p5) {}
    public void ZHPMV(int p0, Double2 p1, Allocation p2, Allocation p3, int p4, Double2 p5, Allocation p6, int p7) {}
    public void ZHPR(int p0, double p1, Allocation p2, int p3, Allocation p4) {}
    public void ZHPR2(int p0, Double2 p1, Allocation p2, int p3, Allocation p4, int p5, Allocation p6) {}
    public void ZSYMM(int p0, int p1, Double2 p2, Allocation p3, Allocation p4, Double2 p5, Allocation p6) {}
    public void ZSYR2K(int p0, int p1, Double2 p2, Allocation p3, Allocation p4, Double2 p5, Allocation p6) {}
    public void ZSYRK(int p0, int p1, Double2 p2, Allocation p3, Double2 p4, Allocation p5) {}
    public void ZTBMV(int p0, int p1, int p2, int p3, Allocation p4, Allocation p5, int p6) {}
    public void ZTBSV(int p0, int p1, int p2, int p3, Allocation p4, Allocation p5, int p6) {}
    public void ZTPMV(int p0, int p1, int p2, Allocation p3, Allocation p4, int p5) {}
    public void ZTPSV(int p0, int p1, int p2, Allocation p3, Allocation p4, int p5) {}
    public void ZTRMM(int p0, int p1, int p2, int p3, Double2 p4, Allocation p5, Allocation p6) {}
    public void ZTRMV(int p0, int p1, int p2, Allocation p3, Allocation p4, int p5) {}
    public void ZTRSM(int p0, int p1, int p2, int p3, Double2 p4, Allocation p5, Allocation p6) {}
    public void ZTRSV(int p0, int p1, int p2, Allocation p3, Allocation p4, int p5) {}
    public static ScriptIntrinsicBLAS create(RenderScript p0) { return null; }
}
