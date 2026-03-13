package android.view;

/**
 * Android-compatible SurfaceTexture shim.
 * OH mapping: OH_NativeImage (EGL image stream)
 *
 * Captures frames from a Camera or MediaPlayer into an OpenGL ES texture.
 */
public class SurfaceTexture {

    /**
     * Callback invoked when a new image frame is available.
     */
    public interface OnFrameAvailableListener {
        /**
         * Called when a new image frame is available on the SurfaceTexture.
         *
         * @param surfaceTexture  the SurfaceTexture for which a new frame is available.
         */
        void onFrameAvailable(SurfaceTexture surfaceTexture);
    }

    private final int texName;
    private int       defaultBufferWidth  = 1;
    private int       defaultBufferHeight = 1;
    private boolean   released            = false;
    private OnFrameAvailableListener listener;

    // Cached transform matrix (column-major, identity by default)
    private final float[] transformMatrix = {
        1, 0, 0, 0,
        0, 1, 0, 0,
        0, 0, 1, 0,
        0, 0, 0, 1
    };
    private long timestamp = 0L;

    /**
     * Constructs a SurfaceTexture attached to the given OpenGL ES texture name.
     *
     * @param texName  the OpenGL ES texture object name (glGenTextures).
     */
    public SurfaceTexture(int texName) {
        this.texName = texName;
    }

    /**
     * Updates the texture image to the most recent frame received.
     * No-op in this shim.
     */
    public void updateTexImage() {
        // no-op — no real GPU in shim
    }

    /**
     * Retrieves the 4×4 texture transform matrix associated with the texture image
     * set by the most recent call to {@link #updateTexImage()}.
     *
     * @param mtx  a float[16] array to receive the matrix in column-major order.
     */
    public void getTransformMatrix(float[] mtx) {
        if (mtx == null || mtx.length < 16) {
            throw new IllegalArgumentException("getTransformMatrix requires a float[16]");
        }
        System.arraycopy(transformMatrix, 0, mtx, 0, 16);
    }

    /**
     * Returns the timestamp (in nanoseconds) associated with the texture image
     * set by the most recent call to {@link #updateTexImage()}.
     */
    public long getTimestamp() { return timestamp; }

    /**
     * Releases all resources held by this SurfaceTexture.
     */
    public void release() { released = true; }

    /**
     * Returns true if the SurfaceTexture has been released.
     */
    public boolean isReleased() { return released; }

    /**
     * Sets the default size of the image buffers produced by the SurfaceTexture.
     *
     * @param width   buffer width in pixels; must be > 0.
     * @param height  buffer height in pixels; must be > 0.
     */
    public void setDefaultBufferSize(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException(
                    "SurfaceTexture.setDefaultBufferSize: width and height must be > 0");
        }
        this.defaultBufferWidth  = width;
        this.defaultBufferHeight = height;
    }

    /**
     * Sets the listener for new image frames. May be called from any thread.
     *
     * @param listener  the listener to receive frame-available callbacks.
     */
    public void setOnFrameAvailableListener(OnFrameAvailableListener listener) {
        this.listener = listener;
    }

    /** Returns the OpenGL ES texture name bound to this SurfaceTexture. */
    public int getTexName() { return texName; }
}
