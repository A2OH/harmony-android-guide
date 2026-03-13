package android.opengl;
import android.view.View;
import android.view.View;

/**
 * Android-compatible GLSurfaceView shim for A2OH migration.
 *
 * Extends Object (not android.view.View) so this file compiles standalone
 * without the View shim on the classpath.
 *
 * The Renderer interface uses Object placeholders for GL10 and EGLConfig so
 * that javax.microedition.khronos stubs are not required.  App code that
 * already depends on those types should add the appropriate shims; this class
 * casts correctly at call sites once the full shim library is assembled.
 */
public class GLSurfaceView {

    public static final int RENDERMODE_WHEN_DIRTY   = 0;
    public static final int RENDERMODE_CONTINUOUSLY = 1;

    private Renderer mRenderer;
    private int mRenderMode = RENDERMODE_CONTINUOUSLY;

    public GLSurfaceView() {}

    /**
     * Renderer callback interface.
     *
     * Parameter types are Object to avoid a hard compile-time dependency on
     * javax.microedition.khronos.opengles.GL10 and
     * javax.microedition.khronos.egl.EGLConfig.
     * Cast to those types inside your implementation when the full runtime is
     * present.
     */
    public interface Renderer {
        /** Called when the surface is created or recreated. gl is GL10, config is EGLConfig. */
        void onSurfaceCreated(Object gl, Object config);
        /** Called when the surface size changes. gl is GL10. */
        void onSurfaceChanged(Object gl, int width, int height);
        /** Called to draw the current frame. gl is GL10. */
        void onDrawFrame(Object gl);
    }

    public void setRenderer(Renderer renderer) {
        mRenderer = renderer;
    }

    public Renderer getRenderer() {
        return mRenderer;
    }

    public void setRenderMode(int renderMode) {
        mRenderMode = renderMode;
    }

    public int getRenderMode() {
        return mRenderMode;
    }

    public void requestRender() {}

    public void onPause() {}

    public void onResume() {}

    public void queueEvent(Runnable r) {}

    public void setEGLContextClientVersion(int version) {}

    public void setEGLConfigChooser(boolean needDepth) {}

    public void setEGLConfigChooser(int redSize, int greenSize, int blueSize,
            int alphaSize, int depthSize, int stencilSize) {}

    public void setPreserveEGLContextOnPause(boolean preserveOnPause) {}
}
