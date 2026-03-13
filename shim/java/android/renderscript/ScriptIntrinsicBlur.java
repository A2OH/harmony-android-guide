package android.renderscript;

/**
 * Android-compatible ScriptIntrinsicBlur shim. Stub for Gaussian blur intrinsic.
 */
public class ScriptIntrinsicBlur {

    protected ScriptIntrinsicBlur() {}

    public static ScriptIntrinsicBlur create(RenderScript rs, Element e) {
        return new ScriptIntrinsicBlur();
    }

    public void setRadius(float radius) {}

    public void setInput(Allocation ain) {}

    public void forEach(Allocation aout) {}
}
