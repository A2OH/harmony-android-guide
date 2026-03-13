package android.opengl;

/**
 * Android-compatible EGL14 shim for A2OH migration.
 * All methods return stub/no-op values.
 */
public class EGL14 {

    /** Sentinel object representing EGL_DEFAULT_DISPLAY (native value 0). */
    public static final EGLDisplay EGL_DEFAULT_DISPLAY  = new EGLDisplay(0);

    /** Sentinel representing EGL_NO_CONTEXT. */
    public static final EGLContext EGL_NO_CONTEXT  = new EGLContext(0);

    /** Sentinel representing EGL_NO_DISPLAY. */
    public static final EGLDisplay EGL_NO_DISPLAY  = new EGLDisplay(-1);

    /** Sentinel representing EGL_NO_SURFACE. */
    public static final EGLSurface EGL_NO_SURFACE  = new EGLSurface(0);

    // EGL error codes
    public static final int EGL_SUCCESS             = 0x3000;
    public static final int EGL_NOT_INITIALIZED     = 0x3001;
    public static final int EGL_BAD_ACCESS          = 0x3002;
    public static final int EGL_BAD_ALLOC           = 0x3003;
    public static final int EGL_BAD_ATTRIBUTE       = 0x3004;
    public static final int EGL_BAD_CONFIG          = 0x3005;
    public static final int EGL_BAD_CONTEXT         = 0x3006;
    public static final int EGL_BAD_CURRENT_SURFACE = 0x3007;
    public static final int EGL_BAD_DISPLAY         = 0x3008;
    public static final int EGL_BAD_MATCH           = 0x3009;
    public static final int EGL_BAD_NATIVE_PIXMAP   = 0x300A;
    public static final int EGL_BAD_NATIVE_WINDOW   = 0x300B;
    public static final int EGL_BAD_PARAMETER       = 0x300C;
    public static final int EGL_BAD_SURFACE         = 0x300D;
    public static final int EGL_CONTEXT_LOST        = 0x300E;

    // Config attributes
    public static final int EGL_NONE               = 0x3038;
    public static final int EGL_RED_SIZE           = 0x3024;
    public static final int EGL_GREEN_SIZE         = 0x3023;
    public static final int EGL_BLUE_SIZE          = 0x3022;
    public static final int EGL_ALPHA_SIZE         = 0x3021;
    public static final int EGL_DEPTH_SIZE         = 0x3025;
    public static final int EGL_STENCIL_SIZE       = 0x3026;
    public static final int EGL_RENDERABLE_TYPE    = 0x3040;
    public static final int EGL_OPENGL_ES2_BIT     = 0x0004;
    public static final int EGL_OPENGL_ES3_BIT     = 0x0040;

    // Context attributes
    public static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

    // Surface type
    public static final int EGL_SURFACE_TYPE  = 0x3033;
    public static final int EGL_WINDOW_BIT    = 0x0004;
    public static final int EGL_PBUFFER_BIT   = 0x0001;

    // ---- API ----

    public static EGLDisplay eglGetDisplay(int display_id) {
        return EGL_DEFAULT_DISPLAY;
    }

    public static boolean eglInitialize(EGLDisplay dpy, int[] major, int majorOffset,
            int[] minor, int minorOffset) {
        if (major != null && major.length > majorOffset) major[majorOffset] = 1;
        if (minor != null && minor.length > minorOffset) minor[minorOffset] = 4;
        return true;
    }

    public static boolean eglChooseConfig(EGLDisplay dpy, int[] attrib_list,
            int attrib_listOffset, EGLConfig[] configs, int configsOffset,
            int config_size, int[] num_config, int num_configOffset) {
        if (num_config != null && num_config.length > num_configOffset)
            num_config[num_configOffset] = 0;
        return true;
    }

    public static EGLContext eglCreateContext(EGLDisplay dpy, EGLConfig config,
            EGLContext share_context, int[] attrib_list, int offset) {
        return EGL_NO_CONTEXT;
    }

    public static EGLSurface eglCreateWindowSurface(EGLDisplay dpy, EGLConfig config,
            Object win, int[] attrib_list, int offset) {
        return EGL_NO_SURFACE;
    }

    public static EGLSurface eglCreatePbufferSurface(EGLDisplay dpy, EGLConfig config,
            int[] attrib_list, int offset) {
        return EGL_NO_SURFACE;
    }

    public static boolean eglMakeCurrent(EGLDisplay dpy, EGLSurface draw,
            EGLSurface read, EGLContext ctx) {
        return true;
    }

    public static boolean eglSwapBuffers(EGLDisplay dpy, EGLSurface surface) {
        return true;
    }

    public static boolean eglDestroyContext(EGLDisplay dpy, EGLContext ctx) {
        return true;
    }

    public static boolean eglDestroySurface(EGLDisplay dpy, EGLSurface surface) {
        return true;
    }

    public static boolean eglTerminate(EGLDisplay dpy) {
        return true;
    }

    public static int eglGetError() {
        return EGL_SUCCESS;
    }

    public static String eglQueryString(EGLDisplay dpy, int name) {
        return "";
    }

    // ---- Lightweight handle wrappers ----

    public static class EGLDisplay {
        public final long nativeHandle;
        public EGLDisplay(long handle) { this.nativeHandle = handle; }
    }

    public static class EGLContext {
        public final long nativeHandle;
        public EGLContext(long handle) { this.nativeHandle = handle; }
    }

    public static class EGLSurface {
        public final long nativeHandle;
        public EGLSurface(long handle) { this.nativeHandle = handle; }
    }

    public static class EGLConfig {
        public final long nativeHandle;
        public EGLConfig(long handle) { this.nativeHandle = handle; }
    }
}
