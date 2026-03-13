package android.opengl;

public final class EGL15 {
    public EGL15() {}

    public static final int EGL_CL_EVENT_HANDLE = 0;
    public static final int EGL_CONDITION_SATISFIED = 0;
    public static final int EGL_CONTEXT_MAJOR_VERSION = 0;
    public static final int EGL_CONTEXT_MINOR_VERSION = 0;
    public static final int EGL_CONTEXT_OPENGL_COMPATIBILITY_PROFILE_BIT = 0;
    public static final int EGL_CONTEXT_OPENGL_CORE_PROFILE_BIT = 0;
    public static final int EGL_CONTEXT_OPENGL_DEBUG = 0;
    public static final int EGL_CONTEXT_OPENGL_FORWARD_COMPATIBLE = 0;
    public static final int EGL_CONTEXT_OPENGL_PROFILE_MASK = 0;
    public static final int EGL_CONTEXT_OPENGL_RESET_NOTIFICATION_STRATEGY = 0;
    public static final int EGL_CONTEXT_OPENGL_ROBUST_ACCESS = 0;
    public static final int EGL_FOREVER = 0;
    public static final int EGL_GL_COLORSPACE = 0;
    public static final int EGL_GL_COLORSPACE_LINEAR = 0;
    public static final int EGL_GL_COLORSPACE_SRGB = 0;
    public static final int EGL_GL_RENDERBUFFER = 0;
    public static final int EGL_GL_TEXTURE_2D = 0;
    public static final int EGL_GL_TEXTURE_3D = 0;
    public static final int EGL_GL_TEXTURE_CUBE_MAP_NEGATIVE_X = 0;
    public static final int EGL_GL_TEXTURE_CUBE_MAP_NEGATIVE_Y = 0;
    public static final int EGL_GL_TEXTURE_CUBE_MAP_NEGATIVE_Z = 0;
    public static final int EGL_GL_TEXTURE_CUBE_MAP_POSITIVE_X = 0;
    public static final int EGL_GL_TEXTURE_CUBE_MAP_POSITIVE_Y = 0;
    public static final int EGL_GL_TEXTURE_CUBE_MAP_POSITIVE_Z = 0;
    public static final int EGL_GL_TEXTURE_LEVEL = 0;
    public static final int EGL_GL_TEXTURE_ZOFFSET = 0;
    public static final int EGL_IMAGE_PRESERVED = 0;
    public static final int EGL_LOSE_CONTEXT_ON_RESET = 0;
    public static final int EGL_NO_CONTEXT = 0;
    public static final int EGL_NO_DISPLAY = 0;
    public static final int EGL_NO_IMAGE = 0;
    public static final int EGL_NO_RESET_NOTIFICATION = 0;
    public static final int EGL_NO_SURFACE = 0;
    public static final int EGL_NO_SYNC = 0;
    public static final int EGL_OPENGL_ES3_BIT = 0;
    public static final int EGL_PLATFORM_ANDROID_KHR = 0;
    public static final int EGL_SIGNALED = 0;
    public static final int EGL_SYNC_CL_EVENT = 0;
    public static final int EGL_SYNC_CL_EVENT_COMPLETE = 0;
    public static final int EGL_SYNC_CONDITION = 0;
    public static final int EGL_SYNC_FENCE = 0;
    public static final int EGL_SYNC_FLUSH_COMMANDS_BIT = 0;
    public static final int EGL_SYNC_PRIOR_COMMANDS_COMPLETE = 0;
    public static final int EGL_SYNC_STATUS = 0;
    public static final int EGL_SYNC_TYPE = 0;
    public static final int EGL_TIMEOUT_EXPIRED = 0;
    public static final int EGL_UNSIGNALED = 0;
    public static int eglClientWaitSync(Object p0, Object p1, Object p2, Object p3) { return 0; }
    public static Object eglCreateImage(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) { return null; }
    public static Object eglCreatePlatformPixmapSurface(Object p0, Object p1, Object p2, Object p3, Object p4) { return null; }
    public static Object eglCreatePlatformWindowSurface(Object p0, Object p1, Object p2, Object p3, Object p4) { return null; }
    public static Object eglCreateSync(Object p0, Object p1, Object p2, Object p3) { return null; }
    public static boolean eglDestroyImage(Object p0, Object p1) { return false; }
    public static boolean eglDestroySync(Object p0, Object p1) { return false; }
    public static Object eglGetPlatformDisplay(Object p0, Object p1, Object p2, Object p3) { return null; }
    public static boolean eglGetSyncAttrib(Object p0, Object p1, Object p2, Object p3, Object p4) { return false; }
    public static boolean eglWaitSync(Object p0, Object p1, Object p2) { return false; }
}
