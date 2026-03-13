package android.opengl;

import java.nio.Buffer;

/**
 * Android-compatible GLES30 shim for A2OH migration.
 * Extends GLES20; adds VAO, transform feedback, instanced draw, buffer mapping.
 */
public class GLES30 extends GLES20 {

    // Buffer mapping
    public static final int GL_MAP_READ_BIT                = 0x0001;
    public static final int GL_MAP_WRITE_BIT               = 0x0002;
    public static final int GL_MAP_INVALIDATE_RANGE_BIT    = 0x0004;
    public static final int GL_MAP_INVALIDATE_BUFFER_BIT   = 0x0008;

    // Transform feedback
    public static final int GL_TRANSFORM_FEEDBACK_BUFFER  = 0x8C8E;
    public static final int GL_INTERLEAVED_ATTRIBS        = 0x8C8C;
    public static final int GL_SEPARATE_ATTRIBS           = 0x8C8D;

    // Query objects
    public static final int GL_ANY_SAMPLES_PASSED         = 0x8C2F;
    public static final int GL_TIME_ELAPSED               = 0x88BF;

    // Uniform buffer objects
    public static final int GL_UNIFORM_BUFFER             = 0x8A11;

    // ---- Vertex Array Objects ----
    public static void glGenVertexArrays(int n, int[] arrays, int offset)    {}
    public static void glBindVertexArray(int array)                           {}
    public static void glDeleteVertexArrays(int n, int[] arrays, int offset) {}

    // ---- Instanced drawing ----
    public static void glDrawArraysInstanced(int mode, int first, int count, int instanceCount) {}
    public static void glDrawElementsInstanced(int mode, int count, int type,
            Buffer indices, int instanceCount)                                   {}
    public static void glDrawElementsInstanced(int mode, int count, int type,
            int indicesOffset, int instanceCount)                                {}
    public static void glVertexAttribDivisor(int index, int divisor)           {}

    // ---- Buffer mapping ----
    public static Buffer glMapBufferRange(int target, int offset, int length, int access) { return null; }
    public static boolean glUnmapBuffer(int target)                            { return true; }

    // ---- Query objects ----
    public static void glGenQueries(int n, int[] ids, int offset)  {}
    public static void glBeginQuery(int target, int id)             {}
    public static void glEndQuery(int target)                       {}
    public static void glDeleteQueries(int n, int[] ids, int offset) {}
    public static void glGetQueryObjectuiv(int id, int pname, int[] params, int offset) {}

    // ---- Transform feedback ----
    public static void glGenTransformFeedbacks(int n, int[] ids, int offset)   {}
    public static void glBindTransformFeedback(int target, int id)             {}
    public static void glBeginTransformFeedback(int primitiveMode)             {}
    public static void glEndTransformFeedback()                                {}
    public static void glTransformFeedbackVaryings(int program, String[] varyings, int bufferMode) {}

    // ---- Uniform buffer objects ----
    public static int  glGetUniformBlockIndex(int program, String uniformBlockName) { return 0; }
    public static void glUniformBlockBinding(int program, int uniformBlockIndex,
            int uniformBlockBinding)                                             {}
    public static void glBindBufferBase(int target, int index, int buffer)     {}
    public static void glBindBufferRange(int target, int index, int buffer,
            int offset, int size)                                               {}

    // ---- Misc GLES 3.0 ----
    public static void glReadBuffer(int mode)                                   {}
    public static void glBlitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1,
            int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter)  {}
    public static void glRenderbufferStorageMultisample(int target, int samples,
            int internalformat, int width, int height)                          {}
    public static void glInvalidateFramebuffer(int target, int numAttachments,
            int[] attachments, int offset)                                      {}
    public static String glGetStringi(int name, int index)                     { return ""; }
    public static void glCopyBufferSubData(int readTarget, int writeTarget,
            int readOffset, int writeOffset, int size)                          {}
    public static void glVertexAttribIPointer(int index, int size, int type,
            int stride, Buffer pointer)                                         {}
    public static void glVertexAttribIPointer(int index, int size, int type,
            int stride, int offset)                                             {}
    public static void glUniform1ui(int location, int v0)                      {}
    public static void glUniform2ui(int location, int v0, int v1)              {}
    public static void glUniform3ui(int location, int v0, int v1, int v2)     {}
    public static void glUniform4ui(int location, int v0, int v1, int v2, int v3) {}
}
