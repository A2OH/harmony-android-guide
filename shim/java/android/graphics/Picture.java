package android.graphics;

/**
 * Android-compatible Picture shim. Stub; no actual recording.
 */
public class Picture {

    private int mWidth;
    private int mHeight;
    private boolean mRecording;

    public Picture() {
        mWidth  = 0;
        mHeight = 0;
    }

    public Picture(Picture src) {
        mWidth  = src != null ? src.mWidth  : 0;
        mHeight = src != null ? src.mHeight : 0;
    }

    /**
     * Begins recording drawing commands into this picture.
     *
     * @param width  the width of the recording area
     * @param height the height of the recording area
     * @return a Canvas to draw into
     */
    public Canvas beginRecording(int width, int height) {
        mWidth    = width;
        mHeight   = height;
        mRecording = true;
        return new Canvas();
    }

    /**
     * Ends the recording session started by {@link #beginRecording}.
     */
    public void endRecording() {
        mRecording = false;
    }

    /**
     * Draws this picture into the given canvas.
     */
    public void draw(Canvas canvas) {
        // stub: no recorded commands to replay
    }

    public int getWidth()  { return mWidth; }
    public int getHeight() { return mHeight; }
}
