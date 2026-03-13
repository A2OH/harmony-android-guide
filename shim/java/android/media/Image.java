package android.media;

import java.nio.ByteBuffer;

/**
 * Android-compatible Image shim. Stub for media image data access.
 */
public abstract class Image implements AutoCloseable {

    public abstract int getWidth();
    public abstract int getHeight();
    public abstract int getFormat();
    public abstract long getTimestamp();
    public abstract Plane[] getPlanes();
    public abstract void close();

    /**
     * A single color plane of image data.
     */
    public static abstract class Plane {
        public abstract int getRowStride();
        public abstract int getPixelStride();
        public abstract ByteBuffer getBuffer();
    }
}
