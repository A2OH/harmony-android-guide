package android.graphics;
import android.annotation.ColorInt;
import android.annotation.ColorInt;

/**
 * Shim: android.graphics.RuntimeShader (API 33+)
 * OH mapping: no direct equivalent
 *
 * Allows specifying custom shading logic via AGSL (Android Graphics Shading
 * Language, an SkSL dialect).  This stub stores the SKSL source but performs
 * no actual compilation or rendering — all uniform-setter methods are no-ops.
 */
public class RuntimeShader extends Shader {

    private final String mSksl;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Create a RuntimeShader from the supplied AGSL/SkSL source.
     *
     * @param sksl the shader source code
     */
    public RuntimeShader(String sksl) {
        mSksl = sksl;
    }

    // ── Float uniforms ───────────────────────────────────────────────────────

    /** No-op stub. */
    public void setFloatUniform(String uniformName, float value) { }

    /** No-op stub. */
    public void setFloatUniform(String uniformName, float value1, float value2) { }

    /** No-op stub. */
    public void setFloatUniform(String uniformName, float[] value) { }

    // ── Int uniforms ─────────────────────────────────────────────────────────

    /** No-op stub. */
    public void setIntUniform(String uniformName, int value) { }

    // ── Color uniforms ───────────────────────────────────────────────────────

    /** No-op stub. Sets a color uniform from an {@code @ColorInt}. */
    public void setColorUniform(String uniformName, int color) { }

    /** No-op stub. Sets a color uniform from a {@code @ColorLong}. */
    public void setColorUniform(String uniformName, long color) { }

    // ── Input shader / buffer ────────────────────────────────────────────────

    /** No-op stub. Binds a child shader to the named uniform sampler. */
    public void setInputShader(String shaderName, Shader shader) { }

    /** No-op stub. Binds an input buffer to the named uniform sampler. */
    public void setInputBuffer(String shaderName, Object buffer) { }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "RuntimeShader(sksl.length=" + (mSksl != null ? mSksl.length() : 0) + ")";
    }
}
