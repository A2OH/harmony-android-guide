package android.opengl;
import android.os.Debug;
import android.os.Debug;

import java.nio.Buffer;

/**
 * Android-compatible GLES32 shim for A2OH migration.
 * Extends GLES31; adds geometry shaders, tessellation, and blend barriers.
 */
public class GLES32 extends GLES31 {

    // Geometry / tessellation shader types
    public static final int GL_GEOMETRY_SHADER        = 0x8DD9;
    public static final int GL_TESS_CONTROL_SHADER    = 0x8E88;
    public static final int GL_TESS_EVALUATION_SHADER = 0x8E87;

    // Geometry shader limits
    public static final int GL_MAX_GEOMETRY_SHADER_INVOCATIONS      = 0x8E5A;
    public static final int GL_MAX_GEOMETRY_UNIFORM_COMPONENTS      = 0x8DDF;
    public static final int GL_MAX_GEOMETRY_OUTPUT_VERTICES         = 0x8DE0;
    public static final int GL_MAX_GEOMETRY_TOTAL_OUTPUT_COMPONENTS = 0x8DE1;

    // Tessellation limits
    public static final int GL_MAX_TESS_GEN_LEVEL               = 0x8E7E;
    public static final int GL_MAX_PATCH_VERTICES                = 0x8E7D;
    public static final int GL_PATCHES                           = 0x000E;
    public static final int GL_PATCH_VERTICES                    = 0x8E72;

    // Debug
    public static final int GL_DEBUG_OUTPUT                      = 0x92E0;
    public static final int GL_DEBUG_OUTPUT_SYNCHRONOUS          = 0x8242;

    // Blend equation advanced
    public static final int GL_MULTIPLY        = 0x9294;
    public static final int GL_SCREEN          = 0x9295;
    public static final int GL_OVERLAY         = 0x9296;
    public static final int GL_DARKEN          = 0x9297;
    public static final int GL_LIGHTEN         = 0x9298;

    // ---- Blend barrier (KHR_blend_equation_advanced) ----
    public static void glBlendBarrier() {}

    // ---- Primitive bounding box ----
    public static void glPrimitiveBoundingBox(float minX, float minY, float minZ, float minW,
            float maxX, float maxY, float maxZ, float maxW) {}

    // ---- Tessellation ----
    public static void glPatchParameteri(int pname, int value) {}

    // ---- Geometry shader layer writes ----
    public static void glFramebufferTexture(int target, int attachment, int texture, int level) {}

    // ---- Debug callbacks ----
    public interface DebugProc {
        void onMessage(int source, int type, int id, int severity, String message);
    }
    public static void glDebugMessageCallback(DebugProc callback) {}
    public static void glDebugMessageControl(int source, int type, int severity,
            int count, int[] ids, int offset, boolean enabled) {}
    public static void glDebugMessageInsert(int source, int type, int id,
            int severity, String buf) {}

    // ---- Multi-draw indirect ----
    public static void glMultiDrawArraysIndirectEXT(int mode, Buffer indirect,
            int drawcount, int stride) {}
    public static void glMultiDrawElementsIndirectEXT(int mode, int type, Buffer indirect,
            int drawcount, int stride) {}
}
