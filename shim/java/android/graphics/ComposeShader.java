package android.graphics;

/**
 * Android-compatible ComposeShader shim. Stub; extends Shader.
 * Composites two shaders together using a Porter-Duff mode or an Xfermode.
 */
public class ComposeShader extends Shader {

    private final Shader mShaderA;
    private final Shader mShaderB;
    private final PorterDuff.Mode mMode;

    /**
     * Composes shaderA and shaderB using the given Porter-Duff blending mode.
     */
    public ComposeShader(Shader shaderA, Shader shaderB, PorterDuff.Mode mode) {
        mShaderA = shaderA;
        mShaderB = shaderB;
        mMode    = mode;
    }

    /**
     * Composes shaderA and shaderB using the given Xfermode.
     * The mode is stored as null since Xfermode is opaque at this shim level.
     */
    public ComposeShader(Shader shaderA, Shader shaderB, Xfermode mode) {
        mShaderA = shaderA;
        mShaderB = shaderB;
        mMode    = null;
    }

    public Shader getShaderA() { return mShaderA; }
    public Shader getShaderB() { return mShaderB; }
    public PorterDuff.Mode getMode() { return mMode; }
}
