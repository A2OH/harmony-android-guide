package android.renderscript;

/**
 * Android-compatible ScriptIntrinsicColorMatrix shim. Stub for color matrix intrinsic.
 */
public class ScriptIntrinsicColorMatrix {

    protected ScriptIntrinsicColorMatrix() {}

    public static ScriptIntrinsicColorMatrix create(RenderScript rs, Element e) {
        return new ScriptIntrinsicColorMatrix();
    }

    public void setColorMatrix(Matrix4f m) {}

    public void setGreyscale() {}

    public void setRGBtoYUV() {}

    public void setYUVtoRGB() {}

    public void forEach(Allocation ain, Allocation aout) {}

    public void forEach(Allocation aout) {}
}
