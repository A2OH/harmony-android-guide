package android.opengl;

/**
 * Android-compatible GLES31 shim for A2OH migration.
 * Extends GLES30; adds compute shaders and SSBOs.
 */
public class GLES31 extends GLES30 {

    // Compute shader
    public static final int GL_COMPUTE_SHADER                       = 0x91B9;
    public static final int GL_MAX_COMPUTE_SHADER_STORAGE_BLOCKS    = 0x90DB;
    public static final int GL_MAX_COMBINED_SHADER_STORAGE_BLOCKS   = 0x90DC;
    public static final int GL_SHADER_STORAGE_BUFFER               = 0x90D2;
    public static final int GL_SHADER_IMAGE_ACCESS_BARRIER_BIT     = 0x00000020;
    public static final int GL_SHADER_STORAGE_BARRIER_BIT          = 0x00002000;
    public static final int GL_ALL_BARRIER_BITS                    = 0xFFFFFFFF;

    // ---- Compute dispatch ----
    public static void glDispatchCompute(int num_groups_x, int num_groups_y, int num_groups_z) {}
    public static void glDispatchComputeIndirect(int indirect) {}

    // ---- Memory barrier ----
    public static void glMemoryBarrier(int barriers) {}
    public static void glMemoryBarrierByRegion(int barriers) {}

    // ---- SSBO ----
    public static void glShaderStorageBlockBinding(int program, int storageBlockIndex, int storageBlockBinding) {}

    // ---- Image units ----
    public static void glBindImageTexture(int unit, int texture, int level,
            boolean layered, int layer, int access, int format) {}

    // ---- Indirect draw ----
    public static final int GL_DRAW_INDIRECT_BUFFER = 0x8F3F;
    public static void glDrawArraysIndirect(int mode, int indirect) {}
    public static void glDrawElementsIndirect(int mode, int type, int indirect) {}

    // ---- Separate shader programs ----
    public static final int GL_PROGRAM_SEPARABLE   = 0x8258;
    public static final int GL_VERTEX_SHADER_BIT   = 0x00000001;
    public static final int GL_FRAGMENT_SHADER_BIT = 0x00000002;
    public static final int GL_ALL_SHADER_BITS      = 0xFFFFFFFF;
    public static void glUseProgramStages(int pipeline, int stages, int program) {}
    public static void glActiveShaderProgram(int pipeline, int program) {}
    public static int  glCreateShaderProgramv(int type, String[] strings) { return 0; }
    public static void glBindProgramPipeline(int pipeline) {}
    public static void glDeleteProgramPipelines(int n, int[] pipelines, int offset) {}
    public static void glGenProgramPipelines(int n, int[] pipelines, int offset) {}

    // ---- Multi-sample textures ----
    public static void glTexStorage2DMultisample(int target, int samples, int internalformat,
            int width, int height, boolean fixedsamplelocations) {}
}
