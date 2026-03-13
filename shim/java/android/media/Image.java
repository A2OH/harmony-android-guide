package android.media;

import java.nio.ByteBuffer;

/**
 * Android-compatible Image shim. Stub for media image data access.
 */
public class Image implements AutoCloseable {

    public int getWidth() { return 0; }
    public int getHeight() { return 0; }
    public int getFormat() { return 0; }
    public long getTimestamp() { return 0; }
    public Plane[] getPlanes() { return null; }
    public void close() {}

    /**
     * A single color plane of image data.
     */
    public static abstract class Plane {
        public int getRowStride() { return 0; }
        public int getPixelStride() { return 0; }
        public ByteBuffer getBuffer() { return null; }
    }
}
