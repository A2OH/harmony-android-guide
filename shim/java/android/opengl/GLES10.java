package android.opengl;

import java.nio.Buffer;

/**
 * Android-compatible GLES10 shim for A2OH migration.
 * All methods are no-op stubs; constants match Android API values.
 */
public class GLES10 {

    // Primitive types
    public static final int GL_POINTS         = 0x0000;
    public static final int GL_LINES          = 0x0001;
    public static final int GL_LINE_STRIP     = 0x0003;
    public static final int GL_TRIANGLES      = 0x0004;
    public static final int GL_TRIANGLE_STRIP = 0x0005;
    public static final int GL_TRIANGLE_FAN   = 0x0006;

    // Enable caps
    public static final int GL_TEXTURE_2D     = 0x0DE1;
    public static final int GL_BLEND          = 0x0BE2;
    public static final int GL_DEPTH_TEST     = 0x0B71;
    public static final int GL_CULL_FACE      = 0x0B44;
    public static final int GL_LIGHTING       = 0x0B50;

    // Clear buffer bits
    public static final int GL_COLOR_BUFFER_BIT   = 0x4000;
    public static final int GL_DEPTH_BUFFER_BIT   = 0x0100;
    public static final int GL_STENCIL_BUFFER_BIT = 0x0400;

    // Data types
    public static final int GL_FLOAT          = 0x1406;
    public static final int GL_UNSIGNED_BYTE  = 0x1401;
    public static final int GL_UNSIGNED_SHORT = 0x1403;

    public static void glEnable(int cap) {}
    public static void glDisable(int cap) {}
    public static void glClear(int mask) {}
    public static void glClearColor(float red, float green, float blue, float alpha) {}
    public static void glViewport(int x, int y, int width, int height) {}
    public static void glMatrixMode(int mode) {}
    public static void glLoadIdentity() {}
    public static void glTranslatef(float x, float y, float z) {}
    public static void glRotatef(float angle, float x, float y, float z) {}
    public static void glScalef(float x, float y, float z) {}
    public static void glPushMatrix() {}
    public static void glPopMatrix() {}

    public static void glVertexPointer(int size, int type, int stride, Buffer pointer) {}
    public static void glColorPointer(int size, int type, int stride, Buffer pointer) {}
    public static void glTexCoordPointer(int size, int type, int stride, Buffer pointer) {}

    public static void glDrawArrays(int mode, int first, int count) {}
    public static void glDrawElements(int mode, int count, int type, Buffer indices) {}

    public static void glEnableClientState(int array) {}
    public static void glDisableClientState(int array) {}

    public static void glFlush() {}
    public static void glFinish() {}
}
