package android.opengl;

import java.nio.Buffer;

/**
 * Android-compatible GLES11 shim for A2OH migration.
 * Extends GLES10 with VBO/buffer-object support.
 */
public class GLES11 extends GLES10 {

    public static final int GL_GENERATE_MIPMAP  = 0x8191;
    public static final int GL_POINT_SPRITE_OES = 0x8861;

    public static void glGenBuffers(int n, int[] buffers, int offset) {}
    public static void glBindBuffer(int target, int buffer) {}
    public static void glBufferData(int target, int size, Buffer data, int usage) {}
    public static void glDeleteBuffers(int n, int[] buffers, int offset) {}
    public static void glBufferSubData(int target, int offset, int size, Buffer data) {}
}
