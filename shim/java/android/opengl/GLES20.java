package android.opengl;

import java.nio.Buffer;
import java.nio.FloatBuffer;

/**
 * Android-compatible GLES20 shim for A2OH migration.
 * All methods are no-op / return-0 stubs.
 */
public class GLES20 {

    // Shader types
    public static final int GL_VERTEX_SHADER   = 0x8B31;
    public static final int GL_FRAGMENT_SHADER = 0x8B30;

    // Shader / program status
    public static final int GL_COMPILE_STATUS  = 0x8B81;
    public static final int GL_LINK_STATUS     = 0x8B82;
    public static final int GL_VALIDATE_STATUS = 0x8B83;

    // Boolean
    public static final int GL_TRUE  = 1;
    public static final int GL_FALSE = 0;

    // Texture targets / units
    public static final int GL_TEXTURE_2D    = 0x0DE1;
    public static final int GL_TEXTURE0      = 0x84C0;

    // Framebuffer / renderbuffer
    public static final int GL_FRAMEBUFFER             = 0x8D40;
    public static final int GL_RENDERBUFFER            = 0x8D41;
    public static final int GL_FRAMEBUFFER_COMPLETE    = 0x8CD5;
    public static final int GL_COLOR_ATTACHMENT0       = 0x8CE0;
    public static final int GL_DEPTH_ATTACHMENT        = 0x8D00;
    public static final int GL_STENCIL_ATTACHMENT      = 0x8D20;

    // Data types / sizes
    public static final int GL_FLOAT         = 0x1406;
    public static final int GL_UNSIGNED_BYTE = 0x1401;
    public static final int GL_RGBA          = 0x1908;
    public static final int GL_DEPTH_COMPONENT16 = 0x81A5;

    // Enable caps
    public static final int GL_BLEND      = 0x0BE2;
    public static final int GL_DEPTH_TEST = 0x0B71;
    public static final int GL_CULL_FACE  = 0x0B44;

    // Clear bits
    public static final int GL_COLOR_BUFFER_BIT   = 0x4000;
    public static final int GL_DEPTH_BUFFER_BIT   = 0x0100;
    public static final int GL_STENCIL_BUFFER_BIT = 0x0400;

    // Primitives
    public static final int GL_TRIANGLES      = 0x0004;
    public static final int GL_TRIANGLE_STRIP = 0x0005;
    public static final int GL_LINES          = 0x0001;
    public static final int GL_POINTS         = 0x0000;

    // Buffer objects
    public static final int GL_ARRAY_BUFFER         = 0x8892;
    public static final int GL_ELEMENT_ARRAY_BUFFER = 0x8893;
    public static final int GL_STATIC_DRAW          = 0x88B4;
    public static final int GL_DYNAMIC_DRAW         = 0x88B8;

    // Texture params
    public static final int GL_TEXTURE_MIN_FILTER = 0x2801;
    public static final int GL_TEXTURE_MAG_FILTER = 0x2800;
    public static final int GL_TEXTURE_WRAP_S     = 0x2802;
    public static final int GL_TEXTURE_WRAP_T     = 0x2803;
    public static final int GL_LINEAR             = 0x2601;
    public static final int GL_NEAREST            = 0x2600;
    public static final int GL_CLAMP_TO_EDGE      = 0x812F;
    public static final int GL_REPEAT             = 0x2901;

    // ---- Shader / Program ----
    public static int  glCreateShader(int type)                          { return 0; }
    public static void glShaderSource(int shader, String source)         {}
    public static void glCompileShader(int shader)                       {}
    public static void glGetShaderiv(int shader, int pname, int[] params, int offset) {}
    public static String glGetShaderInfoLog(int shader)                  { return ""; }
    public static void glDeleteShader(int shader)                        {}

    public static int  glCreateProgram()                                 { return 0; }
    public static void glAttachShader(int program, int shader)           {}
    public static void glLinkProgram(int program)                        {}
    public static void glUseProgram(int program)                         {}
    public static void glDeleteProgram(int program)                      {}
    public static void glGetProgramiv(int program, int pname, int[] params, int offset) {}
    public static String glGetProgramInfoLog(int program)                { return ""; }

    // ---- Attribute / Uniform locations ----
    public static int  glGetAttribLocation(int program, String name)     { return -1; }
    public static int  glGetUniformLocation(int program, String name)    { return -1; }

    public static void glUniform1f(int location, float v0)               {}
    public static void glUniform2f(int location, float v0, float v1)     {}
    public static void glUniform3f(int location, float v0, float v1, float v2) {}
    public static void glUniform4f(int location, float v0, float v1, float v2, float v3) {}
    public static void glUniform1i(int location, int v0)                 {}
    public static void glUniformMatrix4fv(int location, int count, boolean transpose, float[] value, int offset) {}
    public static void glUniformMatrix4fv(int location, int count, boolean transpose, FloatBuffer value) {}

    // ---- Vertex attributes ----
    public static void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, Buffer ptr) {}
    public static void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, int offset) {}
    public static void glEnableVertexAttribArray(int index)              {}
    public static void glDisableVertexAttribArray(int index)             {}

    // ---- Textures ----
    public static void glGenTextures(int n, int[] textures, int offset)  {}
    public static void glBindTexture(int target, int texture)            {}
    public static void glTexImage2D(int target, int level, int internalformat,
            int width, int height, int border, int format, int type, Buffer pixels) {}
    public static void glTexParameteri(int target, int pname, int param) {}
    public static void glActiveTexture(int texture)                      {}

    // ---- Framebuffers ----
    public static void glGenFramebuffers(int n, int[] framebuffers, int offset)  {}
    public static void glBindFramebuffer(int target, int framebuffer)            {}
    public static void glFramebufferTexture2D(int target, int attachment,
            int textarget, int texture, int level)                               {}
    public static int  glCheckFramebufferStatus(int target)                      { return GL_FRAMEBUFFER_COMPLETE; }

    // ---- Renderbuffers ----
    public static void glGenRenderbuffers(int n, int[] renderbuffers, int offset) {}
    public static void glBindRenderbuffer(int target, int renderbuffer)           {}
    public static void glRenderbufferStorage(int target, int internalformat,
            int width, int height)                                                {}
    public static void glFramebufferRenderbuffer(int target, int attachment,
            int renderbuffertarget, int renderbuffer)                             {}

    // ---- Common draw / state ----
    public static void glEnable(int cap)                                         {}
    public static void glDisable(int cap)                                        {}
    public static void glClear(int mask)                                         {}
    public static void glClearColor(float r, float g, float b, float a)         {}
    public static void glViewport(int x, int y, int width, int height)          {}
    public static void glDrawArrays(int mode, int first, int count)             {}
    public static void glDrawElements(int mode, int count, int type, int offset) {}
    public static void glDrawElements(int mode, int count, int type, Buffer indices) {}
    public static void glFlush()                                                  {}
    public static void glFinish()                                                 {}
    public static int  glGetError()                                               { return 0; }

    // ---- Buffer objects ----
    public static void glGenBuffers(int n, int[] buffers, int offset)   {}
    public static void glBindBuffer(int target, int buffer)              {}
    public static void glBufferData(int target, int size, Buffer data, int usage) {}
    public static void glDeleteBuffers(int n, int[] buffers, int offset) {}
    public static void glBlendFunc(int sfactor, int dfactor)             {}
    public static void glDepthFunc(int func)                             {}
    public static void glColorMask(boolean r, boolean g, boolean b, boolean a) {}
    public static void glDepthMask(boolean flag)                         {}
    public static void glLineWidth(float width)                          {}
    public static void glScissor(int x, int y, int width, int height)   {}
}
